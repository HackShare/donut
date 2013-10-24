package com.github.anastasop.donut.engine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;

public class Graph {
	List<Vertex> vertices = new ArrayList<Vertex>();
	List<Vertex> topologicalOrdering = null; 
	
	private boolean containsVertex(Vertex v) {
		return vertices.indexOf(v) != -1;
	}
	
	public Vertex addVertex(String name) {
		Vertex v = new Vertex(name);
		if (containsVertex(v)) {
			throw new RuntimeException("vertex already in the graph");
		}
		vertices.add(v);
		return v;
	}
	
	public void addEdge(Vertex v, String vChannelName, String uChannelName, Vertex u) {
		if (!containsVertex(v) || !containsVertex(u)) {
			throw new RuntimeException("vertices are not in the graph");
		}
		Edge e = new Edge(v, vChannelName, uChannelName, u);
		v.addOutgoingEdge(e);
		u.addIncomingEdge(e);
	}
	
	private static Pattern WHITESPACE = Pattern.compile("\\s+");
	private static Splitter EDGE_SYMBOL_SPLITTER = Splitter.on(WHITESPACE).omitEmptyStrings();
	private static Splitter EDGE_TOKENS_SPLITTER = Splitter.on(":").omitEmptyStrings().trimResults();
	
	public static Graph fromString(String s) {
		Graph g = new Graph();
		Map<String, Vertex> vertices = new HashMap<String, Vertex>();
		for (String es: EDGE_SYMBOL_SPLITTER.split(s)) {
			List<String> tokens = EDGE_TOKENS_SPLITTER.splitToList(es);
			String v1Name, v1Channel, v2Name, v2Channel;
			switch(tokens.size()) {
			case 3:
				v1Name = tokens.get(0);
				v1Channel = tokens.get(1);
				v2Channel = tokens.get(1);
				v2Name = tokens.get(2);
				break;
			case 4:
				v1Name = tokens.get(0);
				v1Channel = tokens.get(1);
				v2Channel = tokens.get(2);
				v2Name = tokens.get(3);
				break;
			default:
				throw new IllegalArgumentException("bad edge format: '" + es + "'");
			}
			Vertex v1 = vertices.get(v1Name);
			if (v1 == null) {
				v1 = g.addVertex(v1Name);
				vertices.put(v1Name, v1);
			}
			Vertex v2 = vertices.get(v2Name);
			if (v2 == null) {
				v2 = g.addVertex(v2Name);
				vertices.put(v2Name, v2);
			}
			g.addEdge(v1, v1Channel, v2Channel, v2);
		}
		return g;
	}
	
	public List<Vertex> getTopologicalOrdering() {
		if (topologicalOrdering != null) {
			return topologicalOrdering;
		}
		
		for (Vertex vv: vertices) {
			vv.preOrder = 0;
			vv.postOrder = 100000;
			vv.edgeIterator = vv.outgoingEdges.iterator();
		}
		Deque<Vertex> stack = new ArrayDeque<Vertex>(vertices.size());
		int preOrder = 1, postOrder = 1;
		for (Vertex vv: vertices) {
			if (vv.preOrder == 0) {
				stack.addLast(vv);
				iteration: while (!stack.isEmpty()) {
					Vertex v = stack.getLast();
					if (v.preOrder == 0) {
						v.preOrder = preOrder++;
					}
					while (v.edgeIterator.hasNext()) {
						Vertex w = v.edgeIterator.next().toVertex;
						if (w.preOrder == 0) {
							stack.addLast(w);
							continue iteration;
						} else if (w.preOrder <= v.preOrder && w.postOrder >= v.postOrder) {
							// w is an ancestor of v -> cycle edge 
							StringBuilder sb = new StringBuilder();
							Vertex u = null;
							do {
								u = stack.removeLast();
								sb.append(u.getKey()).append("(" + u.preOrder + "):");
							} while (u != w && !stack.isEmpty());
							throw new IllegalArgumentException("cycle detected: " + sb.toString());
						}
					}
					v.postOrder = postOrder++;
					if (stack.removeLast() != v) {
						throw new RuntimeException("bug. v is not at top of stack as assumed");
					}
				}
			}
		}
		// topological order: reverse postorder
		List<Vertex> sorted = new ArrayList<Vertex>(vertices);
		Collections.sort(sorted, new Comparator<Vertex>() {
			@Override
			public int compare(Vertex o1, Vertex o2) {
				if (o1.postOrder < o2.postOrder) {
					return 1;
				}
				return -1;
				// return 0 cannot happen as postOrder is unique
			}
		});
		topologicalOrdering = sorted;
		return sorted;
	}
}
