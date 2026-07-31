package gov.vghks.sal.model;

import gov.vghks.common.util.DateUtil;
import gov.vghks.common.util.StringUtil;
import gov.vghks.sal.util.PaCommUtil;
import gov.vghks.sal.Globals;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

public class Paprtdtl implements Serializable{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf_d=new SimpleDateFormat("yyyy-MM-dd");
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 身份證字號*/
    private String payidno;
    /** 發薪日期*/
    private java.sql.Date paybank;
    /** 卡號*/
    private String paycard;
    /** 姓名*/
    private String paynamec;
    /** 職務編號*/
    private String payempno;
    /** 銀行帳號*/
    private String payacno;
    /** 公儲帳號*/
    private String paygedno;
    /** 部門代碼*/
    private String paydept;
    /** 部門中文*/
    private String padepnmc;
    /** 職稱分類*/
    private String patitsub;
    /** 職稱代碼*/
    private String paytitle;
    /** 職稱中文*/
    private String paytitlp;
    /** 階別,1-9,A-F*/
    private String payrank;
    /** 階別名稱*/
    private String rankabbr;
    /** 職等*/
    private String payrnk2n;
    /** 本功1/2*/
    private int payrank3;
    /** 級別*/
    private int paydegr2;
    /** 俸點*/
    private int paydegre;
    /** 離職Y*/
    private String paylvefg;
    /** 異動原因*/
    private int patracod;
    /** 異動原因名稱*/
    private String traname;
    /** 生效日期*/
    private java.sql.Date paeffdt;
    /** 上月支薪日數*/
    private int lastdayno;
    /** 本月支薪日數*/
    private int paydayno;
    /** 上月本薪*/
    private int payobase;
    /** 上月專業加給*/
    private int payospsu;
    /** 上月主管加給*/
    private int payochalw;
    /** 1A0_保留 			2A0_上月院區加給		3A0_上月責任加給*/
    private int payostbn;
    /** 1A0_上月實習費       2A0_保留                         3A0_上月職務加給    */
    private int payosuply;
    /** 本薪*/
    private int paybase;
    /** 本薪(補)*/
    private int payrbase;
    /** 專業加給*/
    private int payspsu;
    /** 專業加給(補)*/
    private int payrspsu;
    /** 專業加給差額*/
    private int payspsd;
    /** 專業加給差額(補)*/
    private int payrspsd;
    /** 主管加給*/
    private int paychalw;
    /** 主管加給(補)*/
    private int payrchalw;
    /** 1A0_工作獎金2    2A0_院區加給           3A0_責任加給*/
    private int paystbnt;
    /** 1A0_專勤醫師費2  2A0_院區加給(補)  3A0_責任加給(補)*/
    private int payrstbn;
    /** 1A0_各項津貼          2A0_身心障礙加給    3A0_保留*/
    private int paytrans;
    /** 1A0_實習費            2A0_特別津貼            3A0_職務加給*/
    private int paysuply;
    /** 1A0_實習費(補)  2A0_護理能力進階      3A0_職務加給(補)*/
    private int payrsuply;
    /** 工作獎金*/
    private int paypfnt;
    /** 專勤醫師費*/
    private int paystal;
    /** 1A0_專勤醫師費_病歷罰扣 2A0_專勤醫師費2 3A0_專勤醫師費_病歷罰扣*/
    private int paystald;
    /** 夜班費*/
    private int paynight;
    /** 不休假獎金*/
    private int payyear;
    /** 年終工作獎金*/
    private int payaddhl;
    /** 考績獎金*/
    private int paygrade;
    /** 晉級補發*/
    private int paypromo;
    /** 休假補助*/
    private int payadrtn;
    /** 子女教育補助、婚喪生育、生日禮金休假補助、計劃補助*/
    private int paycplm1;
    /** 1A0_各項獎勵            2A0_保留      3A0_保留*/
    private int paycplm2;
    /** 加班費,未補休轉加班費*/
    private int paycplm3;
    /** 1A0_超額獎金           2A0_保留      3A0_保留*/
    private int payempfd;
    /** 1A0_超盈額獎金       2A0_專勤醫師費(病歷罰扣)      3A0_保留*/
    private int paysars;
    /** 1A0_顧問費            2A0_各項津貼(值班費,津貼--專科護理師 )      3A0_保留*/
    private int payextra;
    /** 公保費*/
    private int paygeins;
    /** 健保費*/
    private int paydpins;
    /** 勞保費*/
    private int paylbins;
    /** 退撫離職金*/
    private int payretir;
    /** 所得稅*/
    private int paytax;
    /** 公教儲金*/
    private int paygedp;
    /** 1A0_體適能中心清潔費       2A0_保留      3A0_保留*/
    private int payvetd1;
    /** 1A0_國宅貸款_土銀            2A0_保留      3A0_保留*/
    private int payhloa1;
    /** 1A0_國宅貸款_合庫            2A0_保留      3A0_保留*/
    private int payhloa2;
    /** 1A0_國宅貸款_中信            2A0_保留      3A0_保留*/
    private int payhloa3;
    /** 1A0_國宅貸款_第一商銀     2A0_保留      3A0_保留*/
    private int payhloa4;
    /** 1A0_國宅貸款_台銀            2A0_保留      3A0_保留*/
    private int payhloa5;
    /** 1A0_國宅貸款_富邦            2A0_保留      3A0_保留*/
    private int payhloa6;
    /** 1A0_自繳退休金                 2A0_勞工退休金 3A0_勞工退休金 */
    private int paydloan;
    /** 房租津貼      3A0_保留*/
    private int paymhous;
    /** 醫療互助金      3A0_保留*/
    private int paynspfd;
    /** 1A0_代扣停車費            2A0_汽車停車費      3A0_保留*/
    private int paypark1;
    /** 1A0_眷舍停車費->其他代收款2026.5.27#11504112    2A0_保留      3A0_保留*/
    private int paypark2;
    /** 1A0_體育管理費(公共設施使用費)   2A0_遺孤認養金      3A0_保留*/
    private int paygym;
    /** 清償債款*/
    private int paylien;
    /** 1A0_餐費            2A0_保留      3A0_保留*/
    private int paymeal;
    /** 1A0_卡費            2A0_保留      3A0_保留*/
    private int paycardf;
    /** 1A0_眷舍管理費_10F      2A0_宿舍管理費      3A0_保留*/
    private int paydorm1;
    /** 1A0_眷舍管理費_5F       2A0_宿舍電費      3A0_保留*/
    private int paydorm2;
    /** 1A0_眷舍管理費_單身           2A0_醫療救助捐款      3A0_保留*/
    private int paydorm3;
    /** 補充保險費*/
    private int payother1;
    /** 1A0_保留(PGY眷舍管理費) 2A0_高榮代收代付      3A0_保留*/
    private int payother2;
    /** 1A0_醫療糾紛       		  2A0_保留      3A0_保留*/
    private int payother3;
    /** 1A0_惠民捐款       		 2A0_弱勢醫療捐款      3A0_保留*/
    private int payagen1;
    /** 總所得--當月*/
    private int paytotal;
    /** 總所得--補發(法定)*/
    private int paytotis;
    /** 總扣除額*/
    private int paytlded;
    /** 淨所得(當月總所得-總扣除額)*/
    private int paynet;
    /** 醫院別*/
    private String hid;
    /** 餉單編號*/
    private int salnum;
    /** 到院日期*/
    private java.sql.Date payempdt;
    /** map*/
    private Map valMap;
    
