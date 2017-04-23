package org.jfl110.api.jsonexception;

/**
 * A JsonException which is defined by a code and description.
 *
 * @author JFL110
 */
@SuppressWarnings("serial")
public class ErrorDescriptionJsonException extends JsonException {

	public static final JsonExceptionLogLevel DEFAULT_LOG_LEVEL = JsonExceptionLogLevel.WARN;
	public static final String ERROR_CODE_FIELD_NAME = "error";
	public static final String ERROR_DESCRIPTION_FIELD_NAME = "description";

	private final String errorCode;
	private final String errorDescription;
	private final JsonExceptionLogLevel logLevel;

	public ErrorDescriptionJsonException(String errorCode,String errorDescription) {
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
		this.logLevel = DEFAULT_LOG_LEVEL;
	}
	
	public ErrorDescriptionJsonException(String errorCode,String errorDescription,JsonExceptionLogLevel logLevel) {
		this.errorCode = errorCode;
		this.logLevel = logLevel;
		this.errorDescription = errorDescription;
	}

	@Override
	public String toJson() {
		return "{ \"" + ERROR_CODE_FIELD_NAME + "\":" + "\"" + errorCode + "\",\"" + ERROR_DESCRIPTION_FIELD_NAME + "\":" + "\"" + errorDescription + "\"}";
	}

	@Override
	public JsonExceptionLogLevel getLogLevel() {
		return logLevel;
	}
	
	
	public String description(){
		return errorDescription;
	}
}
