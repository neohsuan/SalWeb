package gov.vghks.sal.model;

import gov.vghks.common.util.StringUtil;

import java.io.Serializable;
//績效額外點數對照表 
public class Paexpftbl implements Serializable{
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 代碼*/
    private int expfkey;
    /** 中文說明*/
    private String expfname;
    /** 更改者*/
    private String payupdid;
    /** 更改日期 */
    private java.sql.Timestamp payupdat;
    /** 日期時戳 */
    private java.sql.Timestamp zpaexpftbl;
    
	
	public String getExpfname() {
		return StringUtil.nullToString(expfname);
	}
	public void setExpfname(String expfname) {
		this.expfname = expfname;
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
	public java.sql.Timestamp getZpaexpftbl() {
		return zpaexpftbl;
	}
	public void setZpaexpftbl(java.sql.Timestamp zpaexpftbl) {
		this.zpaexpftbl = zpaexpftbl;
	}
	public int getExpfkey() {
		return expfkey;
	}
	public void setExpfkey(int expfkey) {
		this.expfkey = expfkey;
	}

    }
