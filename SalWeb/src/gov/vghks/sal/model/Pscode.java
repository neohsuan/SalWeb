package gov.vghks.sal.model;

public class Pscode {
	/** 序列版本ID */
	private static final long serialVersionUID = 1L;
	/** 代碼類別*/
    private String pctype;
    /** 代碼編號*/
    private String pcsysno;
    /** 代碼名稱*/
    private String pcsysnm;
    /** 停用註記*/
    private String pccanel;
    /**顯示排列順序*/
    private String pcsort;
    /**級數上限*/
    private int maxlevel;
    
	public String getPctype() {
		return pctype;
	}
	public void setPctype(String pctype) {
		this.pctype = pctype;
	}
	public String getPcsysno() {
		return pcsysno;
	}
	public void setPcsysno(String pcsysno) {
		this.pcsysno = pcsysno;
	}
	public String getPcsysnm() {
		return pcsysnm;
	}
	public void setPcsysnm(String pcsysnm) {
		this.pcsysnm = pcsysnm;
	}
	public String getPccanel() {
		return pccanel;
	}
	public void setPccanel(String pccanel) {
		this.pccanel = pccanel;
	}
	public String getPcsort() {
		return pcsort;
	}
	public void setPcsort(String pcsort) {
		this.pcsort = pcsort;
	}
	public int getMaxlevel() {
		return maxlevel;
	}
	public void setMaxlevel(int maxlevel) {
		this.maxlevel = maxlevel;
	}
    
    
    }
