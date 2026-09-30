package gov.vghks.sal.model;

import gov.vghks.common.util.StringUtil;

import java.io.Serializable;
//勞工退休金月提繳工資分級表  
public class Palbrptbl implements Serializable{
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 最低實際工資*/
    private int lbrplow;
    /** 最高實際工資*/
    private int lbrphigh;
    /** 月提繳工資*/
    private int lbrpamt;
    /** 公提退休金=月提繳工資*6%*/
    private int masteramt;
    /** 助理職業災害保險費*/
    private int assocc;
    /** 日期時戳 */
    private java.sql.Timestamp zpalbrptbl;
    /** 日期時戳 */
    private java.sql.Timestamp zptlbrptbl;
    /** 日期時戳 */
    private java.sql.Timestamp zlhlbrptbl;
	public int getLbrplow() {
		return lbrplow;
	}
	public void setLbrplow(int lbrplow) {
		this.lbrplow = lbrplow;
	}
	public int getLbrphigh() {
		return lbrphigh;
	}
	public void setLbrphigh(int lbrphigh) {
		this.lbrphigh = lbrphigh;
	}
	public int getLbrpamt() {
		return lbrpamt;
	}
	public void setLbrpamt(int lbrpamt) {
		this.lbrpamt = lbrpamt;
	}
	public int getMasteramt() {
		return masteramt;
	}
	public void setMasteramt(int masteramt) {
		this.masteramt = masteramt;
	}
	public int getAssocc() {
		return assocc;
	}
	public void setAssocc(int assocc) {
		this.assocc = assocc;
	}
	public java.sql.Timestamp getZpalbrptbl() {
		return zpalbrptbl;
	}
	public void setZpalbrptbl(java.sql.Timestamp zpalbrptbl) {
		this.zpalbrptbl = zpalbrptbl;
	}
	public java.sql.Timestamp getZptlbrptbl() {
		return zptlbrptbl;
	}
	public void setZptlbrptbl(java.sql.Timestamp zptlbrptbl) {
		this.zptlbrptbl = zptlbrptbl;
	}
	public java.sql.Timestamp getZlhlbrptbl() {
		return zlhlbrptbl;
	}
	public void setZlhlbrptbl(java.sql.Timestamp zlhlbrptbl) {
		this.zlhlbrptbl = zlhlbrptbl;
	}
    
}
