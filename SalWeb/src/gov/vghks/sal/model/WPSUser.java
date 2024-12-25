package gov.vghks.sal.model;

import java.io.Serializable;

public class WPSUser implements Serializable{
	
	/** 序列版本ID */
	private static final long serialVersionUID = 2366284486426627547L;
	/** 登入者卡號 */
	private String loginId;
	/** 登入者姓名 */
	private String userNm;
	/** 登入者單位 */
	private String deptNo;
	/** 登入者單位中文 */
	private String deptName;
	/** 登入者成本中心 */
	private String cosId;
	/** 使用者醫師章號 */
	private String docNo;
	/** 使用者身份證 */
	private String idNo;
	/** 主管級別 1院長 2副院長 X主任秘書 3一級主管 4二級主管 5護理長/副護理長 */
	private String chieftyp;
	/** 薪資職稱代碼 */
	private String payWkTyd;
	/** 電子郵件 */
	private String mail;
	/** LDAP 群組 */
	private String groups;
	/** 醫院代號 */
	private String hid;	
	/**
	 * 取得登入者卡號
	 * @return loginId
	 */
	public String getLoginId() {
		return loginId;
	}
	
	/**
	 * 設定登入者卡號
	 * @param loginId
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	/**
	 * 取得登入者姓名
	 * @return userNm
	 */
	public String getUserNm() {
		return userNm;
	}

	/**
	 * 設定登入者姓名
	 * @param userNm
	 */
	public void setUserNm(String userNm) {
		this.userNm = userNm;
	}

	/**
	 * 取得登入者單位
	 * @return deptNo
	 */
	public String getDeptNo() {
		return deptNo;
	}

	/**
	 * 設定登入者單位
	 * @param deptNo
	 */
	public void setDeptNo(String deptNo) {
		this.deptNo = deptNo;
	}

	/**
	 * 取得登入者單位中文
	 * @return deptName
	 */
	public String getDeptName() {
		return deptName;
	}

	/**
	 * 設定登入者單位中文
	 * @param deptName
	 */
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	/**
	 * 取得登入者成本中心
	 * @return cosId
	 */
	public String getCosId() {
		return cosId;
	}

	/**
	 * 設定登入者成本中心
	 * @param cosId
	 */
	public void setCosId(String cosId) {
		this.cosId = cosId;
	}

	/**
	 * 取得使用者醫師章號
	 * @return docNo
	 */
	public String getDocNo() {
		return docNo;
	}

	/**
	 * 設定使用者醫師章號
	 * @param docNo
	 */
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	/**
	 * 取得使用者身份證
	 * @return idNo
	 */
	public String getIdNo() {
		return idNo;
	}

	/**
	 * 設定使用者身份證
	 * @param idNo
	 */
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	/**
	 * 取得主管級別
	 * @return chieftyp
	 */
	public String getChieftyp() {
		return chieftyp;
	}

	/**
	 * 設定主管級別
	 * @param chieftyp
	 */
	public void setChieftyp(String chieftyp) {
		this.chieftyp = chieftyp;
	}

	/**
	 * 取得薪資職稱代碼
	 * @return payWkTyd
	 */
	public String getPayWkTyd() {
		return payWkTyd;
	}

	/**
	 * 設定薪資職稱代碼
	 * @param payWkTyd
	 */
	public void setPayWkTyd(String payWkTyd) {
		this.payWkTyd = payWkTyd;
	}

	/**
	 * 取得電子郵件
	 * @return mail
	 */
	public String getMail() {
		return mail;
	}

	/**
	 * 設定電子郵件
	 * @param mail
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}

	/**
	 * 取得LDAP 群組
	 * @return groups
	 */
	public String getGroups() {
		return groups;
	}

	/**
	 * 設定LDAP 群組
	 * @param groups
	 */
	public void setGroups(String groups) {
		this.groups = groups;
	}
	public String getHid() {
		return hid;
	}

	public void setHid(String hid) {
		this.hid = hid;
	}
}
