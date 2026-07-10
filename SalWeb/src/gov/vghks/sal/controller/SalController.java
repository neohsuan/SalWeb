package gov.vghks.sal.controller;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import gov.vghks.common.util.DateUtil;
import gov.vghks.common.util.JsonConfig;
import gov.vghks.common.util.StringUtil;
import gov.vghks.common.util.VGHDataAccess;
import gov.vghks.sal.Globals;
import gov.vghks.sal.Hospitals;
import gov.vghks.sal.dao.CommDAO;
import gov.vghks.sal.dao.PsDSNDAO;
import gov.vghks.sal.dao.SalNTDAO;
import gov.vghks.sal.model.Pabaspsn;
import gov.vghks.sal.model.Pabatpay;
import gov.vghks.sal.model.Padatpsn;
import gov.vghks.sal.model.Paprtdtl;
import gov.vghks.sal.model.Parank;
import gov.vghks.sal.model.Pataximp;
import gov.vghks.sal.model.Pataxpsn;
import gov.vghks.sal.model.Patrapay;
import gov.vghks.sal.model.Patrapsn;
import gov.vghks.sal.model.Psbasic;
import gov.vghks.sal.model.WPSUser;
import gov.vghks.sal.pdf.BankTran_1A0;
import gov.vghks.sal.pdf.BankTran_2A0;
import gov.vghks.sal.pdf.BankTran_3A0;
import gov.vghks.sal.pdf.Chgdtl_1A0;
import gov.vghks.sal.pdf.Chgdtl_2A0;
import gov.vghks.sal.pdf.Chgdtl_3A0;
import gov.vghks.sal.pdf.Deduct_1A0;
import gov.vghks.sal.pdf.Deduct_2A0;
import gov.vghks.sal.pdf.Deduct_3A0;
import gov.vghks.sal.pdf.Issue_1A0;
import gov.vghks.sal.pdf.Issue_2A0;
import gov.vghks.sal.pdf.Issue_3A0;
import gov.vghks.sal.pdf.ItemSum_1A0;
import gov.vghks.sal.pdf.ItemSum_2A0;
import gov.vghks.sal.pdf.ItemSum_3A0;
import gov.vghks.sal.pdf.Payslip_1A0;
import gov.vghks.sal.pdf.Payslip_2A0;
import gov.vghks.sal.pdf.Payslip_3A0;
import gov.vghks.sal.txt.BankMedia_1A0;
import gov.vghks.sal.txt.BankMedia_2A0;
import gov.vghks.sal.txt.BankMedia_3A0;
import gov.vghks.sal.util.AmJsonConfig;
import gov.vghks.sal.util.FileUtil;
import gov.vghks.sal.util.SendMail;
import gov.vghks.sal.util.ldap.LDAPUtil;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ibm.mq.headers.Charsets;

/**
 *
 */
@Controller
@RequestMapping(value = "/salController")
public class SalController{
	
	/** 呼叫log4j */
	public static final Logger log = 	Logger.getLogger(SalController.class);
	/** 範例主頁面 */
	public static final String main	=	"sal/main";
	
	JSONArray jsonArrRtn = null;
	JSONObject jsonObj = new JSONObject();
	VGHDataAccess da=null;
	VGHDataAccess daDSN=null;
	SalNTDAO tDAO = null;
	PsDSNDAO psDAO = null;
	String hid="";
	String userid="";
	
