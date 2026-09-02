/*
 * 在 2021/12/8 建立
 *
 * 若要變更這個產生的檔案的範本，請移至
 * 視窗 > 喜好設定 > Java > 程式碼產生 > 程式碼和註解
 */
package gov.vghks.sal.util;
import java.util.*;
import org.apache.log4j.Logger;
/**
 * PS專案公用functon
 * @author 3007
 *
 */
public class PaCommUtil {
	static String className = PaCommUtil.class.getName();
	static Logger log = Logger.getLogger(PaCommUtil.class); //呼叫log4j	

	/**傳回 本功 中文名稱
	 * @param itm 
	 * @return  
	 * Ex: getRank3Nm(1) return 本 
	 */
	public static String getRank3Nm(int itm){		
		String itmName = "";
		if (itm==0) return "";
		if (itm==1) return "本";
		if (itm==2) return "功";
		return itmName;
	}
	/**傳回 職等 中文名稱
	 * @param itm 
	 * @return  
	 * Ex: getRank3Nm(1) return 本 
	 */
	public static String getRnk2nNm(String itm){		
		String itmName = "";
		if (itm.equals("P1")) return "師一";
		else if (itm.equals("P2")) return "師二";
		else if (itm.equals("P3")) return "師三";
		else if (itm.equals("T1")) return "士級";
		else if (itm.equals("01")) return "委一";
		else if (itm.equals("02")) return "委二";
		else if (itm.equals("03")) return "委三";
		else if (itm.equals("04")) return "委四";
		else if (itm.equals("05")) return "委五";
		else if (itm.equals("06")) return "薦六";
		else if (itm.equals("07")) return "薦七";
		else if (itm.equals("08")) return "薦八";
		else if (itm.equals("09")) return "薦九";
		else if (itm.equals("10")) return "簡十";
		else if (itm.equals("11")) return "簡十一";
		else if (itm.equals("12")) return "簡十二";
		else if (itm.equals("13")) return "簡十三";
		return itmName;
	}
	/**傳回 保險費比率 中文名稱 
	 * @param itm 
	 * @return  
	 * Ex: getRank3Nm(1) return 本 
	 */
	public static String getInflgNm(int itm){		
		String itmName = "";
		if (itm==0) return "";
		if (itm==1) return "1/4";
		if (itm==2) return "1/2";
		if (itm==3) return "全額";
		if (itm==4) return "全額有上限";
		if (itm==5) return "公勞1/4、健免";
		if (itm==6) return "公勞1/2、健免";
		if (itm==7) return "公勞有、健免";
		if (itm==8) return "公勞有、健1/4";
		if (itm==9) return "公勞有、健1/2";
		return itmName;
	}
	/**傳回 轉帳項目下一個值 
	 * @param itm 
	 * @return xx表己達最大值  
	 * Ex: getCatgNx("1") return 15 getCatgNx("15") return XX
	 */
	public static String getCatgNext(String itm){		
		String itmName = "";
		if (itm.equals("1")) return "15";
		if (itm.matches("15|52|58|13|49|45")) return "XX";
		else if (itm.equals("24")) return "25";
		else if (itm.equals("25")) return "52";
		else if (itm.equals("35")) return "57";
		else if (itm.equals("57")) return "58";
		else if (itm.equals("2")) return "7";
		else if (itm.equals("7")) return "8";
		else if (itm.equals("8")) return "9";
		else if (itm.equals("9")) return "13";
		else if (itm.equals("14")) return "20";
		else if (itm.equals("20")) return "21";
		else if (itm.equals("21")) return "22";
		else if (itm.equals("22")) return "23";
		else if (itm.equals("23")) return "39";
		else if (itm.equals("39")) return "40";
		else if (itm.equals("40")) return "46";
		else if (itm.equals("46")) return "47";
		else if (itm.equals("47")) return "48";
		else if (itm.equals("48")) return "49";
		else if (itm.equals("3")) return "5";
		else if (itm.equals("5")) return "6";
		else if (itm.equals("6")) return "10";
		else if (itm.equals("10")) return "11";
		else if (itm.equals("11")) return "12";
		else if (itm.equals("12")) return "30";
		else if (itm.equals("30")) return "31";
		else if (itm.equals("31")) return "32";
		else if (itm.equals("32")) return "33";
		else if (itm.equals("33")) return "41";
		else if (itm.equals("41")) return "42";
		else if (itm.equals("42")) return "43";
		else if (itm.equals("43")) return "44";
		else if (itm.equals("44")) return "45";
		return itmName;
	}
	/**傳回 職稱級數range 
	 * @param jt 
	 * @return map(max:  
	 * Ex: getDegre2Rng("DB","1A0") return map(min=35,max=40)
	 * 	   getDegre2Rng("F4","2A0") return map(min=207,max=212) 屏榮級數由200起算
	 */
	public static HashMap getDegre2Rng(String jt,String hid){
		HashMap<String, Integer> rtnMap=new HashMap();
		int vMin=0;
		int vMax=0;
		if (hid.equals("1A0")) {
			if (jt.equals("DB")) { //DB研究員
				vMin=35;
				vMax=40;
			}else if (jt.equals("DC")) { //DC副研究員
				vMin=33;
				vMax=40;
			}else if (jt.equals("DR")) { //DR高級助理研究員
				vMin=22;
				vMax=40;
			}else if (jt.equals("DX")) { //DX資訊助理工程師
				vMin=22;
				vMax=40;
			}else if (jt.equals("DZ")) { //DZ資訊工程師
				vMin=12;
				vMax=28;
			}else if (jt.equals("EN")) { //EN契約高級資訊工程師    
				vMin=21;
				vMax=37;
			}else if (jt.equals("EM")) { //EM資深高級資訊工程師    
				vMin=32;
				vMax=40;
			}else if (jt.equals("EO")) { //EO助理研究員    
				vMin=14;
				vMax=29;
			}else if (jt.equals("CK")) { //CK二年有證不分科住院醫師  
				vMin=1;
				vMax=2;
			}
			else if (jt.matches("E8|EG")) { //E8醫務管理高級專員 EG醫務高級技術師
				vMin=24;
				vMax=37;
			}
			else if (jt.matches("DD|E0|E1|E2|E3|E4|E5|E6|E7|DT|EB|ED|EK")) { 
				//DD醫事檢驗師 E0契約醫事檢驗師 E1醫事放射師 E2營養師 E3物理治療師 E4職能治療師 E5呼吸治療師 E6臨床心理師 E7語言治療師 DT半日契約醫事技術師EB牙體技術師 ED聽力師 EK契約半日聽力師
				vMin=12;					 
				vMax=37;
			}
			else if (jt.matches("DE|EF|EI")) { 
				//DE行政助理  EF廚務佐理員 EI契約照服員
				vMin=1;					 
				vMax=20;
			}
			else if (jt.matches("DG|EA")) { 
				//DG醫務管理組員 EA醫務副技師
				vMin=8;					 
				vMax=22;
			}
			else if (jt.matches("DF|DQ")) { 
				//DF社會工作師 DQ社會工作員
				vMin=11;					 
				vMax=37;
			}
			else if (jt.matches("DH|DI")) { 
				//DH醫務管理專員 DI醫務技術師
				vMin=15;					 
				vMax=30;
			}
			else if (jt.matches("DN|DW|EC")) { 
				//DN藥師 DW半日藥師 EC實習藥師
				vMin=21;					 
				vMax=40;
			}else if (jt.matches("EE|EL")) { //EE契約司廚員   EL駐衛警 
				vMin=6;
				vMax=25;
			}
			else if (jt.matches("DM|DS|EJ")) { //DM護理, DS半日契約護理, EJ契約麻醉護理 
				vMin=11;
				vMax=37;
			}
			else if (jt.matches("CB|CH|AL")) { 
				//CB聘用住院醫師 CH 契約住院醫師 AL不分科住院醫師
				vMin=1;					 
				vMax=6;
			}
		}else if (hid.equals("2A0")) {
			if (jt.matches("DN|DW")) { //DN藥師,DW半日契約藥師(17-21)
				vMin=217;
				vMax=221;
			}else if (jt.matches("E5|E1|E3")) { //E5約用呼吸治療師 E1醫事放射師 E3物理治療師16-24)
				vMin=216;
				vMax=224;
			}else if (jt.matches("E6")) { //E6心理師20-28
				vMin=220;
				vMax=228;
			}else if (jt.matches("E0|E4")) { //E0醫事檢驗師 E4職能治療師14-24)
				vMin=214;
				vMax=224;
			}else if (jt.matches("E7|ED")) { //E7語言治療師 ED聽力師14-26)
				vMin=214;
				vMax=226;
			}else if (jt.matches("E2|EH")) { //E2營養師 EH驗光師10-22)
				vMin=210;
				vMax=222;
			}else if (jt.matches("EB")) { //EB牙體技術師(6 - 20)
				vMin=206;
				vMax=220;
			}else if (jt.matches("EY|ES")) { //EY物理治療生,ES職能治療生(1 - 8)
				vMin=201;
				vMax=208;
			}else if (jt.matches("F1")) { //F1約用資深高級資訊工程師(22 -30)
				vMin=222;
				vMax=230;
			}else if (jt.matches("F2")) { //F2高級資訊工程師 18-28
				vMin=218;
				vMax=228;
			}else if (jt.matches("DZ|DF")) { //DZ契約資訊工程師) DF社會工作師 12-24
				vMin=212;
				vMax=224;
			}else if (jt.matches("DX")) { //DX資訊助理工程師(12 - 17)
				vMin=212;
				vMax=217;
			}else if (jt.matches("EG|E8")) { //EG高級技術師 E8高級專員(20 - 29)
				vMin=220;
				vMax=229;
			}else if (jt.matches("DI|DH")) { //DI技術師 DH專員(13 - 22) 
				vMin=213;
				vMax=222;
			}else if (jt.matches("DU")) { //DU約用副技術師(9 - 20)
				vMin=209;
				vMax=220;
			}else if (jt.matches("DG")) { //DG組員(9-18)
				vMin=209;
				vMax=218;
			}else if (jt.matches("DQ")) { //DQ社會工作員(7 - 11)
				vMin=207;
				vMax=211;
			}else if (jt.matches("F4")) { //F4副組員(7 - 12)
				vMin=207;
				vMax=212;
			}else if (jt.matches("DE|F5|DD")) { //DE行政助理 F5駕駛 DD牙醫助理(5 - 12)
				vMin=205;
				vMax=212;
			}else if (jt.matches("DT")) { //DT照護服務員(3 - 7)
				vMin=203;
				vMax=207;
			}else if (jt.matches("DP")) { //DP服務員(1 - 6)
				vMin=201;
				vMax=206;
			}else if (jt.matches("EI|DM")) { 
//				'EI臨床護士(08輪班單位 3 - 11)(09非輪班單位 1 - 8)
//                DM臨床護理師 抓人事護理單位分類
//                2025-1-1==>DM臨床護理師(10專科護理師 12 - 19)(11訓練中專師 12 - 15)(01麻醉護理師 12 - 24)
//                (02 ICU重症病房,03洗腎室,04手術室產房,05病房單位 10 - 22)(06特殊單位護理師 7 - 19)(07一般門診(注射室、癌症中心) 7-15)
//                (12一般門診護理師 5-13)(13檢查室技術員 11-21)
				vMin=200;
				vMax=200;
			}
		}
		rtnMap.put("min", vMin);
		rtnMap.put("max", vMax);
		return rtnMap;
	}
}