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
import gov.vghks.sal.model.Off0102v;

public class PsNTDAO {
	String gClassName = PsNTDAO.class.getName();
	static Logger log = Logger.getLogger(SalNTDAO.class);  //呼叫log4j
	private VGHDataAccess da = null;
	static ArrayList<Map<String, Object>> result = null;
	static ArrayList<Object> params = new ArrayList<Object>();
	private static final int META_TYPE = 2; 			//回傳map中欄位名稱是小寫
	private static boolean connFlg = false;
	static String g_dataSourceNT="";
	private String hid="";
	private String sql="";
	private String gCardGrp="";
	private String tOff0102V = "PS.OFF0102V";			//考核─請假─契約人員事病假統計表
	
	
	public PsNTDAO() {
		super();
	}
	
	public PsNTDAO(VGHDataAccess da1) {
		super();
		this.da=da1;
		connFlg = true;
	}
	public PsNTDAO(VGHDataAccess da1,String hid) {
		super();
		this.da=da1;
		this.hid=hid;
		connFlg = true;
	}
	//取得契約人員事病假map,不列(研究助理)	
	public ArrayList<Map<String, Object>> getleaveByPS(VGHDataAccess da,String hid) throws SQLException{
		String me = getClass().getName() + ".getleaveByPS()";
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap<String, Off0102v> rtnMap=new HashMap<String, Off0102v>();
		long starttime =0;
		Off0102v oneObj=null; 
		String userid="";
		String offtype="";
		
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		gCardGrp=hospInfo.cardGrp;
		
		sql = "SELECT YM, USERID, EMPNAME, POS, OFFTYPE, OFFHOUR, OFFHOURSUM, OFFDAY, OFFDAYSUM, NOPAY, PAYHALF "+
		 	    "from "+this.tOff0102V +
		 	  " where substr(userid,1,1) in " + gCardGrp + 
		 	  " and OFFTYPE NOT IN ('46','53') "+
		 	  " and POS NOT IN ('T') "+
		 	  " ORDER BY USERID,OFFTYPE WITH UR";
		
		starttime = System.currentTimeMillis();
		log.info(me+"  " + sql+"...params:"+ params.toString());
		
		if(da==null){
			connFlg = false;
			//改用列舉
			g_dataSourceNT=hospInfo.dataSourceNT;
			da=new VGHDataAccess(g_dataSourceNT);
		}
		try{
			result = da.arraylistbyQuery(sql,META_TYPE);
			
			/*if(result.size()>0){
				for (Map<String,Object> data :result){
					oneObj=new Off0102v();
					PropertyUtils.copyProperties(oneObj, data);
					
					userid=StringUtil.nullToString(oneObj.getUserid());
					offtype=StringUtil.nullToString(oneObj.getOfftype());
					
					rtnMap.put(userid+offtype, oneObj);
				}
			}*/
			
			log.info(me+"(size):" + result.size()+"  ...********* dataaccess:共花費："+(System.currentTimeMillis()-starttime)+"毫秒");
		}
		catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}
		finally{
		
		}
	
		return result;
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
