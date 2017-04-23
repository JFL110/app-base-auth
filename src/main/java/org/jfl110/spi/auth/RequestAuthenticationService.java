package org.jfl110.spi.auth;

import javax.servlet.ServletRequest;

import org.jfl110.api.auth.AuthenticationAttempt;

/**
 * Authenticates a request. 
 *
 * Applications using this module will need to provide an implementation for this service.
 * 
 * @author JFL110
 */
public interface RequestAuthenticationService {
	
	/**
	 * Authenticates a request. The authentication may or may not have
	 * been successful, an exception should not be thrown by an unsuccessful
	 * authentication.
	 */
	AuthenticationAttempt authenticateRequest(ServletRequest request);
}