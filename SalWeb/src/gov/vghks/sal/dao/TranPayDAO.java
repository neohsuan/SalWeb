package gov.vghks.sal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import gov.vghks.sal.model.Padept;
import gov.vghks.sal.model.Patitle;
import gov.vghks.sal.model.Patrapay;
import gov.vghks.sal.model.Patrapsn;
import gov.vghks.sal.model.Psbasic;
import gov.vghks.sal.util.AmJsonConfig;
import gov.vghks.sal.util.PaCommUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

//異動檔
public class TranPayDAO {
	String gClassName = TranPayDAO.class.getName();
	static Logger log = Logger.getLogger(TranPayDAO.class);  //呼叫log4j
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
	private String tTraCode  = ""; 	// 1異動代碼
	private String tDept 	 = ""; 	// 2單位代碼
	private String tTitle	 = ""; 	// 3職稱
	private String tBasPsn 	 = ""; 	// 4基本檔
	private String tBasPay 	 = ""; 	// 5基本支扣
	private String tBatPay   = "";	// 6批次檔
	private String tRnk4Tbl	 = "";	// 7適用專業加給對照表
	private String tRankCode = ""; 	// 10階別代碼
	private String tPayItm   = ""; 	// 27支扣項目彙總
	private String tBasAdr   = ""; 	// 28地址
	private String tTraPsn 	 = ""; 	// 30異動個人明細
	private String tTraPsnDw = ""; 	// 31異動個人明細_歷史
	private String tTraPay   = ""; 	// 32異動支扣明細
	private String tTraPayDw = ""; 	// 33異動支扣明細_歷史
	
	private String tmpZtrapsn = "";
	private String tmpZtrapay = "";
	private String tmpZbatpay = "";
	
	public TranPayDAO(VGHDataAccess da1,String hid) {
		super();
		this.da = da1;
		connFlg = true;
		this.hid = hid;
		Tables tabInfo = Tables.getTables(hid);
		
		tTitle 	  = tabInfo.tTitle;
		tDept 	  = tabInfo.tDept;
		tRankCode = tabInfo.tRankCode;
		tRnk4Tbl  = tabInfo.tRnk4Tbl;
		tTraCode  = tabInfo.tTraCode;
		tTraPsn   = tabInfo.tTraPsn;
		tTraPsnDw = tabInfo.tTraPsnDw;
		tBasAdr   = tabInfo.tBasAdr;
		tTraPay   = tabInfo.tTraPay;
		tTraPayDw = tabInfo.tTraPayDw;
		tPayItm   = tabInfo.tPayItm;
		tBasPsn   = tabInfo.tBasPsn;
		tBasPay   = tabInfo.tBasPay;
		tBatPay   = tabInfo.tBatPay;
		
		tmpZtrapsn = tabInfo.tmpZtrapsn;
		tmpZtrapay = tabInfo.tmpZtrapay;
		tmpZbatpay = tabInfo.tmpZbatpay;
	}
	
