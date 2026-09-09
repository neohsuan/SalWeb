package gov.vghks.sal.model;

import gov.vghks.common.util.DateUtil;
import gov.vghks.common.util.StringUtil;
import gov.vghks.sal.util.PaCommUtil;
import gov.vghks.sal.Globals;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//異動個人明細
public class Patrapsn implements Serializable{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf_d=new SimpleDateFormat("yyyy-MM-dd");
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 異動代碼*/
    private int patracod;
    /** 異動日期*/
    private java.sql.Date patradt;	
    /** 身份證字號*/
    private String payidno;
    /** 醫院別*/
    private String hid;
    /** 生效日期*/
    private java.sql.Date paeffdt;
    /** 餉單編號*/
    private int salnum;
    /** 離職否(Y/N)*/
    private String paylvefg;
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
    private String paydpno;
    /** 考績*/
    private String paypflag;
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
    /** 額外點數1代碼*/
    private int payexpfkey1;
    /** 額外點數2代碼*/
    private int payexpfkey2;
    /** 額外點數3代碼*/
    private int payexpfkey3;    
    /** 適用專業加給表*/
    private int payrank4;
    /** 原職稱代碼*/
    private String paotitle;
    /** 原職等01-15,P1-P3,T1*/
    private String paornk2n;
    /** 原本功1/2*/
    private int paorank3;
    /** 原級別*/
    private int paodegr2;
    /** 原俸點*/
    private int paodegre;
    /** 原階別,1-9,A-F*/
    private String paorank;
    /** 原權理職等*/
    private String paorank2;
    /** 原主管(權理)*/
    private String paorankc;
    /** 原專勤基本額度*/
    private int paopfdoc;
    /** 原工作獎金(Y/N)*/
    private String paocsfla;
    /** 原額外點數1*/
    private int paopfsp1;
    /** 原額外點數2*/
    private int paopfsp2;
    /** 原額外點數3*/
    private int paopfsp3;
    /** 原額外點數1代碼*/
    private int paoexpfkey1;
    /** 原額外點數2代碼*/
    private int paoexpfkey2;
    /** 原額外點數3代碼*/
    private int paoexpfkey3;
    /** 原適用專業加給表*/
    private int paorank4;
    
    /** 扶養人數*/
    private int paytaxno;
    /** 補發繳回月數*/
    private int paymonno;
    /** 補發繳回日數*/
    private int paydayno;
    /** 最後更改ID*/
    private String payupdid;
    /** 最後更改TIME*/
    private java.sql.Timestamp payupdtm;
    /** 處理旗標*/
    private String payflag;
    /** 和異動支扣名細關聯*/
    private int tranum;
    /** 料過帳至主檔*/
    private String upmastdt;
    /** PASSWORD*/
    private String paypsw;
    
    /** 中國信託薪津帳號*/
    private String payctcbacno;
    /** 中國信託公儲帳號*/
    private String payctcbgedno;
    /** 投保金額*/
    private int insuamt;
    /** 日期時戳 */
    private java.sql.Timestamp zpatrapsn;
    /** 日期時戳 */
    private java.sql.Timestamp zpttrapsn;
    /** 日期時戳 */
    private java.sql.Timestamp zlhtrapsn;
    /** 部門中文*/
    private String padepnmc;
    
    /** 新支*/    
    /** 階別中文*/
    private String rankname;
    /** 職稱中文*/
    private String paytitlp;
    /** 職稱類別*/
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
    
    /** 原支*/
    /** 實際支領KEY*/
    private String pokey;
    /** 基本點數*/
    private String pobas;
    /** 職等點數*/
    private String podat;
    /** 階別中文*/
    private String ranknamo;
    /** 職稱中文*/
    private String paytitlo;
    /** 職稱類別*/
    private String patitsuo;
    /** 適用專業加給表中文*/
    private String rnk4namo;
    /** 異動日期_String*/
    private String strtradt;
    /** 初任公職日期_String*/
    private String strgovdt;
    /** 到院日期_String*/
    private String strempdt;
    /** 新制契約  契約除醫師外,到院日2015/1起為新制*/
    private String contract;
    /** 新制契約  契約除醫師外,到院日2015/1起為新制*/
    private String contraco;
    
