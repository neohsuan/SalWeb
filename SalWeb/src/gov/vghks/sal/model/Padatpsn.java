package gov.vghks.sal.model;

import gov.vghks.common.util.DateUtil;
import gov.vghks.common.util.StringUtil;
import gov.vghks.sal.util.PaCommUtil;
import gov.vghks.sal.Globals;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
public class Padatpsn implements Serializable{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf_d=new SimpleDateFormat("yyyy-MM-dd");
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 餉單編號*/
    private int salnum;
    /** 發薪日期*/
    private java.sql.Date paybank;
    /** 身份證字號*/
    private String payidno;
    /** 卡號*/
    private String paycard;
    /** 職務編號*/
    private String payempno;
    /** 姓名*/
    private String paynamec;
    /** 部門代碼*/
    private String paydept;
    /** 職稱代碼*/
    private String paytitle;
    /** 職等*/
    private String payrnk2n;
    /** 本功1/2*/
    private int payrank3;
    /** 級別*/
    private int paydegr2;
    /** 俸點*/
    private int paydegre;
    /** 階別,1-9,A-F*/
    private String payrank;
    /** 權理職等01-15,P1-P3,T1*/
    private String payrank2;
    /** 主管(權理)*/
    private String payrankc;
    /** 專勤基本額度*/
    private int paypfdoc;
    /** 工作獎金(Y/N)*/
    private String pacsflag;
    /** 額外點數1*/
    private int paypfsp1;
    /** 額外點數2*/
    private int paypfsp2;
    /** 額外點數3*/
    private int paypfsp3;
    /** 最後更改ID*/
    private String payupdid;
    /** 最後更改TIME*/
    private java.sql.Timestamp payupdtm;
    
    private String updtm;
    /** 醫院別*/
    private String hid;
    /** 適用專業加給表*/
    private int payrank4;
    /** 日期時戳 */
    private java.sql.Timestamp zpadatpsn;
    /** 部門中文*/
    private String padepnmc;
    /** 階別中文*/
    private String rankname;
    /** 職稱中文*/
    private String paytitlp;
    /** 適用專業加給表中文*/
    private String rnk4name;
    /** 本功1/2 中文*/
    private String rank3name;
    /** 實際支領KEY*/
    private String pfkey;
    /** 基本點數*/
    private String pfbas;
    /** 職等點數*/
    private String pfdat;
    /** 初任公職日期*/
    private java.sql.Date paygovdt;
    /** 到院日期*/
    private java.sql.Date payempdt;
    /** 發薪日期_String*/
    private String strbank;
    /** 初任公職日期_String*/
    private String strgovdt;
    /** 到院日期_String*/
    private String strempdt;
    /** 新制契約  契約除醫師外,到院日2015/1起為新制*/
    private String contract;
    /** 投保金額*/
    private int insuamt;
    
    public int getSalnum() {
		return salnum;
	}
	public void setSalnum(int salnum) {
		this.salnum = salnum;
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
	public String getPayempno() {
		return payempno;
	}
	public void setPayempno(String payempno) {
		this.payempno = payempno;
	}
	public String getPaynamec() {
		return paynamec;
	}
	public void setPaynamec(String paynamec) {
		this.paynamec = paynamec;
	}
	public String getPaydept() {
		return paydept;
	}
	public void setPaydept(String paydept) {
		this.paydept = paydept;
	}
	public String getPaytitle() {
		return paytitle;
	}
	public void setPaytitle(String paytitle) {
		this.paytitle = paytitle;
	}
	public String getPayrnk2n() {
		return StringUtil.nullToString(payrnk2n);
	}
	public void setPayrnk2n(String payrnk2n) {
		this.payrnk2n = payrnk2n;
	}
	public int getPayrank3() {
		return payrank3;
	}
	public void setPayrank3(int payrank3) {
		this.payrank3 = payrank3;
	}
	public int getPaydegr2() {
		return paydegr2;
	}
	public void setPaydegr2(int paydegr2) {
		this.paydegr2 = paydegr2;
	}
	public int getPaydegre() {
		return paydegre;
	}
	public void setPaydegre(int paydegre) {
		this.paydegre = paydegre;
	}
	public String getPayrank() {
		return payrank;
	}
	public void setPayrank(String payrank) {
		this.payrank = payrank;
	}
	public String getPayrank2() {
		return StringUtil.nullToString(payrank2);
	}
	public void setPayrank2(String payrank2) {
		this.payrank2 = payrank2;
	}
	public String getPayrankc() {
		return StringUtil.nullToString(payrankc);
	}
	public void setPayrankc(String payrankc) {
		this.payrankc = payrankc;
	}
	public int getPaypfdoc() {
		return paypfdoc;
	}
	public void setPaypfdoc(int paypfdoc) {
		this.paypfdoc = paypfdoc;
	}
	public String getPacsflag() {
		return pacsflag;
	}
	public void setPacsflag(String pacsflag) {
		this.pacsflag = pacsflag;
	}
	public int getPaypfsp1() {
		return paypfsp1;
	}
	public void setPaypfsp1(int paypfsp1) {
		this.paypfsp1 = paypfsp1;
	}
	public int getPaypfsp2() {
		return paypfsp2;
	}
	public void setPaypfsp2(int paypfsp2) {
		this.paypfsp2 = paypfsp2;
	}
	public int getPaypfsp3() {
		return paypfsp3;
	}
	public void setPaypfsp3(int paypfsp3) {
		this.paypfsp3 = paypfsp3;
	}
	public String getPayupdid() {
		return payupdid;
	}
	public void setPayupdid(String payupdid) {
		this.payupdid = payupdid;
	}
	public java.sql.Timestamp getPayupdtm() {
		return payupdtm;
	}
	public void setPayupdtm(java.sql.Timestamp payupdtm) {
		this.payupdtm = payupdtm;
	}
	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
	}
	public int getPayrank4() {
		return payrank4;
	}
	public void setPayrank4(int payrank4) {
		this.payrank4 = payrank4;
	}
	public java.sql.Timestamp getZpadatpsn() {
		return zpadatpsn;
	}
	public void setZpadatpsn(java.sql.Timestamp zpadatpsn) {
		this.zpadatpsn = zpadatpsn;
	}
	public String getPadepnmc() {
		return StringUtil.nullToString(padepnmc);
	}
	public void setPadepnmc(String padepnmc) {
		this.padepnmc = padepnmc;
	}
	