	public TranPayDAO(VGHDataAccess da1,VGHDataAccess da2,String hid) {
		super();
		this.da = da1;
		this.daDSN = da2;
		connFlg = true;
		this.hid = hid;
		Tables tabInfo = Tables.getTables(hid);
		
		tTitle 	  = tabInfo.tTitle;
		tDept 	  = tabInfo.tDept;
		tRankCode = tabInfo.tRankCode;
		tRnk4Tbl  = tabInfo.tRnk4Tbl;
		tTraCode  = tabInfo.tTraCode;
		tTraPsn   = tabInfo.tTraPsn;
		tTraPsnDw = tabInfo.tTraPsnDw;
		tBasAdr   = tabInfo.tBasAdr;
		tTraPay   = tabInfo.tTraPay;
		tTraPayDw = tabInfo.tTraPayDw;
		tPayItm   = tabInfo.tPayItm;
		tBasPsn   = tabInfo.tBasPsn;
		tBasPay   = tabInfo.tBasPay;
		tBatPay   = tabInfo.tBatPay;
		
		tmpZtrapsn = tabInfo.tmpZtrapsn;
		tmpZtrapay = tabInfo.tmpZtrapay;
		tmpZbatpay = tabInfo.tmpZbatpay;
	}
	// 依篩選條件,取得pa.patrapsn集合 item:1個人明細,2篩選
	public JSONArray getTranPsnList(String code_data, String item, String userid, String hid, VGHDataAccess da)
			throws Exception {
		String me = getClass().getName() + ".getTranPsnList(),userid = " + userid;
		log.info(me + " code_data = " + code_data + " hid = " + hid);
		CommDAO cDAO = null;
		HashMap CspfMap = new HashMap();
		HashMap cspfRtn = new HashMap();
		Pacspf cspfObj = null;
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_code_data = null;
		Date start = new Date();
		Date end = new Date();
		String sqlA = "";
		String expfsp = "";
		String csflag = "";
		String paret = "";
		String cardno = "";
		String idtno = "";
		String namec = "";
		String empno = "";
		String effdt_bng = "";
		String effdt_end = "";
		String[] tracod = null;
		String[] salnum = null;
		String inflg = "";
		String[] patradt = null;
		String[] payrank = null;
		String[] paytitle = null;
		String[] paydept = null;
		String[] expfkey1 = null;
		String[] expfkey2 = null;
		String[] expfkey3 = null;
		String subsql = "";
		String strSelect = "";
		Pabaspsn baspsnObj = null;
		int pfbas = 0;
		int pfdat = 0;
		int rank3 = 0;
		String vPFKEY = "";
		int i = 0;
		long starttime = 0;
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> paramsub = new ArrayList<Object>();
		if (code_data != null && !code_data.equals("")) {
			object_code_data = JSONObject.fromObject(code_data);
			tracod = (!object_code_data.has("patracod")) ? null
					: ("".equals(object_code_data.getString("patracod")) ? null
							: object_code_data.getString("patracod").split(","));
			patradt = (!object_code_data.has("patradt")) ? null
					: ("".equals(object_code_data.getString("patradt")) ? null
							: object_code_data.getString("patradt").split(","));
			salnum = (!object_code_data.has("salnum")) ? null
					: ("".equals(object_code_data.getString("salnum")) ? null
							: object_code_data.getString("salnum").split(","));
			cardno = (!object_code_data.has("cardno")) ? "" : object_code_data.getString("cardno");
			idtno = (!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			namec = (!object_code_data.has("namec")) ? "" : object_code_data.getString("namec");
			empno = (!object_code_data.has("empno")) ? "" : object_code_data.getString("empno");
			effdt_bng = (!object_code_data.has("effdt_bng")) ? "" : object_code_data.getString("effdt_bng");
			effdt_end = (!object_code_data.has("effdt_end")) ? "" : object_code_data.getString("effdt_end");
			paydept = (!object_code_data.has("paydept")) ? null
					: ("".equals(object_code_data.getString("paydept")) ? null
							: object_code_data.getString("paydept").split(","));

			csflag = (!object_code_data.has("csflag")) ? "" : object_code_data.getString("csflag");
			expfsp = (!object_code_data.has("expfsp")) ? "" : object_code_data.getString("expfsp");
			inflg = (!object_code_data.has("inflg")) ? "" : object_code_data.getString("inflg");
			paret = (!object_code_data.has("paret")) ? "" : object_code_data.getString("paret");
			payrank = (!object_code_data.has("payrank")) ? null
					: ("".equals(object_code_data.getString("payrank")) ? null
							: object_code_data.getString("payrank").split(","));
			paytitle = (!object_code_data.has("paytitle")) ? null
					: ("".equals(object_code_data.getString("paytitle")) ? null
							: object_code_data.getString("paytitle").split(","));
			expfkey1 = (!object_code_data.has("expfkey1")) ? null
					: ("".equals(object_code_data.getString("expfkey1")) ? null
							: object_code_data.getString("expfkey1").split(","));
//				expfkey2 = (!object_code_data.has("expfkey2")) ? null
//						: ("".equals(object_code_data.getString("expfkey2")) ? null
//								: object_code_data.getString("expfkey2").split(","));
//				expfkey3 = (!object_code_data.has("expfkey3")) ? null
//						: ("".equals(object_code_data.getString("expfkey3")) ? null
//								: object_code_data.getString("expfkey3").split(","));
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

		sqlA = " A " + " left outer join " + this.tDept + " B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID "
				+ " left outer join " + this.tTitle + " C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID "
				+ " left outer join " + this.tRankCode + " D on A.PAYRANK=D.RANKCODE " + " left outer join "
				+ this.tRnk4Tbl + " E on A.PAYRANK4=E.RNK4KEY " + " left outer join " + this.tTitle
				+ " C1 on A.PAOTITLE=C1.PAYTITLK and A.HID=C1.HID " + " left outer join " + this.tRankCode
				+ " D1 on A.PAORANK=D1.RANKCODE " + " left outer join " + this.tRnk4Tbl
				+ " E1 on A.PAORANK4=E1.RNK4KEY " + " left outer join " + this.tTraCode + " F on A.PATRACOD=F.TRACODE "
				+ " where A.HID=? ";
		params.add(hid);

		if (!"".equals(cardno)) {// 卡號
			subsql += " and PAYCARD=? ";
			params.add(cardno);
			paramsub.add(cardno);
		} else if (!"".equals(idtno)) {// 身分證
			subsql += " and PAYIDNO=? ";
			params.add(idtno);
			paramsub.add(idtno);
		} else if (!"".equals(namec)) {// 姓名
			subsql += " and PAYNAMEC like ? ";
			params.add(namec + "%");
			paramsub.add(namec + "%");
		} else if (!"".equals(empno)) {// 職號
			subsql += " and PAYEMPNO like ? ";
			params.add(empno + "%");
			paramsub.add(empno + "%");
		}
		if (!"".equals(effdt_bng) && !"".equals(effdt_end)) {// 生效日
			subsql += " and PAEFFDT between ? and ? ";
			params.add(effdt_bng);
			params.add(effdt_end);
			paramsub.add(effdt_bng);
			paramsub.add(effdt_end);
		}
		if (!(tracod == null || (tracod != null && tracod.length == 0))) { // 異動原因
			subsql += " and PATRACOD in ( ";
			for (i = 0; i < tracod.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(tracod[i]);
				paramsub.add(tracod[i]);
			}
			subsql += ")";
		}
		if (!"".equals(csflag)) {// 支領工作獎金(Y/N)
			subsql += " and PACSFLAG=? ";
			params.add(csflag);
			paramsub.add(csflag);
		}

		if (!(patradt == null || (patradt != null && patradt.length == 0))) { // 異動日期
			subsql += " and PATRADT in ( ";
			for (i = 0; i < patradt.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(patradt[i]);
				paramsub.add(patradt[i]);
			}
			subsql += ")";
		} else if (!(salnum == null || (salnum != null && salnum.length == 0))) { // 餉單編號
			subsql += " and SALNUM in ( ";
			for (i = 0; i < salnum.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(salnum[i]);
				paramsub.add(salnum[i]);
			}
			subsql += ")";
		}
		if (!"".equals(inflg)) {// 保險補助比例
			subsql += " and PAYINFLG<>0 ";
		}
		if (!"".equals(expfsp)) {// 額外點數1 2 3有值皆要列入
			subsql += " and (PAYPFSP1<>0 or PAYPFSP2<>0 or PAYPFSP3<>0) ";
		}
		if (!"".equals(paret)) {// 留職留薪,支領專2(Y/N)
			subsql += " and PARETENTION=? ";
			params.add(paret);
			paramsub.add(paret);
		}
		// 階級 payrank paorank
		if (!(payrank == null || (payrank != null && payrank.length == 0))) {
			for (int j = 0; j < 2; j++) {
				if (j == 0)
					subsql += " and (PAYRANK in ( ";
				else
					subsql += " ) or PAORANK in ( ";
				for (i = 0; i < payrank.length; i++) {
					if (i != 0)
						subsql += ",";
					subsql += "?";
					params.add(payrank[i]);
					paramsub.add(payrank[i]);
				}
			}
			subsql += " ) )";
		}
		// 職稱 paytitle paotitle
		if (!(paytitle == null || (paytitle != null && paytitle.length == 0))) {
			for (int j = 0; j < 2; j++) {
				if (j == 0)
					subsql += " and (PAYTITLE in ( ";
				else
					subsql += " ) or PAOTITLE in ( ";
				for (i = 0; i < paytitle.length; i++) {
					if (i != 0)
						subsql += ",";
					subsql += "?";
					params.add(paytitle[i]);
					paramsub.add(paytitle[i]);
				}
			}
			subsql += " ) )";
		}
		// 單位
		if (!(paydept == null || (paydept != null && paydept.length == 0))) {
			subsql += " and paydept in ( ";
			for (i = 0; i < paydept.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(paydept[i]);
				paramsub.add(paydept[i]);
			}
			subsql += ")";
		}
		if (!(expfkey1 == null || (expfkey1 != null && expfkey1.length == 0)  || (expfkey1.length == 1 && expfkey1[0].equals("0")) )) { // 額外點數1
			subsql += " and ( payexpfkey1 in ( ";
			for (i = 0; i < expfkey1.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(expfkey1[i]);
				paramsub.add(expfkey1[i]);
			}
			subsql += ") or payexpfkey2 in ( ";
			for (i = 0; i < expfkey1.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(expfkey1[i]);
				paramsub.add(expfkey1[i]);
			}
			subsql += ") or payexpfkey3 in ( ";
			for (i = 0; i < expfkey1.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(expfkey1[i]);
				paramsub.add(expfkey1[i]);
			}
			subsql += ") )";
		}
		/*if (!(expfkey2 == null || (expfkey2 != null && expfkey2.length == 0))) { // 額外點數2
			subsql += " and payexpfkey2 in ( ";
			for (i = 0; i < expfkey2.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(expfkey2[i]);
				paramsub.add(expfkey2[i]);
			}
			subsql += ")";
		}
		if (!(expfkey3 == null || (expfkey3 != null && expfkey3.length == 0))) { // 額外點數3
			subsql += " and payexpfkey3 in ( ";
			for (i = 0; i < expfkey3.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(expfkey3[i]);
				paramsub.add(expfkey3[i]);
			}
			subsql += ")";
		}*/

		sql = "select " + strSelect + " from " + this.tTraPsn + sqlA + subsql + " union all " + "select " + strSelect
				+ " from " + this.tTraPsnDw + sqlA + subsql + " order by PATRADT DESC,7 with ur";
		params.add(hid);
		for (int j = 0; j < paramsub.size(); j++) {
			params.add(paramsub.get(j));
		}

		log.info(me + "  " + sql + "...params:" + params.toString());
		starttime = System.currentTimeMillis();
		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}

			start = new Date();
			if ("2".equals(item)) {
				cDAO = new CommDAO(da, hid);
				CspfMap = cDAO.getCsPfByAll(da, hid);
			}

			log.info(me + "  " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					baspsnObj = new Pabaspsn();
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
					jsonOne.put("salnum", StringUtil.nullToString(data.get("salnum")));
					if ("2".equals(item)) {
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
						jsonOne.put("payinflg", PaCommUtil.getInflgNm((Integer) data.get("payinflg")));
						jsonOne.put("paydpno", StringUtil.nullToString(data.get("paydpno")));
						jsonOne.put("paytaxno", StringUtil.nullToString(data.get("paytaxno")));
						jsonOne.put("insuamt", StringUtil.nullToString(data.get("insuamt")));
						jsonOne.put("contract", baspsnObj.getContract());
						jsonOne.put("paretention", baspsnObj.getParetention());
						// 新
						jsonOne.put("paytitle", StringUtil.nullToString(data.get("paytitle")));
						jsonOne.put("paytitlp", StringUtil.nullToString(data.get("paytitlp")));
						jsonOne.put("payrnk2n", StringUtil.nullToString(data.get("payrnk2n")));
						rank3 = (Integer) (data.get("payrank3"));
						jsonOne.put("payrank3", rank3);
						jsonOne.put("rank3nm", PaCommUtil.getRank3Nm(rank3));
						jsonOne.put("payrank3", StringUtil.nullToString(data.get("payrank3")));
						jsonOne.put("paydegr2", StringUtil.nullToString(data.get("paydegr2")));
						jsonOne.put("paydegre", StringUtil.nullToString(data.get("paydegre")));
						jsonOne.put("payrank", StringUtil.nullToString(data.get("payrank")));
						jsonOne.put("rankname", StringUtil.nullToString(data.get("rankname")));
						jsonOne.put("payrank2", StringUtil.nullToString(data.get("payrank2")));
						jsonOne.put("payrankc", StringUtil.nullToString(data.get("payrankc")));
						jsonOne.put("rnk4nm", StringUtil.nullToString(data.get("rnk4nm")));
						csflag = StringUtil.nullToString(data.get("pacsflag"));
						jsonOne.put("pacsflag", csflag);
						jsonOne.put("paypfsp1", StringUtil.nullToString(data.get("paypfsp1")));
						jsonOne.put("paypfsp2", StringUtil.nullToString(data.get("paypfsp2")));
						jsonOne.put("paypfsp3", StringUtil.nullToString(data.get("paypfsp3")));
						jsonOne.put("paypfdoc", StringUtil.nullToString(data.get("paypfdoc")));
						// 工作獎金=Y,找基本績分 俸級績分
						pfbas = 0;
						pfdat = 0;
						if ("Y".equals(csflag)) {
							cspfRtn = cDAO.getCsPfByMap(rank3, StringUtil.nullToString(data.get("paytitle")),
									StringUtil.nullToString(data.get("payrank")),
									StringUtil.nullToString(data.get("payrnk2n")), (Integer) data.get("paydegr2"),
									StringUtil.nullToString(data.get("patradt")), hid, CspfMap, da);
							if (cspfRtn != null) {
								pfbas = (Integer) cspfRtn.get("pfbas");
								pfdat = (Integer) cspfRtn.get("pfdat");
							}
						}
						jsonOne.put("pfbas", pfbas);
						jsonOne.put("pfdat", pfdat);
						// 原
						jsonOne.put("paotitle", StringUtil.nullToString(data.get("paotitle")));
						jsonOne.put("paytitlo", StringUtil.nullToString(data.get("paytitlo")));
						jsonOne.put("paornk2n", StringUtil.nullToString(data.get("paornk2n")));
						rank3 = (Integer) (data.get("paorank3"));
						jsonOne.put("paorank3", rank3);
						jsonOne.put("orank3nm", PaCommUtil.getRank3Nm(rank3));
						jsonOne.put("paorank3", StringUtil.nullToString(data.get("paorank3")));
						jsonOne.put("paodegr2", StringUtil.nullToString(data.get("paodegr2")));
						jsonOne.put("paodegre", StringUtil.nullToString(data.get("paodegre")));
						jsonOne.put("paorank", StringUtil.nullToString(data.get("paorank")));
						jsonOne.put("oranknm", StringUtil.nullToString(data.get("oranknm")));
						jsonOne.put("paorank2", StringUtil.nullToString(data.get("paorank2")));
						jsonOne.put("paorankc", StringUtil.nullToString(data.get("paorankc")));
						jsonOne.put("ornk4nm", StringUtil.nullToString(data.get("ornk4nm")));
						csflag = StringUtil.nullToString(data.get("paocsfla"));
						jsonOne.put("paocsfla", csflag);
						jsonOne.put("paopfsp1", StringUtil.nullToString(data.get("paopfsp1")));
						jsonOne.put("paopfsp2", StringUtil.nullToString(data.get("paopfsp2")));
						jsonOne.put("paopfsp3", StringUtil.nullToString(data.get("paopfsp3")));
						jsonOne.put("paopfdoc", StringUtil.nullToString(data.get("paopfdoc")));
						// 工作獎金=Y,找基本績分 俸級績分
						pfbas = 0;
						pfdat = 0;
						if ("Y".equals(csflag)) {
							cspfRtn = cDAO.getCsPfByMap(rank3, StringUtil.nullToString(data.get("paotitle")),
									StringUtil.nullToString(data.get("paorank")),
									StringUtil.nullToString(data.get("paornk2n")), (Integer) data.get("paodegr2"),
									StringUtil.nullToString(data.get("patradt")), hid, CspfMap, da);
							if (cspfRtn != null) {
								pfbas = (Integer) cspfRtn.get("pfbas");
								pfdat = (Integer) cspfRtn.get("pfdat");
							}
						}
						jsonOne.put("pfobas", pfbas);
						jsonOne.put("pfodat", pfdat);
					}
					jsonRtn.add(jsonOne);
				}
			}
			end = new Date();
			log.info(me + "  ...********* dataaccess:" + (end.getTime() - start.getTime()) + " 共花費："
					+ (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}

		return jsonRtn;
	}
	// 依KEY(id/name,tradt),取得pa.patraPSN(不含薪級異動)
	public Patrapsn getTraPsnByNRank(String in_data, String userid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getTraPsnByNRank(),userid = " + userid;
		log.info(me + " in_data = " + in_data + " hid = " + hid);

		JSONObject object_code_data = null;
		Patrapsn patrapsn = null;
		Date start = new Date();
		Date end = new Date();
		String strSelect = "";
		String idtno = "";
		String cardno = "";
		String patradt = "";
		
		ArrayList<Object> params = new ArrayList<Object>();

		if (in_data != null && !in_data.equals("")) {
			object_code_data = JSONObject.fromObject(in_data);
			patradt = (!object_code_data.has("tradt")) ? "" : object_code_data.getString("tradt");
			cardno = (!object_code_data.has("cardno")) ? "" : object_code_data.getString("cardno");
			idtno = (!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			hid = (!object_code_data.has("hid")) ? "" : object_code_data.getString("hid");
		}
		// 讀取pa.patrapsn(不含12考績獎金 41支扣 51年終獎金    
		// 讀取pa.patrapsn 因要判定新舊制,要join patitle
		strSelect = "PATRACOD, PATRADT, COALESCE(PAEFFDT,'9999-12-31') as PAEFFDT, SALNUM, PAYLVEFG, A.PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, "
				+ "PAYEMPDT, PAYSGNDT, COALESCE(PAYRSGDT,'9999-12-31') as PAYRSGDT, PAYBKID, PAYACNO, PAYGEDNO, COALESCE(A.PAYINFLG,0) as PAYINFLG,COALESCE(PAYDPNO,'0') as PAYDPNO, PAYPFLAG, PAYTITLE, "
				+ "PAYRNK2N, COALESCE(A.PAYRANK3,0) as PAYRANK3, COALESCE(A.PAYDEGR2,0) as PAYDEGR2, COALESCE(A.PAYDEGRE,0) as PAYDEGRE, PAYRANK, COALESCE(A.PAYRANK2,'') as PAYRANK2, "
				+ "COALESCE(A.PAYRANKC,'') as PAYRANKC, COALESCE(A.PAYPFDOC,0) as PAYPFDOC, PACSFLAG, COALESCE(A.PAYPFSP1,0) as PAYPFSP1, "
				+ "COALESCE(A.PAYPFSP2,0) as PAYPFSP2, COALESCE(A.PAYPFSP3,0) as PAYPFSP3, COALESCE(A.PAYMONNO,0) as PAYMONNO, COALESCE(A.PAYDAYNO,0) as PAYDAYNO, PAYPFLAG,PAOTITLE, PAORNK2N, COALESCE(A.PAORANK3,0) as PAORANK3, "
				+ "COALESCE(A.PAODEGR2,0) as PAODEGR2, COALESCE(A.PAODEGRE,0) as PAODEGRE, PAORANK, COALESCE(A.PAORANK2,'') as PAORANK2, COALESCE(A.PAYRANKC,'') as PAORANKC, COALESCE(A.PAOPFDOC,0) as PAOPFDOC, PAOCSFLA, "
				+ "COALESCE(A.PAOPFSP1,0) as PAOPFSP1, COALESCE(A.PAOPFSP2,0) as PAOPFSP2, COALESCE(A.PAOPFSP3,0) as PAOPFSP3, COALESCE(A.PAYTAXNO,0) as PAYTAXNO,"
				+ "A.PAYUPDID, A.PAYUPDTM,PAYFLAG, TRANUM, UPMASTDT, PAYPSW, PAOEXPFKEY1, PAOEXPFKEY2, PAOEXPFKEY3, PAYEXPFKEY1, PAYEXPFKEY2, "
				+ "PAYEXPFKEY3, COALESCE(A.PAORANK4,0) as PAORANK4, COALESCE(A.PAYRANK4,0) as PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, INSUAMT, A.HID, "
				+ "B.PADEPNMC,B.PACSTNO,C.PAYTITLP,H.PAYTITLP PAYTITLO,C.PATITSUB,H.PATITSUB PATITSUO";

		sql = "select " + strSelect + " from " + this.tTraPsn + " A " + 
			   " left outer join " + this.tDept     + " B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID " + 
			   " left outer join " + this.tTitle    + " C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID " +
			   " left outer join " + this.tTitle    + " H on A.PAOTITLE=H.PAYTITLK and A.HID=H.HID " + 
			   " where A.HID=? and PATRADT=? and PATRACOD not in (12,41,51) "; 
		params.add(hid);
		params.add(patradt);
		if (!"".equals(cardno)) {// 卡號
			sql += " and PAYCARD=? with ur";
			params.add(cardno);			
		} else if (!"".equals(idtno)) {// 身分證
			sql += " and PAYIDNO=?  with ur";
			params.add(idtno);			
		}		
		
		log.info(me + "  " + sql + "...params:" + params.toString());
		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			
			start = new Date();
			log.info(me + "  " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());
			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					patrapsn = new Patrapsn();
					PropertyUtils.copyProperties(patrapsn, data);					
				}
			}

			end = new Date();
			log.info(me + "  ...********* dataaccess:" + (end.getTime() - start.getTime()));
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);			
		}

		return patrapsn;
	}
	// 依KEY,取得pa.patraPSN
	public Patrapsn getTraPsnBykey(String code_data, String userid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getTraPsnBykey(),userid = " + userid;
		log.info(me + " code_data = " + code_data + " hid = " + hid);

		CommDAO cDAO = null;
		HashMap CspfMap = new HashMap(); // 工作獎金支扣 對照表
		JSONObject object_code_data = null;
		Patrapsn patrapsn = null;
		Date start = new Date();
		Date end = new Date();
		String strSelect = "";
		String idtno = "";
		String patradt = "";
		String patracod = "";
		ArrayList<Object> params = new ArrayList<Object>();

		if (code_data != null && !code_data.equals("")) {
			object_code_data = JSONObject.fromObject(code_data);
			patradt = (!object_code_data.has("tradt")) ? "" : object_code_data.getString("tradt");
			patracod = (!object_code_data.has("tracod")) ? "" : object_code_data.getString("tracod");
			idtno = (!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			hid = (!object_code_data.has("hid")) ? "" : object_code_data.getString("hid");
		}
		// 讀取pa.patrapsn
		strSelect = "PATRACOD, PATRADT, COALESCE(PAEFFDT,'9999-12-31') as PAEFFDT, SALNUM, PAYLVEFG, A.PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, "
				+ "PAYEMPDT, PAYSGNDT, COALESCE(PAYRSGDT,'9999-12-31') as PAYRSGDT, PAYBKID, PAYACNO, PAYGEDNO, COALESCE(A.PAYINFLG,0) as PAYINFLG,COALESCE(PAYDPNO,'0') as PAYDPNO, PAYPFLAG, PAYTITLE, "
				+ "PAYRNK2N, COALESCE(A.PAYRANK3,0) as PAYRANK3, COALESCE(A.PAYDEGR2,0) as PAYDEGR2, COALESCE(A.PAYDEGRE,0) as PAYDEGRE, PAYRANK, COALESCE(A.PAYRANK2,'') as PAYRANK2, "
				+ "COALESCE(A.PAYRANKC,'') as PAYRANKC, COALESCE(A.PAYPFDOC,0) as PAYPFDOC, PACSFLAG, COALESCE(A.PAYPFSP1,0) as PAYPFSP1, "
				+ "COALESCE(A.PAYPFSP2,0) as PAYPFSP2, COALESCE(A.PAYPFSP3,0) as PAYPFSP3, COALESCE(A.PAYMONNO,0) as PAYMONNO, COALESCE(A.PAYDAYNO,0) as PAYDAYNO, PAYPFLAG,PAOTITLE, PAORNK2N, COALESCE(A.PAORANK3,0) as PAORANK3, "
				+ "COALESCE(A.PAODEGR2,0) as PAODEGR2, COALESCE(A.PAODEGRE,0) as PAODEGRE, PAORANK, COALESCE(A.PAORANK2,'') as PAORANK2, COALESCE(A.PAORANKC,'') as PAORANKC, COALESCE(A.PAOPFDOC,0) as PAOPFDOC, PAOCSFLA, "
				+ "COALESCE(A.PAOPFSP1,0) as PAOPFSP1, COALESCE(A.PAOPFSP2,0) as PAOPFSP2, COALESCE(A.PAOPFSP3,0) as PAOPFSP3, COALESCE(A.PAYTAXNO,0) as PAYTAXNO,"
				+ "A.PAYUPDID, A.PAYUPDTM,PAYFLAG, TRANUM, UPMASTDT, PAYPSW, PAOEXPFKEY1, PAOEXPFKEY2, PAOEXPFKEY3, PAYEXPFKEY1, PAYEXPFKEY2, "
				+ "PAYEXPFKEY3, COALESCE(A.PAORANK4,0) as PAORANK4, COALESCE(A.PAYRANK4,0) as PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, INSUAMT, A.HID, "
				+ "B.PADEPNMC,B.PACSTNO,C.PAYTITLP,H.PAYTITLP PAYTITLO,C.PATITSUB,H.PATITSUB PATITSUO,D.RANKNAME,I.RANKNAME RANKNAMO,"
				+ "E.RNK4NAME,J.RNK4NAME RNK4NAMO,F.TRANAME,G.PAYADDRS ";

		sql = "select " + strSelect + " from " + this.tTraPsn + " A " + " left outer join " + this.tDept
				+ " B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID " + " left outer join " + this.tTitle
				+ " C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID " + " left outer join " + this.tRankCode
				+ " D on A.PAYRANK=D.RANKCODE " + " left outer join " + this.tRnk4Tbl + " E on A.PAYRANK4=E.RNK4KEY "
				+ " left outer join " + this.tTraCode + " F on A.PATRACOD=F.TRACODE " + " left outer join "
				+ this.tBasAdr + " G on A.PAYIDNO=G.PAYIDNO " + " left outer join " + this.tTitle
				+ " H on A.PAOTITLE=H.PAYTITLK and A.HID=H.HID " + " left outer join " + this.tRankCode
				+ " I on A.PAORANK=I.RANKCODE " + " left outer join " + this.tRnk4Tbl + " J on A.PAORANK4=J.RNK4KEY "
				+ " where A.PAYIDNO=? and PATRADT=? and PATRACOD=? and A.HID=? " + " union all " + "select " + strSelect
				+ " from " + this.tTraPsnDw + " A " + " left outer join " + this.tDept
				+ " B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID " + " left outer join " + this.tTitle
				+ " C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID " + " left outer join " + this.tRankCode
				+ " D on A.PAYRANK=D.RANKCODE " + " left outer join " + this.tRnk4Tbl + " E on A.PAYRANK4=E.RNK4KEY "
				+ " left outer join " + this.tTraCode + " F on A.PATRACOD=F.TRACODE " + " left outer join "
				+ this.tBasAdr + " G on A.PAYIDNO=G.PAYIDNO " + " left outer join " + this.tTitle
				+ " H on A.PAOTITLE=H.PAYTITLK and A.HID=H.HID " + " left outer join " + this.tRankCode
				+ " I on A.PAORANK=I.RANKCODE " + " left outer join " + this.tRnk4Tbl + " J on A.PAORANK4=J.RNK4KEY "
				+ " where A.PAYIDNO=? and PATRADT=? and PATRACOD=? and A.HID=? " + " with ur";
		params.add(idtno);
		params.add(patradt);
		params.add(patracod);
		params.add(hid);
		params.add(idtno);
		params.add(patradt);
		params.add(patracod);
		params.add(hid);
		log.info(me + "  " + sql + "...params:" + params.toString());
		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			cDAO = new CommDAO(da, hid);
			start = new Date();
			log.info(me + "  " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());
			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					patrapsn = new Patrapsn();
					PropertyUtils.copyProperties(patrapsn, data);
					// 工作獎金=Y,找基本績分 俸級績分
					if (hid.matches("1A0|2A0")) {
						if ("Y".equals(patrapsn.getPacsflag())) {
							CspfMap = cDAO.getCsPfByKey(patrapsn.getPayrank3(), patrapsn.getPaytitle(),
									patrapsn.getPayrank(), patrapsn.getPayrnk2n(), patrapsn.getPaydegr2(),
									patrapsn.getStrtradt(), hid, da);
							patrapsn.setPfkey((String) CspfMap.get("pfkey"));
							patrapsn.setPfbas((String) CspfMap.get("pfbas"));
							patrapsn.setPfdat((String) CspfMap.get("pfdat"));
						}
						if ("Y".equals(patrapsn.getPaocsfla())) {
							CspfMap = cDAO.getCsPfByKey(patrapsn.getPaorank3(), patrapsn.getPaotitle(),
									patrapsn.getPaorank(), patrapsn.getPaornk2n(), patrapsn.getPaodegr2(),
									patrapsn.getStrtradt(), hid, da);
							patrapsn.setPokey((String) CspfMap.get("pfkey"));
							patrapsn.setPobas((String) CspfMap.get("pfbas"));
							patrapsn.setPodat((String) CspfMap.get("pfdat"));
						}
					}
				}
			}

			end = new Date();
			log.info(me + "  ...********* dataaccess:" + (end.getTime() - start.getTime()));
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
			cDAO = null;
		}

		return patrapsn;
	}
	
	// 依KEY,取得pa.patrapay
	public HashMap<String, Object> getTraPayList(String code_data, String userid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getTraPayList(),userid = " + userid;
		log.info(me + " code_data = " + code_data + " hid = " + hid);
		Patrapay trapayObj = null;
		ArrayList<Patrapay> trapayAry = new ArrayList<Patrapay>(); // 支扣集合
		HashMap rtnMap = new HashMap();
		JSONArray jsonRtn = new JSONArray();
		JSONArray oTtrapayRtn = new JSONArray();
		JSONArray nTtrapayRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_code_data = null;
		Date start = new Date();
		Date end = new Date();
		String strSelect = "";
		String idtno = "";
		String tranum = "";
		String patradt = "";
		String strPaysort = "";
		String payamtp = "";
		String payamtd = "";
		String strPaycode = "";
		int i = 0;
		ArrayList<Object> params = new ArrayList<Object>();
		if (code_data != null && !code_data.equals("")) {
			object_code_data = JSONObject.fromObject(code_data);
			idtno = (!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			tranum = (!object_code_data.has("tranum")) ? "" : object_code_data.getString("tranum");
			patradt = (!object_code_data.has("tradt")) ? "" : object_code_data.getString("tradt");
		}
		// 讀取pa.patrapay
		strSelect = " TRANUM, PAYIDNO, PATRADT, PAYSORT, A.PAYCODE, PAYAMT, A.PAYAUTH, A.PAYIFTAX, PAYUPDTM, PAYFLAG, A.HID, PACODNMC,PACODDEL,PAYREWARD,PAYUPDTM ";
		sql = "select " + strSelect + " from " + this.tTraPay + " A " + " left outer join " + this.tPayItm
				+ " B on A.PAYCODE=B.PAYCODE and A.HID=B.HID " + " where A.HID=? and PAYIDNO=? and PATRADT=? "
				+ " union all " + "select " + strSelect + " from " + this.tTraPayDw + " A " + " left outer join "
				+ this.tPayItm + " B on A.PAYCODE=B.PAYCODE and A.HID=B.HID "
				+ " where A.HID=? and PAYIDNO=? and PATRADT=? " + "order by 3,4,5 with ur ";
		params.add(hid);
		params.add(idtno);
		params.add(patradt);
		params.add(hid);
		params.add(idtno);
		params.add(patradt);

		log.info(me + "  " + sql + "...params:" + params.toString());

		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			start = new Date();
			log.info(me + "  " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					// 放置在Arraylist
					trapayObj = new Patrapay();
					PropertyUtils.copyProperties(trapayObj, data);
					trapayAry.add(trapayObj);

					jsonOne = new JSONObject();
					payamtp = "";
					payamtd = "";
					jsonOne.put("tranum", StringUtil.nullToString(data.get("tranum")));
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("patradt", StringUtil.nullToString(data.get("patradt")));
					strPaysort = StringUtil.nullToString(data.get("paysort"));
					jsonOne.put("paysort", strPaysort);
					strPaycode = StringUtil.nullToString(data.get("paycode"));
					jsonOne.put("paycode", strPaycode);
					if ("P".equals(strPaycode.substring(1, 2)))
						payamtp = StringUtil.nullToString(data.get("payamt"));
					else if ("D".equals(strPaycode.substring(1, 2)))
						payamtd = StringUtil.nullToString(data.get("payamt"));
					jsonOne.put("payamtd", payamtd);
					jsonOne.put("payamtp", payamtp);
					jsonOne.put("payamt", StringUtil.nullToString(data.get("payamt")));
					jsonOne.put("payauth", StringUtil.nullToString(data.get("payauth")));
					jsonOne.put("payflag", StringUtil.nullToString(data.get("payflag")));
					jsonOne.put("payiftax", StringUtil.nullToString(data.get("payiftax")));
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					jsonOne.put("pacodnmc", StringUtil.nullToString(data.get("pacodnmc")));
					jsonOne.put("codenmc", strPaycode + "-" + StringUtil.nullToString(data.get("pacodnmc")));
					jsonOne.put("pacoddel", StringUtil.nullToString(data.get("pacoddel")));
					jsonOne.put("payreward", StringUtil.nullToString(data.get("payreward")));
					jsonOne.put("payupdtm", StringUtil.nullToString(data.get("payupdtm")) == "" ? ""
							: sdf.format(data.get("payupdtm")));
					if ("O".equals(strPaysort)) {
						oTtrapayRtn.add(jsonOne);
					} else if ("N".equals(strPaysort)) {
						nTtrapayRtn.add(jsonOne);
					}
					jsonRtn.add(jsonOne);
				}
			}
			rtnMap.put("trapay_json", jsonRtn);
			rtnMap.put("otrapay_json", oTtrapayRtn);
			rtnMap.put("ntrapay_json", nTtrapayRtn);
			end = new Date();
			log.info(me + "  ...********* dataaccess:" + (end.getTime() - start.getTime()));
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}

		return rtnMap;
	}

	// 依KEY(idtno),取得主機人事ps.psbasic
	public Pabaspsn getPSBasicById(String code_data, String userid, VGHDataAccess da, VGHDataAccess daDSN)
			throws Exception {
		String me = getClass().getName() + ".getBasPsnBykey(),userid = " + userid;
		log.info(me + " code_data = " + code_data + " hid = " + hid);
		CommDAO cDAO = null;
		PsDSNDAO psDAO = null;
		HashMap deptMap = new HashMap(); // 單位 對照表
		HashMap jtMap = new HashMap(); // 職稱 對照表
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_code_data = null;
		Pabaspsn pabaspsn = null;
		Psbasic psbasic = null;

		Date start = new Date();
		Date end = new Date();
		String strSelect = "";
		String idtno = "";
		String cardno = "";
		long starttime = 0;
		String s_empno = "";// 主機_職務編號
		String s_wkcode = "";// 主機_單位
		String s_titcod = "";// 主機_職稱
		String s_pagrad = "";// 主機_俸級
		String s_porank = "";// 主機_官職等
		String s_papont = "";// 主機_俸點
		String a_empno = "";// 薪資_職務編號
		String a_wkcode = "";// 薪資_單位
		Padept a_deptobj = null;// 薪資_單位obj
		Patitle a_titleobj = null;// 薪資_職稱obj
		String a_dept = "";// 薪資_單位
		String a_title = "";// 薪資_職稱
		String a_rank = "";// 薪資_階別
		String a_rnk2n = "";// 薪資_職等
		int a_rank3 = 0;// 薪資_本功
		int a_degr2 = 0;// 薪資_級別
		int a_degre = 0;// 薪資_俸點
		HashMap degrMap = new HashMap(); // 人事-薪資 級數對照
		ArrayList<Object> params = new ArrayList<Object>();

		if (code_data != null && !code_data.equals("")) {
			object_code_data = JSONObject.fromObject(code_data);
			idtno = (!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			cardno = (!object_code_data.has("cardno")) ? "" : object_code_data.getString("cardno");
			hid = (!object_code_data.has("hid")) ? "" : object_code_data.getString("hid");
		}
		try {
			starttime = System.currentTimeMillis();
			if (daDSN == null) {
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSource = hospInfo.dataSourceDSN;
				daDSN = new VGHDataAccess(g_dataSource);
				connFlg = true;
			}
			// 取得單位map,key=B人事單位代碼
			cDAO = new CommDAO(da, hid);
			deptMap = cDAO.getDeptMap("B", hid, da);

			// 取得職稱map,key=人事單位代碼
			jtMap = cDAO.getTitleMap("B", hid, da);

			psDAO = new PsDSNDAO(daDSN, hid);
			psbasic = psDAO.getPsBasById(idtno, hid, daDSN);
			if (psbasic != null) {
				pabaspsn = new Pabaspsn();
				pabaspsn.setHid(hid);
				pabaspsn.setPayidno(psbasic.getPbidtno());
				pabaspsn.setPaycard(StringUtil.nullToString(psbasic.getPbcardno()));
				pabaspsn.setPaynamec(StringUtil.trimWideEmpty(psbasic.getPbnamefd()));
				if ("9999-12-31".equals(psbasic.getPbgocdat().toString()))
					pabaspsn.setPaygovdt(psbasic.getPbgoadat());// 初任公職日,空值改抓到院日
				else
					pabaspsn.setPaygovdt(psbasic.getPbgocdat());// 初任公職日
				pabaspsn.setPayempdt(psbasic.getPbgoadat());// 到院日
				pabaspsn.setPaysgndt(psbasic.getPbposdat());// 現職日
				pabaspsn.setPayrsgdt(java.sql.Date.valueOf("9999-12-31"));// 離職日
				pabaspsn.setPayaddrs(StringUtil.trimWideEmpty(psbasic.getPbdomadr()));// 戶籍地址
				pabaspsn.setPaydpno("1");// 健保口數
				// 職號取首2碼
				s_empno = "";
				a_empno = "";
				// DA行政公務員 DW醫療公務員 DC聘用住院醫師 DE契約住院醫師 DF不分科住院醫師 DJ契主治
				// 不列入--DG技工 DH工友 ET研究助理 EB駐警 EC全時支援(不是醫院的人)
				// 編碼不一致,設[空白]--EA借調 DK契約
				s_empno = StringUtil.nullToString(psbasic.getPbposino());
				if (!"".equals(s_empno) && s_empno.length() > 2) {
					s_empno = s_empno.substring(0, 2);
				}
				if (s_empno.matches("DA|DW|DC|DE|DF|DJ"))
					a_empno = psbasic.getPbposino().substring(1, 8); // 取第2碼-第8碼
				else if (s_empno.matches("DK")) {
					if (hid.matches("2A0|3A0"))
						a_empno = psbasic.getPbposino();
				}
				pabaspsn.setPayempno(a_empno);// 職號
				// 單位,先以全碼找,找不到,改用首2碼+00找
				s_wkcode = psbasic.getPbwkcode();
				a_dept = "";
				a_deptobj = (Padept) deptMap.get(s_wkcode);
				if (a_deptobj != null) {
					a_dept = a_deptobj.getPadepcod();
				} else {
					s_wkcode = s_wkcode.substring(0, 2) + "00";
					a_deptobj = (Padept) deptMap.get(s_wkcode);
					if (a_deptobj != null) {
						a_dept = a_deptobj.getPadepcod();
					}
				}
				pabaspsn.setPaydept(a_dept);
				// 職稱
				s_titcod = StringUtil.nullToString(psbasic.getPbtitcod());
				// 人事職稱無法對應薪資職稱,強制改
				if (s_titcod.equals("A236"))
					s_titcod = "A245";

				a_title = "";
				a_titleobj = (Patitle) jtMap.get(s_titcod);
				if (a_titleobj != null) {
					a_title = a_titleobj.getPaytitlk();
				}
				pabaspsn.setPaytitle(a_title);
				// 官職等
				s_porank = StringUtil.nullToString(psbasic.getPbporank());
				// 俸級
				s_pagrad = psbasic.getPbpagrad();
				if (s_empno.equals("DC")) { // 聘用住院醫師
					a_rank = "E";
					a_degr2 = Integer.parseInt(s_pagrad);
					if (StringUtil.nullToString(psbasic.getPbpapont()) != "")
						a_degre = Integer.parseInt(psbasic.getPbpapont());
				}
				// DA行政公務員 DW醫療公務員 DE契約住院醫師 DJ契主治 DK契約
				else if (s_empno.matches("DA|DW|DE|DJ|DK")) {
					degrMap = cDAO.getDegrBygrade(s_porank, s_pagrad, s_titcod, s_empno, hid);
					a_rank = (String) degrMap.get("rank");
					a_rnk2n = (String) degrMap.get("rnk2n");
					a_rank3 = (Integer) degrMap.get("rank3");
					a_degr2 = (Integer) degrMap.get("degr2");
					//System.out.println("papont:" + StringUtil.nullToString(psbasic.getPbpapont()) + "aa");
					if (!"".equals(StringUtil.nullToString(psbasic.getPbpapont())))
						a_degre = Integer.parseInt(psbasic.getPbpapont());

				}
				pabaspsn.setPayrank(a_rank); // 薪資_階別
				pabaspsn.setPayrnk2n(a_rnk2n); // 薪資_職等
				pabaspsn.setPayrank3(a_rank3); // 薪資_本功
				pabaspsn.setPaydegr2(a_degr2); // 薪資_級別
				pabaspsn.setPaydegre(a_degre); // 薪資_俸點
			}
			log.info(me + "  ...********* dataaccess:共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
			cDAO = null;
		}
		return pabaspsn;
	}	
	/**
	 * 異動檔處理 selCode:selNm:selEn 1:[匯入]晉級調薪:ImpPromoInTra  
	 * 1[匯入]晉級調薪ImpPromoInTra,2[刪除]晉級調薪DelPromoInTra,
	 * ex.2026.9.1發薪,找基本檔2026年8月晉級,高榮滿年晉級依[到職月份];屏榮每年1月晉,依[到職日],寫入異動個人.現職日+1year;聯醫不定期,不處理
	 * ---月尾薪,抓基本檔,要延後一個月,找出月份 
	 * ---月頭薪,抓基本檔,找出月份 ex.2026.9.1發薪,找基本檔2026年9月晉級,高榮滿年晉級依[到職月份]
	 * 3[匯入]待遇調整ImpRemuInTra 4[刪除]待遇調整DelRemuInTra
	 * 
	 **/
	public Map<String, Object> handelTran(String in_data, String userid, String hid, VGHDataAccess da, VGHDataAccess daDSN)
			throws Exception {
		String me = getClass().getName() + ".handelTran(),userid = " + userid;
		log.info(me + ",in_data = " + in_data + ",userid = " + userid + ",hid = " + hid);
		AmJsonConfig cfg = new AmJsonConfig();
		StringBuilder msg = new StringBuilder();
		String rtn = "";
		ResultSet rs = null;
		CommDAO cDAO = null;
		PsDSNDAO psDAO = null;
		String selEn = ""; // 處理項目
		String selNm = ""; // 處理項目中文名稱
		ArrayList<Object> batch_sql = new ArrayList<Object>(); // batch_update sql 資料
		int delCnt = 0;
		long starttime = 0;
		long starttime9 = 0;
		String patradt = ""; // 發薪日
		HashMap psContLimMap = null; // 高榮人事契約限制級數map 屏榮護理單位		
		HashMap jtrngMap = null; // 職稱級數map
		HashMap nurrngMap = null; // 屏榮職稱級數map
		String l_Card = ""; // 卡號
		int l_DEGR2 = 0; 	// 薪資基本檔級數
		int l_SGNDT_DD = 0; // 現職日_日 2026-8-13 取13
		String l_contract = ""; // 契約月頭(舊制)N 月尾(新制)Y/空白
		java.sql.Date l_oldWorkend; // 月頭薪 工作月最後一天
		java.sql.Date l_newWorkend; // 月尾薪 工作月最後一天
		int old_thisMDay = 0; 	// 月頭薪 日數
		int new_thisMDay = 0; 	// 月尾薪 日數
		int l_effDD = 0;	 	// 有效日數
		int l_PAYDAYNO = 0;	 	// 補發日數
		int l_LastDay = 0; 		// 生效日期破月前日數

		String l_SGNDT_Str = ""; // 現職日Str
		java.sql.Date l_SGNDT = null; // 現職日Date
		java.sql.Date l_EFFDT = null; // 生效日期
		int intDEGR2Low = 0; // 級數下限
		int intDEGR2High = 0; // 級數上限
		int intDEGR2HighPS = 0; // 人事級數上限
		Pabaspsn baspsnobj = null; // 基本個人obj
		Pabaspay baspayObj = null; // 基本支扣obj
		Patrapsn trapsnobj = null; // 異動個人obj
		HashMap<String, Patrapsn> trapsnMap = new HashMap();// 異動個人檔 map
		HashMap<String, Patrapsn> trapsn11Map = new HashMap();// 異動個人檔 .考績晉級map
		HashMap<String, ArrayList<Patrapay>> trapayMap = new HashMap();// 異動支扣檔 map
		HashMap<String, ArrayList<Pabatpay>> batpayMap = new HashMap();// 批次檔 map
		HashMap<String, ArrayList<Pabaspay>> bpayMap = new HashMap();// 基本支扣檔 map(idno:paybaspay 集合)
		HashMap NSalMap = new HashMap();// 讀取新支扣
		ArrayList<Pabaspay> bpayAry = new ArrayList<Pabaspay>(); 	// baspay支扣集合
		ArrayList<Patrapay> tpayAry = new ArrayList<Patrapay>(); 	// trapay支扣集合
		ArrayList<Pabatpay> batAry = new ArrayList<Pabatpay>(); 	// batpay支扣集合
		int cnt = 0;
		HashMap dataMap = null;
		int intTraNum_max = 0;
		String strKey = "";
		String preKey = "";
		ArrayList<Object> paramA = new ArrayList<Object>();
		boolean tmpflag = true;
		int l_PAYEXPFKEY1=0,l_PAYEXPFKEY2=0,l_PAYEXPFKEY3=0;
		int l_PAYPFSP1=0,l_PAYPFSP2=0,l_PAYPFSP3=0;
		int l_Paydegr2=0;	//新級別
		int l_Insuamt=0;	//新投保級距
		int l_FD020=0,l_FD030=0;
		String effdt = ""; // 生效日
		String rangeNm="",rangeEn="";//範圍 rangeNm:rangeEn 公保正式人員:Remu_1
		String sqlRang="",sqlBasPsn_Remu="",sqlBasPay_Remu="",sqlTraPsn_Remu="",sqlTraPay_Remu_Del="",sqlBatPay_Remu_Del="",sqlTraPsn_Remu_Del="";
		int monno=0;	//補發月數
		String sqltmp="";
		//sqltmp=" and paycard in ('A711','3007') ";
		
		// 找異動檔.tranum最大值
		String sqlTranum_max = "select max(TRANUM) as MAX_TRANUM from " + this.tTraPsn + " where HID= ? with ur";

		// 晉級調薪_找異動檔 
		String sqlTran_31 = "select PAYCARD from " + this.tTraPsn
						  + " where year(PATRADT)=? and month(PATRADT)=? and PATRACOD=31 and PAYRANK in('E','F') and HID=? with ur";
		// 晉級調薪_找基本檔符合晉級條件_高榮,月尾+月頭(含聘用) 
		
		String sqlBasAdj_1A0 = "select PAYTITLP,A.* " + " from " + this.tBasPsn + " A " + 
								" LEFT OUTER JOIN "+ this.tTitle + " B ON A.PAYTITLE = B.PAYTITLK and a.hid=b.hid "+ 
				               " where (PAYRSGDT IS NULL or PAYRSGDT='9999-12-31') and A.PATRACOD NOT IN (23, 24)  "+
				                 " and A.PAYRANK in ('F' )  and MONTH(A.PAYSGNDT) = ? and a.HID= ?  "+
				                 " and (A.PAYEMPDT >= ? AND NOT B.PAYTITLP LIKE '%醫師%') " + sqltmp + 
				               " union " + 
				               "select PAYTITLP,A.* "+
				                " from " + this.tBasPsn + " A " + 
				                " LEFT OUTER JOIN " + this.tTitle+ " B ON A.PAYTITLE = B.PAYTITLK and a.hid=b.hid "+
				               " where (PAYRSGDT IS NULL or PAYRSGDT='9999-12-31') and A.PATRACOD NOT IN (23, 24) "+
				                 " and A.PAYRANK in ( 'E','F' )  and MONTH(A.PAYSGNDT) = ? and a.HID= ?  "+
				                 " AND (A.PAYEMPDT < ? or B.PAYTITLP LIKE '%醫師%') AND a.paytitle NOT IN ('CG') "+sqltmp +
				               " order by PAYCARD with ur ";
		String sqlBasAdj_2A0 = "select PAYTITLP,A.* " + " from " + this.tBasPsn + " A " 
							  + " LEFT OUTER JOIN "+ this.tTitle + " B ON A.PAYTITLE = B.PAYTITLK and a.hid=b.hid "+
							   " where (PAYRSGDT IS NULL or PAYRSGDT='9999-12-31') and A.PATRACOD NOT IN (23, 24) "+
							   	 " and A.PAYRANK in ('F') and year(a.PAYSGNDT)=year(CURRENT DATE) and month(a.PAYSGNDT)= ? and a.HID= ? "+
				                 " and not B.PAYTITLP LIKE '%醫師%' AND a.PAYSGNDT > ? " + " order by PAYCARD with ur ";
		// 晉級調薪_找基本檔.在職.payrank in ('E','F')
		String sqlBasPay_31 = "select A.PAYIDNO, PAYCODE, PAYAMT, PAYAUTH, PAYIFTAX, a.HID " + 
						     "from " + this.tBasPay+ " A " + 
				           " inner join " + this.tBasPsn + " B on A.PAYIDNO=B.PAYIDNO and A.HID=B.HID and B.PAYRANK in ('E','F') and (PAYRSGDT is null or PAYRSGDT='9999-12-31') "
				         + " where A.HID=? order by A.PAYIDNO, PAYCODE with ur";
		// 晉級調薪_刪除異動檔.支扣明細payrank in ('E','F')
		String sqlTraPay_31_Del = "delete " + this.tTraPay+
				                  " where patradt=? and hid=? and (patradt, payidno, hid) IN (" + 
				                	"    SELECT patradt, payidno, hid "+
				                      "    FROM " + this.tTraPsn+
				                     "    WHERE patradt = ? and patracod = 31 and PAYRANK in ('E','F') and hid = ? ) ";
		// 晉級調薪_刪除異動檔.個人明細.payrank in ('E','F')
		String sqlTraPsn_31_Del = "delete " + this.tTraPsn
								+ " where patradt = ? and patracod = 31 and PAYRANK in ('E','F') and hid = ?  ";
		// 待遇調整_找11考績晉級
		String sqlTraPsn_11 = "select PATRACOD, PATRADT, PAEFFDT, SALNUM, PAYLVEFG, PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, PAYEMPDT, PAYSGNDT, PAYRSGDT, PAYBKID, PAYACNO, PAYGEDNO, PAYINFLG, PAYDPNO, PAYPFLAG, PAYTITLE, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, PAYPFDOC, PACSFLAG, PAYPFSP1, PAYPFSP2, PAYPFSP3, PAYMONNO, PAYDAYNO, PAOTITLE, PAORNK2N, PAORANK3, PAODEGR2, PAODEGRE, PAORANK, PAORANK2, PAORANKC, PAOPFDOC, PAOCSFLA, PAOPFSP1, PAOPFSP2, PAOPFSP3, PAYTAXNO, PAYUPDID, PAYUPDTM, PAYFLAG, TRANUM, UPMASTDT, PAYPSW, PAOEXPFKEY1, PAOEXPFKEY2, PAOEXPFKEY3, PAYEXPFKEY1, PAYEXPFKEY2, PAYEXPFKEY3, PAORANK4, PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, INSUAMT, HID " + 
						        "from " + this.tTraPsn+ 
				              " where HID=? and PATRADT=? and PATRACOD IN (11) with ur";
		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			da.setAutoCommit(false);
			cDAO = new CommDAO(da, hid);

			JSONObject object_in_data = JSONObject.fromObject(in_data);
			selEn = StringUtil.nullToString(object_in_data.getString("selEn"));
			starttime = System.currentTimeMillis();
			// [匯入]晉級調薪
			if ("ImpPromoInTra".equals(selEn)) {
				
				patradt = StringUtil.nullToString(object_in_data.getString("patradt"));
				String yearStr = patradt.substring(0, 4);
				String monthStr = patradt.substring(5, 7);
				int intyear = Integer.parseInt(yearStr);
				int intMonth = Integer.parseInt(monthStr);
				Calendar calendar = Calendar.getInstance();
				// 月頭,月尾天數設定
				// 月頭薪 工作月最後一天=2025.7.31 發薪日 2025.7.1 舊制(月頭,發放2025.7)本月日數=31
				// 發薪日 2026.9.1 舊制(月頭,發放2026.9)本月日數=30
				calendar.setTime(DateUtil.StringToDate(patradt));
				calendar.add(Calendar.MONTH, 1);
				calendar.add(Calendar.DATE, -1);
				l_oldWorkend = java.sql.Date.valueOf(formatter.format(calendar.getTime()));
				old_thisMDay = calendar.get(Calendar.DATE);
				// 月尾薪 工作月最後一天=2026.8.31 發薪日 2025.9 日數=31
				calendar.setTime(DateUtil.StringToDate(patradt));
				calendar.add(Calendar.DATE, -1);
				l_newWorkend = java.sql.Date.valueOf(formatter.format(calendar.getTime()));
				new_thisMDay = calendar.get(Calendar.DATE);
				// 查詢_依階別+發薪日期+異動碼=37
				paramA.add(intyear);
				paramA.add(intMonth);
				paramA.add(hid);
				log.info(me + "  1.查詢_依階別(E,F)+發薪日期+異動碼=31" + sqlTran_31 + "...params:" + paramA.toString());
				rs = da.query(sqlTran_31, paramA);

				while (rs.next()) {
					if (rs.getString("PAYCARD") != null) {
						msg.append("已有" + patradt + "調薪資料,請確認");
						tmpflag = false;
					}
				}
				if (tmpflag) {
					// 月尾薪,抓基本檔,要延後一個月,找出月份
					// ex.2026.9.1發薪,找基本檔2026年8月晉級,高榮滿年晉級依[到職月份],屏榮每年1月晉,依[到職日],寫入異動個人.現職日+1year
					String strNewEff = formatter.format(calendar.getTime());
					int intNewM = 1 + calendar.get(Calendar.MONTH);
					//System.out.println("發薪日:" + patradt + " 月尾薪: " + strNewEff + " 月份:" + intNewM);

					// 找契約限制級數名單
					if (hid.equals("1A0")) {
						log.info(me + "  2.找1A0契約限制級數名單");
						psDAO = new PsDSNDAO(daDSN, hid);
						psContLimMap = psDAO.getContLimMap(daDSN);
					}else if (hid.equals("2A0")) {
						log.info(me + "  2.找2A0契約護理單位限制級數名單");
						psDAO = new PsDSNDAO(daDSN, hid);
						psContLimMap = psDAO.getNurContLimMap(hid,daDSN);
					}

					// 找異動檔.tranum最大值,要加1
					paramA = new ArrayList<Object>();
					paramA.add(hid);
					log.info(me + "  " + sqlTranum_max + "...params:" + paramA.toString());
					rs = da.query(sqlTranum_max, paramA);
					while (rs.next()) {
						intTraNum_max = rs.getInt("MAX_TRANUM");
					}
					log.info(me + "  TraNum_max:" + intTraNum_max);

					// 找基本支扣明細檔符合payrank(E,F)條件
					starttime9 = System.currentTimeMillis();
					params = new ArrayList<Object>();
					params.add(hid);
					log.info(me + " 2-1.找基本支扣明細檔符合payrank(E,F)條件:" + sqlBasPay_31 + "...params:" + params.toString());
					result = da.arraylistbyQuery(sqlBasPay_31, params, META_TYPE);
					log.info(me + "...size:" + result.size());
					if (result.size() > 0) {
						bpayAry = new ArrayList<Pabaspay>();
						for (Map<String, Object> data : result) {
							// 放置在Arraylist
							baspayObj = new Pabaspay();
							PropertyUtils.copyProperties(baspayObj, data);
							strKey = StringUtil.nullToString(baspayObj.getPayidno());
							if ((!"".equals(preKey) && !strKey.equals(preKey))) {
								bpayMap.put(preKey, bpayAry);
								bpayAry = new ArrayList<Pabaspay>();
							}
							bpayAry.add(baspayObj);
							preKey = strKey;
						}
						bpayMap.put(preKey, bpayAry);
					}
					log.info(me + "  2-1.找基本支扣  共花費：" + (System.currentTimeMillis() - starttime9) + "毫秒");

					// 找基本檔符合晉級條件
					paramA = new ArrayList<Object>();
					if (hid.equals("1A0")) {
						sql = sqlBasAdj_1A0;
						paramA.add(intNewM);
						paramA.add(hid);
						paramA.add(Globals.gNcontract);
						paramA.add(intMonth);
						paramA.add(hid);
						paramA.add(Globals.gNcontract);
					} else if (hid.equals("2A0")) {
						sql = sqlBasAdj_2A0;
						paramA.add(intNewM);
						paramA.add(hid);
						paramA.add(Globals.gNcontract_2A0);
					}
					log.info(me + "  2-2.找基本檔符合晉級條件 " + sql + "...params:" + paramA.toString());
					result = da.arraylistbyQuery(sql, paramA, META_TYPE);
					log.info(me + "  2-2.找基本檔符合晉級條件,size: " + result.size());
					if (result.size() > 0) {
						for (Map<String, Object> data : result) {
							baspsnobj = new Pabaspsn();
							tpayAry = null;

							PropertyUtils.copyProperties(baspsnobj, data);
							l_DEGR2 = baspsnobj.getPaydegr2();
							l_Card = StringUtil.nullToString(baspsnobj.getPaycard());
							l_contract = baspsnobj.getContract();
							// 找出級數範圍
							jtrngMap = PaCommUtil.getDegre2Rng(baspsnobj.getPaytitle(), hid);
							intDEGR2Low = (int) jtrngMap.get("min");
							intDEGR2High = (int) jtrngMap.get("max");
							intDEGR2HighPS = 0;
							if (psContLimMap != null) {
								if (hid.equals("1A0")) {//抓級數上限
									if (psContLimMap.containsKey(l_Card)) {
										intDEGR2HighPS = (int) psContLimMap.get(l_Card);
									}
									if (intDEGR2HighPS != 0) {
										intDEGR2High = intDEGR2HighPS;
									}
								}else if (hid.equals("2A0")) {//找護理單位,級數範圍
									if (psContLimMap.containsKey(l_Card)) {
										nurrngMap = (HashMap) psContLimMap.get(l_Card);
									}
									if (nurrngMap != null) {
										intDEGR2High = (int) nurrngMap.get("max");
										intDEGR2Low = (int) jtrngMap.get("min");
									}
								}
							}
							// 目前級數<最高級數,才要寫入異動檔
							if (l_DEGR2 < intDEGR2High) {
								l_SGNDT_Str = formatter.format(baspsnobj.getPaysgndt());
								if (l_contract.equals("Y")) { // 新制/月尾
									// 補發日數
									l_effDD = new_thisMDay;
									// 生效日
									calendar.set(Integer.parseInt(strNewEff.substring(0, 4)),
											Integer.parseInt(strNewEff.substring(5, 7)) - 1,
											Integer.parseInt(l_SGNDT_Str.substring(8, 10)));
									l_EFFDT = new java.sql.Date(calendar.getTimeInMillis());
								} else {
									l_effDD = old_thisMDay; // 舊制/月頭 補發日數

									calendar.set(Integer.parseInt(patradt.substring(0, 4)),
											Integer.parseInt(patradt.substring(5, 7)) - 1,
											Integer.parseInt(l_SGNDT_Str.substring(8, 10)));
									l_EFFDT = new java.sql.Date(calendar.getTimeInMillis());
								}
								// 屏榮現職日=現職日+1year
								if (hid.equals("2A0")) {
									// 生效日
									calendar.set(Integer.parseInt(l_SGNDT_Str.substring(0, 4)) + 1,
											Integer.parseInt(l_SGNDT_Str.substring(5, 7)) - 1,
											Integer.parseInt(l_SGNDT_Str.substring(8, 10)));
									l_SGNDT = new java.sql.Date(calendar.getTimeInMillis());
								} else if (hid.equals("1A0")) {
									l_SGNDT = baspsnobj.getPaysgndt();
								}

								// 上一段日數
								calendar.setTime(baspsnobj.getPaysgndt());
								l_SGNDT_DD = calendar.get(Calendar.DATE);
								l_PAYDAYNO = l_effDD - l_SGNDT_DD + 1;
								l_LastDay = l_effDD - l_PAYDAYNO;
								// 新 用hashmap存值
								// hid醫院別 tradt發薪日 tracd異動碼 effdt生效 monno補發月數 dayno:補發日數
								// title職稱代碼 rnk2n職等 rank3本功別 degr2級別 degre俸點 rank階別代碼rank2職等(權理) rankc主管(權理) rank4適用專業加給表
								// 聘住,有額外加點 1.人才羅致困難科(7),第5年,任職滿年(5),加10點 2.非人才羅致困難科,任職滿年加10點,最多40點
								// CK不分科住院醫師_有證書第2年加97點
								l_PAYEXPFKEY1=baspsnobj.getPayexpfkey1();
								l_PAYEXPFKEY2=baspsnobj.getPayexpfkey2();
								l_PAYEXPFKEY3=baspsnobj.getPayexpfkey3();
								l_PAYPFSP1   =baspsnobj.getPaypfsp1();
								l_PAYPFSP2   =baspsnobj.getPaypfsp2();
								l_PAYPFSP3   =baspsnobj.getPaypfsp3();
								//新級數
								l_Paydegr2   =baspsnobj.getPaydegr2()+1;
								if (baspsnobj.getPacsflag().equals("Y") && hid.equals("1A0") ) {
									if ( baspsnobj.getPayrank().equals("E") || (baspsnobj.getPayrank().equals("F") && baspsnobj.getPaytitle().equals("CH")) ) {
										if (baspsnobj.getPayexpfkey1()==7 || baspsnobj.getPayexpfkey2()==7 || baspsnobj.getPayexpfkey3()==7) {
											if (l_Paydegr2 == 5) {
												if (baspsnobj.getPayexpfkey1() == 0) {
													l_PAYEXPFKEY1 = 5;
						                            l_PAYPFSP1 = 10;
												}else if (baspsnobj.getPayexpfkey2() == 0) {
													l_PAYEXPFKEY2 = 5;
						                            l_PAYPFSP2 = 10;
												}else if (baspsnobj.getPayexpfkey3() == 0) {
													l_PAYEXPFKEY3 = 5;
						                            l_PAYPFSP3 = 10;
												}
											}
										}else {
											if (l_Paydegr2 == 2) {
												if (baspsnobj.getPayexpfkey1() == 0) {
													l_PAYEXPFKEY1 = 5;
						                            l_PAYPFSP1 = 10;
												}else if (baspsnobj.getPayexpfkey2() == 0) {
													l_PAYEXPFKEY2 = 5;
						                            l_PAYPFSP2 = 10;
												}else if (baspsnobj.getPayexpfkey3() == 0) {
													l_PAYEXPFKEY3 = 5;
						                            l_PAYPFSP3 = 10;
												}
											}else if( l_Paydegr2 >=3  && l_Paydegr2 <= 5 ) {
												if (baspsnobj.getPayexpfkey1() == 5) 
													l_PAYPFSP1 = (l_Paydegr2 -1 ) * 10;
												else if (baspsnobj.getPayexpfkey2() == 5) 													
						                            l_PAYPFSP2 = (l_Paydegr2 -1 ) * 10;
												else if (baspsnobj.getPayexpfkey3() == 5)
						                            l_PAYPFSP3 = (l_Paydegr2 -1 ) * 10;
											}
										}
									}else if ( baspsnobj.getPayrank().equals("F") && baspsnobj.getPaytitle().equals("CK")) {
										if (baspsnobj.getPaydegr2()==2) {
											if (baspsnobj.getPayexpfkey1() == 0) {
												l_PAYEXPFKEY1 = 5;
					                            l_PAYPFSP1 = 97;
											}else if (baspsnobj.getPayexpfkey2() == 0) {
												l_PAYEXPFKEY2 = 5;
					                            l_PAYPFSP2 = 97;
											}else if (baspsnobj.getPayexpfkey3() == 0) {
												l_PAYEXPFKEY3 = 5;
					                            l_PAYPFSP3 = 97;
											}
										}
									}
								}
								// 異動支扣--新
								// 從新支扣取得,寫入tpayAry->trapayMap(id,tpayAry)
								dataMap = new HashMap();
								dataMap.put("hid", hid);
								dataMap.put("tradt", patradt);
								dataMap.put("tracd", 31);
								dataMap.put("effdt", formatter.format(l_EFFDT));
								dataMap.put("sgndt", formatter.format(l_SGNDT));
								dataMap.put("monno", 0);
								dataMap.put("dayno", l_PAYDAYNO);
								dataMap.put("title", baspsnobj.getPaytitle());
								dataMap.put("rnk2n", baspsnobj.getPayrnk2n());
								dataMap.put("rank3", baspsnobj.getPayrank3());
								dataMap.put("degr2", l_Paydegr2);
								dataMap.put("degre", baspsnobj.getPaydegre());
								dataMap.put("rank",  baspsnobj.getPayrank());
								dataMap.put("rank2", baspsnobj.getPayrank2());
								dataMap.put("rankc", baspsnobj.getPayrankc());
								dataMap.put("rank4", baspsnobj.getPayrank4());
								dataMap.put("expfkey1", l_PAYEXPFKEY1);
								dataMap.put("expfkey2", l_PAYEXPFKEY2);
								dataMap.put("expfkey3", l_PAYEXPFKEY3);
								dataMap.put("pfsp1", l_PAYPFSP1);
								dataMap.put("pfsp2", l_PAYPFSP2);
								dataMap.put("pfsp3", l_PAYPFSP3);
																
								intTraNum_max++;		// 異動檔.tranum最大值,要加1

								// 寫入異動個人
								trapsnobj = new Patrapsn();
								trapsnobj = putTraPsnObj(baspsnobj, dataMap, intTraNum_max, userid);
								
								// 異動支扣--原
								dataMap = new HashMap();
								dataMap.put("hid", hid);
								dataMap.put("tradt", patradt);
								dataMap.put("sort", "O");
								// 每筆取得基本支扣ary,放入tpayAry,再放入map(id,tpayAry)
								bpayAry = bpayMap.get(baspsnobj.getPayidno());
								if (bpayAry != null) {
									l_FD020=0;
									l_FD030=0;
									for (Pabaspay basobj : bpayAry) {
										//原支(FD020健保,FD030勞保)
										if (basobj.getPaycode().equals("FD020")) {
											l_FD020 =basobj.getPayamt();
										}
										if (basobj.getPaycode().equals("FD030")) {
											l_FD030 =basobj.getPayamt();
										}
									}
									
									tpayAry = putTraPayAry(bpayAry, dataMap, intTraNum_max, userid);
								}
								// 異動支扣--新
								// 從新支扣取得,寫入tpayAry->trapayMap(id,tpayAry)
								NSalMap = cDAO.getDegreByRank("", "B", trapsnobj, tpayAry, l_LastDay, hid, da, null);
															
								JSONArray NSalAry = (JSONArray) NSalMap.get("payitm_json");
								dataMap = new HashMap();
								dataMap.put("hid", hid);
								dataMap.put("tradt", patradt);
								dataMap.put("sort", "N");
								dataMap.put("payidno", baspsnobj.getPayidno());
								tpayAry = putTraPayJson(NSalAry, dataMap, intTraNum_max, tpayAry);
								/*//把原支(FD020健保,FD030勞保) 寫回新支,因user要一致,不重算
								if (tpayAry != null) {
									for (Patrapay traobj : tpayAry) {
										
										if (traobj.getPaycode().equals("FD020")) {
											traobj.setPayamt(l_FD020);
										}
										if (traobj.getPaycode().equals("FD030")) {
											traobj.setPayamtl_FD030);
										}
									}
									
								}*/
								trapayMap.put(baspsnobj.getPayidno(), tpayAry);
								//更新投保額
								l_Insuamt=(int) NSalMap.get("insu_dpin"); 	//新投保級距
								if ( l_Insuamt >0) {
									trapsnobj.setInsuamt(l_Insuamt);
								}
								//寫入異動個人
								trapsnMap.put(baspsnobj.getPayidno(), trapsnobj);
								
								log.info(me + "  3.晉級名單: " + cnt++ + " " + baspsnobj.getPaycard() + " 月頭N/月尾Y:"
										+ l_contract + " 級數:" + l_DEGR2 + " 範圍:" + intDEGR2Low + "-" + intDEGR2High
										+ " 人事限制:" + intDEGR2HighPS + " 有效日數:" + l_effDD + " 補發日數:" + l_PAYDAYNO
										+ " 前一段日數:" + l_LastDay + " 生效日:" + formatter.format(l_EFFDT));
							}
						}
						// 寫入異動檔
						starttime9 = System.currentTimeMillis();
						rtn = updTraTblMap(trapsnMap, trapayMap, batpayMap, hid, da);
						if ( !rtn.equals("") ) {
							tmpflag=false;
							msg.append(rtn);
						}
						msg.append("寫入筆數:"+ cnt +" 共花費：" + (System.currentTimeMillis() - starttime9) + "毫秒");
						log.info(me + "  4.寫入晉級調薪,筆數:"+ cnt +" 共花費：" + (System.currentTimeMillis() - starttime9) + "毫秒");
					}
				}
			}//if ("ImpPromoInTra".equals(selEn)) {
			// [刪除]晉級調薪
			else if ("DelPromoInTra".equals(selEn)) {
				patradt = StringUtil.nullToString(object_in_data.getString("patradt"));
				// pa.patrapay
				paramA = new ArrayList<Object>();
				paramA.add(patradt);
				paramA.add(hid);
				paramA.add(patradt);
				paramA.add(hid);
				params.add(paramA);
				batch_sql.add(sqlTraPay_31_Del);
				// pa.patrapsn
				paramA = new ArrayList<Object>();
				paramA.add(patradt);
				paramA.add(hid);
				params.add(paramA);
				batch_sql.add(sqlTraPsn_31_Del);
				da.startTransaction();
				try {
					log.info(batch_sql + " " + batch_sql + " params:" + params);
					da.batchupdatenocommit(batch_sql, params);
					da.commit();
					da.setAutoCommit(false);
					batch_sql.clear();
					params.clear();
				} catch (Exception e) {
					da.rollback();
					rtn = " fail, " + e;
					msg.append(rtn);
					tmpflag=false;
					da.setAutoCommit(true);
					e.printStackTrace();
					log.info(me + " " + selNm + " " + e.toString());
				} finally {

				}
			}//else if ("DelPromoInTra".equals(selEn))
			// 3:[匯入]待遇調整
			else if ("ImpRemuInTra".equals(selEn)) {
				starttime = System.currentTimeMillis();
				effdt = StringUtil.nullToString(object_in_data.getString("effdt"));
				patradt = StringUtil.nullToString(object_in_data.getString("patradt"));
				rangeEn = StringUtil.nullToString(object_in_data.getString("rangeEn"));
				rangeNm = StringUtil.nullToString(object_in_data.getString("rangeNm"));
				monno   = object_in_data.getInt("monno");
				if (rangeEn.equals("Remu_1"))//1:公保正式人員:Remu_1
					sqlRang =" PAYRANK IN ('1','2','3','4','8','9','G','H') ";
				else if (rangeEn.equals("Remu_2"))//2:勞保正式人員:Remu_2
					sqlRang =" PAYRANK IN ('5','6') ";
				else if (rangeEn.equals("Remu_3"))//3:聘用住院醫師、聘實習醫師:Remu_3
					sqlRang =" PAYRANK IN ('A','B','E') ";
				else if (rangeEn.equals("Remu_4"))//4:契約人員_月頭:Remu_4
					sqlRang =" PAYRANK IN ('F') AND PAYTITLE NOT IN ('CG','CH','CI','CJ','CK') AND PAYEMPDT < '" + Globals.gNcontract + "' ";
				else if (rangeEn.equals("Remu_5"))//5:契約人員_月尾:Remu_5
					sqlRang =" PAYRANK IN ('F') AND PAYTITLE NOT IN ('CG','CH','CI','CJ','CK') AND PAYEMPDT >= '" + Globals.gNcontract + "' ";
				else if (rangeEn.equals("Remu_6"))//6:契約人員_住院醫師:Remu_6
					sqlRang =" PAYTITLE IN ('CH','CI','CJ','CK') ";
				else if (rangeEn.equals("Remu_7"))//7:契約人員_主治醫師:Remu_7
					sqlRang =" PAYTITLE IN ('CG')  ";
				else if (rangeEn.equals("Remu_8"))//8:屏榮契約_不含醫師,半日:Remu_8
					sqlRang =" PAYRANK IN ('F') AND PAYTITLE NOT IN ('CG','CH','CI','CJ','CK','DW') ";
				else if (rangeEn.equals("Remu_9"))//9:聯醫契約_不含半日:Remu_9
					sqlRang =" PAYRANK IN ('5','6') ";
				//step.1先找異動檔是否有 上述條件+ 待遇調整(35),若有,不用做處理.
				sqlTraPsn_Remu="select PATRADT,PAEFFDT,PAYIDNO,PAYCARD "+
								" from " + this.tTraPsn+  
							   " where HID=? and PATRADT=? and PATRACOD = 35 and "+sqlRang+" with ur";
				paramA.add(hid);
				paramA.add(patradt);
				
				log.info(me + "  1.查詢_依階別("+rangeNm+")+發薪日期+異動碼=35 " + sqlTraPsn_Remu + "...params:" + paramA.toString());
				rs = da.query(sqlTraPsn_Remu, paramA);
				while (rs.next()) {
					if (rs.getString("PAYCARD") != null) {
						rtn = "已有" + patradt + "待遇調整(35)資料,請確認";
						msg.append(rtn);
						tmpflag = false;
					}
				}
				
				if (tmpflag) {
					// 找異動檔.tranum最大值,要加1
					paramA = new ArrayList<Object>();
					paramA.add(hid);
					log.info(me + "  " + sqlTranum_max + "...params:" + paramA.toString());
					rs = da.query(sqlTranum_max, paramA);
					while (rs.next()) {
						intTraNum_max = rs.getInt("MAX_TRANUM");
					}
					log.info(me + "  2.TraNum_max:" + intTraNum_max);
					
					// 找基本支扣明細檔符合匯入範圍條件
					starttime9 = System.currentTimeMillis();					
					sqlBasPay_Remu = "select A.PAYIDNO, PAYCODE, PAYAMT, PAYAUTH, PAYIFTAX, a.HID " + 
									   "from " + this.tBasPay+ " A " + 
					                 " inner join (select PAYIDNO,HID from "+this.tBasPsn + " where HID=? and (PAYRSGDT is null or PAYRSGDT='9999-12-31') and "+sqlRang+" ) B on A.PAYIDNO=B.PAYIDNO and A.HID=B.HID "+
					                 " where A.HID=? order by A.PAYIDNO, PAYCODE with ur";
					paramA = new ArrayList<Object>();
					paramA.add(hid);
					paramA.add(hid);
					log.info(me + " 2-1.找基本支扣明細檔符合["+rangeNm+"]條件:" + sqlBasPay_Remu + "...params:" + paramA.toString());
					result = da.arraylistbyQuery(sqlBasPay_Remu, paramA, META_TYPE);
					log.info(me + "...size:" + result.size());
					if (result.size() > 0) {
						bpayAry = new ArrayList<Pabaspay>();
						for (Map<String, Object> data : result) {
							// 放置在Arraylist
							baspayObj = new Pabaspay();
							PropertyUtils.copyProperties(baspayObj, data);
							strKey = StringUtil.nullToString(baspayObj.getPayidno());
							if ((!"".equals(preKey) && !strKey.equals(preKey))) {
								bpayMap.put(preKey, bpayAry);
								bpayAry = new ArrayList<Pabaspay>();
							}
							bpayAry.add(baspayObj);
							preKey = strKey;
						}
						bpayMap.put(preKey, bpayAry);
					}
					log.info(me + "  2-1.找基本支扣 筆數:"+bpayMap.size()+"  共花費：" + (System.currentTimeMillis() - starttime9) + "毫秒");
					
					// 找異動檔.考績晉級
					starttime9 = System.currentTimeMillis();
					paramA = new ArrayList<Object>();
					paramA.add(hid);
					paramA.add(patradt);
					log.info(me + " 2-2.找異動檔.考績晉級:" + sqlTraPsn_11 + "...params:" + paramA.toString());
					result = da.arraylistbyQuery(sqlTraPsn_11, paramA, META_TYPE);
					log.info(me + "...size:" + result.size());
					if (result.size() > 0) {
						for (Map<String, Object> data : result) {
							trapsnobj = new Patrapsn();
							PropertyUtils.copyProperties(trapsnobj, data);
							strKey = StringUtil.nullToString(trapsnobj.getPayidno());
							trapsn11Map.put(strKey, trapsnobj);
						}
					}
					log.info(me + "  2-2.找異動檔.考績晉級  筆數:"+result.size()+" 共花費：" + (System.currentTimeMillis() - starttime9) + "毫秒");
					
					starttime9 = System.currentTimeMillis();
					// 待遇調整_找基本檔1.入列基本支扣有FP0,2.不列(23停薪,24逾期請假) 3.異動檔.發薪日,不列(31晉級調薪,21離職,2復職,23停薪,24逾期請假 11考績晉級)
					sqlBasPsn_Remu = "select PAYLVEFG, A.PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, PAYEMPDT, PAYSGNDT, PAYRSGDT, PAYBKID, PAYACNO, PAYGEDNO, PAYINFLG, PAYDPNO, PAYPSW, PAYTITLE, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, PAYPFDOC, PACSFLAG, PAYPFSP1, PAYPFSP2, PAYPFSP3, PATRACOD, PATRADT, PAYTAXNO, PAYUPDID, PAYUPDTM, PAYEXPFKEY1, PAYEXPFKEY2, PAYEXPFKEY3, PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, INSUAMT, PARETENTION, A.HID " + 
								       "from " + this.tBasPsn+ " A " + 
							          " inner join (SELECT payidno,hid FROM " + this.tBasPay+ " WHERE substr(paycode,1,3)='FP0' GROUP BY substr(paycode,1,3),payidno,hid ) b ON a.hid=b.hid AND a.payidno=b.payidno " +
							          " where (PAYRSGDT IS NULL or PAYRSGDT='9999-12-31') AND PATRACOD not in (23,24) AND a.HID=? AND "+sqlRang+
							          "   and (A.PAYIDNO,A.HID) not in (SELECT payidno,hid FROM " + this.tTraPsn+ " where PATRACOD IN (21,31,2,23,24,11) AND HID=? and PATRADT=? ) "+
							          sqltmp+
							          " ORDER BY PAYCARD with ur";
					paramA = new ArrayList<Object>();
					paramA.add(hid);
					paramA.add(hid);
					paramA.add(patradt);
					log.info(me + " 3.找基本個人明細檔符合條件:" + sqlBasPsn_Remu + "...params:" + paramA.toString());
					result = da.arraylistbyQuery(sqlBasPsn_Remu, paramA, META_TYPE);
					log.info(me + "...size:" + result.size());
					if (result.size() > 0) {
						for (Map<String, Object> data : result) {
							baspsnobj = new Pabaspsn();
							tpayAry = null;

							PropertyUtils.copyProperties(baspsnobj, data);
							strKey=baspsnobj.getPayidno();
							// 異動支扣--新
							// 從新支扣取得,寫入tpayAry->trapayMap(id,tpayAry)
							dataMap = new HashMap();
							dataMap.put("hid", hid);
							dataMap.put("tradt", patradt);
							dataMap.put("tracd", 35);
							dataMap.put("effdt", effdt);
							dataMap.put("sgndt", formatter.format(baspsnobj.getPaysgndt()));
							dataMap.put("monno", monno);
							dataMap.put("dayno", 0);
							dataMap.put("title", baspsnobj.getPaytitle());
							dataMap.put("rnk2n", baspsnobj.getPayrnk2n());
							dataMap.put("rank3", baspsnobj.getPayrank3());
							dataMap.put("degr2", baspsnobj.getPaydegr2());
							dataMap.put("degre", baspsnobj.getPaydegre());
							dataMap.put("rank",  baspsnobj.getPayrank());
							dataMap.put("rank2", baspsnobj.getPayrank2());
							dataMap.put("rankc", baspsnobj.getPayrankc());
							dataMap.put("rank4", baspsnobj.getPayrank4());
							dataMap.put("expfkey1", baspsnobj.getPayexpfkey1());
							dataMap.put("expfkey2", baspsnobj.getPayexpfkey2());
							dataMap.put("expfkey3", baspsnobj.getPayexpfkey3());
							dataMap.put("pfsp1", baspsnobj.getPaypfsp1());
							dataMap.put("pfsp2", baspsnobj.getPaypfsp1());
							dataMap.put("pfsp3", baspsnobj.getPaypfsp1());
							
							intTraNum_max++;		// 異動檔.tranum最大值,要加1
							
							// 異動個人
							trapsnobj = new Patrapsn();
							trapsnobj = putTraPsnObj(baspsnobj, dataMap, intTraNum_max, userid);
							
							// 異動支扣--原
							dataMap = new HashMap();
							dataMap.put("hid", hid);
							dataMap.put("tradt", patradt);
							dataMap.put("sort", "O");
							// 每筆取得基本支扣ary,放入tpayAry,再放入map(id,tpayAry)
							bpayAry = bpayMap.get(baspsnobj.getPayidno());
							if (bpayAry != null) {															
								tpayAry = putTraPayAry(bpayAry, dataMap, intTraNum_max, userid);
							}
							// 異動支扣--新
							// 從新支扣取得,寫入tpayAry->trapayMap(id,tpayAry)
							NSalMap = cDAO.getDegreByRank("", "B", trapsnobj, tpayAry, 0, hid, da, null);
							JSONArray NSalAry = (JSONArray) NSalMap.get("payitm_json");
							dataMap = new HashMap();
							dataMap.put("hid", hid);
							dataMap.put("tradt", patradt);
							dataMap.put("sort", "N");
							dataMap.put("payidno", baspsnobj.getPayidno());
							tpayAry = putTraPayJson(NSalAry, dataMap, intTraNum_max, tpayAry);
							trapayMap.put(baspsnobj.getPayidno(), tpayAry);
							//更新投保額
							l_Insuamt=(int) NSalMap.get("insu_dpin"); 	//新投保級距
							if ( l_Insuamt >0) {
								trapsnobj.setInsuamt(l_Insuamt);
							}
							//批次檔,不一定有,依情況寫入
							JSONArray batJAry = (JSONArray) NSalMap.get("bat_json");
							if (batJAry !=null) {
								dataMap = new HashMap();
								dataMap.put("hid", hid);
								dataMap.put("tradt", patradt);
								dataMap.put("tracod", "35");
								dataMap.put("salnum", 0);
								dataMap.put("payidno", baspsnobj.getPayidno());
								dataMap.put("paycard", baspsnobj.getPaycard());
								dataMap.put("userid", userid);
								batAry = putBatPayJson(batJAry, dataMap);
								batpayMap.put(baspsnobj.getPayidno(), batAry);
							}
							
							//寫入異動個人
							trapsnMap.put(baspsnobj.getPayidno(), trapsnobj);
							log.info(me + "  3.待遇調整: " + cnt++ + " " + baspsnobj.getPaycard() + " 投保額:"+baspsnobj.getInsuamt()+"->"+ l_Insuamt );
							
						}	
						// 寫入異動檔(patrapsn,patrapay,pabatpay)
						starttime9 = System.currentTimeMillis();
						rtn = updTraTblMap(trapsnMap, trapayMap, batpayMap,hid, da);
						if ( !rtn.equals("") ) {
							tmpflag=false;
							msg.append(rtn);
						}
						msg.append("寫入筆數:"+ cnt +" 共花費：" + (System.currentTimeMillis() - starttime9) + "毫秒");
						log.info(me + "  4.寫入待遇調整,筆數:"+ cnt +" 共花費：" + (System.currentTimeMillis() - starttime9) + "毫秒");						
					}
				}
				
			}//else if ("ImpRemuInTra".equals(selEn))				
			// 4:[刪除]待遇調整
			else if ("DelRemuInTra".equals(selEn)) {
				effdt = StringUtil.nullToString(object_in_data.getString("effdt"));
				patradt = StringUtil.nullToString(object_in_data.getString("patradt"));
				rangeEn = StringUtil.nullToString(object_in_data.getString("rangeEn"));
				rangeNm = StringUtil.nullToString(object_in_data.getString("rangeNm"));
				if (rangeEn.equals("Remu_1"))//1:公保正式人員:Remu_1
					sqlRang =" PAYRANK IN ('1','2','3','4','8','9','G','H') ";
				else if (rangeEn.equals("Remu_2"))//2:勞保正式人員:Remu_2
					sqlRang =" PAYRANK IN ('5','6') ";
				else if (rangeEn.equals("Remu_3"))//3:聘用住院醫師、聘實習醫師:Remu_3
					sqlRang =" PAYRANK IN ('A','B','E') ";
				else if (rangeEn.equals("Remu_4"))//4:契約人員_月頭:Remu_4
					sqlRang =" PAYRANK IN ('F') AND PAYTITLE NOT IN ('CG','CH','CI','CJ','CK') AND PAYEMPDT < '" + Globals.gNcontract + "' ";
				else if (rangeEn.equals("Remu_5"))//5:契約人員_月尾:Remu_5
					sqlRang =" PAYRANK IN ('F') AND PAYTITLE NOT IN ('CG','CH','CI','CJ','CK') AND PAYEMPDT >= '" + Globals.gNcontract + "' ";
				else if (rangeEn.equals("Remu_6"))//6:契約人員_住院醫師:Remu_6
					sqlRang =" PAYTITLE IN ('CH','CI','CJ','CK') ";
				else if (rangeEn.equals("Remu_7"))//7:契約人員_主治醫師:Remu_7
					sqlRang =" PAYTITLE IN ('CG')  ";
				else if (rangeEn.equals("Remu_8"))//8:屏榮契約_不含醫師,半日:Remu_8
					sqlRang =" PAYRANK IN ('F') AND PAYTITLE NOT IN ('CG','CH','CI','CJ','CK','DW') ";
				else if (rangeEn.equals("Remu_9"))//9:聯醫契約_不含半日:Remu_9
					sqlRang =" PAYRANK IN ('5','6') ";
				
				params = new ArrayList<Object>();
				// pa.patrapay
				sqlTraPay_Remu_Del="delete " + this.tTraPay+
		                           " where HID=? and PATRADT=? and (patradt, payidno, hid) IN (" + 
		                	   "    SELECT patradt, payidno, hid "+
		                         "    FROM " + this.tTraPsn+
		                        "    WHERE HID=? and PATRADT=? and patracod = 35 and "+sqlRang+"  ) ";
				batch_sql.add(sqlTraPay_Remu_Del);
				paramA = new ArrayList<Object>();
				paramA.add(hid);
				paramA.add(patradt);
				paramA.add(hid);
				paramA.add(patradt);
				params.add(paramA);
				
				// pa.pabatpay 
				sqlBatPay_Remu_Del="delete " + this.tBatPay+
		                           " where HID=? and PATRADT=? and substr(PADUTYID,1,2)='35' and (patradt, payidno, hid) IN (" + 
		                	   "    SELECT patradt, payidno, hid "+
		                         "    FROM " + this.tTraPsn+
		                        "    WHERE HID=? and PATRADT=? and patracod = 35 and "+sqlRang+"  ) ";
				batch_sql.add(sqlBatPay_Remu_Del);
				paramA = new ArrayList<Object>();
				paramA.add(hid);
				paramA.add(patradt);
				paramA.add(hid);
				paramA.add(patradt);
				params.add(paramA);
				
				// pa.patrapsn
				sqlTraPsn_Remu_Del="delete " + this.tTraPsn
								 + " where HID=? and PATRADT=? and patracod = 35 and "+sqlRang;
				batch_sql.add(sqlTraPsn_Remu_Del);
				paramA = new ArrayList<Object>();
				paramA.add(hid);
				paramA.add(patradt);				
				params.add(paramA);

				da.startTransaction();
				try {
					log.info(batch_sql + " " + batch_sql + " params:" + params);
					da.batchupdatenocommit(batch_sql, params);
					da.commit();
					da.setAutoCommit(false);
					batch_sql.clear();
					params.clear();
				} catch (Exception e) {
					da.rollback();
					rtn = " fail, selNm"+ selNm+ " "+ e;
					msg.append(rtn);
					tmpflag=false;
					da.setAutoCommit(true);
					e.printStackTrace();
					log.info(me + " " + selNm + " " + e.toString());
				} finally {

				}
			}//elseif ("DelRemuInTra".equals(selEn))
			
			log.info(me + "  ...********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Exception e) {
			rtn = " fail, " + e;
			msg.append(rtn);
			tmpflag=false;
			e.printStackTrace();
			log.info(me + " " + e.toString());
		} finally {
			closeAll(da, rs, connFlg);
		}
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", tmpflag);
		resultMap.put("msg", msg.toString());
	    return resultMap;
	}
	// bpsnobj:基本檔,nMap:新敘薪
	private Patrapsn putTraPsnObj(Pabaspsn bobj, HashMap nMap, int tranum, String userid) {
		String me = ".putTraPsnObj()";
		Patrapsn rtnobj = new Patrapsn();
		if (bobj != null) {

			rtnobj.setHid(bobj.getHid()); // 66醫院別
			rtnobj.setPatracod((int) nMap.get("tracd")); // 0異動代碼
			rtnobj.setPatradt(java.sql.Date.valueOf((String) nMap.get("tradt"))); // 1發薪日期
			rtnobj.setPaeffdt(java.sql.Date.valueOf((String) nMap.get("effdt"))); // 2生效日期
			rtnobj.setSalnum(0); // 3餉單編號
			rtnobj.setPaylvefg(bobj.getPaylvefg()); // 4離職否(Y/N)
			rtnobj.setPayidno(bobj.getPayidno()); // 5身分證字號
			rtnobj.setPaycard(bobj.getPaycard()); // 6卡號
			rtnobj.setPayempno(bobj.getPayempno()); // 7職號
			rtnobj.setPaynamec(bobj.getPaynamec()); // 8姓名
			rtnobj.setPaydept(bobj.getPaydept()); // 9部門代碼
			rtnobj.setPaygovdt(bobj.getPaygovdt()); // 10初入公職日期
			rtnobj.setPayempdt(bobj.getPayempdt()); // 11到院日期
			rtnobj.setPaysgndt(java.sql.Date.valueOf((String) nMap.get("sgndt"))); // 12現職日期
			rtnobj.setPayrsgdt(bobj.getPayrsgdt()); // 13離職日期
			rtnobj.setPayinflg(bobj.getPayinflg()); // 17保險補助比率
			rtnobj.setPaydpno(bobj.getPaydpno()); // 18健保數
			rtnobj.setPaypflag("0"); // 19考績
			rtnobj.setPaytaxno(bobj.getPaytaxno()); // 48扶養人數
			rtnobj.setPaypsw(bobj.getPaypsw()); // 54PASSWORD
			rtnobj.setPayctcbacno(bobj.getPayctcbacno()); // 63薪津帳號
			rtnobj.setPayctcbgedno(bobj.getPayctcbgedno()); // 64公儲帳號
			rtnobj.setInsuamt(bobj.getInsuamt()); // 65投保金額
			rtnobj.setPayupdid(userid); // 49 更改者
			rtnobj.setTranum(tranum); // 52和異動支扣名細關聯
			// rtnobj.setPayupdtm( new java.sql.Timestamp(System.currentTimeMillis())); //50
			// 更改日期時間

			rtnobj.setPaytitle((String) nMap.get("title")); // 20新--職稱代碼
			rtnobj.setPayrnk2n((String) nMap.get("rnk2n")); // 21 職等
			rtnobj.setPayrank3((int) nMap.get("rank3")); // 22 本功別
			rtnobj.setPaydegr2((int) nMap.get("degr2")); // 23 級別
			rtnobj.setPaydegre((int) nMap.get("degre")); // 24 俸點
			rtnobj.setPayrank((String) nMap.get("rank")); // 25 階別代碼
			rtnobj.setPayrank2((String) nMap.get("rank2")); // 26 職等(權理)
			rtnobj.setPayrankc((String) nMap.get("rankc")); // 27 主管(權理)
			rtnobj.setPayrank4((int) nMap.get("rank4")); // 62 適用專業加給表
			rtnobj.setPaymonno((int) nMap.get("monno")); // 33 補發繳回月數
			rtnobj.setPaydayno((int) nMap.get("dayno")); // 34 補發繳回日數
			rtnobj.setPaypfsp1((int) nMap.get("pfsp1")); // 30 額外點數1
			rtnobj.setPaypfsp2((int) nMap.get("pfsp2")); // 31 額外點數2
			rtnobj.setPaypfsp3((int) nMap.get("pfsp3")); // 32 額外點數3
			rtnobj.setPayexpfkey1((int) nMap.get("expfkey1")); // 58 額外點數1代碼
			rtnobj.setPayexpfkey2((int) nMap.get("expfkey2")); // 59 額外點數2代碼
			rtnobj.setPayexpfkey3((int) nMap.get("expfkey3")); // 60 額外點數3代碼
			
			rtnobj.setPaypfdoc(bobj.getPaypfdoc()); // 28 高榮/屏榮--專勤基本額度
			rtnobj.setPacsflag(bobj.getPacsflag()); // 29 工作獎金(Y/N)			
			rtnobj.setPaotitle(bobj.getPaytitle()); // 35原--職稱代碼
			rtnobj.setPaornk2n(bobj.getPayrnk2n()); // 36 職等
			rtnobj.setPaorank3(bobj.getPayrank3()); // 37 本功別
			rtnobj.setPaodegr2(bobj.getPaydegr2()); // 38 級別
			rtnobj.setPaodegre(bobj.getPaydegre()); // 39 俸點
			rtnobj.setPaorank(bobj.getPayrank()); // 40 階別代碼
			rtnobj.setPaorank2(bobj.getPayrank2()); // 41 職等(權理)
			rtnobj.setPaorankc(bobj.getPayrank2()); // 42 主管(權理)
			rtnobj.setPaorank4(bobj.getPayrank4()); // 61 適用專業加給表
			rtnobj.setPaopfdoc(bobj.getPaypfdoc()); // 43 高榮/屏榮--專勤基本額度
			rtnobj.setPaocsfla(bobj.getPacsflag()); // 44 工作獎金(Y/N)
			rtnobj.setPaopfsp1(bobj.getPaypfsp1()); // 45 額外點數1
			rtnobj.setPaopfsp2(bobj.getPaypfsp2()); // 46 額外點數2
			rtnobj.setPaopfsp3(bobj.getPaypfsp3()); // 47 額外點數3
			rtnobj.setPaoexpfkey1(bobj.getPayexpfkey1()); // 55 額外點數1代碼
			rtnobj.setPaoexpfkey2(bobj.getPayexpfkey2()); // 56 額外點數2代碼
			rtnobj.setPaoexpfkey3(bobj.getPayexpfkey3()); // 57 額外點數3代碼

			rtnobj.setPaybkid(bobj.getPaybkid()); // 14銀行帳號科目x
			rtnobj.setPayacno(bobj.getPayacno()); // 15銀行帳號x
			rtnobj.setPaygedno(bobj.getPaygedno()); // 16公儲帳號x
			rtnobj.setPayflag(""); // 51處理旗標x
			rtnobj.setUpmastdt(""); // 53資料過帳至主檔x
		}
		return rtnobj;

	}
	// bary:基本明細集合,tranum與異動個人關連,userid輸入人員
	private ArrayList<Patrapay> putTraPayAry(ArrayList<Pabaspay> bary, HashMap nMap, int tranum, String userid) {
		String me = ".putTraPayAry()";
		Patrapay oneobj = null;
		ArrayList<Patrapay> rtnAry = new ArrayList<Patrapay>();
		if (bary != null) {
			for (Pabaspay data : bary) {
				oneobj = new Patrapay();
				oneobj.setTranum(tranum); 				// 0和異動支扣名細關聯
				oneobj.setPayidno(data.getPayidno()); 	// 1身分證字號
				oneobj.setPatradt(java.sql.Date.valueOf((String) nMap.get("tradt"))); // 1發薪日期
				oneobj.setPaysort((String) nMap.get("sort")); // 3分類(N:新 O:原)
				oneobj.setPaycode(data.getPaycode()); 	// 4支扣代碼
				oneobj.setPayamt(data.getPayamt()); 	// 5金額
				oneobj.setPayauth(data.getPayauth()); 	// 6授權碼
				oneobj.setPayiftax(data.getPayiftax()); // 7是否列入所得
				// oneobj.setPayupdtm( new java.sql.Timestamp(System.currentTimeMillis()));
				// //8更改日期時間
				oneobj.setPayflag(""); 					// 9處理旗標
				oneobj.setHid(data.getHid()); 			// 10醫院別

				rtnAry.add(oneobj);
			}
		}
		return rtnAry;
	}
	// NSalAry:新支扣jsonary->array,tranum與異動個人關連
	private ArrayList<Pabatpay> putBatPayJson(JSONArray batJAry, HashMap nMap) {
		String me = ".putBatPayJson()";
		Pabatpay oneobj = null;
		ArrayList<Pabatpay> rtnAry=new ArrayList<Pabatpay>();
		for (Object obj : batJAry) {

			JSONObject jsonObject = (JSONObject) obj;
			oneobj = new Pabatpay();

			oneobj.setPatradt(java.sql.Date.valueOf((String) nMap.get("tradt"))); // 0發薪日期
			oneobj.setPayidno((String) nMap.get("payidno")); 	// 1身分證字號
			oneobj.setSalnum((int) nMap.get("salnum")); 			// 2餉單編號
			oneobj.setPaycard((String) nMap.get("paycard")); 		// 3卡號
			oneobj.setPaycode(jsonObject.getString("paycode")); // 4支扣代碼
			oneobj.setPaupbas(jsonObject.getString("paupbas")); // 5Y更新主檔N歸零支扣代碼
			oneobj.setPayamt(jsonObject.getInt("payamt")); 		// 6金額
			oneobj.setPadutyid((String) nMap.get("tracod")+(String) nMap.get("userid")); 	// 7patracod+userid
			//oneobj.setPayupdtm( new java.sql.Timestamp(System.currentTimeMillis()));//8更改日期時間
			oneobj.setPayflag(""); 								// 9處理旗標
			oneobj.setUpmastdt(""); 							// 10資料過帳至主檔
			oneobj.setHid((String) nMap.get("hid")); 			// 11醫院別
			rtnAry.add(oneobj);
		}

		return rtnAry;
	}
	// NSalAry:新支扣jsonary->array,tranum與異動個人關連
	private ArrayList<Patrapay> putTraPayJson(JSONArray NSalAry, HashMap nMap, int tranum, ArrayList<Patrapay> rtnAry) {
		String me = ".putTraPayJson()";
		Patrapay oneobj = null;
		for (Object obj : NSalAry) {

			JSONObject jsonObject = (JSONObject) obj;
			oneobj = new Patrapay();

			oneobj.setTranum(tranum); 							// 0和異動支扣名細關聯
			oneobj.setPayidno((String) nMap.get("payidno")); 	// 1身分證字號
			oneobj.setPatradt(java.sql.Date.valueOf((String) nMap.get("tradt"))); // 1發薪日期
			oneobj.setPaysort((String) nMap.get("sort")); 		// 3分類(N:新 O:原)
			oneobj.setPaycode(jsonObject.getString("paycode")); // 4支扣代碼
			oneobj.setPayamt(jsonObject.getInt("payamt")); 		// 5金額
			oneobj.setPayauth(jsonObject.getString("payauth")); // 6授權碼
			oneobj.setPayiftax(jsonObject.getString("payiftax")); // 7是否列入所得
			// oneobj.setPayupdtm( new java.sql.Timestamp(System.currentTimeMillis()));
			// //8更改日期時間
			oneobj.setPayflag(""); 								// 9處理旗標
			oneobj.setHid((String) nMap.get("hid")); 			// 10醫院別

			rtnAry.add(oneobj);
		}

		return rtnAry;
	}
	// 寫入異動個人/支扣明細,先寫異動個人一筆,再讀異動支扣明細, 批次檔有值才寫入.
	// trapsnMap trapayMap batpayMap
	// 差額依狀況會寫至批次檔
	private String updTraTblMap(HashMap<String, Patrapsn> trapsnMap, HashMap<String, ArrayList<Patrapay>> trapayMap,HashMap<String, ArrayList<Pabatpay>> batpayMap,String hid, VGHDataAccess da) {
		String me = getClass().getName() + ".updTraTblMap()";
		String rtn = "";
		ArrayList<Object> param = new ArrayList<Object>();
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> batch_sql = new ArrayList<Object>(); // batch_update sql 資料
		ArrayList<Object> paramA = new ArrayList<Object>();
		ArrayList<Patrapay> oneAry = null;
		ArrayList<Pabatpay> batAry = null;
		String strKey = "";
		ResultSet rs = null;
		int write_cnt = 0;
		int limitcnt = 100;

		// 異動檔_寫入個人明細__patrapsn
		String sqlTrapsn_ins = " insert into " + this.tTraPsn
				+ " (PAEFFDT, SALNUM, PAYLVEFG, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, PAYEMPDT, PAYSGNDT, PAYRSGDT, PAYINFLG, PAYDPNO, PAYPFLAG, PAYTITLE, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, PAYPFDOC, PACSFLAG, PAYPFSP1, PAYPFSP2, PAYPFSP3, PAYMONNO, PAYDAYNO, PAOTITLE, PAORNK2N, PAORANK3, PAODEGR2, PAODEGRE, PAORANK, PAORANK2, PAORANKC, PAOPFDOC, PAOCSFLA, PAOPFSP1, PAOPFSP2, PAOPFSP3, PAYTAXNO, PAYUPDID, PAYUPDTM, TRANUM, PAOEXPFKEY1, PAOEXPFKEY2, PAOEXPFKEY3, PAYEXPFKEY1, PAYEXPFKEY2, PAYEXPFKEY3, PAORANK4, PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, INSUAMT, "
				+ tmpZtrapsn + ",PAYIDNO,PATRADT, PATRACOD,  HID)"
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,current timestamp,?,?,?,?,?,?,?,?,?,?,?,?,current timestamp,?,?,?,?) ";
		// 異動檔_寫入支扣明細_patrapay
		String sqlTrapay_ins = "insert into " + this.tTraPay + " (TRANUM, PAYAMT, PAYAUTH, PAYIFTAX, PAYFLAG,PAYUPDTM, "
				+ tmpZtrapay + ", PAYCODE, HID, PAYIDNO, PATRADT, PAYSORT)"
				+ " values(?,?,?,?,'',current timestamp,current timestamp,?,?,?,?,?) ";
		// 批次檔_pabatpay
		String sqlBatpay_ins = " insert into " + this.tBatPay
				+ " (PAYCARD, PAUPBAS, PAYAMT, PADUTYID, PAYUPDAT, PAYFLAG, UPMASTDT, " + tmpZbatpay + ", PATRADT, PAYIDNO, SALNUM, PAYCODE, HID)"
				+ " values( ?,?,?,?,current timestamp,?, ?,current timestamp,?,?,?,?,?) ";
		
		
		try {
			da.setAutoCommit(false);
			if (trapsnMap != null) {
				for (Map.Entry<String, Patrapsn> trapsnentry : trapsnMap.entrySet()) {

					Patrapsn data = trapsnentry.getValue();
					if (data != null) {
						strKey = data.getPayidno();
						batch_sql.add(sqlTrapsn_ins);
						param = new ArrayList<Object>();
						// (PAEFFDT, SALNUM, PAYLVEFG, PAYCARD, PAYEMPNO,
						param.add(data.getPaeffdt());
						param.add(data.getSalnum());
						param.add(data.getPaylvefg());
						param.add(data.getPaycard());
						param.add(data.getPayempno());
						// PAYNAMEC, PAYDEPT, PAYGOVDT, PAYEMPDT, PAYSGNDT,
						param.add(data.getPaynamec());
						param.add(data.getPaydept());
						param.add(data.getPaygovdt());
						param.add(data.getPayempdt());
						param.add(data.getPaysgndt());
						// PAYRSGDT, PAYINFLG, PAYDPNO, PAYPFLAG, PAYTITLE,
						param.add(data.getPayrsgdt());
						param.add(data.getPayinflg());
						param.add(data.getPaydpno());
						param.add(data.getPaypflag());
						param.add(data.getPaytitle());
						// PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK,
						param.add(data.getPayrnk2n());
						param.add(data.getPayrank3());
						param.add(data.getPaydegr2());
						param.add(data.getPaydegre());
						param.add(data.getPayrank());
						// PAYRANK2, PAYRANKC, PAYPFDOC, PACSFLAG, PAYPFSP1,
						param.add(data.getPayrank2());
						param.add(data.getPayrankc());
						param.add(data.getPaypfdoc());
						param.add(data.getPacsflag());
						param.add(data.getPaypfsp1());
						// PAYPFSP2, PAYPFSP3, PAYMONNO, PAYDAYNO, PAOTITLE,
						param.add(data.getPaypfsp2());
						param.add(data.getPaypfsp3());
						param.add(data.getPaymonno());
						param.add(data.getPaydayno());
						param.add(data.getPaotitle());
						// PAORNK2N, PAORANK3, PAODEGR2, PAODEGRE, PAORANK,
						param.add(data.getPaornk2n());
						param.add(data.getPaorank3());
						param.add(data.getPaodegr2());
						param.add(data.getPaodegre());
						param.add(data.getPaorank());
						// PAORANK2, PAORANKC, PAOPFDOC, PAOCSFLA, PAOPFSP1,
						param.add(data.getPaorank2());
						param.add(data.getPaorankc());
						param.add(data.getPaopfdoc());
						param.add(data.getPaocsfla());
						param.add(data.getPaopfsp1());
						// PAOPFSP2, PAOPFSP3, PAYTAXNO, PAYUPDID, PAYUPDTM,
						param.add(data.getPaopfsp2());
						param.add(data.getPaopfsp3());
						param.add(data.getPaytaxno());
						param.add(data.getPayupdid());
						// TRANUM, PAOEXPFKEY1, PAOEXPFKEY2, PAOEXPFKEY3, PAYEXPFKEY1,
						param.add(data.getTranum()); // <==
						param.add(data.getPaoexpfkey1());
						param.add(data.getPaoexpfkey2());
						param.add(data.getPaoexpfkey3());
						param.add(data.getPayexpfkey1());
						// PAYEXPFKEY2, PAYEXPFKEY3, PAORANK4, PAYRANK4, PAYCTCBACNO,
						param.add(data.getPayexpfkey2());
						param.add(data.getPayexpfkey3());
						param.add(data.getPaorank4());
						param.add(data.getPayrank4());
						param.add(data.getPayctcbacno());
						// PAYCTCBGEDNO, INSUAMT, "+tmpZtrapsn+",PAYIDNO,PATRADT,
						param.add(data.getPayctcbgedno());
						param.add(data.getInsuamt());
						param.add(strKey);
						param.add(data.getPatradt());
						// PATRACOD, HID)" +
						param.add(data.getPatracod());
						param.add(data.getHid());
						params.add(param);
						//log.info(" param:" + param);
						
						if (++write_cnt % limitcnt == 0) {
							log.info(batch_sql + " 寫入:" + batch_sql + " params:" + params);
							da.batchupdatenocommit(batch_sql, params);
							da.commit();

							batch_sql.clear();
							params.clear();
						}
						
						if (trapayMap != null) {
							oneAry = trapayMap.get(strKey);
							if (oneAry != null) {
								for (Patrapay pdata : oneAry) {
									batch_sql.add(sqlTrapay_ins);
									param = new ArrayList<Object>();
									if (pdata.getPaycode() == null || pdata.getPayauth() == null || pdata.getPayiftax() == null) {
										log.info(" null:" + pdata.getPayidno());
									}
									// String sqlTrapay_ins= "insert into "+ this.tTraPay + " (TRANUM, PAYAMT,
									// PAYAUTH, PAYIFTAX, PAYFLAG,PAYUPDTM, "+tmpZtrapay+", PAYCODE, HID, PAYIDNO,
									// PATRADT, PAYSORT)" +
									// " values(?,?,?,?,'',current timestamp,current timestamp,?,?,?,?,?) ";
									param.add(pdata.getTranum());
									param.add(pdata.getPayamt());
									param.add(StringUtil.nullToString(pdata.getPayauth()));
									param.add(StringUtil.nullToString(pdata.getPayiftax()));
									param.add(StringUtil.nullToString(pdata.getPaycode()));
									param.add(StringUtil.nullToString(pdata.getHid()));
									param.add(StringUtil.nullToString(pdata.getPayidno()));
									param.add(StringUtil.nullToString(pdata.getPatradt()));
									param.add(StringUtil.nullToString(pdata.getPaysort()));
									//log.info(" param:" + param);
									params.add(param);
								}
								if (++write_cnt % limitcnt == 0) {
									log.info(batch_sql + " 寫入:" + batch_sql + " params:" + params);
									da.batchupdatenocommit(batch_sql, params);
									da.commit();

									batch_sql.clear();
									params.clear();
								}
							}	
						}
						//批次檔
						if (trapayMap != null) {
							batAry = batpayMap.get(strKey);
							if (batAry != null) {
								for (Pabatpay batData : batAry) {
									batch_sql.add(sqlBatpay_ins);
									param = new ArrayList<Object>();
									if (batData.getPaycode() == null || batData.getPaycode() == null ) {
										log.info(" null:" + batData.getPayidno());
									}
//									sqlBatpay_ins = " insert into " + this.tBatPay
//											+ " (PAYCARD, PAUPBAS, PAYAMT, PADUTYID, PAYUPDAT, PAYFLAG, UPMASTDT, " + tmpZbatpay + ", PATRADT, PAYIDNO, SALNUM, PAYCODE, HID)"
//											+ " values( ?,?,?,?,current timestamp,?, ?,current timestamp,?,?,?,?,?) ";
									param.add(StringUtil.nullToString(batData.getPaycard()));	
									param.add(StringUtil.nullToString(batData.getPaupbas()));
									param.add(batData.getPayamt());
									param.add(StringUtil.nullToString(batData.getPadutyid()));
									param.add(StringUtil.nullToString(batData.getPayflag()));
									param.add(StringUtil.nullToString(batData.getUpmastdt()));
									param.add(StringUtil.nullToString(batData.getPatradt()));
									param.add(StringUtil.nullToString(batData.getPayidno()));
									param.add(batData.getSalnum());
									param.add(StringUtil.nullToString(batData.getPaycode()));
									param.add(StringUtil.nullToString(batData.getHid()));
									//log.info(" param:" + param);
									params.add(param);
									
									if (++write_cnt % limitcnt == 0) {
										log.info(batch_sql + " 寫入:" + batch_sql + " params:" + params);
										da.batchupdatenocommit(batch_sql, params);
										da.commit();

										batch_sql.clear();
										params.clear();
									}
								}
							}	
						}
					}
				}
				//log.info(batch_sql + " " + batch_sql + " params:" + params);
				da.batchupdatenocommit(batch_sql, params);
				da.commit();
			}

		} catch (Exception e) {
			da.rollback();
			rtn = " fail, " + e;
			da.setAutoCommit(true);
			e.printStackTrace();
			log.info(me + " " + e.toString());
		} finally {

		}
		return rtn;
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
