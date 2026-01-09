package gov.vghks.sal.model;

import gov.vghks.common.util.StringUtil;

import java.io.Serializable;
//所得稅個人明細 
public class Pataxpsn implements Serializable{
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 身分證字號*/
    private String payidno;
    /** 西元年*/
    private int payear;
    /** 卡號*/
    private String paycard;
    /** 姓名*/
    private String paynamec;
    /** 單位*/
    private String paydept;
    /** 身分證檢查碼註記--F外籍 E有誤*/
    private String payidflg;
    /** 所得開始日期*/
    private java.sql.Date paybegin;
    /** 所得結束日期*/
    private java.sql.Date payend;
    /** 流水號*/
    private int payserno;
    /** 更改者*/
    private String payupdid;
    /** 最後更改TIME*/
    private java.sql.Timestamp payupdtm;
    /** 流水號9A*/
    private int payserno9a;
    /** 流水號9B*/
    private int payserno9b;
    /** 自繳退休金*/
    private int paysereamt;
    /** 流水號91*/
    private int payserno91;
    /** 流水號92*/
    private int payserno92;
    /** 醫院別*/
    private String hid;
    /** 日期時戳 */
    private java.sql.Timestamp zpataxpsn;
    /** 日期時戳 */
    private java.sql.Timestamp zpttaxpsn;
    /** 日期時戳 */
    private java.sql.Timestamp zlhtaxpsn;
    /** 職號*/
    private String payempno;
    /** 流水號51*/
    private int payserno51;
    /** 戶籍地址*/
    private String payaddrs;
    /** 部門中文*/
    private String padepnmc;
    
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
	public String getPaycard() {
		return paycard;
	}
	public void setPaycard(String paycard) {
		this.paycard = paycard;
	}
	public String getPaynamec() {
		return paynamec;
	}
	public void setPaynamec(String paynamec) {
		this.paynamec = paynamec;
	}
	public String getPaydept() {
		return paydept;
	}
	public void setPaydept(String paydept) {
		this.paydept = paydept;
	}
	public String getPayidflg() {
		return payidflg;
	}
	public void setPayidflg(String payidflg) {
		this.payidflg = payidflg;
	}
	public java.sql.Date getPaybegin() {
		return paybegin;
	}
	public void setPaybegin(java.sql.Date paybegin) {
		this.paybegin = paybegin;
	}
	public java.sql.Date getPayend() {
		return payend;
	}
	public void setPayend(java.sql.Date payend) {
		this.payend = payend;
	}
	public int getPayserno() {
		return payserno;
	}
	public void setPayserno(int payserno) {
		this.payserno = payserno;
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
	public int getPayserno9a() {
		return payserno9a;
	}
	public void setPayserno9a(int payserno9a) {
		this.payserno9a = payserno9a;
	}
	public int getPayserno9b() {
		return payserno9b;
	}
	public void setPayserno9b(int payserno9b) {
		this.payserno9b = payserno9b;
	}
	public int getPaysereamt() {
		return paysereamt;
	}
	public void setPaysereamt(int paysereamt) {
		this.paysereamt = paysereamt;
	}
	public int getPayserno91() {
		return payserno91;
	}
	public void setPayserno91(int payserno91) {
		this.payserno91 = payserno91;
	}
	public int getPayserno92() {
		return payserno92;
	}
	public void setPayserno92(int payserno92) {
		this.payserno92 = payserno92;
	}
	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
	}
	public java.sql.Timestamp getZpataxpsn() {
		return zpataxpsn;
	}
	public void setZpataxpsn(java.sql.Timestamp zpataxpsn) {
		this.zpataxpsn = zpataxpsn;
	}
	public int getPayserno51() {
		return payserno51;
	}
	public void setPayserno51(int payserno51) {
		this.payserno51 = payserno51;
	}
	public String getPayaddrs() {
		return payaddrs;
	}
	public void setPayaddrs(String payaddrs) {
		this.payaddrs = payaddrs;
	}
	public String getPadepnmc() {
		return padepnmc;
	}
	public void setPadepnmc(String padepnmc) {
		this.padepnmc = padepnmc;
	}
	public String getPayempno() {
		return payempno;
	}
	public void setPayempno(String payempno) {
		this.payempno = payempno;
	}
	public java.sql.Timestamp getZpttaxpsn() {
		return zpttaxpsn;
	}
	public void setZpttaxpsn(java.sql.Timestamp zpttaxpsn) {
		this.zpttaxpsn = zpttaxpsn;
	}
	public java.sql.Timestamp getZlhtaxpsn() {
		return zlhtaxpsn;
	}
	public void setZlhtaxpsn(java.sql.Timestamp zlhtaxpsn) {
		this.zlhtaxpsn = zlhtaxpsn;
	}
    
 }
