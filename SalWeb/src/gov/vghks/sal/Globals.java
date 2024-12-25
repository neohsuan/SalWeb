package gov.vghks.sal;

import java.io.Serializable;

public class Globals implements Serializable{
	private static final long serialVersionUID = -2859744183434430985L;
	//DATA_SOURCE========================================================================================================
	public static final String dataSource_DSNP ="salDSNds";	//連接主機
	public static final String dataSource_NT =	"salNTds";	//連接NT mgtdb
	public static final String gNcontract =	"2015-1-1";	//新制契約生效日期
	
	
	
	public static final String SESSION_USER_ID = "opsUserID";
	public static final String SESSION_USER_NAME = "opsUsrName";
	public static final String SESSION_USER_COST_ID = "opsCostId";
	public static final String SESSION_USER_OU_NAME = "opsOuName";
	public static final String SESSION_PSWKTYP = "opsUserPswktyp";//職稱代碼
	public static final String SESSION_USER_MAIL = "opsMail"; //信箱
	public static final String SESSION_USER_HID = "opsHID"; //醫院別
	public static final String SESSION_SYS_ADMIN = "OPS_ADM";
	
}

