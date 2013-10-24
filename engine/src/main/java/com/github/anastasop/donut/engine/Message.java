package com.github.anastasop.donut.engine;

import java.nio.charset.Charset;

public class Message {
	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
	private byte[] payload;
	
	private Message() {}
	
	public static Message withStringPayload(String s) {
		Message m = new Message();
		m.payload = s.getBytes(UTF8_CHARSET);
		return m;
	}
	
	public static Message withBytesPayload(byte[] payload) {
		Message m = new Message();
		m.payload = payload;
		return m;
	}
	
	public String getPayloadAsString() {
		return new String(payload, UTF8_CHARSET);
	}

	public byte[] getPayloadAsByteArray() {
		return payload;
	}
}
