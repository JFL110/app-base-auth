package org.jfl110.api.auth;

public enum AuthTokenStatus{
	VALID,
	OTHER_NOT_VALID,
	EXPIRED,
	NOT_FOUND;
	
	
	public String toErrorCode(){
		return "TOKEN_"+toString();
	}
}