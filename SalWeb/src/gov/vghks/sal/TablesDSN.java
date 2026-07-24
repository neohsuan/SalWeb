package gov.vghks.sal;

public enum TablesDSN {
	KSVGH(
			"1A0", 		//1醫院別
			"PS.UPUSER",//2使用者資料檔
			"PS.PSBASIC",//3基本資料檔
			"PS.PSTAB0",//4人事代碼檔
			"PS.PSCODE",//5人事代碼檔明細	
			"PS.PSEXPERI",//6經歷
			"PS.PSNURBAS",//7護理人員--基本資料檔
			"PS.PSINSU",//8員工保險
			"PS.PSDETLB",//9勞保繳費明細
			"PS.PSDETWR",//10勞工退休金繳費明細
			"PS.PSDETDP"//11健保繳費明細
	),
	PTVGH(
			"2A0", 		//1醫院別
			"PS.UPUSER",//2使用者資料檔
			"PS.PTBASIC",//3基本資料檔
			"PS.PTTAB0",//4人事代碼檔時戳
			"PS.PTCODE",//5人事代碼檔明細	
			"PS.PTEXPERI",//6經歷
			"PS.PTNURBAS",//7護理人員--基本資料檔
			"",//8員工保險
			"",//9勞保繳費明細
			"",//10勞工退休金繳費明細
			""//11健保繳費明細
	),
	KMUH(
			"2A0", 		//1醫院別
			"PS.UPUSER",//2使用者資料檔
			"PS.LHBASIC",//3基本資料檔
			"PS.LHTAB0",//4人事代碼檔時戳
			"PS.LHCODE",//5人事代碼檔明細	
			"PS.LHEXPERI",//6經歷
			"PS.LHNURBAS",//7護理人員--基本資料檔
			"",//8員工保險
			"",//9勞保繳費明細
			"",//10勞工退休金繳費明細
			""//11健保繳費明細
	),
	;
	
	public final String hid;   				//1醫院別
	public final String upuser;   			//2使用者資料檔
	public final String tbasic;				//3基本資料檔
	public final String ttab0;				//4人事代碼檔
	public final String tcode;				//5人事代碼檔
	public final String texperi;			//6經歷異動檔
	public final String tnurbas;			//7護理人員--基本資料檔
	public final String tinsu;				//8員工保險
	public final String tdetlb;				//9勞保繳費明細
	public final String tdetwr; 			//10勞工退休金繳費明細
	public final String tdetdp;				//11健保繳費明細
	
	TablesDSN(
		final String hid, 
		final String upuser,
		final String tbasic,
		final String ttab0,
		final String tcode ,	
		final String texperi,		
		final String tnurbas,		
		final String tinsu,
		final String tdetlb,
		final String tdetwr,
		final String tdetdp
	)
	{
        this.hid = hid;
        this.upuser = upuser;
        this.tbasic = tbasic;
        this.ttab0 = ttab0;
        this.tcode = tcode  ;
        this.texperi = texperi;
        this.tnurbas = tnurbas;
    	this.tinsu = tinsu ;
    	this.tdetlb=tdetlb  ;
    	this.tdetwr= tdetwr ;
    	this.tdetdp=tdetdp  ;    	
    }
	
	/**
	 * 依hid取得某醫院資訊<br>
	 * @param hid
	 * @return
	 */
	public static TablesDSN getTables(final String hid){
		TablesDSN rs = null;
		for (final TablesDSN t : TablesDSN.values()) {
			if(hid.equals(t.hid)) {
				rs = t;
				break;
			}
		}
		return rs;
	}
}
