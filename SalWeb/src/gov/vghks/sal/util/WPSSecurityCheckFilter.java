package gov.vghks.sal.util;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class WPSSecurityCheckFilter
 */
public class WPSSecurityCheckFilter implements Filter {

	private FilterConfig filterConfig;
	private String loginURL;
	
    /**
     * Default constructor. 
     */
    public WPSSecurityCheckFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession(false);
		
		if(session==null)
		{
			System.out.println("WPSSecurityCheckFilter ,session is null,RequestURI:"+req.getRequestURI());
			res.sendRedirect(loginURL);
		}
		else
		{
			if("Y".equals(session.getAttribute("opplServlet"))){
				//do nothing
			}else if(session.getAttribute("ssID")==null)
			{
				System.out.println("WPSSecurityCheckFilter ,Session('ssID') is null,RequestURI:"+req.getRequestURI());
				res.sendRedirect(loginURL);
			}
			else
			{
				//do nothing
			}
		}
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
		this.filterConfig = fConfig;		
		this.loginURL = filterConfig.getInitParameter("loginURL");
	}

}
