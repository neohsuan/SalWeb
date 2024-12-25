package gov.vghks.sal.model;

import gov.vghks.common.util.StringUtil;

import java.io.Serializable;

public class Patitle implements Serializable{
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 代碼*/
    private String paytitlk;
    /** 群組分類*/
    private String patitsub;
    /** 主管否*/
    private String pachief;
    /** 報表用*/
    private String paytitlp;
    /** 內部用*/
    private String paytitlc;
    /** 刪除否*/
    private String pacoddel;
    /** 更改者*/
    private String payupdid;
    /** 最後更改TIME*/
    private java.sql.Timestamp payupdat;
    /** 醫院別*/
    private String hid;
    /** 日期時戳 */
    private java.sql.Timestamp zpatitle;
    
        
	public String getPaytitlk() {
		return paytitlk;
	}
	public void setPaytitlk(String paytitlk) {
		this.paytitlk = paytitlk;
	}
	public String getPatitsub() {
		return patitsub;
	}
	public void setPatitsub(String patitsub) {
		this.patitsub = patitsub;
	}
	public String getPachief() {
		return pachief;
	}
	public void setPachief(String pachief) {
		this.pachief = pachief;
	}
	public String getPaytitlp() {
		return StringUtil.nullToString(paytitlp);
	}
	public void setPaytitlp(String paytitlp) {
		this.paytitlp = paytitlp;
	}
	public String getPaytitlc() {
		return StringUtil.nullToString(paytitlc);
	}
	public void setPaytitlc(String paytitlc) {
		this.paytitlc = paytitlc;
	}
	public String getPacoddel() {
		return pacoddel;
	}
	public void setPacoddel(String pacoddel) {
		this.pacoddel = pacoddel;
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
	public java.sql.Timestamp getZpatitle() {
		return zpatitle;
	}
	public void setZpatitle(java.sql.Timestamp zpatitle) {
		this.zpatitle = zpatitle;
	}
    
	    }
