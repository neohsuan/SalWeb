package gov.vghks.sal.dao;

import gov.vghks.common.util.DateUtil;
import gov.vghks.common.util.StringUtil;
import gov.vghks.common.util.VGHDataAccess;
import gov.vghks.sal.Globals;
import gov.vghks.sal.Hospitals;
import gov.vghks.sal.Tables;
import gov.vghks.sal.model.Pabank;
import gov.vghks.sal.model.Pabasadr;
import gov.vghks.sal.model.Pabaspay;
import gov.vghks.sal.model.Pabaspsn;
import gov.vghks.sal.model.Pabatpay;
import gov.vghks.sal.model.Pacatgtbl;
import gov.vghks.sal.model.Pacspf;
import gov.vghks.sal.model.Padatpay;
import gov.vghks.sal.model.Padatpsn;
import gov.vghks.sal.model.Padept;
import gov.vghks.sal.model.Padpintbl;
import gov.vghks.sal.model.Paexpftbl;
import gov.vghks.sal.model.Pahist;
import gov.vghks.sal.model.Panhical;
import gov.vghks.sal.model.Panhidet;
import gov.vghks.sal.model.Papayitm;
import gov.vghks.sal.model.Paprtdtl;
import gov.vghks.sal.model.Parank4tbl;
import gov.vghks.sal.model.Pareport;
import gov.vghks.sal.model.Pataxcash;
import gov.vghks.sal.model.Pataximp;
import gov.vghks.sal.model.Pataxobj;
import gov.vghks.sal.model.Pataxpsn;
import gov.vghks.sal.model.Pataxtran;
import gov.vghks.sal.model.Patitle;
import gov.vghks.sal.model.Patracode;
import gov.vghks.sal.model.Patrapay;
import gov.vghks.sal.model.Patrapsn;
import gov.vghks.sal.model.Psbasic;
import gov.vghks.sal.model.RankCode;
import gov.vghks.sal.util.AmJsonConfig;
import gov.vghks.sal.util.FileUtil;
import gov.vghks.sal.util.PaCommUtil;
import gov.vghks.sal.util.StringUtil2;
import gov.vghks.sal.util.ldap.LDAPUtil;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.sql.*;
import java.util.*;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

/*import org.json.JSONArray;
import org.json.JSONObject;*/
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SalNTDAO {
	public static String newline = System.getProperty("line.separator");
	String gClassName = SalNTDAO.class.getName();
	private String sql = "";
	static ArrayList<Map<String, Object>> result = null;
	ArrayList<Object> params = new ArrayList<Object>();
	static Logger log = Logger.getLogger(SalNTDAO.class); // 呼叫log4j
	private VGHDataAccess da = null;
	private VGHDataAccess daDSN = null;
	private static final int META_TYPE = 2; // 回傳map中欄位名稱是小寫
	private static boolean connFlg = false; // false:由此物件new conn，true:由呼叫物件傳入conn
	private String hid = "";
	static String g_dataSourceNT = "";
	static String g_dataSource = "";
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private String tBasPay = "PA.PABASPAY"; // 基本支扣
	private String tBasPsn = "PA.PABASPSN"; // 基本檔
	private String tBatPay = "PA.PABATPAY"; // 批次檔
	private String tTraCode = "PA.PATRACODE"; // 1異動代碼
	private String tDept = "PA.PADEPT"; // 2單位代碼
	private String tTitle = "PA.PATITLE"; // 3職稱
	private String tRankCode = "PA.PARANKCODE"; // 4階別代碼
	private String tCatgTbl = "PA.PACATGTBL"; // 5人工造冊應計轉帳項目代碼
	private String tExpfTbl = "PA.PAEXPFTBL"; // 6績效額外點數對照表
	private String tRnk4Tbl = "PA.PARNK4TBL"; // 7適用專業加給對照表
	private String tBankDtTbl = "PA.PABANK"; // 1發薪日及入帳日對應檔
	private String tTaxTbl = "PA.PATAXTBL"; // 9薪資所得扣繳稅額表
	private String tPftlTbl = "PA.PAPFTL"; // 10全院績效彙總
	private String tDpinTbl = "PA.PADPINTBL"; // 全民健康保險負擔金額表
	private String tLbinTbl = "PA.PALBINTBL"; // 勞保投保金額對照表
	private String tLbrpTbl = "PA.PALBRPTBL"; // 勞工退休金月提繳工資分級表
	private String tPosiTbl = "PA.PAPOSI"; // 職等支扣--專業加給及主管加給
	private String tRankTbl = "PA.PARANK"; // 階級支扣
	private String tCspfTbl = "PA.PACSPF"; // 工作獎金支扣
	private String tDatPsn = "PA.PADATPSN"; // 月支個人明細
	private String tDatPsnDw = "PA.PADATPSN_DW"; // 月支個人明細_歷史
	private String tDatPay = "PA.PADATPAY"; // 月支支扣明細
	private String tDatPayDw = "PA.PADATPAY_DW"; // 月支支扣明細_歷史
	private String tPayItm = "PA.PAPAYITM"; // 支扣項目彙總
	private String tBasAdr = "PA.PABASADR"; // 地址
	private String tHist = "PA.PAHIST"; // 歷史檔
	private String tTraPsn = "PA.PATRAPSN"; // 異動個人明細
	private String tTraPsnDw = "PA.PATRAPSN_DW"; // 異動個人明細_歷史
	private String tTraPay = "PA.PATRAPAY"; // 異動支扣明細
	private String tTraPayDw = "PA.PATRAPAY_DW"; // 異動支扣明細_歷史
	private String tTaxPsn = "PA.PATAXPSN"; // 所得稅個人明細
	private String tTaxTran = "PA.PATAXTRAN"; // 所得稅轉帳明細
	private String tTaxCash = "PA.PATAXCASH"; // 所得稅現金明細
	private String tRPDTL = "PA.PAREPDETAIL"; // 製作報表明細
	private String tRPPRT = "PA.PAREPORT"; // 報表檔架構
	private String tNhiCal = "PA.PANHICAL"; // 二代健保(補充保險費_獎金)
	private String tNhiDet = "PA.PANHIDET"; // 二代健保申報扣費明細
	private String tTeaChaw = "PA.PATEACHAW"; // 教職及論文獎勵
	private String tLMONT = "PA.PALMONT";				//39製作報表明細
	private String tWKRETIR = "PA.PAWKRETIR";				//40勞工退休金檔
	
	private String tmpZtitle = "ZPATITLE"; // 時戳
	private String tmpZrankce = "ZPARANKCODE";
	private String tmpZbank = "ZPABANK";
	private String tmpZdept = "ZPADEPT";
	private String tmpZrnk4 = "ZPARNK4TBL";
	private String tmpZexpf = "ZPAEXPFTBL";
	private String tmpZtracd = "ZPATRACODE";
	private String tmpZbaspsn = "ZPABASPSN";
	private String tmpZbatpay = "ZPABATPAY";
	private String tmpZtrapsn = "ZPATRAPSN";
	private String tmpZtrapay = "ZPATRAPAY";
	private String tmpZhist = "ZPAHIST";
	private String tmpZdpintbl = "ZPADPINTBL";
	private String tmpZlbintbl = "ZPALBINTBL";
	private String tmpZrpintbl = "ZPALBRPTBL";
	private String tmpZcspf = "ZPACSPF";
	private String tmpZpayitm = "ZPAPAYITM";
	private String tmpZtaxtran = "ZPATAXTRAN";
	private String tmpZtaxcash = "ZPATAXCASH";
	private String tmpZbasadr ="ZPABASADR";
	private String tmpZtaxpsn ="ZPATAXPSN";
	private String tmpZdatpay ="ZPADATPAY";
	private String tmpZdatpsn ="ZPADATPSN";
	private String tmpZnhical ="ZPANHICAL";
	private String tmpZnhidet ="ZPANHIDET";
	private String tmpZrpdtl ="ZPAREPDETAIL";
	private String tmpZwkretir ="ZPAWKRETIR";
	private String tmpZlmont ="ZPAREPDETAIL";
	
	public SalNTDAO() {
		super();
	}

	public SalNTDAO(VGHDataAccess da1) {
		super();
		this.da = da1;
		connFlg = true;
	}

	public SalNTDAO(VGHDataAccess da1, String hid) {
		super();
		this.da = da1;
		connFlg = true;
		this.hid = hid;
		Tables tabInfo = Tables.getTables(hid);

		tTaxPsn = tabInfo.tTaxPsn; // 所得稅個人明細
		tTaxTran = tabInfo.tTaxTran; // 所得稅轉帳明細
		tTaxCash = tabInfo.tTaxCash; // 所得稅現金明細
		tBankDtTbl = tabInfo.tBankDtTbl; // 發薪日及入帳日對應檔
		tBasPsn = tabInfo.tBasPsn;
		tBasPay = tabInfo.tBasPay;
		tBatPay = tabInfo.tBatPay;
		tTraCode = tabInfo.tTraCode;
		tDept = tabInfo.tDept;
		tTitle = tabInfo.tTitle;
		tRankCode = tabInfo.tRankCode;
		tCatgTbl = tabInfo.tCatgTbl;
		tExpfTbl = tabInfo.tExpfTbl;
		tRnk4Tbl = tabInfo.tRnk4Tbl;
		tBankDtTbl = tabInfo.tBankDtTbl;
		tTaxTbl = tabInfo.tTaxTbl;
		tPftlTbl = tabInfo.tPftlTbl;
		tDpinTbl = tabInfo.tDpinTbl;
		tLbinTbl = tabInfo.tLbinTbl;
		tLbrpTbl = tabInfo.tLbrpTbl;
		tPosiTbl = tabInfo.tPosiTbl;
		tRankTbl = tabInfo.tRankTbl;
		tCspfTbl = tabInfo.tCspfTbl;
		tDatPsn = tabInfo.tDatPsn;
		tDatPsnDw = tabInfo.tDatPsnDw;
		tDatPay = tabInfo.tDatPay;
		tDatPayDw = tabInfo.tDatPayDw;
		tPayItm = tabInfo.tPayItm;
		tBasAdr = tabInfo.tBasAdr;
		tHist = tabInfo.tHist;
		tTraPsn = tabInfo.tTraPsn;
		tTraPsnDw = tabInfo.tTraPsnDw;
		tTraPay = tabInfo.tTraPay;
		tTraPayDw = tabInfo.tTraPayDw;
		tRPDTL = tabInfo.tRPDTL;
		tRPPRT = tabInfo.tRPPRT;
		tNhiCal = tabInfo.tNhiCal;
		tNhiDet = tabInfo.tNhiDet;
		tTeaChaw = tabInfo.tTeaChaw;
		tLMONT=tabInfo.tLMONT;
		tWKRETIR=tabInfo.tWKRETIR;
		
		tmpZtitle = tabInfo.tmpZtitle;
		tmpZrankce = tabInfo.tmpZrankce;
		tmpZbank = tabInfo.tmpZbank;
		tmpZdept = tabInfo.tmpZdept;
		tmpZrnk4 = tabInfo.tmpZrnk4;
		tmpZexpf = tabInfo.tmpZexpf;
		tmpZtracd = tabInfo.tmpZtracd;
		tmpZbaspsn = tabInfo.tmpZbaspsn;
		tmpZbatpay = tabInfo.tmpZbatpay;
		tmpZtrapsn = tabInfo.tmpZtrapsn;
		tmpZtrapay = tabInfo.tmpZtrapay;
		tmpZhist = tabInfo.tmpZhist;
		tmpZdpintbl = tabInfo.tmpZdpintbl;
		tmpZlbintbl = tabInfo.tmpZlbintbl;
		tmpZrpintbl = tabInfo.tmpZrpintbl;
		tmpZcspf = tabInfo.tmpZcspf;
		tmpZtaxtran = tabInfo.tmpZtaxtran;
		tmpZtaxcash = tabInfo.tmpZtaxcash;
		tmpZpayitm = tabInfo.tmpZpayitm;
		tmpZbasadr = tabInfo.tmpZbasadr;
		tmpZtaxpsn=tabInfo.tmpZtaxpsn;
		tmpZdatpay=tabInfo.tmpZdatpay;
		tmpZdatpsn=tabInfo.tmpZdatpsn;
		tmpZnhical=tabInfo.tmpZnhical;
		tmpZnhidet=tabInfo.tmpZnhidet;
		tmpZrpdtl=tabInfo.tmpZrpdtl;
		tmpZwkretir=tabInfo.tmpZwkretir;
		tmpZlmont=tabInfo.tmpZlmont;
	}

	public SalNTDAO(VGHDataAccess da1, VGHDataAccess da2, String hid) {
		super();
		this.da = da1;
		this.daDSN = da2;
		connFlg = true;
		this.hid = hid;
		Tables tabInfo = Tables.getTables(hid);

		tTaxPsn = tabInfo.tTaxPsn; // 所得稅個人明細
		tTaxTran = tabInfo.tTaxTran; // 所得稅轉帳明細
		tTaxCash = tabInfo.tTaxCash; // 所得稅現金明細
		tBankDtTbl = tabInfo.tBankDtTbl; // 發薪日及入帳日對應檔
		tBasPsn = tabInfo.tBasPsn;
		tBasPay = tabInfo.tBasPay;
		tBatPay = tabInfo.tBatPay;
		tTraCode = tabInfo.tTraCode;
		tDept = tabInfo.tDept;
		tTitle = tabInfo.tTitle;
		tRankCode = tabInfo.tRankCode;
		tCatgTbl = tabInfo.tCatgTbl;
		tExpfTbl = tabInfo.tExpfTbl;
		tRnk4Tbl = tabInfo.tRnk4Tbl;
		tBankDtTbl = tabInfo.tBankDtTbl;
		tTaxTbl = tabInfo.tTaxTbl;
		tPftlTbl = tabInfo.tPftlTbl;
		tDpinTbl = tabInfo.tDpinTbl;
		tLbinTbl = tabInfo.tLbinTbl;
		tLbrpTbl = tabInfo.tLbrpTbl;
		tPosiTbl = tabInfo.tPosiTbl;
		tRankTbl = tabInfo.tRankTbl;
		tCspfTbl = tabInfo.tCspfTbl;
		tDatPsn = tabInfo.tDatPsn;
		tDatPsnDw = tabInfo.tDatPsnDw;
		tDatPay = tabInfo.tDatPay;
		tDatPayDw = tabInfo.tDatPayDw;
		tPayItm = tabInfo.tPayItm;
		tBasAdr = tabInfo.tBasAdr;
		tHist = tabInfo.tHist;
		tTraPsn = tabInfo.tTraPsn;
		tTraPsnDw = tabInfo.tTraPsnDw;
		tTraPay = tabInfo.tTraPay;
		tTraPayDw = tabInfo.tTraPayDw;
		tRPDTL = tabInfo.tRPDTL;
		tRPPRT = tabInfo.tRPPRT;
		tNhiCal = tabInfo.tNhiCal;
		tNhiDet = tabInfo.tNhiDet;
		tTeaChaw = tabInfo.tTeaChaw;
		tLMONT=tabInfo.tLMONT;
		tWKRETIR=tabInfo.tWKRETIR;

		tmpZtitle = tabInfo.tmpZtitle;
		tmpZrankce = tabInfo.tmpZrankce;
		tmpZbank = tabInfo.tmpZbank;
		tmpZdept = tabInfo.tmpZdept;
		tmpZrnk4 = tabInfo.tmpZrnk4;
		tmpZexpf = tabInfo.tmpZexpf;
		tmpZtracd = tabInfo.tmpZtracd;
		tmpZbaspsn = tabInfo.tmpZbaspsn;
		tmpZbatpay = tabInfo.tmpZbatpay;
		tmpZtrapsn = tabInfo.tmpZtrapsn;
		tmpZtrapay = tabInfo.tmpZtrapay;
		tmpZhist = tabInfo.tmpZhist;
		tmpZdpintbl = tabInfo.tmpZdpintbl;
		tmpZlbintbl = tabInfo.tmpZlbintbl;
		tmpZrpintbl = tabInfo.tmpZrpintbl;
		tmpZcspf = tabInfo.tmpZcspf;
		tmpZtaxtran = tabInfo.tmpZtaxtran;
		tmpZtaxcash = tabInfo.tmpZtaxcash;
		tmpZpayitm = tabInfo.tmpZpayitm;
		tmpZbasadr = tabInfo.tmpZbasadr;
		tmpZtaxpsn=tabInfo.tmpZtaxpsn;
		tmpZdatpay=tabInfo.tmpZdatpay;
		tmpZdatpsn=tabInfo.tmpZdatpsn;
		tmpZnhical=tabInfo.tmpZnhical;
		tmpZnhidet=tabInfo.tmpZnhidet;
		tmpZrpdtl=tabInfo.tmpZrpdtl;
		tmpZwkretir=tabInfo.tmpZwkretir;
		tmpZlmont=tabInfo.tmpZlmont;
	}

	// 代入代碼檔
	public ArrayList<Object> getLoadCodeTbl(String tbl, String userid, String hid, VGHDataAccess da)
			throws SQLException {
		String me = getClass().getName() + ".getLoadCodeTbl(),userid = " + userid;
		ArrayList<Object> aryRtn = new ArrayList<Object>();
		params = new ArrayList<Object>();
		RankCode tmpRank = null;
		Patitle tmpTitle = null;
		Pabank tmpBank = null;
		Padept tmpDept = null;
		Parank4tbl tmpRnk4 = null;
		Paexpftbl tmpexpf = null;
		Patracode tmptracd = null;
		Papayitm tmppayitm = null;
		Padpintbl tmpdpintbl = null;
		Pacatgtbl tmpcatgtbl = null;
		Pataxtran tmptaxtran = null;

		if ("PARANKCODE".equals(tbl)) {
			sql = "SELECT RANKCODE, RANKNAME, RANKABBR, " + tmpZrankce + " from " + this.tRankCode
					+ " order by RANKCODE with ur";
		}
		// 職稱
		else if ("PATITLE".equals(tbl)) {
			sql = "SELECT PAYTITLK, PATITSUB, PACHIEF, PAYTITLP, PAYTITLC, PACODDEL, PAYUPDID, PAYUPDAT, HID, "
					+ tmpZtitle + " from " + this.tTitle + " where hid=? order by PATITSUB,PAYTITLK with ur";
			params.add(hid);
		} else if ("PABANK".equals(tbl)) {// 6個月內
			sql = "SELECT SALNUM, PAYBANK, PAYSALDT, PAYUPDID, PAYUPDAT,HID, " + tmpZbank + " from " + this.tBankDtTbl
					+ " where HID=? and PAYBANK BETWEEN CURRENT DATE AND CURRENT DATE +6 month order by SALNUM desc with ur";
			params.add(hid);
		} else if ("PABANK_2M".equals(tbl)) {// 2個月內
			sql = "SELECT SALNUM, PAYBANK, PAYSALDT, PAYUPDID, PAYUPDAT,HID, " + tmpZbank + " from " + this.tBankDtTbl
					+ " where HID=? and PAYBANK BETWEEN CURRENT DATE -1 month AND CURRENT DATE +1 month order by SALNUM desc with ur";
			params.add(hid);
		} else if ("PABANK_3Y".equals(tbl)) {// 3年內
			sql = "SELECT SALNUM, PAYBANK, PAYSALDT, PAYUPDID, PAYUPDAT,HID, " + tmpZbank + " from " + this.tBankDtTbl
					+ " where HID=? and PAYBANK BETWEEN CURRENT DATE -3 year AND CURRENT DATE order by SALNUM desc with ur";
			params.add(hid);
		} else if ("PADEPT".equals(tbl)) {
			sql = "SELECT PADEPCOD, PACSTNO, PADEPNMC, PAYUPDID, PAYUPDAT, HID, PBWKCODE," + tmpZdept + " from "
					+ this.tDept + " where HID=? order by PACSTNO,PADEPCOD with ur";
			params.add(hid);
		} else if ("PARNK4TBL".equals(tbl)) {
			sql = "SELECT RNK4KEY, RNK4NAME, PAYUPDTM, " + tmpZrnk4 + " from " + this.tRnk4Tbl
					+ " order by RNK4KEY with ur";
		} else if ("PAEXPFTBL".equals(tbl)) {
			sql = "SELECT EXPFKEY, EXPFNAME, PAYUPDID, PAYUPDAT, " + tmpZexpf + " from " + this.tExpfTbl
				+ " order by EXPFKEY with ur";
		}
		// 異動碼
		else if ("PATRACODE".equals(tbl)) {
			sql = "SELECT TRACODE, TRANAME, " + tmpZtracd + " from " + this.tTraCode + " order by TRACODE with ur";
		}
		// 支扣代碼
		else if ("PAPAYITM".equals(tbl)) {
			sql = "SELECT PAYCODE, PACODNMC, PACODDEL, PAYAUTH, PAYIFTAX, PAUPBAS, PAYREWARD, PAYUPDID, PAYUPDAT, HID "
					+ "from " + this.tPayItm + " where HID=? order by PAYCODE with ur";
			params.add(hid);
		}
		// 支扣代碼,限讀[刪除=N]
		else if ("PAPAYITM_N".equals(tbl)) {
			sql = "SELECT PAYCODE, PACODNMC, PACODDEL, PAYAUTH, PAYIFTAX, PAUPBAS, PAYREWARD, PAYUPDID, PAYUPDAT, HID "
					+ "from " + this.tPayItm + " where HID=? and PACODDEL='N' order by PAYCODE with ur";
			params.add(hid);
		} else if ("PADPINTBL".equals(tbl)) {
			sql = "SELECT DPINLOW, case DPINHIGH WHEN 999999 THEN " + Globals.gDpinMax
					+ " ELSE dpinhigh END dpinhigh, DPINAMT, PAYUPDID, DPINDEPT1, DPINDEPT3 " + "from " + this.tDpinTbl
					+ " order by DPINLOW with ur";
		} else if ("TAXPSNYR".equals(tbl)) {
			sql = "SELECT payear from " + this.tTaxPsn + " where HID=? GROUP BY payear ORDER BY payear DESC WITH ur";
			params.add(hid);
		} else if ("NHIPAYDT".equals(tbl)) {
			sql = "SELECT PAPAYDT from " + this.tNhiCal + " where HID=? GROUP BY PAPAYDT ORDER BY PAPAYDT DESC WITH ur";
			params.add(hid);
		} else if ("NHIPAYYR".equals(tbl)) {
			sql = "SELECT YEAR(PAPAYDT) PAPAYYR from " + this.tNhiCal
					+ " where HID=? GROUP BY YEAR(PAPAYDT) ORDER BY YEAR(PAPAYDT) DESC WITH ur";
			params.add(hid);
		}
		//
		else if ("TAXCATG".equals(tbl)) {
			sql = "SELECT PAYCATG, CATGNAME from " + this.tCatgTbl
					+ " where (NOT CATGNAME LIKE '%x%' and not PAYCATG in (0) ) ORDER BY PAYCATG WITH ur";
		}
		// 已轉帳所得稅年月....2026.6.17改成只限讀2個月日期
		else if ("TAXTRANYR".equals(tbl)) {
			// sql = "SELECT PAYEAR,PAYMONTH,PAYCATG from "+this.tTaxTran +" where HID=? and
			// PAYEAR IN (SELECT MAX(PAYEAR) FROM "+this.tTaxTran +" where HID=?) GROUP BY
			// payear,paymonth,paycatg order by PAYMONTH DESC,PAYCATG DESC with ur";
			sql = "SELECT PAYEAR,PAYMONTH,PAYCATG from " + this.tTaxTran
					+ " where HID=? AND DATE(TIMESTAMP_FORMAT(payear || RIGHT(DIGITS(PAYMONTH), 2) || '01' , 'YYYY-MM-DD')) BETWEEN CURRENT date-2 month AND CURRENT date GROUP BY payear,paymonth,paycatg order by PAYMONTH DESC,PAYCATG DESC with ur";
			params.add(hid);
		}

		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			log.info(me + "  " + sql + "...params:" + params.toString());
			result = null;
			if (tbl.matches("PARANKCODE|PARNK4TBL|PAEXPFTBL|PATRACODE|PADPINTBL|TAXCATG")) {
				result = da.arraylistbyQuery(sql, META_TYPE);
			} else if (tbl.matches(
					"PATITLE|PABANK|PABANK_2M|PABANK_3Y|PADEPT|PAPAYITM|PAPAYITM_N|TAXPSNYR|TAXTRANYR|NHIPAYDT|NHIPAYYR")) {
				result = da.arraylistbyQuery(sql, params, META_TYPE);
			}
			log.info(me + "  ...size:" + result.size());
			if (result != null && result.size() > 0) {
				for (Map<String, Object> data : result) {
					if ("PARANKCODE".equals(tbl)) { // 階級代碼
						tmpRank = new RankCode();
						PropertyUtils.copyProperties(tmpRank, data);
						aryRtn.add(tmpRank);
					} else if ("PATITLE".equals(tbl)) { // 職稱代碼
						tmpTitle = new Patitle();
						PropertyUtils.copyProperties(tmpTitle, data);
						aryRtn.add(tmpTitle);
					} else if (tbl.matches("PABANK|PABANK_2M|PABANK_3Y")) { // 發薪日及入帳日對應檔
						tmpBank = new Pabank();
						PropertyUtils.copyProperties(tmpBank, data);
						aryRtn.add(tmpBank);
					} else if ("PADEPT".equals(tbl)) { // 單位
						tmpDept = new Padept();
						PropertyUtils.copyProperties(tmpDept, data);
						aryRtn.add(tmpDept);
					} else if ("PARNK4TBL".equals(tbl)) { // 適用專業加給對照表
						tmpRnk4 = new Parank4tbl();
						PropertyUtils.copyProperties(tmpRnk4, data);
						aryRtn.add(tmpRnk4);
					} else if ("PAEXPFTBL".equals(tbl)) { // 績效額外點數對照表
						tmpexpf = new Paexpftbl();
						PropertyUtils.copyProperties(tmpexpf, data);
						aryRtn.add(tmpexpf);
					} else if ("PATRACODE".equals(tbl)) { // 異動代碼
						tmptracd = new Patracode();
						PropertyUtils.copyProperties(tmptracd, data);
						aryRtn.add(tmptracd);
					} else if (tbl.matches("PAPAYITM|PAPAYITM_N")) { // 支扣代碼
						tmppayitm = new Papayitm();
						PropertyUtils.copyProperties(tmppayitm, data);
						aryRtn.add(tmppayitm);
					} else if ("PADPINTBL".equals(tbl)) { // 健保級距
						tmpdpintbl = new Padpintbl();
						PropertyUtils.copyProperties(tmpdpintbl, data);
						aryRtn.add(tmpdpintbl);
					} else if ("TAXPSNYR".equals(tbl)) { // 所得稅年度
						aryRtn.add(StringUtil.nullToString(data.get("payear")));
					} else if ("TAXCATG".equals(tbl)) { // 所得稅轉帳項目
						tmpcatgtbl = new Pacatgtbl();
						PropertyUtils.copyProperties(tmpcatgtbl, data);
						aryRtn.add(tmpcatgtbl);
					} else if ("TAXTRANYR".equals(tbl)) { // 已轉帳所得稅年月
						tmptaxtran = new Pataxtran();
						PropertyUtils.copyProperties(tmptaxtran, data);
						aryRtn.add(tmptaxtran);
					} else if ("NHIPAYDT".equals(tbl)) { // 補充費給付日期
						aryRtn.add(StringUtil.nullToString(data.get("papaydt")));
					} else if ("NHIPAYYR".equals(tbl)) { // 補充費給付年度 PAPAYYR
						aryRtn.add(StringUtil.nullToString(data.get("papayyr")));
					}
				}
			}
		} catch (Throwable e) {
			throw new SQLException(me + " tbl:" + tbl + " " + e.toString());
		} finally {
			closeAll(da, null, connFlg);
		}
		return aryRtn;
	}

	/**
	 * 依table名單及KEY,刪除record
	 * 
	 * @throws SQLException
	 * @param code_data(參數map)
	 *            userid(登入者) hid(醫院代號)
	 */

	public boolean delTblCode(String code_data, String tblname, String userid, String hid, VGHDataAccess da)
			throws Exception {
		String me = getClass().getName() + ".delTblCode(),userid = " + userid;
		boolean success = false;
		int delCnt = 0;
		JSONObject object_code_data = JSONObject.fromObject(code_data);
		params = new ArrayList<Object>();
		if ("PATRACODE".equals(tblname)) {
			sql = "delete from " + this.tTraCode + " where tracode=? ";
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
		} else if ("PADEPT".equals(tblname)) {
			sql = "delete from " + this.tDept + " where padepcod=? and hid=? ";
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
			params.add(hid);
		} else if ("PARANKCODE".equals(tblname)) {
			sql = "delete from " + this.tRankCode + " where rankcode=? ";
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
		} else if ("PACATGTBL".equals(tblname)) {
			sql = "delete from " + this.tCatgTbl + " where paycatg=? ";
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
		} else if ("PAEXPFTBL".equals(tblname)) {
			sql = "delete from " + this.tExpfTbl + " where expfkey=? ";
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
		} else if ("PARNK4TBL".equals(tblname)) {
			sql = "delete from " + this.tRnk4Tbl + " where rnk4key=? ";
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
		} else if ("PABANK".equals(tblname)) {
			sql = "delete from " + this.tBankDtTbl + " where salnum=? ";
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
		} else if ("PATAXTBL".equals(tblname)) {
			sql = "delete from " + this.tTaxTbl + " where sallow=? and kindred=? ";
			params.add(StringUtil.nullToString(object_code_data.getString("sallow")));
			params.add(StringUtil.nullToString(object_code_data.getString("kindred")));
		} else if ("PAPFTL".equals(tblname)) {
			sql = "delete from " + this.tPftlTbl + " where paypfmon=? ";
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
		} else if ("PADPINTBL".equals(tblname)) {
			sql = "delete from " + this.tDpinTbl + " where dpinlow=? ";
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
		} else if ("PALBINTBL".equals(tblname)) {
			sql = "delete from " + this.tLbinTbl + " where lbinlow=? ";
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
		} else if ("PALBRPTBL".equals(tblname)) {
			sql = "delete from " + this.tLbrpTbl + " where lbrplow=? ";
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
		} else if ("PATITLE".equals(tblname)) {
			sql = "delete from " + this.tTitle + " where paytitlk=? and hid=? ";
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
			params.add(hid);
		} else if ("PAPAYITM".equals(tblname)) {
			sql = "delete from " + this.tPayItm + " where paycode=? and hid=? ";
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
			params.add(hid);
		} else if ("PAPOSI".equals(tblname)) {
			sql = "delete from " + this.tPosiTbl + " where payposik=? and degrlow=? ";
			params.add(StringUtil.nullToString(object_code_data.getString("payposik")));
			params.add(StringUtil.nullToString(object_code_data.getString("degrlow")));
		} else if ("PARANK".equals(tblname)) {
			sql = "delete from " + this.tRankTbl + " where paylev1v=? and paylev2v=? and payrankv=? and HID=?";
			params.add(StringUtil.nullToString(object_code_data.getString("paylev1v")));
			params.add(StringUtil.nullToString(object_code_data.getString("paylev2v")));
			params.add(StringUtil.nullToString(object_code_data.getString("payrankv")));
			params.add(hid);
		} else if ("PACSPF".equals(tblname)) {
			sql = "delete from " + this.tCspfTbl + " where pacskey=? ";
			params.add(StringUtil.nullToString(object_code_data.getString("key")));
		} else if ("PATRAPAY".equals(tblname)) {
			sql = "delete from " + this.tTraPay
					+ " where tranum=? and patradt=? and paysort=? and paycode=? and hid=? ";
			params.add(StringUtil.nullToString(object_code_data.getString("tranum")));
			params.add(StringUtil.nullToString(object_code_data.getString("patradt")));
			params.add(StringUtil.nullToString(object_code_data.getString("paysort")));
			params.add(StringUtil.nullToString(object_code_data.getString("paycode")));
			params.add(StringUtil.nullToString(object_code_data.getString("hid")));
		} else if ("PANHICAL".equals(tblname)) {
			sql = "delete from " + this.tNhiCal
					+ " where papaydt=? and paysource=? and itemnum=? and payidno=? and hid=? ";
			params.add(StringUtil.nullToString(object_code_data.getString("papaydt")));
			params.add(StringUtil.nullToString(object_code_data.getString("paysource")));
			params.add(StringUtil.nullToString(object_code_data.getString("itemnum")));
			params.add(StringUtil.nullToString(object_code_data.getString("payidno")));
			params.add(StringUtil.nullToString(object_code_data.getString("hid")));
		}

		try {
			if (da == null) {
				connFlg = false;
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
			}

			log.info(me + "  " + sql + "...params:" + params.toString());
			delCnt = da.update(sql, params);
			if (delCnt > 0)
				success = true;
			log.info(me + "...delCnt:" + delCnt);
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}
		return success;
	}

	/**
	 * editMode:update/insert 
	 * table  pa.pabasic(薪資基本檔) panhical補充費_獎金 panhidet申報扣費明細 pabatpay批次檔 patrapsn(異動檔)
	 * tblname:table pk_data:key tabJson:各欄
	 * 傳出:true/false
	 */
	public boolean updPATblByKey(String editMode, String tblname, String pk_data, String tabJson, String userid,
			String hid, VGHDataAccess da) throws SQLException {
		String me = getClass().getName() + ".updPATblByKey() userid:" + userid;
		log.info(me + " tblname:" + tblname + " editMode = " + editMode + " pk_data = " + pk_data + " tabJson = "
				+ tabJson + " userid = " + userid + " hid = " + hid);
		boolean success = false;
		JSONObject object_pk_data = JSONObject.fromObject(pk_data);
		;
		JSONObject obj_item = null;
		int updCnt = 0;
		JSONArray array = null;
		params = new ArrayList<Object>();
		ArrayList<Object> sql_stmts = new ArrayList<Object>();
		ArrayList<Object> paramA = new ArrayList<Object>();
		ArrayList<Object> paramsB = new ArrayList<Object>();
		ArrayList<Object> param = new ArrayList<Object>();
		ResultSet rs = null;
		String sqlAdr_inq = "";
		String sqlAdr_upd = "";
		String sqlAdr_ins = "";
		String sqlTranum_max = "";
		String sqlTrapsn_upd = "";
		String sqlTrapsn_ins = "";
		String sqlTrapay_ins = "";
		String sqlTrapsn_del = "";
		String sqlTrapay_del = "";
		String sqlTrapay_cnt = "";
		String strPaycode = "";
		String strPayauth = "";
		String strPayiftax = "";
		String strPaysort = "";
		String strAddr = "";
		String[] aryPaycode = null;
		String formnm = "";
		int datacnt = 0;
		boolean dataOK = false;
		int adrcnt = 0;
		boolean adrOK = false;
		String strKey = "";
		int intTraNum_max = 0;
		int intTraNum = 0;
		boolean blnTraPay = true;

		boolean blnAdr = false;
		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			da.setAutoCommit(false);
			// sql
			// 地址
			sqlAdr_inq = " select PAYIDNO, PAYADDRS from " + this.tBasAdr + " where PAYIDNO=? ";
			sqlAdr_upd = " update " + this.tBasAdr + " set (PAYADDRS, " + tmpZbasadr
					+ ") = (?, current timestamp) where PAYIDNO=? ";
			sqlAdr_ins = " INSERT INTO " + this.tBasAdr + " (PAYADDRS, " + tmpZbasadr
					+ ",PAYIDNO) VALUES(?, current timestamp, ?);";
			if ("PABASPSN".equals(tblname)) {
				strKey = StringUtil.nullToString(object_pk_data.getString("payidno"));
				hid = StringUtil.nullToString(object_pk_data.getString("hid"));
				if (editMode.equals("update")) {
					// 基本檔
					sql = " update " + this.tBasPsn
							+ " set (PAYEMPNO,PAYNAMEC, PAYDEPT,PAYSGNDT,PAYTAXNO, INSUAMT,PACSFLAG, PARETENTION,PAYEXPFKEY1, PAYEXPFKEY2, PAYEXPFKEY3,PAYPFSP1, PAYPFSP2, PAYPFSP3,PAYCTCBACNO,PAYCTCBGEDNO,PAYUPDID, PAYUPDTM) "
							+ "= (?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,current timestamp) where PAYIDNO=? and HID=? ";
				}
				// 地址空白不處理
				paramA = new ArrayList<Object>();
				paramA.add(strKey);
				rs = da.query(sqlAdr_inq, paramA);
				blnAdr = false;
				while (rs.next()) {
					log.info(me + " 地址-> " + rs.getString("PAYIDNO"));
					blnAdr = true;
				}
			}
			// 補充費_獎金 , PAPAYDT PAYSOURCE, ITEMNUM, PAYIDNO,
			else if ("PANHICAL".equals(tblname)) {
				if (editMode.equals("update")) {
					sql = " update " + this.tNhiCal
							+ " set (PAYITEM, PAYCARD, PAYNAMEC, MONINSU, MONINSU4, BONUSAMT, JACKPOT, MTINSU, SUPLYBASE, NHIAMT, INSUIDENT, PAYFLAG, PAYUPDID, PAYUPDAT, ZPANHICAL)"
							+ " =(?,?,?,?,?, ?,?,?,?,?, ?,?,?,current timestamp,current timestamp) where PAPAYDT=? and PAYSOURCE=? and ITEMNUM=? and PAYIDNO=? and HID=? ";
				} else if (editMode.equals("insert")) {
					sql = " insert into " + this.tNhiCal
							+ " (PAYITEM, PAYCARD, PAYNAMEC, MONINSU, MONINSU4, BONUSAMT, JACKPOT, MTINSU, SUPLYBASE, NHIAMT, INSUIDENT, PAYFLAG, PAYUPDID, PAYUPDAT, ZPANHICAL,PAPAYDT,PAYSOURCE,ITEMNUM,PAYIDNO,HID)"
							+ " values( ?,?,?,?,?, ?,?,?,?,?, ?,?,?,current timestamp,current timestamp,?,?,?,?,?) ";
				} else if (editMode.equals("delete")) {
					sql = " delete from " + this.tNhiCal
							+ " where PAPAYDT=? and PAYSOURCE=? and ITEMNUM=? and PAYIDNO=? and HID=? ";
				}
			}
			// 申報扣費明細 , PAPAYDT PAYIDNO, PAYITEM HID
			else if ("PANHIDET".equals(tblname)) {
				if (editMode.equals("update")) {
					sql = " update " + this.tNhiDet
							+ " set (PAYCARD, PAYNAMEC, SERIAL, PAYAMT, NHIAMT, MONINSU, JACKPOT, INSUIDENT, PAYFLAG, PAYUPDID, PAYUPDAT, ZPANHIDET)"
							+ " =(?,?,?,?,?, ?,?,?,?,?, current timestamp,current timestamp) where PAPAYDT=? and PAYIDNO=? and PAYITEM=? and HID=? ";
				} else if (editMode.equals("insert")) {
					sql = " insert into " + this.tNhiDet
							+ " (PAYCARD, PAYNAMEC, SERIAL, PAYAMT, NHIAMT, MONINSU, JACKPOT, INSUIDENT, PAYFLAG, PAYUPDID, PAYUPDAT, ZPANHIDET,PAPAYDT, PAYIDNO, PAYITEM,HID)"
							+ " values( ?,?,?,?,?, ?,?,?,?,?,current timestamp,current timestamp,?,?,?,?) ";
				} else if (editMode.equals("delete")) {
					sql = " delete from " + this.tNhiDet + " where PAPAYDT=? and PAYIDNO=? and PAYITEM=? and HID=? ";
				}
			}
			// 批次檔 PAtradt+PAYIDNO+salnum+paycode+HID
			else if ("PABATPAY".equals(tblname)) {
				if (editMode.equals("update")) {
					sql = " update " + this.tBatPay
							+ " set (PAYCARD, PAUPBAS, PAYAMT, PADUTYID, PAYUPDAT, PAYFLAG, UPMASTDT, " + tmpZbatpay
							+ ")"
							+ " =(?,?,?,?, current timestamp, ?,?, current timestamp) where PATRADT=? and PAYIDNO=? and SALNUM=? and PAYCODE=? and HID=? ";
				} else if (editMode.equals("insert")) {
					sql = " insert into " + this.tBatPay
							+ " (PAYCARD, PAUPBAS, PAYAMT, PADUTYID, PAYUPDAT, PAYFLAG, UPMASTDT, " + tmpZbatpay
							+ ", PATRADT, PAYIDNO, SALNUM, PAYCODE, HID)"
							+ " values( ?,?,?,?,current timestamp,?, ?,current timestamp,?,?,?,?,?) ";
				} else if (editMode.equals("delete")) {
					sql = " delete from " + this.tBatPay
							+ " where PATRADT=? and PAYIDNO=? and SALNUM=? and PAYCODE=? and HID=? ";
				}
			}
			// 異動檔 PAtradt+PAYIDNO+paytracod+HID
			// 不寫入PAYBKID, PAYACNO,PAYGEDNO,UPMASTDT, PAYPSW, payflag
			else if ("PATRAPSN".equals(tblname)) {
				sqlTranum_max = "select max(TRANUM) as MAX_TRANUM from " + this.tTraPsn + " where HID= ? with ur";
				sqlTrapsn_upd = " update " + this.tTraPsn
						+ " set (PAEFFDT, SALNUM, PAYLVEFG, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, PAYEMPDT, PAYSGNDT, PAYRSGDT, PAYINFLG, PAYDPNO, PAYPFLAG, PAYTITLE, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, PAYPFDOC, PACSFLAG, PAYPFSP1, PAYPFSP2, PAYPFSP3, PAYMONNO, PAYDAYNO, PAOTITLE, PAORNK2N, PAORANK3, PAODEGR2, PAODEGRE, PAORANK, PAORANK2, PAORANKC, PAOPFDOC, PAOCSFLA, PAOPFSP1, PAOPFSP2, PAOPFSP3, PAYTAXNO, PAYUPDID, PAYUPDTM, TRANUM, PAOEXPFKEY1, PAOEXPFKEY2, PAOEXPFKEY3, PAYEXPFKEY1, PAYEXPFKEY2, PAYEXPFKEY3, PAORANK4, PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, INSUAMT, "
						+ tmpZtrapsn + ")"
						+ " =(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,current timestamp,?,?,?,?,?,?,?,?,?,?,?,?,current timestamp) where PAYIDNO=? and PATRADT=? and PATRACOD=? and HID=? ";
				sqlTrapsn_ins = " insert into " + this.tTraPsn
						+ " (PAEFFDT, SALNUM, PAYLVEFG, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, PAYEMPDT, PAYSGNDT, PAYRSGDT, PAYINFLG, PAYDPNO, PAYPFLAG, PAYTITLE, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, PAYPFDOC, PACSFLAG, PAYPFSP1, PAYPFSP2, PAYPFSP3, PAYMONNO, PAYDAYNO, PAOTITLE, PAORNK2N, PAORANK3, PAODEGR2, PAODEGRE, PAORANK, PAORANK2, PAORANKC, PAOPFDOC, PAOCSFLA, PAOPFSP1, PAOPFSP2, PAOPFSP3, PAYTAXNO, PAYUPDID, PAYUPDTM, TRANUM, PAOEXPFKEY1, PAOEXPFKEY2, PAOEXPFKEY3, PAYEXPFKEY1, PAYEXPFKEY2, PAYEXPFKEY3, PAORANK4, PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, INSUAMT, "
						+ tmpZtrapsn + ",PAYIDNO,PATRADT, PATRACOD,  HID)"
						+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,current timestamp,?,?,?,?,?,?,?,?,?,?,?,?,current timestamp,?,?,?,?) ";
				// 異動檔(異動個人+異動支扣)全刪
				sqlTrapsn_del = "delete from " + this.tTraPsn
						+ " where HID=? and PAYIDNO=? and PATRADT=? and PATRACOD=? ";
				sqlTrapay_del = "delete from " + this.tTraPay
						+ " where HID=? and PAYIDNO=? and PATRADT=? and PAYSORT=?  ";
				sqlTrapay_cnt = "select count(*) cnt from " + this.tTraPay
						+ " where HID=? and PAYIDNO=? and PATRADT=? and PAYSORT=? ";
				// 支扣明細 payflag=''
				sqlTrapay_ins = "insert into " + this.tTraPay
						+ " (TRANUM, PAYAMT, PAYAUTH, PAYIFTAX, PAYFLAG,PAYUPDTM, " + tmpZtrapay
						+ ", PAYCODE, HID, PAYIDNO, PATRADT, PAYSORT)"
						+ " values(?,?,?,?,'',current timestamp,current timestamp,?,?,?,?,?) ";
			}
			// delete
			if (editMode.matches("delete")) {
				// 補充費_獎金
				if ("PANHICAL".equals(tblname)) {
					// key
					param.add(StringUtil.nullToString(object_pk_data.getString("papaydt")));
					param.add(StringUtil.nullToString(object_pk_data.getString("paysource")));
					param.add(StringUtil.nullToString(object_pk_data.getInt("itemnum")));
					param.add(StringUtil.nullToString(object_pk_data.getString("payidno")));
					param.add(StringUtil.nullToString(object_pk_data.getString("hid")));
					sql_stmts.add(sql);
					params.add(param);
				}
				// 申報扣費明細
				else if ("PANHIDET".equals(tblname)) {
					// key
					param.add(StringUtil.nullToString(object_pk_data.getString("papaydt")));
					param.add(StringUtil.nullToString(object_pk_data.getString("payidno")));
					param.add(StringUtil.nullToString(object_pk_data.getString("payitem")));
					param.add(StringUtil.nullToString(object_pk_data.getString("hid")));
					sql_stmts.add(sql);
					params.add(param);
				}
				// 批次費
				else if ("PABATPAY".equals(tblname)) {
					// key
					param.add(StringUtil.nullToString(object_pk_data.getString("patradt")));
					param.add(StringUtil.nullToString(object_pk_data.getString("payidno")));
					param.add(StringUtil.nullToString(object_pk_data.getString("salnum")));
					param.add(StringUtil.nullToString(object_pk_data.getString("paycode")));
					param.add(StringUtil.nullToString(object_pk_data.getString("hid")));
					sql_stmts.add(sql);
					params.add(param);
				}
				// 異動檔(異動個人+異動支扣)全刪
				else if ("PATRAPSN".equals(tblname)) {
					// sqlTrapsn_del = "delete from "+ this.tTraPsn + " where HID=? and PAYIDNO=?
					// and PATRADT=? and PATRACOD=? ";
					param = new ArrayList<Object>();
					param.add(StringUtil.nullToString(object_pk_data.getString("hid")));
					param.add(StringUtil.nullToString(object_pk_data.getString("payidno")));
					param.add(StringUtil.nullToString(object_pk_data.getString("patradt")));
					param.add(StringUtil.nullToString(object_pk_data.getString("patracod")));

					sql_stmts.add(sqlTrapsn_del);
					params.add(param);
					// sqlTrapay_del = "delete from "+ this.tTraPay + " where HID=? and PAYIDNO=?
					// and PATRADT=? and PAYSORT=? ";
					// 原支扣明細
					param = new ArrayList<Object>();
					param.add(StringUtil.nullToString(object_pk_data.getString("hid")));
					param.add(StringUtil.nullToString(object_pk_data.getString("payidno")));
					param.add(StringUtil.nullToString(object_pk_data.getString("patradt")));
					param.add("O");
					sql_stmts.add(sqlTrapay_del);
					params.add(param);
					// 新支扣明細
					param = new ArrayList<Object>();
					param.add(StringUtil.nullToString(object_pk_data.getString("hid")));
					param.add(StringUtil.nullToString(object_pk_data.getString("payidno")));
					param.add(StringUtil.nullToString(object_pk_data.getString("patradt")));
					param.add("N");
					sql_stmts.add(sqlTrapay_del);
					params.add(param);
				}
			}
			// insert/update
			else if (editMode.matches("insert|update")) {
				JSONArray tabJsonary = JSONArray.fromObject(tabJson);
				params = new ArrayList<Object>();
				paramA = new ArrayList<Object>();
				for (int i = 0; i < tabJsonary.size(); ++i) {
					JSONObject obj = tabJsonary.getJSONObject(i);
					if (obj != null && obj.has("formnm")) {
						formnm = StringUtil.nullToString(obj.getString("formnm"));
						blnTraPay = true;
						if ("PABASPSN".equals(formnm)) {// 基本檔
							array = obj.getJSONArray("value");
							if (null != array && array.size() > 0) {
								for (int j = 0; j < array.size(); j++) {
									obj_item = array.getJSONObject(j);

									param = new ArrayList<Object>();
									param.add((!obj_item.has("payempno")) ? "" : obj_item.getString("payempno"));
									param.add((!obj_item.has("paynamec")) ? "" : obj_item.getString("paynamec"));
									param.add((!obj_item.has("paydept")) ? "" : obj_item.getString("paydept"));
									param.add((!obj_item.has("paysgndt")) ? java.sql.Date.valueOf("9999-12-31")
											: ("".equals(obj_item.getString("paysgndt"))
													? java.sql.Date.valueOf("9999-12-31")
													: obj_item.getString("paysgndt")));
									param.add((!obj_item.has("paytaxno")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paytaxno"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paytaxno")));
									param.add((!obj_item.has("insuamt")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("insuamt"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("insuamt")));
									param.add((!obj_item.has("pacsflag")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("pacsflag"))) ? "N"
													: StringUtil.nullToString(obj_item.getString("pacsflag")));
									param.add((!obj_item.has("paretention")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paretention")))
													? ""
													: StringUtil.nullToString(obj_item.getString("paretention")));
									param.add((!obj_item.has("payexpfkey1")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("payexpfkey1")))
													? "0"
													: StringUtil.nullToString(obj_item.getString("payexpfkey1")));
									param.add((!obj_item.has("payexpfkey2")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("payexpfkey2")))
													? "0"
													: StringUtil.nullToString(obj_item.getString("payexpfkey2")));
									param.add((!obj_item.has("payexpfkey3")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("payexpfkey3")))
													? "0"
													: StringUtil.nullToString(obj_item.getString("payexpfkey3")));
									param.add((!obj_item.has("paypfsp1")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paypfsp1"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paypfsp1")));
									param.add((!obj_item.has("paypfsp2")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paypfsp2"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paypfsp2")));
									param.add((!obj_item.has("paypfsp3")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paypfsp3"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paypfsp3")));
									param.add((!obj_item.has("payctcbacno")) ? "" : obj_item.getString("payctcbacno"));
									param.add(
											(!obj_item.has("payctcbgedno")) ? "" : obj_item.getString("payctcbgedno"));
									param.add(userid);
									param.add(strKey);
									param.add(hid);
									// paramA.add((!obj_item.has("payaddrs")) ? "" :
									// StringUtil.toWideCase(StringUtil.trimWideEmpty((String)obj_item.getString("payaddrs"))));
									// paramA.add(strKey);

									sql_stmts.add(sql);
									params.add(param);

									strAddr = (!obj_item.has("payaddrs")) ? "" : obj_item.getString("payaddrs");
									if (!strAddr.equals("")) {
										param = new ArrayList<Object>();
										param.add(strAddr);
										param.add(strKey);
										String sqlAddr = blnAdr ? sqlAdr_upd : sqlAdr_ins;
										sql_stmts.add(sqlAddr);
										params.add(param);
									}
								}
							}
						} // 補充費_獎金
						else if ("PANHICAL".equals(formnm)) {
							array = obj.getJSONArray("value");
							if (null != array && array.size() > 0) {
								for (int j = 0; j < array.size(); j++) {
									obj_item = array.getJSONObject(j);

									param.add((!obj_item.has("payitem")) ? "" : obj_item.getString("payitem"));
									param.add((!obj_item.has("paycard")) ? "" : obj_item.getString("paycard"));
									param.add((!obj_item.has("paynamec")) ? "" : obj_item.getString("paynamec"));
									param.add((!obj_item.has("moninsu")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("moninsu"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("moninsu")));
									param.add((!obj_item.has("moninsu4")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("moninsu4"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("moninsu4")));
									param.add((!obj_item.has("bonusamt")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("bonusamt"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("bonusamt")));
									param.add((!obj_item.has("jackpot")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("jackpot"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("jackpot")));
									param.add((!obj_item.has("mtinsu")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("mtinsu"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("mtinsu")));
									param.add((!obj_item.has("suplybase")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("suplybase")))
													? "0"
													: StringUtil.nullToString(obj_item.getString("suplybase")));
									param.add((!obj_item.has("nhiamt")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("nhiamt"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("nhiamt")));
									param.add((!obj_item.has("insuident")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("insuident")))
													? "0"
													: StringUtil.nullToString(obj_item.getString("insuident")));
									param.add((!obj_item.has("payflag")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("payflag"))) ? "N"
													: StringUtil.nullToString(obj_item.getString("payflag")));
									param.add(userid);
									// key
									param.add(StringUtil.nullToString(object_pk_data.getString("papaydt")));
									param.add(StringUtil.nullToString(object_pk_data.getString("paysource")));
									param.add(StringUtil.nullToString(object_pk_data.getInt("itemnum")));
									param.add(StringUtil.nullToString(object_pk_data.getString("payidno")));
									param.add(StringUtil.nullToString(object_pk_data.getString("hid")));

									sql_stmts.add(sql);
									params.add(param);
								}
							}
						}
						// 申報扣費明細
						else if ("PANHIDET".equals(formnm)) {
							array = obj.getJSONArray("value");
							if (null != array && array.size() > 0) {
								for (int j = 0; j < array.size(); j++) {
									obj_item = array.getJSONObject(j);

									param.add((!obj_item.has("paycard")) ? "" : obj_item.getString("paycard"));
									param.add((!obj_item.has("paynamec")) ? "" : obj_item.getString("paynamec"));
									param.add((!obj_item.has("serial")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("serial"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("serial")));
									param.add((!obj_item.has("payamt")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("payamt"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("payamt")));
									param.add((!obj_item.has("nhiamt")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("nhiamt"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("nhiamt")));
									param.add((!obj_item.has("moninsu")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("moninsu"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("moninsu")));
									param.add((!obj_item.has("jackpot")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("jackpot"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("jackpot")));
									param.add((!obj_item.has("insuident")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("insuident")))
													? "0"
													: StringUtil.nullToString(obj_item.getString("insuident")));
									param.add((!obj_item.has("payflag")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("payflag"))) ? "N"
													: StringUtil.nullToString(obj_item.getString("payflag")));
									param.add(userid);

									// key
									param.add(StringUtil.nullToString(object_pk_data.getString("papaydt")));
									param.add(StringUtil.nullToString(object_pk_data.getString("payidno")));
									param.add(StringUtil.nullToString(object_pk_data.getString("payitem")));
									param.add(StringUtil.nullToString(object_pk_data.getString("hid")));

									sql_stmts.add(sql);
									params.add(param);
								}
							}
						}
						// 批次檔 paycard, paupbas, payamt, padutyid, payupdat, payflag, upmastdt,
						// zpabatpay
						else if ("PABATPAY".equals(formnm)) {
							array = obj.getJSONArray("value");
							if (null != array && array.size() > 0) {
								for (int j = 0; j < array.size(); j++) {
									obj_item = array.getJSONObject(j);

									param.add((!obj_item.has("paycard")) ? "" : obj_item.getString("paycard"));
									param.add((!obj_item.has("paupbas")) ? "" : obj_item.getString("paupbas"));
									param.add((!obj_item.has("payamt")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("payamt"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("payamt")));
									param.add(userid);
									param.add("");
									param.add("");
									// key
									param.add(StringUtil.nullToString(object_pk_data.getString("patradt")));
									param.add(StringUtil.nullToString(object_pk_data.getString("payidno")));
									param.add((!object_pk_data.has("salnum")) ? ""
											: ("").equals(StringUtil.nullToString(object_pk_data.getString("salnum")))
													? "0"
													: StringUtil.nullToString(object_pk_data.getString("salnum")));
									param.add(StringUtil.nullToString(object_pk_data.getString("paycode")));
									param.add(StringUtil.nullToString(object_pk_data.getString("hid")));

									sql_stmts.add(sql);
									params.add(param);
								}
							}
						}
						// 異動檔--個人明細
						else if ("PATRAPSN".equals(formnm)) {
							// 找地址
							paramA = new ArrayList<Object>();
							paramA.add(StringUtil.nullToString(object_pk_data.getString("payidno")));
							log.info(me + "  " + sqlAdr_inq + "...params:" + paramA.toString());
							rs = da.query(sqlAdr_inq, paramA);
							blnAdr = false;
							while (rs.next()) {
								log.info(me + " 地址-> " + rs.getString("PAYIDNO"));
								blnAdr = true;
							}
							// insert--取得tranum最大值
							if (editMode.matches("insert")) {
								paramA = new ArrayList<Object>();
								paramA.add(hid);
								log.info(me + "  " + sqlTranum_max + "...params:" + paramA.toString());
								rs = da.query(sqlTranum_max, paramA);
								while (rs.next()) {
									intTraNum_max = rs.getInt("MAX_TRANUM");
								}
								log.info(me + "  intTraNum_max:" + intTraNum_max);
								sql_stmts.add(sqlTrapsn_ins);
							} else {
								sql_stmts.add(sqlTrapsn_upd);
							}

							array = obj.getJSONArray("value");
							if (null != array && array.size() > 0) {
								for (int j = 0; j < array.size(); j++) {
									obj_item = array.getJSONObject(j);
									// paeffdt, salnum, paylvefg, paycard, payempno,
									param.add((!obj_item.has("paeffdt")) ? java.sql.Date.valueOf("9999-12-31")
											: ("".equals(obj_item.getString("paeffdt"))
													? java.sql.Date.valueOf("9999-12-31")
													: obj_item.getString("paeffdt")));
									param.add((!obj_item.has("salnum")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("salnum"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("salnum")));
									param.add((!obj_item.has("paylvefg")) ? "" : obj_item.getString("paylvefg"));
									param.add((!obj_item.has("paycard")) ? "" : obj_item.getString("paycard"));
									param.add((!obj_item.has("payempno")) ? "" : obj_item.getString("payempno"));
									// paynamec, paydept, paygovdt, payempdt, paysgndt,
									param.add((!obj_item.has("paynamec")) ? "" : obj_item.getString("paynamec"));
									param.add((!obj_item.has("paydept")) ? "" : obj_item.getString("paydept"));
									param.add((!obj_item.has("paygovdt")) ? java.sql.Date.valueOf("9999-12-31")
											: ("".equals(obj_item.getString("paygovdt"))
													? java.sql.Date.valueOf("9999-12-31")
													: obj_item.getString("paygovdt")));
									param.add((!obj_item.has("payempdt")) ? java.sql.Date.valueOf("9999-12-31")
											: ("".equals(obj_item.getString("payempdt"))
													? java.sql.Date.valueOf("9999-12-31")
													: obj_item.getString("payempdt")));
									param.add((!obj_item.has("paysgndt")) ? java.sql.Date.valueOf("9999-12-31")
											: ("".equals(obj_item.getString("paysgndt"))
													? java.sql.Date.valueOf("9999-12-31")
													: obj_item.getString("paysgndt")));
									// payrsgdt, *paybkid, *payacno, *paygedno, payinflg,paydpno, paypflag,
									param.add((!obj_item.has("payrsgdt")) ? java.sql.Date.valueOf("9999-12-31")
											: ("".equals(obj_item.getString("payrsgdt"))
													? java.sql.Date.valueOf("9999-12-31")
													: obj_item.getString("payrsgdt")));
									// param.add((!obj_item.has("paygedno")) ? "" : obj_item.getString("paygedno"));
									param.add((!obj_item.has("payinflg")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("payinflg"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("payinflg")));
									param.add((!obj_item.has("paydpno")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paydpno"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paydpno")));
									param.add((!obj_item.has("paypflag")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paypflag"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paypflag")));
									// paytitle, payrnk2n, payrank3, paydegr2, paydegre,
									param.add((!obj_item.has("paytitle")) ? "" : obj_item.getString("paytitle"));
									param.add((!obj_item.has("payrnk2n")) ? "" : obj_item.getString("payrnk2n"));
									param.add((!obj_item.has("payrank3")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("payrank3"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("payrank3")));
									param.add((!obj_item.has("paydegr2")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paydegr2"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paydegr2")));
									param.add((!obj_item.has("paydegre")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paydegre"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paydegre")));
									// payrank, payrank2, payrankc, paypfdoc, pacsflag,
									param.add((!obj_item.has("payrank")) ? "" : obj_item.getString("payrank"));
									param.add((!obj_item.has("payrank2")) ? "" : obj_item.getString("payrank2"));
									param.add((!obj_item.has("payrankc")) ? "" : obj_item.getString("payrankc"));
									param.add((!obj_item.has("paypfdoc")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paypfdoc"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paypfdoc")));
									param.add((!obj_item.has("pacsflag")) ? "" : obj_item.getString("pacsflag"));
									// paypfsp1, paypfsp2, paypfsp3, paymonno, paydayno,
									param.add((!obj_item.has("paypfsp1")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paypfsp1"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paypfsp1")));
									param.add((!obj_item.has("paypfsp2")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paypfsp2"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paypfsp2")));
									param.add((!obj_item.has("paypfsp3")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paypfsp3"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paypfsp3")));
									param.add((!obj_item.has("paymonno")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paymonno"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paymonno")));
									param.add((!obj_item.has("paydayno")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paydayno"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paydayno")));
									// paotitle, paornk2n, paorank3, paodegr2, paodegre,
									param.add((!obj_item.has("paotitle")) ? "" : obj_item.getString("paotitle"));
									param.add((!obj_item.has("paornk2n")) ? "" : obj_item.getString("paornk2n"));
									param.add((!obj_item.has("paorank3")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paorank3"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paorank3")));
									param.add((!obj_item.has("paodegr2")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paodegr2"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paodegr2")));
									param.add((!obj_item.has("paodegre")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paodegre"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paodegre")));
									// paorank, paorank2, paorankc, paopfdoc, paocsfla,
									param.add((!obj_item.has("paorank")) ? "" : obj_item.getString("paorank"));
									param.add((!obj_item.has("paorank2")) ? "" : obj_item.getString("paorank2"));
									param.add((!obj_item.has("paorankc")) ? "" : obj_item.getString("paorankc"));
									param.add((!obj_item.has("paopfdoc")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paopfdoc"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paopfdoc")));
									param.add((!obj_item.has("paocsfla")) ? "" : obj_item.getString("paocsfla"));
									// paopfsp1, paopfsp2, paopfsp3, paytaxno, payupdid,
									param.add((!obj_item.has("paopfsp1")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paopfsp1"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paopfsp1")));
									param.add((!obj_item.has("paopfsp2")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paopfsp2"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paopfsp2")));
									param.add((!obj_item.has("paopfsp3")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paopfsp3"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paopfsp3")));
									param.add((!obj_item.has("paytaxno")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paytaxno"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paytaxno")));
									param.add(userid);
									// payupdtm, *payflag, +tranum+, *upmastdt, *paypsw,paoexpfkey1, paoexpfkey2,
									// paoexpfkey3
									// param.add((!obj_item.has("payflag")) ? "" : obj_item.getString("payflag"));
									if (editMode.matches("insert")) {
										intTraNum = intTraNum_max + 1; // 2026.8.3fix
									} else {
										intTraNum = (Integer) (!obj_item.has("tranum") ? ""
												: ("").equals(StringUtil.nullToString(obj_item.getString("tranum"))) ? 0
														: obj_item.getInt("tranum"));
									}
									param.add(intTraNum);
									param.add((!obj_item.has("paoexpfkey1")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paoexpfkey1")))
													? "0"
													: StringUtil.nullToString(obj_item.getString("paoexpfkey1")));
									param.add((!obj_item.has("paoexpfkey2")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paoexpfkey2")))
													? "0"
													: StringUtil.nullToString(obj_item.getString("paoexpfkey2")));
									param.add((!obj_item.has("paoexpfkey3")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paoexpfkey3")))
													? "0"
													: StringUtil.nullToString(obj_item.getString("paoexpfkey3")));
									// payexpfkey1, payexpfkey2,payexpfkey3, paorank4, payrank4,
									param.add((!obj_item.has("payexpfkey1")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("payexpfkey1")))
													? "0"
													: StringUtil.nullToString(obj_item.getString("payexpfkey1")));
									param.add((!obj_item.has("payexpfkey2")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("payexpfkey2")))
													? "0"
													: StringUtil.nullToString(obj_item.getString("payexpfkey2")));
									param.add((!obj_item.has("payexpfkey3")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("payexpfkey3")))
													? "0"
													: StringUtil.nullToString(obj_item.getString("payexpfkey3")));
									param.add((!obj_item.has("paorank4")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("paorank4"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("paorank4")));
									param.add((!obj_item.has("payrank4")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("payrank4"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("payrank4")));
									// payctcbacno, payctcbgedno, insuamt, *zpatrapsn,
									param.add((!obj_item.has("payctcbacno")) ? "" : obj_item.getString("payctcbacno"));
									param.add(
											(!obj_item.has("payctcbgedno")) ? "" : obj_item.getString("payctcbgedno"));
									param.add((!obj_item.has("insuamt")) ? ""
											: ("").equals(StringUtil.nullToString(obj_item.getString("insuamt"))) ? "0"
													: StringUtil.nullToString(obj_item.getString("insuamt")));
									// key payidno,patradt, patracod, hid
									param.add(StringUtil.nullToString(object_pk_data.getString("payidno")));
									param.add(StringUtil.nullToString(object_pk_data.getString("patradt")));
									param.add(StringUtil.nullToString(object_pk_data.getString("patracod")));
									param.add(StringUtil.nullToString(object_pk_data.getString("hid")));

									params.add(param);

									// 地址,空白不處理
									strAddr = (!obj_item.has("payaddrs")) ? ""
											: StringUtil.toWideCase(StringUtil.trimWideEmpty(
													StringUtil.nullToString(obj_item.getString("payaddrs"))));
									if (!strAddr.equals("")) {
										param = new ArrayList<Object>();
										param.add(strAddr);
										param.add((!obj_item.has("payidno")) ? "" : obj_item.getString("payidno"));
										if (blnAdr)
											sql = sqlAdr_upd;
										else
											sql = sqlAdr_ins;

										sql_stmts.add(sql);
										params.add(param);
									}
								}
							}
						}
						// 地址
						else if ("PABASADR".equals(formnm)) {
							array = obj.getJSONArray("value");
							if (null != array && array.size() > 0) {
								for (int j = 0; j < array.size(); j++) {
									obj_item = array.getJSONObject(j);
									params.add((!obj_item.has("payaddrs")) ? ""
											: StringUtil.toWideCase(StringUtil.trimWideEmpty(
													StringUtil.nullToString(obj_item.getString("payaddrs")))));
									params.add((!obj_item.has("payidno")) ? "" : obj_item.getString("payidno"));
									if (editMode.matches("insert")) {
										sql = sqlAdr_ins;
									} else if (editMode.matches("update")) {
										sql = sqlAdr_upd;
									}
									sql_stmts.add(sql);
									params.add(param);
								}
							}
						}
						// 異動檔--支扣明細
						// 原支,insert要寫入,Update不處理, 新支(insert/update)先刪除,再新增,
						else if (formnm.matches("TraPayO|TraPayN")) {
							array = obj.getJSONArray("value");
							if (null != array && array.size() > 0) {
								for (int j = 0; j < array.size(); j++) {
									obj_item = array.getJSONObject(j);
									// 筆數=1,paycode="",表示空白記錄,不用寫入
									if (array.size() == 1 && !obj_item.has("paycode")) {

									} else {
										// 1.TraPayO 先查詢,若有,不用處理,若無,insert
										// 2.TraPayN 先刪除,再全部insert
										if (j == 0) {
											if (formnm.matches("TraPayO")) {
												param = new ArrayList<Object>();
												// sqlTrapay_inq = "select TRANUM, PAYAMT, PAYAUTH, PAYIFTAX, PAYUPDTM,
												// PAYIDNO, PATRADT, PAYSORT, PAYCODE, HID from "+ this.tTraPay +
												// " where HID=? and PAYIDNO=? and PATRADT=? and PAYSORT=? ";
												param.add(hid);
												param.add(StringUtil.nullToString(object_pk_data.getString("payidno")));
												param.add(StringUtil.nullToString(object_pk_data.getString("patradt")));
												param.add("O");
												// sqlTrapay_cnt = "select count(*) cnt from "+ this.tTraPay + " where
												// HID=? and PAYIDNO=? and PATRADT=? and PAYSORT=? ";
												log.info(me + "  " + sqlTrapay_cnt + "...params:" + param.toString());
												rs = da.query(sqlTrapay_cnt, param);
												blnTraPay = true;
												if (rs.next()) {
													if (rs.getInt("cnt") != 0) {
														blnTraPay = false;
													}
												}
												log.info(me + "  blnTraPayO:" + blnTraPay);
											} else if (formnm.matches("TraPayN")) {
												// sqlTrapay_del = "delete from "+ this.tTraPay + " where HID=? and
												// PAYIDNO=? and PATRADT=? and PAYSORT=? ";
												param = new ArrayList<Object>();
												param.add(hid);
												param.add(StringUtil.nullToString(object_pk_data.getString("payidno")));
												param.add(StringUtil.nullToString(object_pk_data.getString("patradt")));
												param.add("N");
												log.info(me + " " + formnm + " " + sqlTrapay_del + "...params:"
														+ param.toString());
												sql_stmts.add(sqlTrapay_del);
												params.add(param);
											}
										}
										// insert
										// sqltrapay_ins= "insert into "+ this.ttrapay + " (tranum, payamt, payauth,
										// payiftax, payupdtm, "+
										// tmpztrapay+", paycode, hid, payidno, patradt, paysort)" +
										// " values(?,?,?,?,current timestamp,current timestamp,?,?,?,?,?) ";
										if (blnTraPay) {
											if (formnm.matches("TraPayN")) {
												// paycode:pauauth:payiftax
												aryPaycode = null;
												strPaycode = "";
												strPayauth = "";
												strPayiftax = "";
												strPaysort = "N";
												if (obj_item.has("paycode")) {
													aryPaycode = obj_item.getString("paycode").split(":");
													strPaycode = aryPaycode[0];
													strPayauth = aryPaycode[1];
													strPayiftax = aryPaycode[2];
												}
											} else {
												strPaycode = (!obj_item.has("paycode")) ? ""
														: obj_item.getString("paycode");
												strPayauth = (!obj_item.has("payauth")) ? ""
														: obj_item.getString("payauth");
												strPayiftax = (!obj_item.has("payiftax")) ? ""
														: obj_item.getString("payiftax");
												strPaysort = "O";
											}
											param = new ArrayList<Object>();
											param.add(intTraNum);
											param.add((!obj_item.has("payamt")) ? 0
													: ("").equals(StringUtil.nullToString(obj_item.getString("payamt")))
															? "0"
															: StringUtil.nullToString(obj_item.getString("payamt")));
											param.add(strPayauth);
											param.add(strPayiftax);
											param.add(strPaycode);
											param.add(hid);
											param.add(StringUtil.nullToString(object_pk_data.getString("payidno")));
											param.add(StringUtil.nullToString(object_pk_data.getString("patradt")));
											param.add(strPaysort);

											sql_stmts.add(sqlTrapay_ins);
											params.add(param);
											log.info(me + " " + formnm + " " + sqlTrapay_ins + "...params:"
													+ param.toString());
										}
									}
								}
							}
						} // else if (formnm.matches("TraPayO|TraPayN")){
					} // if ( obj != null && obj.has("formnm") ) {
				} // for (int i=0; i< tabJsonary.size(); ++i) {
			} // editMode.matches("insert|update"

			log.info(me + "sql:" + sql_stmts + " ...params:" + params.toString());
			if (sql_stmts.size() > 0) {
				da.startTransaction();
				try {
					da.batchupdatenocommit(sql_stmts, params);
					da.commit();
					success = true;
				} catch (Exception e) {
					da.rollback();
					da.setAutoCommit(true);
					e.printStackTrace();
					log.info(me + " " + tblname + " " + e.toString());
				} finally {
					closeAll(da, null, connFlg);
				}
			}

		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {

		}
		return success;
	}

	/**
	 * 銀行回傳比對(銀行回傳的薪津有含停薪) 傳入:vobjs,tradt發薪日,salnum餉單編號,filename,檔案,hid院區
	 * selNm:薪資Bank_PayC,公儲:Bank_PayS 傳出:rtn文字訊息
	 **/
	public String impByBankC(ArrayList<Paprtdtl> vobjs, String tradt, String salnum, String selEn, String filename,
			String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".impTaxByBat(),userid = " + userid;
		String rtn = "";
		String outputstring = "";
		Paprtdtl oneobj = null;
		HashMap<String, Paprtdtl> prtdtlMap = new HashMap();
		HashMap<String, Paprtdtl> stopMap = new HashMap();
		StringBuilder sb = new StringBuilder();
		BufferedWriter bfw = null;
		Writer output = null;
		String strYM = "";
		String strIdno = "";
		String strLine = "";
		String strComp = "";
		String strCompNM = "";
		long starttime = 0;
		int docnt = 0;
		ArrayList<Object> paramA = new ArrayList<Object>();
		String sqlPrtdtl = "select PAYIDNO, PAYBANK, PAYCARD, PAYNAMEC, PAYACNO, PAYGEDNO,HID, PAYNET, PAYTOTIS,PAYGEDP "
				+ " from " + this.tRPDTL + " where HID= ? and SALNUM=? ";
		if ("Bank_PayC".equals(selEn)) {
			sqlPrtdtl += " and (PAYNET + PAYTOTIS) > 0 AND (PAYACNO IS NOT NULL and PAYACNO<>'') with ur";
		} else if ("Bank_PayS".equals(selEn)) {
			sqlPrtdtl += " and PAYGEDP > 0 AND (PAYGEDNO IS NOT NULL and PAYGEDNO<>'') with ur";
		}
		String strSelect = "PAYIDNO, PAYCARD, PAYNAMEC, PAYCTCBACNO AS PAYACNO, '' as PAYGEDNO,HID, 0 PAYNET, 0 PAYTOTIS,0 PAYGEDP";

		String sqlStop = "select " + strSelect + " from " + this.tTraPsn
				+ " where HID=? and SALNUM=? and PATRACOD in (23,24) " + " union" + " select " + strSelect + " from "
				+ this.tBasPsn + " where PATRADT >= '2007-9-1' AND PATRACOD in (23,24) AND HID=? "
				+ "  AND PAYIDNO NOT IN (SELECT PAYIDNO FROM " + this.tTraPsn
				+ " WHERE PATRACOD=2 AND HID=? and SALNUM=?  ) " + " with ur";
		try {
			Hospitals hospInfo = Hospitals.getOneHosp(hid);
			if (da == null) {
				// 改用列舉
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			strYM = tradt.substring(0, 4) + tradt.substring(5, 7);
			// 寫入資料夾設定
			FileUtil fileUtil = new FileUtil();
			String folderPath = hospInfo.fileSRV + "bank\\" + strYM + "\\";
			filename = filename.substring(0, filename.lastIndexOf(".") == -1 ? 0 : filename.lastIndexOf("."));
			String filePath = folderPath + filename + "log.csv";
			log.info(me + "  0.資料夾: " + filePath);

			if (!FileUtil.FileToSmbFile(folderPath, hid).exists()) {
				fileUtil.FileToSmbFile(folderPath, hid).mkdirs();
			}
			// 建立檔案
			SmbFile payfile = fileUtil.FileToSmbFile(filePath, hid);
			bfw = new BufferedWriter(new OutputStreamWriter(new SmbFileOutputStream(payfile, false), "Big5"));

			// sb.setLength(0);

			// 找報表檔,寫入map
			starttime = System.currentTimeMillis();
			paramA = new ArrayList<Object>();
			paramA.add(hid);
			paramA.add(salnum);
			log.info(me + "  1.找報表檔,寫入map " + sqlPrtdtl + "...params:" + paramA.toString());
			result = da.arraylistbyQuery(sqlPrtdtl, paramA, META_TYPE);
			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					oneobj = new Paprtdtl();
					PropertyUtils.copyProperties(oneobj, data);
					strIdno = StringUtil.nullToString(oneobj.getPayidno());

					prtdtlMap.put(strIdno, oneobj);
				}
			}
			log.info(me + "  1.找報表檔,寫入map,size: " + result.size() + " 共花費：" + (System.currentTimeMillis() - starttime)
					+ "毫秒");

			// 找停薪,薪津檔才寫入map
			if ("Bank_PayC".equals(selEn)) {
				starttime = System.currentTimeMillis();
				paramA = new ArrayList<Object>();
				paramA.add(hid);
				paramA.add(salnum);
				paramA.add(hid);
				paramA.add(hid);
				paramA.add(salnum);
				log.info(me + "  1-1.找停薪,寫入map " + sqlStop + "...params:" + paramA.toString());
				result = da.arraylistbyQuery(sqlStop, paramA, META_TYPE);
				if (result.size() > 0) {
					for (Map<String, Object> data : result) {
						oneobj = new Paprtdtl();
						PropertyUtils.copyProperties(oneobj, data);
						strIdno = StringUtil.nullToString(oneobj.getPayidno());

						stopMap.put(strIdno, oneobj);
					}
				}
				log.info(me + "  1-1.找停薪,寫入map,size: " + result.size() + " 共花費："
						+ (System.currentTimeMillis() - starttime) + "毫秒");
			}
			// 2.讀取alPrtDtl ,找報表檔,找到,比對,一一寫入log
			starttime = System.currentTimeMillis();
			log.info(me + "  2.讀取alPrtDtl ,找報表檔,找到,比對,一一寫入log,並寫入obj.comp ");
			for (Paprtdtl itm : vobjs) {
				if (itm != null) {
					++docnt;
					strIdno = StringUtil.nullToString(itm.getPayidno());
					// 停薪=金額為0,改抓stopmap
					if (itm.getPayamt() == 0) {
						strLine = "=\"" + itm.getPayacno() + "\","
								+ StringUtil.addZero(Integer.toString(itm.getPayamt()), 10) + "," + itm.getPayidno()
								+ "," + itm.getBankflag() + ",";
						oneobj = (Paprtdtl) stopMap.get(strIdno);
						if (oneobj != null) {
							// 有比對到,寫入Y
							oneobj.setComp("Y");
							strLine += "停薪找到";
						} else {
							strLine += "停薪沒找到";
						}
						/*
						 * sb.append(strLine); sb.append(newline);
						 */
						bfw.write(strLine);
						bfw.newLine();
						bfw.flush();
					} else {
						oneobj = (Paprtdtl) prtdtlMap.get(strIdno);
						if (oneobj != null) {
							// 有比對到,寫入Y
							oneobj.setComp("Y");
							strComp = "符合";
							strCompNM = "";
							// 薪津
							if ("Bank_PayC".equals(selEn)) {
								strLine = "=\"" + itm.getPayacno() + "\","
										+ StringUtil.addZero(Integer.toString(itm.getPayamt()), 10) + ","
										+ itm.getPayidno() + "," + itm.getBankflag() + ",";
								if (!StringUtil.nullToString(oneobj.getPayacno())
										.equals(StringUtil.nullToString(itm.getPayacno()))) {
									strComp = "不符合";
									strCompNM = "帳號不符";
								}
								if (itm.getBankflag().equals("2")) {
									strComp = "不符合";
									strCompNM += "銀行列為帳號不符合";
								}
								if (itm.getPayamt() != (oneobj.getPaynet() + oneobj.getPaytotis())) {
									strComp = "不符合";
									strCompNM += "金額不符";
								}
							}
							// 公儲
							else if ("Bank_PayS".equals(selEn)) {
								strLine = "=\"" + itm.getPaygedno() + "\","
										+ StringUtil.addZero(Integer.toString(itm.getPayamt()), 10) + ","
										+ itm.getPayidno() + "," + itm.getBankflag() + ",";
								strComp = "符合";
								oneobj.setComp("Y");
								if (!StringUtil.nullToString(oneobj.getPaygedno())
										.equals(StringUtil.nullToString(itm.getPaygedno()))) {
									strComp = "不符合";
									strCompNM = "帳號不符";
								}
								if (itm.getBankflag().equals("2")) {
									strComp = "不符合";
									strCompNM += "銀行列為帳號不符合";
								}
								if (itm.getPayamt() != oneobj.getPaygedp()) {
									strComp = "不符合";
									strCompNM += "金額不符";
								}
							}
						} else {
							strLine = "," + "," + itm.getPayidno() + "," + itm.getBankflag() + ",";
							strComp = "不符合";
							strCompNM = "身分證找不到";
						}
						strLine += strComp + "," + strCompNM;
						/*
						 * sb.append(strLine); sb.append(newline);
						 */
						bfw.write(strLine);
						bfw.newLine();
						bfw.flush();
						strComp = "";
						strCompNM = "";
						strLine = "";
					}
				}
			}
			// 找出prtdtlMap,沒註記
			log.info(me + "  2.找出alPrtDtl,沒註記 ");
			// 使用 foreach 迴圈遍歷 HashMap
			for (Map.Entry<String, Paprtdtl> entry : prtdtlMap.entrySet()) {
				System.out.println(entry.getKey() + " : " + entry.getValue());
				Paprtdtl data = entry.getValue();
				if (data != null) {
					if (!"Y".equals(data.getComp())) {
						// 薪津
						if ("Bank_PayC".equals(selEn))
							strLine = data.getPayacno() + "," + Integer.toString(data.getPaynet() + data.getPaytotis())
									+ "," + data.getPayidno() + "," + ",不符合,高榮有_銀行無資料";
						else if ("Bank_PayS".equals(selEn))
							strLine = data.getPaygedno() + "," + data.getPaygedp() + "," + data.getPayidno() + ","
									+ ",不符合,高榮有_銀行無資料";
						/*
						 * sb.append(strLine); sb.append(newline);
						 */
						bfw.write(strLine);
						bfw.newLine();
						bfw.flush();
					}
				}
			}

			log.info(me + "  ...********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
			rtn = "寫入筆數:" + docnt + " ********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒";
			/*
			 * starttime = System.currentTimeMillis();
			 * log.info(me+"  開始寫出文字檔："+filePath+"..."); outputstring = sb.toString(); File
			 * file = new File(filePath); output = new BufferedWriter(new FileWriter(file));
			 * System.out.println("開始寫出文字檔 "+filePath+"..."); output.write(outputstring);
			 * output.close();
			 * log.info(me+"  ...********* 寫入成功...共花費："+(System.currentTimeMillis()-
			 * starttime)+"毫秒");
			 */
		} catch (Exception e) {
			da.rollback();

			rtn = " fail, " + e;
			da.setAutoCommit(true);
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			if (bfw != null)
				bfw.close();
			closeAll(da, null, connFlg);
		}
		return rtn;
	}

	/**
	 * 批次人工入稅 傳入:vobjs,intYY年,userid使用者,hid院區 傳出:rtn文字訊息
	 **/
	public String impTaxByBat(ArrayList<Pataximp> vobjs, int intYY, String filename, String userid, String hid,
			VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".impTaxByBat(),userid = " + userid;
		StringBuilder sb = new StringBuilder();
		BufferedWriter bfw = null;
		Writer output = null;
		String outputstring = "";
		String rtn = "";
		Pataximp oldobj = null;

		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> paramA = new ArrayList<Object>();
		ArrayList<Object> obj = new ArrayList<Object>();
		ArrayList<Object> batch_sql = new ArrayList<Object>(); // batch_update sql 資料
		HashMap taxpsnIdMap = new HashMap();
		HashMap taxpsnCardMap = new HashMap();
		HashMap baspsnIdMap = new HashMap();
		HashMap baspsnCardMap = new HashMap();
		HashMap taxCashMap = new HashMap();
		HashMap baspsnAdrMap = new HashMap();
		Pataxpsn taxpsnobj = null; // 所得稅個人obj
		Pataxcash taxcashobj = null; // 所得稅_人工obj
		Pabaspsn baspsnobj = null; // 基本檔個人obj
		Pabasadr basadrobj = null; // 基本檔地址obj
		String strIdno = "";
		String strCita = "";
		String strMon = "";
		String strCatg = ""; // 文字檔_轉帳項目
		String mCatg = ""; // 轉帳項目新值
		String tmpCatg = ""; // obj_轉帳項目
		String strCard = "";
		String tmpIdno = "";
		String tmpCita = "";
		String tmpMon = "";
		String tmpCard = "";
		String strMsg = "";
		int limitcnt = 100;
		int write_cnt = 0;// 寫入table第N筆
		int docnt = 0; // 執行第N筆
		long starttime = 0;
		long starttimeM = 0;
		int int_year = 0;
		int int_month = 0;
		boolean blnTaxPsn = true;
		boolean blnFlag = true;
		java.sql.Date mEnd = null;
		java.sql.Date mBegin = null;
		String sqlTaxPsn_end_upd = " update " + this.tTaxPsn
				+ " set (PAYEND,PAYUPDID,PAYUPDTM) = (?, ?, current timestamp) "
				+ " where PAYIDNO =? and PAYEAR =? and HID=?";
		String sqlTaxPsn_card_upd = " update " + this.tTaxPsn
				+ " set (PAYCARD,PAYUPDID,PAYUPDTM) = (?, ?, current timestamp) "
				+ " where PAYIDNO =? and PAYEAR =? and HID=?";
		String sqlTaxPsn_ins = " insert into " + this.tTaxPsn
				+ " (PAYIDNO,PAYEAR,PAYCARD,PAYNAMEC,PAYBEGIN,PAYEND,PAYUPDID,HID,PAYUPDTM,PAYDEPT,PAYIDFLG,PAYSERNO) "
				+ "values (?,?,?,?,?,?,?,?,current timestamp,'','',0) ";
		String sqlTaxPsn = "select PAYIDNO, PAYEAR, PAYCARD, PAYNAMEC, PAYDEPT, COALESCE(PAYIDFLG,'')PAYIDFLG, PAYBEGIN, PAYEND, COALESCE(PAYSERNO,0) PAYSERNO, PAYUPDID, PAYUPDTM, PAYSERNO9A, PAYSERNO9B, PAYSEREAMT, PAYSERNO92, PAYSERNO91, HID, PAYEMPNO, PAYSERNO51 "
				+ " from " + this.tTaxPsn + " where PAYEAR = ? and HID= ?  with ur";
		String sqlBasPsn = "select PAYLVEFG, PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, PAYEMPDT, PAYSGNDT, PAYRSGDT, PAYTITLE, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, HID "
				+ " from " + this.tBasPsn + " where HID= ?  with ur";
		String sqlBasAdr = "select PAYIDNO, PAYADDRS  from " + this.tBasAdr + " with ur";
		String sqlBasAdr_ins = "INSERT INTO " + this.tBasAdr + " (PAYADDRS,PAYIDNO) VALUES(?, ?);";
		String sqlBasAdr_upd = "update " + this.tBasAdr + " set (PAYADDRS) = (?) where PAYIDNO=? ";
		String sqlTaxCash = "select payidno,paycitation,paymonth, MAX(PAYCATG) AS PAYCATG " + " from " + this.tTaxCash
				+ " where PAYEAR = ? and HID= ? GROUP by payidno,paycitation,paymonth with ur";
		String sqlTaxCash_ins = "insert into " + this.tTaxCash
				+ " (PAYIDNO, PAYEAR, PAYMONTH, PAYCATG, PAYFORMAT, PAYCITATION, PAYTAXPAY, PAYTAXDEDUCT, PAYUPDID, PAYUPDTM, PAYFLAG, HID) "
				+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT TIMESTAMP, ?, ?);";

		try {
			Hospitals hospInfo = Hospitals.getOneHosp(hid);
			if (da == null) {
				// 改用列舉
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}

			// 寫入資料夾設定
			FileUtil fileUtil = new FileUtil();

			String folderPath = hospInfo.fileSRV + "log\\人工入稅\\";
			filename = filename.substring(0, filename.lastIndexOf(".") == -1 ? 0 : filename.lastIndexOf("."));
			String filePath = folderPath + filename + "log.csv";

			if (!FileUtil.FileToSmbFile(folderPath, hid).exists()) {
				fileUtil.FileToSmbFile(folderPath, hid).mkdirs();
			}
			// 建立檔案
			SmbFile payfile = fileUtil.FileToSmbFile(filePath, hid);
			bfw = new BufferedWriter(new OutputStreamWriter(new SmbFileOutputStream(payfile, false), "Big5"));
			// sb.setLength(0);

			da.setAutoCommit(false);
			params.clear();

			// 找基本檔個人
			starttime = System.currentTimeMillis();
			paramA = new ArrayList<Object>();
			paramA.add(hid);
			log.info(me + "  1.找基本檔個人 " + sqlBasPsn + "...params:" + paramA.toString());
			result = da.arraylistbyQuery(sqlBasPsn, paramA, META_TYPE);
			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					baspsnobj = new Pabaspsn();
					PropertyUtils.copyProperties(baspsnobj, data);
					strIdno = StringUtil.nullToString(baspsnobj.getPayidno());
					strCard = StringUtil.nullToString(baspsnobj.getPaycard());

					baspsnIdMap.put(strIdno, baspsnobj);
					baspsnCardMap.put(strCard, baspsnobj);
				}
			}
			log.info(me + "  1.找基本檔個人,size: " + result.size() + " 共花費：" + (System.currentTimeMillis() - starttime)
					+ "毫秒");

			// 找基本檔地址
			starttime = System.currentTimeMillis();
			paramA = new ArrayList<Object>();
			paramA.add(hid);
			log.info(me + "  1-1.找基本檔地址 " + sqlBasAdr);
			result = da.arraylistbyQuery(sqlBasAdr, META_TYPE);
			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					basadrobj = new Pabasadr();
					PropertyUtils.copyProperties(baspsnobj, data);
					strIdno = StringUtil.nullToString(baspsnobj.getPayidno());
					baspsnAdrMap.put(strIdno, basadrobj);
				}
			}
			log.info(me + "  1-1.找基本檔地址,size: " + result.size() + " 共花費：" + (System.currentTimeMillis() - starttime)
					+ "毫秒");

			// 2.讀取Pataximp ary,一一寫入pataxpsn pataxcash
			starttimeM = System.currentTimeMillis();
			log.info(me + "  3.讀取Pataximp ary,一一寫入pataxpsn pataxcash ");
			Calendar calendar = Calendar.getInstance();
			for (Pataximp itm : vobjs) {
				if (itm != null) {
					++docnt;
					strIdno = StringUtil.nullToString(itm.getPayidno());
					strCard = StringUtil.nullToString(itm.getPaycard());
					strCita = StringUtil.nullToString(itm.getPaycitation());
					int_year = itm.getPayear();
					strMon = StringUtil.nullToString(itm.getPaymonth());
					strCatg = StringUtil.nullToString(itm.getCatgcd());
					strMsg = "";
					if ("".equals(strIdno) && "".equals(strCard)) {
						blnFlag = false;
						strMsg = "身分證_卡號[空白],";
						strMsg += itm.getPayidno() + "," + itm.getPaycard() + "," + itm.getPaynamec();
						/*
						 * sb.append(strMsg); sb.append(newline);
						 */
						bfw.write(strMsg);
						bfw.newLine();
						bfw.flush();
					}
					if (blnFlag) {
						// [起]
						if (blnTaxPsn) {
							// 依年,找所得稅個人
							starttime = System.currentTimeMillis();
							paramA = new ArrayList<Object>();
							paramA.add(intYY);
							paramA.add(hid);
							log.info(me + "  2.依年,找所得稅個人 " + sqlTaxPsn + "...params:" + paramA.toString());
							result = da.arraylistbyQuery(sqlTaxPsn, paramA, META_TYPE);
							if (result.size() > 0) {
								for (Map<String, Object> data : result) {
									taxpsnobj = new Pataxpsn();
									PropertyUtils.copyProperties(taxpsnobj, data);
									tmpIdno = StringUtil.nullToString(taxpsnobj.getPayidno());
									tmpCard = StringUtil.nullToString(taxpsnobj.getPaycard());
									taxpsnIdMap.put(tmpIdno, taxpsnobj);
									if (!"".equals(tmpCard))
										taxpsnCardMap.put(tmpCard, taxpsnobj);
								}
							}
							log.info(me + "  2.依年,找所得稅個人,size: " + result.size() + " 共花費："
									+ (System.currentTimeMillis() - starttime) + "毫秒");
							// 依年,找所得稅人工入稅
							starttime = System.currentTimeMillis();
							paramA = new ArrayList<Object>();
							paramA.add(intYY);
							paramA.add(hid);
							log.info(me + "  2-1.依年,找所得稅人工入稅 " + sqlTaxCash + "...params:" + paramA.toString());
							result = da.arraylistbyQuery(sqlTaxCash, paramA, META_TYPE);
							if (result.size() > 0) {
								for (Map<String, Object> data : result) {
									taxcashobj = new Pataxcash();
									PropertyUtils.copyProperties(taxcashobj, data);
									tmpIdno = StringUtil.nullToString(taxcashobj.getPayidno());
									tmpCita = StringUtil.nullToString(taxcashobj.getPaycitation());
									tmpMon = StringUtil.nullToString(taxcashobj.getPaymonth());
									taxCashMap.put(tmpIdno + tmpCita + tmpMon, taxpsnobj);
								}
							}
							log.info(me + "  2-1.依年,找所得稅人工入稅,size: " + result.size() + " 共花費："
									+ (System.currentTimeMillis() - starttime) + "毫秒");

							blnTaxPsn = false;
						}
						int_month = itm.getPaymonth() - 1;
						calendar.set(int_year, int_month, 1);
						mBegin = java.sql.Date.valueOf(formatter.format(calendar.getTime()));
						;
						// 找出[訖]
						calendar.add(Calendar.MONTH, 1);
						calendar.add(Calendar.DATE, -1);
						mEnd = java.sql.Date.valueOf(formatter.format(calendar.getTime()));

						// 1.先找taxpsn所得稅個人(先依身分證,再依卡號)
						if (!"".equals(strIdno))
							taxpsnobj = (Pataxpsn) taxpsnIdMap.get(strIdno);
						else
							taxpsnobj = (Pataxpsn) taxpsnCardMap.get(strCard);

						// 2.所得稅taxpsn個人 無,找基本檔個人
						if (taxpsnobj == null) {
							if (!"".equals(strIdno))
								baspsnobj = (Pabaspsn) baspsnIdMap.get(strIdno);
							else
								baspsnobj = (Pabaspsn) baspsnCardMap.get(strCard);

							if (baspsnobj == null) {
								// 所得稅個人(無)+基本檔個人(無)+起訖日期(有)+文字檔IDNO(有),可csv新增
								if ((!("").equals(StringUtil.nullToString(itm.getPayidno())))
										&& (!("").equals(StringUtil.nullToString(itm.getPaybegin())))
										&& (!("").equals(StringUtil.nullToString(itm.getPaybegin())))) {
									batch_sql.add(sqlTaxPsn_ins);
									obj = new ArrayList<Object>();
									obj.add(itm.getPayidno());
									obj.add(itm.getPayear());
									obj.add(itm.getPaycard());
									obj.add(itm.getPaynamec());
									obj.add(itm.getPaybegin());
									obj.add(itm.getPayend());
									obj.add(userid);
									obj.add(hid);
									params.add(obj);
									strMsg = "依身分檔找不到[所得稅個人檔及基本個人檔]_起訖日期[有],";
									strMsg += itm.getPayidno() + "," + itm.getPaycard() + "," + itm.getPaynamec() + ",";
								}
								// 所得稅個人(無)+基本檔個人(無)+起訖日期(無)<==log
								else {
									blnFlag = false;
									strMsg = "依身分檔找不到[所得稅個人檔及基本個人檔]_起訖日期[無],";
									strMsg += itm.getPayidno() + "," + itm.getPaycard() + "," + itm.getPaynamec() + ",";
									/*
									 * sb.append(strMsg); sb.append(newline);
									 */
									bfw.write(strMsg);
									bfw.newLine();
									bfw.flush();
								}
							} else {
								// 所得稅個人(無)+基本檔個人(有)
								// <==用卡號找,要改用基本檔idno,去找所得稅個人idno(maybe換卡號),update所得稅個人_卡號,else insert用基本檔個人
								if ("".equals(strIdno)) {
									strIdno = baspsnobj.getPayidno();
									taxpsnobj = (Pataxpsn) taxpsnIdMap.get(strIdno);
								}
								if (taxpsnobj != null) {
									batch_sql.add(sqlTaxPsn_card_upd);
									obj = new ArrayList<Object>();
									obj.add(baspsnobj.getPaycard());
									obj.add(userid);
									obj.add(strIdno);
									obj.add(itm.getPayear());
									obj.add(hid);
									params.add(obj);
									strMsg = "update--所得稅個人檔卡號,";
									strMsg += itm.getPayidno() + "," + itm.getPaycard() + "," + itm.getPaynamec() + ","
											+ taxpsnobj.getPaycard() + ",";

								} else {
									// 用pabaspsn新增
									batch_sql.add(sqlTaxPsn_ins);
									obj = new ArrayList<Object>();
									obj.add(baspsnobj.getPayidno());
									obj.add(itm.getPayear());
									obj.add(baspsnobj.getPaycard());
									obj.add(baspsnobj.getPaynamec());
									if ((!("").equals(StringUtil.nullToString(itm.getPaybegin())))
											&& (!("").equals(StringUtil.nullToString(itm.getPayend())))) {
										obj.add(itm.getPaybegin());
										obj.add(itm.getPayend());
									} else {
										obj.add(mBegin);
										obj.add(mEnd);
									}
									obj.add(userid);
									obj.add(hid);
									params.add(obj);
									strMsg = "用pabaspsn新增,";
									strMsg += baspsnobj.getPayidno() + "," + itm.getPaycard() + ","
											+ baspsnobj.getPaynamec() + ",";

								}
							}
						}
						// 所得稅個人有找到,更新[所得結束日期]payend
						else {
							strIdno = taxpsnobj.getPayidno();
							// 匯入日期有KEY,若結束日期 < 匯入日期,要update 沒KEY,改用mEnd變數
							if (itm.getPayend() != null) {
								batch_sql.add(sqlTaxPsn_end_upd);
								obj = new ArrayList<Object>();
								if (DateUtil.compareDate(DateUtil.getDbDate(taxpsnobj.getPayend(), "-"),
										DateUtil.getDbDate(formatter.parse(itm.getPayend()), "-"))) {
									obj.add(itm.getPayend());
								} else {
									obj.add(mEnd);
								}
								obj.add(userid);
								obj.add(itm.getPayidno());
								obj.add(itm.getPayear());
								obj.add(hid);
								params.add(obj);
								strMsg += "所得稅個人有找到更新[所得結束日期]payend,";
								strMsg += itm.getPayidno() + "," + itm.getPaycard() + "," + itm.getPaynamec() + ",";

							}
						}

						if (blnFlag) {
							// 地址
							if (!"".equals(StringUtil.trimWideEmpty(itm.getPayaddrs()))) {
								basadrobj = (Pabasadr) baspsnAdrMap.get(strIdno);
								if (basadrobj != null) {
									batch_sql.add(sqlBasAdr_upd);
									strMsg += "地址update,";
								} else {
									batch_sql.add(sqlBasAdr_ins);
									strMsg += "地址insert,";
								}
								obj = new ArrayList<Object>();
								obj.add(StringUtil.trimWideEmpty(itm.getPayaddrs()));
								obj.add(strIdno);
								params.add(obj);
							}
							// PATAXCASH
							// 夜班費1,15 研究助理24,25,52 二代健保 35,57,58 離職2,7,8,9,13
							// 院外人士14,20,21,22,23,39,40,46,47,48,49
							// 在職3,5,6,10,11,12,30,31,32,33,41,42,43,44,45
							taxcashobj = (Pataxcash) taxCashMap.get(strIdno + strCita + strMon);
							// 找到 傳票製票號最大值,設定轉帳項目
							if (taxcashobj == null) {
								mCatg = strCatg;
							} else {
								tmpCatg = StringUtil.nullToString(taxcashobj.getPaycatg());
								if ("".equals(tmpCatg)) {
									mCatg = strCatg;
								} else {
									mCatg = PaCommUtil.getCatgNext(strCatg);
									if ("".equals(mCatg))
										mCatg = strCatg;
									else if ("XX".equals(mCatg)) {
										blnFlag = false;
										if (!"".equals(strMsg)) {
											/*
											 * sb.append(strMsg); sb.append(newline);
											 */
											bfw.write(strMsg);
											bfw.newLine();
											bfw.flush();
										}
										strMsg = "$$無法匯入PATAXCASH(INSERT)已達最大值_要補代碼$$,";
										strMsg += itm.getPayidno() + "," + itm.getPaycard() + "," + itm.getPaynamec()
												+ "," + itm.getCatgcd() + "," + itm.getCatgnm() + ","
												+ itm.getPayformat() + "," + itm.getPaycitation() + ","
												+ itm.getPaytaxpay() + "," + itm.getPaytaxdeduct() + ",";
									}
								}
							}

							if (blnFlag) {
								batch_sql.add(sqlTaxCash_ins);
								obj = new ArrayList<Object>();
								obj.add(strIdno);
								obj.add(int_year);
								obj.add(strMon);
								obj.add(strCatg);
								obj.add(StringUtil.nullToString(itm.getPayformat()));
								obj.add(strCita);
								obj.add(("").equals(StringUtil.nullToString(itm.getPaytaxpay())) ? "0"
										: StringUtil.nullToString(itm.getPaytaxpay()));
								obj.add(("").equals(StringUtil.nullToString(itm.getPaytaxdeduct())) ? "0"
										: StringUtil.nullToString(itm.getPaytaxdeduct()));
								obj.add(userid);
								obj.add("");
								obj.add(hid);
								params.add(obj);
								strMsg += "人工入稅insert," + itm.getPaycard() + "," + itm.getPaynamec() + ","
										+ itm.getCatgcd() + "," + itm.getCatgnm() + "," + itm.getPayformat() + ","
										+ itm.getPaycitation() + "," + itm.getPaytaxpay() + "," + itm.getPaytaxdeduct()
										+ ",";
								;
							}
						}
						// 寫log
						if (!"".equals(strMsg)) {
							/*
							 * sb.append(strMsg); sb.append(newline);
							 */
							bfw.write(strMsg);
							bfw.newLine();
							bfw.flush();
						}
						// log.info(write_cnt+" "+obj);

						if (++write_cnt % limitcnt == 0 || write_cnt == vobjs.size() || docnt == vobjs.size()) {
							log.info(batch_sql + " " + batch_sql + " params:" + params);
							da.batchupdatenocommit(batch_sql, params);
							da.commit();
							da.setAutoCommit(false);
							batch_sql.clear();
							params.clear();
						}
					}
				}
			}
			rtn = "寫入筆數:" + write_cnt;
			log.info(me + "  ...********* 共花費：" + (System.currentTimeMillis() - starttimeM) + "毫秒");

			/*
			 * starttime = System.currentTimeMillis();
			 * log.info(me+"  開始寫出文字檔："+filePath+"..."); outputstring = sb.toString(); File
			 * file = new File(filePath); output = new BufferedWriter(new FileWriter(file));
			 * System.out.println("開始寫出文字檔 "+filePath+"..."); output.write(outputstring);
			 * output.close();
			 * log.info(me+"  ...********* 寫入成功...共花費："+(System.currentTimeMillis()-
			 * starttime)+"毫秒");
			 */
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.info(me + " fail, " + e);
			throw new FileNotFoundException(me + ", fail!!." + e);
		} catch (IOException e) {
			e.printStackTrace();
			log.info(me + " fail, " + e);
			throw new IOException(me + ", fail!!." + e);
		} catch (Exception e) {
			da.rollback();

			rtn = " fail, " + e;
			da.setAutoCommit(true);
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			if (bfw != null)
				bfw.close();
			closeAll(da, null, connFlg);
		}
		return rtn;
	}

	/**
	 * 支扣項目匯入批次檔 傳入:vobjs,tradt發薪日,paycode支扣項目,chkbas(Y/N),userid使用者,hid院區
	 * 傳出:rtn文字訊息
	 **/
	public String ImpPayInBat(ArrayList<Pabatpay> vobjs, String tradt, String paycode, String chkbas, String selEn,
			String filename, String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".ImpPayInBat(),userid = " + userid;
		log.info(me + ",tradt = " + tradt + ",paycode = " + paycode + ",chkbas = " + chkbas + ",selEn = " + selEn
				+ ",filename = " + filename);
		StringBuilder sb = new StringBuilder();
		BufferedWriter bfw = null;
		Writer output = null;
		String outputstring = "";
		String rtn = "";

		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> paramA = new ArrayList<Object>();
		ArrayList<Object> obj = new ArrayList<Object>();
		ArrayList<Object> batch_sql = new ArrayList<Object>(); // batch_update sql 資料

		HashMap baspsnCardMap = new HashMap();
		HashMap batwrtMap = new HashMap(); // 來源檔寫入至map
		HashMap batpayMap = new HashMap();
		HashMap trapsnMap = new HashMap();
		HashMap trapayMap = new HashMap();
		Pabaspsn baspsnobj = null; // 基本檔個人obj
		Pabatpay batpayobj = null; // 批次檔obj
		Patrapsn trapsnobj = null; // 異動檔個人obj
		Patrapay trapayobj = null; // 異動檔支扣obj
		String strIdno = "";
		String strCard = "";
		String strCode = "";
		String strUpbas = "";
		String strTradt = "";
		int intAmt = 0;
		int intbatamt = 0;
		int inttracod = 0;
		boolean blnFlag = true;
		String strLine = "";
		int limitcnt = 100;
		int write_cnt = 0;// 寫入table第N筆
		int docnt = 0; // 執行第N筆
		long starttime = 0;
		long starttimeM = 0;
		String l_newWorkfirst; // 月尾薪 工作月第一天
		String strYM = "";
		String jsonStr = ""; // patrapsn用json傳入參數

		// or PATRACOD=33 or PATRACOD=37<==基本檔可以抓,不用這段
		String sqlTranPsn = "select A.*,B.PAYTITLP paytitlo,c.PAYTITLP,d.TRANAME " + " from " + this.tTraPsn + " A "
				+ " left outer join " + this.tTitle + " B ON A.PAOTITLE=B.PAYTITLK and A.HID=B.HID "
				+ " left outer join " + this.tTitle + " C ON A.PAYTITLE=C.PAYTITLK and A.HID=C.HID "
				+ " left outer join " + this.tTraCode + " D ON A.patracod=d.TRACODE  "
				+ "where a.HID=? and ((PATRACOD BETWEEN 1 AND 10 ) OR ((PATRACOD BETWEEN 21 AND 30) AND PAEFFDT <= PATRADT)) "
				+ " and PATRADT = ? " + " with ur ";
		String sqlBatPay_inq = "select PATRADT, PAYIDNO, SALNUM, PAYCARD, PAYCODE, PAUPBAS, PAYAMT, PADUTYID, PAYUPDAT, PAYFLAG, UPMASTDT, HID "
				+ " from " + this.tBatPay + " where HID= ? and PATRADT=? and PAYCODE=? with ur";
		String sqlBatPay_ins = " insert into " + this.tBatPay
				+ " (PAYCARD, PAUPBAS, PAYAMT, PADUTYID, PAYUPDAT, PAYFLAG, UPMASTDT, ZPABATPAY,PATRADT, PAYIDNO, SALNUM, PAYCODE, HID) "
				+ "values (?,?,?,?,current timestamp,?,?,current timestamp,?,?,?,?,?) ";
		String sqlBatPay_upd = " update " + this.tBatPay
				+ " set (PAYCARD, PAUPBAS, PAYAMT, PADUTYID, PAYUPDAT, PAYFLAG, UPMASTDT, ZPABATPAY) = (?, ?, ?, ?,current timestamp,?,?,current timestamp) "
				+ " where PATRADT =? and PAYIDNO =? and SALNUM =? and PAYCODE =? and  HID=?";

		try {
			Hospitals hospInfo = Hospitals.getOneHosp(hid);
			if (da == null) {
				// 改用列舉
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			// log放的位置,加年月
			strYM = tradt.substring(0, 4) + tradt.substring(5, 7);
			FileUtil fileUtil = new FileUtil();
			String folderPath = hospInfo.fileSRV + "log\\批次檔\\" + strYM + "\\";

			if (!FileUtil.FileToSmbFile(folderPath, hid).exists()) {
				fileUtil.FileToSmbFile(folderPath, hid).mkdirs();
			}
			// 寫入資料夾設定
			filename = filename.substring(0, filename.lastIndexOf(".") == -1 ? 0 : filename.lastIndexOf("."));
			String filePath = folderPath + filename + "log.csv";
			// 建立檔案
			SmbFile payfile = fileUtil.FileToSmbFile(filePath, hid);
			bfw = new BufferedWriter(new OutputStreamWriter(new SmbFileOutputStream(payfile, false), "Big5"));

			// sb.setLength(0);

			da.setAutoCommit(false);
			params.clear();

			starttimeM = System.currentTimeMillis();
			// 找基本檔個人
			baspsnCardMap = getBasEmpCardMap(hid, da);

			// 找批次檔_發薪日+支扣代碼
			starttime = System.currentTimeMillis();
			paramA = new ArrayList<Object>();
			paramA.add(hid);
			paramA.add(tradt);
			paramA.add(paycode);
			log.info(me + "  2.找批次檔_發薪日+支扣代碼 " + sqlBatPay_inq + "...params:" + paramA.toString());
			result = da.arraylistbyQuery(sqlBatPay_inq, paramA, META_TYPE);
			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					batpayobj = new Pabatpay();
					PropertyUtils.copyProperties(batpayobj, data);
					strCard = StringUtil.nullToString(batpayobj.getPaycard());
					batpayMap.put(strCard, batpayobj);
				}
			}
			log.info(me + "  2.找批次檔_發薪日+支扣代碼 ,size: " + result.size() + " 共花費："
					+ (System.currentTimeMillis() - starttime) + "毫秒");

			// 找異動個人_發薪日
			// String jsonStr = "{\"city\":\"Taipei\", \"pop\":2600000}";
			jsonStr = "{hid:" + hid + ", tradt:" + tradt + "}";
			trapsnMap = getTraPsnMap(jsonStr, 1, da);
			// 2026.3.30找異動明細_發薪日_支扣代碼(新)
			trapayMap = getTraPayMap(hid, tradt, paycode, da);

			// 月尾薪,2026-4-1發薪日,工作月第一天2026-3-1,判斷有效日期 (2026/3/1-2026/3/31)
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(DateUtil.StringToDate(tradt));
			calendar.add(Calendar.MONTH, -1);
			// calendar.add(Calendar.DATE,1);
			l_newWorkfirst = formatter.format(calendar.getTime());
			log.info(me + "  2-1.月尾薪工作月第一天: " + l_newWorkfirst);
			// 2.讀取ary,一一寫入pabatpay
			starttime = System.currentTimeMillis();
			log.info(me + "  3.讀取 ary,一一寫入pabatpay ");

			strLine = "年,月,卡號,金額,姓名,訊息,寫入批次檔";
			bfw.write(strLine);
			bfw.newLine();
			bfw.flush();
			// sb.append(strLine);
			// sb.append(newline);
			for (Pabatpay itm : vobjs) {
				if (itm != null) {
					docnt++;
					strCard = StringUtil.nullToString(itm.getPaycard());
					/*
					 * if (strCard.matches("8146|8150|8152|8444")) System.out.println(strCard);
					 */
					strCode = StringUtil.nullToString(paycode);
					strUpbas = StringUtil.nullToString(itm.getPaupbas());
					intAmt = itm.getPayamt();
					strTradt = StringUtil.nullToString(itm.getPatradt());
					blnFlag = true;

					strLine = itm.getYy() + "," + itm.getMm() + "," + itm.getPaycard() + "," + itm.getPayamt();
					if ("".equals(strCard)) {
						blnFlag = false;
						strLine += ",,卡號[空白],N";
						bfw.write(strLine);
						bfw.newLine();
						bfw.flush();
						// sb.append(strLine);
						// sb.append(newline);
					} else {
						trapsnobj = (Patrapsn) trapsnMap.get(strCard);
						trapayobj = (Patrapay) trapayMap.get(strCard);
						baspsnobj = (Pabaspsn) baspsnCardMap.get(strCard);

						// 基本檔,異動檔皆找不到
						if (trapsnobj == null && baspsnobj == null) {
							strLine += ",,卡號[找不到],N";
							bfw.write(strLine);
							bfw.newLine();
							bfw.flush();
							// sb.append(strLine);
							// sb.append(newline);
							blnFlag = false;
						} else {
							if (trapsnobj != null) { // 先找異動檔
								inttracod = trapsnobj.getPatracod();
								// 新進,復職
								if (inttracod < 10) {
									strIdno = StringUtil.nullToString(trapsnobj.getPayidno());
								}
								// 離職,留停
								else {
									// 生效日date2>發薪日date1 2025-11-15>2025-11-1 boolean true:date1 < date2 ，false:
									// date1 >= date2
									if (DateUtil.compareDate(DateUtil.getDbDate(trapsnobj.getPatradt(), "-"),
											DateUtil.getDbDate(trapsnobj.getPaeffdt(), "-"))) {
										strIdno = StringUtil.nullToString(trapsnobj.getPayidno());
									} else {
										// 2025-10-15生效日 < 2025-11-1發薪日
										// 舊制(月頭) 發薪日2025/11/1 <生效日2025/11/1,寫入log
										// 新制(月尾) 發薪日2025/11/1 生效日10/2-11/1 要入批次檔 true:月尾起 < 生效日 10/2 < 10/15
										if ("Y".equals(trapsnobj.getContraco()) && DateUtil.compareDate(l_newWorkfirst,
												DateUtil.getDbDate(trapsnobj.getPaeffdt(), "-"))) {
											// 2026.3.26 H260王明耀 原留停,2026.4.1發薪做2026.3.31離職,同日復職同日離職,檢測基本檔=停薪,不用入批次
											if (baspsnobj.getPatracod() == 23 || baspsnobj.getPatracod() == 24) {
												strLine += "," + trapsnobj.getPaynamec() + ",基本--"
														+ baspsnobj.getTraname() + "/異動--" + trapsnobj.getTraname()
														+ trapsnobj.getPaeffdt() + ",N";
												blnFlag = false;
												bfw.write(strLine);
												bfw.newLine();
												bfw.flush();
											} else
												strIdno = StringUtil.nullToString(trapsnobj.getPayidno());
										} else {
											if ("N".equals(chkbas))// 不檢核,寫入批次檔
												strLine += "," + trapsnobj.getPaynamec() + ",異動--"
														+ trapsnobj.getTraname() + trapsnobj.getPaeffdt() + ",Y";
											else { // 檢核,不寫入批次檔
												strLine += "," + trapsnobj.getPaynamec() + ",異動--"
														+ trapsnobj.getTraname() + trapsnobj.getPaeffdt() + ",N";
												blnFlag = false;
											}
											bfw.write(strLine);
											bfw.newLine();
											bfw.flush();
											// sb.append(strLine);
											// sb.append(newline);
										}
									}
								}
							}
							// 找基本檔
							else if (baspsnobj != null) {
								inttracod = baspsnobj.getPatracod();
								strIdno = StringUtil.nullToString(baspsnobj.getPayidno());
								if (inttracod >= 20 && inttracod < 30) {
									if ("N".equals(chkbas))// 不檢核,寫入批次檔
										strLine += "," + baspsnobj.getPaynamec() + ",基本--" + baspsnobj.getTraname()
												+ ",Y";
									else { // 檢核,不寫入批次檔
										strLine += "," + baspsnobj.getPaynamec() + ",基本--" + baspsnobj.getTraname()
												+ ",N";
										blnFlag = false;
									}
									bfw.write(strLine);
									bfw.newLine();
									bfw.flush();
									// sb.append(strLine);
									// sb.append(newline);
								}
							}
							// 找異動支扣是否有該筆項目
							if (trapayobj != null) {
								strLine += "," + baspsnobj.getPaynamec() + ",異動檔已有該項支扣,N";
								blnFlag = false;
								bfw.write(strLine);
								bfw.newLine();
								bfw.flush();
							}

							if (blnFlag) {

								// 找是否有在寫入MAP內,若有,寫log,不寫入批次檔
								if (batwrtMap.containsKey(strCard)) {
									strLine += "," + baspsnobj.getPaynamec() + ",重複,Y";
									bfw.write(strLine);
									bfw.newLine();
									bfw.flush();
								} else {
									batpayobj = null;
									// 1A0 VP013專勤醫師--病歷獎勵罰扣 VD070補充保險費 找到,金額加總update,沒找到insert
									if (paycode.equals("VP013|VD070") && hid.matches("1A0")) {
										batpayobj = (Pabatpay) batpayMap.get(strCard);
										if (batpayobj != null)
											batch_sql.add(sqlBatPay_upd);
										else
											batch_sql.add(sqlBatPay_ins);
									} else
										batch_sql.add(sqlBatPay_ins);

									obj = new ArrayList<Object>();
									obj.add(strCard);
									obj.add(strUpbas);
									if (batpayobj != null)
										obj.add(intAmt + batpayobj.getPayamt());
									else
										obj.add(intAmt);
									obj.add("I" + userid);
									obj.add("");
									obj.add("");
									obj.add(strTradt);
									obj.add(strIdno);
									obj.add(0);
									obj.add(strCode);
									obj.add(hid);
									params.add(obj);

									batwrtMap.put(strCard, intAmt);
								}
							}
						}
					}
					if (++write_cnt % limitcnt == 0 || write_cnt == vobjs.size() || docnt == vobjs.size()) {
						log.info(batch_sql + " " + batch_sql + " params:" + params);
						da.startTransaction();
						try {
							da.batchupdatenocommit(batch_sql, params);
							da.commit();
							da.setAutoCommit(false);
							batch_sql.clear();
							params.clear();

						} catch (Exception e) {
							da.rollback();
							rtn = " fail, " + e;
							da.setAutoCommit(true);
							e.printStackTrace();
							log.info(me + " " + selEn + " " + e.toString());
						} finally {

						}
					}
				}
			}
			if ("".equals(rtn)) {
				rtn = "寫入筆數:" + write_cnt;
				log.info(me + "  ...********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");

				// starttime = System.currentTimeMillis();
				// log.info(me+" 開始寫出文字檔："+filePath+"...");
				// outputstring = sb.toString();
				// log.info(me+" 文字檔內容："+outputstring+"...");
				// File file = new File(filePath);
				// output = new BufferedWriter(new FileWriter(file));
				// output.write(outputstring);
				// output.close();
				// log.info(me+" ...*********
				// ..共花費："+(System.currentTimeMillis()-starttime)+"毫秒");
			}

			rtn += "...*********..共花費：" + (System.currentTimeMillis() - starttimeM) + "毫秒";
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.info(me + " fail, " + e);
			throw new FileNotFoundException(me + ", fail!!." + e);
		} catch (IOException e) {
			e.printStackTrace();
			log.info(me + " fail, " + e);
			throw new IOException(me + ", fail!!." + e);
		} catch (Exception e) {
			da.rollback();

			rtn = " fail, " + e;
			da.setAutoCommit(true);
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			if (bfw != null)
				bfw.close();
			closeAll(da, null, connFlg);
		}
		return rtn;
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
		Pabaspsn bpsnobj = null;
		Pabaspay baspayobj = null;
		Patrapsn trapsnobj = null;
		Patrapay trapayobj = null;
		Padatpay datpayobj = null;
		Padatpsn datpsnobj = null;
		Paprtdtl prtdtlobj = null;
		Pareport parptobj = null;
		Pabatpay tbatobj = null;
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
		// 發薪明細_產生_1_取得基本個人檔(在職),2024-7-12 聯醫特約不放月薪,payrank=I key:payidno
		String sqlBaspsnSgn_inq = "select PAYLVEFG, PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, PAYEMPDT, PAYSGNDT, PAYRSGDT, PAYBKID, PAYACNO, PAYGEDNO, PAYINFLG, PAYDPNO, PAYPSW, PAYTITLE, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, COALESCE(PAYPFDOC,0) PAYPFDOC, PACSFLAG, PAYPFSP1, PAYPFSP2, PAYPFSP3, PATRACOD, PATRADT, PAYTAXNO, PAYUPDID, PAYUPDTM, PAYEXPFKEY1, PAYEXPFKEY2, PAYEXPFKEY3, PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, INSUAMT, PARETENTION, HID "
				+ "from " + this.tBasPsn
				+ " where PAYLVEFG = 'N' and PATRACOD not in (23,24) AND HID=? and PAYRANK NOT IN ('I') " + tmp
				+ " order by paycard with ur";
		// 發薪明細_產生_1_取得基本個人檔(全抓)and (payrsgdt >= CURRENT date-1 year OR payrsgdt IS null)
		// 2024-7-12 聯醫特約不放月薪,payrank=I key:payidno
		String sqlBaspsnAll_inq = "select PAYLVEFG, PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, PAYEMPDT, PAYSGNDT, PAYRSGDT, PAYBKID, PAYACNO, PAYGEDNO, PAYINFLG, PAYDPNO, PAYPSW, PAYTITLE, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, COALESCE(PAYPFDOC,0) PAYPFDOC, PACSFLAG, PAYPFSP1, PAYPFSP2, PAYPFSP3, PATRACOD, PATRADT, PAYTAXNO, PAYUPDID, PAYUPDTM, PAYEXPFKEY1, PAYEXPFKEY2, PAYEXPFKEY3, PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, INSUAMT, PARETENTION, HID "
				+ "from " + this.tBasPsn + " where HID=? " + tmp + " order by paycard with ur";
		// 發薪明細_產生_2_取得基本支扣檔,2024-7-12 聯醫特約不放月薪,payrank=I key:payidno
		String sqlBaspay_inq = "select a.PAYIDNO, PAYCODE, PAYAMT, PAYAUTH, PAYIFTAX, PAYUPDTM, a.HID " + "from "
				+ this.tBasPay + " a " + " inner join ( select PAYIDNO,HID from " + this.tBasPsn
				+ " where PAYLVEFG = 'N' and PATRACOD not in (23,24) AND HID=? and PAYRANK NOT IN ('I') " + tmp
				+ " ) b " + "    on a.payidno=b.payidno and a.hid=b.hid " + " order by a.payidno,a.paycode with ur";
		// 發薪明細_產生_3_取得異動個人檔 key:payidno 聯醫特約不放月薪,payrank=I
		String sqlTrapsn_inq = "select PATRACOD, PATRADT, PAEFFDT, SALNUM, PAYLVEFG, PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, PAYEMPDT, PAYSGNDT, PAYRSGDT, PAYBKID, PAYACNO, PAYGEDNO, PAYINFLG, PAYDPNO, PAYPFLAG, PAYTITLE, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, PAYPFDOC, PACSFLAG, PAYPFSP1, PAYPFSP2, PAYPFSP3, PAYMONNO, PAYDAYNO, PAOTITLE, PAORNK2N, PAORANK3, PAODEGR2, PAODEGRE, PAORANK, PAORANK2, PAORANKC, PAOPFDOC, PAOCSFLA, PAOPFSP1, PAOPFSP2, PAOPFSP3, PAYTAXNO, PAYUPDID, PAYUPDTM, PAYFLAG, TRANUM, UPMASTDT, PAYPSW, PAOEXPFKEY1, PAOEXPFKEY2, PAOEXPFKEY3, PAYEXPFKEY1, PAYEXPFKEY2, PAYEXPFKEY3, PAORANK4, PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, INSUAMT, HID "
				+ "from " + this.tTraPsn
				+ " where SALNUM=? AND HID=? and (PAYRANK NOT IN ('I') or PAORANK NOT IN ('I')) " + tmp
				+ " order by paycard with ur";
		// 發薪明細_產生_4_取得異動支扣檔 key:payidno
		String sqlTrapay_inq = "select TRANUM, a.PAYIDNO, a.PATRADT, PAYSORT, PAYCODE, PAYAMT, PAYAUTH, PAYIFTAX, PAYUPDTM, PAYFLAG, a.HID "
				+ "from " + this.tTraPay + " a " + " inner join ( select PAYIDNO,HID,PATRADT from " + this.tTraPsn
				+ " where SALNUM=? AND HID=? and (PAYRANK NOT IN ('I') or PAORANK NOT IN ('I')) " + tmp + " ) b "
				+ "    on a.payidno=b.payidno and a.hid=b.hid and a.PATRADT=b.PATRADT "
				+ " where PAYSORT='N' order by a.payidno,a.paycode with ur";
		// 發薪明細_產生_5_取得批次檔 key:payidno+paycode
		String sqlBatpay_inq = "select PATRADT, PAYIDNO, SALNUM, PAYCARD, A.PAYCODE, a.PAUPBAS, PAYAMT, PADUTYID, a.PAYUPDAT, PAYFLAG, UPMASTDT, A.HID,PAYIFTAX,PAYAUTH "
				+ "from " + this.tBatPay + " a " + " left outer join " + this.tPayItm
				+ " b on a.PAYCODE=b.PAYCODE and a.HID=b.HID " + " where salnum=? AND A.HID=? " + tmp
				+ " order by PAYIDNO,A.PAYCODE  with ur";
		// 報表檔_產生,取得月支個人檔 key=salnum+hid
		String sqlPrtDatpsn_inq = "SELECT COALESCE(e.PAYLVEFG,'N') PAYLVEFG, COALESCE(e.PAEFFDT,'9999-12-31') PAEFFDT,COALESCE(e.traname,'') traname,"
				+ "COALESCE(e.patracod,0) patracod,A.PAYBANK, A.PAYIDNO, A.PAYCARD, A.PAYNAMEC, A.PAYEMPNO, "
				+ "CASE WHEN COALESCE(D.PAYCTCBACNO,'')='' THEN COALESCE(E.PAYCTCBACNO,'') ELSE COALESCE(D.PAYCTCBACNO,'') END AS PAYACNO,"
				+ "CASE WHEN COALESCE(D.PAYCTCBGEDNO,'')='' THEN COALESCE(E.PAYCTCBGEDNO,'') ELSE COALESCE(D.PAYCTCBGEDNO,'') END AS PAYGEDNO,"
				+ "A.PAYDEPT, B.PADEPNMC,T.PATITSUB, A.PAYTITLE, T.PAYTITLP, A.PAYRANK, C.RANKABBR,A.PAYRNK2N, A.PAYRANK3 , A.PAYDEGR2, A.PAYDEGRE,A.HID,A.SALNUM,"
				+ "CASE WHEN NOT e.PAYEMPDT IS NULL THEN e.payempdt ELSE d.payempdt END payempdt " + "from "
				+ this.tDatPsn + " A " + "LEFT OUTER JOIN " + this.tDept
				+ " B ON A.PAYDEPT  = B.PADEPCOD AND A.HID=B.HID " + "LEFT OUTER JOIN " + this.tTitle
				+ " T ON A.PAYTITLE = T.PAYTITLK AND A.HID=T.HID " + "LEFT OUTER JOIN " + this.tRankCode
				+ " C ON A.PAYRANK  = C.RANKCODE " + "LEFT OUTER JOIN " + this.tBasPsn
				+ " D ON A.PAYIDNO  = D.PAYIDNO  AND A.HID=D.HID "
				+ "LEFT OUTER JOIN (SELECT salnum,patracod,b.traname,payidno,hid,PAYCTCBACNO,PAYCTCBGEDNO,PAYEMPDT,PAYLVEFG,PAEFFDT "
				+ "FROM " + this.tTraPsn + " a " + "LEFT OUTER JOIN " + this.tTraCode + " b ON a.patracod=b.TRACODE "
				+ "WHERE salnum=? AND hid=? AND patracod NOT IN ('41','51')) E ON A.PAYIDNO  = E.PAYIDNO  AND A.HID=E.HID AND A.SALNUM=E.SALNUM  "
				+ " where A.SALNUM=? and A.HID=? " + tmp + " order by paycard with ur";

		// 報表檔_產生,取得月支支扣檔 key=salnum+hid
		String sqlPrtDatpay_inq = "SELECT b.PAYIDNO, PAYCODE,'' paysort,b.SALNUM,  PAYAMT, PAYAUTH, PAYIFTAX,  PAYFLAG, b.HID "
				+ "FROM " + this.tDatPay + " b " + "LEFT OUTER JOIN " + this.tDatPsn
				+ " a ON a.hid=b.hid AND a.payidno=b.payidno AND a.salnum=b.salnum " + "where b.salnum=? AND b.hid=? "
				+ tmp + "union "
				+ "SELECT a.PAYIDNO, PAYCODE, a.paysort, b.SALNUM, PAYAMT, PAYAUTH, PAYIFTAX,PAYFLAG, a.HID " + "FROM "
				+ this.tTraPay + " a " + "inner join (select salnum,payidno,patradt,hid,patracod " + "from "
				+ this.tTraPsn + " a " + "WHERE salnum=? and HID=? AND patracod NOT IN ('41','51') " + tmp
				+ ") b ON a.hid=b.hid AND a.payidno=b.payidno AND a.patradt=b.patradt "
				+ "where a.patradt =? and a.hid=? AND paysort='O' AND substr(paycode,1,2)='FP' "
				+ " order by 1,2,3 with ur";
		// 報表產製(主計)_產生,取得月支支扣檔 key=salnum+hid
		String sqlDatpay_inq = "SELECT b.PAYIDNO, PAYCODE,b.SALNUM,  PAYAMT, PAYAUTH, PAYIFTAX,  PAYFLAG, b.HID "
				+ "FROM " + this.tDatPay + " b " + "LEFT OUTER JOIN " + this.tDatPsn
				+ " a ON a.hid=b.hid AND a.payidno=b.payidno AND a.salnum=b.salnum " + "where b.salnum=? AND b.hid=? "
				+ tmp + " order by 1,2,3 with ur";
		// 報表架構
		String sqlPrtRpt = "select PAYTYPE, PAYNUM, PAYCODE, HID, COALESCE(PAYAMT,0) PAYAMT " + "from " + this.tRPPRT
				+ " where hid=? and PAYTYPE=? " + "ORDER BY PAYNUM,PAYCODE " + "WITH UR";
		// 報表產製(績效組)_產生,取得月支支扣檔 (VP01,VP049,FD440)key=salnum+hid,回傳 id,payamt
		// 2014-12-5 改成不讀的項目排除,VP01*全部,排除VP013 VP010
		// VP049專二補發,FP440專勤獎金一培訓補助,FP80工作-住宿津貼
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
		String sqlDatdoc_Adj = "SELECT SALNUM, PAYBANK, SALNUM, PAYBANK, PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYTITLE, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, PAYPFDOC, PACSFLAG, PAYPFSP1, PAYPFSP2, PAYPFSP3, PAYUPDID, PAYUPDTM, PAYRANK4, HID, ZPADATPSN, INSUAMT, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYTITLE, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, PAYPFDOC, PACSFLAG, PAYPFSP1, PAYPFSP2, PAYPFSP3, PAYUPDID, PAYUPDTM, PAYRANK4, HID, INSUAMT"
				+ " FROM " + this.tDatPsn + " WHERE hid=? AND payidno IN (SELECT payidno FROM " + this.tTraPsn + " a "
				+ " LEFT OUTER JOIN " + this.tTitle + " b ON a.hid=b.hid and a.PAYTITLE=b.PAYTITLK "
				+ " WHERE patradt BETWEEN ? AND ? AND patracod in (37) AND b.PATITSUB IN ('V') )"
				+ " and paybank=? with ur";
		// 論文及教職獎勵_取基本支扣FP420教職獎勵 FP430論文獎勵
		String sqlBaspay_Tea = "select a.PAYIDNO, PAYCODE, PAYAMT, PAYAUTH, PAYIFTAX, a.HID " + "from " + this.tBasPay
				+ " a " + " inner join ( select PAYIDNO,HID from " + this.tBasPsn
				+ " where PAYLVEFG = 'N' and PATRACOD not in (23,24) and PAYRANK NOT IN ('I') " + tmp + " ) b "
				+ "    on a.payidno=b.payidno and a.hid=b.hid " + " where paycode in ('FP420','FP430') and a.hid=?  "
				+ " order by a.payidno,a.paycode with ur";
		// 論文及教職獎勵
		String sqlTeachaw = "select PAYIDNO, 'FP420' PAYCODE, PAYCARD, PAYNAMEC, PAYAMT,hid " + " from " + this.tTeaChaw
				+ " where hid=? AND ? BETWEEN PABDATE AND PAVDATE  AND PAYITEM in ('00') " + " union "
				+ " SELECT PAYIDNO, 'FP430' PAYCODE, PAYCARD, PAYNAMEC, sum(PAYAMT) PAYAMT,hid " + " from "
				+ this.tTeaChaw + " WHERE hid=? AND ? BETWEEN PABDATE AND PAVDATE  AND PAYITEM in ('10','20') "
				+ " GROUP BY PAYIDNO,PAYCARD,PAYNAMEC,hid " + " ORDER BY paycard with ur";
		try {
			starttime = System.currentTimeMillis();
			Hospitals hospInfo = Hospitals.getOneHosp(hid);
			if (da == null) {
				// 改用列舉
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}

			if (tblNm.equals("BASPSN"))
				sql = sqlBaspsnSgn_inq;
			else if (tblNm.equals("BASPSNALL"))
				sql = sqlBaspsnAll_inq;
			else if (tblNm.equals("BASPAY"))
				sql = sqlBaspay_inq;
			else if (tblNm.equals("TRAPSN"))
				sql = sqlTrapsn_inq;
			else if (tblNm.equals("TRAPAY"))
				sql = sqlTrapay_inq;
			else if (tblNm.equals("BATPAY"))
				sql = sqlBatpay_inq;
			else if (tblNm.equals("PrtDATPSN"))
				sql = sqlPrtDatpsn_inq;
			else if (tblNm.equals("PrtDATPAY"))
				sql = sqlPrtDatpay_inq;
			else if (tblNm.equals("DATPAY"))
				sql = sqlDatpay_inq;
			else if (tblNm.equals("DATDOC"))
				sql = sqlDatdoc_inq;
			else if (tblNm.equals("DATDOCAdj"))
				sql = sqlDatdoc_Adj;
			else if (tblNm.equals("PrtRpt"))
				sql = sqlPrtRpt;
			else if (tblNm.equals("TEACHAWBas"))
				sql = sqlBaspay_Tea;
			else if (tblNm.equals("TEACHAW"))
				sql = sqlTeachaw;

			log.info(me + "  " + sql + "...params:" + lparam.toString());

			result = da.arraylistbyQuery(sql, lparam, META_TYPE);
			if (result.size() > 0) {
				bpayAry = new ArrayList<Object>();
				tpayAry = new ArrayList<Object>();
				dpayAry = new ArrayList<Object>();
				for (Map<String, Object> data : result) {
					if (tblNm.matches("BASPSN|BASPSNALL")) {
						bpsnobj = new Pabaspsn();
						PropertyUtils.copyProperties(bpsnobj, data);
						strKey = StringUtil.nullToString(bpsnobj.getPayidno());
						rtnMap.put(strKey, bpsnobj);
					} else if (tblNm.equals("BASPAY")) {
						baspayobj = new Pabaspay();
						PropertyUtils.copyProperties(baspayobj, data);
						strKey = StringUtil.nullToString(baspayobj.getPayidno());
						// cnt++;|| cnt == result.size()
						if ((!"".equals(preKey) && !strKey.equals(preKey))) {
							/*
							 * if (cnt == result.size()){ bpayAry.add(baspayobj); preKey = strKey; }
							 */
							rtnMap.put(preKey, bpayAry);
							bpayAry = new ArrayList<Object>();
						}
						bpayAry.add(baspayobj);
						preKey = strKey;
					} else if (tblNm.equals("TRAPSN")) {
						trapsnobj = new Patrapsn();
						PropertyUtils.copyProperties(trapsnobj, data);
						strKey = StringUtil.nullToString(trapsnobj.getPayidno());
						rtnMap.put(strKey, trapsnobj);
					} else if (tblNm.equals("TRAPAY")) {
						trapayobj = new Patrapay();
						PropertyUtils.copyProperties(trapayobj, data);
						strKey = StringUtil.nullToString(trapayobj.getPayidno());
						// cnt++;|| cnt == result.size()
						if ((!"".equals(preKey) && !strKey.equals(preKey))) {
							/*
							 * if (cnt == result.size()){ tpayAry.add(trapayobj); preKey = strKey; }
							 */
							rtnMap.put(preKey, tpayAry);
							tpayAry = new ArrayList<Object>();
						}
						tpayAry.add(trapayobj);
						preKey = strKey;
					} else if (tblNm.equals("BATPAY")) {
						tbatobj = new Pabatpay();
						PropertyUtils.copyProperties(tbatobj, data);
						strKey = StringUtil.nullToString(tbatobj.getPayidno());
						strKeyA = StringUtil.nullToString(tbatobj.getPaycode());
						rtnMap.put(strKey + strKeyA, tbatobj);
					} else if (tblNm.equals("PrtDATPSN")) {
						prtdtlobj = new Paprtdtl();
						PropertyUtils.copyProperties(prtdtlobj, data);
						strKey = StringUtil.nullToString(prtdtlobj.getPayidno());
						rtnMap.put(strKey, prtdtlobj);
					} else if (tblNm.equals("PrtDATPAY")) {
						datpayobj = new Padatpay();
						PropertyUtils.copyProperties(datpayobj, data);
						strKey = StringUtil.nullToString(datpayobj.getPayidno());
						/*
						 * if ("Y220383815".equals(strKey)){ System.out.println(strKey); }
						 */
						// cnt++; || cnt == result.size()
						if ((!"".equals(preKey) && !strKey.equals(preKey))) {
							// if (cnt == result.size()){
							// dpayAry.add(datpayobj);
							// preKey = strKey;
							// }
							rtnMap.put(preKey, dpayAry);
							dpayAry = new ArrayList<Object>();
						}
						dpayAry.add(datpayobj);
						preKey = strKey;
					} else if (tblNm.matches("DATPAY|DATDOC")) {
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
					} else if (tblNm.equals("PrtRpt")) {
						parptobj = new Pareport();
						PropertyUtils.copyProperties(parptobj, data);
						strKey = StringUtil.nullToString(parptobj.getPaycode());
						rtnMap.put(strKey, parptobj);
					} else if (tblNm.matches("TEACHAWBas|TEACHAW")) {
						baspayobj = new Pabaspay();
						PropertyUtils.copyProperties(baspayobj, data);
						strKey = StringUtil.nullToString(baspayobj.getPayidno())
								+ StringUtil.nullToString(baspayobj.getPaycode());
						rtnMap.put(strKey, baspayobj);
					}
				}
				if (tblNm.equals("BASPAY")) {
					rtnMap.put(preKey, bpayAry);
				}
				if (tblNm.matches("PrtDATPAY|DATPAY|DATDOC")) {
					rtnMap.put(preKey, dpayAry);
				}
				if (tblNm.equals("TRAPAY")) {
					rtnMap.put(preKey, tpayAry);
				}
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

	// trapsnobj:異動檔,bpsnobj:基本檔,salnum餉單編號,traflag:N(新)O(原)patradt:發薪日,userid:登入者,HID:醫院別
	private Padatpsn setDatPsnObj(Patrapsn trapsnobj, Pabaspsn bpsnobj, int salnum, String traflag, String patradt,
			String userid, String hid) {
		Padatpsn rtnobj = new Padatpsn();

		if (trapsnobj != null) {
			rtnobj.setSalnum(salnum);
			rtnobj.setPaybank(java.sql.Date.valueOf(patradt));
			rtnobj.setPayidno(trapsnobj.getPayidno());
			rtnobj.setPaycard(trapsnobj.getPaycard());
			rtnobj.setPayempno(trapsnobj.getPayempno());

			rtnobj.setPaynamec(trapsnobj.getPaynamec());
			rtnobj.setPaydept(trapsnobj.getPaydept());
			rtnobj.setPayupdid(userid);
			rtnobj.setHid(hid);
			rtnobj.setInsuamt(trapsnobj.getInsuamt());

			if (traflag.equals("N")) {
				rtnobj.setPaytitle(trapsnobj.getPaytitle());
				rtnobj.setPayrnk2n(trapsnobj.getPayrnk2n());
				rtnobj.setPayrank3(trapsnobj.getPayrank3());
				rtnobj.setPaydegr2(trapsnobj.getPaydegr2());
				rtnobj.setPaydegre(trapsnobj.getPaydegre());

				rtnobj.setPayrank(trapsnobj.getPayrank());
				rtnobj.setPayrank2(trapsnobj.getPayrank2());
				rtnobj.setPayrankc(trapsnobj.getPayrankc());
				rtnobj.setPaypfdoc(trapsnobj.getPaypfdoc());
				rtnobj.setPacsflag(trapsnobj.getPacsflag());

				rtnobj.setPaypfsp1(trapsnobj.getPaypfsp1());
				rtnobj.setPaypfsp2(trapsnobj.getPaypfsp2());
				rtnobj.setPaypfsp3(trapsnobj.getPaypfsp3());
				rtnobj.setPayrank4(trapsnobj.getPayrank4());
			} else if (traflag.equals("O")) {
				rtnobj.setPaytitle(trapsnobj.getPaotitle());
				rtnobj.setPayrnk2n(trapsnobj.getPaornk2n());
				rtnobj.setPayrank3(trapsnobj.getPaorank3());
				rtnobj.setPaydegr2(trapsnobj.getPaodegr2());
				rtnobj.setPaydegre(trapsnobj.getPaodegre());

				rtnobj.setPayrank(trapsnobj.getPaorank());
				rtnobj.setPayrank2(trapsnobj.getPaorank2());
				rtnobj.setPayrankc(trapsnobj.getPaorankc());
				rtnobj.setPaypfdoc(trapsnobj.getPaopfdoc());
				rtnobj.setPacsflag(trapsnobj.getPaocsfla());

				rtnobj.setPaypfsp1(trapsnobj.getPaopfsp1());
				rtnobj.setPaypfsp2(trapsnobj.getPaopfsp2());
				rtnobj.setPaypfsp3(trapsnobj.getPaopfsp3());
				rtnobj.setPayrank4(trapsnobj.getPaorank4());
			}

		} else if (bpsnobj != null) {
			rtnobj.setSalnum(salnum);
			rtnobj.setPaybank(java.sql.Date.valueOf(patradt));
			rtnobj.setPayidno(bpsnobj.getPayidno());
			rtnobj.setPaycard(bpsnobj.getPaycard());
			rtnobj.setPayempno(bpsnobj.getPayempno());

			rtnobj.setPaynamec(bpsnobj.getPaynamec());
			rtnobj.setPaydept(bpsnobj.getPaydept());
			rtnobj.setPaytitle(bpsnobj.getPaytitle());
			rtnobj.setPayrnk2n(bpsnobj.getPayrnk2n());
			rtnobj.setPayrank3(bpsnobj.getPayrank3());

			rtnobj.setPaydegr2(bpsnobj.getPaydegr2());
			rtnobj.setPaydegre(bpsnobj.getPaydegre());
			rtnobj.setPayrank(bpsnobj.getPayrank());
			rtnobj.setPayrank2(bpsnobj.getPayrank2());
			rtnobj.setPayrankc(bpsnobj.getPayrankc());

			rtnobj.setPaypfdoc(bpsnobj.getPaypfdoc());
			rtnobj.setPacsflag(bpsnobj.getPacsflag());
			rtnobj.setPaypfsp1(bpsnobj.getPaypfsp1());
			rtnobj.setPaypfsp2(bpsnobj.getPaypfsp2());
			rtnobj.setPaypfsp3(bpsnobj.getPaypfsp3());
			rtnobj.setPayupdid(userid);
			rtnobj.setPayrank4(bpsnobj.getPayrank4());
			rtnobj.setHid(hid);
			rtnobj.setInsuamt(bpsnobj.getInsuamt());
		}
		return rtnobj;
	}

	// 寫入報表檔
	// prtMap 月支map
	private String updPrtTblMap(HashMap<String, Paprtdtl> prtMap, VGHDataAccess da) {
		String me = getClass().getName() + ".updPrtTblMap()";
		String rtn = "";
		ArrayList<Object> param = new ArrayList<Object>();
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> batch_sql = new ArrayList<Object>(); // batch_update sql 資料
		int write_cnt = 0;
		int limitcnt = 100;
		Map valMap = null;
		// 發薪明細_寫入_1_rpdtl
		String sqlRptDtl_ins = "INSERT INTO " + this.tRPDTL
				+ " (PAYIDNO, PAYBANK, PAYCARD, PAYNAMEC, PAYEMPNO, PAYACNO, PAYGEDNO, PAYDEPT, PADEPNMC, PATITSUB, PAYTITLE, PAYTITLP, PAYRANK, RANKABBR, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYLVEFG, TRANAME, PAEFFDT, LASTDAYNO, PAYDAYNO, PAYOBASE, PAYOSPSU, PAYOCHALW, PAYOSTBN, PAYOSUPLY, PAYBASE, PAYRBASE, PAYSPSU, PAYRSPSU, PAYSPSD, PAYRSPSD, PAYCHALW, PAYRCHALW, PAYSTBNT, PAYRSTBN, PAYTRANS, PAYSUPLY, PAYRSUPLY, PAYPFNT, PAYSTAL, PAYSTALD, PAYNIGHT, PAYYEAR, PAYADDHL, PAYGRADE, PAYPROMO, PAYADRTN, PAYCPLM1, PAYCPLM2, PAYCPLM3, PAYEMPFD, PAYSARS, PAYEXTRA, PAYGEINS, PAYDPINS, PAYLBINS, PAYRETIR, PAYTAX, PAYGEDP, PAYVETD1, PAYHLOA1, PAYHLOA2, PAYHLOA3, PAYHLOA4, PAYHLOA5, PAYHLOA6, PAYDLOAN, PAYMHOUS, PAYNSPFD, PAYPARK1, PAYPARK2, PAYGYM, PAYLIEN, PAYMEAL, PAYCARDF, PAYDORM1, PAYDORM2, PAYDORM3, PAYOTHER1, PAYOTHER2, PAYOTHER3, PAYAGEN1, PAYTOTAL, PAYTOTIS, PAYTLDED, PAYNET, HID, SALNUM) "
				+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			da.setAutoCommit(false);

			if (prtMap != null) {
				for (Map.Entry<String, Paprtdtl> prtentry : prtMap.entrySet()) {

					Paprtdtl data = prtentry.getValue();
					if (data != null) {
						valMap = data.getValMap();
						batch_sql.add(sqlRptDtl_ins);
						param = new ArrayList<Object>();
						System.out.println(data.getPayidno());
						param.add(data.getPayidno());
						param.add(data.getPaybank());
						param.add(data.getPaycard());
						param.add(data.getPaynamec());
						param.add(data.getPayempno());
						//
						param.add(data.getPayacno());
						param.add(data.getPaygedno());
						param.add(data.getPaydept());
						param.add(data.getPadepnmc());
						param.add(data.getPatitsub());
						//
						param.add(data.getPaytitle());
						param.add(data.getPaytitlp());
						param.add(data.getPayrank());
						param.add(data.getRankabbr());
						param.add(data.getPayrnk2n());
						//
						param.add(data.getPayrank3());
						param.add(data.getPaydegr2());
						param.add(data.getPaydegre());
						param.add(data.getPaylvefg());
						param.add(data.getTraname());
						//
						param.add(data.getPaeffdt());
						param.add(data.getLastdayno());
						param.add(data.getPaydayno());
						if (valMap.get("PAYOBASE") != null)
							param.add(valMap.get("PAYOBASE"));
						else
							param.add(data.getPayobase());

						if (valMap.get("PAYOSPSU") != null)
							param.add(valMap.get("PAYOSPSU"));
						else
							param.add(data.getPayospsu());
						//
						if (valMap.get("PAYOCHALW") != null)
							param.add(valMap.get("PAYOCHALW"));
						else
							param.add(data.getPayochalw());

						if (valMap.get("PAYOSTBN") != null)
							param.add(valMap.get("PAYOSTBN"));
						else
							param.add(data.getPayostbn());

						if (valMap.get("PAYOSUPLY") != null)
							param.add(valMap.get("PAYOSUPLY"));
						else
							param.add(data.getPayosuply());

						if (valMap.get("PAYBASE") != null)
							param.add(valMap.get("PAYBASE"));
						else
							param.add(data.getPaybase());

						if (valMap.get("PAYRBASE") != null)
							param.add(valMap.get("PAYRBASE"));
						else
							param.add(data.getPayrbase());
						//
						if (valMap.get("PAYSPSU") != null)
							param.add(valMap.get("PAYSPSU"));
						else
							param.add(data.getPayspsu());

						if (valMap.get("PAYRSPSU") != null)
							param.add(valMap.get("PAYRSPSU"));
						else
							param.add(data.getPayrspsu());

						if (valMap.get("PAYSPSD") != null)
							param.add(valMap.get("PAYSPSD"));
						else
							param.add(data.getPayspsd());

						if (valMap.get("PAYRSPSD") != null)
							param.add(valMap.get("PAYRSPSD"));
						else
							param.add(data.getPayrspsd());

						if (valMap.get("PAYCHALW") != null)
							param.add(valMap.get("PAYCHALW"));
						else
							param.add(data.getPaychalw());
						//
						if (valMap.get("PAYRCHALW") != null)
							param.add(valMap.get("PAYRCHALW"));
						else
							param.add(data.getPayrchalw());

						if (valMap.get("PAYSTBNT") != null)
							param.add(valMap.get("PAYSTBNT"));
						else
							param.add(data.getPaystbnt());

						if (valMap.get("PAYRSTBN") != null)
							param.add(valMap.get("PAYRSTBN"));
						else
							param.add(data.getPayrstbn());

						if (valMap.get("PAYTRANS") != null)
							param.add(valMap.get("PAYTRANS"));
						else
							param.add(data.getPaytrans());

						if (valMap.get("PAYSUPLY") != null)
							param.add(valMap.get("PAYSUPLY"));
						else
							param.add(data.getPaysuply());
						//
						if (valMap.get("PAYRSUPLY") != null)
							param.add(valMap.get("PAYRSUPLY"));
						else
							param.add(data.getPayrsuply());

						if (valMap.get("PAYPFNT") != null)
							param.add(valMap.get("PAYPFNT"));
						else
							param.add(data.getPaypfnt());

						if (valMap.get("PAYSTAL") != null)
							param.add(valMap.get("PAYSTAL"));
						else
							param.add(data.getPaystal());

						if (valMap.get("PAYSTALD") != null)
							param.add(valMap.get("PAYSTALD"));
						else
							param.add(data.getPaystald());

						if (valMap.get("PAYNIGHT") != null)
							param.add(valMap.get("PAYNIGHT"));
						else
							param.add(data.getPaynight());
						//
						if (valMap.get("PAYYEAR") != null)
							param.add(valMap.get("PAYYEAR"));
						else
							param.add(data.getPayyear());

						if (valMap.get("PAYADDHL") != null)
							param.add(valMap.get("PAYADDHL"));
						else
							param.add(data.getPayaddhl());

						if (valMap.get("PAYGRADE") != null)
							param.add(valMap.get("PAYGRADE"));
						else
							param.add(data.getPaygrade());

						if (valMap.get("PAYPROMO") != null)
							param.add(valMap.get("PAYPROMO"));
						else
							param.add(data.getPaypromo());

						if (valMap.get("PAYADRTN") != null)
							param.add(valMap.get("PAYADRTN"));
						else
							param.add(data.getPayadrtn());
						//
						if (valMap.get("PAYCPLM1") != null)
							param.add(valMap.get("PAYCPLM1"));
						else
							param.add(data.getPaycplm1());

						if (valMap.get("PAYCPLM2") != null)
							param.add(valMap.get("PAYCPLM2"));
						else
							param.add(data.getPaycplm2());

						if (valMap.get("PAYCPLM3") != null)
							param.add(valMap.get("PAYCPLM3"));
						else
							param.add(data.getPaycplm3());

						if (valMap.get("PAYEMPFD") != null)
							param.add(valMap.get("PAYEMPFD"));
						else
							param.add(data.getPayempfd());

						if (valMap.get("PAYSARS") != null)
							param.add(valMap.get("PAYSARS"));
						else
							param.add(data.getPaysars());
						//
						if (valMap.get("PAYEXTRA") != null)
							param.add(valMap.get("PAYEXTRA"));
						else
							param.add(data.getPayextra());

						if (valMap.get("PAYGEINS") != null)
							param.add(valMap.get("PAYGEINS"));
						else
							param.add(data.getPaygeins());

						if (valMap.get("PAYDPINS") != null)
							param.add(valMap.get("PAYDPINS"));
						else
							param.add(data.getPaydpins());

						if (valMap.get("PAYLBINS") != null)
							param.add(valMap.get("PAYLBINS"));
						else
							param.add(data.getPaylbins());

						if (valMap.get("PAYRETIR") != null)
							param.add(valMap.get("PAYRETIR"));
						else
							param.add(data.getPayretir());
						//
						if (valMap.get("PAYTAX") != null)
							param.add(valMap.get("PAYTAX"));
						else
							param.add(data.getPaytax());

						if (valMap.get("PAYGEDP") != null)
							param.add(valMap.get("PAYGEDP"));
						else
							param.add(data.getPaygedp());

						if (valMap.get("PAYVETD1") != null)
							param.add(valMap.get("PAYVETD1"));
						else
							param.add(data.getPayvetd1());

						if (valMap.get("PAYHLOA1") != null)
							param.add(valMap.get("PAYHLOA1"));
						else
							param.add(data.getPayhloa1());

						if (valMap.get("PAYHLOA2") != null)
							param.add(valMap.get("PAYHLOA2"));
						else
							param.add(data.getPayhloa2());
						//
						if (valMap.get("PAYHLOA3") != null)
							param.add(valMap.get("PAYHLOA3"));
						else
							param.add(data.getPayhloa3());

						if (valMap.get("PAYHLOA4") != null)
							param.add(valMap.get("PAYHLOA4"));
						else
							param.add(data.getPayhloa4());

						if (valMap.get("PAYHLOA5") != null)
							param.add(valMap.get("PAYHLOA5"));
						else
							param.add(data.getPayhloa5());

						if (valMap.get("PAYHLOA6") != null)
							param.add(valMap.get("PAYHLOA6"));
						else
							param.add(data.getPayhloa6());

						if (valMap.get("PAYDLOAN") != null)
							param.add(valMap.get("PAYDLOAN"));
						else
							param.add(data.getPaydloan());
						//
						if (valMap.get("PAYMHOUS") != null)
							param.add(valMap.get("PAYMHOUS"));
						else
							param.add(data.getPaymhous());

						if (valMap.get("PAYNSPFD") != null)
							param.add(valMap.get("PAYNSPFD"));
						else
							param.add(data.getPaynspfd());

						if (valMap.get("PAYPARK1") != null)
							param.add(valMap.get("PAYPARK1"));
						else
							param.add(data.getPaypark1());

						if (valMap.get("PAYPARK2") != null)
							param.add(valMap.get("PAYPARK2"));
						else
							param.add(data.getPaypark2());

						if (valMap.get("PAYGYM") != null)
							param.add(valMap.get("PAYGYM"));
						else
							param.add(data.getPaygym());
						//
						if (valMap.get("PAYLIEN") != null)
							param.add(valMap.get("PAYLIEN"));
						else
							param.add(data.getPaylien());

						if (valMap.get("PAYMEAL") != null)
							param.add(valMap.get("PAYMEAL"));
						else
							param.add(data.getPaymeal());

						if (valMap.get("PAYCARDF") != null)
							param.add(valMap.get("PAYCARDF"));
						else
							param.add(data.getPaycardf());

						if (valMap.get("PAYDORM1") != null)
							param.add(valMap.get("PAYDORM1"));
						else
							param.add(data.getPaydorm1());

						if (valMap.get("PAYDORM2") != null)
							param.add(valMap.get("PAYDORM2"));
						else
							param.add(data.getPaydorm2());
						//
						if (valMap.get("PAYDORM3") != null)
							param.add(valMap.get("PAYDORM3"));
						else
							param.add(data.getPaydorm3());

						if (valMap.get("PAYOTHER1") != null)
							param.add(valMap.get("PAYOTHER1"));
						else
							param.add(data.getPayother1());

						if (valMap.get("PAYOTHER2") != null)
							param.add(valMap.get("PAYOTHER2"));
						else
							param.add(data.getPayother2());

						if (valMap.get("PAYOTHER3") != null)
							param.add(valMap.get("PAYOTHER3"));
						else
							param.add(data.getPayother3());

						if (valMap.get("PAYAGEN1") != null)
							param.add(valMap.get("PAYAGEN1"));
						else
							param.add(data.getPayagen1());
						//
						param.add(data.getPaytotal());
						param.add(data.getPaytotis());
						param.add(data.getPaytlded());
						param.add(data.getPaynet());
						param.add(data.getHid());
						param.add(data.getSalnum());

						params.add(param);

						if (++write_cnt % limitcnt == 0 || write_cnt == prtMap.size()) {
							// log.info(batch_sql+" "+batch_sql+" params:"+params);
							da.batchupdatenocommit(batch_sql, params);
							da.commit();

							batch_sql.clear();
							params.clear();
						}
					}
				}
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

	// 寫入月支個人
	// datpsnMap 月支map
	private String updDatTblMap(HashMap<String, Padatpsn> datpsnMap, HashMap<String, Padatpay> datpayMap,
			VGHDataAccess da) {
		String me = getClass().getName() + ".updDatTblMap()";
		String rtn = "";
		ArrayList<Object> param = new ArrayList<Object>();
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> batch_sql = new ArrayList<Object>(); // batch_update sql 資料
		int write_cnt = 0;
		int limitcnt = 100;

		// 發薪明細_寫入_1_padatpsn
		String sqlDatPsn_ins = "INSERT INTO " + this.tDatPsn
				+ " (SALNUM, PAYBANK, PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYTITLE, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, PAYPFDOC, PACSFLAG, PAYPFSP1, PAYPFSP2, PAYPFSP3, PAYUPDID, PAYUPDTM, PAYRANK4, HID, INSUAMT) "
				+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,current timestamp,?,?,?) ";
		// 發薪明細_寫入_1_padatpay
		String sqlDatPay_ins = "INSERT INTO " + this.tDatPay
				+ " (SALNUM, PAYIDNO, PAYCODE, PAYAMT, PAYAUTH, PAYIFTAX, PAYUPDTM, PAYFLAG, HID)  "
				+ " VALUES(?, ?, ?, ?, ?, ?, current timestamp, ?, ?) ";

		try {
			da.setAutoCommit(false);

			if (datpsnMap != null) {
				for (Map.Entry<String, Padatpsn> datpsnentry : datpsnMap.entrySet()) {

					Padatpsn data = datpsnentry.getValue();
					if (data != null) {

						batch_sql.add(sqlDatPsn_ins);
						param = new ArrayList<Object>();
						param.add(data.getSalnum());
						param.add(data.getPaybank());
						param.add(data.getPayidno());
						param.add(data.getPaycard());
						param.add(data.getPayempno());

						param.add(data.getPaynamec());
						param.add(data.getPaydept());
						param.add(data.getPaytitle());
						param.add(data.getPayrnk2n());
						param.add(data.getPayrank3());

						param.add(data.getPaydegr2());
						param.add(data.getPaydegre());
						param.add(data.getPayrank());
						param.add(data.getPayrank2());
						param.add(data.getPayrankc());

						param.add(data.getPaypfdoc());
						param.add(data.getPacsflag());
						param.add(data.getPaypfsp1());
						param.add(data.getPaypfsp2());
						param.add(data.getPaypfsp3());

						param.add(data.getPayupdid());
						param.add(data.getPayrank4());
						param.add(data.getHid());
						param.add(data.getInsuamt());
						params.add(param);

						if (++write_cnt % limitcnt == 0 || write_cnt == datpsnMap.size()) {
							// log.info(batch_sql+" "+batch_sql+" params:"+params);
							da.batchupdatenocommit(batch_sql, params);
							da.commit();

							batch_sql.clear();
							params.clear();
						}
					}
				}
			} else if (datpayMap != null) {
				for (Map.Entry<String, Padatpay> datpayentry : datpayMap.entrySet()) {
					Padatpay data = datpayentry.getValue();

					batch_sql.add(sqlDatPay_ins);
					param = new ArrayList<Object>();
					if (data.getPaycode() == null || data.getPayauth() == null || data.getPayiftax() == null
							|| data.getPayflag() == null) {
						log.info(" null:" + data.getPayidno());
					}
					param.add(data.getSalnum());
					param.add(StringUtil.nullToString(data.getPayidno()));
					param.add(StringUtil.nullToString(data.getPaycode()));
					param.add(data.getPayamt());
					param.add(StringUtil.nullToString(data.getPayauth()));
					param.add(StringUtil.nullToString(data.getPayiftax()));
					param.add(StringUtil.nullToString(data.getPayflag()));
					param.add(StringUtil.nullToString(hid));
					// log.info(" param:"+param);
					params.add(param);

					if (++write_cnt % limitcnt == 0 || write_cnt == datpayMap.size()) {
						// log.info(batch_sql+" "+batch_sql+" params:"+params);
						da.batchupdatenocommit(batch_sql, params);
						da.commit();

						batch_sql.clear();
						params.clear();
					}
				}
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
	 * [產生]報表檔 in_data:(1:餉單編號設定,salnum) 2產生發薪明細
	 * 
	 * 傳出:rtn文字訊息
	 **/
	public String prodPrtSal(String in_data, String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".prodPrtSal(),userid = " + userid;
		log.info(me + ",in_data = " + in_data + ",userid = " + userid + ",hid = " + hid);
		String rtn = "";
		int updcnt = 0;
		boolean updOK = false;
		int salnum = 0;
		int salnum_prt = 0; // 報表檔的餉單編號
		ResultSet rs = null;
		ArrayList<Object> batch_sql = new ArrayList<Object>(); // batch_update sql 資料
		params = new ArrayList<Object>();
		ArrayList<Object> param = new ArrayList<Object>();
		JSONArray array = null;
		JSONArray tabJsonary = null;
		HashMap<String, Object> datpsnMap = new HashMap();// 月支個人檔 map
		HashMap datpayMap = new HashMap();// 月支支扣檔 map
		HashMap<String, Object> prPrtMap = new HashMap(); // 報表架構 map
		HashMap<String, Paprtdtl> prtMap = new HashMap(); // 報表檔 map
		ArrayList<Padatpay> datpayAry = null;
		Map rowMap = new HashMap();
		Map valMap = new HashMap();
		Paprtdtl prtdtlobj = null;
		Pareport prtrptobj = null; // 報表檔架構
		long starttime = 0;
		long starttime9 = 0;
		String selEn = ""; // 處理項目
		String patradt = ""; // 異動日期
		String l_Idtno = "";
		Date nowdate3 = null;
		int old_thisMDay = 0;// 舊制 本月日數
		int old_lastMDay = 0;// 舊制上月日數
		int new_thisMDay = 0;// 新制 本月日數
		int new_lastMDay = 0;// 新制上月日數
		int l_thisMday = 0; // 本月日數
		int l_lastMday = 0; // 上月日數
		String l_tracod = ""; // 異動原因
		String l_lvefg = ""; // 離職註記Y
		java.sql.Date l_oldWorkfirst; // 月頭薪 工作月第一天
		java.sql.Date l_oldWorkend; // 月頭薪 工作月最後一天
		java.sql.Date l_newWorkfirst; // 月尾薪 工作月第一天
		java.sql.Date l_newWorkend; // 月尾薪 工作月最後一天
		int l_pay = 0; // 總所得--當月
		int l_issue = 0; // 總所得--補發
		int l_duduct = 0; // 總扣得額
		String l_paycode = "";
		int l_prepaynum = 0;
		int l_Dat_payamt = 0;
		String rowName = "";

		// 報表檔_查詢,報表檔是否己產,若有,才要做還原動作
		String sqlDatpsn_inq = "select salnum from " + this.tRPDTL
				+ " where SALNUM=? and HID=? group by SALNUM with ur";

		// 報表檔_刪除,KEY=餉單編號
		String sqlRptDtl_del = "delete from " + this.tRPDTL + " where SALNUM=? and HID=? ";

		// 以發薪明細為主,寫入到報表檔
		String sqlRptDtl_inq = "select PAYOBASE, PAYOSPSU, PAYOCHALW, PAYOSTBN, PAYOSUPLY, PAYBASE, PAYRBASE, PAYSPSU, PAYRSPSU, PAYSPSD, PAYRSPSD, PAYCHALW, PAYRCHALW, PAYSTBNT, PAYRSTBN, PAYTRANS, PAYSUPLY, PAYRSUPLY, PAYPFNT, PAYSTAL, PAYSTALD, PAYNIGHT, PAYYEAR, PAYADDHL, PAYGRADE, PAYPROMO, PAYADRTN, PAYCPLM1, PAYCPLM2, PAYCPLM3, PAYEMPFD, PAYSARS, PAYEXTRA, PAYGEINS, PAYDPINS, PAYLBINS, PAYRETIR, PAYTAX, PAYGEDP, PAYVETD1, PAYHLOA1, PAYHLOA2, PAYHLOA3, PAYHLOA4, PAYHLOA5, PAYHLOA6, PAYDLOAN, PAYMHOUS, PAYNSPFD, PAYPARK1, PAYPARK2, PAYGYM, PAYLIEN, PAYMEAL, PAYCARDF, PAYDORM1, PAYDORM2, PAYDORM3, PAYOTHER1, PAYOTHER2, PAYOTHER3, PAYAGEN1, PAYTOTAL, PAYTOTIS, PAYTLDED, PAYNET, HID, SALNUM "
				+ " from " + this.tRPDTL + " where SALNUM=? and HID=? ";
		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			da.setAutoCommit(false);
			JSONObject object_in_data = JSONObject.fromObject(in_data);

			selEn = StringUtil.nullToString(object_in_data.getString("selEn"));
			// 產生發薪報表檔_月
			if (selEn.matches("Prt_M")) {
				starttime = System.currentTimeMillis();
				salnum = object_in_data.getInt("salnum");
				patradt = object_in_data.getString("patradt");

				// 找出[本月日數][本月月底][上月日數][上月月底]
				String nowdate2 = patradt.substring(0, 4) + "-" + patradt.substring(5, 7) + "-" + "01";
				nowdate3 = DateUtil.StringToDate(nowdate2);
				// 月頭薪 工作月第1天
				l_oldWorkfirst = java.sql.Date.valueOf(nowdate2);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(nowdate3);
				calendar.add(Calendar.MONTH, 1);
				calendar.add(Calendar.DATE, -1);
				// 月頭薪 工作月最後一天=2025.7.31 發薪日 2025.7.1 舊制(月頭,發放2025.7)本月日數=31
				l_oldWorkend = java.sql.Date.valueOf(formatter.format(calendar.getTime()));
				// 發薪日 2025.7.1 舊制(月頭,發放2025.7)本月日數=31
				old_thisMDay = calendar.get(Calendar.DATE);
				// 發薪日 2025.7.1 新制(月尾,發放2025.6)本月(6月)日數=30
				// 發薪日 2025.7.1 舊制(月頭,發放2025.7)上月(6月)日數=30
				calendar.setTime(nowdate3);
				calendar.add(Calendar.DATE, -1);
				// 月尾薪 工作月最後一天=2025.6.30 發薪日 2025.7. 新制(月尾,發放2025.6)本月(6月)日數=30
				l_newWorkend = java.sql.Date.valueOf(formatter.format(calendar.getTime()));
				new_thisMDay = calendar.get(Calendar.DATE);
				old_lastMDay = calendar.get(Calendar.DATE);
				// 發薪日 2025.7.1 新制(月尾,發放2025.6)上月(5月)日數=31
				calendar.setTime(nowdate3);
				calendar.add(Calendar.MONTH, -1);
				// 月尾薪 工作月第1天
				l_newWorkfirst = java.sql.Date.valueOf(formatter.format(calendar.getTime()));
				calendar.add(Calendar.DATE, -1);
				java.sql.Date dd = java.sql.Date.valueOf(formatter.format(calendar.getTime()));
				new_lastMDay = calendar.get(Calendar.DATE);

				// 依餉單編號,查詢報表檔.餉單編號
				param = new ArrayList<Object>();
				param.add(salnum);
				param.add(hid);
				log.info(me + "  1.先查詢月支個人檔,若有,進行[還原]動作" + sqlDatpsn_inq + "...params:" + param.toString());
				rs = da.query(sqlDatpsn_inq, param);
				while (rs.next()) {
					salnum_prt = (rs.getInt("SALNUM"));
				}
				log.info(me + "  1查詢報表檔,SALNUM=" + salnum_prt);
				if (salnum_prt != 0) {
					starttime9 = System.currentTimeMillis();
					log.info(me + "  2-1刪除報表檔,SALNUM=" + salnum_prt);
					updcnt = 0;
					updcnt = da.update(sqlRptDtl_del, param);
					if (updcnt > 0)
						updOK = true;
					if (!updOK) {
						log.info(me + "  2-1刪除報表檔,SALNUM=" + salnum_prt + "  ==>失敗");
						throw new SQLException("2-1刪除報表檔,SALNUM=" + salnum_prt + "=>失敗");
					} else
						log.info(me + "  2-1刪除報表檔,SALNUM=" + salnum_prt + ",筆數:" + updcnt + " 共花費："
								+ (System.currentTimeMillis() - starttime9) + "毫秒");

					da.commit();
				}

				starttime9 = System.currentTimeMillis();
				param = new ArrayList<Object>();
				param.add(salnum);
				param.add(hid);
				param.add(salnum);
				param.add(hid);
				datpsnMap = this.getPaItmMap(param, "PrtDATPSN", hid, da);
				log.info(me + "  2-1.讀取月支個人 ,筆數:" + datpsnMap.size() + ",共花費："
						+ (System.currentTimeMillis() - starttime9) + "毫秒");

				starttime9 = System.currentTimeMillis();
				param = new ArrayList<Object>();
				param.add(salnum);
				param.add(hid);
				param.add(salnum);
				param.add(hid);
				param.add(patradt);
				param.add(hid);
				datpayMap = this.getPaItmMap(param, "PrtDATPAY", hid, da);
				log.info(me + "  2-2.讀取月支明細 ,筆數:" + datpayMap.size() + "共花費："
						+ (System.currentTimeMillis() - starttime9) + "毫秒");

				starttime9 = System.currentTimeMillis();
				param = new ArrayList<Object>();
				param.add(hid);
				param.add("1");
				prPrtMap = this.getPaItmMap(param, "PrtRpt", hid, da);
				log.info(me + "  2-3.讀取報表架構 ,筆數:" + prPrtMap.size() + "共花費：" + (System.currentTimeMillis() - starttime9)
						+ "毫秒");

				starttime9 = System.currentTimeMillis();
				param = new ArrayList<Object>();
				param.add(salnum);
				param.add(hid);
				rs = da.query(sqlRptDtl_inq, param);

				ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();

				for (int i = 1; i <= columnCount; i++) {
					String columnName = metaData.getColumnName(i);
					// Object columnValue = rs.getObject(i);
					rowMap.put(i, columnName);
				}

				log.info(me + "  2-4.讀取報表檔 ,筆數:" + columnCount + "共花費：" + (System.currentTimeMillis() - starttime9)
						+ "毫秒");

				for (Map.Entry<String, Object> Entry : datpsnMap.entrySet()) {
					datpayAry = null;
					l_pay = 0; // 總所得--當月
					l_issue = 0; // 總所得--補發
					l_duduct = 0; // 總扣得額
					l_paycode = "";
					l_Idtno = Entry.getKey();
					System.out.println(l_Idtno);
					/*
					 * if ("R224869259".equals(l_Idtno)){ System.out.println(l_Idtno); }
					 */
					l_lvefg = "N";
					// 清空報表架構
					for (Map.Entry<String, Object> structEntry : prPrtMap.entrySet()) {
						prtrptobj = (Pareport) structEntry.getValue();
						prtrptobj.setPayamt(0);
					}

					prtdtlobj = (Paprtdtl) Entry.getValue();
					// System.out.println(prtdtlobj.getPayidno());
					// 月頭
					if ("N".equals(prtdtlobj.getContract())) {
						l_thisMday = old_thisMDay; // 本月日數
						l_lastMday = old_lastMDay; // 上月日數
					} else if ("Y".equals(prtdtlobj.getContract())) {
						l_thisMday = new_thisMDay;
						l_lastMday = new_lastMDay;
					}
					l_tracod = Integer.toString(prtdtlobj.getPatracod());
					// 新進,復職
					if (l_tracod.matches("1|2")) {
						l_lastMday = 0;
						if ("N".equals(prtdtlobj.getContract()))
							l_thisMday = DateUtil.calDays(prtdtlobj.getPaeffdt(), l_oldWorkend) + 1;
						else if ("Y".equals(prtdtlobj.getContract()))
							l_thisMday = DateUtil.calDays(prtdtlobj.getPaeffdt(), l_newWorkend) + 1;
					} else if (l_tracod.matches("21|22|23|24")) {
						if (l_tracod.matches("21|22")) {
							l_lvefg = "Y";
						}
						if ("N".equals(prtdtlobj.getContract()))
							l_thisMday = DateUtil.calDays(l_oldWorkfirst, prtdtlobj.getPaeffdt());
						else if ("Y".equals(prtdtlobj.getContract()))
							l_thisMday = DateUtil.calDays(l_newWorkfirst, prtdtlobj.getPaeffdt());
					}

					prtdtlobj.setPaylvefg(l_lvefg); // 離職Y
					prtdtlobj.setPaydayno(l_thisMday); // 本月日數
					prtdtlobj.setLastdayno(l_lastMday); // 上月日數

					// 月支支扣
					datpayAry = (ArrayList) datpayMap.get(l_Idtno);
					if (datpayAry != null) {
						for (Padatpay data : datpayAry) {
							l_paycode = data.getPaycode();
							l_Dat_payamt = data.getPayamt();
							if (data.getPaysort().equals("")) {
								if (l_paycode.substring(1, 2).equals("P")) {
									// 法定支扣 補發項(含實習費補發)
									// 1A0 FP009本薪(補) FP019專業加給(補) FP039主管加給(補) FP079實習費(補) FP089專業加給差額(補)
									// 2A0 FP009本薪(補)+FP029本薪增額(補) FP019專業加給(補) FP039主管加給(補) FP089專業加給差額(補)
									// FP109院區加給(補)
									// 3A0 FP009本薪(補) FP019專業加給(補) FP039主管加給(補) FP089專業加給差額(補) FP099責任加給(補)
									// FP199職務加給(補)
									if (l_paycode.substring(4, 5).equals("9") && l_paycode.compareTo("FP110") < 0) {
										l_issue = l_issue + l_Dat_payamt;
									} else
										l_pay = l_pay + data.getPayamt();
								} else if (l_paycode.substring(1, 2).equals("D")) {
									l_duduct = l_duduct + l_Dat_payamt;
								}
								prtrptobj = (Pareport) prPrtMap.get(l_paycode);
								if (prtrptobj == null) {
									prtrptobj = (Pareport) prPrtMap.get(l_paycode.substring(0, 4));
									if (prtrptobj != null) {
										prtrptobj.setPayamt(prtrptobj.getPayamt() + data.getPayamt());
									}
								} else {
									prtrptobj.setPayamt(data.getPayamt());
								}
							}
							// 上月
							else if (data.getPaysort().equals("O")) {
								prtrptobj = (Pareport) prPrtMap.get("O" + l_paycode.substring(0, 4));
								if (prtrptobj != null) {
									prtrptobj.setPayamt(l_Dat_payamt);
								}
							}
						}
					}
					// prPrtMap=pa.pareport報表檔架構
					// rowMap=pa.parepdetail(num,欄位名稱) rowMap(28,PAYBASE)
					valMap = new HashMap();
					for (Map.Entry<String, Object> prtEntry : prPrtMap.entrySet()) {
						prtrptobj = (Pareport) prtEntry.getValue();
						l_prepaynum = prtrptobj.getPaynum();
						rowName = (String) rowMap.get(l_prepaynum);
						if (valMap.get(rowName) != null)
							valMap.put(rowName, (Integer) valMap.get(rowName) + prtrptobj.getPayamt());
						else
							valMap.put(rowName, prtrptobj.getPayamt());
					}
					prtdtlobj.setValMap(valMap);
					prtdtlobj.setPaytotal(l_pay); // 總所得--當月
					prtdtlobj.setPaytotis(l_issue); // 總所得--補發
					prtdtlobj.setPaytlded(l_duduct);// 總扣得額
					prtdtlobj.setPaynet(l_pay - l_duduct);// 淨所得=總所得--當月-總扣得額

					prtMap.put(l_Idtno, prtdtlobj);

				}
				log.info(me + "  3.寫入報表檔....");
				starttime9 = System.currentTimeMillis();
				rtn = updPrtTblMap(prtMap, da);
				log.info(me + "  3.寫入報表檔  共花費：" + (System.currentTimeMillis() - starttime9) + "毫秒");

			}

		} catch (Exception e) {
			da.rollback();
			rtn += " fail, " + e;
			da.setAutoCommit(true);
			e.printStackTrace();
			log.info(me + " " + e.toString());
		} finally {
			closeAll(da, rs, connFlg);
		}

		return rtn;
	}

	/**
	 * 發薪明細處理 in_data:(1:餉單編號設定,salnum) 2產生發薪明細 tabJson:勾選的清單 傳出:rtn文字訊息
	 **/
	public String handelPayroll(String in_data, String tabJson, String userid, String hid, VGHDataAccess da)
			throws Exception {
		String me = getClass().getName() + ".handelPayroll(),userid = " + userid;
		log.info(me + ",in_data = " + in_data + " tabJson = " + tabJson + ",userid = " + userid + ",hid = " + hid);
		String rtn = "";
		String formnm = "";
		ResultSet rs = null;
		int salnum = 0;
		String tracod = "";
		String patradt = ""; // 異動日期
		int salnum_dat = 0; // 發薪明細的餉單編號
		int salnum_n = 0; // 設定新餉單編號
		int salnum_o = 0; // 原餉單編號
		int limitcnt = 100;
		int write_cnt = 0; // 寫入table第N筆
		int intBpsnTpay = 0; // 基本檔個人+異動檔支扣 旗標 1:基本檔個人+異動檔支扣 2:異動檔支扣(41支扣 )
		String selEn = ""; // 處理項目TranSetSalnum BatSetSalnum
		// 設定餉單編號_異動檔
		String sqlsalnumTra_upd = "update " + this.tTraPsn + " set SALNUM=? where SALNUM=? and PATRADT=? and HID=? ";
		String sqlsalnumTraID_upd = "update " + this.tTraPsn
				+ " set SALNUM=? where SALNUM=? and PAYIDNO=? and PATRADT=? and PATRACOD=? and HID=? ";
		// 設定餉單編號_批次檔
		String sqlsalnumBat_upd = "update " + this.tBatPay + " set SALNUM=? where SALNUM=? and PATRADT=? and HID=? ";
		String sqlsalnumBatID_upd = "update " + this.tBatPay
				+ " set SALNUM=? where SALNUM=? and PAYIDNO=? and PATRADT=? and PAYCODE=? and HID=? ";
		// 發薪明細_查詢padatpsn是否己產,若有,才要做還原動作
		String sqlDatpsn_inq = "select salnum from " + this.tDatPsn
				+ " where SALNUM=? and HID=? group by SALNUM with ur";
		// 發薪明細_還原_1_更新異動檔旗標
		String sqlTrafg_upd = "update " + this.tTraPsn
				+ " set PAYFLAG='' where SALNUM=? and (PAYFLAG IS NOT NULL or PAYFLAG<>'') and HID=? ";
		// 發薪明細_還原_2_刪除padatpsn
		String sqlDatPsn_del = "delete from " + this.tDatPsn + " where SALNUM=? and HID=? ";
		// 發薪明細_還原_3_刪除padatpay F V
		String sqlDatPay_del = "delete from " + this.tDatPay + " where SALNUM=? and HID=? and SUBSTR(PAYCODE,1,1)=? ";
		// 發薪明細_還原_3_刪除padatpay VD VP
		String sqlDatPayA_del = "delete from " + this.tDatPay + " where SALNUM=? and HID=? and SUBSTR(PAYCODE,1,2)=? ";
		// 發薪明細_寫入_1_padatpsn
		String sqlDatPsn_ins = "INSERT INTO " + this.tDatPsn
				+ " (SALNUM, PAYBANK, PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYTITLE, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, PAYPFDOC, PACSFLAG, PAYPFSP1, PAYPFSP2, PAYPFSP3, PAYUPDID, PAYUPDTM, PAYRANK4, HID, INSUAMT) "
				+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,current timestamp,?,?,?) ";
		// 發薪明細_寫入_1_padatpay
		String sqlDatPay_ins = "INSERT INTO " + this.tDatPay
				+ " (SALNUM, PAYIDNO, PAYCODE, PAYAMT, PAYAUTH, PAYIFTAX, PAYUPDTM, PAYFLAG, HID)  "
				+ " VALUES(?, ?, ?, ?, ?, ?, current timestamp, ?, ?) ";
		// 發薪明細_所得稅_1 給付金額淨額>起扣點+列入扣5%計算,金額>0,才能扣稅
		String sqlDatTax_inq = "SELECT a.payidno,a.paycard,a.paynamec,COALESCE(b.tamt,0) tamt,COALESCE(c.pamt,0) pamt "
				+ "from " + this.tDatPsn + " a " + "LEFT OUTER JOIN (SELECT salnum,payidno,a.hid,sum(payamt) tamt "
				+ "FROM " + this.tDatPay + " a " + "LEFT OUTER JOIN " + this.tPayItm
				+ " b ON a.paycode=b.paycode AND a.hid=b.hid "
				+ "WHERE SALNUM =?  AND a.HID=? AND substr(a.paycode,2,1)='P' AND b.payreward='Y' "
				+ "GROUP by salnum,payidno,a.hid) b ON a.payidno=b.payidno AND a.hid=b.hid AND a.salnum=b.salnum "
				+ "LEFT OUTER join (SELECT salnum,payidno,a.hid,sum(payamt) pamt " + "FROM " + this.tDatPay + " a "
				+ "LEFT OUTER JOIN " + this.tPayItm + " b ON a.paycode=b.paycode AND a.hid=b.hid "
				+ "WHERE SALNUM =?  AND a.HID=? AND substr(a.paycode,2,1)='P' AND b.payiftax='Y' "
				+ "GROUP by salnum,payidno,a.hid) c ON a.payidno=c.payidno AND a.hid=c.hid AND a.salnum=c.salnum "
				+ "WHERE a.salnum=? AND a.HID=? AND pamt > ? with ur";

		ArrayList<Object> batch_sql = new ArrayList<Object>(); // batch_update sql 資料
		params = new ArrayList<Object>();
		ArrayList<Object> param = new ArrayList<Object>();
		JSONArray array = null;
		JSONArray tabJsonary = null;
		long starttime = 0;
		long starttime9 = 0;
		HashMap baspsnMap = new HashMap();// 基本個人檔 map
		HashMap baspsnAllMap = new HashMap();// 基本個人檔(在職及離職1年內) map
		HashMap baspayMap = new HashMap();// 基本支扣檔 map
		HashMap<String, Object> trapsnMap = new HashMap();// 異動個人檔 map
		HashMap trapayMap = new HashMap();// 異動支扣檔 map
		HashMap<String, ArrayList> tra41Map = new HashMap();// 異動支扣檔 _支扣map
		HashMap<String, Object> batpayMap = new HashMap();// 批次檔 map
		HashMap<String, Padatpsn> datpsnMap = new HashMap();// 月支個人檔 map
		HashMap<String, Padatpay> datpayMap = new HashMap();// 月支支扣檔 map
		ArrayList<Pabaspay> baspayAry = null;
		ArrayList<Patrapay> trapayAry = null;
		ArrayList<Map<String, Object>> datTaxAry = null;
		Pabaspsn baspsnobj = null;
		Patrapsn trapsnobj = null;
		Padatpsn datpsnobj = null;
		Padatpay datpayobj = null;
		String strIdtno = "";
		String mIdtno = "";
		String strPaycode = "";
		int pamt = 0;// 支扣代碼=P,金額加總
		int tamt = 0;// 支扣代碼=P+應計稅,金額加總
		int mTaxVar = 0;// 入稅獎金*5%
		int updcnt = 0;
		boolean updOK = false;
		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			da.setAutoCommit(false);
			JSONObject object_in_data = JSONObject.fromObject(in_data);
			if (!"".equals(tabJson)) {
				tabJsonary = JSONArray.fromObject(tabJson);
			}
			selEn = StringUtil.nullToString(object_in_data.getString("selEn"));
			// 設定餉單編號
			if (selEn.matches("TranSetSalnum|BatSetSalnum")) {
				starttime = System.currentTimeMillis();
				salnum_n = object_in_data.getInt("salnum_n");
				salnum_o = object_in_data.getInt("salnum_o");
				patradt = StringUtil.nullToString(object_in_data.getString("patradt_o"));
				// 依勾選清單
				if (tabJsonary != null && tabJsonary.size() > 0) {
					for (int i = 0; i < tabJsonary.size(); ++i) {
						JSONObject obj = tabJsonary.getJSONObject(i);
						if (obj != null && obj.has("formnm")) {
							formnm = StringUtil.nullToString(obj.getString("formnm"));
							array = obj.getJSONArray("value");

							if (null != array && array.size() > 0) {
								for (int j = 0; j < array.size(); j++) {
									JSONObject obj_item = array.getJSONObject(j);
									Set<Map.Entry> entries = obj_item.entrySet();
									for (Map.Entry entry : entries) {
										JSONObject value = (JSONObject) entry.getValue();
										if (value.has("payidno")) {

											if ("PATRAPSN".equals(formnm)) {
												batch_sql.add(sqlsalnumTraID_upd);
											} else if ("PABATPAY".equals(formnm)) {
												batch_sql.add(sqlsalnumBatID_upd);
											}

											param = new ArrayList<Object>();
											param.add(salnum_n);
											param.add(salnum_o);
											param.add((!value.has("payidno")) ? "" : value.getString("payidno"));
											param.add((!value.has("patradt")) ? "" : value.getString("patradt"));
											if ("PATRAPSN".equals(formnm)) {
												param.add((!value.has("patracod")) ? "" : value.getString("patracod"));
											} else if ("PABATPAY".equals(formnm)) {
												param.add((!value.has("paycode")) ? "" : value.getString("paycode"));
											}

											param.add(hid);
											params.add(param);
											log.info(++write_cnt + " " + param);
										} else {
											log.info(me + value.getString("payidno") + "_1:空白");
										}
									}
								}
							}
						}
					}
				}
				// 餉單編號=0,寫入[新編號]
				else {
					param = new ArrayList<Object>();
					param.add(salnum_n);
					param.add(salnum_o);
					param.add(patradt);
					param.add(hid);
					params.add(param);
					if (selEn.equals("TranSetSalnum"))
						batch_sql.add(sqlsalnumTra_upd);
					else if (selEn.equals("BatSetSalnum"))
						batch_sql.add(sqlsalnumBat_upd);

					log.info(batch_sql + " " + batch_sql + " params:" + params);
				}

				if (batch_sql.size() > 0) {
					log.info(batch_sql + " " + batch_sql + " params:" + params);
					da.batchupdatenocommit(batch_sql, params);
					da.commit();
					// da.setAutoCommit(false);
					batch_sql.clear();
					params.clear();
				}

				log.info(me + "共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
			}
			// 產生發薪明細 Pay_1(基本+異動+批次) Pay_2(異動+批次) Pay_3(批次)
			else if (selEn.matches("Pay_1|Pay_2|Pay_3")) {
				starttime = System.currentTimeMillis();
				salnum = object_in_data.getInt("salnum");
				patradt = object_in_data.getString("patradt");
				// 依餉單編號,查詢月支個人檔.餉單編號
				param = new ArrayList<Object>();
				param.add(salnum);
				param.add(hid);
				log.info(me + "  1.先查詢月支個人檔,若有,進行[還原]動作" + sqlDatpsn_inq + "...params:" + param.toString());
				rs = da.query(sqlDatpsn_inq, param);
				while (rs.next()) {
					salnum_dat = (rs.getInt("SALNUM"));
				}
				log.info(me + "  1查詢padatpsn,SALNUM=" + salnum_dat);
				if (salnum_dat != 0) {
					starttime9 = System.currentTimeMillis();
					log.info(me + "  2-1還原padatpsn,SALNUM=" + salnum_dat + "  更新異動檔旗標");
					updcnt = da.update(sqlTrafg_upd, param);
					if (updcnt > 0)
						updOK = true;
					if (!updOK) {
						log.info(me + "  2-1還原padatpsn,SALNUM=" + salnum_dat + "  更新異動檔旗標=>失敗");
					} else
						log.info(me + "  2-1還原padatpsn,SALNUM=" + salnum_dat + "  更新異動檔旗標,筆數:" + updcnt + " 共花費："
								+ (System.currentTimeMillis() - starttime9) + "毫秒");

					starttime9 = System.currentTimeMillis();
					log.info(me + "  2-2還原padatpsn,SALNUM=" + salnum_dat + "  刪除padatpsn");
					updcnt = 0;
					updcnt = da.update(sqlDatPsn_del, param);
					if (updcnt > 0)
						updOK = true;
					if (!updOK) {
						log.info(me + "  2-2還原padatpsn,SALNUM=" + salnum_dat + "  刪除padatpsn==>失敗");
						throw new SQLException("還原padatpsn,SALNUM=" + salnum_dat + "  刪除padatpsn=>失敗");
					} else
						log.info(me + "  2-2還原padatpsn,SALNUM=" + salnum_dat + "  刪除padatpsn,筆數:" + updcnt + " 共花費："
								+ (System.currentTimeMillis() - starttime9) + "毫秒");

					starttime9 = System.currentTimeMillis();
					log.info(me + "  2-3還原padatpsn,SALNUM=" + salnum_dat + "  刪除padatpay F");
					updcnt = 0;
					param = new ArrayList<Object>();
					param.add(salnum);
					param.add(hid);
					param.add("F");
					updcnt = da.update(sqlDatPay_del, param);
					if (updcnt > 0)
						updOK = true;
					if (!updOK) {
						log.info(me + "  2-3還原padatpsn,SALNUM=" + salnum_dat + "  刪除padatpay F==>失敗");
						throw new SQLException("2-3還原padatpsn,SALNUM=" + salnum_dat + "  刪除padatpay F=>失敗");
					} else
						log.info(me + "  2-3還原padatpsn,SALNUM=" + salnum_dat + "  刪除padatpay F,筆數:" + updcnt + " 共花費："
								+ (System.currentTimeMillis() - starttime9) + "毫秒");
					da.commit();

					starttime9 = System.currentTimeMillis();
					updcnt = 0;
					param = new ArrayList<Object>();
					param.add(salnum);
					param.add(hid);
					param.add("V");
					log.info(me + "  2-4還原padatpsn,SALNUM=" + salnum_dat + "  刪除padatpay V,sql:" + sqlDatPay_del
							+ " param:" + param);
					updcnt = da.update(sqlDatPay_del, param);
					if (updcnt > 0)
						updOK = true;
					if (!updOK) {
						log.info(me + "  2-4還原padatpsn,SALNUM=" + salnum_dat + "  刪除padatpay V==>失敗");
						throw new SQLException("2-4還原padatpsn,SALNUM=" + salnum_dat + "  刪除padatpay VD=>失敗");
					} else
						log.info(me + "  2-4還原padatpsn,SALNUM=" + salnum_dat + "  刪除padatpay V,筆數:" + updcnt + " 共花費："
								+ (System.currentTimeMillis() - starttime9) + "毫秒");
				}
				starttime9 = System.currentTimeMillis();
				param = new ArrayList<Object>();
				param.add(hid);
				baspsnMap = this.getPaItmMap(param, "BASPSN", hid, da);
				log.info(me + "  2-1.讀取基本個人(在職),一人一筆 ,筆數:" + baspsnMap.size() + ",共花費："
						+ (System.currentTimeMillis() - starttime9) + "毫秒");

				starttime9 = System.currentTimeMillis();
				baspsnAllMap = this.getPaItmMap(param, "BASPSNALL", hid, da);
				log.info(me + "  2-2.讀取基本個人(在職及離職1年內),筆數:" + baspsnAllMap.size() + ",共花費："
						+ (System.currentTimeMillis() - starttime9) + "毫秒");

				starttime9 = System.currentTimeMillis();
				baspayMap = this.getPaItmMap(param, "BASPAY", hid, da);
				log.info(me + "  2-3.讀取基本支扣,筆數:" + baspayMap.size() + " 共花費："
						+ (System.currentTimeMillis() - starttime9) + "毫秒");

				starttime9 = System.currentTimeMillis();
				log.info(me + "  2-4.讀取批次檔;");
				param = new ArrayList<Object>();
				param.add(salnum);
				param.add(hid);
				batpayMap = this.getPaItmMap(param, "BATPAY", hid, da);
				log.info(me + "  2-4.讀取批次檔,筆數:" + batpayMap.size() + "  共花費："
						+ (System.currentTimeMillis() - starttime9) + "毫秒");

				starttime9 = System.currentTimeMillis();
				log.info(me + "  2-5.讀取異動個人,一個餉單編號,一人一筆");
				trapsnMap = this.getPaItmMap(param, "TRAPSN", hid, da);
				log.info(me + "  2-5.讀取異動個人,一個餉單編號,一人一筆,筆數:" + trapsnMap.size() + "共花費："
						+ (System.currentTimeMillis() - starttime9) + "毫秒");

				starttime9 = System.currentTimeMillis();
				log.info(me + "  2-6.讀取異動支扣;");
				trapayMap = this.getPaItmMap(param, "TRAPAY", hid, da);
				log.info(me + "  2-6.讀取異動支扣 ,筆數:" + trapayMap.size() + "共花費："
						+ (System.currentTimeMillis() - starttime9) + "毫秒");

				log.info(me + "  3.整合中....");

				// ----先以基本檔(在職)為主
				// 月支檔寫入條件.
				// 1.異動檔個人+異動檔支扣 11考績晉級 31晉級調薪 32改任換敘 33納實 34考績升等 35待遇調整 36晉級 37職務調整 38降級
				// 2.基本檔個人+異動檔支扣(有內容) 21離職 22退休 23停薪 24逾期請假 12考績獎金
				// 51年終獎金<==異動檔支扣(無內容),不用寫入月支個人,月支明細
				// 3.異動檔個人+異動檔支扣 異動檔(1新進 2復職)<==基本檔loop走完,再以此寫入
				// 4.異動檔41支扣,寫入trapay41map,寫月支明細再get
				// 5.批次檔 寫入月支支扣(批次檔->月支支扣)
				// ----trapay41map+batpaymap loop,更新datpaymap月支明細
				// ----datpsnmap,datpaymap -> insert
				// ----計算tax(給付金額淨額 > 起扣點,'2017.1.16 列入扣5%計算,金額>0,才能扣稅)

				// 基本檔+異動檔+批次檔
				if (selEn.matches("Pay_1")) {
					Iterator<Map.Entry<String, Pabaspsn>> itr = baspsnMap.entrySet().iterator();
					while (itr.hasNext()) {
						trapsnobj = null;
						datpsnobj = null;
						intBpsnTpay = 0;
						trapayAry = null;
						Map.Entry<String, Pabaspsn> entry = itr.next();
						strIdtno = entry.getKey();
						/*
						 * if (strIdtno.equals("F230551483")){ System.out.println(strIdtno); }
						 */
						baspsnobj = (Pabaspsn) entry.getValue();
						// 找異動檔,因KEY=IDNO
						trapsnobj = (Patrapsn) trapsnMap.get(strIdtno);
						// 1新進2復職(基本檔抓在職) 11考績晉級 31晉級調薪 32改任換敘 33納實 34考績升等 35待遇調整 36晉級 37職務調整 38降級
						if (trapsnobj != null) {
							tracod = String.valueOf(trapsnobj.getPatracod());
							if (tracod.matches("11|31|32|33|34|35|36|37|38")) {
								datpsnobj = new Padatpsn();
								datpsnobj = setDatPsnObj(trapsnobj, null, salnum, "N", patradt, userid, hid);
								datpsnMap.put(strIdtno, datpsnobj);
								// 月支支扣
								trapayAry = (ArrayList) trapayMap.get(strIdtno);
								if (trapayAry != null) {
									for (Patrapay data : trapayAry) {
										datpayobj = new Padatpay();
										datpayobj.setSalnum(salnum);
										datpayobj.setPayidno(data.getPayidno());
										datpayobj.setPaycode(data.getPaycode());
										datpayobj.setPayamt(data.getPayamt());
										datpayobj.setPayauth(data.getPayauth());
										datpayobj.setPayiftax(data.getPayiftax());
										datpayobj.setPayflag("");
										datpayobj.setHid(hid);
										datpayMap.put(data.getPayidno() + data.getPaycode(), datpayobj);
									}
								}
							}
							// 21離職 22退休 23停薪 24逾期請假
							// 12考績獎金 51年終獎金
							else if (tracod.matches("21|22|23|24|12|51")) {
								trapayAry = (ArrayList) trapayMap.get(strIdtno);
								if (trapayAry != null) {
									datpsnobj = new Padatpsn();
									datpsnobj = setDatPsnObj(trapsnobj, null, salnum, "O", patradt, userid, hid);
									datpsnMap.put(strIdtno, datpsnobj);
									// intBpsnTpay=1; //旗標 1:異動檔個人+異動檔支扣 2:異動檔離職,沒支扣,不用列入
									for (Patrapay data : trapayAry) {
										datpayobj = new Padatpay();
										datpayobj.setSalnum(salnum);
										datpayobj.setPayidno(data.getPayidno());
										datpayobj.setPaycode(data.getPaycode());
										datpayobj.setPayamt(data.getPayamt());
										datpayobj.setPayauth(data.getPayauth());
										datpayobj.setPayiftax(data.getPayiftax());
										datpayobj.setPayflag("");
										datpayobj.setHid(hid);
										datpayMap.put(data.getPayidno() + data.getPaycode(), datpayobj);
									}
								} else {
									intBpsnTpay = 2; // 旗標 1:基本檔個人+異動檔支扣 2:異動檔離職,沒支扣,不用列入
								}
							}
							// 41支扣
							else if (tracod.matches("41")) {
								trapayAry = (ArrayList) trapayMap.get(strIdtno);
								intBpsnTpay = 0; // 旗標 1:基本檔個人+異動檔支扣 2:異動檔離職,沒支扣,不用列入
								if (trapayAry != null) {
									tra41Map.put(strIdtno, trapayAry);
								}
							}
						}

						// 月支個人找不到且非離職,用基本檔個人 2不用讀基本檔
						// 2026.2.10or旗標=1基本檔個人+異動檔支扣 ,拿掉
						if ((datpsnobj == null && intBpsnTpay != 2)) {
							datpsnobj = new Padatpsn();
							datpsnobj = setDatPsnObj(null, baspsnobj, salnum, "", patradt, userid, hid);
							datpsnMap.put(strIdtno, datpsnobj);

							if (intBpsnTpay == 0) {
								// 月支明細無資料,用基本支扣
								baspayAry = (ArrayList) baspayMap.get(strIdtno);

								if (baspayAry != null) {
									for (Pabaspay data : baspayAry) {
										datpayobj = new Padatpay();
										datpayobj.setSalnum(salnum);
										datpayobj.setPayidno(data.getPayidno());
										datpayobj.setPaycode(data.getPaycode());
										datpayobj.setPayamt(data.getPayamt());
										datpayobj.setPayauth(data.getPayauth());
										datpayobj.setPayiftax(data.getPayiftax());
										datpayobj.setPayflag("");
										datpayobj.setHid(hid);
										datpayMap.put(data.getPayidno() + data.getPaycode(), datpayobj);
									}
								}
							}
						}
					}
					// 異動檔_新進,寫入 月支個人
					for (Map.Entry<String, Object> tpEntry : trapsnMap.entrySet()) {
						mIdtno = tpEntry.getKey();
						trapsnobj = (Patrapsn) tpEntry.getValue();
						tracod = String.valueOf(trapsnobj.getPatracod());
						if (tracod.matches("1|2")) {
							// 寫入 月支個人
							datpsnobj = new Padatpsn();
							datpsnobj = setDatPsnObj(trapsnobj, null, salnum, "N", patradt, userid, hid);
							datpsnMap.put(mIdtno, datpsnobj);
							// 月支支扣
							trapayAry = (ArrayList) trapayMap.get(mIdtno);
							if (trapayAry != null) {
								for (Patrapay data : trapayAry) {
									datpayobj = new Padatpay();
									datpayobj.setSalnum(salnum);
									datpayobj.setPayidno(data.getPayidno());
									datpayobj.setPaycode(data.getPaycode());
									datpayobj.setPayamt(data.getPayamt());
									datpayobj.setPayauth(data.getPayauth());
									datpayobj.setPayiftax(data.getPayiftax());
									datpayobj.setPayflag("");
									datpayobj.setHid(hid);
									datpayMap.put(data.getPayidno() + data.getPaycode(), datpayobj);
								}
							}
						}
					}
					log.info(me + "  4.寫入月支個人....");
					// 寫入月支個人
					starttime9 = System.currentTimeMillis();
					// trapayMap= this.getPaItmMap(param, "TRAPAY", hid,da);
					rtn = updDatTblMap(datpsnMap, null, da);
					log.info(me + "  4.寫入月支個人  共花費：" + (System.currentTimeMillis() - starttime9) + "毫秒");

					log.info(me + "  5.寫入月支支扣(異動41支扣->月支支扣)....");
					starttime9 = System.currentTimeMillis();
					// 寫入月支支扣.異動41支扣->月支支扣
					for (Map.Entry<String, ArrayList> entry : tra41Map.entrySet()) {
						mIdtno = entry.getKey();
						trapayAry = (ArrayList) entry.getValue();
						if (trapayAry != null) {
							for (Patrapay data : trapayAry) {
								datpayobj = datpayMap.get(data.getPayidno() + data.getPaycode());
								if (datpayobj != null) {
									datpayobj.setPayamt(data.getPayamt());
								} else {
									datpayobj = new Padatpay();
									datpayobj.setSalnum(salnum);
									datpayobj.setPayidno(data.getPayidno());
									datpayobj.setPaycode(data.getPaycode());
									datpayobj.setPayamt(data.getPayamt());
									datpayobj.setPayauth(data.getPayauth());
									datpayobj.setPayiftax(data.getPayiftax());
									datpayobj.setPayflag("");
									datpayobj.setHid(hid);
									datpayMap.put(data.getPayidno() + data.getPaycode(), datpayobj);
								}
							}
						}
					}
					log.info(me + "  5.寫入月支支扣(異動41支扣->月支支扣)  共花費：" + (System.currentTimeMillis() - starttime9) + "毫秒");

					log.info(me + "  6.寫入月支支扣(批次檔->月支支扣)....");
					starttime9 = System.currentTimeMillis();
					// batpay->寫入月支支扣
					// datpsnMap找不到,不用寫入
					for (Map.Entry<String, Object> entry : batpayMap.entrySet()) {
						if (entry.getKey().length() < 15) {
							log.info(me + "  6.寫入月支支扣(批次檔->月支支扣) key error...."+entry.getKey());
							rtn+="批次檔 key error...."+entry.getKey();
						}else {
							mIdtno = entry.getKey().substring(0, 10);
							strPaycode = entry.getKey().substring(10, 15);
							if (datpsnMap.get(mIdtno) == null) {
								log.info(me + "  batpay->寫入月支支扣,[月支個人找不到,不用寫入月支支扣]:" + mIdtno + " paycode:" + strPaycode);
							} else {
								Pabatpay data = (Pabatpay) entry.getValue();
	
								datpayobj = datpayMap.get(mIdtno + strPaycode);
								if (datpayobj != null) {
									datpayobj.setPayamt(data.getPayamt());
								} else {
									datpayobj = new Padatpay();
									datpayobj.setSalnum(salnum);
									datpayobj.setPayidno(data.getPayidno());
									datpayobj.setPaycode(data.getPaycode());
									datpayobj.setPayamt(data.getPayamt());
									datpayobj.setPayauth(data.getPayauth());
									datpayobj.setPayiftax(data.getPayiftax());
									datpayobj.setPayflag("");
									datpayobj.setHid(hid);
									datpayMap.put(data.getPayidno() + data.getPaycode(), datpayobj);
								}
							}
						}
					}
					log.info(me + "  7.寫入月支支扣  共花費：" + (System.currentTimeMillis() - starttime9) + "毫秒");
					rtn += updDatTblMap(null, datpayMap, da);

				}

				// 異動檔+批次檔
				else if (selEn.matches("Pay_2")) {
					log.info(me + "  1.異動檔,寫入 月支個人<==有異動支扣,才寫入....");
					// 異動檔,寫入 月支個人<==有異動支扣,才寫入
					// 1新進 2復職 11考績晉級 31晉級調薪 32改任換敘 33納實 34考績升等 35待遇調整 36晉級 37職務調整 38降級
					for (Map.Entry<String, Object> tpEntry : trapsnMap.entrySet()) {
						mIdtno = tpEntry.getKey();
						trapsnobj = (Patrapsn) tpEntry.getValue();
						tracod = String.valueOf(trapsnobj.getPatracod());
						if (trapsnobj != null) {
							tracod = String.valueOf(trapsnobj.getPatracod());
							// 月支支扣,不為空白
							trapayAry = (ArrayList) trapayMap.get(mIdtno);
							if (trapayAry != null) {
								// 月支個人
								datpsnobj = new Padatpsn();
								if (tracod.matches("1|2|11|31|32|33|34|35|36|37|38")) {
									datpsnobj = setDatPsnObj(trapsnobj, null, salnum, "N", patradt, userid, hid);
								} else if (tracod.matches("21|22|23|24|12|51|41|12|51")) {
									datpsnobj = setDatPsnObj(trapsnobj, null, salnum, "O", patradt, userid, hid);
								}
								datpsnMap.put(mIdtno, datpsnobj);
								// 月支支扣
								for (Patrapay data : trapayAry) {
									datpayobj = new Padatpay();
									datpayobj.setSalnum(salnum);
									datpayobj.setPayidno(data.getPayidno());
									datpayobj.setPaycode(data.getPaycode());
									datpayobj.setPayamt(data.getPayamt());
									datpayobj.setPayauth(data.getPayauth());
									datpayobj.setPayiftax(data.getPayiftax());
									datpayobj.setPayflag("");
									datpayobj.setHid(hid);
									datpayMap.put(data.getPayidno() + data.getPaycode(), datpayobj);
								}
							}
						}
					}
					log.info(me + "  2.批次檔,寫入 月支個人....");
					// 批次檔
					for (Map.Entry<String, Object> batentry : batpayMap.entrySet()) {
						mIdtno = batentry.getKey().substring(0, 10);
						strPaycode = batentry.getKey().substring(10, 15);
						Pabatpay data = (Pabatpay) batentry.getValue();
						// 找不到月支個人,要先加一筆基本個人
						datpsnobj = (Padatpsn) datpsnMap.get(mIdtno);
						if (datpsnobj == null) {
							baspsnobj = (Pabaspsn) baspsnAllMap.get(mIdtno);
							if (baspsnobj != null) {
								datpsnobj = new Padatpsn();
								datpsnobj = setDatPsnObj(null, baspsnobj, salnum, "", patradt, userid, hid);
								datpsnMap.put(mIdtno, datpsnobj);
							}
						}

						// 月支支扣
						datpayobj = datpayMap.get(mIdtno + strPaycode);
						if (datpayobj != null) {
							datpayobj.setPayamt(data.getPayamt());
						} else {
							datpayobj = new Padatpay();
							datpayobj.setSalnum(salnum);
							datpayobj.setPayidno(data.getPayidno());
							datpayobj.setPaycode(data.getPaycode());
							datpayobj.setPayamt(data.getPayamt());
							datpayobj.setPayauth(data.getPayauth());
							datpayobj.setPayiftax(data.getPayiftax());
							datpayobj.setPayflag("");
							datpayobj.setHid(hid);
							datpayMap.put(data.getPayidno() + data.getPaycode(), datpayobj);
						}
					}

					log.info(me + "  3.寫入月支個人....");
					// 寫入月支個人
					rtn = updDatTblMap(datpsnMap, null, da);

					log.info(me + "  4.寫入月支支扣....");
					rtn += updDatTblMap(null, datpayMap, da);

				}
				// 批次檔
				else if (selEn.matches("Pay_3")) {
					log.info(me + "  1.批次檔,寫入 月支個人....");
					for (Map.Entry<String, Object> entry : batpayMap.entrySet()) {
						mIdtno = entry.getKey().substring(0, 10);
						strPaycode = entry.getKey().substring(10, 15);
						Pabatpay data = (Pabatpay) entry.getValue();
						// 找不到月支個人,要先加一筆基本個人
						datpsnobj = (Padatpsn) datpsnMap.get(mIdtno);
						if (datpsnobj == null) {
							baspsnobj = (Pabaspsn) baspsnAllMap.get(mIdtno);
							if (baspsnobj != null) {
								datpsnobj = new Padatpsn();
								datpsnobj = setDatPsnObj(null, baspsnobj, salnum, "", patradt, userid, hid);
							}
						}
						if (datpsnobj != null) {
							datpsnMap.put(mIdtno, datpsnobj);
						}

						// 月支支扣
						datpayobj = datpayMap.get(mIdtno + strPaycode);
						if (datpayobj != null) {
							datpayobj.setPayamt(data.getPayamt());
						} else {
							datpayobj = new Padatpay();
							datpayobj.setSalnum(salnum);
							datpayobj.setPayidno(data.getPayidno());
							datpayobj.setPaycode(data.getPaycode());
							datpayobj.setPayamt(data.getPayamt());
							datpayobj.setPayauth(data.getPayauth());
							datpayobj.setPayiftax(data.getPayiftax());
							datpayobj.setPayflag("");
							datpayobj.setHid(hid);
							datpayMap.put(data.getPayidno() + data.getPaycode(), datpayobj);
						}
					}

					log.info(me + "  2.寫入月支個人....");
					starttime9 = System.currentTimeMillis();
					rtn = updDatTblMap(datpsnMap, null, da);
					log.info(me + "  2.寫入月支個人....共花費：" + (System.currentTimeMillis() - starttime9) + "毫秒");

					log.info(me + "  3.寫入月支支扣....");
					starttime9 = System.currentTimeMillis();
					rtn += updDatTblMap(null, datpayMap, da);
					log.info(me + "  3.寫入月支支扣....共花費：" + (System.currentTimeMillis() - starttime9) + "毫秒");
				}
				//
				log.info(me + "  9.變動所得稅處理....所得淨額>起扣點 map,列入入稅獎金*5%");
				starttime9 = System.currentTimeMillis();
				param = new ArrayList<Object>();
				param.add(salnum);
				param.add(hid);
				param.add(salnum);
				param.add(hid);
				param.add(salnum);
				param.add(hid);
				param.add(Globals.gSTaxAmt);
				// log.info(me+" " + sqlDatTax_inq+"...params:"+ param.toString());

				datTaxAry = da.arraylistbyQuery(sqlDatTax_inq, param, META_TYPE);
				write_cnt = 0;
				for (int i = 0; i < datTaxAry.size(); i++) {
					HashMap<String, Object> data = (HashMap<String, Object>) datTaxAry.get(i);

					pamt = (Integer) data.get("pamt");
					tamt = (Integer) data.get("tamt");
					mTaxVar = 0;
					if (tamt > 0) {
						mTaxVar = (int) Math.floor((tamt * Globals.gTaxTate));

						batch_sql.add(sqlDatPay_ins);
						param = new ArrayList<Object>();
						param.add(salnum);
						param.add(data.get("payidno"));
						param.add("VD030");
						param.add(mTaxVar);
						param.add("");
						param.add("N");
						param.add("");
						param.add(hid);
						// log.info(" param:"+param);
						params.add(param);

						if (batch_sql.size() > 0 || ++write_cnt % limitcnt == 0 || write_cnt == datTaxAry.size()) {
							// log.info(batch_sql+" "+batch_sql+" params:"+params);
							da.batchupdatenocommit(batch_sql, params);
							da.commit();

							batch_sql.clear();
							params.clear();
						}
					}
				}
				log.info(me + "變動所得稅處理.共花費：" + (System.currentTimeMillis() - starttime9) + "毫秒");

				log.info(me + "共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");

			}

		} catch (Exception e) {
			da.rollback();
			rtn += " fail, " + e;
			da.setAutoCommit(true);
			e.printStackTrace();
			log.info(me + " " + e.toString());
		} finally {
			closeAll(da, rs, connFlg);
		}
		return rtn;
	}

	// jsonFromStr:參數
	// 1.找異動檔新進,離職期限內map hid tradt
	// 2.找異動檔 hid salnum tracod1,tracod2
	private HashMap getTraPsnMap(String jsonStr, int opt, VGHDataAccess da) throws Exception {
		String me = ".getTraPsnMap()";
		HashMap rtnMap = new HashMap();
		Patrapsn oneobj = null; // 異動檔obj
		String strKey = "";
		long starttime = 0;

		params = new ArrayList<Object>();
		try {
			starttime = System.currentTimeMillis();
			JSONObject jsonFromStr = JSONObject.fromObject(jsonStr);

			if (opt == 1) {
				// or PATRACOD=33 or PATRACOD=37<==基本檔可以抓,不用這段
				sql = "select A.*,B.PAYTITLP paytitlo,c.PAYTITLP,d.TRANAME " + " from " + this.tTraPsn + " A "
						+ " left outer join " + this.tTitle + " B ON A.PAOTITLE=B.PAYTITLK and A.HID=B.HID "
						+ " left outer join " + this.tTitle + " C ON A.PAYTITLE=C.PAYTITLK and A.HID=C.HID "
						+ " left outer join " + this.tTraCode + " D ON A.patracod=d.TRACODE  "
						+ "where a.HID=? and ((PATRACOD BETWEEN 1 AND 10 ) OR ((PATRACOD BETWEEN 21 AND 30) AND PAEFFDT <= PATRADT)) "
						+ " and PATRADT = ? " + " with ur ";

				params.add(jsonFromStr.getString("hid"));
				params.add(jsonFromStr.getString("tradt"));
			} else if (opt == 2) {
				// 讀取pa.patrapsn 是否有新進,納實 初任公職日,職號,新制契約要改抓異動檔
				sql = "select PAYIDNO,PAYGOVDT,PAYEMPNO,PAYCARD,PAYTITLE,A.HID,PAYRANK,PAYRANK " + " from "
						+ this.tTraPsn + " A " + " left outer join " + this.tTitle
						+ " t on A.PAYTITLE = T.PAYTITLK and A.hid=t.hid "
						+ " where A.HID=? and A.SALNUM =? and a.patracod in (?,?) order by paycard with ur";
				params.add(jsonFromStr.getString("hid"));
				params.add(jsonFromStr.getInt("salnum"));
				params.add(jsonFromStr.getInt("tracod1"));
				params.add(jsonFromStr.getInt("tracod2"));
			}
			log.info(me + "  找異動檔_發薪日 " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					oneobj = new Patrapsn();
					PropertyUtils.copyProperties(oneobj, data);
					strKey = StringUtil.nullToString(oneobj.getPaycard());
					rtnMap.put(strKey, oneobj);
				}
			}
			log.info(me + "  找異動檔_發薪日 ,size: " + result.size() + " 共花費：" + (System.currentTimeMillis() - starttime)
					+ "毫秒");

		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new Exception(me + ", fail!!." + e);
		} finally {

		}
		return rtnMap;
	}

	// 找異動支扣_發薪日_支扣代碼(新)map
	private HashMap getTraPayMap(String hid, String tradt, String paycode, VGHDataAccess da) throws Exception {
		String me = ".getTraPayMap()";
		HashMap rtnMap = new HashMap();
		Patrapay oneobj = null; // 異動支扣obj
		String strKey = "";
		long starttime = 0;

		sql = "select a.TRANUM, a.PAYIDNO, a.PATRADT, PAYSORT, PAYCODE, PAYAMT, PAYAUTH, PAYIFTAX, a.PAYFLAG, a.HID,b.PAYCARD "
				+ " from " + this.tTraPay + " A " + " left outer join " + this.tTraPsn
				+ " B ON a.payidno=b.payidno AND a.hid=b.hid AND a.patradt=b.patradt "
				+ " where b.HID=? and b.PATRADT = ? and a.paycode=? and a.paysort=?" + " with ur ";
		try {

			starttime = System.currentTimeMillis();
			params = new ArrayList<Object>();
			params.add(hid);
			params.add(tradt);
			params.add(paycode);
			params.add("N");
			log.info(me + "  找異動支扣_發薪日_支扣代碼(新) " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					oneobj = new Patrapay();
					PropertyUtils.copyProperties(oneobj, data);
					strKey = StringUtil.nullToString(oneobj.getPaycard());
					rtnMap.put(strKey, oneobj);
				}
			}
			log.info(me + "  找找異動支扣_發薪日_支扣代碼(新) ,size: " + result.size() + " 共花費："
					+ (System.currentTimeMillis() - starttime) + "毫秒");

		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new Exception(me + ", fail!!." + e);
		} finally {

		}
		return rtnMap;
	}

	// 找基本檔個人map
	private HashMap getBasEmpCardMap(String hid, VGHDataAccess da) throws Exception {
		String me = ".getBasEmpCardMap()";
		HashMap rtnMap = new HashMap();
		Pabaspsn oneobj = null; // 基本檔個人obj
		String strKey = "";
		long starttime = 0;
		// 5年內離職+在職人員
		// 2026.3.25改寫法
		// 2026.3.27 payrsgdt=null判斷修改
		sql = "select PAYLVEFG, PAYIDNO, a.PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, PAYEMPDT, PAYSGNDT, COALESCE(a.payrsgdt,'9999-12-31') PAYRSGDT, PAYBKID, PAYACNO, PAYGEDNO, PAYINFLG, PAYDPNO, PAYPSW, PAYTITLE, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, COALESCE(PAYPFDOC, 0)PAYPFDOC, PACSFLAG, PAYPFSP1, PAYPFSP2, PAYPFSP3, PATRACOD, PATRADT, PAYTAXNO, PAYUPDID, PAYUPDTM, PAYEXPFKEY1, PAYEXPFKEY2, PAYEXPFKEY3, PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, INSUAMT, PARETENTION, a.HID,traname "
				+ " from " + this.tBasPsn + " A " + " INNER JOIN (SELECT hid,paycard,max(payrsgdt) payrsgdt FROM "
				+ this.tBasPsn
				+ " where (payrsgdt IS NULL OR payrsgdt>CURRENT date -5 YEARS) GROUP BY hid,paycard) C ON a.paycard=c.paycard AND COALESCE(a.payrsgdt,'9999-12-31')=COALESCE(c.payrsgdt,'9999-12-31') AND a.hid=c.hid "
				+ " left outer join " + this.tTraCode + " B ON A.patracod=B.TRACODE " + " where a.HID= ? with ur";
		try {

			starttime = System.currentTimeMillis();
			params = new ArrayList<Object>();
			params.add(hid);
			log.info(me + "  找基本檔個人 " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					oneobj = new Pabaspsn();
					PropertyUtils.copyProperties(oneobj, data);
					strKey = StringUtil.nullToString(oneobj.getPaycard());

					rtnMap.put(strKey, oneobj);
				}
			}
			log.info(
					me + "  找基本檔個人,size: " + result.size() + " 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new Exception(me + ", fail!!." + e);
		} finally {

		}
		return rtnMap;
	}

	// 找發薪明細時薪map
	private HashMap getPayrollHRidMap(String hid, String tradt, VGHDataAccess da) throws Exception {
		String me = ".getPayrollHRCardMap()";
		HashMap rtnMap = new HashMap();
		Padatpay oneobj = null;
		String strKey = "";
		long starttime = 0;
		String strPAYCODE = "";
		if (hid.equals("1A0"))
			strPAYCODE = "('FP000','FP010','FP030')";
		else if (hid.equals("2A0")) {
			// 2022.12.13
			// 本俸FP000+專業加給FP010+FP030主管加給+證照加給FP090+院區加給FP100+身心障礙加給FP110+羅致困難加給->特別津貼FP120
			// 2025.7.8 本薪增額FP020
			strPAYCODE = "('FP000','FP010','FP020','FP030','FP090','FP100','FP110','FP120')";
		} else if (hid.equals("3A0")) {
			// '2024.5.24 本俸FP000+專業加給FP010+FP030主管加給+責任加給FP090+職務加給FP100
			strPAYCODE = "('FP000','FP010','FP030','FP090','FP100')";
		}
		sql = "select a.payidno,sum(PAYAMT) PAYAMT  " + " from " + this.tDatPsn + " A " + " inner join " + this.tDatPay
				+ " B ON a.SALNUM = b.SALNUM and a.PAYIDNO = b.PAYIDNO and a.hid=b.hid AND PAYCODE IN " + strPAYCODE
				+ " where a.HID= ? AND PAYBANK = ? " + " group BY a.payidno " + " with ur";
		try {
			starttime = System.currentTimeMillis();
			params = new ArrayList<Object>();
			params.add(hid);
			params.add(tradt);
			log.info(me + "  找發薪明細時薪 " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					oneobj = new Padatpay();
					PropertyUtils.copyProperties(oneobj, data);
					strKey = StringUtil.nullToString(oneobj.getPayidno());

					rtnMap.put(strKey, oneobj);
				}
			}
			log.info(me + "  找發薪明細時薪,size: " + result.size() + " 共花費：" + (System.currentTimeMillis() - starttime)
					+ "毫秒");
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new Exception(me + ", fail!!." + e);
		} finally {

		}
		return rtnMap;
	}

	/**
	 * [批次檔--事病假] in_data: 傳出:HashMap
	 **/
	public HashMap<String, Object> getLeave(String in_data, String userid, String hid, VGHDataAccess da)
			throws Exception {
		String me = getClass().getName() + ".getLeave(),userid = " + userid;
		log.info(me + ",in_data = " + in_data + ",userid = " + userid + ",hid = " + hid);
		PsNTDAO psNTDAO = null;
		VGHDataAccess daPSNT = null;
		CommDAO cDAO = null;
		ArrayList<Map<String, Object>> leaveAry = null;
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_in_data = null;
		String rtn = "";
		String strOfftype = "", preOfftype = "";
		String strpos = "";
		long starttime = 0, starttimeM = 0;
		HashMap<String, Object> rtnMap = new HashMap();
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> paramA = new ArrayList<Object>();
		HashMap baspsnCardMap = new HashMap();
		HashMap trapsnCardMap = new HashMap();
		HashMap datpayIdMap = new HashMap();
		Pabaspsn baspsnobj = null; // 基本檔個人obj
		Patrapsn trapsnobj = null; // 異動檔obj
		Padatpay datpayobj = null; // 時薪obj
		String tradt = ""; // 發薪日期
		String datTradt = ""; // 找view年月的發薪明細.發薪日
		String strCard = "", preCard = ""; // 卡號
		String empname = ""; // 姓名
		String strYM = ""; // view.YM
		String strMM = "";
		String strIdno = "";
		String strRtn = "";
		int inttracod = 0;
		int intEffdt = 0; // 有效日數
		int tEffdt = 30; // 30日
		int tEffMaxdt = 0; // commonDao.getDayLimByPos 天數 全日=30
		double intDaySalay = 0; // 日薪
		double intHalfDS = 0; // 折半日薪
		int intMonSalay = 0; // 月薪
		BigDecimal vOffDay; // 假別小計
		BigDecimal vOffDaySum; // 假別小計(不含當月)
		BigDecimal vNoDay; // 不發給工資天數
		BigDecimal vHalfDay;// 折半天數

		double dblOffDay = 0; // 假別小計
		double dblOffBef = 0, preOffBef = 0; // 假別小計(不含當月) 扣除1月
		double dblNoDay = 0; // 不發給工資天數
		double dblHalfDay = 0; // 折半天數
		double dblLeaAmt = 0; // 事病假金額
		boolean blnFlag = true;
		boolean blnYM = true;
		String jsonStr = "";
		try {
			Hospitals hospInfo = Hospitals.getOneHosp(hid);
			if (da == null) {
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			object_in_data = JSONObject.fromObject(in_data);
			tradt = StringUtil.nullToString(object_in_data.getString("patradt"));
			cDAO = new CommDAO(da, hid);

			// 找基本檔個人 card
			baspsnCardMap = getBasEmpCardMap(hid, da);
			// 找異動檔_發薪日 card
			jsonStr = "{hid:" + hid + ", tradt:" + tradt + "}";
			trapsnCardMap = getTraPsnMap(jsonStr, 1, da);

			// 目前只有apPA01取得off0102v授權
			// Hospitals hospInfo1A0 = Hospitals.getOneHosp(hid);
			// 要讀Prod.PS.off0102v
			// String dataSourcePSNT=hospInfo1A0.dataSourceNTP;
			String dataSourcePSNT = hospInfo.dataSourceNT;
			daPSNT = new VGHDataAccess(dataSourcePSNT);

			psNTDAO = new PsNTDAO(daPSNT, hid);

			leaveAry = psNTDAO.getleaveByPS(daPSNT, hid);
			log.info(me + "... leaveAry size:" + leaveAry.size());
			if (leaveAry.size() > 0) {
				for (Map<String, Object> data : leaveAry) {
					jsonOne = new JSONObject();
					blnFlag = true;
					intDaySalay = 0;
					intHalfDS = 0;
					intMonSalay = 0;
					dblHalfDay = 0;
					dblNoDay = 0;
					dblOffBef = 0;
					strRtn = "";
					strYM = StringUtil.nullToString(data.get("ym"));
					strMM = strYM.substring(4, 6);
					jsonOne.put("ym", strYM);
					if (blnYM) {
						// strYM=20250901
						if (strYM.length() == 8) {
							datTradt = strYM.substring(0, 4) + "-" + strYM.substring(4, 6) + "-"
									+ strYM.substring(6, 8);
						}
						// 找時薪_KEY=身分證,傳入off0102v.年月
						datpayIdMap = getPayrollHRidMap(hid, datTradt, da);
						blnYM = false;
					}
					strCard = StringUtil.nullToString(data.get("userid"));
					jsonOne.put("paycard", strCard);
					empname = StringUtil.trimWideEmpty(StringUtil.nullToString(data.get("empname")));
					jsonOne.put("empname", empname);
					strpos = StringUtil.nullToString(data.get("pos"));
					jsonOne.put("pos", strpos);
					jsonOne.put("posnm", cDAO.getPosNm(strpos));
					strOfftype = StringUtil.nullToString(data.get("offtype"));
					jsonOne.put("offtype", strOfftype);
					jsonOne.put("offtypnm", cDAO.getOfftypNm(strOfftype));
					jsonOne.put("offhour", StringUtil.nullToString(data.get("offhour")));
					jsonOne.put("offhoursum", StringUtil.nullToString(data.get("offhoursum")));
					vOffDay = (BigDecimal) data.get("offday");
					jsonOne.put("offday", StringUtil.nullToString(vOffDay));
					vOffDaySum = (BigDecimal) data.get("offdaysum");
					jsonOne.put("offdaysum", StringUtil.nullToString(vOffDaySum));
					vNoDay = (BigDecimal) data.get("nopay");
					jsonOne.put("nopay", StringUtil.nullToString(vNoDay));
					vHalfDay = (BigDecimal) data.get("payhalf");
					jsonOne.put("payhalf", StringUtil.nullToString(vHalfDay));

					log.info("userid:" + strCard + " empname:" + empname + " offtype:" + strOfftype + " offday:"
							+ vOffDay + " OffDaySum:" + vOffDaySum + " nopay:" + vNoDay + " payhalf:" + vHalfDay);

					trapsnobj = (Patrapsn) trapsnCardMap.get(strCard);
					baspsnobj = (Pabaspsn) baspsnCardMap.get(strCard);
					// 基本檔,異動檔皆找不到
					if (trapsnobj == null && baspsnobj == null) {
						strRtn = "卡號[找不到]";
					} else {
						if (trapsnobj != null) { // 先找異動檔
							inttracod = trapsnobj.getPatracod();
							strIdno = StringUtil.nullToString(trapsnobj.getPayidno());
							if (inttracod >= 20 && inttracod < 30) {
								strRtn = "異動--" + trapsnobj.getTraname() + trapsnobj.getPaeffdt();
								blnFlag = false;
							}

						} // 找基本檔
						else if (baspsnobj != null) {
							inttracod = baspsnobj.getPatracod();
							strIdno = StringUtil.nullToString(baspsnobj.getPayidno());
							if (inttracod >= 20 && inttracod < 30) {
								strRtn = "基本--" + baspsnobj.getTraname();
								blnFlag = false;
							}
						}
						if (blnFlag) {
							datpayobj = (Padatpay) datpayIdMap.get(strIdno);

							if (datpayobj == null) {
								strRtn = "日薪=0";
							} else {

								intMonSalay = datpayobj.getPayamt();
								if (intMonSalay > 0) {
									intDaySalay = Math.floor(datpayobj.getPayamt() / tEffdt);
									intHalfDS = Math.floor(datpayobj.getPayamt() / (tEffdt * 2));
								}

								// 因view在1月時,發薪日3月,OFFDAYSUM的小計是前年度1-12月
								if (strMM.equals("01"))
									dblOffBef = 0;
								else
									dblOffBef = vOffDaySum.doubleValue();
								// 有效天數
								tEffMaxdt = cDAO.getDayLimByPos(strpos);
								// 病假+生理假>30天,不發給工資,半日用15天
								// 病假30天+生理假第4天起,不發給工資
								if (strOfftype.matches("02|35")) {
									if (preCard.equals(strCard) && preOfftype.matches("02")
											&& strOfftype.matches("35")) {
										// 生理假
										if ((vOffDaySum.doubleValue() + vOffDay.doubleValue()) > 3)
											dblOffDay = preOffBef + vOffDay.doubleValue() + dblOffBef - 3;
										else // 1-3天,半薪
											dblOffDay = vOffDay.doubleValue();
									} else {
										dblOffDay = 0 + vOffDay.doubleValue() + dblOffBef;
									}

									// 不含當月,已超過30天 例:不含當月26天,當月5.5天=>折半工資:4天,不發工資:1.5天
									if (dblOffDay > tEffMaxdt) {
										if (dblOffBef > tEffMaxdt) {
											dblNoDay = vOffDay.doubleValue();
										} else {
											dblNoDay = dblOffDay - tEffMaxdt;
											dblHalfDay = (dblOffDay - tEffMaxdt)
													- (vOffDaySum.doubleValue() + vOffDay.doubleValue());
										}
									} else {
										// view在1月,OFFDAYSUM的小計是前年度1-12月,可能有>30
										if (strMM.equals("01")) {
											dblHalfDay = vOffDay.doubleValue();
										} else {
											dblNoDay = vNoDay.doubleValue();
											dblHalfDay = vHalfDay.doubleValue();
										}
									}
								} else {
									dblNoDay = vNoDay.doubleValue();
									dblHalfDay = vHalfDay.doubleValue();
								}
							}
						}
					}
					jsonOne.put("patradt", tradt);
					jsonOne.put("payidno", strIdno);
					jsonOne.put("salnum", 0);
					jsonOne.put("payupbas", "N");
					jsonOne.put("paycode", "FP001");
					jsonOne.put("msalary", intMonSalay);
					jsonOne.put("dsalary", intDaySalay);
					jsonOne.put("hsalary", intHalfDS);
					jsonOne.put("rtn", strRtn);
					jsonOne.put("nopay", dblNoDay);
					jsonOne.put("halfpay", dblHalfDay);
					dblLeaAmt = Math.floor(intDaySalay * dblNoDay + intHalfDS * dblHalfDay);
					jsonOne.put("payamt", dblLeaAmt);
					jsonRtn.add(jsonOne);

					preCard = strCard;
					preOfftype = strOfftype;
					preOffBef = dblOffDay;
				}
			}
			rtnMap.put("leave_json", jsonRtn);
		} catch (Exception e) {
			rtn = " fail, " + e;
			e.printStackTrace();
			log.info(me + " " + e.toString());
		} finally {
			closeAll(da, null, connFlg);
		}

		return rtnMap;
	}

	/**
	 * [批次檔--教職及論文獎勵] in_data: 傳出:HashMap
	 **/
	public HashMap<String, Object> getTeacheraw(String in_data, String userid, String hid, VGHDataAccess da)
			throws Exception {
		String me = getClass().getName() + ".getTeacheraw(),userid = " + userid;
		log.info(me + ",in_data = " + in_data + ",userid = " + userid + ",hid = " + hid);
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_in_data = null;
		String tradt = ""; // 發薪日期
		long starttime = 0, starttime9 = 0;
		HashMap baspsnCardMap = new HashMap();
		HashMap trapsnCardMap = new HashMap();
		String jsonStr = "";
		ArrayList<Object> param = new ArrayList<Object>();
		HashMap<String, Object> rtnMap = new HashMap();
		HashMap baspayTeaMap = new HashMap();
		HashMap teachawMap = new HashMap();
		String l_Idtno = ""; // 身分證
		String l_paycode = ""; // 支扣代碼
		String m_paycode = ""; // 支扣代碼
		int l_payamt = 0; // 金額
		Pabaspsn baspsnobj = null; // 基本檔個人obj
		Pabaspay baspayobj = null; // 基本檔支扣obj
		Patrapsn trapsnobj = null; // 異動檔obj
		String rtn = "";
		int intRec = 0;
		try {
			Hospitals hospInfo = Hospitals.getOneHosp(hid);
			if (da == null) {
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			object_in_data = JSONObject.fromObject(in_data);
			tradt = StringUtil.nullToString(object_in_data.getString("patradt"));
			starttime = System.currentTimeMillis();
			// 找基本檔個人 card
			starttime9 = System.currentTimeMillis();
			baspsnCardMap = getBasEmpCardMap(hid, da);
			log.info(me + "  1-1.讀取基本檔個人,筆數:" + baspsnCardMap.size() + "共花費："
					+ (System.currentTimeMillis() - starttime9) + "毫秒");
			// 找異動檔_發薪日 card
			starttime9 = System.currentTimeMillis();
			jsonStr = "{hid:" + hid + ", tradt:" + tradt + "}";
			trapsnCardMap = getTraPsnMap(jsonStr, 1, da);
			log.info(me + "  1-2.讀取異動檔_發薪日,筆數:" + trapsnCardMap.size() + "共花費："
					+ (System.currentTimeMillis() - starttime9) + "毫秒");
			// 讀取基本支扣(FP420,FP430) 回傳 ID+支扣碼
			starttime9 = System.currentTimeMillis();
			param = new ArrayList<Object>();
			param.add(hid);
			baspayTeaMap = this.getPaItmMap(param, "TEACHAWBas", hid, da);
			log.info(me + "  1-3.讀取基本支扣(FP420,FP430),筆數:" + baspayTeaMap.size() + "共花費："
					+ (System.currentTimeMillis() - starttime9) + "毫秒");

			// 讀取教職,論文獎勵(pateachaw) 回傳 ID+支扣碼
			starttime9 = System.currentTimeMillis();
			param = new ArrayList<Object>();
			param.add(hid);
			param.add(tradt);
			param.add(hid);
			param.add(tradt);
			teachawMap = this.getPaItmMap(param, "TEACHAW", hid, da);
			log.info(me + "  1-4.讀取教職,論文獎勵(pateachaw),筆數:" + teachawMap.size() + "共花費："
					+ (System.currentTimeMillis() - starttime9) + "毫秒");

			// 因論文有2篇要先加總 第1篇補發及第2篇補發要加總
			sql = "select PAYIDNO,PAYCARD,PAYNAMEC, 'FP430' AS PAYCODE, SUM(PAYAMT) AS PAYAMT " + " from "
					+ this.tTeaChaw + " WHERE hid=? and ? BETWEEN PABDATE AND PAVDATE AND PAYITEM IN ('10', '20') "
					+ " GROUP BY PAYIDNO,PAYCARD,PAYNAMEC " + " union "
					+ " SELECT PAYIDNO,PAYCARD,PAYNAMEC, 'FP439' as PAYCODE, SUM(PAYAMT) AS PAYAMT " + " from "
					+ this.tTeaChaw + " WHERE hid=? and ? BETWEEN PABDATE AND PAVDATE AND PAYITEM IN ('19', '29') "
					+ " GROUP BY PAYIDNO,PAYCARD,PAYNAMEC " + " union "
					+ " SELECT PAYIDNO,PAYCARD,PAYNAMEC, 'FP420' PAYCODE, SUM(PAYAMT) AS PAYAMT " + " from "
					+ this.tTeaChaw + " WHERE hid=? and ? BETWEEN PABDATE AND PAVDATE AND PAYITEM IN ('00') "
					+ " GROUP BY PAYIDNO,PAYCARD,PAYNAMEC " + " union "
					+ " SELECT PAYIDNO,PAYCARD,PAYNAMEC, 'FP429' PAYCODE, SUM(PAYAMT) AS PAYAMT " + " from "
					+ this.tTeaChaw + " WHERE hid=? and ? BETWEEN PABDATE AND PAVDATE AND PAYITEM IN ('09') "
					+ " GROUP BY PAYIDNO,PAYCARD,PAYNAMEC " + " ORDER BY PAYIDNO,PAYCODE  WITH ur ";
			params = new ArrayList<Object>();
			params.add(hid);
			params.add(tradt);
			params.add(hid);
			params.add(tradt);
			params.add(hid);
			params.add(tradt);
			params.add(hid);
			params.add(tradt);
			log.info(me + "  2-1.讀取pa.pateachaw:" + sql + "...params:" + params.toString());
			log.info(me + "  " + sql + "...params:" + params.toString());
			// 先以教職論文找基本支扣
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());
			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					jsonOne = new JSONObject();
					rtn = "";
					l_Idtno = StringUtil.nullToString(data.get("payidno"));
					l_paycode = StringUtil.nullToString(data.get("paycode"));
					l_payamt = (Integer) data.get("payamt");
					jsonOne.put("patradt", tradt);
					jsonOne.put("salnum", 0);
					jsonOne.put("payupbas", "Y");
					jsonOne.put("payidno", l_Idtno);
					jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));
					jsonOne.put("paynamec", StringUtil.nullToString(data.get("paynamec")));
					jsonOne.put("paycode", l_paycode);
					jsonOne.put("payamt", l_payamt);
					// FP420獎勵--教職獎勵 FP430獎勵--論文獎勵
					baspayobj = (Pabaspay) baspayTeaMap.get(l_Idtno + l_paycode);
					if (baspayobj != null) {
						if (baspayobj.getPayamt() != l_payamt) {
							rtn = "金額不符,基本支扣:" + baspayobj.getPayamt();
						}
					} else {
						rtn = "基本支扣金額=0";
					}

					jsonOne.put("rtn", rtn);
					jsonRtn.add(jsonOne);
				}
			}
			log.info(me + "[先以教職論文找基本支扣],筆數:" + result.size() + "共花費：" + (System.currentTimeMillis() - starttime)
					+ "毫秒");

			// 以基本支扣為主,找PATEACHAW,若找不到,表示已被刪除或期限已到,PABATPAY設為0
			sql = "select A.PAYIDNO, A.PAYCARD,PAYNAMEC, PAYCODE, PAYAMT " + " from " + this.tBasPsn + " a "
					+ " inner JOIN " + this.tBasPay
					+ " B ON A.PAYIDNO = B.PAYIDNO and a.hid=b.hid AND PAYCODE IN ('FP420','FP430') "
					+ "WHERE (PAYRSGDT IS NULL or PAYRSGDT='9999-12-31')  and a.HID= ? WITH ur";
			params = new ArrayList<Object>();
			params.add(hid);
			log.info(me + "  2-2.讀取pa.pabaspay(FP420+FP430):" + sql + "...params:" + params.toString());
			log.info(me + "  " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());
			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					jsonOne = new JSONObject();
					rtn = "";
					l_Idtno = StringUtil.nullToString(data.get("payidno"));
					l_paycode = StringUtil.nullToString(data.get("paycode"));
					l_payamt = (Integer) data.get("payamt");
					// FP420獎勵--教職獎勵 FP430獎勵--論文獎勵
					baspayobj = (Pabaspay) teachawMap.get(l_Idtno + l_paycode);
					if (baspayobj == null) {
						jsonOne.put("patradt", tradt);
						jsonOne.put("salnum", 0);
						jsonOne.put("payupbas", "Y");
						jsonOne.put("payidno", l_Idtno);
						jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));
						jsonOne.put("paynamec", StringUtil.nullToString(data.get("paynamec")));
						jsonOne.put("paycode", l_paycode);
						jsonOne.put("payamt", 0);
						jsonOne.put("rtn", "期限已到,基本支扣=" + l_payamt);
						jsonRtn.add(jsonOne);
						intRec++;
					}
				}
			}
			log.info(me + "[找期限已到期],筆數:" + intRec + "共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");

			rtnMap.put("teacheraw_json", jsonRtn);
		} catch (Exception e) {
			e.printStackTrace();
			log.info(me + " " + e.toString());
		} finally {
			closeAll(da, null, connFlg);
		}

		return rtnMap;
	}

	/**
	 * [批次檔] 1:[匯入]批次檔:ImpPayInBat selCode:selNm:selEn
	 * in_data:(2:[刪除]批次檔:DelPayInBat writeBat:事假病匯入批次檔 writeBatByTeach:教職論文獎勵
	 * tabJson(tblname,value) 傳出:rtn文字訊息
	 **/
	public String prodBat(String in_data, String tabJson, String userid, String hid, VGHDataAccess da)
			throws Exception {
		String me = getClass().getName() + ".prodBat(),userid = " + userid;
		log.info(me + ",in_data = " + in_data + ",tabJson = " + tabJson + ",userid = " + userid + ",hid = " + hid);
		String rtn = "";
		String selEn = ""; // 處理項目
		String selNm = ""; // 處理項目 中文
		String patradt = ""; // 發薪日期
		String paycard = ""; // 卡號
		String paycode = ""; // 支扣代碼
		String payupbas = ""; // Y需更新至主檔 N每月歸零
		int payamt = 0; // 金額
		int salnum = 0; // 餉單編號
		int updCnt = 0;
		String payidno = "";
		String formnm = "";
		JSONArray array = null;
		long starttime = 0, starttimeW = 0;
		int delCnt = 0;
		ArrayList<Object> paramA = new ArrayList<Object>();
		ArrayList<Object> batch_sql = new ArrayList<Object>(); // batch_update sql 資料
		String sqlBatPay_del = "delete from " + this.tBatPay + " where HID=? and PATRADT =? and PAYCODE=? with ur";
		try {
			Hospitals hospInfo = Hospitals.getOneHosp(hid);
			if (da == null) {
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			JSONObject object_in_data = JSONObject.fromObject(in_data);
			selEn = StringUtil.nullToString((!object_in_data.has("selEn")) ? "" : object_in_data.getString("selEn"));
			selNm = StringUtil.nullToString((!object_in_data.has("selNm")) ? "" : object_in_data.getString("selNm"));
			patradt = StringUtil
					.nullToString((!object_in_data.has("patradt")) ? "" : object_in_data.getString("patradt"));
			paycode = StringUtil
					.nullToString((!object_in_data.has("paycode")) ? "" : object_in_data.getString("paycode"));
			// 2:[刪除]批次檔:DelPayInBat
			if ("DelPayInBat".equals(selEn)) {
				starttime = System.currentTimeMillis();
				paramA.clear();
				paramA.add(hid);
				paramA.add(patradt);
				paramA.add(paycode);

				params.add(paramA);
				batch_sql.add(sqlBatPay_del);

				da.startTransaction();
				try {
					log.info(me + "  " + selNm + ">>" + sqlBatPay_del + "...params:" + paramA.toString());
					delCnt = da.update(sqlBatPay_del, paramA);
					da.commit();
				} catch (Exception e) {
					da.rollback();
					rtn = " fail, " + e;
					da.setAutoCommit(true);
					e.printStackTrace();
					log.info(me + " " + selNm + " " + e.toString());
				} finally {

				}
				rtn = "刪除筆數:" + delCnt + " ********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒";
				log.info(me + "  " + selNm + " delCnt:" + delCnt + " 共花費：" + (System.currentTimeMillis() - starttime)
						+ "毫秒");
			}
			// 事病假寫入批次檔,前端先用卡號排序,若有相同卡號,要加總寫入
			else if ("writeBatByLeav".equals(selEn)) {
				starttime = System.currentTimeMillis();

				JSONArray tabJsonary = JSONArray.fromObject(tabJson);
				for (int i = 0; i < tabJsonary.size(); ++i) {
					JSONObject obj = tabJsonary.getJSONObject(i);
					if (obj != null && obj.has("formnm")) {
						formnm = StringUtil.nullToString(obj.getString("formnm"));
						if ("PABATPAY".equals(formnm)) {
							String sqlBatPay_ins = " insert into " + this.tBatPay
									+ " (PAYCARD, PAUPBAS, PAYAMT, PADUTYID, PAYUPDAT, PAYFLAG, UPMASTDT, ZPABATPAY, PATRADT, PAYIDNO, SALNUM, PAYCODE, HID)"
									+ " values( ?,?,?,?,current timestamp,?, ?,current timestamp,?,?,?,?,?) ";
							array = obj.getJSONArray("value");
							if (null != array && array.size() > 0) {
								for (int j = 0; j < array.size(); j++) {
									JSONObject obj_item = array.getJSONObject(j);

									Set<Map.Entry> entries = obj_item.entrySet();
									for (Map.Entry entry : entries) {
										JSONObject value = (JSONObject) entry.getValue();
										// 金額<>0,寫入
										if (value.getInt("payamt") != 0) {
											if (!paycard.equals("") && !paycard.equals(value.getString("paycard"))) {
												paramA = new ArrayList<Object>();
												paramA.add(paycard);
												paramA.add(payupbas);
												paramA.add(-1 * payamt);
												paramA.add("O" + userid);
												paramA.add("");
												paramA.add("");
												paramA.add(patradt);
												paramA.add(payidno);
												paramA.add(salnum);
												paramA.add(paycode);
												paramA.add(hid);
												updCnt++;
												payamt = 0;
												params.add(paramA);
												batch_sql.add(sqlBatPay_ins);
											}

											patradt = !value.has("patradt") ? "" : value.getString("patradt");
											paycard = !value.has("paycard") ? "" : value.getString("paycard");
											payupbas = !value.has("payupbas") ? "" : value.getString("payupbas");
											payamt = payamt + (!value.has("payamt") ? 0 : value.getInt("payamt"));
											payidno = !value.has("payidno") ? "" : value.getString("payidno");
											salnum = !value.has("salnum") ? 0 : value.getInt("salnum");
											paycode = !value.has("paycode") ? "" : value.getString("paycode");

										}
									}
									// 最末一筆
									paramA = new ArrayList<Object>();
									paramA.add(paycard);
									paramA.add(payupbas);
									paramA.add(-1 * payamt);
									paramA.add("O" + userid);
									paramA.add("");
									paramA.add("");
									paramA.add(patradt);
									paramA.add(payidno);
									paramA.add(salnum);
									paramA.add(paycode);
									paramA.add(hid);
									updCnt++;
									params.add(paramA);
									batch_sql.add(sqlBatPay_ins);
								}
							}
						}
					}
				}
				log.info(me + "sql:" + batch_sql + " ...params:" + params.toString());
				if (batch_sql.size() > 0) {
					da.startTransaction();
					try {
						da.batchupdatenocommit(batch_sql, params);
						da.commit();
						rtn = "事病假寫入批次檔:" + updCnt + " ********* 共花費：" + (System.currentTimeMillis() - starttime)
								+ "毫秒";
						log.info(me + "  " + selNm + " Cnt:" + updCnt + " 共花費："
								+ (System.currentTimeMillis() - starttime) + "毫秒");
					} catch (Exception e) {
						da.rollback();
						rtn = " fail, " + e;
						da.setAutoCommit(true);
						e.printStackTrace();
						log.info(me + " " + formnm + " " + e.toString());
					} finally {
						closeAll(da, null, connFlg);
					}
				}

			}
		} catch (Exception e) {
			rtn = " fail, " + e;
			e.printStackTrace();
			log.info(me + " " + e.toString());
		} finally {
			closeAll(da, null, connFlg);
		}

		return rtn;
	}

	/**
	 * [補充保費]計算/匯出 1:補充費_[計算寫入](餉單編單):Cal_1 selCode:selNm:selEn
	 * in_data:(1:補充費_[計算寫入](餉單編單):Cal_1 2:補充費_[刪除](餉單編單):Cal_2
	 * 3:補充費_[產生]至批次檔(餉單編號):Cal_3 4:補充費_[刪除]批次檔(餉單編號):Det_1
	 * 5:申報扣費明細_[產生](給付日期):Det_1 6:申報扣費明細_[刪除](給付日期):Det_2
	 * 7:申報扣費明細_[產生]媒體申報檔(給付日期):Det_3 改成WEB版,先用給付日期,待後期再用製票號 8:所得稅_[寫入]轉帳項目:35 57 58
	 * (給付日期):Tax_1 9:所得稅_[寫入]轉帳項目:59(給付日期):Tax_2 10:所得稅_[刪除](給付日期):Tax_3 傳出:rtn文字訊息
	 **/
	public String prodNhi(String in_data, String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".prodNhi(),userid = " + userid;
		log.info(me + ",in_data = " + in_data + ",userid = " + userid + ",hid = " + hid);
		ArrayList<Object> paramA = new ArrayList<Object>();
		ArrayList<Object> batch_sql = new ArrayList<Object>(); // batch_update sql 資料
		ResultSet rs = null;
		String rtn = "";
		long starttime = 0, starttimeW = 0;
		String selEn = ""; // 處理項目
		String selNm = ""; // 處理項目 中文
		int salnum = 0; // 餉單編號
		String paydt = ""; // 給付日期
		String paybank = ""; // 發薪日期
		String payno = ""; // 製票號
		String lognm; // log filename
		String filePath = "";
		StringBuilder sb = new StringBuilder();
		BufferedWriter bfw = null;
		Writer output = null;
		String outputstring = "";
		String strLine = "";
		String strIdno, strPaycard = "", strPAYNAMEC = "", strRankAbbr = "", strTracod = "", strPAYRANK = "",
				strPAYTITLE = "", strPAYITEM = "", strPAYTITLP = "", strPAPAYDT = "";
		String strUnitId = "";
		String strPaycode = "VD070";// 補充保費
		boolean blnMONINSU = true;// 是否有月投保金額,沒有月投保金額,不用處理,但兼職人員可
		Boolean blnPart;
		int intINSUAMT = 0, intINSUIDENT = 0, intPayAmt = 0, intNHIAMT = 0, intJACKPOT = 0, intMTINSU = 0,
				intSUPLYBASE = 0, intMONINSU = 0, intITEMNUM = 0;
		int intcnt = 0, docnt = 0, write_cnt = 0, delCnt = 0, limitcnt = 100, paydt_yy = 0, paydt_mm = 0;
		Patrapsn trapsnobj = null; // 異動檔個人obj
		Panhidet nhidetobj = null; // 二代健保申報扣費明細 obj
		HashMap trapsnMap = new HashMap();
		HashMap datpay63Map = new HashMap(); // 兼職
		HashMap datpay62CMap = new HashMap();
		HashMap datpay62LMap = new HashMap();
		HashMap jackpotMap = new HashMap();
		// 擷取發薪明細 insuamt,若發薪明細空值,抓基本檔
		String sqlDatPsn = "select PAYBANK,SALNUM,A.PAYIDNO,A.PAYCARD,A.PAYNAMEC,A.PAYTITLE,A.PAYRANK,CASE WHEN COALESCE(a.INSUAMT,0) = 0 THEN b.insuamt ELSE COALESCE(a.INSUAMT,0) end INSUAMT,RANKABBR,PAYTITLP "
				+ " from " + this.tDatPsn + " A " + " left outer join " + this.tBasPsn
				+ " B ON A.PAYIDNO=B.PAYIDNO AND A.HID=B.HID " + " left outer join " + this.tRankCode
				+ " C ON A.PAYRANK=C.RANKCODE " + " left outer join " + this.tTitle + " D ON A.PAYTITLE=D.PAYTITLK "
				+ " where A.HID=? and salnum=? order by A.PAYCARD with ur";
		// 依餉單編號,擷取異動檔,列(1新進 2復職 11考績晉級 33納實 34考績升等 31晉級調薪 36晉級 37職務調整 38降級)
		String sqlTraPsn = "select PATRACOD, PATRADT, PAEFFDT, SALNUM, PAYLVEFG, PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, PAYEMPDT, PAYSGNDT, PAYRSGDT, PAYBKID, PAYACNO, PAYGEDNO, PAYINFLG, PAYDPNO, PAYPFLAG, PAYTITLE, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, PAYPFDOC, PACSFLAG, PAYPFSP1, PAYPFSP2, PAYPFSP3, PAYMONNO, PAYDAYNO, PAOTITLE, PAORNK2N, PAORANK3, PAODEGR2, PAODEGRE, PAORANK, PAORANK2, PAORANKC, PAOPFDOC, PAOCSFLA, PAOPFSP1, PAOPFSP2, PAOPFSP3, PAYTAXNO, PAYUPDID, PAYUPDTM, PAYFLAG, TRANUM, UPMASTDT, PAYPSW, PAOEXPFKEY1, PAOEXPFKEY2, PAOEXPFKEY3, PAYEXPFKEY1, PAYEXPFKEY2, PAYEXPFKEY3, PAORANK4, PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, INSUAMT, HID "
				+ " from " + this.tTraPsn
				+ " where PATRACOD IN ( 1, 2, 31, 33, 36, 37, 38, 11, 34) and HID=? and salnum=? with ur";
		// 依餉單編號,擷取發薪支扣--兼職及進修醫師,PAYAUTH第3碼=Y
		String sqlDatPay_63 = "select PAYIDNO,coalesce(SUM(PAYAMT),0) AS PAYAMT " + " from " + this.tDatPay + " A "
				+ " left outer join " + this.tPayItm + " B on A.PAYCODE=B.PAYCODE AND A.HID=B.HID "
				+ " where  A.HID=? and SALNUM=? and PACODDEL='N' AND SUBSTR(B.PAYAUTH,3,1)='Y' GROUP BY payidno WITH UR";
		// 依餉單編號,擷取發薪支扣--公保身份1 2 3 4 8 9,駐警 7 PAYAUTH第1碼=Y
		String sqlDatPay_62_civil = "select PAYIDNO,coalesce(SUM(PAYAMT),0) AS PAYAMT " + " from " + this.tDatPay
				+ " A " + " left outer join " + this.tPayItm + " B on A.PAYCODE=B.PAYCODE AND A.HID=B.HID "
				+ " where  A.HID=? and SALNUM=? and PACODDEL='N' AND SUBSTR(B.PAYAUTH,1,1)='Y' GROUP BY payidno WITH UR";
		// 依餉單編號,擷取發薪支扣--勞保身份5 6 A B E F,PAYAUTH第2碼=Y
		String sqlDatPay_62_labor = "select PAYIDNO,coalesce(SUM(PAYAMT),0) AS PAYAMT " + " from " + this.tDatPay
				+ " A " + " left outer join " + this.tPayItm + " B on A.PAYCODE=B.PAYCODE AND A.HID=B.HID "
				+ " where  A.HID=? and SALNUM=? and PACODDEL='N' AND SUBSTR(B.PAYAUTH,2,1)='Y' GROUP BY payidno WITH UR";
		// 找累積獎金
		// test 沒法匯入全部data,先改sql
		String sqlNhiJackpot = "select PAYIDNO,coalesce(SUM(BONUSAMT),0) AS BONUSAMT " + " from " + this.tNhiCal
				+ " where HID=? and YEAR(PAPAYDT)=? GROUP BY payidno WITH UR";
		/*
		 * String sqlNhiJackpot="select a.payidno,a.JACKPOT BONUSAMT "+ " from "+
		 * this.tNhiCal+ " A "+
		 * " inner join (SELECT payidno,max(JACKPOT) JACKPOT FROM "+ this.tNhiCal+
		 * " WHERE HID=? and YEAR(PAPAYDT)=?  GROUP BY payidno) b ON a.payidno=b.payidno AND a.JACKPOT=b.JACKPOT "
		 * + " where a.HID=? and YEAR(PAPAYDT)=? AND a.JACKPOT>0 WITH UR" ;
		 */
		// 補充費_inq
		String sqlNhiCal_inq = "select count(*) cnt  from " + this.tNhiCal + " where HID=? and ITEMNUM =? with ur";
		// 補充費_inq(人工入稅,發薪日)
		String sqlNhiCalTax_inq = "select count(*) cnt  from " + this.tNhiCal
				+ " where HID=? and PAYSOURCE ='TAX' and year(PAPAYDT)=? and month(PAPAYDT)=? with ur";
		// 補充費_新增
		String sqlNhiCal_ins = "insert into " + this.tNhiCal
				+ "(PAYITEM, PAYCARD, PAYNAMEC, MONINSU, MONINSU4, BONUSAMT, JACKPOT, MTINSU, SUPLYBASE, NHIAMT, INSUIDENT, PAYFLAG, PAYUPDID, PAYUPDAT, PAPAYDT, PAYSOURCE, ITEMNUM, PAYIDNO, HID) "
				+ "values(?,?,?,?,?, ?,?,?,?,?, ?,?,?,current timestamp,?, ?,?,?,? );";
		// 補充費_刪除
		String sqlNhiCal_del = "delete from " + this.tNhiCal
				+ " where HID=? and ITEMNUM =? and PAYSOURCE='DAT' with ur";
		// 補充費_刪除(人工入稅,發薪日)
		String sqlNhiCalTax_del = "delete from " + this.tNhiCal
				+ " where HID=? and PAPAYDT =? and PAYSOURCE='TAX' with ur";
		// 批次檔_inq
		String sqlBatPay_inq = "select count(*) cnt " + " from " + this.tBatPay
				+ " where HID=? and SALNUM=? and PAYCODE=? with ur";
		// 補充費寫入批次檔_HID,itemnum NHIAMT>0
		String sqlNhiCal_bat = "select PAPAYDT,ITEMNUM,PAYIDNO,PAYCARD,NHIAMT,PAYNAMEC " + " from " + this.tNhiCal
				+ " where HID=? and ITEMNUM =? and NHIAMT>0 with ur";
		// 批次檔_新增
		String sqlBatPay_ins = "insert into " + this.tBatPay
				+ "(PATRADT, PAYIDNO, SALNUM, PAYCARD, PAYCODE, PAUPBAS, PAYAMT, PADUTYID, PAYUPDAT, PAYFLAG, UPMASTDT, HID) "
				+ "values(?,?,?,?,?, ?,?,?,current timestamp,?, ?,?);";
		// 批次檔_刪除
		String sqlBatPay_del = "delete from " + this.tBatPay + " where HID=? and SALNUM =? and PAYCODE=? with ur";
		// 申報明細_inq
		String sqlNhiDet_inq = "select count(*) cnt  from " + this.tNhiDet + " where HID=? and PAPAYDT =? with ur";
		// 補充費寫入申報檔_HID,PAPAYDT NHIAMT>0身份=無,補充費>0,才寫入
		String sqlNhiCal_Det = "select PAPAYDT,PAYITEM,ITEMNUM,PAYIDNO,PAYCARD,NHIAMT,PAYNAMEC,BONUSAMT,MONINSU,JACKPOT,INSUIDENT "
				+ " from " + this.tNhiCal
				+ " where HID=? and PAPAYDT =? and ( (INSUIDENT =0 AND nhiamt>0) OR INSUIDENT <>0 ) order by PAYITEM,PAYCARD,ITEMNUM DESC,PAYSOURCE with ur";
		// 申報檔_新增
		String sqlNhiDet_ins = "insert into " + this.tNhiDet
				+ "(PAPAYDT, PAYIDNO, PAYITEM, PAYCARD, PAYNAMEC, SERIAL, PAYAMT, NHIAMT, MONINSU, JACKPOT, INSUIDENT, PAYFLAG, PAYUPDID, PAYUPDAT, HID) "
				+ "values(?,?,?,?,?, ?,?,?,?,?, ?,?,?,current timestamp,?);";
		// 申報檔_刪除
		String sqlNhiDet_del = "delete from " + this.tNhiDet + " where HID=? and PAPAYDT =?  with ur";
		// 申報檔--媒體申報檔
		String sqlNhiDet_Media = "select PAPAYDT,PAYIDNO,PAYITEM,PAYCARD,PAYNAMEC,SERIAL,PAYAMT,NHIAMT,MONINSU,JACKPOT,INSUIDENT "
				+ " from " + this.tNhiDet + " where HID=? and PAPAYDT =? order by PAYITEM,PAYCARD,SERIAL with ur";
		// 申報檔--地址檔
		String sqlNhiDet_Adr = "select A.PAYIDNO,PAYNAMEC,PAYADDRS " + " from " + this.tNhiDet + " A "
				+ " left outer join " + this.tBasAdr + " B on A.PAYIDNO=B.PAYIDNO "
				+ " where HID=? and PAPAYDT =? AND A.PAYIDNO NOT IN (SELECT PAYIDNO FROM " + this.tNhiDet
				+ " WHERE HID=? and PAPAYDT<? ) order by A.PAYIDNO with ur";
		// 現金所得稅(35 57 58)_inq FETCH FIRST 10 ROWS ONLY
		String sqlTaxCash35_inq = "select PAYEAR,PAYMONTH,PAYTAXPAY,A.PAYIDNO,B.PAYCARD,B.PAYNAMEC,B.PAYTITLE,d.paytitlp,B.PAYRANK,INSUAMT,RANKABBR "
				+ " from (SELECT PAYEAR,PAYMONTH,sum(PAYTAXPAY) PAYTAXPAY,PAYIDNO,HID " + " from " + this.tTaxCash
				+ " WHERE HID=? and PAYEAR =? and PAYMONTH=? and PAYCATG IN (?,?,?) GROUP BY payidno,PAYEAR,PAYMONTH,HID ) A "
				+ "LEFT OUTER JOIN " + this.tBasPsn + " B ON A.PAYIDNO=B.PAYIDNO and a.HID=B.HID " + "LEFT OUTER JOIN "
				+ this.tRankCode + " C ON B.PAYRANK=C.RANKCODE " + "LEFT OUTER JOIN " + this.tTitle
				+ " D ON B.PAYTITLE=d.paytitlk and b.hid=d.hid " + " order by A.PAYIDNO WITH UR";
		// 現金所得稅(59)_inq
		String sqlTaxCash59_inq = "select PAYEAR,PAYMONTH,PAYTAXPAY,A.PAYIDNO,B.PAYCARD,B.PAYNAMEC,B.PAYTITLE,d.paytitlp,B.PAYRANK,INSUAMT,RANKABBR "
				+ " from (SELECT PAYEAR,PAYMONTH,sum(PAYTAXPAY) PAYTAXPAY,PAYIDNO,HID " + " from " + this.tTaxCash
				+ " WHERE HID=? and PAYEAR =? and PAYMONTH=? and PAYCATG IN (?) GROUP BY payidno,PAYEAR,PAYMONTH,HID) A "
				+ "LEFT OUTER JOIN " + this.tBasPsn + " B ON A.PAYIDNO=B.PAYIDNO and a.HID=B.HID " + "LEFT OUTER JOIN "
				+ this.tRankCode + " C ON B.PAYRANK=C.RANKCODE " + "LEFT OUTER JOIN " + this.tTitle
				+ " D ON B.PAYTITLE=d.paytitlk and b.hid=d.hid " + "order by A.PAYIDNO WITH UR";
		// 發薪明細.投保金額
		String sqlDatPsn_insuamt = "SELECT insuamt " + " FROM " + this.tDatPsn
				+ " WHERE hid=? and payidno=? AND insuamt>0 ORDER BY salnum DESC FETCH FIRST 1 ROWS ONLY wITH ur";
		try {
			Hospitals hospInfo = Hospitals.getOneHosp(hid);
			if (da == null) {
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			JSONObject object_in_data = JSONObject.fromObject(in_data);
			selEn = StringUtil.nullToString(object_in_data.getString("selEn"));
			selNm = StringUtil.nullToString(object_in_data.getString("selNm"));
			salnum = (!object_in_data.has("salnum")) ? 0
					: ("").equals(StringUtil.nullToString(object_in_data.getString("salnum"))) ? 0
							: object_in_data.getInt("salnum");
			paybank = (!object_in_data.has("paybank")) ? ""
					: ("").equals(StringUtil.nullToString(object_in_data.getString("paybank"))) ? ""
							: object_in_data.getString("paybank");
			paydt = (!object_in_data.has("paydt")) ? "" : StringUtil.nullToString(object_in_data.getString("paydt"));
			payno = StringUtil.nullToString(object_in_data.getString("payno"));
			if ("".equals(paydt)) {
				paydt_yy = Integer.parseInt(paybank.substring(0, 4));
				paydt_mm = Integer.parseInt(paybank.substring(5, 7));
			} else {
				paydt_yy = Integer.parseInt(paydt.substring(0, 4));
				paydt_mm = Integer.parseInt(paydt.substring(5, 7));
			}

			// 1:補充費_[計算寫入](餉單編單):Cal_1
			if ("Cal_1".equals(selEn)) {
				// 先查詢panhical是否有內容
				paramA = new ArrayList<Object>();
				paramA.add(hid);
				paramA.add(salnum);
				log.info(me + "  0.先查詢panhical是否有內容" + sqlNhiCal_inq + "...params:" + paramA.toString());
				rs = da.query(sqlNhiCal_inq, paramA);
				while (rs.next()) {
					if (rs.getString("CNT") != null) {
						intcnt = Integer.parseInt(rs.getString("cnt"));
					}
				}
				log.info(me + "  0.先查詢panhical是否有內容:size:" + intcnt);
				// 有記錄,不用再寫入
				if (intcnt > 0) {
					rtn = " 補充費已有紀錄,salnum= " + salnum + " 筆數:" + intcnt;
				} else {
					// 寫入資料夾設定
					FileUtil fileUtil = new FileUtil();
					String folderPath = hospInfo.fileSRV + "log\\補充費\\";
					lognm = salnum + ".csv";
					filePath = folderPath + lognm;
					if (!FileUtil.FileToSmbFile(folderPath, hid).exists()) {
						fileUtil.FileToSmbFile(folderPath, hid).mkdirs();
					}
					// 建立檔案
					SmbFile payfile = fileUtil.FileToSmbFile(filePath, hid);
					bfw = new BufferedWriter(new OutputStreamWriter(new SmbFileOutputStream(payfile, false), "Big5"));

					// sb.setLength(0);

					starttime = System.currentTimeMillis();
					// 依餉單編號,擷取異動檔,列(1新進 2復職 11考績晉級 33納實 34考績升等 31晉級調薪 36晉級 37職務調整 38降級)
					paramA = new ArrayList<Object>();
					paramA.add(hid);
					paramA.add(salnum);
					log.info(me + "  1.依餉單編號,擷取異動檔 " + sqlTraPsn + "...params:" + paramA.toString());
					result = da.arraylistbyQuery(sqlTraPsn, paramA, META_TYPE);
					log.info(me + "  1.依餉單編號,擷取異動檔,size: " + result.size());
					if (result.size() > 0) {
						for (Map<String, Object> data : result) {
							trapsnobj = new Patrapsn();
							PropertyUtils.copyProperties(trapsnobj, data);
							strIdno = trapsnobj.getPayidno();
							trapsnMap.put(strIdno, trapsnobj);
						}
					}
					// 依餉單編號,擷取發薪支扣--兼職及進修醫師,PAYAUTH第3碼=Y
					paramA = new ArrayList<Object>();
					paramA.add(hid);
					paramA.add(salnum);
					log.info(me + "  2-1.依餉單編號,擷取發薪支扣--兼職及進修醫師,PAYAUTH第3碼=Y " + sqlDatPay_63 + "...params:"
							+ paramA.toString());
					result = da.arraylistbyQuery(sqlDatPay_63, paramA, META_TYPE);
					log.info(me + "  2-1.依餉單編號,擷取發薪支扣--兼職及進修醫師,PAYAUTH第3碼=Y,size: " + result.size());
					if (result.size() > 0) {
						for (Map<String, Object> data : result) {
							strIdno = StringUtil.nullToString((String) data.get("payidno"));
							datpay63Map.put(strIdno, (Integer) data.get("payamt"));
						}
					}

					// 依餉單編號,--擷取發薪支扣--公保身份1 2 3 4 8 9,PAYAUTH第1碼=Y
					paramA = new ArrayList<Object>();
					paramA.add(hid);
					paramA.add(salnum);
					log.info(me + "  2-2.依餉單編號,擷取發薪支扣--公保身份1 2 3 4 8 9,PAYAUTH第1碼=Y " + sqlDatPay_62_civil
							+ "...params:" + paramA.toString());
					result = da.arraylistbyQuery(sqlDatPay_62_civil, paramA, META_TYPE);
					log.info(me + "  2-2.依餉單編號,擷取發薪支扣--公保身份1 2 3 4 8 9,PAYAUTH第1碼=Y,size: " + result.size());
					if (result.size() > 0) {
						for (Map<String, Object> data : result) {
							strIdno = StringUtil.nullToString((String) data.get("payidno"));
							datpay62CMap.put(strIdno, (Integer) data.get("payamt"));
						}
					}

					// 依餉單編號,--擷取發薪支扣--勞保身份5 6 A B E F,PAYAUTH第2碼=Y
					paramA = new ArrayList<Object>();
					paramA.add(hid);
					paramA.add(salnum);
					log.info(me + "  2-3.依餉單編號,擷取發薪支扣--勞保身份5 6 A B E F,PAYAUTH第2碼=Y " + sqlDatPay_62_labor
							+ "...params:" + paramA.toString());
					result = da.arraylistbyQuery(sqlDatPay_62_labor, paramA, META_TYPE);
					log.info(me + "  2-3.依餉單編號,擷取發薪支扣--勞保身份5 6 A B E F,PAYAUTH第2碼=Y,size: " + result.size());
					if (result.size() > 0) {
						for (Map<String, Object> data : result) {
							strIdno = StringUtil.nullToString((String) data.get("payidno"));
							datpay62LMap.put(strIdno, (Integer) data.get("payamt"));
						}
					}

					// 累積獎金 BONUSAMT
					paramA = new ArrayList<Object>();
					paramA.add(hid);
					paramA.add(paydt_yy);
					// paramA.add(hid);
					// paramA.add(paydt_yy);
					log.info(me + "  2-4.累積獎金 " + sqlNhiJackpot + "...params:" + paramA.toString());
					result = da.arraylistbyQuery(sqlNhiJackpot, paramA, META_TYPE);
					log.info(me + "  2-3.累積獎金,size: " + result.size());
					if (result.size() > 0) {
						for (Map<String, Object> data : result) {
							strIdno = StringUtil.nullToString((String) data.get("payidno"));
							jackpotMap.put(strIdno, (Integer) data.get("bonusamt"));
						}
					}

					// 依發薪明細
					paramA = new ArrayList<Object>();
					paramA.add(hid);
					paramA.add(salnum);
					log.info(me + "  3.依發薪明細" + sqlDatPsn + "...params:" + paramA.toString());
					result = da.arraylistbyQuery(sqlDatPsn, paramA, META_TYPE);
					log.info(me + "  3.依發薪明細,size: " + result.size());
					if (result.size() > 0) {
						strLine = "身分證,卡號,姓名,階別,投保金額,所得類別,給付金額,補充費,備註";
						/*
						 * sb.append(strLine); sb.append(newline);
						 */
						bfw.write(strLine);
						bfw.newLine();
						bfw.flush();
						for (Map<String, Object> data : result) {
							docnt++;
							strIdno = (String) data.get("payidno");
							strPaycard = StringUtil.nullToString((String) data.get("paycard"));
							// if (strPaycard.equals("0341")){
							// System.out.println(strPaycard);
							// }
							strPAYNAMEC = StringUtil.trimWideEmpty((String) data.get("paynamec"));
							strRankAbbr = StringUtil.trimWideEmpty((String) data.get("rankabbr"));
							strPAYRANK = StringUtil.nullToString((String) data.get("payrank"));
							strPAYTITLE = StringUtil.nullToString((String) data.get("paytitle"));
							strPAYTITLP = StringUtil.nullToString((String) data.get("paytitlp"));
							strPAPAYDT = formatter.format(data.get("paybank"));
							intINSUAMT = (Integer) data.get("insuamt");
							strLine = strIdno + "," + strPaycard + "," + strPAYNAMEC + "," + strRankAbbr + ","
									+ intINSUAMT + ",";
							blnMONINSU = true;
							intNHIAMT = 0; // 補充費
							intPayAmt = 0; // 給付金額
							blnPart = false;
							strPAYITEM = "62";
							intINSUIDENT = 0; // 保險身份 0無1公保2勞保
							intJACKPOT = 0; // 累計獎金
							intMTINSU = 0; // 累計超過4倍投保金額之獎金
							intSUPLYBASE = 0; // 補充保險費費基
							// 進修醫師BK 兼職人員=G
							if (strPAYTITLE.equals("BK") || strPAYRANK.equals("G")) {
								blnPart = true;
								strPAYITEM = "63";
								intPayAmt = (datpay63Map.get(strIdno) == null) ? 0 : (Integer) datpay63Map.get(strIdno);
							} else if (strPAYRANK.matches("1|2|3|4|8|9|7")) {
								intINSUIDENT = 1;
								intPayAmt = (datpay62CMap.get(strIdno) == null) ? 0
										: (Integer) datpay62CMap.get(strIdno);

								if (intINSUAMT == 0) {
									paramA = new ArrayList<Object>();
									paramA.add(hid);
									paramA.add(strIdno);
									// 基本檔.投保金額=0,找發薪明細有保投金額最末一筆
									rs = da.query(sqlDatPsn_insuamt, paramA);
									while (rs.next()) {
										intINSUAMT = rs.getInt("INSUAMT");
									}
									if (intINSUAMT == 0) {
										blnMONINSU = false;
										strLine += strPAYITEM + "," + intPayAmt + ",,公保--月投保金額=0";
									}
								}
							} else if (strPAYRANK.matches("5|6|A|B|E|F")) {
								intINSUIDENT = 2;
								intPayAmt = (datpay62LMap.get(strIdno) == null) ? 0
										: (Integer) datpay62LMap.get(strIdno);
								if (intINSUAMT == 0) {
									paramA = new ArrayList<Object>();
									paramA.add(hid);
									paramA.add(strIdno);
									// 基本檔.投保金額=0,找發薪明細有保投金額最末一筆
									rs = da.query(sqlDatPsn_insuamt, paramA);
									while (rs.next()) {
										intINSUAMT = rs.getInt("INSUAMT");
									}
									if (intINSUAMT == 0) {
										blnMONINSU = false;
										strLine += strPAYITEM + "," + intPayAmt + ",,勞保--月投保金額=0";
									}
								}
							} else {
								blnMONINSU = false;
								strLine += ",,,職稱:" + strPAYTITLP + "免扣除";
							}
							// 有月投保金額或兼職人員
							if (blnMONINSU) {
								strLine += strPAYITEM + ",";
								// 勞保身份因工作獎金列入投保額,獎金追補有可能是負的,要排除
								if (intPayAmt > 0) {
									if (blnPart) {
										// 獎金>最低工資, 補充費=獎金*費率
										if (intPayAmt >= Globals.gBasSalMin) {
											intNHIAMT = (int) Math.round(intPayAmt * Globals.gNHIRate);
										}
									} else {
										// 累積獎金
										Integer value = (Integer) jackpotMap.get(strIdno);
										intJACKPOT = 0;
										if (value != null)
											intJACKPOT = value;

										// 累計超過4倍投保金額之獎金=累計獎金-4倍投保金額,未超過該項為0
										intJACKPOT = intPayAmt + intJACKPOT;
										intMTINSU = intJACKPOT - (intINSUAMT * 4);
										if (intMTINSU < 0) {
											intMTINSU = 0;
										}
										// 補充保險費費基=min(累計超過4倍投保金額之獎金,單次獎金)
										if (intMTINSU < intPayAmt)
											intSUPLYBASE = intMTINSU;
										else
											intSUPLYBASE = intPayAmt;
										// 補充保險費
										if (intSUPLYBASE <= 0)
											intNHIAMT = 0;
										else
											intNHIAMT = (int) Math.round(intSUPLYBASE * Globals.gNHIRate);
									}
									batch_sql.add(sqlNhiCal_ins);
									paramA = new ArrayList<Object>();
									paramA.add(strPAYITEM);
									paramA.add(strPaycard);
									paramA.add(strPAYNAMEC);
									paramA.add(intINSUAMT);
									paramA.add(intINSUAMT * 4);

									paramA.add(intPayAmt);
									paramA.add(intJACKPOT);
									paramA.add(intMTINSU);
									paramA.add(intSUPLYBASE);
									paramA.add(intNHIAMT);

									paramA.add(intINSUIDENT);
									paramA.add("");
									paramA.add(userid);
									paramA.add(strPAPAYDT);

									paramA.add("DAT");
									paramA.add(salnum);
									paramA.add(strIdno);
									paramA.add(hid);
									params.add(paramA);

									strLine += intPayAmt + "," + intNHIAMT;

								} else {
									strLine += ",,單次獎金金額(" + intPayAmt + ")<=0";
								}

								/*
								 * sb.append(strLine); sb.append(newline);
								 */
								bfw.write(strLine);
								bfw.newLine();
								bfw.flush();
								if (++write_cnt % limitcnt == 0 || docnt == result.size()) {
									log.info(batch_sql + " " + batch_sql + " params:" + params);
									da.startTransaction();
									try {
										da.batchupdatenocommit(batch_sql, params);
										da.commit();
										da.setAutoCommit(false);
										batch_sql.clear();
										params.clear();
									} catch (Exception e) {
										da.rollback();
										rtn = " fail, " + e;
										da.setAutoCommit(true);
										e.printStackTrace();
										log.info(me + " " + selNm + " " + e.toString());
									} finally {

									}
								}
							} else {
								/*
								 * sb.append(strLine); sb.append(newline);
								 */
								bfw.write(strLine);
								bfw.newLine();
								bfw.flush();
							}
						}
					}
					log.info(me + "  ...********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");

					/*
					 * starttimeW = System.currentTimeMillis();
					 * log.info(me+"  開始寫出文字檔："+filePath+"..."); outputstring = sb.toString(); File
					 * file = new File(filePath); output = new BufferedWriter(new FileWriter(file));
					 * System.out.println("開始寫出文字檔 "+filePath+"..."); output.write(outputstring);
					 * output.close();
					 * log.info(me+"  ...********* 寫入文字檔成功...共花費："+(System.currentTimeMillis()-
					 * starttimeW)+"毫秒");
					 * log.info(me+"  ...********* 寫入成功...共花費："+(System.currentTimeMillis()-
					 * starttime)+"毫秒");
					 */rtn = "寫入筆數:" + write_cnt + " ********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒";
				}
			}
			// 2:補充費_[刪除](餉單編單):Cal_2
			else if ("Cal_2".equals(selEn)) {
				starttime = System.currentTimeMillis();
				paramA.clear();
				paramA.add(hid);
				paramA.add(salnum);

				params.add(paramA);
				batch_sql.add(sqlNhiCal_del);

				da.startTransaction();
				try {
					log.info(me + "  " + selNm + ">>" + sqlNhiCal_del + "...params:" + paramA.toString());
					delCnt = da.update(sqlNhiCal_del, paramA);
					da.commit();
				} catch (Exception e) {
					da.rollback();
					rtn = " fail, " + e;
					da.setAutoCommit(true);
					e.printStackTrace();
					log.info(me + " " + selNm + " " + e.toString());
				} finally {

				}
				rtn = "刪除筆數:" + delCnt + " ********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒";
				log.info(me + "  " + selNm + " delCnt:" + delCnt + " 共花費：" + (System.currentTimeMillis() - starttime)
						+ "毫秒");
			}
			// 3:補充費_[產生]至批次檔(餉單編號):Cal_3
			else if ("Cal_3".equals(selEn)) {
				starttime = System.currentTimeMillis();
				// 先查詢pabatpay是否有內容
				paramA = new ArrayList<Object>();
				paramA.add(hid);
				paramA.add(salnum);
				paramA.add(strPaycode);
				log.info(me + "  0.先查詢pabatpay是否有內容" + sqlBatPay_inq + "...params:" + paramA.toString());
				rs = da.query(sqlBatPay_inq, paramA);
				while (rs.next()) {
					if (rs.getString("CNT") != null) {
						intcnt = Integer.parseInt(rs.getString("cnt"));
					}
				}
				log.info(me + "  0.先查詢pabatpay是否有內容:size:" + intcnt);
				// 有記錄,不用再寫入
				if (intcnt > 0) {
					rtn = " 批次檔已有紀錄,salnum= " + salnum + " 筆數:" + intcnt;
				} else {
					// 依補充費 salnum
					paramA = new ArrayList<Object>();
					paramA.add(hid);
					paramA.add(salnum);
					log.info(me + "  1.依補充費.餉單編號" + sqlNhiCal_bat + "...params:" + paramA.toString());
					result = da.arraylistbyQuery(sqlNhiCal_bat, paramA, META_TYPE);
					log.info(me + "  1.依補充費.餉單編號,size: " + result.size());
					if (result.size() > 0) {
						for (Map<String, Object> data : result) {
							strPAPAYDT = formatter.format(data.get("papaydt"));
							strIdno = (String) data.get("payidno");
							strPaycard = StringUtil.nullToString((String) data.get("paycard"));
							intNHIAMT = (Integer) data.get("nhiamt");

							batch_sql.add(sqlBatPay_ins);
							paramA = new ArrayList<Object>();
							paramA.add(strPAPAYDT);
							paramA.add(strIdno);
							paramA.add(salnum);
							paramA.add(strPaycard);
							paramA.add(strPaycode);
							paramA.add("N");
							paramA.add(intNHIAMT);
							paramA.add("O" + userid);
							paramA.add("");
							paramA.add("");
							paramA.add(hid);

							params.add(paramA);

							if (++write_cnt % limitcnt == 0 || write_cnt == result.size()) {
								log.info(batch_sql + " " + batch_sql + " params:" + params);
								da.startTransaction();
								try {
									da.batchupdatenocommit(batch_sql, params);
									da.commit();
									da.setAutoCommit(false);
									batch_sql.clear();
									params.clear();
								} catch (Exception e) {
									da.rollback();
									rtn = " fail, " + e;
									da.setAutoCommit(true);
									e.printStackTrace();
									log.info(me + " " + selNm + " " + e.toString());
								} finally {

								}
							}

						}
					}
					log.info(me + "  ...********* 寫入成功...共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
					rtn = "寫入筆數:" + write_cnt + " ********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒";
				}
			}
			// 4:補充費_[刪除]批次檔(餉單編號):Cal_4
			else if ("Cal_4".equals(selEn)) {
				starttime = System.currentTimeMillis();
				paramA.clear();
				paramA.add(hid);
				paramA.add(salnum);
				paramA.add(strPaycode);
				params.add(paramA);
				batch_sql.add(sqlBatPay_del);

				da.startTransaction();
				try {
					log.info(me + "  " + selNm + ">>" + sqlBatPay_del + "...params:" + paramA.toString());
					delCnt = da.update(sqlBatPay_del, paramA);
					da.commit();
				} catch (Exception e) {
					da.rollback();
					rtn = " fail, " + e;
					da.setAutoCommit(true);
					e.printStackTrace();
					log.info(me + " " + selNm + " " + e.toString());
				} finally {

				}
				rtn = "刪除筆數:" + delCnt + " ********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒";
				log.info(me + "  " + selNm + " delCnt:" + delCnt + " 共花費：" + (System.currentTimeMillis() - starttime)
						+ "毫秒");
			}

			// 5:申報扣費明細_[產生](給付日期):Det_1
			else if ("Det_1".equals(selEn)) {
				starttime = System.currentTimeMillis();
				// 先查詢panhidet是否有內容
				paramA = new ArrayList<Object>();
				paramA.add(hid);
				paramA.add(paydt);
				log.info(me + "  0.先查詢panhidet是否有內容" + sqlNhiDet_inq + "...params:" + paramA.toString());
				rs = da.query(sqlNhiDet_inq, paramA);
				while (rs.next()) {
					if (rs.getString("CNT") != null) {
						intcnt = Integer.parseInt(rs.getString("cnt"));
					}
				}
				log.info(me + "  0.先查詢panhidet是否有內容:size:" + intcnt);
				// 有記錄,不用再寫入
				if (intcnt > 0) {
					rtn = " 申報扣費檔已有紀錄,給付日期=" + paydt + " 筆數:" + intcnt;
				} else {
					// 補充費寫入申報檔_HID,PAPAYDT NHIAMT>0身份=無,補充費>0,才寫入
					paramA = new ArrayList<Object>();
					paramA.add(hid);
					paramA.add(paydt);
					log.info(me + "  1.依補充費.給付日期" + sqlNhiCal_Det + "...params:" + paramA.toString());
					result = da.arraylistbyQuery(sqlNhiCal_Det, paramA, META_TYPE);
					log.info(me + "  1.依補充費.給付日期,size: " + result.size());
					strPAYITEM = "";
					strIdno = "";
					if (result.size() > 0) {
						for (Map<String, Object> data : result) {
							if ((!strIdno.equals((String) data.get("payidno")) && !("").equals(strIdno))
									|| (!strPAYITEM.equals((String) data.get("payitem")) && !("").equals(strPAYITEM))) {
								batch_sql.add(sqlNhiDet_ins);
								paramA = new ArrayList<Object>();

								paramA.add(strPAPAYDT);
								paramA.add(strIdno);
								paramA.add(strPAYITEM);
								paramA.add(strPaycard);
								paramA.add(strPAYNAMEC);

								paramA.add(++write_cnt);
								paramA.add(intPayAmt);
								paramA.add(intNHIAMT);
								paramA.add(intMONINSU);
								paramA.add(intJACKPOT);

								paramA.add(intINSUIDENT);
								paramA.add("");
								paramA.add(userid);
								paramA.add(hid);
								params.add(paramA);

								intNHIAMT = 0;
								intPayAmt = 0;
								intMONINSU = 0;
								intJACKPOT = 0;
								intINSUIDENT = 0;
							}
							if (write_cnt % limitcnt == 0) {
								log.info(batch_sql + " " + batch_sql + " params:" + params);
								da.startTransaction();
								try {
									da.batchupdatenocommit(batch_sql, params);
									da.commit();
									da.setAutoCommit(false);
									batch_sql.clear();
									params.clear();
								} catch (Exception e) {
									da.rollback();
									rtn = " fail, " + e;
									da.setAutoCommit(true);
									e.printStackTrace();
									log.info(me + " " + selNm + " " + e.toString());
								} finally {

								}
							}

							strPAPAYDT = formatter.format(data.get("papaydt"));
							strIdno = (String) data.get("payidno");
							strPaycard = StringUtil.nullToString((String) data.get("paycard"));
							strPAYITEM = StringUtil.nullToString((String) data.get("payitem"));
							strPAYNAMEC = StringUtil.trimWideEmpty((String) data.get("paynamec"));
							intNHIAMT = (Integer) data.get("nhiamt") + intNHIAMT;
							intPayAmt = (Integer) data.get("bonusamt") + intPayAmt;
							intJACKPOT = (Integer) data.get("jackpot");
							intMONINSU = (Integer) data.get("moninsu");
							intINSUIDENT = (Integer) data.get("insuident");
						}
					}
					// 最後一筆
					batch_sql.add(sqlNhiDet_ins);
					paramA = new ArrayList<Object>();
					paramA.add(strPAPAYDT);
					paramA.add(strIdno);
					paramA.add(strPAYITEM);
					paramA.add(strPaycard);
					paramA.add(strPAYNAMEC);

					paramA.add(++write_cnt);
					paramA.add(intPayAmt);
					paramA.add(intNHIAMT);
					paramA.add(intMONINSU);
					paramA.add(intJACKPOT);

					paramA.add(intINSUIDENT);
					paramA.add("");
					paramA.add(userid);
					paramA.add(hid);
					params.add(paramA);
					if (write_cnt % limitcnt == 0 || write_cnt == result.size()) {
						log.info(batch_sql + " " + batch_sql + " params:" + params);
						da.startTransaction();
						try {
							da.batchupdatenocommit(batch_sql, params);
							da.commit();
							da.setAutoCommit(false);
							batch_sql.clear();
							params.clear();
						} catch (Exception e) {
							da.rollback();
							rtn = " fail, " + e;
							da.setAutoCommit(true);
							e.printStackTrace();
							log.info(me + " " + selNm + " " + e.toString());
						} finally {

						}
					}
					log.info(me + "  ...********* 寫入成功...共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
					rtn = "寫入筆數:" + write_cnt + " ********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒";
				}
			}
			// 6:申報扣費明細_[刪除](給付日期):Det_2
			else if ("Det_2".equals(selEn)) {
				starttime = System.currentTimeMillis();
				paramA.clear();
				paramA.add(hid);
				paramA.add(paydt);

				params.add(paramA);
				batch_sql.add(sqlNhiDet_del);

				da.startTransaction();
				try {
					log.info(me + "  " + selNm + ">>" + sqlNhiDet_del + "...params:" + paramA.toString());
					delCnt = da.update(sqlNhiDet_del, paramA);
					da.commit();
				} catch (Exception e) {
					da.rollback();
					rtn = " fail, " + e;
					da.setAutoCommit(true);
					e.printStackTrace();
					log.info(me + " " + selNm + " " + e.toString());
				} finally {

				}
				rtn = "刪除筆數:" + delCnt + " ********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒";
				log.info(me + "  " + selNm + " delCnt:" + delCnt + " 共花費：" + (System.currentTimeMillis() - starttime)
						+ "毫秒");
			}
			// 7:申報扣費明細_[產生]媒體申報檔(給付日期):Det_3
			else if ("Det_3".equals(selEn)) {
				// 寫入資料夾設定
				FileUtil fileUtil = new FileUtil();
				String folderPath = hospInfo.fileSRV + "data\\補充費\\";
				lognm = "DPR02_" + DateUtil.ConverToStr(paydt) + ".csv";
				filePath = folderPath + lognm;

				if (!FileUtil.FileToSmbFile(folderPath, hid).exists()) {
					fileUtil.FileToSmbFile(folderPath, hid).mkdirs();
				}
				// 建立檔案
				SmbFile payfile = fileUtil.FileToSmbFile(filePath, hid);
				bfw = new BufferedWriter(new OutputStreamWriter(new SmbFileOutputStream(payfile, false), "Big5"));

				// sb.setLength(0);

				starttime = System.currentTimeMillis();
				// 1.申報檔--媒體申報檔
				paramA = new ArrayList<Object>();
				paramA.add(hid);
				paramA.add(paydt);
				log.info(me + "  1.申報檔--媒體申報檔 " + sqlNhiDet_Media + "...params:" + paramA.toString());
				result = da.arraylistbyQuery(sqlNhiDet_Media, paramA, META_TYPE);
				log.info(me + "  1.申報檔--媒體申報檔,size: " + result.size());
				if (result.size() > 0) {
					strLine = "投保單位代號,統一編號,所得人身分證號,姓名,所得給付日期,所得類別,所得(收入)給付金額(股利所得時，請填列股利淨額),給付當月投保金額,本次股利所屬期間，以雇主身分加保之投保總金額,股利註記,信託註記,扣取時可扣抵稅額,年度確定可扣抵稅額,除權(息)基準日,特殊註記,扣繳補充保險費金額,資料註記,股利/執行業務收入所得所屬年度,股利所屬期間起訖年月";
					/*
					 * sb.append(strLine); sb.append(newline);
					 */
					bfw.write(strLine);
					bfw.newLine();
					bfw.flush();
					for (Map<String, Object> data : result) {
						nhidetobj = new Panhidet();
						PropertyUtils.copyProperties(nhidetobj, data);
						strIdno = nhidetobj.getPayidno();
						strUnitId = "";
						if (nhidetobj.getInsuident() == 1) // 公保
							strUnitId = hospInfo.healthCivID;
						else if (nhidetobj.getInsuident() == 2) // 勞保
							strUnitId = hospInfo.healthLabID;

						strLine = strUnitId + "," + hospInfo.taxNo + ","
								+ StringUtil.nullToString(nhidetobj.getPayidno()) + ","
								+ StringUtil.trimWideEmpty(nhidetobj.getPaynamec()) + ","
								+ DateUtil.getHcDT(nhidetobj.getPapaydt()) + ","
								+ StringUtil.nullToString(nhidetobj.getPayitem()) + ","
								+ StringUtil.nullToString(nhidetobj.getPayamt()) + ","
								+ StringUtil.nullToString(nhidetobj.getMoninsu()) + ",,,,,,,,,,,";

						/*
						 * sb.append(strLine); sb.append(newline);
						 */
						bfw.write(strLine);
						bfw.newLine();
						bfw.flush();
					}
				}

				/*
				 * starttimeW = System.currentTimeMillis();
				 * log.info(me+"  開始寫出文字檔："+filePath+"..."); outputstring = sb.toString(); File
				 * file = new File(filePath); output = new BufferedWriter(new FileWriter(file));
				 * System.out.println("開始寫出文字檔 "+filePath+"..."); output.write(outputstring);
				 * output.close();
				 * log.info(me+"  ...********* 寫入文字檔成功...共花費："+(System.currentTimeMillis()-
				 * starttimeW)+"毫秒");
				 */
				rtn = "寫入申報檔筆數:" + result.size() + " ********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒";
				// 2.申報檔--地址
				sb.setLength(0);
				paramA = new ArrayList<Object>();
				paramA.add(hid);
				paramA.add(paydt);
				paramA.add(hid);
				paramA.add(paydt);
				log.info(me + "  2.申報檔--地址 " + sqlNhiDet_Adr + "...params:" + paramA.toString());
				result = da.arraylistbyQuery(sqlNhiDet_Adr, paramA, META_TYPE);
				log.info(me + "  2.申報檔--地址,size: " + result.size());
				if (result.size() > 0) {
					// 寫入資料夾設定
					folderPath = hospInfo.fileSRV + "data\\補充費\\";
					lognm = "DPR01_" + DateUtil.ConverToStr(paydt) + ".csv";
					filePath = folderPath + lognm;

					if (!FileUtil.FileToSmbFile(folderPath, hid).exists()) {
						fileUtil.FileToSmbFile(folderPath, hid).mkdirs();
					}
					// 建立檔案
					payfile = fileUtil.FileToSmbFile(filePath, hid);
					bfw = new BufferedWriter(new OutputStreamWriter(new SmbFileOutputStream(payfile, false), "Big5"));

					strLine = "統一編號,所得人身分證號,所得人姓名,所得人地址";
					/*
					 * sb.append(strLine); sb.append(newline);
					 */
					bfw.write(strLine);
					bfw.newLine();
					bfw.flush();
					for (Map<String, Object> data : result) {
						strLine = hospInfo.taxNo + "," + StringUtil.nullToString((String) data.get("payidno")) + ","
								+ StringUtil.trimWideEmpty((String) data.get("paynamec")) + ","
								+ StringUtil.trimWideEmpty((String) data.get("payaddrs"));

						/*
						 * sb.append(strLine); sb.append(newline);
						 */
						bfw.write(strLine);
						bfw.newLine();
						bfw.flush();
					}
				}
				if (result.size() > 0) {
					/*
					 * starttimeW = System.currentTimeMillis();
					 * log.info(me+"  開始寫出[地址]文字檔："+filePath+"..."); outputstring = sb.toString();
					 * file = new File(filePath); output = new BufferedWriter(new FileWriter(file));
					 * System.out.println("開始寫出[地址]文字檔 "+filePath+"...");
					 * output.write(outputstring); output.close();
					 */
					log.info(
							me + "  ...********* 寫入地址]文字檔成功...共花費：" + (System.currentTimeMillis() - starttimeW) + "毫秒");
				}

				log.info(me + "  ...********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
				rtn += "\n 寫入地址筆數:" + result.size() + " ********* 共花費：" + (System.currentTimeMillis() - starttime)
						+ "毫秒";
			}
			// 8:所得稅_[寫入]轉帳項目:35 57 58 (給付日期):Tax_1
			// 9:所得稅_[寫入]轉帳項目:59(給付日期):Tax_2
			else if (selEn.matches("Tax_1|Tax_2")) {
				// 先查詢panhical是否有內容
				paramA = new ArrayList<Object>();
				paramA.add(hid);
				paramA.add(paydt_yy);
				paramA.add(paydt_mm);
				log.info(me + "  0.先查詢panhical是否有內容" + sqlNhiCalTax_inq + "...params:" + paramA.toString());
				rs = da.query(sqlNhiCalTax_inq, paramA);
				while (rs.next()) {
					if (rs.getString("CNT") != null) {
						intcnt = Integer.parseInt(rs.getString("cnt"));
					}
				}
				log.info(me + "  0.先查詢panhical是否有內容:size:" + intcnt);
				// 有記錄,不用再寫入
				if (intcnt > 0) {
					rtn = " 補充費已有紀錄,paydt= " + paydt + " 筆數:" + intcnt;
				} else {
					// 寫入資料夾設定
					FileUtil fileUtil = new FileUtil();
					String folderPath = hospInfo.fileSRV + "log\\補充費_人工入稅\\";
					lognm = paydt + ".csv";
					filePath = folderPath + lognm;
					if (!FileUtil.FileToSmbFile(folderPath, hid).exists()) {
						fileUtil.FileToSmbFile(folderPath, hid).mkdirs();
					}
					// 建立檔案
					SmbFile payfile = fileUtil.FileToSmbFile(filePath, hid);
					bfw = new BufferedWriter(new OutputStreamWriter(new SmbFileOutputStream(payfile, false), "Big5"));

					starttime = System.currentTimeMillis();

					// 累積獎金 BONUSAMT
					paramA = new ArrayList<Object>();
					paramA.add(hid);
					paramA.add(paydt_yy);
					log.info(me + "  1.累積獎金 " + sqlNhiJackpot + "...params:" + paramA.toString());
					result = da.arraylistbyQuery(sqlNhiJackpot, paramA, META_TYPE);
					log.info(me + "  1.累積獎金,size: " + result.size());
					if (result.size() > 0) {
						for (Map<String, Object> data : result) {
							strIdno = StringUtil.nullToString((String) data.get("payidno"));
							jackpotMap.put(strIdno, (Integer) data.get("bonusamt"));
						}
					}

					// 依人工入稅
					paramA = new ArrayList<Object>();
					paramA.add(hid);
					paramA.add(paydt_yy);
					paramA.add(paydt_mm);
					if (selEn.equals("Tax_1")) {
						intITEMNUM = 35;
						paramA.add(35);
						paramA.add(57);
						paramA.add(58);
						log.info(me + "  2.依人工入稅(35 57 58)" + sqlTaxCash35_inq + "...params:" + paramA.toString());
						result = da.arraylistbyQuery(sqlTaxCash35_inq, paramA, META_TYPE);
					} else if (selEn.equals("Tax_2")) {
						intITEMNUM = 59;
						paramA.add(59);
						log.info(me + "  2.依人工入稅(59)" + sqlTaxCash59_inq + "...params:" + paramA.toString());
						result = da.arraylistbyQuery(sqlTaxCash59_inq, paramA, META_TYPE);
					}
					log.info(me + "  2.依人工入稅,size: " + result.size());
					if (result.size() > 0) {
						strLine = "身分證,卡號,姓名,階別,投保金額,所得類別,給付金額,補充費,備註";
						bfw.write(strLine);
						bfw.newLine();
						bfw.flush();
						for (Map<String, Object> data : result) {
							docnt++;
							strIdno = (String) data.get("payidno");
							strPaycard = StringUtil.nullToString((String) data.get("paycard"));
							if (strPaycard.equals("3341")) {
								System.out.println(strPaycard);
							}
							strPAYNAMEC = StringUtil.trimWideEmpty((String) data.get("paynamec"));
							strRankAbbr = StringUtil.trimWideEmpty((String) data.get("rankabbr"));
							strPAYRANK = StringUtil.nullToString((String) data.get("payrank"));
							strPAYTITLE = StringUtil.nullToString((String) data.get("paytitle"));
							strPAYTITLP = StringUtil.nullToString((String) data.get("paytitlp"));
							intPayAmt = (Integer) data.get("paytaxpay"); // 給付金額
							intINSUAMT = (Integer) data.get("insuamt"); // 保險金額
							strPAYITEM = "62";
							intJACKPOT = 0; // 累計獎金
							intNHIAMT = 0; // 補充費
							intMTINSU = 0; // 累計超過4倍投保金額之獎金
							intSUPLYBASE = 0; // 補充保險費費基
							intINSUIDENT = 0; // 保險身份 0無1公保2勞保
							blnPart = false;

							strPAPAYDT = paydt;
							blnMONINSU = true;
							strLine = strIdno + "," + strPaycard + "," + strPAYNAMEC + "," + strRankAbbr + ","
									+ intINSUAMT + ",";
							// 基本檔.投保金額=0,找發薪明細有保投金額最末一筆
							if (intINSUAMT == 0) {
								paramA = new ArrayList<Object>();
								paramA.add(hid);
								paramA.add(strIdno);
								rs = da.query(sqlDatPsn_insuamt, paramA);
								while (rs.next()) {
									intINSUAMT = rs.getInt("INSUAMT");
								}
							}

							if (strPAYRANK.matches("1|2|3|4|8|9|7")) {
								intINSUIDENT = 1;
								strLine += strPAYITEM + "公保,";
								if (intINSUAMT == 0) {
									blnMONINSU = false;
								}
							}
							// 勞保身份 符合PAPAYITM.PAYAUTH第二碼=Y
							else if (strPAYRANK.matches("5|6|A|B|E|F")) {
								intINSUIDENT = 2;
								strLine += strPAYITEM + "勞保,";
								if (intINSUAMT == 0) {
									blnMONINSU = false;
								}
							}
							// 進修醫師BK 兼職人員=G
							else if (strPAYTITLE.equals("BK") || strPAYRANK.equals("G")) {
								blnPart = true;
								strPAYITEM = "63";
								strLine += strPAYITEM + "兼職,";
							} else if (strPAYNAMEC.equals("")) {
								blnMONINSU = false;
								strLine += ",,,找不到姓名";
							} else {
								blnMONINSU = false;
								strLine += ",,,職稱:" + strPAYTITLP + " 免扣除";
							}

							// 有月投保金額或兼職人員
							if (blnMONINSU) {
								// 勞保身份因工作獎金列入投保額,獎金追補有可能是負的,要排除
								if (intPayAmt > 0) {
									// 兼職
									if (blnPart) {
										// 獎金>最低工資, 補充費=獎金*費率
										if (intPayAmt >= Globals.gBasSalMin) {
											intNHIAMT = (int) Math.round(intPayAmt * Globals.gNHIRate);
										}
									} else {
										// 累積獎金
										Integer value = (Integer) jackpotMap.get(strIdno);
										intJACKPOT = 0;
										if (value != null)
											intJACKPOT = value;

										// 累計超過4倍投保金額之獎金=累計獎金-4倍投保金額,未超過該項為0
										intJACKPOT = intPayAmt + intJACKPOT;
										intMTINSU = intJACKPOT - (intINSUAMT * 4);
										if (intMTINSU < 0) {
											intMTINSU = 0;
										}
										// 補充保險費費基=min(累計超過4倍投保金額之獎金,單次獎金)
										if (intMTINSU < intPayAmt)
											intSUPLYBASE = intMTINSU;
										else
											intSUPLYBASE = intPayAmt;
										// 補充保險費
										if (intSUPLYBASE <= 0)
											intNHIAMT = 0;
										else
											intNHIAMT = (int) Math.round(intSUPLYBASE * Globals.gNHIRate);
									}
									// 補充費_新增
									batch_sql.add(sqlNhiCal_ins);
									paramA = new ArrayList<Object>();
									paramA.add(strPAYITEM);
									paramA.add(strPaycard);
									paramA.add(strPAYNAMEC);
									paramA.add(intINSUAMT);
									paramA.add(intINSUAMT * 4);

									paramA.add(intPayAmt);
									paramA.add(intJACKPOT);
									paramA.add(intMTINSU);
									paramA.add(intSUPLYBASE);
									paramA.add(intNHIAMT);

									paramA.add(intINSUIDENT);
									paramA.add("");
									paramA.add(userid);
									paramA.add(strPAPAYDT);

									paramA.add("TAX");
									paramA.add(intITEMNUM);
									paramA.add(strIdno);
									paramA.add(hid);
									params.add(paramA);

									strLine += intPayAmt + "," + intNHIAMT + ",";

								} else {
									strLine += ",,,單次獎金金額(" + intPayAmt + ")<=0";
								}

								bfw.write(strLine);
								bfw.newLine();
								bfw.flush();
								if (++write_cnt % limitcnt == 0 || docnt == result.size()) {
									// log.info(batch_sql+" "+batch_sql+" params:"+params);
									da.startTransaction();
									try {
										da.batchupdatenocommit(batch_sql, params);
										da.commit();
										da.setAutoCommit(false);
										batch_sql.clear();
										params.clear();
										rtn = "寫入筆數:" + write_cnt + "\n請至" + filePath + "查看";
									} catch (Exception e) {
										da.rollback();
										rtn = " fail, " + e;
										da.setAutoCommit(true);
										e.printStackTrace();
										log.info(me + " " + selNm + " " + e.toString());
									} finally {

									}
								}
							} else {
								strLine += intPayAmt + "," + intNHIAMT + ",月投保金額=0";
								bfw.write(strLine);
								bfw.newLine();
								bfw.flush();
							}
						}
					}
					log.info(me + "  ...********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
					rtn += " ********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒";
				}
			}
			// 10:所得稅_[刪除](給付日期):Tax_3
			else if ("Tax_3".equals(selEn)) {
				starttime = System.currentTimeMillis();
				paramA.clear();
				paramA.add(hid);
				paramA.add(paydt);

				params.add(paramA);
				batch_sql.add(sqlNhiCalTax_del);

				da.startTransaction();
				try {
					log.info(me + "  " + selNm + ">>" + sqlNhiCalTax_del + "...params:" + paramA.toString());
					delCnt = da.update(sqlNhiCalTax_del, paramA);
					da.commit();
				} catch (Exception e) {
					da.rollback();
					rtn = " fail, " + e;
					da.setAutoCommit(true);
					e.printStackTrace();
					log.info(me + " " + selNm + " " + e.toString());
				} finally {

				}
				rtn = "刪除筆數:" + delCnt + " ********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒";
				log.info(me + "  " + selNm + " delCnt:" + delCnt + " 共花費：" + (System.currentTimeMillis() - starttime)
						+ "毫秒");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.info(me + " fail, " + e);
			throw new FileNotFoundException(me + ", fail!!." + e);
		} catch (IOException e) {
			e.printStackTrace();
			log.info(me + " fail, " + e);
			throw new IOException(me + ", fail!!." + e);
		} catch (Exception e) {
			rtn = " fail, " + e;
			e.printStackTrace();
			log.info(me + " " + e.toString());
		} finally {
			if (bfw != null)
				bfw.close();
			closeAll(da, null, connFlg);
		}

		return rtn;
	}
	
	// bary:基本明細集合,tranum與異動個人關連,userid輸入人員
	private ArrayList<Patrapay> putTraPayAry(ArrayList<Pabaspay> bary, HashMap nMap, int tranum, String userid) {
		String me = ".putTraPayAry()";
		Patrapay oneobj = null;
		ArrayList<Patrapay> rtnAry = new ArrayList<Patrapay>();
		if (bary != null) {
			for (Pabaspay data : bary) {
				oneobj = new Patrapay();
				oneobj.setTranum(tranum); // 0和異動支扣名細關聯
				oneobj.setPayidno(data.getPayidno()); // 1身分證字號
				oneobj.setPatradt(java.sql.Date.valueOf((String) nMap.get("tradt"))); // 1發薪日期
				oneobj.setPaysort((String) nMap.get("sort")); // 3分類(N:新 O:原)
				oneobj.setPaycode(data.getPaycode()); // 4支扣代碼
				oneobj.setPayamt(data.getPayamt()); // 5金額
				oneobj.setPayauth(data.getPayauth()); // 6授權碼
				oneobj.setPayiftax(data.getPayiftax()); // 7是否列入所得
				// oneobj.setPayupdtm( new java.sql.Timestamp(System.currentTimeMillis()));
				// //8更改日期時間
				oneobj.setPayflag(""); // 9處理旗標
				oneobj.setHid(data.getHid()); // 10醫院別

				rtnAry.add(oneobj);
			}
		}
		return rtnAry;
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

	/**
	 * 應稅處理 in_data:(1:[入稅]轉帳發薪明細,salnum) 2[刪除]轉帳發薪明細yy年/mm月/catg轉帳項目
	 * 5身分證檢查及流水號編號:yy年 4[刪除]批次人工入稅 6產生媒體申報檔 傳出:rtn文字訊息
	 **/
	public String handelTax(String in_data, String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".handelTax(),userid = " + userid;
		log.info(me + ",in_data = " + in_data + ",userid = " + userid + ",hid = " + hid);
		String rtn = "";
		ResultSet rs = null;
		int updCnt = 0;
		CommDAO cDAO = null;
		String sqlbankdt_inq = "select salnum,paybank from " + this.tBankDtTbl + " where salnum=? with ur";
		String sqlgetCatgBytra = " select MAX(PAYCATG) AS CATG from " + this.tTaxTran
				+ " where PAYEAR = ? and PAYMONTH = ? AND HID=?  with ur ";
		String sqlDatPsn = " select SALNUM, PAYBANK, A.PAYIDNO, A.PAYCARD, A.PAYEMPNO, A.PAYNAMEC, A.PAYDEPT, A.PAYTITLE, A.PAYRNK2N, A.PAYRANK3, A.PAYDEGR2, A.PAYDEGRE, A.PAYRANK, A.PAYRANK2, A.PAYRANKC, A.PAYPFDOC, A.PACSFLAG, A.PAYPFSP1, A.PAYPFSP2, A.PAYPFSP3, A.PAYUPDID, A.PAYUPDTM, A.PAYRANK4, A.HID,  A.INSUAMT,COALESCE(B.PAYGOVDT, C.PAYGOVDT) paygovdt"
				+ " from " + this.tDatPsn + " A " + " LEFT OUTER JOIN " + this.tBasPsn
				+ " B ON A.PAYIDNO = B.PAYIDNO AND A.HID=B.HID "
				+ " LEFT OUTER JOIN (SELECT payidno,PAYGOVDT,hid FROM pa.patrapsn WHERE salnum=?  AND patracod<11) c ON a.payidno=c.payidno AND a.hid=c.hid "
				+
				// " where a.SALNUM =? AND a.HID=? and a.payidno='A229200256' ORDER by a.PAYIDNO
				// FETCH FIRST 100 ROWS ONLY with ur";
				" where a.SALNUM =? AND a.HID=? ORDER  by a.PAYIDNO with ur";
		String sqlDatPay = "select SALNUM, PAYIDNO, a.PAYCODE, PAYAMT, a.PAYAUTH, b.PAYIFTAX, PAYUPDTM, PAYFLAG, a.HID "
				+ " from " + this.tDatPay + " A " + " LEFT OUTER JOIN " + this.tPayItm
				+ "  b ON a.paycode=b.paycode AND a.hid=b.hid " + " where SALNUM = ? and a.HID= ? "
				+ " AND ((b.PAYIFTAX='Y') OR a.PAYCODE IN ('FD050','VD030','VD060','VD069','FD040','FD049','FD060','FD069','FD070','FD079')) "
				+ " order by payidno,paycode with ur";
		String sqlTaxPsn = "select PAYIDNO, PAYEAR, PAYCARD, PAYNAMEC, PAYDEPT, COALESCE(PAYIDFLG,'')PAYIDFLG, PAYBEGIN, PAYEND, COALESCE(PAYSERNO,0) PAYSERNO, PAYUPDID, PAYUPDTM, PAYSERNO9A, PAYSERNO9B, PAYSEREAMT, PAYSERNO92, PAYSERNO91, HID, PAYEMPNO, PAYSERNO51 "
				+ " from " + this.tTaxPsn + " where PAYEAR = ? and HID= ?  with ur";
		String sqlTaxPsn_upd = " update " + this.tTaxPsn
				+ " set (PAYCARD,PAYNAMEC,PAYDEPT,PAYEND,PAYUPDID,PAYEMPNO,PAYUPDTM) = (?, ?, ?, ?, ?, ?, current timestamp) "
				+ " where PAYIDNO =? and PAYEAR =? and HID=?";
		String sqlTaxPsn_ins = " insert into " + this.tTaxPsn
				+ " (PAYIDNO,PAYEAR,PAYCARD,PAYNAMEC,PAYDEPT,PAYBEGIN,PAYEND,PAYUPDID,PAYEMPNO,HID,PAYUPDTM) "
				+ "values (?,?,?,?,?,?,?,?,?,?,current timestamp) ";
		String sqlTaxTran_ins = "insert into " + this.tTaxTran
				+ "(PAYIDNO, PAYEAR, PAYMONTH, PAYCATG, PAYTAXPAY, PAYTAXDEDUCT, PAYUPDID, HID, PAYUPDTM) "
				+ "values(?, ?, ?, ?, ?, ?,?, ?,current timestamp);";
		String sqlTaxTran_del = "delete from " + this.tTaxTran
				+ " where PAYEAR = ? and PAYMONTH=? and PAYCATG =? and HID= ? ";
		// 序號清空
		String sqlSerno_upd = " update " + this.tTaxPsn
				+ " set (PAYSERNO,PAYSERNO9A,PAYSERNO9B,PAYSERNO92,PAYSERNO91,PAYSERNO51,PAYSEREAMT) = (0,0,0,0,0,0,0) "
				+ " where PAYEAR =? and HID=? ";
		// 自繳退休金
		String sqlSelfRetir_2A0 = " select A.PAYIDNO,SUM(payamt) AS AMT " + " from " + this.tDatPsn + " A "
				+ " LEFT OUTER JOIN " + this.tDatPay
				+ " B ON a.salnum=b.salnum AND a.payidno=b.payidno AND a.hid=b.HID "
				+ " where YEAR(a.paybank)= ? AND a.HID=? and paycode IN ('FD060','FD069') GROUP BY a.payidno with ur";
		String sqlSelfRetir_1A0 = " select A.PAYIDNO,SUM(payamt) AS AMT " + " from " + this.tDatPsn + " A "
				+ " LEFT OUTER JOIN " + this.tDatPay
				+ " B ON a.salnum=b.salnum AND a.payidno=b.payidno AND a.hid=b.HID "
				+ " where YEAR(a.paybank)= ? AND a.HID=? and paycode IN ('VD060','VD069') GROUP BY a.payidno with ur";
		// 退撫FD04 FD07
		String sqlRetir = " select A.PAYIDNO,SUM(payamt) AS AMT " + " from " + this.tDatPsn + " A "
				+ " LEFT OUTER JOIN " + this.tDatPay
				+ " B ON a.salnum=b.salnum AND a.payidno=b.payidno AND a.hid=b.HID "
				+ " where YEAR(a.paybank)= ? AND a.HID=? and paycode IN ('FD040','FD049','FD070','FD079') "
				+ " AND a.payrank IN ('1','2','3','8','9') and a.paybank >='" + Globals.gPensionFund
				+ "' GROUP BY a.payidno with ur";
		// 找該年員工格式50總所得,若為0不列入serno
		String sqlTax_50 = "select a.payidno,b.amt cash,c.amt tran,COALESCE(b.amt,0)+COALESCE(c.amt,0) both " + "from "
				+ tTaxPsn + " A "
				+ " left outer join (select PAYEAR,PAYIDNO,hid, COALESCE(sum(PAYTAXPAY),0) - COALESCE(sum(PAYTAXDEDUCT),0) AS AMT "
				+ "from " + tTaxCash
				+ " where PAYFORMAT = '50' GROUP BY PAYEAR,PAYIDNO,hid) b ON a.payidno=b.payidno AND a.hid=b.hid AND a.payear=b.payear "
				+ " left outer join (select PAYEAR,PAYIDNO,hid, COALESCE(sum(PAYTAXPAY),0) - COALESCE(sum(PAYTAXDEDUCT),0) AS AMT "
				+ "from " + tTaxTran
				+ " GROUP BY PAYEAR,PAYIDNO,hid) c ON a.payidno=c.payidno AND a.hid=c.hid AND a.payear=c.payear "
				+ " where a.PAYEAR=? AND a.HID=? with ur";
		// 找該年員工格式50總所得,若為0不列入serno,更新流水號
		String sqlTax_N50 = "select a.payidno,b.amt cash " + "from " + tTaxPsn + " A "
				+ " inner join (select PAYEAR,PAYIDNO,hid, COALESCE(sum(PAYTAXPAY),0) - COALESCE(sum(PAYTAXDEDUCT),0) AS AMT "
				+ "from " + tTaxCash
				+ " where PAYFORMAT = ? GROUP BY PAYEAR,PAYIDNO,hid) b ON a.payidno=b.payidno AND a.hid=b.hid AND a.payear=b.payear "
				+ " where a.PAYEAR=? AND a.HID=?  with ur";
		// 更新身分檢查碼,流水號PAYSERNO
		String sqlTaxPsnSer_upd = "update " + this.tTaxPsn
				+ " set (PAYIDFLG,PAYSERNO,PAYSEREAMT)=(?,?,?) where PAYIDNO=? and PAYEAR=? and HID=? ";
		// 媒體申報
		String sqlTax_Medium = "select a.PAYIDNO,PAYNAMEC,PAYBEGIN,PAYEND,PAYSERNO,PAYIDFLG,PAYADDRS,PAYSEREAMT "
				+ "from " + tTaxPsn + " A " + "left outer join " + tBasAdr + " B ON a.payidno=b.payidno "
				+ " where a.PAYEAR=? AND a.HID=? AND PAYSERNO<>0 ORDER BY PAYSERNO with ur";
		// 找該年員工格式50總所得
		String sqlTaxPay_50 = "select a.payidno,'50' payformat,COALESCE(b.PAY,0)+COALESCE(c.PAY,0) paytaxpay,COALESCE(b.DEDUCT,0)+COALESCE(c.DEDUCT,0) paytaxdeduct "
				+ "from " + tTaxPsn + " A "
				+ " left outer join (select PAYEAR,PAYIDNO,hid, COALESCE(sum(PAYTAXPAY),0) PAY, COALESCE(sum(PAYTAXDEDUCT),0) DEDUCT "
				+ "from " + tTaxCash
				+ " where PAYFORMAT = '50' GROUP BY PAYEAR,PAYIDNO,hid) b ON a.payidno=b.payidno AND a.hid=b.hid AND a.payear=b.payear "
				+ " left outer join (select PAYEAR,PAYIDNO,hid, COALESCE(sum(PAYTAXPAY),0) PAY, COALESCE(sum(PAYTAXDEDUCT),0) DEDUCT "
				+ "from " + tTaxTran
				+ " GROUP BY PAYEAR,PAYIDNO,hid) c ON a.payidno=c.payidno AND a.hid=c.hid AND a.payear=c.payear "
				+ " where a.PAYEAR=? AND a.HID=? with ur";
		// 找該年員工格式非50總所得 依payformat group
		String sqlTaxPay_N50 = "select a.payidno,b.PAYFORMAT,b.PAYCATG,COALESCE(b.PAY,0) paytaxpay,COALESCE(b.DEDUCT,0) paytaxdeduct "
				+ "from " + tTaxPsn + " A "
				+ " inner join (select PAYEAR,PAYIDNO,hid,PAYFORMAT,PAYCATG, COALESCE(sum(PAYTAXPAY),0) PAY, COALESCE(sum(PAYTAXDEDUCT),0) DEDUCT "
				+ "from " + tTaxCash
				+ " where PAYFORMAT <>'50' GROUP BY PAYEAR,PAYIDNO,hid,PAYFORMAT,PAYCATG) b ON a.payidno=b.payidno AND a.hid=b.hid AND a.payear=b.payear "
				+ " where a.PAYEAR=? AND a.HID=? ORDER BY a.payidno,b.payformat with ur";
		// 依傳票製票號刪除
		String sqlTaxCashTask_del = "delete from " + this.tTaxCash + " where PAYCITATION = ? and HID= ? ";

		ArrayList<Object> batch_sql = new ArrayList<Object>(); // batch_update sql 資料
		params = new ArrayList<Object>();
		ArrayList<Object> paramA = new ArrayList<Object>();

		String strIdno = "";
		String strFormat = "";

		String strCitation = "";
		String selEn = ""; // 應稅處理項目
		int salnum = 0; // 餉單編號
		String strBankDT = "";// 發薪日期
		int intyear = 0; // 發薪年
		int intMonth = 0; // 發薪月
		String strPayend = "";// 所得結束日期
		Date nowdate3 = null;
		int intCatg = 0; // 轉帳編號
		int intserno = 0; // 流水號
		String strIdflg = ""; // 身分證檢查碼註記--F外籍 E有誤
		String tmpA = ""; // 證號別
		String tmpB = ""; // 共用欄位1
		String tmpC = ""; // 流水號區分
		String tmpD = ""; // 勞工自繳退休金 共用欄位2
		String tmpE = ""; // 所得所屬期間
		String tmp = ""; //
		String tmpF = ""; // 憑單填發方式
		String tmpG = ""; //
		String tmpH = ""; // 稅務識別碼
		String tmpI = ""; // 錯誤註記
		String tmpAddr = ""; // 地址
		String tmpFormat = "";
		String tmpEmpNO = ""; // 職號
		int tmpPAYCATG = 0;
		int tmpsub = 0;
		int tmptax = 0;
		int intRetir = 0; // 自繳退休金+退撫離職金
		Pataxpsn taxpsnobj = null; // 所得稅個人obj
		Padatpay datpayobj = null; // 月支明細obj
		Padatpsn datpsnobj = null; // 月支個人obj
		ArrayList<Padatpay> datpayAry = new ArrayList<Padatpay>(); // 支扣集合
		HashMap sumListMap = new HashMap();
		HashMap datpayMap = new HashMap();
		HashMap taxpsnMap = new HashMap();
		HashMap selfRetirMap = new HashMap();
		Pataxobj taxSum50Obj = null;
		Pataxobj taxSumN50Obj = null;
		HashMap taxSum50Map = new HashMap();
		HashMap taxSumN50Map = new HashMap();
		HashMap retirMap = new HashMap();
		int limitcnt = 100;
		int write_cnt = 0;
		int delCnt = 0;
		int intTaxpay = 0;
		int intTaxdeduct = 0;
		long starttime = 0;

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
			// 1[入稅]轉帳發薪明細
			if ("InTran".equals(selEn)) {
				starttime = System.currentTimeMillis();
				salnum = object_in_data.getInt("salnum");
				// 依餉單編號,查詢發薪日期
				paramA.add(salnum);
				log.info(me + "  1.依餉單編號,查詢發薪日期" + sqlbankdt_inq + "...params:" + paramA.toString());
				rs = da.query(sqlbankdt_inq, paramA);
				while (rs.next()) {
					strBankDT = StringUtil.nullToString(rs.getString("PAYBANK"));
				}
				log.info(me + "  1.依餉單編號,查詢發薪日期=" + strBankDT);
				if (!"".equals(strBankDT)) {
					intyear = Integer.parseInt(strBankDT.substring(0, 4));
					intMonth = Integer.parseInt(strBankDT.substring(5, 7));
					// 所得結束日期
					String nowdate2 = strBankDT.substring(0, 4) + "-" + strBankDT.substring(5, 7) + "-" + "01";
					nowdate3 = DateUtil.StringToDate(nowdate2);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(nowdate3);
					calendar.add(Calendar.MONTH, 1);
					calendar.add(Calendar.DATE, -1);
					strPayend = formatter.format(calendar.getTime());
				}
				// 找轉帳所得稅,該年月的最大編號
				paramA = new ArrayList<Object>();
				paramA.add(intyear);
				paramA.add(intMonth);
				paramA.add(hid);
				log.info(me + "  2.找轉帳所得稅,該年月的最大編號" + sqlgetCatgBytra + "...params:" + paramA.toString());
				rs = da.query(sqlgetCatgBytra, paramA);
				intCatg = 0;
				while (rs.next()) {
					if (rs.getString("CATG") != null) {
						intCatg = Integer.parseInt(rs.getString("CATG")) + 1;
					}
				}
				log.info(me + "  2.找轉帳所得稅,該年月的最大編號:" + intCatg);

				// 依年,找所得稅個人
				paramA = new ArrayList<Object>();
				paramA.add(intyear);
				paramA.add(hid);
				log.info(me + "  3.依年,找所得稅個人 " + sqlTaxPsn + "...params:" + paramA.toString());
				result = da.arraylistbyQuery(sqlTaxPsn, paramA, META_TYPE);
				log.info(me + "  3.依年,找所得稅個人,size: " + result.size());
				if (result.size() > 0) {
					for (Map<String, Object> data : result) {
						taxpsnobj = new Pataxpsn();
						PropertyUtils.copyProperties(taxpsnobj, data);
						strIdno = taxpsnobj.getPayidno();
						taxpsnMap.put(strIdno, taxpsnobj);
					}
				}

				// 依餉單編號,找月支明細
				paramA = new ArrayList<Object>();
				paramA.add(salnum);
				paramA.add(hid);
				log.info(me + "  3-1.依餉單編號,找月支明細 " + sqlDatPay + "...params:" + paramA.toString());
				result = da.arraylistbyQuery(sqlDatPay, paramA, META_TYPE);
				log.info(me + "  3-1.依餉單編號,找月支明細,size: " + result.size());
				if (result.size() > 0) {
					strIdno = "";
					datpayAry = new ArrayList<Padatpay>();
					for (Map<String, Object> data : result) {
						datpayobj = new Padatpay();
						PropertyUtils.copyProperties(datpayobj, data);
						if (!"".equals(strIdno) && !(datpayobj.getPayidno()).equals(strIdno)) {
							datpayMap.put(strIdno, datpayAry);
							datpayAry = new ArrayList<Padatpay>();
						}
						datpayAry.add(datpayobj);
						strIdno = StringUtil.nullToString(datpayobj.getPayidno());
					}
					datpayMap.put(strIdno, datpayAry);
				}

				// 依餉單編號,找發薪明細_個人
				paramA.clear();
				paramA.add(salnum);
				paramA.add(salnum);
				paramA.add(hid);
				log.info(me + "  4.依餉單編號,找發薪明細_個人 " + sqlDatPsn + "...params:" + paramA.toString());
				result = da.arraylistbyQuery(sqlDatPsn, paramA, META_TYPE);
				log.info(me + "  4.依餉單編號,找發薪明細_個人,size: " + result.size());
				if (result.size() > 0) {
					for (Map<String, Object> data : result) {
						datpsnobj = new Padatpsn();
						PropertyUtils.copyProperties(datpsnobj, data);
						strIdno = (String) data.get("payidno");
						// log.info(me+" 4-1.data.IDNO: " + strIdno);
						// 找月支明細array
						datpayAry = (ArrayList<Padatpay>) datpayMap.get(strIdno);
						// 計算應稅所得
						sumListMap = null;
						if (datpayobj != null) {
							/*
							 * log.info(me+"  4.計算應稅所得,strIdno: " + strIdno); if
							 * ("E124688494".equals(strIdno)) log.info(me+"  strIdno: " + strIdno);
							 */
							sumListMap = cDAO.Sum_Pay_Deduct(datpayAry, datpsnobj.getPayrank(), datpsnobj.getPayrank2(),
									datpsnobj.getPayrnk2n(), datpsnobj.getPaydegre(), datpsnobj.getStrgovdt(),
									datpsnobj.getStrbank());
						}
						if (sumListMap != null) {
							batch_sql.add(sqlTaxTran_ins);
							paramA = new ArrayList<Object>();
							paramA.add(strIdno);
							paramA.add(intyear);
							paramA.add(intMonth);
							paramA.add(intCatg);
							paramA.add(sumListMap.get("taxpay"));
							paramA.add(sumListMap.get("tax"));
							paramA.add(userid);
							paramA.add(hid);
							params.add(paramA);
							// 找pataxpsn,有-update所得結束日期,無-insert
							taxpsnobj = (Pataxpsn) taxpsnMap.get(strIdno);
							if (taxpsnobj == null) {
								batch_sql.add(sqlTaxPsn_ins);

								paramA = new ArrayList<Object>();
								paramA.add(strIdno);
								paramA.add(intyear);
								paramA.add(datpsnobj.getPaycard());
								paramA.add(StringUtil.nullToString(datpsnobj.getPaynamec()));
								paramA.add(StringUtil.nullToString(datpsnobj.getPaydept()));
								paramA.add(java.sql.Date.valueOf(strBankDT));
								paramA.add(java.sql.Date.valueOf(strPayend));
								paramA.add(userid);
								paramA.add(datpsnobj.getPayempno());
								paramA.add(hid);
								params.add(paramA);
							} else {
								batch_sql.add(sqlTaxPsn_upd);
								paramA = new ArrayList<Object>();
								paramA.add(datpsnobj.getPaycard());
								paramA.add(StringUtil.nullToString(datpsnobj.getPaynamec()));
								paramA.add(StringUtil.nullToString(datpsnobj.getPaydept()));
								paramA.add(java.sql.Date.valueOf(strPayend));
								paramA.add(userid);
								paramA.add(datpsnobj.getPayempno());
								paramA.add(strIdno);
								paramA.add(intyear);
								paramA.add(hid);
								params.add(paramA);
							}
						}
						if (++write_cnt % limitcnt == 0 || write_cnt == result.size()) {
							// log.info(batch_sql+" "+batch_sql+" params:"+params);
							da.startTransaction();
							try {
								da.batchupdatenocommit(batch_sql, params);
								da.commit();
								da.setAutoCommit(false);
								batch_sql.clear();
								params.clear();
							} catch (Exception e) {
								da.rollback();
								rtn = " fail, " + e;
								da.setAutoCommit(true);
								e.printStackTrace();
								log.info(me + " " + selEn + " " + e.toString());
							} finally {

							}
						}
					}
				}
				log.info(me + "  ...********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
			}
			// 2.[刪除]轉帳發薪明細
			else if ("DelTran".equals(selEn)) {
				starttime = System.currentTimeMillis();
				intyear = object_in_data.getInt("yy");
				intMonth = object_in_data.getInt("mm");
				intCatg = object_in_data.getInt("catg");
				paramA.clear();
				paramA.add(intyear);
				paramA.add(intMonth);
				paramA.add(intCatg);
				paramA.add(hid);
				params.add(paramA);
				batch_sql.add(sqlTaxTran_del);

				// da.startTransaction();
				try {
					log.info(me + "  [刪除]轉帳發薪明細>>" + sqlTaxTran_del + "...params:" + paramA.toString());
					delCnt = da.update(sqlTaxTran_del, paramA);
					da.commit();
				} catch (Exception e) {
					da.rollback();
					rtn = " fail, " + e;
					da.setAutoCommit(true);
					e.printStackTrace();
					log.info(me + " " + selEn + " " + e.toString());
				} finally {

				}
				log.info(me + "  delCnt:" + delCnt + " 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
			}
			// 4[刪除]批次人工入稅
			else if ("DelBatCash".equals(selEn)) {
				starttime = System.currentTimeMillis();
				strCitation = object_in_data.getString("citation");
				paramA.clear();
				paramA.add(strCitation);
				paramA.add(hid);

				try {
					log.info(me + "  [刪除]批次人工入稅>>" + sqlTaxCashTask_del + "...params:" + paramA.toString());
					delCnt = da.update(sqlTaxCashTask_del, paramA);
					da.commit();
				} catch (Exception e) {
					da.rollback();
					rtn = " fail, " + e;
					da.setAutoCommit(true);
					e.printStackTrace();
					log.info(me + " " + selEn + " " + e.toString());
				} finally {

				}
				log.info(me + "  delCnt:" + delCnt + " 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
			}
			// 5身分證檢查及流水號編號
			else if ("ChkID".equals(selEn)) {
				starttime = System.currentTimeMillis();
				intyear = object_in_data.getInt("yy");

				paramA = new ArrayList<Object>();
				paramA.add(intyear);
				paramA.add(hid);

				log.info(me + " 1.[序號,自繳退休金] 清空>>" + sqlSerno_upd + "...params:" + paramA.toString());
				updCnt = da.update(sqlSerno_upd, paramA);
				da.commit();
				log.info(me + "  updCnt:" + updCnt + " 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");

				// 依年,找自繳退休金
				starttime = System.currentTimeMillis();
				paramA = new ArrayList<Object>();
				paramA.add(intyear);
				paramA.add(hid);
				if (hid.matches("2A0|3A0"))
					sql = sqlSelfRetir_2A0;
				else if (hid.matches("1A0"))
					sql = sqlSelfRetir_1A0;
				log.info(me + "  2.依年,找自繳退休金 " + sql + "...params:" + paramA.toString());
				result = da.arraylistbyQuery(sql, paramA, META_TYPE);
				log.info(me + "  2.依年,找自繳退休金,size: " + result.size() + " 共花費："
						+ (System.currentTimeMillis() - starttime) + "毫秒");
				if (result.size() > 0) {
					for (Map<String, Object> data : result) {
						strIdno = (String) data.get("payidno");
						selfRetirMap.put(strIdno, data.get("amt"));
					}
				}

				// 依年,找退撫
				starttime = System.currentTimeMillis();
				paramA = new ArrayList<Object>();
				paramA.add(intyear);
				paramA.add(hid);

				log.info(me + "  2-1.依年,找退撫 " + sqlRetir + "...params:" + paramA.toString());
				result = da.arraylistbyQuery(sqlRetir, paramA, META_TYPE);
				log.info(me + "  2-1.依年,找退撫,size: " + result.size() + " 共花費：" + (System.currentTimeMillis() - starttime)
						+ "毫秒");
				if (result.size() > 0) {
					for (Map<String, Object> data : result) {
						strIdno = (String) data.get("payidno");
						retirMap.put(strIdno, data.get("amt"));
					}
				}

				// 依年,找所得總金額,若為0,不處理序號
				starttime = System.currentTimeMillis();
				paramA = new ArrayList<Object>();
				paramA.add(intyear);
				paramA.add(hid);
				log.info(me + "  3.依年,找所得總金額,若為0,不處理序號 " + sqlTax_50 + "...params:" + paramA.toString());
				result = da.arraylistbyQuery(sqlTax_50, paramA, META_TYPE);
				log.info(me + "  3.依年,找所得總金額,若為0,不處理序號,size: " + result.size() + " 共花費："
						+ (System.currentTimeMillis() - starttime) + "毫秒");
				if (result.size() > 0) {
					for (Map<String, Object> data : result) {
						strIdno = (String) data.get("payidno");
						taxSum50Map.put(strIdno, data.get("both"));
					}
				}

				// 所得稅個人基本資料 格式 50
				starttime = System.currentTimeMillis();
				// 格式=50及檢查碼
				// #11408980 2025年14員外籍不列
				if (hid.equals("1A0") && intyear == 2025) {
					tmpEmpNO = " AND (substr(payempno,1,2) NOT IN ('TF')) ";
				}
				String sqlTaxPsn_50Chk = " select PAYIDNO, PAYEAR, HID, PAYCARD, PAYNAMEC, PAYDEPT, COALESCE(PAYIDFLG,'') PAYIDFLG, PAYBEGIN, PAYEND, COALESCE(PAYSERNO,0) PAYSERNO, PAYSERNO9A, PAYSERNO9B, PAYSEREAMT, PAYSERNO92, PAYSERNO91, PAYEMPNO, PAYSERNO51 "
						+ " from " + this.tTaxPsn + " where PAYEAR= ? and HID= ? " + "and PAYIDNO IN (SELECT PAYIDNO "
						+ " FROM " + this.tTaxCash + " WHERE PAYEAR = ? AND HID=? AND PAYFORMAT = ? " + " UNION ALL "
						+ " SELECT PAYIDNO " + " FROM " + this.tTaxTran + " WHERE PAYEAR = ? AND HID=?) " + tmpEmpNO
						+ "ORDER BY PAYIDNO with ur";

				paramA = new ArrayList<Object>();
				paramA.add(intyear);
				paramA.add(hid);
				paramA.add(intyear);
				paramA.add(hid);
				paramA.add("50");
				paramA.add(intyear);
				paramA.add(hid);
				log.info(me + "  4.所得稅個人基本資料 格式_50,更新流水號 " + sqlTaxPsn_50Chk + "...params:" + paramA.toString());
				result = da.arraylistbyQuery(sqlTaxPsn_50Chk, paramA, META_TYPE);
				if (result.size() > 0) {
					tmpsub = 0;
					intserno = 0;

					for (Map<String, Object> data : result) {
						taxpsnobj = new Pataxpsn();
						PropertyUtils.copyProperties(taxpsnobj, data);
						strIdno = (String) data.get("payidno");
						tmpsub = (Integer) taxSum50Map.get(strIdno);

						char[] strArr = strIdno.toCharArray();// 字串轉成char陣列
						if (tmpsub > 0) { // 若金額合計為0,不需列入
							if (CommDAO.isValidIDorRCNumber(strIdno)) {
								strIdflg = "";
							} else {
								// 若首碼為數字或第二位為英文字--外籍F,否則有誤E
								if ((Character.isDigit(strArr[0])) || (!Character.isDigit(strArr[1])))
									strIdflg = "F";
								else if (!strIdno.substring(1, 2).matches("1|2"))
									strIdflg = "F";
								else
									strIdflg = "E";
							}
							intRetir = 0;
							if (selfRetirMap.get(strIdno) != null)
								intRetir = intRetir + (Integer) selfRetirMap.get(strIdno);
							if (retirMap.get(strIdno) != null)
								intRetir = intRetir + (Integer) retirMap.get(strIdno);

							batch_sql.add(sqlTaxPsnSer_upd);
							paramA = new ArrayList<Object>();
							paramA.add(strIdflg);
							paramA.add(++intserno);
							paramA.add(intRetir);
							paramA.add(strIdno);
							paramA.add(intyear);
							paramA.add(hid);
							params.add(paramA);
							// log.info(write_cnt+" "+batch_sql+" "+batch_sql+" params:"+params);
							if (++write_cnt % limitcnt == 0 || write_cnt == result.size()) {
								// log.info(batch_sql+" "+batch_sql+" params:"+params);
								da.startTransaction();
								try {
									da.batchupdatenocommit(batch_sql, params);
									da.commit();
									da.setAutoCommit(false);
									batch_sql.clear();
									params.clear();
								} catch (Exception e) {
									da.rollback();
									rtn = " fail, " + e;
									da.setAutoCommit(true);
									e.printStackTrace();
									log.info(me + " " + selEn + " " + e.toString());
								} finally {

								}
							}
						}
					}
				}
				log.info(me + "  4.所得稅個人基本資料 格式_50,更新流水號,size: " + result.size() + " 共花費："
						+ (System.currentTimeMillis() - starttime) + "毫秒");

				//
				// 所得稅個人基本資料 格式 非50==>9A 9B 92(其他所得) 91(競技、競賽及機會中獎之獎金或給與) 51(租賃、權利金)
				starttime = System.currentTimeMillis();
				for (int i = 0; i < 5; ++i) {
					if (i == 0) {
						strFormat = "9A";
					} else if (i == 1) {
						strFormat = "9B";
					} else if (i == 2) {
						strFormat = "92";
					} else if (i == 3) {
						strFormat = "91";
					} else if (i == 4) {
						strFormat = "51";
					}
					sqlTaxPsnSer_upd = "update " + this.tTaxPsn + " set (PAYIDFLG,PAYSERNO" + strFormat
							+ ")=(?,?) where PAYIDNO=? and PAYEAR=? and HID=? ";
					write_cnt = 0;

					paramA = new ArrayList<Object>();
					paramA.add(strFormat);
					paramA.add(intyear);
					paramA.add(hid);

					log.info(me + "  5.所得稅個人基本資料 格式_非50,更新流水號,format= " + strFormat + " sql:" + sqlTax_N50
							+ "...params:" + paramA.toString());
					result = da.arraylistbyQuery(sqlTax_N50, paramA, META_TYPE);
					log.info(me + "  5.所得稅個人基本資料 格式_非50,更新流水號,format= " + strFormat + " size:" + result.size());
					if (result.size() > 0) {
						intserno = 0;
						for (Map<String, Object> data : result) {
							strIdno = StringUtil.nullToString((String) data.get("payidno"));

							char[] strArr = strIdno.toCharArray();// 字串轉成char陣列
							if (CommDAO.isValidIDorRCNumber(strIdno)) {
								strIdflg = "";
							} else {
								// ID長度=10,檢測
								if (strIdno.length() == 10) {
									// 若首碼為數字或第二位為英文字--外籍F,否則有誤E
									if ((Character.isDigit(strArr[0])) || (!Character.isDigit(strArr[1])))
										strIdflg = "F";
									else if (!strIdno.substring(1, 2).matches("1|2"))
										strIdflg = "F";
									else
										strIdflg = "E";
								}
							}

							batch_sql.add(sqlTaxPsnSer_upd);
							paramA = new ArrayList<Object>();
							paramA.add(strIdflg);
							paramA.add(++intserno);
							paramA.add(strIdno);
							paramA.add(intyear);
							paramA.add(hid);
							params.add(paramA);
							// log.info(write_cnt+" "+batch_sql+" "+batch_sql+" params:"+params);
							if (++write_cnt % limitcnt == 0 || write_cnt == result.size()) {
								// log.info(batch_sql+" "+batch_sql+" params:"+params);
								da.startTransaction();
								try {
									da.batchupdatenocommit(batch_sql, params);
									da.commit();
									da.setAutoCommit(false);
									batch_sql.clear();
									params.clear();
								} catch (Exception e) {
									da.rollback();
									rtn = " fail, " + e;
									da.setAutoCommit(true);
									e.printStackTrace();
									log.info(me + " " + selEn + " " + e.toString());
								} finally {

								}
							}
						}
					}
				}
				log.info(me + "  5.所得稅個人基本資料 格式_非50,更新流水號, 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
			}
			// 6產生媒體申報檔
			else if ("MediaTax".equals(selEn)) {
				intyear = object_in_data.getInt("yy");
				Hospitals hospInfo = Hospitals.getOneHosp(hid);

				// 依年,找50總所得金額
				starttime = System.currentTimeMillis();
				paramA = new ArrayList<Object>();
				paramA.add(intyear);
				paramA.add(hid);
				log.info(me + "  1.依年,找50總所得金額 " + sqlTaxPay_50 + "...params:" + paramA.toString());
				result = da.arraylistbyQuery(sqlTaxPay_50, paramA, META_TYPE);

				if (result.size() > 0) {
					for (Map<String, Object> data : result) {
						taxSum50Obj = new Pataxobj();
						strIdno = (String) data.get("payidno");
						PropertyUtils.copyProperties(taxSum50Obj, data);
						taxSum50Map.put(strIdno, taxSum50Obj);
					}
				}
				log.info(me + "  1.依年,找50總所得金額,size: " + result.size() + " 共花費："
						+ (System.currentTimeMillis() - starttime) + "毫秒");
				// 依年,找N50總所得金額
				starttime = System.currentTimeMillis();
				paramA = new ArrayList<Object>();
				paramA.add(intyear);
				paramA.add(hid);
				log.info(me + "  1-1.依年,找N50總所得金額 " + sqlTaxPay_N50 + "...params:" + paramA.toString());
				result = da.arraylistbyQuery(sqlTaxPay_N50, paramA, META_TYPE);

				if (result.size() > 0) {
					intTaxpay = 0;
					intTaxdeduct = 0;
					strIdno = "";
					strFormat = "";
					taxSumN50Obj = new Pataxobj();
					for (Map<String, Object> data : result) {
						if ((!"".equals(strIdno)
								&& !strIdno.equals(StringUtil.nullToString((String) data.get("payidno"))))
								|| (!"".equals(strFormat) && !strFormat
										.equals(StringUtil.nullToString((String) data.get("payformat"))))) {
							taxSumN50Obj.setPayidno(strIdno);
							taxSumN50Obj.setPayformat(strFormat);
							taxSumN50Obj.setPaytaxpay(intTaxpay);
							taxSumN50Obj.setPaytaxdeduct(intTaxdeduct);
							taxSumN50Obj.setPaycatg(intCatg);
							taxSumN50Map.put(strIdno + strFormat, taxSumN50Obj);
							taxSumN50Obj = new Pataxobj();
							intTaxpay = 0;
							intTaxdeduct = 0;
						}
						strIdno = StringUtil.nullToString((String) data.get("payidno"));
						strFormat = (String) data.get("payformat");
						intCatg = (Integer) data.get("paycatg");
						intTaxpay += (Integer) data.get("paytaxpay");
						intTaxdeduct += (Integer) data.get("paytaxdeduct");
					}
					taxSumN50Obj.setPayidno(strIdno);
					taxSumN50Obj.setPayformat(strFormat);
					taxSumN50Obj.setPaytaxpay(intTaxpay);
					taxSumN50Obj.setPaytaxdeduct(intTaxdeduct);
					taxSumN50Obj.setPaycatg(intCatg);
					taxSumN50Map.put(strIdno + strFormat, taxSumN50Obj);
				}
				log.info(me + "  1-1.依年,找N50總所得金額,size: " + result.size() + " 共花費："
						+ (System.currentTimeMillis() - starttime) + "毫秒");
				// 寫入資料夾設定
				String folderPath = hospInfo.fileSRV + "data\\";
				String filePath = folderPath + hospInfo.taxNo + "." + (intyear - 1911) + ".U8";
				FileUtil fileUtil = new FileUtil();

				if (!FileUtil.FileToSmbFile(folderPath, hid).exists()) {
					fileUtil.FileToSmbFile(folderPath, hid).mkdirs();
				}

				BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(
						new SmbFileOutputStream(FileUtil.FileToSmbFile(filePath, hid), false), "utf-8"));
				// 第一行
				// 稽徵機關代號|流水號(空值)|申報單位統一編號|資料區分(固定1)|申報單位名稱|申報單位地址|扣繳義務人名稱|聯絡人姓名|聯絡人電話|電子郵件|申報單位稅籍編號|申報次數(空值)|重複申報原因(空值)|上市公司註記(N)|金融機構註記(空值)|事務所、受任申報單位或檔案識別碼註記(N)|負責人姓名|
				String firstline = hospInfo.agencyNo + "||" + hospInfo.taxNo + "|1|" + hospInfo.nmFull + "|"
						+ hospInfo.adr + "|" + hospInfo.nmFull + "|" + hospInfo.contactNm + "|" + hospInfo.contactTel
						+ "||" + hospInfo.taxID + "|Y|||N||N" + "|" + hospInfo.agent + "|";
				bfw.write(firstline);
				bfw.newLine();
				bfw.flush();
				String strline = "";
				starttime = System.currentTimeMillis();
				for (int i = 0; i < 6; ++i) {
					if (i == 0) {
						strFormat = "";
						tmpC = "00"; // 依格式不同,流水號頭碼要分類
					} else if (i == 1) {
						strFormat = "9A";
						tmpC = "01";
					} else if (i == 2) {
						strFormat = "9B";
						tmpC = "02";
					} else if (i == 3) {
						strFormat = "92";
						tmpC = "03";
					} else if (i == 4) {
						strFormat = "91";
						tmpC = "04";
					} else if (i == 5) {
						strFormat = "51";
						tmpC = "05";
					}
					// 依所得格式,抓所得稅個人明細
					sqlTax_Medium = "select a.PAYIDNO,PAYNAMEC,PAYBEGIN,PAYEND,PAYSERNO" + strFormat
							+ ",PAYIDFLG,PAYADDRS,PAYSEREAMT " + "from " + tTaxPsn + " A " + "left outer join "
							+ tBasAdr + " B ON a.payidno=b.payidno " + " where a.PAYEAR=? AND a.HID=? AND PAYSERNO"
							+ strFormat + "<>0 ORDER BY PAYSERNO" + strFormat + " with ur";
					log.info(me + "  2.所得稅個人基本資料 ,format= " + strFormat + " sql:" + sqlTax_Medium + "...params:"
							+ paramA.toString());
					result = da.arraylistbyQuery(sqlTax_Medium, paramA, META_TYPE);
					log.info(me + "  2.所得稅個人基本資料 ,format= " + strFormat + " size:" + result.size());
					if (result.size() > 0) {
						String split_char = "\n";
						String split_char2 = "\r";
						for (Map<String, Object> data : result) {
							tmpF = "1"; // 免填發
							tmpG = "|||";
							tmpH = ""; // 稅務證別碼
							tmpD = ""; // 勞工自繳退休金 共用欄位2
							tmpA = "0"; // 證號別
							tmpsub = 0;
							tmptax = 0;
							tmpI = " "; // 錯誤註記
							strIdno = StringUtil.nullToString((String) data.get("payidno"));
							taxSum50Obj = (Pataxobj) taxSum50Map.get(strIdno);
							taxSumN50Obj = (Pataxobj) taxSumN50Map.get(strIdno + strFormat);
							tmpAddr = StringUtil.trimWideEmpty((String) data.get("payaddrs"));
							// 地址換行處理
							tmpAddr = (tmpAddr != null) ? tmpAddr.replaceAll(split_char2, "") : "";
							tmpAddr = tmpAddr.replaceAll(split_char, "");

							// 序號
							if (i == 0) {
								intserno = ((data.get("payserno") == null) ? 0 : (Integer) data.get("payserno"));
							} else if (i == 1) {
								intserno = ((data.get("payserno9a") == null) ? 0 : (Integer) data.get("payserno9a"));
							} else if (i == 2) {
								intserno = ((data.get("payserno9b") == null) ? 0 : (Integer) data.get("payserno9b"));
							} else if (i == 3) {
								intserno = ((data.get("payserno92") == null) ? 0 : (Integer) data.get("payserno92"));
							} else if (i == 4) {
								intserno = ((data.get("payserno91") == null) ? 0 : (Integer) data.get("payserno91"));
							} else if (i == 5) {
								intserno = ((data.get("payserno51") == null) ? 0 : (Integer) data.get("payserno51"));
							}

							strline = hospInfo.agencyNo + "|" + tmpC + StringUtil.intFillZero(intserno, 7) + "|"
									+ hospInfo.taxNo + "||";
							// 所得所屬期間
							if ((data.get("paybegin") == null) || (data.get("paybegin").equals("9999-12-31")))
								tmpE = "     ";
							else {
								// 2024-01-01 => 113/01/01
								tmp = DateUtil.ConverToTW(DateUtil
										.ConverToStr(DateUtil.getDbDate((java.util.Date) data.get("paybegin"), "-")));
								tmpE = tmp.substring(0, 3) + tmp.substring(4, 6);
							}
							if ((data.get("payend") == null) || (data.get("payend").equals("9999-12-31")))
								tmpE += "     ";
							else {
								// 2024-01-01 => 113/01/01
								tmp = DateUtil.ConverToTW(DateUtil
										.ConverToStr(DateUtil.getDbDate((java.util.Date) data.get("payend"), "-")));
								tmpE += tmp.substring(0, 3) + tmp.substring(4, 6);
							}
							// 所得,己扣
							if (i == 0) {
								tmpFormat = StringUtil.nullToString((String) taxSum50Obj.getPayformat());
								tmpsub = (Integer) taxSum50Obj.getPaytaxpay();
								tmptax = (Integer) taxSum50Obj.getPaytaxdeduct();

							} else {
								if (taxSumN50Obj != null) {
									tmpFormat = StringUtil.nullToString((String) taxSumN50Obj.getPayformat());
									tmpsub = (Integer) taxSumN50Obj.getPaytaxpay();
									tmptax = (Integer) taxSumN50Obj.getPaytaxdeduct();
									tmpPAYCATG = taxSumN50Obj.getPaycatg();
								}
							}
							// 證號別 事業團體
							if (strIdno.length() != 10)
								tmpA = "1";
							// 居住超過183天
							else if (data.get("payidflg").equals("F")) {
								tmpA = "3";
								tmpG = "|ZZ||";
								tmpH = "NOTIN";
							}
							// 錯誤註記
							// 【Ａ】所得人統一編（證）號邏輯檢查不符，經與所得人身分證、戶口名 簿、居留證或旅行證核對後確實相同無誤者。
							// 【Ｂ】所得人統一編（證）號邏輯檢查不符，所得人離職他去無從查對者。
							// 空值：所得人統一編（證）號邏輯檢查無誤。
							else if (data.get("payidflg").equals("E"))
								tmpI = "A";

							// 共用欄位1 tempB 自提退休金tempD
							if (tmpFormat.equals("50")) {
								tmpB = hid + tmpC + StringUtil.intFillZero(intserno, 7);
								tmpD = StringUtil.intFillZero((Integer) data.get("paysereamt"), 10);
							}
							// 92其他所得 2013.3.18
							else if (tmpFormat.equals("92"))
								tmpB = "8Z";
							// 91競技、競賽及機會中獎之獎金或給與 2013.6.3
							else if (tmpFormat.equals("91"))
								tmpB = hid + tmpC + StringUtil.intFillZero(intserno, 7);
							else if (tmpFormat.matches("9A|9B")) {
								tmpB = CommDAO.getCatgCd(tmpPAYCATG);
							}
							strline += tmpFormat + "|" + strIdno + "|" + tmpA + "|" + StringUtil.nullToString(tmpsub)
									+ "|" + StringUtil.nullToString(tmptax) + "|"
									+ StringUtil.nullToString(tmpsub - tmptax) + "|" + tmpB + "||||" + tmpI + "|"
									+ StringUtil.intFillZero(intyear - 1911, 3) + "|"
									+ StringUtil.trimWideEmpty((String) data.get("paynamec")) + "|" + tmpAddr + "|"
									+ tmpE + "|" + tmpD + "||||||" + tmpF + "|" + tmpG + "|"
									+ DateUtil.GetAppDate()[1].substring(4, 8) + "|" + tmpH;
							// log.info(strline);
							bfw.write(strline);
							bfw.newLine();
							bfw.flush();
						}
					}
				}
				log.info(me + "  6產生媒體申報檔, 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
				bfw.flush();
				if (bfw != null)
					bfw.close();
			}
		} catch (Exception e) {
			rtn = " fail, " + e;
			e.printStackTrace();
			log.info(me + " " + e.toString());
		} finally {

			closeAll(da, rs, connFlg);
		}
		return rtn;
	}

	/**
	 * update pa.pataxpsn(所得稅個人檔) pa.pabasaddr(地址) pa.pataxcash(所得稅現金明細)
	 * item:update/insert pk_data:key 傳出:true/false
	 **/
	public boolean updTaxByKey(String item, String pk_data, String tabJson, String userid, String hid, VGHDataAccess da)
			throws SQLException {
		String me = getClass().getName() + ".updTaxByKey(),userid = " + userid;
		log.info(me + ",item = " + item + ",pk_data = " + pk_data + ",tabJson = " + tabJson + ",userid = " + userid
				+ ",hid = " + hid);
		boolean success = false;
		JSONObject object_pk_data = JSONObject.fromObject(pk_data);
		ResultSet rs = null;
		ArrayList resultA = null;
		int updCnt = 0;
		String formnm = "";

		String sqlAdr = "";
		String sqltax = "";
		String sqltaxpsn_upd = " update " + this.tTaxPsn
				+ " set (PAYCARD, PAYNAMEC, PAYDEPT, PAYIDFLG, PAYBEGIN, PAYEND, PAYSERNO, PAYUPDID, PAYUPDTM, PAYSERNO9A, PAYSERNO9B, PAYSEREAMT, PAYSERNO92, PAYSERNO91, PAYEMPNO, PAYSERNO51) "
				+ "= (?,?,?,?,?,?,?,?,current timestamp,?,?,?,?,?,?,?) where PAYIDNO=? and PAYEAR =? and HID=? ";
		String sqltaxpsn_ins = " insert into " + this.tTaxPsn
				+ " (PAYCARD, PAYNAMEC, PAYDEPT, PAYIDFLG, PAYBEGIN, PAYEND, PAYSERNO, PAYUPDID, PAYUPDTM, PAYSERNO9A, PAYSERNO9B, PAYSEREAMT, PAYSERNO92, PAYSERNO91, PAYEMPNO, PAYSERNO51,PAYIDNO,PAYEAR,HID) "
				+ "values (?,?,?,?,?,?,?,?,current timestamp,?,?,?,?,?,?,?,?,?,?) ";
		String sqlbasadr_ins = " insert into " + this.tBasAdr + " (PAYADDRS,PAYIDNO) values (?,?)  ";
		String sqlbasadr_inq = "select payidno,payaddrs from " + this.tBasAdr + " where payidno=? with ur";
		String sqlbasadr_upd = " update " + this.tBasAdr + " set (PAYADDRS) = (?) where PAYIDNO=? ";
		String sqltaxcash_inq = "select PAYIDNO, PAYEAR, PAYMONTH, PAYCATG, PAYFORMAT, PAYCITATION, PAYTAXPAY, PAYTAXDEDUCT, PAYUPDID, PAYUPDTM, HID "
				+ "from " + this.tTaxCash
				+ " where payidno =? and PAYEAR =? and PAYMONTH =? and PAYCATG =? and PAYFORMAT =? and PAYCITATION =? and HID=? with ur";
		String sqltaxcash_ins = "insert into " + this.tTaxCash
				+ "(PAYTAXPAY, PAYTAXDEDUCT, PAYUPDID, PAYUPDTM, PAYIDNO, PAYEAR, PAYMONTH, PAYCATG, PAYFORMAT, PAYCITATION, HID) "
				+ "values(?, ?, ?, current timestamp, ?,?, ?, ?, ?, ?, ?);";
		String sqltaxcash_upd = " update " + this.tTaxCash
				+ " set (PAYTAXPAY, PAYTAXDEDUCT, PAYUPDID, PAYUPDTM) = (?, ?, ?, current timestamp) "
				+ "where PAYIDNO =? and PAYEAR =? and PAYMONTH =? and PAYCATG =? and PAYFORMAT =? and PAYCITATION =? and HID=?";
		String sqltaxcash_del = "delete from " + this.tTaxCash
				+ " where payidno =? and PAYEAR =? and PAYMONTH =? and PAYCATG =? and PAYFORMAT =? and PAYCITATION =? and HID=?";
		ArrayList<Object> batch_sql = new ArrayList<Object>(); // batch_update sql 資料
		params = new ArrayList<Object>();
		ArrayList<Object> paramA = new ArrayList<Object>();
		ArrayList<Object> param = new ArrayList<Object>();

		String strIdno = "";
		String strYear = "";
		String strMonth = "";
		String strCatg = "";
		String strFormat = "";
		String strCitation = "";

		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			da.setAutoCommit(false);
			JSONArray tabJsonary = JSONArray.fromObject(tabJson);
			for (int i = 0; i < tabJsonary.size(); ++i) {
				JSONObject obj = tabJsonary.getJSONObject(i);
				if (obj != null && obj.has("formnm")) {
					formnm = StringUtil.nullToString(obj.getString("formnm"));
					// 所得稅個人基本檔
					if ("TAXPSN".equals(formnm)) {
						strIdno = StringUtil.nullToString(object_pk_data.getString("payidno"));
						hid = StringUtil.nullToString(object_pk_data.getString("hid"));
						strYear = StringUtil.nullToString(object_pk_data.getString("payear"));

						if (item.equals("update")) {
							sqltax = sqltaxpsn_upd;
						} else if (item.equals("insert")) {
							sqltax = sqltaxpsn_ins;
						}

						// 查詢地址,找無,用insert,有用update
						paramA.add(strIdno);
						// rs = da.query(sqlbasadr_inq,paramA);
						resultA = da.arraylistbyQuery(sqlbasadr_inq, paramA, META_TYPE);
						sqlAdr = sqlbasadr_ins;
						if (resultA != null && resultA.size() > 0) {
							// while (rs.next()) {
							sqlAdr = sqlbasadr_upd;
						}

						JSONArray array = obj.getJSONArray("value");
						params = new ArrayList<Object>();
						if (null != array && array.size() > 0) {
							for (int j = 0; j < array.size(); j++) {
								JSONObject obj_item = array.getJSONObject(j);

								// 所得稅個人明細
								param = new ArrayList<Object>();
								param.add((!obj_item.has("paycard")) ? "" : obj_item.getString("paycard"));
								param.add((!obj_item.has("paynamec")) ? "" : obj_item.getString("paynamec"));
								param.add((!obj_item.has("paydept")) ? "" : obj_item.getString("paydept"));
								param.add((!obj_item.has("payidflg")) ? "" : obj_item.getString("payidflg"));
								param.add((!obj_item.has("paybegin")) ? java.sql.Date.valueOf("9999-12-31")
										: ("".equals(obj_item.getString("paybegin"))
												? java.sql.Date.valueOf("9999-12-31")
												: obj_item.getString("paybegin")));
								param.add((!obj_item.has("payend")) ? java.sql.Date.valueOf("9999-12-31")
										: ("".equals(obj_item.getString("payend")) ? java.sql.Date.valueOf("9999-12-31")
												: obj_item.getString("payend")));
								param.add((!obj_item.has("payserno")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("payserno"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("payserno")));
								param.add(userid);
								param.add((!obj_item.has("payserno9a")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("payserno9a"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("payserno9a")));
								param.add((!obj_item.has("payserno9b")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("payserno9b"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("payserno9b")));
								param.add((!obj_item.has("paysereamt")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paysereamt"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paysereamt")));
								param.add((!obj_item.has("payserno92")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("payserno92"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("payserno92")));
								param.add((!obj_item.has("payserno91")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("payserno91"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("payserno91")));
								param.add((!obj_item.has("payempno")) ? "" : obj_item.getString("payempno"));
								param.add((!obj_item.has("payserno51")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("payserno51"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("payserno51")));
								param.add(strIdno);
								param.add(strYear);
								param.add(hid);

								batch_sql.add(sqltax);
								params.add(param);
								log.info("1.batch_sql " + batch_sql + " params:" + params);
								// 地址
								param = new ArrayList<Object>();
								param.add((!obj_item.has("payaddrs")) ? ""
										: StringUtil
												.toWideCase(StringUtil.nullToString(obj_item.getString("payaddrs"))));
								param.add(strIdno);
								batch_sql.add(sqlAdr);
								params.add(param);
								log.info("2.batch_sql " + batch_sql + " params:" + params);
							}
						}
					}
					// 現金所得稅
					else if ("TAXCASH".equals(formnm)) {
						strIdno = StringUtil.nullToString(object_pk_data.getString("payidno"));
						hid = StringUtil.nullToString(object_pk_data.getString("hid"));
						strYear = StringUtil.nullToString(object_pk_data.getString("payear"));
						strMonth = StringUtil.nullToString(object_pk_data.getString("paymonth"));
						strCatg = StringUtil.nullToString(object_pk_data.getString("paycatg"));
						strFormat = StringUtil.nullToString(object_pk_data.getString("payformat"));
						strCitation = StringUtil.nullToString(object_pk_data.getString("paycitation"));

						paramA.add(strIdno);
						paramA.add(strYear);
						paramA.add(strMonth);
						paramA.add(strCatg);
						paramA.add(strFormat);
						paramA.add(strCitation);
						paramA.add(hid);
						resultA = da.arraylistbyQuery(sqltaxcash_inq, paramA, META_TYPE);
						if (item.equals("D")) {// 先查詢,有,用delete
							sqltax = "";
							if (resultA != null && resultA.size() > 0) {
								// while (rs.next()) {
								sqltax = sqltaxcash_del;
							}
							batch_sql.add(sqltax);

							params.add(paramA);

						} else if (item.equals("U")) {// 先查詢,[無]insert,[有]update
							sqltax = sqltaxcash_ins;
							if (resultA != null && resultA.size() > 0) {
								// while (rs.next() ) {
								sqltax = sqltaxcash_upd;
							}
							batch_sql.add(sqltax);

							JSONArray array = obj.getJSONArray("value");
							params = new ArrayList<Object>();
							if (null != array && array.size() > 0) {
								for (int j = 0; j < array.size(); j++) {
									JSONObject obj_item = array.getJSONObject(j);

									param = new ArrayList<Object>();
									if (item.matches("U")) {
										param.add((!obj_item.has("paytaxpay")) ? ""
												: ("").equals(StringUtil.nullToString(obj_item.getString("paytaxpay")))
														? "0"
														: StringUtil.nullToString(obj_item.getString("paytaxpay")));
										param.add(
												(!obj_item.has("paytaxdeduct")) ? ""
														: ("").equals(StringUtil
																.nullToString(obj_item.getString("paytaxdeduct"))) ? "0"
																		: StringUtil.nullToString(
																				obj_item.getString("paytaxdeduct")));
										param.add(userid);
									}
									param.add((!obj_item.has("payidno")) ? ""
											: StringUtil.nullToString(obj_item.getString("payidno")));
									param.add((!obj_item.has("payear")) ? ""
											: StringUtil.nullToString(obj_item.getString("payear")));
									param.add((!obj_item.has("paymonth")) ? ""
											: StringUtil.nullToString(obj_item.getString("paymonth")));
									param.add((!obj_item.has("paycatg")) ? ""
											: StringUtil.nullToString(obj_item.getString("paycatg")));
									param.add((!obj_item.has("payformat")) ? ""
											: StringUtil.nullToString(obj_item.getString("payformat")));
									param.add((!obj_item.has("paycitation")) ? ""
											: StringUtil.nullToString(obj_item.getString("paycitation")));
									param.add(hid);

									params.add(param);
								}
							}
						}

					}
					try {
						log.info(batch_sql + " " + batch_sql + " params:" + params);
						da.batchupdatenocommit(batch_sql, params);
						da.commit();
						success = true;

					} catch (SQLException e) {
						da.rollback();
						da.setAutoCommit(true);
						e.printStackTrace();
						log.info(me + " " + e.toString());
					}
				}
			}
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}
		return success;
	}

	// 依條件,取得pa.pabaspsn
	// 讀取範圍item:0(基本檔) 1(基本檔+異動檔_新進)
	public Pabaspsn getBasPsnByKey(String in_data, String item, String hid, VGHDataAccess da) throws Exception {
		Pabaspsn pabaspsn = null;
		String me = getClass().getName() + ".getBasPsnByKey()";
		log.info(me + " item = " + item + " in_data = " + in_data + " hid = " + hid);
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_in_data = null;
		String strSelect_bas = "";
		String strSelect_tra = "";
		String sqlFrom = "";
		String data_nm = "";
		String data_val = "";
		String data_tradt = "";
		long starttime = 0;
		ArrayList<Object> params = new ArrayList<Object>();
		starttime = System.currentTimeMillis();

		if (in_data != null && !in_data.equals("")) {
			object_in_data = JSONObject.fromObject(in_data);
			data_val = (!object_in_data.has("data_val")) ? "" : object_in_data.getString("data_val");
			data_nm = (!object_in_data.has("data_nm")) ? "" : object_in_data.getString("data_nm");
			data_tradt = (!object_in_data.has("data_tradt")) ? "" : object_in_data.getString("data_tradt");
		}
		// 讀取pa.pabaspsn
		strSelect_bas = "PAYLVEFG, A.PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, PAYEMPDT, PAYSGNDT, PAYRSGDT, PAYBKID, PAYACNO, "
				+ "PAYGEDNO, COALESCE(A.PAYINFLG,0) as PAYINFLG, COALESCE(A.PAYDPNO,'0') as PAYDPNO,  PAYTITLE, PAYRNK2N, COALESCE(A.PAYRANK3,0) as PAYRANK3, COALESCE(A.PAYDEGR2,0) as PAYDEGR2, "
				+ "COALESCE(A.PAYDEGRE,0) as PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, COALESCE(A.PAYPFDOC,0) as PAYPFDOC, PACSFLAG, "
				+ "COALESCE(A.PAYPFSP1,0) as PAYPFSP1, COALESCE(A.PAYPFSP2,0) as PAYPFSP2, COALESCE(A.PAYPFSP3,0) as PAYPFSP3, A.PATRACOD, "
				+ "PATRADT, COALESCE(A.PAYTAXNO,0) as PAYTAXNO, A.PAYUPDID, A.PAYUPDTM, PAYEXPFKEY1, PAYEXPFKEY2, PAYEXPFKEY3, COALESCE(A.PAYRANK4,0) as PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, "
				+ "INSUAMT, PARETENTION, A.HID, " + tmpZbaspsn + ","
				+ "B.PADEPNMC,B.PACSTNO,C.PAYTITLP,C.PATITSUB,D.RANKNAME,E.RNK4NAME,F.TRANAME ";
		// 讀取pa.patrapsn
		strSelect_tra = "PAYLVEFG, A.PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, PAYEMPDT, PAYSGNDT, PAYRSGDT, PAYBKID, PAYACNO, "
				+ "PAYGEDNO, COALESCE(A.PAYINFLG,0) as PAYINFLG, COALESCE(A.PAYDPNO,'0') as PAYDPNO,  PAYTITLE, PAYRNK2N, COALESCE(A.PAYRANK3,0) as PAYRANK3, COALESCE(A.PAYDEGR2,0) as PAYDEGR2, "
				+ "COALESCE(A.PAYDEGRE,0) as PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, COALESCE(A.PAYPFDOC,0) as PAYPFDOC, PACSFLAG, "
				+ "COALESCE(A.PAYPFSP1,0) as PAYPFSP1, COALESCE(A.PAYPFSP2,0) as PAYPFSP2, COALESCE(A.PAYPFSP3,0) as PAYPFSP3, A.PATRACOD, "
				+ "PATRADT, COALESCE(A.PAYTAXNO,0) as PAYTAXNO, A.PAYUPDID, A.PAYUPDTM, PAYEXPFKEY1, PAYEXPFKEY2, PAYEXPFKEY3, COALESCE(A.PAYRANK4,0) as PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, "
				+ "INSUAMT, '' PARETENTION, A.HID, " + tmpZtrapsn + ","
				+ "B.PADEPNMC,B.PACSTNO,C.PAYTITLP,C.PATITSUB,D.RANKNAME,E.RNK4NAME,F.TRANAME ";

		sqlFrom = " from " + this.tBasPsn + " A " + " left outer join " + this.tDept
				+ " B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID " + " left outer join " + this.tTitle
				+ " C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID " + " left outer join " + this.tRankCode
				+ " D on A.PAYRANK=D.RANKCODE " + " left outer join " + this.tRnk4Tbl + " E on A.PAYRANK4=E.RNK4KEY "
				+ " left outer join " + this.tTraCode + " F on A.PATRACOD=F.TRACODE " + " where A.HID=? ";

		sql = "select " + strSelect_bas + sqlFrom;
		params.add(hid);
		if (data_nm.equals("payidno")) {
			sql += " and A.payidno=? ";
		} else if (data_nm.equals("paycard")) {
			sql += " and A.paycard=? ";
		}
		params.add(data_val);
		// 因早期卡號有重覆
		sql += " order by PAYGOVDT desc FETCH FIRST 1 ROW ONLY with ur";

		log.info(me + "  " + sql + "...params:" + params.toString());

		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			// 先找 基本檔
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());
			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					pabaspsn = new Pabaspsn();
					PropertyUtils.copyProperties(pabaspsn, data);
				}
			} else {
				// 加讀 異動檔新進,要有發薪日
				if ("1".equals(item)) {
					if (!"".equals(data_tradt)) {
						sql = "select " + strSelect_tra + sqlFrom + " and A.PATRACOD BETWEEN 1 AND 10 and A.PATRADT=? ";
						params.add(hid);
						params.add(data_tradt);
						if (data_nm.equals("payidno")) {
							sql += " and A.payidno=? ";
						} else if (data_nm.equals("paycard")) {
							sql += " and A.paycard=? ";
						}
						params.add(data_val);
						sql += " with ur";
						log.info(me + "  " + sql + "...params:" + params.toString());
						result = da.arraylistbyQuery(sql, params, META_TYPE);
						log.info(me + "...size:" + result.size());
						if (result.size() > 0) {
							for (Map<String, Object> data : result) {
								pabaspsn = new Pabaspsn();
								PropertyUtils.copyProperties(pabaspsn, data);
							}
						}
					}
				}
			}

			log.info(me + "  ...********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}
		return pabaspsn;
	}

	// 依條件,取得pa.patrapay
	public Patrapay getTrapayByKey(String in_data, String hid, VGHDataAccess da) throws Exception {
		Patrapay patrapay = null;
		String me = getClass().getName() + ".getTrapayByKey()";
		log.info(me + " in_data = " + in_data + " hid = " + hid);
		JSONObject object_in_data = null;
		String strSelect_bat = "";
		String sqlFrom = "";
		String data_nm = "";
		String data_val = "";
		String data_tradt = "";
		String data_idno = "";
		long starttime = 0;
		ArrayList<Object> params = new ArrayList<Object>();
		starttime = System.currentTimeMillis();

		if (in_data != null && !in_data.equals("")) {
			object_in_data = JSONObject.fromObject(in_data);
			data_val = (!object_in_data.has("data_val")) ? "" : object_in_data.getString("data_val");
			data_nm = (!object_in_data.has("data_nm")) ? "" : object_in_data.getString("data_nm");
			data_tradt = (!object_in_data.has("data_tradt")) ? "" : object_in_data.getString("data_tradt");
			data_idno = (!object_in_data.has("data_idno")) ? "" : object_in_data.getString("data_idno");
		}
		// 讀取pa.patrapay
		strSelect_bat = "TRANUM, PAYIDNO, PATRADT, PAYSORT, PAYCODE, PAYAMT, PAYAUTH, PAYIFTAX, PAYUPDTM, PAYFLAG, HID, "
				+ tmpZtrapay;
		sql = "select " + strSelect_bat + " from " + this.tTraPay
				+ " where HID=? and PATRADT=? and PAYIDNO=? and PAYSORT =? and PAYCODE=? with ur";

		params.add(hid);
		params.add(data_tradt);
		params.add(data_idno);
		params.add("N");
		params.add(data_val);

		log.info(me + "  " + sql + "...params:" + params.toString());

		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());
			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					patrapay = new Patrapay();
					PropertyUtils.copyProperties(patrapay, data);
				}
			}

			log.info(me + "  ...********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}
		return patrapay;
	}

	// 依條件,取得pa.pabatpay
	public Pabatpay getBatpayByKey(String in_data, String hid, VGHDataAccess da) throws Exception {
		Pabatpay pabatpay = null;
		String me = getClass().getName() + ".getBatpayByKey()";
		log.info(me + " in_data = " + in_data + " hid = " + hid);
		JSONObject object_in_data = null;
		String strSelect_bat = "";
		String sqlFrom = "";
		String data_nm = "";
		String data_val = "";
		String data_tradt = "";
		String data_idno = "";
		long starttime = 0;
		ArrayList<Object> params = new ArrayList<Object>();
		starttime = System.currentTimeMillis();

		if (in_data != null && !in_data.equals("")) {
			object_in_data = JSONObject.fromObject(in_data);
			data_val = (!object_in_data.has("data_val")) ? "" : object_in_data.getString("data_val");
			data_nm = (!object_in_data.has("data_nm")) ? "" : object_in_data.getString("data_nm");
			data_tradt = (!object_in_data.has("data_tradt")) ? "" : object_in_data.getString("data_tradt");
			data_idno = (!object_in_data.has("data_idno")) ? "" : object_in_data.getString("data_idno");
		}
		// 讀取pa.pabatpay
		strSelect_bat = "PATRADT, PAYIDNO, SALNUM, PAYCARD, PAYCODE, PAUPBAS, PAYAMT, PADUTYID, PAYUPDAT, PAYFLAG, UPMASTDT, HID, "
				+ tmpZbatpay + " ZPABATPAY ";
		sql = "select " + strSelect_bat + " from " + this.tBatPay
				+ " where HID=? and PATRADT=? and PAYIDNO=? and PAYCODE=? with ur";

		params.add(hid);
		params.add(data_tradt);
		params.add(data_idno);
		params.add(data_val);

		log.info(me + "  " + sql + "...params:" + params.toString());

		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());
			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					pabatpay = new Pabatpay();
					PropertyUtils.copyProperties(pabatpay, data);
				}
			}

			log.info(me + "  ...********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}
		return pabatpay;
	}

	// 依條件,取得pa.panhical 同年度上期累加獎金
	public int getJackpotByKey(String idtno, String yy, String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getJackpotByKey(),userid = " + userid;
		log.info(me + " idtno = " + idtno + " yy = " + yy + " hid = " + hid);
		int rtn = 0;
		ResultSet rs = null;
		long starttime = 0;
		ArrayList<Object> params = new ArrayList<Object>();
		starttime = System.currentTimeMillis();

		// 讀取pa.panhical
		sql = "select coalesce(SUM(BONUSAMT),0) AS BONUSAMT " + " from " + this.tNhiCal + " where HID=? "
				+ " and payidno=? " + " and year(PAPAYDT)=? with ur";
		params.add(hid);
		params.add(idtno);
		params.add(yy);

		log.info(me + "  " + sql + "...params:" + params.toString());

		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}

			rs = da.query(sql, params);
			while (rs.next()) {
				rtn = rs.getInt("BONUSAMT");
			}
			log.info(me + "...rtn:" + rtn);

			log.info(me + "  ...********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, rs, connFlg);
		}
		return rtn;
	}

	// 依篩選條件,取得pa.pabaspsn集合 item:1個人明細,2篩選
	public JSONArray getBasPsnList(String code_data, String item, String userid, String hid, VGHDataAccess da)
			throws Exception {
		String me = getClass().getName() + ".getBasPsnList(),userid = " + userid;
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
		String lvefg = "";
		String expfsp = "";
		String csflag = "";
		String paret = "";
		String cardno = "";
		String idtno = "";
		String namec = "";
		String empno = "";
		String tracod = "";
		String inflg = "";
		String[] patradt = null;
		String[] payrank = null;
		String[] paytitle = null;
		String[] paydept = null;
		String[] expfkey1 = null;
		String[] expfkey2 = null;
		String[] expfkey3 = null;
		String[] insuamt = null;
		String subsql = "";
		String strSelect = "";
		Pabaspsn baspsnObj = null;
		int pfbas = 0;
		int pfdat = 0;
		String pfkey = "";
		int i = 0;
		long starttime = 0;
		ArrayList<Object> params = new ArrayList<Object>();

		if (code_data != null && !code_data.equals("")) {
			object_code_data = JSONObject.fromObject(code_data);
			lvefg = (!object_code_data.has("lvefg")) ? "" : object_code_data.getString("lvefg");
			csflag = (!object_code_data.has("csflag")) ? "" : object_code_data.getString("csflag");
			expfsp = (!object_code_data.has("expfsp")) ? "" : object_code_data.getString("expfsp");
			tracod = (!object_code_data.has("tracod")) ? "" : object_code_data.getString("tracod");
			inflg = (!object_code_data.has("inflg")) ? "" : object_code_data.getString("inflg");
			paret = (!object_code_data.has("paret")) ? "" : object_code_data.getString("paret");
			cardno = (!object_code_data.has("cardno")) ? "" : object_code_data.getString("cardno");
			idtno = (!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			namec = (!object_code_data.has("namec")) ? "" : object_code_data.getString("namec");
			empno = (!object_code_data.has("empno")) ? "" : object_code_data.getString("empno");
			patradt = (!object_code_data.has("patradt")) ? null
					: ("".equals(object_code_data.getString("patradt")) ? null
							: object_code_data.getString("patradt").split(","));
			payrank = (!object_code_data.has("payrank")) ? null
					: ("".equals(object_code_data.getString("payrank")) ? null
							: object_code_data.getString("payrank").split(","));
			paytitle = (!object_code_data.has("paytitle")) ? null
					: ("".equals(object_code_data.getString("paytitle")) ? null
							: object_code_data.getString("paytitle").split(","));
			paydept = (!object_code_data.has("paydept")) ? null
					: ("".equals(object_code_data.getString("paydept")) ? null
							: object_code_data.getString("paydept").split(","));
			expfkey1 = (!object_code_data.has("expfkey1")) ? null
					: ("".equals(object_code_data.getString("expfkey1")) ? null
							: object_code_data.getString("expfkey1").split(","));
			/*expfkey2 = (!object_code_data.has("expfkey2")) ? null
					: ("".equals(object_code_data.getString("expfkey2")) ? null
							: object_code_data.getString("expfkey2").split(","));
			expfkey3 = (!object_code_data.has("expfkey3")) ? null
					: ("".equals(object_code_data.getString("expfkey3")) ? null
							: object_code_data.getString("expfkey3").split(","));*/
			insuamt = (!object_code_data.has("insuamt")) ? null
					: ("".equals(object_code_data.getString("insuamt")) ? null
							: object_code_data.getString("insuamt").split(","));
		}
		// 讀取pa.pabaspsn
		strSelect = "PAYLVEFG, A.PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, PAYEMPDT, PAYSGNDT, PAYRSGDT, PAYBKID, PAYACNO, "
				+ "PAYGEDNO, COALESCE(A.PAYINFLG,0) as PAYINFLG, COALESCE(A.PAYDPNO,'0') as PAYDPNO, PAYPSW, PAYTITLE, PAYRNK2N, COALESCE(A.PAYRANK3,0) as PAYRANK3, COALESCE(A.PAYDEGR2,0) as PAYDEGR2, "
				+ "COALESCE(A.PAYDEGRE,0) as PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, COALESCE(A.PAYPFDOC,0) as PAYPFDOC, PACSFLAG, "
				+ "COALESCE(A.PAYPFSP1,0) as PAYPFSP1, COALESCE(A.PAYPFSP2,0) as PAYPFSP2, COALESCE(A.PAYPFSP3,0) as PAYPFSP3, A.PATRACOD, "
				+ "PATRADT, COALESCE(A.PAYTAXNO,0) as PAYTAXNO, A.PAYUPDID, A.PAYUPDTM, PAYEXPFKEY1, PAYEXPFKEY2, PAYEXPFKEY3, COALESCE(A.PAYRANK4,0) as PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, "
				+ "INSUAMT, PARETENTION, A.HID, " + tmpZbaspsn + ","
				+ "B.PADEPNMC,B.PACSTNO,C.PAYTITLP,C.PATITSUB,D.RANKNAME,E.RNK4NAME,F.TRANAME ";
		sql = "select " + strSelect + " from " + this.tBasPsn + " A " + " left outer join " + this.tDept
				+ " B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID " + " left outer join " + this.tTitle
				+ " C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID " + " left outer join " + this.tRankCode
				+ " D on A.PAYRANK=D.RANKCODE " + " left outer join " + this.tRnk4Tbl + " E on A.PAYRANK4=E.RNK4KEY "
				+ " left outer join " + this.tTraCode + " F on A.PATRACOD=F.TRACODE " + " where A.HID=? ";
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
		} else if (!"".equals(empno)) {// 職號
			subsql += " and PAYEMPNO like ? ";
			params.add(empno + "%");
		}
		if (!"".equals(lvefg)) {// 離職否(Y/N)
			subsql += " and PAYLVEFG=? ";
			params.add(lvefg);
		}
		if (!"".equals(paret)) {// 留職留薪,支領專2(Y/N)
			subsql += " and PARETENTION=? ";
			params.add(paret);
		}
		if (!"".equals(tracod)) {// 異動原因
			subsql += " and PATRACOD=? ";
			params.add(tracod);
		}
		if (!"".equals(csflag)) {// 支領工作獎金(Y/N)
			subsql += " and PACSFLAG=? ";
			params.add(csflag);
		}
		if (!"".equals(inflg)) {// 保險補助比例
			subsql += " and PAYINFLG<>0 ";
		}
		if (!"".equals(expfsp)) {// 額外點數1 2 3有值皆要列入
			subsql += " and (PAYPFSP1<>0 or PAYPFSP2<>0 or PAYPFSP3<>0) ";
		}
		if (!(patradt == null || (patradt != null && patradt.length == 0))) { // 異動日期
			subsql += " and PATRADT in ( ";
			for (i = 0; i < patradt.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(patradt[i]);
			}
			subsql += ")";
		}
		if (!(payrank == null || (payrank != null && payrank.length == 0))) { // 階級
			subsql += " and PAYRANK in ( ";
			for (i = 0; i < payrank.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(payrank[i]);
			}
			subsql += ")";
		}
		if (!(paytitle == null || (paytitle != null && paytitle.length == 0))) { // 職稱
			subsql += " and PAYTITLE in ( ";
			for (i = 0; i < paytitle.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(paytitle[i]);
			}
			subsql += ")";
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
		/*if (!(expfkey2 == null || (expfkey2 != null && expfkey2.length == 0) || (expfkey2.length == 1 && expfkey2[0].equals("0"))) ) { // 額外點數2
			subsql += " and payexpfkey2 in ( ";
			for (i = 0; i < expfkey2.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(expfkey2[i]);
			}
			subsql += ")";
		}
		if (!(expfkey3 == null || (expfkey3 != null && expfkey3.length == 0) || (expfkey3.length == 1 && expfkey3[0].equals("0")))) { // 額外點數3
			subsql += " and payexpfkey3 in ( ";
			for (i = 0; i < expfkey3.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(expfkey3[i]);
			}
			subsql += ")";
		}*/
		if (!(insuamt == null || (insuamt != null && insuamt.length == 0))) { // 保險級距
			subsql += " and insuamt in ( ";
			for (i = 0; i < insuamt.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(insuamt[i]);
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
			start = new Date();
			if ("2".equals(item) & hid.matches("1A0|2A0")) {
				cDAO = new CommDAO(da, hid);
				CspfMap = cDAO.getCsPfByAll(da, hid);
			}

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
					jsonOne.put("pacstno", StringUtil.nullToString(data.get("pacstno")));
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					if ("2".equals(item)) {
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
						jsonOne.put("rank3name", PaCommUtil.getRank3Nm((Integer) data.get("payrank3")));
						jsonOne.put("payinflg", PaCommUtil.getInflgNm((Integer) data.get("payinflg")));
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
						// 工作獎金=Y,找基本績分 俸級績分
						pfbas = 0;
						pfdat = 0;
						pfkey = "";
						if ("Y".equals(baspsnObj.getPacsflag()) & hid.matches("1A0|2A0")) {

							cspfRtn = cDAO.getCsPfByMap(baspsnObj.getPayrank3(), baspsnObj.getPaytitle(),
									baspsnObj.getPayrank(), baspsnObj.getPayrnk2n(), baspsnObj.getPaydegr2(),
									"9999-12-31", hid, CspfMap, da);
							if (cspfRtn != null) {
								pfkey = (String) cspfRtn.get("pfkey");
								pfbas = (Integer) cspfRtn.get("pfbas");
								pfdat = (Integer) cspfRtn.get("pfdat");
							}
						}
						jsonOne.put("pfbas", pfbas);
						jsonOne.put("pfdat", pfdat);
					}
					jsonRtn.add(jsonOne);
				}
			}
			end = new Date();
			log.info(me + "  ...********* dataaccess:" + (end.getTime() - start.getTime()) + ", 共花費："
					+ (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}

		return jsonRtn;
	}

	// 依篩選條件,取得所得稅集合
	// typ:1轉帳 2現金 3轉帳+現金
	public JSONArray getTaxViewList(String code_data, String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getTaxViewList(),userid = " + userid;
		log.info(me + " code_data = " + code_data + " hid = " + hid);

		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_code_data = null;
		Date start = new Date();
		Date end = new Date();
		String cardno = "";
		String idtno = "";
		String namec = "";
		String typ = "";
		String paycitation = "";
		String[] payear = null;
		String[] paymonth = null;
		String[] payformat = null;
		String payformats = "";
		String subsqlA = "";
		String subsqlB = "";
		String strSelect = "";
		boolean blnTran = true;
		int i = 0;
		ArrayList<Object> paramA = new ArrayList<Object>();
		ArrayList<Object> paramB = new ArrayList<Object>();
		ArrayList<Object> param = new ArrayList<Object>();
		if (code_data != null && !code_data.equals("")) {
			object_code_data = JSONObject.fromObject(code_data);
			cardno = (!object_code_data.has("cardno")) ? "" : object_code_data.getString("cardno");
			idtno = (!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			namec = (!object_code_data.has("namec")) ? "" : object_code_data.getString("namec");
			typ = (!object_code_data.has("typ")) ? "" : object_code_data.getString("typ");
			paycitation = (!object_code_data.has("paycitation")) ? "" : object_code_data.getString("paycitation");

			payear = (!object_code_data.has("payear")) ? null
					: ("".equals(object_code_data.getString("payear")) ? null
							: object_code_data.getString("payear").split(","));
			paymonth = (!object_code_data.has("paymonth")) ? null
					: ("".equals(object_code_data.getString("paymonth")) ? null
							: object_code_data.getString("paymonth").split(","));
			payformat = (!object_code_data.has("payformat")) ? null
					: ("".equals(object_code_data.getString("payformat")) ? null
							: object_code_data.getString("payformat").split(","));

		}
		paramA.add(hid);
		paramB.add(hid);
		if (!"".equals(cardno)) {// 卡號
			subsqlA += " and PAYCARD=? ";
			subsqlB += " and PAYCARD=? ";
			paramA.add(cardno);
			paramB.add(cardno);
		} else if (!"".equals(idtno)) {// 身分證
			subsqlA += " and A.PAYIDNO=? ";
			subsqlB += " and A.PAYIDNO=? ";
			paramA.add(idtno);
			paramB.add(idtno);
		} else if (!"".equals(namec)) {// 姓名
			subsqlA += " and PAYNAMEC like ? ";
			subsqlB += " and PAYNAMEC like ? ";
			paramA.add(namec + "%");
			paramB.add(namec + "%");
		}
		if (!(payear == null || (payear != null && payear.length == 0))) { // 年
			subsqlA += " and a.PAYEAR in ( ";
			subsqlB += " and a.PAYEAR in ( ";
			for (i = 0; i < payear.length; i++) {
				if (i != 0) {
					subsqlA += ",";
					subsqlB += ",";
				}
				subsqlA += "?";
				subsqlB += "?";
				paramA.add(payear[i]);
				paramB.add(payear[i]);
			}
			subsqlA += ")";
			subsqlB += ")";
		}
		if (!(paymonth == null || (paymonth != null && paymonth.length == 0))) { // 月
			subsqlA += " and PAYMONTH in ( ";
			subsqlB += " and PAYMONTH in ( ";
			for (i = 0; i < paymonth.length; i++) {
				if (i != 0) {
					subsqlA += ",";
					subsqlB += ",";
				}
				subsqlA += "?";
				subsqlB += "?";
				paramA.add(paymonth[i]);
				paramB.add(paymonth[i]);
			}
			subsqlA += ")";
			subsqlB += ")";
		}

		if (!"".equals(paycitation)) {// 傳票製票號
			subsqlB += " and PAYCITATION = ? ";
			paramB.add(paycitation);
			blnTran = false; // 現金
		}

		if (!(payformat == null || (payformat != null && payformat.length == 0))) { // 格式
			subsqlB += " and PAYFORMAT in ( ";
			for (i = 0; i < payformat.length; i++) {
				if (i != 0)
					subsqlB += ",";
				subsqlB += "?";
				paramB.add(payformat[i]);
				payformats += payformat[i] + ";";
			}
			subsqlB += ")";
			// if ("50".equals(payformat[i])) blnTran=false; //現金
			// else typ = "2";
			if (!payformats.contains("50")) {
				typ = "2";
			}
		}

		strSelect = " PAYTAXPAY, PAYTAXDEDUCT, a.PAYUPDID, a.PAYUPDTM, a.HID,PACSTNO,PADEPNMC,PAYCARD, PAYNAMEC, B.PAYDEPT, PAYIDFLG, PAYBEGIN, PAYEND, PAYSEREAMT,PAYEMPNO, PAYSERNO, PAYSERNO9A, PAYSERNO9B, PAYSERNO92, PAYSERNO91,  PAYSERNO51 ";
		// 1轉帳 2現金 3轉帳+現金
		sql = "";
		if (typ.matches("1|3") && blnTran) {
			sql = "select a.PAYIDNO, a.PAYEAR, PAYMONTH,  0 AS PAYCATG,  '轉帳' AS PAYITEM,'50' AS PAYFORMAT,'         ' AS PAYCITATION,'無' CATGNAME,"
					+ strSelect + " from " + this.tTaxTran + " A " + " left outer join " + this.tTaxPsn
					+ " B on a.payidno=b.payidno AND a.hid=b.hid AND a.payear=b.payear " + " left outer join "
					+ this.tDept + " D on B.PAYDEPT=D.PADEPCOD AND B.HID=D.HID " + " where A.HID=? " + subsqlA;
			for (int j = 0; j < paramA.size(); j++) {
				param.add(paramA.get(j));
			}
		}
		if ("3".equals(typ)) {
			sql += " union all ";
		}
		if (typ.matches("2|3")) {
			sql += "select a.PAYIDNO, a.PAYEAR, PAYMONTH, a.PAYCATG, '人工' AS PAYITEM,payformat,paycitation,CATGNAME,"
					+ strSelect + " from " + this.tTaxCash + " A " + " left outer join " + this.tTaxPsn
					+ " B on a.payidno=b.payidno AND a.hid=b.hid AND a.payear=b.payear " + " left outer join "
					+ this.tCatgTbl + " C on a.PAYCATG=c.PAYCATG " + " left outer join " + this.tDept
					+ " D on B.PAYDEPT=D.PADEPCOD AND B.HID=D.HID " + " where A.HID=? " + subsqlB;
			for (int j = 0; j < paramB.size(); j++) {
				param.add(paramB.get(j));
			}
		}

		sql += " order by 1,2 desc ,3 desc,4 with ur";

		log.info(me + "  " + sql + "...params:" + param.toString());

		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}

			start = new Date();
			result = da.arraylistbyQuery(sql, param, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					jsonOne = new JSONObject();
					jsonOne.put("payear", StringUtil.nullToString(data.get("payear")));//
					jsonOne.put("paymonth", StringUtil.nullToString(data.get("paymonth")));//
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));//
					jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));//
					jsonOne.put("paynamec", StringUtil.nullToString(data.get("paynamec")));//
					jsonOne.put("payempno", StringUtil.nullToString(data.get("payempno")));
					jsonOne.put("pacstno", StringUtil.nullToString(data.get("pacstno")));
					jsonOne.put("padepnmc", StringUtil.nullToString(data.get("padepnmc")));//
					jsonOne.put("payitem", StringUtil.nullToString(data.get("payitem")));// 性質
					jsonOne.put("payformat", StringUtil.nullToString(data.get("payformat")));// 格式
					jsonOne.put("catgname", StringUtil.nullToString(data.get("catgname")));// 轉帳項目
					jsonOne.put("paycitation", StringUtil.nullToString(data.get("paycitation")));// 傳票製票號
					jsonOne.put("paybegin", StringUtil.nullToString(data.get("paybegin")));// 起日
					jsonOne.put("payend", StringUtil.nullToString(data.get("payend")));// 訖日
					jsonOne.put("paytaxpay", StringUtil.nullToString(data.get("paytaxpay")));// 應稅所得
					jsonOne.put("paytaxdeduct", StringUtil.nullToString(data.get("paytaxdeduct")));// 己扣
					jsonOne.put("paysereamt", StringUtil.nullToString(data.get("paysereamt")));// 自繳退休金
					jsonOne.put("payserno", StringUtil.nullToString(data.get("payserno")));
					jsonOne.put("payserno9a", StringUtil.nullToString(data.get("payserno9a")));
					jsonOne.put("payserno9b", StringUtil.nullToString(data.get("payserno9b")));
					jsonOne.put("payserno91", StringUtil.nullToString(data.get("payserno91")));
					jsonOne.put("payserno92", StringUtil.nullToString(data.get("payserno92")));
					jsonOne.put("payserno51", StringUtil.nullToString(data.get("payserno51")));
					jsonOne.put("payidflg", StringUtil.nullToString(data.get("payidflg")));// 身分證檢查碼註記
					jsonOne.put("payupdid", StringUtil.nullToString(data.get("payupdid")));
					jsonOne.put("payupdtm", StringUtil.nullToString(data.get("payupdtm")));
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					jsonOne.put("paydept", StringUtil.nullToString(data.get("paydept")));

					jsonRtn.add(jsonOne);
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

		return jsonRtn;
	}

	// 依年,傳票製票號,身分證,卡號,姓名,取得所得稅個人明細(pataxpsn)
	public JSONArray getTaxTaskList(String code_data, String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getTaxTaskList(),userid = " + userid;
		log.info(me + " code_data = " + code_data + " hid = " + hid);

		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_code_data = null;
		Date start = new Date();
		Date end = new Date();
		String cardno = "";
		String idtno = "";
		String namec = "";
		String paycitation = "";
		String payear[] = null;

		String subsqlA = "";
		String subsqlB = "";
		String strSelect = "";
		int i = 0;
		ArrayList<Object> paramA = new ArrayList<Object>();
		ArrayList<Object> param = new ArrayList<Object>();
		if (code_data != null && !code_data.equals("")) {
			object_code_data = JSONObject.fromObject(code_data);
			cardno = (!object_code_data.has("cardno")) ? "" : object_code_data.getString("cardno");
			idtno = (!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			namec = (!object_code_data.has("namec")) ? "" : object_code_data.getString("namec");
			paycitation = (!object_code_data.has("paycitation")) ? "" : object_code_data.getString("paycitation");
			payear = (!object_code_data.has("payear")) ? null
					: ("".equals(object_code_data.getString("payear")) ? null
							: object_code_data.getString("payear").split(","));
			;
		}
		paramA.add(hid);
		if (!"".equals(cardno)) {// 卡號
			subsqlA += " and PAYCARD=? ";
			paramA.add(cardno);
		} else if (!"".equals(idtno)) {// 身分證
			subsqlA += " and A.PAYIDNO=? ";
			paramA.add(idtno);
		} else if (!"".equals(namec)) {// 姓名
			subsqlA += " and PAYNAMEC like ? ";
			paramA.add(namec + "%");
		}
		if (!(payear == null || (payear != null && payear.length == 0))) { // 年
			subsqlA += " and a.PAYEAR in ( ";
			for (i = 0; i < payear.length; i++) {
				if (i != 0) {
					subsqlA += ",";
				}
				subsqlA += "?";
				paramA.add(payear[i]);
			}
			subsqlA += ")";
		}

		sql = "select PACSTNO,PADEPNMC,a.PAYIDNO, a.PAYEAR, PAYCARD, PAYNAMEC, PAYDEPT, PAYIDFLG, PAYBEGIN, PAYEND, PAYSERNO, a.PAYUPDID, PAYUPDTM, PAYSERNO9A, PAYSERNO9B, PAYSEREAMT, PAYSERNO92, PAYSERNO91, a.HID, PAYEMPNO, PAYSERNO51 "
				+ " from " + this.tTaxPsn + " A " + " left outer join " + this.tDept
				+ " D on A.PAYDEPT=D.PADEPCOD AND A.HID=D.HID ";
		if (!"".equals(paycitation)) {// 傳票製票號
			sql += "INNER JOIN (SELECT hid,payidno,payear FROM " + this.tTaxCash
					+ " WHERE hid=? AND PAYCITATION=?) b ON a.hid=b.hid AND a.payidno=b.payidno and a.payear=b.payear ";
			param.add(hid);
			param.add(paycitation);
		}
		sql += " where A.HID=? " + subsqlA + " order by payear desc,paycard,a.payidno with ur";
		for (int j = 0; j < paramA.size(); j++) {
			param.add(paramA.get(j));
		}
		log.info(me + "  " + sql + "...params:" + param.toString());

		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}

			start = new Date();
			result = da.arraylistbyQuery(sql, param, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					jsonOne = new JSONObject();
					jsonOne.put("payear", StringUtil.nullToString(data.get("payear")));//
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));//
					jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));//
					jsonOne.put("payempno", StringUtil.nullToString(data.get("payempno")));
					jsonOne.put("paynamec", StringUtil.nullToString(data.get("paynamec")));//
					jsonOne.put("paydept", StringUtil.nullToString(data.get("paydept")));
					jsonOne.put("payidflg", StringUtil.nullToString(data.get("payidflg")));// 身分證檢查碼註記
					jsonOne.put("paybegin", StringUtil.nullToString(data.get("paybegin")));// 起日
					jsonOne.put("payend", StringUtil.nullToString(data.get("payend")));// 訖日

					jsonOne.put("pacstno", StringUtil.nullToString(data.get("pacstno")));
					jsonOne.put("padepnmc", StringUtil.nullToString(data.get("padepnmc")));//
					jsonOne.put("paysereamt", StringUtil.nullToString(data.get("paysereamt")));// 自繳退休金
					jsonOne.put("payserno", StringUtil.nullToString(data.get("payserno")));
					jsonOne.put("payserno9a", StringUtil.nullToString(data.get("payserno9a")));
					jsonOne.put("payserno9b", StringUtil.nullToString(data.get("payserno9b")));
					jsonOne.put("payserno91", StringUtil.nullToString(data.get("payserno91")));
					jsonOne.put("payserno92", StringUtil.nullToString(data.get("payserno92")));
					jsonOne.put("payserno51", StringUtil.nullToString(data.get("payserno51")));

					jsonOne.put("payupdid", StringUtil.nullToString(data.get("payupdid")));
					jsonOne.put("payupdtm", StringUtil.nullToString(data.get("payupdtm")));
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));

					jsonRtn.add(jsonOne);
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

		return jsonRtn;
	}

	// 依篩選條件,取得所得稅彙總
	public JSONArray getTaxSumList(String code_data, String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getTaxSumList(),userid = " + userid;
		log.info(me + " code_data = " + code_data + " hid = " + hid);

		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_code_data = null;
		Date start = new Date();
		Date end = new Date();
		String paycitation = "";
		String[] payear = null;
		String[] paymonth = null;
		String[] payformat = null;
		String typ = "";
		String subsqlA = "";
		String subsqlB = "";
		String strSelect = "";
		int i = 0;
		ArrayList<Object> paramA = new ArrayList<Object>();
		ArrayList<Object> paramB = new ArrayList<Object>();
		ArrayList<Object> param = new ArrayList<Object>();
		if (code_data != null && !code_data.equals("")) {
			object_code_data = JSONObject.fromObject(code_data);
			typ = (!object_code_data.has("typ")) ? "" : object_code_data.getString("typ");
			paycitation = (!object_code_data.has("paycitation")) ? "" : object_code_data.getString("paycitation");
			payear = (!object_code_data.has("payear")) ? null
					: ("".equals(object_code_data.getString("payear")) ? null
							: object_code_data.getString("payear").split(","));
			paymonth = (!object_code_data.has("paymonth")) ? null
					: ("".equals(object_code_data.getString("paymonth")) ? null
							: object_code_data.getString("paymonth").split(","));
			payformat = (!object_code_data.has("payformat")) ? null
					: ("".equals(object_code_data.getString("payformat")) ? null
							: object_code_data.getString("payformat").split(","));

		}
		paramA.add(hid);
		paramB.add(hid);
		if (!(payear == null || (payear != null && payear.length == 0))) { // 年
			subsqlA += " and a.PAYEAR in ( ";
			subsqlB += " and a.PAYEAR in ( ";
			for (i = 0; i < payear.length; i++) {
				if (i != 0) {
					subsqlA += ",";
					subsqlB += ",";
				}
				subsqlA += "?";
				subsqlB += "?";
				paramA.add(payear[i]);
				paramB.add(payear[i]);
			}
			subsqlA += ")";
			subsqlB += ")";
		}
		if (!(paymonth == null || (paymonth != null && paymonth.length == 0))) { // 月
			subsqlA += " and PAYMONTH in ( ";
			subsqlB += " and PAYMONTH in ( ";
			for (i = 0; i < paymonth.length; i++) {
				if (i != 0) {
					subsqlA += ",";
					subsqlB += ",";
				}
				subsqlA += "?";
				subsqlB += "?";
				paramA.add(paymonth[i]);
				paramB.add(paymonth[i]);
			}
			subsqlA += ")";
			subsqlB += ")";
		}

		if (!"".equals(paycitation)) {// 傳票製票號
			subsqlB += " and PAYCITATION = ? ";
			paramB.add(paycitation);

		}

		if (!(payformat == null || (payformat != null && payformat.length == 0))) { // 格式
			// 轉帳所得稅都是50,現金所得稅格式都有
			subsqlB += " and PAYFORMAT in ( ";
			for (i = 0; i < payformat.length; i++) {
				if (i != 0)
					subsqlB += ",";
				subsqlB += "?";
				paramB.add(payformat[i]);
				if (!payformat[i].equals("50")) {
					typ = "2";
				}
			}
			subsqlB += ")";
		}

		strSelect = " payear,a.hid, SUM(PAYTAXPAY) AS TAXPAY, SUM(PAYTAXDEDUCT) AS TAXDEDUCT, COUNT(*) AS TAXPER ";
		// 1轉帳 2現金 3轉帳+現金
		sql = "";
		if (typ.matches("1|3") && ("".equals(paycitation))) {
			sql = "select PAYMONTH,PAYCATG,'無' CATGNAME, '50' AS PAYFORMAT," + strSelect + " from " + this.tTaxTran
					+ " A " + " where A.HID=? " + subsqlA;
			for (int j = 0; j < paramA.size(); j++) {
				param.add(paramA.get(j));
			}
			sql += " GROUP BY a.hid,payear,PAYMONTH,PAYCATG ";
		}
		if ("3".equals(typ)) {
			sql += " union all ";
		}
		if (typ.matches("2|3") || (!"".equals(paycitation))) {
			sql += "select PAYMONTH,a.PAYCATG,CATGNAME,PAYFORMAT," + strSelect + " from " + this.tTaxCash + " A "
					+ " left outer join " + this.tCatgTbl + " B on a.PAYCATG=B.PAYCATG " + " where A.HID=? " + subsqlB;
			for (int j = 0; j < paramB.size(); j++) {
				param.add(paramB.get(j));
			}
			sql += " GROUP BY a.hid,payear,PAYMONTH,PAYFORMAT,CATGNAME,a.PAYCATG ";
		}

		sql += " ORDER BY 1,2,3 " + " with ur";

		log.info(me + "  " + sql + "...params:" + param.toString());

		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}

			start = new Date();
			result = da.arraylistbyQuery(sql, param, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					jsonOne = new JSONObject();
					jsonOne.put("paycatg", StringUtil.nullToString(data.get("paycatg")));// 轉帳項目代碼
					jsonOne.put("payformat", StringUtil.nullToString(data.get("payformat")));// 格式
					jsonOne.put("catgname", StringUtil.nullToString(data.get("catgname")));// 轉帳中文
					jsonOne.put("payear", StringUtil.nullToString(data.get("payear")));// 年
					jsonOne.put("paymonth", StringUtil.nullToString(data.get("paymonth")));// 月
					jsonOne.put("taxpay", StringUtil.nullToString(data.get("taxpay")));// 應稅所得
					jsonOne.put("taxdeduct", StringUtil.nullToString(data.get("taxdeduct")));// 己扣
					jsonOne.put("taxper", StringUtil.nullToString(data.get("taxper")));// 人數
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));

					jsonRtn.add(jsonOne);
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

		return jsonRtn;
	}

	// 依篩選條件,取得所得稅統計_限定格式50
	// 2026.3.24加職稱
	public JSONArray getTaxStatList(String code_data, String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getTaxStatList(),userid = " + userid;
		log.info(me + " code_data = " + code_data + " hid = " + hid);

		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_code_data = null;
		Date start = new Date();
		Date end = new Date();
		String cardno = "";
		String idtno = "";
		String namec = "";
		String tmpsqlA = "";
		String tmpsqlB = "";
		String[] payear = null;
		String[] paymonth = null;
		String[] paydept = null;
		String subsqlA = "";

		int i = 0;

		ArrayList<Object> param = new ArrayList<Object>();
		if (code_data != null && !code_data.equals("")) {
			object_code_data = JSONObject.fromObject(code_data);
			cardno = (!object_code_data.has("cardno")) ? "" : object_code_data.getString("cardno");
			idtno = (!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			namec = (!object_code_data.has("namec")) ? "" : object_code_data.getString("namec");
			payear = (!object_code_data.has("payear")) ? null
					: ("".equals(object_code_data.getString("payear")) ? null
							: object_code_data.getString("payear").split(","));
			paymonth = (!object_code_data.has("paymonth")) ? null
					: ("".equals(object_code_data.getString("paymonth")) ? null
							: object_code_data.getString("paymonth").split(","));
			paydept = (!object_code_data.has("paydept")) ? null
					: ("".equals(object_code_data.getString("paydept")) ? null
							: object_code_data.getString("paydept").split(","));

		}
		if (!(paymonth == null || (paymonth != null && paymonth.length == 0))) { // 月
			tmpsqlA += " and PAYMONTH in ( ";
			for (i = 0; i < paymonth.length; i++) {
				if (i != 0) {
					tmpsqlA += ",";
				}
				tmpsqlA += "?";
				param.add(paymonth[i]);
			}
			tmpsqlB += " where PAYMONTH in ( ";
			for (i = 0; i < paymonth.length; i++) {
				if (i != 0) {
					tmpsqlB += ",";
				}
				tmpsqlB += "?";
				param.add(paymonth[i]);
			}
			tmpsqlA += ")";
			tmpsqlB += ")";
		} else {
			tmpsqlA = "";
			tmpsqlB = "";
		}

		param.add(hid);

		if (!"".equals(cardno)) {// 卡號
			subsqlA += " and A.PAYCARD=? ";
			param.add(cardno);
		} else if (!"".equals(idtno)) {// 身分證
			subsqlA += " and A.PAYIDNO=? ";
			param.add(idtno);
		} else if (!"".equals(namec)) {// 姓名
			subsqlA += " and A.PAYNAMEC like ? ";
			param.add(namec + "%");
		}
		if (!(payear == null || (payear != null && payear.length == 0))) { // 年
			subsqlA += " and a.PAYEAR in ( ";
			for (i = 0; i < payear.length; i++) {
				if (i != 0) {
					subsqlA += ",";
				}
				subsqlA += "?";
				param.add(payear[i]);
			}
			subsqlA += ")";
		}
		if (!(paydept == null || (paydept != null && paydept.length == 0))) { // 單位
			subsqlA += " and a.paydept in ( ";
			for (i = 0; i < paydept.length; i++) {
				if (i != 0) {
					subsqlA += ",";
				}
				subsqlA += "?";
				param.add(paydept[i]);
			}
			subsqlA += ")";
		}
		// 找該年員工格式50總所得,<>0列出
		sql = "select a.hid,a.payear,a.payidno,a.paycard,a.paynamec,a.paydept,COALESCE(e.PADEPNMC,d.PADEPNMC)PADEPNMC,a.paydept,COALESCE(b.PAY,0)+COALESCE(c.PAY,0) paytaxpay,COALESCE(b.DEDUCT,0)+COALESCE(c.DEDUCT,0) paytaxdeduct,D.PAYTITLP "
				+ "from " + tTaxPsn + " A "
				+ " left outer join (select PAYEAR,PAYIDNO,hid, COALESCE(sum(PAYTAXPAY),0) PAY, COALESCE(sum(PAYTAXDEDUCT),0) DEDUCT "
				+ "from " + tTaxCash + " where PAYFORMAT = '50' " + tmpsqlA
				+ " GROUP BY PAYEAR,PAYIDNO,hid) b ON a.payidno=b.payidno AND a.hid=b.hid AND a.payear=b.payear "
				+ " left outer join (select PAYEAR,PAYIDNO,hid, COALESCE(sum(PAYTAXPAY),0) PAY, COALESCE(sum(PAYTAXDEDUCT),0) DEDUCT "
				+ "from " + tTaxTran + tmpsqlB
				+ " GROUP BY PAYEAR,PAYIDNO,hid) c ON a.payidno=c.payidno AND a.hid=c.hid AND a.payear=c.payear "
				+ " left outer join (select PAYIDNO,PAYTITLE,PAYTITLP,PAYDEPT,PADEPNMC,A.HID " + "from " + tBasPsn
				+ " A " + "left outer join " + tTitle + " B on A.PAYTITLE=B.PAYTITLK and A.HID=B.HID "
				+ "left outer join " + tDept
				+ " C on A.PAYDEPT =C.PADEPCOD and A.HID=C.HID) d ON A.PAYIDNO=D.PAYIDNO AND A.HID=D.HID "
				+ " left outer join " + tDept + " e on A.PAYDEPT =e.PADEPCOD and A.HID=e.HID "
				+ " where a.HID=? and (COALESCE(b.PAY,0)+COALESCE(c.PAY,0)<>0 or COALESCE(b.DEDUCT,0)+COALESCE(c.DEDUCT,0) <>0) ";

		sql += subsqlA + " order by a.hid,a.payear,a.payidno with ur";

		log.info(me + "  " + sql + "...params:" + param.toString());

		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}

			start = new Date();
			result = da.arraylistbyQuery(sql, param, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					jsonOne = new JSONObject();
					jsonOne.put("payear", StringUtil.nullToString(data.get("payear")));// 年
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));// 身分證字號
					jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));// 卡號
					jsonOne.put("paynamec", StringUtil.trimWideEmpty(StringUtil.nullToString(data.get("paynamec"))));// 姓名
					jsonOne.put("padepnmc", StringUtil.nullToString(data.get("padepnmc")));// 單位 PADEPNMC
					jsonOne.put("paytitlp", StringUtil.nullToString(data.get("paytitlp")));// 職稱
					jsonOne.put("taxpay", StringUtil.nullToString(data.get("paytaxpay")));// 應稅所得
					jsonOne.put("taxdeduct", StringUtil.nullToString(data.get("paytaxdeduct")));// 己扣
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					jsonRtn.add(jsonOne);
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

		return jsonRtn;
	}

	// 依篩選條件,取得pa.PAREPDETAIL
	public JSONArray getRrtDtlList(String in_data, String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getRrtDtlList(),userid = " + userid;
		log.info(me + " in_data = " + in_data + " hid = " + hid);
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_in_data = null;
		String selEn = "";
		String selCode = "";
		String selNm = "";
		String patradt = "";
		int salnum = 0;
		String strSelect = "";
		long starttime = 0;
		int l_paynet = 0;
		int l_paytotis = 0;
		// 1:月報:Prt_M selCode:selNm:selEn
		if (in_data != null && !in_data.equals("")) {
			object_in_data = JSONObject.fromObject(in_data);
			selEn = (!object_in_data.has("selEn")) ? "" : object_in_data.getString("selEn");
			selCode = (!object_in_data.has("selCode")) ? "" : object_in_data.getString("selCode");
			selNm = (!object_in_data.has("selNm")) ? "" : object_in_data.getString("selNm");
			patradt = (!object_in_data.has("patradt")) ? "" : object_in_data.getString("patradt");
			salnum = (!object_in_data.has("salnum")) ? 0 : object_in_data.getInt("salnum");
		}

		strSelect = " PAYIDNO, PAYBANK, PAYCARD, PAYNAMEC, PAYEMPNO, PAYACNO, PAYGEDNO, PAYDEPT, PADEPNMC, PATITSUB, PAYTITLE, PAYTITLP, PAYRANK, RANKABBR, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYLVEFG, TRANAME, PAEFFDT, LASTDAYNO, PAYDAYNO, PAYOBASE, PAYOSPSU, PAYOCHALW, PAYOSTBN, PAYOSUPLY, PAYBASE, PAYRBASE, PAYSPSU, PAYRSPSU, PAYSPSD, PAYRSPSD, PAYCHALW, PAYRCHALW, PAYSTBNT, PAYRSTBN, PAYTRANS, PAYSUPLY, PAYRSUPLY, PAYPFNT, PAYSTAL, PAYSTALD, PAYNIGHT, PAYYEAR, PAYADDHL, PAYGRADE, PAYPROMO, PAYADRTN, PAYCPLM1, PAYCPLM2, PAYCPLM3, PAYEMPFD, PAYSARS, PAYEXTRA, PAYGEINS, PAYDPINS, PAYLBINS, PAYRETIR, PAYTAX, PAYGEDP, PAYVETD1, PAYHLOA1, PAYHLOA2, PAYHLOA3, PAYHLOA4, PAYHLOA5, PAYHLOA6, PAYDLOAN, PAYMHOUS, PAYNSPFD, PAYPARK1, PAYPARK2, PAYGYM, PAYLIEN, PAYMEAL, PAYCARDF, PAYDORM1, PAYDORM2, PAYDORM3, PAYOTHER1, PAYOTHER2, PAYOTHER3, PAYAGEN1, PAYTOTAL, PAYTOTIS, PAYTLDED, PAYNET, HID, SALNUM ";

		sql = "select " + strSelect + " from " + this.tRPDTL + " where HID=? " + " and SALNUM=? with ur";
		params.add(hid);
		params.add(salnum);

		log.info(me + "  " + sql + "...params:" + params.toString());
		starttime = System.currentTimeMillis();
		result = da.arraylistbyQuery(sql, params, META_TYPE);
		log.info(me + "...size:" + result.size());

		if (result.size() > 0) {
			for (Map<String, Object> data : result) {
				jsonOne = new JSONObject();
				jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
				jsonOne.put("paybank", StringUtil.nullToString(data.get("paybank")));
				jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));
				jsonOne.put("paynamec", StringUtil.nullToString(data.get("paynamec")));
				jsonOne.put("payempno", StringUtil.nullToString(data.get("payempno")));

				jsonOne.put("payacno", StringUtil.nullToString(data.get("payacno")));
				jsonOne.put("paygedno", StringUtil.nullToString(data.get("paygedno")));
				jsonOne.put("paydept", StringUtil.nullToString(data.get("paydept")));
				jsonOne.put("padepnmc", StringUtil.nullToString(data.get("padepnmc")));
				jsonOne.put("patitsub", StringUtil.nullToString(data.get("patitsub")));

				jsonOne.put("paytitle", StringUtil.nullToString(data.get("paytitle")));
				jsonOne.put("paytitlp", StringUtil.nullToString(data.get("paytitlp")));
				jsonOne.put("payrank", StringUtil.nullToString(data.get("payrank")));
				jsonOne.put("rankabbr", StringUtil.nullToString(data.get("rankabbr")));
				jsonOne.put("payrnk2n", StringUtil.nullToString(data.get("payrnk2n")));

				jsonOne.put("payrank3", StringUtil.nullToString(data.get("payrank3")));
				jsonOne.put("paydegr2", StringUtil.nullToString(data.get("paydegr2")));
				jsonOne.put("paydegre", StringUtil.nullToString(data.get("paydegre")));
				jsonOne.put("paylvefg", StringUtil.nullToString(data.get("paylvefg")));
				jsonOne.put("traname", StringUtil.nullToString(data.get("traname")));

				jsonOne.put("paeffdt", StringUtil.nullToString(data.get("paeffdt")));
				jsonOne.put("lastdayno", StringUtil.nullToString(data.get("lastdayno")));
				jsonOne.put("paydayno", StringUtil.nullToString(data.get("paydayno")));
				jsonOne.put("payobase", StringUtil.nullToString(data.get("payobase")));
				jsonOne.put("payospsu", StringUtil.nullToString(data.get("payospsu")));

				jsonOne.put("payochalw", StringUtil.nullToString(data.get("payochalw")));
				jsonOne.put("payostbn", StringUtil.nullToString(data.get("payostbn")));
				jsonOne.put("payosuply", StringUtil.nullToString(data.get("payosuply")));
				jsonOne.put("paybase", StringUtil.nullToString(data.get("paybase")));
				jsonOne.put("payrbase", StringUtil.nullToString(data.get("payrbase")));

				jsonOne.put("payspsu", StringUtil.nullToString(data.get("payspsu")));
				jsonOne.put("payrspsu", StringUtil.nullToString(data.get("payrspsu")));
				jsonOne.put("payspsd", StringUtil.nullToString(data.get("payspsd")));
				jsonOne.put("payrspsd", StringUtil.nullToString(data.get("payrspsd")));
				jsonOne.put("paychalw", StringUtil.nullToString(data.get("paychalw")));

				jsonOne.put("payrchalw", StringUtil.nullToString(data.get("payrchalw")));
				jsonOne.put("paystbnt", StringUtil.nullToString(data.get("paystbnt")));
				jsonOne.put("payrstbn", StringUtil.nullToString(data.get("payrstbn")));
				jsonOne.put("paytrans", StringUtil.nullToString(data.get("paytrans")));
				jsonOne.put("paysuply", StringUtil.nullToString(data.get("paysuply")));

				jsonOne.put("payrsuply", StringUtil.nullToString(data.get("payrsuply")));
				jsonOne.put("paypfnt", StringUtil.nullToString(data.get("paypfnt")));
				jsonOne.put("paystal", StringUtil.nullToString(data.get("paystal")));
				jsonOne.put("paystald", StringUtil.nullToString(data.get("paystald")));
				jsonOne.put("paynight", StringUtil.nullToString(data.get("paynight")));

				jsonOne.put("payyear", StringUtil.nullToString(data.get("payyear")));
				jsonOne.put("payaddhl", StringUtil.nullToString(data.get("payaddhl")));
				jsonOne.put("paygrade", StringUtil.nullToString(data.get("paygrade")));
				jsonOne.put("paypromo", StringUtil.nullToString(data.get("paypromo")));
				jsonOne.put("payadrtn", StringUtil.nullToString(data.get("payadrtn")));

				jsonOne.put("paycplm1", StringUtil.nullToString(data.get("paycplm1")));
				jsonOne.put("paycplm2", StringUtil.nullToString(data.get("paycplm2")));
				jsonOne.put("paycplm3", StringUtil.nullToString(data.get("paycplm3")));
				jsonOne.put("payempfd", StringUtil.nullToString(data.get("payempfd")));
				jsonOne.put("paysars", StringUtil.nullToString(data.get("paysars")));

				jsonOne.put("payextra", StringUtil.nullToString(data.get("payextra")));
				jsonOne.put("paygeins", StringUtil.nullToString(data.get("paygeins")));
				jsonOne.put("paydpins", StringUtil.nullToString(data.get("paydpins")));
				jsonOne.put("paylbins", StringUtil.nullToString(data.get("paylbins")));
				jsonOne.put("payretir", StringUtil.nullToString(data.get("payretir")));

				jsonOne.put("paytax", StringUtil.nullToString(data.get("paytax")));
				jsonOne.put("paygedp", StringUtil.nullToString(data.get("paygedp")));
				jsonOne.put("payvetd1", StringUtil.nullToString(data.get("payvetd1")));
				jsonOne.put("payhloa1", StringUtil.nullToString(data.get("payhloa1")));
				jsonOne.put("payhloa2", StringUtil.nullToString(data.get("payhloa2")));

				jsonOne.put("payhloa3", StringUtil.nullToString(data.get("payhloa3")));
				jsonOne.put("payhloa4", StringUtil.nullToString(data.get("payhloa4")));
				jsonOne.put("payhloa5", StringUtil.nullToString(data.get("payhloa5")));
				jsonOne.put("payhloa6", StringUtil.nullToString(data.get("payhloa6")));
				jsonOne.put("paydloan", StringUtil.nullToString(data.get("paydloan")));

				jsonOne.put("paymhous", StringUtil.nullToString(data.get("paymhous")));
				jsonOne.put("paynspfd", StringUtil.nullToString(data.get("paynspfd")));
				jsonOne.put("paypark1", StringUtil.nullToString(data.get("paypark1")));
				jsonOne.put("paypark2", StringUtil.nullToString(data.get("paypark2")));
				jsonOne.put("paygym", StringUtil.nullToString(data.get("paygym")));

				jsonOne.put("paylien", StringUtil.nullToString(data.get("paylien")));
				jsonOne.put("paymeal", StringUtil.nullToString(data.get("paymeal")));
				jsonOne.put("paycardf", StringUtil.nullToString(data.get("paycardf")));
				jsonOne.put("paydorm1", StringUtil.nullToString(data.get("paydorm1")));
				jsonOne.put("paydorm2", StringUtil.nullToString(data.get("paydorm2")));

				jsonOne.put("paydorm3", StringUtil.nullToString(data.get("paydorm3")));
				jsonOne.put("payother1", StringUtil.nullToString(data.get("payother1")));
				jsonOne.put("payother2", StringUtil.nullToString(data.get("payother2")));
				jsonOne.put("payother3", StringUtil.nullToString(data.get("payother3")));
				jsonOne.put("payagen1", StringUtil.nullToString(data.get("payagen1")));

				jsonOne.put("paytotal", StringUtil.nullToString(data.get("paytotal")));
				jsonOne.put("paytotis", StringUtil.nullToString(data.get("paytotis")));
				jsonOne.put("paytlded", StringUtil.nullToString(data.get("paytlded")));
				jsonOne.put("paynet", StringUtil.nullToString(data.get("paynet")));
				l_paynet = (Integer) data.get("paynet");
				l_paytotis = (Integer) data.get("paytotis");
				jsonOne.put("payreal", StringUtil.nullToString(l_paynet + l_paytotis));
				jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));

				jsonOne.put("salnum", StringUtil.nullToString(data.get("salnum")));

				jsonRtn.add(jsonOne);
			}
		}
		log.info(me + ",筆數:" + result.size() + "共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
		return jsonRtn;
	}

	// 依篩選條件,取得pa.padatpsn集合
	public JSONArray getDatPsnList(String in_data, String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getDatPsnList(),userid = " + userid;
		log.info(me + " in_data = " + in_data + " hid = " + hid);
		HashMap CspfMap = new HashMap();
		HashMap cspfRtn = new HashMap();
		Pacspf cspfObj = null;
		CommDAO cDAO = null;
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_code_data = null;
		Date start = new Date();
		Date end = new Date();
		String cardno = "";
		String idtno = "";
		String namec = "";
		String[] salnum = null;
		String[] paybank = null;
		String[] payrank = null;
		String[] paytitle = null;
		String[] paydept = null;
		String[] insuamt = null;
		String xbank = "";// 發薪日
		String csflag = "";// 工獎YN
		int rank3 = 0;// 本功
		String title = "";// 職稱
		String rank2 = "";// 權理
		String rnk2n = "";// 職等
		String degr2 = "";// 級別
		String rank = "";// 階別
		String pfkey = "";// 工獎KEY
		int pfbas = 0;// 工獎基本點數
		int pfdat = 0;// 工獎職等點數
		String subsql = "";
		String strSelect = "";
		long starttime = 0;
		int i = 0;
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> paramsub = new ArrayList<Object>();
		if (in_data != null && !in_data.equals("")) {
			object_code_data = JSONObject.fromObject(in_data);
			cardno = (!object_code_data.has("cardno")) ? "" : object_code_data.getString("cardno");
			idtno = (!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			namec = (!object_code_data.has("namec")) ? "" : object_code_data.getString("namec");
			salnum = (!object_code_data.has("salnum")) ? null
					: ("".equals(object_code_data.getString("salnum")) ? null
							: object_code_data.getString("salnum").split(","));
			paybank = (!object_code_data.has("paybank")) ? null
					: ("".equals(object_code_data.getString("paybank")) ? null
							: object_code_data.getString("paybank").split(","));
			payrank = (!object_code_data.has("payrank")) ? null
					: ("".equals(object_code_data.getString("payrank")) ? null
							: object_code_data.getString("payrank").split(","));
			paytitle = (!object_code_data.has("paytitle")) ? null
					: ("".equals(object_code_data.getString("paytitle")) ? null
							: object_code_data.getString("paytitle").split(","));
			paydept = (!object_code_data.has("paydept")) ? null
					: ("".equals(object_code_data.getString("paydept")) ? null
							: object_code_data.getString("paydept").split(","));
			insuamt = (!object_code_data.has("insuamt")) ? null
					: ("".equals(object_code_data.getString("insuamt")) ? null
							: object_code_data.getString("insuamt").split(","));

		}

		strSelect = " RNK4NAME,RANKNAME,RANKABBR,PAYTITLP,PADEPNMC,PACSTNO,SALNUM, PAYBANK, PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYTITLE, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, PAYPFDOC, PACSFLAG, PAYPFSP1, PAYPFSP2, PAYPFSP3, PAYRANK4, A.HID, INSUAMT ";

		sql = "select " + strSelect + " from " + this.tDatPsn + " A " + " left outer join " + this.tDept
				+ " B on A.PAYDEPT=B.PADEPCOD  AND A.HID=B.HID " + " left outer join " + this.tTitle
				+ " C on A.PAYTITLE=C.PAYTITLK AND A.HID=C.HID " + " left outer join " + this.tRankCode
				+ " D on A.PAYRANK=D.RANKCODE " + " left outer join " + this.tRnk4Tbl + " E on A.PAYRANK4=E.RNK4KEY "
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
		}
		starttime = System.currentTimeMillis();
		if (!(paybank == null || (paybank != null && paybank.length == 0))) { // 發薪日
			subsql += " and PAYBANK in ( ";
			for (i = 0; i < paybank.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(paybank[i]);
				paramsub.add(paybank[i]);
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
		if (!(payrank == null || (payrank != null && payrank.length == 0))) { // 階級
			subsql += " and PAYRANK in ( ";
			for (i = 0; i < payrank.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(payrank[i]);
				paramsub.add(payrank[i]);
			}
			subsql += ")";
		}
		if (!(paytitle == null || (paytitle != null && paytitle.length == 0))) { // 職稱
			subsql += " and PAYTITLE in ( ";
			for (i = 0; i < paytitle.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(paytitle[i]);
				paramsub.add(paytitle[i]);
			}
			subsql += ")";
		}
		if (!(paydept == null || (paydept != null && paydept.length == 0))) { // 單位
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
		if (!(insuamt == null || (insuamt != null && insuamt.length == 0))) { // 投保級距
			subsql += " and insuamt in ( ";
			for (i = 0; i < insuamt.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(insuamt[i]);
				paramsub.add(insuamt[i]);
			}
			subsql += ")";
		}
		sql += subsql + " union all ";
		// 加上歷史月支個人明細
		sql += "select " + strSelect + " from " + this.tDatPsnDw + " A " + " left outer join " + this.tDept
				+ " B on A.PAYDEPT=B.PADEPCOD  AND A.HID=B.HID " + " left outer join " + this.tTitle
				+ " C on A.PAYTITLE=C.PAYTITLK AND A.HID=C.HID " + " left outer join " + this.tRankCode
				+ " D on A.PAYRANK=D.RANKCODE " + " left outer join " + this.tRnk4Tbl + " E on A.PAYRANK4=E.RNK4KEY "
				+ " where A.HID=? ";

		params.add(hid);
		sql += subsql + " order by SALNUM DESC,PAYCARD with ur";
		for (int j = 0; j < paramsub.size(); j++) {
			params.add(paramsub.get(j));
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
			cDAO = new CommDAO(da, hid);
			start = new Date();
			// 找取得績效點數map
			CspfMap = cDAO.getCsPfByAll(da, hid);
			log.info(me + "  " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					jsonOne = new JSONObject();
					jsonOne.put("pacstno", StringUtil.nullToString(data.get("pacstno")));
					jsonOne.put("padepnmc", StringUtil.nullToString(data.get("padepnmc")));
					jsonOne.put("paytitlp", StringUtil.nullToString(data.get("paytitlp")));
					jsonOne.put("salnum", StringUtil.nullToString(data.get("salnum")));
					xbank = StringUtil.nullToString(data.get("paybank"));
					jsonOne.put("paybank", xbank);
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));
					jsonOne.put("payempno", StringUtil.nullToString(data.get("payempno")));
					jsonOne.put("paynamec", StringUtil.nullToString(data.get("paynamec")));
					jsonOne.put("paydept", StringUtil.nullToString(data.get("paydept")));
					title = StringUtil.nullToString(data.get("paytitle"));
					jsonOne.put("paytitle", title);
					rnk2n = StringUtil.nullToString(data.get("payrnk2n"));
					jsonOne.put("payrnk2n", rnk2n);
					jsonOne.put("rnk2nNm", PaCommUtil.getRnk2nNm(StringUtil.nullToString(data.get("payrnk2n"))));
					rank3 = (Integer) (data.get("payrank3"));
					jsonOne.put("payrank3", rank3);
					jsonOne.put("rank3Nm", PaCommUtil.getRank3Nm((Integer) data.get("payrank3")));
					degr2 = StringUtil.nullToString(data.get("paydegr2"));
					jsonOne.put("paydegr2", degr2);
					jsonOne.put("paydegre", StringUtil.nullToString(data.get("paydegre")));
					rank = StringUtil.nullToString(data.get("payrank"));
					jsonOne.put("payrank", rank);
					jsonOne.put("rankNm", StringUtil.nullToString(data.get("rankname")));
					rank2 = StringUtil.nullToString(data.get("payrank2"));
					jsonOne.put("payrank2", rank2);
					jsonOne.put("payrankc", StringUtil.nullToString(data.get("payrankc")));
					jsonOne.put("paypfdoc", StringUtil.nullToString(data.get("paypfdoc")));
					csflag = StringUtil.nullToString(data.get("pacsflag"));
					jsonOne.put("pacsflag", csflag);
					jsonOne.put("paypfsp1", StringUtil.nullToString(data.get("paypfsp1")));
					jsonOne.put("paypfsp2", StringUtil.nullToString(data.get("paypfsp2")));
					jsonOne.put("paypfsp3", StringUtil.nullToString(data.get("paypfsp3")));
					jsonOne.put("payrank4", StringUtil.nullToString(data.get("payrank4")));
					jsonOne.put("rnk4name", StringUtil.nullToString(data.get("rnk4name")));
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					jsonOne.put("insuamt", StringUtil.nullToString(data.get("insuamt")));
					pfbas = 0;
					pfdat = 0;
					pfkey = "";
					// 績效點數
					if ("Y".equals(csflag) && hid.matches("1A0|2A0")) {

						cspfRtn = cDAO.getCsPfByMap(rank3, title, rank, rnk2n, Integer.valueOf(degr2), xbank, hid,
								CspfMap, da);
						if (cspfRtn != null) {
							pfkey = (String) cspfRtn.get("pfkey");
							pfbas = (Integer) cspfRtn.get("pfbas");
							pfdat = (Integer) cspfRtn.get("pfdat");
						}
					}
					jsonOne.put("pfkey", pfkey);
					jsonOne.put("pfbas", pfbas);
					jsonOne.put("pfdat", pfdat);

					jsonRtn.add(jsonOne);
				}
			}
			end = new Date();
			log.info(me + "  ...********* dataaccess:" + (end.getTime() - start.getTime()) + "共花費："
					+ (System.currentTimeMillis() - starttime) + "毫秒");

		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}

		return jsonRtn;
	}

	// 依異動/批次,取得餉單編號清單
	public JSONArray getSalnumList(String in_data, String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getSalnumList(),userid = " + userid;
		log.info(me + "in_data=" + in_data + " hid = " + hid);
		ArrayList<Padatpay> datpayAry = new ArrayList<Padatpay>(); // 支扣集合
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_in_data = null;
		Date start = new Date();
		Date end = new Date();
		String salnum = "";
		String selCode = "";
		String selNm = "";
		String cardno = "";
		String patradt = "";
		String subsql = "";
		if (in_data != null && !in_data.equals("")) {
			object_in_data = JSONObject.fromObject(in_data);
			selCode = (!object_in_data.has("selCode")) ? "" : object_in_data.getString("selCode");
			selNm = (!object_in_data.has("selNm")) ? "" : object_in_data.getString("selNm");
			salnum = (!object_in_data.has("salnum")) ? "" : object_in_data.getString("salnum");
			cardno = (!object_in_data.has("cardno")) ? "" : StringUtil.nullToString(object_in_data.getString("cardno"));
			patradt = (!object_in_data.has("patradt")) ? ""
					: StringUtil.nullToString(object_in_data.getString("patradt"));
		}
		if (!"".equals(cardno)) {
			subsql = " and A.PAYCARD=? ";
		}
		if (!"".equals(patradt)) {
			subsql += " and A.PATRADT=? ";
		}
		// 1.餉單編號>0
		// 2.餉單編號+發薪日+卡號,都是空白,抓餉單編號=0
		// 2026.3.19改成傳入餉單編號
		// if ( !"".equals(salnum) ||
		// ( "".equals(salnum) && "".equals(patradt) && "".equals(cardno) ) ){
		// subsql+=" and A.SALNUM=? ";
		// }
		if (!"".equals(salnum)) {
			subsql += " and A.SALNUM=? ";
		}
		// 讀取餉單編單=0(1異動檔 2批次檔)
		if (selCode.equals("1"))
			sql = " select A.PAYCARD,A.PAYIDNO,A.PAYNAMEC,B.TRANAME,A.PATRADT,A.PATRACOD,a.SALNUM " + " from "
					+ this.tTraPsn + " A " + " left outer join " + this.tTraCode + " B on a.PATRACOD = b.TRACODE "
					+ " where HID=? " + subsql + " order by a.PATRACOD,a.PAYCARD ";
		else if (selCode.equals("2"))
			sql = " select A.PAYCARD,A.PAYIDNO,C.PAYNAMEC,B.PACODNMC,A.PATRADT,A.PAYCODE,a.SALNUM " + " from "
					+ this.tBatPay + " A " + " left outer join " + this.tPayItm
					+ " B on a.PAYCODE = b.PAYCODE and a.hid=b.hid " + " left outer join " + this.tBasPsn
					+ " C on a.PAYIDNO = c.PAYIDNO and a.hid=c.hid " + " where a.HID=? " + subsql
					+ " order by a.PATRADT,a.PAYCODE,a.PAYCARD ";
		params.add(hid);

		if (!"".equals(cardno)) {
			params.add(cardno);
		}
		if (!"".equals(patradt)) {
			params.add(patradt);
		}
		if (!"".equals(salnum)) {
			params.add(salnum);
		} else if ("".equals(salnum) && "".equals(patradt) && "".equals(cardno)) {
			params.add(0);
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
					jsonOne = new JSONObject();
					jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));
					jsonOne.put("paynamec", StringUtil.nullToString(data.get("paynamec")));
					jsonOne.put("patradt", StringUtil.nullToString(data.get("patradt")));
					jsonOne.put("salnum", StringUtil.nullToString(data.get("salnum")));
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					if (selCode.equals("1")) {
						jsonOne.put("typnm", "異動");
						jsonOne.put("itmnm", StringUtil.nullToString(data.get("traname")));
						jsonOne.put("patracod", StringUtil.nullToString(data.get("patracod")));
					} else if (selCode.equals("2")) {
						jsonOne.put("typnm", "批次");
						jsonOne.put("itmnm", StringUtil.nullToString(data.get("pacodnmc")));
						jsonOne.put("paycode", StringUtil.nullToString(data.get("paycode")));
					}

					jsonRtn.add(jsonOne);
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

		return jsonRtn;
	}

	// 依餉單編號/發薪日期,取得月支支扣pa.padatpay_Sum
	public JSONArray getCheckPayBykey(String in_data, String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getCheckPayBykey(),userid = " + userid;
		log.info(me + " in_data = " + in_data + " hid = " + hid);
		Padatpay datpayObj = null;
		ArrayList<Padatpay> datpayAry = new ArrayList<Padatpay>(); // 支扣集合
		CommDAO cDAO = null;
		HashMap rtnMap = new HashMap();
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_in_data = null;
		Date start = new Date();
		Date end = new Date();
		String strSelect = "";
		String strPaycode = "";
		String subsql = "";
		String salnum = "";
		String paybank = "";
		String payamtp = "";
		String payamtd = "";

		int i = 0;
		HashMap sumListMap = new HashMap();
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> paramsub = new ArrayList<Object>();
		if (in_data != null && !in_data.equals("")) {
			object_in_data = JSONObject.fromObject(in_data);
			paybank = (!object_in_data.has("paybank")) ? "" : object_in_data.getString("paybank");
			salnum = (!object_in_data.has("salnum")) ? "" : object_in_data.getString("salnum");
		}
		// 讀取pa.padatpay+pa.padatpay_dw
		strSelect = " a.PAYCODE, SUM(a.PAYAMT) AS sum_amt, b.PACODNMC, a.SALNUM,count(*) as cnt ";
		sql = "select " + strSelect + " from " + this.tDatPay + " A " + " left outer join " + this.tPayItm
				+ " B on A.PAYCODE=B.PAYCODE and A.HID=B.HID " + " left outer join " + this.tDatPsn
				+ " C on A.PAYIDNO=C.PAYIDNO AND A.SALNUM=C.SALNUM and A.HID=B.HID " + " where A.HID=? ";
		params.add(hid);
		if (!"".equals(paybank)) {// 發薪日
			subsql += " and C.PAYBANK=? ";
			params.add(paybank);
			paramsub.add(paybank);
		} else if (!"".equals(salnum)) {// 餉單編號
			subsql += " and A.SALNUM=? ";
			params.add(salnum);
			paramsub.add(salnum);
		}
		sql += subsql + " GROUP BY a.SALNUM, a.PAYCODE, b.PACODNMC " + "union all ";
		// 加上歷史月支明細
		sql += " select " + strSelect + " from " + this.tDatPayDw + " A " + " left outer join " + this.tPayItm
				+ " B on A.PAYCODE=B.PAYCODE and A.HID=B.HID " + " left outer join " + this.tDatPsnDw
				+ " C on A.PAYIDNO=C.PAYIDNO AND A.SALNUM=C.SALNUM and A.HID=B.HID " + " where A.HID=? ";
		params.add(hid);
		sql += subsql + " GROUP BY a.SALNUM, a.PAYCODE, b.PACODNMC " + " order by 1 with ur";

		for (int j = 0; j < paramsub.size(); j++) {
			params.add(paramsub.get(j));
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
					jsonOne = new JSONObject();
					payamtp = "";
					payamtd = "";
					jsonOne.put("salnum", StringUtil.nullToString(data.get("salnum")));
					jsonOne.put("pacodnmc", StringUtil.nullToString(data.get("pacodnmc")));
					strPaycode = StringUtil.nullToString(data.get("paycode"));
					jsonOne.put("paycode", strPaycode);
					jsonOne.put("payamt", StringUtil.nullToString(data.get("sum_amt")));
					if ("P".equals(strPaycode.substring(1, 2)))
						payamtp = StringUtil.nullToString(data.get("sum_amt"));
					else if ("D".equals(strPaycode.substring(1, 2)))
						payamtd = StringUtil.nullToString(data.get("sum_amt"));
					jsonOne.put("payamtd", payamtd);
					jsonOne.put("payamtp", payamtp);
					jsonOne.put("cnt", StringUtil.nullToString(data.get("cnt")));

					jsonRtn.add(jsonOne);
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

		return jsonRtn;
	}

	// 檔案產製 selCode:selNm:selEn 1:主計室_發放:BAS_1 2:主計室_扣款:BAS_2
	public JSONArray getBASFileList(String in_data, String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getBASFileList(),userid = " + userid;
		log.info(me + " in_data = " + in_data + " hid = " + hid);
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_in_data = null;
		long starttime = 0;
		String selEn = "";
		String selCode = "";
		String selNm = "";
		String patradt = "";
		int salnum = 0;
		int i = 0;
		String jsonStr = "";
		String strRank = "";
		String l_Govfg = " "; // 2023.6.21 A-新制無公保年資 B-新制有公保年資(此類待有人員,再設定)
		String strGovdt = ""; // 初任公職日
		String l_Contract = "";// 空白(月頭) Y(月尾)
		String l_paycode = ""; // 支扣代碼
		String l_Idtno = ""; // 身分證
		int l_Dat_payamt = 0; // 發薪明細金額
		int l_prepaynum = 0;
		String l_prepaycode = "";
		HashMap trapsnMap = new HashMap();
		Padatpsn padatpsn = null;
		long starttime9 = 0;
		HashMap<String, Object> prPrtMap = new HashMap(); // 報表架構 map
		HashMap datpayMap = new HashMap();// 月支支扣檔 map
		TreeMap<Integer, Integer> valMap = new TreeMap();
		ArrayList<Padatpay> datpayAry = null;// 月支支扣檔 Array
		Pareport prtrptobj = null; // 報表檔架構
		ArrayList<Object> param = new ArrayList<Object>();
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

		try {
			Hospitals hospInfo = Hospitals.getOneHosp(hid);
			if (da == null) {
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			starttime9 = System.currentTimeMillis();
			// 寫入資料夾設定
			FileUtil fileUtil = new FileUtil();
			if (selEn.matches("BAS_1|BAS_2")) { // BAS_1主計_發放 BAS_2主計_扣款
				// folderPath = hospInfo.fileSRV+"bak\\";
				folderPath = hospInfo.fileAPSRV;
				if (selEn.equals("BAS_1")) { // 主計_發放
					filename = "PAY";
				} else if (selEn.equals("BAS_2")) { // 主計_扣款
					filename = "DEDUCT";
				}
				if (patradt.substring(8, 10).equals("02") && hid.equals("1A0")) {// 高榮2號是破月
					filename = "QUIT" + filename;
				}
				if (hid.matches("2A0|3A0")) { // 屏榮,聯醫,檔名加HID
					filename += hid;
				}
			}
			filename += ".txt";
			filePath = folderPath + filename;

			if (!FileUtil.FileToSmbFile(folderPath, hid).exists()) {
				fileUtil.FileToSmbFile(folderPath, hid).mkdirs();
			}
			// 建立檔案 難字要用utf8
			SmbFile payfile = fileUtil.FileToSmbFile(filePath, hid);
			bfw = new BufferedWriter(new OutputStreamWriter(new SmbFileOutputStream(payfile, false), "utf-8"));

			log.info(me + "  1.讀取報表架構 ,");
			param = new ArrayList<Object>();
			param.add(hid);
			if (selEn.equals("BAS_1")) { // 主計_發放
				param.add("2");
			} else if (selEn.equals("BAS_2")) { // 主計_扣款
				param.add("3");
			}
			prPrtMap = this.getPaItmMap(param, "PrtRpt", hid, da);

			log.info(me + "  1.讀取報表架構 ,筆數:" + prPrtMap.size() + "共花費：" + (System.currentTimeMillis() - starttime9)
					+ "毫秒");

			starttime9 = System.currentTimeMillis();
			param = new ArrayList<Object>();
			param.add(salnum);
			param.add(hid);
			datpayMap = this.getPaItmMap(param, "DATPAY", hid, da);
			log.info(me + "  2-1.讀取月支明細 ,筆數:" + datpayMap.size() + "共花費：" + (System.currentTimeMillis() - starttime9)
					+ "毫秒");

			// 讀取pa.padatpsn
			if (hid.equals("1A0")) {
				// tmp=" and a.paycard in ('3007','0168' ) ";
				sql = "select A.PAYBANK, A.PAYDEPT, A.PAYCARD, A.PAYNAMEC, A.PAYTITLE, T.PAYTITLP,SUBSTR(A.PAYIDNO,2,1) AS SEX, A.PAYEMPNO ,COALESCE(tra.PAYGOVDT, B.PAYGOVDT) PAYGOVDT,COALESCE(tra.PAYEMPDT, B.PAYEMPDT) PAYEMPDT,A.PAYIDNO,A.PAYRANK,A.hid "
						+ " from " + this.tDatPsn + " A " + " left outer join " + this.tTitle
						+ " t on A.PAYTITLE = T.PAYTITLK and A.hid=t.hid " + " left outer join " + this.tBasPsn
						+ " b on A.PAYIDNO = B.PAYIDNO and A.hid=B.hid " + " left outer join " + this.tTraPsn
						+ " tra on A.PAYIDNO = tra.PAYIDNO and A.hid=tra.hid and a.salnum=tra.salnum and tra.patracod in (1,33) "
						+ " where A.HID=? and A.SALNUM =? " + tmp + " order by paycard with ur";
			}
			params.add(hid);
			params.add(salnum);

			log.info(me + "  2-2.讀取pa.padatpsn:" + sql + "...params:" + params.toString());
			log.info(me + "  " + sql + "...params:" + params.toString());

			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					padatpsn = new Padatpsn();
					datpayAry = null;
					strLine = "";
					PropertyUtils.copyProperties(padatpsn, data);

					jsonOne = new JSONObject();
					strTmp = StringUtil.nullToString(padatpsn.getPaybank());
					strLine = strTmp.substring(0, 4) + strTmp.substring(5, 7) + strTmp.substring(8, 10);
					jsonOne.put("paybank", StringUtil.nullToString(padatpsn.getPaybank()));
					strLine += StringUtil.fillChar(StringUtil.nullToString(padatpsn.getPaydept()), 4, " ", true);
					jsonOne.put("paydept", StringUtil.nullToString(padatpsn.getPaydept()));
					strLine += StringUtil.nullToString(padatpsn.getPaycard());
					jsonOne.put("paycard", StringUtil.nullToString(padatpsn.getPaycard()));
					strLine += StringUtil.strFillWideWhite(StringUtil.nullToString(padatpsn.getPaynamec()), 6, true);
					jsonOne.put("paynamec", StringUtil.nullToString(padatpsn.getPaynamec()));
					strLine += StringUtil.nullToString(padatpsn.getPaytitle());
					jsonOne.put("paytitle", StringUtil.nullToString(padatpsn.getPaytitle()));
					strLine += StringUtil.strFillWideWhite(StringUtil.nullToString(padatpsn.getPaytitlp()), 11, true);
					jsonOne.put("paytitlp", StringUtil.nullToString(padatpsn.getPaytitlp()));
					strLine += StringUtil.nullToString(padatpsn.getSex());
					jsonOne.put("sex", StringUtil.nullToString(padatpsn.getSex()));
					strLine += StringUtil.fillChar(StringUtil.nullToString(padatpsn.getPayempno()), 7, " ", true);
					jsonOne.put("payempno", StringUtil.nullToString(padatpsn.getPayempno()));
					// 2023.6.21 A-新制無公保年資 B-新制有公保年資(此類待有人員,再設定)
					strRank = StringUtil.nullToString(padatpsn.getPayrank());
					l_Govfg = " ";
					strGovdt = StringUtil.nullToString(padatpsn.getStrgovdt());
					if (strRank.matches("1|2|3|8|9") && !DateUtil.compareDate(strGovdt, Globals.gCivilServants)) {
						l_Govfg = "A";
					}
					strLine += l_Govfg;
					jsonOne.put("l_Govfg", l_Govfg);// 新制公保
					// 契約月尾薪Y
					l_Contract = padatpsn.getContract();
					if (l_Contract.equals("N") || l_Contract == "") {
						l_Contract = " ";
					}

					if (selEn.equals("BAS_1")) { // 主計_發放
						strLine += l_Contract;
						jsonOne.put("l_contract", l_Contract);// 契約月頭(空白) 月尾(Y)
					}
					// 清空報表架構
					for (Map.Entry<String, Object> structEntry : prPrtMap.entrySet()) {
						prtrptobj = (Pareport) structEntry.getValue();
						prtrptobj.setPayamt(0);
						// System.out.println(prtrptobj.getPaynum() + " : " + prtrptobj.getPaycode());
					}
					// 月支支扣
					l_Idtno = StringUtil.nullToString(padatpsn.getPayidno());
					datpayAry = (ArrayList) datpayMap.get(l_Idtno);
					if (datpayAry != null) {
						for (Padatpay dataP : datpayAry) {
							l_paycode = dataP.getPaycode();
							l_Dat_payamt = dataP.getPayamt();

							prtrptobj = (Pareport) prPrtMap.get(l_paycode);
							if (prtrptobj == null) {
								prtrptobj = (Pareport) prPrtMap.get(l_paycode.substring(0, 4));
								if (prtrptobj != null) {
									prtrptobj.setPayamt(prtrptobj.getPayamt() + dataP.getPayamt());
								}
							} else {
								prtrptobj.setPayamt(dataP.getPayamt());
							}
						}
					}
					// 將同paynum數字加總放valMap
					valMap = new TreeMap();
					l_prepaycode = "";
					for (Map.Entry<String, Object> prtEntry : prPrtMap.entrySet()) {
						prtrptobj = (Pareport) prtEntry.getValue();
						l_prepaynum = prtrptobj.getPaynum();
						l_prepaycode = prtrptobj.getPaycode();
						if (valMap.get(l_prepaynum) != null)
							valMap.put(l_prepaynum, (Integer) valMap.get(l_prepaynum) + prtrptobj.getPayamt());
						else {
							valMap.put(l_prepaynum, prtrptobj.getPayamt());
						}
					}
					// 寫入jsonOne
					String tmpKey = "";
					for (Map.Entry<Integer, Integer> entry : valMap.entrySet()) {
						// System.out.println(entry.getKey() + " : " + entry.getValue());
						if (selEn.equals("BAS_1")) { // 主計_發放
							tmpKey = "P" + entry.getKey();
						} else if (selEn.equals("BAS_2")) { // 主計_扣款
							tmpKey = "D" + entry.getKey();
						}
						jsonOne.put(tmpKey, entry.getValue());
						strLine += StringUtil.fillChar(Integer.toString(entry.getValue()), 9, " ", false);
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
		String l_Paretention = "";// [專一]0,[專二]Y/N
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
				folderPath = hospInfo.fileSRV+"bak\\";
				//folderPath = hospInfo.fileSALPPF;
				filename = "DocBas";
				if (hid.matches("2A0")) { // 屏榮,檔名加HID 聯醫沒有用
					filename += hid;
				}
			} else if (selEn.matches("Perf_2")) { // Perf_2績效組_員工
				folderPath = hospInfo.fileSRV+"bak\\";
				//folderPath = hospInfo.fileSALEPF;
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
			// 建立檔案 難字要用utf8
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
				// 2.專勤額度=0 and 專勤獎金細項>0,不含(VP010 VP013)
				// 3.專1=0,專2=Y(PARETENTION)
				// 4.paeffdt 離職2個月內的醫師
				strTmp = " (DAT.PAYPFDOC >0 "
						+ " or  (DAT.PAYPFDOC = 0 AND DAT.PACSFLAG = 'N' AND DAT.PAYPFSP1+DAT.PAYPFSP2+DAT.PAYPFSP3 = 0 "
						+ " AND (DAT.PAYIDNO IN (SELECT PAYIDNO FROM " + this.tDatPay + " WHERE HID=? AND SALNUM = ? "
						+ " AND SUBSTR(PAYCODE,1,4)='VP01' AND PAYCODE NOT IN ('VP013','VP010'))) ) "
						+ " OR (BAS.PARETENTION='Y')) " + 
						" UNION "+
					   " select TRA.PATRADT PAYBANK, tra.PAYDEPT, D.PADEPNMC, tra.PAYCARD, tra.PAYNAMEC, bas.PAYTITLE, T.PAYTITLP,bas.PAYRNK2N, bas.PAYRANK3, bas.PAYDEGR2,bas.PAYDEGRE, "
						+ "COALESCE(tra.PAYEMPDT, BAS.PAYSGNDT) PAYEMPDT, tra.PAYIDNO, bas.PAYRANK2, bas.PAYRANK, bas.PAYPFDOC, bas.PACSFLAG,bas.PAYPFSP1+tra.PAYPFSP2+tra.PAYPFSP3 as PAYPFSP,"
						+ "PATITSUB, bas.PARETENTION,tra.PAYEMPNO,COALESCE(TRA.PAYRSGDT,BAS.PAYRSGDT) PAYRSGDT,COALESCE(TRA.PAYEMPDT,BAS.PAYEMPDT) PAYEMPDT, COALESCE(tra.PAYSGNDT,bas.PAYSGNDT) PAYSGNDT,"
						+ "COALESCE(tra.PATRACOD,0) patracod,tra.PAYMONNO,tra.PAYDAYNO,case WHEN tra.patracod IN(21,22) then tra.payrsgdt WHEN tra.patracod IN (23) THEN tra.PAEFFDT else '9999-12-31' end payrsgdt,"
						+ "COALESCE(tra.PAYPFSP2,bas.PAYPFSP2)PAYPFSP2,COALESCE(tra.PAYEXPFKEY2,bas.PAYEXPFKEY2) PAYEXPFKEY2 "
						+ " from " + this.tTraPsn + " Tra " + 
						" left outer join " + this.tDept + " d on tra.PAYDEPT = D.PADEPCOD AND tra.HID=D.HID " + 
						" left outer join " + this.tTitle+ " t on tra.PAOTITLE = T.PAYTITLK and tra.hid=t.hid " + 
						" left outer join " + this.tBasPsn+ " bas on tra.PAYIDNO = Bas.PAYIDNO and tra.hid=Bas.hid "+
				       " where tra.HID=? and (t.PATITSUB IN ('A','V') OR TRA.PAOTITLE IN ('CG') )"+
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
					+ "COALESCE(tra.PATRACOD,0) patracod,tra.PAYMONNO,tra.PAYDAYNO,case WHEN tra.patracod IN(21,22) then tra.payrsgdt WHEN tra.patracod IN (23) THEN tra.PAEFFDT else '9999-12-31' end payrsgdt,"
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

					// 專勤額度
					l_Pfdoc = padatpsn.getPaypfdoc();
					// [專一]0,[專二]Y/N
					l_Paretention = StringUtil.nullToString(padatpsn.getParetention());
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
					jsonOne.put("padepnmc", StringUtil.nullToString(padatpsn.getPaynamec()));
					strLine += StringUtil.nullToString(padatpsn.getPaycard());
					jsonOne.put("paycard", StringUtil.nullToString(padatpsn.getPaycard()));
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
						strLine += StringUtil.fillChar(StringUtil.nullToString(padatpsn.getPaypfdoc()), 7, " ", false);
						jsonOne.put("pfdoc", StringUtil.nullToString(padatpsn.getPaypfdoc()));
						strLine += StringUtil.fillChar((l_Open == 0 ? "" : String.valueOf(l_Open)), 6, " ", false);
						jsonOne.put("pfopen", l_Open);
						strLine += StringUtil.fillChar((l_VP01 == 0 ? "" : String.valueOf(l_VP01)), 8, " ", false);
						jsonOne.put("vp019", l_VP01);
						strLine += StringUtil.fillChar((l_VP049 == 0 ? "" : String.valueOf(l_VP049)), 8, " ", false);
						jsonOne.put("vp049", l_VP049);
						strLine += StringUtil.fillChar((l_FP440 == 0 ? "" : String.valueOf(l_FP440)), 8, " ", false);
						jsonOne.put("fp440", l_FP440);
						strLine += StringUtil.fillChar((l_teach_00 == 0 ? "" : String.valueOf(l_teach_00)), 6, " ",
								false);
						jsonOne.put("teach_00", l_teach_00);
						strLine += StringUtil.fillChar((l_teach_09 == 0 ? "" : String.valueOf(l_teach_09)), 6, " ",
								false);
						jsonOne.put("teach_09", l_teach_09);
						strLine += StringUtil.fillChar((l_thesis_10 == 0 ? "" : String.valueOf(l_thesis_10)), 6, " ",
								false);
						jsonOne.put("thesis_10", l_thesis_10);
						strLine += StringUtil.fillChar((l_thesis_19 == 0 ? "" : String.valueOf(l_thesis_19)), 6, " ",
								false);
						jsonOne.put("thesis_19", l_thesis_19);
						strLine += StringUtil.fillChar((l_thesis_20 == 0 ? "" : String.valueOf(l_thesis_20)), 6, " ",
								false);
						jsonOne.put("thesis_20", l_thesis_20);
						strLine += StringUtil.fillChar((l_thesis_29 == 0 ? "" : String.valueOf(l_thesis_29)), 6, " ",
								false);
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
						strLine += StringUtil.fillChar(StringUtil.nullToString(padatpsn.getPaypfsp()), 4, "0", false);
						jsonOne.put("pfsp", StringUtil.nullToString(padatpsn.getPaypfsp()));
						strLine += StringUtil.fillChar((l_Open == 0 ? "" : String.valueOf(l_Open)), 6, " ", false);
						jsonOne.put("pfopen", l_Open);
						// 教職 論文
						strLine += StringUtil.fillChar((l_teach_00 == 0 ? "" : String.valueOf(l_teach_00)), 6, " ",
								false);
						jsonOne.put("teach_00", l_teach_00);
						strLine += StringUtil.fillChar((l_teach_09 == 0 ? "" : String.valueOf(l_teach_09)), 6, " ",
								false);
						jsonOne.put("teach_09", l_teach_09);
						strLine += StringUtil.fillChar((l_thesis_10 == 0 ? "" : String.valueOf(l_thesis_10)), 6, " ",
								false);
						jsonOne.put("thesis_10", l_thesis_10);
						strLine += StringUtil.fillChar((l_thesis_19 == 0 ? "" : String.valueOf(l_thesis_19)), 6, " ",
								false);
						jsonOne.put("thesis_19", l_thesis_19);
						strLine += StringUtil.fillChar((l_thesis_20 == 0 ? "" : String.valueOf(l_thesis_20)), 6, " ",
								false);
						jsonOne.put("thesis_20", l_thesis_20);
						strLine += StringUtil.fillChar((l_thesis_29 == 0 ? "" : String.valueOf(l_thesis_29)), 6, " ",
								false);
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

	// 依支扣代碼,取得日期範圍名單
	public JSONArray getItmPayList(String in_data, String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getItmPayList(),userid = " + userid;
		log.info(me + " in_data = " + in_data + " hid = " + hid);
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_in_data = null;
		long starttime = 0;
		String strSelect = "";
		String subsql = "";
		String[] paydept = null;
		String pabankdt_beg = "";
		String pabankdt_end = "";
		String cardno = "";
		String idtno = "";
		String namec = "";
		String paycode = "";
		int i = 0;
		starttime = System.currentTimeMillis();
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> paramsub = new ArrayList<Object>();
		if (in_data != null && !in_data.equals("")) {
			object_in_data = JSONObject.fromObject(in_data);
			pabankdt_beg = (!object_in_data.has("pabankdt_beg")) ? "" : object_in_data.getString("pabankdt_beg");
			pabankdt_end = (!object_in_data.has("pabankdt_end")) ? "" : object_in_data.getString("pabankdt_end");
			paydept = (!object_in_data.has("paydept")) ? null
					: ("".equals(object_in_data.getString("paydept")) ? null
							: object_in_data.getString("paydept").split(","));
			cardno = (!object_in_data.has("cardno")) ? "" : object_in_data.getString("cardno");
			idtno = (!object_in_data.has("idtno")) ? "" : object_in_data.getString("idtno");
			namec = (!object_in_data.has("namec")) ? "" : object_in_data.getString("namec");
			paycode = (!object_in_data.has("paycode")) ? "" : object_in_data.getString("paycode");
		}
		// 讀取pa.padatpay+pa.padatpay_dw
		strSelect = " paybank,paycard,paynamec,a.payidno,a.salnum,paydept,c.padepnmc,paytitle,paytitlc,b.payamt,b.paycode,e.pacodnmc ";
		sql = "select " + strSelect + " from " + this.tDatPsn + " A " + " inner join " + this.tDatPay
				+ " B on A.PAYIDNO=b.PAYIDNO   AND A.SALNUM=b.SALNUM and A.HID=B.HID " + " left outer join "
				+ this.tDept + " C on a.paydept=c.padepcod  and A.HID=c.HID " + " left outer join " + this.tTitle
				+ " d on a.paytitle=d.paytitlk and A.HID=d.HID " + " left outer join " + this.tPayItm
				+ " e on b.paycode=e.paycode   and A.HID=e.HID " + " where A.HID=? and b.paycode=?";
		params.add(hid);
		params.add(paycode);
		if (!"".equals(cardno)) {// 卡號
			subsql += " and a.PAYCARD=? ";
			params.add(cardno);
			paramsub.add(cardno);
		} else if (!"".equals(idtno)) {// 身分證
			subsql += " and a.PAYIDNO=? ";
			params.add(idtno);
			paramsub.add(idtno);
		} else if (!"".equals(namec)) {// 姓名
			subsql += " and a.PAYNAMEC like ? ";
			params.add(namec + "%");
			paramsub.add(namec + "%");
		}
		if (!(paydept == null || (paydept != null && paydept.length == 0))) { // 單位
			subsql += " and a.paydept in ( ";
			for (i = 0; i < paydept.length; i++) {
				if (i != 0)
					subsql += ",";
				subsql += "?";
				params.add(paydept[i]);
				paramsub.add(paydept[i]);
			}
			subsql += ")";
		}
		if (!"".equals(pabankdt_beg) && !"".equals(pabankdt_end)) {// 發薪日期間
			subsql += " and a.paybank between ? and ? ";
			params.add(pabankdt_beg);
			params.add(pabankdt_end);
			paramsub.add(pabankdt_beg);
			paramsub.add(pabankdt_end);
		} else if (!"".equals(pabankdt_beg) && "".equals(pabankdt_end)) {// 發薪日起
			subsql += " and a.paybank = ? ";
			params.add(pabankdt_beg);
			paramsub.add(pabankdt_beg);
		}

		sql += subsql + "union all ";
		// 加上歷史月支明細
		sql += " select " + strSelect + " from " + this.tDatPsnDw + " A " + " inner join " + this.tDatPayDw
				+ " B on A.PAYIDNO=b.PAYIDNO   AND A.SALNUM=b.SALNUM and A.HID=B.HID " + " left outer join "
				+ this.tDept + " C on a.paydept=c.padepcod  and A.HID=c.HID " + " left outer join " + this.tTitle
				+ " d on a.paytitle=d.paytitlk and A.HID=d.HID " + " left outer join " + this.tPayItm
				+ " e on b.paycode=e.paycode   and A.HID=e.HID " + " where A.HID=? and b.paycode=?";
		params.add(hid);
		params.add(paycode);
		sql += subsql + " order by 1 desc,2 with ur";

		for (int j = 0; j < paramsub.size(); j++) {
			params.add(paramsub.get(j));
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
			log.info(me + "  " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					jsonOne = new JSONObject();
					//
					jsonOne.put("paybank", StringUtil.nullToString(data.get("paybank")));
					jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));
					jsonOne.put("paynamec", StringUtil.nullToString(data.get("paynamec")));
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("salnum", StringUtil.nullToString(data.get("salnum")));
					jsonOne.put("paydept", StringUtil.nullToString(data.get("paydept")));
					jsonOne.put("padepnmc", StringUtil.nullToString(data.get("padepnmc")));
					jsonOne.put("paytitle", StringUtil.nullToString(data.get("paytitle")));
					jsonOne.put("paytitlc", StringUtil.nullToString(data.get("paytitlc")));
					jsonOne.put("payamt", StringUtil.nullToString(data.get("payamt")));
					jsonOne.put("pacodnmc", StringUtil.nullToString(data.get("pacodnmc")));
					jsonOne.put("paycode", StringUtil.nullToString(data.get("paycode")));

					jsonRtn.add(jsonOne);
				}
			}
			log.info(me + "  ...********* dataaccess:共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}

		return jsonRtn;
	}

	// 依KEY,取得pa.pataxtran pa.pataxcash
	public HashMap<String, Object> getTaxPayListbyKey(String code_data, String userid, String hid, VGHDataAccess da)
			throws Exception {
		String me = getClass().getName() + ".getTaxPayListbyKey(),userid = " + userid;
		log.info(me + " code_data = " + code_data + " hid = " + hid);

		HashMap<String, Object> rtnMap = new HashMap();
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_code_data = null;
		Date start = new Date();
		Date end = new Date();
		String strsql_tran = "";
		String strsql_cash = "";
		String idtno = "";
		String payear = "";
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> paramA = new ArrayList<Object>();
		if (code_data != null && !code_data.equals("")) {
			object_code_data = JSONObject.fromObject(code_data);
			idtno = (!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			payear = (!object_code_data.has("payear")) ? "" : object_code_data.getString("payear");
		}
		// 讀取pa.pataxtran
		strsql_tran = "select PAYIDNO, PAYEAR, PAYMONTH, PAYCATG, PAYTAXPAY, PAYTAXDEDUCT, PAYUPDID, PAYUPDTM, HID, "
				+ tmpZtaxtran + " ZPATAXTRAN " + " from " + this.tTaxTran + " where PAYEAR=? and PAYIDNO=? and HID=? "
				+ "order by 3 with ur ";
		params.add(payear);
		params.add(idtno);
		params.add(hid);

		// 讀取pa.pataxcash
		strsql_cash = "select PAYIDNO, PAYEAR, PAYMONTH, PAYCATG, PAYFORMAT, PAYCITATION, PAYTAXPAY, PAYTAXDEDUCT, PAYUPDID, PAYUPDTM, PAYFLAG, HID, "
				+ tmpZtaxcash + " ZPATAXCASH " + " from " + this.tTaxCash + " where PAYEAR=? and PAYIDNO=? and HID=? "
				+ "order by 3 with ur ";
		paramA.add(payear);
		paramA.add(idtno);
		paramA.add(hid);
		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			start = new Date();
			// --轉帳
			log.info(me + " 轉帳所得稅: " + strsql_tran + "...params:" + params.toString());
			result = da.arraylistbyQuery(strsql_tran, params, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					jsonOne = new JSONObject();
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("payear", StringUtil.nullToString(data.get("payear")));
					jsonOne.put("paymonth", StringUtil.nullToString(data.get("paymonth")));
					jsonOne.put("paycatg", StringUtil.nullToString(data.get("paycatg")));
					jsonOne.put("paytaxpay", StringUtil.nullToString(data.get("paytaxpay")));
					jsonOne.put("paytaxdeduct", StringUtil.nullToString(data.get("paytaxdeduct")));
					jsonOne.put("payupdid", StringUtil.nullToString(data.get("payupdid")));
					jsonOne.put("payupdtm", StringUtil.nullToString(data.get("payupdtm")));
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					jsonOne.put("zpataxtran", StringUtil.nullToString(data.get("zpataxtran")));

					jsonRtn.add(jsonOne);
				}
			}
			rtnMap.put("taxTran_json", jsonRtn);
			// --現金所得稅
			log.info(me + " 現金所得稅: " + strsql_cash + "...params:" + paramA.toString());
			result = da.arraylistbyQuery(strsql_cash, paramA, META_TYPE);
			log.info(me + "...size:" + result.size());
			jsonRtn = new JSONArray();
			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					jsonOne = new JSONObject();
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("payear", StringUtil.nullToString(data.get("payear")));
					jsonOne.put("paymonth", StringUtil.nullToString(data.get("paymonth")));
					jsonOne.put("paycatg", StringUtil.nullToString(data.get("paycatg")));
					jsonOne.put("payformat", StringUtil.nullToString(data.get("payformat")));
					jsonOne.put("paycitation", StringUtil.nullToString(data.get("paycitation")));
					jsonOne.put("paytaxpay", StringUtil.nullToString(data.get("paytaxpay")));
					jsonOne.put("paytaxdeduct", StringUtil.nullToString(data.get("paytaxdeduct")));
					jsonOne.put("payupdid", StringUtil.nullToString(data.get("payupdid")));
					jsonOne.put("payupdtm", StringUtil.nullToString(data.get("payupdtm")));
					jsonOne.put("payflag", StringUtil.nullToString(data.get("payflag")));
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					jsonOne.put("zpataxcash", StringUtil.nullToString(data.get("zpataxcash")));

					jsonRtn.add(jsonOne);
				}
			}
			rtnMap.put("taxCash_json", jsonRtn);
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

	// 依KEY,取得pa.padatpay
	public HashMap<String, Object> getDatPayList(String code_data, Padatpsn psnitem, String userid, String hid,
			VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getDatPayList(),userid = " + userid;
		log.info(me + " code_data = " + code_data + " hid = " + hid);
		Padatpay datpayObj = null;
		ArrayList<Padatpay> datpayAry = new ArrayList<Padatpay>(); // 支扣集合
		CommDAO cDAO = null;
		HashMap<String, Object> rtnMap = new HashMap();
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_code_data = null;
		Date start = new Date();
		Date end = new Date();
		String strSelect = "";
		String idtno = "";
		String salnum = null;
		String strPaycode = "";
		String payamtp = "";
		String payamtd = "";
		int i = 0;
		HashMap sumListMap = new HashMap();
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> paramsub = new ArrayList<Object>();
		if (code_data != null && !code_data.equals("")) {
			object_code_data = JSONObject.fromObject(code_data);
			idtno = (!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			salnum = (!object_code_data.has("salnum")) ? "" : object_code_data.getString("salnum");
		}
		// 讀取pa.padatpay+pa.padatpay_dw
		strSelect = " SALNUM, PAYIDNO, A.PAYCODE, PAYAMT, A.PAYAUTH, A.PAYIFTAX, A.HID, PACODNMC,PACODDEL,PAYREWARD ";
		sql = "select " + strSelect + " from " + this.tDatPay + " A " + " left outer join " + this.tPayItm
				+ " B on A.PAYCODE=B.PAYCODE and A.HID=B.HID " + " where SALNUM=? and PAYIDNO=? and A.HID=? "
				+ " union all " + " select " + strSelect + " from " + this.tDatPayDw + " A " + " left outer join "
				+ this.tPayItm + " B on A.PAYCODE=B.PAYCODE and A.HID=B.HID "
				+ " where SALNUM=? and PAYIDNO=? and A.HID=? " + "order by 3 with ur ";
		params.add(salnum);
		params.add(idtno);
		params.add(hid);
		params.add(salnum);
		params.add(idtno);
		params.add(hid);

		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			start = new Date();
			cDAO = new CommDAO(da, hid);
			log.info(me + "  " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					// 放置在Arraylist
					datpayObj = new Padatpay();
					PropertyUtils.copyProperties(datpayObj, data);
					datpayAry.add(datpayObj);

					jsonOne = new JSONObject();
					payamtp = "";
					payamtd = "";
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("salnum", StringUtil.nullToString(data.get("salnum")));
					strPaycode = StringUtil.nullToString(data.get("paycode"));
					jsonOne.put("paycode", strPaycode);
					jsonOne.put("payamt", StringUtil.nullToString(data.get("payamt")));
					if ("P".equals(strPaycode.substring(1, 2)))
						payamtp = StringUtil.nullToString(data.get("payamt"));
					else if ("D".equals(strPaycode.substring(1, 2)))
						payamtd = StringUtil.nullToString(data.get("payamt"));
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
			// 加總
			sumListMap = cDAO.Sum_Pay_Deduct(datpayAry, psnitem.getPayrank(), psnitem.getPayrank2(),
					psnitem.getPayrnk2n(), psnitem.getPaydegre(), psnitem.getStrgovdt(), psnitem.getStrbank());
			rtnMap.put("datpaySum_map", sumListMap);
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

	// 依KEY,取得pa.pabaspay
	public HashMap<String, Object> getBasPayList(String idtno, String tracod, String userid, String hid,
			VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getBasPayList(),userid = " + userid;
		log.info(me + " idtno = " + idtno + " tracod = " + tracod + " hid = " + hid);
		Pabaspay baspayObj = null;
		ArrayList<Pabaspay> baspayAry = new ArrayList<Pabaspay>(); // 支扣集合
		HashMap rtnMap = new HashMap();
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_code_data = null;
		Date start = new Date();
		Date end = new Date();
		String strSelect = "";
		String salnum = null;
		String strPaycode = "";
		String payamtp = "";
		String payamtd = "";
		String coderng = "";
		int i = 0;
		ArrayList<Object> params = new ArrayList<Object>();

		// 讀取pa.pabaspay 12考績獎金,51年終獎金=>讀取FP000 FP010 FP030 FP080
		if (tracod.matches("12|51")) {
			if (hid.matches("1A0|2A0")) {
				coderng = " AND A.PAYCODE IN ('FP000','FP010','FP030','FP080') ";
			} else if (hid.matches("3A0")) {
				coderng = " AND A.PAYCODE IN ('FP000','FP010','FP030','FP080','FP090','FP100') ";
			}
		}
		sql = "select A.PAYIDNO, A.PAYCODE, PAYAMT, A.PAYAUTH, A.PAYIFTAX, A.HID, PACODNMC,PACODDEL,PAYREWARD "
				+ " from " + this.tBasPay + " A " + " left outer join " + this.tPayItm
				+ " B on A.PAYCODE=B.PAYCODE and A.HID=B.HID " + " where A.HID=? and A.PAYIDNO=? " + coderng
				+ " order by a.paycode with ur ";

		params.add(hid);
		params.add(idtno);

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
					baspayObj = new Pabaspay();
					PropertyUtils.copyProperties(baspayObj, data);
					baspayAry.add(baspayObj);

					jsonOne = new JSONObject();
					payamtp = "";
					payamtd = "";
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					strPaycode = StringUtil.nullToString(data.get("paycode"));
					jsonOne.put("paycode", strPaycode);
					jsonOne.put("payamt", StringUtil.nullToString(data.get("payamt")));
					if ("P".equals(strPaycode.substring(1, 2)))
						payamtp = StringUtil.nullToString(data.get("payamt"));
					else if ("D".equals(strPaycode.substring(1, 2)))
						payamtd = StringUtil.nullToString(data.get("payamt"));
					jsonOne.put("payamtd", payamtd);
					jsonOne.put("payamtp", payamtp);
					jsonOne.put("payauth", StringUtil.nullToString(data.get("payauth")));
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
			log.info(me + "  ...********* dataaccess:" + (end.getTime() - start.getTime()));
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}

		return rtnMap;
	}
	
	// 依KEY,取得pa.pahist
	public HashMap<String, Object> getHistList(String idtno, String userid, String hid, VGHDataAccess da)
			throws Exception {
		String me = getClass().getName() + ".getHistList(),userid = " + userid;
		log.info(me + " idtno = " + idtno + " hid = " + hid);
		Pahist pahistObj = null;
		ArrayList<Pahist> pahistAry = new ArrayList<Pahist>(); // 歷史檔集合
		HashMap rtnMap = new HashMap();
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_code_data = null;
		Date start = new Date();
		Date end = new Date();
		String strSelect = "";
		String salnum = null;
		String strPaycode = "";
		String payamtp = "";
		String payamtd = "";
		int i = 0;
		ArrayList<Object> params = new ArrayList<Object>();

		// 讀取pa.pahist
		strSelect = "A.PATRADT,A.PATRACOD, A.PAYIDNO, A.PAYCARD, A.PAYEMPNO, A.PAYNAMEC, A.PAYDEPT, "
				+ "A.PAYTITLE,COALESCE(A.PAYRNK2N,'') as PAYRNK2N, COALESCE(A.PAYRANK3,0) as PAYRANK3, COALESCE(A.PAYDEGR2,0) as PAYDEGR2, "
				+ "COALESCE(A.PAYDEGRE,0) as PAYDEGRE, A.PAYRANK, COALESCE(A.PAYRANK2,'') as PAYRANK2, COALESCE(A.PAYRANKC,'') as PAYRANKC, "
				+ "A.PAYEMPDT, A.PAYRSGDT, A.PAYUPDID, A.PAYUPDTM, A.HID, " + tmpZhist + ","
				+ "B.PADEPNMC,C.PAYTITLP,C.PATITSUB,D.RANKNAME,COALESCE(F.TRANAME,'')  TRANAME ";

		sql = "select " + strSelect + " from " + this.tHist + " A " + " left outer join " + this.tDept
				+ " B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID " + " left outer join " + this.tTitle
				+ " C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID " + " left outer join " + this.tRankCode
				+ " D on A.PAYRANK=D.RANKCODE " + " left outer join " + this.tTraCode + " F on A.PATRACOD=F.TRACODE "
				+ " where A.HID=? and A.PAYIDNO=? ORDER BY A.PAYUPDTM DESC with ur ";

		params.add(hid);
		params.add(idtno);
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
					pahistObj = new Pahist();
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
			log.info(me + "  ...********* dataaccess:" + (end.getTime() - start.getTime()));
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}

		return rtnMap;
	}

	// 健保申報扣費明細PANHIDET:papaydt+paysource+itemnum+payidno+hid
	public HashMap<String, Object> getNhiDetList(String in_data, String userid, String hid, VGHDataAccess da)
			throws Exception {
		String me = getClass().getName() + ".getNhiDetList() userid:" + userid;
		log.info(me + " in_data = " + in_data + " hid = " + hid + " userid = " + userid);
		CommDAO cDAO = null;
		JSONArray jsonRtn = new JSONArray();
		HashMap<String, Object> rtnMap = new HashMap();
		ArrayList<Object> params = new ArrayList<Object>();
		JSONObject jsonOne = null;
		JSONObject object_in_data = null;
		long starttime = 0;
		String subsqlA = "";
		String cardno = "";
		String idtno = "";
		String namec = "";
		String payitem = "";

		String[] payyr = null;
		String[] paymon = null;
		String[] paydt = null;
		int i = 0;
		int tmpPartPayDet = 0;// 兼職給付總額
		int tmpPartNHIDet = 0;// 兼職給付總額
		int tmpGEINFPayDet = 0;// 公保給付總額
		int tmpGEINFNHIDet = 0;// 公保給付總額
		int tmpLBINFPayDet = 0;// 勞保給付總額
		int tmpLBINFNHIDet = 0;// 勞保給付總額
		int intINSUIDENT = 0;// 身份別:0無1公保2勞保
		int intPAYAMT = 0;// 單次獎金
		int intNHIAMT = 0;// 補充保險費金額

		starttime = System.currentTimeMillis();
		if (in_data != null && !in_data.equals("")) {
			object_in_data = JSONObject.fromObject(in_data);
			cardno = (!object_in_data.has("cardno")) ? "" : object_in_data.getString("cardno");
			namec = (!object_in_data.has("namec")) ? "" : object_in_data.getString("namec");
			idtno = (!object_in_data.has("idtno")) ? "" : object_in_data.getString("idtno");
			payitem = (!object_in_data.has("payitem")) ? "" : object_in_data.getString("payitem").split(":")[0];
			payyr = (!object_in_data.has("payyr")) ? null
					: ("".equals(object_in_data.getString("payyr")) ? null
							: object_in_data.getString("payyr").split(","));
			paymon = (!object_in_data.has("paymon")) ? null
					: ("".equals(object_in_data.getString("paymon")) ? null
							: object_in_data.getString("paymon").split(","));
			paydt = (!object_in_data.has("paydt")) ? null
					: ("".equals(object_in_data.getString("paydt")) ? null
							: object_in_data.getString("paydt").split(","));

		}
		sql = "select PAPAYDT, PAYIDNO, PAYITEM, PAYCARD, PAYNAMEC, SERIAL, PAYAMT, NHIAMT, MONINSU, JACKPOT, INSUIDENT, PAYFLAG, PAYUPDID, PAYUPDAT, HID "
				+ " from " + this.tNhiDet + " where HID=? ";
		params.add(hid);
		if (!"".equals(cardno)) {// 卡號
			subsqlA += " and PAYCARD=? ";
			params.add(cardno);
		} else if (!"".equals(idtno)) {// 身分證
			subsqlA += " and PAYIDNO=? ";
			params.add(idtno);
		} else if (!"".equals(namec)) {// 姓名
			subsqlA += " and PAYNAMEC like ? ";
			params.add(namec + "%");
		}
		if (!"".equals(payitem)) { // 類別
			subsqlA += " and PAYITEM= ? ";
			params.add(payitem);
		}
		if (!(payyr == null || (payyr != null && payyr.length == 0))) { // 年
			subsqlA += " and year(PAPAYDT) in ( ";
			for (i = 0; i < payyr.length; i++) {
				if (i != 0) {
					subsqlA += ",";
				}
				subsqlA += "?";
				params.add(payyr[i]);
			}
			subsqlA += ")";
		}
		if (!(paymon == null || (paymon != null && paymon.length == 0))) { // 月
			subsqlA += " and month(PAPAYDT) in ( ";
			for (i = 0; i < paymon.length; i++) {
				if (i != 0) {
					subsqlA += ",";
				}
				subsqlA += "?";
				params.add(paymon[i]);
			}
			subsqlA += ")";
		}
		if (!(paydt == null || (paydt != null && paydt.length == 0))) { // 給付日期
			subsqlA += " and PAPAYDT in ( ";
			for (i = 0; i < paydt.length; i++) {
				if (i != 0) {
					subsqlA += ",";
				}
				subsqlA += "?";
				params.add(paydt[i]);
			}
			subsqlA += ")";
		}
		sql += subsqlA + " order by PAPAYDT desc,PAYCARD with ur";
		log.info(me + "  " + sql + "...params:" + params.toString());

		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}

			log.info(me + "  " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					jsonOne = new JSONObject();

					jsonOne.put("papaydt", StringUtil.nullToString(data.get("papaydt")));
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("payitem", StringUtil.nullToString(data.get("payitem")));
					jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));
					jsonOne.put("paynamec", StringUtil.nullToString(data.get("paynamec")));
					jsonOne.put("serial", StringUtil.nullToString(data.get("serial")));
					intPAYAMT = (Integer) data.get("payamt");
					jsonOne.put("payamt", StringUtil.nullToString(data.get("payamt")));
					intNHIAMT = (Integer) data.get("nhiamt");
					jsonOne.put("nhiamt", intNHIAMT);
					jsonOne.put("moninsu", StringUtil.nullToString(data.get("moninsu")));
					jsonOne.put("jackpot", StringUtil.nullToString(data.get("jackpot")));
					intINSUIDENT = (Integer) data.get("insuident");
					jsonOne.put("insuident", intINSUIDENT);
					jsonOne.put("insunm", cDAO.getInsuident(intINSUIDENT));
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					jsonOne.put("payupdat", formatter.format(data.get("payupdat")));

					jsonRtn.add(jsonOne);
					// 金額加總
					if (intINSUIDENT == 0) {
						tmpPartPayDet += intPAYAMT;
						tmpPartNHIDet += intNHIAMT;
					} else if (intINSUIDENT == 1) {
						tmpGEINFPayDet += intPAYAMT;
						tmpGEINFNHIDet += intNHIAMT;
					} else if (intINSUIDENT == 2) {
						tmpLBINFPayDet += intPAYAMT;
						tmpLBINFNHIDet += intNHIAMT;
					}
				}
			}
			rtnMap.put("nhidet_json", jsonRtn);
			rtnMap.put("PartPayDet", tmpPartPayDet);
			rtnMap.put("PartNHIDet", tmpPartNHIDet);
			rtnMap.put("GEINFPayDet", tmpGEINFPayDet);
			rtnMap.put("GEINFNHIDet", tmpGEINFNHIDet);
			rtnMap.put("LBINFPayDet", tmpLBINFPayDet);
			rtnMap.put("LBINFNHIDet", tmpLBINFNHIDet);

			log.info(me + "  ...********* dataaccess:共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}

		return rtnMap;
	}

	// 補充費_累積數
	public HashMap<String, Object> getNhiAccuList(String in_data, String userid, String hid, VGHDataAccess da)
			throws Exception {
		String me = getClass().getName() + ".getNhiAccuList() userid:" + userid;
		log.info(me + " in_data = " + in_data + " hid = " + hid + " userid = " + userid);
		CommDAO cDAO = null;
		JSONArray jsonRtn = new JSONArray();
		HashMap<String, Object> rtnMap = new HashMap();
		ArrayList<Object> params = new ArrayList<Object>();
		JSONObject jsonOne = null;
		JSONObject object_in_data = null;
		long starttime = 0;
		String subsqlA = "";
		String cardno = "";
		String idtno = "";
		String namec = "";

		String payyr = "";
		int i = 0;

		starttime = System.currentTimeMillis();
		if (in_data != null && !in_data.equals("")) {
			object_in_data = JSONObject.fromObject(in_data);
			cardno = (!object_in_data.has("cardno")) ? "" : object_in_data.getString("cardno");
			namec = (!object_in_data.has("namec")) ? "" : object_in_data.getString("namec");
			idtno = (!object_in_data.has("idtno")) ? "" : object_in_data.getString("idtno");
			payyr = (!object_in_data.has("payyr")) ? "" : object_in_data.getString("payyr");

		}
		sql = "select year(a.PAPAYDT)yy, a.PAYIDNO, PAYCARD, PAYNAMEC, MONINSU, MONINSU4,c.bonusamt,a.hid " + " from "
				+ this.tNhiCal + " a " + "INNER JOIN (SELECT max(papaydt)papaydt,payidno,YEAR(papaydt)yy,hid " + "FROM "
				+ this.tNhiCal
				+ " GROUP BY payidno,YEAR(papaydt),hid) b ON a.payidno=b.payidno AND a.papaydt=b.PAPAYDT and a.hid=b.hid "
				+ "LEFT outer JOIN (SELECT sum(bonusamt)bonusamt,payidno,YEAR(papaydt)yy,hid " + "FROM " + this.tNhiCal
				+ " GROUP BY payidno,YEAR(papaydt),hid) c ON a.payidno=c.payidno AND year(a.papaydt)=c.yy and a.hid=c.hid "
				+ "WHERE a.hid=? AND payitem=? ";
		params.add(hid);
		params.add("62");
		if (!"".equals(payyr)) {// 年
			subsqlA += " and year(a.PAPAYDT) = ? ";
			params.add(payyr);
		}
		if (!"".equals(cardno)) {// 卡號
			subsqlA += " and a.PAYCARD=? ";
			params.add(cardno);
		} else if (!"".equals(idtno)) {// 身分證
			subsqlA += " and a.PAYIDNO=? ";
			params.add(idtno);
		} else if (!"".equals(namec)) {// 姓名
			subsqlA += " and a.PAYNAMEC like ? ";
			params.add(namec + "%");
		}

		sql += subsqlA + " order by a.PAYIDNO with ur";
		log.info(me + "  " + sql + "...params:" + params.toString());

		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}

			log.info(me + "  " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					jsonOne = new JSONObject();

					jsonOne.put("yy", StringUtil.nullToString(data.get("yy")));
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));
					jsonOne.put("paynamec", StringUtil.nullToString(data.get("paynamec")));
					jsonOne.put("moninsu", StringUtil.nullToString(data.get("moninsu")));
					jsonOne.put("moninsu4", StringUtil.nullToString(data.get("moninsu4")));
					jsonOne.put("bonusamt", StringUtil.nullToString(data.get("bonusamt")));
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));

					jsonRtn.add(jsonOne);
				}
			}
			rtnMap.put("nhidet_json", jsonRtn);

			log.info(me + "  ...********* dataaccess:共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}

		return rtnMap;
	}

	// 批次檔 PAbatpay:patradt+payidno+salnum+paycode+hid
	public HashMap<String, Object> getBatPayList(String in_data, String userid, String hid, VGHDataAccess da)
			throws Exception {
		String me = getClass().getName() + ".getBatPayList() userid:" + userid;
		log.info(me + " in_data = " + in_data + " hid = " + hid + " userid = " + userid);
		CommDAO cDAO = null;
		JSONArray jsonRtn = new JSONArray();
		HashMap<String, Object> rtnMap = new HashMap();
		ArrayList<Object> params = new ArrayList<Object>();
		JSONObject jsonOne = null;
		JSONObject object_in_data = null;
		long starttime = 0;
		String subsqlA = "";
		String cardno = "";
		String idtno = "";
		String namec = "";
		String patradt = "";
		String paycode = "";
		String salnum = "";

		starttime = System.currentTimeMillis();
		if (in_data != null && !in_data.equals("")) {
			object_in_data = JSONObject.fromObject(in_data);
			cardno = (!object_in_data.has("cardno")) ? "" : object_in_data.getString("cardno");
			idtno = (!object_in_data.has("idtno")) ? "" : object_in_data.getString("idtno");
			patradt = (!object_in_data.has("patradt")) ? "" : object_in_data.getString("patradt");
			paycode = (!object_in_data.has("paycode")) ? "" : object_in_data.getString("paycode").split(":")[0];
			salnum = (!object_in_data.has("salnum")) ? "" : object_in_data.getString("salnum");
		}
		sql = "select a.hid,a.patradt,a.payidno,a.salnum,a.paycard,a.paycode,payamt,a.payupdat,a.paupbas,b.pacodnmc,b.paupbas,c.paynamec,c.paydept,c.paytitle,c1.padepnmc,c2.paytitlp,d.patracod tracod,d.paynamec tranamec,d.paydept tradept,d.paytitle tratitle,d1.padepnmc tradepnmc,d2.paytitlp tratitlp "
				+ " from " + this.tBatPay + " A " + " left outer join " + this.tPayItm
				+ " B on a.PAYCODE = b.PAYCODE    AND a.HID=B.HID " + " left outer join " + this.tBasPsn
				+ " C on A.PAYIDNO  = C.PAYIDNO   AND A.HID =C.HID " + " LEFT OUTER JOIN " + this.tDept
				+ " C1 ON C.PAYDEPT  = C1.PADEPCOD AND C.HID =C1.HID " + " LEFT OUTER JOIN " + this.tTitle
				+ " C2 ON C.PAYTITLE = C2.PAYTITLK AND C.HID =C2.HID " + " left outer join " + this.tTraPsn
				+ " D on A.PAYIDNO  = D.PAYIDNO   AND A.HID =D.HID AND A.PATRADT = D.PATRADT  AND D.PATRACOD IN (1,2,31,32,33,36,37,38) "
				+ " LEFT OUTER JOIN " + this.tDept + " D1 ON D.PAYDEPT  = D1.PADEPCOD AND D.HID =D1.HID "
				+ " LEFT OUTER JOIN " + this.tTitle + " D2 ON D.PAYTITLE = D2.PAYTITLK AND D.HID =D2.HID "
				+ " where A.HID=? ";
		params.add(hid);

		if (!"".equals(patradt)) { // 發薪日
			subsqlA += " and A.PATRADT= ? ";
			params.add(patradt);
		}
		if (!"".equals(salnum)) { // 餉單編號
			subsqlA += " and A.SALNUM=? ";
			params.add(salnum);
		}
		if (!"".equals(paycode)) { // 支扣項目
			subsqlA += " and A.PAYCODE= ? ";
			params.add(paycode);
		}
		if (!"".equals(cardno)) {// 卡號
			subsqlA += " and A.PAYCARD=? ";
			params.add(cardno);
		} else if (!"".equals(idtno)) {// 身分證
			subsqlA += " and A.PAYIDNO=? ";
			params.add(idtno);
		}

		sql += subsqlA + " order by A.PAYCODE,A.PAYCARD with ur";
		log.info(me + "  " + sql + "...params:" + params.toString());

		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}

			log.info(me + "  " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					jsonOne = new JSONObject();
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					jsonOne.put("patradt", StringUtil.nullToString(data.get("patradt")));
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("salnum", (Integer) data.get("salnum"));
					jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));
					jsonOne.put("paycode", StringUtil.nullToString(data.get("paycode")));
					jsonOne.put("pacodnmc", StringUtil.nullToString(data.get("pacodnmc")));
					jsonOne.put("paupbas", StringUtil.nullToString(data.get("paupbas")));

					jsonOne.put("payamt", (Integer) data.get("payamt"));
					jsonOne.put("payupdat", sdf.format(data.get("payupdat")));
					jsonOne.put("paupbas", StringUtil.nullToString(data.get("paupbas")));

					jsonOne.put("paynamec",
							("".equals(StringUtil.nullToString(data.get("paynamec"))))
									? StringUtil.nullToString(data.get("tranamec"))
									: StringUtil.nullToString(data.get("paynamec")));
					jsonOne.put("paydept",
							("".equals(StringUtil.nullToString(data.get("paydept"))))
									? StringUtil.nullToString(data.get("tradept"))
									: StringUtil.nullToString(data.get("paydept")));
					jsonOne.put("paytitle",
							("".equals(StringUtil.nullToString(data.get("paytitle"))))
									? StringUtil.nullToString(data.get("tratitle"))
									: StringUtil.nullToString(data.get("paytitle")));
					jsonOne.put("padepnmc",
							("".equals(StringUtil.nullToString(data.get("padepnmc"))))
									? StringUtil.nullToString(data.get("tradepnmc"))
									: StringUtil.nullToString(data.get("padepnmc")));
					jsonOne.put("paytitlp",
							("".equals(StringUtil.nullToString(data.get("paytitlp"))))
									? StringUtil.nullToString(data.get("tratitlp"))
									: StringUtil.nullToString(data.get("paytitlp")));
					jsonOne.put("tracod", StringUtil.nullToString(data.get("tracod")));

					jsonRtn.add(jsonOne);
				}
			}
			rtnMap.put("batpay_json", jsonRtn);

			log.info(me + "  ...********* dataaccess:共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}

		return rtnMap;
	}

	// 依補充保費PANHICAL:papaydt+paysource+itemnum+payidno+hid
	public HashMap<String, Object> getNhiCalList(String in_data, String userid, String hid, VGHDataAccess da)
			throws Exception {
		String me = getClass().getName() + ".getNhiCalList() userid:" + userid;
		log.info(me + " in_data = " + in_data + " hid = " + hid + " userid = " + userid);
		CommDAO cDAO = null;
		JSONArray jsonRtn = new JSONArray();
		HashMap<String, Object> rtnMap = new HashMap();
		ArrayList<Object> params = new ArrayList<Object>();
		JSONObject jsonOne = null;
		JSONObject object_in_data = null;
		long starttime = 0;
		String subsqlA = "";
		String cardno = "";
		String idtno = "";
		String namec = "";
		String payitem = "";
		String painsu = "";
		String[] payyr = null;
		String[] paymon = null;
		String[] paydt = null;
		int i = 0;
		int tmpPartPayAmt = 0;// 兼職給付總額
		int tmpPartNHIAmt = 0;// 兼職給付總額
		int tmpGEINFPayAmt = 0;// 公保給付總額
		int tmpGEINFNHIAmt = 0;// 公保給付總額
		int tmpLBINFPayAmt = 0;// 勞保給付總額
		int tmpLBINFNHIAmt = 0;// 勞保給付總額
		int intINSUIDENT = 0;// 身份別:0無1公保2勞保
		int intBONUSAMT = 0;// 單次獎金
		int intNHIAMT = 0;// 補充保險費金額

		starttime = System.currentTimeMillis();
		if (in_data != null && !in_data.equals("")) {
			object_in_data = JSONObject.fromObject(in_data);
			cardno = (!object_in_data.has("cardno")) ? "" : object_in_data.getString("cardno");
			namec = (!object_in_data.has("namec")) ? "" : object_in_data.getString("namec");
			idtno = (!object_in_data.has("idtno")) ? "" : object_in_data.getString("idtno");
			painsu = (!object_in_data.has("painsu")) ? "" : object_in_data.getString("painsu").split(":")[0];
			payitem = (!object_in_data.has("payitem")) ? "" : object_in_data.getString("payitem").split(":")[0];
			payyr = (!object_in_data.has("payyr")) ? null
					: ("".equals(object_in_data.getString("payyr")) ? null
							: object_in_data.getString("payyr").split(","));
			paymon = (!object_in_data.has("paymon")) ? null
					: ("".equals(object_in_data.getString("paymon")) ? null
							: object_in_data.getString("paymon").split(","));
			paydt = (!object_in_data.has("paydt")) ? null
					: ("".equals(object_in_data.getString("paydt")) ? null
							: object_in_data.getString("paydt").split(","));

		}
		sql = "select PAPAYDT, PAYSOURCE, ITEMNUM, a.PAYIDNO, PAYITEM, a.PAYCARD, a.PAYNAMEC, MONINSU, MONINSU4, BONUSAMT, JACKPOT, MTINSU, SUPLYBASE, NHIAMT, INSUIDENT,a.HID,a.PAYUPDAT,b.payempno "
				+ " from " + this.tNhiCal + " A " + " left outer join " + this.tBasPsn
				+ " B on A.PAYIDNO=B.PAYIDNO AND A.HID=B.HID " + " where A.HID=? ";
		params.add(hid);
		if (!"".equals(cardno)) {// 卡號
			subsqlA += " and A.PAYCARD=? ";
			params.add(cardno);
		} else if (!"".equals(idtno)) {// 身分證
			subsqlA += " and A.PAYIDNO=? ";
			params.add(idtno);
		} else if (!"".equals(namec)) {// 姓名
			subsqlA += " and A.PAYNAMEC like ? ";
			params.add(namec + "%");
		}
		if (!"".equals(painsu)) { // 保險別
			subsqlA += " and INSUIDENT= ? ";
			params.add(painsu);
		}
		if (!"".equals(payitem)) { // 類別
			subsqlA += " and PAYITEM= ? ";
			params.add(payitem);
		}
		if (!(payyr == null || (payyr != null && payyr.length == 0))) { // 年
			subsqlA += " and year(PAPAYDT) in ( ";
			for (i = 0; i < payyr.length; i++) {
				if (i != 0) {
					subsqlA += ",";
				}
				subsqlA += "?";
				params.add(payyr[i]);
			}
			subsqlA += ")";
		}
		if (!(paymon == null || (paymon != null && paymon.length == 0))) { // 月
			subsqlA += " and month(PAPAYDT) in ( ";
			for (i = 0; i < paymon.length; i++) {
				if (i != 0) {
					subsqlA += ",";
				}
				subsqlA += "?";
				params.add(paymon[i]);
			}
			subsqlA += ")";
		}
		if (!(paydt == null || (paydt != null && paydt.length == 0))) { // 給付日期
			subsqlA += " and PAPAYDT in ( ";
			for (i = 0; i < paydt.length; i++) {
				if (i != 0) {
					subsqlA += ",";
				}
				subsqlA += "?";
				params.add(paydt[i]);
			}
			subsqlA += ")";
		}
		sql += subsqlA + " order by PAPAYDT desc,A.PAYCARD with ur";
		log.info(me + "  " + sql + "...params:" + params.toString());

		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}

			log.info(me + "  " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					jsonOne = new JSONObject();

					jsonOne.put("papaydt", StringUtil.nullToString(data.get("papaydt")));
					jsonOne.put("paysource", StringUtil.nullToString(data.get("paysource")));
					jsonOne.put("itemnum", StringUtil.nullToString(data.get("itemnum")));
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("payitem", StringUtil.nullToString(data.get("payitem")));
					jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));
					jsonOne.put("paynamec", StringUtil.nullToString(data.get("paynamec")));
					jsonOne.put("moninsu", StringUtil.nullToString(data.get("moninsu")));
					jsonOne.put("moninsu4", StringUtil.nullToString(data.get("moninsu4")));
					intBONUSAMT = (Integer) data.get("bonusamt");
					jsonOne.put("bonusamt", intBONUSAMT);
					jsonOne.put("jackpot", StringUtil.nullToString(data.get("jackpot")));
					jsonOne.put("mtinsu", StringUtil.nullToString(data.get("mtinsu")));
					jsonOne.put("suplybase", StringUtil.nullToString(data.get("suplybase")));
					intNHIAMT = (Integer) data.get("nhiamt");
					jsonOne.put("nhiamt", intNHIAMT);
					intINSUIDENT = (Integer) data.get("insuident");
					jsonOne.put("insuident", intINSUIDENT);
					jsonOne.put("insunm", cDAO.getInsuident(intINSUIDENT));
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					jsonOne.put("payupdat", formatter.format(data.get("payupdat")));
					jsonOne.put("payempno", StringUtil.nullToString(data.get("payempno")));
					jsonRtn.add(jsonOne);
					// 金額加總
					if (intINSUIDENT == 0) {
						tmpPartPayAmt += intBONUSAMT;
						tmpPartNHIAmt += intNHIAMT;
					} else if (intINSUIDENT == 1) {
						tmpGEINFPayAmt += intBONUSAMT;
						tmpGEINFNHIAmt += intNHIAMT;
					} else if (intINSUIDENT == 2) {
						tmpLBINFPayAmt += intBONUSAMT;
						tmpLBINFNHIAmt += intNHIAMT;
					}
				}
			}
			rtnMap.put("nhical_json", jsonRtn);
			rtnMap.put("PartPayAmt", tmpPartPayAmt);
			rtnMap.put("PartNHIAmt", tmpPartNHIAmt);
			rtnMap.put("GEINFPayAmt", tmpGEINFPayAmt);
			rtnMap.put("GEINFNHIAmt", tmpGEINFNHIAmt);
			rtnMap.put("LBINFPayAmt", tmpLBINFPayAmt);
			rtnMap.put("LBINFNHIAmt", tmpLBINFNHIAmt);

			log.info(me + "  ...********* dataaccess:共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}

		return rtnMap;
	}

	// 依salnum+paycode找pa.padaypay清單
	public JSONArray getDayPayByCode(String in_data, String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getDayPayByCode(),userid = " + userid;
		log.info(me + " in_data = " + in_data + " hid = " + hid);

		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_in_data = null;
		Date start = new Date();
		Date end = new Date();
		String strSelect = "";
		String paycode = "";
		String salnum = "";

		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> paramsub = new ArrayList<Object>();
		if (in_data != null && !in_data.equals("")) {
			object_in_data = JSONObject.fromObject(in_data);
			paycode = (!object_in_data.has("paycode")) ? "" : object_in_data.getString("paycode");
			salnum = (!object_in_data.has("salnum")) ? "" : object_in_data.getString("salnum");
		}
		// 讀取pa.padatpay+pa.padatpay_dw
		strSelect = " A.SALNUM,A.PAYIDNO,A.PAYCODE, A.PAYAMT,"
				+ "B.PAYCARD, B.PAYEMPNO,PAYNAMEC, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK2, PAYBANK,"
				+ "C.PADEPNMC,D.PAYTITLC, E.PACODNMC ";
		sql = "select " + strSelect + " from " + this.tDatPay + " A " + " left outer join " + this.tDatPsn
				+ " B on A.SALNUM=B.SALNUM AND A.PAYIDNO=B.PAYIDNO AND A.HID=B.HID " + " left outer join " + this.tDept
				+ " C on B.PAYDEPT=C.PADEPCOD AND B.HID=C.HID " + " left outer join " + this.tTitle
				+ " D ON B.PAYTITLE=D.PAYTITLK AND B.HID=D.HID " + " left outer join " + this.tPayItm
				+ " E ON A.PAYCODE =E.PAYCODE AND A.HID=E.HID " + " where A.SALNUM=? and A.PAYCODE=? and A.HID=? "
				+ " union all " + " select " + strSelect + " from " + this.tDatPayDw + " A " + " left outer join "
				+ this.tDatPsnDw + " B on A.SALNUM=B.SALNUM AND A.PAYIDNO=B.PAYIDNO AND A.HID=B.HID "
				+ " left outer join " + this.tDept + " C on B.PAYDEPT=C.PADEPCOD AND B.HID=C.HID " + " left outer join "
				+ this.tTitle + " D ON B.PAYTITLE=D.PAYTITLK AND B.HID=D.HID " + " left outer join " + this.tPayItm
				+ " E ON A.PAYCODE =E.PAYCODE AND A.HID=E.HID " + " where A.SALNUM=? and A.PAYCODE=? and A.HID=? "
				+ "order by 5 with ur ";
		params.add(salnum);
		params.add(paycode);
		params.add(hid);
		params.add(salnum);
		params.add(paycode);
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
			start = new Date();
			log.info(me + "  " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
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
			log.info(me + "  ...********* dataaccess:" + (end.getTime() - start.getTime()));
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}

		return jsonRtn;
	}

	// 依KEY,取得pa.padatPSN
	public Padatpsn getDatPsnBykey(String in_data, String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getDatPsnBykey(),userid = " + userid;
		log.info(me + " in_data = " + in_data + " hid = " + hid);
		CommDAO cDAO = null;
		HashMap CspfMap = new HashMap(); // 工作獎金支扣 對照表
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_in_data = null;
		Padatpsn padatpsn = null;
		Date start = new Date();
		Date end = new Date();
		String strSelect = "";
		String idtno = "";
		String salnum = null;
		int i = 0;
		ArrayList<Object> params = new ArrayList<Object>();
		ArrayList<Object> paramsub = new ArrayList<Object>();
		if (in_data != null && !in_data.equals("")) {
			object_in_data = JSONObject.fromObject(in_data);
			idtno = (!object_in_data.has("idtno")) ? "" : object_in_data.getString("idtno");
			salnum = (!object_in_data.has("salnum")) ? "" : object_in_data.getString("salnum");
		}
		// 讀取pa.padatpsn+pa.padatpsn_dw
		strSelect = " a.SALNUM, PAYBANK, A.PAYIDNO, A.PAYCARD, A.PAYEMPNO, A.PAYNAMEC, A.PAYDEPT, A.PAYTITLE, A.PAYRNK2N, "
				+ "COALESCE(A.PAYRANK3,0) as PAYRANK3, COALESCE(A.PAYDEGR2,0) as PAYDEGR2, COALESCE(A.PAYDEGRE,0) as PAYDEGRE, A.PAYRANK, A.PAYRANK2, A.PAYRANKC, COALESCE(A.PAYPFDOC,0) as PAYPFDOC, A.PACSFLAG, "
				+ "COALESCE(A.PAYPFSP1,0) as PAYPFSP1, COALESCE(A.PAYPFSP2,0) as PAYPFSP2, COALESCE(A.PAYPFSP3,0) as PAYPFSP3, A.PAYUPDID, A.PAYUPDTM, A.PAYRANK4, A.HID, A.INSUAMT, "
				+ "B.PADEPNMC,C.PAYTITLP,D.RANKNAME,E.RNK4NAME,COALESCE(F.PAYGOVDT, G.PAYGOVDT) as PAYGOVDT ,COALESCE(F.PAYEMPDT, G.PAYEMPDT) as PAYEMPDT ";
		sql = "select " + strSelect + " from " + this.tDatPsn + " A " + " left outer join " + this.tDept
				+ " B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID " + " left outer join " + this.tTitle
				+ " C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID " + " left outer join " + this.tRankCode
				+ " D on A.PAYRANK=D.RANKCODE " + " left outer join " + this.tRnk4Tbl + " E on A.PAYRANK4=E.RNK4KEY "
				+ " left outer join " + this.tBasPsn + " F on A.PAYIDNO=F.PAYIDNO AND A.HID=F.HID "
				+ " left outer join " + this.tTraPsn
				+ " G on A.PAYIDNO=G.PAYIDNO and A.SALNUM=G.SALNUM AND A.HID=G.HID "
				+ " where a.SALNUM=? and A.PAYIDNO=? and A.HID=? " + " union all " + " select " + strSelect + " from "
				+ this.tDatPsnDw + " A " + " left outer join " + this.tDept
				+ " B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID " + " left outer join " + this.tTitle
				+ " C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID " + " left outer join " + this.tRankCode
				+ " D on A.PAYRANK=D.RANKCODE " + " left outer join " + this.tRnk4Tbl + " E on A.PAYRANK4=E.RNK4KEY "
				+ " left outer join " + this.tBasPsn + " F on A.PAYIDNO=F.PAYIDNO AND A.HID=F.HID "
				+ " left outer join " + this.tTraPsn
				+ " G on A.PAYIDNO=G.PAYIDNO and A.SALNUM=G.SALNUM AND A.HID=G.HID "
				+ " where a.SALNUM=? and A.PAYIDNO=? and A.HID=? with ur ";
		params.add(salnum);
		params.add(idtno);
		params.add(hid);
		params.add(salnum);
		params.add(idtno);
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
					padatpsn = new Padatpsn();
					PropertyUtils.copyProperties(padatpsn, data);
					if (hid.matches("1A0|2A0")) {
						// 工作獎金=Y,找基本績分 俸級績分
						if ("Y".equals(padatpsn.getPacsflag())) {
							CspfMap = cDAO.getCsPfByKey(padatpsn.getPayrank3(), padatpsn.getPaytitle(),
									padatpsn.getPayrank(), padatpsn.getPayrnk2n(), padatpsn.getPaydegr2(),
									padatpsn.getStrbank(), hid, da);
							padatpsn.setPfkey((String) CspfMap.get("pfkey"));
							padatpsn.setPfbas((String) CspfMap.get("pfbas"));
							padatpsn.setPfdat((String) CspfMap.get("pfdat"));
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

		return padatpsn;
	}

	// 依KEY,取得製作報表明細PA.PAREPDETAIL
	// itm是報表群組
	// selEn:Prt_M_1薪津表 Prt_M_2扣款表 Prt_M_3薪津表(補發扣) Prt_M_4薪津異動明細表 Prt_M_5發薪受款者明細表
	// Prt_M_6銀行轉帳明細表 Bank_T銀行媒體傳輸
	public ArrayList<Paprtdtl> getPrtdtlBykey(String in_data, int itm, String userid, String hid, VGHDataAccess da)
			throws Exception {
		String me = getClass().getName() + ".getPrtdtlBykey(),userid = " + userid;
		log.info(me + " in_data = " + in_data + " itm = " + itm + " hid = " + hid);
		ArrayList<Paprtdtl> aryRtn = new ArrayList<Paprtdtl>();
		JSONObject object_in_data = null;
		Paprtdtl paprtdtl = null;
		long starttime = 0;
		String idtno = "";

		ArrayList<Object> params = new ArrayList<Object>();
		String selEn = "";
		String selCode = "";
		String selNm = "";
		String patradt = "";
		int salnum = 0;
		String strSelect = "";
		String subWhere = "";
		String strOrder = "";
		// 1:月報:Prt_M_1 selCode:selNm:selEn
		if (in_data != null && !in_data.equals("")) {
			object_in_data = JSONObject.fromObject(in_data);
			selEn = (!object_in_data.has("selEn")) ? "" : object_in_data.getString("selEn");
			selCode = (!object_in_data.has("selCode")) ? "" : object_in_data.getString("selCode");
			selNm = (!object_in_data.has("selNm")) ? "" : object_in_data.getString("selNm");
			patradt = (!object_in_data.has("patradt")) ? "" : object_in_data.getString("patradt");
			salnum = (!object_in_data.has("salnum")) ? 0 : object_in_data.getInt("salnum");
		}
		if (hid.equals("1A0")) {
			if (selEn.matches("Prt_M_1|Prt_M_2")) {
				if (itm == 0) // 總計
					subWhere = " ";
				else if (itm == 1) // 護理_師級
					subWhere = " and SUBSTR(PAYEMPNO,1,3) IN ('W04','W06') ";
				else if (itm == 2) // 護理_契約
					subWhere = " and PAYEMPNO LIKE 'TV%' ";
				else if (itm == 3) // 職員_公職
					subWhere = " and PAYEMPNO LIKE 'A%' AND PAYTITLE <>'16' ";
				else if (itm == 4) // 醫事_契約
					subWhere = " and SUBSTR(PAYEMPNO,1,2) IN ('TA') ";
				else if (itm == 5) // 醫事_師級
					subWhere = " and SUBSTR(PAYEMPNO,1,3) BETWEEN 'W09' AND 'W19' ";
				else if (itm == 6) // 醫師_進修,實習
					subWhere = " and PAYEMPNO LIKE 'TB%' ";
				else if (itm == 7) // 醫師_公職
					subWhere = " and PAYTITLE ='16' ";
				else if (itm == 8) // 醫師_師級
					subWhere = " and SUBSTR(PAYEMPNO,1,3) IN ('W01','W02','W03') ";
				else if (itm == 9) // 醫師_聘用
					subWhere = " and SUBSTR(PAYEMPNO,1,1) IN ('C') ";
				else if (itm == 10) // 醫師_契約
					subWhere = " and SUBSTR(PAYEMPNO,1,1) IN ('J','E') ";
				else if (itm == 11) // 不分科
					subWhere = " and SUBSTR(PAYEMPNO,1,1) IN ('F') ";
				else if (itm == 12) // 大武
					subWhere = " and SUBSTR(PAYEMPNO,1,1) IN ('Y') ";
				else if (itm == 13) // 兼職
					subWhere = " and SUBSTR(PAYEMPNO,1,1) IN ('B') ";
				else if (itm == 14) // 技工友
					subWhere = " and SUBSTR(PAYEMPNO,1,1) IN ('G','H') ";
				else if (itm == 15) // 計畫
					subWhere = " and SUBSTR(PAYEMPNO,1,1) IN ('Z') ";
				else if (itm == 16) // 駐衛警
					subWhere = " and SUBSTR(PAYEMPNO,1,2) IN ('TP') ";
				else if (itm == 17) // 外籍照服員 2025.9.15
					subWhere = " and SUBSTR(PAYEMPNO,1,2) IN ('TF') ";
			}
			// 薪津表(補發扣)
			else if (selEn.matches("Prt_M_3")) {
				subWhere = " and (PAYRBASE<>0 OR PAYRSPSU<>0 OR PAYRSPSD <> 0 OR PAYRCHALW<>0 OR PAYRSUPLY<> 0 )";
			}
			// 薪津異動明細表
			else if (selEn.matches("Prt_M_4")) {
				subWhere = " and TRANAME <> '' "
						+ " and NOT ( (PAYOBASE + PAYOSPSU + PAYOCHALW + PAYOSUPLY) =(PAYBASE + PAYSPSU + PAYCHALW  + PAYSUPLY) AND (PAYRBASE + PAYRSPSU + PAYRCHALW  + PAYRSUPLY)=0 ) ";
			}
			// 發薪受款者明細表
			else if (selEn.matches("Prt_M_5")) {
				subWhere = " GROUP BY PAYBANK ";
			}
			// Prt_M_6銀行轉帳明細表 Bank_T銀行傳輸
			else if (selEn.matches("Prt_M_6|Bank_T")) {
				if (itm == 0) // 薪津
					subWhere = " and LTRIM(PAYACNO)<>'' AND (PAYNET + PAYTOTIS) > 0 ";
				else if (itm == 1) // 公儲
					subWhere = " and LTRIM(PAYGEDNO)<>'' AND PAYGEDP > 0 ";
				else if (itm == 2) // 停薪
					subWhere = " ";
			}
		} else if (hid.equals("2A0")) {
			if (selEn.matches("Prt_M_1|Prt_M_2")) {
				if (itm == 0) // 總計
					subWhere = " ";
				else if (itm == 1) // 護理_師級
					subWhere = " and PAYRANK='8' AND PATITSUB='N' ";
				else if (itm == 2) // 護理_契約
					subWhere = " and PAYRANK='F' AND PAYTITLP LIKE '%護%' ";
				else if (itm == 3) // 職員_公職
					subWhere = " and PAYRANK IN ('1','2','3') ";
				else if (itm == 4) // 醫事_契約
					subWhere = " and PAYRANK='F' AND not (PAYTITLP LIKE ('%醫師%') OR PAYTITLP LIKE ('%護%')) ";
				else if (itm == 5) // 醫事_師級
					subWhere = " and PAYRANK='8' AND not (PAYTITLP LIKE ('%醫師%') OR PAYTITLP LIKE ('%護理%')) ";
				else if (itm == 6) // 醫師_師級+聘用
					subWhere = " and (PAYRANK='8' AND PATITSUB='V') or PAYRANK IN ('E') ";
				else if (itm == 7) // 醫師_契約
					subWhere = " and PAYRANK='F' AND PAYTITLP LIKE '%醫師%' ";
				else if (itm == 8) // 技工友
					subWhere = " and PAYRANK IN ('5','6') ";
			}
			// 薪津表(補發扣)
			else if (selEn.matches("Prt_M_3")) {
				subWhere = " and (PAYRBASE<>0 OR PAYRSPSU<>0 OR PAYRSPSD <> 0 OR PAYRCHALW<>0 OR PAYRSTBN<> 0) ";
			}
			// 薪津異動明細表
			else if (selEn.matches("Prt_M_4")) {
				subWhere = " and TRANAME <> '' "
						+ " and NOT ( (PAYOBASE + PAYOSPSU + PAYOCHALW + PAYOSTBN) = (PAYBASE + PAYSPSU + PAYCHALW  + PAYSTBNT) AND (PAYRBASE + PAYRSPSU + PAYRCHALW  + PAYRSTBN)=0 ) ";
			}
			// 發薪受款者明細表
			else if (selEn.matches("Prt_M_5")) {
				subWhere = " GROUP BY PAYBANK ";
			}
			// Prt_M_6銀行轉帳明細表 Bank_T銀行傳輸
			else if (selEn.matches("Prt_M_6|Bank_T")) {
				if (itm == 0) // 薪津
					subWhere = " and LTRIM(PAYACNO)<>'' AND (PAYNET + PAYTOTIS) > 0 ";
				else if (itm == 1) // 公儲
					subWhere = " and LTRIM(PAYGEDNO)<>'' AND PAYGEDP > 0 ";
			}
		} else if (hid.equals("3A0")) {
			if (selEn.matches("Prt_M_1|Prt_M_2")) {
				if (itm == 0) // 總計
					subWhere = " ";
				else if (itm == 1) // 公職
					subWhere = " and PAYRANK in('1','2','3','8','9','H','G') ";
				else if (itm == 2) // 工級
					subWhere = " and PAYRANK IN ('5','6') ";
				else if (itm == 3) // 契約
					subWhere = " and PAYRANK not in('1','2','3','8','9','5','6','H','G') ";
			}
			// 薪津表(補發扣)
			else if (selEn.matches("Prt_M_3")) {
				subWhere = " and (PAYRBASE<>0 OR PAYRSPSU<>0 OR PAYRSPSD <> 0 OR PAYRCHALW<>0 OR PAYRSTBN<> 0 or PAYRSUPLY<>0)";
			}
			// 薪津異動明細表
			else if (selEn.matches("Prt_M_4")) {
				subWhere = " and TRANAME <> '' "
						+ " and NOT ( (PAYOBASE + PAYOSPSU + PAYOCHALW + PAYOSTBN + PAYOSUPLY) = (PAYBASE + PAYSPSU + PAYCHALW  + PAYSTBNT + PAYSUPLY) AND (PAYRBASE + PAYRSPSU + PAYRCHALW  + PAYRSTBN +PAYRSUPLY)=0 ) ";
			}
			// 發薪受款者明細表
			else if (selEn.matches("Prt_M_5")) {
				subWhere = " GROUP BY PAYBANK ";
			}
			// Prt_M_6銀行轉帳明細表 Bank_T銀行傳輸
			else if (selEn.matches("Prt_M_6|Bank_T")) {
				if (itm == 0) // 薪津
					subWhere = " and LTRIM(PAYACNO)<>'' AND (PAYNET + PAYTOTIS) > 0 ";
				else if (itm == 1) // 公儲
					subWhere = " and LTRIM(PAYGEDNO)<>'' AND PAYGEDP > 0 ";
			}
		}

		// Prt_M_1薪津表 Prt_M_2扣款表 Prt_M_3薪津表(補發扣) Prt_M_4薪津異動明細表
		if (selEn.matches("Prt_M_1|Prt_M_2|Prt_M_3|Prt_M_4")) {
			strSelect = " PAYIDNO, PAYBANK, PAYCARD, PAYNAMEC, PAYEMPNO, PAYACNO, PAYGEDNO, PAYDEPT, PADEPNMC, PATITSUB, PAYTITLE, PAYTITLP, PAYRANK, RANKABBR, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYLVEFG, TRANAME, case PAEFFDT when '9999-12-31' then PAYBANK else PAEFFDT end as PAEFFDT , LASTDAYNO, PAYDAYNO, PAYOBASE, PAYOSPSU, PAYOCHALW, PAYOSTBN, PAYOSUPLY, PAYBASE, PAYRBASE, PAYSPSU, PAYRSPSU, PAYSPSD, PAYRSPSD, PAYCHALW, PAYRCHALW, PAYSTBNT, PAYRSTBN, PAYTRANS, PAYSUPLY, PAYRSUPLY, PAYPFNT, PAYSTAL, PAYSTALD, PAYNIGHT, PAYYEAR, PAYADDHL, PAYGRADE, PAYPROMO, PAYADRTN, PAYCPLM1, PAYCPLM2, PAYCPLM3, PAYEMPFD, PAYSARS, PAYEXTRA, PAYGEINS, PAYDPINS, PAYLBINS, PAYRETIR, PAYTAX, PAYGEDP, PAYVETD1, PAYHLOA1, PAYHLOA2, PAYHLOA3, PAYHLOA4, PAYHLOA5, PAYHLOA6, PAYDLOAN, PAYMHOUS, PAYNSPFD, PAYPARK1, PAYPARK2, PAYGYM, PAYLIEN, PAYMEAL, PAYCARDF, PAYDORM1, PAYDORM2, PAYDORM3, PAYOTHER1, PAYOTHER2, PAYOTHER3, PAYAGEN1, PAYTOTAL, PAYTOTIS, PAYTLDED, PAYNET, HID, SALNUM ";
			if (hid.matches("1A0|2A0"))
				strOrder = " order by PAYEMPNO,PAYTITLP,PAYCARD ";
			else if (hid.matches("3A0"))
				strOrder = " order by paycard ";
		}
		// Prt_M_5發薪受款者明細表
		else if (selEn.matches("Prt_M_5")) {
			if (hid.matches("1A0"))
				// 2025-8-20高榮 PAYOTHER2 PGY眷舍管理費
				strSelect = " PAYBANK, SUM(PAYNET + PAYTOTIS + PAYGEDP) AS pay, SUM(PAYHLOA5) AS PAYHLOA5,SUM(PAYHLOA3) AS PAYHLOA3,SUM(PAYHLOA2) AS PAYHLOA2,SUM(PAYHLOA4) AS PAYHLOA4, "
						+ "SUM(PAYHLOA1) AS PAYHLOA1,SUM(PAYHLOA6) AS PAYHLOA6,SUM(PAYGEINS) AS PAYGEINS,SUM(PAYDPINS) AS PAYDPINS,SUM(PAYLBINS) AS PAYLBINS,SUM(PAYOTHER1) AS PAYOTHER1,SUM(PAYDLOAN) AS PAYDLOAN,SUM(PAYRETIR) AS PAYRETIR,"
						+ "SUM(PAYTAX) AS PAYTAX,SUM(PAYMHOUS) AS PAYMHOUS,SUM(PAYNSPFD) AS PAYNSPFD,SUM(PAYPARK1) AS PAYPARK1,SUM(PAYPARK2) AS PAYPARK2,SUM(PAYOTHER2) AS PAYOTHER2, SUM(PAYGYM) AS PAYGYM,SUM(PAYLIEN) AS PAYLIEN,"
						+ "SUM(PAYMEAL) AS PAYMEAL,SUM(PAYCARDF) AS PAYCARDF,SUM(PAYOTHER3) AS PAYOTHER3, SUM(PAYAGEN1) AS PAYAGEN1,SUM(PAYVETD1) AS PAYVETD1,SUM(PAYTOTAL + PAYTOTIS - PAYDORM1 - PAYDORM2 - PAYDORM3 - PAYOTHER2) AS NT1,"
						+ "SUM(PAYDORM1) AS PAYDORM1,SUM(PAYDORM2) AS PAYDORM2, SUM(PAYDORM3) AS PAYDORM3,SUM(PAYTOTAL + PAYTOTIS) As NT2,sum(PAYGEDP) AS PAYGEDP";
			else if (hid.matches("2A0"))
				strSelect = "PAYBANK,SUM(PAYNET + PAYTOTIS + PAYGEDP) AS pay, SUM(PAYGEINS) AS PAYGEINS,SUM(PAYDPINS) AS PAYDPINS, SUM(PAYLBINS) AS PAYLBINS,SUM(PAYOTHER1) AS PAYOTHER1, SUM(PAYDLOAN) AS PAYDLOAN, "
						+ "SUM(PAYRETIR) AS PAYRETIR,SUM(PAYTAX) AS PAYTAX,SUM(PAYMHOUS) AS PAYMHOUS,SUM(PAYNSPFD) AS PAYNSPFD,SUM(PAYPARK1) AS PAYPARK1,SUM(PAYGYM) AS PAYGYM,SUM(PAYAGEN1) AS PAYAGEN1,SUM(PAYLIEN) AS PAYLIEN,"
						+ "SUM(PAYDORM1) AS PAYDORM1,SUM(PAYDORM2) AS PAYDORM2,SUM(PAYDORM3) AS PAYDORM3,SUM(PAYOTHER2) AS PAYOTHER2,SUM(PAYTOTAL + PAYTOTIS) AS NT1";
			else if (hid.matches("3A0"))
				strSelect = "PAYBANK, SUM(PAYNET + PAYTOTIS + PAYGEDP) AS pay,SUM(PAYGEINS) AS PAYGEINS,SUM(PAYDPINS) AS PAYDPINS,SUM(PAYLBINS) AS PAYLBINS, "
						+ "SUM(PAYRETIR) AS PAYRETIR,SUM(PAYTAX) AS PAYTAX,SUM(PAYDLOAN) AS PAYDLOAN,SUM(PAYOTHER1) AS PAYOTHER1,SUM(PAYLIEN) AS PAYLIEN,SUM(PAYTOTAL + PAYTOTIS) AS NT1";
		}
		// Prt_M_6銀行轉帳明細表
		else if (selEn.matches("Prt_M_6")) {
			if (hid.matches("1A0")) {
				strOrder = " ORDER BY DEPOSIT,ACNO ";
				// 薪津
				if (String.valueOf(itm).matches("0"))
					strSelect = " SUBSTR(PAYACNO,1,5) AS DEPOSIT, SUBSTR(PAYACNO,6,7) AS ACNO, (PAYNET + PAYTOTIS) AS PAYAMT, "
							+ "PAYCARD, PAYNAMEC, PAYBANK";
				// 公儲
				else if (String.valueOf(itm).matches("1"))
					strSelect = " SUBSTR(PAYGEDNO,1,5) AS DEPOSIT, SUBSTR(PAYGEDNO,6,7) AS ACNO, PAYGEDP AS PAYAMT, "
							+ "PAYCARD, PAYNAMEC, PAYBANK";
				// 停薪
				else if (String.valueOf(itm).matches("2")) {
					strSelect = " SUBSTR(PAYCTCBACNO, 1, 5) AS DEPOSIT, SUBSTR(PAYCTCBACNO, 6, 7) AS ACNO, 0 AS PAYAMT, "
							+ " PAYCARD, PAYNAMEC, date('" + patradt + "') AS PAYBANK ";
				}
			} else if (hid.matches("2A0")) {
				// 薪津
				if (String.valueOf(itm).matches("0")) {
					strOrder = " ORDER BY PAYACNO ";
					strSelect = " PAYACNO, (PAYNET + PAYTOTIS) AS PAYAMT,PAYIDNO,PAYNAMEC, PAYBANK";
				}
				// 公儲
				else if (String.valueOf(itm).matches("1")) {
					strOrder = " ORDER BY PAYGEDNO ";
					strSelect = " PAYGEDNO AS PAYACNO, PAYGEDP AS PAYAMT,PAYIDNO,PAYNAMEC, PAYBANK";
				}
			} else if (hid.matches("3A0")) {
				strOrder = " ORDER BY PAYCARD ";
				// 薪津
				if (String.valueOf(itm).matches("0")) {
					strSelect = " PAYACNO, (PAYNET + PAYTOTIS) AS PAYAMT,PAYIDNO,PAYNAMEC, PAYBANK,PAYTITLP,PAYCARD";
				}
				// 公儲
				else if (String.valueOf(itm).matches("1")) {
					strSelect = " PAYGEDNO AS PAYACNO, PAYGEDP AS PAYAMT,PAYIDNO,PAYNAMEC, PAYBANK,PAYTITLP,PAYCARD";
				}
			}
		}
		// Bank_T銀行傳輸
		else if (selEn.matches("Bank_T")) {
			if (hid.matches("1A0")) {
				strOrder = " ORDER BY PAYACNO ";
				// 薪津
				if (String.valueOf(itm).matches("0"))
					strSelect = " RTRIM(PAYACNO) AS PAYACNO, (PAYNET + PAYTOTIS) AS PAYAMT, PAYBANK,PAYIDNO";
				// 公儲
				else if (String.valueOf(itm).matches("1"))
					strSelect = " RTRIM(PAYGEDNO) AS PAYACNO, PAYGEDP AS PAYAMT, PAYBANK,PAYIDNO";
				// 停薪
				else if (String.valueOf(itm).matches("2")) {
					strSelect = " RTRIM(PAYCTCBACNO) AS PAYACNO , 0 AS PAYAMT, date('" + patradt
							+ "') AS PAYBANK,PAYIDNO ";
				}
			} else if (hid.matches("2A0|3A0")) {
				strOrder = " ORDER BY ACNO ";
				// 薪津
				if (String.valueOf(itm).matches("0")) {
					strSelect = " PAYACNO AS ACNO, (PAYNET + PAYTOTIS) AS PAYAMT, PAYBANK, PAYIDNO";
				}
				// 公儲
				else if (String.valueOf(itm).matches("1")) {
					strSelect = " PAYGEDNO AS ACNO, PAYGEDP AS PAYAMT, PAYBANK, PAYIDNO";
				}
			}

		}
		// sql
		// 1A0高榮-2停薪-[Prt_M_6銀行轉帳明細表/Bank_T銀行傳輸]
		if (hid.equals("1A0") && String.valueOf(itm).matches("2") && selEn.matches("Prt_M_6|Bank_T")) {
			sql = "select " + strSelect + " from " + this.tTraPsn + " where HID=? and SALNUM=? and PATRACOD in (23,24) "
					+ " union" + " select " + strSelect + " from " + this.tBasPsn
					+ " where PATRADT >= '2007-9-1' AND PATRACOD in (23,24) AND HID=? "
					+ "  AND PAYIDNO NOT IN (SELECT PAYIDNO FROM " + this.tTraPsn
					+ " WHERE PATRACOD=2 AND HID=? and SALNUM=?  ) " + strOrder + " with ur";

			params.add(hid);
			params.add(salnum);
			params.add(hid);
			params.add(hid);
			params.add(salnum);
		}
		// 高榮-發薪受款者明細表 ,要另找帳號空白
		else if (selEn.matches("Prt_M_5") && hid.equals("1A0")) {
			sql = "select " + strSelect + " from " + this.tRPDTL + " where HID=?  and SALNUM=? " + subWhere + " union "
					+ "select " + strSelect + " from " + this.tRPDTL + " where HID=?  and SALNUM=? and payacno='' "
					+ subWhere + " ORDER BY pay DESC with ur ";
			params.add(hid);
			params.add(salnum);
			params.add(hid);
			params.add(salnum);
		}
		// 2A0屏榮-Bank_T銀行傳輸
		else if (hid.matches("2A0|3A0") && selEn.matches("Bank_T")) {
			sql = "select '' AS ACNO,sum(paynet)+sum(PAYTOTIS) PAYAMT,DATE('" + patradt
					+ "') AS PAYBANK,TO_CHAR(COUNT(*)) AS PAYIDNO " + " from " + this.tRPDTL
					+ " where HID=?  and SALNUM=? " + subWhere + " union " + "select " + strSelect + " from "
					+ this.tRPDTL + " where HID=?  and SALNUM=? " + subWhere + strOrder + "  with ur ";
			params.add(hid);
			params.add(salnum);
			params.add(hid);
			params.add(salnum);
		} else {
			sql = "select " + strSelect + " from " + this.tRPDTL + " where HID=? " + " and SALNUM=? " + subWhere
					+ strOrder + " with ur";

			params.add(hid);
			params.add(salnum);
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

			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					paprtdtl = new Paprtdtl();
					PropertyUtils.copyProperties(paprtdtl, data);
					aryRtn.add(paprtdtl);
				}
			}
			log.info(me + ",筆數:" + result.size() + "共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");

		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}
		return aryRtn;
	}

	// 依KEY,取得pa.pataxPSN
	public Pataxpsn getTaxPsnBykey(String code_data, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getTaxPsnBykey()";
		log.info(me + " code_data = " + code_data + " hid = " + hid);
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_code_data = null;
		Pataxpsn pataxpsn = null;
		Date start = new Date();
		Date end = new Date();
		String strSelect = "";
		String idtno = "";
		String payear = "";
		int payserno = 0;

		ArrayList<Object> params = new ArrayList<Object>();

		if (code_data != null && !code_data.equals("")) {
			object_code_data = JSONObject.fromObject(code_data);
			idtno = (!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			payear = (!object_code_data.has("payear")) ? "" : object_code_data.getString("payear");
		}
		// 讀取pa.pataxpsn
		strSelect = "a.PAYIDNO, PAYEAR, PAYCARD, PAYNAMEC, PAYDEPT, PAYIDFLG, PAYBEGIN, PAYEND, PAYSERNO, a.PAYUPDID, PAYUPDTM, PAYSERNO9A, PAYSERNO9B, PAYSEREAMT, PAYSERNO92, PAYSERNO91, a.HID, PAYEMPNO, PAYSERNO51, "
				+ "B.PADEPNMC,c.PAYADDRS ";
		sql = "select " + strSelect + " from " + this.tTaxPsn + " A " + " left outer join " + this.tDept
				+ " B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID " + " left outer join " + this.tBasAdr
				+ " C on A.PAYIDNO=C.PAYIDNO " + " where A.HID=? and A.PAYIDNO=? and a.payear=?  with ur ";
		params.add(hid);
		params.add(idtno);
		params.add(payear);
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
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());
			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					pataxpsn = new Pataxpsn();
					data.put("payidflg", StringUtil.nullToString(data.get("payidflg")));
					data.put("paynamec", StringUtil.nullToString(data.get("paynamec")));
					payserno = (Integer) ((data.get("payserno") == null) ? 0 : data.get("payserno"));
					data.put("payserno", payserno);
					PropertyUtils.copyProperties(pataxpsn, data);
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
		return pataxpsn;
	}

	// 依KEY(idtno,cardno),取得pa.pabasPSN
	public Pabaspsn getBasPsnBykey(String code_data, String userid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".getBasPsnBykey(),userid = " + userid;
		log.info(me + " code_data = " + code_data + " hid = " + hid);
		CommDAO cDAO = null;
		HashMap CspfMap = new HashMap(); // 工作獎金支扣 對照表
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		JSONObject object_code_data = null;
		Pabaspsn pabaspsn = null;
		Date start = new Date();
		Date end = new Date();
		String strSelect = "";
		String idtno = "";
		String cardno = "";

		ArrayList<Object> params = new ArrayList<Object>();

		if (code_data != null && !code_data.equals("")) {
			object_code_data = JSONObject.fromObject(code_data);
			idtno = (!object_code_data.has("idtno")) ? "" : object_code_data.getString("idtno");
			cardno = (!object_code_data.has("cardno")) ? "" : object_code_data.getString("cardno");
			hid = (!object_code_data.has("hid")) ? "" : object_code_data.getString("hid");
		}
		// 讀取pa.pabaspsn
		strSelect = "PAYLVEFG, A.PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYGOVDT, PAYEMPDT, PAYSGNDT, COALESCE(PAYRSGDT,'9999-12-31') PAYRSGDT, PAYBKID, PAYACNO, "
				+ "PAYGEDNO, PAYINFLG, PAYDPNO, PAYPSW, PAYTITLE, PAYRNK2N, COALESCE(A.PAYRANK3,0) as PAYRANK3, COALESCE(A.PAYDEGR2,0) as PAYDEGR2, "
				+ "COALESCE(A.PAYDEGRE,0) as PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, COALESCE(A.PAYPFDOC,0) as PAYPFDOC, PACSFLAG, "
				+ "COALESCE(A.PAYPFSP1,0) as PAYPFSP1, COALESCE(A.PAYPFSP2,0) as PAYPFSP2, COALESCE(A.PAYPFSP3,0) as PAYPFSP3, A.PATRACOD, "
				+ "PATRADT, PAYTAXNO, A.PAYUPDID, A.PAYUPDTM, PAYEXPFKEY1, PAYEXPFKEY2, PAYEXPFKEY3, PAYRANK4, PAYCTCBACNO, PAYCTCBGEDNO, "
				+ "INSUAMT, PARETENTION, A.HID, " + tmpZbaspsn + ","
				+ "B.PADEPNMC,C.PAYTITLP,C.PATITSUB,D.RANKNAME,E.RNK4NAME,F.TRANAME,G.PAYADDRS ";
		sql = "select " + strSelect + " from " + this.tBasPsn + " A " + " left outer join " + this.tDept
				+ " B on A.PAYDEPT=B.PADEPCOD and A.HID=B.HID " + " left outer join " + this.tTitle
				+ " C on A.PAYTITLE=C.PAYTITLK and A.HID=C.HID " + " left outer join " + this.tRankCode
				+ " D on A.PAYRANK=D.RANKCODE " + " left outer join " + this.tRnk4Tbl + " E on A.PAYRANK4=E.RNK4KEY "
				+ " left outer join " + this.tTraCode + " F on A.PATRACOD=F.TRACODE " + " left outer join "
				+ this.tBasAdr + " G on A.PAYIDNO=G.PAYIDNO " + " where A.HID=? and ";
		params.add(hid);
		if (!idtno.equals("")) {
			sql += "A.PAYIDNO=? order by A.payrsgdt desc with ur ";
			params.add(idtno);
		} else if (!cardno.equals("")) {
			sql += "A.PAYCARD=? order by A.payrsgdt desc with ur ";
			params.add(cardno);
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
			cDAO = new CommDAO(da, hid);
			start = new Date();
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());
			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					pabaspsn = new Pabaspsn();
					PropertyUtils.copyProperties(pabaspsn, data);
					// 工作獎金=Y,找基本績分 俸級績分
					if (hid.matches("1A0|2A0")) {
						if ("Y".equals(pabaspsn.getPacsflag())) {
							CspfMap = cDAO.getCsPfByKey(pabaspsn.getPayrank3(), pabaspsn.getPaytitle(),
									pabaspsn.getPayrank(), pabaspsn.getPayrnk2n(), pabaspsn.getPaydegr2(), "9999-12-31",
									hid, da);
							pabaspsn.setPfkey((String) CspfMap.get("pfkey"));
							pabaspsn.setPfbas((String) CspfMap.get("pfbas"));
							pabaspsn.setPfdat((String) CspfMap.get("pfdat"));
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
		return pabaspsn;
	}

	// 依table名單及KEY,取得各項參考檔
	public JSONArray qryCodeList(String tblname, String code_data, String userid, String hid, VGHDataAccess da)
			throws Exception {
		String me = getClass().getName() + ".qryCodeList(),userid = " + userid;
		log.info(me + " tblname = " + tblname + " code_data = " + code_data + " hid = " + hid);
		JSONArray jsonRtn = new JSONArray();
		JSONObject jsonOne = null;
		Date start = new Date();
		Date end = new Date();
		ArrayList<Object> params = new ArrayList<Object>();
		String subsql = "";
		sql = "";
		String key = "";
		JSONObject object_code_data = null;
		if (code_data != null && !code_data.equals("")) {
			object_code_data = JSONObject.fromObject(code_data);
			if (tblname.matches("PATITLE|PATAXTBL|PAPOSI|PARANK|PAPAYITM")) {// KEY不止一項

			} else {
				key = StringUtil.nullToString(object_code_data.getString("key"));
			}
		}

		if ("PATRACODE".equals(tblname)) {// 異動代碼 PATRACOD

			sql = "SELECT TRANAME, TRACODE FROM " + this.tTraCode;
			if (!key.equals("")) {
				subsql += " where TRACODE = ? ";
				params.add(key);
			}
			sql = sql + subsql + " ORDER BY TRACODE WITH UR";
		}
		// 單位代碼
		else if ("PADEPT".equals(tblname)) {

			sql = "SELECT PADEPCOD, PACSTNO, PADEPNMC, PAYUPDID, PAYUPDAT, HID,PBWKCODE " + "FROM " + this.tDept
					+ " where hid=? ";
			params.add(hid);
			if (!key.equals("")) {
				subsql += " and PADEPCOD = ? ";
				params.add(key);
			}
			sql = sql + subsql + " ORDER BY PACSTNO,PADEPCOD WITH UR";
		} else if ("PARANKCODE".equals(tblname)) {// 階別代碼

			sql = "SELECT RANKCODE,RANKNAME,RANKABBR FROM " + this.tRankCode;
			if (!key.equals("")) {
				subsql += " where RANKCODE = ? ";
				params.add(key);
			}

			sql = sql + subsql + " ORDER BY RANKCODE WITH UR";
		} else if ("PACATGTBL".equals(tblname)) {// 人工造冊應計轉帳項目代碼

			sql = "SELECT PAYCATG, CATGNAME, PAYUPDID, PAYUPDAT FROM " + this.tCatgTbl;
			if (!key.equals("")) {
				subsql += " where PAYCATG = ? ";
				params.add(key);
			}

			sql = sql + subsql + " ORDER BY PAYCATG WITH UR";
		} else if ("PAEXPFTBL".equals(tblname)) {// 績效額外點數對照表

			sql = "SELECT EXPFKEY, EXPFNAME, PAYUPDID, PAYUPDAT FROM " + this.tExpfTbl;
			if (!key.equals("")) {
				subsql += " where EXPFKEY = ? ";
				params.add(key);
			}

			sql = sql + subsql + " ORDER BY EXPFKEY WITH UR";
		} else if ("PARNK4TBL".equals(tblname)) {// 適用專業加給對照表

			sql = "SELECT RNK4KEY, RNK4NAME, PAYUPDTM FROM " + this.tRnk4Tbl;
			if (!key.equals("")) {
				subsql += " where RNK4KEY = ? ";
				params.add(key);
			}

			sql = sql + subsql + " ORDER BY RNK4KEY WITH UR";
		} else if ("PABANK".equals(tblname)) {// 發薪日及入帳日對應檔

			sql = "SELECT SALNUM, PAYBANK, PAYSALDT, PAYUPDID, PAYUPDAT,HID FROM " + this.tBankDtTbl + " where hid=?";
			params.add(hid);
			if (!key.equals("")) {
				subsql += " and SALNUM = ? ";
				params.add(key);
			}

			sql = sql + subsql + " ORDER BY SALNUM DESC WITH UR";
		} else if ("PATAXTBL".equals(tblname)) {// 薪資所得扣繳稅額表

			sql = "SELECT SALLOW, SALHIGH, KINDRED, TAXAMT, PAYUPDID, PAYUPDAT FROM " + this.tTaxTbl;
			if (!object_code_data.getString("sallow").equals("")) {
				subsql += " where SALLOW = ? ";
				params.add(StringUtil.nullToString(object_code_data.getString("sallow")));
				if (!object_code_data.getString("kindred").equals("")) {
					subsql += " and KINDRED=? ";
					params.add(StringUtil.nullToString(object_code_data.getString("kindred")));
				}
			}

			sql = sql + subsql + " ORDER BY SALLOW, KINDRED WITH UR";
		} else if ("PAPFTL".equals(tblname)) {// 全院績效彙總

			sql = "SELECT PAYPFMON, PAYPFUNT, PAPFAMT, PAYPFPF, PAYPFREV, PAYPFYMD, PAYPFD, PAYPFE FROM "
					+ this.tPftlTbl;
			if (!key.equals("")) {
				subsql += " where PAYPFMON = ? ";
				params.add(key);
			}

			sql = sql + subsql + " ORDER BY PAYPFMON desc WITH UR";
		} else if ("PADPINTBL".equals(tblname)) {// 全民健康保險負擔金額表

			sql = "SELECT DPINLOW, DPINHIGH, DPINAMT, PAYUPDID, PAYUPDAT, DPINDEPT1, DPINDEPT3, " + tmpZdpintbl
					+ " FROM " + this.tDpinTbl;
			if (!key.equals("")) {
				subsql += " where DPINLOW = ? ";
				params.add(key);
			}

			sql = sql + subsql + " ORDER BY DPINLOW WITH UR";
		} else if ("PALBINTBL".equals(tblname)) {// 勞保投保金額對照表

			sql = "SELECT LBINLOW, LBINHIGH, LBINAMT, LBINDEPT, " + tmpZlbintbl + " FROM " + this.tLbinTbl;
			if (!key.equals("")) {
				subsql += " where LBINLOW = ? ";
				params.add(key);
			}

			sql = sql + subsql + " ORDER BY LBINLOW WITH UR";
		} else if ("PALBRPTBL".equals(tblname)) {// 勞工退休金月提繳工資分級表

			sql = "SELECT LBRPLOW, LBRPHIGH, LBRPAMT, MASTERAMT,ASSOCC, " + tmpZrpintbl + " FROM " + this.tLbrpTbl;
			if (!key.equals("")) {
				subsql += " where LBRPLOW = ? ";
				params.add(key);
			}

			sql = sql + subsql + " ORDER BY LBRPLOW WITH UR";
		}
		// 職稱
		else if ("PATITLE".equals(tblname)) {

			sql = "SELECT PAYTITLK, PATITSUB, PACHIEF, PAYTITLP, PAYTITLC, PACODDEL, PAYUPDID, PAYUPDAT, HID " + "FROM "
					+ this.tTitle + " where hid=? ";
			params.add(hid);
			if (!StringUtil.nullToString(object_code_data.getString("paytitlk")).equals("")) {
				subsql += " and PAYTITLK = ? ";
				params.add(StringUtil.nullToString(object_code_data.getString("paytitlk")));
			}

			if (!StringUtil.nullToString(object_code_data.get("patitsub")).equals("")) {
				subsql += " and PATITSUB=? ";
				params.add(StringUtil.nullToString(object_code_data.get("patitsub")));
			}
			if (!StringUtil.nullToString(object_code_data.get("pacoddel")).equals("")) {
				subsql += " and PACODDEL=? ";
				params.add(StringUtil.nullToString(object_code_data.get("pacoddel")));
			}
			sql = sql + subsql + " ORDER BY PATITSUB,PAYTITLK WITH UR";
		} else if ("PAPOSI".equals(tblname)) {// 職等支扣--專業加給及主管加給

			sql = "SELECT PAYPOSIK, DEGRLOW, DEGRHIGH, PAYPSP1, PAYPSP2, PAYPSP3, PAYPSP4, PAYPSP5, PAYPSP6, PAYPOSID, PAYPSP7, PAYUPDAT, PAYPSP8 FROM "
					+ this.tPosiTbl;
			if (!StringUtil.nullToString(object_code_data.getString("payposik")).equals("")) {
				subsql += " where PAYPOSIK = ? ";
				params.add(StringUtil.nullToString(object_code_data.getString("payposik")));
			}

			if (!StringUtil.nullToString(object_code_data.get("degrlow")).equals("")) {
				if ("".equals(subsql))
					subsql += " where ";
				else
					subsql += " and ";
				subsql += " DEGRLOW=? ";
				params.add(StringUtil.nullToString(object_code_data.get("degrlow")));
			}

			sql = sql + subsql + " ORDER BY PAYPOSIK, DEGRLOW WITH UR";
		} else if ("PARANK".equals(tblname)) {// 階級支扣

			sql = "SELECT PAYLEV1V, PAYLEV2V, PAYDEGRV, PAYRANKV, PAYBASEV, PAYGEINF, PAYLBINF, PAYDPINV, PAYGEINN, PAYREBEF, PAYREBEN,PAYUSEID, PAYUPDAT "
					+ "FROM " + this.tRankTbl + " where hid=? ";
			params.add(hid);
			if (!StringUtil.nullToString(object_code_data.get("payrankv")).equals("")) {
				subsql += " and PAYRANKV=? ";
				params.add(StringUtil.nullToString(object_code_data.get("payrankv")));
			}
			if (!StringUtil.nullToString(object_code_data.getString("paylev1v")).equals("")) {
				subsql += " and PAYLEV1V = ? ";
				params.add(StringUtil.nullToString(object_code_data.getString("paylev1v")));
			}
			if (!StringUtil.nullToString(object_code_data.get("paylev2v")).equals("")) {
				subsql += " and PAYLEV2V=? ";
				params.add(StringUtil.nullToString(object_code_data.get("paylev2v")));
			}

			sql = sql + subsql + " ORDER BY PAYRANKV, PAYLEV1V, PAYLEV2V  WITH UR";
		} else if ("PACSPF".equals(tblname)) {// 工作獎金支扣

			sql = "SELECT PACSKEY, PACSBASE, PACSDATA, PACSNAME, " + tmpZcspf + " FROM " + this.tCspfTbl;
			if (!key.equals("")) {
				if (key.length() == 2)
					subsql += " where substr(PACSKEY,1,2)= ? ";
				else
					subsql += " where PACSKEY= ? ";
				params.add(key);
			}
			sql = sql + subsql + " ORDER BY PACSKEY WITH UR";
		} else if ("PAPAYITM".equals(tblname)) {// 支扣項目

			sql = "SELECT PAYCODE, PACODNMC, PACODDEL, PAYAUTH, PAYIFTAX, PAUPBAS, PAYREWARD, PAYUPDID, PAYUPDAT, HID, "
					+ tmpZpayitm + " FROM " + this.tPayItm;
			if (!StringUtil.nullToString(object_code_data.get("paycode")).equals("")) {
				subsql += " where PAYCODE like ? ";
				params.add(StringUtil.nullToString(object_code_data.get("paycode")) + "%");
			}
			if (!StringUtil.nullToString(object_code_data.get("pacoddel")).equals("")) {
				if ("".equals(subsql))
					subsql += " where ";
				else
					subsql += " and ";
				subsql += " PACODDEL=? ";
				params.add(StringUtil.nullToString(object_code_data.get("pacoddel")));
			}
			sql = sql + subsql + " ORDER BY PAYCODE WITH UR";
		}
		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			start = new Date();
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "  " + sql + "...params:" + params.toString() + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					jsonOne = new JSONObject();
					if ("PATRACODE".equals(tblname)) {// 異動代碼 PATRACODE
						jsonOne.put("tracode", StringUtil.nullToString(data.get("tracode")));
						jsonOne.put("traname", StringUtil.nullToString(data.get("traname")));
					} else if ("PADEPT".equals(tblname)) {// 單位代碼 PAdept
						jsonOne.put("padepcod", StringUtil.nullToString(data.get("padepcod")));
						jsonOne.put("pacstno", StringUtil.nullToString(data.get("pacstno")));
						jsonOne.put("padepnmc", StringUtil.nullToString(data.get("padepnmc")));
						jsonOne.put("payupdid", StringUtil.nullToString(data.get("payupdid")));
						jsonOne.put("payupdat", StringUtil.nullToString(data.get("payupdat")) == "" ? ""
								: sdf.format(data.get("payupdat")));
						jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
						jsonOne.put("pbwkcode", StringUtil.nullToString(data.get("pbwkcode")));
					} else if ("PARANKCODE".equals(tblname)) {// 階別代碼
						jsonOne.put("rankcode", StringUtil.nullToString(data.get("rankcode")));
						jsonOne.put("rankname", StringUtil.nullToString(data.get("rankname")));
						jsonOne.put("rankabbr", StringUtil.nullToString(data.get("rankabbr")));
					} else if ("PACATGTBL".equals(tblname)) {// 人工造冊應計轉帳項目代碼
						jsonOne.put("paycatg", StringUtil.nullToString(data.get("paycatg")));
						jsonOne.put("catgname", StringUtil.nullToString(data.get("catgname")));
						jsonOne.put("payupdid", StringUtil.nullToString(data.get("payupdid")));
						jsonOne.put("payupdat", StringUtil.nullToString(data.get("payupdat")) == "" ? ""
								: sdf.format(data.get("payupdat")));
					} else if ("PAEXPFTBL".equals(tblname)) {// 績效額外點數對照表
						jsonOne.put("expfkey", StringUtil.nullToString(data.get("expfkey")));
						jsonOne.put("expfname", StringUtil.nullToString(data.get("expfname")));
						jsonOne.put("payupdid", StringUtil.nullToString(data.get("payupdid")));
						jsonOne.put("payupdat", StringUtil.nullToString(data.get("payupdat")) == "" ? ""
								: sdf.format(data.get("payupdat")));
					} else if ("PARNK4TBL".equals(tblname)) {// 適用專業加給對照表
						jsonOne.put("rnk4key", StringUtil.nullToString(data.get("rnk4key")));
						jsonOne.put("rnk4name", StringUtil.nullToString(data.get("rnk4name")));
						jsonOne.put("payupdtm", StringUtil.nullToString(data.get("payupdtm")) == "" ? ""
								: sdf.format(data.get("payupdtm")));
					} else if ("PABANK".equals(tblname)) {// 發薪日及入帳日對應檔
						jsonOne.put("salnum", StringUtil.nullToString(data.get("salnum")));
						jsonOne.put("paybank", StringUtil.nullToString(data.get("paybank")));
						jsonOne.put("paysaldt", StringUtil.nullToString(data.get("paysaldt")));
						jsonOne.put("payupdid", StringUtil.nullToString(data.get("payupdid")));
						jsonOne.put("payupdat", StringUtil.nullToString(data.get("payupdat")) == "" ? ""
								: sdf.format(data.get("payupdat")));
						jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					} else if ("PATAXTBL".equals(tblname)) {// 薪資所得扣繳稅額表
						jsonOne.put("sallow", StringUtil.nullToString(data.get("sallow")));
						jsonOne.put("kindred", StringUtil.nullToString(data.get("kindred")));
						jsonOne.put("salhigh", StringUtil.nullToString(data.get("salhigh")));
						jsonOne.put("taxamt", StringUtil.nullToString(data.get("taxamt")));
						jsonOne.put("payupdid", StringUtil.nullToString(data.get("payupdid")));
						jsonOne.put("payupdat", StringUtil.nullToString(data.get("payupdat")) == "" ? ""
								: sdf.format(data.get("payupdat")));
					} else if ("PAPFTL".equals(tblname)) {// 全院績效彙總
						jsonOne.put("paypfmon", StringUtil.nullToString(data.get("paypfmon")));
						jsonOne.put("paypfunt", StringUtil.nullToString(data.get("paypfunt")));
						jsonOne.put("papfamt", StringUtil.nullToString(data.get("papfamt")));
						jsonOne.put("paypfpf", StringUtil.nullToString(data.get("paypfpf")));
						jsonOne.put("paypfrev", StringUtil.nullToString(data.get("paypfrev")));
						jsonOne.put("paypfd", StringUtil.nullToString(data.get("paypfd")));
						jsonOne.put("paypfe", StringUtil.nullToString(data.get("paypfe")));
						jsonOne.put("paypfymd", StringUtil.nullToString(data.get("paypfymd")) == "" ? ""
								: sdf.format(data.get("paypfymd")));
					} else if ("PADPINTBL".equals(tblname)) {// 全民健康保險負擔金額表
						jsonOne.put("dpinlow", StringUtil.nullToString(data.get("dpinlow")));
						jsonOne.put("dpinhigh", StringUtil.nullToString(data.get("dpinhigh")));
						jsonOne.put("dpinamt", StringUtil.nullToString(data.get("dpinamt")));
						jsonOne.put("dpindept1", StringUtil.nullToString(data.get("dpindept1")));
						jsonOne.put("dpindept3", StringUtil.nullToString(data.get("dpindept3")));
						jsonOne.put("payupdid", StringUtil.nullToString(data.get("payupdid")));
						if (hid.equals("1A0"))
							jsonOne.put("zpadpintbl", StringUtil.nullToString(data.get("zpadpintbl")) == "" ? ""
									: sdf.format(data.get("zpadpintbl")));
						else if (hid.equals("2A0"))
							jsonOne.put("zpadpintbl", StringUtil.nullToString(data.get("zptdpintbl")) == "" ? ""
									: sdf.format(data.get("zptdpintbl")));
						else if (hid.equals("3A0"))
							jsonOne.put("zpadpintbl", StringUtil.nullToString(data.get("zlhdpintbl")) == "" ? ""
									: sdf.format(data.get("zlhdpintbl")));
					} else if ("PALBINTBL".equals(tblname)) {// 勞保投保金額對照表
						jsonOne.put("lbinlow", StringUtil.nullToString(data.get("lbinlow")));
						jsonOne.put("lbinhigh", StringUtil.nullToString(data.get("lbinhigh")));
						jsonOne.put("lbinamt", StringUtil.nullToString(data.get("lbinamt")));
						jsonOne.put("lbindept", StringUtil.nullToString(data.get("lbindept")));
						jsonOne.put("zpalbintbl", StringUtil.nullToString(data.get("zpalbintbl")) == "" ? ""
								: sdf.format(data.get("zpalbintbl")));
					} else if ("PALBRPTBL".equals(tblname)) {// 勞工退休金月提繳工資分級表
						jsonOne.put("lbrplow", StringUtil.nullToString(data.get("lbrplow")));
						jsonOne.put("lbrphigh", StringUtil.nullToString(data.get("lbrphigh")));
						jsonOne.put("lbrpamt", StringUtil.nullToString(data.get("lbrpamt")));
						jsonOne.put("masteramt", StringUtil.nullToString(data.get("masteramt")));
						jsonOne.put("assocc", StringUtil.nullToString(data.get("assocc")));
						jsonOne.put("zpalbrptbl", StringUtil.nullToString(data.get("zpalbrptbl")) == "" ? ""
								: sdf.format(data.get("zpalbrptbl")));
					} else if ("PATITLE".equals(tblname)) {// 職稱代碼
						jsonOne.put("paytitlk", StringUtil.nullToString(data.get("paytitlk")));
						jsonOne.put("patitsub", StringUtil.nullToString(data.get("patitsub")));
						jsonOne.put("pachief", StringUtil.nullToString(data.get("pachief")));
						jsonOne.put("paytitlp", StringUtil.nullToString(data.get("paytitlp")));
						jsonOne.put("paytitlc", StringUtil.nullToString(data.get("paytitlc")));
						jsonOne.put("pacoddel", StringUtil.nullToString(data.get("pacoddel")));
						jsonOne.put("payupdid", StringUtil.nullToString(data.get("payupdid")));
						jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
						jsonOne.put("payupdat", StringUtil.nullToString(data.get("payupdat")) == "" ? ""
								: sdf.format(data.get("payupdat")));
					} else if ("PAPOSI".equals(tblname)) {// 職等支扣--專業加給及主管加給
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
						jsonOne.put("payupdat", StringUtil.nullToString(data.get("payupdat")) == "" ? ""
								: sdf.format(data.get("payupdat")));
					} else if ("PARANK".equals(tblname)) {// 階級支扣
						jsonOne.put("paylev1v", StringUtil.nullToString(data.get("paylev1v")));
						jsonOne.put("paylev2v", StringUtil.nullToString(data.get("paylev2v")));
						jsonOne.put("paydegrv", StringUtil.nullToString(data.get("paydegrv")));
						jsonOne.put("payrankv", StringUtil.nullToString(data.get("payrankv")));
						jsonOne.put("paybasev", StringUtil.nullToString(data.get("paybasev")));
						jsonOne.put("paygeinf", StringUtil.nullToString(data.get("paygeinf")));
						jsonOne.put("paylbinf", StringUtil.nullToString(data.get("paylbinf")));
						jsonOne.put("paydpinv", StringUtil.nullToString(data.get("paydpinv")));
						jsonOne.put("paygeinn", StringUtil.nullToString(data.get("paygeinn")));
						jsonOne.put("payrebef", StringUtil.nullToString(data.get("payrebef")));
						jsonOne.put("payreben", StringUtil.nullToString(data.get("payreben")));
						jsonOne.put("payuseid", StringUtil.nullToString(data.get("payuseid")));
						jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
						jsonOne.put("payupdat", StringUtil.nullToString(data.get("payupdat")) == "" ? ""
								: sdf.format(data.get("payupdat")));
					} else if ("PACSPF".equals(tblname)) {// 工作獎金支扣
						jsonOne.put("pacskey", StringUtil.nullToString(data.get("pacskey")));
						jsonOne.put("pacsbase", StringUtil.nullToString(data.get("pacsbase")));
						jsonOne.put("pacsdata", StringUtil.nullToString(data.get("pacsdata")));
						jsonOne.put("pacsname", StringUtil.nullToString(data.get("pacsname")));
						jsonOne.put("zpacspf", StringUtil.nullToString(data.get("zpacspf")) == "" ? ""
								: sdf.format(data.get("zpacspf")));
					} else if ("PAPAYITM".equals(tblname)) {// 支扣項目
						jsonOne.put("paycode", StringUtil.nullToString(data.get("paycode")));
						jsonOne.put("pacodnmc", StringUtil.nullToString(data.get("pacodnmc")));
						jsonOne.put("pacoddel", StringUtil.nullToString(data.get("pacoddel")));
						jsonOne.put("payauth", StringUtil.nullToString(data.get("payauth")));
						jsonOne.put("payiftax", StringUtil.nullToString(data.get("payiftax")));
						jsonOne.put("payauth", StringUtil.nullToString(data.get("payauth")));
						jsonOne.put("paupbas", StringUtil.nullToString(data.get("paupbas")));
						jsonOne.put("payauth", StringUtil.nullToString(data.get("payauth")));
						jsonOne.put("payreward", StringUtil.nullToString(data.get("payreward")));
						jsonOne.put("payupdid", StringUtil.nullToString(data.get("payupdid")));
						jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
						jsonOne.put("payupdat", StringUtil.nullToString(data.get("payupdat")) == "" ? ""
								: sdf.format(data.get("payupdat")));
					}
					jsonRtn.add(jsonOne);
				}
			}
			end = new Date();
			log.info(me + "  " + sql + "...********* dataaccess:" + (end.getTime() - start.getTime()));
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}
		return jsonRtn;
	}

	/**
	 * insert/update (代碼維護 ) item:insert/update,tabJson(tblname,value) 傳出:true/false
	 */
	public boolean updSalCodeTbl(String item, String tabJson, String userid, String hid, VGHDataAccess da)
			throws SQLException {
		String me = getClass().getName() + ".updSalCodeTbl(),userid = " + userid;
		log.info(me + " item = " + item + " tabJson = " + tabJson + " userid = " + userid + " hid = " + hid);
		boolean success = false;
		int updCnt = 0;
		String formnm = "";
		ArrayList<Object> params = new ArrayList<Object>();
		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}

			JSONArray tabJsonary = JSONArray.fromObject(tabJson);
			for (int i = 0; i < tabJsonary.size(); ++i) {
				JSONObject obj = tabJsonary.getJSONObject(i);
				if (obj != null && obj.has("formnm")) {
					formnm = StringUtil.nullToString(obj.getString("formnm"));
					// 異動代碼
					if ("PATRACODE".equals(formnm)) {
						if (item.equals("update"))
							sql = " update " + this.tTraCode + " set (TRANAME) = (?) where TRACODE=? ";
						else if (item.equals("insert"))
							sql = " insert into " + this.tTraCode + " (TRANAME, TRACODE) values (?,?) ";
					}
					// 單位
					else if ("PADEPT".equals(formnm)) {
						if (item.equals("update"))
							sql = " update " + this.tDept
									+ " set (PACSTNO, PADEPNMC, PAYUPDID, PAYUPDAT,PBWKCODE) = (?,?,?,CURRENT TIMESTAMP,?) where HID=? and PADEPCOD=? ";
						else if (item.equals("insert"))
							sql = " insert into " + this.tDept
									+ " (PACSTNO, PADEPNMC, PAYUPDID, PAYUPDAT,PBWKCODE, HID,PADEPCOD) values (?,?,?,CURRENT TIMESTAMP,?,?,?) ";
					}
					// 階別代碼
					else if ("PARANKCODE".equals(formnm)) {
						if (item.equals("update"))
							sql = " update " + this.tRankCode + " set (RANKNAME, RANKABBR) = (?,?) where RANKCODE=? ";
						else if (item.equals("insert"))
							sql = " insert into " + this.tRankCode + " (RANKNAME,RANKABBR,RANKCODE) values (?,?,?) ";
					}
					// 人工造冊應計轉帳項目代碼
					else if ("PACATGTBL".equals(formnm)) {
						if (item.equals("update"))
							sql = " update " + this.tCatgTbl
									+ " set (CATGNAME,PAYUPDID, PAYUPDAT) = (?,?,CURRENT TIMESTAMP) where PAYCATG=? ";
						else if (item.equals("insert"))
							sql = " insert into " + this.tCatgTbl
									+ " (CATGNAME,PAYUPDID, PAYUPDAT,PAYCATG) values (?,?,CURRENT TIMESTAMP,?) ";
					}
					// 績效額外點數對照表
					else if ("PAEXPFTBL".equals(formnm)) {
						if (item.equals("update"))
							sql = " update " + this.tExpfTbl
									+ " set (EXPFNAME, PAYUPDID, PAYUPDAT) = (?,?,CURRENT TIMESTAMP) where EXPFKEY=? ";
						else if (item.equals("insert"))
							sql = " insert into " + this.tExpfTbl
									+ " (EXPFNAME, PAYUPDID, PAYUPDAT,EXPFKEY) values (?,?,CURRENT TIMESTAMP,?) ";
					}
					// 適用專業加給對照表
					else if ("PARNK4TBL".equals(formnm)) {
						if (item.equals("update"))
							sql = " update " + this.tRnk4Tbl
									+ " set (RNK4NAME, PAYUPDTM) = (?,CURRENT TIMESTAMP) where RNK4KEY=? ";
						else if (item.equals("insert"))
							sql = " insert into " + this.tRnk4Tbl
									+ " (RNK4NAME, PAYUPDTM,RNK4KEY) values (?,CURRENT TIMESTAMP,?) ";
					}
					// 發薪日及入帳日對應檔
					else if ("PABANK".equals(formnm)) {
						if (item.equals("update"))
							sql = " update " + this.tBankDtTbl
									+ " set (PAYBANK, PAYSALDT, PAYUPDID, PAYUPDAT) = (?,?,?,CURRENT TIMESTAMP) where SALNUM=? and HID=?";
						else if (item.equals("insert"))
							sql = " insert into " + this.tBankDtTbl
									+ " (PAYBANK, PAYSALDT, PAYUPDID, PAYUPDAT,SALNUM,HID) values (?,?,?,CURRENT TIMESTAMP,?,?) ";
					}
					// 薪資所得扣繳稅額表
					else if ("PATAXTBL".equals(formnm)) {
						if (item.equals("update"))
							sql = " update " + this.tTaxTbl
									+ " set ( SALHIGH, TAXAMT, PAYUPDID, PAYUPDAT) = (?,?,?,CURRENT TIMESTAMP) where SALLOW=? and KINDRED=? ";
						else if (item.equals("insert"))
							sql = " insert into " + this.tTaxTbl
									+ " (SALHIGH, TAXAMT, PAYUPDID, PAYUPDAT,SALLOW,KINDRED) values (?,?,?,CURRENT TIMESTAMP,?,?) ";
					}
					// 全院績效彙總
					else if ("PAPFTL".equals(formnm)) {
						if (item.equals("update"))
							sql = " update " + this.tPftlTbl
									+ " set ( PAYPFUNT, PAPFAMT, PAYPFPF, PAYPFREV, PAYPFYMD, PAYPFD, PAYPFE) = (?,?,?,?,CURRENT TIMESTAMP,?,?) where PAYPFMON=? ";
						else if (item.equals("insert"))
							sql = " insert into " + this.tPftlTbl
									+ " (PAYPFUNT, PAPFAMT, PAYPFPF, PAYPFREV, PAYPFYMD, PAYPFD, PAYPFE,PAYPFMON) values (?,?,?,?,CURRENT TIMESTAMP,?,?,?) ";
					}
					// 全民健康保險負擔金額表
					else if ("PADPINTBL".equals(formnm)) {
						if (item.equals("update"))
							sql = " update " + this.tDpinTbl
									+ " set (DPINHIGH, DPINAMT, PAYUPDID, PAYUPDAT, DPINDEPT1, DPINDEPT3) = (?,?,?,CURRENT TIMESTAMP,?,?) where DPINLOW=? ";
						else if (item.equals("insert"))
							sql = " insert into " + this.tDpinTbl
									+ " (DPINHIGH, DPINAMT, PAYUPDID, PAYUPDAT, DPINDEPT1, DPINDEPT3,DPINLOW) values (?,?,?,CURRENT TIMESTAMP,?,?,?) ";
					}
					// 勞保投保金額對照表
					else if ("PALBINTBL".equals(formnm)) {
						if (item.equals("update"))
							sql = " update " + this.tLbinTbl
									+ " set (LBINHIGH, LBINAMT, LBINDEPT) = (?,?,?) where LBINLOW=? ";
						else if (item.equals("insert"))
							sql = " insert into " + this.tLbinTbl
									+ " (LBINHIGH, LBINAMT, LBINDEPT,LBINLOW) values (?,?,?,?) ";
					}
					// 勞工退休金月提繳工資分級表
					else if ("PALBRPTBL".equals(formnm)) {
						if (item.equals("update"))
							sql = " update " + this.tLbrpTbl
									+ " set (LBRPHIGH, LBRPAMT, MASTERAMT,ASSOCC) = (?,?,?,?) where LBRPLOW=? ";
						else if (item.equals("insert"))
							sql = " insert into " + this.tLbrpTbl
									+ " (LBRPHIGH, LBRPAMT, MASTERAMT,ASSOCC,LBRPLOW) values (?,?,?,?,?) ";
					}
					// 職稱代碼
					else if ("PATITLE".equals(formnm)) {
						if (item.equals("update"))
							sql = " update " + this.tTitle
									+ " set (PATITSUB, PACHIEF, PAYTITLP, PAYTITLC, PACODDEL, PAYUPDID, PAYUPDAT) = (?,?,?,?,?,?,CURRENT TIMESTAMP) where PAYTITLK=? and HID=? ";
						else if (item.equals("insert"))
							sql = " insert into " + this.tTitle
									+ " (PATITSUB, PACHIEF, PAYTITLP, PAYTITLC, PACODDEL, PAYUPDID, PAYUPDAT, PAYTITLK,HID) values (?,?,?,?,?,?,CURRENT TIMESTAMP,?,?) ";
					}
					// 支扣代碼
					else if ("PAPAYITM".equals(formnm)) {
						if (item.equals("update"))
							sql = " update " + this.tPayItm
									+ " set (PACODNMC, PACODDEL, PAYAUTH, PAYIFTAX, PAUPBAS, PAYREWARD, PAYUPDID, PAYUPDAT) = (?,?,?,?,?,?,?,CURRENT TIMESTAMP) where PAYCODE=? and HID=? ";
						else if (item.equals("insert"))
							sql = " insert into " + this.tPayItm
									+ " (PACODNMC, PACODDEL, PAYAUTH, PAYIFTAX, PAUPBAS, PAYREWARD, PAYUPDID, PAYUPDAT,PAYCODE,HID) values (?,?,?,?,?,?,?,CURRENT TIMESTAMP,?,?) ";
					}
					// 職等支扣--專業加給及主管加給
					else if ("PAPOSI".equals(formnm)) {
						if (item.equals("update"))
							sql = " update " + this.tPosiTbl
									+ " set (DEGRHIGH, PAYPSP1, PAYPSP2, PAYPSP3, PAYPSP4, PAYPSP5, PAYPSP6, PAYPSP7, PAYPSP8, PAYPOSID, PAYUPDAT) = (?,?,?,?,?,?,?,?,?,?,CURRENT TIMESTAMP) where PAYPOSIK=? and DEGRLOW=? ";
						else if (item.equals("insert"))
							sql = " insert into " + this.tPosiTbl
									+ " (DEGRHIGH, PAYPSP1, PAYPSP2, PAYPSP3, PAYPSP4, PAYPSP5, PAYPSP6, PAYPSP7, PAYPSP8, PAYPOSID, PAYUPDAT, PAYPOSIK, DEGRLOW) values (?,?,?,?,?,?,?,?,?,?,CURRENT TIMESTAMP,?,?) ";
					}
					// 階級支扣
					else if ("PARANK".equals(formnm)) {
						if (item.equals("update"))
							sql = " update " + this.tRankTbl
									+ " set (PAYDEGRV, PAYBASEV, PAYGEINF, PAYLBINF, PAYDPINV, PAYGEINN,PAYREBEF,PAYREBEN, PAYUSEID, PAYUPDAT) = (?,?,?,?,?,?,?,?,?,CURRENT TIMESTAMP) where PAYLEV1V=? and PAYLEV2V=? and PAYRANKV=? and HID=?";
						else if (item.equals("insert"))
							sql = " insert into " + this.tRankTbl
									+ " (PAYDEGRV, PAYBASEV, PAYGEINF, PAYLBINF, PAYDPINV, PAYGEINN,PAYREBEF, PAYREBEN,PAYUSEID, PAYUPDAT,PAYLEV1V, PAYLEV2V, PAYRANKV,HID) values (?,?,?,?,?,?,?,?,?,CURRENT TIMESTAMP,?,?,?,?) ";
					}
					// 工作獎金支扣
					else if ("PACSPF".equals(formnm)) {
						if (item.equals("update"))
							sql = " update " + this.tCspfTbl
									+ " set (PACSBASE, PACSDATA, PACSNAME) = (?,?,?) where PACSKEY=? ";
						else if (item.equals("insert"))
							sql = " insert into " + this.tCspfTbl
									+ " (PACSBASE, PACSDATA, PACSNAME, PACSKEY) values (?,?,?,?) ";
					}
					// 異動支扣
					else if ("PATRAPAY".equals(formnm)) {
						if (item.equals("update"))
							sql = " update " + this.tTraPay
									+ " set (PAYIDNO, PAYAMT, PAYAUTH, PAYIFTAX, PAYUPDTM, PAYFLAG) = (?,?,?,?,current timestapm,?) where TRANUM=? and PATRADT=? and PAYSORT =? and PAYCODE=? and HID=? ";
						else if (item.equals("insert"))
							sql = " insert into " + this.tTraPay
									+ " (PAYIDNO, PAYAMT, PAYAUTH, PAYIFTAX, PAYUPDTM, PAYFLAG,TRANUM,PATRADT,PAYSORT ,PAYCODE,HID) values (?,?,?,?,current timestamp,?,?,?,?,?,?) ";
					}

					JSONArray array = obj.getJSONArray("value");
					params = new ArrayList<Object>();
					if (null != array && array.size() > 0) {
						for (int j = 0; j < array.size(); j++) {
							JSONObject obj_item = array.getJSONObject(j);
							if ("PATRACODE".equals(formnm)) {// 異動代碼
								params.add((!obj_item.has("traname")) ? "" : obj_item.getString("traname"));
								params.add((!obj_item.has("tracode")) ? "" : obj_item.getString("tracode"));
							} else if ("PADEPT".equals(formnm)) {// 單位
								params.add((!obj_item.has("pacstno")) ? "" : obj_item.getString("pacstno"));
								params.add((!obj_item.has("padepnmc")) ? "" : obj_item.getString("padepnmc"));
								params.add(userid);
								params.add(hid);
								params.add((!obj_item.has("padepcod")) ? "" : obj_item.getString("padepcod"));
								params.add((!obj_item.has("pbwkcode")) ? "" : obj_item.getString("pbwkcode"));
							} else if ("PARANKCODE".equals(formnm)) {// 階別
								params.add((!obj_item.has("rankname")) ? "" : obj_item.getString("rankname"));
								params.add((!obj_item.has("rankabbr")) ? "" : obj_item.getString("rankabbr"));
								params.add((!obj_item.has("rankcode")) ? "" : obj_item.getString("rankcode"));
							} else if ("PACATGTBL".equals(formnm)) {// 人工造冊應計轉帳項目代碼
								params.add((!obj_item.has("catgname")) ? "" : obj_item.getString("catgname"));
								params.add(userid);
								params.add((!obj_item.has("paycatg")) ? "" : obj_item.getString("paycatg"));
							} else if ("PAEXPFTBL".equals(formnm)) {// 績效額外點數對照表
								params.add((!obj_item.has("expfname")) ? "" : obj_item.getString("expfname"));
								params.add(userid);
								params.add((!obj_item.has("expfkey")) ? "" : obj_item.getString("expfkey"));
							} else if ("PARNK4TBL".equals(formnm)) {// 適用專業加給對照表
								params.add((!obj_item.has("rnk4name")) ? "" : obj_item.getString("rnk4name"));
								params.add((!obj_item.has("rnk4key")) ? "" : obj_item.getString("rnk4key"));
							} else if ("PABANK".equals(formnm)) {// 發薪日及入帳日對應檔
								params.add((!obj_item.has("paybank")) ? "" : obj_item.getString("paybank"));
								params.add((!obj_item.has("paysaldt")) ? "" : obj_item.getString("paysaldt"));
								params.add(userid);
								params.add((!obj_item.has("salnum")) ? "" : obj_item.getString("salnum"));
								params.add(hid);
							} else if ("PATAXTBL".equals(formnm)) {// 薪資所得扣繳稅額表
								params.add((!obj_item.has("salhigh")) ? "" : obj_item.getString("salhigh"));
								params.add((!obj_item.has("taxamt")) ? "" : obj_item.getString("taxamt"));
								params.add(userid);
								params.add((!obj_item.has("sallow")) ? "" : obj_item.getString("sallow"));
								params.add((!obj_item.has("kindred")) ? "" : obj_item.getString("kindred"));
							} else if ("PAPFTL".equals(formnm)) {// 全院績效彙總
								params.add((!obj_item.has("paypfunt")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paypfunt"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paypfunt")));
								params.add((!obj_item.has("papfamt")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("papfamt"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("papfamt")));
								params.add((!obj_item.has("paypfpf")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paypfpf"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paypfpf")));
								params.add((!obj_item.has("paypfrev")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paypfrev"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paypfrev")));
								params.add((!obj_item.has("paypfd")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paypfd"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paypfd")));
								params.add((!obj_item.has("paypfe")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paypfe"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paypfe")));
								params.add((!obj_item.has("paypfmon")) ? "" : obj_item.getString("paypfmon"));
							} else if ("PADPINTBL".equals(formnm)) {// 全民健康保險負擔金額表
								params.add((!obj_item.has("dpinhigh")) ? ""
										: StringUtil.nullToString(obj_item.getString("dpinhigh")));
								params.add((!obj_item.has("dpinamt")) ? ""
										: StringUtil.nullToString(obj_item.getString("dpinamt")));
								params.add((!obj_item.has("payupdid")) ? ""
										: StringUtil.nullToString(obj_item.getString("payupdid")));
								params.add((!obj_item.has("dpindept1")) ? ""
										: StringUtil.nullToString(obj_item.getString("dpindept1")));
								params.add((!obj_item.has("dpindept3")) ? ""
										: StringUtil.nullToString(obj_item.getString("dpindept3")));
								params.add((!obj_item.has("dpinlow")) ? ""
										: StringUtil.nullToString(obj_item.getString("dpinlow")));
							} else if ("PALBINTBL".equals(formnm)) {// 勞保投保金額對照表
								params.add((!obj_item.has("lbinhigh")) ? ""
										: StringUtil.nullToString(obj_item.getString("lbinhigh")));
								params.add((!obj_item.has("lbinamt")) ? ""
										: StringUtil.nullToString(obj_item.getString("lbinamt")));
								params.add((!obj_item.has("lbindept")) ? ""
										: StringUtil.nullToString(obj_item.getString("lbindept")));
								params.add((!obj_item.has("lbinlow")) ? ""
										: StringUtil.nullToString(obj_item.getString("lbinlow")));
							} else if ("PALBRPTBL".equals(formnm)) {// 勞工退休金月提繳工資分級表
								params.add((!obj_item.has("lbrphigh")) ? ""
										: StringUtil.nullToString(obj_item.getString("lbrphigh")));
								params.add((!obj_item.has("lbrpamt")) ? ""
										: StringUtil.nullToString(obj_item.getString("lbrpamt")));
								params.add((!obj_item.has("masteramt")) ? ""
										: StringUtil.nullToString(obj_item.getString("masteramt")));
								params.add((!obj_item.has("assocc")) ? ""
										: StringUtil.nullToString(obj_item.getString("assocc")));
								params.add((!obj_item.has("lbrplow")) ? ""
										: StringUtil.nullToString(obj_item.getString("lbrplow")));
							} else if ("PATITLE".equals(formnm)) {// 職稱
								params.add((!obj_item.has("patitsub")) ? "" : obj_item.getString("patitsub"));
								params.add((!obj_item.has("pachief")) ? "" : obj_item.getString("pachief"));
								params.add((!obj_item.has("paytitlp")) ? "" : obj_item.getString("paytitlp"));
								params.add((!obj_item.has("paytitlc")) ? "" : obj_item.getString("paytitlc"));
								params.add((!obj_item.has("pacoddel")) ? "" : obj_item.getString("pacoddel"));
								params.add(userid);
								params.add((!obj_item.has("paytitlk")) ? "" : obj_item.getString("paytitlk"));
								params.add(hid);
							} else if ("PAPAYITM".equals(formnm)) {// 支扣項目
								params.add((!obj_item.has("pacodnmc")) ? "" : obj_item.getString("pacodnmc"));
								params.add((!obj_item.has("pacoddel")) ? "" : obj_item.getString("pacoddel"));
								params.add((!obj_item.has("payauth")) ? "" : obj_item.getString("payauth"));
								params.add((!obj_item.has("payiftax")) ? "" : obj_item.getString("payiftax"));
								params.add((!obj_item.has("paupbas")) ? "" : obj_item.getString("paupbas"));
								params.add((!obj_item.has("payreward")) ? "" : obj_item.getString("payreward"));
								params.add(userid);
								params.add((!obj_item.has("paycode")) ? "" : obj_item.getString("paycode"));
								params.add(hid);
							} else if ("PAPOSI".equals(formnm)) {// 職等支扣--專業加給及主管加給
								params.add((!obj_item.has("degrhigh")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("degrhigh"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("degrhigh")));
								params.add((!obj_item.has("paypsp1")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paypsp1"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paypsp1")));
								params.add((!obj_item.has("paypsp2")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paypsp2"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paypsp2")));
								params.add((!obj_item.has("paypsp3")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paypsp3"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paypsp3")));
								params.add((!obj_item.has("paypsp4")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paypsp4"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paypsp4")));
								params.add((!obj_item.has("paypsp5")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paypsp5"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paypsp5")));
								params.add((!obj_item.has("paypsp6")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paypsp6"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paypsp6")));
								params.add((!obj_item.has("paypsp7")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paypsp7"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paypsp7")));
								params.add((!obj_item.has("paypsp8")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paypsp8"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paypsp8")));
								params.add(userid);
								params.add((!obj_item.has("payposik")) ? "" : obj_item.getString("payposik"));
								params.add((!obj_item.has("degrlow")) ? "" : obj_item.getString("degrlow"));
							} else if ("PARANK".equals(formnm)) {// 階級支扣
								params.add((!obj_item.has("paydegrv")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paydegrv"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paydegrv")));
								params.add((!obj_item.has("paybasev")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paybasev"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paybasev")));
								params.add((!obj_item.has("paygeinf")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paygeinf"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paygeinf")));
								params.add((!obj_item.has("paylbinf")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paylbinf"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paylbinf")));
								params.add((!obj_item.has("paydpinv")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paydpinv"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paydpinv")));
								params.add((!obj_item.has("paygeinn")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("paygeinn"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("paygeinn")));
								params.add((!obj_item.has("payrebef")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("payrebef"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("payrebef")));
								params.add((!obj_item.has("payreben")) ? ""
										: ("").equals(StringUtil.nullToString(obj_item.getString("payreben"))) ? "0"
												: StringUtil.nullToString(obj_item.getString("payreben")));
								params.add(userid);
								params.add((!obj_item.has("paylev1v")) ? "" : obj_item.getString("paylev1v"));
								params.add((!obj_item.has("paylev2v")) ? "" : obj_item.getString("paylev2v"));
								params.add((!obj_item.has("payrankv")) ? "" : obj_item.getString("payrankv"));
								params.add(hid);
							} else if ("PACSPF".equals(formnm)) {// 工作獎金支扣
								params.add((!obj_item.has("pacsbase")) ? "" : obj_item.getString("pacsbase"));
								params.add((!obj_item.has("pacsdata")) ? "" : obj_item.getString("pacsdata"));
								params.add((!obj_item.has("pacsname")) ? "" : obj_item.getString("pacsname"));
								params.add((!obj_item.has("pacskey")) ? "" : obj_item.getString("pacskey"));
							} else if ("PATRAPAY".equals(formnm)) {// 異動支扣
								// PAYIDNO, PAYAMT, PAYAUTH, PAYIFTAX, PAYUPDTM, PAYFLAG,TRANUM,PATRADT,PAYSORT
								// ,PAYCODE,HID
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
							log.info(me + "  " + sql + "...params:" + params.toString());
							updCnt = da.update(sql, params);
							log.info(me + "  updCnt:" + updCnt);
							da.commit();
							log.info(me + "...updcnt:" + updCnt);
							if (updCnt > 0)
								success = true;
						}
					}
				}
			}

		} catch (Throwable e) {
			throw new SQLException(me + "(error):" + e.toString());
		} finally {
			closeAll(da, null, connFlg);
		}
		return success;
	}

	// 取得相對應卡號之姓名
	// 2026.5.27 2A0 3A0 查自家的,1A0不限
	public JSONObject queryNamebyID(String cardno, String Loginhid) throws Exception {
		JSONObject user = new JSONObject();

		try {
			// 由ldap取得個人資料
			String hostname = InetAddress.getLocalHost().getHostName();
			String programName = this.getClass().getName();
			LDAPUtil ldapUtil = LDAPUtil.getInstance();
			String userHID = ldapUtil.getLdapAttr(cardno, "HID", hostname, programName);
			if ((Loginhid.matches("2A0|3A0") && userHID.equals(Loginhid)) || Loginhid.matches("1A0")) {
				String userName = ldapUtil.getUserNm(cardno);
				String userCosId = ldapUtil.getCostId(cardno);
				// 成本中心中文
				String userDeptName = ldapUtil.getDeptName(cardno, hostname, programName);

				// 成本中心英文
				String userDept = ldapUtil.getDeptNo(cardno, hostname, programName);
				if (userDept.length() >= 3 && userDept.substring(0, 3).equals("NUR"))
					userDept = userDept.substring(3, userDept.length());
				// 職稱代碼
				String user_psWkTypd = ldapUtil.getLdapAttr(cardno, "payWkTyd", hostname, programName);

				// 查詢是否為薪資管理者權限
				boolean pa_admin = ldapUtil.checkUserinGroup(cardno, Globals.SESSION_SYS_ADMIN);
				System.out.println("管理者" + ":" + pa_admin);
				String strAdmin = pa_admin ? "Y" : "N";

				// 查詢是否為薪資系統_維護角色
				boolean pa_edit = ldapUtil.checkUserinGroup(cardno, Globals.SESSION_SYS_EDIT);
				System.out.println("維護角色" + ":" + pa_edit);
				String strEdit = pa_edit ? "Y" : "N";

				// 查詢是否為薪資系統_查詢角色
				boolean pa_view = ldapUtil.checkUserinGroup(cardno, Globals.SESSION_SYS_VIEW);
				System.out.println("查詢角色" + ":" + pa_view);
				String strView = pa_view ? "Y" : "N";

				// 查詢是否為薪資系統_員工獎金角色
				boolean pa_bones = ldapUtil.checkUserinGroup(cardno, Globals.SESSION_SYS_BONES);
				System.out.println("員工獎金角色" + ":" + pa_bones);
				String strBones = pa_bones ? "Y" : "N";

				// 查詢是否為薪資系統_所得稅角色
				boolean pa_tax = ldapUtil.checkUserinGroup(cardno, Globals.SESSION_SYS_TAX);
				System.out.println("所得稅角色" + ":" + pa_tax);
				String strTax = pa_tax ? "Y" : "N";

				user.element("userCardno", cardno);
				user.element("userName", userName);
				user.element("userCosId", userCosId);
				user.element("userDept", userDept);
				user.element("userDeptName", userDeptName);
				user.element("userHid", userHID);
				user.element("userPsWkTypd", user_psWkTypd);
				user.element("userAdmin", strAdmin);
				user.element("userEdit", strEdit);
				user.element("userView", strView);
				user.element("userBones", strBones);
				user.element("userTax", strTax);
			}
		} catch (Exception e) {
			log.info(" fail, " + e);
		} finally {

		}
		return user;
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

	/**
	 * editMode:1/2 
	 * table1:PA.PABASPSN(薪資基本檔)  PABATPAY	 
	   * 傳出:true/false
	 */	
	public Map<String, Object> updateTblID(String editMode,String in_data,String userid,String hid,VGHDataAccess da) throws SQLException {
		int Cnt = 0;
		String me = getClass().getName() + ".updPATblByKey() userid:"+userid;
		log.info(me + " editMode = " + editMode + " in_data = " + in_data + " userid = " + userid+ " hid = " + hid);
		
		String cardno="";	    //舊卡號
		String cardno_new="";	//新卡號
		String idtno="";	    //舊id
		String idtno_new="";	//新id
		JSONObject object_in_data = JSONObject.fromObject(in_data);		
		cardno = StringUtil.nullToString( object_in_data.getString("cardno") );
		cardno_new = StringUtil.nullToString( object_in_data.getString("cardno_new") );
		idtno = StringUtil.nullToString( object_in_data.getString("idtno") );
		idtno_new = StringUtil.nullToString( object_in_data.getString("idtno_new") );
		boolean success=false;
		StringBuilder msg = new StringBuilder();
		
		try{
			if (editMode.equals("1")) {
				//基本個人明細
				sql="UPDATE "+this.tBasPsn+ " SET PAYCARD=? , "+tmpZbaspsn+" =current timestamp where PAYCARD=? and hid=? ";
				params = new ArrayList<Object>();
				params.add(cardno_new);
				params.add(cardno);
				params.add(hid);
				
				Cnt=da.update(sql, params);	
				if (Cnt <= 0) {
					log.info(me + "基本個人明細："+ Cnt +" 無異動資料，已跳過卡號 " + cardno);
				    msg.append("基本個人明細：無異動資料，卡號 "+ cardno +" 已跳過\n");
				}else{
					success=true;
					log.info(me + "基本個人明細："+ Cnt +" 卡號 " + cardno + " → " + cardno_new +" 更新成功 ");
					msg.append("基本個人明細：卡號 " + cardno + " → " + cardno_new +" 更新成功 \n");
				}
				
				//所得稅個人明細 (判斷當年度PAYEAR)
				sql="UPDATE "+this.tTaxPsn+" SET PAYCARD=? , "+tmpZtaxpsn+" =current timestamp where PAYCARD=? and PAYEAR=YEAR(CURRENT DATE) and hid=? ";
				params = new ArrayList<Object>();
				params.add(cardno_new);
				params.add(cardno);
				params.add(hid);
				Cnt=da.update(sql, params);
				if (Cnt <= 0) {
					log.info(me + "所得稅："+ Cnt +" 無異動資料，已跳過卡號 " + cardno);
				    msg.append("所得稅：無異動資料，卡號 "+ cardno +" 已跳過\n");
				}else{
					success=true;
					log.info(me + "當年度所得稅："+ Cnt +" 卡號  " + cardno + " → " + cardno_new +" 更新成功 ");
					msg.append("當年度所得稅：卡號  " + cardno + " → " + cardno_new +" 更新成功 \n");
				}
								
			}else if (editMode.equals("2")) {
				//基本個人明細
				sql="UPDATE "+this.tBasPsn+ " SET PAYIDNO=? , "+tmpZbaspsn+" =current timestamp where PAYIDNO=? and hid=? ";
				params = new ArrayList<Object>();
				params.add(idtno_new);
				params.add(idtno);
				params.add(hid);
				Cnt=da.update(sql, params);					
				if (Cnt <= 0) {
					log.info(me + "基本個人明細："+ Cnt +" 無異動資料，已跳過身份證 " + idtno);
				    msg.append("基本個人明細：無異動資料，身份證 "+ idtno +" 已跳過\n");
				}else{
					success=true;
					log.info(me + "基本個人明細："+ Cnt +" 身份證  " + idtno + " → " + idtno_new +" 更新成功 ");
					msg.append("基本個人明細：身份證  " + idtno + " → " + idtno_new +" 更新成功 \n");
				}
				
				//異動支扣明細
				sql="UPDATE "+this.tTraPay+" SET PAYIDNO=? , "+tmpZtrapay+" =current timestamp where PAYIDNO=? and hid=? ";	
				params = new ArrayList<Object>();
				params.add(idtno_new);
				params.add(idtno);
				params.add(hid);
				Cnt=da.update(sql, params);
				if (Cnt <= 0) {
					log.info(me + "異動支扣明細："+ Cnt +" 無異動資料，已跳過身份證 " + idtno);
				    msg.append("異動支扣明細：無異動資料，身份證 "+ idtno +" 已跳過\n");
				}else{
					success=true;
					log.info(me + "異動支扣明細："+ Cnt +" 身份證 " + idtno + " → " + idtno_new +" 更新成功 ");
					msg.append("異動支扣明細：身份證 " + idtno + " → "+ idtno_new +" 更新成功 \n");
				}
				
				//批次檔
				sql="UPDATE "+this.tBatPay+" SET PAYIDNO=? , "+tmpZbatpay+" =current timestamp where PAYIDNO=? and hid=? ";
				params = new ArrayList<Object>();
				params.add(idtno_new);
				params.add(idtno);
				params.add(hid);
				Cnt=da.update(sql, params);
				if (Cnt <= 0) {
					log.info(me + "批次檔："+ Cnt +" 無異動資料，已跳過身份證 " + idtno);
				    msg.append("批次檔：無異動資料，身份證 "+ idtno +" 已跳過\n");
				}else{
					success=true;
					log.info(me + "批次檔："+ Cnt +" 身份證 " + idtno + " → " + idtno_new +" 更新成功 ");
					msg.append("批次檔：身份證 " + idtno + " → " + idtno_new +" 更新成功 \n");
				}
				
				//所得稅個人明細
				sql="UPDATE "+this.tTaxPsn+" SET PAYIDNO=? , "+tmpZtaxpsn+" =current timestamp where PAYIDNO=? and hid=? ";	
				params = new ArrayList<Object>();
				params.add(idtno_new);
				params.add(idtno);
				params.add(hid);
				Cnt=da.update(sql, params);
				if (Cnt <= 0) {
					log.info(me + "所得稅個人明細："+ Cnt +" 無異動資料，已跳過身份證 " + idtno);
				    msg.append("所得稅個人明細：無異動資料，身份證 "+ idtno +" 已跳過\n");
				}else{
					success=true;
					log.info(me + "所得稅個人明細："+ Cnt +" 身份證 " + idtno + " → " + idtno_new +" 更新成功 ");
					msg.append("所得稅個人明細：身份證 " + idtno + " → " + idtno_new +" 更新成功 \n");
				}
				
				//住址
				sql="UPDATE "+this.tBasAdr+" SET PAYIDNO=? , "+tmpZbasadr+" =current timestamp where PAYIDNO=? ";
				params = new ArrayList<Object>();
				params.add(idtno_new);
				params.add(idtno);
				Cnt=da.update(sql, params);
				if (Cnt <= 0) {
					log.info(me + "住址："+ Cnt +" 無異動資料，已跳過身份證 " + idtno);
				    msg.append("住址：無異動資料，身份證 "+ idtno +" 已跳過\n");
				}else{
					success=true;
					log.info(me + "住址："+ Cnt +" 身份證 " + idtno + " → " + idtno_new +" 更新成功 ");
					msg.append("住址：身份證 " + idtno + " → " + idtno_new +" 更新成功 \n");
				}
				
				//月支支扣明細
				sql="UPDATE "+this.tDatPay+" SET PAYIDNO=? , "+tmpZdatpay+" =current timestamp where PAYIDNO=? ";
				params = new ArrayList<Object>();
				params.add(idtno_new);
				params.add(idtno);
				Cnt=da.update(sql, params);
				if (Cnt <= 0) {
					log.info(me + "月支支扣明細："+ Cnt +" 無異動資料，已跳過身份證 " + idtno);
				    msg.append("月支支扣明細：無異動資料，身份證 "+ idtno +" 已跳過\n");
				}else{
					success=true;
					log.info(me + "月支支扣明細："+ Cnt +" 身份證 " + idtno + " → " + idtno_new +" 更新成功 ");
					msg.append("月支支扣明細：身份證 " + idtno + " → " + idtno_new +" 更新成功 \n");
				}
				
				//月支個人明細
				sql="UPDATE "+this.tDatPsn+" SET PAYIDNO=? , "+tmpZdatpsn+" =current timestamp where PAYIDNO=? ";
				params = new ArrayList<Object>();
				params.add(idtno_new);
				params.add(idtno);
				Cnt=da.update(sql, params);
				if (Cnt <= 0) {
					log.info(me + "月支個人明細："+ Cnt +" 無異動資料，已跳過身份證 " + idtno);
				    msg.append("月支個人明細：無異動資料，身份證 "+ idtno +" 已跳過\n");
				}else{
					success=true;
					log.info(me + "月支個人明細："+ Cnt +" 身份證 " + idtno + " → " + idtno_new +" 更新成功 ");
					msg.append("月支個人明細：身份證 " + idtno + " → " + idtno_new +" 更新成功 \n");
				}
				
				//歷史檔
				sql="UPDATE "+this.tHist+" SET PAYIDNO=? , "+tmpZhist+" =current timestamp where PAYIDNO=? ";
				params = new ArrayList<Object>();
				params.add(idtno_new);
				params.add(idtno);
				Cnt=da.update(sql, params);
				if (Cnt <= 0) {
					log.info(me + "歷史檔："+ Cnt +" 無異動資料，已跳過身份證 " + idtno);
				    msg.append("歷史檔：無異動資料，身份證 "+ idtno +" 已跳過\n");
				}else{
					success=true;
					log.info(me + "歷史檔："+ Cnt +" 身份證 " + idtno + " → " + idtno_new +" 更新成功 ");
					msg.append("歷史檔：身份證 " + idtno + " → " + idtno_new +" 更新成功 \n");
				}
				
				//二代健保
				sql="UPDATE "+this.tNhiCal+" SET PAYIDNO=? , "+tmpZnhical+" =current timestamp where PAYIDNO=? ";
				params = new ArrayList<Object>();
				params.add(idtno_new);
				params.add(idtno);
				Cnt=da.update(sql, params);
				if (Cnt <= 0) {
					log.info(me + "二代健保："+ Cnt +" 無異動資料，已跳過身份證 " + idtno);
				    msg.append("二代健保：無異動資料，身份證 "+ idtno +" 已跳過\n");
				}else{
					success=true;
					log.info(me + "二代健保："+ Cnt +" 身份證 " + idtno + " → " + idtno_new +" 更新成功 ");
					msg.append("二代健保：身份證 " + idtno + " → " + idtno_new +" 更新成功 \n");
				}
				
				//二代健保申報扣費明細
				sql="UPDATE "+this.tNhiDet+" SET PAYIDNO=? , "+tmpZnhidet+" =current timestamp where PAYIDNO=? ";
				params = new ArrayList<Object>();
				params.add(idtno_new);
				params.add(idtno);
				Cnt=da.update(sql, params);
				if (Cnt <= 0) {
					log.info(me + "二代健保申報扣費明細："+ Cnt +" 無異動資料，已跳過身份證 " + idtno);
				    msg.append("二代健保申報扣費明細：無異動資料，身份證 "+ idtno +" 已跳過\n");
				}else{
					success=true;
					log.info(me + "二代健保申報扣費明細："+ Cnt +" 身份證 " + idtno + " → " + idtno_new +" 更新成功 ");
					msg.append("二代健保申報扣費明細：身份證 " + idtno + " → " + idtno_new +" 更新成功 \n");
				}
				
				//製作報表明細
				sql="UPDATE "+this.tRPDTL+" SET PAYIDNO=? , "+tmpZrpdtl+" =current timestamp where PAYIDNO=? ";
				params = new ArrayList<Object>();
				params.add(idtno_new);
				params.add(idtno);
				Cnt=da.update(sql, params);
				if (Cnt <= 0) {
					log.info(me + "製作報表明細："+ Cnt +" 無異動資料，已跳過身份證 " + idtno);
				    msg.append("製作報表明細：無異動資料，身份證 "+ idtno +" 已跳過\n");
				}else{
					success=true;
					log.info(me + "製作報表明細："+ Cnt +" 身份證 " + idtno + " → " + idtno_new +" 更新成功 ");
					msg.append("製作報表明細：身份證 " + idtno + " → " + idtno_new +" 更新成功 \n");
				}
				
				//所得稅轉帳明細
				sql="UPDATE "+this.tTaxTran+" SET PAYIDNO=? , "+tmpZtaxtran+" =current timestamp where PAYIDNO=? ";
				params = new ArrayList<Object>();
				params.add(idtno_new);
				params.add(idtno);
				Cnt=da.update(sql, params);
				if (Cnt <= 0) {
					log.info(me + "所得稅轉帳明細："+ Cnt +" 無異動資料，已跳過身份證 " + idtno);
				    msg.append("所得稅轉帳明細：無異動資料，身份證 "+ idtno +" 已跳過\n");
				}else{
					success=true;
					log.info(me + "所得稅轉帳明細："+ Cnt +" 身份證 " + idtno + " → " + idtno_new +" 更新成功 ");
					msg.append("所得稅轉帳明細：身份證 " + idtno + " → " + idtno_new +" 更新成功 \n");
				}
				
				//所得稅現金明細
				sql="UPDATE "+this.tTaxCash+" SET PAYIDNO=? , "+tmpZtaxcash+" =current timestamp where PAYIDNO=? ";
				params = new ArrayList<Object>();
				params.add(idtno_new);
				params.add(idtno);
				Cnt=da.update(sql, params);
				if (Cnt <= 0) {
					log.info(me + "所得稅現金明細："+ Cnt +" 無異動資料，已跳過身份證 " + idtno);
				    msg.append("所得稅現金明細：無異動資料，身份證 "+ idtno +" 已跳過\n");
				}else{
					success=true;
					log.info(me + "所得稅現金明細："+ Cnt +" 身份證 " + idtno + " → " + idtno_new +" 更新成功 ");
					msg.append("所得稅現金明細：身份證 " + idtno + " → " + idtno_new +" 更新成功 \n");
				}
				
				//勞工退休金檔
				sql="UPDATE "+this.tWKRETIR+" SET PAYIDNO=? , "+tmpZwkretir+" =current timestamp where PAYIDNO=? ";
				params = new ArrayList<Object>();
				params.add(idtno_new);
				params.add(idtno);
				Cnt=da.update(sql, params);
				if (Cnt <= 0) {
					log.info(me + "勞工退休金檔："+ Cnt +" 無異動資料，已跳過身份證 " + idtno);
				    msg.append("勞工退休金檔：無異動資料，身份證 "+ idtno +" 已跳過\n");
				}else{
					success=true;
					log.info(me + "勞工退休金檔："+ Cnt +" 身份證 " + idtno + " → " + idtno_new +" 更新成功 ");
					msg.append("勞工退休金檔：身份證 " + idtno + " → " + idtno_new +" 更新成功 \n");
				}
				
				//製作報表明細(MIS)
				sql="UPDATE "+this.tLMONT+" SET PAYIDNO=? , "+tmpZlmont+" =current timestamp where PAYIDNO=? ";
				params = new ArrayList<Object>();
				params.add(idtno_new);
				params.add(idtno);
				Cnt=da.update(sql, params);
				if (Cnt <= 0) {
					log.info(me + "製作報表明細(MIS)："+ Cnt +" 無異動資料，已跳過身份證 " + idtno);
				    msg.append("製作報表明細(MIS)：無異動資料，身份證 "+ idtno +" 已跳過\n");
				}else{
					success=true;
					log.info(me + "製作報表明細(MIS)："+ Cnt +" 身份證 " + idtno + " → " + idtno_new +" 更新成功 ");
					msg.append("製作報表明細(MIS)：身份證 " + idtno + " → " + idtno_new +" 更新成功 \n");
				}
				
				success=true;
			}else{
				
			}
		}catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}
		finally{
		
		}
		//return success;
		Map<String, Object> result = new HashMap<String, Object>();
	    result.put("success", success);
	    result.put("msg", msg.toString());
	    return result;
	}
}
