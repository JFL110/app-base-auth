package org.jfl110.api.auth;

/**
 * Details of a token which may or may not be valid
 * and the hash to verify it.
 *
 * @author JFL110
 */
public interface HashedAuthToken extends AuthTokenClaim{

	/**
	 * The hash for the AuthTokenClaim
	 */
	String hash();
}
