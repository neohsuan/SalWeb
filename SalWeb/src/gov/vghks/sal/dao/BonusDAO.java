package gov.vghks.sal.dao;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import gov.vghks.common.util.DateUtil;
import gov.vghks.common.util.StringUtil;
import gov.vghks.common.util.VGHDataAccess;
import gov.vghks.sal.Globals;
import gov.vghks.sal.Hospitals;
import gov.vghks.sal.Tables;
import gov.vghks.sal.model.Pabaspay;
import gov.vghks.sal.model.Pabaspsn;
import gov.vghks.sal.model.Pabatpay;
import gov.vghks.sal.model.Pacspf;
import gov.vghks.sal.model.Padatpay;
import gov.vghks.sal.model.Padatpsn;
import gov.vghks.sal.model.Padept;
import gov.vghks.sal.model.Palbrptbl;
import gov.vghks.sal.model.Paprtdtl;
import gov.vghks.sal.model.Pareport;
import gov.vghks.sal.model.Patitle;
import gov.vghks.sal.model.Patrapay;
import gov.vghks.sal.model.Patrapsn;
import gov.vghks.sal.model.Pawkretir;
import gov.vghks.sal.model.PsInsu;
import gov.vghks.sal.model.Psbasic;
import gov.vghks.sal.util.AmJsonConfig;
import gov.vghks.sal.util.FileUtil;
import gov.vghks.sal.util.PaCommUtil;
import gov.vghks.sal.util.StringUtil2;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

//績效組
public class BonusDAO {
	String gClassName = BonusDAO.class.getName();
	static Logger log = Logger.getLogger(BonusDAO.class);  //呼叫log4j
	private VGHDataAccess da = null;
	private VGHDataAccess daDSN = null;
	static ArrayList<Map<String, Object>> result = null;
	static ArrayList<Object> params = new ArrayList<Object>();
	static String g_dataSourceNT = "";
	static String g_dataSource = "";
	private static final int META_TYPE = 2; 			//回傳map中欄位名稱是小寫
	private static boolean connFlg = false;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private String sql = "";
	private String hid = "";
	private String tTraCode  = "";	// 1異動代碼
	private String tDept 	 = ""; 	// 2單位代碼
	private String tTitle	 = ""; 	// 3職稱
	private String tBasPsn 	 = ""; 	// 4基本檔
	private String tBasPay   = ""; // 5基本支扣
	private String tRnk4Tbl  = "";	// 7適用專業加給對照表
	private String tRankCode = ""; 	// 10階別代碼
	private String tDatPsn   = ""; // 23月支個人明細
	private String tDatPay   = ""; // 25月支支扣明細
	private String tTraPsn   = ""; // 30異動個人明細
	private String tTeaChaw  = "" ; // 38教職及論文獎勵
	private String tmpZdatpsn ="";
	
	private String tmpZbaspsn = "";	
	
