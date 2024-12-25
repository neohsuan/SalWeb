package gov.vghks.sal.util;

import org.apache.log4j.*;
/**
 * @author 3360
 *
 */

public class LogUtil {
	public static String debug="debug";
	public static String info="info";
	public static String warn="warn";
	public static String error="error";
	public static String fatal="fatal";
	
	
	/**
	 * Method log. 將msg紀錄在log中
	 * @param className
	 * @param level
	 * @param msg
	 */
	public static void log(String className,String level,Object msg) {
	    Logger out   = Logger.getLogger(className);			
        if (level.equals("debug") & out.isDebugEnabled()) out.debug(msg);
        if (level.equals("info") & out.isInfoEnabled()) out.info(msg);
        if (level.equals("warn")) out.warn(msg);
        if (level.equals("error")) out.error(msg);
        if (level.equals("fatal")) out.fatal(msg);
	}

	/**
	 * Method log. 將msg 及 throwable 物件紀錄在log中
	 * @param className
	 * @param level
	 * @param Object msg
	 * @param Throwable e
	 */
	public static void log(String className,String level,Object msg,Throwable e) {
	    Logger out   = Logger.getLogger(className);			
        if (level.equals("debug") & out.isDebugEnabled()) out.debug(msg,e);
        if (level.equals("info") & out.isInfoEnabled()) out.info(msg,e);
        if (level.equals("warn")) out.warn(msg,e);
        if (level.equals("error")) out.error(msg,e);
        if (level.equals("fatal")) out.fatal(msg,e);
	}
}
