package org.jfl110.api.clientexception;

/**
 * An exception which can be serialised into Json and 
 * written to the response.
 *
 * @author JFL110
 */
@SuppressWarnings("serial")
public abstract class ClientException extends RuntimeException{

	protected ClientException() {}
	
	protected ClientException(Throwable e){
		super(e);
	}
	
	public abstract ClientExceptionLogLevel getLogLevel();
	
	public abstract String errorCode();
	
	public abstract String description();
}