    /** 到院日期_String*/
    private String strempdt;
    /** 新制契約  契約除醫師外,到院日2015/1起為新制*/
    private String contract;
    /**員工薪津**/
    private int pay;
    /**應發總金額(一)**/
    private int nt1;
    /**應發總金額(二)**/
    private int nt2;
    /** 存款別*/
    private String deposit;
    /** 帳號*/
    private String acno;
    /** 轉帳金額*/
    private int payamt;
    /** 銀行回報結果*/
    private String bankflag;
    /** 比對結果*/
    private String comp;
    
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
	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
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
	public java.sql.Date getPaybank() {
		return paybank;
	}
	public void setPaybank(java.sql.Date paybank) {
		this.paybank = paybank;
	}
	public String getRank3name() {
		return PaCommUtil.getRank3Nm(payrank3);
	}
	public java.sql.Date getPayempdt() {
		return payempdt;
	}
	public void setPayempdt(java.sql.Date payempdt) {
		this.payempdt = payempdt;
	}
	//到院日_string
	public String getStrempdt() {
		String rtnTmp="";
		rtnTmp = (this.payempdt == null) ? "" : sdf_d.format(this.payempdt);
		return rtnTmp;		
	}

	//契約
	//高榮1A0--醫師:舊制N,契約:到院日2015/1起為新制Y(月尾),舊制N(月頭)
	//屏榮2A0--醫師:舊制N,契約:新制Y
	//聯醫3A0--新制Y
	public String getContract() { 
		String rtnTmp="N";
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
	public String getPatitsub() {
		return patitsub;
	}
	public void setPatitsub(String patitsub) {
		this.patitsub = patitsub;
	}
	public String getRankabbr() {
		return rankabbr;
	}
	public void setRankabbr(String rankabbr) {
		this.rankabbr = rankabbr;
	}
	public String getPaylvefg() {
		return paylvefg;
	}
	public void setPaylvefg(String paylvefg) {
		this.paylvefg = paylvefg;
	}
	public String getTraname() {
		return traname;
	}
	public void setTraname(String traname) {
		this.traname = traname;
	}
	public java.sql.Date getPaeffdt() {
		return paeffdt;
	}
	public void setPaeffdt(java.sql.Date paeffdt) {
		this.paeffdt = paeffdt;
	}
	public int getLastdayno() {
		return lastdayno;
	}
	public void setLastdayno(int lastdayno) {
		this.lastdayno = lastdayno;
	}
	public int getPaydayno() {
		return paydayno;
	}
	public void setPaydayno(int paydayno) {
		this.paydayno = paydayno;
	}
	public int getPayobase() {
		return payobase;
	}
	public void setPayobase(int payobase) {
		this.payobase = payobase;
	}
	public int getPayospsu() {
		return payospsu;
	}
	public void setPayospsu(int payospsu) {
		this.payospsu = payospsu;
	}
	public int getPayochalw() {
		return payochalw;
	}
	public void setPayochalw(int payochalw) {
		this.payochalw = payochalw;
	}
	public int getPayostbn() {
		return payostbn;
	}
	public void setPayostbn(int payostbn) {
		this.payostbn = payostbn;
	}
	public int getPayosuply() {
		return payosuply;
	}
	public void setPayosuply(int payosuply) {
		this.payosuply = payosuply;
	}
	public int getPaybase() {
		return paybase;
	}
	public void setPaybase(int paybase) {
		this.paybase = paybase;
	}
	public int getPayrbase() {
		return payrbase;
	}
	public void setPayrbase(int payrbase) {
		this.payrbase = payrbase;
	}
	public int getPayspsu() {
		return payspsu;
	}
	public void setPayspsu(int payspsu) {
		this.payspsu = payspsu;
	}
	public int getPayrspsu() {
		return payrspsu;
	}
	public void setPayrspsu(int payrspsu) {
		this.payrspsu = payrspsu;
	}
	public int getPayspsd() {
		return payspsd;
	}
	public void setPayspsd(int payspsd) {
		this.payspsd = payspsd;
	}
	public int getPayrspsd() {
		return payrspsd;
	}
	public void setPayrspsd(int payrspsd) {
		this.payrspsd = payrspsd;
	}
	public int getPaychalw() {
		return paychalw;
	}
	public void setPaychalw(int paychalw) {
		this.paychalw = paychalw;
	}
	public int getPayrchalw() {
		return payrchalw;
	}
	public void setPayrchalw(int payrchalw) {
		this.payrchalw = payrchalw;
	}
	public int getPaystbnt() {
		return paystbnt;
	}
	public void setPaystbnt(int paystbnt) {
		this.paystbnt = paystbnt;
	}
	public int getPayrstbn() {
		return payrstbn;
	}
	public void setPayrstbn(int payrstbn) {
		this.payrstbn = payrstbn;
	}
	public int getPaytrans() {
		return paytrans;
	}
	public void setPaytrans(int paytrans) {
		this.paytrans = paytrans;
	}
	public int getPaysuply() {
		return paysuply;
	}
	public void setPaysuply(int paysuply) {
		this.paysuply = paysuply;
	}
	public int getPayrsuply() {
		return payrsuply;
	}
	public void setPayrsuply(int payrsuply) {
		this.payrsuply = payrsuply;
	}
	public int getPaypfnt() {
		return paypfnt;
	}
	public void setPaypfnt(int paypfnt) {
		this.paypfnt = paypfnt;
	}
	public int getPaystal() {
		return paystal;
	}
	public void setPaystal(int paystal) {
		this.paystal = paystal;
	}
	public int getPaystald() {
		return paystald;
	}
	public void setPaystald(int paystald) {
		this.paystald = paystald;
	}
	public int getPaynight() {
		return paynight;
	}
	public void setPaynight(int paynight) {
		this.paynight = paynight;
	}
	public int getPayyear() {
		return payyear;
	}
	public void setPayyear(int payyear) {
		this.payyear = payyear;
	}
	public int getPayaddhl() {
		return payaddhl;
	}
	public void setPayaddhl(int payaddhl) {
		this.payaddhl = payaddhl;
	}
	public int getPaygrade() {
		return paygrade;
	}
	public void setPaygrade(int paygrade) {
		this.paygrade = paygrade;
	}
	public int getPaypromo() {
		return paypromo;
	}
	public void setPaypromo(int paypromo) {
		this.paypromo = paypromo;
	}
	public int getPayadrtn() {
		return payadrtn;
	}
	public void setPayadrtn(int payadrtn) {
		this.payadrtn = payadrtn;
	}
	public int getPaycplm1() {
		return paycplm1;
	}
	public void setPaycplm1(int paycplm1) {
		this.paycplm1 = paycplm1;
	}
	public int getPaycplm2() {
		return paycplm2;
	}
	public void setPaycplm2(int paycplm2) {
		this.paycplm2 = paycplm2;
	}
	public int getPaycplm3() {
		return paycplm3;
	}
	public void setPaycplm3(int paycplm3) {
		this.paycplm3 = paycplm3;
	}
	public int getPayempfd() {
		return payempfd;
	}
	public void setPayempfd(int payempfd) {
		this.payempfd = payempfd;
	}
	public int getPaysars() {
		return paysars;
	}
	public void setPaysars(int paysars) {
		this.paysars = paysars;
	}
	public int getPayextra() {
		return payextra;
	}
	public void setPayextra(int payextra) {
		this.payextra = payextra;
	}
	public int getPaygeins() {
		return paygeins;
	}
	public void setPaygeins(int paygeins) {
		this.paygeins = paygeins;
	}
	public int getPaydpins() {
		return paydpins;
	}
	public void setPaydpins(int paydpins) {
		this.paydpins = paydpins;
	}
	public int getPaylbins() {
		return paylbins;
	}
	public void setPaylbins(int paylbins) {
		this.paylbins = paylbins;
	}
	public int getPayretir() {
		return payretir;
	}
	public void setPayretir(int payretir) {
		this.payretir = payretir;
	}
	public int getPaytax() {
		return paytax;
	}
	public void setPaytax(int paytax) {
		this.paytax = paytax;
	}
	public int getPaygedp() {
		return paygedp;
	}
	public void setPaygedp(int paygedp) {
		this.paygedp = paygedp;
	}
	public int getPayvetd1() {
		return payvetd1;
	}
	public void setPayvetd1(int payvetd1) {
		this.payvetd1 = payvetd1;
	}
	public int getPayhloa1() {
		return payhloa1;
	}
	public void setPayhloa1(int payhloa1) {
		this.payhloa1 = payhloa1;
	}
	public int getPayhloa2() {
		return payhloa2;
	}
	public void setPayhloa2(int payhloa2) {
		this.payhloa2 = payhloa2;
	}
	public int getPayhloa3() {
		return payhloa3;
	}
	public void setPayhloa3(int payhloa3) {
		this.payhloa3 = payhloa3;
	}
	public int getPayhloa4() {
		return payhloa4;
	}
	public void setPayhloa4(int payhloa4) {
		this.payhloa4 = payhloa4;
	}
	public int getPayhloa5() {
		return payhloa5;
	}
	public void setPayhloa5(int payhloa5) {
		this.payhloa5 = payhloa5;
	}
	public int getPayhloa6() {
		return payhloa6;
	}
	public void setPayhloa6(int payhloa6) {
		this.payhloa6 = payhloa6;
	}
	public int getPaydloan() {
		return paydloan;
	}
	public void setPaydloan(int paydloan) {
		this.paydloan = paydloan;
	}
	public int getPaymhous() {
		return paymhous;
	}
	public void setPaymhous(int paymhous) {
		this.paymhous = paymhous;
	}
	public int getPaynspfd() {
		return paynspfd;
	}
	public void setPaynspfd(int paynspfd) {
		this.paynspfd = paynspfd;
	}
	public int getPaypark1() {
		return paypark1;
	}
	public void setPaypark1(int paypark1) {
		this.paypark1 = paypark1;
	}
	public int getPaypark2() {
		return paypark2;
	}
	public void setPaypark2(int paypark2) {
		this.paypark2 = paypark2;
	}
	public int getPaygym() {
		return paygym;
	}
	public void setPaygym(int paygym) {
		this.paygym = paygym;
	}
	public int getPaylien() {
		return paylien;
	}
	public void setPaylien(int paylien) {
		this.paylien = paylien;
	}
	public int getPaymeal() {
		return paymeal;
	}
	public void setPaymeal(int paymeal) {
		this.paymeal = paymeal;
	}
	public int getPaycardf() {
		return paycardf;
	}
	public void setPaycardf(int paycardf) {
		this.paycardf = paycardf;
	}
	public int getPaydorm1() {
		return paydorm1;
	}
	public void setPaydorm1(int paydorm1) {
		this.paydorm1 = paydorm1;
	}
	public int getPaydorm2() {
		return paydorm2;
	}
	public void setPaydorm2(int paydorm2) {
		this.paydorm2 = paydorm2;
	}
	public int getPaydorm3() {
		return paydorm3;
	}
	public void setPaydorm3(int paydorm3) {
		this.paydorm3 = paydorm3;
	}
	public int getPayother1() {
		return payother1;
	}
	public void setPayother1(int payother1) {
		this.payother1 = payother1;
	}
	public int getPayother2() {
		return payother2;
	}
	public void setPayother2(int payother2) {
		this.payother2 = payother2;
	}
	public int getPayother3() {
		return payother3;
	}
	public void setPayother3(int payother3) {
		this.payother3 = payother3;
	}
	public int getPayagen1() {
		return payagen1;
	}
	public void setPayagen1(int payagen1) {
		this.payagen1 = payagen1;
	}
	public int getPaytotal() {
		return paytotal;
	}
	public void setPaytotal(int paytotal) {
		this.paytotal = paytotal;
	}
	public int getPaytotis() {
		return paytotis;
	}
	public void setPaytotis(int paytotis) {
		this.paytotis = paytotis;
	}
	public int getPaytlded() {
		return paytlded;
	}
	public void setPaytlded(int paytlded) {
		this.paytlded = paytlded;
	}
	public int getPaynet() {
		return paynet;
	}
	public void setPaynet(int paynet) {
		this.paynet = paynet;
	}
	public void setContract(String contract) {
		this.contract = contract;
	}
	public int getPatracod() {
		return patracod;
	}
	public void setPatracod(int patracod) {
		this.patracod = patracod;
	}
	public Map getValMap() {
		return valMap;
	}
	public void setValMap(Map valMap) {
		this.valMap = valMap;
	}
	public int getPay() {
		return pay;
	}
	public void setPay(int pay) {
		this.pay = pay;
	}
	public int getNt1() {
		return nt1;
	}
	public void setNt1(int nt1) {
		this.nt1 = nt1;
	}
	public int getNt2() {
		return nt2;
	}
	public void setNt2(int nt2) {
		this.nt2 = nt2;
	}
	public String getDeposit() {
		return deposit;
	}
	public void setDeposit(String deposit) {
		this.deposit = deposit;
	}
	public String getAcno() {
		return acno;
	}
	public void setAcno(String acno) {
		this.acno = acno;
	}
	public int getPayamt() {
		return payamt;
	}
	public void setPayamt(int payamt) {
		this.payamt = payamt;
	}
	public String getBankflag() {
		return bankflag;
	}
	public void setBankflag(String bankflag) {
		this.bankflag = bankflag;
	}
	public String getComp() {
		return comp;
	}
	public void setComp(String comp) {
		this.comp = comp;
	}
 
}
	