    /** 戶籍地址*/
    private String payaddrs;
    /** 異動原因中文*/
    private String traname;
    
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
		return StringUtil.nullToString(paynamec);
	}
	public void setPaynamec(String paynamec) {
		this.paynamec = paynamec;
	}
	public String getPaydept() {
		return StringUtil.nullToString(paydept);
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
		return StringUtil.nullToString(rnk4name);
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
	
	//契約
	//高榮1A0--醫師:舊制N,契約:到院日2015/1起為新制Y,舊制N
	//屏榮2A0--醫師:舊制N,契約:新制Y
	//聯醫3A0--新制Y
	public String getContraco() { 
		String rtnTmp="";
		try {
			String empdt=this.getStrempdt();
			if (this.getHid().equals("3A0"))
				rtnTmp="Y";
			else if (this.getHid().equals("2A0")){
				if (("F".equals(paorank) && !this.paytitlo.contains("醫師")))
					rtnTmp="N";
				else
					rtnTmp="Y";
			}
			else if (this.getHid().equals("1A0")){	
				if (("F".equals(paorank) && !this.paytitlo.contains("醫師"))){
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
	public String getStrtradt() {
		return sdf_d.format(this.paygovdt);
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
	
	public void setRank3name(String rank3name) {
		this.rank3name = rank3name;
	}
	public java.sql.Date getPaeffdt() {
		return paeffdt;
	}
	public void setPaeffdt(java.sql.Date paeffdt) {
		this.paeffdt = paeffdt;
	}
	public int getSalnum() {
		return salnum;
	}
	public void setSalnum(int salnum) {
		this.salnum = salnum;
	}
	public String getPaypflag() {
		return paypflag;
	}
	public void setPaypflag(String paypflag) {
		this.paypflag = paypflag;
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
	public String getPaotitle() {
		return paotitle;
	}
	public void setPaotitle(String paotitle) {
		this.paotitle = paotitle;
	}
	public String getPaornk2n() {
		return StringUtil.nullToString(paornk2n);
	}
	public void setPaornk2n(String paornk2n) {
		this.paornk2n = paornk2n;
	}
	public int getPaorank3() {
		return paorank3;
	}
	public void setPaorank3(int paorank3) {
		this.paorank3 = paorank3;
	}
	public int getPaodegr2() {
		return paodegr2;
	}
	public void setPaodegr2(int paodegr2) {
		this.paodegr2 = paodegr2;
	}
	public int getPaodegre() {
		return paodegre;
	}
	public void setPaodegre(int paodegre) {
		this.paodegre = paodegre;
	}
	public String getPaorank() {
		return paorank;
	}
	public void setPaorank(String paorank) {
		this.paorank = paorank;
	}
	public String getPaorank2() {
		return StringUtil.nullToString(paorank2);
	}
	public void setPaorank2(String paorank2) {
		this.paorank2 = paorank2;
	}
	public String getPaorankc() {
		return StringUtil.nullToString(paorankc);
	}
	public void setPaorankc(String paorankc) {
		this.paorankc = paorankc;
	}
	public int getPaopfdoc() {
		return paopfdoc;
	}
	public void setPaopfdoc(int paopfdoc) {
		this.paopfdoc = paopfdoc;
	}
	public String getPaocsfla() {
		return paocsfla;
	}
	public void setPaocsfla(String paocsfla) {
		this.paocsfla = paocsfla;
	}
	public int getPaopfsp1() {
		return paopfsp1;
	}
	public void setPaopfsp1(int paopfsp1) {
		this.paopfsp1 = paopfsp1;
	}
	public int getPaopfsp2() {
		return paopfsp2;
	}
	public void setPaopfsp2(int paopfsp2) {
		this.paopfsp2 = paopfsp2;
	}
	public int getPaopfsp3() {
		return paopfsp3;
	}
	public void setPaopfsp3(int paopfsp3) {
		this.paopfsp3 = paopfsp3;
	}
	public String getPayflag() {
		return payflag;
	}
	public void setPayflag(String payflag) {
		this.payflag = payflag;
	}
	public int getTranum() {
		return tranum;
	}
	public void setTranum(int tranum) {
		this.tranum = tranum;
	}
	public String getUpmastdt() {
		return upmastdt;
	}
	public void setUpmastdt(String upmastdt) {
		this.upmastdt = upmastdt;
	}
	public int getPaoexpfkey1() {
		return paoexpfkey1;
	}
	public void setPaoexpfkey1(int paoexpfkey1) {
		this.paoexpfkey1 = paoexpfkey1;
	}
	public int getPaoexpfkey2() {
		return paoexpfkey2;
	}
	public void setPaoexpfkey2(int paoexpfkey2) {
		this.paoexpfkey2 = paoexpfkey2;
	}
	public int getPaoexpfkey3() {
		return paoexpfkey3;
	}
	public void setPaoexpfkey3(int paoexpfkey3) {
		this.paoexpfkey3 = paoexpfkey3;
	}
	public int getPaorank4() {
		return paorank4;
	}
	public void setPaorank4(int paorank4) {
		this.paorank4 = paorank4;
	}
	public java.sql.Timestamp getZpatrapsn() {
		return zpatrapsn;
	}
	public void setZpatrapsn(java.sql.Timestamp zpatrapsn) {
		this.zpatrapsn = zpatrapsn;
	}
	public String getPokey() {
		return pokey;
	}
	public void setPokey(String pokey) {
		this.pokey = pokey;
	}
	public String getPobas() {
		return pobas;
	}
	public void setPobas(String pobas) {
		this.pobas = pobas;
	}
	public String getPodat() {
		return podat;
	}
	public void setPodat(String podat) {
		this.podat = podat;
	}
	public String getPatitsub() {
		return patitsub;
	}
	public void setPatitsub(String patitsub) {
		this.patitsub = patitsub;
	}
	public String getRanknamo() {
		return StringUtil.nullToString(ranknamo);
	}
	public void setRanknamo(String ranknamo) {
		this.ranknamo = ranknamo;
	}
	public String getPaytitlo() {
		return StringUtil.nullToString(paytitlo);
	}
	public void setPaytitlo(String paytitlo) {
		this.paytitlo = paytitlo;
	}
	public String getPatitsuo() {
		return patitsuo;
	}
	public void setPatitsuo(String patitsuo) {
		this.patitsuo = patitsuo;
	}
	public String getRnk4namo() {
		return StringUtil.nullToString(rnk4namo);
	}
	public void setRnk4namo(String rnk4namo) {
		this.rnk4namo = rnk4namo;
	}
	public String getPayaddrs() {
		return StringUtil.trimWideEmpty(payaddrs);
	}
	public void setPayaddrs(String payaddrs) {
		this.payaddrs = payaddrs;
	}
	public String getPaydpno() {
		return StringUtil.nullToString(paydpno);
	}
	public void setPaydpno(String paydpno) {
		this.paydpno = paydpno;
	}
	public java.sql.Timestamp getZpttrapsn() {
		return zpttrapsn;
	}
	public void setZpttrapsn(java.sql.Timestamp zpttrapsn) {
		this.zpttrapsn = zpttrapsn;
	}
	public java.sql.Timestamp getZlhtrapsn() {
		return zlhtrapsn;
	}
	public void setZlhtrapsn(java.sql.Timestamp zlhtrapsn) {
		this.zlhtrapsn = zlhtrapsn;
	}
	public String getTraname() {
		return traname;
	}
	public void setTraname(String traname) {
		this.traname = traname;
	}
	public int getPaytaxno() {
		return paytaxno;
	}
	public void setPaytaxno(int paytaxno) {
		this.paytaxno = paytaxno;
	}
    
        
}
	