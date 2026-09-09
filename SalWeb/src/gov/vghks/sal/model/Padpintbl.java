package gov.vghks.sal.model;

import java.io.Serializable;
//全民健康保險負擔金額表 
public class Padpintbl implements Serializable{
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	private int dpinlow=0;      	//最低月投保金額
	private int dpinhigh=0;      	//最高月投保金額
	private int dpinamt=0;      	//健保費
	private String payupdid="";     //負責人ID
	private int dpindept1=0;      	//單位負擔公保身份
	private int dpindept3=0;      	//單位負擔勞保身份
	
	public int getDpinlow() {
		return dpinlow;
	}
	public void setDpinlow(int dpinlow) {
		this.dpinlow = dpinlow;
	}
	public int getDpinhigh() {
		return dpinhigh;
	}
	public void setDpinhigh(int dpinhigh) {
		this.dpinhigh = dpinhigh;
	}
	public int getDpinamt() {
		return dpinamt;
	}
	public void setDpinamt(int dpinamt) {
		this.dpinamt = dpinamt;
	}
	public String getPayupdid() {
		return payupdid;
	}
	public void setPayupdid(String payupdid) {
		this.payupdid = payupdid;
	}
	public int getDpindept1() {
		return dpindept1;
	}
	public void setDpindept1(int dpindept1) {
		this.dpindept1 = dpindept1;
	}
	public int getDpindept3() {
		return dpindept3;
	}
	public void setDpindept3(int dpindept3) {
		this.dpindept3 = dpindept3;
	}
		  
}
