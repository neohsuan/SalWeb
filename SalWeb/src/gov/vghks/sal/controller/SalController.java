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
import gov.vghks.sal.dao.SalNTDAO;
import gov.vghks.sal.model.Pabaspsn;
import gov.vghks.sal.model.Padatpsn;
import gov.vghks.sal.model.Paprtdtl;
import gov.vghks.sal.model.Pataximp;
import gov.vghks.sal.model.Pataxpsn;
import gov.vghks.sal.model.Patrapsn;
import gov.vghks.sal.model.WPSUser;
import gov.vghks.sal.pdf.PayslipPDF;
import gov.vghks.sal.util.AmJsonConfig;
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
	SalNTDAO tDAO = null;
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
	 * 取得opsmaon.oprole手術室角色分派.單位清單 把代碼參考檔選項寫入session
	 *//*
	@RequestMapping(params="method=getOpRoleDept")
	public void getOpRoleDept(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getOpRoleDept()";
		VGHDataAccess da=new VGHDataAccess(Globals.dataSource_NT);
		String gAmian = (String) request.getSession().getAttribute("gAdmin");
		ArrayList alCost=null;
		//讀取pspm.pccosttbl,放至session,只需讀一次
		ArrayList alOpRoleDept= (ArrayList) request.getSession().getAttribute("alPCCosttbl");
		String flag=request.getParameter("flag");	//Y:重讀,N:不處理
		String selCode=request.getParameter("selCode");	//代碼參考檔選項
		SalNTDAO tDAO = new SalNTDAO(da);
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			request.getSession().setAttribute("selCode",selCode);
			tDAO = new SalNTDAO(da);
			try{
				if (alOpRoleDept==null||alOpRoleDept.size()==0||flag.equals("Y")){	
					alOpRoleDept=tDAO.getOpRoleDept(gAmian,"0",da);
					request.getSession().setAttribute("alOpRoleDept", alOpRoleDept);  
				}
				jsonObj.element("alOpRoleDept", alOpRoleDept);
				//
				alCost=tDAO.getOpRoleDept(gAmian,"1",da);
				jsonObj.element("alCost", alCost);
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
	
	*//**
	 * 依單位/卡號,取得oproole清單 手術室角色分派
	 *//*
	@RequestMapping(params="method=qryOpRoleDept")
	public void qryOpRoleDept(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".qryOpRoleDept()";
		VGHDataAccess da=new VGHDataAccess(Globals.dataSource_NT);
		String deptId = request.getParameter("deptId")==null?"":request.getParameter("deptId");
		String userId = request.getParameter("userId")==null?"":request.getParameter("userId");
		SalNTDAO tDAO = new SalNTDAO(da);
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			
			tDAO = new SalNTDAO(da);
			try{
				jsonArrRtn=tDAO.qryOpRoleDeptList(deptId,userId,da);
				// 用一個root tag將JSONArray包裝起來
				jsonObj.element("oproleData", jsonArrRtn);  
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
	*//**
	 * oprole 刪除
	 *//*
	@RequestMapping(params="method=delOpRoleDept")
	public void delOpRoleDept(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".delOpRoleDept()";
		jsonObj = new JSONObject();
		VGHDataAccess da=new VGHDataAccess(Globals.dataSource_NT);
		String userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		SalNTDAO tDAO = new SalNTDAO(da);
		boolean success=false;
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			
			tDAO = new SalNTDAO(da);
			try{
				success=tDAO.delOpRoleDept(userid,da);
			}catch(Exception e){
				jsonObj.element("msg", e.toString());
				log.info("刪除 oproole 失敗=="+e.toString());
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
	}*/
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
		String userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
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
	 */
	@RequestMapping(params="method=updPATblByKey")
	public void updPATblByKey(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String me = getClass().getName() + ".updPATBLByKey()";
		response.setCharacterEncoding("utf-8");//如果有中文，避免亂碼
		PrintWriter out = response.getWriter();
		
		boolean blnupdPATBLBy=false;
		String hid=request.getParameter("hid");
		String item=request.getParameter("item");
		String tblname=request.getParameter("tblname");
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
				blnupdPATBLBy =  tDAO.updPATblByKey(item,pk_data,tabJson,user.getLoginId(),hid,da);
				if (blnupdPATBLBy){	//update success,重新reload
					jsonObj.put("msg", "薪資基本檔 "+pk_data+" "+item+" 成功");
				}else{
					jsonObj.put("msg", "薪資基本檔 "+pk_data+" "+item+" 失敗");
				}
				
			}catch(Exception e){
				jsonObj.element("msg", e.toString());
				log.info(tblname+" "+item+"失敗==>"+e.toString());
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
	}
	/**
	 * 更新所得稅資料
	 */
	@RequestMapping(params="method=updTaxByKey")
	public void updTaxByKey(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String me = getClass().getName() + ".updPATBLByKey()";
		response.setCharacterEncoding("utf-8");//如果有中文，避免亂碼
		PrintWriter out = response.getWriter();
		
		boolean blnupd=false;
		String hid=request.getParameter("hid");
		String item=request.getParameter("item");
		String tblname=request.getParameter("tblname");
		String userid=request.getParameter("userid");
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
	 * 預先載入代碼 item:表單(payDetail發薪明細 psnBasic基本檔) tranPay)異動檔)
	 * payTax 所得稅
	 */
	@RequestMapping(params="method=getLoadCodeTbl")
	public void getLoadCodeTbl(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getLoadCodeTbl()";
		JsonConfig cfg = new JsonConfig();
		
		String flag = request.getParameter("flag")==null?"":request.getParameter("flag");//Y:重讀,N:不處理	
		String item = request.getParameter("item")==null?"":request.getParameter("item");	//表單
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		
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
		else if("tranPay".equals(item)){
			alpayitmTbl= (ArrayList) request.getSession().getAttribute("alpayitmTbl");
		}
		else if("payTax".equals(item)){
			alTaxYrTbl= (ArrayList) request.getSession().getAttribute("alTaxYrTbl");
			alTaxCatgTbl= (ArrayList) request.getSession().getAttribute("alTaxCatgTbl");
			alTaxTranYrTbl= (ArrayList) request.getSession().getAttribute("alTaxTranYrTbl");
		}
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,hid);
			try{
				if (alRankCodeTbl==null||alRankCodeTbl.size()==0 || flag.equals("Y") ){	
					alRankCodeTbl=tDAO.getLoadCodeTbl("PARANKCODE",hid,da);
					request.getSession().setAttribute("alRankCodeTbl", alRankCodeTbl);						
				}
				if (alTitleCodeTbl==null||alTitleCodeTbl.size()==0 || flag.equals("Y") ){	
					alTitleCodeTbl=tDAO.getLoadCodeTbl("PATITLE",hid,da);
					request.getSession().setAttribute("alTitleCodeTbl", alTitleCodeTbl);  
				}
				if (alBankDtTbl==null||alBankDtTbl.size()==0 || flag.equals("Y")){	
					alBankDtTbl=tDAO.getLoadCodeTbl("PABANK",hid,da);
					request.getSession().setAttribute("alBankDtTbl", alBankDtTbl);  
				}
				if (alBankDtTbl_2M==null||alBankDtTbl_2M.size()==0 || flag.equals("Y")){	
					alBankDtTbl_2M=tDAO.getLoadCodeTbl("PABANK_2M",hid,da);
					request.getSession().setAttribute("alBankDtTbl_2M", alBankDtTbl_2M);  
				}
				if (alDeptCodeTbl==null||alDeptCodeTbl.size()==0 || flag.equals("Y")){	
					alDeptCodeTbl=tDAO.getLoadCodeTbl("PADEPT",hid,da);
					request.getSession().setAttribute("alDeptCodeTbl", alDeptCodeTbl);  
				}
				if (alDpinTbl==null||alDpinTbl.size()==0 || flag.equals("Y")){	
					alDpinTbl=tDAO.getLoadCodeTbl("PADPINTBL",hid,da);
					request.getSession().setAttribute("alDpinTbl", alDpinTbl);  
				}
				if (item.matches("tranPay")){	
					if (alpayitmTbl==null||alpayitmTbl.size()==0 || flag.equals("Y")){	
						alpayitmTbl=tDAO.getLoadCodeTbl("PAPAYITM",hid,da);
						request.getSession().setAttribute("alpayitmTbl", alpayitmTbl);  
					}
				}
				if (item.matches("psnBasic|tranPay")){	
					if (alRnk4Tbl==null||alRnk4Tbl.size()==0){
						alRnk4Tbl=tDAO.getLoadCodeTbl("PARNK4TBL",hid,da);
						request.getSession().setAttribute("alRnk4Tbl", alRnk4Tbl);
					}
					if (alexpfTbl==null||alexpfTbl.size()==0){
						alexpfTbl=tDAO.getLoadCodeTbl("PAEXPFTBL",hid,da);
						request.getSession().setAttribute("alexpfTbl", alexpfTbl);
					}
					if (altracdTbl==null||altracdTbl.size()==0){
						altracdTbl=tDAO.getLoadCodeTbl("PATRACODE",hid,da);
						request.getSession().setAttribute("altracdTbl", altracdTbl);
					}
				}
				if (item.matches("payTax")){	
					if (alTaxYrTbl==null || alTaxYrTbl.size()==0 || flag.equals("Y")){	
						alTaxYrTbl=tDAO.getLoadCodeTbl("TAXPSNYR",hid,da);
						request.getSession().setAttribute("alpayitmTbl", alTaxYrTbl);  
					}
					if (alTaxCatgTbl==null || alTaxCatgTbl.size()==0 || flag.equals("Y")){	
						alTaxCatgTbl=tDAO.getLoadCodeTbl("TAXCATG",hid,da);
						request.getSession().setAttribute("alTaxCatgTbl", alTaxCatgTbl);  
					}
					if (alTaxTranYrTbl==null || alTaxTranYrTbl.size()==0 || flag.equals("Y")){	
						alTaxTranYrTbl=tDAO.getLoadCodeTbl("TAXTRANYR",hid,da);
						request.getSession().setAttribute("alTaxTranYrTbl", alTaxTranYrTbl);  
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
				if (item.matches("tranPay")){
					jsonObj.element("alpayitmTbl", alpayitmTbl);					
				}
				if (item.matches("payTax")){
					jsonObj.element("alTaxYrTbl", alTaxYrTbl);
					jsonObj.element("alTaxCatgTbl", alTaxCatgTbl);
					jsonObj.element("alTaxTranYrTbl", alTaxTranYrTbl);
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
	 * 依KEY,取得選單代碼檔維護 codeId(A
	 */
	@RequestMapping(params="method=qryCodeList")
	public void qryCodeList(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".qryCodeList()";
		response.setCharacterEncoding("utf-8");
		String tblname = request.getParameter("tblname")==null?"":request.getParameter("tblname");
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{
			tDAO = new SalNTDAO(da,hid);
			try{
				jsonArrRtn=tDAO.qryCodeList(tblname,code_data,hid,da);
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
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
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
				TaxMap=tDAO.getTaxPayListbyKey(code_data,hid,da);
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
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
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
				jsonArrRtn=tDAO.getTaxTaskList(code_data,hid,da);
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
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
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
				jsonArrRtn=tDAO.getTaxViewList(code_data,hid,da);
				// 用一個root tag將JSONArray包裝起來
				jsonObj.element("taxViewList", jsonArrRtn);  
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
	@RequestMapping(params="method=getTaxSumList")
	public void getTaxSumList(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getTaxSumList()";		
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
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
				jsonArrRtn=tDAO.getTaxSumList(code_data,hid,da);
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
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
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
				jsonArrRtn=tDAO.getTaxStatList(code_data,hid,da);
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
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
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
				jsonArrRtn=tDAO.getDatPsnList(code_data,hid,da);
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
		VGHDataAccess da=new VGHDataAccess(Globals.dataSource_NT);
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
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
				jsonArrRtn=tDAO.getBasPsnList(code_data,item,hid,da);
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
	 * 依KEY,取得基本個人明細LIST item:1個人明細,2篩選
	 */
	@RequestMapping(params="method=getTranPsnList")
	public void getTranPsnList(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getTranPsnList()";
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		VGHDataAccess da=new VGHDataAccess(Globals.dataSource_NT);

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
				jsonArrRtn=tDAO.getTranPsnList(code_data,item,hid,da);
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
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
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
				jsonArrRtn=tDAO.getSalnumList(in_data,hid,da);
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
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
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
				psnitem=tDAO.getDatPsnBykey(code_data,hid,da);
				jsonObj.element("datPsnItem", psnitem,cfg);
				//個人支扣
				datPayMap=tDAO.getDatPayList(code_data,psnitem,hid,da);
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
	 */
	@RequestMapping(params="method=getNamebyID")
	public void getNamebyID(HttpServletRequest request, HttpServletResponse response){
		response.setCharacterEncoding("utf-8");//如果有中文，避免亂碼
		String me = getClass().getName() + ".getNamebyID()";
		
		String cardno = request.getParameter("cardno")==null?"":request.getParameter("cardno");		
		
		try{
			tDAO = new SalNTDAO();
			try{
				jsonObj = tDAO.queryNamebyID(cardno);
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
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
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
				psnitem=tDAO.getBasPsnBykey(code_data,da);
				jsonObj.element("basPsnItem", psnitem,cfg);
				//個人支扣
				basPayMap=tDAO.getBasPayList(code_data,hid,da);
				jsonObj.element("basPayList", basPayMap.get("baspay_json"));
				//歷史資料
				histListMap=tDAO.getHistList(code_data,hid,da);
				jsonObj.element("histList", histListMap.get("pahist_json"),cfg);
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
	 * 依KEY,取得異動個人明細
	 */
	@RequestMapping(params="method=getTraPsnByKey")
	public void getTraPsnByKey(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getTraPsnByKey()";
		//如果有中文，避免亂碼
		response.setCharacterEncoding("utf-8");
		
		AmJsonConfig cfg = new AmJsonConfig();
		Patrapsn psnitem=null;
		HashMap traPayMap=new HashMap();
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{
			tDAO = new SalNTDAO(da,hid);
			try{
				jsonObj = new JSONObject();
				//異動個人明細
				psnitem=tDAO.getTraPsnBykey(code_data,da);
				jsonObj.element("traPsnItem", psnitem,cfg);
				//異動個人支扣
				traPayMap=tDAO.getTraPayList(code_data,da);
				jsonObj.element("otraPayList", traPayMap.get("otrapay_json"));
				jsonObj.element("ntraPayList", traPayMap.get("ntrapay_json"));
				
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
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		
		String dataSourceNT="";
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		try{
			tDAO = new SalNTDAO(da,hid);
			try{
				jsonObj = new JSONObject();
				//支扣sum
				jsonArrRtn=tDAO.getCheckPayBykey(code_data,hid,da);
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
	 * 依salnum+paycode找pa.padaypay清單
	 */
	@RequestMapping(params="method=getDayPayByCode")
	public void getDayPayByCode(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getDayPayByCode()";
		//如果有中文，避免亂碼
		response.setCharacterEncoding("utf-8");
		
		AmJsonConfig cfg = new AmJsonConfig();
		
		jsonArrRtn=new JSONArray();		
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
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
				jsonArrRtn=tDAO.getDayPayByCode(code_data,hid,da);
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
		String hid = request.getParameter("hid");
		
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
			String userid = request.getParameter("userid");
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
	 * [產生/查詢]報表檔
	 */
	@RequestMapping(params="method=handelPrtSal")
	public void btn_prodPrtSal(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String me = getClass().getName() + ".handelPrtSal()";
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		String userid=request.getParameter("userid");
		String in_data = request.getParameter("in_data")==null?"":request.getParameter("in_data");//傳入json
		String selNm = request.getParameter("selNm")==null?"":request.getParameter("selNm");
		String salnum = request.getParameter("salnum")==null?"":request.getParameter("salnum");
		String fun = request.getParameter("fun")==null?"":request.getParameter("fun");
		String strRtn = "";
		String dataSourceNT="";
		ArrayList<Paprtdtl> paprtdtlAry=null;
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		dataSourceNT=hospInfo.dataSourceNT;
		da=new VGHDataAccess(dataSourceNT);
		
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		jsonObj = new JSONObject();
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,hid);
			try{
				//[產生]報表檔
				if (fun.equals("handelPrt") ){
					strRtn=tDAO.prodPrtSal(in_data,userid,hid,da);
					if ("".equals(strRtn)) {
						jsonObj.put("msg", selNm+"成功");
						jsonObj.put("status", "Y");
					}else{
						jsonObj.put("status", "N");
						jsonObj.put("msg", selNm+"更新失敗\n\r"+strRtn);
					}
				}	
				//[查詢]報表檔
				else if (fun.equals("inqPrt") ){
					jsonArrRtn=tDAO.getRrtDtlList(in_data,hid,da);
					// 用一個root tag將JSONArray包裝起來
					jsonObj.element("prtDtlList", jsonArrRtn);  
				}
				//報表檔[PDF]
				else if (fun.equals("printPrt") ){
					paprtdtlAry=tDAO.getPrtdtlBykey(in_data,hid,da);
					if(paprtdtlAry!=null){
						System.out.println(paprtdtlAry.size());
						new PayslipPDF(paprtdtlAry,hid,salnum);
					}	
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
	 * 發薪明細處理
	 */
	@RequestMapping(params="method=handelPayroll")
	public void handelPayroll(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String me = getClass().getName() + ".handelPayroll()";
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		String userid=request.getParameter("userid");
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
	 * 應稅處理
	 */
	@RequestMapping(params="method=handelTax")
	public void handelTax(HttpServletRequest request, HttpServletResponse response)throws IOException{
		String me = getClass().getName() + ".handelTax()";
		String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
		String userid=request.getParameter("userid");
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
		ArrayList<Pataximp> alTaxImp = new ArrayList<Pataximp>();
		String tmp;
		int fieldslen=0;
		try {
			response.setCharacterEncoding("utf-8");
 
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload (factory);
			upload.setFileSizeMax(1024*1024*10);
			upload.setHeaderEncoding("UTF-8");
			FileItemIterator iter =  upload.getItemIterator(request);
			BufferedReader br=null;
			String hid = request.getParameter("hid")==null?"":request.getParameter("hid");
			String userid=request.getParameter("userid");
			String selNm = request.getParameter("selNm")==null?"":request.getParameter("selNm");
			String strLine;
			tDAO = new SalNTDAO(da,hid);
			InputStream in = null;
			int do_cnt = 0;
			int intYear = 0;
			jsonObj = new JSONObject();
			while (iter.hasNext()) {
				FileItemStream fileItem = iter.next();
				//多個檔案
				if(!fileItem.isFormField()){
					String filename = fileItem.getName();
					filename = filename.substring(filename.lastIndexOf("\\")==-1?0:filename.lastIndexOf("\\"));
					in = fileItem.openStream();
					br = new BufferedReader(new InputStreamReader(in));
					while( (strLine = br.readLine() ) != null) {
						if ("InBatCash".equals(selNm)){
							if (!"".equals(strLine)){
								//第1行是標題
								if (!strLine.startsWith("年")){
									pataximp=new Pataximp();
									String[] fields = strLine.split(",");
					    			//排除空白列
					    			if (fields.length>0){
					    				fieldslen=fields.length-1;
						    			log.info("uploadFile:cnt:"+do_cnt++ +" selNm: "+selNm+" strLine:"+strLine+" length:"+fieldslen);
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
					}
					
					//批次所得稅人工入稅
					if ("InBatCash".equals(selNm)){
						strRtn=tDAO.impTaxByBat(alTaxImp,intYear,filename,userid,hid,da);
					}
					//if ("".equals(strRtn)) {
						log.info("匯入批次所得稅人工入稅 "+strRtn);
						jsonObj.put("msg", "匯入批次所得稅人工入稅 "+strRtn);
					//
					
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
