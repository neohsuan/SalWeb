package gov.vghks.sal.util;

import gov.vghks.common.util.converter.ConverterA;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//import org.apache.log4j.Logger;

/**
 * @author 9360 
 */

public class StringUtil2 {
	//static Logger log=Logger.getLogger(StringUtil.class);  //©I¥slog4j
	
	private final static String delim = ",";
	
	/**
	 * objÂà¦r¦ê¨Ã¥hªÅ¥Õ(if null return "" )
	 * 
	 * @param{0} ±ýÂà´«ªº¦r¦ê
	 * @return Âà´««áªº¦r¦ê
	 */
	public static String nullToString(Object oldStr) {
		String newStr = "";
		try {
			newStr = (oldStr == null) ? "" : oldStr.toString().trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newStr;
	}
	
	/**
	 * NT ³B²z Žõ¦rÂà´«
	 */
	public static String nullToStringForNT(Object oldStr) {
		String newStr = "";
		try {
			newStr = (oldStr == null) ? "" : oldStr.toString().trim();
			//¯S®íÃø¦r§PÂ_³B²z
			if(newStr.indexOf("Žõ") == -1){
				ConverterA cv = new ConverterA();
				String specialWord = cv.cvrtH2PC(newStr).trim();
				if(specialWord.indexOf("Žõ") != -1)
					newStr = specialWord;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return newStr;
	}
	
	/**
	 * ¥D¾÷±M¥Î¡AÃø¦rÂà´«
	 * @param oldStr
	 * @return
	 */
	public static String nullToStringForHis(Object oldStr) {
		String newStr = "";
		try {
			ConverterA cv = new ConverterA();
			newStr = (oldStr == null) ? "" : cv.cvrtH2PC(oldStr.toString()).trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return newStr;
	}
	
	/**
	 * objÂà¦r¦ê¨Ã¥hªÅ¥Õ(if null return param{1} )
	 * 
	 * @param{0} ±ýÂà´«ªº¦r¦ê
	 * @param{1} null­È±ý¦^¶Çªº¦r¦ê
	 * @return Âà´««áªº¦r¦ê
	 */
	public static String nullToString(Object oldStr, String replaceStr) {
		String newStr = "";
		try {
			newStr = oldStr == null ? replaceStr : oldStr.toString().trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newStr;
	}
	
	/**
	 * ¥b§ÎÂà¥þ§Î
	 * 
	 * @param ±ýÂà´«ªº¦r¦ê
	 * @return Âà´««áªº¦r¦ê
	 */
	public static String toWideCase(String oldStr) {
		String WideCaseStr = "";
		try {
			byte[] ch = oldStr.getBytes("UTF-16BE");
			for (int i = 0; i < ch.length; i++) {
				if ((i % 2 == 0) && (ch[i] == 0)) {
					//³B²zªÅ¥Õ
					if (ch[i + 1] == 32){
						ch[i] = 48;
						ch[i + 1] = 0;
					}else{
						ch[i] = -1;
						ch[i + 1] -= 32;
					}	
				}
			}
			WideCaseStr = new String(ch, "UTF-16BE");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return WideCaseStr;
	}

	/**
	 * ¥h±¼¥þ§Î/¥b§ÎªÅ¥Õ
	 * 
	 * @param ±ýÂà´«ªº¦r¦ê
	 * @return Âà´««áªº¦r¦ê
	 */
	public static String trimWideEmpty(String oldStr) {		
		//log.debug("¡@¡@­ì©l¸ê®Æ:"+oldStr+"<---");		
		if (oldStr == null) return new String("");

		char[] ca = oldStr.toCharArray();		
		StringBuffer sf = new StringBuffer();
		for (int i = 0; i < ca.length; i++) {
			if (!isEscapeChar(ca[i])) {
				sf.append(ca[i]);
			}
		}
		String temp2 = sf.toString();
		//log.debug("¡@¥h±¼±±¨î½X:"+temp2+"<---");		
		sf = null;
		char[] ca2 = temp2.toCharArray();
		int mark = ca2.length;
		for(int i=ca2.length-1; i > -1; i--) {
			//log.debug("ascii "+ca2[i]+"   -->"+(int)ca2[i]+"<--");
			if ((ca2[i] == 32) || (ca2[i] == 12288)) {//¥b«¬/¥þ«¬ªÅ¥Õ
				mark = i;			
			} else {					
				break;
			}
		}
		//log.debug("¥h±¼§ÀºÝªÅ¥Õ:"+temp2.substring(0, mark)+"<---");
		return temp2.substring(0, mark);		
	}
	
	
//	private static boolean isControl(char codePoint) {
//		return (codePoint >= 0x0000 && codePoint <= 0x0008)
//				|| (codePoint >= 0x000B && codePoint <= 0x000C)
//				|| (codePoint >= 0x000E && codePoint <= 0x001F);
//	}
	
	/**
	 * @param c
	 * @return
	 */
	private static boolean isEscapeChar(char c) {
		if ((c > 0) && (c < 11)) {	//ascii 1 ~ 10
			return true;
		} else if (c == 26) {		//ascii 26
			return true;
		} else{
			return false;
		}
	}

	/**
	 * ¨ú¥N¦r¦ê(ex:³æ¤Þ¸¹Âà¨â­Ó³æ¤Þ¸¹)
	 */
	public static String replace(String src, String find, String repl) {
		StringBuffer sb = new StringBuffer(src);
		int index = src.indexOf(find);
		while (index != -1) {
			sb.delete(index, index + find.length());
			sb.insert(index, repl);
			index = sb.toString().indexOf(find, index + repl.length());
		}
		return sb.toString();
	}
	
	/**
	 * ¦r¦êÂà´«
	 * 
	 * @param ­ì©l¦r¦ê
	 * @param ±ýÂà´«ªº¦r¦ê
	 * @param ´À¥Nªº¦r¦ê
	 * @return Âà´««áªº¦r¦ê
	 * @throws RESyntaxException 
	 */
	public static String replaceAll(String src, String modify, String target) {
		//RE r = new RE(modify); // Compile expression
		//return r.subst(src, target);
		return src.replaceAll(modify, target);
	}
	
	/**
	 * ¦r¦ê¤Á³Î
	 * 
	 * @param ­ì©l¦r¦ê
	 * @param ¤À¹j¦r¤¸
	 * @return ¨Ì¤À¹j¦r¤¸¤Á³Î«áªº¦r¦ê°}¦C
	 */
	public static String[] split(String line, String string) {
		StringTokenizer st = new StringTokenizer(line, string, false);
		
		String[] tmp = new String[st.countTokens() + 1];
		int i = 0;
		while (st.hasMoreTokens()) {
			tmp[i] = st.nextToken();
			i++;
		}
		return tmp;
	}
	
	
	/*
	 * ºâ¥X¹ê»Úªø«×
	 */
	public static int FullLength(String string) {
		int count = 0;
		char[] ca = string.toCharArray();
		for (int i = 0; i < ca.length; i++) {
			if (ca[i] > '\255')
				count += 2;
			else
				count += 1;
		}
		return count;
	}
	
	/**
	 *ÀË¬d¦r¦êªø«×¡A­pºâ¥D¾÷DB2¸ê®Æªø«×(§t¤¤¤å«e«á+0e¡B0f)
	 * 
	 *@param ¦r¦ê
	 *@return ¦^¶Ç¦r¦êªø«× ¨Ò:GetFullLength("1¤¤2") ¦^¶Çªø«×¬° 6
	 */
	public static int GetFullLength(String string) {
		int count = 0;
		char[] ca = string.toCharArray();
		boolean flg = false; // ¤¤¤åªºflg
		for (int i = 0; i < ca.length; i++) {
			if (ca[i] > '\255') {
				count += 2;
				if (!flg)
					count += 1;// «e¤@­Ó¦r¬O­^¤å or ¥Ø«e¬°²Ä¤@­Ó¦r¤¸¥B¬O¤¤¤å (0e)
				if (i == ca.length - 1)
					count += 1; // ³Ì«á¤@­Ó¦r¬O¤¤¤å (0f)
				flg = true; // ¤¤¤å
			} else {
				count += 1;
				if (flg)
					count += 1; // «e¤@­Ó¦r¬O¤¤¤å(0f)
				flg = false; // ­^¤å
			}
		}
		return count;
	}	
	
	/**
	 *¦r¦ê¤Á³Î­Y¦r¦ê¶W¥XMAX¡A¤Á³Î¦¨¤@­Óarray
	 *@param  string
	 *@param  max
	 *@return ­Y¦r¦ê¶W¥XMAX¡A¤Á³Î¦¨¤@­Óarray
	 */
	public static ArrayList<String> FullLength2(String string,int max) {
		int count = 0;
		boolean flg = false; // ¤¤¤åªºflg
		ArrayList<String> aryStr = new ArrayList<String>();
		String[] string_arr = string.split("\r\n") ;

		for (int j = 0 ; j < string_arr.length ; j++){
			String tmpStr = "";
			count = 0;
			char[] ca = string_arr[j].toCharArray();
			for (int i = 0; i < ca.length; i++) {
				if (ca[i] > '\255') {
					count += 2;
					if (!flg)
						count += 1;// «e¤@­Ó¦r¬O­^¤å or ¥Ø«e¬°²Ä¤@­Ó¦r¤¸¥B¬O¤¤¤å (0e)
					if (i == ca.length - 1)
						count += 1; // ³Ì«á¤@­Ó¦r¬O¤¤¤å (0f)
					flg = true; // ¤¤¤å
				} else {
					count += 1;
					if (flg)
						count += 1; // «e¤@­Ó¦r¬O¤¤¤å(0f)
					flg = false; // ­^¤å
				}

				if (count > max) { // ¦¹¦¸¦r¤¸¤£¥[
					aryStr.add(tmpStr);
					count = 0;
					tmpStr = "" + ca[i]; // ±N¦¹¦r¤¸Âk©ó¤U­Óary
				}
				tmpStr = tmpStr + ca[i];
				if (count == max) {
					aryStr.add(tmpStr);
					count = 0;
					tmpStr = "";
				}
			}
			aryStr.add(tmpStr);
		}
		return aryStr;
	}

		
	public static int getLen(String data) {
		StringTokenizer tokens = new StringTokenizer(data, delim);
		return tokens.countTokens();
	}
	
	public static String rest(String data) {
		int ptr = data.indexOf(delim);
		if (data.indexOf(delim) != -1)
			return data.substring(ptr + 1);
		else
			return "";
	}
	
	public static String clearDefaultDate(String date) {
		if (date.startsWith("9999"))
			return "";
		else
			return date;
	}
	
	/**
	 *ÀË¬d¦rªø
	 *@param ¦r¦ê¤ÎÀË¬d¦r¦êªø«×
	 *@param ¨Ò: Check_StrLen("¤¤Abc","4")
	 *@return ­Y¦r¦ê¶W¥X©Îµ¥©óÀË¬d¦r¦êªø«×,«h¦^¶Ç1,§_«h¦^¶Ç0  ¨Ò:"1"
	 */
	public static String Check_StrLen(String Get_Str, String Str_Len) {
		String errFlag = "0";
		int byteCnt = 0;
		int ilen = 0;
		try {
			// Get_Str=Get_Str.trim();
			byte[] ch = Get_Str.getBytes("Big5");
			int len = ch.length;
			//String aa = "";
			byteCnt = len;
			ilen = Integer.parseInt(Str_Len);
			if (len > 1) {
				if ((ch[0] == -95) && (ch[1] == 64)) {
					byteCnt = byteCnt - 2;
				}
				if ((ch[len - 2] == -95) && (ch[len - 1] == 64)) {
					byteCnt = byteCnt - 2;
				}
			}
			if (byteCnt >= ilen) {
				errFlag = "1";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return errFlag;
	}
	
	/**
	 * assumes UTF-8 or UTF-16 as encoding,
	 * @param content
	 * @return
	 */
	public static String escape(String content) {
		StringBuffer buffer = new StringBuffer();
		
		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);
			/*
			 if(c == '<')
			 buffer.append("&#60;");//buffer.append("&lt;");
			 else if(c == '>')
			 buffer.append("&#62;");//buffer.append("&gt;");
			 //else if(c == '&')
			  //    buffer.append("&#38;");//buffer.append("&amp;");
			   else if(c == '"')
			   buffer.append("&quot;");
			   else if(c == '\'')
			   buffer.append("&apos;");
			   */
			if (c == (char) 26)
				buffer.append(' ');
			else
				buffer.append(c);
		}//end-for(int i = 0;i < content.length();i++)
		return buffer.toString();
	}
	
	/**
	 * ¶Ç¤JString¤º®edata
	 * @param data
	 * @param findchr
	 * @param replaceStr
	 * @return
	 */
	public static String ConvertToStr(String data, char findchr,
			String replaceStr) {
		StringBuffer chgContext = new StringBuffer();
		String newdata = null;

		for (int i = 0; i < data.length(); i++) {
			if (data.charAt(i) == (char) findchr) {
				chgContext.append(replaceStr);
			} else {
				chgContext.append(data.charAt(i));
			}
		}
		newdata = chgContext.toString();
		return newdata;
	}

	/**
	 * §PÂ_¦r¦ê¬O§_¬°ªÅ
	 * @param in_parameter
	 * @return
	 */
	public static boolean isEmpty(String in_parameter) {
		return in_parameter == null || in_parameter.equals("")
				|| in_parameter.length() < 1;
	}

	/**
	 * §PÂ_ArrayList¬O§_¬°ªÅ
	 * @param in_parameter
	 * @return
	 */
	public static boolean isEmpty(ArrayList<?> in_parameter) {
		if (in_parameter == null || in_parameter.size() <= 0)
			return true;
		else
			return false;
	}
	
	/**
	 *ÀË¬d¦rªø¡A¶Ç¦^¨Ì³Ì¤jªø«×ºI¨ú«á¤§¦r¦ê
	 * 
	 * @param String ­ì¦r¦ê
	 *@param int ³Ì¤jªø«× ¨Ò: get390MaxStr("¤¤Abc",4)
	 *@return ­Y¦r¦ê¶W¥XÀË¬d¦r¦êªø«×,«h¦^¶ÇºIµu«á¤§¦r¦ê
	 */
	public static String get390MaxStr(String string, int maxLen) {
		int count = 0;
		String newStr = "";
		string = nullToString(string);
		char[] ca = string.toCharArray();
		boolean flg = false; // ¤¤¤åªºflg
		for (int i = 0; i < ca.length; i++) {
			if (ca[i] > '\255') {
				count += 2;
				if (!flg)
					count += 1;// «e¤@­Ó¦r¬O­^¤å or ¥Ø«e¬°²Ä¤@­Ó¦r¤¸¥B¬O¤¤¤å (0e)
				if (i == ca.length - 1)
					count += 1; // ³Ì«á¤@­Ó¦r¬O¤¤¤å (0f)
				flg = true; // ¤¤¤å
			} else {
				count += 1;
				if (flg)
					count += 1; // «e¤@­Ó¦r¬O¤¤¤å(0f)
				flg = false; // ­^¤å
			}
			if (count > maxLen)
				break;
			newStr = newStr + ca[i];
		}
		return newStr;
	}	

	/**
	 * ±N¤¤­^¤å¦r¦ê½Õ¾ã¦¨¬Y¤@ªø«×¡Aªø«×¤£°÷¸ÉªÅ¥Õ¡Aªø«×¤ÓªøºIÂ_ ¤¤¤å¤Î­^¤å¥þ§Î¦rºâ2¦ì¡A
	 * ­^¤å¥b§Î¦rºâ1¦ì¡A¤¤¤å³QºIÂ_¸É1¦ìªÅ¥Õ
	 * 
	 * @param sourceStr
	 *     			 ­ì¦r¦ê
	 * @param maxLen
	 *            §Æ±æ½Õ¾ãªºªø«× ¨Ò: addSpace("a¤¤bc",4) ==>return "a¤¤b"
	 *            addSpace("a¤¤bc",3) ==>return "a¤¤" addSpace("a¤¤bc",2) ==>return
	 *            "a "
	 * @return String ¦^¶Ç½Õ¾ã«á¦r¦ê
	 */
	public static String addSpace(String sourceStr, int maxLen) {
		int count = 0;
		String newStr = "";
		sourceStr = nullToString(sourceStr);
		char[] ca = sourceStr.toCharArray();
		boolean flg = false; // ¤¤¤åªºflg
		for (int i = 0; i < ca.length; i++) {
			if (ca[i] > '\255') {
				count += 2;
				flg = true; // ¤¤¤å
			} else {
				count += 1;
				flg = false; // ­^¤å
			}
			if (count > maxLen) {
				if (flg)
					count -= 2;
				break;
			}
			newStr = newStr + ca[i];
		}
		while (count < maxLen) {
			newStr = newStr + " ";
			count += 1;
		}
		return newStr;
		/*
		 * if (sourceStr.length() > maxLen) { sourceStr = sourceStr.substring(0,
		 * maxLen); } else { while (sourceStr.length() < maxLen) { sourceStr =
		 * sourceStr + " "; } } return sourceStr;
		 */
	}
	
	/**
	 * ±N¤¤­^¤å¦r¦ê½Õ¾ã¦¨¬Y¤@ªø«×¡Aªø«×¤£°÷¸ÉªÅ¥Õ¡Aªø«×¤ÓªøºIÂ_ ¤¤¤å¤Î­^¤å¥þ§Î¦rºâ2¦ì¡A
	 * ­^¤å¥b§Î¦rºâ1¦ì¡A¤¤¤å³QºIÂ_¸É1¦ìªÅ¥Õ
	 * (ªÅ¥Õ¸É¦b¥ªÃä)
	 * @param sourceStr
	 *     			 ­ì¦r¦ê
	 * @param maxLen
	 *            §Æ±æ½Õ¾ãªºªø«× ¨Ò: addSpace("a¤¤bc",4) ==>return "a¤¤b"
	 *            addSpace("a¤¤bc",3) ==>return "a¤¤" addSpace("a¤¤bc",2) ==>return
	 *            "a "
	 * @return String ¦^¶Ç½Õ¾ã«á¦r¦ê
	 */
	public static String addSpace1(String sourceStr, int maxLen) {
		int count = 0;
		String newStr = "";
		sourceStr = nullToString(sourceStr);
		char[] ca = sourceStr.toCharArray();
		boolean flg = false; // ¤¤¤åªºflg
		for (int i = 0; i < ca.length; i++) {
			if (ca[i] > '\255') {
				count += 2;
				flg = true; // ¤¤¤å
			} else {
				count += 1;
				flg = false; // ­^¤å
			}
			if (count > maxLen) {
				if (flg)
					count -= 2;
				break;
			}
			newStr = newStr + ca[i];
		}
		while (count < maxLen) {
			newStr = " " + newStr;
			count += 1;
		}
		return newStr;
		/*
		 * if (sourceStr.length() > maxLen) { sourceStr = sourceStr.substring(0,
		 * maxLen); } else { while (sourceStr.length() < maxLen) { sourceStr =
		 * sourceStr + " "; } } return sourceStr;
		 */
	}
	
	/**
	 * ±N¤¤­^¤å¦r¦ê½Õ¾ã¦¨¬Y¤@ªø«×¡Aªø«×¤£°÷¸ÉªÅ¥Õ¡Aªø«×¤ÓªøºIÂ_ ¤¤¤å¤Î­^¤å¥þ§Î¦rºâ2¦ì¡A
	 * ­^¤å¥b§Î¦rºâ1¦ì¡A¤¤¤å³QºIÂ_¸É1¦ìªÅ¥Õ(¤¤­^²V¦X¥[­p0e0f)
	 * 
	 * @param sourceStr
	 *     			 ­ì¦r¦ê
	 * @param maxLen
	 *            §Æ±æ½Õ¾ãªºªø«× ¨Ò: addSpace("a¤¤bc",4) ==>return "a¤¤b"
	 *            addSpace("a¤¤bc",3) ==>return "a¤¤" addSpace("a¤¤bc",2) ==>return
	 *            "a "
	 * @return String ¦^¶Ç½Õ¾ã«á¦r¦ê
	 */
	public static String addSpaceOeOf(String sourceStr, int maxLen) {
		int count = 0;
		String newStr = "";
		sourceStr = nullToString(sourceStr);
		char[] ca = sourceStr.toCharArray();
		boolean flg = false; // ¤¤¤åªºflg
		for (int i = 0; i < ca.length; i++) {
			if (ca[i] > '\255') {
				count += 2;
				if (!flg)
					count += 1;// «e¤@­Ó¦r¬O­^¤å or ¥Ø«e¬°²Ä¤@­Ó¦r¤¸¥B¬O¤¤¤å (0e)
				if (i == ca.length - 1)
					count += 1; // ³Ì«á¤@­Ó¦r¬O¤¤¤å (0f)
				flg = true; // ¤¤¤å
			} else {
				count += 1;
				if (flg)
					count += 1; // «e¤@­Ó¦r¬O¤¤¤å(0f)				
				flg = false; // ­^¤å
			}
			if (count > maxLen) {
				if (flg)
					count -= 2;
				else
					count -= 1;
				break;
			}
			newStr = newStr + ca[i];
		}
		while (count < maxLen) {
			newStr = newStr + " ";
			count += 1;
		}
		return newStr;
	}	

	/**
	 * ±NArrayListªº¤¸¯À²Õ¦¨¤@­Ó¦r¦ê
	 * @param datalist
	 * @param crln
	 * @return
	 */
	public static String JoinArraytoString(ArrayList<String> datalist,
			String crln) {
		String text = "";
		if(datalist.size() > 0){
			for(int i = 0; i < datalist.size(); i++){
				text += datalist.get(i);
				if(i != datalist.size()-1)
					text += crln;
			}
		}
		return text;
	}

	/**
	 * ±N¼Æ¦r«¬¦r¦ê½Õ¾ã¦¨¬Y¤@ªø«×¡Aªø«×¤£°÷«e¸É0¡Aªø«×¤Óªø¤£³B²z
	 * 
	 * @param sourceStr ¼Æ¦r«¬­ì¦r¦ê
	 * @param maxLen §Æ±æ½Õ¾ãªºªø«×
	 * @return String ¨Ò: addZero("123",5) ==>return "00123"
	 */
	public static String addZero(String sourceStr, int maxLen) {
		while (sourceStr.length() < maxLen) {
			sourceStr = "0" + sourceStr;
		}
		return sourceStr;
	}
	
	/**
	 * ±N¼Æ¦r«¬¦r¦ê½Õ¾ã¦¨¬Y¤@ªø«×¡Aªø«×¤£°÷«e¸ÉªÅ¥Õ¡Aªø«×¤Óªø¤£³B²z
	 * 
	 * @param sourceStr ¼Æ¦r«¬­ì¦r¦ê
	 * @param maxLen §Æ±æ½Õ¾ãªºªø«×
	 * @return String ¨Ò: addLeftSpace("123",5) ==>return "  123"
	 */
	public static String addLeftSpace(String sourceStr, int maxLen) {
		while (sourceStr.length() < maxLen) {
			sourceStr = " " + sourceStr;
		}
		return sourceStr;
	}
	
	/**
	 * ¦r¦ê¸É¥þ§ÎªÅ¥Õ
	 * 
	 * @param original (­ì¨Óªº¦r¦ê)
	 * @param len (·s¦r¦êªºªø«×)
	 * @param alignLeft (true:¦r¦ê¾a¥ª[¥kÃä¸ÉªÅ¥Õ], false:¦r¦ê¾a¥k[¥ªÃä¸ÉªÅ¥Õ])
	 * @return
	 */
	public static String strFillWideWhite(String original, int len, boolean alignLeft) {
		original = original.trim();
		original = original.replaceAll("\r\n", "");
		return fillChar(original, len, "¡@", alignLeft);
	}
	
	/**
	 * ¦r¦ê¸É¥b§Î¦r¤¸
	 * 
	 * @param original (­ì¨Óªº¦r¦ê)
	 * @param len (·s¦r¦êªºªø«×)
	 * @param fChar (­n¸Éªº¦r¤¸)
	 * @param alignLeft (true:¦r¦ê¾a¥ª[¥kÃä¸ÉªÅ¥Õ], false:¦r¦ê¾a¥k[¥ªÃä¸ÉªÅ¥Õ])
	 * @return
	 */
	public static String fillChar(String original, int len, String fChar, boolean alignLeft) {
		String rtn = original;
		try {
			int diff = len - original.length();
			if (diff > 0) {
				String supplement = "";
				for (int i=0; i<diff; i++) {
					supplement += fChar;
				}
				if (alignLeft) {	// ¦r¦ê¾a¥ª¡]¥kÃä¸ÉªÅ¥Õ¡^
					rtn = original + supplement;
				} else {	// ¦r¦ê¾a¥k¡]¥ªÃä¸ÉªÅ¥Õ¡^
					rtn = supplement + original;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtn;
	}

	/**
	 * addRightSpace ¨Ì¶Ç¤J¤¤­^¤å¦r¦êªºªø«×¡A¥kÃä¦A¸É¨¬¥þ§ÎªÅ¥Õ
	 * @param src	(­ì¨Óªº¦r¦ê)
	 * @param leng	(·s¦r¦êªºªø«×)
	 * @return
	 */
	public static String addRightFullSpace( String src, int leng) {
		StringBuffer result = new StringBuffer(src);
		int strLen=leng-src.getBytes().length;
		//§PÂ_°¸¼ÆÁÙ©_¼Æ
		if (strLen % 2==1) {
			for(int i = src.getBytes().length ; i < leng ; i++ ) {
				result.append(" ");  //©_¼Æ¤@¼Ë¥[¤@¯ëªºªÅ¥Õ
				}
		} else {
			for(int i = 0 ; i < (strLen/2) ; i++ ) {
				result.append("¡@"); //°¸¼Æ«h¥[¥þ§ÎªÅ¥Õ
				}
		}
		return result.toString();
	}
	
	/**
	 * ³B²zHTML¯S®í¦r¤¸¡A¥Î@¥N´À&
	 * @param str
	 * @return
	 */
	public static String translateHTMLCode(String str){
		
		Pattern pattern = Pattern.compile("&#[0-9]{1,5};");
		Matcher matcher = pattern.matcher(str);
		
		String code = "";
		while(matcher.find()) {
			if(code.indexOf(matcher.group()) == -1)
				code += "," + matcher.group();
		}
		if(!code.equals("")){
			String[] codes = code.substring(1).split(",");
			for(int i = 0; i < codes.length; i++){
				str = str.replaceAll(codes[i], "@#"+codes[i].substring(2));
			}
		}
		return str;
	}
	
	/**
	 * Âà¸mHTML¯S®í¦r¤¸¡A¥Î&´À´«@
	 * @param str
	 * @return
	 */
	public static String reverseHTMLCode(String str){
		
		Pattern pattern = Pattern.compile("@#[0-9]{1,5};");
		Matcher matcher = pattern.matcher(str);
		
		String code = "";
		while(matcher.find()) {
			if(code.indexOf(matcher.group()) == -1)
				code += "," + matcher.group();
		}
		if(!code.equals("")){
			String[] codes = code.substring(1).split(",");
			for(int i = 0; i < codes.length; i++){
				str = str.replaceAll(codes[i], "&#"+codes[i].substring(2));
			}
		}
		return str;
	}
	
	/**
	 * ¼Æ¦r¸É¹s
	 * 
	 * @param original (­ì¨Óªº¼Æ¦r)
	 * @param len (·s¦r¦êªºªø«×)
	 * @return
	 */
	public static String intFillZero(String original, int len) {
		return fillChar(original, len, "0", false);
	}	
	
	/**
	 * ¼Æ¦r¸É¹s
	 * 
	 * @param original (­ì¨Óªº¼Æ¦r)
	 * @param len (·s¦r¦êªºªø«×)
	 * @return
	 */
	public static String intFillZero(int original, int len) {
		return intFillZero(original+"", len);
	}
	
	/*
	 * ºâ¥X¦r¦ê¯S©w¦ì¸m¤§«e¦³¦h¤Ö­Ó¥þ§Î¦r
	 */
	public static int countDoubleBytes(String string, int position) {
		int count = 0;
		if (position > string.length()) position = string.length();
		if (position == 0) position = 1 ;  
		char[] ca = string.substring(0, position-1).toCharArray();
		for (int i = 0; i < ca.length; i++) {
			
			if (ca[i] > '\255') count += 1;
		}
		return count;
	}
	
	/**
	 * ¦r¦ê¸m´« -- ¦Ò¼{¥þ§Î¦r¦³ 2 ­ÓBYTES ª¬ªp (¹w³]¬°true) 
	 * 
	 * @param oldString (­ì¨Óªº¦r¦ê)
	 * @param replacement (±ý¸m´«ªº¦r¦ê)
	 * @param start (­ì¦r¦êªº¶}©l¦ì¸m)
	 * @param length (¸m´«¦r¦êªºªø«×)
	 * @param doublefg true/false (¥þ§Î¦r¬O§_­pºâ¬°2­Ó)
	 * @return
	 */
	public static String replaceSubstring(String oldString, String replacement, int start, int length) {
		return replaceSubstring(oldString, replacement, start, length, true);
	}
	
	/**
	 * ¦r¦ê¸m´« -- ¦Ò¼{¥þ§Î¦r¦³ 2 ­ÓBYTES ª¬ªp 
	 * 
	 * @param oldString (­ì¨Óªº¦r¦ê)
	 * @param replacement (±ý¸m´«ªº¦r¦ê)
	 * @param start (­ì¦r¦êªº¶}©l¦ì¸m)
	 * @param length (¸m´«¦r¦êªºªø«×)
	 * @param doublefg true/false (¥þ§Î¦r¬O§_­pºâ¬°2­Ó)
	 * @return
	 */
	public static String replaceSubstring(String oldString, String replacement, int start, int length ,boolean doublefg) {
		int cntDouble = countDoubleBytes(oldString, start);
		
		if (doublefg)
			start -= cntDouble;
		
		return  oldString.substring(0, start) + 
				 StringUtil2.addSpace(replacement, length) + 
				 oldString.substring(start+length);
	}
	
	/**
	 * ¦r¦ê¸É¥b§ÎªÅ¥Õ
	 * 
	 * @param original (­ì¨Óªº¦r¦ê)
	 * @param len (·s¦r¦êªºªø«×)
	 * @param alignLeft (true:¦r¦ê¾a¥ª[¥kÃä¸ÉªÅ¥Õ], false:¦r¦ê¾a¥k[¥ªÃä¸ÉªÅ¥Õ])
	 * @return
	 */
	public static String strFillWhite(String original, int len, boolean alignLeft) {
		return fillChar(original, len, " ", alignLeft);
	}
	
	/**
	 * ¦r¦ê¤¤¯S©w¦r¦ê¥X²{¦¸¼Æ
	 * @param original (­ì¨Óªº¦r¦ê)
	 * @param word     (´M§äªº¦r¦ê)
	 * @return
	 */
	public static int findwordtimes(String original, String word) {
		
		String replstr = original.replace(word, "");
		if (original.length() == replstr.length())
			return 0 ;
		else
			return (original.length()-replstr.length())/word.length();
		
	}
	/**
	 * ¯S®í¦r¤¸Âà´«(">"==>"&gt;")
	 * @param word
	 * @return
	 */
	public static String handleSpecialCharacter(String word){
		word = nullToString( word ); 
		word = word.replaceAll("&", "&amp;");
		word = word.replaceAll(">", "&gt;");
		word = word.replaceAll("<", "&lt;");
		word = word.replaceAll("\"", "&quot;");
		word = word.replaceAll("'", "&apos;");
		return word;
	}
	/**
	 *  ¯S®í¦r¤¸Âà´«("&gt;"==> ">")
	 * @param word
	 * @return
	 */
	public static String handleSpecialCharacterReverse(String word){
		word = nullToString( word );
		word = word.replaceAll("&amp;", "&");
		word = word.replaceAll("&gt;", ">");
		word = word.replaceAll("&lt;", "<");
		word = word.replaceAll("&quot;", "\"");
		word = word.replaceAll("&apos;", "'");
		return word;
	}
	
	/** §PÂ_¦r¦ê¬O§_¬°¼Æ­È
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		Pattern pattern = Pattern.compile("^-?[0-9]+(\\.[0-9]+)?$");
		Matcher matcher = pattern.matcher(str);
		
		if (matcher.find())
			return true ;
		else
			return false ;
	}	
	
	/**
	 * ±N utf-16 ¹ïÀ³¤£¨ì MS950 ªº¯S®í²Å¸¹¥þ§Î¦rÂà¦¨¥i¹ïÀ³ MS950 Åã¥Üªº utf-16 ¦r¤¸
	 * 
	 * @param str ­ì¦r¦ê
	 * @return String Âà´««á¦r¦ê
	 */
	public static String replaceWideSign(String str) {
		 String retStr = str.replaceAll("\u223C", "¡ã")
		 					.replaceAll("\u273D", "¡¯")
		 					.replaceAll("\uFF02", "¡¨")
		 					.replaceAll("\uFF07", "¡¦")
		 					.replaceAll("\u5F5E", "ÂU")
		 					.replaceAll("\uFFE4", "¡U");
		 /* 
         for (int i = 0; i < retStr.length(); i++){
	          System.out.println(i + ":" + retStr.charAt(i)+ Integer.parseInt(Integer.toHexString(retStr.charAt(i)),16));
         }
		 */ 
		 return retStr;
	 }
	/**
	 * <font color="blue">¨ú±o¯f±w¥[±K«áªº¦í§}</font>
	 * 
	 * @param patAdr:¯f±w¦í§}
	 * @return String:¥[±K«áªº¦í§}
	 */
	public static String encryptAddr(String patAdr){
		String retAdr = "";
		try{
			if (patAdr.length()>6){
				retAdr = patAdr.substring(0,6) + fillChar("", patAdr.length()-6, "¡¸", true);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return retAdr;
	}
	
	/**
	 * <font color="blue">¨ú±o¯f±w¥[±K«áªº¹q¸Ü¸¹½X</font>
	 * 
	 * @param patAdr:¯f±w¹q¸Ü¸¹½X
	 * @return String:¥[±K«áªº¹q¸Ü¸¹½X
	 */
	public static String encryptPhoneNumber(String phoneNumber){
		String retPNum = "";
		int lenPNum = phoneNumber.length();
		try{
			if (lenPNum > 5){
				retPNum = phoneNumber.substring(0, lenPNum - 5) + "****" + phoneNumber.substring(lenPNum - 1);
				//retPNum = phoneNumber.substring(0,4) + fillChar("", phoneNumber.length()-4, "*", true);
			} else {
				//¤À¾÷«O¯d¡A©Î¹q¸Ü¸¹½X¤£¨¬¿ù»~¥þ³¡Åã¥Ü
				retPNum = phoneNumber;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return retPNum;
	}
	
	/**
	 * ¤À³Î¦r¦ê¦¨¦r¦ê¦ê¦C
	 * ¨Ì´«¦æ²Å¸¹¤Á³Î¦¨¦r¦ê¡A­Y¸Ó¦æ¤j©ó«ü©w¦r¤¸¼Æ«h§é¦¨¦h¦æ
	 * ¤¤­^¤å¥i¥H²VÂøª½±µ¦^¦s¥D¾÷©T©wªøÄæ¦ì
	 * @param input		±ý¤À³Î¦r¦ê
	 * @param max_char_number	¨C¦æ³Ì¤j¦r¤¸¼Æ
	 * @return ArrayList<String> ¦r¦ê¦ê¦C
	 */
	public static ArrayList<String> SplitStringToArray2(String input, int max_char_number) {
		ArrayList<String> returnData = new ArrayList<String>(); // ¶Ç¦^¦r¦ê°}¦C
		String split_char = "\n";
		String split_char2 = "\r\n";
		String newLine=System.getProperty("line.separator");
		String instr = (input != null) ? input.replaceAll(split_char2, split_char) : "";
		instr = instr.replaceAll(split_char, newLine);
		String[] arySplitData = instr.split(newLine);
		for (int index = 0; index < arySplitData.length; index++) { // ¨Ì´«¦æ²Å¸¹¤Á³Î¦Ó¦¨ªº¦r¦ê°}¦C°j°é
			if (arySplitData[index] != null) {
				String thisStr = arySplitData[index]; // ¦¹¦¸°j°é¨ú±o¤§¦r¦ê
				//if (GetFullLength(thisStr) > max_char_number) { // ¸Óµ§¦r¦êªø«× > ³Ì¤jªø«×
				ArrayList<String> thisData = StringUtil2.splitWord(thisStr, max_char_number); // ¤Á³Î¦r¦ê
				for (int i = 0; i < thisData.size(); i++) {
					returnData.add(thisData.get(i).toString()); // ±N¦r¦ê©ñ¤JreturnData¤¤
				}
				//} else { // ¸Óµ§¦r¦êªø«× <= ³Ì¤jªø«×
				//	returnData.add(thisStr); // ±N²Ö¿n¦Ü¥»¦¸°j°éªº¦r¦ê©ñ¤JreturnData¤¤
				//}
			}
		}
		return returnData;
	}
	
	/**
	 * ¤Á³Î¦r¦ê(­^¤å¬°³æ¦r¤À³Î¡A¤¤¤å³æ¦r¤À³Î¥B¹w¯d0E0FªÅ¶¡)
	 * 
	 * @param string	±ý¤À³Î¦r¦ê
	 * @param maxLen	³Ì¤jªø«×
	 * @return ArrayList<String> ¤Á³Î§¹¦¨¤§¦r¦ê
	 */
	public static ArrayList<String> splitWord(String string, int maxLen) {
		int count = 0;
		int oldcnt = 0;
		char[] ca = string.toCharArray();
		boolean flg = false; // ¤¤¤åªºflg
		ArrayList<String> aryStr = new ArrayList<String>();
		String tmpStr = "";
		char nextchar = ' ';
		String wordStr = "";
		String leftStr = "";
		for (int i = 0; i < ca.length; i++) {
			//§P§O¤¤­^¤åÀ³¥[ªø«×
			if (ca[i] > '\255') {
				count += 2;
				if (!flg)
					count += 2;// «e¤@­Ó¦r¬O­^¤å or ¥Ø«e¬°²Ä¤@­Ó¦r¤¸¥B¬O¤¤¤å (0e0f)
				flg = true; // ¤¤¤å
			} else {
				count += 1;
				flg = false; // ­^¤å
			}
			
			//³æ¦rªº²Õ¦X¥»¦r¤¸
			wordStr = wordStr + ca[i];

			//¥[¥»¦r¤¸¶W¥X³Ì¤jªø«×°e¥XÂÂ¦r¦ê
			if (count > maxLen) {
				if (oldcnt > 0) {
					while (oldcnt > maxLen) {
						leftStr = tmpStr.substring(0, maxLen);
						tmpStr = tmpStr.substring(maxLen);
						oldcnt = oldcnt - maxLen;
						aryStr.add(leftStr);
					}
					if (oldcnt < maxLen) tmpStr = tmpStr + StringUtil2.addSpace(" ", maxLen - oldcnt);	//¥[¸ÉªÅ¥Õ
					aryStr.add(tmpStr);
					count = (flg) ? 4 : wordStr.length();	//­«·s°_ºâ¤¤¤å§t0e0f¦@4¦ì¡A­^¤å¬°³æ¦rªø«×
					tmpStr = "";
					oldcnt = 0;
				}
			}
			
			//¥»¦r¤¸¤¤¤å©ÎªÅ¥Õ©Î.©Î¤U¤@¦r¤¸¤¤¤å«h±N³æ¦r²Õ¦X
			nextchar = (i < (ca.length -1)) ? ca[i+1] : ' ';
			if (flg || (ca[i] == ' ') || (ca[i] == '.') || (nextchar > '\255')) {
				tmpStr = tmpStr + wordStr;
				oldcnt = count;
				wordStr = "";
			}
		}
		//²Õ¦X³Ì«á³æ¦r¤Î°e¥X¦r¦ê
		if (wordStr.length() > 0) {
			tmpStr = tmpStr + wordStr;
			oldcnt = count;
		}
		
		if (tmpStr.length() > 0) {
			while (oldcnt > maxLen) {
				leftStr = tmpStr.substring(0, maxLen);
				tmpStr = tmpStr.substring(maxLen);
				oldcnt = oldcnt - maxLen;
				aryStr.add(leftStr);
			}
			if (oldcnt < maxLen) tmpStr = tmpStr + StringUtil2.addSpace(" ", maxLen - oldcnt);	//¥[¸ÉªÅ¥Õ
			aryStr.add(tmpStr);
		}
		return aryStr;
	}
	
	/**
	 * ¥h°£¥ªÃäªÅ¥Õ
	 * @param s
	 * @return
	 */	
	public static String ltrim(String s) {
		int i = 0;
		while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
			i++;
		}
		return s.substring(i);
	}
	
	/**
	 * ¥h°£¥kÃäªÅ¥Õ
	 * @param s
	 * @return
	 */	
	public static String rtrim(String s) {
		int i = s.length() - 1;
		while (i > 0 && Character.isWhitespace(s.charAt(i))) {
			i--;
		}
		return s.substring(0, i + 1);
	}
	
	public static String encodeToHtmlEntity(String input) {
	    if (input == null) return "";
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < input.length(); i++) {
	        char c = input.charAt(i);
	        // ¦pªG¬O¼Ð·Ç ASCII ½d³ò¤ºªº¦r¤¸¡Aª½±µ«O¯d¡F¦pªG¬O¤¤¤å¦r¡A«hÂà¬° &#xxxxx;
	        if (c > 127) {
	            sb.append("&#").append((int) c).append(";");
	        } else {
	            sb.append(c);
	        }
	    }
	    return sb.toString();
	}
	
	public static String decodeHtmlEntity(String input) {
	    if (input == null) return "";
	    StringBuilder sb = new StringBuilder();
	    int i = 0;
	    while (i < input.length()) {
	        // °»´ú¨ì¹êÅé½X¶}ÀY
	        if (input.startsWith("&#", i)) {
	            int end = input.indexOf(';', i);
	            if (end != -1) {
	                try{
	                    // Â^¨ú¼Æ¦r³¡¤À¨ÃÂà¬°¦r¤¸ (¤Q¶i¦ì)
	                    String numStr = input.substring(i + 2, end);
	                    int codePoint = Integer.parseInt(numStr);
	                    sb.append((char) codePoint);
	                    i = end + 1; // ¸õ¹L³o¬q¹êÅé½X
	                    continue;
	                } catch (NumberFormatException e) {
	                    // µo¥Í¨Ò¥~«h©¿²¤¡A¨Ì·Ó­ì¥»¦r¤¸¿é¥X
	                }
	            }
	        }
	        sb.append(input.charAt(i));
	        i++;
	    }
	    return sb.toString();
	}
	
	public static String decodeTxtEntity(String input) {
		// 1. ¨ú±o Big5 ½s½X¾¹
	    Charset big5 = Charset.forName("Big5");
	    CharsetEncoder encoder = big5.newEncoder();
	
	    // 2. ÀË¬d¤å¦r¡A±NÃø¦r´À´«±¼
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < input.length(); ) {
            // ¨ú±o Unicode ½XÂI¡]¤ä´©¨u¨£ªº¥|¦ì¤¸²Õ¦r¤¸¡^
            int codePoint = input.codePointAt(i);
            String singleChar = new String(Character.toChars(codePoint));

            // ÀË¬d³o­Ó¦r Big5 ¯à¤£¯à¬ÝÀ´
            if (encoder.canEncode(singleChar)) {
            	sb.append(singleChar); // ¬Ý±oÀ´¡A«O¯d­ì¦r
            } else {
            	sb.append("¡¼");       // ¬Ý¤£À´¡]Ãø¦r¡^¡A´À´«¦¨¥þ§Î°Ý¸¹¡]¥i¦Û¦æ§ï¦¨ "_" ©Î¨ä¥¦¦r¡^
            }
            
            // ²¾°Ê¨ì¤U¤@­Ó¦r
            i += Character.charCount(codePoint);
        }
	    
	    return sb.toString();
	}
	
	
	
}