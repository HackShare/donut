package com.github.anastasop.donut.example;

import com.github.anastasop.donut.engine.Engine;
import com.github.anastasop.donut.engine.Flow;
import com.github.anastasop.donut.engine.Graph;

public class Run {
	public static void main(String[] args) {
		String jobGraph =
				"SourceJob:toNumber:number:NumberJob " +
				"SourceJob:toString:string:StringJob " +
				"NumberJob:fmtNumber:firstPart:SinkJob " +
				"StringJob:fmtString:secondPart:SinkJob ";
		Graph g = Graph.fromString(jobGraph);
		
		Engine master = new Engine();
		Flow f = master.submitFlow("FirstFlow", g, new RunModule());
		master.shutdown();
		System.out.println(f.getState());
	}
}
