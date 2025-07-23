package gov.vghks.sal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 醫院別對應<br>
 * @author 
 *
 */
public enum Hospitals {
	
	/**
	 * 已知高榮規則同聯合<br>
	 */
	
	KSVGH(
		"1A0", 
		"高雄榮民總醫院",
		"高榮榮總",
		"高",
		"高榮",
		"KVGH",
		"salds",
		"salDSNds",
		"appa01",
		"pa01ap",
		"\\\\Ap_pd1.vghks.gov.tw\\apps_exe\\SAL\\temp\\1A0\\",
		"高雄市左營區大中一路３８６號",
		"潘麗馡",
		"07-3422121",		
		"08261077",
		"E12",
		"120300016"
	),
	PTVGH(
		"2A0", 
		"屏東榮民總醫院",
		"屏東榮總",
		"屏",
		"屏榮",		
		"VHLC",
		"salds_pt",
		"salDSNds_pt",
		"appa02",
		"ptpa02ap",
		"\\\\Ap_pd1.vghks.gov.tw\\apps_exe\\SAL\\temp\\2A0\\",
		"屏東縣屏東市崇武里榮總東路１號",
		"王瑞祥",
		"08-7557885",
		"92362346",
		"T90",
		"900816938"
	),
	KMUH(
		"3A0", 
		"高雄市立聯合醫院",
		"高市聯醫",
		"聯",
		"聯醫",		
		"",
		"salds_lh",
		"salDSNds_lh",
		"appa04",
		"lhpa04ap$$--",
		"\\\\Ap_pd1.vghks.gov.tw\\apps_exe\\SAL\\temp\\3A0\\",
		"高雄市鼓山區中華一路９７６號",
		"馬光遠",
		"07-5552565",
		"",
		"",
		""
	),
	;
	
	public final String hid;   				//醫院別
	public final String nmFull;				//醫院全銜
	public final String nmShort;			//醫院簡稱
	public final String nm1w;				//醫院一字簡稱
	public final String nm2w;				//醫院二字簡稱
	public final String kmOrgCode;			//km機構代碼
	public final String dataSourceNT;		//DATA_SOURCE_NT
	public final String dataSourceDSN;		//DATA_SOURCE_主機
	public final String gap_id;				//ap帳號
	public final String gap_pwd;			//ap密碼
	public final String fileSRV;			//local_file
	public final String adr;				//醫院地址
	public final String agent;				//扣繳義務人
	public final String tel;				//機關電話
	public final String taxNo;			//申報單位統一編號
	public final String agencyNo;		//稽徵機關代號
	public final String taxID;			//申報單位稅籍編號
	
	Hospitals(
		final String hid, 
		final String nmFull,
		final String nmShort,
		final String nm1w,
		final String nm2w,
		final String kmOrgCode,
		final String dataSourceNT,
		final String dataSourceDSN,
		final String gap_id,
		final String gap_pwd,
		final String fileSRV,
		final String adr,
		final String agent,
		final String tel,
		final String taxNo,
		final String agencyNo,
		final String taxID
	){
        this.hid = hid;
        this.nmFull = nmFull;
        this.nmShort = nmShort;
        this.nm1w = nm1w;
        this.nm2w = nm2w;
        this.kmOrgCode = kmOrgCode;
        this.dataSourceNT = dataSourceNT;
        this.dataSourceDSN = dataSourceDSN;
    	this.gap_id = gap_id;
    	this.gap_pwd = gap_pwd;    	
    	this.fileSRV = fileSRV;
    	this.adr = adr;
        this.agent = agent;
        this.tel = tel;
        this.taxNo = taxNo;
    	this.agencyNo = agencyNo;
    	this.taxID = taxID;
    }
	/**
	 * 依hid取得某醫院資訊<br>
	 * @param hid
	 * @return
	 */
	public static Hospitals getOneHosp(final String hid){
		Hospitals rs = null;
		for (final Hospitals t : Hospitals.values()) {
			if(hid.equals(t.hid)) {
				rs = t;
				break;
			}
		}
		return rs;
	}
	/**
	 * 抓全部的HID清單<br>
	 * 專for測試功能用<br>
	 * @param hid
	 * @return
	 */
	public static List<Map<String, Object>> getAll(){
		final List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
		for (final Hospitals t : Hospitals.values()) {
			final Map<String, Object> m = new HashMap<String, Object>();
			m.put("hid", t.hid);
			m.put("nmFull", t.nmFull);
			rs.add(m);
		}
		return rs;
	}
	/**
	 * 抓全部的HID清單--列舉版<br>
	 * 專for測試功能用<br>
	 * @param hid
	 * @return
	 */
	public static List<Hospitals> getAllEnumLi(){
		final List<Hospitals> rs = new ArrayList<Hospitals>();
		for (final Hospitals t : Hospitals.values()) {
			rs.add(t);
		}
		return rs;
	}
}