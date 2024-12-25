package gov.vghks.sal.model;

import gov.vghks.common.util.StringUtil;

import java.io.Serializable;
//適用專業加給對照表
public class Parank4tbl implements Serializable{
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 代碼*/
    private int rnk4key;
    /** 中文說明*/
    private String rnk4name;
    /** 更改日期 */
    private java.sql.Timestamp payupdtm;
    /** 日期時戳 */
    private java.sql.Timestamp zparank4tbl;
    
	public String getRnk4name() {
		return StringUtil.nullToString(rnk4name);
	}
	public void setRnk4name(String rnk4name) {
		this.rnk4name = rnk4name;
	}
	public java.sql.Timestamp getPayupdtm() {
		return payupdtm;
	}
	public void setPayupdtm(java.sql.Timestamp payupdtm) {
		this.payupdtm = payupdtm;
	}
	public java.sql.Timestamp getZparank4tbl() {
		return zparank4tbl;
	}
	public void setZparank4tbl(java.sql.Timestamp zparank4tbl) {
		this.zparank4tbl = zparank4tbl;
	}
	public int getRnk4key() {
		return rnk4key;
	}
	public void setRnk4key(int rnk4key) {
		this.rnk4key = rnk4key;
	}
	  
    }
