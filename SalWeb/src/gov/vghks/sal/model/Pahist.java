package gov.vghks.sal.model;

public class Pahist {
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	 /** 異動日期*/
    private java.sql.Date patradt;
	/** 異動代碼*/
    private int patracod;
    /** 身份證字號*/
    private String payidno;
    /** 卡號*/
    private String paycard;
    /** 職務編號*/
    private String payempno;
    /** 姓名*/
    private String paynamec;
    /** 部門代碼*/
    private String paydept;
    /** 職稱代碼*/
    private String paytitle;
    /** 職等01-15,P1-P3,T1*/
    private String payrnk2n;
    /** 本功1/2*/
    private int payrank3;
    /** 級別*/
    private int paydegr2;
    /** 俸點*/
    private int paydegre;
    /** 階別,1-9,A-F*/
    private String payrank;
    /** 權理職等*/
    private String payrank2;
    /** 主管(權理)*/
    private String payrankc;
    /** 到院日期*/
    private java.sql.Date payempdt;
    /** 離職日期*/
    private java.sql.Date payrsgdt;
    /** 最後更改ID*/
    private String payupdid;
    /** 最後更改TIME*/
    private java.sql.Timestamp payupdtm;
    /** 醫院別*/
    private String hid;
    /** 日期時戳 */
    private java.sql.Timestamp zpahist;
    /** 日期時戳 */
    private java.sql.Timestamp zpthist;
    /** 日期時戳 */
    private java.sql.Timestamp zlhhist;
    
	public java.sql.Date getPatradt() {
		return patradt;
	}
	public void setPatradt(java.sql.Date patradt) {
		this.patradt = patradt;
	}
	public int getPatracod() {
		return patracod;
	}
	public void setPatracod(int patracod) {
		this.patracod = patracod;
	}
	public String getPayidno() {
		return payidno;
	}
	public void setPayidno(String payidno) {
		this.payidno = payidno;
	}
	public String getPaycard() {
		return paycard;
	}
	public void setPaycard(String paycard) {
		this.paycard = paycard;
	}
	public String getPayempno() {
		return payempno;
	}
	public void setPayempno(String payempno) {
		this.payempno = payempno;
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
	public String getPaytitle() {
		return paytitle;
	}
	public void setPaytitle(String paytitle) {
		this.paytitle = paytitle;
	}
	public String getPayrnk2n() {
		return payrnk2n;
	}
	public void setPayrnk2n(String payrnk2n) {
		this.payrnk2n = payrnk2n;
	}
	public int getPayrank3() {
		return payrank3;
	}
	public void setPayrank3(int payrank3) {
		this.payrank3 = payrank3;
	}
	public int getPaydegr2() {
		return paydegr2;
	}
	public void setPaydegr2(int paydegr2) {
		this.paydegr2 = paydegr2;
	}
	public int getPaydegre() {
		return paydegre;
	}
	public void setPaydegre(int paydegre) {
		this.paydegre = paydegre;
	}
	public String getPayrank() {
		return payrank;
	}
	public void setPayrank(String payrank) {
		this.payrank = payrank;
	}
	public String getPayrank2() {
		return payrank2;
	}
	public void setPayrank2(String payrank2) {
		this.payrank2 = payrank2;
	}
	public String getPayrankc() {
		return payrankc;
	}
	public void setPayrankc(String payrankc) {
		this.payrankc = payrankc;
	}
	public java.sql.Date getPayempdt() {
		return payempdt;
	}
	public void setPayempdt(java.sql.Date payempdt) {
		this.payempdt = payempdt;
	}
	public java.sql.Date getPayrsgdt() {
		return payrsgdt;
	}
	public void setPayrsgdt(java.sql.Date payrsgdt) {
		this.payrsgdt = payrsgdt;
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
	public java.sql.Timestamp getZpahist() {
		return zpahist;
	}
	public void setZpahist(java.sql.Timestamp zpahist) {
		this.zpahist = zpahist;
	}
	public java.sql.Timestamp getZpthist() {
		return zpthist;
	}
	public void setZpthist(java.sql.Timestamp zpthist) {
		this.zpthist = zpthist;
	}
	public java.sql.Timestamp getZlhhist() {
		return zlhhist;
	}
	public void setZlhhist(java.sql.Timestamp zlhhist) {
		this.zlhhist = zlhhist;
	}
}
