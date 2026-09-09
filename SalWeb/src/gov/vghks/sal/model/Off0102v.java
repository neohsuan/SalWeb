package gov.vghks.sal.model;
import java.math.BigDecimal;

public class Off0102v {
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 年月*/
    private String ym;
    /** 卡號*/
    private String userid;
    /** 姓名*/
    private String empname;
    /** 職號分類 posino第2碼 K全日 F半日 P不分科 T研究助理 A職員W醫事職員 G技工H工友 B駐警 C聘用住院 E契約住院 J契約主治*/
    private String pos;
    /** 假別*/
    private String offtype;
    /** 時數*/
    private int offhour;
    /** 累計時數*/
    private int offhoursum;
    /** 日數*/
    private BigDecimal offday;
    /** 累計日數(不含當月)*/
    private BigDecimal offdaysum;
    /** 不發給工資天數*/
    private BigDecimal nopay;
    /** 折半天數 */
    private BigDecimal payhalf;
    
	public String getYm() {
		return ym;
	}
	public void setYm(String ym) {
		this.ym = ym;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getEmpname() {
		return empname;
	}
	public void setEmpname(String empname) {
		this.empname = empname;
	}
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	public String getOfftype() {
		return offtype;
	}
	public void setOfftype(String offtype) {
		this.offtype = offtype;
	}
	public int getOffhour() {
		return offhour;
	}
	public void setOffhour(int offhour) {
		this.offhour = offhour;
	}
	public int getOffhoursum() {
		return offhoursum;
	}
	public void setOffhoursum(int offhoursum) {
		this.offhoursum = offhoursum;
	}
	public BigDecimal getOffday() {
		return offday;
	}
	public void setOffday(BigDecimal offday) {
		this.offday = offday;
	}
	public BigDecimal getOffdaysum() {
		return offdaysum;
	}
	public void setOffdaysum(BigDecimal offdaysum) {
		this.offdaysum = offdaysum;
	}
	public BigDecimal getNopay() {
		return nopay;
	}
	public void setNopay(BigDecimal nopay) {
		this.nopay = nopay;
	}
	public BigDecimal getPayhalf() {
		return payhalf;
	}
	public void setPayhalf(BigDecimal payhalf) {
		this.payhalf = payhalf;
	}
	   
}
