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
