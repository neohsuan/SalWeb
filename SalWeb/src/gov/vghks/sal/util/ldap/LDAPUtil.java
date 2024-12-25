package gov.vghks.sal.util.ldap;
import gov.vghks.sal.model.WPSUser;
import gov.vghks.sal.util.ResourceReader;
import gov.vghks.sal.util.ServiceLocator;
import gov.vghks.sal.util.ServiceLocatorException;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.sql.DataSource;

//import com.ibm.websphere.security.CustomRegistryException;
//import com.ibm.websphere.security.EntryNotFoundException;
//import com.ibm.websphere.security.PasswordCheckFailedException;


/**
 * LDAP 處理類別 整理 偉康科技提供元件(WebComm Mark Su)& Websphere security
 * 
 * <p>Version 1.2 20100609 由 WebComm Mark Su 增修</p>
 * <p>依據 20100601 會議討論需求，</p>
 * <nl>
 * <li>新增一個 private log method，用來紀錄各應用程式使用本處理類別的過程。</li>
 * <li>將應用程式 Binding(登入) LDAP 的使用者與帳號設定獨立於ApplicationResources .properties檔案。</li>
 * </nl>
 * <p>新增的 private log method 描述如下：</p>
 * <p>此 method 會使用 JNDI 名稱為 jdbc/vghks 的 datasource，因此不適用於非 Application Server 環境，並且此 datasource 必須在 web.xml 或是 ejb-jar.xml 中以參照的方式預先宣告好方可使用。</p>
 * <p>此 method 會針對 wps 這個 schema 底下的 ldaplog 這個 table 進行新增動作，因此需確認上述 datasource 的登入帳號是否有 wps 這個 schema 的新增權限。</p>
 * <p>每一個既有的 public method 都另外 override 一個增加 hostname 與 programName 兩個參數的 public method。</p>
 * <p>原因是，新增的 public method 會紀錄下列內容：</p>
 * <ol>
 * <li>應用程式 Binding(登入) LDAP 的使用者(必要)</li>
 * <li>使用的 LDAPUtil method 名稱(必要)</li>
 * <li>使用時間(必要)</li>
 * <li>使用的 LDAPUtil method 應用程式所屬的機器名稱(非必要)</li>
 * <li>使用的 LDAPUtil method 的應用程式名稱(非必要)</li>
 * </ol>
 * <p>而既有的 public method 會紀錄下列內容：</p>
 * <ol>
 * <li>應用程式 Binding(登入) LDAP 的使用者(必要)</li>
 * <li>使用的 LDAPUtil method 名稱(必要)</li>
 * <li>使用時間(必要)</li>
 * </ol>
 * 
 * 
 * 
 * @author Chien upd 99/02/09
 * @author WebComm Mark Su
 * @version 1.2
 */


	

public class LDAPUtil {

	/*
	 * SearchScope=0,只對searchBaseDN的物件做搜尋
	 * SearchScope=1,只對searchBaseDN的物件的下一層做搜尋,不含searchBaseDN的物件本身
	 * SearchScope=2,對於searchBaseDN的物件及其子樹做搜尋
	 */

	private static LDAPUtil me = null;

	/**
	 * singleton
	 * @param hostName 使用此功能應用程式所屬的機器名稱
	 * @param progName 使用此功能的應用程式名稱
	 * @return
	 */
	public static synchronized LDAPUtil getInstance() {
		if (me == null)
			me = new LDAPUtil();
		return me;
	}

	DirContext ctx = null;

	// LDAP 定義 Group 存放的 DN
	private String groupBaseDN = null;

	private String groupOfName = null;

	// LDAP Login ID (管理帳號）
	private String loginID = null;

	// LDAP Login Password (管理帳號）
	private String loginPwd = null;

	private String member = null;

	// LDAP Server URL
	private String url = null;

	// LDAP 定義 User 存放的 DN
	private String userBaseDN = null;
	
	//所使用的 LDAP Util method
	private String methodName = null;
	
	//使用此功能應用程式所屬的機器名稱
	//private String hostname = null; 
	
	//使用此功能的應用程式名稱
	//private String programName = null; 

	/**
	 * Constructor
	 */
	private LDAPUtil() {
		// 取得各項所需參數
		url = ResourceReader.getInstance().getResource("ldap.url");
		loginID = ResourceReader.getInstance().getResource("ldap.loginID");
		loginPwd = ResourceReader.getInstance().getResource("ldap.loginPwd");
		userBaseDN = ResourceReader.getInstance().getResource("ldap.userDN");
		groupBaseDN = ResourceReader.getInstance().getResource("ldap.groupDN");
		member = ResourceReader.getInstance().getResource("ldap.member");
		groupOfName = ResourceReader.getInstance().getResource(
				"ldap.groupOfName");
	}
	
	

	// START 使用者管理
	// -----------------------------------------------------------------------------------------------
	/**
	 * Chien upd LDAP 新增使用者
	 * 
	 * @param user 使用者資訊
	 * @param groupID 群組代碼
	 * @throws LDAPUtilException
	 */
	public void addUser(LDAPUser user, String groupRDN, String hostname, String programName)
			throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "addUser";
		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		// 建立 RDN, RDN 為 uid
		String userID = user.getUserId();
		String urdn = new StringBuffer().append("cn=").append(userID).append(
				",").append(groupRDN).toString();
		Attributes attrUser = new BasicAttributes(true);
		attrUser.put("userPassword", user.getUserPwd());
		attrUser.put("objectClass", "inetOrgPerson");
		attrUser.put("sn", user.getSn());
		attrUser.put("givenName", user.getSn());
		attrUser.put("carLicense", user.getIdNo());
		attrUser.put("Title", user.getTitle());
		attrUser.put("OU", user.getOu());
		attrUser.put("departmentNumber", user.getDeptDesc());
		attrUser.put("Description", user.getDeptDescC());
		attrUser.put("displayName", user.getDisplayNm());
		attrUser.put("HID", user.getHID());

