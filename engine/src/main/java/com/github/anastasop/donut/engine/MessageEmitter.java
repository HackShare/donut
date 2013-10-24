package com.github.anastasop.donut.engine;

public interface MessageEmitter {
	boolean isValid();
	
	void emit(Message msg);
}
