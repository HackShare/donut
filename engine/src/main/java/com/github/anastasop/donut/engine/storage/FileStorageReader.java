package com.github.anastasop.donut.engine.storage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.List;

import com.github.anastasop.donut.engine.IOStatus;
import com.github.anastasop.donut.engine.Message;
import com.github.anastasop.donut.engine.MessageIterator;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class FileStorageReader {
	private static Charset UTF8_CHARSET = Charset.forName("UTF-8");
	private static final File FILES_DIR = new File("/tmp");
	
	private RandomAccessFile disk;
	private ListMultimap<String, FileStorageWriter.Record> index;
	
	private FileStorageReader() {}
	
	public static FileStorageReader forFile(String name) throws IOException {
		ListMultimap<String, FileStorageWriter.Record> index = ArrayListMultimap.create();
		RandomAccessFile disk = null;
		try {
			disk = new RandomAccessFile(new File(FILES_DIR, name).getAbsolutePath(), "r");
			disk.seek(disk.length() - 8);
			int indexOffset = disk.readInt();
			int indexSize = disk.readInt();
			disk.seek(indexOffset);
			for (int i = 0; i < indexSize; i++) {
				short keyLen = disk.readShort();
				byte[] keyBytes = new byte[keyLen];
				disk.readFully(keyBytes);
				String key = new String(keyBytes, UTF8_CHARSET);
				int offset = disk.readInt();
				int length = disk.readInt();
				index.put(key, new FileStorageWriter.Record(key, offset, length));
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (disk != null) {
				disk.close();
			}
		}
		
		FileStorageReader reader = new FileStorageReader();
		reader.disk = disk;
		reader.index = index;
		return reader;
	}
	
	public void close() throws IOException {
		disk.close();
	}
	
	public class Iterator implements MessageIterator {
		private String key;
		private boolean isFinished;
		private List<FileStorageWriter.Record> keysRecordList;
		private int pos;
		private Message cachedMessage;
		private IOStatus status;
		
		Iterator(String key) {
			this.key = key;
			this.isFinished = false;
			this.keysRecordList = index.get(key);
			this.pos = 0;
			this.status = new IOStatus();
			goNext();
		}

		@Override
		public boolean isValid() {
			return status.isOk() && !isFinished;
		}

		@Override
		public boolean isFinished() {
			return isFinished;
		}

		@Override
		public void goNext() {
			if (!isValid()) {
				return;
			}
			if (pos < keysRecordList.size()) {
				FileStorageWriter.Record r = keysRecordList.get(pos++);
				try {
					byte[] bytes = new byte[(int)r.length];
					disk.seek(r.offset);
					disk.readFully(bytes);
					cachedMessage = Message.withBytesPayload(bytes);
				} catch (IOException e) {
					status.setFailureException(e);
				}
			} else {
				isFinished = true;
			}
		}

		@Override
		public Message getMessage() {
			return isValid()? cachedMessage: null;
		}
		
		public IOStatus getStatus() {
			return status;
		}
		
		public String getKey() {
			return key;
		}
	}
	
	public Iterator getMessageIteratorFor(String channelName) {
		return new Iterator(channelName);
	}
}
