package gov.vghks.sal.dao;

import gov.vghks.common.util.DateUtil;
import gov.vghks.common.util.StringUtil;
import gov.vghks.common.util.VGHDataAccess;
import gov.vghks.sal.Globals;
import gov.vghks.sal.Hospitals;
import gov.vghks.sal.Tables;
import gov.vghks.sal.model.Pacspf;
import gov.vghks.sal.model.Padatpay;
import gov.vghks.sal.model.Padept;
import gov.vghks.sal.model.Papayitm;
import gov.vghks.sal.model.Paposi;
import gov.vghks.sal.model.Parank;
import gov.vghks.sal.model.Patitle;
import gov.vghks.sal.model.PsInsu;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

public class CommDAO {
	String gClassName = CommDAO.class.getName();
	private String sql="";
	static ArrayList<Map<String, Object>> result = null;
	static ArrayList<Object> params = new ArrayList<Object>();
	static Logger log = Logger.getLogger(SalNTDAO.class);  //呼叫log4j
	private VGHDataAccess da = null;
	private VGHDataAccess daDSN = null;
	private static final int META_TYPE = 2; 			//回傳map中欄位名稱是小寫
	private static boolean connFlg = false;
	static String g_dataSourceNT="";
	private String hid="";
	private String tCspfTbl  = "PA.PACSPF";				//工作獎金支扣
	private String tDept 	 = "PA.PADEPT";				//單位代碼
	private String tTitle    = "PA.PATITLE";			//職稱
	private String tPayitm   = "PA.PAPAYITM";			//支扣項目
	private String tPosiTbl  = "PA.PAPOSI";				//職等支扣--專業加給及主管加給 
	private String tRankTbl  = "PA.PARANK";				//階級支扣
	private String tDpinTbl = "PA.PADPINTBL";			//全民健康保險負擔金額表
	private String tLbinTbl = "PA.PALBINTBL";			//勞保投保金額對照表 
	
	public CommDAO() {
		super();
	}
	
	public CommDAO(VGHDataAccess da1) {
		super();
		this.da=da1;
		connFlg = true;
	}
	public CommDAO(VGHDataAccess da1,String hid) {
		super();
		this.da=da1;
		connFlg = true;
		Tables tabInfo = Tables.getTables(hid);
		connFlg = true;
		tCspfTbl=tabInfo.tCspfTbl;
		tRankTbl=tabInfo.tRankTbl;
		tPayitm =tabInfo.tPayItm;
		tDept   =tabInfo.tDept;
		tTitle  =tabInfo.tTitle;
		tPosiTbl=tabInfo.tPosiTbl;
		tDpinTbl=tabInfo.tDpinTbl;
		tLbinTbl=tabInfo.tLbinTbl;
	}
	
	public CommDAO(VGHDataAccess da1,VGHDataAccess da2,String hid) {
		super();
		this.da=da1;
		this.daDSN=da2;
		this.hid=hid;
		Tables tabInfo = Tables.getTables(hid);
		connFlg = true;
		tCspfTbl=tabInfo.tCspfTbl;
		tRankTbl=tabInfo.tRankTbl;
		tPayitm =tabInfo.tPayItm;
		tTitle  =tabInfo.tTitle;
		tPosiTbl=tabInfo.tPosiTbl;
		tDpinTbl=tabInfo.tDpinTbl;
		tLbinTbl=tabInfo.tLbinTbl;
	}
	/**傳回 A俸點對應 B取得新俸點 C計算補發扣
	 * @param 
	 * 		  RANK3:本功別     Title:職稱                     RANK:階別  RNK2N:職等  DEGR2:級別 RANK2:權理
'			  rankc:主管權理  rank4:適用專業加給表 inflg:保險補助比率  idno:身分證 tracod:異動碼
'			  vhid : 醫院別		
'             da(NT) daDSN(主機)
	 * @throws SQLException  
	 */
	
