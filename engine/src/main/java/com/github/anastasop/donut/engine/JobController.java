package com.github.anastasop.donut.engine;


public interface JobController {
	void cancel();
	
	void resume();
	
	JobStatus getStatus();
}
