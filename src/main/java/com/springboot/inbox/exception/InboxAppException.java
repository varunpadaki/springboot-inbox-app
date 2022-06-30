package com.springboot.inbox.exception;

import org.springframework.http.HttpStatus;

public class InboxAppException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String errorMessage;
	private HttpStatus httpStatus;
	private String debugMessage;
	private String errorCode;

	public InboxAppException() {
		super();
	}

	public InboxAppException(String errorMessage) {
		super(errorMessage);
	}

	public InboxAppException(String errorMessage, HttpStatus httpStatus, String debugMessage, String errorCode) {
		super(errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.httpStatus = httpStatus;
		this.debugMessage = debugMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
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
