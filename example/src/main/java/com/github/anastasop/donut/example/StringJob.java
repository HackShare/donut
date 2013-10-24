package com.github.anastasop.donut.example;

import com.github.anastasop.donut.engine.Job;
import com.github.anastasop.donut.engine.JobInput;
import com.github.anastasop.donut.engine.JobOutput;
import com.github.anastasop.donut.engine.Message;
import com.github.anastasop.donut.engine.MessageEmitter;
import com.github.anastasop.donut.engine.MessageIterator;

public class StringJob implements Job {
	@Override
	public void compute(JobInput input, JobOutput output) throws Exception {
		MessageEmitter emitter = output.getEmitter("fmtString");
		for (MessageIterator it = input.messagesFromChannel("string"); it.isValid(); it.goNext()) {
			Integer i = Integer.valueOf(it.getMessage().getPayloadAsString());
			emitter.emit(Message.withStringPayload(String.format("%02x", i)));
		}
	}
}
