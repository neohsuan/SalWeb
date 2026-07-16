package gov.vghks.sal;

public enum Tables {
	KSVGH(
			"1A0", 
			"PA.PATAXPSN",
			"PA.PATAXTRAN",
			"PA.PATAXCASH",
			"PA.PABASPSN",
			"PA.PABASPAY",
			"PA.PABATPAY",
			"PA.PATRACODE",
			"PA.PADEPT",
			"PA.PATITLE",
			"PA.PARANKCODE",
			"PA.PACATGTBL",
			"PA.PAEXPFTBL",
			"PA.PARNK4TBL",
			"PA.PABANK",
			"PA.PATAXTBL",
			"PA.PAPFTL",
			"PA.PADPINTBL",
			"PA.PALBINTBL",
			"PA.PALBRPTBL",
			"PA.PAPOSI",
			"PA.PARANK",
			"PA.PACSPF",
			"PA.PADATPSN",
			"PA.PADATPSN_DW",
			"PA.PADATPAY",
			"PA.PADATPAY_DW",
			"PA.PAPAYITM",
			"PA.PABASADR",
			"PA.PAHIST",
			"PA.PATRAPSN",
			"PA.PATRAPSN_DW",
			"PA.PATRAPAY",
			"PA.PATRAPAY_DW",
			"PA.PAREPDETAIL",
			"PA.PAREPORT",
			"PA.PANHICAL",
			"PA.PANHIDET",
			"PA.PATEACHAW",
			
			"ZPATITLE",
			"ZPARANKCODE",
			"ZPABANK",
			"ZPADEPT",
			"ZPARNK4TBL",
			"ZPAEXPFTBL",
			"ZPATRACODE",
			"ZPABASPSN",
			"ZPABATPAY",
			"ZPATRAPSN",
			"ZPATRAPAY",
			"ZPAHIST",
			"ZPADPINTBL",
			"ZPALBINTBL",
			"ZPALBRPTBL",
			"ZPACSPF",
			"ZPAPAYITM",
			"ZPATAXTRAN",
			"ZPATAXCASH",
			"ZPABASADR"
	),
	PTVGH(
		"2A0", 
		"PA.PTTAXPSN",
		"PA.PTTAXTRAN",
		"PA.PTTAXCASH",
		"PA.PTBASPSN",
		"PA.PTBASPAY",
		"PA.PTBATPAY",
		"PA.PTTRACODE",
		"PA.PTDEPT",
		"PA.PTTITLE",
		"PA.PTRANKCE",
		"PA.PTCATGTBL",
		"PA.PTEXPFTBL",
		"PA.PTRNK4TBL",
		"PA.PTBANK",
		"PA.PTTAXTBL",
		"PA.PTPFTL",
		"PA.PTDPINTBL",
		"PA.PTLBINTBL",
		"PA.PTLBRPTBL",
		"PA.PTPOSI",
		"PA.PTRANK",
		"PA.PTCSPF",
		"PA.PTDATPSN",
		"PA.PTDA#TPSN",
		"PA.PTDATPAY",
		"PA.PTDA#TPAY",
		"PA.PTPAYITM",
		"PA.PTBASADR",
		"PA.PTHIST",
		"PA.PTTRAPSN",
		"PA.PTTR#APSN",
		"PA.PTTRAPAY",
		"PA.PTTR#APAY",
		"PA.PTRPDTL",
		"PA.PTREPORT",
		"PA.PTNHICAL",
		"PA.PTNHIDET",
		"PA.PTTEACHAW",
		
		"ZPTTITLE",
		"ZPTRANKCE",
		"ZPTBANK",
		"ZPTDEPT",
		"ZPTRNK4TBL",
		"ZPTEXPFTBL",
		"ZPTTRACODE",
		"ZPTBASPSN",
		"ZPTBATPAY",
		"ZPTTRAPSN",
		"ZPTTRAPAY",
		"ZPTHIST",
		"ZPTDPINTBL",
		"ZPTLBINTBL",
		"ZPTLBRPTBL",
		"ZPTCSPF",
		"ZPTPAYITM",
		"ZPTTAXTRAN",
		"ZPTTAXCASH",
		"ZPTBASADR"
	),
	KMUH(
		"3A0",          //1醫院別
		"PA.LHTAXPSN",  //2所得稅個人明細
		"PA.LHTAXTRAN", //3所得稅轉帳明細
		"PA.LHTAXCASH", //4所得稅現金明細
		"PA.LHBASPSN",  //5基本檔
		"PA.LHBASPAY",  //6基本支扣
		"PA.LHBATPAY",  //6批次檔
		"PA.LHTRACODE", //7異動代碼
		"PA.LHDEPT",    //8單位代碼
		"PA.LHTITLE",   //9職稱
		"PA.LHRANKCE",  //10階別代碼
		"PA.LHCATGTBL", //11人工造冊應計轉帳項目代碼
		"PA.LHEXPFTBL", //12績效額外點數對照表
		"PA.LHRNK4TBL", //13適用專業加給對照表
		"PA.LHBANK",    //14發薪日及入帳日對應檔
		"PA.LHTAXTBL",  //15薪資所得扣繳稅額表 
		"PA.LHPFTL",    //16全院績效彙總 
		"PA.LHDPINTBL", //17全民健康保險負擔金額表 
		"PA.LHLBINTBL", //18勞保投保金額對照表
		"PA.LHLBRPTBL", //19勞工退休金月提繳工資分級表
		"PA.LHPOSI",    //20職等支扣--專業加給及主管加給
		"PA.LHRANK",    //21階級支扣
		"PA.LHCSPF",    //22工作獎金支扣
		"PA.LHDATPSN",  //23月支個人明細
		"PA.LHDA#TPSN", //24月支個人明細_歷史
		"PA.LHDATPAY",  //25月支支扣明細
		"PA.LHDA#TPAY", //26月支支扣明細_歷史
		"PA.LHPAYITM",  //27支扣項目彙總
		"PA.LHBASADR",  //28地址
		"PA.LHHIST",    //29歷史檔
		"PA.LHTRAPSN",  //30異動個人明細
		"PA.LHTR#APSN", //31異動個人明細_歷史
		"PA.LHTRAPAY",  //32異動支扣明細
		"PA.LHTRAPAY",  //33異動支扣明細_歷史
		"PA.LHRPDTL",   //34製作報表明細
		"PA.LHREPORT",  //35報表檔架構
		"PA.LHNHICAL",  //二代健保(補充保險費_獎金)
		"PA.LHNHIDET",  //二代健保申報扣費明細		
		"",				//教職及論文獎勵
		
		"ZLHTITLE",    //時戳
		"ZLHRANKCE",
		"ZLHBANK",
		"ZLHDEPT",
		"ZLHRNK4TBL",
		"ZLHEXPFTBL",
		"ZLHTRACODE",
		"ZLHBASPSN",
		"ZLHBATPAY",
		"ZLHTRAPSN",
		"ZLHTRAPAY",
		"ZLHHIST",
		"ZLHDPINTBL",
		"ZLHLBINTBL",
		"ZLHLBRPTBL",
		"ZLHCSPF",
		"ZLHPAYITM",
		"ZLHTAXTRAN",
		"ZLHTAXCASH",
		"ZLHBASADR"
	),
	;
	
