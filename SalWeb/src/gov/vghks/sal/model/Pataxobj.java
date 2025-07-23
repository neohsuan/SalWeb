package gov.vghks.sal.model;

import gov.vghks.common.util.StringUtil;

import java.io.Serializable;
//所得稅個人明細+轉帳/現金所得稅 
public class Pataxobj implements Serializable{
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
    /** 所得開始日期PAYBEGIN*/
    private java.sql.Date paybegin;
    /** 所得結束日期PAYEND*/
    private java.sql.Date payend;
    /** 流水號PAYSERNO*/
    private int payserno;
    /** 更改者PAYUPDID*/
    private String payupdid;
    /** 最後更改TIME PAYUPDTM*/
    private java.sql.Timestamp payupdtm;
    /** 流水號PAYSERNO9A*/
    private int payserno9a;
    /** 流水號PAYSERNO9B*/
    private int payserno9b;
    /** 自繳退休金PAYSEREAMT*/
    private int payseramt;
    /** 流水號PAYSERNO91*/
    private int payserno91;
    /** 流水號PAYSERNO92*/
    private int payserno92;
    /** 醫院別*/
    private String hid;
     /** 職號PAYEMPNO*/
    private int payempno;
    /** 流水號PAYSERNO51*/
    private int payserno51;
    
    /** 月份PAYMONTH*/
    private int paymonth;
    /** 轉帳項目PAYCATG*/
    private int paycatg;
    /** 應稅所得PAYTAXPAY*/
    private int paytaxpay;
    /** 已扣所得稅PAYTAXDEDUCT*/ 
    private int paytaxdeduct;
    
    /** 格式--50薪資 9A執行業務所得PAYFORMAT*/
    private String payformat;
    /** 傳票製票號PAYCITATION*/
    private String paycitation;
    
    
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
	public int getPayseramt() {
		return payseramt;
	}
	public void setPayseramt(int payseramt) {
		this.payseramt = payseramt;
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
	public int getPayempno() {
		return payempno;
	}
	public void setPayempno(int payempno) {
		this.payempno = payempno;
	}
	public int getPayserno51() {
		return payserno51;
	}
	public void setPayserno51(int payserno51) {
		this.payserno51 = payserno51;
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
	public int getPaytaxpay() {
		return paytaxpay;
	}
	public void setPaytaxpay(int paytaxpay) {
		this.paytaxpay = paytaxpay;
	}
	public int getPaytaxdeduct() {
		return paytaxdeduct;
	}
	public void setPaytaxdeduct(int paytaxdeduct) {
		this.paytaxdeduct = paytaxdeduct;
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
    
 }
