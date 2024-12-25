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
}