	public final String hid;   				//1醫院別
	public final String tTaxPsn;			//2所得稅個人明細
	public final String tTaxTran;			//3所得稅轉帳明細
	public final String tTaxCash;			//4所得稅現金明細 
	public final String tBasPsn;			//5基本檔
	public final String tBasPay;			//6基本支扣
	public final String tBatPay;			//6批次檔
	public final String tTraCode;			//7異動代碼
	public final String tDept;				//8單位代碼
	public final String tTitle;				//9職稱
	public final String tRankCode;			//10階別代碼
	public final String tCatgTbl;			//11人工造冊應計轉帳項目代碼 
	public final String tExpfTbl;			//12績效額外點數對照表 
	public final String tRnk4Tbl;			//13適用專業加給對照表
	public final String tBankDtTbl;			//14發薪日及入帳日對應檔
	public final String tTaxTbl;			//15薪資所得扣繳稅額表 
	public final String tPftlTbl;			//16全院績效彙總 
	public final String tDpinTbl;			//17全民健康保險負擔金額表 
	public final String tLbinTbl;			//18勞保投保金額對照表
	public final String tLbrpTbl;			//19勞工退休金月提繳工資分級表
	public final String tPosiTbl;			//20職等支扣--專業加給及主管加給
	public final String tRankTbl;			//21階級支扣
	public final String tCspfTbl;			//22工作獎金支扣
	public final String tDatPsn;			//23月支個人明細
	public final String tDatPsnDw;			//24月支個人明細_歷史
	public final String tDatPay;			//25月支支扣明細
	public final String tDatPayDw;			//26月支支扣明細_歷史
	public final String tPayItm;			//27支扣項目彙總
	public final String tBasAdr;			//28地址
	public final String tHist;				//29歷史檔
	public final String tTraPsn;			//30異動個人明細
	public final String tTraPsnDw;			//31異動個人明細_歷史
	public final String tTraPay;			//32異動支扣明細
	public final String tTraPayDw;			//33異動支扣明細_歷史
	public final String tRPDTL;				//34製作報表明細
	public final String tRPPRT;				//35報表檔架構
	public final String tNhiCal;			//二代健保(補充保險費_獎金)
	public final String tNhiDet;			//二代健保申報扣費明細
	public final String tTeaChaw;			//教職及論文獎勵
	
