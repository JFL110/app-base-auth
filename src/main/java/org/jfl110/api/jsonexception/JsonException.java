package org.jfl110.api.jsonexception;

/**
 * An exception which can be serialised into Json and 
 * written to the response.
 *
 * @author JFL110
 */
@SuppressWarnings("serial")
public abstract class JsonException extends RuntimeException{

	protected JsonException() {}
	
	protected JsonException(Throwable e){
		super(e);
	}
	
	public abstract String toJson();
	
	public abstract JsonExceptionLogLevel getLogLevel();
}