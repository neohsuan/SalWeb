package gov.vghks.sal.model;

import java.io.Serializable;
//所得稅轉帳明細  
public class Pataxtran implements Serializable{
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
    /** 應稅所得PAYTAXPAY*/
    private int paytaxpay;
    /** 已扣所得稅PAYTAXDEDUCT*/
    private int paytaxdeduct;
    /** 更改者PAYUPDID*/
    private String payupdid;
    /** 修改日期時間 PAYUPDTM*/
    private java.sql.Timestamp payupdtm;
    /** 醫院別*/
    private String hid;
    /** 日期時戳 */
    private java.sql.Timestamp zpataxtran;
    /** 日期時戳 */
    private java.sql.Timestamp zlhtaxtran;
    /** 日期時戳 */
    private java.sql.Timestamp zpttaxtran;
    
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
	
	public int getPaytaxdeduct() {
		return paytaxdeduct;
	}
	public void setPaytaxdeduct(int paytaxdeduct) {
		this.paytaxdeduct = paytaxdeduct;
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
	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
	}
	public java.sql.Timestamp getZpataxtran() {
		return zpataxtran;
	}
	public void setZpataxtran(java.sql.Timestamp zpataxtran) {
		this.zpataxtran = zpataxtran;
	}
	public int getPaytaxpay() {
		return paytaxpay;
	}
	public void setPaytaxpay(int paytaxpay) {
		this.paytaxpay = paytaxpay;
	}
	public java.sql.Timestamp getZlhtaxtran() {
		return zlhtaxtran;
	}
	public void setZlhtaxtran(java.sql.Timestamp zlhtaxtran) {
		this.zlhtaxtran = zlhtaxtran;
	}
	public java.sql.Timestamp getZpttaxtran() {
		return zpttaxtran;
	}
	public void setZpttaxtran(java.sql.Timestamp zpttaxtran) {
		this.zpttaxtran = zpttaxtran;
	}
    
    
 }
