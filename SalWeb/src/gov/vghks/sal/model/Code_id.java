package gov.vghks.sal.model;

public class Code_id {
	/** 序列版本ID */
	private static final long serialVersionUID = 2366284486426627547L;
	/** 身分代碼*/
    private String id;
    /** 身分中文*/
    private String id_name;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId_name() {
		return id_name;
	}
	public void setId_name(String id_name) {
		this.id_name = id_name;
	}
}
