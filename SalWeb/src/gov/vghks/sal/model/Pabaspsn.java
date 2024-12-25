package gov.vghks.sal.model;

import gov.vghks.common.util.DateUtil;
import gov.vghks.common.util.StringUtil;
import gov.vghks.sal.util.PaCommUtil;
import gov.vghks.sal.Globals;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//基本個人明細
public class Pabaspsn implements Serializable{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf_d=new SimpleDateFormat("yyyy-MM-dd");
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 離職否(Y/N)*/
    private String paylvefg;
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
    /** 初任公職日期*/
    private java.sql.Date paygovdt;
    /** 到院日期*/
    private java.sql.Date payempdt;
    /** 現職日期*/
    private java.sql.Date paysgndt;
    /** 離職日期*/
    private java.sql.Date payrsgdt;
    /** 銀行帳號科目*/
    private String paybkid;
    /** 銀行帳號*/
    private String payacno;
    /** 公儲帳號*/
    private String paygedno;
    /** 保險費比率(1,1/4,1/2,0,上限)*/
    private int payinflg;
    /** 健保數*/
    private int paydpno;
    /** PASSWORD*/
    private String paypsw;
    /** 職稱代碼*/
    private String paytitle;
    /** 職等01-15,P1-P3,T1*/
    private String payrnk2n;
    /** 本功1/2*/
    private int payrank3;
    /** 級別*/
    private int paydegr2;
    /** 俸點*/
    private int paydegre;
    /** 階別,1-9,A-F*/
    private String payrank;
    /** 權理職等*/
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
    /** 異動代碼*/
    private int patracod;
    /** 異動日期*/
    private java.sql.Date patradt;
    /** 扶養人數*/
    private int paytaxno;
    /** 最後更改ID*/
    private String payupdid;
    /** 最後更改TIME*/
    private java.sql.Timestamp payupdtm;
    /** 額外點數1代碼*/
    private int payexpfkey1;
    /** 額外點數2代碼*/
    private int payexpfkey2;
    /** 額外點數3代碼*/
    private int payexpfkey3;
    /** 適用專業加給表*/
    private int payrank4;
    /** 中國信託薪津帳號*/
    private String payctcbacno;
    /** 中國信託公儲帳號*/
    private String payctcbgedno;
    /** 投保金額*/
    private int insuamt;
    /** 留職留薪,支領專2(Y/N)*/
    private String paretention;
    /** 醫院別*/
    private String hid;
    /** 日期時戳 */
    private java.sql.Timestamp zpabaspsn;
    /** 部門中文*/
    private String padepnmc;
    /** 階別中文*/
    private String rankname;
    /** 職稱中文*/
    private String paytitlp;
    /** 職稱群組*/
    private String patitsub;
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
    
    /** 初任公職日期_String*/
    private String strgovdt;
    /** 到院日期_String*/
    private String strempdt;
    /** 新制契約  契約除醫師外,到院日2015/1起為新制*/
    private String contract;
    /** 異動代碼中文*/
    private String traname;
    /** 傳回 保險費比率 中文名稱*/
    private String inflgnm;
    /** 戶籍地址*/
    private String payaddrs;
    
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

	//契約除醫師外,到院日2015/1起為新制Y,舊制N
	public String getContract() { 
		String rtnTmp="";
		try {
			String empdt=this.getStrempdt();
			if (("F".equals(payrank) && !this.paytitlp.contains("醫師"))){
				if( DateUtil.compareDate(empdt, Globals.gNcontract) )
					rtnTmp="N";
				else
					rtnTmp="Y";
			}
		} catch (ParseException e) {
			// TODO 自動產生 catch 區塊
			e.printStackTrace();
		}
		return rtnTmp;
	}
	
