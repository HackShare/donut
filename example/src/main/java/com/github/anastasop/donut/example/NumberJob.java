package com.github.anastasop.donut.example;

import com.github.anastasop.donut.engine.Job;
import com.github.anastasop.donut.engine.JobInput;
import com.github.anastasop.donut.engine.JobOutput;
import com.github.anastasop.donut.engine.MessageEmitter;
import com.github.anastasop.donut.engine.MessageIterator;

public class NumberJob implements Job {
	@Override
	public void compute(JobInput input, JobOutput output) throws Exception {
		MessageEmitter emitter = output.getEmitter("fmtNumber");
		for (MessageIterator it = input.messagesFromChannel("number"); it.isValid(); it.goNext()) {
			emitter.emit(it.getMessage());
		}
	}
}
