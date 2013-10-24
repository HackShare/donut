package com.github.anastasop.donut.engine;

public interface MessageIterator {
	boolean isValid();
	
	boolean isFinished();
	
	void goNext();
	
	Message getMessage();
}
