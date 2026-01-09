package gov.vghks.sal.model;

import gov.vghks.common.util.StringUtil;

import java.io.Serializable;
//所得稅現金明細 
public class Pataxcash implements Serializable{
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 身分證字號*/
    private String payidno;
    /** 西元年*/
    private int payear;
    /** 月份PAYMONTH*/
    private int paymonth;
    /** 轉帳項目PAYCATG*/
    private int paycatg;
    /** 格式--50薪資 9A執行業務所得PAYFORMAT*/
    private String payformat;
    /** 傳票製票號PAYCITATION*/
    private String paycitation;
    /** 應稅所得PAYTAXPAY*/
    private int paytaxpay;
    /** 已扣所得稅PAYTAXDEDUCT*/
    private int paytaxdeduct;
    /** 更改者PAYUPDID*/
    private String payupdid;
    /** 修改日期時間 PAYUPDTM*/
    private java.sql.Timestamp payupdtm;
    /** 處理旗標PAYFLAG*/
    private String payflag;
    /** 醫院別*/
    private String hid;
    /** 日期時戳 */
    private java.sql.Timestamp zpataxcash;
    /** 日期時戳 */
    private java.sql.Timestamp zpttaxcash;
    /** 日期時戳 */
    private java.sql.Timestamp zlhtaxcash;
    
	public String getPayidno() {
		return payidno;
	}
	public void setPayidno(String payidno) {
		this.payidno = payidno;
	}
	public int getPayear() {
		return payear;
	}
	public void setPayear(int payear) {
		this.payear = payear;
	}
	public int getPaymonth() {
		return paymonth;
	}
	public void setPaymonth(int paymonth) {
		this.paymonth = paymonth;
	}
	public int getPaycatg() {
		return paycatg;
	}
	public void setPaycatg(int paycatg) {
		this.paycatg = paycatg;
	}
	public String getPayformat() {
		return payformat;
	}
	public void setPayformat(String payformat) {
		this.payformat = payformat;
	}
	public String getPaycitation() {
		return paycitation;
	}
	public void setPaycitation(String paycitation) {
		this.paycitation = paycitation;
	}
	public int getPaytaxpay() {
		return paytaxpay;
	}
	public void setPaytaxpay(int paytaxpay) {
		this.paytaxpay = paytaxpay;
	}
	public String getPayupdid() {
		return payupdid;
	}
	public void setPayupdid(String payupdid) {
		this.payupdid = payupdid;
	}
	public java.sql.Timestamp getPayupdtm() {
		return payupdtm;
	}
	public void setPayupdtm(java.sql.Timestamp payupdtm) {
		this.payupdtm = payupdtm;
	}
	public String getPayflag() {
		return payflag;
	}
	public void setPayflag(String payflag) {
		this.payflag = payflag;
	}
	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
	}
	public java.sql.Timestamp getZpataxcash() {
		return zpataxcash;
	}
	public void setZpataxcash(java.sql.Timestamp zpataxcash) {
		this.zpataxcash = zpataxcash;
	}
	public int getPaytaxdeduct() {
		return paytaxdeduct;
	}
	public void setPaytaxdeduct(int paytaxdeduct) {
		this.paytaxdeduct = paytaxdeduct;
	}
	public java.sql.Timestamp getZpttaxcash() {
		return zpttaxcash;
	}
	public void setZpttaxcash(java.sql.Timestamp zpttaxcash) {
		this.zpttaxcash = zpttaxcash;
	}
	public java.sql.Timestamp getZlhtaxcash() {
		return zlhtaxcash;
	}
	public void setZlhtaxcash(java.sql.Timestamp zlhtaxcash) {
		this.zlhtaxcash = zlhtaxcash;
	}
    
	
 }
