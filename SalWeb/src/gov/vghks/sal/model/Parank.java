package gov.vghks.sal.model;

import java.io.Serializable;
//階級支扣 
public class Parank implements Serializable{
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 級別*/
    private String paylev1v;
    /** 俸級*/
    private String pay2ev1v;
    /** 俸點*/
    private int paydegrv;
    /** 階級*/
    private String payrankv;
    /** 本薪*/
    private int paybasev;
    /** 公保費*/
    private int paygeinf;
    /** 勞保費*/
    private int paylbinf;
    /** 健保費*/
    private int paydpinv;
    /** 新制公保費*/
    private int paygeinn;
    /** 退撫基金/離職金*/
    private int payrebef;
    /** 修改人員*/
    private String payuseid;
    /** 醫院別*/
    private String hid;
	/** 新制退撫離職金*/
    private int payreben;
    /** 更改日期 */
    private java.sql.Timestamp payupdat;
    /** 日期時戳 */
    private java.sql.Timestamp zparank;
    /** 日期時戳 */
    private java.sql.Timestamp zptrank;
    /** 日期時戳 */
    private java.sql.Timestamp zlhrank;
    
	public String getPaylev1v() {
		return paylev1v;
	}
	public void setPaylev1v(String paylev1v) {
		this.paylev1v = paylev1v;
	}
	public String getPay2ev1v() {
		return pay2ev1v;
	}
	public void setPay2ev1v(String pay2ev1v) {
		this.pay2ev1v = pay2ev1v;
	}
	public int getPaydegrv() {
		return paydegrv;
	}
	public void setPaydegrv(int paydegrv) {
		this.paydegrv = paydegrv;
	}
	public String getPayrankv() {
		return payrankv;
	}
	public void setPayrankv(String payrankv) {
		this.payrankv = payrankv;
	}
	public int getPaybasev() {
		return paybasev;
	}
	public void setPaybasev(int paybasev) {
		this.paybasev = paybasev;
	}
	public int getPaygeinf() {
		return paygeinf;
	}
	public void setPaygeinf(int paygeinf) {
		this.paygeinf = paygeinf;
	}
	public int getPaylbinf() {
		return paylbinf;
	}
	public void setPaylbinf(int paylbinf) {
		this.paylbinf = paylbinf;
	}
	public int getPaydpinv() {
		return paydpinv;
	}
	public void setPaydpinv(int paydpinv) {
		this.paydpinv = paydpinv;
	}
	public int getPaygeinn() {
		return paygeinn;
	}
	public void setPaygeinn(int paygeinn) {
		this.paygeinn = paygeinn;
	}
	public int getPayrebef() {
		return payrebef;
	}
	public void setPayrebef(int payrebef) {
		this.payrebef = payrebef;
	}
	public String getPayuseid() {
		return payuseid;
	}
	public void setPayuseid(String payuseid) {
		this.payuseid = payuseid;
	}
	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
	}
	public int getPayreben() {
		return payreben;
	}
	public void setPayreben(int payreben) {
		this.payreben = payreben;
	}
	public java.sql.Timestamp getPayupdat() {
		return payupdat;
	}
	public void setPayupdat(java.sql.Timestamp payupdat) {
		this.payupdat = payupdat;
	}
	public java.sql.Timestamp getZparank() {
		return zparank;
	}
	public void setZparank(java.sql.Timestamp zparank) {
		this.zparank = zparank;
	}
	public java.sql.Timestamp getZptrank() {
		return zptrank;
	}
	public void setZptrank(java.sql.Timestamp zptrank) {
		this.zptrank = zptrank;
	}
	public java.sql.Timestamp getZlhrank() {
		return zlhrank;
	}
	public void setZlhrank(java.sql.Timestamp zlhrank) {
		this.zlhrank = zlhrank;
	}
	  
    }
