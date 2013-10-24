package com.github.anastasop.donut.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import com.github.anastasop.donut.engine.storage.FileStorageReader;
import com.github.anastasop.donut.engine.storage.FileStorageWriter;

public class JobBoss implements JobController, Callable<Void> {
	private Logger logger = Logger.getLogger(JobBoss.class.getName());
	
	private Vertex jobVertex;
	private JobStatus status = new JobStatus();
	
	private Map<String, FileStorageReader> readers =
			new HashMap<String, FileStorageReader>();
	
	private FileStorageWriter writer;
	
	private Future<Void> jobFuture;

	private JobInput createInputSpec() {
		for (Edge e : jobVertex.incomingEdges) {
			try {
				FileStorageReader reader = FileStorageReader.forFile(e.fromVertex.getKey());
				readers.put(e.fromVertex.getKey(), reader);
			} catch (IOException ex) {
				logger.severe("cannot create reader file for "
						+ e.fromVertex.getKey() + ": " + ex.getMessage());
				status.changeStatus(JobState.SUBMITTED, ex);
				return null;
			}
		}

		final JobInput inputSpec = new JobInput() {
			@Override
			public MessageIterator messagesFromChannel(String channelName) {
				List<FileStorageReader.Iterator> its =
						new ArrayList<FileStorageReader.Iterator>();
				for (Edge e : jobVertex.incomingEdges) {
					if (e.toChannelName.equals(channelName)) {
						its.add(readers.get(e.fromVertex.getKey()).getMessageIteratorFor(e.fromChannelName));
					}
				}
				switch(its.size()) {
				case 1:
					return its.get(0);
				case 0:
					throw new RuntimeException("undefined channel " + channelName);
				default:
					throw new RuntimeException("aggregating iterator not implemented");
				}
			}
		};
		return inputSpec;
	}
	
	private JobOutput createOutputSpec() {
		try {
			writer = FileStorageWriter.forFile(jobVertex.getKey());
		} catch (IOException ex) {
			logger.severe("cannot create writer file for " +
					jobVertex.getKey() + ": " + ex.getMessage());
			status.changeStatus(JobState.SUBMITTED, ex);
			return null;
		}
		
		final Map<String, FileStorageWriter.Emitter> emitters =
				new HashMap<String, FileStorageWriter.Emitter>();
		for (Edge e : jobVertex.outgoingEdges) {
			FileStorageWriter.Emitter emitter = writer.getEmitterFor(e.fromChannelName);
			emitters.put(e.fromChannelName, emitter);
		}

		final JobOutput emitter = new JobOutput() {
			@Override
			public MessageEmitter getEmitter(String channelName) {
				FileStorageWriter.Emitter emitter = emitters.get(channelName);
				if (emitter == null) {
					logger.severe("no emitter for " + channelName);
					return null;
				}
				return emitter;
			}
		};
		return emitter;
	}
	
	public void cleanup(boolean deletePartialOutput) {
		for (FileStorageReader r: readers.values()) {
			try {
				r.close();
			} catch (IOException e) {
				logger.severe("reader cleanup failed: " + e.getMessage());
				getStatus().changeStatus(JobState.FAILED, e);
			}
		}
		
		try {
			writer.close();
			if (deletePartialOutput) {
				// TODO remove file
			}
		} catch (IOException e) {
			logger.severe("writer cleanup failed: " + e.getMessage());
			getStatus().changeStatus(JobState.FAILED, e);
		}
	}
	
	public JobBoss(Vertex jobVertex) {
		this.jobVertex = jobVertex;
	}
	
	@Override
	public Void call() throws Exception {
		JobInput inputSpec = createInputSpec();
		JobOutput outputSpec = createOutputSpec();
		try {
			if (getStatus().isOK()) {
				jobVertex.getJob().compute(inputSpec, outputSpec);
			}
		} catch (ExecutionException e) {
			getStatus().changeStatus(JobState.FAILED, (Exception)e.getCause());
		} catch (CancellationException e) {
			getStatus().changeStatus(JobState.CANCELLED);
		} catch (InterruptedException e) {
			getStatus().changeStatus(JobState.CANCELLED);
		}
		
		cleanup(false);
		return null;
	}

	@Override
	public void cancel() {
		jobFuture.cancel(true);
	}

	@Override
	public void resume() {
		throw new RuntimeException("Not implemented yet");
	}

	@Override
	public JobStatus getStatus() {
		return status;
	}

	public Future<Void> getJobFuture() {
		return jobFuture;
	}

	public void setJobFuture(Future<Void> jobFuture) {
		this.jobFuture = jobFuture;
	}
}