	public BonusDAO(VGHDataAccess da1,String hid) {
		super();
		this.da = da1;
		connFlg = true;
		this.hid = hid;
		Tables tabInfo = Tables.getTables(hid);
		tTraCode = tabInfo.tTraCode;
		tTitle 	  = tabInfo.tTitle;
		tDept 	  = tabInfo.tDept;
		tRankCode = tabInfo.tRankCode;
		tBasPsn   = tabInfo.tBasPsn;
		tBasPay   = tabInfo.tBasPay;
		tRnk4Tbl  = tabInfo.tRnk4Tbl;
		tDatPsn   = tabInfo.tDatPsn;
		tDatPay   = tabInfo.tDatPay;
		tTraPsn   = tabInfo.tTraPsn;
		tTeaChaw  = tabInfo.tTeaChaw;
		tmpZbaspsn = tabInfo.tmpZbaspsn;
		tmpZdatpsn =tabInfo.tmpZdatpsn;
	}
	// 取得績效_異動_查詢集合 異動碼不列(12	考績獎金 41支扣    51年終獎金)
	public JSONArray getBousTraList(String in_data, String userid, String hid, VGHDataAccess da)
			throws Exception {
		String me = getClass().getName() + ".getBousTraList(), userid:" + userid;
		log.info(me + " in_data = " + in_data + " hid = " + hid + " userid = " + userid);
		CommDAO cDAO = null;
		JSONArray jsonRtn = new JSONArray();
		ArrayList<Object> params = new ArrayList<Object>();
		JSONObject jsonOne = null;
		JSONObject object_in_data = null;
		HashMap CspfMap = new HashMap();
		HashMap cspfRtn = new HashMap();
		Patrapsn oneobj = null;
		long starttime = 0;
		String cardno = "";
		String idtno = "";
		String namec = "";
		String tradt = null;
		String[] tracod = null;
		String strSelect = "";
		String sqlA = "";
		String perf = "";	//1:醫師,2:員工
		String subsql = "";
		int i = 0;
		int pfbas = 0;
		int pfdat = 0;
		String pfkey = "";
		if (in_data != null && !in_data.equals("")) {
			object_in_data = JSONObject.fromObject(in_data);
			cardno = (!object_in_data.has("cardno")) ? "" : object_in_data.getString("cardno");
			idtno = (!object_in_data.has("idtno")) ? "" : object_in_data.getString("idtno");
			namec = (!object_in_data.has("namec")) ? "" : object_in_data.getString("namec");
			tradt = (!object_in_data.has("tradt")) ? "" : object_in_data.getString("tradt");
			perf = (!object_in_data.has("perf")) ? "" : object_in_data.getString("perf");
			tracod = (!object_in_data.has("tracod")) ? null
					: ("".equals(object_in_data.getString("tracod")) ? null
							: object_in_data.getString("tracod").split(","));
		}
		// 讀取pa.patrapsn
		strSelect = "PATRACOD, PATRADT, PAEFFDT, SALNUM, PAYLVEFG, PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, "
				+ "PAYEMPDT, PAYSGNDT, PAYRSGDT, PAYBKID, PAYACNO, PAYGEDNO, COALESCE(A.PAYINFLG,0) as PAYINFLG,COALESCE(PAYDPNO,'0') as PAYDPNO, PAYPFLAG, PAYTITLE, "
				+ "PAYRNK2N, COALESCE(A.PAYRANK3,0) as PAYRANK3, COALESCE(A.PAYDEGR2,0) as PAYDEGR2, COALESCE(A.PAYDEGRE,0) as PAYDEGRE, PAYRANK, PAYRANK2, "
				+ "PAYRANKC, COALESCE(A.PAYPFDOC,0) as PAYPFDOC, PACSFLAG, COALESCE(A.PAYPFSP1,0) as PAYPFSP1, "
				+ "COALESCE(A.PAYPFSP2,0) as PAYPFSP2, COALESCE(A.PAYPFSP3,0) as PAYPFSP3, PAYMONNO, PAYDAYNO, PAOTITLE, PAORNK2N, COALESCE(A.PAORANK3,0) as PAORANK3, "
				+ "COALESCE(A.PAODEGR2,0) as PAODEGR2, COALESCE(A.PAODEGRE,0) as PAODEGRE, PAORANK, PAORANK2, PAORANKC, COALESCE(A.PAOPFDOC,0) as PAOPFDOC, PAOCSFLA, "
				+ "COALESCE(A.PAOPFSP1,0) as PAOPFSP1, COALESCE(A.PAOPFSP2,0) as PAOPFSP2, COALESCE(A.PAOPFSP3,0) as PAOPFSP3, COALESCE(A.PAYTAXNO,0) as PAYTAXNO,"
				+ "A.PAYUPDID, A.PAYUPDTM,PAYFLAG, TRANUM, UPMASTDT, PAYPSW, PAOEXPFKEY1, PAOEXPFKEY2, PAOEXPFKEY3, PAYEXPFKEY1, PAYEXPFKEY2, "
				+ "PAYEXPFKEY3, COALESCE(A.PAORANK4,0) as PAORANK4, COALESCE(A.PAYRANK4,0) as PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, INSUAMT, A.HID, "
				+ "B.PADEPNMC,B.PACSTNO,C.PAYTITLP,C.PATITSUB,D.RANKNAME,E.RNK4NAME RNK4NM,C1.PAYTITLP PAYTITLO,C1.PATITSUB PAOTITSUB,D1.RANKNAME ORANKNM,E1.RNK4NAME ORNK4NM,F.TRANAME ";

		sql = "select " + strSelect + " from " + this.tTraPsn+ " A " 
				+ " left outer join " + this.tDept     + " B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID "
				+ " left outer join " + this.tTitle    + " C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID "
				+ " left outer join " + this.tRankCode + " D on A.PAYRANK=D.RANKCODE "  
				+ " left outer join " + this.tRnk4Tbl  + " E on A.PAYRANK4=E.RNK4KEY " 
				+ " left outer join " + this.tTitle    + " C1 on A.PAOTITLE=C1.PAYTITLK and A.HID=C1.HID " 
				+ " left outer join " + this.tRankCode + " D1 on A.PAORANK=D1.RANKCODE " 
				+ " left outer join " + this.tRnk4Tbl  + " E1 on A.PAORANK4=E1.RNK4KEY " 
				+ " left outer join " + this.tTraCode  + " F on A.PATRACOD=F.TRACODE "
				+ " where A.PATRACOD NOT IN (12,41,51) and A.HID=? ";
		params.add(hid);
		if (!"".equals(cardno)) {// 卡號
			subsql += " and PAYCARD=? ";
			params.add(cardno);			
		} else if (!"".equals(idtno)) {// 身分證
			subsql += " and PAYIDNO=? ";
			params.add(idtno);			
		} else if (!"".equals(namec)) {// 姓名
			subsql += " and PAYNAMEC like ? ";
			params.add(namec + "%");			
		}
		if (!"".equals(tradt) ) { // 異動日期
			subsql += " and PATRADT =? ";
			params.add(tradt);			
		}
		if (!"".equals(perf)) {// 1醫師 2員工 (V醫師職類 A兼職 CG契約主治醫師) 排除16住院醫師
			if (perf.equals("1")) {
				subsql += " AND ( ((c.PATITSUB in('V','A') OR c.PAYTITLK IN('CG')) and c.PAYTITLK not IN('16') ) "
			                 +"or ((c1.PATITSUB in('V','A') OR c1.PAYTITLK IN('CG')) and c1.PAYTITLK not IN('16')) )";
			}	
			else
				subsql += " AND not ( ((c.PATITSUB in('V','A') OR c.PAYTITLK IN('CG')) and c.PAYTITLK not IN('16') ) "
				                 +"or ((c1.PATITSUB in('V','A') OR c1.PAYTITLK IN('CG')) and c1.PAYTITLK not IN('16')) )";
		}
		if (!(tracod == null || (tracod != null && tracod.length == 0))) { // 異動原因
			subsql += " and PATRACOD in ( ";
			for (i = 0; i < tracod.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(tracod[i]);
			}
			subsql += ")";
		}
		sql += subsql + " order by PAYCARD,PAYRSGDT DESC with ur";
		log.info(me + "  " + sql + "...params:" + params.toString());
		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			starttime = System.currentTimeMillis();
			cDAO = new CommDAO(da, hid);
			// 找取得績效點數map
			CspfMap = cDAO.getCsPfByAll(da, hid);
			log.info(me + ",找取得績效點數map筆數:" + CspfMap.size() + "共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
			starttime = System.currentTimeMillis();
			log.info(me + "  " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					oneobj = new Patrapsn();
					PropertyUtils.copyProperties(oneobj, data);
					jsonOne = new JSONObject();
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));
					jsonOne.put("paynamec", StringUtil.nullToString(data.get("paynamec")));
					jsonOne.put("patracod", StringUtil.nullToString(data.get("patracod")));
					jsonOne.put("patradt", StringUtil.nullToString(data.get("patradt")));
					jsonOne.put("traname", StringUtil.nullToString(data.get("traname")));
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					jsonOne.put("patracod", StringUtil.nullToString(data.get("patracod")));
					jsonOne.put("salnum", StringUtil.nullToString(data.get("salnum")));
					jsonOne.put("paydept", StringUtil.nullToString(data.get("paydept")));
					jsonOne.put("padepnmc", StringUtil.nullToString(data.get("padepnmc")));
					jsonOne.put("paylvefg", StringUtil.nullToString(data.get("paylvefg")));
					jsonOne.put("payempno", StringUtil.nullToString(data.get("payempno")));
					jsonOne.put("paygovdt", StringUtil.nullToString(data.get("paygovdt")));
					jsonOne.put("payempdt", StringUtil.nullToString(data.get("payempdt")));
					jsonOne.put("paysgndt", StringUtil.nullToString(data.get("paysgndt")));
					jsonOne.put("payrsgdt", StringUtil.nullToString(data.get("payrsgdt")));
					jsonOne.put("paeffdt", StringUtil.nullToString(data.get("paeffdt")));
					jsonOne.put("pacstno", StringUtil.nullToString(data.get("pacstno")));
					
					// 新
					jsonOne.put("patitsub", StringUtil.nullToString(data.get("patitsub")));
					jsonOne.put("paytitle", StringUtil.nullToString(data.get("paytitle")));
					jsonOne.put("paytitlp", StringUtil.nullToString(data.get("paytitlp")));
					jsonOne.put("payrnk2n", StringUtil.nullToString(data.get("payrnk2n")));
					jsonOne.put("payrank3", StringUtil.nullToString(data.get("payrank3")));
					jsonOne.put("rank3nm", PaCommUtil.getRank3Nm((Integer) data.get("payrank3")));
					jsonOne.put("paydegr2", StringUtil.nullToString(data.get("paydegr2")));
					jsonOne.put("paydegre", StringUtil.nullToString(data.get("paydegre")));
					jsonOne.put("payrank", StringUtil.nullToString(data.get("payrank")));
					jsonOne.put("ranknm", StringUtil.nullToString(data.get("rankname")));
					jsonOne.put("payrank2", StringUtil.nullToString(data.get("payrank2")));
					jsonOne.put("payrankc", StringUtil.nullToString(data.get("payrankc")));
					jsonOne.put("rnk4nm", StringUtil.nullToString(data.get("rnk4name")));
					jsonOne.put("pacsflag", StringUtil.nullToString(data.get("pacsflag")));
					jsonOne.put("paypfsp1", StringUtil.nullToString(data.get("paypfsp1")));
					jsonOne.put("paypfsp2", StringUtil.nullToString(data.get("paypfsp2")));
					jsonOne.put("paypfsp3", StringUtil.nullToString(data.get("paypfsp3")));
					jsonOne.put("payexpfkey1", StringUtil.nullToString(data.get("payexpfkey1")));
					jsonOne.put("payexpfkey2", StringUtil.nullToString(data.get("payexpfkey2")));
					jsonOne.put("payexpfkey3", StringUtil.nullToString(data.get("payexpfkey3")));
					jsonOne.put("paypfdoc", StringUtil.nullToString(data.get("paypfdoc")));
					jsonOne.put("contract", oneobj.getContract());
					// 工作獎金=Y,找基本績分 俸級績分
					pfbas = 0;
					pfdat = 0;
					pfkey = "";
					if ("Y".equals(oneobj.getPacsflag()) & hid.matches("1A0|2A0")) {
						cspfRtn = cDAO.getCsPfByMap(oneobj.getPayrank3(), oneobj.getPaytitle(),
								oneobj.getPayrank(), oneobj.getPayrnk2n(), oneobj.getPaydegr2(),
								"9999-12-31", hid, CspfMap, da);
						if (cspfRtn != null) {
							pfbas = (Integer) cspfRtn.get("pfbas");
							pfdat = (Integer) cspfRtn.get("pfdat");
							pfkey = (String) cspfRtn.get("pfkey");
						}
					}
					jsonOne.put("pfkey", pfkey);
					jsonOne.put("pfbas", pfbas);
					jsonOne.put("pfdat", pfdat);
					// 原 
					jsonOne.put("paotitsub", StringUtil.nullToString(data.get("paotitsub")));
					jsonOne.put("paotitle", StringUtil.nullToString(data.get("paotitle")));
					jsonOne.put("paytitlo", StringUtil.nullToString(data.get("paytitlo")));
					jsonOne.put("paornk2n", StringUtil.nullToString(data.get("paornk2n")));
					jsonOne.put("paorank3", StringUtil.nullToString(data.get("paorank3")));
					jsonOne.put("orank3nm", PaCommUtil.getRank3Nm((Integer) data.get("paorank3")));
					jsonOne.put("paorank3", StringUtil.nullToString(data.get("paorank3")));
					jsonOne.put("paodegr2", StringUtil.nullToString(data.get("paodegr2")));
					jsonOne.put("paodegre", StringUtil.nullToString(data.get("paodegre")));
					jsonOne.put("paorank", StringUtil.nullToString(data.get("paorank")));
					jsonOne.put("oranknm", StringUtil.nullToString(data.get("oranknm")));
					jsonOne.put("paorank2", StringUtil.nullToString(data.get("paorank2")));
					jsonOne.put("paorankc", StringUtil.nullToString(data.get("paorankc")));
					jsonOne.put("ornk4nm", StringUtil.nullToString(data.get("ornk4nm")));
					jsonOne.put("paocsfla", StringUtil.nullToString(data.get("paocsfla")));
					jsonOne.put("paopfsp1", StringUtil.nullToString(data.get("paopfsp1")));
					jsonOne.put("paopfsp2", StringUtil.nullToString(data.get("paopfsp2")));
					jsonOne.put("paopfsp3", StringUtil.nullToString(data.get("paopfsp3")));
					jsonOne.put("paoexpfkey1", StringUtil.nullToString(data.get("paoexpfkey1")));
					jsonOne.put("paoexpfkey2", StringUtil.nullToString(data.get("paoexpfkey2")));
					jsonOne.put("paoexpfkey3", StringUtil.nullToString(data.get("paoexpfkey3")));
					jsonOne.put("paopfdoc", StringUtil.nullToString(data.get("paopfdoc")));
					jsonOne.put("contraco", oneobj.getContraco());
					// 工作獎金=Y,找基本績分 俸級績分
					pfbas = 0;
					pfdat = 0;
					pfkey = "";
					if ("Y".equals(oneobj.getPaocsfla()) & hid.matches("1A0|2A0")) {
						cspfRtn = cDAO.getCsPfByMap(oneobj.getPaorank3(), StringUtil.nullToString(data.get("paotitle")),
								StringUtil.nullToString(data.get("paorank")),
								StringUtil.nullToString(data.get("paornk2n")), (Integer) data.get("paodegr2"),
								StringUtil.nullToString(data.get("patradt")), hid, CspfMap, da);
						if (cspfRtn != null) {
							pfbas = (Integer) cspfRtn.get("pfbas");
							pfdat = (Integer) cspfRtn.get("pfdat");
							pfkey = (String) cspfRtn.get("pfkey");
						}
					}
					jsonOne.put("pobas", pfbas);
					jsonOne.put("podat", pfdat);
					jsonOne.put("pokey", pfkey);
					
					jsonRtn.add(jsonOne);					
				}
			}
			
			log.info(me + "  取得績效_異動_查詢集合 ...筆數:"+jsonRtn.size()+"********* dataaccess:共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}
		
		return jsonRtn;
	}
	// 取得績效組_基本檔_查詢集合
	public JSONArray getBousBasList(String in_data, String userid, String hid, VGHDataAccess da)
			throws Exception {
		String me = getClass().getName() + ".getBousBasList(), userid:" + userid;
		log.info(me + " in_data = " + in_data + " hid = " + hid + " userid = " + userid);
		CommDAO cDAO = null;
		JSONArray jsonRtn = new JSONArray();
		ArrayList<Object> params = new ArrayList<Object>();
		JSONObject jsonOne = null;
		JSONObject object_in_data = null;
		HashMap CspfMap = new HashMap();
		HashMap cspfRtn = new HashMap();
		Pabaspsn oneobj = null;
		long starttime = 0;
		String lvefg = "";	//Y離職
		String cardno = "";
		String idtno = "";
		String namec = "";
		String[] expfkey1 = null;//額外代碼
		String[] paydept = null;
		String csflag = "";
		String expfsp = "";	//額外點數
		String perf = "";	//1:醫師,2:員工
		String strSelect = "";
		String subsql = "";
		
		int i = 0;
		int pfbas = 0;
		int pfdat = 0;
		String pfkey = "";
		
		if (in_data != null && !in_data.equals("")) {
			object_in_data = JSONObject.fromObject(in_data);
			lvefg = (!object_in_data.has("lvefg")) ? "" : object_in_data.getString("lvefg");
			cardno = (!object_in_data.has("cardno")) ? "" : object_in_data.getString("cardno");
			idtno = (!object_in_data.has("idtno")) ? "" : object_in_data.getString("idtno");
			namec = (!object_in_data.has("namec")) ? "" : object_in_data.getString("namec");
			expfkey1 = (!object_in_data.has("expfkey1")) ? null	: ("".equals(object_in_data.getString("expfkey1")) ? null : object_in_data.getString("expfkey1").split(","));
			csflag = (!object_in_data.has("csflag")) ? "" : object_in_data.getString("csflag");
			expfsp = (!object_in_data.has("expfsp")) ? "" : object_in_data.getString("expfsp");
			perf = (!object_in_data.has("perf")) ? "" : object_in_data.getString("perf");
			paydept = (!object_in_data.has("paydept")) ? null : ("".equals(object_in_data.getString("paydept")) ? null: object_in_data.getString("paydept").split(","));			
		}
		// 讀取pa.pabaspsn
		strSelect = "PAYLVEFG, A.PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, PAYEMPDT, PAYSGNDT, PAYRSGDT, PAYBKID, PAYACNO, "
				+ "PAYGEDNO, COALESCE(A.PAYINFLG,0) as PAYINFLG, COALESCE(A.PAYDPNO,'0') as PAYDPNO, PAYPSW, PAYTITLE, PAYRNK2N, COALESCE(A.PAYRANK3,0) as PAYRANK3, COALESCE(A.PAYDEGR2,0) as PAYDEGR2, "
				+ "COALESCE(A.PAYDEGRE,0) as PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, COALESCE(A.PAYPFDOC,0) as PAYPFDOC, PACSFLAG, "
				+ "COALESCE(A.PAYPFSP1,0) as PAYPFSP1, COALESCE(A.PAYPFSP2,0) as PAYPFSP2, COALESCE(A.PAYPFSP3,0) as PAYPFSP3, A.PATRACOD, "
				+ "PATRADT, COALESCE(A.PAYTAXNO,0) as PAYTAXNO, A.PAYUPDID, A.PAYUPDTM, PAYEXPFKEY1, PAYEXPFKEY2, PAYEXPFKEY3, COALESCE(A.PAYRANK4,0) as PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, "
				+ "INSUAMT, PARETENTION, A.HID, " + tmpZbaspsn + ","
				+ "B.PADEPNMC,B.PACSTNO,C.PAYTITLP,C.PATITSUB,D.RANKNAME,E.RNK4NAME,F.TRANAME ";
		sql = "select " + strSelect + " from " + this.tBasPsn + " A " 
			 + " left outer join " + this.tDept+ " B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID " 
			 + " left outer join " + this.tTitle+ " C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID "
			 + " left outer join " + this.tRankCode+ " D on A.PAYRANK=D.RANKCODE "
			 + " left outer join " + this.tRnk4Tbl + " E on A.PAYRANK4=E.RNK4KEY "
			 + " left outer join " + this.tTraCode+ " F on A.PATRACOD=F.TRACODE and A.HID=C.HID "
			 +" where A.HID=? ";
		params.add(hid);
		if (!"".equals(cardno)) {// 卡號
			subsql += " and PAYCARD=? ";
			params.add(cardno);
		} else if (!"".equals(idtno)) {// 身分證
			subsql += " and PAYIDNO=? ";
			params.add(idtno);

		} else if (!"".equals(namec)) {// 姓名
			subsql += " and PAYNAMEC like ? ";
			params.add(namec + "%");
		}
		if (!"".equals(lvefg)) {// 離職否(Y/N)
			subsql += " and PAYLVEFG=? ";
			params.add(lvefg);
		}
		if (!"".equals(csflag)) {// 支領工作獎金(Y/N)
			subsql += " and PACSFLAG=? ";
			params.add(csflag);
		}
		if (!"".equals(perf)) {// 1醫師 2員工 (V醫師職類 A兼職 CG契約主治醫師),排除16住院醫師 
			if (perf.equals("1"))
				subsql += " AND ((c.PATITSUB in('V','A') OR c.PAYTITLK IN('CG')) and c.PAYTITLK not IN('16')) ";
			else
				subsql += " AND not ((c.PATITSUB in('V','A') OR c.PAYTITLK IN('CG')) and c.PAYTITLK not IN('16')) ";
		}
		if ("Y".equals(expfsp)) {// 額外點數1 2 3有值皆要列入
			subsql += " and (PAYPFSP1<>0 or PAYPFSP2<>0 or PAYPFSP3<>0) ";
		}else if ("N".equals(expfsp)) {
			subsql += " and (PAYPFSP1=0 and PAYPFSP2=0 and PAYPFSP3=0) ";
		}
		if (!(paydept == null || (paydept != null && paydept.length == 0))) { // 單位
			subsql += " and paydept in ( ";
			for (i = 0; i < paydept.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(paydept[i]);
			}
			subsql += ")";
		}
		if ( !(expfkey1 == null || (expfkey1 != null && expfkey1.length == 0) || (expfkey1.length == 1 && expfkey1[0].equals("0"))) ) { // 額外點數1
			subsql += " and ( payexpfkey1 in ( ";
			for (i = 0; i < expfkey1.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(expfkey1[i]);
			}
			subsql += ") or payexpfkey2 in ( ";
			for (i = 0; i < expfkey1.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(expfkey1[i]);
			}
			subsql += ") or payexpfkey3 in ( ";
			for (i = 0; i < expfkey1.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(expfkey1[i]);
			}
			subsql += ") )";
		}
		sql += subsql + " order by PAYCARD,PAYRSGDT DESC with ur";
		log.info(me + "  " + sql + "...params:" + params.toString());

		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			starttime = System.currentTimeMillis();
			cDAO = new CommDAO(da, hid);
			// 找取得績效點數map
			CspfMap = cDAO.getCsPfByAll(da, hid);
			log.info(me + ",找取得績效點數map筆數:" + CspfMap.size() + "共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
			starttime = System.currentTimeMillis();
			log.info(me + "  " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					oneobj = new Pabaspsn();
					PropertyUtils.copyProperties(oneobj, data);

					jsonOne = new JSONObject();

					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));
					jsonOne.put("paynamec", StringUtil.nullToString(data.get("paynamec")));
					jsonOne.put("pacstno", StringUtil.nullToString(data.get("pacstno")));
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					jsonOne.put("paydept", StringUtil.nullToString(data.get("paydept")));
					jsonOne.put("padepnmc", StringUtil.nullToString(data.get("padepnmc")));
					jsonOne.put("paylvefg", StringUtil.nullToString(data.get("paylvefg")));
					jsonOne.put("payempno", StringUtil.nullToString(data.get("payempno")));
					jsonOne.put("paygovdt", StringUtil.nullToString(data.get("paygovdt")));
					jsonOne.put("payempdt", StringUtil.nullToString(data.get("payempdt")));
					jsonOne.put("paysgndt", StringUtil.nullToString(data.get("paysgndt")));
					jsonOne.put("payrsgdt", StringUtil.nullToString(data.get("payrsgdt")));
					jsonOne.put("patradt", StringUtil.nullToString(data.get("patradt")));
					jsonOne.put("traname", StringUtil.nullToString(data.get("traname")));
					jsonOne.put("patitsub", StringUtil.nullToString(data.get("patitsub")));
					jsonOne.put("paytitle", StringUtil.nullToString(data.get("paytitle")));
					jsonOne.put("payrnk2n", StringUtil.nullToString(data.get("payrnk2n")));
					jsonOne.put("payrank3", StringUtil.nullToString(data.get("payrank3")));
					jsonOne.put("paydegr2", StringUtil.nullToString(data.get("paydegr2")));
					jsonOne.put("paydegre", StringUtil.nullToString(data.get("paydegre")));
					jsonOne.put("payrank", StringUtil.nullToString(data.get("payrank")));
					jsonOne.put("ranknm", StringUtil.nullToString(data.get("rankname")));
					jsonOne.put("payrank2", StringUtil.nullToString(data.get("payrank2")));
					jsonOne.put("payrankc", StringUtil.nullToString(data.get("payrankc")));
					jsonOne.put("paytitlp", StringUtil.nullToString(data.get("paytitlp")));
					jsonOne.put("rnk4nm", StringUtil.nullToString(data.get("rnk4name")));
					jsonOne.put("rank3nm", PaCommUtil.getRank3Nm((Integer) data.get("payrank3")));
					jsonOne.put("payinflg", PaCommUtil.getInflgNm((Integer) data.get("payinflg")));
					jsonOne.put("paydpno", StringUtil.nullToString(data.get("paydpno")));
					jsonOne.put("paytaxno", StringUtil.nullToString(data.get("paytaxno")));
					jsonOne.put("insuamt", StringUtil.nullToString(data.get("insuamt")));
					jsonOne.put("contract", oneobj.getContract());

					jsonOne.put("pacsflag", StringUtil.nullToString(data.get("pacsflag")));
					jsonOne.put("payexpfkey1", StringUtil.nullToString(data.get("payexpfkey1")));
					jsonOne.put("payexpfkey2", StringUtil.nullToString(data.get("payexpfkey2")));
					jsonOne.put("payexpfkey3", StringUtil.nullToString(data.get("payexpfkey3")));
					jsonOne.put("paypfsp1", StringUtil.nullToString(data.get("paypfsp1")));
					jsonOne.put("paypfsp2", StringUtil.nullToString(data.get("paypfsp2")));
					jsonOne.put("paypfsp3", StringUtil.nullToString(data.get("paypfsp3")));
					jsonOne.put("paretention", StringUtil.nullToString(data.get("paretention")));
					jsonOne.put("pareNm",PaCommUtil.getRetenNm((String) StringUtil.nullToString(data.get("paretention")))) ;
					jsonOne.put("paypfdoc", StringUtil.nullToString(data.get("paypfdoc")));
					// 工作獎金=Y,找基本績分 俸級績分
					pfbas = 0;
					pfdat = 0;
					pfkey = "";
					if ("Y".equals(oneobj.getPacsflag()) & hid.matches("1A0|2A0")) {

						cspfRtn = cDAO.getCsPfByMap(oneobj.getPayrank3(), oneobj.getPaytitle(),
								oneobj.getPayrank(), oneobj.getPayrnk2n(), oneobj.getPaydegr2(),
								"9999-12-31", hid, CspfMap, da);
						if (cspfRtn != null) {
							pfbas = (Integer) cspfRtn.get("pfbas");
							pfdat = (Integer) cspfRtn.get("pfdat");
							pfkey = (String) cspfRtn.get("pfkey");
						}
					}
					jsonOne.put("pfkey", pfkey);
					jsonOne.put("pfbas", pfbas);
					jsonOne.put("pfdat", pfdat);
					jsonRtn.add(jsonOne);
					
				}
			}
			
			log.info(me + "  取得基本檔集合 ...筆數:"+jsonRtn.size()+"********* dataaccess:共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}

		return jsonRtn;
	}
	
	/**
	 * 取得map
	 * 
	 * @param hid:院別
	 *            selCode:1基本檔 lparam:參數
	 * @return map
	 * @throws SQLException
	 */
	private HashMap<String, Object> getPaItmMap(ArrayList<Object> lparam, String tblNm, String hid, VGHDataAccess da)
			throws SQLException {
		String me = ".getPaItmMap()";
		long starttime = 0;
		Padatpay datpayobj = null;
		Padatpsn datpsnobj = null;
		ArrayList<Object> bpayAry = null;
		ArrayList<Object> tpayAry = null;
		ArrayList<Object> dpayAry = null;
		String strKey = "";
		String preKey = "";
		String strKeyA = "";
		int cnt = 0;
		HashMap<String, Object> rtnMap = new HashMap<String, Object>();
		String tmp = "";
		// tmp=" and a.paycard in ('P037')"; //測試用
		// 教職及論文獎勵 key=hid+paybank
		String sqlDatdoc_inq = "SELECT a.payidno,'VP01' PAYCODE,SUM(PAYAMT) AS PAYAMT " + "FROM " + this.tDatPsn + " a "
				+ "LEFT OUTER JOIN " + this.tDatPay + " b ON a.hid=b.hid AND a.payidno=b.payidno AND a.salnum=b.salnum "
				+ "where a.hid=? and a.salnum=? " + tmp
				+ " AND SUBSTR(PAYCODE,1,4)='VP01' AND PAYCODE NOT IN ('VP013','VP010')" + " GROUP BY a.payidno "
				+ " UNION ALL " + "SELECT a.payidno,'FP80' PAYCODE,SUM(PAYAMT) AS PAYAMT " + "FROM " + this.tDatPsn
				+ " a " + "LEFT OUTER JOIN " + this.tDatPay
				+ " b ON a.hid=b.hid AND a.payidno=b.payidno AND a.salnum=b.salnum " + "where a.hid=? and a.salnum=? "
				+ tmp + " AND SUBSTR(PAYCODE,1,4)='FP80' " + " GROUP BY a.payidno " + " UNION ALL "
				+ " SELECT a.payidno, PAYCODE,SUM(PAYAMT) AS PAYAMT " + " FROM " + this.tDatPsn + " a "
				+ " LEFT OUTER JOIN " + this.tDatPay
				+ " b ON a.hid=b.hid AND a.payidno=b.payidno AND a.salnum=b.salnum " + " where a.hid=? and a.salnum=? "
				+ tmp + " AND PAYCODE IN('VP049','FP440') " + " GROUP BY a.payidno,PAYCODE " + " UNION ALL "
				+ " SELECT payidno,PAYITEM PAYCODE,SUM(PAYAMT) AS PAYAMT " + " FROM " + this.tTeaChaw + " a "
				+ " where a.hid=? " + tmp
				+ " AND ? BETWEEN PABDATE AND PAVDATE GROUP BY payidno,PAYITEM ORDER BY payidno,paycode with ur ";
		// 找出醫師.職務調整,2個月前發薪明細的職等
		String sqlDatdoc_Adj = "SELECT SALNUM, PAYBANK, SALNUM, PAYBANK, PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYTITLE, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, PAYPFDOC, PACSFLAG, PAYPFSP1, PAYPFSP2, PAYPFSP3, PAYUPDID, PAYUPDTM, PAYRANK4, HID, "+tmpZdatpsn+", INSUAMT, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYTITLE, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, PAYPFDOC, PACSFLAG, PAYPFSP1, PAYPFSP2, PAYPFSP3, PAYUPDID, PAYUPDTM, PAYRANK4, HID, INSUAMT"
				+ " FROM " + this.tDatPsn + " WHERE hid=? AND payidno IN (SELECT payidno FROM " + this.tTraPsn + " a "
				+ " LEFT OUTER JOIN " + this.tTitle + " b ON a.hid=b.hid and a.PAYTITLE=b.PAYTITLK "
				+ " WHERE patradt BETWEEN ? AND ? AND patracod in (37) AND b.PATITSUB IN ('V') )"
				+ " and paybank=? with ur";
		try {
			starttime = System.currentTimeMillis();
			Hospitals hospInfo = Hospitals.getOneHosp(hid);
			if (da == null) {
				// 改用列舉
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}

			if (tblNm.equals("DATDOC"))
				sql = sqlDatdoc_inq;
			else if (tblNm.equals("DATDOCAdj"))
				sql = sqlDatdoc_Adj;
			
			log.info(me + "  " + sql + "...params:" + lparam.toString());

			result = da.arraylistbyQuery(sql, lparam, META_TYPE);
			if (result.size() > 0) {
				bpayAry = new ArrayList<Object>();
				tpayAry = new ArrayList<Object>();
				dpayAry = new ArrayList<Object>();
				for (Map<String, Object> data : result) {
					if (tblNm.matches("DATDOC")) {
						datpayobj = new Padatpay();
						PropertyUtils.copyProperties(datpayobj, data);
						strKey = StringUtil.nullToString(datpayobj.getPayidno());
						if ((!"".equals(preKey) && !strKey.equals(preKey))) {
							rtnMap.put(preKey, dpayAry);
							dpayAry = new ArrayList<Object>();
						}
						dpayAry.add(datpayobj);
						preKey = strKey;
					} else if (tblNm.equals("DATDOCAdj")) {
						datpsnobj = new Padatpsn();
						PropertyUtils.copyProperties(datpsnobj, data);
						strKey = StringUtil.nullToString(datpsnobj.getPayidno());
						rtnMap.put(strKey, datpsnobj);
					} 
				}
			}
			if (tblNm.matches("DATDOC")) {
				rtnMap.put(preKey, dpayAry);
			}
			log.info(me + "  ...*********" + tblNm + " size:" + result.size() + " ...共花費："
					+ (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Throwable e) {
			log.info(me + " fail, " + e);
			throw new SQLException("取得 map :" + e.toString());
		} finally {
			if (!connFlg)
				closeAll(da, null, connFlg);
		}
		return rtnMap;
	}
	
	// 檔案產製 selCode:selNm:selEn
	// 1:績效組_醫師:Perf_1 2:績效組_員工:Perf_2
	public JSONArray getPerfFileList(String in_data, String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getPerfFileList(),userid = " + userid;
		log.info(me + " in_data = " + in_data + " hid = " + hid);
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_in_data = null;
		CommDAO cDAO = null;
		HashMap CspfMap = new HashMap(); // 工作獎金支扣 對照表
		long starttime = 0;
		String selEn = "";
		String selCode = "";
		String selNm = "";
		String patradt = "";
		int salnum = 0;
		int i = 0;
		String jsonStr = "";
		String l_Idtno = ""; // 身分證
		String l_card = ""; // 卡號
		String l_paycode = ""; // 支扣代碼

		int l_Pfdoc = 0; // 專勤額度
		String l_Rnk2n = "";// 職稱
		String l_Title = "";// 職稱
		String l_TitSub = "";// 職稱分類
		int l_DEGR2 = 0; // 級別
		int l_Open = 0; // 不開業獎金 專二/工二
		String l_Csflag = "";// 工作獎金(Y/N)
		int l_Tracod = 0;

		boolean blnflag = false;
		String l_Paretention = "";// [專一]0,[專二]Y/N 2026.10.19專1=Y,專2=Y 2:專1=Y,專2=N 3:專1=N,專2=Y 4:專1=N,專2=N 
		HashMap datpsnAdjMap = new HashMap();
		Padatpsn padatpsn = null;
		Padatpsn padatpsnadj = null;
		Padatpay padatpay = null;
		int l_VP01 = 0; // 專一
		int l_FP80 = 0; // 工作獎金_住宿津貼
		int l_VP049 = 0; // 專二_補
		int l_FP440 = 0; // 專一_培訓補助
		int l_teach_00 = 0; // 教職獎勵
		int l_teach_09 = 0; // 教職獎勵_補
		int l_thesis_10 = 0; // 論文獎勵第1篇
		int l_thesis_19 = 0; // 論文獎勵第1篇_補
		int l_thesis_20 = 0; // 論文獎勵第2篇
		int l_thesis_29 = 0; // 論文獎勵第2篇_補
		String l_pfcr = ""; // 總醫師
		String l_pfbas = ""; // 基本績分
		String l_pfdat = ""; // 俸級績分
		String l_Paysgndt = ""; // 現職日
		String l_Paybank = ""; // 發薪日
		String vDate = "";
		long starttime9 = 0;
		HashMap datdocMap = new HashMap(); // 月支支扣檔 map
		TreeMap<Integer, Integer> valMap = new TreeMap();
		ArrayList<Padatpay> datpayAry = null;// 月支支扣檔 Array
		ArrayList<Object> param = new ArrayList<Object>();
		ResultSet rs = null;
		BufferedWriter bfw = null;
		String strLine = "";
		String strTmp = "";
		String tmp = "";
		String folderPath = "";
		String filename = "";
		String filePath = "";
		starttime = System.currentTimeMillis();
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> paramsub = new ArrayList<Object>();
		if (in_data != null && !in_data.equals("")) {
			object_in_data = JSONObject.fromObject(in_data);
			selEn = (!object_in_data.has("selEn")) ? "" : object_in_data.getString("selEn");
			selCode = (!object_in_data.has("selCode")) ? "" : object_in_data.getString("selCode");
			selNm = (!object_in_data.has("selNm")) ? "" : object_in_data.getString("selNm");
			patradt = (!object_in_data.has("patradt")) ? "" : object_in_data.getString("patradt");
			salnum = (!object_in_data.has("salnum")) ? 0 : object_in_data.getInt("salnum");			
		}

		String sqlDatpsn_inq = "";

		try {
			cDAO = new CommDAO(da, hid);
			Hospitals hospInfo = Hospitals.getOneHosp(hid);
			if (da == null) {
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			starttime9 = System.currentTimeMillis();
			// 寫入資料夾設定
			FileUtil fileUtil = new FileUtil();
			if (selEn.matches("Perf_1")) { // Perf_1績效組_醫師
				folderPath = hospInfo.fileSALPPF;
				//folderPath = hospInfo.fileSRV+"bak\\";
				filename = "DocBas";
				if (hid.matches("2A0")) { // 屏榮,檔名加HID 聯醫沒有用
					filename += hid;
				}
			} else if (selEn.matches("Perf_2")) { // Perf_2績效組_員工
				folderPath = hospInfo.fileSALEPF;
				//folderPath = hospInfo.fileSRV+"bak\\";
				filename = "EmpEff";
				if (hid.matches("2A0")) { // 屏榮,檔名加HID 聯醫沒有用
					filename += hid;
				}
			}
			filename += ".txt";
			filePath = folderPath + filename;

			if (!FileUtil.FileToSmbFile(folderPath, hid).exists()) {
				fileUtil.FileToSmbFile(folderPath, hid).mkdirs();
			}
			// 建立檔案 難字要用utf8,因接收端無法用utf8,改為big5,難字轉換成□
			SmbFile payfile = fileUtil.FileToSmbFile(filePath, hid);
			//bfw = new BufferedWriter(new OutputStreamWriter(new SmbFileOutputStream(payfile, false), "utf-8"));
			bfw = new BufferedWriter(new OutputStreamWriter(new SmbFileOutputStream(payfile, false), "big5"));
			
			starttime9 = System.currentTimeMillis();
			param = new ArrayList<Object>();
			param.add(hid);
			param.add(salnum);
			param.add(hid);
			param.add(salnum);
			param.add(hid);
			param.add(salnum);
			param.add(hid);
			param.add(patradt);
			datdocMap = this.getPaItmMap(param, "DATDOC", hid, da);
			log.info(me + "  1-1.讀取月支明細VP01專一補發項+VP04專二補發項+FP80工作獎金_住宿津貼+FP440+讀取教職及論文獎勵,筆數:" + datdocMap.size()
					+ "共花費：" + (System.currentTimeMillis() - starttime9) + "毫秒");
			if (selEn.matches("Perf_1")) {
				starttime9 = System.currentTimeMillis();
				param = new ArrayList<Object>();
				param.add(hid);
				java.sql.Date dd = java.sql.Date.valueOf(patradt);
				vDate = formatter.format(DateUtil.add(dd, Calendar.MONTH, -1));
				param.add(vDate);
				param.add(patradt);
				vDate = formatter.format(DateUtil.add(dd, Calendar.MONTH, -2));
				param.add(vDate);
				datpsnAdjMap = this.getPaItmMap(param, "DATDOCAdj", hid, da);
				log.info(me + "  1-2.讀取醫師有職務異動,筆數:" + datdocMap.size() + "共花費："
						+ (System.currentTimeMillis() - starttime9) + "毫秒");

				// 績效組_醫師
				// 1.專勤額度>0
				// 2.專勤額度=0 and 專勤獎金細項>0,不含(VP010專勤獎金一 VP013病歷罰扣)
				// 3.專1=0,專2=Y(PARETENTION)
				// 4.paeffdt 離職2個月內的醫師
				// 2026.10.19 5.原專二PARETENTION,若專勤額度=0,要抓Y,發專二,改為 空白:專1=Y,專2=Y 2:專1=Y,專2=N 3:專1=N,專2=Y 4:專1=N,專2=N,待穩定,再修sql
				
				strTmp = " (DAT.PAYPFDOC >0 "
						+ " or  (DAT.PAYPFDOC = 0 AND DAT.PACSFLAG = 'N' AND DAT.PAYPFSP1+DAT.PAYPFSP2+DAT.PAYPFSP3 = 0 "
						+ " AND (DAT.PAYIDNO IN (SELECT PAYIDNO FROM " + this.tDatPay + " WHERE HID=? AND SALNUM = ? "
						+ " AND SUBSTR(PAYCODE,1,4)='VP01' AND PAYCODE NOT IN ('VP013','VP010'))) ) "
						+ " OR (BAS.PARETENTION IN ('Y','3'))) " + 
						" UNION "+
					   " select TRA.PATRADT PAYBANK, tra.PAYDEPT, D.PADEPNMC, tra.PAYCARD, tra.PAYNAMEC, bas.PAYTITLE, T.PAYTITLP,bas.PAYRNK2N, bas.PAYRANK3, bas.PAYDEGR2,bas.PAYDEGRE, "
						+ "COALESCE(tra.PAYEMPDT, BAS.PAYSGNDT) PAYEMPDT, tra.PAYIDNO, bas.PAYRANK2, bas.PAYRANK, bas.PAYPFDOC, bas.PACSFLAG,bas.PAYPFSP1+tra.PAYPFSP2+tra.PAYPFSP3 as PAYPFSP,"
						+ "PATITSUB, bas.PARETENTION,tra.PAYEMPNO,COALESCE(TRA.PAYRSGDT,BAS.PAYRSGDT) PAYRSGDT,COALESCE(TRA.PAYEMPDT,BAS.PAYEMPDT) PAYEMPDT, COALESCE(tra.PAYSGNDT,bas.PAYSGNDT) PAYSGNDT,"
						+ "COALESCE(tra.PATRACOD,0) patracod,case WHEN tra.patracod IN(21,22) then tra.payrsgdt WHEN tra.patracod IN (23) THEN tra.PAEFFDT else '9999-12-31' end payrsgdt,"
						+ "COALESCE(tra.PAYPFSP2,bas.PAYPFSP2)PAYPFSP2,COALESCE(tra.PAYEXPFKEY2,bas.PAYEXPFKEY2) PAYEXPFKEY2 "
						+ " from " + this.tTraPsn + " Tra " + 
						" left outer join " + this.tDept + " d on tra.PAYDEPT = D.PADEPCOD AND tra.HID=D.HID " + 
						" left outer join " + this.tTitle+ " t on tra.PAOTITLE = T.PAYTITLK and tra.hid=t.hid " + 
						" left outer join " + this.tBasPsn+ " bas on tra.PAYIDNO = Bas.PAYIDNO and tra.hid=Bas.hid "+
				       " where tra.HID=? and ((t.PATITSUB IN ('A','V') OR TRA.PAOTITLE IN ('CG')) and TRA.PAOTITLE NOT IN ('16'))"+
						  "AND tra.patracod IN (21,22,23) " + 
				         " AND TRA.PaeffDT BETWEEN DATE('" + patradt+ "') - 2 MONTH + 1 DAY AND DATE('" + patradt + "') + 1 MONTH - 1 DAY "+
						 " AND tra.payidno NOT IN (SELECT payidno FROM " + this.tDatPsn + " WHERE hid=? AND salnum=?) ";

			} else if (selEn.matches("Perf_2")) { // Perf_2績效組_員工 1.員工獎金=Y,2.員工獎金=N and 其他點數>0
				strTmp = " (DAT.PACSFLAG = 'Y' "
						+ "OR (DAT.PACSFLAG = 'N' AND DAT.PAYPFSP1+DAT.PAYPFSP2+DAT.PAYPFSP3>0) ) ";
			}

			// 讀取pa.padatpsn,異動檔抓1新進33職務調整 21離職22退休23停薪

			//tmp=" and dat.paycard in ('0133','0168') ";
			sql = "select DAT.PAYBANK, DAT.PAYDEPT, D.PADEPNMC, DAT.PAYCARD, DAT.PAYNAMEC, DAT.PAYTITLE, T.PAYTITLP,DAT.PAYRNK2N, DAT.PAYRANK3, DAT.PAYDEGR2, DAT.PAYDEGRE, "
					+ "COALESCE(tra.PAYEMPDT, BAS.PAYSGNDT) PAYEMPDT, DAT.PAYIDNO, DAT.PAYRANK2, DAT.PAYRANK, DAT.PAYPFDOC, DAT.PACSFLAG, DAT.PAYPFSP1+DAT.PAYPFSP2+DAT.PAYPFSP3 as PAYPFSP,"
					+ "PATITSUB, bas.PARETENTION,DAT.PAYEMPNO,COALESCE(TRA.PAYRSGDT,BAS.PAYRSGDT) PAYRSGDT,COALESCE(TRA.PAYEMPDT,BAS.PAYEMPDT) PAYEMPDT, COALESCE(tra.PAYSGNDT,bas.PAYSGNDT) PAYSGNDT,"
					+ "COALESCE(tra.PATRACOD,0) patracod,case WHEN tra.patracod IN(21,22) then tra.payrsgdt WHEN tra.patracod IN (23) THEN tra.PAEFFDT else '9999-12-31' end payrsgdt,"
					+ "COALESCE(tra.PAYPFSP2,bas.PAYPFSP2)PAYPFSP2,COALESCE(tra.PAYEXPFKEY2,bas.PAYEXPFKEY2) PAYEXPFKEY2 "
					+ " from " + this.tDatPsn + " DAT " + 
					  " left outer join " + this.tDept + " d on DAT.PAYDEPT = D.PADEPCOD AND DAT.HID=D.HID " + 
					  " left outer join " + this.tTitle+ " t on DAT.PAYTITLE = T.PAYTITLK and DAT.hid=t.hid " + 
					  " left outer join " + this.tBasPsn+ " bas on DAT.PAYIDNO = Bas.PAYIDNO and DAT.hid=Bas.hid " + 
					  " left outer join " + this.tTraPsn+ " tra on DAT.PAYIDNO = tra.PAYIDNO and DAT.hid=tra.hid and DAT.salnum=tra.salnum and tra.patracod in (1,33,21,22,23,31,36,37,38)  "
				   + " where dat.HID=? and dat.SALNUM =? " + tmp + " AND " + strTmp + " order by 4 with ur";

			params.add(hid);
			params.add(salnum);
			if (selEn.matches("Perf_1")) {
				params.add(hid);
				params.add(salnum);
				params.add(hid);
				params.add(hid);
				params.add(salnum);
			}
			log.info(me + "  2-2.讀取pa.padatpsn:" + sql + "...params:" + params.toString());
			log.info(me + "  " + sql + "...params:" + params.toString());

			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());
			// 員工
			// 發薪日;單位代碼;單位中文;卡號;姓名;職稱代碼;職稱中文;官職等;本功;級別;現職日;基本績分;俸級績分;額外績分;工二;獎勵_教職獎勵FP420;教職獎勵補發扣;獎勵_論文獎勵第1篇;論文獎勵第1篇補發扣;獎勵_論文獎勵第2篇;論文獎勵第2篇補發扣;工作_住宿津貼FP800;獎金差額;總醫師
			// DAT.PAYBANK, DAT.PAYDEPT, D.PADEPNMC, DAT.PAYCARD, DAT.PAYNAMEC,
			// DAT.PAYTITLE, T.PAYTITLP,DAT.PAYRNK2N, DAT.PAYRANK3, DAT.PAYDEGR2,
			// DAT.PAYDEGRE, COALESCE(tra.PAYEMPDT, BAS.PAYSGNDT) PAYEMPDT, DAT.PAYIDNO,
			// DAT.PAYRANK2, DAT.PAYRANK, DAT.PAYPFDOC, DAT.PACSFLAG,
			// DAT.PAYPFSP1+DAT.PAYPFSP2+DAT.PAYPFSP3 as PAYPFSP,PATITSUB,
			// bas.PARETENTION,DAT.PAYEMPNO,COALESCE(TRA.PAYRSGDT,BAS.PAYRSGDT)
			// PAYRSGDT,COALESCE(TRA.PAYEMPDT,BAS.PAYEMPDT) PAYEMPDT,
			// COALESCE(tra.PAYSGNDT,bas.PAYSGNDT)
			// PAYSGNDT,tra.PATRACOD,tra.PAYMONNO,tra.PAYDAYNO
			// 醫師
			// '0發薪日1單位代碼 2單位中文 3卡號 4姓名 5職稱代碼 6職稱中文 7職等 8本功 9級別 10現職日期 11專勤醫師基本額度 12專2額度
			// '13新進補發 值班費 補發繳回'VP01' AND <>('VP013','VP010') 14專勤獎金2 VP049 15專勤獎金一培訓補助VP049
			// '16教職獎勵FP420 17 FP429 18論文-1 FP430-1 19 FP439-1 20論文-2 FP430-2 21 FP430-2
			// 22離職日
			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					padatpsn = new Padatpsn();
					datpayAry = null;
					blnflag = false;
					strLine = "";
					l_VP01 = 0;
					l_VP049 = 0;
					l_FP440 = 0;
					l_FP80 = 0;
					l_teach_00 = 0; // 教職獎勵
					l_teach_09 = 0; // 教職獎勵_補
					l_thesis_10 = 0; // 論文獎勵第1篇
					l_thesis_19 = 0; // 論文獎勵第1篇_補
					l_thesis_20 = 0; // 論文獎勵第2篇
					l_thesis_29 = 0; // 論文獎勵第2篇_補
					l_pfbas = "0"; // 基本績分
					l_pfdat = "0"; // 俸級績分
					l_Open = 0;
					l_pfcr = "    "; // 總醫師
					jsonOne = new JSONObject();
					PropertyUtils.copyProperties(padatpsn, data);
					l_card = StringUtil.nullToString(padatpsn.getPaycard());
					/*if (l_card.matches("4094|3909|4472|2407")) {
						System.out.print(l_card);
					}*/
					// [專一]0,[專二]Y/N=>2026.10.19專1=Y,專2=Y 2:專1=Y,專2=N 3:專1=N,專2=Y 4:專1=N,專2=N 
					l_Paretention = StringUtil.nullToString(padatpsn.getParetention());
					// 專勤額度
					if (l_Paretention.matches("3|4"))
						l_Pfdoc = 0;
					else
						l_Pfdoc = padatpsn.getPaypfdoc();
					
					l_Title = StringUtil.nullToString(padatpsn.getPaytitle());
					l_TitSub = StringUtil.nullToString(padatpsn.getPatitsub());
					l_Rnk2n = StringUtil.nullToString(padatpsn.getPayrnk2n());
					l_DEGR2 = padatpsn.getPaydegr2();
					l_Csflag = StringUtil.nullToString(padatpsn.getPacsflag());
					l_Idtno = StringUtil.nullToString(padatpsn.getPayidno());
					
					l_Tracod = padatpsn.getPatracod();
					l_Paysgndt = StringUtil.nullToString(padatpsn.getPaysgndt());
					l_Paybank = StringUtil.nullToString(padatpsn.getPaybank());
					// 不開業獎金(專二/工二) 6
					// 職務調整,找發薪前2個月的padatpsn
					padatpsnadj = (Padatpsn) datpsnAdjMap.get(l_Idtno);
					if (padatpsnadj != null) {
						l_Rnk2n = padatpsnadj.getPayrnk2n();
						l_DEGR2 = padatpsnadj.getPaydegr2();
					}

					l_Open = cDAO.getOpenBonus(l_Title, l_TitSub, l_Rnk2n, l_DEGR2, l_Pfdoc, l_Paretention, l_Csflag);

					strLine = l_Paybank.substring(0, 4) + l_Paybank.substring(5, 7) + l_Paybank.substring(8, 10);
					jsonOne.put("paybank", StringUtil.nullToString(padatpsn.getPaybank()));
					strLine += StringUtil.fillChar(StringUtil.nullToString(padatpsn.getPaydept()), 4, " ", true);
					jsonOne.put("paydept", StringUtil.nullToString(padatpsn.getPaydept()));
					strLine += StringUtil.strFillWideWhite(StringUtil.nullToString(padatpsn.getPadepnmc()), 11, true);
					jsonOne.put("padepnmc", StringUtil.nullToString(padatpsn.getPadepnmc()));
					strLine += StringUtil.nullToString(padatpsn.getPaycard());
					jsonOne.put("paycard", l_card);
					//中文姓名,要檢測難字,是難字轉換成□
					strLine += StringUtil2.decodeTxtEntity(StringUtil.strFillWideWhite(StringUtil.nullToString(padatpsn.getPaynamec()), 6, true));
					jsonOne.put("paynamec", StringUtil.nullToString(padatpsn.getPaynamec()));
					strLine += StringUtil.nullToString(padatpsn.getPaytitle());
					jsonOne.put("paytitle", StringUtil.nullToString(padatpsn.getPaytitle()));
					strLine += StringUtil.strFillWideWhite(StringUtil.nullToString(padatpsn.getPaytitlp()), 11, true);
					jsonOne.put("paytitlp", StringUtil.nullToString(padatpsn.getPaytitlp()));
					strLine += StringUtil.fillChar(StringUtil.nullToString(padatpsn.getPayrnk2n()), 2, "0", true);
					jsonOne.put("payrnk2n", StringUtil.nullToString(padatpsn.getPayrnk2n()));
					strLine += StringUtil.nullToString(padatpsn.getPayrank3());
					jsonOne.put("payrank3", StringUtil.nullToString(padatpsn.getPayrank3()));
					strLine += StringUtil.fillChar(StringUtil.nullToString(padatpsn.getPaydegr2()), 2, "0", false);
					jsonOne.put("paydegr2", StringUtil.nullToString(padatpsn.getPaydegr2()));
					strTmp = StringUtil.nullToString(padatpsn.getPaysgndt());
					strLine += strTmp.substring(0, 4) + strTmp.substring(5, 7) + strTmp.substring(8, 10);
					jsonOne.put("paysgndt", StringUtil.nullToString(padatpsn.getPaysgndt()));
					// VP01專一_補 8 VP049專二_補 8 FP440專一_培訓補助(8) l_teach_00教職獎勵(6) l_teach_09教職獎勵_補(6)
					// l_thesis_10論文獎勵第1篇(6) l_thesis_19論文獎勵第1篇_補(6) l_thesis_20論文獎勵第2篇(6)
					// l_thesis_29論文獎勵第2篇_補(6)
					datpayAry = (ArrayList) datdocMap.get(l_Idtno);
					if (datpayAry != null) {
						for (Padatpay dataP : datpayAry) {
							l_paycode = dataP.getPaycode();
							if (l_paycode.equals("VP01"))
								l_VP01 = dataP.getPayamt();
							if (l_paycode.equals("FP80"))
								l_FP80 = dataP.getPayamt();
							else if (l_paycode.equals("VP049"))
								l_VP049 = dataP.getPayamt();
							else if (l_paycode.equals("FP440"))
								l_FP440 = dataP.getPayamt();
							else if (l_paycode.equals("00"))
								l_teach_00 = dataP.getPayamt();
							else if (l_paycode.equals("09"))
								l_teach_09 = dataP.getPayamt();
							else if (l_paycode.equals("10"))
								l_thesis_10 = dataP.getPayamt();
							else if (l_paycode.equals("19"))
								l_thesis_19 = dataP.getPayamt();
							else if (l_paycode.equals("20"))
								l_thesis_20 = dataP.getPayamt();
							else if (l_paycode.equals("29"))
								l_thesis_29 = dataP.getPayamt();
						}
					}
					if (selEn.matches("Perf_1")) {
						// 專一額度 7
//						strLine += StringUtil.fillChar(StringUtil.nullToString(padatpsn.getPaypfdoc()), 7, " ", false);
//						jsonOne.put("pfdoc", StringUtil.nullToString(padatpsn.getPaypfdoc()));
						strLine += StringUtil.fillChar(String.valueOf(l_Pfdoc), 7, " ", false);
						jsonOne.put("pfdoc", l_Pfdoc);
						strLine += StringUtil.fillChar((l_Open == 0 ? "" : String.valueOf(l_Open)), 6, " ", false);
						jsonOne.put("pfopen", l_Open);
						strLine += StringUtil.fillChar((l_VP01 == 0 ? "" : String.valueOf(l_VP01)), 8, " ", false);
						jsonOne.put("vp019", l_VP01);
						strLine += StringUtil.fillChar((l_VP049 == 0 ? "" : String.valueOf(l_VP049)), 8, " ", false);
						jsonOne.put("vp049", l_VP049);
						strLine += StringUtil.fillChar((l_FP440 == 0 ? "" : String.valueOf(l_FP440)), 8, " ", false);
						jsonOne.put("fp440", l_FP440);
						strLine += StringUtil.fillChar((l_teach_00 == 0 ? "" : String.valueOf(l_teach_00)), 6, " ",false);
						jsonOne.put("teach_00", l_teach_00);
						strLine += StringUtil.fillChar((l_teach_09 == 0 ? "" : String.valueOf(l_teach_09)), 6, " ",false);
						jsonOne.put("teach_09", l_teach_09);
						strLine += StringUtil.fillChar((l_thesis_10 == 0 ? "" : String.valueOf(l_thesis_10)), 6, " ",false);
						jsonOne.put("thesis_10", l_thesis_10);
						strLine += StringUtil.fillChar((l_thesis_19 == 0 ? "" : String.valueOf(l_thesis_19)), 6, " ",false);
						jsonOne.put("thesis_19", l_thesis_19);
						strLine += StringUtil.fillChar((l_thesis_20 == 0 ? "" : String.valueOf(l_thesis_20)), 6, " ",false);
						jsonOne.put("thesis_20", l_thesis_20);
						strLine += StringUtil.fillChar((l_thesis_29 == 0 ? "" : String.valueOf(l_thesis_29)), 6, " ",false);
						jsonOne.put("thesis_29", l_thesis_29);
						if (l_Tracod >= 21 && l_Tracod <= 23) {
							strTmp = StringUtil.nullToString(padatpsn.getPayrsgdt());
							strLine += strTmp.substring(0, 4) + strTmp.substring(5, 7) + strTmp.substring(8, 10);
							jsonOne.put("payrsgdt", StringUtil.nullToString(padatpsn.getPayrsgdt()));
						} else {
							strLine += "";
							jsonOne.put("payrsgdt", "");
						}
					} else if (selEn.matches("Perf_2")) {
						// 工作獎金=Y,找基本績分 俸級績分
						if ("Y".equals(padatpsn.getPacsflag())) {
							CspfMap = cDAO.getCsPfByKey(padatpsn.getPayrank3(), padatpsn.getPaytitle(),
									padatpsn.getPayrank(), padatpsn.getPayrnk2n(), padatpsn.getPaydegr2(),
									padatpsn.getStrbank(), hid, da);
							l_pfbas = (String) CspfMap.get("pfbas");
							l_pfdat = (String) CspfMap.get("pfdat");
						}
						strLine += StringUtil.fillChar(l_pfbas, 4, "0", false);
						jsonOne.put("pfbas", l_pfbas);
						strLine += StringUtil.fillChar(l_pfdat, 4, "0", false);
						jsonOne.put("pfdat", l_pfdat);
						
						//2026.9.22有負值呈現,0-36,改為-036
						if ( padatpsn.getPaypfsp() < 0) {
							strLine += "-" + StringUtil.fillChar(StringUtil.nullToString(Math.abs(padatpsn.getPaypfsp())), 3, "0", false);
						}else {
							strLine += StringUtil.fillChar(StringUtil.nullToString(padatpsn.getPaypfsp()), 4, "0", false);
						}
						jsonOne.put("pfsp", StringUtil.nullToString(padatpsn.getPaypfsp()));
						
						strLine += StringUtil.fillChar((l_Open == 0 ? "" : String.valueOf(l_Open)), 6, " ", false);
						jsonOne.put("pfopen", l_Open);
						// 教職 論文
						strLine += StringUtil.fillChar((l_teach_00 == 0 ? "" : String.valueOf(l_teach_00)), 6, " ",false);
						jsonOne.put("teach_00", l_teach_00);
						strLine += StringUtil.fillChar((l_teach_09 == 0 ? "" : String.valueOf(l_teach_09)), 6, " ",false);
						jsonOne.put("teach_09", l_teach_09);
						strLine += StringUtil.fillChar((l_thesis_10 == 0 ? "" : String.valueOf(l_thesis_10)), 6, " ",false);
						jsonOne.put("thesis_10", l_thesis_10);
						strLine += StringUtil.fillChar((l_thesis_19 == 0 ? "" : String.valueOf(l_thesis_19)), 6, " ",false);
						jsonOne.put("thesis_19", l_thesis_19);
						strLine += StringUtil.fillChar((l_thesis_20 == 0 ? "" : String.valueOf(l_thesis_20)), 6, " ",false);
						jsonOne.put("thesis_20", l_thesis_20);
						strLine += StringUtil.fillChar((l_thesis_29 == 0 ? "" : String.valueOf(l_thesis_29)), 6, " ",false);
						jsonOne.put("thesis_29", l_thesis_29);
						strLine += StringUtil.fillChar((l_FP80 == 0 ? "" : String.valueOf(l_FP80)), 6, " ", false);
						jsonOne.put("fp80", l_FP80);
						strLine += StringUtil.fillChar("", 6, " ", false);
						jsonOne.put("pfdiff", 0);
						// 總醫師
						strTmp = StringUtil.nullToString(padatpsn.getPayexpfkey2());
						if (strTmp.equals("6")) {
							l_pfcr = "  20";
						}
						strLine += StringUtil.fillChar(l_pfcr, 4, " ", false);
						jsonOne.put("pfcr", l_pfcr);
					}
					bfw.write(strLine);
					bfw.newLine();
					bfw.flush();
					jsonRtn.add(jsonOne);
				}

			}
			log.info(me + "  ...********* dataaccess:共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			if (bfw != null)
				bfw.close();
			closeAll(da, null, connFlg);
		}

		return jsonRtn;
	}
	
	
	/**
	 * 關閉VGHDataAccess與ResultSet
	 * 
	 * @param da
	 * @param rs
	 */
	protected void closeAll(VGHDataAccess da, ResultSet rs, boolean blnclose) {
		if (rs != null) {
			try {
				rs.close();
				rs = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!blnclose) {
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
