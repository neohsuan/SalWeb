package gov.vghks.sal.model;

public class Patrapay {
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 和異動支扣名細關聯*/
    private int tranum;
	/** 身份證字號*/
    private String payidno;
    /** 異動日期*/
    private java.sql.Date patradt;
    /** 原O/新N */
    private String paysort;
    /** 支扣代碼*/
    private String paycode;
    /** 金額*/
    private int payamt;
    /** 授權碼*/
    private String payauth;
    /** 是否列入所得*/
    private String payiftax;
    /** 最後更改TIME*/
    private java.sql.Timestamp payupdtm;
    /** 處理旗標*/
    private String payflag;
    /** 醫院別*/
    private String hid;
    /** 日期時戳 */
    private java.sql.Timestamp zpatrapay;
    /** 日期時戳 */
    private java.sql.Timestamp zpttrapay;
    /** 日期時戳 */
    private java.sql.Timestamp zlhtrapay;
    /** 支扣代碼 中文*/
    private String pacodnmc;
    /** 支扣代碼_刪除*/
    private String pacoddel;
    /** 是否入所得稅應計收入*/
    private String payreward;
    /** 卡號*/
    private String paycard;
    
	public String getPayidno() {
		return payidno;
	}
	public void setPayidno(String payidno) {
		this.payidno = payidno;
	}
	public String getPaycode() {
		return paycode;
	}
	public void setPaycode(String paycode) {
		this.paycode = paycode;
	}
	public int getPayamt() {
		return payamt;
	}
	public void setPayamt(int payamt) {
		this.payamt = payamt;
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
	public String getPacodnmc() {
		return pacodnmc;
	}
	public void setPacodnmc(String pacodnmc) {
		this.pacodnmc = pacodnmc;
	}
	public int getTranum() {
		return tranum;
	}
	public void setTranum(int tranum) {
		this.tranum = tranum;
	}
	public java.sql.Date getPatradt() {
		return patradt;
	}
	public void setPatradt(java.sql.Date patradt) {
		this.patradt = patradt;
	}
	public String getPaysort() {
		return paysort;
	}
	public void setPaysort(String paysort) {
		this.paysort = paysort;
	}
	public String getPayflag() {
		return payflag;
	}
	public void setPayflag(String payflag) {
		this.payflag = payflag;
	}
	public java.sql.Timestamp getZpatrapay() {
		return zpatrapay;
	}
	public void setZpatrapay(java.sql.Timestamp zpatrapay) {
		this.zpatrapay = zpatrapay;
	}
	public String getPacoddel() {
		return pacoddel;
	}
	public void setPacoddel(String pacoddel) {
		this.pacoddel = pacoddel;
	}
	public String getPayreward() {
		return payreward;
	}
	public void setPayreward(String payreward) {
		this.payreward = payreward;
	}
	public String getPaycard() {
		return paycard;
	}
	public void setPaycard(String paycard) {
		this.paycard = paycard;
	}
	
    
}
