package gov.vghks.sal.model;

public class Padatpay {
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 餉單編號*/
    private int salnum;
    /** 身份證字號*/
    private String payidno;
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
    private java.sql.Timestamp zpadatpay;
    /** 支扣代碼 中文*/
    private String pacodnmc;
    
	public int getSalnum() {
		return salnum;
	}
	public void setSalnum(int salnum) {
		this.salnum = salnum;
	}
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
	public java.sql.Timestamp getZpadatpay() {
		return zpadatpay;
	}
	public void setZpadatpay(java.sql.Timestamp zpadatpay) {
		this.zpadatpay = zpadatpay;
	}
	public String getPacodnmc() {
		return pacodnmc;
	}
	public void setPacodnmc(String pacodnmc) {
		this.pacodnmc = pacodnmc;
	}
    
}
