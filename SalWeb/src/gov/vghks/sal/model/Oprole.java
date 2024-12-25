package gov.vghks.sal.model;

import java.io.Serializable;

public class Oprole implements Serializable{
	/** 序列版本ID */
	private static final long serialVersionUID = 2366284486426627547L;
	/** 卡號*/
    private String userid;
    /** 姓名*/
    private String username;
    /** 密碼*/
    private String password;
    /** 成本中心代碼*/
    private String dept;
    /** 權限*/
    private String authority;
    /** 更改者*/
    private String updid;
    /** 日期時戳 */
    private java.sql.Timestamp zoprole;
    
    
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	public java.sql.Timestamp getZoprole() {
		return zoprole;
	}
	public void setZoprole(java.sql.Timestamp zoprole) {
		this.zoprole = zoprole;
	}
	public String getUpdid() {
		return updid;
	}
	public void setUpdid(String updid) {
		this.updid = updid;
	}
}
