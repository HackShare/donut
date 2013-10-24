package com.github.anastasop.donut.engine;

import java.util.List;

public interface Flow {
	List<JobController> getJobsByState(JobState state);
	
	JobController getJobControllerByName(String jobName);
	
	JobState getState();
}
