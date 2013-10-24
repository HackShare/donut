package com.github.anastasop.donut.engine;

public interface JobOutput {
	MessageEmitter getEmitter(String channelName);
}
