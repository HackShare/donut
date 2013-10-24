package com.github.anastasop.donut.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

public class DefaultFlow implements Flow {
	String name;
	Map<String, JobBoss> bosses = new HashMap<String, JobBoss>();
	Graph graph;
	ExecutorService executorService;
	
	DefaultFlow(String name, Graph graph, ExecutorService executorService) {
		this.name = name;
		this.graph = graph;
		this.executorService = executorService;
	}
	
	void run() {
		List<Vertex> ordering = graph.getTopologicalOrdering();
		for (Vertex v : ordering) {
			JobBoss boss = new JobBoss(v);
			bosses.put(v.getKey(), boss);
			try {
				boss.setJobFuture(executorService.submit(boss));
				boss.getStatus().changeStatus(JobState.READY);
			} catch (RejectedExecutionException e) {
				boss.getStatus().changeStatus(JobState.SUBMITTED, e);
			}
		}
	}

	@Override
	public List<JobController> getJobsByState(JobState state) {
		List<JobController> jobs = new ArrayList<JobController>();
		for (JobBoss boss: bosses.values()) {
			if (boss.getStatus().getState().equals(state)) {
				jobs.add(boss);
			}
		}
		return jobs;
	}

	@Override
	public JobController getJobControllerByName(String jobName) {
		return bosses.get(jobName);
	}

	@Override
	public JobState getState() {
		List<JobController> jobs;
		jobs = getJobsByState(JobState.SUBMITTED);
		if (jobs.size() > 0) {
			return JobState.SUBMITTED;
		}
		jobs = getJobsByState(JobState.READY);
		if (jobs.size() > 0) {
			return JobState.READY;
		}
		jobs = getJobsByState(JobState.RUNNING);
		if (jobs.size() > 0) {
			return JobState.RUNNING;
		}
		jobs = getJobsByState(JobState.FAILED);
		if (jobs.size() > 0) {
			return JobState.FAILED;
		}
		jobs = getJobsByState(JobState.CANCELLED);
		if (jobs.size() > 0) {
			return JobState.CANCELLED;
		}
		return JobState.DONE;
	}
}
