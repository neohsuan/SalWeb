package gov.vghks.sal.dao;

import gov.vghks.common.util.DateUtil;
import gov.vghks.common.util.StringUtil;
import gov.vghks.common.util.VGHDataAccess;
import gov.vghks.sal.Globals;
import gov.vghks.sal.model.Pabaspsn;
import gov.vghks.sal.model.Pacspf;
import gov.vghks.sal.model.Padatpay;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

public class CommDAO {
	String gClassName = CommDAO.class.getName();
	private String sql="";
	static ArrayList<Map<String, Object>> result = null;
	static ArrayList<Object> params = new ArrayList<Object>();
	private String dataSourceName = Globals.dataSource_NT;
	static Logger log = Logger.getLogger(SalNTDAO.class);  //呼叫log4j
	private VGHDataAccess da = null;
	private static final int META_TYPE = 2; 			//回傳map中欄位名稱是小寫
	private static boolean connFlg = false;
	
	private String tCspfTbl = "PA.PACSPF";					//工作獎金支扣
	
	public CommDAO() {
		super();
	}
	
	public CommDAO(VGHDataAccess da1) {
		super();
		this.da=da1;
		connFlg = true;
	}
	/**傳回 績效點數
	 * @param 
	 * 		  vRANK3  : 本功別
'             vTitle  : 職稱
'             vRANK2  : 權理
'             vRNK2N  : 職等
'             vDEGR2  : 級別
'   @return   vPFKEY  : 實際支領KEY
'             vPFBAS  : 基本點數
'             vPFDAT  : 職等點數
	 * @throws SQLException 
	 */
	
