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
}