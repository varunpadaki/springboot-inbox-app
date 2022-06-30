package com.springboot.inbox.exception;

public class BadRequestException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String errorMessage;
	private String debugMessage;
	// custom error code specific to application not HTTPSTATUS Code
	private String errorCode;

	public BadRequestException() {
		super();
	}

	public BadRequestException(String errorMessage) {
		super(errorMessage);
	}

	public BadRequestException(String errorMessage, String debugMessage, String errorCode) {
		super(errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.debugMessage = debugMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getDebugMessage() {
		return debugMessage;
	}

	public void setDebugMessage(String debugMessage) {
		this.debugMessage = debugMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public String toString() {
		return errorMessage;
	}

}