	public HashMap getCsPfByKey(int intRANK3,String vTitle,String vRANK2,String vRNK2N,int intDEGR2,VGHDataAccess da) throws SQLException{
		String me = getClass().getName() + ".getCsPfByKey()";
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap rtnMap=null;
		String vRANK3="";
		String vPFKEY="";
		String vPFBAS="";
		String vPFDAT="";
		String vDEGR2="";
		//本功別
		vRANK3=String.valueOf(intRANK3);
		//職稱
		vTitle=StringUtil.nullToString(vTitle);
		//權理
		vRANK2=StringUtil.nullToString(vRANK2);
		//職等
		vRNK2N=StringUtil.nullToString(vRNK2N);
		//級別
		vDEGR2=String.valueOf(intDEGR2);
		vDEGR2=StringUtil.fillChar(StringUtil.nullToString(vDEGR2),2,"0",false);
		//實際支領KEY 本功別=0,表示pfkey組成為職稱+"00000"
		if (intRANK3==0)
			vPFKEY=vTitle+"00000";
		else
			vPFKEY = vTitle + vRNK2N + vRANK3 + vDEGR2 ;
		rtnMap=new HashMap();	
		rtnMap.put("pfkey", vPFKEY);
		
		sql = "select  PACSBASE, PACSDATA "+
		 	    "from "+this.tCspfTbl +
		 	  " where PACSKEY = ? "+
		 	  "  with ur";
		params.add(vPFKEY);
		Date begin = new Date();
		log.info(me+"  " + sql+"...params:"+ params.toString());
		result = da.arraylistbyQuery(sql,params,META_TYPE);
		log.info(me+"  ...size:"+result.size());
		if(result.size()>0){
			for (Map<String,Object> data :result){
				vPFBAS=StringUtil.nullToString(data.get("pacsbase"));
				vPFDAT=StringUtil.nullToString(data.get("pacsdata"));
				rtnMap.put("pfbas", vPFBAS);
				rtnMap.put("pfdat", vPFDAT);
			}
		}
		Date end = new Date();
		log.info(me+"(size):" + result.size()+" dataaccess:"+(end.getTime()-begin.getTime()));
	
		return rtnMap;
	}
	//取得績效點數map	
	public HashMap getCsPfByAll(VGHDataAccess da) throws SQLException{
		String me = getClass().getName() + ".getCsPfByAll()";
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap rtnMap=new HashMap();
		Pacspf oneObj=null; 
		String cskey="";
		sql = "select PACSKEY, PACSBASE, PACSDATA, PACSNAME, COALESCE(PAYUPDID,'') as PAYUPDID,  ZPACSPF "+
		 	    "from "+this.tCspfTbl +
		 	  "  with ur";
		
		Date begin = new Date();
		log.info(me+"  " + sql+"...params:"+ params.toString());
		try{
			result = da.arraylistbyQuery(sql,META_TYPE);
			log.info(me+"  ...size:"+result.size());
			if(result.size()>0){
				for (Map<String,Object> data :result){
					oneObj=new Pacspf();
					PropertyUtils.copyProperties(oneObj, data);
					
					cskey=StringUtil.nullToString(oneObj.getPacskey());
					
					rtnMap.put(cskey, oneObj);
				}
			}
			Date end = new Date();
			log.info(me+"(size):" + result.size()+" dataaccess:"+(end.getTime()-begin.getTime()));
		}
		catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}
		finally{
		
		}
		return rtnMap;
	}
	/**傳回計算總所得,應稅所得(總收入-房租-實物代金-主管加給-不休假獎金-交通費-福利互助金),淨所得,總扣除額
	 * '副程式名稱： Sum_Pay_Deduct
'目    的： 計算總所得,應稅所得(總收入-房租-實物代金-主管加給-不休假獎金-交通費-福利互助金),淨所得,總扣除額
'           92.8.26福利互助金VP400不列入應稅所得
'           94.7.1自繳退休金VD060 VD069應稅所得要扣除
'           94.7.1房租 實物代金不能列入,房租生效日為78.7.1 實物代金生效日79.7.1
'           94.8.29 房租生效日及實物代金生效日皆為84.7.1 之前要扣除 之後不扣
'           97.6.23 本薪--事病薪等停發VD070應稅所得要扣除?? ==>取消102.1.3
'           97.9.10 總所得,淨所得:不列所得要加上,如:病歷罰扣不得列入所得 (2008-8-19VP017看診逾時罰扣 VP016超出健保總額) ==>取消102.1.3
'           101.2.6 VP016改成健保獎勵罰扣(從99.10開始有正項),
'           103.4.22 2014.4生效 VP200加班費 VP437超額獎金--論文補助<==不列所得
'           109.1.21 讀取PA.PAYITM.PAYIFTAX='Y',因有新增科目,未同步加入
'參數說明：
'           vRank        :階別	
'           vRnk2N       :職等
'           vRank2       :權理
'           vDegre       :俸點              92.8.15 房租津貼T1 385以上=>600 T1 385以上=>500
'           vGovDt       :初任公職日
'           vSalDt       :發薪日
'return:	vpay   	     :總所得
'           vtaxpay   	 :應稅所得
'           vnetPay      :淨所得
'			vduduct      :應支出
'*******************************************************************/
	public HashMap Sum_Pay_Deduct(ArrayList<Padatpay> datpayAry,String vRank,String vRANK2,String vRNK2N,int vDegre,String vGovDt,String vSalDt)throws Exception {
		String me = getClass().getName() + ".Sum_Pay_Deduct()";
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap rtnMap=new HashMap();	
		int vrent_house =0;  //房租
		int vthing_money =0; //實物代金
		int vpay =0;        //總收入
		int vduduct =0;     //總支出
		int vnetpay=0;		//淨所得
		int vtaxPay_Y =0;         //應稅所得,列Y部分
		int vtaxpay=0;		//應稅所得,扣除實物代金及房租
		
		int vchief =0;      //主管加給
		int vnon_rest =0;   //不休假獎金
		int vtraffic =0;    //交通費
		int vWelfare =0;    //福利互助金
		int vPapers =0;            //超額獎金_論文補助 2014.4.1生效 2014.4.22改
		int vOvertime =0;           //加班費2014.4.1生效 2014.4.22改
		int vDuty =0;           //VP455總值日值班費
		int vSelfRetire =0; //勞工自繳退休金
		
		String vRank_P ="";     //職等
		
		Boolean vflag =false;    //有本薪才計算應稅所得
		String vHourseDt ="1995-7-1";   //房租生效日 1989-7-1
		String vThingDt ="1995-7-1";    //實物代金生效日 1990-7-1
		String vSalBgdt ="2005-7-1";      //開始不扣房租津貼及實物代金日期
		
		Padatpay datpayObj=null;
		
		Iterator<Padatpay> it =  datpayAry.iterator();
		while ( it.hasNext()) {
			datpayObj = (Padatpay) it.next();
			if ("FP00".equals(datpayObj.getPaycode().substring(0, 4))){
				vflag = true;
			}
			//總收入,應稅收入,總支出
			if ("P".equals(datpayObj.getPaycode().substring(1, 2))){
				vpay +=	datpayObj.getPayamt();
				if ("Y".equals(datpayObj.getPayiftax())){
					vtaxPay_Y+=datpayObj.getPayamt();
				}
			}else if ("D".equals(datpayObj.getPaycode().substring(1, 2))){
				vduduct+=datpayObj.getPayamt();
			}
			//'主管加給 年終獎金--主管特支  考績獎金--主管特支
	        if ("FP03".equals(datpayObj.getPaycode().substring(0, 4)) || datpayObj.getPaycode().substring(0, 4).matches("VP124|VP134")){
	        	vchief += datpayObj.getPayamt();
	        }
	        //不休假獎金
	        else if("VP11".equals(datpayObj.getPaycode().substring(0, 4))){
	        	vnon_rest += datpayObj.getPayamt();
	        }
	        //交通費    
	        else if(datpayObj.getPaycode().substring(0, 4).matches("FP06|VP19")){
	        	vtraffic += datpayObj.getPayamt();
	        }
	        //福利互助金   
	        else if("VP400".equals(datpayObj.getPaycode())){
	        	vWelfare += datpayObj.getPayamt();
	        }
	        //自繳退休金  
	        else if(datpayObj.getPaycode().substring(0, 4).matches("VD06")){
	        	vSelfRetire += datpayObj.getPayamt();
	        }
	        //超額獎金_論文補助   
	        else if("VP437".equals(datpayObj.getPaycode())){
	        	vPapers += datpayObj.getPayamt();
	        }
	        //加班費
	        else if("VP200".equals(datpayObj.getPaycode())){
	        	vOvertime += datpayObj.getPayamt();
	        }
	        //總值日值班費
	        else if("VP455".equals(datpayObj.getPaycode())){
	        	vDuty += datpayObj.getPayamt();
	        }
		}
		
	    //房租,實物代金
		if (("".equals(StringUtil.nullToString(vRANK2))) && ("".equals(StringUtil.nullToString(vRNK2N)))){
			vRank_P ="";
		}else{
			if(!"".equals(StringUtil.nullToString(vRANK2)))
				vRank_P = StringUtil.nullToString(vRANK2);
			else
				vRank_P = StringUtil.nullToString(vRNK2N);
		}
	    
	    
	    if ( DateUtil.compareDate(vSalDt, vSalBgdt) ){	//vSaldt < vSalBgdt '2005-7-20改 2005-7-1開始不計算房租及實物代金
	    	if (vRank.matches("5|6")){
	    		vrent_house = 400;
	    	    vthing_money = 930;
	    	}else if (vRank_P.matches("01|02|03") || (vRank_P.equals("T1") && vDegre<385 ) || "4".equals(vRank)){
	    		vrent_house = 500;
	    	    vthing_money = 930;
	    	}else if (vRank_P.matches("04|05|06|07|P3") || (vRank_P.equals("T1") && vDegre>=385 )){
	    		vrent_house = 600;
	    	    vthing_money = 930;
	    	}else if (vRank_P.matches("08|09|10|11|12|13|14|P2|P3") ){
	    		 vrent_house = 700;
	    		 vthing_money = 930;
	    	}
	    }	
	    else	//2005-8-29改 以84.7.1為基準計算 之前要扣 之後不扣 (初任公職日<基準日,有房租及實物代金)
	    {
	    	if (vRank.matches("5|6")){
	    		if ( DateUtil.compareDate(vGovDt, vHourseDt) )
	    			vrent_house = 400;
	    		if ( DateUtil.compareDate(vGovDt, vThingDt) )
	    			vthing_money = 930;
	    	}else if (vRank_P.matches("01|02|03") || (vRank_P.equals("T1") && vDegre<385 ) || "4".equals(vRank)){
	    		if ( DateUtil.compareDate(vGovDt, vHourseDt) )
	    			vrent_house = 500;
	    		if ( DateUtil.compareDate(vGovDt, vThingDt) )
	    			vthing_money = 930;
	    	}else if (vRank_P.matches("04|05|06|07|P3") || (vRank_P.equals("T1") && vDegre>=385 )){
	    		if ( DateUtil.compareDate(vGovDt, vHourseDt) )
	    			vrent_house = 600;
	    		if ( DateUtil.compareDate(vGovDt, vThingDt) )
	    			vthing_money = 930;
	    	}else if (vRank_P.matches("08|09|10|11|12|13|14|P2|P3") ){	
	    		if ( DateUtil.compareDate(vGovDt, vHourseDt) )
	    			vrent_house = 700;
	    		if ( DateUtil.compareDate(vGovDt, vThingDt) )
	    			vthing_money = 930;
	    	}
	    }
	    //淨所得=總所得-總支出
	    vnetpay = vpay - vduduct;
	    if (vflag){
			//應稅所得=datpay.getPayiftax()="Y"-實物代金-房租-自繳退休金
		    vtaxpay = vtaxPay_Y - vrent_house - vthing_money-vSelfRetire;
		    if (vtaxpay<0)vtaxpay=0;
	    }else{
	    	vtaxpay = vtaxPay_Y;
	    }
	    
	    rtnMap.put("pay", vpay);
		rtnMap.put("duduct", vduduct); 
		rtnMap.put("netpay", vnetpay);
		rtnMap.put("taxpay", vtaxpay); 
		rtnMap.put("thing_money", vthing_money); 
		rtnMap.put("rent_house", vrent_house); 
		return rtnMap;
	}
	/**
	 * 關閉VGHDataAccess與ResultSet
	 * 
	 * @param da
	 * @param rs
	 */
	protected void closeAll(VGHDataAccess da, ResultSet rs,boolean blnclose) {
		if (rs != null) {
			try {
				rs.close();
				rs = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!blnclose){
			if (da != null) {
				try {
					da.close();
					da = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}	
	}	
}
