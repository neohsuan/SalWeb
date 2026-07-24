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
import gov.vghks.sal.model.PsInsu;
import gov.vghks.sal.model.Psbasic;

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
	private String tinsu = "PS.PSINSU";			//員工保險
	private String tbasic = "PS.PSBASIC";		//基本資料檔
	private String ttab0 = "PS.PSTAB0";			//人事代碼檔
	private String texperi = "PS.PSEXPERI";		//經歷異動資料檔
	
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
		texperi  = tabInfo.texperi;		//經歷異動資料檔
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
