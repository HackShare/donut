package com.github.anastasop.donut.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Vertex {
	private String key;
	List<Edge> outgoingEdges = new ArrayList<Edge>();
	List<Edge> incomingEdges = new ArrayList<Edge>();
	private Job job;
	
	// used to compute the topological ordering
	int preOrder = 0;
	int postOrder = 0;
	Iterator<Edge> edgeIterator = null;
	
	Vertex(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Vertex)) {
			return false;
		}
		Vertex that = (Vertex)o;
		return this.key.equals(that.key);
	}
	
	@Override
	public int hashCode() {
		return key.hashCode();
	}
	
	@Override
	public String toString() {
		return key;
	}
	
	private void assertNonDuplicateEdge(List<Edge> edges, Edge e) {
		for (Edge ee: edges) {
			if (ee.fromVertex.equals(e.fromVertex) && ee.toVertex.equals(e.toVertex)) {
				throw new RuntimeException(String.format("edge (%s,  %s) already exists", e.fromVertex.getKey(), e.toVertex.getKey()));
			}
		}
	}
	
	public void addOutgoingEdge(Edge e) {
		assertNonDuplicateEdge(outgoingEdges, e);
		outgoingEdges.add(e);
	}
	
	public void addIncomingEdge(Edge e) {
		assertNonDuplicateEdge(incomingEdges, e);
		incomingEdges.add(e);
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}
}
