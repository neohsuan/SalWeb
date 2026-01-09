package gov.vghks.sal.model;

public class Pacatgtbl {
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 轉帳項目代碼*/
    private int paycatg;
    /** 轉帳項目中文*/
    private String catgname;
    /** 負責人ID*/
    private String payupdid;
    /** 最後更改TIME*/
    private java.sql.Timestamp payupdat;
    /** 日期時戳 */
    private java.sql.Timestamp zpacatgtbl;
    /** 日期時戳 */
    private java.sql.Timestamp zptcatgtbl;
    /** 日期時戳 */
    private java.sql.Timestamp zlhcatgtbl;
	
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
	public java.sql.Timestamp getZpacatgtbl() {
		return zpacatgtbl;
	}
	public void setZpacatgtbl(java.sql.Timestamp zpacatgtbl) {
		this.zpacatgtbl = zpacatgtbl;
	}
	public int getPaycatg() {
		return paycatg;
	}
	public void setPaycatg(int paycatg) {
		this.paycatg = paycatg;
	}
	public String getCatgname() {
		return catgname;
	}
	public void setCatgname(String catgname) {
		this.catgname = catgname;
	}
    
	
}
