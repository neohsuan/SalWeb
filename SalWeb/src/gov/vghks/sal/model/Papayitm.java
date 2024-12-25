package gov.vghks.sal.model;

import java.io.Serializable;
//支扣代碼 
public class Papayitm implements Serializable{
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 代碼*/
    private String paycode;
    /** 中文說明*/
    private String pacodnmc;
    /** 刪除否*/
    private String pacoddel;
    /** 授權碼*/
    private String payauth;
    /** 是否列入所得*/
    private String payiftax;
    /** 是否列入寫入基本檔*/
    private String paupbas;
    /** 是否入所得稅應計收入*/
    private String payreward;;
    /** 更改者*/
    private String payupdid;
    /** 最後更改TIME*/
    private java.sql.Timestamp payupdat;
    /** 醫院別*/
    private String hid;
    /** 日期時戳 */
    private java.sql.Timestamp zpapayitm;
    
	public String getPaycode() {
		return paycode;
	}
	public void setPaycode(String paycode) {
		this.paycode = paycode;
	}
	public String getPacodnmc() {
		return pacodnmc;
	}
	public void setPacodnmc(String pacodnmc) {
		this.pacodnmc = pacodnmc;
	}
	public String getPacoddel() {
		return pacoddel;
	}
	public void setPacoddel(String pacoddel) {
		this.pacoddel = pacoddel;
	}
	public String getPayauth() {
		return payauth;
	}
	public void setPayauth(String payauth) {
		this.payauth = payauth;
	}
	public String getPayiftax() {
		return payiftax;
	}
	public void setPayiftax(String payiftax) {
		this.payiftax = payiftax;
	}
	public String getPaupbas() {
		return paupbas;
	}
	public void setPaupbas(String paupbas) {
		this.paupbas = paupbas;
	}
	public String getPayreward() {
		return payreward;
	}
	public void setPayreward(String payreward) {
		this.payreward = payreward;
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
	public java.sql.Timestamp getZpapayitm() {
		return zpapayitm;
	}
	public void setZpapayitm(java.sql.Timestamp zpapayitm) {
		this.zpapayitm = zpapayitm;
	}
	
	
    }
