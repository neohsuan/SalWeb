package gov.vghks.sal.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import gov.vghks.common.util.DateUtil;
import gov.vghks.common.util.StringUtil;
import gov.vghks.common.util.VGHDataAccess;
import gov.vghks.sal.Globals;
import gov.vghks.sal.Hospitals;
import gov.vghks.sal.Tables;
import gov.vghks.sal.model.Pabaspay;
import gov.vghks.sal.model.Pabaspsn;
import gov.vghks.sal.model.Pabatpay;
import gov.vghks.sal.model.Pacspf;
import gov.vghks.sal.model.Padatpsn;
import gov.vghks.sal.model.Padept;
import gov.vghks.sal.model.Palbrptbl;
import gov.vghks.sal.model.Patitle;
import gov.vghks.sal.model.Patrapay;
import gov.vghks.sal.model.Patrapsn;
import gov.vghks.sal.model.Pawkretir;
import gov.vghks.sal.model.PsInsu;
import gov.vghks.sal.model.Psbasic;
import gov.vghks.sal.util.AmJsonConfig;
import gov.vghks.sal.util.PaCommUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

//勞工退休金
public class WkRetirDAO {
	String gClassName = WkRetirDAO.class.getName();
	static Logger log = Logger.getLogger(WkRetirDAO.class);  //呼叫log4j
	private VGHDataAccess da = null;
	private VGHDataAccess daDSN = null;
	static ArrayList<Map<String, Object>> result = null;
	static ArrayList<Object> params = new ArrayList<Object>();
	static String g_dataSourceNT = "";
	static String g_dataSource = "";
	private static final int META_TYPE = 2; 			//回傳map中欄位名稱是小寫
	private static boolean connFlg = false;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private String sql = "";
	private String hid = "";
	private String tDept 	 = ""; 	// 2單位代碼
	private String tTitle	 = ""; 	// 3職稱
	private String tBasPsn 	 = ""; 	// 4基本檔
	private String tBasPay 	 = ""; 	// 5基本支扣
	private String tBatPay   = "";	// 6批次檔
	private String tRankCode = ""; 	// 10階別代碼
	private String tDatPsn 	 = ""; 	// 23月支個人明細	
	private String tPayItm   = ""; 	// 27支扣項目彙總
	private String tTraPsn 	 = ""; 	// 30異動個人明細
	private String tWkretir  = "";	// 40勞工退休金檔
	private String tmpZbatpay = "";
	private String tmpZwkretir = ""; 
	
	public WkRetirDAO(VGHDataAccess da1,String hid) {
		super();
		this.da = da1;
		connFlg = true;
		this.hid = hid;
		Tables tabInfo = Tables.getTables(hid);
		
		tTitle 	  = tabInfo.tTitle;
		tDept 	  = tabInfo.tDept;
		tRankCode = tabInfo.tRankCode;
		tPayItm   = tabInfo.tPayItm;
		tBasPsn   = tabInfo.tBasPsn;
		tBasPay   = tabInfo.tBasPay;
		tBatPay   = tabInfo.tBatPay;
		tTraPsn   = tabInfo.tTraPsn;
		tWkretir  = tabInfo.tWKRETIR;
		tDatPsn   = tabInfo.tDatPsn;
		tmpZbatpay = tabInfo.tmpZbatpay;
		tmpZwkretir= tabInfo.tmpZwkretir;
	}
	