	public final String tmpZtitle;		    //時戳
	public final String tmpZrankce;
	public final String tmpZbank;
	public final String tmpZdept;
	public final String tmpZrnk4;
	public final String tmpZexpf;
	public final String tmpZtracd;
	public final String tmpZbaspsn;
	public final String tmpZbatpay;
	public final String tmpZtrapsn;
	public final String tmpZtrapay;
	public final String tmpZhist;
	public final String tmpZdpintbl;
	public final String tmpZlbintbl;
	public final String tmpZrpintbl;
	public final String tmpZcspf;
	public final String tmpZpayitm;
	public final String tmpZtaxtran;
	public final String tmpZtaxcash;
	public final String tmpZbasadr;
	
	Tables(
		final String hid, 
		final String tTaxPsn,
		final String tTaxTran,
		final String tTaxCash,
		final String tBasPsn ,	
		final String tBasPay,		
		final String tBatPay,		
		final String tTraCode,
		final String tDept,
		final String tTitle,
		final String tRankCode,
		final String tCatgTbl, 
		final String tExpfTbl,
		final String tRnk4Tbl,
		final String tBankDtTbl,
		final String tTaxTbl,
		final String tPftlTbl,
		final String tDpinTbl,
		final String tLbinTbl,
		final String tLbrpTbl,
		final String tPosiTbl,
		final String tRankTbl,
		final String tCspfTbl,
		final String tDatPsn,
		final String tDatPsnDw,
		final String tDatPay,
		final String tDatPayDw,
		final String tPayItm,
		final String tBasAdr,
		final String tHist,
		final String tTraPsn,
		final String tTraPsnDw,
		final String tTraPay,
		final String tTraPayDw,
		final String tRPDTL,
		final String tRPPRT,
		final String tNhiCal,
		final String tNhiDet,
		final String tTeaChaw,
		
		final String tmpZtitle,		
		final String tmpZrankce,
		final String tmpZbank,
		final String tmpZdept,
		final String tmpZrnk4,
		final String tmpZexpf,
		final String tmpZtracd,
		final String tmpZbaspsn,
		final String tmpZbatpay,
		final String tmpZtrapsn,
		final String tmpZtrapay,
		final String tmpZhist,
		final String tmpZdpintbl,
		final String tmpZlbintbl,
		final String tmpZrpintbl,
		final String tmpZcspf,
		final String tmpZpayitm,
		final String tmpZtaxtran,
		final String tmpZtaxcash,
		final String tmpZbasadr
	)
	{
        this.hid= hid;
        this.tTaxPsn= tTaxPsn;
        this.tTaxTran= tTaxTran;
        this.tTaxCash= tTaxCash;
        this.tBasPsn= tBasPsn;
        this.tBasPay= tBasPay;
        this.tBatPay= tBatPay;
    	this.tTraCode= tTraCode;
    	this.tDept= tDept;
    	this.tTitle= tTitle;
    	this.tRankCode= tRankCode;
    	this.tCatgTbl= tCatgTbl; 
    	this.tExpfTbl= tExpfTbl;
    	this.tRnk4Tbl= tRnk4Tbl; 
    	this.tBankDtTbl= tBankDtTbl;
    	this.tTaxTbl= tTaxTbl;
    	this.tPftlTbl= tPftlTbl;
    	this.tDpinTbl= tDpinTbl;
    	this.tLbinTbl= tLbinTbl;
    	this.tLbrpTbl= tLbrpTbl;
    	this.tPosiTbl= tPosiTbl;
    	this.tRankTbl= tRankTbl;
    	this.tCspfTbl= tCspfTbl;
    	this.tDatPsn= tDatPsn;
    	this.tDatPsnDw= tDatPsnDw;
    	this.tDatPay= tDatPay;
    	this.tDatPayDw= tDatPayDw;
    	this.tPayItm= tPayItm;
    	this.tBasAdr= tBasAdr;
    	this.tHist= tHist;
    	this.tTraPsn= tTraPsn;
    	this.tTraPsnDw= tTraPsnDw;
    	this.tTraPay= tTraPay;
    	this.tTraPayDw= tTraPayDw;
    	this.tRPDTL= tRPDTL;
    	this.tRPPRT= tRPPRT;
    	this.tNhiCal= tNhiCal;
    	this.tNhiDet= tNhiDet;
    	this.tTeaChaw= tTeaChaw;
    	
    	this.tmpZtitle= tmpZtitle;		//時戳
    	this.tmpZrankce= tmpZrankce;
    	this.tmpZbank= tmpZbank;
    	this.tmpZdept= tmpZdept;
    	this.tmpZrnk4= tmpZrnk4;
    	this.tmpZexpf= tmpZexpf;
    	this.tmpZtracd= tmpZtracd;
    	this.tmpZbaspsn= tmpZbaspsn;
    	this.tmpZbatpay= tmpZbatpay;
    	this.tmpZtrapsn= tmpZtrapsn;
    	this.tmpZtrapay= tmpZtrapay;
    	this.tmpZhist= tmpZhist;
    	this.tmpZdpintbl= tmpZdpintbl;
    	this.tmpZlbintbl= tmpZlbintbl;
    	this.tmpZrpintbl= tmpZrpintbl;
    	this.tmpZcspf= tmpZcspf;
    	this.tmpZpayitm= tmpZpayitm;
    	this.tmpZtaxtran= tmpZtaxtran;
    	this.tmpZtaxcash= tmpZtaxcash;
    	this.tmpZbasadr= tmpZbasadr;
    }
	
	/**
	 * 依hid取得某醫院資訊<br>
	 * @param hid
	 * @return
	 */
	public static Tables getTables(final String hid){
		Tables rs = null;
		for (final Tables t : Tables.values()) {
			if(hid.equals(t.hid)) {
				rs = t;
				break;
			}
		}
		return rs;
	}
}