		Map<?, ?> infos = user.getInfoMap();
		if (infos != null) {
			for (Iterator<?> iter = infos.keySet().iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				Object value = infos.get(key);
				attrUser.put(key, value);
			}
		}
		// 建立使用者
		create(userBaseDN, urdn, attrUser);
	}


	/**
	 * @param userRDNArray
	 * @param groupRDN
	 * @throws LDAPUtilException
	 */
	public void addUsersToGroup(String[] userRDNArray, String groupRDN, String hostname, String programName)
			throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "addUsersToGroup";
		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		Attributes attributes = new BasicAttributes(true);
		Attribute attr = new BasicAttribute(member);

		String[] userDNArray = null;
		if (userRDNArray != null) {
			userDNArray = new String[userRDNArray.length];

			for (int i = 0; i < userRDNArray.length; i++) {
				userDNArray[i] = findUserDN(userRDNArray[i]) + "," + userBaseDN;
				attr.add(userDNArray[i]);
			}
		}
		// 以groupRDN查詢目前存在此使用者的群組
		SearchResult rmGroup = findGroupByGroupId(groupRDN);
		attributes.put(attr);
		if (rmGroup != null) {
			String rmGroupDN = rmGroup.getName();
			String[] rmGroupDNs = rmGroupDN.split(",");
			String parentDN = new String("");
			for (int i = 1; i < rmGroupDNs.length; i++) {
				parentDN += rmGroupDNs[i] + ",";
			}
			modify(parentDN + groupBaseDN, rmGroupDNs[0], attributes,
					DirContext.ADD_ATTRIBUTE);
		}
	}


	// START 使用者群組管理
	// --------------------------------------------------------------------------------------------
	/**
	 * @param userRDN
	 * @param groupRDN
	 * @throws LDAPUtilException
	 */
	public void addUserToGroup(String userRDN, String groupRDN, String hostname, String programName)
			throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "addUsersToGroup";
		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String userDN = findUserDN(userRDN);
		// 以 User DN 查詢目前存在此使用者的群組
		SearchResult rmGroup = findGroupByGroupId(groupRDN);
		Attributes attributes = new BasicAttributes(true);
		attributes.put(member, userDN + "," + userBaseDN);
		if (rmGroup != null) {
			String rmGroupDN = rmGroup.getName();
			String[] rmGroupDNs = rmGroupDN.split(",");
			String parentDN = new String("");
			for (int i = 1; i < rmGroupDNs.length; i++) {
				parentDN += rmGroupDNs[i] + ",";
			}
			modify(parentDN + groupBaseDN, rmGroupDNs[0], attributes,
					DirContext.ADD_ATTRIBUTE);
		}
	}

	/**
	 * 變更群組
	 * 
	 * @param userRDN 使用者 ID
	 * @param fromGroupRDN 使用者原始群組 ID
	 * @param toGroupRDN 使用者目標群組 ID
	 * @throws LDAPUtilException
	 */
	public void changeGroup(String userRDN, String fromGroupRDN,
			String toGroupRDN, String hostname, String programName) throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "changeGroup";
		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String userDN = findUserDN(userRDN);
		// 以 User DN 查詢目前存在此使用者的群組
		SearchResult rmGroup = findGroupByGroupId(fromGroupRDN);
		Attributes attributes = new BasicAttributes(true);
		attributes.put(member, userDN + "," + userBaseDN);
		// 原群組刪除此使用者
		if (rmGroup != null) {
			String rmGroupDN = rmGroup.getName();
			String[] rmGroupDNs = rmGroupDN.split(",");
			String parentDN = new String("");
			for (int i = 1; i < rmGroupDNs.length; i++) {
				parentDN += rmGroupDNs[i] + ",";
			}
			modify(parentDN + groupBaseDN, rmGroupDNs[0], attributes,
					DirContext.REMOVE_ATTRIBUTE);
		}
		// 將使用者加入至新群組
		String addGroupRDN = new StringBuffer().append("cn=")
				.append(toGroupRDN).toString();
		String filter = new StringBuffer().append("(&(").append(addGroupRDN)
				.append(")(objectclass=groupOfNames))").toString();
		SearchResult addGroup = find(groupBaseDN, filter);
		if (addGroup != null) {
			String addGroupDN = addGroup.getName();
			String[] addGroupDNs = addGroupDN.split(",");
			String parentDN = new String("");
			for (int i = 1; i < addGroupDNs.length; i++) {
				parentDN += addGroupDNs[i] + ",";
			}
			modify(parentDN + groupBaseDN, addGroupDNs[0], attributes,
					DirContext.ADD_ATTRIBUTE);
		}
	}

	// START 使用者驗証
	// -----------------------------------------------------------------------------------------------
	/**
	 * 檢查使用者是否存在
	 * 
	 * @author David Tung
	 * @param userId
	 * @return boolean
	 * @throws LDAPUtilException
	 */
	public boolean checkUserExist(String userId, String hostname, String programName) throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "checkUserExist";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String filter = new StringBuffer().append("(&(cn=").append(userId)
				.append(")(objectClass=inetOrgPerson))").toString();
		SearchResult validUser;
		try {
			validUser = find(userBaseDN, filter);
		} catch (LDAPUtilException e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : checkUserExist , Error Message : "
							+ e.getMessage());
			e.printStackTrace();
			throw new LDAPUtilException(e);
		}
		if (validUser == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 在LDAP建立節點
	 * 
	 * @param parentDN 根節點位置
	 * @param rdn 節點 RDN
	 */
	private void create(String parentDN, String rdn, Attributes attributes)
			throws LDAPUtilException {
		DirContext ctx = null;
		Properties env = new Properties();
		// LDAP 存取相關資訊
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, new StringBuffer().append(url)
				.append("/").append(parentDN).toString());
		env.put(Context.SECURITY_PRINCIPAL, loginID);
		env.put(Context.SECURITY_CREDENTIALS, loginPwd);
		try {
			ctx = new InitialDirContext(env);
			ctx.createSubcontext(rdn, attributes);
		} catch (NamingException e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : create , Error Message : "
							+ e.getMessage());
			e.printStackTrace();
			throw new LDAPUtilException(e);
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (Exception e) {
					System.out
							.println("Class Name : LDAPUtil , Method Name : create , Error Message : "
									+ e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Chien upd 刪除使用者
	 * 
	 * @param userID User ID
	 * @throws LDAPUtilException
	 */
	public void deleteUser(String userID, String hostname, String programName) throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "deleteUser";
		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		// 以 User DN 刪除
		String urdn = findUserDN(userID);
		if (urdn == null || urdn.equals("")) {
			throw new LDAPUtilException("查無卡號(帳號)：" + userID + "之使用者");
		} else {
			// 刪除群組關聯
			removeUserFromAllGroup(userID);
			// 刪除 User
			destory(userBaseDN, urdn);
		}
	}

	// END 使用者管理
	// -----------------------------------------------------------------------------------------------

	// 以下為 2010/01/28版本
	// *******************************************************************************************

	/**
	 * 刪除節點
	 * 
	 * @param parentDN 根節點位置
	 * @param rdn 刪除的節點 RDN
	 */
	private void destory(String parentDN, String rdn) throws LDAPUtilException {
		DirContext ctx = null;
		Properties env = new Properties();
		// LDAP 存取相關資訊
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, new StringBuffer().append(url)
				.append("/").append(parentDN).toString());
		env.put(Context.SECURITY_PRINCIPAL, loginID);
		env.put(Context.SECURITY_CREDENTIALS, loginPwd);
		try {
			ctx = new InitialDirContext(env);
			ctx.destroySubcontext(rdn);
		} catch (NamingException e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : destory , Error Message : "
							+ e.getMessage());
			e.printStackTrace();
			throw new LDAPUtilException(e);
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (Exception e) {
					System.out
							.println("Class Name : LDAPUtil , Method Name : destory , Error Message : "
									+ e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 單筆查詢
	 * 
	 * @param dn 查詢根節點
	 * @param filter 條件字串
	 * @return 單筆結果
	 * @throws LDAPUtilException
	 */
	private SearchResult find(String dn, String filter)
			throws LDAPUtilException {
		SearchResult result = null;
		NamingEnumeration<?> results = findList(dn, filter);
		if (results == null)
			return null;
		try {
			if (results.hasMore())
				result = (SearchResult) results.next();
		} catch (NamingException e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : find , Error Message : "
							+ e.getMessage());
			e.printStackTrace();
			throw new LDAPUtilException(e);
		}
		return result;
	}
	/**
	 * @param groupRDN
	 * @return
	 * @throws LDAPUtilException
	 */
	public SearchResult findGroupByGroupId(String groupRDN)
			throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
//		methodName = "findGroupByGroupId";
//		log(methodName, new Date(), "", "");
		// 20100609 End of add
		String filter = new StringBuffer().append("(&(cn=").append(groupRDN)
				.append(")(objectClass=").append(groupOfName).append("))")
				.toString();
		return find(groupBaseDN, filter);
	}

	/**
	 * @param groupRDN
	 * @return
	 * @throws LDAPUtilException
	 */
	public SearchResult findGroupByGroupId(String groupRDN, String hostname, String programName)
			throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
//		methodName = "findGroupByGroupId";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String filter = new StringBuffer().append("(&(cn=").append(groupRDN)
				.append(")(objectClass=").append(groupOfName).append("))")
				.toString();
		return find(groupBaseDN, filter);
	}

	/**
	 * 由 User DN 查詢群組(單筆)
	 * 
	 * @param userRDN User DN
	 * @return 單筆結果
	 * @throws LDAPUtilException
	 */
	public SearchResult findGroupByUser(String userRDN, String hostname, String programName)
			throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
//		methodName = "findGroupByUser";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String userDN = findUserDN(userRDN);
		String filter = new StringBuffer().append("(&(").append(member).append(
				"=").append(userDN).append(",").append(userBaseDN).append(
				")(objectClass=").append(groupOfName).append("))").toString();
		return find(groupBaseDN, filter);
	}

	/**
	 * 由 User DN 查詢群組(單筆)
	 * 
	 * @param userRDN User DN
	 * @return 單筆結果
	 * @throws LDAPUtilException
	 */
	public SearchResult findGroupByUser(String userRDN)
			throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
//		methodName = "findGroupsByUser";
//		log(methodName, new Date(), "", "");
		// 20100609 End of add
		String userDN = findUserDN(userRDN);
		String filter = new StringBuffer().append("(&(").append(member).append(
				"=").append(userDN).append(",").append(userBaseDN).append(
				")(objectClass=").append(groupOfName).append("))").toString();
		return find(groupBaseDN, filter);
	}

	/**
	 * @param userRDN
	 * @return
	 * @throws LDAPUtilException
	 */
	public Collection<String> findGroupsByUser(String userRDN) throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
//		methodName = "findGroupsByUser";
//		log(methodName, new Date(), "", "");
		// 20100609 End of add
		String userDN = findUserDN(userRDN);
		String filter = new StringBuffer().append("(&(").append(member).append(
				"=").append(userDN).append(",").append(userBaseDN).append(
				")(objectClass=").append(groupOfName).append("))").toString();
		NamingEnumeration<?> enums = findList(groupBaseDN, filter);
		Collection<String> groups = new ArrayList<String>();
		while (enums.hasMoreElements()) {
			SearchResult result = (SearchResult) enums.nextElement();
			groups.add(result.getName());
		}
		return groups;
	}

	/**
	 * @param userRDN
	 * @return
	 * @throws LDAPUtilException
	 */
	public Collection<String> findGroupsByUser(String userRDN, String hostname, String programName) throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
//		methodName = "findGroupsByUser";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String userDN = findUserDN(userRDN);
		String filter = new StringBuffer().append("(&(").append(member).append(
				"=").append(userDN).append(",").append(userBaseDN).append(
				")(objectClass=").append(groupOfName).append("))").toString();
		NamingEnumeration<?> enums = findList(groupBaseDN, filter);
		Collection<String> groups = new ArrayList<String>();
		while (enums.hasMoreElements()) {
			SearchResult result = (SearchResult) enums.nextElement();
			groups.add(result.getName());
		}
		return groups;
	}
	/**
	 * 由 User DN 查詢群組(多筆)
	 * 
	 * @param userRDN User DN
	 * @return 多筆結果，只含cn值
	 * @throws LDAPUtilException
	 */
	public Collection<String> findGroupsRDNByUser(String userRDN)
			throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
//		methodName = "findGroupsRDNByUser";
//		log(methodName, new Date(), "", "");
		// 20100609 End of add
		String userDN = findUserDN(userRDN);
		String filter = new StringBuffer().append("(&(").append(member).append(
				"=").append(userDN).append(",").append(userBaseDN).append(
				")(objectClass=").append(groupOfName).append("))").toString();
		NamingEnumeration<?> enums = findList(groupBaseDN, filter);
		Collection<String> groups = new ArrayList<String>();
		while (enums.hasMoreElements()) {
			SearchResult result = (SearchResult) enums.nextElement();
			if (result.getName() != null) {
				String[] names = result.getName().split(",");
				String name = names[0].substring(3);
				groups.add(name);
			}
		}
		return groups;
	}

	/**
	 * 由 User DN 查詢群組(多筆)
	 * 
	 * @param userRDN User DN
	 * @return 多筆結果，只含cn值
	 * @throws LDAPUtilException
	 */
	public Collection<String> findGroupsRDNByUser(String userRDN, String hostname, String programName)
			throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
