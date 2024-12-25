package gov.vghks.sal.model;

import gov.vghks.common.util.StringUtil;

import java.io.Serializable;

public class Padept implements Serializable{
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 單位代碼*/
    private String padepcod;
    /** 成本中心代碼*/
    private String pacstno;
    /** 單位中文*/
    private String padepnmc;
    /** 更改者*/
    private String payupdid;
    /** 最後更改TIME*/
    private java.sql.Timestamp payupdat;
    /** 醫院別*/
    private String hid;
    /** 日期時戳 */
    private java.sql.Timestamp zpadept;
    
	public String getPadepcod() {
		return padepcod;
	}
	public void setPadepcod(String padepcod) {
		this.padepcod = padepcod;
	}
	public String getPacstno() {
		return pacstno;
	}
	public void setPacstno(String pacstno) {
		this.pacstno = pacstno;
	}
	public String getPadepnmc() {
		return StringUtil.nullToString(padepnmc);
	}
	public void setPadepnmc(String padepnmc) {
		this.padepnmc = padepnmc;
	}
	public String getPayupdid() {
		return payupdid;
	}
	public void setPayupdid(String payupdid) {
		this.payupdid = payupdid;
	}
	public java.sql.Timestamp getPayupdat() {
		return payupdat;
	}
	public void setPayupdat(java.sql.Timestamp payupdat) {
		this.payupdat = payupdat;
	}
	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
	}
	public java.sql.Timestamp getZpadept() {
		return zpadept;
	}
	public void setZpadept(java.sql.Timestamp zpadept) {
		this.zpadept = zpadept;
	}
    
}
