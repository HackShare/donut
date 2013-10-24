package com.github.anastasop.donut.engine;

public class Edge {
	final Vertex fromVertex;
	final String fromChannelName;
	final String toChannelName;
	final Vertex toVertex;
	
	Edge(Vertex fromVertex, String fromChannelName, String toChannelName, Vertex toVertex) {
		this.fromVertex = fromVertex;
		this.fromChannelName = fromChannelName;
		this.toChannelName = toChannelName;
		this.toVertex = toVertex;
	}
	
	public Vertex getFromVertex() {
		return fromVertex;
	}
	
	public String getFromChannelName() {
		return fromChannelName;
	}
	
	public String getToChannelName() {
		return toChannelName;
	}
	
	public Vertex getToVertex() {
		return toVertex;
	}
}
