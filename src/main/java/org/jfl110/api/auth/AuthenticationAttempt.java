package org.jfl110.api.auth;

import com.google.common.base.Optional;

/**
 * The result of an authentication on an AuthToken, if there was one.
 *
 * @author JFL110
 */
public interface AuthenticationAttempt {

	/**
	 * The AuthToken, if one was present
	 */
	Optional<AuthTokenClaim> authToken();
	
	/**
	 * The status of the token, indicating its validity.
	 */
	AuthTokenStatus status();
}