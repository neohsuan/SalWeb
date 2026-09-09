package gov.vghks.sal.model;

import java.io.Serializable;
//二代健保(補充保險費_獎金)
public class Panhical implements Serializable{
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 給付日期*/
    private java.sql.Date papaydt;	
    /** 資料來源,DAT:發薪明細,TAX:人工計稅*/
    private String paysource;
    /** 餉單編號/轉帳項目--0無 1夜班費 2離職人員補發扣 3代發薪資*/
    private int itemnum;
    /** 身分證字號*/
    private String payidno;
    /** 給付所得類別62獎金 63兼職*/
    private String payitem;
    /** 卡號*/
    private String paycard;
    /** 姓名*/
    private String paynamec;
    /** 當月投保金額(A)*/
    private int moninsu;
    /** 4倍投保金額(B=Ax4)*/
    private int moninsu4;
    /** 單次獎金金額(C)*/
    private int bonusamt;
    /** 累積獎金金額(D)*/
    private int jackpot;
    /** 累積超過4倍投保金額之獎金(E=D-B)*/
    private int mtinsu;
    /** 補充保險費費基(F)min(E,C)*/
    private int suplybase;
    /** 補充保險費金額(G=F*2%)*/
    private int nhiamt;
    /** 身份別:0無1公保2勞保*/
    private int insuident;
    /** 處理旗標*/
    private String payflag;
    /** 更改者PAYUPDID*/
    private String payupdid;
    /** 修改日期時間 PAYUPDTM*/
    private java.sql.Timestamp payupdat;
    /** 醫院別*/
    private String hid;
    /** 日期時戳 */
    private java.sql.Timestamp zpanhical;
    /** 日期時戳 */
    private java.sql.Timestamp zptnhical;
    /** 日期時戳 */
    private java.sql.Timestamp zlhnhical;
    /** 年度*/
    private int papayyr;
    
	public java.sql.Date getPapaydt() {
		return papaydt;
	}
	public void setPapaydt(java.sql.Date papaydt) {
		this.papaydt = papaydt;
	}
	public String getPaysource() {
		return paysource;
	}
	public void setPaysource(String paysource) {
		this.paysource = paysource;
	}
	public int getItemnum() {
		return itemnum;
	}
	public void setItemnum(int itemnum) {
		this.itemnum = itemnum;
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
	public int getMoninsu() {
		return moninsu;
	}
	public void setMoninsu(int moninsu) {
		this.moninsu = moninsu;
	}
	public int getMoninsu4() {
		return moninsu4;
	}
	public void setMoninsu4(int moninsu4) {
		this.moninsu4 = moninsu4;
	}
	public int getBonusamt() {
		return bonusamt;
	}
	public void setBonusamt(int bonusamt) {
		this.bonusamt = bonusamt;
	}
	public int getJackpot() {
		return jackpot;
	}
	public void setJackpot(int jackpot) {
		this.jackpot = jackpot;
	}
	public int getMtinsu() {
		return mtinsu;
	}
	public void setMtinsu(int mtinsu) {
		this.mtinsu = mtinsu;
	}
	public int getSuplybase() {
		return suplybase;
	}
	public void setSuplybase(int suplybase) {
		this.suplybase = suplybase;
	}
	public int getNhiamt() {
		return nhiamt;
	}
	public void setNhiamt(int nhiamt) {
		this.nhiamt = nhiamt;
	}
	public int getInsuident() {
		return insuident;
	}
	public void setInsuident(int insuident) {
		this.insuident = insuident;
	}
	public String getPayflag() {
		return payflag;
	}
	public void setPayflag(String payflag) {
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
	public int getPapayyr() {
		return papayyr;
	}
	public void setPapayyr(int papayyr) {
		this.papayyr = papayyr;
	}
	public java.sql.Timestamp getZptnhical() {
		return zptnhical;
	}
	public void setZptnhical(java.sql.Timestamp zptnhical) {
		this.zptnhical = zptnhical;
	}
	public java.sql.Timestamp getZlhnhical() {
		return zlhnhical;
	}
	public void setZlhnhical(java.sql.Timestamp zlhnhical) {
		this.zlhnhical = zlhnhical;
	}
    
	
 }
