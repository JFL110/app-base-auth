package org.jfl110.api.auth;

import org.jfl110.api.clientexception.ErrorDescriptionClientException;

/**
 * Exception thrown when an error occured during the authentication process.
 *
 * @author JFL110
 */
@SuppressWarnings("serial")
public class InvalidAuthException extends ErrorDescriptionClientException{
	
	public InvalidAuthException(AuthTokenStatus authTokenStatus){
		super(authTokenStatus.toErrorCode(),"");
	}
	
	
	public InvalidAuthException(String errorCode){
		super(errorCode,"");
	}
	
	
	public InvalidAuthException(String errorCode,String description){
		super(errorCode,description);
	}
}