//		methodName = "findGroupsRDNByUser";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String userDN = findUserDN(userRDN);
		String filter = new StringBuffer().append("(&(").append(member).append(
				"=").append(userDN).append(",").append(userBaseDN).append(
				")(objectClass=").append(groupOfName).append("))").toString();
		NamingEnumeration<?> enums = findList(groupBaseDN, filter);
		Collection<String> groups = new ArrayList<String>();
		while (enums.hasMoreElements()) {
			SearchResult result = (SearchResult) enums.nextElement();
			if (result.getName() != null) {
				String[] names = result.getName().split(",");
				String name = names[0].substring(3);
				groups.add(name);
			}
		}
		return groups;
	}


	/**
	 * 多筆查詢
	 * 
	 * @param dn 查詢根節點
	 * @param filter 條件字串
	 * @return 多筆結果
	 * @throws LDAPUtilException
	 */
	private NamingEnumeration<?> findList(String dn, String filter)
			throws LDAPUtilException {
		NamingEnumeration<?> results = null;
		DirContext ctx = null;
		Properties env = new Properties();
		// LDAP 存取相關資訊
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_PRINCIPAL, loginID);
		env.put(Context.SECURITY_CREDENTIALS, loginPwd);
		try {
			ctx = new InitialDirContext(env);
			SearchControls constraints = new SearchControls();
			// 查詢範圍，針對高榮本身 LDAP 的設計，採用整顆子樹的搜尋
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			// 不設定查詢結果數量及使用時間限制
			constraints.setCountLimit(0);
			constraints.setTimeLimit(0);
			results = ctx.search(dn, filter, constraints);
		} catch (NamingException e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : findList , Error Message : "
							+ e.getMessage());
			e.printStackTrace();
			throw new LDAPUtilException(e);
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (Exception e) {
					System.out
							.println("Class Name : LDAPUtil , Method Name : findList , Error Message : "
									+ e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return results;
	}

	/**
	 * 用卡號回傳使用者LDAP物件
	 * 
	 * @param userId
	 * @return
	 * @throws LDAPUtilException
	 */
	public SearchResult findUser(String userId, String hostname, String programName) throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "findUser";
//		log(methodName, new Date(), hostname, programName);
//		// 20100609 End of add
		String filter = new StringBuffer().append("(&(cn=").append(userId)
				.append(")(objectClass=inetOrgPerson))").toString();
		SearchResult validUser;
		try {
			validUser = find(userBaseDN, filter);
		} catch (LDAPUtilException e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : findUser , Error Message : "
							+ e.getMessage());
			e.printStackTrace();
			throw new LDAPUtilException(e);
		}
		return validUser;
	}
	/**
	 * 用卡號回傳使用者LDAP DN
	 * 
	 * @param userRDN
	 * @return
	 * @throws LDAPUtilException
	 */
	public String findUserDN(String userRDN) throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