	public HashMap getDegreByRank(String in_data,String fun,String vHid,VGHDataAccess da,VGHDataAccess daDSN) throws Exception{
		String me = getClass().getName() + ".getDegreByRank()";
		log.info(me +  "fun="+fun+" in_data = " + in_data+ " hid = " + vHid);
		JSONObject object_in_data =null;
		Parank oneObj=null;
		
		HashMap rtnMap=new HashMap();
		HashMap<String,Integer> itmMap=new HashMap();//寫入新支扣-錢 集合
		HashMap<String,Papayitm> payitmMap= new HashMap();	//支扣項目map
		HashMap<String,Patitle> titleMap= new HashMap();	//職稱map
		ArrayList<Object> params = new ArrayList<Object>();
		JSONObject jsonOne =null;
		JSONArray jsonRtn = new JSONArray();
		boolean vflag=true;
		String strvalue="";
		int intvalue=0;
		
		String fieldname="";
		String vLEVEL1="";
		String vLEVEL2="";
		int intDegr2=0;
		int payamtp =0;
		int payamtd =0;
		long starttime =0;
		String strPaycode="";
		String tNewServant="";//新制公務員公保及退撫新制,初任公職日2023-7-1起為新制Y
		String tServant="";		//公務員
		String tLabor="";		//勞工
		String jt="";//職稱
		String rnk2n="";//職等
		String rank="";//階別
		String rank2="";//權理
		String rank3="";//本功別
		String degr2="";//級別
		String rank4="";//適用專用加給表
		String rankc="";//主管權理
		String degre="";//俸點
		String odegre="";//原俸點
		String govdt="";//初入公職日
		String effdt="";//生效日
		String inflg="";//保險補助比率
		String dpno="";//健保口數
		int pflag=0;//考績獎金月數 年終獎金比例
		int tracod=0;//異動碼
		int monno=0;//補發月數
		int dayno=0;//補發日數
		String ojt="";//原職稱
		String orank="";//原階別		
		String idno="";//身分證
		JSONArray o_basPayAry=null;//原支支扣明細 前端傳來
		JSONObject o_basPay=null;
		String l_titSub="";//職稱歸類
		String l_chief="";//職稱是否主管
		boolean l_lbins=true;//勞保費計算
		String l_posi="";//職等支扣--專業加給及主管加給 key
		int l_degrlow=0;//職等支扣--專業加給及主管加給 key
		int l_Insu=0;//本薪+專業加給+專業加給差額+主管加給,計算健保費依據
		int l_lbinig=0;//勞保投保級距
		int n_FP000=0;//本薪
		int o_FP080=0;//專業加給差額
		int o_FP090=0;//3A0責任加給
		int o_FP100=0;//3A0職務加給 2A0院區加給
		int n_FP010=0;//專業加給
		int l_insuDpin=0;//投保金額--健保
		int l_insuLBin=0;//投保金額--勞保		
		HashMap<String,Integer> l_lbinMap=new HashMap();//取得勞保參考檔
		HashMap<String,Integer> l_dpinMap=new HashMap();//取得健保參考檔
		Boolean l_jtFlg=false;//職稱(91:直接設支扣碼,不讀payitm)
		Boolean l_intern=false;//實習醫學生
		Patitle titleObj=null;
		Paposi posiObj=null;
		PsInsu insuObj=null;//1A0勞保身分保險檔	
		PsDSNDAO psDAO = null;
		
		starttime = System.currentTimeMillis();
		
		if (in_data!=null && !in_data.equals("")){
			object_in_data = JSONObject.fromObject(in_data);
			jt=(!object_in_data.has("jt")) ? "" : object_in_data.getString("jt");
			rnk2n=(!object_in_data.has("rnk2n")) ? "" : object_in_data.getString("rnk2n");
			rank=(!object_in_data.has("rank")) ? "" : object_in_data.getString("rank");
			rank2=(!object_in_data.has("rank2")) ? "" : object_in_data.getString("rank2");
			rank3=(!object_in_data.has("rank3")) ? "0" : ("").equals(StringUtil.nullToString(object_in_data.getString("rank3"))) ? "0" : object_in_data.getString("rank3");
			degr2=(!object_in_data.has("degr2")) ? "" : ("null").equals(StringUtil.nullToString(object_in_data.getString("degr2"))) ? "0" :StringUtil.addZero(object_in_data.getString("degr2"),2);
			govdt=(!object_in_data.has("govdt")) ? ""  : object_in_data.getString("govdt");
			rank4=(!object_in_data.has("rank4")) ? ""  : object_in_data.getString("rank4");
			rankc=(!object_in_data.has("rankc")) ? ""  : object_in_data.getString("rankc");
			degre=(!object_in_data.has("degre")) ? ""  : object_in_data.getString("degre");
			odegre=(!object_in_data.has("odegre")) ? ""  : object_in_data.getString("odegre");
			inflg=(!object_in_data.has("inflg")) ? ""  : object_in_data.getString("inflg");
			pflag=(!object_in_data.has("pflag")) ? 0  : ("").equals(StringUtil.nullToString(object_in_data.getString("pflag"))) ? 0 : object_in_data.getInt("pflag");
			dpno=(!object_in_data.has("dpno")) ? ""  : object_in_data.getString("dpno");
			tracod=(!object_in_data.has("tracod")) ? 0  : object_in_data.getInt("tracod");
			idno=(!object_in_data.has("idno")) ? ""  : object_in_data.getString("idno");
			o_basPayAry =(JSONArray) ((!object_in_data.has("o_basPay")) ? null  : ("").equals( StringUtil.nullToString(object_in_data.getString("o_basPay")) ) ? null : object_in_data.getJSONArray("o_basPay"));
			monno=(!object_in_data.has("monno")) ? 0 : ("").equals(StringUtil.nullToString(object_in_data.getString("monno"))) ? 0 : object_in_data.getInt("monno");
			dayno=(!object_in_data.has("dayno")) ? 0 : ("").equals(StringUtil.nullToString(object_in_data.getString("dayno"))) ? 0 : object_in_data.getInt("dayno");
			ojt=(!object_in_data.has("ojt")) ? "" : object_in_data.getString("ojt");
			orank=(!object_in_data.has("orank")) ? "" : object_in_data.getString("orank");	
			effdt=(!object_in_data.has("effdt")) ? ""  : object_in_data.getString("effdt");
		}
		
		try{
			if (jt.matches("91"))//職稱(91:直接設支扣碼,不讀payitm)
				l_jtFlg=true;
			
			if (jt.matches("BK|AD|AE|AI"))//職稱(BK:進修醫師 AD:實習醫師 AE牙醫實習醫學生 AI:中醫實習醫學生 讀payitm)
				l_intern=true;
			
			intDegr2 = Integer.parseInt(degr2);
			vLEVEL1 = rnk2n;
			if (degr2.equals("")){
				vflag=false;
			}
			//0無職等--實習醫學生
			if (rank.matches("0")){
				vLEVEL1 = jt;
			}
			//7臨
			else if (rank.matches("7")){
				vflag=false;	
			}
			//1簡任
			else if ( rank.matches("1")) {
				if (!rnk2n.matches("10|11|12|13") )
					vflag=false;
			}
			//2薦任
			else if ( rank.matches("2") ){
				if ( !rnk2n.matches("6|7|8|9|06|07|08|09") )		
					vflag=false;
			}
			//3委任 H警察人員 
			else if ( rank.matches("3|H") ){
				if (!rnk2n.matches("1|2|3|4|5|01|02|03|04|05") )
					vflag=false;
			}
			//8師 
			else if ( rank.matches("8") ){
				if ( !rnk2n.matches("P1|P2|P3") )
					vflag=false;
			}	
			//9士 
			else if ( rank.matches("9") ) {
				if ( !rnk2n.matches("T1") )
					vflag=false;
			}
			//F契約
			else if ( rank.matches("F") ){
				if ( jt.equals("CG") && vHid.matches("1A0|2A0") ){
					
				}else if ( !jt.equals("") ){
					//CH契約住院醫師(證書) CI契約住院醫師 CJ二年無證不分科住院醫師 CK二年有證不分科住院醫師 DS半日契約護理 DW半日契約藥師
					if ( jt.matches("CH|CI|CJ|CK|DS|DW"))
						vLEVEL1 = jt;
					else if (vHid.matches("2A0") && intDegr2 <200)
						vLEVEL1 = jt;
					else 
						vLEVEL1 = "ZZ";
				}else
					vLEVEL1 = "N"+rank;
			}else{
				vLEVEL1 = "N"+rank;
			}
		                
			if (vflag){
				if (vHid.equals("1A0")){
					if (rank3.matches("0|1|2") ){
						vLEVEL2 =rank3+StringUtil.intFillZero(degr2,2);
					}
				}else if (vHid.equals("2A0")){
					if ( intDegr2 >100 ){
						vLEVEL2 = degr2;
					}else{
						vLEVEL2 = rank3+degr2;
					}
				}else if (vHid.equals("3A0")){
					if (rank.equals("F")){
						vLEVEL2 = "";
					}else{
						vLEVEL2 = rank3+degr2;
					}	
				}
			}
			//支扣代碼map
			payitmMap= this.getPayitmMap("N", vHid, da);
			// A俸點對應 B取得新俸點 C計算補發扣
			if ( (!vLEVEL1.equals("") && fun.equals("A")) || (l_jtFlg || fun.equals("B")) ) {
				if ( !vLEVEL1.equals("") ) {
					sql = " select PAYLEV1V, PAYLEV2V, PAYDEGRV, PAYRANKV, PAYBASEV, PAYGEINF, PAYLBINF, PAYDPINV, PAYGEINN, PAYREBEF, PAYUSEID, PAYUPDAT, HID, PAYREBEN "+
							 "from "+this.tRankTbl + 
						   " where HID=? and PAYRANKV=? and PAYLEV1V=? and PAYLEV2V=? WITH UR";
					params.add(vHid);
					params.add(rank);
					params.add(vLEVEL1);
					params.add(vLEVEL2);
					
					log.info(me+"  " + sql+"...params:"+ params.toString());
					result = da.arraylistbyQuery(sql,params,META_TYPE);
					log.info(me+"  ...size:"+result.size());
					if(result.size()>0){
						for (Map<String,Object> data :result){
							oneObj = new Parank();
							PropertyUtils.copyProperties(oneObj, data);
							n_FP000 = oneObj.getPaybasev();//本薪 用在聯醫本薪<最低工資用
						}
					}
					rtnMap.put("rankItem", oneObj);
				}
				
				if (fun.equals("B")){
					//BK進修醫師,AD實習醫師	FP070=9000-->改讀parank
					//91顧問				FP410=3000
					if (l_jtFlg) {
//						if (jt.matches("AD|BK")) {
//							strPaycode = "FP070";
//							intvalue   = 9000;
//						}else 
						if (jt.equals("91")) {
							strPaycode = "FP410";
							intvalue   = 3000;
						}
						itmMap.put(strPaycode, intvalue);
					}
					else {
						//職稱map
						titleMap= this.getTitleMap("A",vHid, da);
						//公務員 (H 3A0警察人員,不是公職)
						if (rank.matches("1|2|3|4|8|9|H") ){
							tServant="Y";
						}else {
							tLabor="Y";
							//1A0勞保身分取人事保險檔psinsu
							if (hid.equals("1A0")) {
								psDAO = new PsDSNDAO(daDSN,hid);
								insuObj=psDAO.getInsuByidno(idno, hid, daDSN);
							}
						}
							
						//新制公務員
						if (!govdt.equals("")) {
							if (rank.matches("1|2|3|4|8|9") && !DateUtil.compareDate(govdt,Globals.gCivilServants) ){
								tNewServant="Y";
							}
						}
						//職稱歸類
						titleObj=titleMap.get(jt); 
						if (titleObj !=null) {
							l_titSub =titleObj.getPatitsub();
							l_chief =titleObj.getPachief();
						}
						
						//-------歷遍parank各項欄位,找本薪FP000,公保FD010,新公保FD010,退撫,新制退撫FD040
						//勞保--1.parank ((1)H警察人員 (2)2A0 CG契主治
						//     2.1A0改成抓人事psinsu
						if (oneObj != null) {
							Field[] fields=oneObj.getClass().getDeclaredFields();
							for (Field field :fields){
								field.setAccessible(true);
								if (field.getType()==int.class){
									intvalue = (Integer) field.get(oneObj);
								}	
								fieldname= field.getName();
								//本薪要判斷實習醫師,其他不用
								if ( (fieldname != "paybasev" && (!l_intern)) || (fieldname=="paybasev") ) {
									jsonOne = new JSONObject();
									payamtp=0;
									payamtd=0;
									strPaycode="";
									if (fieldname.equals("paybasev")){	//本薪
										if (l_intern)
											strPaycode="FP070";//實習費
										else
											strPaycode="FP000";//本薪
									}else if (fieldname.equals("paygeinf")){//公保費
										if ( !tNewServant.equals("Y") )
											strPaycode="FD010";
									}else if (fieldname.equals("paygeinn")){//新制公保費
										if (tNewServant.equals("Y") )
											strPaycode="FD010";
									}else if (fieldname.equals("payrebef")){//退撫基金PAYREBEF
										if ( !tNewServant.equals("Y") )
											strPaycode="FD040";
									}else if (fieldname.equals("payreben")){//新制退撫基金PAYREBEN
										if (tNewServant.equals("Y") )
											strPaycode="FD040";
									}else if (fieldname.equals("paylbinf")){//勞保費PAYLBINF ((1)H警察人員 (2)2A0 CG契主治
										if ( (hid.equals("3A0") && rank.matches("H")) || (hid.equals("2A0") && jt.matches("CG")) ) {
											strPaycode="FD030";
											l_lbins=false;
										}	
									}
								}
								if (!"".equals(strPaycode) && intvalue >0 ){
									itmMap.put(strPaycode, intvalue);
									System.out.println("parank各項欄位=>"+strPaycode+":"+intvalue);
								}
							}	
						}
						
						//--------專業加給(非實習醫師),paposi 優先順序:權理rank2 > 職等RNK2N > 階別RANK
						if(!l_intern) {
							if (!"".equals(rank2) )
								l_posi = rank2 ;
							else if (!"".equals(rnk2n) && !"00".equals(rnk2n))
								l_posi = rnk2n ;
							else if ("F".equals(rank) ) {
								if ( "3A0".equals(hid)) { //3A0契約 payposik=NF,degrlow=級別
									l_posi = "N"+rank ;
									l_degrlow = intDegr2;
								}	
								else if ( "2A0".equals(hid) && (intDegr2 >=201 && intDegr2 <=240) ) //2A0契約 degrlow=級別201-240
									l_posi = jt; 
							}else if (rank.matches("5|6") ) //5技工 6工友
								l_posi = "N"+rank;
							
							sql = " select PAYPSP1, PAYPSP2, PAYPSP3, PAYPSP4, PAYPSP5, PAYPSP6, PAYPSP7, PAYUPDAT, PAYPSP8 "+
									 "from "+this.tPosiTbl + 
								   " where PAYPOSIK=? ";
							params.clear();
							params.add(l_posi);
							if (l_degrlow > 0) {
								sql+=" and DEGRLOW=? ";
								params.add(l_degrlow);
							}
							sql+=" with ur ";
						
							log.info(me+"  " + sql+"...params:"+ params.toString());
							result = da.arraylistbyQuery(sql,params,META_TYPE);
							log.info(me+"  ...size:"+result.size());
							if(result.size()>0){
								for (Map<String,Object> data :result){
									posiObj = new Paposi();
									PropertyUtils.copyProperties(posiObj, data);
								}
							}
						}
						//歷遍paposi專業加給
						if (posiObj != null) {
							Field[] fields=posiObj.getClass().getDeclaredFields();
							for (Field field :fields){
								field.setAccessible(true);
								if (field.getType()==int.class){
									intvalue = (Integer) field.get(posiObj);
								}	
								fieldname= field.getName();
								
								strPaycode="";
								if (fieldname.equals("paypsp1")){
									//1.適用專業加給對照表=4(醫研(24-1)公職) [5師生級(24-2)這組應該不用] 
									//2.職稱歸類=V醫師 S研究人員 [H聘用住院醫師 待確認}
									if (rank4.matches("4") || (rank4.matches("0|") && l_titSub.matches("V|S")))
										strPaycode="FP010";
								}else if (fieldname.equals("paypsp2")){
									//1.適用專業加給對照表=5師生級(24-2) 6醫務技術(26) 
									//2.職稱歸類=N護理 T醫務技術D 藥師
									if (rank4.matches("5|6") || (rank4.matches("0|") && l_titSub.matches("T|D|N")))
										strPaycode="FP010";
								}else if (fieldname.equals("paypsp6")){
									//1.適用專業加給對照表=2 醫學工程(3)  
									//2.職稱歸類=W 醫學工程
									if (rank4.matches("2") || (rank4.matches("0|") && l_titSub.matches("W")))
										strPaycode="FP010";
								}else if (fieldname.equals("paypsp4")){
									//1.適用專業加給對照表=3資訊(20) 
									//2.職稱歸類=C 資訊人員
									//3.職稱歸類=屏榮契約I 契約人員 FP100院區加給
									if ( rank4.matches("3") || 
										( rank4.matches("0|") && l_titSub.matches("C"))  )
										strPaycode="FP010";
									else if ( hid.equals("2A0") && l_titSub.matches("I") )
										strPaycode="FP100";
								}else if (fieldname.equals("paypsp7")){
									//1.適用專業加給對照表=7 工程(7)  
									//2.職稱歸類=U 工程人員
									if (rank4.matches("7") || (rank4.matches("0|") && l_titSub.matches("U")))
										strPaycode="FP010";
								}else if (fieldname.equals("paypsp8")){
									//1.適用專業加給對照表=8 社工(2)  
									//2.職稱歸類=B 社工
									if (rank4.matches("8") || (rank4.matches("0|") && l_titSub.matches("B")))
										strPaycode="FP010";
								}
								else if (fieldname.equals("paypsp3")){
									//1.適用專業加給對照表=1 行政人員(1)   
									//2.職稱歸類=(1)G 行政人員 (2)Y 技工友 (3)屏榮2A0 契約I 契約人員  (4)聯醫3A0契約I 契約人員
									//3.聯醫3A0警察,rank=H
									if ( rank4.matches("1") || 
										( rank4.matches("0|") && ( l_titSub.matches("G|Y") || 
												                ( hid.matches("2A0|3A0") && l_titSub.matches("I")) ) ) ||
										( hid.matches("3A0") && rank.matches("H")) )
										strPaycode="FP010";
								}
								
								if (!"".equals(strPaycode) && intvalue>0){
									//聯醫,小於最低工資,補在專業加給
									if ( hid.matches("3A0") && l_titSub.matches("I") ) {
										if ( n_FP000+ intvalue < Globals.gSalStandard3A0 )
											intvalue = intvalue + ( Globals.gSalStandard3A0 - intvalue - n_FP000);
		                            }
									n_FP010=intvalue;
									itmMap.put(strPaycode, intvalue);
									System.out.println("paposi專業加給=>"+strPaycode+":"+intvalue);
								}
								
							}	
						}
						//-----主管加給FP030 本職是主管 或 主管權理
 						if ( (l_chief.equals("Y") || !rankc.equals("") ) ) {
 							params.clear();
							sql = " select PAYPSP5 "+
									 "from "+this.tPosiTbl + 
								   " where PAYPOSIK=? ";
							if ( !rankc.equals("") ) {
								sql += " WITH UR";
								params.add(rankc);
							}	
							else if (rnk2n.matches("05|06|07|08|09|10|11|12|13|14")) {
								sql += " WITH UR";
								params.add(rnk2n);
							}else if (rnk2n.matches("P1|P2|P3")) {
								sql += " and ? BETWEEN DEGRLOW AND DEGRHIGH WITH UR";
								params.add(rnk2n);
								params.add(degre);
							}
							log.info(me+"  " + sql+"...params:"+ params.toString());
							result = da.arraylistbyQuery(sql,params,META_TYPE);
							log.info(me+"  ...size:"+result.size());
							strPaycode="";
							for (Map<String,Object> data :result){
								strPaycode="FP030";
								intvalue= (Integer) data.get("paypsp5");
							}	
								
							if (!"".equals(strPaycode)){
								itmMap.put(strPaycode, intvalue);
								System.out.println("主管加給=>"+strPaycode+":"+intvalue);
							}
						}
 						
 						//--勞保身分--勞保費FD030健保FD020
 						//1.1A0高榮擷取人事保險級距 
 						//2.2A0/3A0 [新進]計算
 						if (tLabor.equals("Y") && !l_intern ) {
 							if (hid.equals("1A0")) {
 								//勞保費FD030
 								if (insuObj != null) {
	 								l_insuLBin = insuObj.getLbinig();//投保金額--勞保
	 								l_lbinMap  = GetLaborInsuByPay(inflg, l_insuLBin, da);	//勞保費
	 								if ( l_lbinMap!=null ) {
	 									strPaycode= "FD030";
	 									intvalue  = l_lbinMap.get("lbinamt");
	 									l_lbinig  = l_lbinMap.get("lbinig");
	 									if (intvalue > 0) {
	 	 									itmMap.put(strPaycode, intvalue);
	 	 									System.out.println("勞保費=>"+strPaycode+":"+intvalue);
	 	 								}
	 								}
	 								//健保費FD020
	 								l_insuDpin = insuObj.getDpinig();//投保金額--健保 
	 								if (dpno=="")
	 									dpno="1";
	 								l_dpinMap  = GetHealthInsuRelief(inflg, dpno,l_insuDpin, da);	
	 								if ( l_dpinMap!=null ) {
	 									strPaycode="FD020";
	 									intvalue = l_dpinMap.get("dpinamt");
	 									if (intvalue > 0) {
	 	 									itmMap.put(strPaycode, intvalue);
	 	 									System.out.println("健保費=>"+strPaycode+":"+intvalue);
	 	 								}
	 									rtnMap.put("insu_dpin", l_dpinMap.get("dpinig"));	//存放投保級距
	 								}
 								}
 							}else if (hid.matches("2A0|3A0")) {
 								if (tracod == 1) {
 									//勞保
	 								strPaycode= "FD030";
									l_lbinMap  = GetLaborInsuByPay(inflg, n_FP000 + n_FP010 + o_FP090 + o_FP100, da);	
	 								if ( l_lbinMap!=null ) {
	 									strPaycode= "FD030";
	 									intvalue  = l_lbinMap.get("lbinamt");
	 									l_lbinig  = l_lbinMap.get("lbinig");
	 									itmMap.put(strPaycode, intvalue);
	 									System.out.println("勞保費=>"+strPaycode+":"+intvalue);
	 								}
	 								//健保
	 								l_dpinMap  = GetHealthInsuRelief(inflg, dpno,n_FP000 + n_FP010 + o_FP090 + o_FP100, da);	
	 								if ( l_dpinMap!=null ) {
	 									strPaycode="FD020";
	 									intvalue = l_dpinMap.get("dpinamt");
	 									if (intvalue > 0) {
	 	 									itmMap.put(strPaycode, intvalue);
	 	 									System.out.println("健保費=>"+strPaycode+":"+intvalue);
	 	 								}
	 									rtnMap.put("insu_dpin", l_dpinMap.get("dpinig"));	//存放投保級距
	 								}
 								}	
 							}
 						}
 						//------醫療互助金FD210
 						strPaycode="";
 						intvalue=0;
 						if (hid.matches("1A0|2A0") && !l_intern ) {
 							if (hid.equals("1A0")) {
 								if (l_titSub.matches("V")) {
 									if (jt.equals("16"))	//'16住院醫師$200 主治醫師以上$400   2004-1-1生效
 										intvalue=200;
 									else
 										intvalue=400;
 								}
 								else if (l_titSub.matches("H")) {//AL一年有證不分科住院醫師 AM	聘用實習醫師 AN一年無證不分科住院醫師 BK進修醫師 CB聘用住院醫師          
 									intvalue=200;
 								}
 								else if (l_titSub.matches("N|D")) {//N護理 D藥師          
 									intvalue=100;
 								}
 								else if (l_titSub.matches("I")) {
 									if (jt.matches("CH|CI|CJ|CK"))	//'CH契約住院醫師(證書) CI契約住院醫師 CK 2年有證不分科住院醫師 CJ二年無證不分科住院醫師
 										intvalue=200;
 									else if (jt.matches("DM|DN|EJ|DS|DW"))	//'DM契約護理 DN契約藥師 EJ契約麻醉護理 DS半日契約護理DW半日契約藥師
 										intvalue=100;
 									else if (jt.matches("CG"))	//'CG契約主治醫師
 										intvalue=400;
 								}
 							}else if (hid.equals("2A0")) {
 								if (l_titSub.matches("V")) {	//醫師
 									intvalue=1500;
 								}
 								else if (l_titSub.matches("N|D|T")) {//N護理 D藥師 T 醫務技術          
 									intvalue=150;
 								}
 								else if (l_titSub.matches("I")) {
 									if (jt.matches("DN|E0|E1|E3|E4|E5|E6|E7|EI|EJ|EK|EL|EN|ES|EU|EY|DM"))	//契約的護理師、放射、檢驗、復健、心理、藥師等
 										intvalue=200;
 									else if (jt.matches("CG"))	//'CG契約主治醫師
 										intvalue=1500;
 								}
 							}
 							if (intvalue != 0) {
 								strPaycode="FD210";
 								itmMap.put(strPaycode, intvalue);
 								System.out.println("醫療互助金=>"+strPaycode+":"+intvalue);
 							}
 						}

 						//找出原支支扣未在新支扣,要寫入,ex.FP080專業加給差額 FD050固定所得稅...
 						//不列入---1.勞保身分,[新進]FD020健保 FD030勞保
 						//		 2.公保身分,FD020健保
 						//		 3.itmMap己有,不再重複
 						if (o_basPayAry != null) {
							for (int i = 0; i < o_basPayAry.size(); i++) {
							    o_basPay = o_basPayAry.getJSONObject(i);
							    strPaycode=o_basPay.getString("paycode");							    
							    if (strPaycode != "") {
							    	if ( tLabor.equals("Y") && tracod == 1 && strPaycode.matches("FD020|FD030")){
						    			
							    	}else if ( tServant.equals("Y") && strPaycode.matches("FD020")){
							    		
							    	}else {
								    	if (itmMap.get(strPaycode)==null) {//找新支扣map是否有在內,如果有,就不用寫入								    		
								    		if (strPaycode.equals("FP080") )
								    			o_FP080=o_basPay.getInt("payamt");//專業加給差額,
								    		if ( hid.matches("3A0")) {
								    			if (strPaycode.equals("FP090") )
								    				o_FP090=o_basPay.getInt("payamt");//3A0責任加給FP090
								    			if (strPaycode.equals("FP100") )
								    				o_FP100=o_basPay.getInt("payamt");//3A0職務加給FP100
								    		}
								    			
									    	jsonRtn.add(o_basPay);
									    	System.out.println("原支=>"+strPaycode+":"+o_basPay.getString("payamt"));
									    }
						    		}	
							    }
							}
 						}
					}
					
					// 使用 foreach 迴圈遍歷 itmMap
					l_Insu=0;
					for (Map.Entry<String, Integer> entry : itmMap.entrySet()) {
					    System.out.println("itmMap=>"+entry.getKey() + " : " + entry.getValue());
					    strPaycode = entry.getKey();
					    intvalue  =  entry.getValue();
					    //
					    if (strPaycode.matches("FP000|FP010|FP030|FP080") && tServant.equals("Y")) {
					    	//有公務人員身分的健保,(本薪+專業加給+主管加給)*公務員身份的健保全薪比例 ,再根據PADPINTBL找出健保費
					    	l_Insu += intvalue;
					    }
					    //公保		
					    if (strPaycode.equals("FD010")) {
					    	/*'105.5.16
			                '身心障礙人員應繳自付部分保險費核算方式(公)：
			                '1、依上開計算公式先行核算一般身分應繳自付部分保險費
			                '2、應繳自付部分保險費＊（1－補助比例）＝身心障礙人員應繳自付部分保險費（應四捨五入）
			                1-1/4 2-1/2 3-全額 4-全額有上限 5-公勞1/4、健免  6-公勞1/2、健免  7-公勞有、健免 8-公勞有、健1/4 9-公勞有、健1/2
							10-不適用就業保險 11-不適用就業保險且老年職災加保人員
					    	*/
					    	if (tServant.equals("Y")) {
					    		if (inflg.matches("1|5"))
					    			intvalue = (int)(intvalue - Math.round((double)intvalue * 0.25));
					    		else if (inflg.matches("2|6"))
					    			intvalue = (int)(intvalue- Math.round((double)intvalue * 0.5));
					    		else if (inflg.matches("3"))
					    			intvalue = 0;
					    	}
					    }
					    //取得支扣代碼
					    jsonOne = new JSONObject();
					    jsonOne=getPayitemBycode(strPaycode,intvalue,payitmMap);
						jsonRtn.add(jsonOne);
					}
					
					//公保身分健保FD020,且為1新進 2復職 11考績晉級 33納實 34考績升等 35待遇調整 36晉級37職務調整 38降級
					if ( (l_Insu+o_FP080 >0) && tServant.equals("Y") && (tracod == 1 ||tracod == 2 || tracod == 11 || (tracod >=33 && tracod <=38)) ){
						l_dpinMap  = GetHealthInsuRelief(inflg, dpno,l_Insu+o_FP080, da);	
						if ( l_dpinMap!=null ) {
							strPaycode="FD020";
							intvalue = l_dpinMap.get("dpinamt");
							if (intvalue > 0) {
								//取得支扣代碼
							    jsonOne = new JSONObject();
							    jsonOne=getPayitemBycode(strPaycode,intvalue,payitmMap);
								jsonRtn.add(jsonOne);
							}
							rtnMap.put("insu_dpin", l_dpinMap.get("dpinig"));	//存放投保級距
						}
					}
					
					//--計算補發扣 
					if (monno != 0 || dayno != 0) {
						getReissue(jsonRtn,o_basPayAry,tracod,effdt,monno,dayno,l_lbinig,orank,rank,ojt,jt,odegre,pflag,payitmMap,hid);
					}	
					//sort
					jsonRtn = jsonArySort(jsonRtn);
					
					rtnMap.put("payitm_json", jsonRtn);
				}
			}
			else if(fun.equals("C")){	//計算補發扣
				//--計算當月實際日數 
				if (monno != 0 || dayno != 0 || tracod==12) {
					getReissue(jsonRtn,o_basPayAry,tracod,effdt,monno,dayno,l_lbinig,orank,rank,ojt,jt,odegre,pflag,payitmMap,hid);
				}	
				//sort
				jsonRtn = jsonArySort(jsonRtn);
				
				rtnMap.put("payitm_json", jsonRtn);
			}
			
			Date end = new Date();
			log.info(me+", 共花費："+(System.currentTimeMillis()-starttime)+"毫秒");
		}catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}finally{
			closeAll(da,null,connFlg);			
		}
		return rtnMap;
	}
	//jsonArray排序
	private JSONArray jsonArySort(JSONArray jsonTmp) {
		JSONArray jsonRtn = new JSONArray();
		// 【升序排序】 (A -> Z) 筆數>0,才排序
		if (jsonTmp.size()>0) {
			jsonTmp.sort((o1, o2) -> {
	            String s1 = ((JSONObject) o1).getString("paycode");
	            String s2 = ((JSONObject) o2).getString("paycode");
	            return s1.compareTo(s2); 
	        });
	        //System.out.println("字母升序: " + jsonTmp);
		}
        jsonRtn = jsonTmp;
		return jsonRtn;
	}
	
	//計算補發扣 
	//nPay新支扣,oPay原支扣,tracod異動別,effdt生效日,monno月數,dayno日數,lbig勞保級距,orank原階別,rank新階別,ojt原職稱,jt職稱,odegre原俸點,pflag考績獎金月數/年終獎金比例,payitmMap支扣碼集合,hid
	//2007-4-1 公保破月計算修正,以實際加保日數計算 
	//2024.12.13 破月新進 無條件進位,離職 無條件捨去
	private JSONArray getReissue(JSONArray nPay,JSONArray oPay,int tracod,String effdt,int monno,int dayno,int lbig,String orank,String rank,String ojt,String jt,String odegre,int pflag,HashMap<String,Papayitm> payitmMap,String hid) {
		String me = ".getReissue()";
		JSONArray jsonRtn = new JSONArray();
		JSONArray l_Pay = new JSONArray();
		String jsonString ="";
		JSONObject l_payObj=null;
		JSONObject o_payObj=null;
		JSONObject jsonOne=null;	
		String l_paycode="";
		String o_paycode="";
		int l_degre=0;	//原俸點
		double l_pflag=0;//考績獎金比例
		int l_prof=0;	//專業加給+專業加給差額
		int l_payamt=0;
		int o_payamt=0;
		int r_payamt=0;//補發金額
		String r_paycode="";//補發支扣碼
		int l_effdt=30;
		String l_Deed="N";
		double l_breakFD00=0;
		//有效日期的當月實際日數
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, Integer.parseInt(effdt.substring(0,	4)));
		calendar.set(Calendar.MONTH, Integer.parseInt(effdt.substring(5, 7))- 1);
		int l_caldt = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		System.out.println("有效日期的當月實際日數:"+l_caldt);
		
		//2A0:屏榮,一律用30日 3A0:契約30日,其他用實際日數 1A0:高榮,契約不含醫師,30日計算 ,其他用實際日數
		if (hid.matches("2A0")) {
			if (orank.equals("F") || rank.equals("F"))
				l_Deed="Y";
		}
		else if (hid.matches("2A0|3A0")) {
			if (orank.equals("F") || rank.equals("F"))
				l_Deed="Y";
			else
				l_effdt=l_caldt;
		}
		else if (hid.matches("1A0")) {	//CG契約主治醫師 CI契約住院醫師  CH契約住院醫師(證書) CK二年有證不分科住院醫師 
			if ( ( orank.equals("F") || rank.equals("F") ) && 
				 !( ojt.matches("CG|CJ|CH|CK") || jt.matches("CG|CJ|CH|CK")) )
				l_Deed="Y";
			else
				l_effdt=l_caldt;
		}
		System.out.println("依身份的有效日數:"+l_effdt);
		//原俸點
		if (odegre.equals(""))
			l_degre=0;
		else
			l_degre=Integer.parseInt(odegre);
		
		//先設定返回值=新支扣 共用同一個記憶體位置
		jsonRtn= nPay;
		
		//1新進2復職
		if (tracod==1 ||tracod==2) {
			if (nPay != null) {
				// （建立全新且獨立的陣列，互不影響）
				jsonString = nPay.toString();
				l_Pay= JSONArray.fromObject(jsonString);
				
				for (int i = 0; i < l_Pay.size(); i++) {
				    // 根據實際型別使用 getJSONObject, getString 或 getInt
				    l_payObj = l_Pay.getJSONObject(i);
				    l_paycode= l_payObj.getString("paycode");
				    l_payamt = l_payObj.getInt("payamt");
				    r_payamt=0;
				    r_paycode=l_paycode.substring(0,4)+"9";
				    if (l_paycode.equals("FD020") ) {	//FD020健保 破月扣一個月,2024.12.16負值不列
				    	if (dayno > 0)
				    		r_payamt = l_payamt * (monno+1);
				    	if (dayno < 0 || monno > 0)
				    		r_payamt = l_payamt * monno;
				    }
				    else if (l_paycode.equals("FD210") ) {	//FD210醫療互助金 破月不扣
				    	if (monno != 0)
				    		r_payamt = l_payamt * monno;
				    }
				    else if (l_paycode.equals("FD030") ) {	//FD030勞保費 日數是負數,要再研究
				    	if (lbig >0) {
				    		//破月
				    		l_breakFD00=Math.round((double)lbig * dayno / 30 * Globals.gAccident * 20 / 100) + Math.round((double)lbig * dayno / 30 * Globals.gEmploy * 20 / 100);
				    		r_payamt = (int) (l_payamt * monno + l_breakFD00);
				    	}
				    }
				    else if (l_paycode.matches("FP000|FP010|FP090|FP100|FP110|FP120") ) {	//FP000本薪 FP010專業加給 FP090證照加給/責任加給 FP100 院區加給/職務加給 FP110身心障礙加給 FP120特別津貼
				    	//'2024-12-13 無條件進位
				    	r_payamt = (int) ((l_payamt * monno) + Math.ceil((double) l_payamt * dayno / l_effdt ));
				    }else	//'按當月日數 
				    	r_payamt = (int) ((l_payamt * monno) + Math.round((double) l_payamt * dayno / l_effdt ));
				    
				    if (r_payamt != 0) {
				    	//取得支扣代碼
					    jsonOne = new JSONObject();
					    jsonOne=getPayitemBycode(r_paycode,r_payamt,payitmMap);
						jsonRtn.add(jsonOne);
				    }
				}
			}
		}
		//退出 21離職 22退休 23留職停薪 24逾期請假
		else if (tracod >=21 && tracod <=24) {
			if (oPay != null) {
				jsonString = oPay.toString();
				l_Pay= JSONArray.fromObject(jsonString);
				for (int i = 0; i < l_Pay.size(); i++) {
					 // 根據實際型別使用 getJSONObject, getString 或 getInt
				    l_payObj = l_Pay.getJSONObject(i);
				    l_paycode= l_payObj.getString("paycode");
				    l_payamt = l_payObj.getInt("payamt");
				    r_payamt=0;
				    r_paycode=l_paycode.substring(0,4)+"9";
				    if (l_paycode.equals("FD210") ) {	//FD210醫療互助金 破月扣一個月
				    	if (dayno > 0)
				    		r_payamt = l_payamt * (monno+1);
				    	else
				    		r_payamt = l_payamt * monno;
				    }else if (l_paycode.equals("FD020") ) {	//FD020健保 破月不扣
				    	if (dayno != 0 && monno>0) 
				    		r_payamt = l_payamt * (monno-1);
				    	else
				    		r_payamt = l_payamt * monno;
				    }else if (l_paycode.equals("FD030") ) {	//FD030勞保 
				    	if (lbig >0) {
				    		//破月
				    		l_breakFD00=Math.round((double)lbig * dayno / 30 * Globals.gAccident * 20 / 100) + Math.round((double)lbig * dayno / 30 * Globals.gEmploy * 20 / 100);
				    		r_payamt = (int) (l_payamt * monno + l_breakFD00);
				    	}else {
				    		r_payamt = (int) ((l_payamt * monno) + (Math.round( (double)l_payamt * dayno / 30 )));
				    	}
				    }else if (l_paycode.matches("FP000|FP010|FP030|FP080|FP090|FP100|FP110|FP120") ) {	
				    	//FP000本薪 FP010專業加給 FP030主管加給 FP080專業加給差額 FP090證照加給/責任加給 FP100 院區加給/職務加給 FP110身心障礙加給 FP120特別津貼
				    	//2024.12.13 破月新進 無條件進位,離職 無條件捨去
			    		r_payamt = (int) ((l_payamt * monno) + Math.ceil((double) l_payamt * dayno / l_effdt ) -l_payamt) ;
				    }else {
				    	r_payamt = (int) ((l_payamt * monno) + (Math.round( (double)l_payamt * dayno / l_effdt )));//'按當月日數
				    }
				    
				    if (r_payamt != 0) {
				    	//取得支扣代碼
					    jsonOne = new JSONObject();
					    jsonOne=getPayitemBycode(r_paycode,r_payamt,payitmMap);
					    if (jsonOne !=null) {
					    	jsonRtn.add(jsonOne);
					    }	
				    }
				    //本薪...要寫一筆原支進新支
				    if (l_paycode.matches("FP000|FP010|FP030|FP080|FP090|FP100|FP110|FP120") ) {
				    	//取得支扣代碼
					    jsonOne = new JSONObject();
					    jsonOne=getPayitemBycode(l_paycode,l_payamt,payitmMap);
					    if (jsonOne !=null) {
					    	jsonRtn.add(jsonOne);
					    }	
				    }
				}
			}
		}	
		//11考績晉級 31晉級調薪 32改任換敘 33納實 34考績升等 35待遇調整 36晉級 37職務調整 38降級   
		//除了 考績及考績升等,待遇調整 要追溯至生效日期,其他異動項除了退撫離職金及薪津外,至多扣1個月
        //2005-2-2 新增待遇調整,要追溯至生效日期 2024.2.7 契約,勞,健保,不變
		//FD010公保依據當月實際加保日數佔當月實際天數的比例來按日計算，且每次計算步驟皆須獨立執行四捨五入
		//FD040「全月基金費用提撥總額 × 實際加入日數 ÷ 當月實際天數（四捨五入）」
		else if ( (tracod >=31 && tracod <=38) || tracod ==11 ) {
			if (nPay != null) {
				jsonString = nPay.toString();
				l_Pay= JSONArray.fromObject(jsonString);
				for (int i = 0; i < l_Pay.size(); i++) {
				    // 根據實際型別使用 getJSONObject, getString 或 getInt
				    l_payObj = l_Pay.getJSONObject(i);
				    l_paycode= l_payObj.getString("paycode");
				    l_payamt = l_payObj.getInt("payamt");
				    r_payamt=0;
				    r_paycode=l_paycode.substring(0,4)+"9";
				    o_payamt=0;
				    for (int j = 0; j < oPay.size(); j++) {
				        o_payObj = oPay.getJSONObject(j);
				        // 確認 key 是否存在以避免錯誤
				        if (o_payObj.has( "paycode" )) {
				        	o_paycode = o_payObj.getString("paycode");
				        	if ( o_paycode.equals(l_paycode) ) {
				        		o_payamt = o_payObj.getInt("payamt");
				        	}
				        }
				    }
				    if (o_payamt > 0 && o_payamt != l_payamt) {
				    	if ( tracod ==11 || tracod ==34 || tracod ==35 ) {
				    		if (l_paycode.matches("FD020")) {	//破月扣一個月 FD020健保不做補發扣
				    		
				    		}else if (l_paycode.matches("FD210")) {	//FD210醫療互助金,破月扣一個月 FD020健保不做補發扣
				    			if(dayno !=0)
				    				r_payamt=(l_payamt-o_payamt) * (monno+1);
				    			else
				    				r_payamt=(l_payamt-o_payamt) * monno;
				    		}
				    		else if (l_paycode.matches("FD030")) 	//FD030勞保 按30日
				    			r_payamt=(int) ((l_payamt-o_payamt) * monno+ Math.round((double)(l_payamt-o_payamt) * dayno / 30));
				    		else 
				    			r_payamt=(int) ((l_payamt-o_payamt) * monno+ Math.round( (double) (l_payamt-o_payamt) * dayno / l_effdt)) ;
				    		
				    	}else {
				    		if (l_paycode.matches("FD040") ) 	//FD040退撫 
				    			r_payamt=(int) ((l_payamt-o_payamt) * monno+ Math.round( (double)(l_payamt-o_payamt) * dayno / l_effdt)) ;
				    		else if (l_paycode.substring(1,2).equals("P") ) 	
				    			r_payamt=(int) ((l_payamt-o_payamt) * monno+ Math.ceil( (double)(l_payamt-o_payamt) * dayno / l_effdt )) ;
				    		else if ((l_Deed.equals("N") || hid.equals("3A0") ) &&  !l_paycode.matches("FD020")){	
				    			//2024.2.7契約不用差額追補 2025.5.20 3A0排外,FD020不做追補  
				    			r_payamt=(int) ((l_payamt-o_payamt) * monno+ Math.round((double)(l_payamt-o_payamt) * dayno / l_effdt));				    			
				    		}	
				    	}
				    }
				    
				    if (r_payamt != 0) {
				    	System.out.println("補發扣:"+l_paycode+" "+ o_payamt+" l_payamt:"+l_payamt+" r_payamt:"+r_payamt);
				    	//取得支扣代碼
					    jsonOne = new JSONObject();
					    jsonOne=getPayitemBycode(r_paycode,r_payamt,payitmMap);
					    if (jsonOne !=null) {
					    	jsonRtn.add(jsonOne);
					    }	
				    }
				} 
			}
		}
		//51年終獎金,依核發月數,發放年終獎金比例,計算至元位，請遵循「無條件捨去至個位數」的原則
		//年終獎金計算至元位，請遵循「無條件捨去至個位數」的原則。
		//2026.7.13 出納詢問人事,折合率不再使用,用本薪(CB聘用住院醫師 CH契約住院醫師 AL一年有證不分科住院醫師 AN一年無證不分科住院醫師)==>月支報酬 = 報酬薪點 × 薪點折合率（計算至元為止，未達整數者，小數點以下無條件捨去）
		else if ( tracod ==51 ) {
			if (oPay != null) {
				jsonString = oPay.toString();
				l_Pay= JSONArray.fromObject(jsonString);
				for (int i = 0; i < l_Pay.size(); i++) {
					l_payObj = l_Pay.getJSONObject(i);
				    l_paycode= l_payObj.getString("paycode");
				    l_payamt = l_payObj.getInt("payamt");
				    
				    /*if (ojt.matches("CB|CH") && l_degre > 0) {
				    	l_payamt=(int) Math.round(l_degre * Globals.gRPRemu);
				    }else
				    	l_payamt = l_payObj.getInt("payamt");*/
				    
				    if (l_paycode.equals("FP000") ) {	//FP000本薪
				    	r_paycode="VP121";
				    }else if (l_paycode.matches("FP010|FP080") ) {	//FP010專業加給 FP080專業加給差額
				    	r_paycode="VP122";
				    }else if (l_paycode.equals("FP030") ) {	//FP030主管加給
				    	r_paycode="VP124";	
				    }else if (l_paycode.equals("FP090") ) {	//FP090責任加給
				    	r_paycode="VP126";	
				    }else if (l_paycode.equals("FP100") ) {	//FP100職務加給
				    	r_paycode="VP125";	
				    }
				    //1.5
				    l_payamt = (int) Math.floor(l_payamt * 1.5 );
				    //月數比例	
				    if (monno != 12 )	
				    	l_payamt = (int) Math.floor(l_payamt * monno /12 );
				    
			    	//發放比例
			    	if (pflag == 1)	//發放1/3
			    		r_payamt = (int) Math.floor(l_payamt  / 3 );
			    	else if (pflag == 2)	//發放2/3
			    		r_payamt = (int) Math.floor(l_payamt *2 / 3 );
			    	else if (pflag == 3)	//發放全部
			    		r_payamt = l_payamt ;
			    	else if (pflag == 4)	//發放3/4 申誡
			    		r_payamt = (int) Math.floor(l_payamt *3 / 4 );
			    	//FP010專業加給 FP080專業加給差額
			    	if (l_paycode.matches("FP010|FP080") ) {	
			    		l_prof +=r_payamt;
			    		r_payamt=0;
			    	}
			    	
				    if (r_payamt != 0 ) {
				    	//取得支扣代碼,因專業加給FP020+專業加給差額FP080,要寫入一筆VP122
					    jsonOne = new JSONObject();
					    jsonOne=getPayitemBycode(r_paycode,r_payamt,payitmMap);
					    if (jsonOne !=null) {
					    	jsonRtn.add(jsonOne);
					    	r_payamt=0;
					    }	
				    }
				}
				if (l_prof != 0 ) {
			    	//取得支扣代碼,因專業加給FP020+專業加給差額FP080,要寫入一筆VP122
				    jsonOne = new JSONObject();
				    jsonOne=getPayitemBycode("VP122",l_prof,payitmMap);
				    if (jsonOne !=null) {
				    	jsonRtn.add(jsonOne);
				    }	
			    }
			}
		}	
		//12考績獎金 金額須計算至元位，小數點後無條件四捨五入（四捨五入至個位數）。
		else if ( tracod ==12 ) {
			if (oPay != null) {
				jsonString = oPay.toString();
				l_Pay= JSONArray.fromObject(jsonString);
				for (int i = 0; i < l_Pay.size(); i++) {
					l_payObj = l_Pay.getJSONObject(i);
				    l_paycode= l_payObj.getString("paycode");
				    l_payamt = l_payObj.getInt("payamt");
				    
				    if (l_paycode.equals("FP000") ) 	//FP000本薪
				    	r_paycode="VP131";
				    else if (l_paycode.matches("FP010|FP080") ) 	//FP010專業加給 FP080專業加給差額
				    	r_paycode="VP132";
				    else if (l_paycode.equals("FP030") ) 	//FP030主管加給
				    	r_paycode="VP134";	
				    else if (l_paycode.equals("FP090") ) 	//FP090責任加給
				    	r_paycode="VP136";	
				    else if (l_paycode.equals("FP100") ) 	//FP100職務加給
				    	r_paycode="VP135";					    
				    
			    	//發放比例
			    	if (pflag == 1)	//發放0.5月
			    		l_pflag=0.5;
			    	else if (pflag == 2)	//發放1個月
			    		l_pflag=1;
			    	else if (pflag == 3)	//發放1.5個月
			    		l_pflag=1.5;
			    	else if (pflag == 4)	//發放2個月
			    		l_pflag=2;
			    	
			    	r_payamt = (int) Math.round(l_payamt * l_pflag );
			    	//FP010專業加給 FP080專業加給差額
			    	if (l_paycode.matches("FP010|FP080") ) {	
			    		l_prof +=r_payamt;
			    		r_payamt=0;
			    	}
			    	//取得支扣代碼,因專業加給FP020+專業加給差額FP080,要寫入一筆VP132
				    if (r_payamt != 0 ) {				    	
					    jsonOne = new JSONObject();
					    jsonOne=getPayitemBycode(r_paycode,r_payamt,payitmMap);
					    if (jsonOne !=null) {
					    	jsonRtn.add(jsonOne);
					    	r_payamt=0;
					    }	
				    }
				}
				if (l_prof != 0 ) {
			    	//取得支扣代碼,因專業加給FP020+專業加給差額FP080,要寫入一筆VP132
				    jsonOne = new JSONObject();
				    jsonOne=getPayitemBycode("VP132",l_prof,payitmMap);
				    if (jsonOne !=null) {
				    	jsonRtn.add(jsonOne);
				    }	
			    }
			}
		}
		
		return jsonRtn;
	}
	
	
	//依paycode,取得payitem,存放jsonOne
	private JSONObject getPayitemBycode(String strPaycode,int intvalue,HashMap<String,Papayitm> payitmMap) {
		String me = ".getPayitemBycode()";
		JSONObject jsonOne = null;
		Papayitm payitmObj=null;
		int payamtp=0;
		int payamtd=0;	
		payitmObj=payitmMap.get(strPaycode);
		if (payitmObj != null) {
			jsonOne = new JSONObject();
			jsonOne.put("paycode", StringUtil.nullToString(payitmObj.getPaycode()));
			jsonOne.put("pacodnmc", StringUtil.nullToString(payitmObj.getPacodnmc()));
			jsonOne.put("payamt", intvalue );
			if ("P".equals(strPaycode.substring(1,2)))
				payamtp=intvalue;
			else if ("D".equals(strPaycode.substring(1,2)))
				payamtd=intvalue;
			jsonOne.put("payamtd", payamtd);
			jsonOne.put("payamtp", payamtp);
			jsonOne.put("payauth", StringUtil.nullToString(payitmObj.getPayauth()));
			jsonOne.put("payiftax", StringUtil.nullToString(payitmObj.getPayiftax())); 
			jsonOne.put("hid", StringUtil.nullToString(payitmObj.getHid()));
			jsonOne.put("pacodnmc", StringUtil.nullToString(payitmObj.getPacodnmc()));
			jsonOne.put("pacoddel", StringUtil.nullToString(payitmObj.getPacoddel()));
			jsonOne.put("payreward", StringUtil.nullToString(payitmObj.getPayreward()));
		}
		return jsonOne;
	}
	
	//健保補助標準及口數,計算出保費 
	//---健保費若有補助二分之一（如中度身心障礙者之自付保費補助），其金額若產生小數點，處理原則為無條件進位，即小數點以下無論多少一律進位為整數。
	//而一般健保費繳納，以元為單位，角以下則是採「四捨五入」法。
	//Inflg保險補助比率  DPNO健保口數 有帶小數點 dpniamt健保費
	// 1-1/4 2-1/2 3-全額 4-全額有上限 5-公勞1/4、健免  6-公勞1/2、健免  7-公勞有、健免 8-公勞有、健1/4 9-公勞有、健1/2
	//10-不適用就業保險 11-不適用就業保險且老年職災加保人員
	public HashMap GetHealthInsuRelief(String inflg,String DPNO,int l_pay,VGHDataAccess da)throws SQLException{
		String me = getClass().getName() + ".GetHealthInsuRelief()";
		log.info(me +  " inflg = " + inflg+ " DPNO = " + DPNO+ " l_pay = " + l_pay);
		
		HashMap rtnMap=new HashMap();
		int tmpamt=0;
		int l_dpinamt=0;
		int l_dpinig=0;	//投保級距
		double l_dpno = Double.parseDouble(DPNO);
		
		params.clear();
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		
		sql = " select DPINAMT,case DPINHIGH when 999999 then "+ Globals.gDpinMax +" else DPINHIGH end as DPINHIGH "+
				 "from "+this.tDpinTbl + 
			   " where ? between DPINLOW and DPINHIGH  with ur ";
		params.add(l_pay);
		
		log.info(me+"  " + sql+"...params:"+ params.toString());
		try{
			if(da==null){
				connFlg = false;
				//改用列舉
				g_dataSourceNT=hospInfo.dataSourceNT;
				da=new VGHDataAccess(g_dataSourceNT);
			}
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"  ...size:"+result.size());
			
			for (Map<String,Object> data :result){
				tmpamt   = (Integer) data.get("dpinamt");
				l_dpinig = (Integer) data.get("dpinhigh");
			}
			
			if (inflg.matches("1|8")) 
				l_dpinamt = (int) (tmpamt - Math.ceil((double) tmpamt * 0.25));
			else if (inflg.matches("2|9"))
				l_dpinamt = (int) (tmpamt - Math.ceil((double) tmpamt * 0.5));
			else if (inflg.matches("3|5|6|7"))
				l_dpinamt = 0;
			else if (inflg.matches("4")) {
				if (tmpamt > Globals.gInsuLimit) 
					l_dpinamt =  tmpamt - Globals.gInsuLimit;
				else
					l_dpinamt = 0;
			}else
				l_dpinamt =  tmpamt;
			
			//計算健保人數(0-1算1人不用計算眷屬,超過1人,表示有眷屬)
		    if (l_dpno > 1) {
		        //眷屬健保人數上限3口 2004-1-20
		        if (l_dpno > 4)
		        	l_dpinamt = tmpamt + (3 * tmpamt);
		        else
		        	l_dpinamt = tmpamt + (int) Math.round( (double)(l_dpno - 1) * tmpamt);
		    }
		    rtnMap.put("dpinig", l_dpinig);	//健保級距
			rtnMap.put("dpinamt", l_dpinamt);	//健保費
		}
		catch(Throwable e){
			throw new SQLException(me+" "+e.toString());
		}finally{
		
		}
		return rtnMap;
	}
		
	//勞保費 
	//---健保費若有補助二分之一（如中度身心障礙者之自付保費補助），其金額若產生小數點，處理原則為無條件進位，即小數點以下無論多少一律進位為整數。
	//而一般健保費繳納，以元為單位，角以下則是採「四捨五入」法。
	//Inflg保險補助比率   vpay
	// 1-1/4 2-1/2 3-全額 4-全額有上限 5-公勞1/4、健免  6-公勞1/2、健免  7-公勞有、健免 8-公勞有、健1/4 9-公勞有、健1/2
	//10-不適用就業保險 11-不適用就業保險且老年職災加保人員
	public HashMap GetLaborInsuByPay(String inflg,int l_pay,VGHDataAccess da) throws SQLException{
		String me = getClass().getName() + ".GetLaborInsuByPay()";
		log.info(me +  " inflg = " + inflg+  " l_pay = " + l_pay);
		
		HashMap rtnMap=new HashMap();
		int tmpamt=0;	
		int lbinamt=0;  
		int lbinig=0;	//投保級距
		params.clear();
		Hospitals hospInfo = Hospitals.getOneHosp(hid);
		
		sql = " select LBINAMT,case LBINHIGH when 999999 then "+ Globals.gLbInMax +" else LBINHIGH end as LBINHIGH "+
				 "from "+this.tLbinTbl + 
			   " where ? between LBINLOW and LBINHIGH  with ur ";
		params.add(l_pay);
		
		log.info(me+"  " + sql+"...params:"+ params.toString());
		try{
			if(da==null){
				connFlg = false;
				//改用列舉
				g_dataSourceNT=hospInfo.dataSourceNT;
				da=new VGHDataAccess(g_dataSourceNT);
			}
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"  ...size:"+result.size());
			
			for (Map<String,Object> data :result){
				tmpamt   = (Integer) data.get("lbinamt");
				lbinig = (Integer) data.get("lbinhigh");
			}
			
			
			if (inflg.matches("1|5")) 
				lbinamt = (int) (tmpamt - Math.ceil((double) tmpamt * 0.25));
			else if (inflg.matches("2|6"))
				lbinamt = (int) (tmpamt - Math.ceil((double) tmpamt * 0.5));
			else if (inflg.matches("3"))
				lbinamt = 0;
			else
				lbinamt =  tmpamt;
			
			rtnMap.put("lbinig", lbinig);	//勞保級距
			rtnMap.put("lbinamt", lbinamt);	//勞保費
		}catch(Throwable e){
			throw new SQLException(me+" "+e.toString());
		}finally{
		
		}
		return rtnMap;
	}
		
	/**傳回 績效點數
	 * @param 
	 * 		  vRANK3  : 本功別
'             vTitle  : 職稱
'			  vRANK   : 階別
'             vRNK2N  : 職等
'             vDEGR2  : 級別
'  			  vBANKDT : 發薪日
'			  hid    : 醫院別		
'   @return   vPFKEY  : 實際支領KEY
'             vPFBAS  : 基本點數
'             vPFDAT  : 職等點數
	 * @throws SQLException 
	 */
	
	public HashMap getCsPfByKey(int vRANK3,String vTitle,String vRANK,String vRNK2N,int intDEGR2,String vBANKDT,String hid,VGHDataAccess da) throws Exception{
		String me = getClass().getName() + ".getCsPfByKey()";
		log.info(me +  " vTitle = " + vTitle+" vRANK = " + vRANK+" vRNK2N = " + vRNK2N+" intDEGR2 = " + intDEGR2+" vBANKDT = " + vBANKDT+ " hid = " + hid);
		
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap rtnMap=new HashMap();
		String vPFKEY="";
		String vPFBAS="";
		String vPFDAT="";
		String vDEGR2="";
		//職稱
		vTitle=StringUtil.nullToString(vTitle);
		//職等
		vRNK2N=StringUtil.nullToString(vRNK2N);
		//階別
		vRANK=StringUtil.nullToString(vRANK);
		//級別
		vDEGR2=String.valueOf(intDEGR2);
		vDEGR2=StringUtil.fillChar(StringUtil.nullToString(vDEGR2),2,"0",false);
		//實際支領KEY 本功別=0,表示pfkey組成為職稱+"00000"
		if (vRANK.equals("F") && !vTitle.matches("CG|CH|CK")){
			if (hid.equals("1A0")){ // >2024-9-1 加級數
				if ( DateUtil.compareDate( Globals.gPFNew , vBANKDT ) ){
					vPFKEY=vTitle+"000"+StringUtil.fillChar(vDEGR2,2,"0",false);
				}else{
					if (vTitle.matches("DX|DY"))	//DX契約資訊助理工程師  DY契約資訊副工程師
						vPFKEY=vTitle+"000"+StringUtil.fillChar(vDEGR2,2,"0",false);
					else
						vPFKEY=vTitle+"00000";
				}
			}else if(hid.equals("2A0")){
				vPFKEY=vTitle+"00000";
			}
		}else{
			if (vRANK3==0)
				vPFKEY=vTitle+"00000";
			else
				vPFKEY = vTitle + vRNK2N + vRANK3 + StringUtil.fillChar(vDEGR2,2,"0",false) ;
		}
		
		rtnMap.put("pfkey", vPFKEY);
		
		sql = "select  PACSBASE, PACSDATA "+
		 	    "from "+this.tCspfTbl +
		 	  " where PACSKEY = ? "+
		 	  "  with ur";
		params.add(vPFKEY);
		Date begin = new Date();
		log.info(me+"  " + sql+"...params:"+ params.toString());
		result = da.arraylistbyQuery(sql,params,META_TYPE);
		log.info(me+"  ...size:"+result.size());
		if(result.size()>0){
			for (Map<String,Object> data :result){
				vPFBAS=StringUtil.nullToString(data.get("pacsbase"));
				vPFDAT=StringUtil.nullToString(data.get("pacsdata"));
			}
		}
		rtnMap.put("pfbas", vPFBAS);
		rtnMap.put("pfdat", vPFDAT);
		Date end = new Date();
		log.info(me+"(size):" + result.size()+" dataaccess:"+(end.getTime()-begin.getTime()));
	
		return rtnMap;
	}
	/**傳回 績效點數
	 * @param 
	 * 		  vRANK3  : 本功別
'             vTitle  : 職稱
'			  vRANK   : 階別
'             vRNK2N  : 職等
'             vDEGR2  : 級別
'  			  vBANKDT : 發薪日
'			  vCSPF   : map	
'			  vhid    : 醫院別		
'   @return   vPFKEY  : 實際支領KEY
'             vPFBAS  : 基本點數
'             vPFDAT  : 職等點數
	 * @throws SQLException 
	 */
	
	public HashMap getCsPfByMap(int vRANK3,String vTitle,String vRANK,String vRNK2N,int intDEGR2,String vBANKDT,String vHid,HashMap vCSPFMap,VGHDataAccess da) throws Exception{
		String me = getClass().getName() + ".getCsPfByKey()";
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap rtnMap=new HashMap();
		String vPFKEY="";
		int vpfbas=0;
		int vpfdat=0;
		String vDEGR2="";
		Pacspf cspfObj=null;
		//職稱
		vTitle=StringUtil.nullToString(vTitle);
		//職等
		vRNK2N=StringUtil.nullToString(vRNK2N);
		//階別
		vRANK=StringUtil.nullToString(vRANK);
		//級別
		vDEGR2=String.valueOf(intDEGR2);
		vDEGR2=StringUtil.fillChar(StringUtil.nullToString(vDEGR2),2,"0",false);
		//實際支領KEY 本功別=0,表示pfkey組成為職稱+"00000"
		if (vRANK.equals("F") && !vTitle.matches("CG|CH|CK")){
			if (hid.equals("1A0")){ // >2024-9-1 加級數
				if ( DateUtil.compareDate( Globals.gPFNew , vBANKDT ) ){
					vPFKEY=vTitle+"000"+StringUtil.fillChar(vDEGR2,2,"0",false);
				}else{
					if (vTitle.matches("DX|DY"))	//DX契約資訊助理工程師  DY契約資訊副工程師
						vPFKEY=vTitle+"000"+StringUtil.fillChar(vDEGR2,2,"0",false);
					else
						vPFKEY=vTitle+"00000";
				}
			}else if(hid.equals("2A0")){
				vPFKEY=vTitle+"00000";
			}
		}else{
			if (vRANK3==0)
				vPFKEY=vTitle+"00000";
			else
				vPFKEY = vTitle + vRNK2N + vRANK3 + StringUtil.fillChar(vDEGR2,2,"0",false) ;
		}
		
		rtnMap.put("pfkey", vPFKEY);
		
		cspfObj=(Pacspf) vCSPFMap.get(vPFKEY);
		if (cspfObj!=null){
			vpfbas= cspfObj.getPacsbase();
			vpfdat= cspfObj.getPacsdata();
		}
		rtnMap.put("pfbas", vpfbas);
		rtnMap.put("pfdat", vpfdat);
		
		return rtnMap;
	}
	/**取得支扣項目map	
	 * @param 
	 * 		  vcoddel : 刪除否(Y已刪除/N使用中),空白(全取)
'             vhid    : 醫院別		
'   @return   papayitm : map
	 * @throws SQLException 
	 */
	public HashMap<String, Papayitm> getPayitmMap(String vcoddel,String hid,VGHDataAccess da) throws SQLException{
		String me = getClass().getName() + ".getPayitmMap()";
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap<String, Papayitm> rtnMap=new HashMap();
		Papayitm oneObj=null; 
		String oneKey="";
		long starttime =0;
		sql = "select PAYCODE, PACODNMC, PACODDEL, PAYAUTH, PAYIFTAX, PAUPBAS, PAYREWARD, HID "+
		 	    "from "+this.tPayitm +" where hid=? ";
		try{
			starttime = System.currentTimeMillis();
			params.add(hid);
			if (!"".equals(vcoddel) ){
				sql += " and PACODDEL=? ";
				params.add(vcoddel);
			}
			sql += " with ur";
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"  " + sql+"...params:"+ params.toString());
			log.info(me+"  ...size:"+result.size());
			if(result.size()>0){
				for (Map<String,Object> data :result){
					oneObj=new Papayitm();
					PropertyUtils.copyProperties(oneObj, data);					
					oneKey=StringUtil.nullToString(oneObj.getPaycode());					
					rtnMap.put(oneKey, oneObj);
				}
			}
			log.info(me+"(size):" + result.size()+" 共花費："+(System.currentTimeMillis()-starttime)+"毫秒");
		}
		catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}
		finally{
		
		}
		
		return rtnMap;
	}
	/**取得 職等支扣--專業加給及主管加給 map	
	 * key=PAYPOSIK+DEGRLOW
	 * 		  
'   @return   paposi : map
	 * @throws SQLException 
	 */
	public HashMap<String, Paposi> getPosiMap(VGHDataAccess da) throws SQLException{
		String me = getClass().getName() + ".getPayitmMap()";
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap<String, Paposi> rtnMap=new HashMap();
		Paposi oneObj=null; 
		String oneKey="";
		long starttime =0;
		sql = "select PAYPOSIK, DEGRLOW, DEGRHIGH, PAYPSP1, PAYPSP2, PAYPSP3, PAYPSP4, PAYPSP5, PAYPSP6, PAYPOSID, PAYPSP7, PAYUPDAT, PAYPSP8 "+
		 	    "from "+this.tPosiTbl +" with ur ";
		try{
			starttime = System.currentTimeMillis();
			result = da.arraylistbyQuery(sql,META_TYPE);
			log.info(me+"  " + sql);
			log.info(me+"  ...size:"+result.size());
			if(result.size()>0){
				for (Map<String,Object> data :result){
					oneObj=new Paposi();
					PropertyUtils.copyProperties(oneObj, data);					
					oneKey=StringUtil.nullToString(oneObj.getPayposik()) + StringUtil.nullToString(oneObj.getDegrlow());					
					rtnMap.put(oneKey, oneObj);
				}
			}
			log.info(me+"(size):" + result.size()+" 共花費："+(System.currentTimeMillis()-starttime)+"毫秒");
		}
		catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}
		finally{
		
		}
		
		return rtnMap;
	}
	/**取得職稱map	
	 * @param item:A(職稱代碼) B(人事職稱代碼)
	 * 		  vhid    : 醫院別		
'   @return   Patitle : map
	 * @throws SQLException 
	 */
	public HashMap<String, Patitle> getTitleMap(String item,String hid,VGHDataAccess da) throws SQLException{
		String me = getClass().getName() + ".getTitleMap()";
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap<String, Patitle> rtnMap=new HashMap();
		Patitle oneObj=null; 
		String oneKey="";
		long starttime =0;
		sql = "select PAYTITLK, PATITSUB, PACHIEF, PAYTITLP, PAYTITLC, PACODDEL, HID, PBTITCOD "+
		 	    "from "+this.tTitle +" where hid=? with ur";
		params.add(hid);
		log.info(me+"  " + sql+"...params:"+ params.toString());
		try{
			starttime = System.currentTimeMillis();
			
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"  ...size:"+result.size());
			if(result.size()>0){
				for (Map<String,Object> data :result){
					oneObj=new Patitle();
					PropertyUtils.copyProperties(oneObj, data);
					//A(職稱代碼) B(人事職稱代碼)
					oneKey="";
					if (item.equals("A"))
						oneKey=StringUtil.nullToString(oneObj.getPaytitlk());
					else if (item.equals("B"))
						oneKey=StringUtil.nullToString(oneObj.getPbtitcod());
					if ( !"".equals(oneKey) )
						rtnMap.put(oneKey, oneObj);
				}
			}
			log.info(me+"(size):" + result.size()+" 共花費："+(System.currentTimeMillis()-starttime)+"毫秒");
		}
		catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}
		finally{
		
		}
		
		return rtnMap;
	}
	/**取得單位map	
	 * @param item:A(單位代碼) B(人事單位代碼)
	 * 		  vhid    : 醫院別		
'   @return   Padept : map
	 * @throws SQLException 
	 */
	public HashMap<String, Padept> getDeptMap(String item,String hid,VGHDataAccess da) throws SQLException{
		String me = getClass().getName() + ".getDeptMap()";
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap<String, Padept> rtnMap=new HashMap();
		Padept oneObj=null; 
		String oneKey="";
		long starttime =0;
		sql = "select PADEPCOD, PACSTNO, PADEPNMC, PAYUPDID, PAYUPDAT, HID, PBWKCODE "+
		 	    "from "+this.tDept +" where HID=? with ur";
		params.add(hid);
		log.info(me+"  " + sql+"...params:"+ params.toString());
		try{
			starttime = System.currentTimeMillis();
			
			result = da.arraylistbyQuery(sql,params,META_TYPE);
			log.info(me+"  ...size:"+result.size());
			if(result.size()>0){
				for (Map<String,Object> data :result){
					oneObj=new Padept();
					PropertyUtils.copyProperties(oneObj, data);
					//A(單位代碼) B(人事單位代碼)
					oneKey="";
					if (item.equals("A"))
						oneKey=StringUtil.nullToString(oneObj.getPadepcod());
					else if (item.equals("B")) 
						oneKey=StringUtil.nullToString(oneObj.getPbwkcode());
					if ( !"".equals(oneKey) )
						rtnMap.put(oneKey, oneObj);
				}
			}
			log.info(me+"(size):" + result.size()+" 共花費："+(System.currentTimeMillis()-starttime)+"毫秒");
		}
		catch(Exception e){
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		}
		finally{
		
		}
		
		return rtnMap;
	}
	//取得績效點數map	
	public HashMap getCsPfByAll(VGHDataAccess da,String hid) throws SQLException{
		String me = getClass().getName() + ".getCsPfByAll()";
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap rtnMap=new HashMap();
		Pacspf oneObj=null; 
		String cskey="";
		sql = "select PACSKEY, PACSBASE, PACSDATA, PACSNAME, COALESCE(PAYUPDID,'') as PAYUPDID "+
		 	    "from "+this.tCspfTbl +
		 	  "  with ur";
		
		Date begin = new Date();
		log.info(me+"  " + sql+"...params:"+ params.toString());
		if (hid.matches("1A0|2A0")){
			try{
				result = da.arraylistbyQuery(sql,META_TYPE);
				log.info(me+"  ...size:"+result.size());
				if(result.size()>0){
					for (Map<String,Object> data :result){
						oneObj=new Pacspf();
						PropertyUtils.copyProperties(oneObj, data);
						
						cskey=StringUtil.nullToString(oneObj.getPacskey());
						
						rtnMap.put(cskey, oneObj);
					}
				}
				Date end = new Date();
				log.info(me+"(size):" + result.size()+" dataaccess:"+(end.getTime()-begin.getTime()));
			}
			catch(Exception e){
				log.info(me + " fail, " + e);
				throw new SQLException(me + ", fail!!." + e);
			}
			finally{
			
			}
		}
		return rtnMap;
	}
	/**傳回計算總所得,應稅所得(總收入-房租-實物代金-主管加給-不休假獎金-交通費-福利互助金),淨所得,總扣除額
	 * '副程式名稱： Sum_Pay_Deduct
'目    的： 計算總所得,應稅所得(總收入-房租-實物代金-主管加給-不休假獎金-交通費-福利互助金),淨所得,總扣除額
'           92.8.26福利互助金VP400不列入應稅所得
'           94.7.1自繳退休金VD060 VD069應稅所得要扣除
'           94.7.1房租 實物代金不能列入,房租生效日為78.7.1 實物代金生效日79.7.1
'           94.8.29 房租生效日及實物代金生效日皆為84.7.1 之前要扣除 之後不扣
'           97.6.23 本薪--事病薪等停發VD070應稅所得要扣除?? ==>取消102.1.3
'           97.9.10 總所得,淨所得:不列所得要加上,如:病歷罰扣不得列入所得 (2008-8-19VP017看診逾時罰扣 VP016超出健保總額) ==>取消102.1.3
'           101.2.6 VP016改成健保獎勵罰扣(從99.10開始有正項),
'           103.4.22 2014.4生效 VP200加班費 VP437超額獎金--論文補助<==不列所得
'           109.1.21 讀取PA.PAYITM.PAYIFTAX='Y',因有新增科目,未同步加入
'           111.1.19 公職退撫基金溯自110年1月1日免納入所得稅
'           111.5.2 契約主治不扣實物代金及房租
'           111.11.10 屏榮FD060 FD069自繳退休金 應稅所得要扣除
'參數說明：
'           vRank        :階別	
'           vRnk2N       :職等
'           vRank2       :權理
'           vDegre       :俸點              92.8.15 房租津貼T1 385以上=>600 T1 385以上=>500
'           vGovDt       :初任公職日
'           vSalDt       :發薪日
'return:	vpay   	     :總所得
'           vtaxpay   	 :應稅所得
'           vnetPay      :淨所得
'			vduduct      :應支出
'			vtax         :所得稅
'*******************************************************************/
	public HashMap Sum_Pay_Deduct(ArrayList<Padatpay> datpayAry,String vRank,String vRANK2,String vRNK2N,int vDegre,String vGovDt,String vSalDt)throws Exception {
		String me = getClass().getName() + ".Sum_Pay_Deduct()";
		//log.info(me+"  vRank:" + vRank+" vRANK2:"+ vRANK2+" vRNK2N:"+ vRNK2N+" vDegre:"+ vDegre+" vGovDt:"+ vGovDt+" vSalDt:"+ vSalDt);
		ArrayList<Object> params = new ArrayList<Object>();
		HashMap rtnMap=new HashMap();	
		int vrent_house =0;  //房租
		int vthing_money =0; //實物代金
		int vpay =0;        //總收入
		int vduduct =0;     //總支出
		int vnetpay=0;		//淨所得
		int vtaxPay_Y =0;   //應稅所得,列Y部分
		int vtaxpay=0;		//應稅所得,扣除實物代金及房租
		int vPension = 0;	//退撫基金
				
		int vchief =0;      //主管加給
		int vnon_rest =0;   //不休假獎金
		int vtraffic =0;    //交通費
		int vWelfare =0;    //福利互助金
		int vPapers =0;         //VP437超額獎金_論文補助 2014.4.1生效 2014.4.22改
		int vOvertime =0;       //VP200加班費2014.4.1生效 2014.4.22改
		int vDuty =0;           //VP455總值日值班費
		int vSelfRetire =0; 	//VD06|FD06勞工自繳退休金
		int vtax=0;				//FD050 VD030所得稅
		String vRank_P ="";     //職等
		
		Boolean vflag =false;    //有本薪才計算應稅所得
		String vHourseDt ="1995-7-1";   //房租生效日 1989-7-1
		String vThingDt ="1995-7-1";    //實物代金生效日 1990-7-1
		String vSalBgdt ="2005-7-1";      //開始不扣房租津貼及實物代金日期
		
		Padatpay datpayObj=null;
		if (datpayAry != null && !datpayAry.isEmpty()) {
			Iterator<Padatpay> it =  datpayAry.iterator();
			while ( it.hasNext()) {
				datpayObj = (Padatpay) it.next();
				if ("FP00".equals(datpayObj.getPaycode().substring(0, 4))){
					vflag = true;
				}
				//總收入,應稅收入,總支出
				if ("P".equals(datpayObj.getPaycode().substring(1, 2))){
					vpay +=	datpayObj.getPayamt();
					if ("Y".equals(datpayObj.getPayiftax())){
						vtaxPay_Y+=datpayObj.getPayamt();
					}
				}else if ("D".equals(datpayObj.getPaycode().substring(1, 2))){
					vduduct+=datpayObj.getPayamt();
				}
				//主管加給 年終獎金--主管特支  考績獎金--主管特支
		        if ("FP03".equals(datpayObj.getPaycode().substring(0, 4)) || datpayObj.getPaycode().substring(0, 4).matches("VP124|VP134")){
		        	vchief += datpayObj.getPayamt();
		        }
		        //公職退撫基金溯自110年1月1日免納入所得稅 2023.7.1 FD07退撫離職金自願提撥,免納入所得稅
		        else if (datpayObj.getPaycode().substring(0, 4).matches("FD04|FD07") ){
		        	if ( vRank.matches("1|2|3|8|9") && !DateUtil.compareDate(vSalDt,Globals.gPensionFund) ) {
		        		vPension += datpayObj.getPayamt();
		        	}	
		        }
		        
		        //不休假獎金
		        else if("VP11".equals(datpayObj.getPaycode().substring(0, 4))){
		        	vnon_rest += datpayObj.getPayamt();
		        }
		        //交通費,己廢除    
	//	        else if(datpayObj.getPaycode().substring(0, 4).matches("FP06|VP19")){
	//	        	vtraffic += datpayObj.getPayamt();
	//	        }
		        //福利互助金   
		        else if("VP400".equals(datpayObj.getPaycode())){
		        	vWelfare += datpayObj.getPayamt();
		        }
		        //自繳退休金  ,2022.11.10 屏榮FD06
		        else if(datpayObj.getPaycode().substring(0, 4).matches("VD06|FD06")){
		        	vSelfRetire += datpayObj.getPayamt();
		        }
		        //超額獎金_論文補助   
		        else if("VP437".equals(datpayObj.getPaycode())){
		        	vPapers += datpayObj.getPayamt();
		        }
		        //加班費
		        else if("VP200".equals(datpayObj.getPaycode())){
		        	vOvertime += datpayObj.getPayamt();
		        }
		        //總值日值班費
		        else if("VP455".equals(datpayObj.getPaycode())){
		        	vDuty += datpayObj.getPayamt();
		        }
		        //所得稅
		        else if(datpayObj.getPaycode().substring(0, 4).matches("FD05|VD03")){
		        	vtax += datpayObj.getPayamt();
		        }
			}
			
		    //房租,實物代金
			if (("".equals(StringUtil.nullToString(vRANK2))) && ("".equals(StringUtil.nullToString(vRNK2N)))){
				vRank_P ="";
			}else{
				if(!"".equals(StringUtil.nullToString(vRANK2)))
					vRank_P = StringUtil.nullToString(vRANK2);
				else
					vRank_P = StringUtil.nullToString(vRNK2N);
			}
		    
		    if (vflag){	//有本薪才有實物代金 房租
			    if ( DateUtil.compareDate(vSalDt, vSalBgdt) ){	//vSaldt < vSalBgdt '2005-7-20改 2005-7-1開始不計算房租及實物代金
			    	if (vRank.matches("5|6")){
			    		vrent_house = 400;
			    	    vthing_money = 930;
			    	}//2022.5.2契約主治沒有房租及實物代金
			    	else if (!vRank.matches("F")){
			    		if (vRank_P.matches("01|02|03") || (vRank_P.equals("T1") && vDegre<385 ) || "4".equals(vRank)){
				    		vrent_house = 500;
				    	    vthing_money = 930;
				    	}else if (vRank_P.matches("04|05|06|07|P3") || (vRank_P.equals("T1") && vDegre>=385 )){
				    		vrent_house = 600;
				    	    vthing_money = 930;
				    	}else if (vRank_P.matches("08|09|10|11|12|13|14|P1|P2") ){
				    		 vrent_house = 700;
				    		 vthing_money = 930;
				    	}
			    	} 
			    }	
			    else	//2005-8-29改 以84.7.1為基準計算 之前要扣 之後不扣 (初任公職日<基準日,有房租及實物代金)
			    {
			    	if (vRank.matches("5|6")){
			    		if ( DateUtil.compareDate(vGovDt, vHourseDt) )
			    			vrent_house = 400;
			    		if ( DateUtil.compareDate(vGovDt, vThingDt) )
			    			vthing_money = 930;
			    	}
			    	//2022.5.2契約主治沒有房租及實物代金
			    	else if (!vRank.matches("F")){
			    		if (vRank_P.matches("01|02|03") || (vRank_P.equals("T1") && vDegre<385 ) || "4".equals(vRank)){
				    		if ( DateUtil.compareDate(vGovDt, vHourseDt) )
				    			vrent_house = 500;
				    		if ( DateUtil.compareDate(vGovDt, vThingDt) )
				    			vthing_money = 930;
				    	}else if (vRank_P.matches("04|05|06|07|P3") || (vRank_P.equals("T1") && vDegre>=385 )){
				    		if ( DateUtil.compareDate(vGovDt, vHourseDt) )
				    			vrent_house = 600;
				    		if ( DateUtil.compareDate(vGovDt, vThingDt) )
				    			vthing_money = 930;
				    	}else if (vRank_P.matches("08|09|10|11|12|13|14|P1|P2") ){	
				    		if ( DateUtil.compareDate(vGovDt, vHourseDt) )
				    			vrent_house = 700;
				    		if ( DateUtil.compareDate(vGovDt, vThingDt) )
				    			vthing_money = 930;
				    	}
			    	} 
			    }
		    }
		    //淨所得=總所得-總支出
		    vnetpay = vpay - vduduct;
		    if (vflag){
				//應稅所得=datpay.getPayiftax()="Y"-實物代金-房租-自繳退休金-公職退撫基金
			    vtaxpay = vtaxPay_Y - vrent_house - vthing_money - vSelfRetire -vPension;
			    if (vtaxpay<0) vtaxpay=0;
		    }else{
		    	vtaxpay = vtaxPay_Y;
		    }
		}    
	    rtnMap.put("tax", vtax);
	    rtnMap.put("pay", vpay);
		rtnMap.put("duduct", vduduct); 
		rtnMap.put("netpay", vnetpay);
		rtnMap.put("taxpay", vtaxpay); //應稅所得,扣除實物代金及房租
		rtnMap.put("thing_money", vthing_money); 
		rtnMap.put("rent_house", vrent_house);
		rtnMap.put("retire", vPension+vSelfRetire);
		return rtnMap;
	}
	
	//身分證字號與居留證(統一證)編號檢核程式碼 http://experimentrecord.blogspot.tw
	public static boolean isValidIDorRCNumber(String str) {

	    if (str == null || "".equals(str)) {
	        return false;
	    }

	    final char[] pidCharArray = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	    // 原身分證英文字應轉換為10~33，這裡直接作個位數*9+10
	    final int[] pidIDInt = { 1, 10, 19, 28, 37, 46, 55, 64, 39, 73, 82, 2, 11, 20, 48, 29, 38, 47, 56, 65, 74, 83, 21, 3, 12, 30 };

	    // 原居留證第一碼英文字應轉換為10~33，十位數*1，個位數*9，這裡直接作[(十位數*1) mod 10] + [(個位數*9) mod 10]
	    final int[] pidResidentFirstInt = { 1, 10, 9, 8, 7, 6, 5, 4, 9, 3, 2, 2, 11, 10, 8, 9, 8, 7, 6, 5, 4, 3, 11, 3, 12, 10 };

	    // 原居留證第二碼英文字應轉換為10~33，並僅取個位數*8，這裡直接取[(個位數*8) mod 10]
	    final int[] pidResidentSecondInt = {0, 8, 6, 4, 2, 0, 8, 6, 2, 4, 2, 0, 8, 6, 0, 4, 2, 0, 8, 6, 4, 2, 6, 0, 8, 4};

	    str = str.toUpperCase();// 轉換大寫
	    final char[] strArr = str.toCharArray();// 字串轉成char陣列
	    int verifyNum = 0;

	    /* 檢查身分證字號 */
	    if (str.matches("[A-Z]{1}[1-2]{1}[0-9]{8}")) {
	        // 第一碼
	        verifyNum = verifyNum + pidIDInt[Arrays.binarySearch(pidCharArray, strArr[0])];
	        // 第二~九碼
	        for (int i = 1, j = 8; i < 9; i++, j--) {
	            verifyNum += Character.digit(strArr[i], 10) * j;
	        }
	        // 檢查碼
	        verifyNum = (10 - (verifyNum % 10)) % 10;

	        return verifyNum == Character.digit(strArr[9], 10);
	    }

	    /* 檢查統一證(居留證)編號 (2031/1/1停用) 
	    verifyNum = 0;
	    if (str.matches("[A-Z]{1}[A-D]{1}[0-9]{8}")) {
	        // 第一碼
	        verifyNum += pidResidentFirstInt[Arrays.binarySearch(pidCharArray, strArr[0])];
	        // 第二碼
	        verifyNum += pidResidentSecondInt[Arrays.binarySearch(pidCharArray, strArr[1])];
	        // 第三~八碼
	        for (int i = 2, j = 7; i < 9; i++, j--) {
	            verifyNum += Character.digit(strArr[i], 10) * j;
	        }
	        // 檢查碼
	        verifyNum = (10 - (verifyNum % 10)) % 10;

	        return verifyNum == Character.digit(strArr[9], 10);
	    }

	     檢查統一證(居留證)編號 (2021/1/2實施) 
	    verifyNum = 0;
	    if (str.matches("[A-Z]{1}[89]{1}[0-9]{8}")) {
	        // 第一碼
	        verifyNum += pidResidentFirstInt[Arrays.binarySearch(pidCharArray, strArr[0])];
	        // 第二~八碼
	        for (int i = 1, j = 8; i < 9; i++, j--) {
	            verifyNum += Character.digit(strArr[i], 10) * j;
	        }
	        // 檢查碼
	        verifyNum = (10 - (verifyNum % 10)) % 10;

	        return verifyNum == Character.digit(strArr[9], 10);
	    }*/
	    
	    return false;
	}
	/**傳回 薪津表/扣款表/薪津表(補發扣) 對應
	 * @param selEn 表冊 Prt_M_1薪津表 Prt_M_2扣款表 Prt_M_3薪津表(補發扣) Bank_T銀行媒體傳輸
	 * @param itm 項目
	 * @return 報表中文名稱
	 * Ex: getPrtNmBysal("Prt_M_1",1,"1A0") return 護理_師級 
	 */
	public static String getPrtNmBysal(String selEn,int itm,String hid){		
		String itmCd = " ";
		if (hid.equals("1A0")){
			//薪津表/扣款表
			if (selEn.matches("Prt_M_1|Prt_M_2")){
				if (itm==0) return "總計";
				if (itm==1) return "護理_師級";
				if (itm==2) return "護理_契約";
				if (itm==3) return "職員_公職";
				if (itm==4) return "醫事_契約";
				if (itm==5) return "醫事_師級";
				if (itm==6) return "醫師_進修,實習";
				if (itm==7) return "醫師_公職";
				if (itm==8) return "醫師_師級";
				if (itm==9) return "醫師_聘用";
				if (itm==10) return "醫師_契約";
				if (itm==11) return "不分科";
				if (itm==12) return "大武";
				if (itm==13) return "兼職";
				if (itm==14) return "技工友";
				if (itm==15) return "計畫";
				if (itm==16) return "駐衛警";
				if (itm==17) return "外籍照服員";
			}	
			//薪津表(補發扣) 
			else if (selEn.matches("Prt_M_3")){
				if (itm==0) return "總計";
				if (itm==1) return "明細";
			}
			//Prt_M_6銀行轉帳明細表 Bank_T銀行轉帳明細表
			else if (selEn.matches("Prt_M_6|Bank_T")){
				if (itm==0) return "薪津";
				if (itm==1) return "公儲";
				if (itm==2) return "停薪";
			}
			
		}
		else if (hid.equals("2A0")){
			//薪津表/扣款表
			if (selEn.matches("Prt_M_1|Prt_M_2")){
				if (itm==0) return "總計";
				if (itm==1) return "護理_師級";
				if (itm==2) return "護理_約用";
				if (itm==3) return "職員_公職";
				if (itm==4) return "醫事_約用";
				if (itm==5) return "醫事_師級";
				if (itm==6) return "醫師_師級+聘用";
				if (itm==7) return "醫師_約用";
				if (itm==8) return "技工友";
			}	
			//薪津表(補發扣)
			else if (selEn.matches("Prt_M_3")){
				if (itm==0) return "總計";
				if (itm==1) return "明細";
			}
			//Prt_M_6銀行轉帳明細表 Bank_T銀行轉帳明細表
			else if (selEn.matches("Prt_M_6|Bank_T")){
				if (itm==0) return "薪津";
				if (itm==1) return "公儲";
			}
		}
		else if (hid.equals("3A0")){
			//薪津表/扣款表
			if (selEn.matches("Prt_M_1|Prt_M_2")){
				if (itm==0) return "總計";
				if (itm==1) return "公職";
				if (itm==2) return "工級";
				if (itm==3) return "契約";
			}
			//薪津表(補發扣)
			else if (selEn.matches("Prt_M_3")){
				if (itm==0) return "總計";
				if (itm==1) return "明細";
			}
			//Prt_M_6銀行轉帳明細表 Bank_T銀行轉帳明細表
			else if (selEn.matches("Prt_M_6|Bank_T")){
				if (itm==0) return "薪津";
				if (itm==1) return "優惠存款";
			}
		}
		return itmCd;
	}
	/**傳回 9A 9B 執行業務者業別代號對照表
	 * @param itm 轉帳項目
	 * @return 執行業務者業別代號
	 * Ex: getCatgCd(16) return 10 
	 */
	public static String getCatgCd(int itm){		
		String itmCd = " ";
		if (itm==16) return "10";	//律師
		if (itm==17) return "20";	//技師
		if (itm==18) return "21";	//建築師
		if (itm==19) return "98";	//稿費 演講費
		if (itm==28) return "93";	//專利代理人 2011.1.14
		if (itm==29) return "70";	//表演人 2011.1.17
		if (itm==34) return "56";	//牙體技術師 2013.1.31
		if (itm==38) return "76";	//一般經紀人 2013.7.24
		if (itm==50) return "42";	//醫事檢驗師 2015.1.20
		if (itm==49) return "42";	//醫事檢驗師 2025.10.3
		if (itm==51) return "13";	//地政士 2017.1.11
		if (itm==53) return "17";	//不動產估價師 2018.1.22
		if (itm==54) return "50";	//中醫師 2019.1.22
		if (itm==55) return "30";	//內科醫師 2020.1.20
		if (itm==56) return "36";	//牙科醫師 2020.1.20
		if (itm==61) return "91";	//牙科醫師 2020.1.20
		if (itm==62) return "18";	//租賃 2024.1.4
		if (itm==63) return "46";	//支援醫師--醫師經核准至該他醫療機構服務但與該他醫療機構不具僱傭關係者 2024.1.4
		if (itm==71) return "61";	//書畫家、版畫家 2024.1.27
		if (itm==72) return "16";	//民間公證人 2024.1.27
		return itmCd;
	}
	/**傳回 補充保費保險別
	 * @param itm 代碼
	 * @return 保險中文名稱
	 * Ex: getInsuident(1) return 公保 
	 */
	public static String getInsuident(int itm){		
		String itmCd = " ";
		if (itm==0) return "無";	
		if (itm==1) return "公保";
		if (itm==2) return "勞保";
		return itmCd;
	}
	/**傳回 考勤_假別中文
	 * @param itm 假況
	 * @return 中文說明
	 * Ex: getOfftypNm(01) return 事 
	 */
	public static String getOfftypNm(String itm){		
		String itmCd = " ";
		if (itm.equals("01")) return "事假";	
		if (itm.equals("02")) return "病假";
		if (itm.equals("34")) return "家庭照顧假";
		if (itm.equals("35")) return "生理假";
		if (itm.equals("53")) return "公假-居家照護";
		return itmCd;
	}
	/**傳回 考勤_職號第2碼中文
	 * @param itm 職號第2碼
	 * @return 中文說明
	 * Ex: getPosNm('K') return '契約' 
	 */
	public static String getPosNm(String itm){		
		String itmCd = " ";
		if (itm.equals("K")) return "契約";	
		if (itm.equals("F")) return "半日契約";
		if (itm.equals("P")) return "不分科";
		if (itm.equals("T")) return "研究助理";
		if (itm.equals("E")) return "契約住院醫師";
		if (itm.equals("C")) return "聘用住院醫師";
		if (itm.equals("G")) return "技工";
		if (itm.equals("H")) return "工友";
		if (itm.equals("J")) return "契約主治醫師";		
		return itmCd;
	}
	/**傳回 考勤_每月有效天數,全日=30,半日=15
	 * @param itm 職號第2碼
	 * @return 天數
	 * Ex: getDayLimByPos('K') return 30 
	 */
	public int getDayLimByPos(String itm){		
		int itmDay = 30;
		if (itm.equals("K")) return 30;	
		if (itm.equals("F")) return 15;
		return itmDay;
	}
	/**傳回 不開業獎金(專二/工二)
	 * tracod=33(職務調整)升主管,職等有調整,要抓原先,2個月 2026/5/1 A152 3/30更換,5月
	4650(4/2) 3633(4/1),5月及6月
	 * @param jt職稱,jtsub職稱分類,rnk2n職等,degr2級數,pfdoc專勤額度,reten(專一=0,專二YN) csflag(工獎YN)
	 * @return 
	 * Ex: getOpenBonus('CB','H','',6,0,'','Y') return 20000 
	 * @throws Exception 
	 */
	public int getOpenBonus(String jt,String jtsub,String rnk2n,int degr2,int pfdoc,String reten,String csfg) throws Exception{		
		int rtnAmt = 0;
		if (jt.matches("AL|CB|CH|CK")){//AL不分科住院醫師(證書) CB聘用住院醫師 CH契約住院醫師(證書) CK PGY
			if (degr2==1 ||degr2==2)
				rtnAmt=20000;
			else if (degr2==3 ||degr2==4)
				rtnAmt=25000;
			else if (degr2==5 ||degr2==6)
				rtnAmt=30000;
		}
		else if (jt.matches("CG") || jtsub.matches("V|A")){//CG契主治 職稱分類=V醫師/A兼職
			if ( pfdoc == 0 && !reten.equals("Y") && csfg.equals("N") )
				rtnAmt=0;
			else{
				if (rnk2n.equals("P1"))
					rtnAmt=36000;
				else if (rnk2n.equals("P2"))
					rtnAmt=33000;
				else if (rnk2n.matches("P3|07"))	//16公職住院醫師職等=07
					rtnAmt=25000;
			}
		}
			
		return rtnAmt;
	}
	
	/**傳回 階別/職等/本功/級數
	 * @param porank官職等 pagrad人事俸級 jt職稱 emp職號首2碼
	 * @return 薪資階別/職等/本功/級數
	 * Ex: getDegrBygrade('BW13','01','A246','DJ','1A0') return rank='2' rnk2n='09' rank3=2 degr2=9
	 */
	public HashMap getDegrBygrade(String porank,String pagrad,String jt,String emp,String hid){
		HashMap rtnMap=null;
		int degr2=0;//級別
		int rank3=0;//本功
		String rnk2n="";//職等
		String rank="";//階別
		String tcode2 ="";
		String strTmp=StringUtil.nullToString(pagrad);
		if (!strTmp.equals("H") && !"".equals(strTmp)){//聯醫時薪H=0 級數空白
			if (strTmp.length()==2){
				if( strTmp.substring(0,1).matches("B|F")){
					if (strTmp.substring(0,1).equals("B"))
						rank3=1;
					else if (strTmp.substring(0,1).equals("F"))
						rank3=2;
					tcode2 = strTmp.substring(1,2);
					if (tcode2.matches("1|2|3|4|5|6|7|8|9")){
						degr2=Integer.parseInt(tcode2);
					}else{
						degr2 = (int)(tcode2.charAt(0))-55 ;
					}
				}else{
					degr2=Integer.parseInt(strTmp);
				}
			}else
				degr2=Integer.parseInt(strTmp);
			
			if (emp.matches("DK")) {	//契約
				rank ="F";
			}else if (porank.matches("BP01|BP02|BP03|BP04|BP05|BQ05")) {	//委
				rank ="3";
				rnk2n=porank.substring(2,4);
			}else if (porank.matches("BP06|BP07|BP08|BP09")) {	//薦
				rank="2";
				rnk2n=porank.substring(2,4);
			}else if (porank.matches("BP10|BP11|BP12|BP13|BP14")) {	//簡
				rank="1";
				rnk2n=porank.substring(2,4);
			}else if (porank.matches("BW11|BW12|BW13")) {	//師一
				rnk2n="P1";
				if (emp.equals("DJ")) //契主治 
					rank="F";
				else
					rank="8";
			}else if (porank.matches("BW20|BW21")) {	//師二
				rnk2n="P2";
				if (emp.equals("DJ")) 
					rank="F";
				else
					rank="8";
			}else if (porank.matches("BW30|BW31")) {	//師三
				rnk2n="P3";
				if (emp.equals("DJ"))
					rank="F";
				else
					rank="8";
			}else if (porank.matches("BW40")) {	//士
				rank="9";
				rnk2n="T1";
			}else if (porank.matches("BJ50")) {	//比照聘用住院醫師
				rnk2n="";
				if (jt.matches("A246|A232")) {	//A246契約住院醫師 A232不分科住院醫師
					rank="F";							
				}else if (jt.equals("A235")) {	//A235不分科住院醫師軍費生
					rank="0";
				}
			}
			
			rtnMap=new HashMap();
			rtnMap.put("rank", rank);
			rtnMap.put("rnk2n", rnk2n);
			rtnMap.put("rank3", rank3);
			rtnMap.put("degr2", degr2);
		}
		return rtnMap;
	}
	/**
	 * 關閉VGHDataAccess與ResultSet
	 * 
	 * @param da
	 * @param rs
	 */
	protected void closeAll(VGHDataAccess da, ResultSet rs,boolean blnclose) {
		if (rs != null) {
			try {
				rs.close();
				rs = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!blnclose){
			if (da != null) {
				try {
					da.close();
					da = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (daDSN != null) {
				try {
					daDSN.close();
					daDSN = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}	
	}	
}
