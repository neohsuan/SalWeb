package gov.vghks.sal.util.ldap;

import java.io.Serializable;
import java.util.Map;

/**
 * LDAP 使用者資料(新增用)
 * 
 * @author Chien
 * 
 */
public class LDAPUser implements Serializable {
   
	/** 序列版本ID */
	private static final long serialVersionUID = 2197828967145459706L;
	private String HID;// 醫院別
	private String userId;// 使用者 ID
    private String userPwd; // 使用者密碼
    private String sn; // 使用者姓名
    private String idNo; // 身份証字號
	private String title; // 職稱
	private String ou; //  成本中心代碼
    private String deptDescC; // 組織中文名稱
	private String deptDesc;//組織英文代碼
    private String displayNm; // 顯示名稱
    
    // 其它資訊
    private Map<?, ?> infoMap;

    // ------------------------------------------ Constructors

    /**
     * Constructor
     */
    public LDAPUser() {

    }

    /**
     * Constructor
     * 
     * @param userID
     *        使用者 ID
     * @param userPwd
     *        使用者密碼
     * @param cn
     *        使用者名
     * @param sn
     *        使用者姓
     */
    public LDAPUser(String userID, String userPwd, String sn,String idNo,String title,String ou,String deptDesc,String deptDescC,String displayNm) {
		this.userId = userID;
		this.userPwd = userPwd;
		this.sn = sn;
		this.idNo = idNo;
		this.title = title;
		this.ou = ou;
		this.deptDesc = deptDesc;
		this.deptDescC = deptDescC;
		this.displayNm = displayNm;
    }
    public LDAPUser(String userID, String userPwd, String sn) {
    	this.userId = userID;
    	this.userPwd = userPwd;
    	this.sn = sn;
    	this.idNo = "";
		this.title = "";
		this.ou = "";
		this.deptDesc = "";
		this.deptDescC = "";
		this.displayNm = "";
    }
    // ------------------------------------------ Properties

  

    /**
     * get 其它資訊
     * 
     * @return the infos
     */
    public Map<?, ?> getInfoMap() {
	return infoMap;
    }

    /**
     * set 其它資訊
     * 
     * @param infos
     *        the infos to set
     */
    public void setInfoMap(Map<?, ?> infos) {
	this.infoMap = infos;
    }

    /**
     * get 使用者姓
     * 
     * @return the sn
     */
    public String getSn() {
	return sn;
    }

    /**
     * set 使用者姓
     * 
     * @param sn
     *        the sn to set
     */
    public void setSn(String sn) {
	this.sn = sn;
    }

    /**
     * get 使用者 ID
     * 
     * @return the userID
     */
    public String getUserId() {
	return userId;
    }

    /**
     * set 使用者 ID
     * 
     * @param userID
     *        the userID to set
     */
    public void setUserId(String userID) {
	this.userId = userID;
    }

    /**
     * get 使用者密碼
     * 
     * @return the userPwd
     */
    public String getUserPwd() {
	return userPwd;
    }

    /**
     * set 使用者密碼
     * 
     * @param userPwd
     *        the userPwd to set
     */
    public void setUserPwd(String userPwd) {
	this.userPwd = userPwd;
    }

	public String getDeptDesc() {
		return deptDesc;
	}

	public void setDeptDesc(String deptDesc) {
		this.deptDesc = deptDesc;
	}

	public String getDeptDescC() {
		return deptDescC;
	}

	public void setDeptDescC(String deptDescC) {
		this.deptDescC = deptDescC;
	}

	public String getDisplayNm() {
		return displayNm;
	}

	public void setDisplayNm(String displayNm) {
		this.displayNm = displayNm;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getOu() {
		return ou;
	}

	public void setOu(String ou) {
		this.ou = ou;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHID() {
		return HID;
	}

	public void setHID(String hID) {
		HID = hID;
	}

}
