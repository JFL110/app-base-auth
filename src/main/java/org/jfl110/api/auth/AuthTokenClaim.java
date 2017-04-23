package org.jfl110.api.auth;

import java.io.Serializable;
import java.util.List;

import org.joda.time.LocalDateTime;

/**
 * Details of the claims made by a token.
 *
 * @author JFL110
 */
public interface AuthTokenClaim extends Serializable{

	
	String userIdentifier();
	
	
	String nonce();
	
	
	LocalDateTime issuedTime();
	
	
	List<String> claims();
	
}