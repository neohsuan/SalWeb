package gov.vghks.sal.util;





import java.io.File;
import java.util.Properties;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
   
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * @version 	1.0
 * @author
 */
public class Log4JInit extends HttpServlet implements Servlet {
   
	private static final long serialVersionUID = 6054401710497075439L;
	static Logger log = Logger.getLogger(Log4JInit.class);  //呼叫log4j	
	private static Properties properties;
	String prefix,file;
  
	public void init() throws ServletException {
		super.init();
		
		prefix = getServletContext().getRealPath("/");
		file = getServletConfig().getInitParameter("log4j-config-file");	
		System.out.println("OPTMWeb Started!!   log4j properties path ="+prefix+file);
		if (file != null) {
			PropertyConfigurator.configure(prefix + File.separator+ file);
			log.info("Server start...");			
		} else {
			System.out.println(prefix + file+"讀取不到log4j設定檔");
		log.info(prefix + file+"讀取不到log4j設定檔") ;
		}
		
		//Log4jInit();
		setCommonVariable();	

		//delete PDF/XML
		clearTemplateDir(GlobalProp.getProp("root")+"\\PDF");
		/*(GlobalProp.getProp("root")+"\\UDTRXML");
		clearTemplateDir(GlobalProp.getProp("root")+"\\UDTRXML\\log");*/
	}
	

	public void Log4jInit() {
		prefix = getServletContext().getRealPath("/");
		file = getServletConfig().getInitParameter("log4j-config-file");	

		if (file != null) {
			PropertyConfigurator.configure(prefix + File.separator+ file);
			log.info("Server start...");			
		} else {
		log.info(prefix + file+"讀取不到log4j設定檔") ;
		}
	}
	
	public void setCommonVariable() {
		GlobalProp.setProp("root",prefix);
	}
	
	private int clearTemplateDir(String deltarget) {
		log.debug("del tree " + deltarget);
		try{
			File dir = new File(deltarget);		
			File[] subdir = dir.listFiles();		
			if (dir.isDirectory()) {		
				for (int i = 0; i < subdir.length; i++) {			
					//if(!(subdir[i].getName().equals("EmrHttpClient.exe") || 
					//	subdir[i].getName().equals("Newtonsoft.Json.dll"))  ){
						subdir[i].delete();
					//}				
				}
			}
		}catch(Exception e ){
			log.debug("===============>>del file error =="+e.getMessage());
		}
		return 0;		
	}
	
	
}