package gov.vghks.sal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import gov.vghks.common.util.StringUtil;
import gov.vghks.common.util.VGHDataAccess;
import gov.vghks.sal.Hospitals;
import gov.vghks.sal.TablesDSN;
import gov.vghks.sal.model.Papayitm;
import gov.vghks.sal.model.PsInsu;
import gov.vghks.sal.model.Psbasic;
import gov.vghks.sal.model.Pscode;

public class PsDSNDAO {
	String gClassName = PsDSNDAO.class.getName();
	static Logger log = Logger.getLogger(PsDSNDAO.class);  //呼叫log4j
	private VGHDataAccess da = null;
	static ArrayList<Map<String, Object>> result = null;
	static ArrayList<Object> params = new ArrayList<Object>();
	private static final int META_TYPE = 2; 			//回傳map中欄位名稱是小寫
	private static boolean connFlg = false;
	static String g_dataSource="";
	private String hid="";
	private String sql="";
	private String tinsu   = "PS.PSINSU";		//員工保險
	private String tbasic  = "PS.PSBASIC";		//基本資料檔
	private String ttab0   = "PS.PSTAB0";		//人事代碼檔
	private String texperi = "PS.PSEXPERI";		//經歷異動資料檔
	private String tnurbas = "PS.PSNURBAS";		//護理人員--基本資料檔
	private String tcode   = "PS.PSCODE";		//人事代碼檔（明細）
	
	public PsDSNDAO() {
		super();
	}
	
