package gov.vghks.sal.model;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

public class PsInsu {
	SimpleDateFormat sdf_d=new SimpleDateFormat("yyyy-MM-dd");
	private static final long serialVersionUID = 1L;
	static Logger log = Logger.getLogger(PsInsu.class); //呼叫log4j
	
	private String pbidtno="";      	//身分證字號
	private int inver=0;	      		//版次:0最新版,有異動+1
	private String hid="";      		//醫院代碼
	private String pbsupfg="";      	//工作院區
	private String paycard="";      	//卡號
	private String pbnamefd="";      	//姓名
	private java.sql.Date pbbirthd;      	//出生日期
	private String pbposino;      		//職務編號
	private String pbporank;      		//官職等
	private String pbpagrad;      		//俸級
	private String pbpapont;	      	//俸點
	private String pbpchief;	      	//主管級別
	private String pbtitcod;			//職稱代碼
	private String pbtitnme;			//職稱中文
	private String instus;				//在保狀態 2退保,3薪資調整,4加保,5留職停薪續保,6留停續保人員回職復薪
	private java.sql.Date pblsvdat;		//生效日
	private int inreal;					//健保實際工資
	private int lbreal;					//勞保實際工資
	private int dpinig;					//健保投保級距
	private int lbinig;					//勞保投保級距
	private int wrinig;					//勞退投保級距 1500-150000
	private int occinig;				//職災投保級距 26400-72800
	private int selfvol;				//個人自願提繳率 0-6
	private String lbspflg;				//勞保特殊身分別 一般、B2、P0、S0、V0、H0、H3、H4
	private String dpspflg;				//健保特殊身分別 0一般、3_全免、4_全免有上限、2_補助1/2、1_補助1/4
	private java.sql.Date inupdt;		//匯出日期
	private String inrmk;				//備註
	private String updnm;				//記錄人員
	private java.sql.Timestamp updtm;//日期時戳  
    
    /** 出生日期_String*/
    private String strbirthd;
    /** 生效日_String*/
    private String strlsvdat;
    /** 匯出日期_String*/
    private String strinupdt;
    
  
	public String getPbidtno() {
		return pbidtno;
	}

	public void setPbidtno(String pbidtno) {
		this.pbidtno = pbidtno;
	}

	public int getInver() {
		return inver;
	}

	public void setInver(int inver) {
		this.inver = inver;
	}

	public String getHid() {
		return hid;
	}

	public void setHid(String hid) {
		this.hid = hid;
	}

	public String getPbsupfg() {
		return pbsupfg;
	}

	public void setPbsupfg(String pbsupfg) {
		this.pbsupfg = pbsupfg;
	}

	public String getPaycard() {
		return paycard;
	}

	public void setPaycard(String paycard) {
		this.paycard = paycard;
	}

	public String getPbnamefd() {
		return pbnamefd;
	}

	public void setPbnamefd(String pbnamefd) {
		this.pbnamefd = pbnamefd;
	}

	public java.sql.Date getPbbirthd() {
		return pbbirthd;
	}

	public void setPbbirthd(java.sql.Date pbbirthd) {
		this.pbbirthd = pbbirthd;
	}

	public String getPbposino() {
		return pbposino;
	}

	public void setPbposino(String pbposino) {
		this.pbposino = pbposino;
	}

	public String getPbporank() {
		return pbporank;
	}

	public void setPbporank(String pbporank) {
		this.pbporank = pbporank;
	}

	public String getPbpagrad() {
		return pbpagrad;
	}

	public void setPbpagrad(String pbpagrad) {
		this.pbpagrad = pbpagrad;
	}

	public String getPbpapont() {
		return pbpapont;
	}

	public void setPbpapont(String pbpapont) {
		this.pbpapont = pbpapont;
	}

	public String getPbpchief() {
		return pbpchief;
	}

	public void setPbpchief(String pbpchief) {
		this.pbpchief = pbpchief;
	}

	public String getPbtitcod() {
		return pbtitcod;
	}

	public void setPbtitcod(String pbtitcod) {
		this.pbtitcod = pbtitcod;
	}

	public String getPbtitnme() {
		return pbtitnme;
	}

	public void setPbtitnme(String pbtitnme) {
		this.pbtitnme = pbtitnme;
	}

	public String getInstus() {
		return instus;
	}

	public void setInstus(String instus) {
		this.instus = instus;
	}

	public java.sql.Date getPblsvdat() {
		return pblsvdat;
	}

	public void setPblsvdat(java.sql.Date pblsvdat) {
		this.pblsvdat = pblsvdat;
	}

	public int getInreal() {
		return inreal;
	}

	public void setInreal(int inreal) {
		this.inreal = inreal;
	}

	public int getDpinig() {
		return dpinig;
	}

	public void setDpinig(int dpinig) {
		this.dpinig = dpinig;
	}

	public int getLbinig() {
		return lbinig;
	}

	public void setLbinig(int lbinig) {
		this.lbinig = lbinig;
	}

	public int getWrinig() {
		return wrinig;
	}

	public void setWrinig(int wrinig) {
		this.wrinig = wrinig;
	}

	public int getOccinig() {
		return occinig;
	}

	public void setOccinig(int occinig) {
		this.occinig = occinig;
	}

	public int getSelfvol() {
		return selfvol;
	}

	public void setSelfvol(int selfvol) {
		this.selfvol = selfvol;
	}

	public String getLbspflg() {
		return lbspflg;
	}

	public void setLbspflg(String lbspflg) {
		this.lbspflg = lbspflg;
	}

	public java.sql.Date getInupdt() {
		return inupdt;
	}

	public void setInupdt(java.sql.Date inupdt) {
		this.inupdt = inupdt;
	}

	public String getInrmk() {
		return inrmk;
	}

	public void setInrmk(String inrmk) {
		this.inrmk = inrmk;
	}

	public String getUpdnm() {
		return updnm;
	}

	public void setUpdnm(String updnm) {
		this.updnm = updnm;
	}

	public java.sql.Timestamp getUpdtm() {
		return updtm;
	}

	public void setUpdtm(java.sql.Timestamp updtm) {
		this.updtm = updtm;
	}

	public String getStrbirthd() {
		return sdf_d.format(this.pbbirthd);		
	}

	public String getStrlsvdat() {
		return sdf_d.format(this.pblsvdat);		
	}

	public String getStrinupdt() {
		return sdf_d.format(this.inupdt);		
	}

	public int getLbreal() {
		return lbreal;
	}

	public void setLbreal(int lbreal) {
		this.lbreal = lbreal;
	}

	public String getDpspflg() {
		return dpspflg;
	}

	public void setDpspflg(String dpspflg) {
		this.dpspflg = dpspflg;
	}
  

	
}
