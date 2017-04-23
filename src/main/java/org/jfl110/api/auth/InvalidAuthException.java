package org.jfl110.api.auth;

import org.jfl110.api.jsonexception.ErrorDescriptionJsonException;

/**
 * Exception thrown when an error occured during the authentication process.
 *
 * @author JFL110
 */
@SuppressWarnings("serial")
public class InvalidAuthException extends ErrorDescriptionJsonException{
	
	public InvalidAuthException(String errorCode){
		super(errorCode,"");
	}
	
	
	public InvalidAuthException(String errorCode,String description){
		super(errorCode,description);
	}
}