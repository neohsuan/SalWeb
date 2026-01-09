package gov.vghks.sal.dao;

import gov.vghks.common.util.DateUtil;
import gov.vghks.common.util.StringUtil;
import gov.vghks.common.util.VGHDataAccess;
import gov.vghks.sal.Globals;
import gov.vghks.sal.Tables;
import gov.vghks.sal.model.Pabaspsn;
import gov.vghks.sal.model.Pacspf;
import gov.vghks.sal.model.Padatpay;
import gov.vghks.sal.model.Padatpsn;
import gov.vghks.sal.model.Papayitm;
import gov.vghks.sal.model.Parank;
import gov.vghks.sal.model.Patitle;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
	private String hid="";
	private String tCspfTbl  = "PA.PACSPF";				//工作獎金支扣
	private String tTitle    = "PA.PATITLE";					//職稱
	private String tPayitm   = "PA.PAPAYITM";				//支扣項目
	private String tRankTbl  = "PA.PARANK";				//階級支扣
	
	public CommDAO() {
		super();
	}
	
	public CommDAO(VGHDataAccess da1) {
		super();
		this.da=da1;
		connFlg = true;
	}
	public CommDAO(VGHDataAccess da1,String hid) {
		super();
		this.da=da1;
		this.hid=hid;
		Tables tabInfo = Tables.getTables(hid);
		connFlg = true;
		tCspfTbl=tabInfo.tCspfTbl;
		tRankTbl=tabInfo.tRankTbl;
		tPayitm =tabInfo.tPayItm;
		tTitle  =tabInfo.tTitle;
	}
	/**傳回 俸點
	 * @param 
	 * 		  vRANK3  : 本功別
'             vTitle  : 職稱
'			  vRANK   : 階別
'             vRNK2N  : 職等
'             vDEGR2  : 級別
'			  vhid    : 醫院別		
'   @return   Parank : 階級支扣 
'             
	 * @throws SQLException 
	 */
	
	public HashMap getDegreByRank(String vRANK3,String vTitle,String vRANK,String vRNK2N,String vDEGR2,String vHid,VGHDataAccess da) throws Exception{
		String me = getClass().getName() + ".getDegreByRank()";
		log.info(me +  " vTitle = " + vTitle+" vRANK = " + vRANK+" vRNK2N = " + vRNK2N+" vDEGR2 = " + vDEGR2+ " hid = " + vHid);
		Parank oneObj=null;
		Papayitm payitmObj=null;
		HashMap rtnMap=new HashMap();
		HashMap<String,Papayitm> payitmMap= new HashMap();	//支扣項目map
		HashMap<String,Patitle> titleMap= new HashMap();	//職稱map
		ArrayList<Object> params = new ArrayList<Object>();
		JSONObject jsonOne =null;
		JSONArray jsonRtn = new JSONArray();
		boolean vflag=true;
		String strvalue="";
		int intvalue=0;
		String fieldname="";
		String vLEVEL1="";
		String vLEVEL2="";
		int intDegr2=0;
		long starttime =0;
		String strPaycode="";
		starttime = System.currentTimeMillis();
		//職稱
		vTitle=StringUtil.nullToString(vTitle);
		//職等
		vRNK2N=StringUtil.nullToString(vRNK2N);
		//階別
		vRANK=StringUtil.nullToString(vRANK);
		//本功別
		vRANK3=StringUtil.nullToString(vRANK3);
		//級別
		vDEGR2=StringUtil.fillChar(StringUtil.nullToString(vDEGR2),2,"0",false);
		intDegr2 = Integer.parseInt(vDEGR2);
		
		vLEVEL1 = vRNK2N;
		if (vDEGR2.equals("")){
			vflag=false;
		}
		//0無職等、7臨
		if (vRANK.matches("0|7")){
			vflag=false;	
		}
		//1簡任
		else if ( vRANK.matches("1")) {
			if (!vRNK2N.matches("10|11|12|13") )
				vflag=false;
		}
		//2薦任
		else if ( vRANK.matches("2") ){
			if ( !vRNK2N.matches("6|7|8|9") )		
				vflag=false;
		}
		//3委任 H警察人員 
		else if ( vRANK.matches("3|H") ){
			if (!vRNK2N.matches("1|2|3|4|5") )
				vflag=false;
		}
		//8師 
		else if ( vRANK.matches("8") ){
			if ( !vRNK2N.matches("P1|P2|P3") )
				vflag=false;
		}	
		//9士 
		else if ( vRANK.matches("9") ) {
			if ( !vRNK2N.matches("T1") )
				vflag=false;
		}	
		//F契約
		else if ( vRANK.matches("F") ){
			if ( vTitle.equals("CG") && vHid.matches("1A0|2A0") ){
				
			}else if ( !vTitle.equals("") ){
				//CH契約住院醫師(證書) CI契約住院醫師 CJ二年無證不分科住院醫師 CK二年有證不分科住院醫師 DS半日契約護理 DW半日契約藥師
				if ( vTitle.matches("CH|CI|CJ|CK|DS|DW"))
					vLEVEL1 = vTitle;
				else if (vHid.matches("2A0") && intDegr2 <200)
					vLEVEL1 = vTitle;
				else 
					vLEVEL1 = "ZZ";
			}else
				vLEVEL1 = "N"+vRANK;
		}else{
			vLEVEL1 = "N"+vRANK;
		}
	                
		if (vflag){
			if (vHid.equals("1A0")){
				if (vRANK3.matches("0|1|2") ){
					vLEVEL2 =vRANK3+vDEGR2;
				}
			}else if (vHid.equals("2A0")){
				if ( intDegr2 >100 ){
					vLEVEL2 = vDEGR2;
				}else{
					vLEVEL2 = vRANK3+vDEGR2;
				}
			}else if (vHid.equals("3A0")){
				if (vRANK.equals("F")){
					vLEVEL2 = "";
				}else{
					vLEVEL2 = vRANK3+vDEGR2;
				}	
			}
		}
		try{
			//支扣代碼map
			payitmMap= this.getPayitmMap("N", vHid, da);
			//職稱map
			titleMap= this.getTitleMap(vHid, da);
			
			sql = " select PAYLEV1V, PAYLEV2V, PAYDEGRV, PAYRANKV, PAYBASEV, PAYGEINF, PAYLBINF, PAYDPINV, PAYGEINN, PAYREBEF, PAYUSEID, PAYUPDAT, HID, PAYREBEN "+
					 "from "+this.tRankTbl + 
				   " where PAYLEV1V=? and PAYLEV2V=? and HID=? WITH UR";
	
			params.add(vLEVEL1);
			params.add(vLEVEL2);
			params.add(vHid);
		
			log.info(me+"  " + sql+"...params:"+ params.toString());
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"  ...size:"+result.size());
			if(result.size()>0){
				for (Map<String,Object> data :result){
					oneObj = new Parank();
					PropertyUtils.copyProperties(oneObj, data);
				}
			}
			rtnMap.put("rankItem", oneObj);
			//歷遍parank各項欄位
			Field[] fields=oneObj.getClass().getDeclaredFields();
			for (Field field :fields){
				field.setAccessible(true);
				if (field.getType()==int.class){
					intvalue = (Integer) field.get(oneObj);
				}	
				fieldname= field.getName();
				

				jsonOne = new JSONObject();
				if (fieldname.equals("paybasev")){
					strPaycode="FP000";
				}else{
					strPaycode="";
				}
				if (!"".equals(strPaycode)){
					//取得支扣代碼
					payitmObj=payitmMap.get(strPaycode);
					jsonOne.put("paycode", strPaycode);
					jsonOne.put("pacodnmc", StringUtil.nullToString(payitmObj.getPacodnmc()));
					jsonOne.put("payamt", intvalue );
					jsonRtn.add(jsonOne);
					System.out.println("Field Name: " + field.getName() + ", Type: " + field.getType());
				}
			}
			rtnMap.put("payitm_json", jsonRtn);
			Date end = new Date();
			log.info(me+", 共花費："+(System.currentTimeMillis()-starttime)+"毫秒");
		}catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}finally{
			closeAll(da,null,connFlg);			
		}
		return rtnMap;
	}
	/**傳回 績效點數
	 * @param 
	 * 		  vRANK3  : 本功別
'             vTitle  : 職稱
'			  vRANK   : 階別
'             vRNK2N  : 職等
'             vDEGR2  : 級別
'  			  vBANKDT : 發薪日
'			  vhid    : 醫院別		
'   @return   vPFKEY  : 實際支領KEY
'             vPFBAS  : 基本點數
'             vPFDAT  : 職等點數
	 * @throws SQLException 
	 */
	
	public HashMap getCsPfByKey(int vRANK3,String vTitle,String vRANK,String vRNK2N,int intDEGR2,String vBANKDT,String vHid,VGHDataAccess da) throws Exception{
		String me = getClass().getName() + ".getCsPfByKey()";
		log.info(me +  " vTitle = " + vTitle+" vRANK = " + vRANK+" vRNK2N = " + vRNK2N+" intDEGR2 = " + intDEGR2+" vBANKDT = " + vBANKDT+ " hid = " + vHid);
		
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap rtnMap=new HashMap();
		String vPFKEY="";
		String vPFBAS="";
		String vPFDAT="";
		String vDEGR2="";
		//職稱
		vTitle=StringUtil.nullToString(vTitle);
		//職等
		vRNK2N=StringUtil.nullToString(vRNK2N);
		//階別
		vRANK=StringUtil.nullToString(vRANK);
		//級別
		vDEGR2=String.valueOf(intDEGR2);
		vDEGR2=StringUtil.fillChar(StringUtil.nullToString(vDEGR2),2,"0",false);
		//實際支領KEY 本功別=0,表示pfkey組成為職稱+"00000"
		if (vRANK.equals("F") && !vTitle.matches("CG|CH|CK")){
			if (hid.equals("1A0")){ // >2024-9-1 加級數
				if ( DateUtil.compareDate( Globals.gPFNew , vBANKDT ) ){
					vPFKEY=vTitle+"000"+StringUtil.fillChar(vDEGR2,2,"0",false);
				}else{
					if (vTitle.matches("DX|DY"))	//DX契約資訊助理工程師  DY契約資訊副工程師
						vPFKEY=vTitle+"000"+StringUtil.fillChar(vDEGR2,2,"0",false);
					else
						vPFKEY=vTitle+"00000";
				}
			}else if(hid.equals("2A0")){
				vPFKEY=vTitle+"00000";
			}
		}else{
			if (vRANK3==0)
				vPFKEY=vTitle+"00000";
			else
				vPFKEY = vTitle + vRNK2N + vRANK3 + StringUtil.fillChar(vDEGR2,2,"0",false) ;
		}
		
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
			}
		}
		rtnMap.put("pfbas", vPFBAS);
		rtnMap.put("pfdat", vPFDAT);
		Date end = new Date();
		log.info(me+"(size):" + result.size()+" dataaccess:"+(end.getTime()-begin.getTime()));
	
		return rtnMap;
	}
	/**傳回 績效點數
	 * @param 
	 * 		  vRANK3  : 本功別
'             vTitle  : 職稱
'			  vRANK   : 階別
'             vRNK2N  : 職等
'             vDEGR2  : 級別
'  			  vBANKDT : 發薪日
'			  vCSPF   : map	
'			  vhid    : 醫院別		
'   @return   vPFKEY  : 實際支領KEY
'             vPFBAS  : 基本點數
'             vPFDAT  : 職等點數
	 * @throws SQLException 
	 */
	
	public HashMap getCsPfByMap(int vRANK3,String vTitle,String vRANK,String vRNK2N,int intDEGR2,String vBANKDT,String vHid,HashMap vCSPFMap,VGHDataAccess da) throws Exception{
		String me = getClass().getName() + ".getCsPfByKey()";
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap rtnMap=new HashMap();
		String vPFKEY="";
		int vpfbas=0;
		int vpfdat=0;
		String vDEGR2="";
		Pacspf cspfObj=null;
		//職稱
		vTitle=StringUtil.nullToString(vTitle);
		//職等
		vRNK2N=StringUtil.nullToString(vRNK2N);
		//階別
		vRANK=StringUtil.nullToString(vRANK);
		//級別
		vDEGR2=String.valueOf(intDEGR2);
		vDEGR2=StringUtil.fillChar(StringUtil.nullToString(vDEGR2),2,"0",false);
		//實際支領KEY 本功別=0,表示pfkey組成為職稱+"00000"
		if (vRANK.equals("F") && !vTitle.matches("CG|CH|CK")){
			if (hid.equals("1A0")){ // >2024-9-1 加級數
				if ( DateUtil.compareDate( Globals.gPFNew , vBANKDT ) ){
					vPFKEY=vTitle+"000"+StringUtil.fillChar(vDEGR2,2,"0",false);
				}else{
					if (vTitle.matches("DX|DY"))	//DX契約資訊助理工程師  DY契約資訊副工程師
						vPFKEY=vTitle+"000"+StringUtil.fillChar(vDEGR2,2,"0",false);
					else
						vPFKEY=vTitle+"00000";
				}
			}else if(hid.equals("2A0")){
				vPFKEY=vTitle+"00000";
			}
		}else{
			if (vRANK3==0)
				vPFKEY=vTitle+"00000";
			else
				vPFKEY = vTitle + vRNK2N + vRANK3 + StringUtil.fillChar(vDEGR2,2,"0",false) ;
		}
		
		rtnMap.put("pfkey", vPFKEY);
		
		cspfObj=(Pacspf) vCSPFMap.get(vPFKEY);
		if (cspfObj!=null){
			vpfbas= cspfObj.getPacsbase();
			vpfdat= cspfObj.getPacsdata();
		}
		rtnMap.put("pfbas", vpfbas);
		rtnMap.put("pfdat", vpfdat);
		
		return rtnMap;
	}
	/**取得支扣項目map	
	 * @param 
	 * 		  vcoddel : 刪除否(Y已刪除/N使用中),空白(全取)
'             vhid    : 醫院別		
'   @return   papayitm : map
	 * @throws SQLException 
	 */
	public HashMap<String, Papayitm> getPayitmMap(String vcoddel,String hid,VGHDataAccess da) throws SQLException{
		String me = getClass().getName() + ".getPayitmMap()";
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap<String, Papayitm> rtnMap=new HashMap();
		Papayitm oneObj=null; 
		String oneKey="";
		long starttime =0;
		sql = "select PAYCODE, PACODNMC, PACODDEL, PAYAUTH, PAYIFTAX, PAUPBAS, PAYREWARD, HID "+
		 	    "from "+this.tPayitm ;
		
		log.info(me+"  " + sql+"...params:"+ params.toString());
		try{
			starttime = System.currentTimeMillis();
			if (!"".equals(vcoddel) ){
				sql += " PACODNMC='N' ";
			}
			sql += " with ur";
			result = da.arraylistbyQuery(sql,META_TYPE);
			log.info(me+"  ...size:"+result.size());
			if(result.size()>0){
				for (Map<String,Object> data :result){
					oneObj=new Papayitm();
					PropertyUtils.copyProperties(oneObj, data);					
					oneKey=StringUtil.nullToString(oneObj.getPaycode());					
					rtnMap.put(oneKey, oneObj);
				}
			}
			log.info(me+"(size):" + result.size()+" 共花費："+(System.currentTimeMillis()-starttime)+"毫秒");
		}
		catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}
		finally{
		
		}
		
		return rtnMap;
	}
	/**取得職稱map	
	 * @param 
	 * 		  vhid    : 醫院別		
'   @return   Patitle : map
	 * @throws SQLException 
	 */
	public HashMap<String, Patitle> getTitleMap(String hid,VGHDataAccess da) throws SQLException{
		String me = getClass().getName() + ".getTitleMap()";
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap<String, Patitle> rtnMap=new HashMap();
		Patitle oneObj=null; 
		String oneKey="";
		long starttime =0;
		sql = "select PAYTITLK, PATITSUB, PACHIEF, PAYTITLP, PAYTITLC, PACODDEL, HID, PBTITCOD "+
		 	    "from "+this.tTitle +" with ur";
		
		log.info(me+"  " + sql+"...params:"+ params.toString());
		try{
			starttime = System.currentTimeMillis();
			
			result = da.arraylistbyQuery(sql,META_TYPE);
			log.info(me+"  ...size:"+result.size());
			if(result.size()>0){
				for (Map<String,Object> data :result){
					oneObj=new Patitle();
					PropertyUtils.copyProperties(oneObj, data);					
					oneKey=StringUtil.nullToString(oneObj.getPaytitlk());					
					rtnMap.put(oneKey, oneObj);
				}
			}
			log.info(me+"(size):" + result.size()+" 共花費："+(System.currentTimeMillis()-starttime)+"毫秒");
		}
		catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}
		finally{
		
		}
		
		return rtnMap;
	}
	//取得績效點數map	
	public HashMap getCsPfByAll(VGHDataAccess da,String hid) throws SQLException{
		String me = getClass().getName() + ".getCsPfByAll()";
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap rtnMap=new HashMap();
		Pacspf oneObj=null; 
		String cskey="";
		sql = "select PACSKEY, PACSBASE, PACSDATA, PACSNAME, COALESCE(PAYUPDID,'') as PAYUPDID "+
		 	    "from "+this.tCspfTbl +
		 	  "  with ur";
		
		Date begin = new Date();
		log.info(me+"  " + sql+"...params:"+ params.toString());
		if (hid.matches("1A0|2A0")){
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
'           111.1.19 公職退撫基金溯自110年1月1日免納入所得稅
'           111.5.2 契約主治不扣實物代金及房租
'           111.11.10 屏榮FD060 FD069自繳退休金 應稅所得要扣除
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
'			vtax         :所得稅
'*******************************************************************/
	public HashMap Sum_Pay_Deduct(ArrayList<Padatpay> datpayAry,String vRank,String vRANK2,String vRNK2N,int vDegre,String vGovDt,String vSalDt)throws Exception {
		String me = getClass().getName() + ".Sum_Pay_Deduct()";
		//log.info(me+"  vRank:" + vRank+" vRANK2:"+ vRANK2+" vRNK2N:"+ vRNK2N+" vDegre:"+ vDegre+" vGovDt:"+ vGovDt+" vSalDt:"+ vSalDt);
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap rtnMap=new HashMap();	
		int vrent_house =0;  //房租
		int vthing_money =0; //實物代金
		int vpay =0;        //總收入
		int vduduct =0;     //總支出
		int vnetpay=0;		//淨所得
		int vtaxPay_Y =0;   //應稅所得,列Y部分
		int vtaxpay=0;		//應稅所得,扣除實物代金及房租
		int vPension = 0;	//退撫基金
				
		int vchief =0;      //主管加給
		int vnon_rest =0;   //不休假獎金
		int vtraffic =0;    //交通費
		int vWelfare =0;    //福利互助金
		int vPapers =0;         //VP437超額獎金_論文補助 2014.4.1生效 2014.4.22改
		int vOvertime =0;       //VP200加班費2014.4.1生效 2014.4.22改
		int vDuty =0;           //VP455總值日值班費
		int vSelfRetire =0; 	//VD06|FD06勞工自繳退休金
		int vtax=0;				//FD050 VD030所得稅
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
			//主管加給 年終獎金--主管特支  考績獎金--主管特支
	        if ("FP03".equals(datpayObj.getPaycode().substring(0, 4)) || datpayObj.getPaycode().substring(0, 4).matches("VP124|VP134")){
	        	vchief += datpayObj.getPayamt();
	        }
	        //公職退撫基金溯自110年1月1日免納入所得稅 2023.7.1 FD07退撫離職金自願提撥,免納入所得稅
	        else if (datpayObj.getPaycode().substring(0, 4).matches("FD04|FD07") ){
	        	if ( vRank.matches("1|2|3|8|9") && !DateUtil.compareDate(vSalDt,Globals.gPensionFund) ) {
	        		vPension += datpayObj.getPayamt();
	        	}	
	        }
	        
	        //不休假獎金
	        else if("VP11".equals(datpayObj.getPaycode().substring(0, 4))){
	        	vnon_rest += datpayObj.getPayamt();
	        }
	        //交通費,己廢除    
//	        else if(datpayObj.getPaycode().substring(0, 4).matches("FP06|VP19")){
//	        	vtraffic += datpayObj.getPayamt();
//	        }
	        //福利互助金   
	        else if("VP400".equals(datpayObj.getPaycode())){
	        	vWelfare += datpayObj.getPayamt();
	        }
	        //自繳退休金  ,2022.11.10 屏榮FD06
	        else if(datpayObj.getPaycode().substring(0, 4).matches("VD06|FD06")){
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
	        //所得稅
	        else if(datpayObj.getPaycode().substring(0, 4).matches("FD05|VD03")){
	        	vtax += datpayObj.getPayamt();
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
	    
	    if (vflag){	//有本薪才有實物代金 房租
		    if ( DateUtil.compareDate(vSalDt, vSalBgdt) ){	//vSaldt < vSalBgdt '2005-7-20改 2005-7-1開始不計算房租及實物代金
		    	if (vRank.matches("5|6")){
		    		vrent_house = 400;
		    	    vthing_money = 930;
		    	}//2022.5.2契約主治沒有房租及實物代金
		    	else if (!vRank.matches("F")){
		    		if (vRank_P.matches("01|02|03") || (vRank_P.equals("T1") && vDegre<385 ) || "4".equals(vRank)){
			    		vrent_house = 500;
			    	    vthing_money = 930;
			    	}else if (vRank_P.matches("04|05|06|07|P3") || (vRank_P.equals("T1") && vDegre>=385 )){
			    		vrent_house = 600;
			    	    vthing_money = 930;
			    	}else if (vRank_P.matches("08|09|10|11|12|13|14|P1|P2") ){
			    		 vrent_house = 700;
			    		 vthing_money = 930;
			    	}
		    	} 
		    }	
		    else	//2005-8-29改 以84.7.1為基準計算 之前要扣 之後不扣 (初任公職日<基準日,有房租及實物代金)
		    {
		    	if (vRank.matches("5|6")){
		    		if ( DateUtil.compareDate(vGovDt, vHourseDt) )
		    			vrent_house = 400;
		    		if ( DateUtil.compareDate(vGovDt, vThingDt) )
		    			vthing_money = 930;
		    	}
		    	//2022.5.2契約主治沒有房租及實物代金
		    	else if (!vRank.matches("F")){
		    		if (vRank_P.matches("01|02|03") || (vRank_P.equals("T1") && vDegre<385 ) || "4".equals(vRank)){
			    		if ( DateUtil.compareDate(vGovDt, vHourseDt) )
			    			vrent_house = 500;
			    		if ( DateUtil.compareDate(vGovDt, vThingDt) )
			    			vthing_money = 930;
			    	}else if (vRank_P.matches("04|05|06|07|P3") || (vRank_P.equals("T1") && vDegre>=385 )){
			    		if ( DateUtil.compareDate(vGovDt, vHourseDt) )
			    			vrent_house = 600;
			    		if ( DateUtil.compareDate(vGovDt, vThingDt) )
			    			vthing_money = 930;
			    	}else if (vRank_P.matches("08|09|10|11|12|13|14|P1|P2") ){	
			    		if ( DateUtil.compareDate(vGovDt, vHourseDt) )
			    			vrent_house = 700;
			    		if ( DateUtil.compareDate(vGovDt, vThingDt) )
			    			vthing_money = 930;
			    	}
		    	} 
		    }
	    }
	    //淨所得=總所得-總支出
	    vnetpay = vpay - vduduct;
	    if (vflag){
			//應稅所得=datpay.getPayiftax()="Y"-實物代金-房租-自繳退休金-公職退撫基金
		    vtaxpay = vtaxPay_Y - vrent_house - vthing_money - vSelfRetire -vPension;
		    if (vtaxpay<0) vtaxpay=0;
	    }else{
	    	vtaxpay = vtaxPay_Y;
	    }
	    rtnMap.put("tax", vtax);
	    rtnMap.put("pay", vpay);
		rtnMap.put("duduct", vduduct); 
		rtnMap.put("netpay", vnetpay);
		rtnMap.put("taxpay", vtaxpay); //應稅所得,扣除實物代金及房租
		rtnMap.put("thing_money", vthing_money); 
		rtnMap.put("rent_house", vrent_house);
		rtnMap.put("retire", vPension+vSelfRetire);
		return rtnMap;
	}
	
	//身分證字號與居留證(統一證)編號檢核程式碼 http://experimentrecord.blogspot.tw
	public static boolean isValidIDorRCNumber(String str) {

	    if (str == null || "".equals(str)) {
	        return false;
	    }

	    final char[] pidCharArray = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	    // 原身分證英文字應轉換為10~33，這裡直接作個位數*9+10
	    final int[] pidIDInt = { 1, 10, 19, 28, 37, 46, 55, 64, 39, 73, 82, 2, 11, 20, 48, 29, 38, 47, 56, 65, 74, 83, 21, 3, 12, 30 };

	    // 原居留證第一碼英文字應轉換為10~33，十位數*1，個位數*9，這裡直接作[(十位數*1) mod 10] + [(個位數*9) mod 10]
	    final int[] pidResidentFirstInt = { 1, 10, 9, 8, 7, 6, 5, 4, 9, 3, 2, 2, 11, 10, 8, 9, 8, 7, 6, 5, 4, 3, 11, 3, 12, 10 };

	    // 原居留證第二碼英文字應轉換為10~33，並僅取個位數*8，這裡直接取[(個位數*8) mod 10]
	    final int[] pidResidentSecondInt = {0, 8, 6, 4, 2, 0, 8, 6, 2, 4, 2, 0, 8, 6, 0, 4, 2, 0, 8, 6, 4, 2, 6, 0, 8, 4};

	    str = str.toUpperCase();// 轉換大寫
	    final char[] strArr = str.toCharArray();// 字串轉成char陣列
	    int verifyNum = 0;

	    /* 檢查身分證字號 */
	    if (str.matches("[A-Z]{1}[1-2]{1}[0-9]{8}")) {
	        // 第一碼
	        verifyNum = verifyNum + pidIDInt[Arrays.binarySearch(pidCharArray, strArr[0])];
	        // 第二~九碼
	        for (int i = 1, j = 8; i < 9; i++, j--) {
	            verifyNum += Character.digit(strArr[i], 10) * j;
	        }
	        // 檢查碼
	        verifyNum = (10 - (verifyNum % 10)) % 10;

	        return verifyNum == Character.digit(strArr[9], 10);
	    }

	    /* 檢查統一證(居留證)編號 (2031/1/1停用) 
	    verifyNum = 0;
	    if (str.matches("[A-Z]{1}[A-D]{1}[0-9]{8}")) {
	        // 第一碼
	        verifyNum += pidResidentFirstInt[Arrays.binarySearch(pidCharArray, strArr[0])];
	        // 第二碼
	        verifyNum += pidResidentSecondInt[Arrays.binarySearch(pidCharArray, strArr[1])];
	        // 第三~八碼
	        for (int i = 2, j = 7; i < 9; i++, j--) {
	            verifyNum += Character.digit(strArr[i], 10) * j;
	        }
	        // 檢查碼
	        verifyNum = (10 - (verifyNum % 10)) % 10;

	        return verifyNum == Character.digit(strArr[9], 10);
	    }

	     檢查統一證(居留證)編號 (2021/1/2實施) 
	    verifyNum = 0;
	    if (str.matches("[A-Z]{1}[89]{1}[0-9]{8}")) {
	        // 第一碼
	        verifyNum += pidResidentFirstInt[Arrays.binarySearch(pidCharArray, strArr[0])];
	        // 第二~八碼
	        for (int i = 1, j = 8; i < 9; i++, j--) {
	            verifyNum += Character.digit(strArr[i], 10) * j;
	        }
	        // 檢查碼
	        verifyNum = (10 - (verifyNum % 10)) % 10;

	        return verifyNum == Character.digit(strArr[9], 10);
	    }*/
	    
	    return false;
	}
	/**傳回 薪津表/扣款表/薪津表(補發扣) 對應
	 * @param selEn 表冊 Prt_M_1薪津表 Prt_M_2扣款表 Prt_M_3薪津表(補發扣) Bank_T銀行媒體傳輸
	 * @param itm 項目
	 * @return 報表中文名稱
	 * Ex: getPrtNmBysal("Prt_M_1",1,"1A0") return 護理_師級 
	 */
	public static String getPrtNmBysal(String selEn,int itm,String hid){		
		String itmCd = " ";
		if (hid.equals("1A0")){
			//薪津表/扣款表
			if (selEn.matches("Prt_M_1|Prt_M_2")){
				if (itm==0) return "總計";
				if (itm==1) return "護理_師級";
				if (itm==2) return "護理_契約";
				if (itm==3) return "職員_公職";
				if (itm==4) return "醫事_契約";
				if (itm==5) return "醫事_師級";
				if (itm==6) return "醫師_進修,實習";
				if (itm==7) return "醫師_公職";
				if (itm==8) return "醫師_師級";
				if (itm==9) return "醫師_聘用";
				if (itm==10) return "醫師_契約";
				if (itm==11) return "不分科";
				if (itm==12) return "大武";
				if (itm==13) return "兼職";
				if (itm==14) return "技工友";
				if (itm==15) return "計畫";
				if (itm==16) return "駐衛警";
				if (itm==17) return "外籍照服員";
			}	
			//薪津表(補發扣) 
			else if (selEn.matches("Prt_M_3")){
				if (itm==0) return "總計";
				if (itm==1) return "明細";
			}
			//Prt_M_6銀行轉帳明細表 Bank_T銀行轉帳明細表
			else if (selEn.matches("Prt_M_6|Bank_T")){
				if (itm==0) return "薪津";
				if (itm==1) return "公儲";
				if (itm==2) return "停薪";
			}
			
		}
		else if (hid.equals("2A0")){
			//薪津表/扣款表
			if (selEn.matches("Prt_M_1|Prt_M_2")){
				if (itm==0) return "總計";
				if (itm==1) return "護理_師級";
				if (itm==2) return "護理_約用";
				if (itm==3) return "職員_公職";
				if (itm==4) return "醫事_約用";
				if (itm==5) return "醫事_師級";
				if (itm==6) return "醫師_師級+聘用";
				if (itm==7) return "醫師_約用";
				if (itm==8) return "技工友";
			}	
			//薪津表(補發扣)
			else if (selEn.matches("Prt_M_3")){
				if (itm==0) return "總計";
				if (itm==1) return "明細";
			}
			//Prt_M_6銀行轉帳明細表 Bank_T銀行轉帳明細表
			else if (selEn.matches("Prt_M_6|Bank_T")){
				if (itm==0) return "薪津";
				if (itm==1) return "公儲";
			}
		}
		else if (hid.equals("3A0")){
			//薪津表/扣款表
			if (selEn.matches("Prt_M_1|Prt_M_2")){
				if (itm==0) return "總計";
				if (itm==1) return "公職";
				if (itm==2) return "工級";
				if (itm==3) return "契約";
			}
			//薪津表(補發扣)
			else if (selEn.matches("Prt_M_3")){
				if (itm==0) return "總計";
				if (itm==1) return "明細";
			}
			//Prt_M_6銀行轉帳明細表 Bank_T銀行轉帳明細表
			else if (selEn.matches("Prt_M_6|Bank_T")){
				if (itm==0) return "薪津";
				if (itm==1) return "優惠存款";
			}
		}
		return itmCd;
	}
	/**傳回 9A 9B 執行業務者業別代號對照表
	 * @param itm 轉帳項目
	 * @return 執行業務者業別代號
	 * Ex: getCatgCd(16) return 10 
	 */
	public static String getCatgCd(int itm){		
		String itmCd = " ";
		if (itm==16) return "10";	//律師
		if (itm==17) return "20";	//技師
		if (itm==18) return "21";	//建築師
		if (itm==19) return "98";	//稿費 演講費
		if (itm==28) return "93";	//專利代理人 2011.1.14
		if (itm==29) return "70";	//表演人 2011.1.17
		if (itm==34) return "56";	//牙體技術師 2013.1.31
		if (itm==38) return "76";	//一般經紀人 2013.7.24
		if (itm==50) return "42";	//醫事檢驗師 2015.1.20
		if (itm==49) return "42";	//醫事檢驗師 2025.10.3
		if (itm==51) return "13";	//地政士 2017.1.11
		if (itm==53) return "17";	//不動產估價師 2018.1.22
		if (itm==54) return "50";	//中醫師 2019.1.22
		if (itm==55) return "30";	//內科醫師 2020.1.20
		if (itm==56) return "36";	//牙科醫師 2020.1.20
		if (itm==61) return "91";	//牙科醫師 2020.1.20
		if (itm==62) return "18";	//租賃 2024.1.4
		if (itm==63) return "46";	//支援醫師--醫師經核准至該他醫療機構服務但與該他醫療機構不具僱傭關係者 2024.1.4
		if (itm==71) return "61";	//書畫家、版畫家 2024.1.27
		if (itm==72) return "16";	//民間公證人 2024.1.27
		return itmCd;
	}
	/**傳回 補充保費保險別
	 * @param itm 代碼
	 * @return 保險中文名稱
	 * Ex: getInsuident(1) return 公保 
	 */
	public static String getInsuident(int itm){		
		String itmCd = " ";
		if (itm==0) return "無";	
		if (itm==1) return "公保";
		if (itm==2) return "勞保";
		return itmCd;
	}
	/**傳回 考勤_假別中文
	 * @param itm 假況
	 * @return 中文說明
	 * Ex: getOfftypNm(01) return 事 
	 */
	public static String getOfftypNm(String itm){		
		String itmCd = " ";
		if (itm.equals("01")) return "事假";	
		if (itm.equals("02")) return "病假";
		if (itm.equals("34")) return "家庭照顧假";
		if (itm.equals("35")) return "生理假";
		if (itm.equals("53")) return "公假-居家照護";
		return itmCd;
	}
	/**傳回 考勤_職號第2碼中文
	 * @param itm 職號第2碼
	 * @return 中文說明
	 * Ex: getPosNm('K') return '契約' 
	 */
	public static String getPosNm(String itm){		
		String itmCd = " ";
		if (itm.equals("K")) return "契約";	
		if (itm.equals("F")) return "半日契約";
		if (itm.equals("P")) return "不分科";
		if (itm.equals("T")) return "研究助理";
		return itmCd;
	}
	/**傳回 考勤_每月有效天數,全日=30,半日=15
	 * @param itm 職號第2碼
	 * @return 天數
	 * Ex: getDayLimByPos('K') return 30 
	 */
	public int getDayLimByPos(String itm){		
		int itmDay = 30;
		if (itm.equals("K")) return 30;	
		if (itm.equals("F")) return 15;
		return itmDay;
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