	public WkRetirDAO(VGHDataAccess da1,VGHDataAccess da2,String hid) {
		super();
		this.da = da1;
		this.daDSN = da2;
		connFlg = true;
		this.hid = hid;
		Tables tabInfo = Tables.getTables(hid);
		
		tTitle 	  = tabInfo.tTitle;
		tDept 	  = tabInfo.tDept;
		tRankCode = tabInfo.tRankCode;
		tPayItm   = tabInfo.tPayItm;
		tBasPsn   = tabInfo.tBasPsn;
		tBasPay   = tabInfo.tBasPay;
		tBatPay   = tabInfo.tBatPay;
		tTraPsn   = tabInfo.tTraPsn;
		tWkretir  = tabInfo.tWKRETIR;
		tDatPsn   = tabInfo.tDatPsn;
		tmpZbatpay = tabInfo.tmpZbatpay;
		tmpZwkretir= tabInfo.tmpZwkretir;
	}
	/**
	 * 勞工退休金處理 [只限高榮]selCode:selNm:selEn 1:[寫入]勞工退休金:Lab_1  
	 * 1:[寫入]勞工退休金:Lab_1,2[刪除]勞工退休金Lab_2,
	 * ex.2026.10.1發薪,1.發薪明細+人事勞健保.勞退級距>0
	 * 3[寫入]批次檔Lab_3 4[刪除]Lab_4
	 * 
	 **/
	public Map<String, Object> handelWkRetir(String in_data, String userid, String hid, VGHDataAccess da, VGHDataAccess daDSN)
			throws Exception {
		String me = getClass().getName() + ".handelWkRetir(),userid = " + userid;
		log.info(me + ",in_data = " + in_data + ",userid = " + userid + ",hid = " + hid);
		AmJsonConfig cfg = new AmJsonConfig();
		StringBuilder msg = new StringBuilder();
		String rtn = "";
		ResultSet rs = null;
		CommDAO cDAO = null;
		PsDSNDAO psDAO = null;
		String selEn = ""; // 處理項目
		String selNm = ""; // 處理項目中文名稱
		ArrayList<Object> batch_sql = new ArrayList<Object>(); // batch_update sql 資料
		long starttime = 0;
		long starttime9 = 0;
		String patradt = ""; // 發薪日
		int salnum=0;//餉單編號
		Padatpsn datpsnobj = null; 	// 發薪明細個人obj
		PsInsu insuobj = null; 		// 保險obj
		Pawkretir wkretirobj = new Pawkretir();
		Palbrptbl lbrptobj = new Palbrptbl();
		HashMap dataMap = null;
		HashMap<String, Pawkretir> wkretirMap = new HashMap();// 勞工退休金檔 map
		HashMap<String, PsInsu> psinsuMap = new HashMap();	// 人事勞健保保 map
		HashMap<Integer, Palbrptbl> lbretirMap = new HashMap();	// Palbrptbl
		HashMap<String,Integer> l_lbinMap=new HashMap();	//取得勞保參考檔
		int cnt = 0,monpay=0,masteramt=0,selfamt=0;
		Integer wrinig=0,l_salnum=0;
		double selfvol=0;
		ArrayList<Object> paramA = new ArrayList<Object>();
		boolean tmpflag = true;
		String sqltmp="",strflag="",l_paupbas="",l_payflag="",l_upmastdt="",l_paycode="";
		int write_cnt = 0;
		int limitcnt = 100;
		//sqltmp=" and paycard in ('0069','HJ39','5344') ";
				
		// 勞工退休金_找發薪日
		String sqlWkretirBytradt = "select COUNT(*) AS CNT from " + this.tWkretir + " where HID=? and PATRADT=?  with ur";
		// 勞工退休金_刪除
		String sqlWkretir_Del = "delete " + this.tWkretir + " where HID=? and patradt = ? ";
		// 勞工退休金_找發薪明細勞保身分 payrank in(5,6,E,F) 		
		String sqlDatPsn_Wk = "select SALNUM, PAYBANK, PAYIDNO, PAYCARD, PAYEMPNO, PAYNAMEC, PAYDEPT, PAYTITLE, PAYRNK2N, PAYRANK3, PAYDEGR2, PAYDEGRE, PAYRANK, PAYRANK2, PAYRANKC, PAYPFDOC, PACSFLAG, PAYPFSP1, PAYPFSP2, PAYPFSP3, PAYUPDID, PAYUPDTM, PAYRANK4, HID, INSUAMT,RANKABBR " + 
							   " from " + this.tDatPsn + " A " +
							   " left outer join " + this.tRankCode + " B on A.PAYRANK=B.RANKCODE " +
							  " where HID=? and SALNUM=? and PAYRANK in('5','6','E','F') "+sqltmp+" with ur ";
		// 勞工退休金_找發薪日及自繳退休金>0		
		String sqlWkretir_VD060 = "select PATRADT, PAYIDNO, PAYCARD, PAYRANK, PAYTITLE, PAYSORT, MONPAY, MASTERAMT, SELFEXRATE, SELFAMT, PAYMONNO, PAYDAYNO, PADUTYID, PAYUPDAT, PAYFLAG, HID, "+tmpZwkretir + 
							   " from " + this.tWkretir +
							  " where HID=? and PATRADT=? and SELFAMT>0 "+sqltmp+" with ur ";
				
		// 批次檔_刪除VD060
		String sqlBat_DelVD060 = "delete " + this.tBatPay + " where HID=? and PATRADT = ? and PAYCODE=?";
		// 批次檔_寫入VD060
		String sqlBat_insVD060 = " insert into " + this.tBatPay
								+ " (PAYCARD, PAUPBAS, PAYAMT, PADUTYID, PAYUPDAT, PAYFLAG, UPMASTDT, "+tmpZbatpay+",PATRADT, PAYIDNO, SALNUM, PAYCODE, HID) "
								+ "values (?,?,?,?,current timestamp,?,?,current timestamp,?,?,?,?,?) ";		
		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}
			da.setAutoCommit(false);
			cDAO = new CommDAO(da, hid);

