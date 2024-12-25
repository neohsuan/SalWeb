package gov.vghks.sal.dao;

import gov.vghks.common.util.DateUtil;
import gov.vghks.common.util.StringUtil;
import gov.vghks.common.util.VGHDataAccess;
import gov.vghks.sal.Globals;
import gov.vghks.sal.model.Oprole;
import gov.vghks.sal.model.Pabank;
import gov.vghks.sal.model.Pabaspay;
import gov.vghks.sal.model.Pabaspsn;
import gov.vghks.sal.model.Pacspf;
import gov.vghks.sal.model.Padatpay;
import gov.vghks.sal.model.Padatpsn;
import gov.vghks.sal.model.Padept;
import gov.vghks.sal.model.Paexpftbl;
import gov.vghks.sal.model.Pahist;
import gov.vghks.sal.model.Papayitm;
import gov.vghks.sal.model.Parank4tbl;
import gov.vghks.sal.model.Patitle;
import gov.vghks.sal.model.Patracode;
import gov.vghks.sal.model.Patrapay;
import gov.vghks.sal.model.Patrapsn;
import gov.vghks.sal.model.RankCode;
import gov.vghks.sal.util.PaCommUtil;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

/*import org.json.JSONArray;
import org.json.JSONObject;*/
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SalNTDAO {
	String gClassName = SalNTDAO.class.getName();
	private String sql="";
	static ArrayList<Map<String, Object>> result = null;
	ArrayList<Object> params=new ArrayList<Object>();
	private String dataSourceName = Globals.dataSource_NT;
	static Logger log = Logger.getLogger(SalNTDAO.class);  //呼叫log4j
	private VGHDataAccess da = null;
	private static final int META_TYPE = 2; 			//回傳map中欄位名稱是小寫
	private static boolean connFlg = false; 			// false:由此物件new conn，true:由呼叫物件傳入conn
	private String hid="";
	
	private String tOpRole = "OPSMAIN.OPROLE";				//手術室角色分派
	private String tBasPay = "PA.PABASPAY";			//基本支扣
	private String tBasPsn = "PA.PABASPSN";			//基本檔
	private String tTraCode = "PA.PATRACODE";			//1異動代碼
	private String tDept 	= "PA.PADEPT";				//2單位代碼
	private String tTitle   = "PA.PATITLE";				//3職稱
	private String tRankCode = "PA.PARANKCODE";			//4階別代碼
	private String tCatgTbl = "PA.PACATGTBL";			//5人工造冊應計轉帳項目代碼 
	private String tExpfTbl = "PA.PAEXPFTBL";			//6績效額外點數對照表 
	private String tRnk4Tbl = "PA.PARNK4TBL";			//7適用專業加給對照表 
	private String tBankDtTbl = "PA.PABANK";				//8發薪日及入帳日對應檔 
	private String tTaxTbl = "PA.PATAXTBL";				//9薪資所得扣繳稅額表 
	private String tPftlTbl = "PA.PAPFTL";					//10全院績效彙總 
	private String tDpinTbl = "PA.PADPINTBL";			//全民健康保險負擔金額表 
	private String tLbinTbl = "PA.PALBINTBL";			//勞保投保金額對照表
	private String tLbrpTbl = "PA.PALBRPTBL";			//勞工退休金月提繳工資分級表
	private String tPosiTbl = "PA.PAPOSI";					//職等支扣--專業加給及主管加給
	private String tRankTbl = "PA.PARANK";					//階級支扣
	private String tCspfTbl = "PA.PACSPF";					//工作獎金支扣
	private String tDatPsnTbl = "PA.PADATPSN";					//月支個人明細
	private String tDatPsnDw = "PA.PADATPSN_DW";					//月支個人明細_歷史
	private String tDatPayTbl = "PA.PADATPAY";					//月支支扣明細
	private String tDatPayDw = "PA.PADATPAY_DW";				//月支支扣明細_歷史
	private String tPayIItm = "PA.PAPAYITM";					//支扣項目彙總
	private String tBasAdr = "PA.PABASADR";					//地址
	private String tHist = "PA.PAHIST";					//歷史檔
	private String tTraPsnTbl = "PA.PATRAPSN";					//異動個人明細
	private String tTraPsnDw = "PA.PATRAPSN_DW";					//異動個人明細_歷史
	private String tTraPayTbl = "PA.PATRAPAY";					//異動支扣明細
	private String tTraPayDw = "PA.PATRAPAY_DW";					//異動支扣明細_歷史
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	
	
	public SalNTDAO() {
		super();
	}
	
	public SalNTDAO(VGHDataAccess da1) {
		super();
		this.da=da1;
		connFlg = true;
	}
	public SalNTDAO(VGHDataAccess da1,String hid) {
		super();
		this.da=da1;
		connFlg = true;
		this.hid=hid;
	}	
	//item:0 取得opsmaon.oprole手術室角色分派.單位清單/1:取得成本中心  
	public ArrayList<Oprole> getOpRoleDept(String gAdmin,String item,VGHDataAccess da) throws SQLException{
		String me = getClass().getName() + ".getOpRoleDept()";
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Oprole> aryRtn=new ArrayList<Oprole>();
		Oprole tmpOne=null;
		if (item.equals("0")){
			sql = "SELECT DEPT from "+this.tOpRole;
			if (gAdmin.equals("N"))
				sql+=" WHERE DEPT NOT IN ('950E','950') ";
			sql+=" GROUP BY DEPT ORDER BY DEPT WITH UR";
		}else if (item.equals("1")){
			sql = "SELECT DEPT,USERNAME from "+this.tOpRole +" where USERID=DEPT ";
			if (gAdmin.equals("N"))
				sql+=" and DEPT NOT IN ('950E','950') ";
			sql+="with ur";
		}
		try{
			if (da == null) {
				da = new VGHDataAccess(this.dataSourceName); 
				connFlg = true;
			}
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"  " + sql+"...params:"+ params.toString()+"...size:"+result.size());
			if(result.size()>0){
				if (item.equals("0")){
					for (Map<String,Object> data :result){
						tmpOne = new Oprole();
						tmpOne.setDept(StringUtil.nullToString(data.get("dept")));
						aryRtn.add(tmpOne);
					}
				}else if (item.equals("1")){
					for (Map<String,Object> data :result){
						tmpOne = new Oprole();
						tmpOne.setDept(StringUtil.nullToString(data.get("dept")));
						tmpOne.setUsername(StringUtil.nullToString(data.get("username")));
						aryRtn.add(tmpOne);
					}
				}	
			}
			
		}catch(Throwable e){
			throw new SQLException(me+e.toString());
		}finally{
			closeAll(da,null,connFlg);
		}
		return aryRtn;
	}	
	
	//依單位/卡號,取得oproole清單 手術室角色分派
	public JSONArray qryOpRoleDeptList(String deptId,String userId,VGHDataAccess da) throws Exception{
		String me = getClass().getName() + ".qryOpRoleDeptList()";
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne =null;
		ArrayList<Object> params = new ArrayList<Object>();
		String subsql="";
		
		sql = "SELECT USERID, USERNAME, PASSWORD, DEPT, AUTHORITY, UPDID,ZOPROLE FROM "+ this.tOpRole;
		if (deptId!=null && !deptId.equals("")){
			if (!subsql.equals("")) subsql+=" and ";
			else if (subsql.equals("")) subsql+=" where ";
			subsql+=" DEPT=? ";
			params.add(deptId);
		}
		if (userId!=null && !userId.equals("")){
			if (!subsql.equals("")) subsql+=" and ";
			else if (subsql.equals("")) subsql+=" where ";
			subsql+=" USERID=? ";
			params.add(userId);
		}
		sql+=subsql+" ORDER BY DEPT,USERID WITH UR";
		try{
			if (da == null) {
				da = new VGHDataAccess(this.dataSourceName); 
				connFlg = true;
			}
			
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"  " + sql+"...params:"+ params.toString()+"...size:"+result.size());
			if(result.size()>0){
				for (Map<String,Object> data :result){
					jsonOne = new JSONObject(); 
					jsonOne.put("userid", StringUtil.nullToString(data.get("userid"))); //卡號
					jsonOne.put("username", StringUtil.nullToString(data.get("username"))); //姓名
					jsonOne.put("password", StringUtil.nullToString(data.get("password"))); //密碼
					jsonOne.put("dept", StringUtil.nullToString(data.get("dept"))); //成本中心代碼
					jsonOne.put("authority", StringUtil.nullToString(data.get("authority"))); //權限
					jsonOne.put("updid", StringUtil.nullToString(data.get("updid"))); //權限
					jsonOne.put("zoprole", sdf.format(data.get("zoprole"))); //權限
					jsonRtn.add(jsonOne);
				}
			}
		}catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}finally{
			closeAll(da,null,connFlg);
		}
		return jsonRtn;
	}
	
	/**
	  * 刪除參考檔
	 *  @throws SQLException
	 *  @param  userid(卡號)   
	 */

	public boolean delOpRoleDept(String userid,VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".delOpRoleDept()";	
		boolean success=false;
		int delCnt = 0;
		sql="delete from "+this.tOpRole+ " where USERID=? " ;
		try{
			if(da==null){
				connFlg = false;
				da= new VGHDataAccess(Globals.dataSource_DSNP);
			}
			params.add(userid);
			log.info(me+"  " + sql+"...params:"+ params.toString());
			delCnt=da.update(sql, params);	
			if (delCnt>0) success=true;
			log.info(me+"...delCnt:"+ delCnt);
		}
		catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally{
			closeAll(da,null,connFlg);			
		}	
		return success;	
	}
	//代入代碼檔
	public ArrayList<Object> getLoadCodeTbl(String tbl,String hid,VGHDataAccess da) throws SQLException{
		String me = getClass().getName() + ".getLoadCodeTbl()";
		ArrayList<Object> aryRtn=new ArrayList<Object>();
		params= new ArrayList<Object>();
		RankCode tmpRank=null;
		Patitle tmpTitle=null;
		Pabank tmpBank=null;
		Padept tmpDept=null;
		Parank4tbl tmpRnk4=null;
		Paexpftbl tmpexpf=null;
		Patracode tmptracd=null;
		Papayitm tmppayitm=null;
		if ("PARANKCODE".equals(tbl)){
			sql = "SELECT RANKCODE, RANKNAME, RANKABBR, ZPARANKCODE from "+this.tRankCode +" order by RANKCODE with ur";
		}else if ("PATITLE".equals(tbl)){
			sql = "SELECT PAYTITLK, PATITSUB, PACHIEF, PAYTITLP, PAYTITLC, PACODDEL, PAYUPDID, PAYUPDAT, HID, ZPATITLE from "+this.tTitle +" where hid=? order by PATITSUB,PAYTITLK with ur";
			params.add(hid);
		}else if ("PABANK".equals(tbl)){
			sql = "SELECT SALNUM, PAYBANK, PAYSALDT, PAYUPDID, PAYUPDAT,HID, ZPABANK from "+this.tBankDtTbl +" where HID=? and PAYBANK<=(CURRENT DATE +2 month) order by SALNUM desc with ur";
			params.add(hid);
		}else if ("PADEPT".equals(tbl)){
			sql = "SELECT PADEPCOD, PACSTNO, PADEPNMC, PAYUPDID, PAYUPDAT, HID, ZPADEPT from "+this.tDept +" where HID=? order by PACSTNO,PADEPCOD with ur";
			params.add(hid);
		}else if ("PARNK4TBL".equals(tbl)){
			sql = "SELECT RNK4KEY, RNK4NAME, PAYUPDTM, ZPARNK4TBL from "+this.tRnk4Tbl +" order by RNK4KEY with ur";			
		}else if ("PAEXPFTBL".equals(tbl)){
			sql = "SELECT EXPFKEY, EXPFNAME, PAYUPDID, PAYUPDAT, ZPAEXPFTBL from "+this.tExpfTbl +" order by EXPFKEY with ur";			
		}else if ("PATRACODE".equals(tbl)){
			sql = "SELECT TRACODE, TRANAME, ZPATRACODE from "+this.tTraCode +" order by TRACODE with ur";			
		}else if ("PAPAYITM".equals(tbl)){
			sql = "SELECT PAYCODE, PACODNMC, PACODDEL, PAYAUTH, PAYIFTAX, PAUPBAS, PAYREWARD, PAYUPDID, PAYUPDAT, HID from "+this.tPayIItm +" where PACODDEL='N' and HID=? order by PAYCODE with ur";
			params.add(hid);
		}
		try{
			if (da == null) {
				da = new VGHDataAccess(this.dataSourceName); 
				connFlg = true;
			}
			log.info(me+"  " + sql+"...params:"+ params.toString());
			result = null;
			if (tbl.matches("PARANKCODE|PARNK4TBL|PAEXPFTBL|PATRACODE")){
				result = da.arraylistbyQuery(sql,META_TYPE);
			}else if (tbl.matches("PATITLE|PABANK|PADEPT|PAPAYITM")){
				result = da.arraylistbyQuery(sql,params,META_TYPE);
			}
			log.info(me+"  ...size:"+result.size());
			if(result.size()>0){
				for (Map<String,Object> data :result){
					if ("PARANKCODE".equals(tbl)){	//階級代碼 
						tmpRank = new RankCode();
						PropertyUtils.copyProperties(tmpRank, data);
						aryRtn.add(tmpRank);
					}
					else if ("PATITLE".equals(tbl)){	//職稱代碼
						tmpTitle = new Patitle();
						PropertyUtils.copyProperties(tmpTitle, data);
						aryRtn.add(tmpTitle);
					}
					else if ("PABANK".equals(tbl)){	//發薪日及入帳日對應檔
						tmpBank = new Pabank();
						PropertyUtils.copyProperties(tmpBank, data);
						aryRtn.add(tmpBank);
					}
					else if ("PADEPT".equals(tbl)){	//單位
						tmpDept = new Padept();
						PropertyUtils.copyProperties(tmpDept, data);
						aryRtn.add(tmpDept);
					}
					else if ("PARNK4TBL".equals(tbl)){	//適用專業加給對照表
						tmpRnk4 = new Parank4tbl();
						PropertyUtils.copyProperties(tmpRnk4, data);
						aryRtn.add(tmpRnk4);
					}
					else if ("PAEXPFTBL".equals(tbl)){	//績效額外點數對照表 
						tmpexpf = new Paexpftbl();
						PropertyUtils.copyProperties(tmpexpf, data);
						aryRtn.add(tmpexpf);
					}
					else if ("PATRACODE".equals(tbl)){	//異動代碼  
						tmptracd = new Patracode();
						PropertyUtils.copyProperties(tmptracd, data);
						aryRtn.add(tmptracd);
					}
					else if ("PAPAYITM".equals(tbl)){	//支扣代碼  
						tmppayitm = new Papayitm();
						PropertyUtils.copyProperties(tmppayitm, data);
						aryRtn.add(tmppayitm);
					}
				}
			}
		}catch(Throwable e){
			throw new SQLException(me+e.toString());
		}finally{
			closeAll(da,null,connFlg);
		}
		return aryRtn;
	}	
	
	/**
	  * 依table名單及KEY,刪除record
	 *  @throws SQLException
	 *  @param  code_data(參數map) userid(登入者) hid(醫院代號)  
	 */

	public boolean delTblCode(String code_data,String tblname,String userid,String hid,VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".delTblCode()";	
		boolean success=false;
		int delCnt = 0;
		JSONObject object_code_data = JSONObject.fromObject(code_data);
		params= new ArrayList<Object>();
		if ("PATRACODE".equals(tblname)){
			sql="delete from "+this.tTraCode+ " where tracode=? " ;
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
		}else if ("PADEPT".equals(tblname)){
			sql="delete from "+this.tDept+ " where padepcod=? and hid=? " ;
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
			params.add(hid);
		}else if ("PARANKCODE".equals(tblname)){
			sql="delete from "+this.tRankCode+ " where rankcode=? " ;
			params.add(StringUtil.nullToString(object_code_data.getString("key")));			
		}else if ("PACATGTBL".equals(tblname)){
			sql="delete from "+this.tCatgTbl+ " where paycatg=? " ;
			params.add(StringUtil.nullToString(object_code_data.getString("key")));			
		}else if ("PAEXPFTBL".equals(tblname)){
			sql="delete from "+this.tExpfTbl+ " where expfkey=? " ;
			params.add(StringUtil.nullToString(object_code_data.getString("key")));			
		}else if ("PARNK4TBL".equals(tblname)){
			sql="delete from "+this.tRnk4Tbl+ " where rnk4key=? " ;
			params.add(StringUtil.nullToString(object_code_data.getString("key")));			
		}else if ("PABANK".equals(tblname)){
			sql="delete from "+this.tBankDtTbl+ " where salnum=? " ;
			params.add(StringUtil.nullToString(object_code_data.getString("key")));			
		}else if ("PATAXTBL".equals(tblname)){
			sql="delete from "+this.tTaxTbl+ " where sallow=? and kindred=? " ;
			params.add(StringUtil.nullToString(object_code_data.getString("sallow")));
			params.add(StringUtil.nullToString(object_code_data.getString("kindred")));
		}else if ("PAPFTL".equals(tblname)){
			sql="delete from "+this.tPftlTbl+ " where paypfmon=? " ;
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
		}else if ("PADPINTBL".equals(tblname)){
			sql="delete from "+this.tDpinTbl+ " where dpinlow=? " ;
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
		}else if ("PALBINTBL".equals(tblname)){
			sql="delete from "+this.tLbinTbl+ " where lbinlow=? " ;
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
		}else if ("PALBRPTBL".equals(tblname)){
			sql="delete from "+this.tLbrpTbl+ " where lbrplow=? " ;
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
		}else if ("PATITLE".equals(tblname)){
			sql="delete from "+this.tTitle+ " where paytitlk=? and hid=? " ;
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
			params.add(hid);
		}else if ("PAPOSI".equals(tblname)){
			sql="delete from "+this.tPosiTbl+ " where payposik=? and degrlow=? " ;
			params.add(StringUtil.nullToString(object_code_data.getString("payposik")));
			params.add(StringUtil.nullToString(object_code_data.getString("degrlow")));
		}else if ("PARANK".equals(tblname)){
			sql="delete from "+this.tRankTbl+ " where paylev1v=? and paylev2v=? and payrankv=?" ;
			params.add(StringUtil.nullToString(object_code_data.getString("paylev1v")));
			params.add(StringUtil.nullToString(object_code_data.getString("paylev2v")));
			params.add(StringUtil.nullToString(object_code_data.getString("payrankv")));
		}else if ("PACSPF".equals(tblname)){
			sql="delete from "+this.tCspfTbl+ " where pacskey=? " ;
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
		}else if ("PATRAPAY".equals(tblname)){
			sql="delete from "+this.tTraPayTbl+ " where tranum=? and patradt=? and paysort=? and paycode=? and hid=? " ;
			params.add(StringUtil.nullToString(object_code_data.getString("tranum")));
			params.add(StringUtil.nullToString(object_code_data.getString("patradt")));
			params.add(StringUtil.nullToString(object_code_data.getString("paysort")));
			params.add(StringUtil.nullToString(object_code_data.getString("paycode")));
			params.add(StringUtil.nullToString(object_code_data.getString("hid")));
		}
		
		try{
			if(da==null){
				connFlg = false;
				da= new VGHDataAccess(Globals.dataSource_NT);
			}
			
			log.info(me+"  " + sql+"...params:"+ params.toString());
			delCnt=da.update(sql, params);	
			if (delCnt>0) success=true;
			log.info(me+"...delCnt:"+ delCnt);
		}
		catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally{
			closeAll(da,null,connFlg);			
		}	
		return success;	
	}
	/**
	 * update pa.pabasic(薪資基本檔) 
	 * item:update pk_data:key
	 * 傳出:true/false
	 */	
	public boolean updPATblByKey(String item,String pk_data,String tabJson,String userId,String hid,VGHDataAccess da) throws SQLException {
		String me = getClass().getName() + ".updPATblByKey()";
		log.info(me + " item = " + item+ " pk_data = " + pk_data+ " tabJson = " + tabJson+ " userId = " + userId+ " hid = " + hid);
		boolean success=false;
		JSONObject object_pk_data = JSONObject.fromObject(pk_data);
		int updCnt = 0;
		String tblname ="";
		params = new ArrayList<Object>();
		ArrayList<Object> paramsA = new ArrayList<Object>();
		String sqlAdr="";
		int datacnt=0;
		boolean dataOK=false;
		int adrcnt=0;
		boolean adrOK=false;
		String strKey="";
		
		if (da == null) {
			da = new VGHDataAccess(this.dataSourceName); 
			connFlg = true;
		}
		da.setAutoCommit(false);		
		JSONArray tabJsonary = JSONArray.fromObject(tabJson);
		for (int i=0; i< tabJsonary.size(); ++i) {
			JSONObject obj = tabJsonary.getJSONObject(i);
			if ( obj != null &&  obj.has("tblname") ) {
				tblname = StringUtil.nullToString( obj.getString("tblname") );
				if ("PABASPSN".equals(tblname)){
					strKey=StringUtil.nullToString(object_pk_data.getString("payidno"));
					hid=StringUtil.nullToString(object_pk_data.getString("hid"));
					if (item.equals("update")){
						//基本檔
						sql =" update "+ this.tBasPsn+ " set (PAYEMPNO,PAYNAMEC, PAYDEPT,PAYSGNDT,PAYTAXNO, INSUAMT,PACSFLAG, PARETENTION,PAYEXPFKEY1, PAYEXPFKEY2, PAYEXPFKEY3,PAYPFSP1, PAYPFSP2, PAYPFSP3,PAYCTCBACNO,PAYCTCBGEDNO,PAYUPDID, PAYUPDTM) "+
							 "= (?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,current timestamp) where PAYIDNO=? and HID=? ";
						//地址
						sqlAdr =" update "+ this.tBasAdr+ " set (PAYADDRS) = (?) where PAYIDNO=? ";
					}
				}
				JSONArray array = obj.getJSONArray("value");
				params = new ArrayList<Object>();
				paramsA = new ArrayList<Object>();
				if (null != array && array.size() > 0 ) {
					for (int j=0; j<array.size(); j++) {
						JSONObject obj_item = array.getJSONObject(j);
						if ("PABASPSN".equals(tblname)){//基本檔
							params.add((!obj_item.has("payempno")) ? "" : obj_item.getString("payempno"));
							params.add((!obj_item.has("paynamec")) ? "" : obj_item.getString("paynamec"));
							params.add((!obj_item.has("paydept")) ? "" : obj_item.getString("paydept"));
							params.add((!obj_item.has("paysgndt")) ? java.sql.Date.valueOf("9999-12-31") : ("".equals(obj_item.getString("paysgndt")) ? java.sql.Date.valueOf("9999-12-31") : obj_item.getString("paysgndt")));
							params.add((!obj_item.has("paytaxno")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paytaxno"))) ? "0" :StringUtil.nullToString(obj_item.getString("paytaxno")));
							params.add((!obj_item.has("insuamt")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("insuamt"))) ? "0" :StringUtil.nullToString(obj_item.getString("insuamt")));
							params.add((!obj_item.has("pacsflag")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("pacsflag"))) ? "N" :StringUtil.nullToString(obj_item.getString("pacsflag")));
							params.add((!obj_item.has("paretention")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paretention"))) ? "" :StringUtil.nullToString(obj_item.getString("paretention")));
							params.add((!obj_item.has("payexpfkey1")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("payexpfkey1"))) ? "0" :StringUtil.nullToString(obj_item.getString("payexpfkey1")));
							params.add((!obj_item.has("payexpfkey2")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("payexpfkey2"))) ? "0" :StringUtil.nullToString(obj_item.getString("payexpfkey2")));
							params.add((!obj_item.has("payexpfkey3")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("payexpfkey3"))) ? "0" :StringUtil.nullToString(obj_item.getString("payexpfkey3")));
							params.add((!obj_item.has("paypfsp1")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paypfsp1"))) ? "0" :StringUtil.nullToString(obj_item.getString("paypfsp1")));
							params.add((!obj_item.has("paypfsp2")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paypfsp2"))) ? "0" :StringUtil.nullToString(obj_item.getString("paypfsp2")));
							params.add((!obj_item.has("paypfsp3")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paypfsp3"))) ? "0" :StringUtil.nullToString(obj_item.getString("paypfsp3")));
							params.add((!obj_item.has("payctcbacno")) ? "" : obj_item.getString("payctcbacno"));
							params.add((!obj_item.has("payctcbgedno")) ? "" : obj_item.getString("payctcbgedno"));
							params.add(userId);
							params.add(strKey);
							params.add(hid);
							paramsA.add((!obj_item.has("payaddrs")) ? "" : StringUtil.toWideCase(StringUtil.trimWideEmpty((String)obj_item.getString("payaddrs"))));
							paramsA.add(strKey);
						}
						try{
							log.info(me+"  " + sql+"...params:"+ params.toString());
							datacnt=da.update(sql, params);
							if (datacnt>0) dataOK=true;
//							log.info(me+" "+tblname+" updCnt:" + datacnt);
						}catch(SQLException e1){
							da.rollback();
							da.setAutoCommit(true);
							e1.printStackTrace();
							log.info(me+" "+tblname+" "+e1.toString());
							 
						}catch(Exception e2){
							 da.rollback();
							 da.setAutoCommit(true);
							 e2.printStackTrace();
							 log.info(me+" "+tblname+" "+e2.toString());
						}	
						if (!dataOK){
							da.rollback();
							da.setAutoCommit(true);
							success=false; 
						}
						else{	//地址
							try{
								log.info(me+"  " + sqlAdr+"...params:"+ params.toString());
								adrcnt=da.update(sqlAdr, paramsA);
								if (adrcnt>0) adrOK=true;
								log.info(me+" "+this.tBasAdr+" updCnt:" + adrcnt);
							}catch(SQLException e1){
								da.rollback();
								da.setAutoCommit(true);
								e1.printStackTrace();
								log.info(me+" "+this.tBasAdr+" "+e1.toString());
								 
							}catch(Exception e2){
								 da.rollback();
								 da.setAutoCommit(true);
								 e2.printStackTrace();
								 log.info(me+" "+this.tBasAdr+" "+e2.toString());
							}
						}	
					}	
					
				}	
			}
		}
		
		if ("PABASPSN".equals(tblname)){
			if (dataOK && adrOK){
				da.commit();
				log.info(me +strKey+"儲存成功!");
			    da.setAutoCommit(true);
			    closeAll(da,null,connFlg);
			    success=true;
			}else{
				da.rollback();
				da.setAutoCommit(true);
				closeAll(da,null,connFlg);
				log.info(me +strKey+"儲存失敗!");
				success=false; 
			}
		}	
		return success;
	}
	
	//依篩選條件,取得pa.patrapsn集合 item:1個人明細,2篩選
	public JSONArray getTranPsnList(String code_data,String item,String hid,VGHDataAccess da) throws Exception{
		String me = getClass().getName() + ".getTranPsnList()";
		log.info(me +  " code_data = " + code_data+ " hid = " + hid);
		CommDAO cDAO = new CommDAO();
		HashMap CspfMap=new HashMap();	
		Pacspf cspfObj=null;
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne =null;
		JSONObject object_code_data =null;
		Date start = new Date();
		Date end = new Date();
		String sqlA="";
		String expfsp="";
		String csflag="";
		String paret="";
		String cardno="";
		String idtno="";
		String namec="";
		String empno="";
		String[] tracod=null;
		String[] salnum=null;
		String inflg="";
		String[] patradt=null;
		String[] payrank=null;
		String[] paytitle=null;
		String[] paydept=null;
		String[] expfkey1=null;
		String[] expfkey2=null;
		String[] expfkey3=null;
		String subsql="";
		String strSelect="";
		Pabaspsn baspsnObj=null;		
		int pfbas=0;
		int pfdat=0;
		String vPFKEY="";
		int i=0;
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> paramsub = new ArrayList<Object>();
		if (code_data!=null && !code_data.equals("")){
			object_code_data = JSONObject.fromObject(code_data);
			tracod =(!object_code_data.has("patracod")) ? null : ("".equals(object_code_data.getString("patracod"))  ? null : object_code_data.getString("patracod").split(","));
			patradt=(!object_code_data.has("patradt")) ? null : ("".equals(object_code_data.getString("patradt"))  ? null : object_code_data.getString("patradt").split(","));
			salnum =(!object_code_data.has("salnum")) ? null : ("".equals(object_code_data.getString("salnum"))  ? null : object_code_data.getString("salnum").split(","));
			cardno=(!object_code_data.has("cardno")) ? "" : object_code_data.getString("cardno");
			idtno=(!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			namec=(!object_code_data.has("namec")) ? "" : object_code_data.getString("namec");
			empno=(!object_code_data.has("empno")) ? "" : object_code_data.getString("empno");
			paydept=(!object_code_data.has("paydept")) ? null : ("".equals(object_code_data.getString("paydept"))  ? null : object_code_data.getString("paydept").split(","));
			
			csflag=(!object_code_data.has("csflag")) ? "" : object_code_data.getString("csflag");
			expfsp=(!object_code_data.has("expfsp")) ? "" : object_code_data.getString("expfsp");			
			inflg=(!object_code_data.has("inflg")) ? "" : object_code_data.getString("inflg");
			paret=(!object_code_data.has("paret")) ? "" : object_code_data.getString("paret");
			payrank=(!object_code_data.has("payrank")) ? null : ("".equals(object_code_data.getString("payrank"))  ? null : object_code_data.getString("payrank").split(","));
			paytitle=(!object_code_data.has("paytitle")) ? null : ("".equals(object_code_data.getString("paytitle"))  ? null : object_code_data.getString("paytitle").split(","));			
			expfkey1=(!object_code_data.has("expfkey1")) ? null : ("".equals(object_code_data.getString("expfkey1"))  ? null : object_code_data.getString("expfkey1").split(","));
			expfkey2=(!object_code_data.has("expfkey2")) ? null : ("".equals(object_code_data.getString("expfkey2"))  ? null : object_code_data.getString("expfkey2").split(","));
			expfkey3=(!object_code_data.has("expfkey3")) ? null : ("".equals(object_code_data.getString("expfkey3"))  ? null : object_code_data.getString("expfkey3").split(","));
		}
		//讀取pa.patrapsn
		strSelect="PATRACOD, PATRADT, PAEFFDT, SALNUM, PAYLVEFG, PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, "+
				"PAYEMPDT, PAYSGNDT, PAYRSGDT, PAYBKID, PAYACNO, PAYGEDNO, COALESCE(A.PAYINFLG,0) as PAYINFLG,COALESCE(PAYDPNO,0) as PAYDPNO, PAYPFLAG, PAYTITLE, "+
				"PAYRNK2N, COALESCE(A.PAYRANK3,0) as PAYRANK3, COALESCE(A.PAYDEGR2,0) as PAYDEGR2, COALESCE(A.PAYDEGRE,0) as PAYDEGRE, PAYRANK, PAYRANK2, "+ 
				"PAYRANKC, COALESCE(A.PAYPFDOC,0) as PAYPFDOC, PACSFLAG, COALESCE(A.PAYPFSP1,0) as PAYPFSP1, "+ 
				"COALESCE(A.PAYPFSP2,0) as PAYPFSP2, COALESCE(A.PAYPFSP3,0) as PAYPFSP3, PAYMONNO, PAYDAYNO, PAOTITLE, PAORNK2N, COALESCE(A.PAORANK3,0) as PAORANK3, "+
				"COALESCE(A.PAODEGR2,0) as PAODEGR2, COALESCE(A.PAODEGRE,0) as PAODEGRE, PAORANK, PAORANK2, PAORANKC, COALESCE(A.PAOPFDOC,0) as PAOPFDOC, PAOCSFLA, "+
				"COALESCE(A.PAOPFSP1,0) as PAOPFSP1, COALESCE(A.PAOPFSP2,0) as PAOPFSP2, COALESCE(A.PAOPFSP3,0) as PAOPFSP3, COALESCE(A.PAYTAXNO,0) as PAYTAXNO,"+
				"A.PAYUPDID, A.PAYUPDTM,PAYFLAG, TRANUM, UPMASTDT, PAYPSW, PAOEXPFKEY1, PAOEXPFKEY2, PAOEXPFKEY3, PAYEXPFKEY1, PAYEXPFKEY2, "+ 
				"PAYEXPFKEY3, COALESCE(A.PAORANK4,0) as PAORANK4, COALESCE(A.PAYRANK4,0) as PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, INSUAMT, A.HID, "+
				"B.PADEPNMC,B.PACSTNO,C.PAYTITLP,C.PATITSUB,D.RANKNAME,E.RNK4NAME,F.TRANAME ";
		
		sqlA = " A "+
			   " left outer join "+ this.tDept+" B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID "+
			   " left outer join "+ this.tTitle+" C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID "+
			   " left outer join "+ this.tRankCode+" D on A.PAYRANK=D.RANKCODE "+
			   " left outer join "+ this.tRnk4Tbl+" E on A.PAYRANK4=E.RNK4KEY "+
			   " left outer join "+ this.tTraCode+" F on A.PATRACOD=F.TRACODE "+
			   " where A.HID=? ";
		params.add(hid);
		
		if (!"".equals(cardno))	{//卡號
			subsql+=" and PAYCARD=? ";
			params.add(cardno);
			paramsub.add(cardno);
		}
		else if (!"".equals(idtno))	{//身分證
			subsql+=" and PAYIDNO=? ";
			params.add(idtno);
			paramsub.add(idtno);
		}
		else if (!"".equals(namec))	{//姓名
			subsql+=" and PAYNAMEC like ? ";
			params.add(namec+"%");
			paramsub.add(namec+"%");
		}
		else if (!"".equals(empno))	{//職號
			subsql+=" and PAYEMPNO like ? ";
			params.add(empno+"%");
			paramsub.add(empno+"%");
		}
		
		if (!(tracod==null || (tracod!=null && tracod.length==0))){ //異動原因
			subsql+=" and PATRACOD in ( ";
			for (i=0;i<tracod.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(tracod[i]);	
				paramsub.add(tracod[i]);
			}	
			subsql+=")";
		}
		if (!"".equals(csflag))	{//支領工作獎金(Y/N)
			subsql+=" and PACSFLAG=? ";
			params.add(csflag);
			paramsub.add(csflag);
		}

		if (!(patradt==null || (patradt!=null && patradt.length==0))){ //異動日期
			subsql+=" and PATRADT in ( ";
			for (i=0;i<patradt.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(patradt[i]);	
				paramsub.add(patradt[i]);
			}	
			subsql+=")";
		}else if (!(salnum==null || (salnum!=null && salnum.length==0))){	//餉單編號
			subsql+=" and SALNUM in ( ";
			for (i=0;i<salnum.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(salnum[i]);
				paramsub.add(salnum[i]);
			}	
			subsql+=")";
		}
		if (!"".equals(inflg))	{//保險補助比例
			subsql+=" and PAYINFLG<>0 ";
		}
		if (!"".equals(expfsp))	{//額外點數1 2 3有值皆要列入
			subsql+=" and (PAYPFSP1<>0 or PAYPFSP2<>0 or PAYPFSP3<>0) ";			
		}
		if (!"".equals(paret))	{//留職留薪,支領專2(Y/N)
			subsql+=" and PARETENTION=? ";
			params.add(paret);
			paramsub.add(paret);
		}
		if (!(payrank==null || (payrank!=null && payrank.length==0))){		//階級 
			subsql+=" and PAYRANK in ( ";
			for (i=0;i<payrank.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(payrank[i]);
				paramsub.add(payrank[i]);
			}	
			subsql+=")";
		}
		
		if (!(paytitle==null || (paytitle!=null && paytitle.length==0))){		//職稱 
			subsql+=" and PAYTITLE in ( ";
			for (i=0;i<paytitle.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(paytitle[i]);
				paramsub.add(paytitle[i]);
			}	
			subsql+=")";
		}
		if (!(paydept==null || (paydept!=null && paydept.length==0))){		//單位 
			subsql+=" and paydept in ( ";
			for (i=0;i<paydept.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(paydept[i]);
				paramsub.add(paydept[i]);
			}	
			subsql+=")";
		}
		if (!(expfkey1==null || (expfkey1!=null && expfkey1.length==0))){		//額外點數1 
			subsql+=" and payexpfkey1 in ( ";
			for (i=0;i<expfkey1.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(expfkey1[i]);
				paramsub.add(expfkey1[i]);
			}	
			subsql+=")";
		}		
		if (!(expfkey2==null || (expfkey2!=null && expfkey2.length==0))){		//額外點數2 
			subsql+=" and payexpfkey2 in ( ";
			for (i=0;i<expfkey2.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(expfkey2[i]);
				paramsub.add(expfkey2[i]);
			}	
			subsql+=")";
		}
		if (!(expfkey3==null || (expfkey3!=null && expfkey3.length==0))){		//額外點數3 
			subsql+=" and payexpfkey3 in ( ";
			for (i=0;i<expfkey3.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(expfkey3[i]);
				paramsub.add(expfkey3[i]);
			}	
			subsql+=")";
		}
				
		sql="select " + strSelect +" from " + this.tTraPsnTbl +sqlA+subsql+
			" union all "+
			"select " + strSelect +" from " + this.tTraPsnDw +sqlA+subsql+
			" order by PATRADT DESC,7 with ur";
		params.add(hid);
		for (int j=0;j<paramsub.size();j++){
			params.add(paramsub.get(j));
		}
		
		log.info(me+"  " + sql+"...params:"+ params.toString());
		
		try{
			if (da == null) {
				da = new VGHDataAccess(this.dataSourceName); 
				connFlg = true;
			}
			
			start = new Date();
			if ("2".equals(item)){
				cDAO = new CommDAO(da);
				CspfMap=cDAO.getCsPfByAll(da);
			}
			
			log.info(me+"  " + sql+"...params:"+ params.toString());
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"...size:"+result.size());
			
			if(result.size()>0){
				for (Map<String,Object> data :result){
					baspsnObj=new Pabaspsn();
					PropertyUtils.copyProperties(baspsnObj, data);
					
					jsonOne = new JSONObject();
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));
					jsonOne.put("paynamec", StringUtil.nullToString(data.get("paynamec")));
					jsonOne.put("patradt", StringUtil.nullToString(data.get("patradt")));
					jsonOne.put("traname", StringUtil.nullToString(data.get("traname")));
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					jsonOne.put("patracod", StringUtil.nullToString(data.get("patracod")));
					jsonOne.put("tranum", StringUtil.nullToString(data.get("tranum")));
					if ("2".equals(item)){
						jsonOne.put("paydept", StringUtil.nullToString(data.get("paydept")));
						jsonOne.put("padepnmc", StringUtil.nullToString(data.get("padepnmc")));					
						jsonOne.put("paylvefg", StringUtil.nullToString(data.get("paylvefg")));
						jsonOne.put("payempno", StringUtil.nullToString(data.get("payempno")));
						jsonOne.put("paygovdt", StringUtil.nullToString(data.get("paygovdt")));
						jsonOne.put("payempdt",StringUtil.nullToString(data.get("payempdt")) );
						jsonOne.put("paysgndt", StringUtil.nullToString(data.get("paysgndt")));
						jsonOne.put("payrsgdt", StringUtil.nullToString(data.get("payrsgdt")));
						jsonOne.put("pacstno", StringUtil.nullToString(data.get("pacstno")));
						
						jsonOne.put("paytitle", StringUtil.nullToString(data.get("paytitle")));
						jsonOne.put("payrnk2n", StringUtil.nullToString(data.get("payrnk2n")));
						jsonOne.put("payrank3", StringUtil.nullToString(data.get("payrank3")));
						jsonOne.put("paydegr2", StringUtil.nullToString(data.get("paydegr2")));
						jsonOne.put("paydegre", StringUtil.nullToString(data.get("paydegre")));
						jsonOne.put("payrank", StringUtil.nullToString(data.get("payrank")));
						jsonOne.put("rankname", StringUtil.nullToString(data.get("rankname")));
						jsonOne.put("payrank2", StringUtil.nullToString(data.get("payrank2")));
						jsonOne.put("payrankc", StringUtil.nullToString(data.get("payrankc")));
						jsonOne.put("paytitlp", StringUtil.nullToString(data.get("paytitlp")));
						jsonOne.put("rnk4name", StringUtil.nullToString(data.get("rnk4name")));
						jsonOne.put("rank3name",PaCommUtil.getRank3Nm((Integer) data.get("payrank3")));
						jsonOne.put("payinflg",PaCommUtil.getInflgNm((Integer) data.get("payinflg")));
						jsonOne.put("paydpno", StringUtil.nullToString(data.get("paydpno")));
						jsonOne.put("paytaxno", StringUtil.nullToString(data.get("paytaxno")));
						jsonOne.put("insuamt", StringUtil.nullToString(data.get("insuamt")));
						jsonOne.put("contract", baspsnObj.getContract());					
						
						jsonOne.put("pacsflag", StringUtil.nullToString(data.get("pacsflag")));
						jsonOne.put("paypfsp1", StringUtil.nullToString(data.get("paypfsp1")));
						jsonOne.put("paypfsp2", StringUtil.nullToString(data.get("paypfsp2")));
						jsonOne.put("paypfsp3", StringUtil.nullToString(data.get("paypfsp3")));
						jsonOne.put("paretention", baspsnObj.getParetention());
						jsonOne.put("paypfdoc", StringUtil.nullToString(data.get("paypfdoc")));
						//工作獎金=Y,找基本績分 俸級績分
						pfbas=0;
						pfdat=0;
						if ("Y".equals(baspsnObj.getPacsflag())){
							if (baspsnObj.getPayrank3()==0)
								vPFKEY=baspsnObj.getPaytitle()+"00000";
							else
								vPFKEY = baspsnObj.getPaytitle() + baspsnObj.getPayrnk2n() + baspsnObj.getPayrank3() + StringUtil.fillChar(StringUtil.nullToString(baspsnObj.getPaydegr2()),2,"0",false) ;
							
							cspfObj=(Pacspf) CspfMap.get(vPFKEY);
							if (cspfObj!=null){
								pfbas= cspfObj.getPacsbase();
								pfdat= cspfObj.getPacsdata();
							}	
						}
						jsonOne.put("pfbas", pfbas);
						jsonOne.put("pfdat", pfdat);
					}	
					jsonRtn.add(jsonOne);
				}
			}
			end = new Date();
			log.info(me+"  ...********* dataaccess:"+(end.getTime()-start.getTime()));
		}catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}finally{
			closeAll(da,null,connFlg);
		}
		
		return jsonRtn;
	}
	
	//依篩選條件,取得pa.pabaspsn集合 item:1個人明細,2篩選
	public JSONArray getBasPsnList(String code_data,String item,String hid,VGHDataAccess da) throws Exception{
		String me = getClass().getName() + ".getBasPsnList()";
		log.info(me +  " code_data = " + code_data+ " hid = " + hid);
		CommDAO cDAO = new CommDAO();
		HashMap CspfMap=new HashMap();	
		Pacspf cspfObj=null;
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne =null;
		JSONObject object_code_data =null;
		Date start = new Date();
		Date end = new Date();
		String lvefg="";
		String expfsp="";
		String csflag="";
		String paret="";
		String cardno="";
		String idtno="";
		String namec="";
		String empno="";
		String tracod="";
		String inflg="";
		String[] patradt=null;
		String[] payrank=null;
		String[] paytitle=null;
		String[] paydept=null;
		String[] expfkey1=null;
		String[] expfkey2=null;
		String[] expfkey3=null;
		String subsql="";
		String strSelect="";
		Pabaspsn baspsnObj=null;		
		int pfbas=0;
		int pfdat=0;
		String vPFKEY="";
		int i=0;
		ArrayList<Object> params = new ArrayList<Object>();
		
		if (code_data!=null && !code_data.equals("")){
			object_code_data = JSONObject.fromObject(code_data);
			lvefg=(!object_code_data.has("lvefg")) ? "" : object_code_data.getString("lvefg");
			csflag=(!object_code_data.has("csflag")) ? "" : object_code_data.getString("csflag");
			expfsp=(!object_code_data.has("expfsp")) ? "" : object_code_data.getString("expfsp");
			tracod=(!object_code_data.has("tracod")) ? "" : object_code_data.getString("tracod");
			inflg=(!object_code_data.has("inflg")) ? "" : object_code_data.getString("inflg");
			paret=(!object_code_data.has("paret")) ? "" : object_code_data.getString("paret");
			cardno=(!object_code_data.has("cardno")) ? "" : object_code_data.getString("cardno");
			idtno=(!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			namec=(!object_code_data.has("namec")) ? "" : object_code_data.getString("namec");
			empno=(!object_code_data.has("empno")) ? "" : object_code_data.getString("empno");
			patradt=(!object_code_data.has("patradt")) ? null : ("".equals(object_code_data.getString("patradt"))  ? null : object_code_data.getString("patradt").split(","));
			payrank=(!object_code_data.has("payrank")) ? null : ("".equals(object_code_data.getString("payrank"))  ? null : object_code_data.getString("payrank").split(","));
			paytitle=(!object_code_data.has("paytitle")) ? null : ("".equals(object_code_data.getString("paytitle"))  ? null : object_code_data.getString("paytitle").split(","));
			paydept=(!object_code_data.has("paydept")) ? null : ("".equals(object_code_data.getString("paydept"))  ? null : object_code_data.getString("paydept").split(","));
			expfkey1=(!object_code_data.has("expfkey1")) ? null : ("".equals(object_code_data.getString("expfkey1"))  ? null : object_code_data.getString("expfkey1").split(","));
			expfkey2=(!object_code_data.has("expfkey2")) ? null : ("".equals(object_code_data.getString("expfkey2"))  ? null : object_code_data.getString("expfkey2").split(","));
			expfkey3=(!object_code_data.has("expfkey3")) ? null : ("".equals(object_code_data.getString("expfkey3"))  ? null : object_code_data.getString("expfkey3").split(","));
		}
		//讀取pa.pabaspsn
		strSelect="PAYLVEFG, A.PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, PAYEMPDT, PAYSGNDT, PAYRSGDT, PAYBKID, PAYACNO, "+
				  "PAYGEDNO, COALESCE(A.PAYINFLG,0) as PAYINFLG, COALESCE(A.PAYDPNO,0) as PAYDPNO, PAYPSW, PAYTITLE, PAYRNK2N, COALESCE(A.PAYRANK3,0) as PAYRANK3, COALESCE(A.PAYDEGR2,0) as PAYDEGR2, "+
				  "COALESCE(A.PAYDEGRE,0) as PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, COALESCE(A.PAYPFDOC,0) as PAYPFDOC, PACSFLAG, "+
				  "COALESCE(A.PAYPFSP1,0) as PAYPFSP1, COALESCE(A.PAYPFSP2,0) as PAYPFSP2, COALESCE(A.PAYPFSP3,0) as PAYPFSP3, A.PATRACOD, "+
				  "PATRADT, COALESCE(A.PAYTAXNO,0) as PAYTAXNO, A.PAYUPDID, A.PAYUPDTM, PAYEXPFKEY1, PAYEXPFKEY2, PAYEXPFKEY3, COALESCE(A.PAYRANK4,0) as PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, "+
				  "INSUAMT, PARETENTION, A.HID, ZPABASPSN,"+
				  "B.PADEPNMC,B.PACSTNO,C.PAYTITLP,C.PATITSUB,D.RANKNAME,E.RNK4NAME,F.TRANAME ";
		sql = "select " + strSelect +
			   " from " + this.tBasPsn +" A "+
			   " left outer join "+ this.tDept+" B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID "+
			   " left outer join "+ this.tTitle+" C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID "+
			   " left outer join "+ this.tRankCode+" D on A.PAYRANK=D.RANKCODE "+
			   " left outer join "+ this.tRnk4Tbl+" E on A.PAYRANK4=E.RNK4KEY "+
			   " left outer join "+ this.tTraCode+" F on A.PATRACOD=F.TRACODE "+
			   " where A.HID=? ";
		params.add(hid);
		
		if (!"".equals(cardno))	{//卡號
			subsql+=" and PAYCARD=? ";
			params.add(cardno);
		}
		else if (!"".equals(idtno))	{//身分證
			subsql+=" and PAYIDNO=? ";
			params.add(idtno);
			
		}
		else if (!"".equals(namec))	{//姓名
			subsql+=" and PAYNAMEC like ? ";
			params.add(namec+"%");
		}
		else if (!"".equals(empno))	{//職號
			subsql+=" and PAYEMPNO like ? ";
			params.add(empno+"%");
		}
		if (!"".equals(lvefg))	{//離職否(Y/N)
			subsql+=" and PAYLVEFG=? ";
			params.add(lvefg);
		}
		if (!"".equals(paret))	{//留職留薪,支領專2(Y/N)
			subsql+=" and PARETENTION=? ";
			params.add(paret);
		}
		if (!"".equals(tracod))	{//異動原因
			subsql+=" and PATRACOD=? ";
			params.add(tracod);
		}
		if (!"".equals(csflag))	{//支領工作獎金(Y/N)
			subsql+=" and PACSFLAG=? ";
			params.add(csflag);
		}
		if (!"".equals(inflg))	{//保險補助比例
			subsql+=" and PAYINFLG<>0 ";
		}
		if (!"".equals(expfsp))	{//額外點數1 2 3有值皆要列入
			subsql+=" and (PAYPFSP1<>0 or PAYPFSP2<>0 or PAYPFSP3<>0) ";			
		}
		if (!(patradt==null || (patradt!=null && patradt.length==0))){ //異動日期
			subsql+=" and PATRADT in ( ";
			for (i=0;i<patradt.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(patradt[i]);	
			}	
			subsql+=")";
		}
		if (!(payrank==null || (payrank!=null && payrank.length==0))){		//階級 
			subsql+=" and PAYRANK in ( ";
			for (i=0;i<payrank.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(payrank[i]);
			}	
			subsql+=")";
		}
		if (!(paytitle==null || (paytitle!=null && paytitle.length==0))){		//職稱 
			subsql+=" and PAYTITLE in ( ";
			for (i=0;i<paytitle.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(paytitle[i]);
			}	
			subsql+=")";
		}
		if (!(paydept==null || (paydept!=null && paydept.length==0))){		//單位 
			subsql+=" and paydept in ( ";
			for (i=0;i<paydept.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(paydept[i]);
			}	
			subsql+=")";
		}
		if (!(expfkey1==null || (expfkey1!=null && expfkey1.length==0))){		//額外點數1 
			subsql+=" and payexpfkey1 in ( ";
			for (i=0;i<expfkey1.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(expfkey1[i]);
			}	
			subsql+=")";
		}		
		if (!(expfkey2==null || (expfkey2!=null && expfkey2.length==0))){		//額外點數2 
			subsql+=" and payexpfkey2 in ( ";
			for (i=0;i<expfkey2.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(expfkey2[i]);
			}	
			subsql+=")";
		}
		if (!(expfkey3==null || (expfkey3!=null && expfkey3.length==0))){		//額外點數3 
			subsql+=" and payexpfkey3 in ( ";
			for (i=0;i<expfkey3.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(expfkey3[i]);
			}	
			subsql+=")";
		}
		
		sql+=subsql+" order by PAYCARD,PAYRSGDT DESC with ur";
		log.info(me+"  " + sql+"...params:"+ params.toString());
		
		try{
			if (da == null) {
				da = new VGHDataAccess(this.dataSourceName); 
				connFlg = true;
			}
			
			start = new Date();
			if ("2".equals(item)){
				cDAO = new CommDAO(da);
				CspfMap=cDAO.getCsPfByAll(da);
			}
			
			log.info(me+"  " + sql+"...params:"+ params.toString());
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"...size:"+result.size());
			
			if(result.size()>0){
				for (Map<String,Object> data :result){
					baspsnObj=new Pabaspsn();
					PropertyUtils.copyProperties(baspsnObj, data);
					
					jsonOne = new JSONObject();
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));
					jsonOne.put("paynamec", StringUtil.nullToString(data.get("paynamec")));
					jsonOne.put("pacstno", StringUtil.nullToString(data.get("pacstno")));
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					if ("2".equals(item)){
						jsonOne.put("paydept", StringUtil.nullToString(data.get("paydept")));
						jsonOne.put("padepnmc", StringUtil.nullToString(data.get("padepnmc")));					
						jsonOne.put("paylvefg", StringUtil.nullToString(data.get("paylvefg")));
						jsonOne.put("payempno", StringUtil.nullToString(data.get("payempno")));
						jsonOne.put("paygovdt", StringUtil.nullToString(data.get("paygovdt")));
						jsonOne.put("payempdt",StringUtil.nullToString(data.get("payempdt")) );
						jsonOne.put("paysgndt", StringUtil.nullToString(data.get("paysgndt")));
						jsonOne.put("payrsgdt", StringUtil.nullToString(data.get("payrsgdt")));
						jsonOne.put("patradt", StringUtil.nullToString(data.get("patradt")));
						jsonOne.put("traname", StringUtil.nullToString(data.get("traname")));
						jsonOne.put("paytitle", StringUtil.nullToString(data.get("paytitle")));
						jsonOne.put("payrnk2n", StringUtil.nullToString(data.get("payrnk2n")));
						jsonOne.put("payrank3", StringUtil.nullToString(data.get("payrank3")));
						jsonOne.put("paydegr2", StringUtil.nullToString(data.get("paydegr2")));
						jsonOne.put("paydegre", StringUtil.nullToString(data.get("paydegre")));
						jsonOne.put("payrank", StringUtil.nullToString(data.get("payrank")));
						jsonOne.put("rankname", StringUtil.nullToString(data.get("rankname")));
						jsonOne.put("payrank2", StringUtil.nullToString(data.get("payrank2")));
						jsonOne.put("payrankc", StringUtil.nullToString(data.get("payrankc")));
						jsonOne.put("paytitlp", StringUtil.nullToString(data.get("paytitlp")));
						jsonOne.put("rnk4name", StringUtil.nullToString(data.get("rnk4name")));
						jsonOne.put("rank3name",PaCommUtil.getRank3Nm((Integer) data.get("payrank3")));
						jsonOne.put("payinflg",PaCommUtil.getInflgNm((Integer) data.get("payinflg")));
						jsonOne.put("paydpno", StringUtil.nullToString(data.get("paydpno")));
						jsonOne.put("paytaxno", StringUtil.nullToString(data.get("paytaxno")));
						jsonOne.put("insuamt", StringUtil.nullToString(data.get("insuamt")));
						jsonOne.put("contract", baspsnObj.getContract());					
						
						jsonOne.put("pacsflag", StringUtil.nullToString(data.get("pacsflag")));
						jsonOne.put("paypfsp1", StringUtil.nullToString(data.get("paypfsp1")));
						jsonOne.put("paypfsp2", StringUtil.nullToString(data.get("paypfsp2")));
						jsonOne.put("paypfsp3", StringUtil.nullToString(data.get("paypfsp3")));
						jsonOne.put("paretention", baspsnObj.getParetention());
						jsonOne.put("paypfdoc", StringUtil.nullToString(data.get("paypfdoc")));
						//工作獎金=Y,找基本績分 俸級績分
						pfbas=0;
						pfdat=0;
						if ("Y".equals(baspsnObj.getPacsflag())){
							if (baspsnObj.getPayrank3()==0)
								vPFKEY=baspsnObj.getPaytitle()+"00000";
							else
								vPFKEY = baspsnObj.getPaytitle() + baspsnObj.getPayrnk2n() + baspsnObj.getPayrank3() + StringUtil.fillChar(StringUtil.nullToString(baspsnObj.getPaydegr2()),2,"0",false) ;
							
							cspfObj=(Pacspf) CspfMap.get(vPFKEY);
							if (cspfObj!=null){
								pfbas= cspfObj.getPacsbase();
								pfdat= cspfObj.getPacsdata();
							}	
						}
						jsonOne.put("pfbas", pfbas);
						jsonOne.put("pfdat", pfdat);
					}	
					jsonRtn.add(jsonOne);
				}
			}
			end = new Date();
			log.info(me+"  ...********* dataaccess:"+(end.getTime()-start.getTime()));
		}catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}finally{
			closeAll(da,null,connFlg);
		}
		
		return jsonRtn;
	}
	
	//依篩選條件,取得pa.padatpsn集合
	public JSONArray getDatPsnList(String code_data,String hid,VGHDataAccess da) throws Exception{
		String me = getClass().getName() + ".getDatPsnList()";
		log.info(me +  " code_data = " + code_data+ " hid = " + hid);
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne =null;
		JSONObject object_code_data =null;
		Date start = new Date();
		Date end = new Date();
		String cardno="";
		String idtno="";
		String namec="";
		String empno="";
		String[] salnum=null;
		String[] paybank=null;
		String[] payrank=null;
		String[] paytitle=null;
		String[] paydept=null;
		String subsql="";
		String strSelect="";
		int i=0;
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> paramsub = new ArrayList<Object>();
		if (code_data!=null && !code_data.equals("")){
			object_code_data = JSONObject.fromObject(code_data);
			cardno=(!object_code_data.has("cardno")) ? "" : object_code_data.getString("cardno");
			idtno=(!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			namec=(!object_code_data.has("namec")) ? "" : object_code_data.getString("namec");
			empno=(!object_code_data.has("empno")) ? "" : object_code_data.getString("empno");
			salnum=(!object_code_data.has("salnum")) ? null : ("".equals(object_code_data.getString("salnum"))  ? null : object_code_data.getString("salnum").split(","));
			paybank=(!object_code_data.has("paybank")) ? null : ("".equals(object_code_data.getString("paybank")) ? null : object_code_data.getString("paybank").split(","));
			payrank=(!object_code_data.has("payrank")) ? null : ("".equals(object_code_data.getString("payrank"))  ? null : object_code_data.getString("payrank").split(","));
			paytitle=(!object_code_data.has("paytitle")) ? null : ("".equals(object_code_data.getString("paytitle"))  ? null : object_code_data.getString("paytitle").split(","));
			paydept=(!object_code_data.has("paydept")) ? null : ("".equals(object_code_data.getString("paydept"))  ? null : object_code_data.getString("paydept").split(","));
			
		}
		strSelect=" PAYIDNO,PAYCARD,PAYBANK,PAYNAMEC,PAYDEPT,SALNUM,PACSTNO,A.HID ";
		sql = "select " + strSelect +
				" from " + this.tDatPsnTbl +" A "+
				" left outer join "+ this.tDept +" B on A.PAYDEPT=B.PADEPCOD AND A.HID=B.HID "+
				" where A.HID=? ";
		params.add(hid);
		
		if (!"".equals(cardno))	{//卡號
			subsql+=" and PAYCARD=? ";
			params.add(cardno);
			paramsub.add(cardno);
		}
		else if (!"".equals(idtno))	{//身分證
			subsql+=" and PAYIDNO=? ";
			params.add(idtno);
			paramsub.add(idtno);
		}
		else if (!"".equals(namec))	{//姓名
			subsql+=" and PAYNAMEC like ? ";
			params.add(namec+"%");
			paramsub.add(namec+"%");
		}
		else if (!"".equals(empno))	{//職號
			subsql+=" and PAYEMPNO like ? ";
			params.add(empno+"%");
			paramsub.add(empno+"%");
		}
		if (!(paybank==null || (paybank!=null && paybank.length==0))){ //發薪日
			subsql+=" and PAYBANK in ( ";
			for (i=0;i<paybank.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(paybank[i]);	
				paramsub.add(paybank[i]);	
			}	
			subsql+=")";
		}
		else if (!(salnum==null || (salnum!=null && salnum.length==0))){	//餉單編號
			subsql+=" and SALNUM in ( ";
			for (i=0;i<salnum.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(salnum[i]);
				paramsub.add(salnum[i]);
			}	
			subsql+=")";
		}
		if (!(payrank==null || (payrank!=null && payrank.length==0))){		//階級 
			subsql+=" and PAYRANK in ( ";
			for (i=0;i<payrank.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(payrank[i]);
				paramsub.add(payrank[i]);
			}	
			subsql+=")";
		}	
		if (!(paytitle==null || (paytitle!=null && paytitle.length==0))){		//職稱 
			subsql+=" and PAYTITLE in ( ";
			for (i=0;i<paytitle.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(paytitle[i]);
				paramsub.add(paytitle[i]);
			}	
			subsql+=")";
		}
		if (!(paydept==null || (paydept!=null && paydept.length==0))){		//單位 
			subsql+=" and paydept in ( ";
			for (i=0;i<paydept.length;i++){
				if (i!=0) subsql+=",";
				subsql+="?";
				params.add(paydept[i]);
				paramsub.add(paydept[i]);
			}	
			subsql+=")";
		}
		sql+=subsql+" union all ";
		//加上歷史月支個人明細
		sql +=  "select " + strSelect +
				" from " + this.tDatPsnDw +" A "+
				" left outer join "+ this.tDept +" B on A.PAYDEPT=B.PADEPCOD AND A.HID=B.HID "+
				" where A.HID=? ";
				
		params.add(hid);
		sql+=subsql+" order by SALNUM DESC,PAYCARD with ur";
		for (int j=0;j<paramsub.size();j++){
			params.add(paramsub.get(j));
		}
		
		log.info(me+"  " + sql+"...params:"+ params.toString());
		
		try{
			if (da == null) {
				da = new VGHDataAccess(this.dataSourceName); 
				connFlg = true;
			}
			start = new Date();
			log.info(me+"  " + sql+"...params:"+ params.toString());
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"...size:"+result.size());
			
			if(result.size()>0){
				for (Map<String,Object> data :result){
					jsonOne = new JSONObject();
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));
					jsonOne.put("paybank", StringUtil.nullToString(data.get("paybank"))); 
					jsonOne.put("paynamec", StringUtil.nullToString(data.get("paynamec")));
					jsonOne.put("paydept", StringUtil.nullToString(data.get("paydept")));
					jsonOne.put("pacstno", StringUtil.nullToString(data.get("pacstno")));
					jsonOne.put("salnum", StringUtil.nullToString(data.get("salnum")));
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					jsonRtn.add(jsonOne);
				}
			}
			end = new Date();
			log.info(me+"  ...********* dataaccess:"+(end.getTime()-start.getTime()));
		}catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}finally{
			closeAll(da,null,connFlg);
		}
		
		return jsonRtn;
	}
	//依餉單編號/發薪日期,取得月支支扣pa.padatpay_Sum
	public JSONArray getCheckPayBykey(String code_data,String hid,VGHDataAccess da) throws Exception{
		String me = getClass().getName() + ".getCheckPayBykey()";
		log.info(me +  " code_data = " + code_data+ " hid = " + hid);
		Padatpay datpayObj=null;
		ArrayList<Padatpay> datpayAry=new ArrayList<Padatpay>();		//支扣集合
		CommDAO cDAO = new CommDAO();
		HashMap rtnMap=new HashMap();
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne =null;
		JSONObject object_code_data =null;
		Date start = new Date();
		Date end = new Date();
		String strSelect="";
		String strPaycode="";
		String subsql="";		
		String salnum="";
		String paybank="";
		String payamtp="";
		String payamtd="";
		
		int i=0;
		HashMap sumListMap=new HashMap();
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> paramsub = new ArrayList<Object>();
		if (code_data!=null && !code_data.equals("")){
			object_code_data = JSONObject.fromObject(code_data);
			paybank=(!object_code_data.has("paybank")) ? "" : object_code_data.getString("paybank");
			salnum=(!object_code_data.has("salnum")) ? "" : object_code_data.getString("salnum");
		}
		//讀取pa.padatpay+pa.padatpay_dw
		strSelect=" a.PAYCODE, SUM(a.PAYAMT) AS sum_amt, b.PACODNMC, a.SALNUM ";
		sql = "select " + strSelect +
			   " from " + this.tDatPayTbl +" A "+
			   " left outer join "+this.tPayIItm+" B on A.PAYCODE=B.PAYCODE and A.HID=B.HID "+
			   " left outer join "+this.tDatPsnTbl+" C on A.PAYIDNO=C.PAYIDNO AND A.SALNUM=C.SALNUM and A.HID=B.HID "+
			  " where A.HID=? ";
		params.add(hid);
		if (!"".equals(paybank))	{//發薪日
			subsql+=" and C.PAYBANK=? ";
			params.add(paybank);
			paramsub.add(paybank);
		}else if (!"".equals(salnum))	{//餉單編號
			subsql+=" and A.SALNUM=? ";
			params.add(salnum);
			paramsub.add(salnum);
		}
		sql+=subsql+" GROUP BY a.SALNUM, a.PAYCODE, b.PACODNMC "+
			"union all ";
		//加上歷史月支明細
		sql+=" select " + strSelect +
			   " from " + this.tDatPayDw +" A "+
			   " left outer join "+this.tPayIItm+" B on A.PAYCODE=B.PAYCODE and A.HID=B.HID "+
			   " left outer join "+this.tDatPsnDw+" C on A.PAYIDNO=C.PAYIDNO AND A.SALNUM=C.SALNUM and A.HID=B.HID "+
			  " where A.HID=? ";
		params.add(hid);
		sql+=subsql+" GROUP BY a.SALNUM, a.PAYCODE, b.PACODNMC "+
		  " order by 1 with ur";
		
		for (int j=0;j<paramsub.size();j++){
			params.add(paramsub.get(j));
		}	  
				
		log.info(me+"  " + sql+"...params:"+ params.toString());
		
		try{
			if (da == null) {
				da = new VGHDataAccess(this.dataSourceName); 
				connFlg = true;
			}
			start = new Date();
			log.info(me+"  " + sql+"...params:"+ params.toString());
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"...size:"+result.size());
			
			if(result.size()>0){
				for (Map<String,Object> data :result){
					jsonOne = new JSONObject();
					payamtp="";
					payamtd="";
					jsonOne.put("salnum", StringUtil.nullToString(data.get("salnum")));
					jsonOne.put("pacodnmc", StringUtil.nullToString(data.get("pacodnmc")));
					strPaycode=StringUtil.nullToString(data.get("paycode"));
					jsonOne.put("paycode", strPaycode);
					jsonOne.put("payamt", StringUtil.nullToString(data.get("sum_amt")));
					if ("P".equals(strPaycode.substring(1,2)))
						payamtp=StringUtil.nullToString(data.get("sum_amt"));
					else if ("D".equals(strPaycode.substring(1,2)))
						payamtd=StringUtil.nullToString(data.get("sum_amt"));
					jsonOne.put("payamtd", payamtd);
					jsonOne.put("payamtp", payamtp);
					
					jsonRtn.add(jsonOne);
				}
			}
			end = new Date();
			log.info(me+"  ...********* dataaccess:"+(end.getTime()-start.getTime()));
		}catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}finally{
			closeAll(da,null,connFlg);
		}
		
		return jsonRtn;
	}
	
	//依KEY,取得pa.padatpay
	public HashMap<String, Object> getDatPayList(String code_data,Padatpsn psnitem,VGHDataAccess da) throws Exception{
		String me = getClass().getName() + ".getDatPayList()";
		log.info(me +  " code_data = " + code_data+ " hid = " + hid);
		Padatpay datpayObj=null;
		ArrayList<Padatpay> datpayAry=new ArrayList<Padatpay>();		//支扣集合
		CommDAO cDAO = new CommDAO();
		HashMap rtnMap=new HashMap();
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne =null;
		JSONObject object_code_data =null;
		Date start = new Date();
		Date end = new Date();
		String strSelect="";
		String idtno="";
		String hid="";
		String salnum=null;
		String strPaycode="";
		String payamtp="";
		String payamtd="";
		int i=0;
		HashMap sumListMap=new HashMap();
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> paramsub = new ArrayList<Object>();
		if (code_data!=null && !code_data.equals("")){
			object_code_data = JSONObject.fromObject(code_data);
			idtno=(!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			salnum=(!object_code_data.has("salnum")) ? "" : object_code_data.getString("salnum");
			hid=(!object_code_data.has("hid")) ? "" : object_code_data.getString("hid");
		}
		//讀取pa.padatpay+pa.padatpay_dw
		strSelect=" SALNUM, PAYIDNO, A.PAYCODE, PAYAMT, A.PAYAUTH, A.PAYIFTAX, A.HID, PACODNMC,PACODDEL,PAYREWARD ";
		sql = "select " + strSelect +
			   " from " + this.tDatPayTbl +" A "+
			   " left outer join "+this.tPayIItm+" B on A.PAYCODE=B.PAYCODE and A.HID=B.HID "+
			  " where SALNUM=? and PAYIDNO=? and A.HID=? "+
			  " union all "+
		  	 " select " + strSelect +
			   " from " + this.tDatPayDw +" A "+
			   " left outer join "+this.tPayIItm+" B on A.PAYCODE=B.PAYCODE and A.HID=B.HID "+
			  " where SALNUM=? and PAYIDNO=? and A.HID=? "+
				"order by 3 with ur ";
		params.add(salnum);
		params.add(idtno);
		params.add(hid);
		params.add(salnum);
		params.add(idtno);
		params.add(hid);
		log.info(me+"  " + sql+"...params:"+ params.toString());
		
		try{
			if (da == null) {
				da = new VGHDataAccess(this.dataSourceName); 
				connFlg = true;
			}
			start = new Date();
			log.info(me+"  " + sql+"...params:"+ params.toString());
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"...size:"+result.size());
			
			if(result.size()>0){
				for (Map<String,Object> data :result){
					//放置在Arraylist
					datpayObj=new Padatpay();
					PropertyUtils.copyProperties(datpayObj, data);					
					datpayAry.add(datpayObj);
					
					jsonOne = new JSONObject();
					payamtp="";
					payamtd="";
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("salnum", StringUtil.nullToString(data.get("salnum")));
					strPaycode=StringUtil.nullToString(data.get("paycode"));
					jsonOne.put("paycode", strPaycode);
					jsonOne.put("payamt", StringUtil.nullToString(data.get("payamt")));
					if ("P".equals(strPaycode.substring(1,2)))
						payamtp=StringUtil.nullToString(data.get("payamt"));
					else if ("D".equals(strPaycode.substring(1,2)))
						payamtd=StringUtil.nullToString(data.get("payamt"));
					jsonOne.put("payamtd", payamtd);
					jsonOne.put("payamtp", payamtp);
					jsonOne.put("payahth", StringUtil.nullToString(data.get("payauth")));
					jsonOne.put("payiftax", StringUtil.nullToString(data.get("payiftax"))); 
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					jsonOne.put("pacodnmc", StringUtil.nullToString(data.get("pacodnmc")));
					jsonOne.put("pacoddel", StringUtil.nullToString(data.get("pacoddel")));
					jsonOne.put("payreward", StringUtil.nullToString(data.get("payreward"))); 
					
					jsonRtn.add(jsonOne);
				}
			}
			rtnMap.put("datpay_json", jsonRtn);
			sumListMap=cDAO.Sum_Pay_Deduct(datpayAry,psnitem.getPayrank(),psnitem.getPayrank2(),psnitem.getPayrnk2n(),psnitem.getPaydegre(),psnitem.getStrgovdt(),psnitem.getStrbank());
			rtnMap.put("datpaySum_map", sumListMap);
			end = new Date();
			log.info(me+"  ...********* dataaccess:"+(end.getTime()-start.getTime()));
		}catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}finally{
			closeAll(da,null,connFlg);
		}
		
		return rtnMap;
	}
	
	//依KEY,取得pa.pabaspay
	public HashMap<String, Object> getBasPayList(String code_data,String hid,VGHDataAccess da) throws Exception{
		String me = getClass().getName() + ".getBasPayList()";
		log.info(me +  " code_data = " + code_data+ " hid = " + hid);
		Pabaspay baspayObj=null;
		ArrayList<Pabaspay> baspayAry=new ArrayList<Pabaspay>();		//支扣集合
		HashMap rtnMap=new HashMap();
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne =null;
		JSONObject object_code_data =null;
		Date start = new Date();
		Date end = new Date();
		String strSelect="";
		String idtno="";
		String salnum=null;
		String strPaycode="";
		String payamtp="";
		String payamtd="";
		int i=0;
		ArrayList<Object> params = new ArrayList<Object>();
		if (code_data!=null && !code_data.equals("")){
			object_code_data = JSONObject.fromObject(code_data);
			idtno=(!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
		}
		//讀取pa.pabaspay
		strSelect=" PAYIDNO, A.PAYCODE, PAYAMT, A.PAYAUTH, A.PAYIFTAX, A.HID, PACODNMC,PACODDEL,PAYREWARD ";
		sql = "select " + strSelect +
			   " from " + this.tBasPay +" A "+
			   " left outer join "+this.tPayIItm+" B on A.PAYCODE=B.PAYCODE and A.HID=B.HID "+
			  " where  PAYIDNO=? and A.HID=? "+
			 	"order by 2 with ur ";
		params.add(idtno);
		params.add(hid);
		log.info(me+"  " + sql+"...params:"+ params.toString());
		
		try{
			if (da == null) {
				da = new VGHDataAccess(this.dataSourceName); 
				connFlg = true;
			}
			start = new Date();
			log.info(me+"  " + sql+"...params:"+ params.toString());
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"...size:"+result.size());
			
			if(result.size()>0){
				for (Map<String,Object> data :result){
					//放置在Arraylist
					baspayObj=new Pabaspay();
					PropertyUtils.copyProperties(baspayObj, data);					
					baspayAry.add(baspayObj);
					
					jsonOne = new JSONObject();
					payamtp="";
					payamtd="";
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("salnum", StringUtil.nullToString(data.get("salnum")));
					strPaycode=StringUtil.nullToString(data.get("paycode"));
					jsonOne.put("paycode", strPaycode);
					jsonOne.put("payamt", StringUtil.nullToString(data.get("payamt")));
					if ("P".equals(strPaycode.substring(1,2)))
						payamtp=StringUtil.nullToString(data.get("payamt"));
					else if ("D".equals(strPaycode.substring(1,2)))
						payamtd=StringUtil.nullToString(data.get("payamt"));
					jsonOne.put("payamtd", payamtd);
					jsonOne.put("payamtp", payamtp);
					jsonOne.put("payahth", StringUtil.nullToString(data.get("payauth")));
					jsonOne.put("payiftax", StringUtil.nullToString(data.get("payiftax"))); 
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					jsonOne.put("pacodnmc", StringUtil.nullToString(data.get("pacodnmc")));
					jsonOne.put("pacoddel", StringUtil.nullToString(data.get("pacoddel")));
					jsonOne.put("payreward", StringUtil.nullToString(data.get("payreward"))); 
					
					jsonRtn.add(jsonOne);
				}
			}
			rtnMap.put("baspay_json", jsonRtn);
			end = new Date();
			log.info(me+"  ...********* dataaccess:"+(end.getTime()-start.getTime()));
		}catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}finally{
			closeAll(da,null,connFlg);
		}
		
		return rtnMap;
	}
	//依KEY,取得pa.patrapay
	public HashMap<String, Object> getTraPayList(String code_data,VGHDataAccess da) throws Exception{
		String me = getClass().getName() + ".getTraPayList()";
		log.info(me +  " code_data = " + code_data+ " hid = " + hid);
		Patrapay trapayObj=null;
		ArrayList<Patrapay> trapayAry=new ArrayList<Patrapay>();		//支扣集合
		HashMap rtnMap=new HashMap();
		JSONArray jsonRtn = new JSONArray();
		JSONArray oTtrapayRtn = new JSONArray();
		JSONArray nTtrapayRtn = new JSONArray();
		JSONObject jsonOne =null;
		JSONObject object_code_data =null;
		Date start = new Date();
		Date end = new Date();
		String strSelect="";
		String idtno="";
		String tranum="";
		String patradt="";
		String strPaysort="";
		String payamtp="";
		String payamtd="";
		String strPaycode="";
		int i=0;
		ArrayList<Object> params = new ArrayList<Object>();
		if (code_data!=null && !code_data.equals("")){
			object_code_data = JSONObject.fromObject(code_data);
			idtno=(!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			tranum=(!object_code_data.has("tranum")) ? "" : object_code_data.getString("tranum");
			patradt=(!object_code_data.has("patradt")) ? "" : object_code_data.getString("patradt");
		}
		//讀取pa.patrapay
		strSelect=" TRANUM, PAYIDNO, PATRADT, PAYSORT, A.PAYCODE, PAYAMT, A.PAYAUTH, A.PAYIFTAX, PAYUPDTM, PAYFLAG, A.HID, PACODNMC,PACODDEL,PAYREWARD,PAYUPDTM ";
		sql = "select " + strSelect +
			   " from " + this.tTraPayTbl +" A "+
			   " left outer join "+this.tPayIItm+" B on A.PAYCODE=B.PAYCODE and A.HID=B.HID "+
			   " where  TRANUM=? and A.HID=? "+
			   " union all "+
			   "select " + strSelect +
			   " from " + this.tTraPayDw +" A "+
			   " left outer join "+this.tPayIItm+" B on A.PAYCODE=B.PAYCODE and A.HID=B.HID "+
			   " where  TRANUM=? and A.HID=? "+
			 	"order by 3,4,5 with ur ";
		params.add(tranum);
		params.add(hid);
		params.add(tranum);
		params.add(hid);
		log.info(me+"  " + sql+"...params:"+ params.toString());
		
		try{
			if (da == null) {
				da = new VGHDataAccess(this.dataSourceName); 
				connFlg = true;
			}
			start = new Date();
			log.info(me+"  " + sql+"...params:"+ params.toString());
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"...size:"+result.size());
			
			if(result.size()>0){
				for (Map<String,Object> data :result){
					//放置在Arraylist
					trapayObj=new Patrapay();
					PropertyUtils.copyProperties(trapayObj, data);					
					trapayAry.add(trapayObj);
					
					jsonOne = new JSONObject();
					payamtp="";
					payamtd="";
					jsonOne.put("tranum", StringUtil.nullToString(data.get("tranum")));
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("patradt", StringUtil.nullToString(data.get("patradt")));
					strPaysort=StringUtil.nullToString(data.get("paysort"));
					jsonOne.put("paysort", strPaysort);
					strPaycode=StringUtil.nullToString(data.get("paycode"));
					jsonOne.put("paycode", strPaycode);
					if ("P".equals(strPaycode.substring(1,2)))
						payamtp=StringUtil.nullToString(data.get("payamt"));
					else if ("D".equals(strPaycode.substring(1,2)))
						payamtd=StringUtil.nullToString(data.get("payamt"));
					jsonOne.put("payamtd", payamtd);
					jsonOne.put("payamtp", payamtp);
					jsonOne.put("payamt", StringUtil.nullToString(data.get("payamt")));
					jsonOne.put("payauth", StringUtil.nullToString(data.get("payauth")));
					jsonOne.put("payflag", StringUtil.nullToString(data.get("payflag")));
					jsonOne.put("payiftax", StringUtil.nullToString(data.get("payiftax"))); 
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					jsonOne.put("pacodnmc", StringUtil.nullToString(data.get("pacodnmc")));
					jsonOne.put("pacoddel", StringUtil.nullToString(data.get("pacoddel")));
					jsonOne.put("payreward", StringUtil.nullToString(data.get("payreward")));
					jsonOne.put("payupdtm", StringUtil.nullToString(data.get("payupdtm")) == "" ? "" : sdf.format(data.get("payupdtm")));
					if ("O".equals(strPaysort)){
						oTtrapayRtn.add(jsonOne);
					}
					else if ("N".equals(strPaysort)){
						nTtrapayRtn.add(jsonOne);
					}
					jsonRtn.add(jsonOne);
				}
			}
			rtnMap.put("trapay_json", jsonRtn);
			rtnMap.put("otrapay_json", oTtrapayRtn);
			rtnMap.put("ntrapay_json", nTtrapayRtn);
			end = new Date();
			log.info(me+"  ...********* dataaccess:"+(end.getTime()-start.getTime()));
		}catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}finally{
			closeAll(da,null,connFlg);
		}
		
		return rtnMap;
	}
	//依KEY,取得pa.pahist
	public HashMap<String, Object> getHistList(String code_data,String hid,VGHDataAccess da) throws Exception{
		String me = getClass().getName() + ".getHistList()";
		log.info(me +  " code_data = " + code_data+ " hid = " + hid);
		Pahist pahistObj=null;
		ArrayList<Pahist> pahistAry=new ArrayList<Pahist>();		//歷史檔集合
		HashMap rtnMap=new HashMap();
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne =null;
		JSONObject object_code_data =null;
		Date start = new Date();
		Date end = new Date();
		String strSelect="";
		String idtno="";
		String salnum=null;
		String strPaycode="";
		String payamtp="";
		String payamtd="";
		int i=0;
		ArrayList<Object> params = new ArrayList<Object>();
		if (code_data!=null && !code_data.equals("")){
			object_code_data = JSONObject.fromObject(code_data);
			idtno=(!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
		}
		//讀取pa.pahist
		
		strSelect="PATRADT,A.PATRACOD, A.PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, "+
				  "PAYTITLE,COALESCE(A.PAYRNK2N,'') as PAYRNK2N, COALESCE(A.PAYRANK3,0) as PAYRANK3, COALESCE(A.PAYDEGR2,0) as PAYDEGR2, "+
				  "COALESCE(A.PAYDEGRE,0) as PAYDEGRE, PAYRANK, COALESCE(A.PAYRANK2,'') as PAYRANK2, COALESCE(A.PAYRANKC,'') as PAYRANKC, "+
				  "PAYEMPDT, PAYRSGDT, A.PAYUPDID, A.PAYUPDTM, A.HID, ZPAHIST,"+
				  "B.PADEPNMC,C.PAYTITLP,C.PATITSUB,D.RANKNAME,COALESCE(F.TRANAME,'')  TRANAME ";
		
		sql = "select " + strSelect +
			   " from " + this.tHist +" A "+
			   " left outer join "+ this.tDept+" B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID "+
			   " left outer join "+ this.tTitle+" C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID "+
			   " left outer join "+ this.tRankCode+" D on A.PAYRANK=D.RANKCODE "+			   
			   " left outer join "+ this.tTraCode+" F on A.PATRACOD=F.TRACODE "+			   
			   " where  A.PAYIDNO=? and A.HID=?  ORDER BY A.PAYUPDTM DESC with ur ";
		
		params.add(idtno);
		params.add(hid);
		log.info(me+"  " + sql+"...params:"+ params.toString());
		
		try{
			if (da == null) {
				da = new VGHDataAccess(this.dataSourceName); 
				connFlg = true;
			}
			start = new Date();
			log.info(me+"  " + sql+"...params:"+ params.toString());
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"...size:"+result.size());
			
			if(result.size()>0){
				for (Map<String,Object> data :result){
					//放置在Arraylist
					pahistObj=new Pahist();
					PropertyUtils.copyProperties(pahistObj, data);					
					pahistAry.add(pahistObj);
					
					jsonOne = new JSONObject();
					jsonOne.put("traname", StringUtil.nullToString(data.get("traname")));
					jsonOne.put("patradt", StringUtil.nullToString(data.get("patradt")));
					jsonOne.put("payrank", StringUtil.nullToString(data.get("rankname")));
					jsonOne.put("payrnk2n", StringUtil.nullToString(data.get("payrnk2n")));
					jsonOne.put("payrank3", StringUtil.nullToString(data.get("payrank3")));
					jsonOne.put("paydegr2", StringUtil.nullToString(data.get("paydegr2")));
					jsonOne.put("paydegre", StringUtil.nullToString(data.get("paydegre")));
					jsonOne.put("payrank2", StringUtil.nullToString(data.get("payrank2")));
					jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard"))); 
					jsonOne.put("payempno", StringUtil.nullToString(data.get("payempno")));
					jsonOne.put("paytitlp", StringUtil.nullToString(data.get("paytitlp")));
					jsonOne.put("payempdt", StringUtil.nullToString(data.get("payempdt")));
					jsonOne.put("payrsgdt", StringUtil.nullToString(data.get("payrsgdt"))); 
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					jsonRtn.add(jsonOne);
				}
			}
			rtnMap.put("pahist_json", jsonRtn);
			end = new Date();
			log.info(me+"  ...********* dataaccess:"+(end.getTime()-start.getTime()));
		}catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}finally{
			closeAll(da,null,connFlg);
		}
		
		return rtnMap;
	}
	//依salnum+paycode找pa.padaypay清單
	public JSONArray getDayPayByCode(String code_data,String hid,VGHDataAccess da) throws Exception{
		String me = getClass().getName() + ".getDayPayByCode()";
		log.info(me +  " code_data = " + code_data+ " hid = " + hid);
		
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne =null;
		JSONObject object_code_data =null;
		Date start = new Date();
		Date end = new Date();
		String strSelect="";
		String paycode="";
		String salnum="";
		
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> paramsub = new ArrayList<Object>();
		if (code_data!=null && !code_data.equals("")){
			object_code_data = JSONObject.fromObject(code_data);
			paycode=(!object_code_data.has("paycode")) ? "" : object_code_data.getString("paycode");
			salnum=(!object_code_data.has("salnum")) ? "" : object_code_data.getString("salnum");
		}
		//讀取pa.padatpay+pa.padatpay_dw
		strSelect=" A.SALNUM,A.PAYIDNO,A.PAYCODE, A.PAYAMT,"+
				   "B.PAYCARD, B.PAYEMPNO,PAYNAMEC, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK2, PAYBANK,"+  
				   "C.PADEPNMC,D.PAYTITLC, E.PACODNMC ";
		sql = "select " + strSelect +
			   " from " + this.tDatPayTbl +" A "+
			   " left outer join "+this.tDatPsnTbl+" B on A.SALNUM=B.SALNUM AND A.PAYIDNO=B.PAYIDNO AND A.HID=B.HID "+
			   " left outer join "+this.tDept+" C on B.PAYDEPT=C.PADEPCOD AND B.HID=C.HID "+
			   " left outer join "+this.tTitle+" D ON B.PAYTITLE=D.PAYTITLK AND B.HID=D.HID "+
			   " left outer join "+this.tPayIItm+" E ON A.PAYCODE =E.PAYCODE AND A.HID=E.HID "+
			  " where A.SALNUM=? and A.PAYCODE=? and A.HID=? "+
			  " union all "+
		  	 " select " + strSelect +
			   " from " + this.tDatPayDw +" A "+
			   " left outer join "+this.tDatPsnDw+" B on A.SALNUM=B.SALNUM AND A.PAYIDNO=B.PAYIDNO AND A.HID=B.HID "+
			   " left outer join "+this.tDept+" C on B.PAYDEPT=C.PADEPCOD AND B.HID=C.HID "+
			   " left outer join "+this.tTitle+" D ON B.PAYTITLE=D.PAYTITLK AND B.HID=D.HID "+
			   " left outer join "+this.tPayIItm+" E ON A.PAYCODE =E.PAYCODE AND A.HID=E.HID "+
			  " where A.SALNUM=? and A.PAYCODE=? and A.HID=? "+
				"order by 5 with ur ";
		params.add(salnum);
		params.add(paycode);
		params.add(hid);
		params.add(salnum);
		params.add(paycode);
		params.add(hid);
		log.info(me+"  " + sql+"...params:"+ params.toString());
		
		try{
			if (da == null) {
				da = new VGHDataAccess(this.dataSourceName); 
				connFlg = true;
			}
			start = new Date();
			log.info(me+"  " + sql+"...params:"+ params.toString());
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"...size:"+result.size());
			
			if(result.size()>0){
				for (Map<String,Object> data :result){
					jsonOne = new JSONObject();
					
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("paycode", StringUtil.nullToString(data.get("paycode")));
					jsonOne.put("payamt", StringUtil.nullToString(data.get("payamt")));
					jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));
					jsonOne.put("payempno", StringUtil.nullToString(data.get("payempno"))); 
					jsonOne.put("paynamec", StringUtil.nullToString(data.get("paynamec")));
					jsonOne.put("payrnk2n", StringUtil.nullToString(data.get("payrnk2n")));
					jsonOne.put("payrank3", StringUtil.nullToString(data.get("payrank3")));
					jsonOne.put("paydegr2", StringUtil.nullToString(data.get("paydegr2")));
					jsonOne.put("paydegre", StringUtil.nullToString(data.get("paydegre"))); 
					jsonOne.put("payrank2", StringUtil.nullToString(data.get("payrank2")));
					
					jsonOne.put("padepnmc", StringUtil.nullToString(data.get("padepnmc"))); 
					jsonOne.put("paytitlc", StringUtil.nullToString(data.get("paytitlc")));
					jsonOne.put("pacodnmc", StringUtil.nullToString(data.get("pacodnmc")));
					jsonRtn.add(jsonOne);
				}
			}
			end = new Date();
			log.info(me+"  ...********* dataaccess:"+(end.getTime()-start.getTime()));
		}catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}finally{
			closeAll(da,null,connFlg);
		}
		
		return jsonRtn;
	}
	//依KEY,取得pa.padatPSN
	public Padatpsn getDatPsnBykey(String code_data,String hid,VGHDataAccess da) throws Exception{
		String me = getClass().getName() + ".getDatPsnBykey()";
		log.info(me +  " code_data = " + code_data+ " hid = " + hid);
		CommDAO cDAO = new CommDAO();
		HashMap CspfMap=new HashMap();		//工作獎金支扣 對照表
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne =null;
		JSONObject object_code_data =null;
		Padatpsn padatpsn=null;
		Date start = new Date();
		Date end = new Date();
		String strSelect="";
		String idtno="";
		String salnum=null;
		int i=0;
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> paramsub = new ArrayList<Object>();
		if (code_data!=null && !code_data.equals("")){
			object_code_data = JSONObject.fromObject(code_data);
			idtno=(!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			salnum=(!object_code_data.has("salnum")) ? "" : object_code_data.getString("salnum");
		}
		//讀取pa.padatpsn+pa.padatpsn_dw
		strSelect=" SALNUM, PAYBANK, A.PAYIDNO, A.PAYCARD, A.PAYEMPNO, A.PAYNAMEC, A.PAYDEPT, A.PAYTITLE, A.PAYRNK2N, "+
					"COALESCE(A.PAYRANK3,0) as PAYRANK3, COALESCE(A.PAYDEGR2,0) as PAYDEGR2, COALESCE(A.PAYDEGRE,0) as PAYDEGRE, A.PAYRANK, A.PAYRANK2, A.PAYRANKC, COALESCE(A.PAYPFDOC,0) as PAYPFDOC, A.PACSFLAG, "+
					"COALESCE(A.PAYPFSP1,0) as PAYPFSP1, COALESCE(A.PAYPFSP2,0) as PAYPFSP2, COALESCE(A.PAYPFSP3,0) as PAYPFSP3, A.PAYUPDID, A.PAYUPDTM, A.PAYRANK4, A.HID, "+
					"B.PADEPNMC,C.PAYTITLP,D.RANKNAME,E.RNK4NAME,F.PAYGOVDT,F.PAYEMPDT ";
		sql = "select " + strSelect +
			   " from " + this.tDatPsnTbl +" A "+
			   " left outer join "+ this.tDept+" B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID "+
			   " left outer join "+ this.tTitle+" C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID "+
			   " left outer join "+ this.tRankCode+" D on A.PAYRANK=D.RANKCODE "+
			   " left outer join "+ this.tRnk4Tbl+" E on A.PAYRANK4=E.RNK4KEY "+
			   " left outer join "+ this.tBasPsn+" F on A.PAYIDNO=F.PAYIDNO AND A.HID=F.HID "+
			   " where SALNUM=? and A.PAYIDNO=? and A.HID=? "+
			  " union all "+
		  	 " select " + strSelect +
			   " from " + this.tDatPsnDw +" A "+
			   " left outer join "+ this.tDept+" B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID "+
			   " left outer join "+ this.tTitle+" C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID "+
			   " left outer join "+ this.tRankCode+" D on A.PAYRANK=D.RANKCODE "+
			   " left outer join "+ this.tRnk4Tbl+" E on A.PAYRANK4=E.RNK4KEY "+
			   " left outer join "+ this.tBasPsn+" F on A.PAYIDNO=F.PAYIDNO AND A.HID=F.HID "+
			  " where SALNUM=? and A.PAYIDNO=? and A.HID=? with ur ";
		params.add(salnum);
		params.add(idtno);
		params.add(hid);
		params.add(salnum);
		params.add(idtno);
		params.add(hid);
		log.info(me+"  " + sql+"...params:"+ params.toString());
		
		try{
			if (da == null) {
				da = new VGHDataAccess(this.dataSourceName); 
				connFlg = true;
			}
			cDAO = new CommDAO(da);
			start = new Date();
			log.info(me+"  " + sql+"...params:"+ params.toString());
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"...size:"+result.size());
			if(result.size()>0){
				for (Map<String,Object> data :result){
					padatpsn=new Padatpsn();
					PropertyUtils.copyProperties(padatpsn, data);
					//工作獎金=Y,找基本績分 俸級績分
					if ("Y".equals(padatpsn.getPacsflag())){
						CspfMap=cDAO.getCsPfByKey(padatpsn.getPayrank3(),padatpsn.getPaytitle(),padatpsn.getPayrank2(),padatpsn.getPayrnk2n(),padatpsn.getPaydegr2(),da);
						padatpsn.setPfkey((String) CspfMap.get("pfkey"));
						padatpsn.setPfbas((String) CspfMap.get("pfbas"));
						padatpsn.setPfdat((String) CspfMap.get("pfdat"));
					}
				}
			}
			
			end = new Date();
			log.info(me+"  ...********* dataaccess:"+(end.getTime()-start.getTime()));
		}catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}finally{
			closeAll(da,null,connFlg);
			cDAO=null;
		}
		
		return padatpsn;
	}
	//依KEY,取得pa.patraPSN
	public Patrapsn getTraPsnBykey(String code_data,VGHDataAccess da) throws Exception{
		String me = getClass().getName() + ".getTraPsnBykey()";
		log.info(me +  " code_data = " + code_data+ " hid = " + hid);
		
		CommDAO cDAO = new CommDAO();
		HashMap CspfMap=new HashMap();		//工作獎金支扣 對照表
		JSONObject object_code_data =null;
		Patrapsn patrapsn=null;
		Date start = new Date();
		Date end = new Date();
		String strSelect="";
		String idtno="";
		String patradt="";
		String patracod="";
		ArrayList<Object> params = new ArrayList<Object>();
		
		if (code_data!=null && !code_data.equals("")){
			object_code_data = JSONObject.fromObject(code_data);
			patradt=(!object_code_data.has("tradt")) ? "" : object_code_data.getString("tradt");
			patracod=(!object_code_data.has("tracod")) ? "" : object_code_data.getString("tracod");
			idtno=(!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			hid=(!object_code_data.has("hid")) ? "" : object_code_data.getString("hid");
		}
		//讀取pa.patrapsn
		strSelect="PATRACOD, PATRADT, COALESCE(PAEFFDT,'9999-12-31') as PAEFFDT, SALNUM, PAYLVEFG, A.PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, "+
				"PAYEMPDT, PAYSGNDT, COALESCE(PAYRSGDT,'9999-12-31') as PAYRSGDT, PAYBKID, PAYACNO, PAYGEDNO, COALESCE(A.PAYINFLG,0) as PAYINFLG,COALESCE(PAYDPNO,0) as PAYDPNO, PAYPFLAG, PAYTITLE, "+
				"PAYRNK2N, COALESCE(A.PAYRANK3,0) as PAYRANK3, COALESCE(A.PAYDEGR2,0) as PAYDEGR2, COALESCE(A.PAYDEGRE,0) as PAYDEGRE, PAYRANK, COALESCE(A.PAYRANK2,'') as PAYRANK2, "+ 
				"COALESCE(A.PAYRANKC,'') as PAYRANKC, COALESCE(A.PAYPFDOC,0) as PAYPFDOC, PACSFLAG, COALESCE(A.PAYPFSP1,0) as PAYPFSP1, "+ 
				"COALESCE(A.PAYPFSP2,0) as PAYPFSP2, COALESCE(A.PAYPFSP3,0) as PAYPFSP3, COALESCE(A.PAYMONNO,0) as PAYMONNO, COALESCE(A.PAYDAYNO,0) as PAYDAYNO, PAYPFLAG,PAOTITLE, PAORNK2N, COALESCE(A.PAORANK3,0) as PAORANK3, "+
				"COALESCE(A.PAODEGR2,0) as PAODEGR2, COALESCE(A.PAODEGRE,0) as PAODEGRE, PAORANK, COALESCE(A.PAORANK2,'') as PAORANK2, COALESCE(A.PAYRANKC,'') as PAORANKC, COALESCE(A.PAOPFDOC,0) as PAOPFDOC, PAOCSFLA, "+
				"COALESCE(A.PAOPFSP1,0) as PAOPFSP1, COALESCE(A.PAOPFSP2,0) as PAOPFSP2, COALESCE(A.PAOPFSP3,0) as PAOPFSP3, COALESCE(A.PAYTAXNO,0) as PAYTAXNO,"+
				"A.PAYUPDID, A.PAYUPDTM,PAYFLAG, TRANUM, UPMASTDT, PAYPSW, PAOEXPFKEY1, PAOEXPFKEY2, PAOEXPFKEY3, PAYEXPFKEY1, PAYEXPFKEY2, "+ 
				"PAYEXPFKEY3, COALESCE(A.PAORANK4,0) as PAORANK4, COALESCE(A.PAYRANK4,0) as PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, INSUAMT, A.HID, "+
				"B.PADEPNMC,B.PACSTNO,C.PAYTITLP,H.PAYTITLP PAYTITLO,C.PATITSUB,H.PATITSUB PATITSUO,D.RANKNAME,I.RANKNAME RANKNAMO,"+
				"E.RNK4NAME,J.RNK4NAME RNK4NAMO,F.TRANAME,G.PAYADDRS ";
		
		sql = "select " + strSelect +
			   " from " + this.tTraPsnTbl +" A "+
			   " left outer join "+ this.tDept+" B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID "+
			   " left outer join "+ this.tTitle+" C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID "+
			   " left outer join "+ this.tRankCode+" D on A.PAYRANK=D.RANKCODE "+
			   " left outer join "+ this.tRnk4Tbl+" E on A.PAYRANK4=E.RNK4KEY "+
			   " left outer join "+ this.tTraCode+" F on A.PATRACOD=F.TRACODE "+
			   " left outer join "+ this.tBasAdr+" G on A.PAYIDNO=G.PAYIDNO "+
			   " left outer join "+ this.tTitle+" H on A.PAOTITLE=H.PAYTITLK and A.HID=H.HID "+
			   " left outer join "+ this.tRankCode+" I on A.PAORANK=I.RANKCODE "+
			   " left outer join "+ this.tRnk4Tbl+" J on A.PAORANK4=J.RNK4KEY "+
			  " where A.PAYIDNO=? and PATRADT=? and PATRACOD=? and A.HID=? "+
		 	  " union all "+
			  "select " + strSelect +
			   " from " + this.tTraPsnDw +" A "+
			   " left outer join "+ this.tDept+" B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID "+
			   " left outer join "+ this.tTitle+" C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID "+
			   " left outer join "+ this.tRankCode+" D on A.PAYRANK=D.RANKCODE "+
			   " left outer join "+ this.tRnk4Tbl+" E on A.PAYRANK4=E.RNK4KEY "+
			   " left outer join "+ this.tTraCode+" F on A.PATRACOD=F.TRACODE "+
			   " left outer join "+ this.tBasAdr+" G on A.PAYIDNO=G.PAYIDNO "+
			   " left outer join "+ this.tTitle+" H on A.PAOTITLE=H.PAYTITLK and A.HID=H.HID "+
			   " left outer join "+ this.tRankCode+" I on A.PAORANK=I.RANKCODE "+
			   " left outer join "+ this.tRnk4Tbl+" J on A.PAORANK4=J.RNK4KEY "+
			  " where A.PAYIDNO=? and PATRADT=? and PATRACOD=? and A.HID=? "+
		       " with ur";
		params.add(idtno);
		params.add(patradt);
		params.add(patracod);
		params.add(hid);
		params.add(idtno);
		params.add(patradt);
		params.add(patracod);
		params.add(hid);
		log.info(me+"  " + sql+"...params:"+ params.toString());
		try{
			if (da == null) {
				da = new VGHDataAccess(this.dataSourceName); 
				connFlg = true;
			}
			cDAO = new CommDAO(da);
			start = new Date();
			log.info(me+"  " + sql+"...params:"+ params.toString());
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"...size:"+result.size());
			if(result.size()>0){
				for (Map<String,Object> data :result){
					patrapsn=new Patrapsn();
					PropertyUtils.copyProperties(patrapsn, data);
					//工作獎金=Y,找基本績分 俸級績分
					if ("Y".equals(patrapsn.getPacsflag())){
						CspfMap=cDAO.getCsPfByKey(patrapsn.getPayrank3(),patrapsn.getPaytitle(),patrapsn.getPayrank2(),patrapsn.getPayrnk2n(),patrapsn.getPaydegr2(),da);
						patrapsn.setPfkey((String) CspfMap.get("pfkey"));
						patrapsn.setPfbas((String) CspfMap.get("pfbas"));
						patrapsn.setPfdat((String) CspfMap.get("pfdat"));
					}
					if ("Y".equals(patrapsn.getPaocsfla())){
						CspfMap=cDAO.getCsPfByKey(patrapsn.getPaorank3(),patrapsn.getPaotitle(),patrapsn.getPaorank2(),patrapsn.getPaornk2n(),patrapsn.getPaodegr2(),da);
						patrapsn.setPokey((String) CspfMap.get("pfkey"));
						patrapsn.setPobas((String) CspfMap.get("pfbas"));
						patrapsn.setPodat((String) CspfMap.get("pfdat"));
					}
				}
			}
			
			end = new Date();
			log.info(me+"  ...********* dataaccess:"+(end.getTime()-start.getTime()));
		}catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}finally{
			closeAll(da,null,connFlg);
			cDAO=null;
		}
		
		return patrapsn;
	}
	//依KEY,取得pa.pabasPSN
	public Pabaspsn getBasPsnBykey(String code_data,VGHDataAccess da) throws Exception{
		String me = getClass().getName() + ".getBasPsnBykey()";
		log.info(me +  " code_data = " + code_data+ " hid = " + hid);
		CommDAO cDAO = new CommDAO();
		HashMap CspfMap=new HashMap();		//工作獎金支扣 對照表
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne =null;
		JSONObject object_code_data =null;
		Pabaspsn pabaspsn=null;
		Date start = new Date();
		Date end = new Date();
		String strSelect="";
		String idtno="";
		
		ArrayList<Object> params = new ArrayList<Object>();
		
		if (code_data!=null && !code_data.equals("")){
			object_code_data = JSONObject.fromObject(code_data);
			idtno=(!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			hid=(!object_code_data.has("hid")) ? "" : object_code_data.getString("hid");
		}
		//讀取pa.pabaspsn
		strSelect="PAYLVEFG, A.PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, PAYEMPDT, PAYSGNDT, COALESCE(PAYRSGDT,'9999-12-31') PAYRSGDT, PAYBKID, PAYACNO, "+
				  "PAYGEDNO, PAYINFLG, PAYDPNO, PAYPSW, PAYTITLE, PAYRNK2N, COALESCE(A.PAYRANK3,0) as PAYRANK3, COALESCE(A.PAYDEGR2,0) as PAYDEGR2, "+
				  "COALESCE(A.PAYDEGRE,0) as PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, COALESCE(A.PAYPFDOC,0) as PAYPFDOC, PACSFLAG, "+
				  "COALESCE(A.PAYPFSP1,0) as PAYPFSP1, COALESCE(A.PAYPFSP2,0) as PAYPFSP2, COALESCE(A.PAYPFSP3,0) as PAYPFSP3, A.PATRACOD, "+
				  "PATRADT, PAYTAXNO, A.PAYUPDID, A.PAYUPDTM, PAYEXPFKEY1, PAYEXPFKEY2, PAYEXPFKEY3, PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, "+
				  "INSUAMT, PARETENTION, A.HID, ZPABASPSN,"+
				  "B.PADEPNMC,C.PAYTITLP,C.PATITSUB,D.RANKNAME,E.RNK4NAME,F.TRANAME,G.PAYADDRS ";
		sql = "select " + strSelect +
			   " from " + this.tBasPsn +" A "+
			   " left outer join "+ this.tDept+" B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID "+
			   " left outer join "+ this.tTitle+" C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID "+
			   " left outer join "+ this.tRankCode+" D on A.PAYRANK=D.RANKCODE "+
			   " left outer join "+ this.tRnk4Tbl+" E on A.PAYRANK4=E.RNK4KEY "+
			   " left outer join "+ this.tTraCode+" F on A.PATRACOD=F.TRACODE "+
			   " left outer join "+ this.tBasAdr+" G on A.PAYIDNO=G.PAYIDNO "+
			   " where  A.PAYIDNO=? and A.HID=?  with ur ";
		
		params.add(idtno);
		params.add(hid);
		log.info(me+"  " + sql+"...params:"+ params.toString());
		
		try{
			if (da == null) {
				da = new VGHDataAccess(this.dataSourceName); 
				connFlg = true;
			}
			cDAO = new CommDAO(da);
			start = new Date();
			log.info(me+"  " + sql+"...params:"+ params.toString());
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"...size:"+result.size());
			if(result.size()>0){
				for (Map<String,Object> data :result){
					pabaspsn=new Pabaspsn();
					PropertyUtils.copyProperties(pabaspsn, data);
					//工作獎金=Y,找基本績分 俸級績分
					if ("Y".equals(pabaspsn.getPacsflag())){
						CspfMap=cDAO.getCsPfByKey(pabaspsn.getPayrank3(),pabaspsn.getPaytitle(),pabaspsn.getPayrank2(),pabaspsn.getPayrnk2n(),pabaspsn.getPaydegr2(),da);
						pabaspsn.setPfkey((String) CspfMap.get("pfkey"));
						pabaspsn.setPfbas((String) CspfMap.get("pfbas"));
						pabaspsn.setPfdat((String) CspfMap.get("pfdat"));
					}
				}
			}
			
			end = new Date();
			log.info(me+"  ...********* dataaccess:"+(end.getTime()-start.getTime()));
		}catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}finally{
			closeAll(da,null,connFlg);
			cDAO=null;
		}
		return pabaspsn;
	}
	//依table名單及KEY,取得各項參考檔
	public JSONArray qryCodeList(String tblname,String code_data,String hid,VGHDataAccess da) throws Exception{
		String me = getClass().getName() + ".qryCodeList()";
		log.info(me + " tblname = " + tblname+ " code_data = " + code_data+ " hid = " + hid);
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne =null;
		Date start = new Date();
		Date end = new Date();
		ArrayList<Object> params = new ArrayList<Object>();
		String subsql="";
		sql="";
		String key="";
		JSONObject object_code_data =null;
		if (code_data!=null && !code_data.equals("")){
			object_code_data = JSONObject.fromObject(code_data);
			if (tblname.matches("PATITLE|PATAXTBL|PAPOSI|PARANK")) {//KEY不止一項				
				
			}else{
				key=StringUtil.nullToString(object_code_data.getString("key"));
			}	
		}
		
		if ("PATRACODE".equals(tblname)){//異動代碼 PATRACOD
		
			sql = "SELECT TRANAME, TRACODE FROM "+ this.tTraCode;
			if (!key.equals("")){
				subsql+=" where TRACODE = ? ";
				params.add(key);
			}
			sql=sql+subsql+" ORDER BY TRACODE WITH UR";
		}
		else if ("PADEPT".equals(tblname)){//單位代碼 
			
			sql = "SELECT PADEPCOD, PACSTNO, PADEPNMC, PAYUPDID, PAYUPDAT, HID "+
			        "FROM "+ this.tDept;			      
			if (!key.equals("")){
				subsql+=" where PADEPCOD = ? ";
				params.add(key);
			}
			if (hid==null||"".equals(hid)){
				
			} else {
				if ("".equals(subsql))subsql+=" where ";
				else subsql+=" and ";
				subsql+=" HID = ? ";
				params.add(hid);
			}
			sql=sql+subsql+" ORDER BY PACSTNO,PADEPCOD WITH UR";
		}
		else if ("PARANKCODE".equals(tblname)){//階別代碼  
			
			sql = "SELECT RANKCODE,RANKNAME,RANKABBR FROM "+ this.tRankCode;
			if (!key.equals("")){
				subsql+=" where RANKCODE = ? ";
				params.add(key);
			}

			sql=sql+subsql+" ORDER BY RANKCODE WITH UR";
		}
		else if ("PACATGTBL".equals(tblname)){//人工造冊應計轉帳項目代碼  
			
			sql = "SELECT PAYCATG, CATGNAME, PAYUPDID, PAYUPDAT FROM "+ this.tCatgTbl;
			if (!key.equals("")){
				subsql+=" where PAYCATG = ? ";
				params.add(key);
			}

			sql=sql+subsql+" ORDER BY PAYCATG WITH UR";
		}
		else if ("PAEXPFTBL".equals(tblname)){//績效額外點數對照表   
			
			sql = "SELECT EXPFKEY, EXPFNAME, PAYUPDID, PAYUPDAT FROM "+ this.tExpfTbl;
			if (!key.equals("")){
				subsql+=" where EXPFKEY = ? ";
				params.add(key);
			}

			sql=sql+subsql+" ORDER BY EXPFKEY WITH UR";
		}
		else if ("PARNK4TBL".equals(tblname)){//適用專業加給對照表   
			
			sql = "SELECT RNK4KEY, RNK4NAME, PAYUPDTM FROM "+ this.tRnk4Tbl;
			if (!key.equals("")){
				subsql+=" where RNK4KEY = ? ";
				params.add(key);
			}

			sql=sql+subsql+" ORDER BY RNK4KEY WITH UR";
		}
		else if ("PABANK".equals(tblname)){//發薪日及入帳日對應檔 
			
			sql = "SELECT SALNUM, PAYBANK, PAYSALDT, PAYUPDID, PAYUPDAT FROM "+ this.tBankDtTbl;
			if (!key.equals("")){
				subsql+=" where SALNUM = ? ";
				params.add(key);
			}

			sql=sql+subsql+" ORDER BY SALNUM DESC WITH UR";
		}
		else if ("PATAXTBL".equals(tblname)){//薪資所得扣繳稅額表  
			
			sql = "SELECT SALLOW, SALHIGH, KINDRED, TAXAMT, PAYUPDID, PAYUPDAT FROM "+ this.tTaxTbl;
			if (!object_code_data.getString("sallow").equals("")){
				subsql+=" where SALLOW = ? ";
				params.add(StringUtil.nullToString(object_code_data.getString("sallow")));
				if (!object_code_data.getString("kindred").equals("")){
					subsql+=" and KINDRED=? ";
					params.add(StringUtil.nullToString(object_code_data.getString("kindred")));
				}	
			}

			sql=sql+subsql+" ORDER BY SALLOW, KINDRED WITH UR";
		}
		else if ("PAPFTL".equals(tblname)){//全院績效彙總   
			
			sql = "SELECT PAYPFMON, PAYPFUNT, PAPFAMT, PAYPFPF, PAYPFREV, PAYPFYMD, PAYPFD, PAYPFE FROM "+ this.tPftlTbl;
			if (!key.equals("")){
				subsql+=" where PAYPFMON = ? ";
				params.add(key);
			}

			sql=sql+subsql+" ORDER BY PAYPFMON desc WITH UR";
		}
		else if ("PADPINTBL".equals(tblname)){//全民健康保險負擔金額表   
			
			sql = "SELECT DPINLOW, DPINHIGH, DPINAMT, PAYUPDID, PAYUPDAT, DPINDEPT1, DPINDEPT3, ZPADPINTBL FROM "+ this.tDpinTbl;
			if (!key.equals("")){
				subsql+=" where DPINLOW = ? ";
				params.add(key);
			}

			sql=sql+subsql+ " ORDER BY DPINLOW WITH UR";
		}
		else if ("PALBINTBL".equals(tblname)){//勞保投保金額對照表  
			
			sql = "SELECT LBINLOW, LBINHIGH, LBINAMT, LBINDEPT, ZPALBINTBL FROM "+ this.tLbinTbl;
			if (!key.equals("")){
				subsql+=" where LBINLOW = ? ";
				params.add(key);
			}

			sql=sql+subsql+ " ORDER BY LBINLOW WITH UR";
		}
		else if ("PALBRPTBL".equals(tblname)){//勞工退休金月提繳工資分級表 
			
			sql = "SELECT LBRPLOW, LBRPHIGH, LBRPAMT, MASTERAMT, ZPALBRPTBL FROM "+ this.tLbrpTbl;
			if (!key.equals("")){
				subsql+=" where LBRPLOW = ? ";
				params.add(key);
			}

			sql=sql+subsql+ " ORDER BY LBRPLOW WITH UR";
		}
		else if ("PATITLE".equals(tblname)){//職稱 
			
			sql = "SELECT PAYTITLK, PATITSUB, PACHIEF, PAYTITLP, PAYTITLC, PACODDEL, PAYUPDID, PAYUPDAT, HID FROM "+ this.tTitle;
			if (!StringUtil.nullToString(object_code_data.getString("paytitlk")).equals("")){
				subsql+=" where PAYTITLK = ? ";
				params.add(StringUtil.nullToString(object_code_data.getString("paytitlk")));
			}	
			
			if (!StringUtil.nullToString(object_code_data.get("patitsub")).equals("")){
				if ("".equals(subsql))subsql+=" where ";
				else subsql+=" and ";
				subsql+=" PATITSUB=? ";
				params.add(StringUtil.nullToString(object_code_data.get("patitsub")));					
			}
			if (!StringUtil.nullToString(object_code_data.get("pacoddel")).equals("")){
				if ("".equals(subsql))subsql+=" where ";
				else subsql+=" and ";
				subsql+=" PACODDEL=? ";
				params.add(StringUtil.nullToString(object_code_data.get("pacoddel")));					
			}
			
			if (hid==null||"".equals(hid)){
				
			} else {
				if ("".equals(subsql))subsql+=" where ";
				else subsql+=" and ";
				subsql+=" HID = ? ";
				params.add(hid);
			}

			sql=sql+subsql+ " ORDER BY PATITSUB,PAYTITLK WITH UR";
		}
		else if ("PAPOSI".equals(tblname)){//職等支扣--專業加給及主管加給
			
			sql = "SELECT PAYPOSIK, DEGRLOW, DEGRHIGH, PAYPSP1, PAYPSP2, PAYPSP3, PAYPSP4, PAYPSP5, PAYPSP6, PAYPOSID, PAYPSP7, PAYUPDAT, PAYPSP8 FROM "+ this.tPosiTbl;
			if (!StringUtil.nullToString(object_code_data.getString("payposik")).equals("")){
				subsql+=" where PAYPOSIK = ? ";
				params.add(StringUtil.nullToString(object_code_data.getString("payposik")));
			}	
			
			if (!StringUtil.nullToString(object_code_data.get("degrlow")).equals("")){
				if ("".equals(subsql))subsql+=" where ";
				else subsql+=" and ";
				subsql+=" DEGRLOW=? ";
				params.add(StringUtil.nullToString(object_code_data.get("degrlow")));					
			}
			
			sql=sql+subsql+ " ORDER BY PAYPOSIK, DEGRLOW WITH UR";
		}
		else if ("PARANK".equals(tblname)){//階級支扣
			
			sql = "SELECT PAYLEV1V, PAYLEV2V, PAYDEGRV, PAYRANKV, PAYBASEV, PAYGEINF, PAYLBINF, PAYDPINV, PAYEMPFV, PAYREBEF, PAYUSEID, PAYUPDAT FROM "+ this.tRankTbl;
			if (!StringUtil.nullToString(object_code_data.getString("paylev1v")).equals("")){
				subsql+=" where PAYLEV1V = ? ";
				params.add(StringUtil.nullToString(object_code_data.getString("paylev1v")));
			}	
			
			if (!StringUtil.nullToString(object_code_data.get("paylev2v")).equals("")){
				if ("".equals(subsql))subsql+=" where ";
				else subsql+=" and ";
				subsql+=" PAYLEV2V=? ";
				params.add(StringUtil.nullToString(object_code_data.get("paylev2v")));					
			}
			
			if (!StringUtil.nullToString(object_code_data.get("payrankv")).equals("")){
				if ("".equals(subsql))subsql+=" where ";
				else subsql+=" and ";
				subsql+=" PAYRANKV=? ";
				params.add(StringUtil.nullToString(object_code_data.get("payrankv")));					
			}
			sql=sql+subsql+ " ORDER BY PAYLEV1V, PAYLEV2V, PAYRANKV WITH UR";
		}
		else if ("PACSPF".equals(tblname)){//工作獎金支扣 
			
			sql = "SELECT PACSKEY, PACSBASE, PACSDATA, PACSNAME, ZPACSPF FROM "+ this.tCspfTbl;
			if (!key.equals("")){
				subsql+=" where substr(PACSKEY,1,2)= ? ";
				params.add(key);
			}	
			sql=sql+subsql+ " ORDER BY PACSKEY WITH UR";
		}
		try{
			if (da == null) {
				da = new VGHDataAccess(this.dataSourceName); 
				connFlg = true;
			}
			start = new Date();
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"  " + sql+"...params:"+ params.toString()+"...size:"+result.size());
			
			if(result.size()>0){
				for (Map<String,Object> data :result){
					jsonOne = new JSONObject(); 
					if ("PATRACODE".equals(tblname)){//異動代碼 PATRACODE
						jsonOne.put("tracode", StringUtil.nullToString(data.get("tracode"))); 
						jsonOne.put("traname", StringUtil.nullToString(data.get("traname")));
					}
					else if ("PADEPT".equals(tblname)){//單位代碼 PAdept
						jsonOne.put("padepcod", StringUtil.nullToString(data.get("padepcod"))); 
						jsonOne.put("pacstno", StringUtil.nullToString(data.get("pacstno")));
						jsonOne.put("padepnmc", StringUtil.nullToString(data.get("padepnmc")));
						jsonOne.put("payupdid", StringUtil.nullToString(data.get("payupdid")));
						jsonOne.put("payupdat", StringUtil.nullToString(data.get("payupdat")) == "" ? "" : sdf.format(data.get("payupdat")));
						jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					}
					else if ("PARANKCODE".equals(tblname)){//階別代碼 
						jsonOne.put("rankcode", StringUtil.nullToString(data.get("rankcode"))); 
						jsonOne.put("rankname", StringUtil.nullToString(data.get("rankname")));
						jsonOne.put("rankabbr", StringUtil.nullToString(data.get("rankabbr")));
					}
					else if ("PACATGTBL".equals(tblname)){//人工造冊應計轉帳項目代碼 
						jsonOne.put("paycatg", StringUtil.nullToString(data.get("paycatg"))); 
						jsonOne.put("catgname", StringUtil.nullToString(data.get("catgname")));
						jsonOne.put("payupdid", StringUtil.nullToString(data.get("payupdid")));
						jsonOne.put("payupdat", StringUtil.nullToString(data.get("payupdat")) == "" ? "" : sdf.format(data.get("payupdat")));
					}
					else if ("PAEXPFTBL".equals(tblname)){//績效額外點數對照表 
						jsonOne.put("expfkey", StringUtil.nullToString(data.get("expfkey"))); 
						jsonOne.put("expfname", StringUtil.nullToString(data.get("expfname")));
						jsonOne.put("payupdid", StringUtil.nullToString(data.get("payupdid")));
						jsonOne.put("payupdat", StringUtil.nullToString(data.get("payupdat")) == "" ? "" : sdf.format(data.get("payupdat")));
					}
					else if ("PARNK4TBL".equals(tblname)){//適用專業加給對照表
						jsonOne.put("rnk4key", StringUtil.nullToString(data.get("rnk4key"))); 
						jsonOne.put("rnk4name", StringUtil.nullToString(data.get("rnk4name")));
						jsonOne.put("payupdtm", StringUtil.nullToString(data.get("payupdtm")) == "" ? "" : sdf.format(data.get("payupdtm")));
					}
					else if ("PABANK".equals(tblname)){//發薪日及入帳日對應檔 
						jsonOne.put("salnum", StringUtil.nullToString(data.get("salnum"))); 
						jsonOne.put("paybank", StringUtil.nullToString(data.get("paybank")));
						jsonOne.put("paysaldt", StringUtil.nullToString(data.get("paysaldt")));
						jsonOne.put("payupdid", StringUtil.nullToString(data.get("payupdid")));
						jsonOne.put("payupdat", StringUtil.nullToString(data.get("payupdat")) == "" ? "" : sdf.format(data.get("payupdat")));
					}
					else if ("PATAXTBL".equals(tblname)){//薪資所得扣繳稅額表 
						jsonOne.put("sallow", StringUtil.nullToString(data.get("sallow"))); 
						jsonOne.put("kindred", StringUtil.nullToString(data.get("kindred")));
						jsonOne.put("salhigh", StringUtil.nullToString(data.get("salhigh")));
						jsonOne.put("taxamt", StringUtil.nullToString(data.get("taxamt")));
						jsonOne.put("payupdid", StringUtil.nullToString(data.get("payupdid")));
						jsonOne.put("payupdat", StringUtil.nullToString(data.get("payupdat")) == "" ? "" : sdf.format(data.get("payupdat")));
					}
					else if ("PAPFTL".equals(tblname)){//全院績效彙總  
						jsonOne.put("paypfmon", StringUtil.nullToString(data.get("paypfmon"))); 
						jsonOne.put("paypfunt", StringUtil.nullToString(data.get("paypfunt")));
						jsonOne.put("papfamt", StringUtil.nullToString(data.get("papfamt")));
						jsonOne.put("paypfpf", StringUtil.nullToString(data.get("paypfpf")));
						jsonOne.put("paypfrev", StringUtil.nullToString(data.get("paypfrev")));
						jsonOne.put("paypfd", StringUtil.nullToString(data.get("paypfd")));
						jsonOne.put("paypfe", StringUtil.nullToString(data.get("paypfe")));
						jsonOne.put("paypfymd", StringUtil.nullToString(data.get("paypfymd")) == "" ? "" : sdf.format(data.get("paypfymd")));
					}
					else if ("PADPINTBL".equals(tblname)){//全民健康保險負擔金額表   
						jsonOne.put("dpinlow", StringUtil.nullToString(data.get("dpinlow"))); 
						jsonOne.put("dpinhigh", StringUtil.nullToString(data.get("dpinhigh")));
						jsonOne.put("dpinamt", StringUtil.nullToString(data.get("dpinamt")));
						jsonOne.put("dpindept1", StringUtil.nullToString(data.get("dpindept1")));
						jsonOne.put("dpindept3", StringUtil.nullToString(data.get("dpindept3")));
						jsonOne.put("payupdid", StringUtil.nullToString(data.get("payupdid")));
						jsonOne.put("zpadpintbl", StringUtil.nullToString(data.get("zpadpintbl")) == "" ? "" : sdf.format(data.get("zpadpintbl")));
					}
					else if ("PALBINTBL".equals(tblname)){//勞保投保金額對照表    
						jsonOne.put("lbinlow", StringUtil.nullToString(data.get("lbinlow"))); 
						jsonOne.put("lbinhigh", StringUtil.nullToString(data.get("lbinhigh")));
						jsonOne.put("lbinamt", StringUtil.nullToString(data.get("lbinamt")));
						jsonOne.put("lbindept", StringUtil.nullToString(data.get("lbindept")));
						jsonOne.put("zpalbintbl", StringUtil.nullToString(data.get("zpalbintbl")) == "" ? "" : sdf.format(data.get("zpalbintbl")));
					}
					else if ("PALBRPTBL".equals(tblname)){//勞工退休金月提繳工資分級表    
						jsonOne.put("lbrplow", StringUtil.nullToString(data.get("lbrplow"))); 
						jsonOne.put("lbrphigh", StringUtil.nullToString(data.get("lbrphigh")));
						jsonOne.put("lbrpamt", StringUtil.nullToString(data.get("lbrpamt")));
						jsonOne.put("masteramt", StringUtil.nullToString(data.get("masteramt")));
						jsonOne.put("zpalbrptbl", StringUtil.nullToString(data.get("zpalbrptbl")) == "" ? "" : sdf.format(data.get("zpalbrptbl")));
					}
					else if ("PATITLE".equals(tblname)){//職稱代碼    
						jsonOne.put("paytitlk", StringUtil.nullToString(data.get("paytitlk"))); 
						jsonOne.put("patitsub", StringUtil.nullToString(data.get("patitsub")));
						jsonOne.put("pachief", StringUtil.nullToString(data.get("pachief")));
						jsonOne.put("paytitlp", StringUtil.nullToString(data.get("paytitlp")));
						jsonOne.put("paytitlc", StringUtil.nullToString(data.get("paytitlc")));
						jsonOne.put("pacoddel", StringUtil.nullToString(data.get("pacoddel")));
						jsonOne.put("payupdid", StringUtil.nullToString(data.get("payupdid")));
						jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
						jsonOne.put("payupdat", StringUtil.nullToString(data.get("payupdat")) == "" ? "" : sdf.format(data.get("payupdat")));
					}
					else if ("PAPOSI".equals(tblname)){//職等支扣--專業加給及主管加給   
						jsonOne.put("payposik", StringUtil.nullToString(data.get("payposik"))); 
						jsonOne.put("degrlow", StringUtil.nullToString(data.get("degrlow")));
						jsonOne.put("degrhigh", StringUtil.nullToString(data.get("degrhigh")));
						jsonOne.put("paypsp1", StringUtil.nullToString(data.get("paypsp1")));
						jsonOne.put("paypsp2", StringUtil.nullToString(data.get("paypsp2")));
						jsonOne.put("paypsp3", StringUtil.nullToString(data.get("paypsp3")));
						jsonOne.put("paypsp4", StringUtil.nullToString(data.get("paypsp4")));
						jsonOne.put("paypsp5", StringUtil.nullToString(data.get("paypsp5")));
						jsonOne.put("paypsp6", StringUtil.nullToString(data.get("paypsp6")));
						jsonOne.put("paypsp7", StringUtil.nullToString(data.get("paypsp7")));
						jsonOne.put("paypsp8", StringUtil.nullToString(data.get("paypsp8")));
						jsonOne.put("payposid", StringUtil.nullToString(data.get("payposid")));
						jsonOne.put("payupdat", StringUtil.nullToString(data.get("payupdat")) == "" ? "" : sdf.format(data.get("payupdat")));
					}
					else if ("PARANK".equals(tblname)){//階級支扣   
						jsonOne.put("paylev1v", StringUtil.nullToString(data.get("paylev1v"))); 
						jsonOne.put("paylev2v", StringUtil.nullToString(data.get("paylev2v")));
						jsonOne.put("paydegrv", StringUtil.nullToString(data.get("paydegrv")));
						jsonOne.put("payrankv", StringUtil.nullToString(data.get("payrankv")));
						jsonOne.put("paybasev", StringUtil.nullToString(data.get("paybasev")));
						jsonOne.put("paygeinf", StringUtil.nullToString(data.get("paygeinf")));
						jsonOne.put("paylbinf", StringUtil.nullToString(data.get("paylbinf")));
						jsonOne.put("paydpinv", StringUtil.nullToString(data.get("paydpinv")));
						jsonOne.put("payempfv", StringUtil.nullToString(data.get("payempfv")));
						jsonOne.put("payrebef", StringUtil.nullToString(data.get("payrebef")));
						jsonOne.put("payuseid", StringUtil.nullToString(data.get("payuseid")));
						jsonOne.put("payupdat", StringUtil.nullToString(data.get("payupdat")) == "" ? "" : sdf.format(data.get("payupdat")));
					}
					else if ("PACSPF".equals(tblname)){//工作獎金支扣 
						jsonOne.put("pacskey", StringUtil.nullToString(data.get("pacskey"))); 
						jsonOne.put("pacsbase", StringUtil.nullToString(data.get("pacsbase")));
						jsonOne.put("pacsdata", StringUtil.nullToString(data.get("pacsdata")));
						jsonOne.put("pacsname", StringUtil.nullToString(data.get("pacsname")));
						jsonOne.put("zpacspf", StringUtil.nullToString(data.get("zpacspf")) == "" ? "" : sdf.format(data.get("zpacspf")));
					}
					jsonRtn.add(jsonOne);
				}
			}
			end = new Date();
			log.info(me+"  " + sql+"...********* dataaccess:"+(end.getTime()-start.getTime()));
		}catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}finally{
			closeAll(da,null,connFlg);
		}
		return jsonRtn;
	}
	/**
	 * insert/update (代碼維護 ) 
	 * item:insert/update,tabJson(tblname,value)
	 * 傳出:true/false
	 */	
	public boolean updSalCodeTbl(String item,String tabJson,String userid,String hid,VGHDataAccess da) throws SQLException {
		String me = getClass().getName() + ".updSalCodeTbl()";
		log.info(me + " item = " + item+ " tabJson = " + tabJson+ " userid = " + userid+ " hid = " + hid);
		boolean success=false;
		int updCnt = 0;
		String tblname ="";
		ArrayList<Object> params = new ArrayList<Object>();
		try{
			if (da == null) {
				da = new VGHDataAccess(this.dataSourceName); 
				connFlg = true;
			}
			
			JSONArray tabJsonary = JSONArray.fromObject(tabJson);
			for (int i=0; i< tabJsonary.size(); ++i) {
				JSONObject obj = tabJsonary.getJSONObject(i);
				if ( obj != null &&  obj.has("tblname") ) {
					tblname = StringUtil.nullToString( obj.getString("tblname") );
					if ("PATRACODE".equals(tblname)){//異動代碼 
						if (item.equals("update"))
							sql =" update "+ this.tTraCode+ " set (TRANAME) = (?) where TRACODE=? ";
						else if (item.equals("insert"))
							sql =" insert into "+ this.tTraCode+ " (TRANAME, TRACODE) values (?,?) ";
					}else if ("PADEPT".equals(tblname)){//單位PADEPCOD, PACSTNO, PADEPNMC, PAYUPDID, PAYUPDAT, HID
						if (item.equals("update"))
							sql =" update "+ this.tDept+ " set (PACSTNO, PADEPNMC, PAYUPDID, PAYUPDAT) = (?,?,?,CURRENT TIMESTAMP) where HID=? and PADEPCOD=? ";
						else if (item.equals("insert"))
							sql =" insert into "+ this.tDept+ " (PACSTNO, PADEPNMC, PAYUPDID, PAYUPDAT, HID,PADEPCOD) values (?,?,?,CURRENT TIMESTAMP,?,?) ";
					}else if ("PARANKCODE".equals(tblname)){//階別代碼
						if (item.equals("update"))
							sql =" update "+ this.tRankCode+ " set (RANKNAME, RANKABBR) = (?,?) where RANKCODE=? ";
						else if (item.equals("insert"))
							sql =" insert into "+ this.tRankCode+ " (RANKNAME,RANKABBR,RANKCODE) values (?,?,?) ";
					}else if ("PACATGTBL".equals(tblname)){//人工造冊應計轉帳項目代碼 
						if (item.equals("update"))
							sql =" update "+ this.tCatgTbl+ " set (CATGNAME,PAYUPDID, PAYUPDAT) = (?,?,CURRENT TIMESTAMP) where PAYCATG=? ";
						else if (item.equals("insert"))
							sql =" insert into "+ this.tCatgTbl+ " (CATGNAME,PAYUPDID, PAYUPDAT,PAYCATG) values (?,?,CURRENT TIMESTAMP,?) ";
					}else if ("PAEXPFTBL".equals(tblname)){//績效額外點數對照表 
						if (item.equals("update"))
							sql =" update "+ this.tExpfTbl+ " set (EXPFNAME, PAYUPDID, PAYUPDAT) = (?,?,CURRENT TIMESTAMP) where EXPFKEY=? ";
						else if (item.equals("insert"))
							sql =" insert into "+ this.tExpfTbl+ " (EXPFNAME, PAYUPDID, PAYUPDAT,EXPFKEY) values (?,?,CURRENT TIMESTAMP,?) ";
					}else if ("PARNK4TBL".equals(tblname)){//適用專業加給對照表 
						if (item.equals("update"))
							sql =" update "+ this.tRnk4Tbl+ " set (RNK4NAME, PAYUPDTM) = (?,CURRENT TIMESTAMP) where RNK4KEY=? ";
						else if (item.equals("insert"))
							sql =" insert into "+ this.tRnk4Tbl+ " (RNK4NAME, PAYUPDTM,RNK4KEY) values (?,CURRENT TIMESTAMP,?) ";
					}else if ("PABANK".equals(tblname)){//發薪日及入帳日對應檔 
						if (item.equals("update"))
							sql =" update "+ this.tBankDtTbl+ " set (PAYBANK, PAYSALDT, PAYUPDID, PAYUPDAT) = (?,?,?,CURRENT TIMESTAMP) where SALNUM=? ";
						else if (item.equals("insert"))
							sql =" insert into "+ this.tBankDtTbl+ " (PAYBANK, PAYSALDT, PAYUPDID, PAYUPDAT,SALNUM) values (?,?,?,CURRENT TIMESTAMP,?) ";
					}else if ("PATAXTBL".equals(tblname)){//薪資所得扣繳稅額表 
						if (item.equals("update"))
							sql =" update "+ this.tTaxTbl+ " set ( SALHIGH, TAXAMT, PAYUPDID, PAYUPDAT) = (?,?,?,CURRENT TIMESTAMP) where SALLOW=? and KINDRED=? ";
						else if (item.equals("insert"))
							sql =" insert into "+ this.tTaxTbl+ " (SALHIGH, TAXAMT, PAYUPDID, PAYUPDAT,SALLOW,KINDRED) values (?,?,?,CURRENT TIMESTAMP,?,?) ";
					}else if ("PAPFTL".equals(tblname)){//全院績效彙總 
						if (item.equals("update"))
							sql =" update "+ this.tPftlTbl+ " set ( PAYPFUNT, PAPFAMT, PAYPFPF, PAYPFREV, PAYPFYMD, PAYPFD, PAYPFE) = (?,?,?,?,CURRENT TIMESTAMP,?,?) where PAYPFMON=? ";
						else if (item.equals("insert"))
							sql =" insert into "+ this.tPftlTbl+ " (PAYPFUNT, PAPFAMT, PAYPFPF, PAYPFREV, PAYPFYMD, PAYPFD, PAYPFE,PAYPFMON) values (?,?,?,?,CURRENT TIMESTAMP,?,?,?) ";
					}else if ("PADPINTBL".equals(tblname)){//全民健康保險負擔金額表 
						if (item.equals("update"))
							sql =" update "+ this.tDpinTbl+ " set (DPINHIGH, DPINAMT, PAYUPDID, PAYUPDAT, DPINDEPT1, DPINDEPT3) = (?,?,?,CURRENT TIMESTAMP,?,?) where DPINLOW=? ";
						else if (item.equals("insert"))
							sql =" insert into "+ this.tDpinTbl+ " (DPINHIGH, DPINAMT, PAYUPDID, PAYUPDAT, DPINDEPT1, DPINDEPT3,DPINLOW) values (?,?,?,CURRENT TIMESTAMP,?,?,?) ";
					}else if ("PALBINTBL".equals(tblname)){//勞保投保金額對照表 
						if (item.equals("update"))
							sql =" update "+ this.tLbinTbl+ " set (LBINHIGH, LBINAMT, LBINDEPT) = (?,?,?) where LBINLOW=? ";
						else if (item.equals("insert"))
							sql =" insert into "+ this.tLbinTbl+ " (LBINHIGH, LBINAMT, LBINDEPT,LBINLOW) values (?,?,?,?) ";
					}else if ("PALBRPTBL".equals(tblname)){//勞工退休金月提繳工資分級表
						if (item.equals("update"))
							sql =" update "+ this.tLbrpTbl+ " set (LBRPHIGH, LBRPAMT, MASTERAMT) = (?,?,?) where LBRPLOW=? ";
						else if (item.equals("insert"))
							sql =" insert into "+ this.tLbrpTbl+ " (LBRPHIGH, LBRPAMT, MASTERAMT,LBRPLOW) values (?,?,?,?) ";
					}else if ("PATITLE".equals(tblname)){//職稱代碼 
						if (item.equals("update"))
							sql =" update "+ this.tTitle+ " set (PATITSUB, PACHIEF, PAYTITLP, PAYTITLC, PACODDEL, PAYUPDID, PAYUPDAT) = (?,?,?,?,?,?,CURRENT TIMESTAMP) where PAYTITLK=? and HID=? ";
						else if (item.equals("insert"))
							sql =" insert into "+ this.tTitle+ " (PATITSUB, PACHIEF, PAYTITLP, PAYTITLC, PACODDEL, PAYUPDID, PAYUPDAT, PAYTITLK,HID) values (?,?,?,?,?,?,CURRENT TIMESTAMP,?,?) ";
					}else if ("PAPOSI".equals(tblname)){//職等支扣--專業加給及主管加給
						if (item.equals("update"))
							sql =" update "+ this.tPosiTbl+ " set (DEGRHIGH, PAYPSP1, PAYPSP2, PAYPSP3, PAYPSP4, PAYPSP5, PAYPSP6, PAYPSP7, PAYPSP8, PAYPOSID, PAYUPDAT) = (?,?,?,?,?,?,?,?,?,?,CURRENT TIMESTAMP) where PAYPOSIK=? and DEGRLOW=? ";
						else if (item.equals("insert"))
							sql =" insert into "+ this.tPosiTbl+ " (DEGRHIGH, PAYPSP1, PAYPSP2, PAYPSP3, PAYPSP4, PAYPSP5, PAYPSP6, PAYPSP7, PAYPSP8, PAYPOSID, PAYUPDAT, PAYPOSIK, DEGRLOW) values (?,?,?,?,?,?,?,?,?,?,CURRENT TIMESTAMP,?,?) ";
					}else if ("PARANK".equals(tblname)){//階級支扣
						if (item.equals("update"))
							sql =" update "+ this.tRankTbl+ " set (PAYRANKV, PAYBASEV, PAYGEINF, PAYLBINF, PAYDPINV, PAYREBEF, PAYUSEID, PAYUPDAT) = (?,?,?,?,?,?,?,CURRENT TIMESTAMP) where PAYLEV1V=? and PAYLEV2V=? and PAYDEGRV=? ";
						else if (item.equals("insert"))
							sql =" insert into "+ this.tRankTbl+ " (PAYRANKV, PAYBASEV, PAYGEINF, PAYLBINF, PAYDPINV, PAYREBEF, PAYUSEID, PAYUPDAT,PAYLEV1V, PAYLEV2V, PAYDEGRV) values (?,?,?,?,?,?,?,CURRENT TIMESTAMP,?,?,?) ";
					}else if ("PACSPF".equals(tblname)){//工作獎金支扣 
						if (item.equals("update"))
							sql =" update "+ this.tCspfTbl+ " set (PACSBASE, PACSDATA, PACSNAME) = (?,?,?) where PACSKEY=? ";
						else if (item.equals("insert"))
							sql =" insert into "+ this.tCspfTbl+ " (PACSBASE, PACSDATA, PACSNAME, PACSKEY) values (?,?,?,?) ";
					}else if ("PATRAPAY".equals(tblname)){//異動支扣
						if (item.equals("update"))
							sql =" update "+ this.tTraPayTbl+ " set (PAYIDNO, PAYAMT, PAYAUTH, PAYIFTAX, PAYUPDTM, PAYFLAG) = (?,?,?,?,current timestapm,?) where TRANUM=? and PATRADT=? and PAYSORT =? and PAYCODE=? and HID=? ";
						else if (item.equals("insert"))
							sql =" insert into "+ this.tCspfTbl+ " (PAYIDNO, PAYAMT, PAYAUTH, PAYIFTAX, PAYUPDTM, PAYFLAG,TRANUM,PATRADT,PAYSORT ,PAYCODE,HID) values (?,?,?,?,current timestamp,?,?,?,?,?,?) ";
					}
					
					JSONArray array = obj.getJSONArray("value");
					params = new ArrayList<Object>();
					if (null != array && array.size() > 0 ) {
						for (int j=0; j<array.size(); j++) {
							JSONObject obj_item = array.getJSONObject(j);
							if ("PATRACODE".equals(tblname)){//異動代碼 
								params.add((!obj_item.has("traname")) ? "" : obj_item.getString("traname"));
								params.add((!obj_item.has("tracode")) ? "" : obj_item.getString("tracode"));								
							}else if ("PADEPT".equals(tblname)){//單位
								params.add((!obj_item.has("pacstno")) ? "" : obj_item.getString("pacstno"));
								params.add((!obj_item.has("padepnmc")) ? "" : obj_item.getString("padepnmc"));
								params.add(userid);
								params.add(hid);
								params.add((!obj_item.has("padepcod")) ? "" : obj_item.getString("padepcod"));
							}else if ("PARANKCODE".equals(tblname)){//階別
								params.add((!obj_item.has("rankname")) ? "" : obj_item.getString("rankname"));
								params.add((!obj_item.has("rankabbr")) ? "" : obj_item.getString("rankabbr"));
								params.add((!obj_item.has("rankcode")) ? "" : obj_item.getString("rankcode"));
							}else if ("PACATGTBL".equals(tblname)){//人工造冊應計轉帳項目代碼 
								params.add((!obj_item.has("catgname")) ? "" : obj_item.getString("catgname"));
								params.add(userid);
								params.add((!obj_item.has("paycatg")) ? "" : obj_item.getString("paycatg"));
							}else if ("PAEXPFTBL".equals(tblname)){//績效額外點數對照表
								params.add((!obj_item.has("expfname")) ? "" : obj_item.getString("expfname"));
								params.add(userid);
								params.add((!obj_item.has("expfkey")) ? "" : obj_item.getString("expfkey"));
							}else if ("PARNK4TBL".equals(tblname)){//適用專業加給對照表
								params.add((!obj_item.has("rnk4name")) ? "" : obj_item.getString("rnk4name"));
								params.add((!obj_item.has("rnk4key")) ? "" : obj_item.getString("rnk4key"));
							}else if ("PABANK".equals(tblname)){//發薪日及入帳日對應檔
								params.add((!obj_item.has("paybank")) ? "" : obj_item.getString("paybank"));
								params.add((!obj_item.has("paysaldt")) ? "" : obj_item.getString("paysaldt"));
								params.add(userid);
								params.add((!obj_item.has("salnum")) ? "" : obj_item.getString("salnum"));
							}else if ("PATAXTBL".equals(tblname)){//薪資所得扣繳稅額表 
								params.add((!obj_item.has("salhigh")) ? "" : obj_item.getString("salhigh"));
								params.add((!obj_item.has("taxamt")) ? "" : obj_item.getString("taxamt"));
								params.add(userid);
								params.add((!obj_item.has("sallow")) ? "" : obj_item.getString("sallow"));
								params.add((!obj_item.has("kindred")) ? "" : obj_item.getString("kindred"));
							}else if ("PAPFTL".equals(tblname)){//全院績效彙總  
								params.add((!obj_item.has("paypfunt")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paypfunt"))) ? "0" :StringUtil.nullToString(obj_item.getString("paypfunt")));
								params.add((!obj_item.has("papfamt")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("papfamt"))) ? "0" :StringUtil.nullToString(obj_item.getString("papfamt")));
								params.add((!obj_item.has("paypfpf")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paypfpf"))) ? "0" :StringUtil.nullToString(obj_item.getString("paypfpf")));
								params.add((!obj_item.has("paypfrev")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paypfrev"))) ? "0" :StringUtil.nullToString(obj_item.getString("paypfrev")));
								params.add((!obj_item.has("paypfd")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paypfd"))) ? "0" :StringUtil.nullToString(obj_item.getString("paypfd")));
								params.add((!obj_item.has("paypfe")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paypfe"))) ? "0" :StringUtil.nullToString(obj_item.getString("paypfe")));
								params.add((!obj_item.has("paypfmon")) ? "" : obj_item.getString("paypfmon"));
							}else if ("PADPINTBL".equals(tblname)){//全民健康保險負擔金額表  
								params.add((!obj_item.has("dpinhigh")) ? "" : StringUtil.nullToString(obj_item.getString("dpinhigh")));
								params.add((!obj_item.has("dpinamt")) ? "" : StringUtil.nullToString(obj_item.getString("dpinamt")));
								params.add((!obj_item.has("payupdid")) ? "" : StringUtil.nullToString(obj_item.getString("payupdid")));
								params.add((!obj_item.has("dpindept1")) ? "" : StringUtil.nullToString(obj_item.getString("dpindept1")));
								params.add((!obj_item.has("dpindept3")) ? "" : StringUtil.nullToString(obj_item.getString("dpindept3")));
								params.add((!obj_item.has("dpinlow")) ? "" : StringUtil.nullToString(obj_item.getString("dpinlow")));
							}else if ("PALBINTBL".equals(tblname)){//勞保投保金額對照表  
								params.add((!obj_item.has("lbinhigh")) ? "" : StringUtil.nullToString(obj_item.getString("lbinhigh")));
								params.add((!obj_item.has("lbinamt")) ? "" : StringUtil.nullToString(obj_item.getString("lbinamt")));
								params.add((!obj_item.has("lbindept")) ? "" : StringUtil.nullToString(obj_item.getString("lbindept")));
								params.add((!obj_item.has("lbinlow")) ? "" : StringUtil.nullToString(obj_item.getString("lbinlow")));
							}else if ("PALBRPTBL".equals(tblname)){//勞工退休金月提繳工資分級表 
								params.add((!obj_item.has("lbrphigh")) ? "" : StringUtil.nullToString(obj_item.getString("lbrphigh")));
								params.add((!obj_item.has("lbrpamt")) ? "" : StringUtil.nullToString(obj_item.getString("lbrpamt")));
								params.add((!obj_item.has("masteramt")) ? "" : StringUtil.nullToString(obj_item.getString("masteramt")));
								params.add((!obj_item.has("lbrplow")) ? "" : StringUtil.nullToString(obj_item.getString("lbrplow")));
							}else if ("PATITLE".equals(tblname)){//職稱
								params.add((!obj_item.has("patitsub")) ? "" : obj_item.getString("patitsub"));
								params.add((!obj_item.has("pachief")) ? "" : obj_item.getString("pachief"));
								params.add((!obj_item.has("paytitlp")) ? "" : obj_item.getString("paytitlp"));
								params.add((!obj_item.has("paytitlc")) ? "" : obj_item.getString("paytitlc"));
								params.add((!obj_item.has("pacoddel")) ? "" : obj_item.getString("pacoddel"));
								params.add(userid);
								params.add((!obj_item.has("paytitlk")) ? "" : obj_item.getString("paytitlk"));
								params.add(hid);
							}else if ("PAPOSI".equals(tblname)){//職等支扣--專業加給及主管加給
								params.add((!obj_item.has("degrhigh")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("degrhigh"))) ? "0" :StringUtil.nullToString(obj_item.getString("degrhigh")));
								params.add((!obj_item.has("paypsp1")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paypsp1"))) ? "0" :StringUtil.nullToString(obj_item.getString("paypsp1")));
								params.add((!obj_item.has("paypsp2")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paypsp2"))) ? "0" :StringUtil.nullToString(obj_item.getString("paypsp2")));
								params.add((!obj_item.has("paypsp3")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paypsp3"))) ? "0" :StringUtil.nullToString(obj_item.getString("paypsp3")));
								params.add((!obj_item.has("paypsp4")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paypsp4"))) ? "0" :StringUtil.nullToString(obj_item.getString("paypsp4")));
								params.add((!obj_item.has("paypsp5")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paypsp5"))) ? "0" :StringUtil.nullToString(obj_item.getString("paypsp5")));
								params.add((!obj_item.has("paypsp6")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paypsp6"))) ? "0" :StringUtil.nullToString(obj_item.getString("paypsp6")));
								params.add((!obj_item.has("paypsp7")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paypsp7"))) ? "0" :StringUtil.nullToString(obj_item.getString("paypsp7")));
								params.add((!obj_item.has("paypsp8")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paypsp8"))) ? "0" :StringUtil.nullToString(obj_item.getString("paypsp8")));
								params.add(userid);
								params.add((!obj_item.has("payposik")) ? "" : obj_item.getString("payposik"));
								params.add((!obj_item.has("degrlow")) ? "" : obj_item.getString("degrlow"));
							}else if ("PARANK".equals(tblname)){//階級支扣
								params.add((!obj_item.has("payrankv")) ? "" : obj_item.getString("payrankv"));
								params.add((!obj_item.has("paybasev")) ? "" : obj_item.getString("paybasev"));
								params.add((!obj_item.has("paygeinf")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paygeinf"))) ? "0" :StringUtil.nullToString(obj_item.getString("paygeinf")));
								params.add((!obj_item.has("paylbinf")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paylbinf"))) ? "0" :StringUtil.nullToString(obj_item.getString("paylbinf")));
								params.add((!obj_item.has("paydpinv")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("paydpinv"))) ? "0" :StringUtil.nullToString(obj_item.getString("paydpinv")));
								params.add((!obj_item.has("payrebef")) ? "" : ("").equals(StringUtil.nullToString(obj_item.getString("payrebef"))) ? "0" :StringUtil.nullToString(obj_item.getString("payrebef")));
								params.add(userid);
								params.add((!obj_item.has("paylev1v")) ? "" : obj_item.getString("paylev1v"));
								params.add((!obj_item.has("paylev2v")) ? "" : obj_item.getString("paylev2v"));
								params.add((!obj_item.has("paydegrv")) ? "" : obj_item.getString("paydegrv"));
							}else if ("PACSPF".equals(tblname)){//工作獎金支扣 
								params.add((!obj_item.has("pacsbase")) ? "" : obj_item.getString("pacsbase"));
								params.add((!obj_item.has("pacsdata")) ? "" : obj_item.getString("pacsdata"));
								params.add((!obj_item.has("pacsname")) ? "" : obj_item.getString("pacsname"));
								params.add((!obj_item.has("pacskey")) ? "" : obj_item.getString("pacskey"));
							}
							else if ("PATRAPAY".equals(tblname)){//異動支扣
								//PAYIDNO, PAYAMT, PAYAUTH, PAYIFTAX, PAYUPDTM, PAYFLAG,TRANUM,PATRADT,PAYSORT ,PAYCODE,HID
								params.add((!obj_item.has("payidno")) ? "" : obj_item.getString("payidno"));
								params.add((!obj_item.has("payamt")) ? "" : obj_item.getString("payamt"));
								params.add((!obj_item.has("payiftax")) ? "" : obj_item.getString("payiftax"));
								params.add((!obj_item.has("payflag")) ? "" : obj_item.getString("payflag"));
								params.add((!obj_item.has("tranum")) ? "" : obj_item.getString("tranum"));
								params.add((!obj_item.has("patradt")) ? "" : obj_item.getString("patradt"));
								params.add((!obj_item.has("paysort")) ? "" : obj_item.getString("paysort"));
								params.add((!obj_item.has("paycode")) ? "" : obj_item.getString("paycode"));
								params.add((!obj_item.has("hid")) ? "" : obj_item.getString("hid"));
							}
							log.info(me+"  " + sql+"...params:"+ params.toString());
							updCnt=da.update(sql, params);
							log.info(me+"  updCnt:" + updCnt);
							da.commit();
							log.info(me + "...updcnt:"+ updCnt);
							if (updCnt>0) success=true;
						}	
					}
				}
			}
			
		}catch(Throwable e){
			throw new SQLException(me+"(error):" +e.toString());
		} finally{
			closeAll(da,null,connFlg);
		}
		return success;
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
