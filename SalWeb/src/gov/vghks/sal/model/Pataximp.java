package gov.vghks.sal.model;

import gov.vghks.common.util.StringUtil;

import java.io.Serializable;

public class Pataximp implements Serializable{
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 年*/
    private int payear;
    /** 月*/
    private int paymonth;
    /** 卡號*/
    private String paycard;
    /** 身分證號*/
    private String payidno;
    /** 轉帳項目*/
    private String catgnm;
    /** 轉帳項目代碼*/
    private String catgcd;
    /** 所得格式*/
    private String payformat;
    /** 傳票製票號*/
    private String paycitation;
    /** 應稅所得PAYTAXPAY*/
    private int paytaxpay;
    /** 已扣所得稅PAYTAXDEDUCT*/
    private int paytaxdeduct;
    /** 姓名*/
    private String paynamec;
    /** 所得開始日期*/
    private String paybegin;
    /** 所得結束日期*/
    private String payend;
    /** 戶籍地址*/
    private String payaddrs;
    
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
	public String getPaycard() {
		return paycard;
	}
	public void setPaycard(String paycard) {
		this.paycard = paycard;
	}
	public String getPayidno() {
		return payidno;
	}
	public void setPayidno(String payidno) {
		this.payidno = payidno;
	}
	public String getCatgnm() {
		return catgnm;
	}
	public void setCatgnm(String catgnm) {
		this.catgnm = catgnm;
	}
	public String getCatgcd() {
		return catgcd;
	}
	public void setCatgcd(String catgcd) {
		this.catgcd = catgcd;
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
	public int getPaytaxdeduct() {
		return paytaxdeduct;
	}
	public void setPaytaxdeduct(int paytaxdeduct) {
		this.paytaxdeduct = paytaxdeduct;
	}
	public String getPaynamec() {
		return paynamec;
	}
	public void setPaynamec(String paynamec) {
		this.paynamec = paynamec;
	}
	
	public String getPayaddrs() {
		return payaddrs;
	}
	public void setPayaddrs(String payaddrs) {
		this.payaddrs = payaddrs;
	}
	public String getPaybegin() {
		return paybegin;
	}
	public void setPaybegin(String paybegin) {
		this.paybegin = paybegin;
	}
	public String getPayend() {
		return payend;
	}
	public void setPayend(String payend) {
		this.payend = payend;
	}
    
    }
