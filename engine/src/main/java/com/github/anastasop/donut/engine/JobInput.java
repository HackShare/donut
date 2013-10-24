package com.github.anastasop.donut.engine;


public interface JobInput {
	MessageIterator messagesFromChannel(String channelName);
}
