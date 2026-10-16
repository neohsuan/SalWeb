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
import gov.vghks.sal.model.Parank;
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
import gov.vghks.sal.util.FileUtil;
import gov.vghks.sal.util.PaCommUtil;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SYSSettingDAO {

	public static String newline = System.getProperty("line.separator");
	String gClassName = SalNTDAO.class.getName();
	private String sql="";
	static ArrayList<Map<String, Object>> result = null;
	ArrayList<Object> params=new ArrayList<Object>();
	static Logger log = Logger.getLogger(SalNTDAO.class);  //呼叫log4j
	private VGHDataAccess da = null;
	private VGHDataAccess daDSN = null;
	private static final int META_TYPE = 2; 			//回傳map中欄位名稱是小寫
	private static boolean connFlg = false; 			// false:由此物件new conn，true:由呼叫物件傳入conn
	private String hid="";
	static String g_dataSourceNT="";
	static String g_dataSource="";
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private String tBasPay = "PA.PABASPAY";			//基本支扣
	private String tBasPsn = "PA.PABASPSN";			//基本檔
	private String tBatPay = "PA.PABATPAY";			//批次檔
	private String tTraCode = "PA.PATRACODE";			//1異動代碼
	private String tDept 	= "PA.PADEPT";				//2單位代碼
	private String tTitle   = "PA.PATITLE";				//3職稱
	private String tRankCode = "PA.PARANKCODE";			//4階別代碼
	private String tCatgTbl = "PA.PACATGTBL";			//5人工造冊應計轉帳項目代碼 
	private String tExpfTbl = "PA.PAEXPFTBL";			//6績效額外點數對照表 
	private String tRnk4Tbl = "PA.PARNK4TBL";			//7適用專業加給對照表 
	private String tBankDtTbl = "PA.PABANK";				//1發薪日及入帳日對應檔 
	private String tTaxTbl = "PA.PATAXTBL";				//9薪資所得扣繳稅額表 
	private String tPftlTbl = "PA.PAPFTL";					//10全院績效彙總 
	private String tDpinTbl = "PA.PADPINTBL";			//全民健康保險負擔金額表 
	private String tLbinTbl = "PA.PALBINTBL";			//勞保投保金額對照表
	private String tLbrpTbl = "PA.PALBRPTBL";			//勞工退休金月提繳工資分級表
	private String tPosiTbl = "PA.PAPOSI";					//職等支扣--專業加給及主管加給
	private String tRankTbl = "PA.PARANK";					//階級支扣
	private String tCspfTbl = "PA.PACSPF";					//工作獎金支扣
	private String tDatPsn = "PA.PADATPSN";					//月支個人明細
	private String tDatPsnDw = "PA.PADATPSN_DW";					//月支個人明細_歷史
	private String tDatPay = "PA.PADATPAY";					//月支支扣明細
	private String tDatPayDw = "PA.PADATPAY_DW";				//月支支扣明細_歷史
	private String tPayItm = "PA.PAPAYITM";					//支扣項目彙總
	private String tBasAdr = "PA.PABASADR";					//地址
	private String tHist = "PA.PAHIST";					//歷史檔
	private String tTraPsn = "PA.PATRAPSN";					//異動個人明細
	private String tTraPsnDw = "PA.PATRAPSN_DW";					//異動個人明細_歷史
	private String tTraPay = "PA.PATRAPAY";					//異動支扣明細
	private String tTraPayDw = "PA.PATRAPAY_DW";					//異動支扣明細_歷史
	private String tTaxPsn = "PA.PATAXPSN";					//所得稅個人明細
	private String tTaxTran = "PA.PATAXTRAN";					//所得稅轉帳明細
	private String tTaxCash = "PA.PATAXCASH";					//所得稅現金明細
	private String tRPDTL = "PA.PAREPDETAIL";					//製作報表明細
	private String tRPPRT = "PA.PAREPORT";					//報表檔架構
	private String tNhiCal = "PA.PANHICAL";					//二代健保(補充保險費_獎金)
	private String tNhiDet = "PA.PANHIDET";					//二代健保申報扣費明細
	private String tTeaChaw = "PA.PATEACHAW";				//教職及論文獎勵
	private String tLMONT = "PA.PALMONT";				//39製作報表明細
	private String tWKRETIR = "PA.PAWKRETIR";				//40勞工退休金檔
	
	private String tmpZtitle = "ZPATITLE";		//時戳
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
	private String tmpZtaxtran ="ZPATAXTRAN";
	private String tmpZtaxcash ="ZPATAXCASH";
	private String tmpZbasadr ="ZPABASADR";
	private String tmpZtaxpsn ="ZPATAXPSN";
	private String tmpZdatpay ="ZPADATPAY";
	private String tmpZdatpsn ="ZPADATPSN";
	private String tmpZnhical ="ZPANHICAL";
	private String tmpZnhidet ="ZPANHIDET";
	private String tmpZrpdtl ="ZPAREPDETAIL";
	private String tmpZwkretir ="ZPAWKRETIR";
	private String tmpZlmont ="ZPAREPDETAIL";
	private String tmpZrank = "ZPARANK";
	
	public SYSSettingDAO() {
		super();
	}
	public SYSSettingDAO(VGHDataAccess da1) {
		super();
		this.da=da1;
		connFlg = true;
	}
	public SYSSettingDAO(VGHDataAccess da1,String hid) {
		super();
		this.da=da1;
		connFlg = true;
		this.hid=hid;
		Tables tabInfo = Tables.getTables(hid);
		
		tTaxPsn = tabInfo.tTaxPsn;					//所得稅個人明細
		tTaxTran = tabInfo.tTaxTran;				//所得稅轉帳明細
		tTaxCash = tabInfo.tTaxCash;				//所得稅現金明細 
		tBankDtTbl = tabInfo.tBankDtTbl;			//發薪日及入帳日對應檔 
		tTaxPsn=tabInfo.tTaxPsn;
		tTaxTran=tabInfo.tTaxTran;
		tTaxCash=tabInfo.tTaxCash;
		tBasPsn=tabInfo.tBasPsn;
		tBasPay=tabInfo.tBasPay;
		tBatPay = tabInfo.tBatPay;
		tTraCode=tabInfo.tTraCode;
		tDept=tabInfo.tDept;
		tTitle=tabInfo.tTitle;
		tRankCode=tabInfo.tRankCode;
		tCatgTbl=tabInfo.tCatgTbl;
		tExpfTbl=tabInfo.tExpfTbl;
		tRnk4Tbl=tabInfo.tRnk4Tbl;
		tBankDtTbl=tabInfo.tBankDtTbl;
		tTaxTbl=tabInfo.tTaxTbl;
		tPftlTbl=tabInfo.tPftlTbl;
		tDpinTbl=tabInfo.tDpinTbl;
		tLbinTbl=tabInfo.tLbinTbl;
		tLbrpTbl=tabInfo.tLbrpTbl;
		tPosiTbl=tabInfo.tPosiTbl;
		tRankTbl=tabInfo.tRankTbl;
		tCspfTbl=tabInfo.tCspfTbl;
		tDatPsn=tabInfo.tDatPsn;
		tDatPsnDw=tabInfo.tDatPsnDw;
		tDatPay=tabInfo.tDatPay;
		tDatPayDw=tabInfo.tDatPayDw;
		tPayItm=tabInfo.tPayItm;
		tBasAdr=tabInfo.tBasAdr;
		tHist=tabInfo.tHist;
		tTraPsn=tabInfo.tTraPsn;
		tTraPsnDw=tabInfo.tTraPsnDw;
		tTraPay=tabInfo.tTraPay;
		tTraPayDw=tabInfo.tTraPayDw;
		tRPDTL=tabInfo.tRPDTL;
		tRPPRT=tabInfo.tRPPRT;
		tNhiCal=tabInfo.tNhiCal;
		tNhiDet=tabInfo.tNhiDet;
		tTeaChaw=tabInfo.tTeaChaw;
		tLMONT=tabInfo.tLMONT;
		tWKRETIR=tabInfo.tWKRETIR;
		
		tmpZtitle=tabInfo.tmpZtitle;
		tmpZrankce=tabInfo.tmpZrankce;
		tmpZbank=tabInfo.tmpZbank;
		tmpZdept=tabInfo.tmpZdept;
		tmpZrnk4=tabInfo.tmpZrnk4;
		tmpZexpf=tabInfo.tmpZexpf;
		tmpZtracd=tabInfo.tmpZtracd;
		tmpZbaspsn=tabInfo.tmpZbaspsn;
		tmpZbatpay=tabInfo.tmpZbatpay;
		tmpZtrapsn=tabInfo.tmpZtrapsn;
		tmpZtrapay=tabInfo.tmpZtrapay;
		tmpZhist=tabInfo.tmpZhist;
		tmpZdpintbl=tabInfo.tmpZdpintbl;
		tmpZlbintbl=tabInfo.tmpZlbintbl;
		tmpZrpintbl=tabInfo.tmpZrpintbl;
		tmpZcspf=tabInfo.tmpZcspf;
		tmpZtaxtran=tabInfo.tmpZtaxtran;
		tmpZtaxcash=tabInfo.tmpZtaxcash;
		tmpZpayitm=tabInfo.tmpZpayitm;
		tmpZbasadr=tabInfo.tmpZbasadr;
		tmpZtaxpsn=tabInfo.tmpZtaxpsn;
		tmpZdatpay=tabInfo.tmpZdatpay;
		tmpZdatpsn=tabInfo.tmpZdatpsn;
		tmpZnhical=tabInfo.tmpZnhical;
		tmpZnhidet=tabInfo.tmpZnhidet;
		tmpZrpdtl=tabInfo.tmpZrpdtl;
		tmpZwkretir=tabInfo.tmpZwkretir;
		tmpZlmont=tabInfo.tmpZlmont;
		tmpZrank=tabInfo.tmpZrank;
	}
	public SYSSettingDAO(VGHDataAccess da1,VGHDataAccess da2,String hid) {
		super();
		this.da=da1;
		this.daDSN=da2;
		connFlg = true;
		this.hid=hid;
		Tables tabInfo = Tables.getTables(hid);
		
		tBasPsn=tabInfo.tBasPsn;
		
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
				
				//製作報表明細
				sql="UPDATE "+this.tLMONT+" SET PAYIDNO=? , "+tmpZlmont+" =current timestamp where PAYIDNO=? ";
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
	
	/**
	 *  
	 * table:PA.PARANK(階級支扣)  	 
	   * 傳出:true/false
	 */	
	public String ImpPaRank(ArrayList<Parank> alPaRank, String selCode, String userid, String hid, VGHDataAccess da) throws Exception {
		String me = getClass().getName() + ".prodBat(),userid = " + userid;
		log.info(me + ",alPaRank = " + alPaRank+ ",selCode = " + selCode+",userid = " + userid+ ",hid = " + hid);
		String rtn = "";
		
		int delCnt=0;
		int insCnt = 0;

		ArrayList<Object> paramA = new ArrayList<Object>();
		String sqlDel ="delete from "+ this.tRankTbl + " where HID=? and payrankv =? and paylev1v=? and paylev2v=? "; //del已存在的資料		
		
		String sqlIns = "insert into " + this.tRankTbl + " (PAYRANKV, PAYLEV1V, PAYLEV2V, PAYDEGRV, PAYBASEV, PAYGEINF, PAYLBINF, PAYDPINV, PAYGEINN, PAYREBEF, PAYREBEN, PAYUSEID, PAYUPDAT, "+tmpZrank+", HID) "
						+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT TIMESTAMP, CURRENT TIMESTAMP, ?)";
		
		try{
			Hospitals hospInfo = Hospitals.getOneHosp(hid);
			if (da == null) {
				g_dataSourceNT=hospInfo.dataSourceNT;
				da=new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			
			da.startTransaction();
			try{
				for (Parank o : alPaRank) {
	                //先刪除同一階級/級別/俸級的舊資料
	                paramA.clear();
	                paramA.add(hid);
	                paramA.add(o.getPayrankv());
	                paramA.add(o.getPaylev1v());
	                paramA.add(o.getPay2ev1v());
	                log.info(me + " del " + o.getPayrankv() + ">>" + sqlDel + "...params:" + paramA.toString());
	                delCnt += da.update(sqlDel, paramA);

	                paramA.clear();	                
	                paramA.add(o.getPayrankv());
	                paramA.add(o.getPaylev1v());
	                paramA.add(o.getPay2ev1v());
	                paramA.add(o.getPaydegrv());
	                paramA.add(o.getPaybasev());
	                paramA.add(o.getPaygeinf());
	                paramA.add(o.getPaylbinf());
	                paramA.add(o.getPaydpinv());
	                paramA.add(o.getPaygeinn());
	                paramA.add(o.getPayrebef());
	                paramA.add(o.getPayreben());
	                paramA.add(userid);
	                paramA.add(hid);
	                log.info(me + " ins " + o.getPayrankv() + ">>" + sqlIns + "...params:" + paramA.toString());
	                insCnt += da.update(sqlIns, paramA);
	            }
	            da.commit();
			}catch(Exception e){
				da.rollback();
				rtn=" fail, "+e;
				da.setAutoCommit(true);
				e.printStackTrace();
				log.info(me+"  "+e.toString());						     
			}
			
			rtn = "刪除筆數:" + delCnt + "，新增筆數:" + insCnt;
	        log.info(me + " delCnt:" + delCnt + " insCnt:" + insCnt);

		}catch(Exception e){
			rtn=" fail, "+e;
			e.printStackTrace();			
			log.info(me+" "+e.toString());			
		} finally{
			closeAll(da,null,connFlg);			
		}
		
		return rtn;
	}	
	
	/**
	 *  
	 * table:PA.PABASPSN基本檔 PA.PATITLE職稱 PA.PABANK餉單編號對應 PA.PADATPAY月支支扣明細 
     *查詢近 3 個月投保薪資比對清單
     **/
	public List<Map<String, Object>> getInsuPerList(String userid, String hid, String paydate, String paytype) {
	    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	    
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    LocalDate baseDate = LocalDate.now();
	    try {
	        if (paydate != null && !paydate.trim().equals("")) {
	            baseDate = LocalDate.parse(paydate.trim(), dtf);
	        }
	    } catch (Exception e) {
	        baseDate = LocalDate.now();
	    }
	    
	    String m1_date = baseDate.format(dtf);
	    String m2_date = baseDate.minusMonths(1).format(dtf);
	    String m3_date = baseDate.minusMonths(2).format(dtf);

	    try {
	        // 1. 取得近三個月所有對應的 salnum 與日期對照表
	        Map<Integer, String> salnumToBankMap = getSalnumMapByDates(hid, m1_date, m2_date, m3_date);

	        // 2. 取得人員基本與職稱資料
	        Map<String, Map<String, Object>> psnMap = getPersonBaseMap(hid, paytype);
	        if (psnMap == null || psnMap.isEmpty()) {
	            return list;
	        }

	        // 3. 抓取這三個月所有相關 salnum 的總支扣明細
	        Map<String, List<Map<String, Object>>> empPayListMap = getDatPayListBySalnums(hid, salnumToBankMap.keySet());

	        // 4. 💡 恢復以全體員工 (psnMap) 為基底進行轉置，即使當月無發薪也會顯示 0.00
	        list = mergeAndPivotSalaryData(psnMap, empPayListMap, salnumToBankMap, m1_date, m2_date, m3_date);

	    } catch (Exception e) {
	        log.error("getInsuPerList 執行失敗: ", e);
	    }

	    return list;
	}

	/**
	 * 1. 取得近三個月對應的 salnum (PTBANK)
	 * 修正點：改用問號 ? 參數綁定，解決 DB2 DATE 型態與字串比對抓不到資料的問題
	 */
	/**
	 * 修正版：不拘泥單一發薪日，改以「整個月份」為範圍抓取該月所有的 salnum (支援同月份多個發薪日如 235, 236)
	 */
	private Map<Integer, String> getSalnumMapByDates(String hid, String m1, String m2, String m3) {
	    Map<Integer, String> salnumMap = new HashMap<Integer, String>();
	    StringBuilder sb = new StringBuilder();
	    ArrayList<Object> params = new ArrayList<Object>();

	    // 💡 取出三個月份的年月字串 (例如 "2026-09", "2026-08", "2026-07")
	    String m1_ym = (m1 != null && m1.length() >= 7) ? m1.substring(0, 7) : "";
	    String m2_ym = (m2 != null && m2.length() >= 7) ? m2.substring(0, 7) : "";
	    String m3_ym = (m3 != null && m3.length() >= 7) ? m3.substring(0, 7) : "";

	    sb.append("SELECT SALNUM, PAYBANK ");
	    sb.append("FROM PA.PTBANK ");
	    sb.append("WHERE HID = ? ");
	    // 💡 改用以年月開頭（或者介於該月月初到月底），這樣同月份的不同發薪日（如 2026-09-01, 2026-09-05）都能被一網打盡
	    sb.append("  AND (CHAR(PAYBANK) LIKE ? OR CHAR(PAYBANK) LIKE ? OR CHAR(PAYBANK) LIKE ?) ");
	    sb.append("WITH UR ");
	    
	    params.add(hid != null ? hid.trim() : "");
	    params.add(m1_ym + "%");
	    params.add(m2_ym + "%");
	    params.add(m3_ym + "%");

	    log.info("【SQL DEBUG】getSalnumMapByDates (月份範圍) SQL: " + sb.toString());
	    log.info("【SQL DEBUG】getSalnumMapByDates 參數: " + params);

	    try {
	        ArrayList<Map<String, Object>> result = da.arraylistbyQuery(sb.toString(), params, META_TYPE);
	        log.info("【DEBUG】getSalnumMapByDates 實際查詢筆數: " + (result != null ? result.size() : 0));

	        if (result != null) {
	            for (Map<String, Object> row : result) {
	                Object sObj = null;
	                Object bObj = null;

	                for (Map.Entry<String, Object> entry : row.entrySet()) {
	                    String k = entry.getKey();
	                    if (k != null) {
	                        if (k.equalsIgnoreCase("SALNUM")) sObj = entry.getValue();
	                        else if (k.equalsIgnoreCase("PAYBANK")) bObj = entry.getValue();
	                    }
	                }

	                if (sObj != null && bObj != null) {
	                    try {
	                        Integer salnum = Integer.parseInt(sObj.toString().trim());
	                        String paybankStr = bObj.toString().trim();
	                        if (paybankStr.length() > 10) {
	                            paybankStr = paybankStr.substring(0, 10);
	                        }
	                        
	                        // 💡 為了讓後續轉置對應到正確的 M1, M2, M3 欄位，我們將其對應回該月的標準 1 號 (或保留原日期)
	                        // 這裡我們將其歸屬到對應的月份基準日
	                        if (paybankStr.startsWith(m1_ym)) {
	                            salnumMap.put(salnum, m1);
	                        } else if (paybankStr.startsWith(m2_ym)) {
	                            salnumMap.put(salnum, m2);
	                        } else if (paybankStr.startsWith(m3_ym)) {
	                            salnumMap.put(salnum, m3);
	                        } else {
	                            salnumMap.put(salnum, paybankStr);
	                        }
	                        
	                        log.info("【DEBUG】成功對應 salnum: " + salnum + " 歸屬月份基準: " + salnumMap.get(salnum));
	                    } catch (Exception ignored) {}
	                }
	            }
	        }
	    } catch (Exception e) {
	        log.error("getSalnumMapByDates 失敗: ", e);
	    }
	    log.info("【DEBUG】最終 salnumToBankMap 筆數: " + salnumMap.size());
	    return salnumMap;
	}

	/**
	 * 2. 取得人員基本資料與職稱 (加入 paytype 人員類別篩選)
	 * @param hid 醫院代碼
	 * @param paytype 人員類別選項 (例如: "DOCTOR" 醫師, "WORKER" 技工友)
	 */
	private Map<String, Map<String, Object>> getPersonBaseMap(String hid, String paytype) {
	    Map<String, Map<String, Object>> psnMap = new HashMap<String, Map<String, Object>>();
	    StringBuilder sb = new StringBuilder();
	    ArrayList<Object> params = new ArrayList<Object>();

	    sb.append("SELECT A.PAYIDNO, A.PAYCARD, A.PAYNAMEC, A.PAYTITLE, T.PAYTITLC ");
	    sb.append("FROM PA.PTBASPSN A ");
	    sb.append("LEFT OUTER JOIN PA.PTTITLE T ON A.PAYTITLE = T.PAYTITLK AND A.HID = T.HID ");
	    sb.append("WHERE A.HID = ? ");
	    
	    String cleanHid = (hid != null) ? hid.trim() : "";
	    params.add(cleanHid);

	    // 💡 根據 paytype 加入對應的 PAYTITLE 職稱代碼過濾條件
	    String cleanPayType = (paytype != null) ? paytype.trim() : "";
	    if ("DOCTOR".equalsIgnoreCase(cleanPayType) || "醫師".equals(cleanPayType) || "1".equals(cleanPayType)) {
	        // 醫師：PAYTITLE 需為 CG 或 CB
	        sb.append("  AND A.PAYTITLE IN ('CG', 'CB') ");
	    } else if ("WORKER".equalsIgnoreCase(cleanPayType) || "技工友".equals(cleanPayType) || "2".equals(cleanPayType)) {
	        // 技工友：PAYTITLE 需為 72 或 73
	        sb.append("  AND A.PAYTITLE IN ('72', '73') ");
	    }else if ("CONTRACT".equalsIgnoreCase(cleanPayType) || "契約".equals(cleanPayType) || "約用".equals(cleanPayType) || "3".equals(cleanPayType)) {
	        // 💡 契約人員：PAYTITLE 開頭為 D, E, 或 F
	        sb.append("  AND (A.PAYTITLE LIKE 'D%' OR A.PAYTITLE LIKE 'E%' OR A.PAYTITLE LIKE 'F%') ");
	    }
	    // 註：後續契約人員可以在此繼續新增 else if 條件

	    sb.append("WITH UR ");

	    log.info("【SQL DEBUG】getPersonBaseMap SQL: " + sb.toString());
	    log.info("【SQL DEBUG】getPersonBaseMap 參數: " + params + " (paytype: " + cleanPayType + ")");

	    try {
	        ArrayList<Map<String, Object>> result = da.arraylistbyQuery(sb.toString(), params, META_TYPE);
	        log.info("【DEBUG】getPersonBaseMap 原始查詢筆數: " + (result != null ? result.size() : 0));

	        if (result != null) {
	            for (Map<String, Object> row : result) {
	                Object idObj = null;
	                Object cardObj = null;
	                Object nameObj = null;
	                Object titleObj = null;

	                for (Map.Entry<String, Object> entry : row.entrySet()) {
	                    String k = entry.getKey();
	                    if (k != null) {
	                        if (k.equalsIgnoreCase("PAYIDNO")) idObj = entry.getValue();
	                        else if (k.equalsIgnoreCase("PAYCARD")) cardObj = entry.getValue();
	                        else if (k.equalsIgnoreCase("PAYNAMEC")) nameObj = entry.getValue();
	                        else if (k.equalsIgnoreCase("PAYTITLC")) titleObj = entry.getValue();
	                    }
	                }

	                if (idObj != null) {
	                    String idno = StringUtil.nullToString(idObj).trim();
	                    if (!idno.equals("")) {
	                        Map<String, Object> item = new HashMap<String, Object>();
	                        item.put("PAYCARD", StringUtil.nullToString(cardObj).trim());
	                        item.put("PAYNAMEC", StringUtil.nullToString(nameObj).trim());
	                        item.put("PAYTITLE", StringUtil.nullToString(titleObj).trim());
	                        psnMap.put(idno, item);
	                    }
	                }
	            }
	        }
	    } catch (Exception e) {
	        log.error("getPersonBaseMap 失敗: ", e);
	    }
	    log.info("【DEBUG】最終 psnMap 有效筆數: " + psnMap.size());
	    return psnMap;
	}

	/**
	 * 3. 查詢 PTDATPAY 支扣檔 (改為大小寫不拘)
	 */
	private Map<String, List<Map<String, Object>>> getDatPayListBySalnums(String hid, Set<Integer> salnums) {
	    Map<String, List<Map<String, Object>>> payMap = new HashMap<String, List<Map<String, Object>>>();
	    if (salnums == null || salnums.isEmpty()) return payMap;

	    StringBuilder sb = new StringBuilder();
	    ArrayList<Object> params = new ArrayList<Object>();

	    sb.append("SELECT PAYIDNO, SALNUM, PAYCODE, SUM(PAYAMT) AS PAYAMT ");
	    sb.append("FROM ");
	    sb.append(this.tDatPay);
	    sb.append(" WHERE HID = ? ");
	    
	    String cleanHid = (hid != null) ? hid.trim() : "";
	    params.add(cleanHid);

	    sb.append(" AND SALNUM IN (");
	    int idx = 0;
	    for (Integer salnum : salnums) {
	        if (idx > 0) sb.append(",");
	        sb.append("?");
	        params.add(salnum);
	        idx++;
	    }
	    sb.append(") ");

	    sb.append(" AND PAYCODE IN ('FP000','FP010','FP100','FP110','FP120','FP400','VP000','VP010','VP01E','VP040','VP100','VP110','VP200','VP220','VP230','VP457') ");
	    sb.append("GROUP BY PAYIDNO, SALNUM, PAYCODE ");
	    sb.append("WITH UR ");
	    
	 // 💡 印出 SQL 與參數（此處 salnums 參數較多，一併印出）
	    log.info("【SQL DEBUG】getDatPayListBySalnums SQL: " + sb.toString());
	    log.info("【SQL DEBUG】getDatPayListBySalnums 參數: " + params);

	    try {
	        ArrayList<Map<String, Object>> result = da.arraylistbyQuery(sb.toString(), params, META_TYPE);
	        log.info("【DEBUG】getDatPayListBySalnums 原始查詢筆數: " + (result != null ? result.size() : 0));

	        if (result != null) {
	            for (Map<String, Object> row : result) {
	                Object idObj = null;
	                Object salObj = null;
	                Object codeObj = null;
	                Object amtObj = null;

	                for (Map.Entry<String, Object> entry : row.entrySet()) {
	                    String k = entry.getKey();
	                    if (k != null) {
	                        if (k.equalsIgnoreCase("PAYIDNO")) idObj = entry.getValue();
	                        else if (k.equalsIgnoreCase("SALNUM")) salObj = entry.getValue();
	                        else if (k.equalsIgnoreCase("PAYCODE")) codeObj = entry.getValue();
	                        else if (k.equalsIgnoreCase("PAYAMT")) amtObj = entry.getValue();
	                    }
	                }

	                if (idObj != null) {
	                    String idno = StringUtil.nullToString(idObj).trim();
	                    if (!idno.equals("")) {
	                        Map<String, Object> standardizedRow = new HashMap<String, Object>();
	                        standardizedRow.put("PAYIDNO", idno);
	                        standardizedRow.put("SALNUM", salObj);
	                        standardizedRow.put("PAYCODE", codeObj);
	                        standardizedRow.put("PAYAMT", amtObj);

	                        List<Map<String, Object>> list = payMap.get(idno);
	                        if (list == null) {
	                            list = new ArrayList<Map<String, Object>>();
	                            payMap.put(idno, list);
	                        }
	                        list.add(standardizedRow);
	                    }
	                }
	            }
	        }
	    } catch (Exception e) {
	        log.error("getDatPayListBySalnums 失敗: ", e);
	    }
	    log.info("【DEBUG】最終 empPayListMap 有效人數: " + payMap.size());
	    return payMap;
	}

	/**
	 * 4. Java 記憶體轉置整合
	 */
	private List<Map<String, Object>> mergeAndPivotSalaryData(Map<String, Map<String, Object>> psnMap,
            Map<String, List<Map<String, Object>>> empPayListMap,
            Map<Integer, String> salnumToBankMap,
            String m1, String m2, String m3) {
List<Map<String, Object>> finalResult = new ArrayList<Map<String, Object>>();
String[] payCodes = {"FP000","FP010","FP100","FP110","FP120","FP400","VP000","VP010","VP01E","VP040","VP100","VP110","VP200","VP220","VP230","VP457"};

if (psnMap == null || psnMap.isEmpty()) return finalResult;

for (Map.Entry<String, Map<String, Object>> entry : psnMap.entrySet()) {
String payidno = entry.getKey();
Map<String, Object> psn = entry.getValue();

// 💡 檢查該員工在近三個月內是否有任何支扣明細
List<Map<String, Object>> payList = empPayListMap.get(payidno);
if (payList == null || payList.isEmpty()) {
continue; // 如果三個月完全沒有薪資紀錄，直接跳過不顯示！
}

Map<String, Object> rowMap = new LinkedHashMap<String, Object>();
rowMap.put("PAYIDNO", payidno);
rowMap.put("PAYCARD", psn.get("PAYCARD"));
rowMap.put("PAYNAMEC", psn.get("PAYNAMEC"));
rowMap.put("PAYTITLE", psn.get("PAYTITLE"));

// 預先填入三個月份的發薪日
rowMap.put("M1_PAYBANK", m1);
rowMap.put("M2_PAYBANK", m2);
rowMap.put("M3_PAYBANK", m3);

// 初始化所有月份金額欄位為 0.00
for (String code : payCodes) {
rowMap.put("M1_" + code, 0.00);
rowMap.put("M2_" + code, 0.00);
rowMap.put("M3_" + code, 0.00);
}

// 填入並累加實際薪資
for (Map<String, Object> payRow : payList) {
Object salnumObj = payRow.get("SALNUM");
Object codeObj = payRow.get("PAYCODE");
Object amtObj = payRow.get("PAYAMT");

Integer salnum = null;
if (salnumObj != null) {
try {
salnum = Integer.parseInt(salnumObj.toString().trim());
} catch (Exception ignored) {}
}

String paycode = StringUtil.nullToString(codeObj).trim();
String paybank = (salnum != null) ? salnumToBankMap.get(salnum) : null;

double amt = 0.0;
if (amtObj instanceof Number) {
amt = ((Number) amtObj).doubleValue();
} else if (amtObj != null) {
try {
amt = Double.parseDouble(amtObj.toString().trim());
} catch (Exception ignored) {}
}

String prefix = "";
if (m1.equals(paybank)) prefix = "M1_";
else if (m2.equals(paybank)) prefix = "M2_";
else if (m3.equals(paybank)) prefix = "M3_";

if (!prefix.equals("") && !paycode.equals("")) {
String targetKey = prefix + paycode;

double existingAmt = 0.0;
Object currentVal = rowMap.get(targetKey);
if (currentVal instanceof Number) {
existingAmt = ((Number) currentVal).doubleValue();
} else if (currentVal != null) {
try {
existingAmt = Double.parseDouble(currentVal.toString());
} catch (Exception ignored) {}
}

rowMap.put(targetKey, existingAmt + amt);
}
}

finalResult.add(rowMap);
}

return finalResult;
}
	
	/**
	 * 關閉VGHDataAccess與ResultSet
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
