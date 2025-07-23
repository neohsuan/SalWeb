package gov.vghks.sal.model;

public class Pareport {
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 性質 1-月報 2-上傳主計(加項) 3-上傳主計(扣項) 5-年報表 6-不休假加班費 休假補助*/
    private int paytpe;
    /** 編號*/
    private int paynum;
    /** 支扣代碼*/
    private String paycode;
    /** 金額*/
    private int payamt;
    /**醫院別*/
    private String hid;
    
	public int getPaytpe() {
		return paytpe;
	}
	public void setPaytpe(int paytpe) {
		this.paytpe = paytpe;
	}
	public int getPaynum() {
		return paynum;
	}
	public void setPaynum(int paynum) {
		this.paynum = paynum;
	}
	public String getPaycode() {
		return paycode;
	}
	public void setPaycode(String paycode) {
		this.paycode = paycode;
	}
	public int getPayamt() {
		return payamt;
	}
	public void setPayamt(int payamt) {
		this.payamt = payamt;
	}
	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
	}
}
