package gov.vghks.sal.util;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import gov.vghks.common.util.StringUtil;

import gov.vghks.sal.Globals;
import gov.vghks.sal.model.WPSUser;
import gov.vghks.sal.util.ldap.LDAPUtil;

/**
 * Portal Server 整合登入功能
 */
public class WPSAutoLogon extends HttpServlet {
	
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 呼叫log4j */
	static Logger log = Logger.getLogger(WPSAutoLogon.class);

	/**
	 * 建構子
	 */
	public WPSAutoLogon() {
		super();
	}

	/**
	 * doGet與doPost整合再一起
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * doPost
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		/** 目標網址 */
		String targetURL = request.getParameter("targetURL");
		/** 使用者id */
		String strUsrId = StringUtil.nullToString(request.getParameter("uid"));
		
		String ssID = null;
		String ssIDFromSession = null;
		boolean flag = true;
		//從Request物件中取得sessionID
		ssID = securityCheck(request,strUsrId);
		if (ssID == null) {
			//解密失敗,表示非正常程序存取此程式
			PrintWriter out = response.getWriter();
			out.println("Access Denied");
		} else {
			//避免使用者多次點擊造成session id不一致的情況進行處理
			if (request.getSession() != null){
				if (session.getAttribute("ssID") != null) {
					ssIDFromSession = (String) session.getAttribute("ssID");
					if (ssIDFromSession.equals(ssID)){
						flag = false;
					}
				} else {
					request.getSession().setAttribute("ssID", ssID);					
				}
			}

			//從LDAP取得使用者姓名與成本中心
			if (flag) {
				String cosId;
				String dept;
				String userName;
				String user_psWkTypd="";//職稱代碼
				String strOUName="";	//成本中心中文
				String userHID="";			//醫院代碼
				
				try {		
				    String hostname =request.getServerName();				    
				    String programName=this.getClass().getName();
					LDAPUtil ldapUtilObj = LDAPUtil.getInstance() ;
					//strUsrId="P008";
					// 成本中心英文
					dept = ldapUtilObj.getDeptNo(strUsrId, hostname, programName);
					if(dept.length()>=3  && dept.substring(0,3).equals("NUR")) 
						dept = dept.substring(3,dept.length());
					cosId = ldapUtilObj.getCostId(strUsrId);
					userName = ldapUtilObj.getUserNm(strUsrId);
					
					strOUName = ldapUtilObj.getDeptName(strUsrId,hostname,programName);
					//2A0林介閩7074 T785
					if (strUsrId.equals("7074")) {
						userHID ="2A0";
					}else {	
						userHID = ldapUtilObj.getLdapAttr(strUsrId,"HID",hostname,programName);
					}
					
					user_psWkTypd=ldapUtilObj.getLdapAttr(strUsrId,"payWkTyd",hostname,programName);
					
					WPSUser user = new WPSUser();
					user.setLoginId(strUsrId);
					user.setUserNm(userName);
					user.setDeptNo(dept);
					user.setCosId(cosId);
					user.setHid(userHID);
					user.setDeptName(strOUName);
					user.setPayWkTyd(user_psWkTypd);
					
					//查詢是否為薪資管理者權限
					boolean pa_admin = ldapUtilObj.checkUserinGroup(strUsrId, Globals.SESSION_SYS_ADMIN);
					System.out.println("管理者" + ":" + pa_admin);
					String strAdmin = pa_admin ? "Y" : "N";
					user.setPa_adm(strAdmin);
					//查詢是否為薪資系統_維護角色
					boolean pa_edit = ldapUtilObj.checkUserinGroup(strUsrId, Globals.SESSION_SYS_EDIT);
					System.out.println("維護角色" + ":" + pa_edit);
					String strEdit = pa_edit ? "Y" : "N";
					user.setPa_edit(strEdit);
					//查詢是否為薪資系統_查詢角色
					boolean pa_view = ldapUtilObj.checkUserinGroup(strUsrId, Globals.SESSION_SYS_VIEW);
					System.out.println("查詢角色" + ":" + pa_view);
					String strView = pa_view ? "Y" : "N";
					user.setPa_view(strView);
					//查詢是否為薪資系統_員工獎金角色
					boolean pa_bones = ldapUtilObj.checkUserinGroup(strUsrId, Globals.SESSION_SYS_BONES);
					System.out.println("員工獎金角色" + ":" + pa_bones);
					String strBones = pa_bones ? "Y" : "N";
					user.setPa_bones(strBones);
					//查詢是否為薪資系統_所得稅角色
					boolean pa_tax = ldapUtilObj.checkUserinGroup(strUsrId, Globals.SESSION_SYS_TAX);
					System.out.println("所得稅角色" + ":" + pa_tax);
					String strTax = pa_tax ? "Y" : "N";
					user.setPa_tax(strTax);
					
					
					//取得使用者群組，自行判斷要不要使用
//					Collection<String> collections = ldapUtilObj.findGroupsRDNByUser(uid);
//					Iterator<String> iterator = collections.iterator();
//					
//					String groups = "";
//					while (iterator.hasNext()){
//						groups += "," + iterator.next();
//					}
//					groups = groups.equals("")?"":groups.substring(1);
//					user.setGroups(groups);	
					
					request.getSession().setAttribute("user",user);
					
					if(userName == null || userName.equals("")){
						log.error("查無使用者名稱:"+strUsrId);
					}
					
				}catch(Exception ee){
					ee.printStackTrace();
				}	
			}

			if (targetURL != null && (!"".equalsIgnoreCase(targetURL))) {
				targetURL = targetURL.replace("|", "&");
				response.sendRedirect(targetURL);
			}
		}

	}

	/**
	 * 透過integrationPortlet取得ssID並且塞在session之中，以通過驗證
	 * @param request
	 * @param uid
	 * @return
	 */
	private String securityCheck(HttpServletRequest request,String uid) {
		String ssID = null;
		String keyOne = request.getParameter("keyOne");
		String keyTwo = request.getParameter("keyTwo");
		String keyThree = request.getParameter("keyThree");
		String securedSessionID = request.getParameter("ssID");
		
		if (keyOne != null && keyTwo != null && keyThree != null
				&& securedSessionID != null && uid!=null) {
			try {
				// 為了串接至NIS系統，增加以下程式(寫至session)，於Tab_A.jsp會存取此session變數 ，傳至NIS系統
				request.getSession().setAttribute("keyOne", keyOne);
				request.getSession().setAttribute("keyTwo", keyTwo);
				request.getSession().setAttribute("keyThree", keyThree);
				request.getSession().setAttribute("ssID", securedSessionID);
				request.getSession().setAttribute("uid", uid);
				// *******************************************************
				int[] rsaKey = { Integer.parseInt(keyOne),
						Integer.parseInt(keyTwo), Integer.parseInt(keyThree) };
				ClsRsa cls = new ClsRsa();
				cls.setRsaKey(rsaKey);
				ssID = cls.DecodePass(securedSessionID);
				if(ssID!=null && ssID.indexOf(uid)>=0)
				{
					//do nothing, 表示
					System.out.println("securityCheck OK,uid="+uid);
				}
				else
				{
					//uid & sessionID的前四碼不同
					System.out.println("securityCheck Fail,uid="+uid);
					return null;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out
						.println("securityCheck Error,Session ID Decrypt Fail");
			} catch (Exception e) {
				e.printStackTrace();
				System.out
						.println("securityCheck Error,Session ID Decrypt Fail");
			}
		} else {
			// do nothing, 直接回傳null
			System.out
					.println("securityCheck Error,,invalid parameter,keyOne="
							+ keyOne
							+ ",keyTwo="
							+ keyTwo
							+ ",keyThree="
							+ keyThree);
		}
		return ssID;
	}
}