	public String getStrgovdt() {
		return sdf_d.format(this.paygovdt);
	}
	public String getPaylvefg() {
		return paylvefg;
	}
	public void setPaylvefg(String paylvefg) {
		this.paylvefg = paylvefg;
	}
	public java.sql.Date getPaysgndt() {
		return paysgndt;
	}
	public void setPaysgndt(java.sql.Date paysgndt) {
		this.paysgndt = paysgndt;
	}
	public java.sql.Date getPayrsgdt() {
		return payrsgdt;
	}
	public void setPayrsgdt(java.sql.Date payrsgdt) {
		this.payrsgdt = payrsgdt;
	}
	public String getPaybkid() {
		return paybkid;
	}
	public void setPaybkid(String paybkid) {
		this.paybkid = paybkid;
	}
	public String getPayacno() {
		return payacno;
	}
	public void setPayacno(String payacno) {
		this.payacno = payacno;
	}
	public String getPaygedno() {
		return paygedno;
	}
	public void setPaygedno(String paygedno) {
		this.paygedno = paygedno;
	}
	public int getPayinflg() {
		return payinflg;
	}
	public void setPayinflg(int payinflg) {
		this.payinflg = payinflg;
	}
	public int getPaydpno() {
		return paydpno;
	}
	public void setPaydpno(int paydpno) {
		this.paydpno = paydpno;
	}
	public String getPaypsw() {
		return paypsw;
	}
	public void setPaypsw(String paypsw) {
		this.paypsw = paypsw;
	}
	public int getPatracod() {
		return patracod;
	}
	public void setPatracod(int patracod) {
		this.patracod = patracod;
	}
	public java.sql.Date getPatradt() {
		return patradt;
	}
	public void setPatradt(java.sql.Date patradt) {
		this.patradt = patradt;
	}	
	public String getPayctcbacno() {
		return payctcbacno;
	}
	public void setPayctcbacno(String payctcbacno) {
		this.payctcbacno = payctcbacno;
	}
	public String getPayctcbgedno() {
		return payctcbgedno;
	}
	public void setPayctcbgedno(String payctcbgedno) {
		this.payctcbgedno = payctcbgedno;
	}
	public int getInsuamt() {
		return insuamt;
	}
	public void setInsuamt(int insuamt) {
		this.insuamt = insuamt;
	}
	public String getParetention() {
		String tmp="";	
		if ("Y".equals(paretention))
			tmp=paretention;
		return tmp;
	}
	public void setParetention(String paretention) {
		this.paretention = paretention;
	}
	public java.sql.Timestamp getZpabaspsn() {
		return zpabaspsn;
	}
	public void setZpabaspsn(java.sql.Timestamp zpabaspsn) {
		this.zpabaspsn = zpabaspsn;
	}
	public void setRank3name(String rank3name) {
		this.rank3name = rank3name;
	}
	public String getTraname() {
		return traname;
	}
	public void setTraname(String traname) {
		this.traname = traname;
	}
	public String getInflgnm() {
		return PaCommUtil.getInflgNm(this.payinflg);
	}
	public int getPaytaxno() {
		return paytaxno;
	}
	public void setPaytaxno(int paytaxno) {
		this.paytaxno = paytaxno;
	}
	public String getPatitsub() {
		return patitsub;
	}
	public void setPatitsub(String patitsub) {
		this.patitsub = patitsub;
	}
	public String getPayaddrs() {
		return StringUtil.trimWideEmpty(payaddrs);
	}
	public void setPayaddrs(String payaddrs) {
		this.payaddrs = payaddrs;
	}
	public int getPayexpfkey1() {
		return payexpfkey1;
	}
	public void setPayexpfkey1(int payexpfkey1) {
		this.payexpfkey1 = payexpfkey1;
	}
	public int getPayexpfkey2() {
		return payexpfkey2;
	}
	public void setPayexpfkey2(int payexpfkey2) {
		this.payexpfkey2 = payexpfkey2;
	}
	public int getPayexpfkey3() {
		return payexpfkey3;
	}
	public void setPayexpfkey3(int payexpfkey3) {
		this.payexpfkey3 = payexpfkey3;
	}
    
        
}
	