	public String getPaytitlp() {
		return StringUtil.nullToString(paytitlp);
	}
	public void setPaytitlp(String paytitlp) {
		this.paytitlp = paytitlp;
	}
	public String getRankname() {
		return rankname;
	}
	public void setRankname(String rankname) {
		this.rankname = StringUtil.nullToString(rankname);
	}
	public String getRnk4name() {
		return rnk4name;
	}
	public void setRnk4name(String rnk4name) {
		this.rnk4name = rnk4name;
	}
	public java.sql.Date getPaybank() {
		return paybank;
	}
	public void setPaybank(java.sql.Date paybank) {
		this.paybank = paybank;
	}
	public String getRank3name() {
		return PaCommUtil.getRank3Nm(payrank3);
	}
	public String getUpdtm() {
		return sdf.format(payupdtm);
	}
	public String getPfkey() {
		return pfkey;
	}
	public void setPfkey(String pfkey) {
		this.pfkey = pfkey;
	}
	public String getPfbas() {
		return pfbas;
	}
	public void setPfbas(String pfbas) {
		this.pfbas = pfbas;
	}
	public String getPfdat() {
		return pfdat;
	}
	public void setPfdat(String pfdat) {
		this.pfdat = pfdat;
	}
	public java.sql.Date getPaygovdt() {
		return paygovdt;
	}
	public void setPaygovdt(java.sql.Date paygovdt) {
		this.paygovdt = paygovdt;
	}
	public java.sql.Date getPayempdt() {
		return payempdt;
	}
	public void setPayempdt(java.sql.Date payempdt) {
		this.payempdt = payempdt;
	}
	//到院日_string
	public String getStrempdt() {
		return sdf_d.format(this.payempdt);
	}

	//契約
	//高榮1A0--醫師:舊制N,契約:到院日2015/1起為新制Y,舊制N
	//屏榮2A0--醫師:舊制N,契約:新制Y
	//聯醫3A0--新制Y
	public String getContract() { 
		String rtnTmp="";
		try {
			String empdt=this.getStrempdt();
			if (this.getHid().equals("3A0"))
				rtnTmp="Y";
			else if (this.getHid().equals("2A0")){
				if (("F".equals(payrank) && !this.paytitlp.contains("醫師")))
					rtnTmp="N";
				else
					rtnTmp="Y";
			}
			else if (this.getHid().equals("1A0")){	
				if (("F".equals(payrank) && !this.paytitlp.contains("醫師"))){
					if( DateUtil.compareDate(empdt, Globals.gNcontract) )
						rtnTmp="N";
					else
						rtnTmp="Y";
				}
			}	
		} catch (ParseException e) {
			// TODO 自動產生 catch 區塊
			e.printStackTrace();
		}
		return rtnTmp;
	}
	public String getStrbank() {
		return sdf_d.format(this.paybank);
	}
	public String getStrgovdt() {
		return sdf_d.format(this.paygovdt);
	}
	public int getInsuamt() {
		return insuamt;
	}
	public void setInsuamt(int insuamt) {
		this.insuamt = insuamt;
	}
    
        
}
	