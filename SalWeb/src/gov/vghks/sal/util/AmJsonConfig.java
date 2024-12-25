package gov.vghks.sal.util;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonBeanProcessor;

/**
 * Json不支援sql.Dste, sql.Time的轉型，故將原有類別進行改寫
 * @author 6455
 *
 */
public class AmJsonConfig extends JsonConfig {
	
	/**
	 * 建構子
	 */
	public AmJsonConfig() {
		// set sql.Date, sql.Time. sql.Timestamp
		setDts();

	}

	/**
	 * 進行資料庫時間型態的轉型
	 */
	private void setDts() {
		this.registerJsonBeanProcessor(java.sql.Date.class,
				new JsonBeanProcessor() {
					@Override
					public JSONObject processBean(Object arg0, JsonConfig arg1) {
						JSONObject date = new JSONObject();
						date.element("dts", arg0!=null?arg0.toString():"");
						return date;
					}
				});
		this.registerJsonBeanProcessor(java.sql.Time.class,
				new JsonBeanProcessor() {
					@Override
					public JSONObject processBean(Object arg0, JsonConfig arg1) {
						JSONObject time = new JSONObject();
						time.element("dts", arg0!=null?arg0.toString():"");
						return time;
					}
				});
		this.registerJsonBeanProcessor(java.sql.Timestamp.class,
				new JsonBeanProcessor() {
					@Override
					public JSONObject processBean(Object arg0, JsonConfig arg1) {
						JSONObject time = new JSONObject();
						time.element("dts", arg0!=null?arg0.toString():"");
						return time;
					}
				});
	}
}
