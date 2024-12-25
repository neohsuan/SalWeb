/*
 * ¦b 2006/2/13 «Ø¥ß
 */
package gov.vghks.sal.util;

import java.util.HashMap;

/**
 * @author t010
 *
 */
public class GlobalProp {
	static HashMap props = new HashMap();
	
	public static void setProp(String key, String value) {
		props.put(key, value);		
	}
	
	public static String getProp(String key) {
		if(props.get(key)!= null)   return props.get(key).toString();
		else return null;
		
	}
}
