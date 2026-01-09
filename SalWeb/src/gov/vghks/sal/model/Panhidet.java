package gov.vghks.sal.model;

import java.io.Serializable;
//二代健保申報扣費明細
public class Panhidet implements Serializable{
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 給付日期*/
    private java.sql.Date papaydt;
    /** 身分證字號*/
    private String payidno;
    /** 給付所得類別62獎金 63兼職*/
    private String payitem;
    /** 卡號*/
    private String paycard;
    /** 姓名*/
    private String paynamec;
    /** 流水序號*/
    private int serial;
    /** 給付金額*/
    private int payamt;
    /** 扣費額*/
    private int nhiamt;
    /** 當月投保金額*/
    private int moninsu;
    /** 累積獎金金額*/
    private int jackpot;
    /** 身份別:0無1公保2勞保*/
    private int insuident;
    /** 處理旗標*/
    private int payflag;
    /** 更改者PAYUPDID*/
    private String payupdid;
    /** 修改日期時間 PAYUPDTM*/
    private java.sql.Timestamp payupdat;
    /** 醫院別*/
    private String hid;
    /** 日期時戳 */
    private java.sql.Timestamp zpanhical;
    /** 日期時戳 */
    private java.sql.Timestamp zlhnhical;
    /** 日期時戳 */
    private java.sql.Timestamp zptnhical;
    
	public java.sql.Date getPapaydt() {
		return papaydt;
	}
	public void setPapaydt(java.sql.Date papaydt) {
		this.papaydt = papaydt;
	}
	public String getPayidno() {
		return payidno;
	}
	public void setPayidno(String payidno) {
		this.payidno = payidno;
	}
	public String getPayitem() {
		return payitem;
	}
	public void setPayitem(String payitem) {
		this.payitem = payitem;
	}
	public String getPaycard() {
		return paycard;
	}
	public void setPaycard(String paycard) {
		this.paycard = paycard;
	}
	public String getPaynamec() {
		return paynamec;
	}
	public void setPaynamec(String paynamec) {
		this.paynamec = paynamec;
	}
	public int getSerial() {
		return serial;
	}
	public void setSerial(int serial) {
		this.serial = serial;
	}
	public int getPayamt() {
		return payamt;
	}
	public void setPayamt(int payamt) {
		this.payamt = payamt;
	}
	public int getNhiamt() {
		return nhiamt;
	}
	public void setNhiamt(int nhiamt) {
		this.nhiamt = nhiamt;
	}
	public int getMoninsu() {
		return moninsu;
	}
	public void setMoninsu(int moninsu) {
		this.moninsu = moninsu;
	}
	public int getJackpot() {
		return jackpot;
	}
	public void setJackpot(int jackpot) {
		this.jackpot = jackpot;
	}
	public int getInsuident() {
		return insuident;
	}
	public void setInsuident(int insuident) {
		this.insuident = insuident;
	}
	public int getPayflag() {
		return payflag;
	}
	public void setPayflag(int payflag) {
		this.payflag = payflag;
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
	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
	}
	public java.sql.Timestamp getZpanhical() {
		return zpanhical;
	}
	public void setZpanhical(java.sql.Timestamp zpanhical) {
		this.zpanhical = zpanhical;
	}
	public java.sql.Timestamp getZlhnhical() {
		return zlhnhical;
	}
	public void setZlhnhical(java.sql.Timestamp zlhnhical) {
		this.zlhnhical = zlhnhical;
	}
	public java.sql.Timestamp getZptnhical() {
		return zptnhical;
	}
	public void setZptnhical(java.sql.Timestamp zptnhical) {
		this.zptnhical = zptnhical;
	}
    
	
	
 }