//		log("findUserDN", new Date(), "", "");
		// 20100609 End of add
		String filter = new StringBuffer().append("(&(cn=").append(userRDN)
				.append(")(objectClass=inetOrgPerson))").toString();
		SearchResult validUser;
		try {
			validUser = find(userBaseDN, filter);
		} catch (LDAPUtilException e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : findUserDN , Error Message : "
							+ e.getMessage());
			e.printStackTrace();
			throw new LDAPUtilException(e);
		}
		return validUser.getName();
	}


	/**
	 * 用卡號回傳使用者LDAP DN
	 * 
	 * @param userRDN
	 * @return
	 * @throws LDAPUtilException
	 */
	public String findUserDN(String userRDN, String hostname, String programName) throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "findUserDN";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String filter = new StringBuffer().append("(&(cn=").append(userRDN)
				.append(")(objectClass=inetOrgPerson))").toString();
		SearchResult validUser;
		try {
			validUser = find(userBaseDN, filter);
		} catch (LDAPUtilException e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : findUserDN , Error Message : "
							+ e.getMessage());
			e.printStackTrace();
			throw new LDAPUtilException(e);
		}
		return validUser.getName();
	}

	

	/**
	 * @return
	 * @throws EntryNotFoundException
	 * @throws CustomRegistryException
	 * @throws RemoteException
	 */
	public DirContext getCtx() throws RemoteException {
		// 20100609 Added by WebComm Mark Su
//		methodName = "getCtx";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		try {
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, url);
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, loginID);
			env.put(Context.SECURITY_CREDENTIALS, loginPwd);

			ctx = new InitialDirContext(env);

		} catch (javax.naming.AuthenticationException e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : getCtx , Error Message : "
							+ e.getMessage());
			System.out.println("認證失敗");
			System.out.println(e);
		} catch (Exception e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : getCtx , Error Message : "
							+ e.getMessage());
			System.out.print("System error:");
			e.printStackTrace();
		}
		return ctx;
	}


	// START 使用者基本資料查詢 99/02/09 add (王大哥提供)
	// ----------------------------------------------------------------
	/**
	 * 查詢使用者成本中心代碼
	 * 
	 * @param userSecurityName 使用者卡號
	 * @return 成本中心代碼 ex:950
	 * @throws PasswordCheckFailedException
	 * @throws CustomRegistryException
	 * @throws RemoteException
	 */
	public String getCostId(String userSecurityName,String hostname, String programName)
			throws //PasswordCheckFailedException, CustomRegistryException,
			RemoteException,LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "getCostId";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String costId = "";
		try {
			ctx = getCtx();
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<?> results;

			String[] attrIDs = { "ou" }; // 設定欲讀取的屬性
			constraints.setReturningAttributes(attrIDs);
			results = ctx.search(userBaseDN, "cn=" + userSecurityName,
					constraints);

			if (results != null) {
				//int i = 0;
				while (results.hasMore()) {
					//i++;
					//Attributes attrs = null;
					SearchResult si = null;
					si = (SearchResult) results.next();
					Attributes attrs1 = si.getAttributes();
					if(attrs1.get("ou")!=null && attrs1.get("ou").toString().length()>=4) costId = attrs1.get("ou").toString().substring(4);
				}
			}
		} catch (Exception e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : getCostId , Error Message : "
							+ e.getMessage());
			System.out.print("System error:");
			e.printStackTrace();
			throw new LDAPUtilException(e);
		}
		return costId;
	}
	
	   /**
     * 查詢使用者成本中心代碼
     * @param userSecurityName
     *        使用者卡號
     * @return 成本中心代碼 ex:950
     * @throws Exception
     */
	public String getCostId(String userSecurityName) throws RemoteException {
		String costId = null;

		try {
			ctx = getCtx();
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope( SearchControls.SUBTREE_SCOPE );
			NamingEnumeration<?> results;
	
			String[] attrIDs = {"ou"};	//設定欲讀取的屬性
			constraints.setReturningAttributes(attrIDs);
			results = ctx.search(userBaseDN,"cn="+userSecurityName,constraints);
	
			if (results != null) {
				//int i =0 ;
				while ( results.hasMore() ) {
				//	i++;
				//	Attributes attrs = null;
					SearchResult si = null;
					si = (SearchResult) results.next() ;
					Attributes attrs1 = si.getAttributes();
					if(attrs1.get("ou")!=null && attrs1.get("ou").toString().length()>=4) costId = attrs1.get("ou").toString().substring(4);
					//若還是null 抓name中的參數
					if (costId==null||costId.trim().length()==0)
					{
						 String dataAry[] =si.getName().toString().split(",");
						 int k=0;
						 while (k<dataAry.length&&costId==null)
						 {   
							 if (dataAry[k].contains("ou"))
							 {	 String keyid[]=dataAry[k].split("=");
							     costId=keyid[1];
							 }
							 k++;
						 }
							
						 
					}
				}
			}
		} catch(Exception e) {
			System.out.print("System error:");
			e.printStackTrace();
		} 
		return costId;	
	}
	/**
	 * 查詢使用者單位名稱
	 * 
	 * @param userSecurityName 使用者卡號
	 * @return 單位名稱 ex:資訊室
	 * @throws PasswordCheckFailedException
	 * @throws CustomRegistryException
	 * @throws RemoteException
	 */
	public String getDeptName(String userSecurityName,String hostname, String programName)
			throws	RemoteException,LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "getDeptName";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String deptName = "";

		try {
			ctx = getCtx();
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<?> results;

			String[] attrIDs = { "description" }; // 設定欲讀取的屬性
			constraints.setReturningAttributes(attrIDs);
			results = ctx.search(userBaseDN, "cn=" + userSecurityName,
					constraints);

			if (results != null) {
			//	int i = 0;
				while (results.hasMore()) {
			//		i++;
				//	Attributes attrs = null;
					SearchResult si = null;
					si = (SearchResult) results.next();
					Attributes attrs1 = si.getAttributes();
					if(attrs1.get("description")!=null && attrs1.get("description").toString().length()>=13)  deptName = attrs1.get("description").toString().substring(13);
				}
			}
		} catch (Exception e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : getDeptName , Error Message : "
							+ e.getMessage());
			System.out.print("System error:");
			e.printStackTrace();
			throw new LDAPUtilException(e);
		}
		return deptName;
	}

	/**
	 * 查詢使用者單位英文代碼
	 * 
	 * @param userSecurityName 使用者卡號
	 * @return 單位英文代碼 ex:ISC
	 * @throws PasswordCheckFailedException
	 * @throws CustomRegistryException
	 * @throws RemoteException
	 */
	public String getDeptNo(String userSecurityName,String hostname, String programName)
			throws RemoteException,LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "getDeptNo";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String departmentNumber = "";

		try {
			ctx = getCtx();
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<?> results;

			String[] attrIDs = { "departmentNumber" }; // 設定欲讀取的屬性
			constraints.setReturningAttributes(attrIDs);
			results = ctx.search(userBaseDN, "cn=" + userSecurityName,
					constraints);

			if (results != null) {
			//	int i = 0;
				while (results.hasMore()) {
				//	i++;
				//	Attributes attrs = null;
					SearchResult si = null;
					si = (SearchResult) results.next();
					Attributes attrs1 = si.getAttributes();
					if(attrs1.get("departmentNumber")!=null && attrs1.get("departmentNumber").toString().length()>=18) departmentNumber = attrs1.get("departmentNumber").toString().substring(18);
				}
			}
		} catch (Exception e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : getDeptNo , Error Message : "
							+ e.getMessage());
			System.out.print("System error:");
			e.printStackTrace();
			throw new LDAPUtilException(e);
		}
		return departmentNumber;
	}
	
	/**
	 * 抓取LDAP成本中心英文代碼(避免單位英文代碼過長，ISC自編4碼)
	 * @param 卡號
	 * @param hostname
	 * @param programName
	 * @return	成本中心英文代碼 ex.LAB1->PATH
	 * @throws RemoteException
	 * @throws LDAPUtilException
	 */
	public String getCstengID(String userSecurityName,String hostname, String programName)
	throws RemoteException,LDAPUtilException {
		methodName = "getCstengID";
//		log(methodName, new Date(), hostname, programName);
		String departmentNumber = "";

	try {
		ctx = getCtx();
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration<?> results;

		String[] attrIDs = { "CSTENGID" }; // 設定欲讀取的屬性
		constraints.setReturningAttributes(attrIDs);
		results = ctx.search(userBaseDN, "cn=" + userSecurityName,
			constraints);

		if (results != null) {
			//int i = 0;
			while (results.hasMore()) {
			//	i++;
			//	Attributes attrs = null;
				SearchResult si = null;
				si = (SearchResult) results.next();
				Attributes attrs1 = si.getAttributes();
				if(attrs1.get("CSTENGID")!=null) 
					departmentNumber = attrs1.get("CSTENGID").toString().substring(10);
			}	
		}
	} catch (Exception e) {
		System.out
			.println("Class Name : LDAPUtil , Method Name : getDeptNo , Error Message : "
					+ e.getMessage());
		System.out.print("System error:");
		e.printStackTrace();
		throw new LDAPUtilException(e);
	}
	return departmentNumber;
	}
	/**
	 * 查詢使用者DN
	 * 
	 * @param userSecurityName 使用者卡號
	 * @return DN ex:cn=t5091,ou=WPS,ou=SP,ou=950
	 * @throws Exception
	 */
	public String getDn(String userSecurityName,String hostname, String programName) throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "getDn";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String userDn = null;
		try {
			ctx = getCtx();

			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

			NamingEnumeration<?> results;

			String[] attrIDs = { "ou" }; // 設定欲讀取的屬性
			constraints.setReturningAttributes(attrIDs);
			results = ctx.search(userBaseDN, "cn=" + userSecurityName,
					constraints);

			if (results != null && results.hasMore()) {

				//Attributes attrs = null;
				SearchResult si = null;
				si = (SearchResult) results.next();
				userDn = si.getName();
			}
		} catch (javax.naming.AuthenticationException e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : getDn , Error Message : "
							+ e.getMessage());
			System.out.println("認證失敗");
			System.out.println(e);
		} catch (Exception e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : getDn , Error Message : "
							+ e.getMessage());
			System.out.print("System error:");
			e.printStackTrace();
			throw new LDAPUtilException(e);
		}
		return userDn;
	}

	/**
	 * 查詢醫師章號
	 * 
	 * @param userSecurityName 使用者卡號
	 * @return 使用者醫師章號
	 * @throws Exception
	 */
	public String getDocNo(String userSecurityName,String hostname, String programName) throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "getDocNo";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String docNo = "";
		try {
			ctx = getCtx();
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<?> results;

			String[] attrIDs = { "doctorNumber" }; // 設定欲讀取的屬性
			constraints.setReturningAttributes(attrIDs);
			results = ctx.search(userBaseDN, "cn=" + userSecurityName,
					constraints);

			if (results != null) {
				//int i = 0;
				while (results.hasMore()) {
				//	i++;
				//	Attributes attrs = null;
					SearchResult si = null;
					si = (SearchResult) results.next();
					Attributes attrs1 = si.getAttributes();
					if(attrs1.get("doctorNumber")!=null && attrs1.get("doctorNumber").toString().length()>=14) docNo = attrs1.get("doctorNumber").toString().substring(14);
				}
			}
		} catch (Exception e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : getDocNo , Error Message : "
							+ e.getMessage());
			System.out.print("System error:");
			e.printStackTrace();
			throw new LDAPUtilException(e);
		}
		return docNo;
	}

	/**
	 * 查詢使用者電子郵件
	 * 
	 * @param userSecurityName 使用者卡號
	 * @return 電子郵件 ex:yhchien@vghks.gov.tw
	 * @throws PasswordCheckFailedException
	 * @throws CustomRegistryException
	 * @throws RemoteException
	 */
	public String getEMail(String userSecurityName,String hostname, String programName)
			throws 	RemoteException,LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "getEMail";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String eMail = "";
		try {
			ctx = getCtx();
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<?> results;

			String[] attrIDs = { "mail" }; // 設定欲讀取的屬性
			constraints.setReturningAttributes(attrIDs);
			results = ctx.search(userBaseDN, "cn=" + userSecurityName,
					constraints);

			if (results != null) {
				//int i = 0;
				while (results.hasMore()) {
				//	i++;
				//	Attributes attrs = null;
					SearchResult si = null;
					si = (SearchResult) results.next();
					Attributes attrs1 = si.getAttributes();
					if(attrs1.get("mail")!=null && attrs1.get("mail").toString().length()>=6) eMail = attrs1.get("mail").toString().substring(6);
				}
			}
		} catch (Exception e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : getEMail , Error Message : "
							+ e.getMessage());
			System.out.print("System error:");
			e.printStackTrace();
			throw new LDAPUtilException(e);
		}
		return eMail;
	}

	/**
	 * 查詢使用者LDAP 屬性值
	 * 
	 * @author Chien
	 * @param userSecurityName 使用者卡號
	 * @param ldapAttr LDAP屬性代碼
	 * @return 屬性值 ex:S
	 * @throws PasswordCheckFailedException
	 * @throws CustomRegistryException
	 * @throws RemoteException
	 */
	public String getLdapAttr(String userSecurityName, String ldapAttr,String hostname, String programName)
			throws RemoteException,LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "getLdapAttr";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String attrVal = null;
		int attrLen = 0;
		try {
			ctx = getCtx();
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<?> results;

			String[] attrIDs = { ldapAttr }; // 設定欲讀取的屬性
			if (ldapAttr != null)
				attrLen = ldapAttr.length();
			constraints.setReturningAttributes(attrIDs);
			results = ctx.search(userBaseDN, "cn=" + userSecurityName,
					constraints);

			if (results != null) {
				//int i = 0;
				while (results.hasMore()) {
				//	i++;
					SearchResult si = null;
					si = (SearchResult) results.next();
					Attributes attrs1 = si.getAttributes();
					if(attrs1.get(ldapAttr)!=null){
						String tmpVal = attrs1.get(ldapAttr).toString();
					    if(tmpVal.length()>=(attrLen+2)) attrVal = tmpVal.substring(attrLen+2);
					}
				}
			}
		} catch (Exception e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : getLdapAttr , Error Message : "
							+ e.getMessage());
			System.out.print("System error:");
			e.printStackTrace();
			throw new LDAPUtilException(e);
		}
		return attrVal;
	}

	/**
	 * 查詢使用者職稱代碼(psWkTyp)
	 * 
	 * @param userSecurityName 使用者卡號
	 * @return 職稱代碼(psWkTyp) ex:S
	 * @throws PasswordCheckFailedException
	 * @throws CustomRegistryException
	 * @throws RemoteException
	 */
	public String getPsWkTyp(String userSecurityName,String hostname, String programName)
			throws RemoteException,LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "getPsWkTyp";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String psWkTyp = "";

		try {
			ctx = getCtx();
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<?> results;

			String[] attrIDs = { "psWkTyp" }; // 設定欲讀取的屬性
			constraints.setReturningAttributes(attrIDs);
			results = ctx.search(userBaseDN, "cn=" + userSecurityName,
					constraints);

			if (results != null) {
			//	int i = 0;
				while (results.hasMore()) {
			//		i++;
					//Attributes attrs = null;
					SearchResult si = null;
					si = (SearchResult) results.next();
					Attributes attrs1 = si.getAttributes();
					if(attrs1.get("psWkTyp")!=null && attrs1.get("psWkTyp").toString().length()>=9) psWkTyp = attrs1.get("psWkTyp").toString().substring(9);
				}
			}
		} catch (Exception e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : getPsWkTyp , Error Message : "
							+ e.getMessage());
			System.out.print("System error:");
			e.printStackTrace();
			throw new LDAPUtilException(e);
		}
		return psWkTyp;
	}

	/**
	 * 查詢使用者聯絡電話
	 * 
	 * @param userSecurityName 使用者卡號
	 * @return 聯絡電話 ex:5524
	 * @throws PasswordCheckFailedException
	 * @throws CustomRegistryException
	 * @throws RemoteException
	 */
	public String getTelephoneNumber(String userSecurityName,String hostname, String programName)
			throws RemoteException,LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "getTelephoneNumber";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String telephoneNumber = "";
		try {
			ctx = getCtx();
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<?> results;

			String[] attrIDs = { "telephoneNumber" }; // 設定欲讀取的屬性
			constraints.setReturningAttributes(attrIDs);
			results = ctx.search(userBaseDN, "cn=" + userSecurityName,
					constraints);

			if (results != null) {
			//	int i = 0;
				while (results.hasMore()) {
				//	i++;
			//		Attributes attrs = null;
					SearchResult si = null;
					si = (SearchResult) results.next();
					Attributes attrs1 = si.getAttributes();
					if(attrs1.get("telephoneNumber")!=null && attrs1.get("telephoneNumber").toString().length()>=17) telephoneNumber = attrs1.get("telephoneNumber").toString().substring(17);
				}
			}
		} catch (Exception e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : getTelephoneNumber , Error Message : "
							+ e.getMessage());
			System.out.print("System error:");
			e.printStackTrace();
			throw new LDAPUtilException(e);
		}
		return telephoneNumber;
	}

	/**
	 * 查詢使用者職稱
	 * 
	 * @param userSecurityName 使用者卡號
	 * @return 職稱 ex:契約資訊技術士
	 * @throws PasswordCheckFailedException
	 * @throws CustomRegistryException
	 * @throws RemoteException
	 */
	public String getTitle(String userSecurityName,String hostname, String programName) throws RemoteException,LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "getTitle";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String title = "";

		try {
			ctx = getCtx();
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<?> results;

			String[] attrIDs = { "title" }; // 設定欲讀取的屬性
			constraints.setReturningAttributes(attrIDs);
			results = ctx.search(userBaseDN, "cn=" + userSecurityName,
					constraints);

			if (results != null) {
				//int i = 0;
				while (results.hasMore()) {
				//	i++;
				//	Attributes attrs = null;
					SearchResult si = null;
					si = (SearchResult) results.next();
					Attributes attrs1 = si.getAttributes();
					if(attrs1.get("title")!=null && attrs1.get("title").toString().length()>=7) title = attrs1.get("title").toString().substring(7);
				}
			}
		} catch (Exception e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : getTitle , Error Message : "
							+ e.getMessage());
			System.out.print("System error:");
			e.printStackTrace();
			throw new LDAPUtilException(e);
		}
		return title;
	}

	/**
	 * 查詢身份証字號
	 * 
	 * @param userSecurityName 使用者卡號
	 * @return 使用者身份証字號
	 * @throws Exception
	 */
	public String getUserId(String userSecurityName,String hostname, String programName) throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "getUserId";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String userId = "";
		try {
			ctx = getCtx();
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<?> results;

			String[] attrIDs = { "carLicense" }; // 設定欲讀取的屬性
			constraints.setReturningAttributes(attrIDs);
			results = ctx.search(userBaseDN, "cn=" + userSecurityName,
					constraints);

			if (results != null) {
				//int i = 0;
				while (results.hasMore()) {
				//	i++;
				//	Attributes attrs = null;
					SearchResult si = null;
					si = (SearchResult) results.next();
					Attributes attrs1 = si.getAttributes();
					if(attrs1.get("carLicense")!=null && attrs1.get("carLicense").toString().length()>=12) userId = attrs1.get("carLicense").toString().substring(12);
				}
			}
		} catch (Exception e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : getUserId , Error Message : "
							+ e.getMessage());
			System.out.print("System error:");
			e.printStackTrace();
			throw new LDAPUtilException(e);
		}
		return userId;
	}


	/**
	 * 查詢使用者姓名
	 * 
	 * @param userSecurityName 使用者卡號
	 * @return 使用者姓名
	 * @throws Exception
	 */
	public String getUserNm(String userSecurityName) throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "getUserNm";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String userNm = "";
		try {
			ctx = getCtx();
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<?> results;

			String[] attrIDs = { "givenName" }; // 設定欲讀取的屬性
			constraints.setReturningAttributes(attrIDs);
			results = ctx.search(userBaseDN, "cn=" + userSecurityName,
					constraints);

			if (results != null) {
				//int i = 0;
				while (results.hasMore()) {
				//	i++;
				//	Attributes attrs = null;
					SearchResult si = null;
					si = (SearchResult) results.next();
					Attributes attrs1 = si.getAttributes();
					if(attrs1.get("givenName")!=null && attrs1.get("givenName").toString().length()>=11)  userNm = attrs1.get("givenName").toString().substring(11);
				}
			}
		} catch (Exception e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : getUserNm , Error Message : "
							+ e.getMessage());
			System.out.print("System error:");
			e.printStackTrace();
			throw new LDAPUtilException(e);
		}
		return userNm;
	}

	/**
	 * LDAP method 使用紀錄
	 * 
	 * @param methodName 所使用的 LDAP Util method
	 * @param logTime 使用時間
	 * @param hostname 使用此功能應用程式所屬的機器名稱
	 * @param programName 使用此功能的應用程式名稱
	 */
	private void log(String methodName, Date logTime, String hostname,
			String programName) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			DataSource ds = (DataSource) ServiceLocator.getInstance()
					.getDataSource("jdbc/salNTds"); // 請修改成為連至MGTDB的JNDI名稱並於Web.xml參照中設定!!
			conn = ds.getConnection();
			pstmt = conn
					.prepareStatement("insert into wps.ldaplog(userid, methodname, logtime, hostname, programname) values(?, ?, CURRENT_TIMESTAMP, ?, ?)");
			pstmt.setString(1, loginID);
			pstmt.setString(2, methodName);
			pstmt.setString(3, hostname);
			pstmt.setString(4, programName);
			pstmt.executeUpdate();
			conn.close();
		} catch (ServiceLocatorException e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : log , Error Message : "
							+ e.getMessage());
			e.printStackTrace();
		} catch (SQLException e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : log , Error Message : "
							+ e.getMessage());
			e.printStackTrace();
		} catch (Exception ee) {
			
			ee.printStackTrace();	
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					System.out
							.println("Class Name : LDAPUtil , Method Name : log , Error Message : "
									+ e.getMessage());
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();conn = null;
				} catch (SQLException e) {
					System.out
							.println("Class Name : LDAPUtil , Method Name : log , Error Message : "
									+ e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 驗證使用者及密碼的正確性
	 * 
	 * @param dn 使用者 DN
	 * @param pwd 使用者密碼
	 * @return
	 * @throws LDAPUtilException
	 */
	private void login(String dn, String pwd) throws LDAPUtilException {

		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, dn);
		env.put(Context.SECURITY_CREDENTIALS, pwd);
		try {
			ctx = new InitialDirContext(env);
		} catch (AuthenticationException e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : login , Error Message : "
							+ e.getMessage());
			// 驗證失敗, messageCode = LOGIN_FAILURE(1)
			throw new LDAPUtilException("login failed.",
					LDAPUtilException.LOGIN_FAILURE);
		} catch (NamingException e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : login , Error Message : "
							+ e.getMessage());
			e.printStackTrace();
			throw new LDAPUtilException(e);
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (Exception e) {
					System.out
							.println("Class Name : LDAPUtil , Method Name : login , Error Message : "
									+ e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 修改節點
	 * 
	 * @param parentDN 根節點位置
	 * @param rdn 修改的節點 RDN
	 * @param attributes 修改資訊
	 * @param updateMode 修改模式
	 */
	private void modify(String parentDN, String rdn, Attributes attributes,
			int updateMode) throws LDAPUtilException {
		DirContext ctx = null;
		Properties env = new Properties();
		// LDAP 存取相關資訊
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, new StringBuffer().append(url)
				.append("/").append(parentDN).toString());
		env.put(Context.SECURITY_PRINCIPAL, loginID);
		env.put(Context.SECURITY_CREDENTIALS, loginPwd);
		try {
			ctx = new InitialDirContext(env);
			ctx.modifyAttributes(rdn, updateMode, attributes);
		} catch (NamingException e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : modify , Error Message : "
							+ e.getMessage());
			e.printStackTrace();
			throw new LDAPUtilException(e);
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (Exception e) {
					System.out
							.println("Class Name : LDAPUtil , Method Name : modify , Error Message : "
									+ e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 修改使用者密碼
	 * 
	 * @param userID User ID
	 * @param password 新密碼
	 * @throws LDAPUtilException
	 * @author Red Chien upd
	 */
	public void modifyPwd(String userID, String password)
			throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "modifyPwd";
		log(methodName, new Date(), "", "");

		// 20100609 End of add
		String urdn = findUserDN(userID); // String urdn = new
		// StringBuffer().append("uid=").append(userID).toString();
		Attributes attributes = new BasicAttributes(true);
		attributes.put("userPassword", password);
		// 修改 User
		modify(userBaseDN, urdn, attributes, DirContext.REPLACE_ATTRIBUTE);
	}
	/**
	 * 修改使用者密碼
	 * 
	 * @param userID User ID
	 * @param password 新密碼
	 * @throws LDAPUtilException
	 * @author Red Chien upd
	 */
	public void modifyPwd(String userID, String password,String hostname, String programName)
			throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "modifyPwd";
		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String urdn = findUserDN(userID); // String urdn = new
		// StringBuffer().append("uid=").append(userID).toString();
		Attributes attributes = new BasicAttributes(true);
		attributes.put("userPassword", password);
		// 修改 User
		modify(userBaseDN, urdn, attributes, DirContext.REPLACE_ATTRIBUTE);
	}
	/**
	 * Chien upd 修改使用者密碼
	 * 
	 * @param userID User ID
	 * @param newPwd 新密碼
	 * @param oldPwd 原密碼, 如果傳入 Null 則不驗證即變更密碼
	 * @throws LDAPUtilException 如果是驗證失敗, 則
	 *             LDAPUtilException.messageCode=LDAPUtilException.LOGIN_FAILURE
	 */
	public void modifyPwd(String userID, String newPwd, String oldPwd)
			throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "modifyPwd";
		log(methodName, new Date(), "", "");
		// 20100609 End of add
		String urdn = findUserDN(userID);
		String udn = "";
		if (urdn == null || urdn.equals("")) {
			throw new LDAPUtilException("查無卡號(帳號)：" + userID + "之使用者");
		} else {
			// 驗證原密碼
			if (oldPwd != null) {
				try {
					udn = new StringBuffer().append(urdn).append(",").append(
							userBaseDN).toString();
					login(udn, oldPwd);
				} catch (LDAPUtilException e) {
					System.out
							.println("Class Name : LDAPUtil , Method Name : modifyPwd , Error Message : "
									+ e.getMessage());
					e.printStackTrace();
					if (e.getMessageCode() == LDAPUtilException.LOGIN_FAILURE)
						throw new LDAPUtilException("驗証失敗!原帳號、密碼有誤!");
					else
						throw new LDAPUtilException(e);
				} catch (Exception e2) {
					System.out
							.println("Class Name : LDAPUtil , Method Name : modifyPwd , Error Message : "
									+ e2.getMessage());
					e2.printStackTrace();
				}
			}
			/*
			 * 20080916 Added by WebComm Mark Su 為了釐清userPassword這個LDAP attr
			 * 是被AP移除還是被LDAP自己本身的問題移除的 在這裡增加保護動作，避免當新 password 值為null時，
			 * 會導致userPassword這個屬性被LDAP殺掉
			 */
			if (newPwd == null) {
				throw new LDAPUtilException("新密碼為空值!");
			}
			// 20080916 End of add
			Attributes attributes = new BasicAttributes(true);
			attributes.put("userPassword", newPwd);
			// 修改 User
			modify(userBaseDN, urdn, attributes, DirContext.REPLACE_ATTRIBUTE);
		}
	}
	/**
	 * Chien upd 修改使用者密碼
	 * 
	 * @param userID User ID
	 * @param newPwd 新密碼
	 * @param oldPwd 原密碼, 如果傳入 Null 則不驗證即變更密碼
	 * @throws LDAPUtilException 如果是驗證失敗, 則
	 *             LDAPUtilException.messageCode=LDAPUtilException.LOGIN_FAILURE
	 */
	public void modifyPwd(String userID, String newPwd, String oldPwd,String hostname, String programName)
			throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "modifyPwd";
		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String urdn = findUserDN(userID);
		String udn = "";
		if (urdn == null || urdn.equals("")) {
			throw new LDAPUtilException("查無卡號(帳號)：" + userID + "之使用者");
		} else {
			// 驗證原密碼
			if (oldPwd != null) {
				try {
					udn = new StringBuffer().append(urdn).append(",").append(
							userBaseDN).toString();
					login(udn, oldPwd);
				} catch (LDAPUtilException e) {
					System.out
							.println("Class Name : LDAPUtil , Method Name : modifyPwd , Error Message : "
									+ e.getMessage());
					e.printStackTrace();
					if (e.getMessageCode() == LDAPUtilException.LOGIN_FAILURE)
						throw new LDAPUtilException("驗証失敗!原帳號、密碼有誤!");
					else
						throw new LDAPUtilException(e);
				} catch (Exception e2) {
					System.out
							.println("Class Name : LDAPUtil , Method Name : modifyPwd , Error Message : "
									+ e2.getMessage());
					e2.printStackTrace();
				}
			}
			/*
			 * 20080916 Added by WebComm Mark Su 為了釐清userPassword這個LDAP attr
			 * 是被AP移除還是被LDAP自己本身的問題移除的 在這裡增加保護動作，避免當新 password 值為null時，
			 * 會導致userPassword這個屬性被LDAP殺掉
			 */
			if (newPwd == null) {
				throw new LDAPUtilException("新密碼為空值!");
			}
			// 20080916 End of add
			Attributes attributes = new BasicAttributes(true);
			attributes.put("userPassword", newPwd);
			// 修改 User
			modify(userBaseDN, urdn, attributes, DirContext.REPLACE_ATTRIBUTE);
		}
	}
	/**
	 * 移除使用者所有群組
	 * 
	 * @param userRDN 使用者 ID
	 * @throws LDAPUtilException
	 */
	public void removeUserFromAllGroup(String userRDN) throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "removeUserFromAllGroup";
		log(methodName, new Date(), "", "");
		// 20100609 End of add
		String userDN = findUserDN(userRDN);
		// 以 User DN 查詢目前存在此使用者的群組
		Collection<String> rmGroupCollection = findGroupsByUser(userRDN);
		Attributes attributes = new BasicAttributes(true);
		attributes.put(member, userDN + "," + userBaseDN);
		// 原群組刪除此使用者
		if (rmGroupCollection != null) {
			for (Iterator<String> iterator = rmGroupCollection.iterator(); iterator
					.hasNext();) {
				String rmGroupDN = (String) iterator.next();
				String[] rmGroupDNs = rmGroupDN.split(",");
				String parentDN = new String("");
				for (int i = 1; i < rmGroupDNs.length; i++) {
					parentDN += rmGroupDNs[i] + ",";
				}
				modify(parentDN + groupBaseDN, rmGroupDNs[0], attributes,
						DirContext.REMOVE_ATTRIBUTE);
			}
		}
	}

	/**
	 * 移除使用者所有群組
	 * 
	 * @param userRDN 使用者 ID
	 * @throws LDAPUtilException
	 */
	public void removeUserFromAllGroup(String userRDN,String hostname, String programName) throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "removeUserFromAllGroup";
		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String userDN = findUserDN(userRDN);
		// 以 User DN 查詢目前存在此使用者的群組
		Collection<String> rmGroupCollection = findGroupsByUser(userRDN);
		Attributes attributes = new BasicAttributes(true);
		attributes.put(member, userDN + "," + userBaseDN);
		// 原群組刪除此使用者
		if (rmGroupCollection != null) {
			for (Iterator<String> iterator = rmGroupCollection.iterator(); iterator
					.hasNext();) {
				String rmGroupDN = (String) iterator.next();
				String[] rmGroupDNs = rmGroupDN.split(",");
				String parentDN = new String("");
				for (int i = 1; i < rmGroupDNs.length; i++) {
					parentDN += rmGroupDNs[i] + ",";
				}
				modify(parentDN + groupBaseDN, rmGroupDNs[0], attributes,
						DirContext.REMOVE_ATTRIBUTE);
			}
		}
	}
	/**
	 * 依照指定的groupRDN進行user移除
	 * 
	 * @param userRDN
	 * @param groupRDN
	 * @throws LDAPUtilException
	 */
	public void removeUserFromGroup(String userRDN, String groupRDN)
			throws LDAPUtilException {
		// 20100609 Added removeUserFromGroup WebComm Mark Su
		methodName = "removeUserFromGroup";
		log(methodName, new Date(), "", "");

		// 20100609 End of add
		String userDN = findUserDN(userRDN);
		// 以groupRDN查詢目前存在此使用者的群組
		SearchResult rmGroup = findGroupByGroupId(groupRDN);
		Attributes attributes = new BasicAttributes(true);
		attributes.put(member, userDN + "," + userBaseDN);
		// 查出的群組刪除此使用者
		if (rmGroup != null) {
			String rmGroupDN = rmGroup.getName();
			String[] rmGroupDNs = rmGroupDN.split(",");
			String parentDN = new String("");
			for (int i = 1; i < rmGroupDNs.length; i++) {
				parentDN += rmGroupDNs[i] + ",";
			}
			modify(parentDN + groupBaseDN, rmGroupDNs[0], attributes,
					DirContext.REMOVE_ATTRIBUTE);
		}
	}

	/**
	 * 依照指定的groupRDN進行user移除
	 * 
	 * @param userRDN
	 * @param groupRDN
	 * @throws LDAPUtilException
	 */
	public void removeUserFromGroup(String userRDN, String groupRDN,String hostname, String programName)
			throws LDAPUtilException {
		// 20100609 Added removeUserFromGroup WebComm Mark Su
		methodName = "removeUserFromGroup";
		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		String userDN = findUserDN(userRDN);
		// 以groupRDN查詢目前存在此使用者的群組
		SearchResult rmGroup = findGroupByGroupId(groupRDN);
		Attributes attributes = new BasicAttributes(true);
		attributes.put(member, userDN + "," + userBaseDN);
		// 查出的群組刪除此使用者
		if (rmGroup != null) {
			String rmGroupDN = rmGroup.getName();
			String[] rmGroupDNs = rmGroupDN.split(",");
			String parentDN = new String("");
			for (int i = 1; i < rmGroupDNs.length; i++) {
				parentDN += rmGroupDNs[i] + ",";
			}
			modify(parentDN + groupBaseDN, rmGroupDNs[0], attributes,
					DirContext.REMOVE_ATTRIBUTE);
		}
	}
	/**
	 * 依照指定的groupRDN進行user移除
	 * 
	 * @param userRDNArray
	 * @param groupRDN
	 * @throws LDAPUtilException
	 */
	public void removeUsersFromGroup(String[] userRDNArray, String groupRDN)
			throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		  methodName = "removeUsersFromGroup";
		  log(methodName, new Date(), "", "");
		// 20100609 End of add
		Attributes attributes = new BasicAttributes(true);
		Attribute attr = new BasicAttribute(member);

		String[] userDNArray = null;
		if (userRDNArray != null) {
			userDNArray = new String[userRDNArray.length];

			for (int i = 0; i < userRDNArray.length; i++) {
				userDNArray[i] = findUserDN(userRDNArray[i]) + "," + userBaseDN;
				attr.add(userDNArray[i]);
			}
		}
		// 以groupRDN查詢目前存在此使用者的群組
		SearchResult rmGroup = findGroupByGroupId(groupRDN);
		attributes.put(attr);

		// 查出的群組刪除此使用者
		if (rmGroup != null) {
			String rmGroupDN = rmGroup.getName();
			String[] rmGroupDNs = rmGroupDN.split(",");
			String parentDN = new String("");
			for (int i = 1; i < rmGroupDNs.length; i++) {
				parentDN += rmGroupDNs[i] + ",";
			}
			modify(parentDN + groupBaseDN, rmGroupDNs[0], attributes,
					DirContext.REMOVE_ATTRIBUTE);
		}
	}

	/**
	 * 依照指定的groupRDN進行user移除
	 * 
	 * @param userRDNArray
	 * @param groupRDN
	 * @throws LDAPUtilException
	 */
	public void removeUsersFromGroup(String[] userRDNArray, String groupRDN,String hostname,String programName)
			throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "removeUsersFromGroup";
		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		Attributes attributes = new BasicAttributes(true);
		Attribute attr = new BasicAttribute(member);

		String[] userDNArray = null;
		if (userRDNArray != null) {
			userDNArray = new String[userRDNArray.length];

			for (int i = 0; i < userRDNArray.length; i++) {
				userDNArray[i] = findUserDN(userRDNArray[i]) + "," + userBaseDN;
				attr.add(userDNArray[i]);
			}
		}
		// 以groupRDN查詢目前存在此使用者的群組
		SearchResult rmGroup = findGroupByGroupId(groupRDN);
		attributes.put(attr);

		// 查出的群組刪除此使用者
		if (rmGroup != null) {
			String rmGroupDN = rmGroup.getName();
			String[] rmGroupDNs = rmGroupDN.split(",");
			String parentDN = new String("");
			for (int i = 1; i < rmGroupDNs.length; i++) {
				parentDN += rmGroupDNs[i] + ",";
			}
			modify(parentDN + groupBaseDN, rmGroupDNs[0], attributes,
					DirContext.REMOVE_ATTRIBUTE);
		}
	}


	/**
	 * 驗證使用者帳號密碼正確性
	 * 
	 * @author David Tung ，Chien upd
	 * @param userId
	 * @param password
	 * @return boolean
	 * @throws LDAPUtilException
	 */
	public boolean verifyUser(String userId, String password,String hostname,String programName)
			throws LDAPUtilException {
		// 20100609 Added by WebComm Mark Su
		methodName = "verifyUser";
//		log(methodName, new Date(), hostname, programName);
		// 20100609 End of add
		boolean success = true;
		String urdn = "";
		String udn = "";

		try {
			urdn = findUserDN(userId); // Chien upd , 原程式~ urdn = new
			// StringBuffer().append("uid=").append(userId).toString();
			if (urdn == null || urdn.equals("")) {
				throw new LDAPUtilException("查無卡號(帳號)：" + userId + "之使用者");
			} else {
				udn = new StringBuffer().append(urdn).append(",").append(
						userBaseDN).toString();
				login(udn, password);
			}
		} catch (LDAPUtilException e) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : verifyUser , Error Message : "
							+ e.getMessage());
			e.printStackTrace();
			if (e.getMessageCode() == LDAPUtilException.LOGIN_FAILURE)
				return false;
			else
				throw new LDAPUtilException(e);
		} catch (Exception e2) {
			System.out
					.println("Class Name : LDAPUtil , Method Name : verifyUser , Error Message : "
							+ e2.getMessage());
			e2.printStackTrace();
		}
		return success;
	}


	/**
     * 查詢使用者LDAP 屬性值 
     * @author Chien add 99/04/28
     * @param userId
     *        使用者身份証字號
     * @param ldapAttr
     *        LDAP屬性代碼
     * @return 屬性值  ex:S
     * @throws Exception
     */

	public String getLdapAttrById(String userId,String ldapAttr,String hostname,String programName) throws 	RemoteException,LDAPUtilException {
		methodName = "getLdapAttrById";
//		log(methodName, new Date(), hostname, programName);
		String attrVal = null;
		int attrLen = 0;
		try {
			ctx = getCtx();
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope( SearchControls.SUBTREE_SCOPE );
			NamingEnumeration<?> results;
	
			String[] attrIDs = {ldapAttr};	//設定欲讀取的屬性
			if(ldapAttr!=null) attrLen = ldapAttr.length();
			constraints.setReturningAttributes(attrIDs);
			results = ctx.search(userBaseDN,"carLicense="+userId,constraints);
	
			if (results != null) {
				//int i =0 ;
				while ( results.hasMore() ) {
				//	i++;
					SearchResult si = null;
					si = (SearchResult) results.next() ;
					Attributes attrs1 = si.getAttributes();
					if(attrs1.get(ldapAttr)!=null){
						String tmpVal = attrs1.get(ldapAttr).toString();
					    if(tmpVal.length()>=(attrLen+2)) attrVal = tmpVal.substring(attrLen+2);
					}
				}
			}
		} catch(Exception e) {
			System.out.print("System error:"+e.getMessage());
			e.printStackTrace();
			throw new LDAPUtilException(e);
		} 
		return attrVal;	
	}
	/**
	 * 依卡號查詢使用者所有屬性值
	 * 
	 * @param userId 使用者卡號
	 * @return HashMap 該使用者之所有屬性值(key為LDAP的屬性名稱)
	 * @throws LDAPUtilException
	 */
	public HashMap<String, String> findUserAttr(String userId,String hostname,String programName) throws LDAPUtilException {
		methodName = "findUserAttr";
//		log(methodName, new Date(), hostname, programName);
		String filter = new StringBuffer().append("(&(cn=").append(userId)
				.append(")(objectClass=inetOrgPerson))").toString();
		SearchResult validUser;
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			validUser = find(userBaseDN, filter);
			Attributes attrs =validUser.getAttributes();
			NamingEnumeration<?> e =  attrs.getIDs();
			while (e.hasMoreElements()) {
			    String key = e.nextElement().toString();
			    String attrVal = "";
			    if(key!=null && key.length()>0){
			    	int keyLen = key.length();
			    	if(attrs.get(key)!=null){
						String tmpVal = attrs.get(key).toString();
				        if(tmpVal.length()>=(keyLen+2)) attrVal = tmpVal.substring(keyLen+2);
			    	}   
				    map.put(key, attrVal);
			    }    
			}
		} catch (LDAPUtilException e) {
			e.printStackTrace();
			throw new LDAPUtilException(e);
		}
		return map;
	}

	
	/**
     * 查詢群組所有屬性值
     * @author 
     * @param groupId
     *        群組Id 或 科別代碼
     * @return HashMap
     * @throws Exception
     */

	public HashMap<String, String> findGroupAttr(String groupId,String hostname,String programName) throws 	LDAPUtilException {
		methodName = "findGroupAttr";
//		log(methodName, new Date(), hostname, programName);
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			SearchResult rmGroup = findGroupByGroupId(groupId); //取得groupName中的內容
			if (rmGroup != null) { 
				Attributes attrs = rmGroup.getAttributes();
				NamingEnumeration<?> e =  attrs.getIDs();
				while (e.hasMoreElements()) {
				    String key = e.nextElement().toString();
				    String attrVal = "";
				    if(key!=null && key.length()>0){
				    	int keyLen = key.length();
				    	if(attrs.get(key)!=null){
							String tmpVal = attrs.get(key).toString();
							if(tmpVal.length()>=(keyLen+2)) attrVal = tmpVal.substring(keyLen+2);
				    	}
					    map.put(key, attrVal);
				    }    
				}
			}
		} catch (LDAPUtilException e) {
			e.printStackTrace();
			throw new LDAPUtilException(e);
		}
		return map;
	}
	
	/**
     * 查詢群組下之使用者卡號集合
     * @author 5646
     * @param groupId
     *        群組Id 或 科別代碼
     * @return ArrayList users 該群組下之使用者卡號集合
     * EX : getUsersByGroupId("PSY") getUsersByGroupId("PRQ_Plan")
     * @throws Exception
     */

	public ArrayList<String> getUsersByGroupId(String groupId,String hostname,String programName) throws 	RemoteException,LDAPUtilException {
		methodName = "getUsersByGroupId";
		//log(methodName, new Date(), hostname, programName);
		ArrayList<String> userList = new ArrayList<String>();
		try {
			SearchResult rmGroup = findGroupByGroupId(groupId); //取得groupName中的內容
			if (rmGroup != null) { 
				Attributes attrs = rmGroup.getAttributes();
				String memberStr = attrs.get("member")==null?"":attrs.get("member").toString();
				if(!memberStr.equals("")){
					String[] members = memberStr.split("cn=");
					for(int i=0;i<members.length;i++){
						String dnStr = members[i];
						if(dnStr.length()>0 && dnStr.indexOf(",")>=0){
							String cardNo = dnStr.substring(0,dnStr.indexOf(","));
							userList.add(cardNo);
						}
					}
				}	
			}
		} catch(Exception e) {
			System.out.print("System error:"+e.getMessage());
			e.printStackTrace();
			throw new LDAPUtilException(e);
		} 
		return userList;	
	}
	/**
     * 依使用者附屬單位代碼找出對應之附屬單位中/英文名稱
     * @author 5646
     * @param String ou 附屬成本中心代碼
     * @return String[0]成本中心英文 ex:ISC, String[1]成本中心中文 ex:資訊室
     * @throws Exception
     */
	public String[] getDept2(String ou,String hostname,String programName) throws RemoteException,LDAPUtilException {
		methodName = "getDept2";
		//log(methodName, new Date(), hostname, programName);
		String[] dept = new String[2];
		try {
			SearchResult validUser;
			validUser = find("o=vghksou", "(&(objectClass=organizationalUnit)(ou="+ou+"))");
			Attributes attrs1 = validUser.getAttributes();
			dept[0] = attrs1.get("departmentNumber").toString().substring(18); //附屬成本中心英文
			dept[1] = attrs1.get("description").toString().substring(12); //附屬成本中心中文
			
		} catch(Exception e) {
			System.out.print("System error:" + e.getMessage());
			e.printStackTrace();
			throw new LDAPUtilException(e);
		} 
		return dept; 
	}
	/**
     * 查詢醫療單位成本中心(成本中心 2.3.4.5打頭)
     * @author 
     * @return ArrayList 成本中心 (element:HashMap)
     * @throws Exception
     */
	public ArrayList<HashMap<String, String>> getAllMOu(String hostname,String programName) throws RemoteException,LDAPUtilException {
		ArrayList<HashMap<String, String>> ouLst = new ArrayList<HashMap<String, String>>();
		try {
			groupBaseDN = "o=vghksou";
	  		String filter ="(objectClass=organizationalUnit)";
			
	  		//SearchResult result = null;
	  		NamingEnumeration<?> results = findList(groupBaseDN, filter);
	  		
	  		if (results == null){
	  			throw new LDAPUtilException("查無醫療單位成本中心資料(LDAP)!");
	  		}else{
		  		//Collection<?> groups = new ArrayList<Object>();
		  		while (results.hasMoreElements()) {
		  			HashMap<String, String> ouMap = new HashMap<String, String>();
		  				SearchResult group = (SearchResult) results.nextElement();
			  			String deptStr = group.getName();
			  			String deptId = "";
			  			if(deptStr.length()>=6) deptId = deptStr.substring(3,6);
			  			if(!deptId.equals("") && ( deptId.substring(0,1).equals("2") || deptId.substring(0,1).equals("3") || deptId.substring(0,1).equals("4") || deptId.substring(0,1).equals("5") ) ){
				  		  if(!deptId.equals("200") && !deptId.equals("300")){
			  				String[] deptAry = getDept2(deptId,hostname,programName);
				  			String deptEngNm = deptAry[0]; // 英文代碼
				  			String deptChNm = deptAry[1]; // 中文
				  			ouMap.put("ouId",deptId);
				  			ouMap.put("csten",deptEngNm);
				  			ouMap.put("cstnm",deptChNm);	
				  			ouLst.add(ouMap);
				  		  }
			  			}	
		  		}
		  		//if (results.hasMore())
		  			//	result = (SearchResult) results.next();
	  		}   

		} catch(Exception e) {
			System.out.print("System error:" + e.getMessage());
			e.printStackTrace();
			throw new LDAPUtilException(e);
		} 
		return ouLst; 
	}
	/**
     * 查詢外科部成本中心
     * @author 
     * @return ArrayList 成本中心 (element:HashMap)
     * @throws Exception
     */
	public ArrayList<HashMap<String, String>> getSurOu(String hostname,String programName) throws RemoteException,LDAPUtilException {
		methodName = "getSurOu";
		//log(methodName, new Date(), hostname, programName);
		ArrayList<HashMap<String, String>> ouLst = new ArrayList<HashMap<String, String>>();
		try {
			groupBaseDN = "o=vghksou";
	  		String filter ="(objectClass=organizationalUnit)";
	  		//SearchResult result = null;
	  		NamingEnumeration<?> results = findList(groupBaseDN, filter);
	  		
	  		if (results == null){
	  			return null;
	  		}else{
		  		//Collection<?> groups = new ArrayList<Object>();
		  		while (results.hasMoreElements()) {
		  			HashMap<String, String> ouMap = new HashMap<String, String>();
		  				SearchResult group = (SearchResult) results.nextElement();
			  			String deptStr = group.getName();
			  			String deptId = "";
			  			if(deptStr.length()>=6) deptId = deptStr.substring(3,6);
			  			if(!deptId.equals("") && deptId.substring(0,1).equals("3") && !deptId.equals("300") ){
				  			String[] deptAry = getDept2(deptId,hostname, programName);
				  			String deptEngNm = deptAry[0]; // 英文代碼
				  			String deptChNm = deptAry[1]; // 中文
				  			ouMap.put("csten",deptEngNm);
				  			ouMap.put("cstnm",deptChNm);	
				  			ouLst.add(ouMap);
			  			}	
		  		}
		  		//if (results.hasMore())
		  		//		result = (SearchResult) results.next();
	  		}   

		} catch(Exception e) {
			System.out.print("System error:" + e.getMessage());
			e.printStackTrace();
			throw new LDAPUtilException(e);
		} 
		return ouLst; 
	}
	/**
     * 查詢使用者卡號
     * @param userId 身份証字號
     * @return 使用者卡號
     * @throws Exception
     */
	public String getUserCardNo(String userId,String hostname,String programName) throws LDAPUtilException {
		methodName = "getUserCardNo";
		//log(methodName, new Date(), hostname, programName);
		String cardNo = "";
		try {
			ctx = getCtx();
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope( SearchControls.SUBTREE_SCOPE );
			NamingEnumeration<?> results;
	
			String[] attrIDs = {"cn"};	//設定欲讀取的屬性
			constraints.setReturningAttributes(attrIDs);
			results = ctx.search(userBaseDN,"carLicense="+userId,constraints);
	
			if (results != null) {
				//int i =0 ;
				while ( results.hasMore() ) {
				//	i++;
				//	Attributes attrs = null;
					SearchResult si = null;
					si = (SearchResult) results.next() ;
					Attributes attrs1 = si.getAttributes();
					if(attrs1.get("cn")!=null && attrs1.get("cn").toString().length()>=4) cardNo = attrs1.get("cn").toString().substring(4);
				}
			}
		} catch(Exception e) {
			System.out.println("Class Name : LDAPUtil , Method Name : getUserCardNo , Error Message : "+e.getMessage());
			e.printStackTrace();
			throw new LDAPUtilException(e);
		} 
		return cardNo;	
	}
	/**
	 * 由 User DN 查詢群組(多筆)
	 * 
	 * @param userRDN
	 *            User DN
	 * @return 多筆結果，只含cn值
	 * @throws LDAPUtilException
	 */
	public boolean checkGroup(String userRDN,String groupName,String hostname,String programName)
			throws LDAPUtilException  {
			methodName = "checkGroup";
			//log(methodName, new Date(), hostname, programName);
			boolean  flg = false;
			try{
				Collection<String> groups  =	findGroupsRDNByUser(userRDN);
				for (Iterator<String> iterator = groups.iterator(); iterator.hasNext();) {
					    String tmpGroupNm = (String) iterator.next();
						//System.out.println("##=========>>卡號："+userRDN+"之群組名稱："+tmpGroupNm );
						if(tmpGroupNm.equals(groupName)) {
							flg = true;break;
						}
				 }
			}catch(Exception e){
				System.out.println("Class Name : LDAPUtil , Method Name : getUserCardNo , Error Message : "+e.getMessage());
				throw new LDAPUtilException(e);
			}
			return flg;
	}

	/**
	 * 依成本中心搜尋醫師
	 * @param cosId
	 * @throws LDAPUtilException
	 */
	public ArrayList<WPSUser> searchDrsByCosId(String cosId) throws LDAPUtilException {
	
		ArrayList<WPSUser> drs = new ArrayList<WPSUser>();
		String filter = new StringBuffer().append("(&(").append("ou").append(
				"=").append(cosId).append(
				")(objectClass=").append("inetOrgPerson").append("))").toString();
		try{
			
			NamingEnumeration<?> enums = findList(userBaseDN, filter);
			while (enums.hasMoreElements()) {
				SearchResult result = (SearchResult) enums.nextElement();
				Attributes attrs1 = result.getAttributes();
				//判斷使用者帳號是否為啟用
				String enable = attrs1.get("idEnable")!=null?attrs1.get("idEnable").toString().substring(10):"";
				if(enable.equals("Y")){
					WPSUser user = new WPSUser();
					String name = result.getAttributes().get("givenName").toString().substring(11);
					String docNo = result.getAttributes().get("doctorNumber")!=null?result.getAttributes().get("doctorNumber").toString().substring(13):"";
					String cardno = result.getAttributes().get("cn")!=null?result.getAttributes().get("cn").toString().substring(4):"";
					
					String payWkTyd = result.getAttributes().get("payWkTyd")!=null?result.getAttributes().get("payWkTyd").toString().substring(10):"";
					user.setLoginId(cardno);
					user.setDocNo(docNo.trim());
					user.setUserNm(name);
					if(user.getDocNo() != null && !user.getDocNo().equals(""))
						//拿掉薪資代碼為護理師級別的
						if(!payWkTyd.matches("23|26|29|34|41|AV|BN|BR|DM|DS|E9"))
							drs.add(user);
				}
			}
			
		}catch(Throwable e){
			e.printStackTrace();
			System.out.println(e.toString());
		}
		return drs;
	}
	
	/**
	 * 依成本中心搜尋一二級主管
	 * @param cosId
	 * @throws LDAPUtilException
	 */
	public ArrayList<WPSUser> searchSeniorsByCosId(String cosId) throws LDAPUtilException {
	
		ArrayList<WPSUser> drs = new ArrayList<WPSUser>();
		String filter = "(&(|(ou=300)(ou=301)(ou=302)(ou=303)(ou=304)(ou=305)(ou=306)(ou=307)(ou=308)(ou=310)(ou=311)" +
				"(ou=400)(ou=410)(ou=440)(ou=450)(ou=460)(ou=480)(ou=481)(ou=482)(ou=500))(|(chieftyp=3)(chieftyp=4))" +
				"(objectClass=inetOrgPerson))";
		if(cosId != null)
			filter = "(&(ou=" + cosId + ")(|(chieftyp=3)(chieftyp=4))(objectClass=inetOrgPerson))";
		try{
			
			NamingEnumeration<?> enums = findList(userBaseDN, filter);
			while (enums.hasMoreElements()) {
				SearchResult result = (SearchResult) enums.nextElement();
				Attributes attrs1 = result.getAttributes();
				//判斷使用者帳號是否為啟用
				String enable = attrs1.get("idEnable")!=null?attrs1.get("idEnable").toString().substring(10):"";
				if(enable.equals("Y")){
					WPSUser user = new WPSUser();
					String name = result.getAttributes().get("givenName").toString().substring(11);
					String docNo = result.getAttributes().get("doctorNumber")!=null?result.getAttributes().get("doctorNumber").toString().substring(13):"";
					String cardno = result.getAttributes().get("cn")!=null?result.getAttributes().get("cn").toString().substring(4):"";
					String chieftyp = result.getAttributes().get("chieftyp").toString().substring(10);
					String costid = result.getAttributes().get("ou").toString().substring(4);
					String payWkTyd = result.getAttributes().get("payWkTyd")!=null?result.getAttributes().get("payWkTyd").toString().substring(10):"";
					String mail = result.getAttributes().get("mail")!=null?result.getAttributes().get("mail").toString().substring(6):"";
					user.setLoginId(cardno);
					user.setDocNo(docNo.trim());
					user.setUserNm(name);
					user.setChieftyp(chieftyp);
					user.setCosId(costid);
					user.setMail(mail);
					//骨科部、婦女醫學部、麻醉部、口腔醫學部、眼科部、耳鼻喉頭頸部與兒醫部只抓一級主管
					//再扣掉謝耀東與莊旺川醫師
					if(user.getDocNo() != null && !user.getDocNo().equals("") && ((costid.matches("305|400|410|440|450|460|500") && chieftyp.equals("3")) || !costid.matches("305|400|410|440|450|460|500")) && !user.getLoginId().matches("3660|3262"))
						//拿掉薪資代碼為護理師級別的
						if(!payWkTyd.matches("23|26|29|34|41|AV|BN|BR|DM|DS|E9"))
							drs.add(user);
				}
			}
			
		}catch(Throwable e){
			e.printStackTrace();
			System.out.println(e.toString());
		}
		return drs;
	}
}