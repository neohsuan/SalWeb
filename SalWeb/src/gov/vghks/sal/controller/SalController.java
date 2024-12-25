package gov.vghks.sal.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gov.vghks.common.util.JsonConfig;
import gov.vghks.common.util.StringUtil;
import gov.vghks.common.util.VGHDataAccess;
import gov.vghks.sal.Globals;
import gov.vghks.sal.dao.SalNTDAO;
import gov.vghks.sal.model.Code_id;
import gov.vghks.sal.model.Oprole;
import gov.vghks.sal.model.Pabaspsn;
import gov.vghks.sal.model.Padatpsn;
import gov.vghks.sal.model.Patrapsn;
import gov.vghks.sal.model.WPSUser;
import gov.vghks.sal.util.AmJsonConfig;
import gov.vghks.sal.util.SendMail;
import gov.vghks.sal.util.ldap.LDAPUtil;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 */
@Controller
@RequestMapping(value = "/salController")
public class SalController{
	
	/** 呼叫log4j */
	public static final Logger log 				= 	Logger.getLogger(SalController.class);
	/** 範例主頁面 */
	public static final String main				=	"sal/main";
	
	JSONArray jsonArrRtn = null;
	JSONObject jsonObj = new JSONObject();
	/**
	 * 範例主頁面
	 */
	@RequestMapping(params="method=main")
	public String main(HttpServletRequest request){
		String me = getClass().getName() + ".main()";
		String gAdmin="N";	//設定系統管理者角色
		try{
			
			//透過WPSAutoLogin取得登入者資訊，已經存在session，可以自行判斷是否需要帶入頁面
			WPSUser user = (WPSUser)request.getSession().getAttribute("user");
			request.setAttribute("user", user);
			if (user.getLoginId().equals("3007")) gAdmin="Y";
			request.getSession().setAttribute("gAdmin",gAdmin);
			
		}catch (Exception e) {
			log.info(me + " fail, " + e);
			e.printStackTrace();
		}
		
		return main;
	}
	/**
	 * 取得opsmaon.oprole手術室角色分派.單位清單 把代碼參考檔選項寫入session
	 */
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
	
	/**
	 * 依單位/卡號,取得oproole清單 手術室角色分派
	 */
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
	/**
	 * oprole 刪除
	 */
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
		
