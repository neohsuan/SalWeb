package gov.vghks.sal.model;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

public class Psbasic {
	SimpleDateFormat sdf_d=new SimpleDateFormat("yyyy-MM-dd");
	private static final long serialVersionUID = 1L;
	static Logger log = Logger.getLogger(Psbasic.class); //呼叫log4j
	
	private String pbidtno = "";  //身分證號
	private String pbnamefd = ""; //姓名
	private String pbposino = ""; //職務編號
	private String pbposmsk = ""; //在離職註記
	private String pbcardno = ""; //卡號
	private String pbcatego = ""; //職類
	private String pbwkcode = ""; //工作單位代碼
	private String pbwkname = ""; //工作單位
	private String pbtitcod = ""; //職稱一代碼
	private String pbtitnme = ""; //職稱一
	private String pbpagrad = ""; //俸級
	private String pbpapont = ""; //俸點
	private String pblastot = ""; //成本中心
	private String pbporank = ""; //官職等
	private java.sql.Date pbposdat; //到職日期
	private java.sql.Date pbgocdat; //公職起啟日期
	private java.sql.Date pbgoadat; //到院日期
	private String pbpostus = ""; //現職狀況
	private String pbpchief = ""; //主管級別
	private String pbdomadr = ""; //戶籍地址
	private String hid = "";
	
	public String getPbidtno() {
		return pbidtno;
	}
	public void setPbidtno(String pbidtno) {
		this.pbidtno = pbidtno;
	}
	public String getPbnamefd() {
		return pbnamefd;
	}
	public void setPbnamefd(String pbnamefd) {
		this.pbnamefd = pbnamefd;
	}
	public String getPbposino() {
		return pbposino;
	}
	public void setPbposino(String pbposino) {
		this.pbposino = pbposino;
	}
	public String getPbposmsk() {
		return pbposmsk;
	}
	public void setPbposmsk(String pbposmsk) {
		this.pbposmsk = pbposmsk;
	}
	public String getPbcardno() {
		return pbcardno;
	}
	public void setPbcardno(String pbcardno) {
		this.pbcardno = pbcardno;
	}
	public String getPbcatego() {
		return pbcatego;
	}
	public void setPbcatego(String pbcatego) {
		this.pbcatego = pbcatego;
	}
	public String getPbwkcode() {
		return pbwkcode;
	}
	public void setPbwkcode(String pbwkcode) {
		this.pbwkcode = pbwkcode;
	}
	public String getPbwkname() {
		return pbwkname;
	}
	public void setPbwkname(String pbwkname) {
		this.pbwkname = pbwkname;
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
	public String getPblastot() {
		return pblastot;
	}
	public void setPblastot(String pblastot) {
		this.pblastot = pblastot;
	}
	public String getPbporank() {
		return pbporank;
	}
	public void setPbporank(String pbporank) {
		this.pbporank = pbporank;
	}
	public String getPbpostus() {
		return pbpostus;
	}
	public void setPbpostus(String pbpostus) {
		this.pbpostus = pbpostus;
	}
	public String getPbpchief() {
		return pbpchief;
	}
	public void setPbpchief(String pbpchief) {
		this.pbpchief = pbpchief;
	}
	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
	}
	public java.sql.Date getPbposdat() {
		return pbposdat;
	}
	public void setPbposdat(java.sql.Date pbposdat) {
		this.pbposdat = pbposdat;
	}
	public java.sql.Date getPbgocdat() {
		return pbgocdat;
	}
	public void setPbgocdat(java.sql.Date pbgocdat) {
		this.pbgocdat = pbgocdat;
	}
	public java.sql.Date getPbgoadat() {
		return pbgoadat;
	}
	public void setPbgoadat(java.sql.Date pbgoadat) {
		this.pbgoadat = pbgoadat;
	}
	public String getPbdomadr() {
		return pbdomadr;
	}
	public void setPbdomadr(String pbdomadr) {
		this.pbdomadr = pbdomadr;
	}
    
    
}
