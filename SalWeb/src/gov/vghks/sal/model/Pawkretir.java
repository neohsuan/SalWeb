package gov.vghks.sal.model;

import java.io.Serializable;
//勞工退休金檔
public class Pawkretir implements Serializable{
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 發薪日期*/
    private java.sql.Date patradt;	
    /** 身分證字號*/
    private String payidno;
    /** 卡號*/
    private String paycard;
    /** 階級*/
    private String payrank;
    /** 職稱*/
    private String paytitle;
    /** 分類(1:固定 2:補發項)*/
    private int paysort;
    /** 月提繳金額*/
    private int monpay;
    /** 公提退休金6%*/
    private int masteramt;
    /** 自願提繳率*/
    private String selfexrate;
    /** 自繳退休金*/
    private int selfamt;
    /** 補發月數*/
    private int paymonno;
    /** 補發日數*/
    private int paydayno;
    /** 更改者padutyid*/
    private String padutyid;
    /** 修改日期時間 PAYUPDTM*/
    private java.sql.Timestamp payupdat;
    /** 處理旗標*/
    private String payflag;
    /** 醫院別*/
    private String hid;
    /** 日期時戳 */
    private java.sql.Timestamp zpawkretir;
    /** 日期時戳 */
    private java.sql.Timestamp zptwkretir;
    /** 日期時戳 */
    private java.sql.Timestamp zlhwkretir;
    
	public java.sql.Date getPatradt() {
		return patradt;
	}
	public void setPatradt(java.sql.Date patradt) {
		this.patradt = patradt;
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
	public String getPayrank() {
		return payrank;
	}
	public void setPayrank(String payrank) {
		this.payrank = payrank;
	}
	public String getPaytitle() {
		return paytitle;
	}
	public void setPaytitle(String paytitle) {
		this.paytitle = paytitle;
	}
	
	public int getMonpay() {
		return monpay;
	}
	public void setMonpay(int monpay) {
		this.monpay = monpay;
	}
	public int getMasteramt() {
		return masteramt;
	}
	public void setMasteramt(int masteramt) {
		this.masteramt = masteramt;
	}
	public String getSelfexrate() {
		return selfexrate;
	}
	public void setSelfexrate(String selfexrate) {
		this.selfexrate = selfexrate;
	}
	public int getSelfamt() {
		return selfamt;
	}
	public void setSelfamt(int selfamt) {
		this.selfamt = selfamt;
	}
	public int getPaymonno() {
		return paymonno;
	}
	public void setPaymonno(int paymonno) {
		this.paymonno = paymonno;
	}
	public int getPaydayno() {
		return paydayno;
	}
	public void setPaydayno(int paydayno) {
		this.paydayno = paydayno;
	}
	public String getPadutyid() {
		return padutyid;
	}
	public void setPadutyid(String padutyid) {
		this.padutyid = padutyid;
	}
	public java.sql.Timestamp getPayupdat() {
		return payupdat;
	}
	public void setPayupdat(java.sql.Timestamp payupdat) {
		this.payupdat = payupdat;
	}
	public String getPayflag() {
		return payflag;
	}
	public void setPayflag(String payflag) {
		this.payflag = payflag;
	}
	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
	}
	public java.sql.Timestamp getZpawkretir() {
		return zpawkretir;
	}
	public void setZpawkretir(java.sql.Timestamp zpawkretir) {
		this.zpawkretir = zpawkretir;
	}
	public java.sql.Timestamp getZptwkretir() {
		return zptwkretir;
	}
	public void setZptwkretir(java.sql.Timestamp zptwkretir) {
		this.zptwkretir = zptwkretir;
	}
	public java.sql.Timestamp getZlhwkretir() {
		return zlhwkretir;
	}
	public void setZlhwkretir(java.sql.Timestamp zlhwkretir) {
		this.zlhwkretir = zlhwkretir;
	}
	public int getPaysort() {
		return paysort;
	}
	public void setPaysort(int paysort) {
		this.paysort = paysort;
	}
    
}
