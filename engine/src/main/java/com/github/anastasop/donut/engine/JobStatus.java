package com.github.anastasop.donut.engine;

public class JobStatus {
	private JobState state;
	private Exception stateException;
	
	public JobStatus() {
		this.changeStatus(JobState.NEWBORN);
	}
	
	public void changeStatus(JobState state, Exception executionException) {
		this.state = state;
		this.stateException = executionException;
	}
	
	public void changeStatus(JobState state) {
		this.changeStatus(state, null);
	}
	
	public boolean isOK() {
		return stateException == null;
	}
	
	public JobState getState() {
		return state;
	}
	
	public Exception getStateException() {
		return stateException;
	}
}
