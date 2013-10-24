package com.github.anastasop.donut.example;

import com.github.anastasop.donut.engine.Job;
import com.github.anastasop.donut.engine.JobInput;
import com.github.anastasop.donut.engine.JobOutput;
import com.github.anastasop.donut.engine.Message;
import com.github.anastasop.donut.engine.MessageEmitter;

public class SourceJob implements Job {
	@Override
	public void compute(JobInput input, JobOutput output) throws Exception {
		MessageEmitter numbersEmitter = output.getEmitter("toNumber");
		MessageEmitter stringsEmitter = output.getEmitter("toString");
		for (int i = 0; i < 10; i++) {
			numbersEmitter.emit(Message.withStringPayload(String.valueOf(i)));
			stringsEmitter.emit(Message.withStringPayload(String.valueOf(i)));
		}
	}
}
