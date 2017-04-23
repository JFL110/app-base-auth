package org.jfl110.api.auth;

import javax.servlet.ServletRequest;

/**
 * Manages the getting/setting of AuthenticationAttempts in the ServletRequest.
 * This enables filters to process the authentication and make the results available to 
 * other filters/servlets, without needing a common attribute key.
 *
 * @author JFL110
 */
public interface AuthenticationAttemptRequestManager {

	/**
	 * Puts the AuthenticationAttempt into the ServletRequest
	 */
	void insertIntoRequest(ServletRequest request,AuthenticationAttempt authAttempt);
	
	
	/**
	 * Gets the AuthenticationAttempt from the ServletRequest
	 */
	AuthenticationAttempt retrieveFromRequest(ServletRequest request);

}