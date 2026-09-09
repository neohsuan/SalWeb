package gov.vghks.sal.model;

import java.io.Serializable;
//異動代碼 
public class Patracode implements Serializable{
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 代碼*/
    private int tracode;
    /** 中文說明*/
    private String traname;
    /** 日期時戳 */
    private java.sql.Timestamp zpatracode;
    /** 日期時戳 */
    private java.sql.Timestamp zpttracode;
    /** 日期時戳 */
    private java.sql.Timestamp zlhtracode;
    
	public int getTracode() {
		return tracode;
	}
	public void setTracode(int tracode) {
		this.tracode = tracode;
	}
	public String getTraname() {
		return traname;
	}
	public void setTraname(String traname) {
		this.traname = traname;
	}
	public java.sql.Timestamp getZpatracode() {
		return zpatracode;
	}
	public void setZpatracode(java.sql.Timestamp zpatracode) {
		this.zpatracode = zpatracode;
	}
	public java.sql.Timestamp getZpttracode() {
		return zpttracode;
	}
	public void setZpttracode(java.sql.Timestamp zpttracode) {
		this.zpttracode = zpttracode;
	}
	public java.sql.Timestamp getZlhtracode() {
		return zlhtracode;
	}
	public void setZlhtracode(java.sql.Timestamp zlhtracode) {
		this.zlhtracode = zlhtracode;
	}
    
	
    }