	/**
	 * 範例主頁面
	 */
	@RequestMapping(params="method=main")
	public String main(HttpServletRequest request){
		String me = getClass().getName() + ".main()";
		try{
			//透過WPSAutoLogin取得登入者資訊，已經存在session，可以自行判斷是否需要帶入頁面
			WPSUser user = (WPSUser)request.getSession().getAttribute("user");
			request.setAttribute("user", user);
			
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
		
		return main;
	}
	/**
	 * delTblCode 刪除
	 */
	@RequestMapping(params="method=delTblCode")
	public void delTblCode(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String me = getClass().getName() + ".delTblCode()";
		response.setCharacterEncoding("utf-8");//如果有中文，避免亂碼
		PrintWriter out = response.getWriter();
		jsonObj = new JSONObject();
		//透過WPSAutoLogin取得登入者資訊，已經存在session，可以自行判斷是否需要帶入頁面
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		boolean success=false;
		try{			
			String code_data = request.getParameter("code_data");//傳入json
			String tblname = request.getParameter("tblname");
			
			tDAO = new SalNTDAO(da,hid);
			success=tDAO.delTblCode(code_data,tblname,userid,hid,da);
			if (success)
				jsonObj.element("msg", "OK");	
			else
				jsonObj.element("msg", "失敗");
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			jsonObj.element("msg", e.toString());
			e.printStackTrace();
		}finally{
			tDAO=null;
			da.close();
			da = null;
		}
		// 寫出至JSP頁面
		out.print(jsonObj.toString());
		out.flush();
	}
	/**
	 * 更新基本資料
	 *//*
	@RequestMapping(params="method=updPATblByKey")
	public void updPATblByKey(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String me = getClass().getName() + ".updPATBLByKey()";
		response.setCharacterEncoding("utf-8");//如果有中文，避免亂碼
		PrintWriter out = response.getWriter();
		
		boolean blnupd=false;
		hid=request.getParameter("hid");
		String editMode=request.getParameter("editMode");
		String tblname=request.getParameter("tblname");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String pk_data = request.getParameter("pk_data")==null?"":request.getParameter("pk_data");//傳入json
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		//透過WPSAutoLogin取得登入者資訊，已經存在session，可以自行判斷是否需要帶入頁面
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		if (user!=null){
			tDAO = new SalNTDAO(da,hid);
			try{
	    		String tabJson = request.getParameter("tabJson");
				String hidno=request.getParameter("hidno");
				blnupd =  tDAO.updPATblByKey(editMode,tblname,pk_data,tabJson,userid,hid,da);
				if (blnupd){	//update success,重新reload
					jsonObj.put("msg", tblname+pk_data+" "+editMode+" 成功");
				}else{
					jsonObj.put("msg", tblname+pk_data+" "+editMode+" 失敗");
				}
				
			}catch(Exception e){
				jsonObj.element("msg", e.toString());
				log.info(tblname+" "+editMode+"失敗==>"+e.toString());
				SendMail.sendMail("[PSM]"+user.getLoginId()+" 更新 "+ pk_data +" 《薪資基本檔》失敗!",e.getMessage());
				e.printStackTrace();			
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
		}
		// 寫出至JSP頁面
		out.print(jsonObj.toString());
		out.flush();		
	}*/
	/**
	 * 更新所得稅資料
	 */
	@RequestMapping(params="method=updTaxByKey")
	public void updTaxByKey(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String me = getClass().getName() + ".updTaxByKey()";
		response.setCharacterEncoding("utf-8");//如果有中文，避免亂碼
		PrintWriter out = response.getWriter();
		
		boolean blnupd=false;
		hid=request.getParameter("hid");
		String item=request.getParameter("item");
		String tblname=request.getParameter("tblname");
		userid=request.getParameter("userid");
		String pk_data = request.getParameter("pk_data")==null?"":request.getParameter("pk_data");//傳入json
		String tabJson = request.getParameter("tabJson");
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		//透過WPSAutoLogin取得登入者資訊，已經存在session，可以自行判斷是否需要帶入頁面
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		if (user!=null){
			tDAO = new SalNTDAO(da,hid);
			try{
	    		
				String hidno=request.getParameter("hidno");
				blnupd =  tDAO.updTaxByKey(item,pk_data,tabJson,userid,hid,da);
				if (blnupd){	//update success,重新reload
					jsonObj.put("msg", tblname +" pk_data:"+pk_data+" item:"+item+" 成功");
				}else{
					jsonObj.put("msg", tblname +" pk_data:"+pk_data+" item:"+item+" 失敗");
				}
				
			}catch(Exception e){
				jsonObj.element("msg", e.toString());
				log.info(tblname+" "+item+"失敗==>"+e.toString());
				//SendMail.sendMail("[PSM]"+user.getLoginId()+" item:"+item +" pk_data:"+pk_data +" tabJson:"+tabJson+" 《"+tblname+"》失敗!",e.getMessage());
				e.printStackTrace();			
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
		}
		// 寫出至JSP頁面
		out.print(jsonObj.toString());
		out.flush();		
	}
	/**
	 * 預先載入代碼 item:表單(payDetail發薪明細 psnBasic基本檔) tranPay異動檔 payTax所得稅 batchPay批次檔)
	 * 
	 */
	@RequestMapping(params="method=getLoadCodeTbl")
	public void getLoadCodeTbl(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getLoadCodeTbl()";
		JsonConfig cfg = new JsonConfig();
		String flag = request.getParameter("flag")==null?"":request.getParameter("flag");//Y:重讀,N:不處理	
		String item = request.getParameter("item")==null?"":request.getParameter("item");	//表單
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		ArrayList alRankCodeTbl= null;
		ArrayList alTitleCodeTbl= null;
		ArrayList alBankDtTbl= null;
		ArrayList alBankDtTbl_2M= null;
		ArrayList alDeptCodeTbl= null;
		ArrayList alRnk4Tbl= null;
		ArrayList alexpfTbl= null;
		ArrayList altracdTbl= null;
		ArrayList alpayitmTbl=null;//支扣代碼
		ArrayList alDpinTbl=null;//健保級距
		ArrayList alTaxYrTbl=null;//所得稅年度
		ArrayList alTaxCatgTbl=null;//所得稅轉帳項目
		ArrayList alTaxTranYrTbl=null;//已轉帳所得稅年月
		ArrayList alPayDtTbl=null;//補充費給付日期
		ArrayList alPayYrTbl=null;//補充費給付年度
		
		//讀取pspm.pccosttbl,放至session,只需讀一次
		alRankCodeTbl= (ArrayList) request.getSession().getAttribute("alRankCodeTbl");
		alTitleCodeTbl= (ArrayList) request.getSession().getAttribute("alTitleCodeTbl");
		alBankDtTbl= (ArrayList) request.getSession().getAttribute("alBankDtTbl");
		alBankDtTbl_2M= (ArrayList) request.getSession().getAttribute("alBankDtTbl_2M");
		alDeptCodeTbl= (ArrayList) request.getSession().getAttribute("alDeptCodeTbl");
		alDpinTbl= (ArrayList) request.getSession().getAttribute("alDpinTbl");
		if ("psnBasic".equals(item)){
			alRnk4Tbl= (ArrayList) request.getSession().getAttribute("alRnk4Tbl");
			alexpfTbl= (ArrayList) request.getSession().getAttribute("alexpfTbl");
			altracdTbl= (ArrayList) request.getSession().getAttribute("altracdTbl");
		}
		else if(item.matches("tranPay|payDetail")){
			alpayitmTbl= (ArrayList) request.getSession().getAttribute("alpayitmTbl");
		}
		else if("payTax".equals(item)){
			alTaxYrTbl= (ArrayList) request.getSession().getAttribute("alTaxYrTbl");
			alTaxCatgTbl= (ArrayList) request.getSession().getAttribute("alTaxCatgTbl");
			alTaxTranYrTbl= (ArrayList) request.getSession().getAttribute("alTaxTranYrTbl");
		}
		else if("payNhi".equals(item)){
			alPayDtTbl= (ArrayList) request.getSession().getAttribute("alPayDtTbl");
			alPayYrTbl= (ArrayList) request.getSession().getAttribute("alPayYrTbl");
		}
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			System.out.println("dataSourceNT:"+dataSourceNT);
			if (da==null){
				da=new VGHDataAccess(dataSourceNT);
				System.out.println("da is null");
			}
			
			tDAO = new SalNTDAO(da,hid);
			if (tDAO==null){
				System.out.println("tDAO is null");
			}
			try{
				System.out.println("1.PARANKCODE:");
				if (alRankCodeTbl==null||alRankCodeTbl.size()==0 || flag.equals("Y") ){	
					alRankCodeTbl=tDAO.getLoadCodeTbl("PARANKCODE",userid,hid,da);
					request.getSession().setAttribute("alRankCodeTbl", alRankCodeTbl);						
				}
				System.out.println("2.PATITLE:");
				if (alTitleCodeTbl==null||alTitleCodeTbl.size()==0 || flag.equals("Y") ){	
					alTitleCodeTbl=tDAO.getLoadCodeTbl("PATITLE",userid,hid,da);
					request.getSession().setAttribute("alTitleCodeTbl", alTitleCodeTbl);  
				}
				System.out.println("3.PABANK:");
				if (alBankDtTbl==null||alBankDtTbl.size()==0 || flag.equals("Y")){	
					alBankDtTbl=tDAO.getLoadCodeTbl("PABANK",userid,hid,da);
					request.getSession().setAttribute("alBankDtTbl", alBankDtTbl);  
				}
				System.out.println("4.PABANK_2M:");
				if (alBankDtTbl_2M==null||alBankDtTbl_2M.size()==0 || flag.equals("Y")){	
					alBankDtTbl_2M=tDAO.getLoadCodeTbl("PABANK_2M",userid,hid,da);
					request.getSession().setAttribute("alBankDtTbl_2M", alBankDtTbl_2M);  
				}
				System.out.println("5.PADEPT:");
				if (alDeptCodeTbl==null||alDeptCodeTbl.size()==0 || flag.equals("Y")){	
					alDeptCodeTbl=tDAO.getLoadCodeTbl("PADEPT",userid,hid,da);
					request.getSession().setAttribute("alDeptCodeTbl", alDeptCodeTbl);
					System.out.println("5.PADEPT:size("+alDeptCodeTbl.size()+")");
				}
				System.out.println("6.PADPINTBL:");
				if (alDpinTbl==null||alDpinTbl.size()==0 || flag.equals("Y")){	
					alDpinTbl=tDAO.getLoadCodeTbl("PADPINTBL",userid,hid,da);
					request.getSession().setAttribute("alDpinTbl", alDpinTbl);  
				}
				System.out.println("7.PAPAYITM:");
				if (item.matches("tranPay|batchPay|payDetail")){	
					if (alpayitmTbl==null||alpayitmTbl.size()==0 || flag.equals("Y")){	
						alpayitmTbl=tDAO.getLoadCodeTbl("PAPAYITM",userid,hid,da);
						request.getSession().setAttribute("alpayitmTbl", alpayitmTbl);  
					}
				}
				System.out.println("8.psnBasic|tranPay:");
				if (item.matches("psnBasic|tranPay")){	
					if (alRnk4Tbl==null||alRnk4Tbl.size()==0){
						alRnk4Tbl=tDAO.getLoadCodeTbl("PARNK4TBL",userid,hid,da);
						request.getSession().setAttribute("alRnk4Tbl", alRnk4Tbl);
					}
					if (alexpfTbl==null||alexpfTbl.size()==0){
						alexpfTbl=tDAO.getLoadCodeTbl("PAEXPFTBL",userid,hid,da);
						request.getSession().setAttribute("alexpfTbl", alexpfTbl);
					}
					if (altracdTbl==null||altracdTbl.size()==0){
						altracdTbl=tDAO.getLoadCodeTbl("PATRACODE",userid,hid,da);
						request.getSession().setAttribute("altracdTbl", altracdTbl);
					}
				}
				System.out.println("9.payTax:");
				if (item.matches("payTax")){	
					if (alTaxYrTbl==null || alTaxYrTbl.size()==0 || flag.equals("Y")){	
						alTaxYrTbl=tDAO.getLoadCodeTbl("TAXPSNYR",userid,hid,da);
						request.getSession().setAttribute("alpayitmTbl", alTaxYrTbl);  
					}
					if (alTaxCatgTbl==null || alTaxCatgTbl.size()==0 || flag.equals("Y")){	
						alTaxCatgTbl=tDAO.getLoadCodeTbl("TAXCATG",userid,hid,da);
						request.getSession().setAttribute("alTaxCatgTbl", alTaxCatgTbl);  
					}
					if (alTaxTranYrTbl==null || alTaxTranYrTbl.size()==0 || flag.equals("Y")){	
						alTaxTranYrTbl=tDAO.getLoadCodeTbl("TAXTRANYR",userid,hid,da);
						request.getSession().setAttribute("alTaxTranYrTbl", alTaxTranYrTbl);  
					}
				}
				System.out.println("10.payNhi:");
				if (item.matches("payNhi")){	
					if (alPayDtTbl==null || alPayDtTbl.size()==0 || flag.equals("Y")){	
						alPayDtTbl=tDAO.getLoadCodeTbl("NHIPAYDT",userid,hid,da);
						request.getSession().setAttribute("alPayDtTbl", alPayDtTbl);  
					}
					if (alPayYrTbl==null || alPayYrTbl.size()==0 || flag.equals("Y")){	
						alPayYrTbl=tDAO.getLoadCodeTbl("NHIPAYYR",userid,hid,da);
						request.getSession().setAttribute("alPayYrTbl", alPayYrTbl);  
					}
				}	
				jsonObj.element("alRankCodeTbl", alRankCodeTbl);
				jsonObj.element("alTitleCodeTbl", alTitleCodeTbl);
				jsonObj.element("alBankDtTbl", alBankDtTbl,cfg);
				jsonObj.element("alBankDtTbl_2M", alBankDtTbl_2M,cfg);
				jsonObj.element("alDeptCodeTbl", alDeptCodeTbl);
				jsonObj.element("alDpinTbl", alDpinTbl);
				if (item.matches("psnBasic|tranPay")){
					jsonObj.element("alRnk4Tbl", alRnk4Tbl);
					jsonObj.element("alexpfTbl", alexpfTbl);
					jsonObj.element("altracdTbl", altracdTbl);
				}
				if (item.matches("tranPay|batchPay|payDetail|bonus")){
					jsonObj.element("alpayitmTbl", alpayitmTbl);					
				}
				if (item.matches("payTax")){
					jsonObj.element("alTaxYrTbl", alTaxYrTbl);
					jsonObj.element("alTaxCatgTbl", alTaxCatgTbl);
					jsonObj.element("alTaxTranYrTbl", alTaxTranYrTbl);
				}
				if (item.matches("payNhi")){
					jsonObj.element("alPayYrTbl", alPayYrTbl);
					jsonObj.element("alPayDtTbl", alPayDtTbl);					
				}
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				if (da != null) {
					try {
						da.close();
						da = null;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 依KEY,取得選單代碼檔維護 codeId(A
	 */
	@RequestMapping(params="method=qryCodeList")
	public void qryCodeList(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".qryCodeList()";
		response.setCharacterEncoding("utf-8");
		String tblname = request.getParameter("tblname")==null?"":request.getParameter("tblname");
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{
			tDAO = new SalNTDAO(da,hid);
			try{
				jsonArrRtn=tDAO.qryCodeList(tblname,code_data,userid,hid,da);
				// 用一個root tag將JSONArray包裝起來
				jsonObj.element("codeData", jsonArrRtn);  
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 依KEY,取得所得稅個人明細/轉帳+現金支扣
	 */
	@RequestMapping(params="method=getTaxPsnByKey")
	public void getTaxPsnByKey(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getTaxPsnByKey()";
		AmJsonConfig cfg = new AmJsonConfig();
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		Pataxpsn psnitem=null;
		HashMap TaxMap=new HashMap();
		HashMap TaxTranMap=new HashMap();
		HashMap TaxCashMap=new HashMap();
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,hid);
			try{
				jsonObj = new JSONObject();
				//所得稅個人明細
				psnitem=tDAO.getTaxPsnBykey(code_data,hid,da);
				jsonObj.element("taxPsnItem", psnitem,cfg);
				//所得稅轉帳支扣
				TaxMap=tDAO.getTaxPayListbyKey(code_data,userid,hid,da);
				jsonObj.element("taxTranList", TaxMap.get("taxTran_json"));
				//所得稅現金支扣
				jsonObj.element("taxCashList", TaxMap.get("taxCash_json"));
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 依KEY,取得所得稅個人明細LIST__所得稅編輯
	 */
	@RequestMapping(params="method=getTaxTaskList")
	public void getTaxTaskList(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getTaxTaskList()";		
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,hid);
			try{
				jsonArrRtn=tDAO.getTaxTaskList(code_data,userid,hid,da);
				// 用一個root tag將JSONArray包裝起來
				jsonObj.element("taxTaskList", jsonArrRtn);  
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 依KEY,取得所得稅查看LIST
	 */
	@RequestMapping(params="method=getTaxViewList")
	public void getTaxViewList(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getTaxViewList()";		
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
//		if("2A0".equals(hid))
//			da=new VGHDataAccess(Globals.dataSource_NT_PT);
//		else if("3A0".equals(hid))
//			da=new VGHDataAccess(Globals.dataSource_NT_LH);
//		else if("1A0".equals(hid))
//			da=new VGHDataAccess(Globals.dataSource_NT);
//		
//		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,hid);
			try{
				jsonArrRtn=tDAO.getTaxViewList(code_data,userid,hid,da);
				// 用一個root tag將JSONArray包裝起來
				jsonObj.element("taxViewList", jsonArrRtn);  
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				jsonObj.element("taxViewList", e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 依KEY,取得所得稅彙總LIST
	 */
	@RequestMapping(params="method=getTaxSumList")
	public void getTaxSumList(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getTaxSumList()";		
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,hid);
			try{
				jsonArrRtn=tDAO.getTaxSumList(code_data,userid,hid,da);
				// 用一個root tag將JSONArray包裝起來
				jsonObj.element("taxSumList", jsonArrRtn);  
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 依KEY,取得所得稅彙總LIST
	 */
	@RequestMapping(params="method=getTaxStatList")
	public void getTaxStatList(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getTaxStatList()";		
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,hid);
			try{
				jsonArrRtn=tDAO.getTaxStatList(code_data,userid,hid,da);
				// 用一個root tag將JSONArray包裝起來
				jsonObj.element("taxStatList", jsonArrRtn);  
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 依KEY,取得月支個人明細LIST
	 */
	@RequestMapping(params="method=getDatPsnList")
	public void getDatPsnList(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getDatPsnList()";		
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String in_data = request.getParameter("in_data")==null?"":request.getParameter("in_data");//傳入json
		
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,hid);
			try{
				jsonArrRtn=tDAO.getDatPsnList(in_data,userid,hid,da);
				// 用一個root tag將JSONArray包裝起來
				jsonObj.element("datPsnList", jsonArrRtn);  
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 依KEY,取得基本個人明細LIST item:1個人明細,2篩選
	 */
	@RequestMapping(params="method=getBasPsnList")
	public void getBasPsnList(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getBasPsnList()";
				
		//VGHDataAccess da=new VGHDataAccess(Globals.dataSource_NT);
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		String item = request.getParameter("item")==null?"":request.getParameter("item");
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,hid);
			try{
				jsonArrRtn=tDAO.getBasPsnList(code_data,item,userid,hid,da);
				// 用一個root tag將JSONArray包裝起來
				if ("1".equals(item)){
					jsonObj.element("basPsnList", jsonArrRtn);
				}else if ("2".equals(item)){
					jsonObj.element("basPsnFilter", jsonArrRtn);
				}
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 依KEY,取得異動檔個人明細LIST item:1個人明細,2篩選
	 */
	@RequestMapping(params="method=getTranPsnList")
	public void getTranPsnList(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getTranPsnList()";
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		//VGHDataAccess da=new VGHDataAccess(Globals.dataSource_NT);

		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		String item = request.getParameter("item")==null?"":request.getParameter("item");
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,hid);
			try{
				jsonArrRtn=tDAO.getTranPsnList(code_data,item,userid,hid,da);
				// 用一個root tag將JSONArray包裝起來
				if ("1".equals(item)){
					jsonObj.element("traPsnList", jsonArrRtn);
				}else if ("2".equals(item)){
					jsonObj.element("traPsnFilter", jsonArrRtn);
				}
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 依異動/批次,取得餉單編號清單
	 */
	@RequestMapping(params="method=getSalnumList")
	public void getSalnumList(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getSalnumList()";		
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		String in_data = request.getParameter("in_data")==null?"":request.getParameter("in_data");//傳入json
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,hid);
			try{
				jsonArrRtn=tDAO.getSalnumList(in_data,userid,hid,da);
				// 用一個root tag將JSONArray包裝起來
				jsonObj.element("salnumList", jsonArrRtn);  
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 依KEY,取得月支個人明細
	 */
	@RequestMapping(params="method=getDatPsnByKey")
	public void getDatPsnByKey(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getDatPsnByKey()";
		AmJsonConfig cfg = new AmJsonConfig();
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		Padatpsn psnitem=null;
		HashMap datPayMap=new HashMap();
		HashMap datPaySumMap=new HashMap();
		String in_data = request.getParameter("in_data")==null?"":request.getParameter("in_data");//傳入json
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,hid);
			try{
				jsonObj = new JSONObject();
				//個人明細
				psnitem=tDAO.getDatPsnBykey(in_data,userid,hid,da);
				jsonObj.element("datPsnItem", psnitem,cfg);
				//個人支扣
				datPayMap=tDAO.getDatPayList(in_data,psnitem,userid,hid,da);
				jsonObj.element("datPayList", datPayMap.get("datpay_json"));
				//淨所得,應稅所得
				datPaySumMap=(HashMap) datPayMap.get("datpaySum_map");
				jsonObj.element("datPaySum", datPaySumMap);
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 依卡號取的人名
	 * 2026.5.27 加HID,2A0,3A0限查自家
	 */
	@RequestMapping(params="method=getNamebyID")
	public void getNamebyID(HttpServletRequest request, HttpServletResponse response){
		response.setCharacterEncoding("utf-8");//如果有中文，避免亂碼
		String me = getClass().getName() + ".getNamebyID()";
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String cardno = request.getParameter("cardno")==null?"":request.getParameter("cardno");		
		String loginHid = request.getParameter("loginHid")==null?"":request.getParameter("loginHid");
		try{
			tDAO = new SalNTDAO();
			try{
				jsonObj = tDAO.queryNamebyID(cardno,loginHid);
				
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 設定代理人卡號至Session
	 */
	@RequestMapping(params="method=setAgent")
	public void setAgent(HttpServletRequest request, HttpServletResponse response)throws IOException{
		response.setCharacterEncoding("utf-8");//如果有中文，避免亂碼
		String me = getClass().getName() + ".setAgent()";
		PrintWriter out = response.getWriter();
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		try{
			String agent = request.getParameter("agent");
			if (agent.equals(user.getLoginId())){
				request.getSession().removeAttribute("agent");
				request.getSession().removeAttribute("agentname");
				request.getSession().removeAttribute("agentcostid");
				request.getSession().removeAttribute("agentdeptno");
				request.getSession().removeAttribute("agentdeptnm");
				request.getSession().removeAttribute("agenthid");
				request.getSession().removeAttribute("agentpswktypd");
				request.getSession().removeAttribute("agentAdmin");
				request.getSession().removeAttribute("agentEdit");
				request.getSession().removeAttribute("agentView");
				request.getSession().removeAttribute("agentBones");
				request.getSession().removeAttribute("agentTax");
			}else{
				request.getSession().setAttribute("agent", agent);//代理人卡號
				request.getSession().setAttribute("agentname", request.getParameter("agentname"));//姓名
				request.getSession().setAttribute("agentcostid", request.getParameter("agentcostid"));//成本中心
				request.getSession().setAttribute("agentdeptno", request.getParameter("agentdeptno"));	
				request.getSession().setAttribute("agentdeptnm", request.getParameter("agentdeptnm"));
				request.getSession().setAttribute("agenthid", request.getParameter("agenthid"));
				request.getSession().setAttribute("agentpswktypd", request.getParameter("agentpswktypd"));
				request.getSession().setAttribute("agentAdmin", request.getParameter("agentAdmin"));
				request.getSession().setAttribute("agentEdit", request.getParameter("agentEdit"));
				request.getSession().setAttribute("agentView", request.getParameter("agentView"));
				request.getSession().setAttribute("agentBones", request.getParameter("agentBones"));
				request.getSession().setAttribute("agentTax", request.getParameter("agentTax"));
			}
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 依KEY,取得個人明細
	 */
	@RequestMapping(params="method=getBasPsnByKey")
	public void getBasPsnByKey(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getBasPsnByKey()";
		//如果有中文，避免亂碼
		response.setCharacterEncoding("utf-8");
		
		AmJsonConfig cfg = new AmJsonConfig();
		Pabaspsn psnitem=null;
		HashMap basPayMap=new HashMap();
		HashMap histListMap=new HashMap();
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String tracod = request.getParameter("tracod")==null?"0":request.getParameter("tracod");
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{
			tDAO = new SalNTDAO(da,hid);
			try{
				jsonObj = new JSONObject();
				//個人明細
				psnitem=tDAO.getBasPsnBykey(code_data,userid,da);
				jsonObj.element("basPsnItem", psnitem,cfg);
				//tracod>0是異動檔,不用讀取個人支扣:1新進2復職
				if ( psnitem != null && !tracod.matches("1|2") ) {
					//個人支扣
					basPayMap=tDAO.getBasPayList(psnitem.getPayidno(),tracod,userid,hid,da);
					jsonObj.element("basPayList", basPayMap.get("baspay_json"));
					//歷史資料
					histListMap=tDAO.getHistList(psnitem.getPayidno(),userid,hid,da);
					jsonObj.element("histList", histListMap.get("pahist_json"),cfg);
				}
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 依ID,取得主機人事資料
	 */
	@RequestMapping(params="method=getPSBasicById")
	public void getPSBasicById(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getPSBasicById()";
		//如果有中文，避免亂碼
		response.setCharacterEncoding("utf-8");
		
		AmJsonConfig cfg = new AmJsonConfig();
		Pabaspsn psnitem=null;
		
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		String dataSourceDSN=hospInfo.dataSourceDSN;
		daDSN=new VGHDataAccess(dataSourceDSN);
		
		String dataSourceNT=hospInfo.dataSourceNT;		
		da=new VGHDataAccess(dataSourceNT);
		
		try{
			tDAO = new SalNTDAO(da,daDSN,hid);
			try{
				jsonObj = new JSONObject();
				//個人明細
				psnitem=tDAO.getPSBasicById(code_data,userid,da,daDSN);
				jsonObj.element("basPsnItem", psnitem,cfg);				
				
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 依餉單編號/發薪日期,取得月支支扣pa.padatpay_Sum
	 */
	@RequestMapping(params="method=getCheckPayList")
	public void getCheckPayList(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getCheckPayList()";
		//如果有中文，避免亂碼
		response.setCharacterEncoding("utf-8");		
		AmJsonConfig cfg = new AmJsonConfig();		
		jsonArrRtn=new JSONArray();		
		String in_data = request.getParameter("in_data")==null?"":request.getParameter("in_data");//傳入json
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		try{
			tDAO = new SalNTDAO(da,hid);
			try{
				jsonObj = new JSONObject();
				//支扣sum
				jsonArrRtn=tDAO.getCheckPayBykey(in_data,userid,hid,da);
				jsonObj.element("checkPayList", jsonArrRtn);
				
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 依支扣代碼,取得日期範圍名單
	 */
	@RequestMapping(params="method=getItmPayList")
	public void getItmPayList(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getItmPayList()";
		//如果有中文，避免亂碼
		response.setCharacterEncoding("utf-8");		
		AmJsonConfig cfg = new AmJsonConfig();		
		jsonArrRtn=new JSONArray();		
		String in_data = request.getParameter("in_data")==null?"":request.getParameter("in_data");//傳入json
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		try{
			tDAO = new SalNTDAO(da,hid);
			try{
				jsonObj = new JSONObject();
				jsonArrRtn=tDAO.getItmPayList(in_data,userid,hid,da);
				jsonObj.element("itmPayList", jsonArrRtn);
				
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 依salnum+paycode找pa.padaypay清單
	 */
	@RequestMapping(params="method=getDayPayByCode")
	public void getDayPayByCode(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getDayPayByCode()";
		//如果有中文，避免亂碼
		response.setCharacterEncoding("utf-8");
		
		AmJsonConfig cfg = new AmJsonConfig();
		
		jsonArrRtn=new JSONArray();		
		String in_data = request.getParameter("in_data")==null?"":request.getParameter("in_data");//傳入json
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{
			tDAO = new SalNTDAO(da,hid);
			try{
				jsonObj = new JSONObject();
				//支扣清單
				jsonArrRtn=tDAO.getDayPayByCode(in_data,userid,hid,da);
				jsonObj.element("datPayBycodeList", jsonArrRtn);
				
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 代碼維護 新增一筆/更改
	 */
	@RequestMapping(params="method=updTblCode")
	public void updTblCode(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String me = getClass().getName() + ".updTblCode()";
		response.setCharacterEncoding("utf-8");//如果有中文，避免亂碼
		PrintWriter out = response.getWriter();
		hid = request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		boolean blnFlag=false;
		//透過WPSAutoLogin取得登入者資訊，已經存在session，可以自行判斷是否需要帶入頁面
		//WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		HashMap<String, String> OneMap=new HashMap<String, String>();
		boolean success=false;
		try{			
			String item=request.getParameter("item");
			String tabJson = request.getParameter("tabJson");
			userid = request.getParameter("userid");
			tDAO = new SalNTDAO(da,hid);
			blnFlag = tDAO.updSalCodeTbl(item,tabJson,userid,hid,da);
			if (blnFlag){	//update success,重新reload
				jsonObj.put("msg", item+"成功");
				
			}else{
				jsonObj.put("msg", item+"失敗");
				jsonObj.put("status", item+"N");
			}
			
		}catch(Exception e){
			jsonObj.element("msg", e.toString());
			log.info("代碼維護 [新增一筆/更改] 失敗=="+e.toString());
			e.printStackTrace();			
		}finally{
			tDAO=null;
			da.close();
			da = null;
		}
		// 寫出至JSP頁面
		out.print(jsonObj.toString());
		out.flush();
	}
	
	/**
	 * 輸入卡號,找出對應姓名
	 */
	@RequestMapping(params="method=searchNm")
	public void searchNm(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".searchNm()";
		Map<String, Object> map2 = new HashMap<String, Object>();
		ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();
		String cardno = request.getParameter("cardno")==null?"":request.getParameter("cardno");		
		
		LDAPUtil ldapUtilObj = LDAPUtil.getInstance() ; 
		String userName = "";
		jsonObj = new JSONObject();
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			userName =ldapUtilObj.getUserNm(cardno);
			String hostname =request.getServerName();				    
		    String programName=this.getClass().getName();
		    ArrayList ou=ldapUtilObj.getAllMOu(hostname, programName);
			map2.put("name", userName);
			arrayList.add(map2);
			JSONArray jsonArr = JSONArray.fromObject(arrayList);
			// 用一個root tag將JSONArray包裝起來
			jsonObj.element("namedata", jsonArr);
								
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * [檔案產製]主計室,績效組
	 * 1:主計室_發放:BAS_1 selCode:selNm:selEn
	 */
	@RequestMapping(params="method=handelPrtFile")
	public void handelPrtFile(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String me = getClass().getName() + ".handelPrtFile()";
		//如果有中文，避免亂碼
		response.setCharacterEncoding("utf-8");		
		AmJsonConfig cfg = new AmJsonConfig();
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid=request.getParameter("userid");
		String in_data = request.getParameter("in_data")==null?"":request.getParameter("in_data");//傳入json
		JSONObject object_in_data =null;
		String selNm = "";
		int salnum = 0;
		String selEn = "";
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{
			tDAO = new SalNTDAO(da,hid);
			try{
				if (in_data!=null && ! in_data.equals("")){
					object_in_data = JSONObject.fromObject(in_data);
					selEn=(!object_in_data.has("selEn")) ? "" : object_in_data.getString("selEn");					
				}
				//主計
				if ( ! selEn.equals("") && selEn.matches("BAS_1|BAS_2") ){
					jsonArrRtn=tDAO.getBASFileList(in_data,userid,hid,da);
				}
				//績效
				else if ( ! selEn.equals("") && selEn.matches("Perf_1|Perf_2") ){
					jsonArrRtn=tDAO.getPerfFileList(in_data,userid,hid,da);
				}
				// 用一個root tag將JSONArray包裝起來
				jsonObj.element("fileData", jsonArrRtn);  
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * [產生/查詢/PDF]報表檔 銀行作業{媒體傳輸}
	 * 1:月報:Prt_M_1 selCode:selNm:selEn
	 */
	@RequestMapping(params="method=handelPrtSal")
	public void handelPrtSal(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String me = getClass().getName() + ".handelPrtSal()";
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid=request.getParameter("userid");
		String in_data = request.getParameter("in_data")==null?"":request.getParameter("in_data");//傳入json
		JSONObject object_in_data =null;
		String selNm = "";
		int salnum = 0;
		String selEn = "";
		String selCode ="";
		String patradt="";
		String selPs="";
		String fun = request.getParameter("fun")==null?"":request.getParameter("fun");
		String strDir ="";//資料夾路徑
		String strYM ="";
		String strYMD ="";
		String strMD ="";
		String strRtn = "";
		String dateString ="";
		String pdfFileName ="";
		String txtFileName ="";
		Paprtdtl prtobj=null;
		boolean blnSum=false;	//0-總計 1,2,3...-各項表冊
		boolean blnDirection=true;	//true橫,false直
		int intPrts=0;	//HID 不同表冊數量
		String strmsg="";
		boolean markPrtInfo =false;
		long starttime =0;
		String dataSourceNT="";
		ArrayList<Paprtdtl> paprtdtlAry=null;
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		CommDAO cDAO = null;
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		jsonObj = new JSONObject();
		FileUtil fileUtil = new FileUtil();
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,hid);
			cDAO = new CommDAO(da,hid);
			//1:月報:Prt_M_1 selCode:selNm:selEn
			if (in_data!=null && ! in_data.equals("")){
				object_in_data = JSONObject.fromObject(in_data);
				selEn=(!object_in_data.has("selEn")) ? "" : object_in_data.getString("selEn");
				selCode=(!object_in_data.has("selCode")) ? "" : object_in_data.getString("selCode");
				selNm=(!object_in_data.has("selNm")) ? "" : object_in_data.getString("selNm");
				patradt=(!object_in_data.has("patradt")) ? "" : object_in_data.getString("patradt");
				selPs=(!object_in_data.has("selPs")) ? "" : object_in_data.getString("selPs");
				salnum=(!object_in_data.has("salnum")) ? 0 : object_in_data.getInt("salnum");
			}
			
			try{
				//[產生]報表檔
				if (fun.equals("handelPrt") ){
					strRtn=tDAO.prodPrtSal(in_data,userid,hid,da);
					if ("".equals(strRtn)) {
						strmsg=selNm+"成功";
						jsonObj.put("status", "Y");
					}else{
						jsonObj.put("status", "N");
						strmsg=selNm+"更新失敗\n\r"+strRtn;
					}
				}	
				//[查詢]報表檔
				else if (fun.equals("inqPrt") ){
					jsonArrRtn=tDAO.getRrtDtlList(in_data,userid,hid,da);
					// 用一個root tag將JSONArray包裝起來
					jsonObj.element("prtDtlList", jsonArrRtn);  
				}
				//報表檔[PDF]
				else if (fun.equals("printPrt") ){
					//資料夾路徑
					strDir = hospInfo.fileSRV+"prt\\";
					//strDir = hospInfo.fileSRV+"bak\\";
					//冊子最大數
					//薪津表(補發扣)
					if ( selEn.matches("Prt_M_3") )
						intPrts=2;
					//薪津異動明細表Prt_M_4 發薪受款者明細表:Prt_M_5
					else if ( selEn.matches("Prt_M_4|Prt_M_5") )
						intPrts=1;
					//薪津冊Prt_M_1 扣款表Prt_M_2
					else if ( selEn.matches("Prt_M_1|Prt_M_2") ){
						if (hid.equals("1A0"))
							intPrts=18;
						else if (hid.equals("2A0"))
							intPrts=9;
						else if (hid.equals("3A0"))
							intPrts=4;
					}
					//銀行轉帳明細表:Prt_M_6
					else if ( selEn.matches("Prt_M_6") ){
						if (hid.equals("1A0"))
							intPrts=3;
						else if (hid.equals("2A0"))
							intPrts=2;
						else if (hid.equals("3A0"))
							intPrts=2;
					}
					
					starttime = System.currentTimeMillis();
					for (int i=0; i < intPrts ;i++){
						blnSum=false;
						//找出待印清單
						paprtdtlAry=tDAO.getPrtdtlBykey(in_data,i,userid,hid,da);
						//薪津表,扣款表,薪津表補發有總計欄
						if(i==0 && selEn.matches("Prt_M_1|Prt_M_2|Prt_M_3") ) blnSum=true;
						
						if(paprtdtlAry!=null && paprtdtlAry.size() != 0){
							prtobj=paprtdtlAry.get(0);
							dateString = prtobj.getPaybank().toString();
							if (strYM.equals("")){
								strYM=dateString.substring(0,4)+dateString.substring(5, 7);
								strDir+=strYM+"\\";
							}
							//資料夾路徑
							if (selEn.matches("Prt_M_1|Prt_M_2|Prt_M_3|Prt_M_6"))
								pdfFileName = "smb:" + (strDir+selNm+"_"+salnum+"_"+cDAO.getPrtNmBysal(selEn,i, hid)+".pdf").replace("\\", "/");
							else
								pdfFileName = "smb:" + (strDir+selNm+"_"+salnum+".pdf").replace("\\", "/");
							System.out.println("size:"+paprtdtlAry.size()+" smb:"+pdfFileName);
							
							//薪津表
							if ( selEn.matches("Prt_M_1") ) {
								blnDirection=true;//橫式
								if (hid.equals("1A0"))
									new Payslip_1A0(strDir,pdfFileName,blnSum,paprtdtlAry,hid);
								else if (hid.equals("2A0"))
									new Payslip_2A0(strDir,pdfFileName,blnSum,paprtdtlAry,hid);	
								else if (hid.equals("3A0"))
									new Payslip_3A0(strDir,pdfFileName,blnSum,paprtdtlAry,hid);	
							}
							//扣款表
							else if ( selEn.matches("Prt_M_2") ){
								blnDirection=true;//橫式
								if (hid.equals("1A0"))
									new Deduct_1A0(strDir,pdfFileName,blnSum,paprtdtlAry,hid);
								else if (hid.equals("2A0"))
									new Deduct_2A0(strDir,pdfFileName,blnSum,paprtdtlAry,hid);	
								else if (hid.equals("3A0"))
									new Deduct_3A0(strDir,pdfFileName,blnSum,paprtdtlAry,hid);	
							
							}//薪津表(補發扣)
							else if ( selEn.matches("Prt_M_3") ){
								if (hid.equals("1A0")){
									new Issue_1A0(strDir,pdfFileName,blnSum,paprtdtlAry,hid);
									blnDirection=false;//直式
								}	
								else if (hid.equals("2A0")){
									new Issue_2A0(strDir,pdfFileName,blnSum,paprtdtlAry,hid);
									blnDirection=false;//直式
								}	
								else if (hid.equals("3A0")){
									new Issue_3A0(strDir,pdfFileName,blnSum,paprtdtlAry,hid);
									blnDirection=true;//橫式
								}	
							}
							//薪津異動明細表
							else if ( selEn.matches("Prt_M_4") ){
								blnDirection=true;//橫式
								if (hid.equals("1A0"))
									new Chgdtl_1A0(strDir,pdfFileName,paprtdtlAry,hid);
								else if (hid.equals("2A0"))
									new Chgdtl_2A0(strDir,pdfFileName,paprtdtlAry,hid);
								else if (hid.equals("3A0"))
									new Chgdtl_3A0(strDir,pdfFileName,paprtdtlAry,hid);
							}
							//發薪受款者明細表:Prt_M_5
							else if ( selEn.matches("Prt_M_5") ){
								blnDirection=false;//直式
								if (hid.equals("1A0"))
									new ItemSum_1A0(strDir,pdfFileName,paprtdtlAry,hid);
								else if (hid.equals("2A0"))
									new ItemSum_2A0(strDir,pdfFileName,paprtdtlAry,hid);
								else if (hid.equals("3A0"))
									new ItemSum_3A0(strDir,pdfFileName,paprtdtlAry,hid);
							}
							//銀行轉帳明細表:Prt_M_6
							else if ( selEn.matches("Prt_M_6") ){
								blnDirection=false;//直式
								if (hid.equals("1A0"))
									new BankTran_1A0(strDir,pdfFileName,i,cDAO.getPrtNmBysal(selEn,i, hid),paprtdtlAry,hid);
								else if (hid.equals("2A0")){
									new BankTran_2A0(strDir,pdfFileName,paprtdtlAry,hid);
								}	
								else if (hid.equals("3A0"))
									new BankTran_3A0(strDir,pdfFileName,i,cDAO.getPrtNmBysal(selEn,i, hid),paprtdtlAry,hid);
							}
							//頁碼,總計不處理 
							if (!blnSum ){
								markPrtInfo = fileUtil.markPrtPg(user.getLoginId(), blnDirection,pdfFileName,hid);
								
								if (markPrtInfo){
									System.out.println(pdfFileName);
								}
							}
							strmsg+=i+"["+cDAO.getPrtNmBysal(selEn,i, hid)+"]產製\n";
							
						}
						else{
							strmsg+=i+"["+cDAO.getPrtNmBysal(selEn,i, hid)+"]無資料\n";							
						}
					}
					
					strmsg += "請至"+strDir+"查看, 共花費："+(System.currentTimeMillis()-starttime)+"毫秒";						
				}
				//銀行作業{媒體傳輸}
				else if (fun.equals("bankMedia") ){
					//資料夾路徑
					strDir = hospInfo.fileSRV+"bank\\";
					//冊子最大數
					if ( selEn.matches("Bank_T") ){
						if (hid.equals("1A0"))
							intPrts=3;
						else if (hid.matches("2A0|3A0"))
							intPrts=2;
					}
					starttime = System.currentTimeMillis();
					for (int i=0; i < intPrts ;i++){
					//for (int i=0; i < 1 ;i++){
						//找出待印清單
						paprtdtlAry=tDAO.getPrtdtlBykey(in_data,i,userid,hid,da);
						
						if(paprtdtlAry!=null && paprtdtlAry.size() != 0){
							prtobj=paprtdtlAry.get(0);
							dateString = prtobj.getPaybank().toString();
							if (strYM.equals("")){
								strYM=dateString.substring(0,4)+dateString.substring(5, 7);
								strYMD=DateUtil.getHcDT(prtobj.getPaybank());	//民國年月日,沒分隔號 20250901
								strMD=dateString.substring(5, 7)+dateString.substring(8, 10);
								strDir+=strYM+"\\";
							}
							if (hid.equals("1A0")){
								//資料夾路徑
								txtFileName = (strDir+selNm+"_"+cDAO.getPrtNmBysal(selEn,i, hid)+strMD+".txt").replace("\\", "/");
								
								new BankMedia_1A0(strDir,txtFileName,paprtdtlAry,hid);
							}	
							else if (hid.equals("2A0")){
								if (i==0)
									txtFileName = (strDir+"TSBCUST"+strMD+".TXT").replace("\\", "/");
								else if (i==1)
									txtFileName = (strDir+"TSBCUSTS"+strMD+".TXT").replace("\\", "/");
								new BankMedia_2A0(strDir,txtFileName,paprtdtlAry,selPs,hid);	
							}	
							else if (hid.equals("3A0")){
								txtFileName = (strDir+strYMD+cDAO.getPrtNmBysal(selEn,i, hid)+String.valueOf(prtobj.getPayamt())+".TXT").replace("\\", "/");
								new BankMedia_3A0(strDir,txtFileName,paprtdtlAry,hid);
							}	
							System.out.println("size:"+paprtdtlAry.size()+" txt:"+txtFileName);	
							strmsg+=i+"["+cDAO.getPrtNmBysal(selEn,i, hid)+"]產製\n";
							
						}
						else{
							strmsg+=i+"["+cDAO.getPrtNmBysal(selEn,i, hid)+"]無資料\n";							
						}
					}
					
					strmsg += "請至"+strDir+"查看, 共花費："+(System.currentTimeMillis()-starttime)+"毫秒";  
				}
				
			}
			catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
				strmsg="產製失敗 " + e.toString();
			}finally{
				jsonObj.put("msg", strmsg);
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 發薪明細處理
	 */
	@RequestMapping(params="method=handelPayroll")
	public void handelPayroll(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String me = getClass().getName() + ".handelPayroll()";
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String in_data = request.getParameter("in_data")==null?"":request.getParameter("in_data");//傳入json
		String tabJson = request.getParameter("tabJson")==null?"":request.getParameter("tabJson");//傳入json
		
		String selNm = request.getParameter("selNm")==null?"":request.getParameter("selNm");
		String strRtn = "";
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		jsonObj = new JSONObject();
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,hid);
			try{
				
				strRtn=tDAO.handelPayroll(in_data,tabJson,userid,hid,da);
				if ("".equals(strRtn)) {
					jsonObj.put("msg", selNm+"成功");
					jsonObj.put("status", "Y");
				}else{
					jsonObj.put("status", "N");
					jsonObj.put("msg", selNm+"更新失敗\n\r"+strRtn);
				}
					
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 基本檔處理
	 */
	@RequestMapping(params="method=handelBas")
	public void handelBas(HttpServletRequest request, HttpServletResponse response)throws IOException{
		
		String me = getClass().getName() + ".handelBas()";
		AmJsonConfig cfg = new AmJsonConfig();
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String tabJson = request.getParameter("tabJson");
		String tblname = request.getParameter("tblname");
		String editMode = request.getParameter("editMode");
		String pk_data = request.getParameter("pk_data")==null?"":request.getParameter("pk_data");//傳入json
		String in_data = request.getParameter("in_data")==null?"":request.getParameter("in_data");//傳入json
		String fun = request.getParameter("fun")==null?"":request.getParameter("fun");
		String selNm = request.getParameter("selNm")==null?"":request.getParameter("selNm");
		String strRtn = "";
		JSONObject object_in_data =null;
		boolean blnupd=false;
		HashMap rtnMap=new HashMap();
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		Pabaspsn psnobj=null;
		Pabatpay batpayobj=null;
		Patrapay trapayobj=null;
		Patrapsn psnitem=null;
		HashMap traPayMap=new HashMap();
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		jsonObj = new JSONObject();
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,hid);
			try{
				//更新基本檔個人明細
				if (fun.matches("updBasPsn")){
					blnupd = tDAO.updPATblByKey(editMode,tblname,pk_data,tabJson,userid,hid,da);
					
					if (blnupd){	//update success,重新reload
						jsonObj.put("msg", tblname+pk_data+" "+editMode+" 成功");
					}else{
						jsonObj.put("msg", tblname+pk_data+" "+editMode+" 失敗");
					}
					log.info(userid+user.getUserNm()+ tblname+pk_data+" "+editMode +" blnupd:"+blnupd);
				}
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 異動檔處理
	 */
	@RequestMapping(params="method=handelTran")
	public void handelTran(HttpServletRequest request, HttpServletResponse response)throws IOException{
		
		String me = getClass().getName() + ".handelTran()";
		AmJsonConfig cfg = new AmJsonConfig();
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String tabJson = request.getParameter("tabJson");
		String tblname = request.getParameter("tblname");
		String editMode = request.getParameter("editMode");
		String pk_data = request.getParameter("pk_data")==null?"":request.getParameter("pk_data");//傳入json
		String in_data = request.getParameter("in_data")==null?"":request.getParameter("in_data");//傳入json
		String fun = request.getParameter("fun")==null?"":request.getParameter("fun");
		String selNm = request.getParameter("selNm")==null?"":request.getParameter("selNm");
		String strRtn = "";
		JSONObject object_in_data =null;
		boolean blnupd=false;
		HashMap rtnMap=new HashMap();
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		Pabaspsn psnobj=null;
		Pabatpay batpayobj=null;
		Patrapay trapayobj=null;
		Patrapsn psnitem=null;
		HashMap traPayMap=new HashMap();
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		jsonObj = new JSONObject();
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,hid);
			try{
				//更新異動檔個人明細
				if (fun.matches("updTraPsn")){
					blnupd = tDAO.updPATblByKey(editMode,tblname,pk_data,tabJson,userid,hid,da);
					
					if (blnupd){	//update success,重新reload
						jsonObj.put("msg", tblname+pk_data+" "+editMode+" 成功");
					}else{
						jsonObj.put("msg", tblname+pk_data+" "+editMode+" 失敗");
					}
					log.info(userid+user.getUserNm()+ tblname+pk_data+" "+editMode +" blnupd:"+blnupd);
				}else if (fun.matches("getTraPsnByKey")){
					//異動個人明細
					psnitem=tDAO.getTraPsnBykey(in_data,userid,da);
					jsonObj.element("traPsnItem", psnitem,cfg);
					//異動個人支扣
					traPayMap=tDAO.getTraPayList(in_data,userid,da);
					jsonObj.element("otraPayList", traPayMap.get("otrapay_json"));
					jsonObj.element("ntraPayList", traPayMap.get("ntrapay_json"));
				}
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 傳回 績效點數
	 */
	@RequestMapping(params="method=getCsPfBykey")
	public void getCsPfBykey(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String me = getClass().getName() + ".getCsPfBykey()";
		//職稱
		String vJt=request.getParameter("jt")==null?"":request.getParameter("jt");
		//職等
		String vRnk2n=request.getParameter("rnk2n")==null?"":request.getParameter("rnk2n");
		//階別
		String vRank=request.getParameter("rank")==null?"":request.getParameter("rank");
		//級別
		String vDegr2=request.getParameter("degr2")==null?"":request.getParameter("degr2");
		//本功別
		String vRank3=request.getParameter("rank3")==null?"":request.getParameter("rank3");
		//發薪日
		String vTradt=request.getParameter("tradt")==null?"":request.getParameter("tradt");
		
		HashMap rtnMap=new HashMap();
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		jsonObj = new JSONObject();
		CommDAO cDAO = null;
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			cDAO = new CommDAO(da,hid);
			try{
				if (StringUtil.nullToString(vRank3).equals("")) {
					vRank3="0";
				}
				rtnMap=cDAO.getCsPfByKey(Integer.valueOf(vRank3),vJt,vRank,vRnk2n,Integer.valueOf(vDegr2),vTradt,hid,da);
				jsonObj.element("cspfData", rtnMap);
				
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 傳回 A俸點對應 B取得新俸點 C計算補發扣
	 */
	@RequestMapping(params="method=getDegreByRank")
	public void getDegreByRank(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String me = getClass().getName() + ".getDegreByRank()";
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid=request.getParameter("userid");
		String in_data = request.getParameter("in_data")==null?"":request.getParameter("in_data");//傳入json
		String fun = request.getParameter("fun")==null?"":request.getParameter("fun");	//A俸點對應,B支扣明細
			
		HashMap rtnMap=new HashMap();
		Parank oneObj=null;
		String dataSourceNT="";
		String dataSourceDSN="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		dataSourceDSN=hospInfo.dataSourceDSN;		
		daDSN=new VGHDataAccess(dataSourceDSN);
		
		jsonObj = new JSONObject();
		CommDAO cDAO = null;
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			cDAO = new CommDAO(da,daDSN,hid);
			try{
				rtnMap=cDAO.getDegreByRank(in_data,fun,hid,da,daDSN);
				jsonObj.element("rankItem",rtnMap.get("rankItem"));
				jsonObj.element("payItemList",rtnMap.get("payitm_json"));
				jsonObj.element("insu_dpin",rtnMap.get("insu_dpin"));
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
				daDSN.close();
				daDSN = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 批次檔處理
	 */
	@RequestMapping(params="method=handelBatch")
	public void handelBatch(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String me = getClass().getName() + ".handelBatch()";
		AmJsonConfig cfg = new AmJsonConfig();
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String tabJson = request.getParameter("tabJson");
		String tblname = request.getParameter("tblname");
		String editMode = request.getParameter("editMode");
		String pk_data = request.getParameter("pk_data")==null?"":request.getParameter("pk_data");//傳入json
		String in_data = request.getParameter("in_data")==null?"":request.getParameter("in_data");//傳入json
		String fun = request.getParameter("fun")==null?"":request.getParameter("fun");
		String selNm = request.getParameter("selNm")==null?"":request.getParameter("selNm");
		String strRtn = "";
		JSONObject object_in_data =null;
		boolean blnupd=false;
		HashMap rtnMap=new HashMap();
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		Pabaspsn psnobj=null;
		Pabatpay batpayobj=null;
		Patrapay trapayobj=null;
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		jsonObj = new JSONObject();
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,hid);
			try{
				//查詢
				if (fun.equals("qryBatPay")){
					rtnMap=tDAO.getBatPayList(in_data,userid,hid,da);
					// 用一個root tag將JSONArray包裝起來
					jsonObj.element("BatPayList", rtnMap.get("batpay_json"));
				}
				//取得基本檔_ID/CARD
				else if (fun.equals("getBasByKey")){
					psnobj=tDAO.getBasPsnByKey(in_data,"1",hid,da);
					jsonObj.element("basPsnItem", psnobj,cfg);					
				}
				else if (fun.equals("getDupByKey")){
					//取得批次檔_發薪日+ID+支扣代碼
					batpayobj=tDAO.getBatpayByKey(in_data,hid,da);
					jsonObj.element("batPayItem", batpayobj,cfg);
					//取得異動支扣_發薪日+ID+支扣代碼+分類=N
					trapayobj=tDAO.getTrapayByKey(in_data,hid,da);
					jsonObj.element("traPayItem", trapayobj,cfg);
				}
				else if (fun.matches("updBatPay")){
					blnupd = tDAO.updPATblByKey(editMode,tblname,pk_data,tabJson,userid,hid,da);
					
					if (blnupd){	//update success,重新reload
						jsonObj.put("msg", "[寫入]"+selNm+"  成功");
					}else{
						jsonObj.put("msg", "[寫入]"+selNm+"  失敗");
					}
					log.info(userid+user.getUserNm()+" [寫入]"+selNm+" "+pk_data +" blnupd:"+blnupd);
				}
				else if (fun.matches("prodBat")){
					strRtn=tDAO.prodBat(in_data,tabJson,userid,hid,da);
					jsonObj.put("msg", strRtn);
				}
				else if (fun.matches("qryleave")){
					rtnMap=tDAO.getLeave(in_data,userid,hid,da);
					// 用一個root tag將JSONArray包裝起來
					jsonObj.element("PayLeaveList", rtnMap.get("leave_json"));
				}
				else if (fun.matches("qryteacheraw")){	//教職及論文獎勵 _查詢
					rtnMap=tDAO.getTeacheraw(in_data,userid,hid,da);
					// 用一個root tag將JSONArray包裝起來
					jsonObj.element("TeacherawList", rtnMap.get("teacheraw_json"));
				}
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 補充保險費處理
	 */
	@RequestMapping(params="method=handelNhi")
	public void handelNhi(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String me = getClass().getName() + ".handelNhi()";
		AmJsonConfig cfg = new AmJsonConfig();
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String tabJson = request.getParameter("tabJson");
		String tblname = request.getParameter("tblname");
		String editMode = request.getParameter("editMode");
		String pk_data = request.getParameter("pk_data")==null?"":request.getParameter("pk_data");//傳入json
		String in_data = request.getParameter("in_data")==null?"":request.getParameter("in_data");//傳入json
		String fun = request.getParameter("fun")==null?"":request.getParameter("fun");
		String selNm = request.getParameter("selNm")==null?"":request.getParameter("selNm");
		String strRtn = "";
		JSONObject object_in_data =null;
		Pabaspsn psnitem=null;
		int intjack=0;	//累積獎金
		String yy="";	//當年度
		boolean blnupd=false;
		HashMap nhiMap=new HashMap();
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		jsonObj = new JSONObject();
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,hid);
			try{
				if (fun.equals("NhiCal")){
					nhiMap=tDAO.getNhiCalList(in_data,userid,hid,da);
					// 用一個root tag將JSONArray包裝起來
					jsonObj.element("NHICalList", nhiMap.get("nhical_json"));
					jsonObj.element("PartPayAmt", nhiMap.get("PartPayAmt"));
					jsonObj.element("PartNHIAmt", nhiMap.get("PartNHIAmt"));
					jsonObj.element("GEINFPayAmt", nhiMap.get("GEINFPayAmt"));
					jsonObj.element("GEINFNHIAmt", nhiMap.get("GEINFNHIAmt"));
					jsonObj.element("LBINFPayAmt", nhiMap.get("LBINFPayAmt"));
					jsonObj.element("LBINFNHIAmt", nhiMap.get("LBINFNHIAmt"));
				}
				else if (fun.equals("NhiDet")){
					nhiMap=tDAO.getNhiDetList(in_data,userid,hid,da);
					// 用一個root tag將JSONArray包裝起來
					jsonObj.element("NHIDetList", nhiMap.get("nhidet_json"));
					jsonObj.element("PartPayDet", nhiMap.get("PartPayDet"));
					jsonObj.element("PartNHIDet", nhiMap.get("PartNHIDet"));
					jsonObj.element("GEINFPayDet", nhiMap.get("GEINFPayDet"));
					jsonObj.element("GEINFNHIDet", nhiMap.get("GEINFNHIDet"));
					jsonObj.element("LBINFPayDet", nhiMap.get("LBINFPayDet"));
					jsonObj.element("LBINFNHIDet", nhiMap.get("LBINFNHIDet"));
				}
				else if (fun.equals("NhiAccu")){
					nhiMap=tDAO.getNhiAccuList(in_data,userid,hid,da);
					// 用一個root tag將JSONArray包裝起來
					jsonObj.element("NHIAccuList", nhiMap.get("nhidet_json"));
				}
				else if (fun.matches("updNhiCal|updNhiDet")){
					blnupd = tDAO.updPATblByKey(editMode,tblname,pk_data,tabJson,userid,hid,da);
					
					if (blnupd){	//update success,重新reload
						jsonObj.put("msg", "[寫入]"+selNm+"  成功");
					}else{
						jsonObj.put("msg", "[寫入]"+selNm+"  失敗");
					}
					log.info(userid+user.getUserNm()+" [寫入]"+selNm+" "+pk_data +" blnupd:"+blnupd);
				}
				//取得基本檔,補充費該年累積獎金
				else if (fun.equals("getBasByKey")){
					psnitem=tDAO.getBasPsnByKey(in_data,"0",hid,da);
					jsonObj.element("basPsnItem", psnitem,cfg);
					yy=DateUtil.GetAppDate()[1].toString().substring(0,4);
					intjack=tDAO.getJackpotByKey(psnitem.getPayidno(),yy,userid,hid,da);
					jsonObj.element("intjack", intjack);
				}
				//[產生]計算/匯出_執行
				else if (fun.equals("prodNhi")){
					strRtn=tDAO.prodNhi(in_data,userid,hid,da);
					jsonObj.put("msg", strRtn);
				}					
	
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 應稅處理
	 */
	@RequestMapping(params="method=handelTax")
	public void handelTax(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String me = getClass().getName() + ".handelTax()";
		hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String in_data = request.getParameter("in_data")==null?"":request.getParameter("in_data");//傳入json
		
		String selNm = request.getParameter("selNm")==null?"":request.getParameter("selNm");
		String strRtn = "";
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		jsonObj = new JSONObject();
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,hid);
			try{
				strRtn=tDAO.handelTax(in_data,userid,hid,da);
				if ("".equals(strRtn)) {
					jsonObj.put("msg", selNm+"成功");
					jsonObj.put("status", "Y");
				}else{
					jsonObj.put("status", "N");
					jsonObj.put("msg", selNm+"更新失敗\n\r"+strRtn);
				}
					
			}catch(Exception e){	
				log.info(me+"失敗=="+e.toString());
				e.printStackTrace();
			}finally{
				tDAO=null;
				da.close();
				da = null;
			}
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
	/**
	 * 上傳檔案
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=uploadFile")
	public void uploadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String me = getClass().getName() + ".uploadFile()";
		String strRtn = "";
		String rtn = "N";
		String path = "";
		SmbFileOutputStream fout = null;
		Pataximp pataximp=null;
		Paprtdtl paprtdtl=null;
		Pabatpay pabatpay=null;
		ArrayList<Pataximp> alTaxImp = new ArrayList<Pataximp>();
		ArrayList<Paprtdtl> alPrtDtl = new ArrayList<Paprtdtl>();
		ArrayList<Pabatpay> alBatpay = new ArrayList<Pabatpay>();
		String tmp;
		int fieldslen=0;
		int inttmp = 0;
		try {
			response.setCharacterEncoding("utf-8");
 
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload (factory);
			upload.setFileSizeMax(1024*1024*10);
			upload.setHeaderEncoding("UTF-8");
			FileItemIterator iter =  upload.getItemIterator(request);
			BufferedReader br=null;
			hid = request.getParameter("hid")==null?"":request.getParameter("hid");
			userid = request.getParameter("userid")==null?"":request.getParameter("userid");
			String selEn = request.getParameter("selEn")==null?"":request.getParameter("selEn");
			String salnum = request.getParameter("salnum")==null?"0":request.getParameter("salnum");
			String patradt = request.getParameter("patradt")==null?"":request.getParameter("patradt");
			String paycode = request.getParameter("paycode")==null?"":request.getParameter("paycode");
			String paupbas = request.getParameter("paupbas")==null?"":request.getParameter("paupbas");
			String chkbas = request.getParameter("chkbas")==null?"":request.getParameter("chkbas");
			String strLine;
			tDAO = new SalNTDAO(da,hid);
			InputStream in = null;
			int do_cnt = 0;
			int intYear = 0;
			int intLine=0;
			jsonObj = new JSONObject();
			while (iter.hasNext()) {
				FileItemStream fileItem = iter.next();
				//多個檔案
				if(!fileItem.isFormField()){
					String filename = fileItem.getName();
					filename = filename.substring(filename.lastIndexOf("\\")==-1?0:filename.lastIndexOf("\\"));
					in = fileItem.openStream();
					br = new BufferedReader(new InputStreamReader(in,"Big5"));
					while( (strLine = br.readLine() ) != null) {
						//批次所得稅人工入稅
						if ("InBatCash".equals(selEn)){
							if (!"".equals(strLine)){
								//第1行是標題
								if (!strLine.startsWith("年")){
									pataximp=new Pataximp();
									String[] fields = strLine.split(",");
					    			//排除空白列
					    			if (fields.length>0){
					    				fieldslen=fields.length-1;
						    			log.info("uploadFile:cnt:"+do_cnt++ +" selEn: "+selEn+" strLine:"+strLine+" length:"+fieldslen);
						    			//年
						    			pataximp.setPayear(Integer.parseInt(fields[0]));
						    			//必要傳入參數
						    			if (intYear == 0)
						    				intYear = Integer.parseInt(fields[0]);
						    			//月
						    			pataximp.setPaymonth(Integer.parseInt(fields[1]));
						    			//卡號
						    			if ("".equals(StringUtil.nullToString(fields[2])))
						    				pataximp.setPaycard("");
						    			else
						    				pataximp.setPaycard(StringUtil.addZero(StringUtil.nullToString(fields[2]),4));
						    			//身分證號
						    			pataximp.setPayidno(StringUtil.nullToString(fields[3]));
						    			//轉帳項目
						    			pataximp.setCatgnm(StringUtil.nullToString(fields[4]));
						    			//轉帳項目代碼
						    			pataximp.setCatgcd(StringUtil.nullToString(fields[5]));
						    			//所得格式
						    			pataximp.setPayformat(StringUtil.nullToString(fields[6]));
						    			//傳票製票號
						    			pataximp.setPaycitation(StringUtil.nullToString(fields[7]));
						    			//應稅所得
						    			pataximp.setPaytaxpay(Integer.parseInt(fields[8]));
						    			//已扣所得稅
						    			if (fieldslen >= 9 && !"".equals(fields[9]))
							    			pataximp.setPaytaxdeduct(Integer.parseInt(fields[9]));
						    			//姓名
						    			if (fieldslen >= 10)
						    				pataximp.setPaynamec(StringUtil.trimWideEmpty(StringUtil.nullToString(fields[10])));
						    			//所得開始日期
						    			if ( fieldslen >= 11 && !"".equals(fields[11]) )
						    				pataximp.setPaybegin(StringUtil.nullToString(fields[11]));
						    			//所得結束日期
						    			if ( fieldslen >= 12 && !"".equals(fields[12]) )
						    				pataximp.setPayend(StringUtil.nullToString(fields[12]));
						    			//戶籍地址
						    			if (fieldslen >= 13)
						    				pataximp.setPayaddrs(StringUtil.trimWideEmpty(StringUtil.nullToString(fields[13])));
						    			
						    			alTaxImp.add(pataximp);
					    			}
								}	
							}
						}
						//銀行回報 Bank_PayC薪津 Bank_PayS公儲
						else if(selEn.matches("Bank_PayC|Bank_PayS")){
							if (!"".equals(strLine)){
								//第1行不處理
								if (StringUtil.trimWideEmpty(strLine).length()!=22){
									paprtdtl=new Paprtdtl();
									//ID
									tmp=strLine.substring(32,42);
									paprtdtl.setPayidno(tmp);
									//帳號
									tmp=strLine.substring(0,12);
									if (selEn.equals("Bank_PayC"))
										paprtdtl.setPayacno(tmp);
									else
										paprtdtl.setPaygedno(tmp);
									//金額
									inttmp=Integer.parseInt(strLine.substring(12,22));
									paprtdtl.setPayamt(inttmp);
									//回報狀態
									tmp=strLine.substring(44,45);
									paprtdtl.setBankflag(tmp);
									
						    		alPrtDtl.add(paprtdtl);
								}	
							}
						}
						//[匯入]批次檔 ImpPayInBat
						else if(selEn.matches("ImpPayInBat")){
							if (!"".equals(strLine)){
								//若有標題,不匯入
								if (!(strLine.contains("年") && intLine==0)){
									intLine++;
									pabatpay=new Pabatpay();
									//分隔號先判斷 , 再判斷 ;									
									String[] fields = strLine.split(",");
									if (fields.length == 1){
										fields = strLine.split(";");
									}
									if (fields.length>0 && fields[2].matches("556"))
										log.info(fields[2]);
									//排除空白列 
					    			if (fields.length>0 && !"".equals(StringUtil.nullToString(fields[0])) ) {
					    				fieldslen=fields.length-1;
						    			log.info("uploadFile:cnt:"+do_cnt++ +" selEn: "+selEn+" strLine:"+strLine+" length:"+fieldslen);
						    			//年
						    			pabatpay.setYy(StringUtil.nullToString(fields[0]));
						    			//月
						    			pabatpay.setMm(StringUtil.nullToString(fields[1]));
						    			//卡號
						    			pabatpay.setPaycard( StringUtil.addZero(StringUtil.nullToString(fields[2]),4) );
						    			//金額
						    			pabatpay.setPayamt(Integer.parseInt(StringUtil.nullToString(fields[3])));
						    			//Y更新至主檔 N每月歸零
						    			pabatpay.setPaupbas(paupbas);
						    			//發薪日
						    			pabatpay.setPatradt(java.sql.Date.valueOf( patradt));
						    			
						    			alBatpay.add(pabatpay);
					    			}
								}
								
							}
						}
					}
					
					
					//批次所得稅人工入稅
					if ("InBatCash".equals(selEn)){
						strRtn=tDAO.impTaxByBat(alTaxImp,intYear,filename,userid,hid,da);
						log.info("匯入批次所得稅人工入稅 "+strRtn);
						jsonObj.put("msg", "匯入批次所得稅人工入稅 "+strRtn);
					}
					//Bank_PayC:薪資比對,Bank_PayS:公儲比對
					else if (selEn.matches("Bank_PayC|Bank_PayS")){
						strRtn=tDAO.impByBankC(alPrtDtl,patradt,salnum,selEn,filename,userid,hid,da);						
						jsonObj.put("msg", strRtn);
					}
					//[匯入]批次檔 ImpPayInBat
					else if (selEn.matches("ImpPayInBat")){
						strRtn=tDAO.ImpPayInBat(alBatpay,patradt,paycode,chkbas,selEn,filename,userid,hid,da);						
						jsonObj.put("msg", strRtn);
					}
					
					/*path = filepath(hhisnum, odcmdate, odcmtype, hid) + filename;
					fout = new SmbFileOutputStream(FileToSmbFile(path));
					in = fileItem.openStream();
					IOUtils..copy(in, fout);
					fout.close();*/
					in.close();
				}
			}
		}catch(Throwable e){
			jsonObj.element("msg", e.toString());
			log.info(me + " fail, " + e);
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			/*//檔案大小超過限制
			if(errors.toString().indexOf("FileSizeLimitExceededException") != -1){
				if(fout != null)
					fout.close();
				if(in != null)
					in.close();
				rtn = "A";
				FileToSmbFile(path).delete();
			}*/
		}finally{
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print(jsonObj.toString());
			out.flush();
		}
	}
	/**
	 * 測試ajax
	 */
	@RequestMapping(params="method=testAjax")
	public void testAjax(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".testAjax()";
		
		try{
			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			
			PrintWriter out = response.getWriter();
			// 寫出至JSP頁面
			out.print("回傳結果");
			out.flush();
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
	}
}
