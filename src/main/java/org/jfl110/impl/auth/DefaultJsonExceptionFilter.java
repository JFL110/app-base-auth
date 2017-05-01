package org.jfl110.impl.auth;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jfl110.api.jsonexception.JsonException;
import org.jfl110.api.jsonexception.JsonExceptionFilter;
import org.jfl110.api.jsonexception.JsonExceptionLogLevel;


/**
 * Filter to catch JsonException s and print the Json.
 * To be put after ExceptionFilter.
 * 
 * @author JFL110
 */
@Singleton
public class DefaultJsonExceptionFilter implements JsonExceptionFilter{

	private static Logger logger = Logger.getLogger(DefaultJsonExceptionFilter.class.getSimpleName());

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		try {
			chain.doFilter(request, response);
		} catch (JsonException jsonException) {
			if(jsonException.getLogLevel() == JsonExceptionLogLevel.INFO){
				logger.warning(jsonException.toJson());
			}else if(jsonException.getLogLevel() == JsonExceptionLogLevel.WARN){
				logger.severe(jsonException.toJson());
			}else if(jsonException.getLogLevel() != JsonExceptionLogLevel.NOLOG){
				logger.severe(jsonException.toJson());
			}
			
			try {
				response.setContentType("application/json");
			} catch (Exception e) {
				logger.warning("Could not set response type " + e);
			}
			
			response.getWriter().println(jsonException.toJson());
		}

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void destroy() {}
}