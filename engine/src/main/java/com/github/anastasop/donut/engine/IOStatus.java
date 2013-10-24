package com.github.anastasop.donut.engine;

public class IOStatus {
	private boolean ok;
	private Exception failureException;
	
	public IOStatus() {
		this(true, null);
	}
	
	public IOStatus(boolean ok, Exception failureException) {
		this.ok = ok;
		this.failureException = failureException;
	}

	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	public Exception getFailureException() {
		return failureException;
	}

	public void setFailureException(Exception failureException) {
		this.ok = false;
		this.failureException = failureException;
	}
}
