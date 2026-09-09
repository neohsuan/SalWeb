package gov.vghks.sal.model;

import java.io.Serializable;
//職等支扣--專業加給及主管加給 
public class Paposi implements Serializable{
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 職等代碼*/
    private String payposik; 
    /** 最低俸點*/
    private int degrlow;
    /** 最高俸點*/
    private int degrhigh;
    /** 醫師及研究人員(二四-一)(二四-二) */
    private int paypsp1;
    /** 醫務技術人員專業加給(二六)(二四-二)*/
    private int paypsp2;
    /** 行政人員專業加給(一)*/
    private int paypsp3;
    /** 資訊人員專業加給(二十)*/
    private int paypsp4;
    /** 主管加給*/
    private int paypsp5;
    /** 醫學工程*/
    private int paypsp6;
    /** 修改人員*/
    private String payposid;
    /** 工程人員*/
    private int paypsp7;
    /** 社工(二)*/
    private int paypsp8;
    /** 更改日期 */
    private java.sql.Timestamp payupdat;
    /** 日期時戳 */
    private java.sql.Timestamp zpaposi;
    /** 日期時戳 */
    private java.sql.Timestamp zptposi;
    /** 日期時戳 */
    private java.sql.Timestamp zlhposi;
    
	public String getPayposik() {
		return payposik;
	}
	public void setPayposik(String payposik) {
		this.payposik = payposik;
	}
	public int getDegrlow() {
		return degrlow;
	}
	public void setDegrlow(int degrlow) {
		this.degrlow = degrlow;
	}
	public int getDegrhigh() {
		return degrhigh;
	}
	public void setDegrhigh(int degrhigh) {
		this.degrhigh = degrhigh;
	}
	public int getPaypsp1() {
		return paypsp1;
	}
	public void setPaypsp1(int paypsp1) {
		this.paypsp1 = paypsp1;
	}
	public int getPaypsp2() {
		return paypsp2;
	}
	public void setPaypsp2(int paypsp2) {
		this.paypsp2 = paypsp2;
	}
	public int getPaypsp3() {
		return paypsp3;
	}
	public void setPaypsp3(int paypsp3) {
		this.paypsp3 = paypsp3;
	}
	public int getPaypsp4() {
		return paypsp4;
	}
	public void setPaypsp4(int paypsp4) {
		this.paypsp4 = paypsp4;
	}
	public int getPaypsp5() {
		return paypsp5;
	}
	public void setPaypsp5(int paypsp5) {
		this.paypsp5 = paypsp5;
	}
	public int getPaypsp6() {
		return paypsp6;
	}
	public void setPaypsp6(int paypsp6) {
		this.paypsp6 = paypsp6;
	}
	public String getPayposid() {
		return payposid;
	}
	public void setPayposid(String payposid) {
		this.payposid = payposid;
	}
	public int getPaypsp7() {
		return paypsp7;
	}
	public void setPaypsp7(int paypsp7) {
		this.paypsp7 = paypsp7;
	}
	public int getPaypsp8() {
		return paypsp8;
	}
	public void setPaypsp8(int paypsp8) {
		this.paypsp8 = paypsp8;
	}
	public java.sql.Timestamp getPayupdat() {
		return payupdat;
	}
	public void setPayupdat(java.sql.Timestamp payupdat) {
		this.payupdat = payupdat;
	}
	public java.sql.Timestamp getZpaposi() {
		return zpaposi;
	}
	public void setZpaposi(java.sql.Timestamp zpaposi) {
		this.zpaposi = zpaposi;
	}
	public java.sql.Timestamp getZptposi() {
		return zptposi;
	}
	public void setZptposi(java.sql.Timestamp zptposi) {
		this.zptposi = zptposi;
	}
	public java.sql.Timestamp getZlhposi() {
		return zlhposi;
	}
	public void setZlhposi(java.sql.Timestamp zlhposi) {
		this.zlhposi = zlhposi;
	}
    
	
	  
    }
