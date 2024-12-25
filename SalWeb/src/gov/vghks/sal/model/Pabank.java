package gov.vghks.sal.model;

public class Pabank {
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 餉單編號*/
    private int salnum;
    /** 發薪日期*/
    private java.sql.Date paybank;
    /** 入帳日期*/
    private java.sql.Date paysaldt;
    /** 負責人ID*/
    private String payupdid;
    /** 最後更改TIME*/
    private java.sql.Timestamp payupdat;
    /** 日期時戳 */
    private java.sql.Timestamp zpabank;
    /** 醫院別*/
    private String hid;
    
	
	public java.sql.Date getPaybank() {
		return paybank;
	}
	public void setPaybank(java.sql.Date paybank) {
		this.paybank = paybank;
	}
	public java.sql.Date getPaysaldt() {
		return paysaldt;
	}
	public void setPaysaldt(java.sql.Date paysaldt) {
		this.paysaldt = paysaldt;
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
	public java.sql.Timestamp getZpabank() {
		return zpabank;
	}
	public void setZpabank(java.sql.Timestamp zpabank) {
		this.zpabank = zpabank;
	}
	public int getSalnum() {
		return salnum;
	}
	public void setSalnum(int salnum) {
		this.salnum = salnum;
	}
	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
	}
}

