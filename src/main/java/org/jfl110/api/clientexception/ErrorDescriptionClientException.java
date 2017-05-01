package org.jfl110.api.clientexception;

/**
 * A ClientException which is defined by a code and description.
 *
 * @author JFL110
 */
@SuppressWarnings("serial")
public class ErrorDescriptionClientException extends ClientException {

	public static final ClientExceptionLogLevel DEFAULT_LOG_LEVEL = ClientExceptionLogLevel.WARN;

	private final String errorCode;
	private final String errorDescription;
	private final ClientExceptionLogLevel logLevel;

	public ErrorDescriptionClientException(String errorCode,String errorDescription) {
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
		this.logLevel = DEFAULT_LOG_LEVEL;
	}
	
	public ErrorDescriptionClientException(String errorCode,String errorDescription,ClientExceptionLogLevel logLevel) {
		this.errorCode = errorCode;
		this.logLevel = logLevel;
		this.errorDescription = errorDescription;
	}

	@Override
	public ClientExceptionLogLevel getLogLevel() {
		return logLevel;
	}
	
	
	@Override
	public String description(){
		return errorDescription;
	}
	
	
	@Override
	public String errorCode() {
		return errorCode;
	}
}