	public PsDSNDAO(VGHDataAccess da1) {
		super();
		this.da=da1;
		connFlg = true;
	}
	public PsDSNDAO(VGHDataAccess da1,String hid) {
		super();
		this.da=da1;
		this.hid=hid;
		connFlg = true;
		TablesDSN tabInfo = TablesDSN.getTables(hid);
		
		tinsu  = tabInfo.tinsu;			//員工保險
		tbasic = tabInfo.tbasic;		//基本資料檔
		ttab0  = tabInfo.ttab0;			//人事代碼檔
		tcode  = tabInfo.tcode;			//人事代碼檔
		texperi = tabInfo.texperi;		//經歷異動資料檔
		tnurbas = tabInfo.tnurbas;		//護理人員--基本資料檔
	}
	/**取得屏榮護理契約限制晉級map 在職,職號=契約,職類=護理師,不列(半日,停薪)找出護理單位,要設級數範圍	
	 * @param 		
	   @return   map(Q004,210|222) 卡號,級數範圍 
	 * @throws SQLException 
	 */
	public HashMap<String, HashMap> getNurContLimMap(String hid,VGHDataAccess da) throws SQLException{
		String me = getClass().getName() + ".getNurContLimMap()";
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap<String, HashMap> rtnMap=new HashMap<String, HashMap>();
		HashMap<String, Integer> rngMap=new HashMap<String, Integer>();
		String oneKey="";
		String deptc="";
		int vMin=0;
		int vMax=0;
		long starttime =0;
		sql = "SELECT RTRIM(PBCARDNO) pbcardno,pbnamefd,a.pbidtno,pbupgdat,PBPAGRAD,pbtitcod,COALESCE(PSNDEPTC,'') PSNDEPTC "+
		 	    "from "+this.tbasic +" a "+
		 	   " inner JOIN "+ tnurbas +" B ON a.hid=b.hid AND a.PBIDTNO =b.pbidtno " +
		 	  " WHERE a.hid=? and pbposmsk='Y' AND substr(PBPOSINO,1,2)='DK' AND substr(PBPOSTUS,1,1)<>'3' AND pbtitnme NOT LIKE '半日%' AND PBPAGRAD<>'H' AND PBCATEGO IN ('04','05','06','24','28')"+
		 	  " ORDER BY pbcardno WITH ur ";
		try{
			starttime = System.currentTimeMillis();
			params.add(hid);
			
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"  " + sql+"...params:"+ params.toString());
			log.info(me+"  ...size:"+result.size());
			if(result.size()>0){
				for (Map<String,Object> data :result){
					oneKey=StringUtil.nullToString(data.get("pbcardno"));
					deptc =StringUtil.nullToString(data.get("psndeptc"));
					vMin=0;
					vMax=0;
					rngMap=new HashMap<String, Integer>();
					if ( deptc.equals("01") ) {//01麻醉護理師(12-24)
						vMin=212;
						vMax=224;
					}	
					else if ( deptc.matches("02|03|04|05") ){//02 ICU重症病房,03洗腎室,04手術室產房,05病房單位 10 - 22
						vMin=210;
						vMax=222;
					}	
					else if ( deptc.matches("06") ){//06特殊單位護理師 7 - 19
						vMin=207;
						vMax=219;
					}
					else if ( deptc.matches("07") ){//07一般門診(注射室、癌症中心) 7-15
						vMin=207;
						vMax=215;
					}
					else if ( deptc.matches("08") ){//08輪班單位 3 - 11
						vMin=203;
						vMax=211;
					}
					else if ( deptc.matches("09") ){//09非輪班單位 1 - 8
						vMin=201;
						vMax=208;
					}else if ( deptc.matches("10") ){//10專科護理師 12 - 19
						vMin=212;
						vMax=219;
					}else if ( deptc.matches("11") ){//11訓練中專師 12 - 15
						vMin=212;
						vMax=215;
					}else if ( deptc.matches("12") ){//12一般門診護理師 5 - 13
						vMin=205;
						vMax=213;
					}else if ( deptc.matches("13") ){//13檢查室技術員 11-21
						vMin=211;
						vMax=221;
					}
					rngMap.put("min", vMin);
					rngMap.put("max", vMax);
					rtnMap.put(oneKey, rngMap);
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
	/**取得契約限制晉級map	
	 * @param 		
	   @return   map(HI47,15) 卡號,級數上限 
	 * @throws SQLException 
	 */
	public HashMap<String, Integer> getContLimMap(VGHDataAccess da) throws SQLException{
		String me = getClass().getName() + ".getPayitmMap()";
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap<String, Integer> rtnMap=new HashMap<String, Integer>();
		Pscode oneObj=null; 
		String oneKey="";
		String strType="";
		int intLimit=0;
		long starttime =0;
		sql = "select PCTYPE, rtrim(PCSYSNO) PCSYSNO, PCSYSNM, PCCANCEL, PCSORT "+
		 	    "from "+this.tcode +
		 	  " where PCTYPE IN ('002','007','008','009','011','012','013','024','025','026','027','028','032','034') "+
		 	    " AND PCCANCEL =? with ur ";
		try{
			starttime = System.currentTimeMillis();
			params.add("N");
			
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"  " + sql+"...params:"+ params.toString());
			log.info(me+"  ...size:"+result.size());
			if(result.size()>0){
				for (Map<String,Object> data :result){
					oneObj=new Pscode();
					intLimit=0;
					PropertyUtils.copyProperties(oneObj, data);					
					oneKey=StringUtil.nullToString(oneObj.getPcsysno());
					strType=StringUtil.nullToString(oneObj.getPctype());
					if (strType.equals("002"))
						intLimit = 15;
					else if (strType.equals("007"))
						intLimit = 25;
					else if (strType.equals("008"))
						intLimit = 27;
					else if (strType.equals("009"))
						intLimit = 32;
					else if (strType.equals("011"))
						intLimit = 18;
					else if (strType.equals("012"))
						intLimit = 20;
					else if (strType.equals("013"))
						intLimit = 23;
					else if (strType.equals("024"))
						intLimit = 30;
					else if (strType.equals("025"))
						intLimit = 11;
					else if (strType.equals("026"))
						intLimit = 17;
					else if (strType.equals("027"))
						intLimit = 35;
					else if (strType.equals("028"))
						intLimit = 12;
					else if (strType.equals("032"))
						intLimit = 7;
					else if (strType.equals("034"))
						intLimit = 1;
					rtnMap.put(oneKey, intLimit);
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
	
	//取得基本資料檔 BY idno
	public Psbasic getPsBasById(String idtno,String hid,VGHDataAccess da) throws SQLException{
		String me = getClass().getName() + ".getPsBasById()";
		ArrayList<Object> params = new ArrayList<Object>();
		long starttime =0;
		Psbasic oneObj=null; 
		
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		
		sql = "SELECT pbidtno ,pbnamefd,pbposino,pbposmsk,pbcardno,pbcatego,pbwkcode,b1.PASNAME pbwkname,pbtitcod,b2.PASNAME pbtitnme,pbpagrad,pbpapont,pblastot,pbporank,pbposdat,pbpostus,pbpchief,PBGOADAT,PBGOCDAT,pbdomadr "+
		 	    "from "+this.tbasic +" a " + 
		 	    "LEFT OUTER JOIN "+this.ttab0 +" b1 on  a.pbwkcode = b1.pastbky "+
		 	    "LEFT OUTER JOIN "+this.ttab0 +" b2 on  a.pbwkcode = b1.pastbky "+
		 	  " where HID= ? " +  
		 	  " and PBIDTNO =?  WITH UR";
		params.add(hid);
		params.add(idtno);
		starttime = System.currentTimeMillis();
		log.info(me+"  " + sql+"...params:"+ params.toString());
		
		if(da==null){
			connFlg = false;
			g_dataSource=hospInfo.dataSourceDSN;
			da=new VGHDataAccess(g_dataSource);
		}
		try{
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			if(result.size()>0){
				for (Map<String,Object> data :result){
					oneObj=new Psbasic();
					PropertyUtils.copyProperties(oneObj, data);					
				}
			}
			log.info(me+"(size):" + result.size()+"  ...********* dataaccess:共花費："+(System.currentTimeMillis()-starttime)+"毫秒");
		}
		catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}
		finally{
			closeAll(da,null,connFlg);
		}
	
		return oneObj;
	}
	//取得保險檔,依身分證,目前只限高榮	
	public PsInsu getInsuByidno(String idtno,String hid,VGHDataAccess da) throws SQLException{
		String me = getClass().getName() + ".getInsuByidno()";
		ArrayList<Object> params = new ArrayList<Object>();
		long starttime =0;
		PsInsu oneObj=null; 
		
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		
		
		sql = "SELECT PBIDTNO, INVER, HID, PBSUPFG, PBCARDNO, PBNAMEFD, PBBIRTHD, PBPOSINO, PBPORANK, PBPAGRAD, PBPAPONT, PBPCHIEF, PBTITCOD, PBTITNME, INSTUS, PBLSVDAT, INREAL, LBREAL, DPINIG, LBINIG, WRINIG, OCCINIG, SELFVOL, LBSPFLG, DPSPFLG, INUPDT, INRMK, UPDNM, UPDTM "+
		 	    "from "+this.tinsu +
		 	  " where HID= ? " +  
		 	  " and INVER = 0 " +
		 	  " and PBIDTNO =?  WITH UR";
		params.add(hid);
		params.add(idtno);
		starttime = System.currentTimeMillis();
		log.info(me+"  " + sql+"...params:"+ params.toString());
		
		if(da==null){
			connFlg = false;
			//改用列舉
			g_dataSource=hospInfo.dataSourceDSN;
			da=new VGHDataAccess(g_dataSource);
		}
		try{
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			if(result.size()>0){
				for (Map<String,Object> data :result){
					oneObj=new PsInsu();
					PropertyUtils.copyProperties(oneObj, data);					
				}
			}
			log.info(me+"(size):" + result.size()+"  ...********* dataaccess:共花費："+(System.currentTimeMillis()-starttime)+"毫秒");
		}
		catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}
		finally{
			closeAll(da,null,connFlg);
		}
	
		return oneObj;
	}
	//取得保險檔,列退保=2,生效日=當月,不列(臨時工QQ,研究助理ET,退保=2,生效日<當月),目前只限高榮	
	public HashMap<String, PsInsu> getInsuMap(String hid,VGHDataAccess da) throws SQLException{
		String me = getClass().getName() + ".getInsuMap()";
		ArrayList<Object> params = new ArrayList<Object>();
		long starttime =0;
		PsInsu oneObj=null;
		String strKey = "";
		HashMap<String, PsInsu> rtnMap = new HashMap<String, PsInsu>();
		
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		sql = "SELECT PBIDTNO, INVER, HID, PBSUPFG, PBCARDNO, PBNAMEFD, PBBIRTHD, PBPOSINO, PBPORANK, PBPAGRAD, PBPAPONT, PBPCHIEF, PBTITCOD, PBTITNME, INSTUS, PBLSVDAT, INREAL, LBREAL, DPINIG, LBINIG, WRINIG, OCCINIG, SELFVOL, LBSPFLG, DPSPFLG, INUPDT, INRMK, UPDNM, UPDTM "+
		 	    "from "+this.tinsu +
		 	  " where HID= ? and INVER = 0 and (INSTUS <>2 OR (INSTUS=2 AND YEAR(pblsvdat)=YEAR(CURRENT date) AND month(pblsvdat)=month(CURRENT date) )) and substr(PBPOSINO,1,2) not in ('ET','QQ') WITH UR";
		params.add(hid);
		starttime = System.currentTimeMillis();
		log.info(me+"  " + sql+"...params:"+ params.toString());
		
		if(da==null){
			connFlg = false;
			//改用列舉
			g_dataSource=hospInfo.dataSourceDSN;
			da=new VGHDataAccess(g_dataSource);
		}
		try{
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			if(result.size()>0){
				for (Map<String,Object> data :result){
					oneObj=new PsInsu();
					PropertyUtils.copyProperties(oneObj, data);	
					strKey=StringUtil.nullToString(oneObj.getPbidtno());

					rtnMap.put(strKey, oneObj);
				}
			}
			log.info(me+"(size):" + result.size()+"  ...********* dataaccess:共花費："+(System.currentTimeMillis()-starttime)+"毫秒");
		}
		catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}
		finally{
			closeAll(da,null,connFlg);
		}
	
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
