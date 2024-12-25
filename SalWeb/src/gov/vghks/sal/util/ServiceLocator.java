package gov.vghks.sal.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * @author Mark Su
 * 
 * 
 */
public class ServiceLocator {

	private static ServiceLocator instance = null;

	private Context ctx = null;

	static {
		try {
			instance = new ServiceLocator();
		} catch (ServiceLocatorException e) {
			e.printStackTrace();
		}
	}

	private ServiceLocator() throws ServiceLocatorException {
		try {
			ctx = new InitialContext();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public static ServiceLocator getInstance() {
		return instance;
	}

	public DataSource getDataSource(String jndiName)
			throws ServiceLocatorException {

		DataSource ds = null;

		try {
			ds = (DataSource) ctx.lookup("java:comp/env/" + jndiName);
		} catch (NamingException e) {

			e.printStackTrace();
			throw new ServiceLocatorException(e);
		} catch (Exception e) {

			e.printStackTrace();
			throw new ServiceLocatorException(e);
		}

		return ds;
	}
}
