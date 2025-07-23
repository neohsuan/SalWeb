package gov.vghks.sal.model;

public class Pabasadr {
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
    /** 身份證字號*/
    private String payidno;
    /** 住址*/
    private String payaddrs;
    
	public String getPayidno() {
		return payidno;
	}
	public void setPayidno(String payidno) {
		this.payidno = payidno;
	}
	public String getPayaddrs() {
		return payaddrs;
	}
	public void setPayaddrs(String payaddrs) {
		this.payaddrs = payaddrs;
	}
    
}