			JSONObject object_in_data = JSONObject.fromObject(in_data);
			selEn = StringUtil.nullToString(object_in_data.getString("selEn"));
			patradt = StringUtil.nullToString(object_in_data.getString("patradt"));
			salnum = object_in_data.getInt("salnum");
			
			starttime = System.currentTimeMillis();
			// [寫入]勞工退休金
			if ("Lab_1".equals(selEn)) {
				// 查詢_依hid+發薪日期
				paramA.add(hid);
				paramA.add(patradt);
				log.info(me + "  1.查詢_依HID+發薪日期" + sqlWkretirBytradt + "...params:" + paramA.toString());
				rs = da.query(sqlWkretirBytradt, paramA);

				while (rs.next()) {
					if (rs.getString("CNT") != null && rs.getInt("CNT")>0 ) {
						msg.append("[勞工退休金]已有" + patradt + "資料,請確認");
						tmpflag = false;
					}
				}
				if (tmpflag) {
					//1.找人事保險檔
					psDAO = new PsDSNDAO(daDSN,hid);
					psinsuMap = psDAO.getInsuMap(hid,  daDSN);
					log.info(me + " 1.找人事保險檔,筆數:" + psinsuMap.size( ));
					//1-1.找勞工退休金map
					lbretirMap = cDAO.getLbretirMap(da);
					log.info(me + " 1-1.找勞工退休金map,筆數:" + psinsuMap.size( ));
					//2.找發薪明細_發薪日+勞保身分
					paramA = new ArrayList<Object>();
					paramA.add(hid);
					paramA.add(salnum);
					log.info(me + " 2.找發薪明細_發薪日+勞保身分:" + sqlDatPsn_Wk + "...params:" + paramA.toString());
					result = da.arraylistbyQuery(sqlDatPsn_Wk, paramA, META_TYPE);
					log.info(me + "...size:" + result.size());
					if (result.size() > 0) {
						for (Map<String, Object> data : result) {
							datpsnobj = new Padatpsn();
							PropertyUtils.copyProperties(datpsnobj, data);
//							if(datpsnobj.getPayidno().equals("E223748822")) {
//								System.out.print("xx");
//							}
							insuobj = psinsuMap.get(datpsnobj.getPayidno());
							monpay = 0;masteramt=0;selfamt=0;selfvol=0;wrinig=0;
							strflag="X";
							dataMap = new HashMap();
							if (insuobj != null) {								
								wrinig = insuobj.getWrinig();		//人事保險檔.勞退級距
								selfvol = (double) insuobj.getSelfvol();	//人事保險檔個人自願提繳率
								lbrptobj=lbretirMap.get(wrinig);//找勞退map								
								if ( lbrptobj != null ) {
									monpay = lbrptobj.getLbrpamt();		//月提繳工資
									masteramt = lbrptobj.getMasteramt();//公提退休金
									if (selfvol>0) {
										selfamt =  (int) Math.round( (double)wrinig * selfvol / 100) ;	//自繳退休金
									}
								}
								strflag="O";
							}	
							
							dataMap.put("hid", hid);
							dataMap.put("tradt", patradt);
							dataMap.put("paysort", 1);
							dataMap.put("monpay",monpay);
							dataMap.put("masteramt",masteramt);	//公提退休金=月提繳工資*6%
							dataMap.put("selfexrate",selfvol);	//人事保險檔.個人自願提繳率 0-6
							dataMap.put("selfamt",selfamt);		//自繳退休金
							dataMap.put("paymonno",0);			//補發月數
							dataMap.put("paydayno",0);			//補發日數
							dataMap.put("payflag",strflag);		//處理旗標 psinsu,O:找到 X:沒找到
							// 寫入勞退obj
							wkretirobj = new Pawkretir();
							wkretirobj = putWkretirObj(datpsnobj, dataMap, userid);
							// 寫入勞退map
							wkretirMap.put(datpsnobj.getPayidno(), wkretirobj);
							
							log.info(me + cnt++ +"...idno:" + datpsnobj.getPayidno()+" insu:"+wrinig);
						}
						// 寫入勞退檔
						starttime9 = System.currentTimeMillis();
						rtn = updWkretirTblMap(wkretirMap, hid, da);
						if ( !rtn.equals("") ) {
							tmpflag=false;
							msg.append(rtn);
						}else {
							msg.append("寫入筆數:"+ wkretirMap.size() +" 共花費：" + (System.currentTimeMillis() - starttime9) + "毫秒");
						}
						
						log.info(me + "  4.寫入勞工退休金,筆數:"+ cnt +" 共花費：" + (System.currentTimeMillis() - starttime9) + "毫秒");
					}		
				}
			}//if ("Lab_1".equals(selEn)) 
			else if ("Lab_2".equals(selEn)) {	//[刪除]勞工退休金
				paramA = new ArrayList<Object>();
				params = new ArrayList<Object>();
				paramA.add(hid);
				paramA.add(patradt);				
				params.add(paramA);
				batch_sql.add(sqlWkretir_Del);
				da.startTransaction();
				try {
					log.info(batch_sql + " " + batch_sql + " params:" + params);
					da.batchupdatenocommit(batch_sql, params);
					da.commit();
					da.setAutoCommit(false);
					batch_sql.clear();
					params.clear();
				} catch (Exception e) {
					da.rollback();
					rtn = " fail, " + e;
					msg.append(rtn);
					tmpflag=false;
					da.setAutoCommit(true);
					e.printStackTrace();
					log.info(me + " " + selNm + " " + e.toString());
				} finally {

				}
			}//if ("Lab_2".equals(selEn)) 
			else if ("Lab_3".equals(selEn)) {	//[匯入]批次檔VD060
				paramA = new ArrayList<Object>();
				params = new ArrayList<Object>();
				paramA.add(hid);
				paramA.add(patradt);
				params.add(paramA);
				
				result = da.arraylistbyQuery(sqlWkretir_VD060, paramA, META_TYPE);
				log.info(me + "  1.找勞退_發薪日+自繳退休金>0 " + sqlWkretir_VD060 + "...params:" + paramA.toString()+" size:"+result.size());
				l_paupbas="N";
				l_payflag="";
				l_upmastdt="";
				l_salnum=0;
				l_paycode="VD060";
				params = new ArrayList<Object>();
				da.startTransaction();
				try {
					if (result.size() > 0) {
						for (Map<String, Object> data : result) {
							wkretirobj = new Pawkretir();
							PropertyUtils.copyProperties(wkretirobj, data);
							batch_sql.add(sqlBat_insVD060);
							paramA = new ArrayList<Object>();						
	//						sqlBatpay_ins = " insert into " + this.tBatPay
	//								+ " (PAYCARD, PAUPBAS, PAYAMT, PADUTYID, PAYUPDAT, PAYFLAG, UPMASTDT, " + tmpZbatpay + ", PATRADT, PAYIDNO, SALNUM, PAYCODE, HID)"
	//								+ " values( ?,?,?,?,current timestamp,?, ?,current timestamp,?,?,?,?,?) ";
							paramA.add(StringUtil.nullToString(wkretirobj.getPaycard()));	
							paramA.add(l_paupbas);
							paramA.add(wkretirobj.getSelfamt());
							paramA.add(userid);
							paramA.add(l_payflag);
							paramA.add(l_upmastdt);
							paramA.add(patradt);
							paramA.add(StringUtil.nullToString(wkretirobj.getPayidno()));
							paramA.add(l_salnum);
							paramA.add(l_paycode);
							paramA.add(hid);
							log.info(" paramA:" + paramA);
							params.add(paramA);
							
							if (++write_cnt % limitcnt == 0) {
								log.info(batch_sql + " 寫入:" + batch_sql + " params:" + params);
								da.batchupdatenocommit(batch_sql, params);
								da.commit();
	
								batch_sql.clear();
								params.clear();
							}
						}
					}
					da.batchupdatenocommit(batch_sql, params);
					da.commit();				
					
					rtn="寫入批次檔_發薪日"+patradt+"支扣代碼VD060 ,筆數: " + result.size() + " 共花費："+ (System.currentTimeMillis() - starttime) + "毫秒";
					msg.append(rtn);
					log.info(me + rtn);
				} catch (Exception e) {
					da.rollback();
					rtn = " fail, " + e;
					msg.append(rtn);
					tmpflag=false;
					da.setAutoCommit(true);
					e.printStackTrace();
					log.info(me + " " + selNm + " " + e.toString());
				} finally {

				}
			}//if ("Lab_4".equals(selEn)) 
			else if ("Lab_4".equals(selEn)) {	//[刪除]批次檔VD060
				paramA = new ArrayList<Object>();
				params = new ArrayList<Object>();
				paramA.add(hid);
				paramA.add(patradt);
				paramA.add("VD060");
				params.add(paramA);
				batch_sql.add(sqlBat_DelVD060);
				da.startTransaction();
				try {
					log.info(batch_sql + " " + batch_sql + " params:" + params);
					da.batchupdatenocommit(batch_sql, params);
					da.commit();
					da.setAutoCommit(false);
					batch_sql.clear();
					params.clear();
				} catch (Exception e) {
					da.rollback();
					rtn = " fail, " + e;
					msg.append(rtn);
					tmpflag=false;
					da.setAutoCommit(true);
					e.printStackTrace();
					log.info(me + " " + selNm + " " + e.toString());
				} finally {

				}
			}//if ("Lab_4".equals(selEn)) 
			log.info(me + "  ...********* 共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Exception e) {
			rtn = " fail, " + e;
			msg.append(rtn);
			tmpflag=false;
			e.printStackTrace();
			log.info(me + " " + e.toString());
		} finally {
			closeAll(da, rs, connFlg);
		}
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", tmpflag);
		resultMap.put("msg", msg.toString());
	    return resultMap;
	}
	// bpsnobj:基本檔,nMap:新敘薪 PAWKRETIR
	private Pawkretir putWkretirObj(Padatpsn oneobj, HashMap nMap, String userid) {
		String me = ".putWkretirObj()";
		Pawkretir rtnobj = new Pawkretir();
		String strtmp="";
		Object l_value = null;
		if (oneobj != null) {
			rtnobj.setPatradt(java.sql.Date.valueOf((String) nMap.get("tradt"))); // 1發薪日期
			rtnobj.setPayidno(oneobj.getPayidno()); 			// 1身分證字號
			rtnobj.setPaycard(oneobj.getPaycard()); 			// 2卡號
			rtnobj.setPayrank(oneobj.getRank()); 			// 3階級
			rtnobj.setPaytitle(oneobj.getPaytitle()); 			// 4--職稱代碼
			rtnobj.setPaysort((int) nMap.get("paysort")); 		// 5.分類(1:固定 2:補發項)
			rtnobj.setMonpay((int) nMap.get("monpay")); 		// 6.月提繳金額
			rtnobj.setMasteramt((int) nMap.get("masteramt")); 	// 7.公提退休金6%
			l_value = nMap.get("selfexrate");
			strtmp = String.valueOf(l_value);
			rtnobj.setSelfexrate(strtmp); 						// 8.自願提繳率
			rtnobj.setSelfamt((int) nMap.get("selfamt")); 		// 9.自繳退休金額
			rtnobj.setPaymonno((int) nMap.get("paymonno")); 	// 10.補發月數
			rtnobj.setPaydayno((int) nMap.get("paydayno")); 	// 11.補發日數
			rtnobj.setPadutyid(userid); 						// 12 更改者
			// rtnobj.setPayupdat( new java.sql.Timestamp(System.currentTimeMillis())); //13
			rtnobj.setPayflag((String) nMap.get("payflag")); 	// 14處理旗標 X:找不到psinsu O:有找到psinsu
			rtnobj.setHid(oneobj.getHid()); 					// 15醫院別			
		}
		return rtnobj;

	}
	// 寫入勞工退休金檔wkretirMap	
	private String updWkretirTblMap(HashMap<String, Pawkretir> wkretirMap,String hid, VGHDataAccess da) {
		String me = getClass().getName() + ".updWkretirTblMap()";
		String rtn = "";
		ArrayList<Object> param = new ArrayList<Object>();
		ArrayList<Object> batch_sql = new ArrayList<Object>(); // batch_update sql 資料
		int write_cnt = 0;
		int limitcnt = 100;
		// 勞工退休金檔_寫入_PAWKRETIR
		String sqlWkretir_ins = " insert into " + this.tWkretir +
				               " (PATRADT, PAYIDNO, PAYCARD, PAYRANK, PAYTITLE, PAYSORT, MONPAY, MASTERAMT, SELFEXRATE, SELFAMT, PAYMONNO, PAYDAYNO, PADUTYID, PAYUPDAT, PAYFLAG, HID, "+ tmpZwkretir +") "+
				               " values(?,?,?,?,?,?,?,?,?,?,?,?,?,current timestamp,?,?,current timestamp) ";
		
		try {
			da.setAutoCommit(false);
			if (wkretirMap != null) {
				for (Map.Entry<String, Pawkretir> wkretirentry : wkretirMap.entrySet()) {
					Pawkretir data = wkretirentry.getValue();
					if (data != null) {
						batch_sql.add(sqlWkretir_ins);
						
						param = new ArrayList<Object>();
						//(PATRADT, PAYIDNO, PAYCARD, PAYRANK, PAYTITLE, 
						param.add(data.getPatradt());
						param.add(data.getPayidno());
						param.add(data.getPaycard());
						param.add(data.getPayrank());
						param.add(data.getPaytitle());
						//PAYSORT, MONPAY, MASTERAMT, SELFEXRATE, SELFAMT,
						param.add(data.getPaysort());
						param.add(data.getMonpay());
						param.add(data.getMasteramt());
						param.add(data.getSelfexrate());
						param.add(data.getSelfamt());
						//PAYMONNO, PAYDAYNO, PADUTYID, PAYUPDAT, PAYFLAG,HID, "+ tmpZwkretir +")
						param.add(data.getPaymonno());
						param.add(data.getPaydayno());
						param.add(data.getPadutyid());
						param.add(data.getPayflag());
						param.add(data.getHid());
						params.add(param);
					}
					if (++write_cnt % limitcnt == 0) {
						log.info(batch_sql + " 寫入:" + batch_sql + " params:" + params);
						da.batchupdatenocommit(batch_sql, params);
						da.commit();

						batch_sql.clear();
						params.clear();
					}					
				}
				//log.info(batch_sql + " " + batch_sql + " params:" + params);
				da.batchupdatenocommit(batch_sql, params);
				da.commit();
			}

			
		}	
		catch (Exception e) {
			da.rollback();
			rtn = " fail, " + e;
			da.setAutoCommit(true);
			e.printStackTrace();
			log.info(me + " " + e.toString());
		} finally {

		}
		return rtn;
	}	
	// 勞工退休金Pawkretir
	public HashMap<String, Object> getWkRetirList(String in_data, String userid, String hid, VGHDataAccess da)
			throws Exception {
		String me = getClass().getName() + ".getWkRetirList() userid:" + userid;
		log.info(me + " in_data = " + in_data + " hid = " + hid + " userid = " + userid);
		CommDAO cDAO = null;
		JSONArray jsonRtn = new JSONArray();
		HashMap<String, Object> rtnMap = new HashMap();
		ArrayList<Object> params = new ArrayList<Object>();
		JSONObject jsonOne = null;
		JSONObject object_in_data = null;
		long starttime = 0;
		String subsqlA = "";
		String cardno = "";
		String idtno = "";
		String patradt = "";
		
		starttime = System.currentTimeMillis();
		if (in_data != null && !in_data.equals("")) {
			object_in_data = JSONObject.fromObject(in_data);
			cardno = (!object_in_data.has("cardno")) ? "" : object_in_data.getString("cardno");
			idtno = (!object_in_data.has("idtno")) ? "" : object_in_data.getString("idtno");
			patradt = (!object_in_data.has("patradt")) ? "" : object_in_data.getString("patradt");
		}
		sql = "select a.PATRADT, a.PAYIDNO, a.PAYCARD, COALESCE(b.paynamec,b1.paynamec)paynamec,a.PAYRANK, a.PAYTITLE,c.paytitlp, PAYSORT, MONPAY, MASTERAMT, SELFEXRATE, SELFAMT, PAYMONNO, PAYDAYNO, PADUTYID, a.PAYUPDAT, PAYFLAG, a.HID "+
			   " from " + this.tWkretir + " A " + 
			   " left outer join " + this.tBasPsn + " B  on A.PAYIDNO=B.PAYIDNO  AND A.HID=B.HID "+
			   " left outer join " + this.tDatPsn + " B1 on A.PAYIDNO=B1.PAYIDNO AND A.HID=B1.HID AND a.patradt=b1.paybank "+
			   " left outer join " + this.tTitle + " C on a.paytitle=c.paytitlk AND a.hid=b.hid "+ " where A.HID=? ";
		params.add(hid);
		if (!"".equals(cardno)) {// 卡號
			subsqlA += " and A.PAYCARD=? ";
			params.add(cardno);
		} else if (!"".equals(idtno)) {// 身分證
			subsqlA += " and A.PAYIDNO=? ";
			params.add(idtno);
		} 
		if(!"".equals(patradt)) {// 發薪日
			subsqlA += " and A.PATRADT=? ";
			params.add(patradt);
		}
		sql += subsqlA + " order by PATRADT desc,A.PAYCARD with ur";
		log.info(me + "  " + sql + "...params:" + params.toString());

		try {
			if (da == null) {
				// 改用列舉
				Hospitals hospInfo = Hospitals.getOneHosp(hid);
				g_dataSourceNT = hospInfo.dataSourceNT;
				da = new VGHDataAccess(g_dataSourceNT);
				connFlg = true;
			}

			log.info(me + "  " + sql + "...params:" + params.toString());
			result = da.arraylistbyQuery(sql, params, META_TYPE);
			log.info(me + "...size:" + result.size());

			if (result.size() > 0) {
				for (Map<String, Object> data : result) {
					jsonOne = new JSONObject();

					jsonOne.put("patradt", StringUtil.nullToString(data.get("patradt")));
					jsonOne.put("payidno", StringUtil.nullToString(data.get("payidno")));
					jsonOne.put("paycard", StringUtil.nullToString(data.get("paycard")));
					jsonOne.put("paynamec", StringUtil.nullToString(data.get("paynamec")));
					jsonOne.put("payrank", StringUtil.nullToString(data.get("payrank")));					
					jsonOne.put("paytitle", StringUtil.nullToString(data.get("paytitle")));
					jsonOne.put("paytitlp", StringUtil.nullToString(data.get("paytitlp")));
					jsonOne.put("paysort", StringUtil.nullToString(data.get("paysort")));
					jsonOne.put("monpay", StringUtil.nullToString(data.get("monpay")));
					jsonOne.put("masteramt", StringUtil.nullToString(data.get("masteramt")));
					jsonOne.put("selfexrate", StringUtil.nullToString(data.get("selfexrate")));
					jsonOne.put("selfamt", StringUtil.nullToString(data.get("selfamt")));
					jsonOne.put("paymonno", StringUtil.nullToString(data.get("paymonno")));
					jsonOne.put("paydayno", StringUtil.nullToString(data.get("paydayno")));
					jsonOne.put("padutyid", StringUtil.nullToString(data.get("padutyid")));
					jsonOne.put("payflag", StringUtil.nullToString(data.get("payflag")));
					jsonOne.put("hid", StringUtil.nullToString(data.get("hid")));
					jsonOne.put("payupdat", sdf.format(data.get("payupdat")));
					
					jsonRtn.add(jsonOne);
					
				}
			}
			rtnMap.put("wkretir_json", jsonRtn);
			

			log.info(me + "  ...********* dataaccess:共花費：" + (System.currentTimeMillis() - starttime) + "毫秒");
		} catch (Exception e) {
			log.info(me + " fail, " + e);
			throw new SQLException(me + ", fail!!." + e);
		} finally {
			closeAll(da, null, connFlg);
		}

		return rtnMap;
	}			
	/**
	 * 關閉VGHDataAccess與ResultSet
	 * 
	 * @param da
	 * @param rs
	 */
	protected void closeAll(VGHDataAccess da, ResultSet rs, boolean blnclose) {
		if (rs != null) {
			try {
				rs.close();
				rs = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!blnclose) {
			if (da != null) {
				try {
					da.close();
					da = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
