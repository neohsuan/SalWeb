package gov.vghks.sal.model;

public class Pacspf {
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** key*/
    private String pacskey;
    /** 基本積分*/
    private int pacsbase;
    /** 俸級積分*/
    private int pacsdata;
    /** 職稱*/
    private String pacsname;
    /** 負責人ID*/
    private String payupdid;
    /** 最後更改TIME*/
    private java.sql.Timestamp payupdat;
    /** 日期時戳 */
    private java.sql.Timestamp zpacspf;
    /** 日期時戳 */
    private java.sql.Timestamp zptcspf;
    
	public String getPacskey() {
		return pacskey;
	}
	public void setPacskey(String pacskey) {
		this.pacskey = pacskey;
	}
	
	public String getPacsname() {
		return pacsname;
	}
	public void setPacsname(String pacsname) {
		this.pacsname = pacsname;
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
	public java.sql.Timestamp getZpacspf() {
		return zpacspf;
	}
	public void setZpacspf(java.sql.Timestamp zpacspf) {
		this.zpacspf = zpacspf;
	}
	public int getPacsbase() {
		return pacsbase;
	}
	public void setPacsbase(int pacsbase) {
		this.pacsbase = pacsbase;
	}
	public int getPacsdata() {
		return pacsdata;
	}
	public void setPacsdata(int pacsdata) {
		this.pacsdata = pacsdata;
	}
	public java.sql.Timestamp getZptcspf() {
		return zptcspf;
	}
	public void setZptcspf(java.sql.Timestamp zptcspf) {
		this.zptcspf = zptcspf;
	}
}
