package gov.vghks.sal.model;

import gov.vghks.common.util.StringUtil;

import java.io.Serializable;

public class RankCode implements Serializable{
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 階別代碼*/
    private String rankcode;
    /** 中文說明*/
    private String rankname;
    /** 階別簡稱*/
    private String rankabbr;
    /** 日期時戳 */
    private java.sql.Timestamp zparankcode;
    
	public String getRankcode() {
		return rankcode;
	}
	public void setRankcode(String rankcode) {
		this.rankcode = rankcode;
	}
	public String getRankname() {
		return StringUtil.nullToString(rankname);
	}
	public void setRankname(String rankname) {
		this.rankname = rankname;
	}
	public String getRankabbr() {
		return StringUtil.nullToString(rankabbr);
	}
	public void setRankabbr(String rankabbr) {
		this.rankabbr = rankabbr;
	}
	public java.sql.Timestamp getZparankcode() {
		return zparankcode;
	}
	public void setZparankcode(java.sql.Timestamp zparankcode) {
		this.zparankcode = zparankcode;
	}
    
    
    }
