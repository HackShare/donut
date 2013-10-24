package com.github.anastasop.donut.engine;


public interface Job {
	public void compute(JobInput input, JobOutput output) throws Exception;
}