		VGHDataAccess da=new VGHDataAccess(Globals.dataSource_NT);
		//透過WPSAutoLogin取得登入者資訊，已經存在session，可以自行判斷是否需要帶入頁面
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		String userid = request.getParameter("userid")==null?"":request.getParameter("userid");
		SalNTDAO tDAO = new SalNTDAO(da);
		boolean success=false;
		try{			
			String code_data = request.getParameter("code_data");//傳入json
			String tblname = request.getParameter("tblname");
			
			tDAO = new SalNTDAO(da);
			success=tDAO.delTblCode(code_data,tblname,userid,user.getHid(),da);
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
		VGHDataAccess da=new VGHDataAccess(Globals.dataSource_NT);
		String item=request.getParameter("item");
		String tblname=request.getParameter("tblname");
		String pk_data = request.getParameter("pk_data")==null?"":request.getParameter("pk_data");//傳入json
		//透過WPSAutoLogin取得登入者資訊，已經存在session，可以自行判斷是否需要帶入頁面
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		if (user!=null){
			SalNTDAO tDAO = new SalNTDAO(da,user.getHid());
			try{
	    		String tabJson = request.getParameter("tabJson");
				String hidno=request.getParameter("hidno");
				blnupdPATBLBy =  tDAO.updPATblByKey(item,pk_data,tabJson,user.getLoginId(),user.getHid(),da);
				if (blnupdPATBLBy){	//update success,重新reload
					jsonObj.put("msg", "薪資基本檔 "+pk_data+" "+item+" 成功");
				}else{
					jsonObj.put("msg", "薪資基本檔 "+pk_data+" "+item+" 失敗");
				}
				
			}catch(Exception e){
				jsonObj.element("msg", e.toString());
				log.info(tblname+" "+item+"失敗==>"+e.toString());
				SendMail.sendMail("[PSM]"+user.getLoginId()+user.getLoginId()+" 更新 "+ pk_data +" 《薪資基本檔》失敗!",e.getMessage());
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
	 */
	@RequestMapping(params="method=getLoadCodeTbl")
	public void getLoadCodeTbl(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getLoadCodeTbl()";
		JsonConfig cfg = new JsonConfig();
		VGHDataAccess da=new VGHDataAccess(Globals.dataSource_NT);

//		String flag = request.getParameter("flag")==null?"":request.getParameter("flag");//Y:重讀,N:不處理	
		String item = request.getParameter("item")==null?"":request.getParameter("item");	//表單
		
		SalNTDAO tDAO = new SalNTDAO();
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		ArrayList alRankCodeTbl= null;
		ArrayList alTitleCodeTbl= null;
		ArrayList alBankDtTbl= null;
		ArrayList alDeptCodeTbl= null;
		ArrayList alRnk4Tbl= null;
		ArrayList alexpfTbl= null;
		ArrayList altracdTbl= null;
		ArrayList alpayitmTbl=null;//支扣代碼
		
		//讀取pspm.pccosttbl,放至session,只需讀一次
		alRankCodeTbl= (ArrayList) request.getSession().getAttribute("alRankCodeTbl");
		alTitleCodeTbl= (ArrayList) request.getSession().getAttribute("alTitleCodeTbl");
		alBankDtTbl= (ArrayList) request.getSession().getAttribute("alBankDtTbl");
		alDeptCodeTbl= (ArrayList) request.getSession().getAttribute("alDeptCodeTbl");
		if ("psnBasic".equals(item)){
			alRnk4Tbl= (ArrayList) request.getSession().getAttribute("alRnk4Tbl");
			alexpfTbl= (ArrayList) request.getSession().getAttribute("alexpfTbl");
			altracdTbl= (ArrayList) request.getSession().getAttribute("altracdTbl");
		}else if("tranPay".equals(item)){
			alpayitmTbl= (ArrayList) request.getSession().getAttribute("alpayitmTbl");
		}
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da);
			try{
				if (alRankCodeTbl==null||alRankCodeTbl.size()==0){	
					alRankCodeTbl=tDAO.getLoadCodeTbl("PARANKCODE",user.getHid(),da);
					request.getSession().setAttribute("alRankCodeTbl", alRankCodeTbl);						
				}
				if (alTitleCodeTbl==null||alTitleCodeTbl.size()==0){	
					alTitleCodeTbl=tDAO.getLoadCodeTbl("PATITLE",user.getHid(),da);
					request.getSession().setAttribute("alTitleCodeTbl", alTitleCodeTbl);  
				}
				if (alBankDtTbl==null||alBankDtTbl.size()==0){	
					alBankDtTbl=tDAO.getLoadCodeTbl("PABANK",user.getHid(),da);
					request.getSession().setAttribute("alBankDtTbl", alBankDtTbl);  
				}
				if (alDeptCodeTbl==null||alDeptCodeTbl.size()==0){	
					alDeptCodeTbl=tDAO.getLoadCodeTbl("PADEPT",user.getHid(),da);
					request.getSession().setAttribute("alDeptCodeTbl", alDeptCodeTbl);  
				}
				if (item.matches("tranPay")){	
					if (alpayitmTbl==null||alpayitmTbl.size()==0){	
						alpayitmTbl=tDAO.getLoadCodeTbl("PAPAYITM",user.getHid(),da);
						request.getSession().setAttribute("alpayitmTbl", alpayitmTbl);  
					}
				}
				if (item.matches("psnBasic|tranPay")){	
					if (alRnk4Tbl==null||alRnk4Tbl.size()==0){
						alRnk4Tbl=tDAO.getLoadCodeTbl("PARNK4TBL",user.getHid(),da);
						request.getSession().setAttribute("alRnk4Tbl", alRnk4Tbl);
					}
					if (alexpfTbl==null||alexpfTbl.size()==0){
						alexpfTbl=tDAO.getLoadCodeTbl("PAEXPFTBL",user.getHid(),da);
						request.getSession().setAttribute("alexpfTbl", alexpfTbl);
					}
					if (altracdTbl==null||altracdTbl.size()==0){
						altracdTbl=tDAO.getLoadCodeTbl("PATRACODE",user.getHid(),da);
						request.getSession().setAttribute("altracdTbl", altracdTbl);
					}
				}
					
				jsonObj.element("alRankCodeTbl", alRankCodeTbl);
				jsonObj.element("alTitleCodeTbl", alTitleCodeTbl);
				jsonObj.element("alBankDtTbl", alBankDtTbl,cfg);
				jsonObj.element("alDeptCodeTbl", alDeptCodeTbl);
				if (item.matches("psnBasic|tranPay")){
					jsonObj.element("alRnk4Tbl", alRnk4Tbl);
					jsonObj.element("alexpfTbl", alexpfTbl);
					jsonObj.element("altracdTbl", altracdTbl);
				}
				if (item.matches("tranPay")){
					jsonObj.element("alpayitmTbl", alpayitmTbl);
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
		VGHDataAccess da=new VGHDataAccess(Globals.dataSource_NT);

		String tblname = request.getParameter("tblname")==null?"":request.getParameter("tblname");
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		
		SalNTDAO tDAO = new SalNTDAO();
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da);
			try{
				jsonArrRtn=tDAO.qryCodeList(tblname,code_data,user.getHid(),da);
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
	 * 依KEY,取得月支個人明細LIST
	 */
	@RequestMapping(params="method=getDatPsnList")
	public void getDatPsnList(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getDatPsnList()";
		VGHDataAccess da=new VGHDataAccess(Globals.dataSource_NT);

		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		
		SalNTDAO tDAO = new SalNTDAO();
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da);
			try{
				jsonArrRtn=tDAO.getDatPsnList(code_data,user.getHid(),da);
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

		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		String item = request.getParameter("item")==null?"":request.getParameter("item");
		
		SalNTDAO tDAO = new SalNTDAO();
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da);
			try{
				jsonArrRtn=tDAO.getBasPsnList(code_data,item,user.getHid(),da);
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
		VGHDataAccess da=new VGHDataAccess(Globals.dataSource_NT);

		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		String item = request.getParameter("item")==null?"":request.getParameter("item");
		
		SalNTDAO tDAO = new SalNTDAO();
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da);
			try{
				jsonArrRtn=tDAO.getTranPsnList(code_data,item,user.getHid(),da);
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
	 * 依KEY,取得月支個人明細
	 */
	@RequestMapping(params="method=getDayPsnByKey")
	public void getDayPsnByKey(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getDayPsnByKey()";
		VGHDataAccess da=new VGHDataAccess(Globals.dataSource_NT);
		AmJsonConfig cfg = new AmJsonConfig();
		Padatpsn psnitem=null;
		HashMap datPayMap=new HashMap();
		HashMap datPaySumMap=new HashMap();
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		
		SalNTDAO tDAO = new SalNTDAO();
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da);
			try{
				jsonObj = new JSONObject();
				//個人明細
				psnitem=tDAO.getDatPsnBykey(code_data,user.getHid(),da);
				jsonObj.element("datPsnItem", psnitem,cfg);
				//個人支扣
				datPayMap=tDAO.getDatPayList(code_data,psnitem,da);
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
	 * 依KEY,取得個人明細
	 */
	@RequestMapping(params="method=getBasPsnByKey")
	public void getBasPsnByKey(HttpServletRequest request, HttpServletResponse response){
		String me = getClass().getName() + ".getBasPsnByKey()";
		VGHDataAccess da=new VGHDataAccess(Globals.dataSource_NT);
		AmJsonConfig cfg = new AmJsonConfig();
		Pabaspsn psnitem=null;
		HashMap basPayMap=new HashMap();
		HashMap histListMap=new HashMap();
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		
		SalNTDAO tDAO = new SalNTDAO();
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,user.getHid());
			try{
				jsonObj = new JSONObject();
				//個人明細
				psnitem=tDAO.getBasPsnBykey(code_data,da);
				jsonObj.element("basPsnItem", psnitem,cfg);
				//個人支扣
				basPayMap=tDAO.getBasPayList(code_data,user.getHid(),da);
				jsonObj.element("basPayList", basPayMap.get("baspay_json"));
				//歷史資料
				histListMap=tDAO.getHistList(code_data,user.getHid(),da);
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
		VGHDataAccess da=new VGHDataAccess(Globals.dataSource_NT);
		AmJsonConfig cfg = new AmJsonConfig();
		Patrapsn psnitem=null;
		HashMap traPayMap=new HashMap();
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		
		SalNTDAO tDAO = new SalNTDAO();
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da,user.getHid());
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
		VGHDataAccess da=new VGHDataAccess(Globals.dataSource_NT);
		AmJsonConfig cfg = new AmJsonConfig();
		
		jsonArrRtn=new JSONArray();		
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		
		SalNTDAO tDAO = new SalNTDAO();
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da);
			try{
				jsonObj = new JSONObject();
				//支扣sum
				jsonArrRtn=tDAO.getCheckPayBykey(code_data,user.getHid(),da);
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
		VGHDataAccess da=new VGHDataAccess(Globals.dataSource_NT);
		AmJsonConfig cfg = new AmJsonConfig();
		
		jsonArrRtn=new JSONArray();		
		String code_data = request.getParameter("code_data")==null?"":request.getParameter("code_data");//傳入json
		
		SalNTDAO tDAO = new SalNTDAO();
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		
		try{			//如果有中文，避免亂碼
			response.setCharacterEncoding("utf-8");
			tDAO = new SalNTDAO(da);
			try{
				jsonObj = new JSONObject();
				//支扣清單
				jsonArrRtn=tDAO.getDayPayByCode(code_data,user.getHid(),da);
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
		VGHDataAccess da=new VGHDataAccess(Globals.dataSource_NT);
		boolean blnupdTblCode=false;
		//透過WPSAutoLogin取得登入者資訊，已經存在session，可以自行判斷是否需要帶入頁面
		WPSUser user = (WPSUser)request.getSession().getAttribute("user");
		HashMap<String, String> OneMap=new HashMap<String, String>();
		SalNTDAO tDAO = new SalNTDAO(da);
		boolean success=false;
		try{			
			String item=request.getParameter("item");
			String tabJson = request.getParameter("tabJson");
			tDAO = new SalNTDAO(da);
			blnupdTblCode = tDAO.updSalCodeTbl(item,tabJson,user.getLoginId(),user.getHid(),da);
			if (blnupdTblCode){	//update success,重新reload
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
