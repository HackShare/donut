package com.github.anastasop.donut.example;

import com.github.anastasop.donut.engine.Job;
import com.github.anastasop.donut.engine.JobInput;
import com.github.anastasop.donut.engine.JobOutput;
import com.github.anastasop.donut.engine.MessageIterator;

public class SinkJob implements Job {
	@Override
	public void compute(JobInput input, JobOutput output) throws Exception {
		MessageIterator it1 = input.messagesFromChannel("firstPart");
		MessageIterator it2 = input.messagesFromChannel("secondPart");
		while (it1.isValid() && it2.isValid()) {
			String msg = it1.getMessage().getPayloadAsString() + ":" + it2.getMessage().getPayloadAsString();
			System.out.println(msg);
			it1.goNext();
			it2.goNext();
		}
	}
}
