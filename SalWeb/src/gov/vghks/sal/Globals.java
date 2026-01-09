package gov.vghks.sal;

import java.io.Serializable;

public class Globals implements Serializable{
	private static final long serialVersionUID = -2859744183434430985L;
	//DATA_SOURCE========================================================================================================
	public static final String dataSource_DSNP ="salDSNds";	//連接主機 高榮
	//public static final String dataSource_NT =	"salNTds";	//連接NT mgtdb 高榮
	public static final String dataSource_NT =	"salds";	//連接NT mgtdb 高榮
	public static final String dataSource_DSNP_PT ="salDSNds_pt";	//連接主機 屏榮
	public static final String dataSource_NT_PT =	"salds_pt";		//連接NT mgtdb 屏榮
	public static final String dataSource_DSNP_LH ="salDSNds_lh";	//連接主機 聯醫
	public static final String dataSource_NT_LH =	"salds_lh";		//連接NT mgtdb 聯醫
	
	public final static String gap_id	   		  = "appa01";		//薪資帳密
	public final static String gap_pwd	   		  = "pa01ap";
	
	public static final String gNcontract =	"2015-1-1";	//新制契約生效日期
	public static final String gPFNew =	"2024-8-1";		//契約績效組成KEY,含此日期前,職稱+00000
	public static final String gPensionFund =	"2021-1-1";	//退撫基金溯自110年1月1日免納入所得稅
	public static final String gCivilServants =	"2023-7-1";	//112年7月公保及退撫新制上路
	
	public static final String SESSION_SYS_ADMIN = "PA_ADM";	//薪資系統負責人	
	public static final String SESSION_SYS_EDIT = "PA_EDIT";	//薪資系統_維護角色
	public static final String SESSION_SYS_VIEW = "PA_VIEW";	//薪資系統_查詢角色
	public static final String SESSION_SYS_BONES = "PA_BONES";	//薪資系統_員工獎金角色
	public static final String SESSION_SYS_TAX = "PA_TAX";		//薪資系統_所得稅角色
	

	//---------------------投保級距上限
	public static final int gDpinMax = 313000;	//健保(2025.1.1 219500->313000)
	public static final int gLbInMax = 45800;	//勞保
	public static final int gOccInMax = 72800;	//職災
	//---------------------最低基本工資 (2024.1.1 26400->27470)(2025.1.1 27470->28590)(2026.1.1 28590->29500)
	public static final int gBasSalMin = 29500;	
	//---------------------所得淨額起扣標準2024.1.5 88501 2025.12.29 90501
	public static final int gSTaxAmt =90501;	
	//---------------------所得扣繳2015.5.4
	public static final double gTaxTate=0.05;
	//---------------------補充費費基
	public static final double gNHIRate=0.0211;
}

