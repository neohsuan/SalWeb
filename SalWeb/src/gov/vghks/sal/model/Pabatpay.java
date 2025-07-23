package gov.vghks.sal.model;

public class Pabatpay {
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 異動日期*/
    private java.sql.Date patradt;
	/** 身份證字號*/
    private String payidno;
    /** 餉單編號*/
    private int salnum;
    /** 卡號*/
    private String paycard;
    /** 支扣代碼*/
    private String paycode;
    /** Y更新至主檔 N每月歸零*/
    private String paupbas;
    /** 金額*/
    private int payamt;
    /** 輸入ID*/
    private String padutyid;
    /** 最後更改TIME*/
    private java.sql.Timestamp payupdat;
    /** 處理旗標*/
    private String payflag;
    /** 資料過帳至主檔*/
    private String upmastdt;
    /** 醫院別*/
    private String hid;
    /** 支扣代碼 中文*/
    private String pacodnmc;
    /** 授權碼*/
    private String payauth;
    /** 是否列入應稅所得*/
    private String payiftax;
    
	public java.sql.Date getPatradt() {
		return patradt;
	}
	public void setPatradt(java.sql.Date patradt) {
		this.patradt = patradt;
	}
	public String getPayidno() {
		return payidno;
	}
	public void setPayidno(String payidno) {
		this.payidno = payidno;
	}
	public int getSalnum() {
		return salnum;
	}
	public void setSalnum(int salnum) {
		this.salnum = salnum;
	}
	public String getPaycard() {
		return paycard;
	}
	public void setPaycard(String paycard) {
		this.paycard = paycard;
	}
	public String getPaycode() {
		return paycode;
	}
	public void setPaycode(String paycode) {
		this.paycode = paycode;
	}
	public String getPaupbas() {
		return paupbas;
	}
	public void setPaupbas(String paupbas) {
		this.paupbas = paupbas;
	}
	public int getPayamt() {
		return payamt;
	}
	public void setPayamt(int payamt) {
		this.payamt = payamt;
	}
	public String getPadutyid() {
		return padutyid;
	}
	public void setPadutyid(String padutyid) {
		this.padutyid = padutyid;
	}
	public java.sql.Timestamp getPayupdat() {
		return payupdat;
	}
	public void setPayupdat(java.sql.Timestamp payupdat) {
		this.payupdat = payupdat;
	}
	public String getPayflag() {
		return payflag;
	}
	public void setPayflag(String payflag) {
		this.payflag = payflag;
	}
	public String getUpmastdt() {
		return upmastdt;
	}
	public void setUpmastdt(String upmastdt) {
		this.upmastdt = upmastdt;
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
    
	    
}
