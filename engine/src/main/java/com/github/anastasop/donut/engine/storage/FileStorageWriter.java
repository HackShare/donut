package com.github.anastasop.donut.engine.storage;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.github.anastasop.donut.engine.IOStatus;
import com.github.anastasop.donut.engine.Message;
import com.github.anastasop.donut.engine.MessageEmitter;

public class FileStorageWriter {
	private static Charset UTF8_CHARSET = Charset.forName("UTF-8");
	private static final File FILES_DIR = new File("/tmp");
	
	static class Record {
		final String key;
		final int offset;
		final int length;
		
		public Record(String key, int offset, int length) {
			this.key = key;
			this.offset = offset;
			this.length = length;
		}
	}
	
	private DataOutputStream ost;
	private int offset;
	private List<Record> index;
	
	private FileStorageWriter() {}
	
	public static FileStorageWriter forFile(String name) throws IOException {
		FileStorageWriter writer = new FileStorageWriter();
		FileOutputStream fost = new FileOutputStream(new File(FILES_DIR, name).getAbsolutePath());
		writer.ost = new DataOutputStream(fost);
		writer.offset = 0;
		writer.index = new ArrayList<Record>();
		return writer;
	}
	
	public class Emitter implements MessageEmitter {
		private String key;
		private IOStatus status;
		
		public Emitter(String key) {
			this.key = key;
			this.status = new IOStatus();
		}
		
		public String getKey() {
			return key;
		}
		
		public IOStatus getStatus() {
			return status;
		}
		
		public synchronized void emit(Message msg) {
			try {
				byte[] data = msg.getPayloadAsByteArray();
				ost.write(data);
				index.add(new Record(key, offset, data.length));
				offset += data.length;
			} catch (IOException e) {
				status.setFailureException(e);
			}
		}

		@Override
		public boolean isValid() {
			return status.isOk();
		}
	}
	
	public synchronized void close() throws IOException {
		// this is a non-stable sort
		Collections.sort(index, new Comparator<Record>() {
			@Override
			public int compare(Record r1, Record r2) {
				return r1.key.compareTo(r2.key);
			}
		});
		for (Record r: index) {
			byte[] keyBytes = r.key.getBytes(UTF8_CHARSET);
			ost.writeShort(keyBytes.length);
			ost.write(keyBytes);
			ost.writeInt(r.offset);
			ost.writeInt(r.length);
		}
		if (index.size() > 0) {
			ost.writeInt(offset);
			ost.writeInt(index.size());
		}
		ost.close();
	}
	
	public Emitter getEmitterFor(String channelName) {
		return new Emitter(channelName);
	}
}
