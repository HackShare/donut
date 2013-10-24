package com.github.anastasop.donut.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.name.Names;

public class Engine {
	private Logger logger = Logger.getLogger(Engine.class.getName());

	private Injector engineInjector;
	
	private Provider<ExecutorService> executorServiceProvider;
	
	public List<DefaultFlow> allFlows = new ArrayList<DefaultFlow>();
	
	public Engine() {
		engineInjector = Guice.createInjector(new EngineModule());
		executorServiceProvider = engineInjector.getProvider(
				Key.get(ExecutorService.class, Names.named("EngineExecutorService")));
	}
	
	private boolean checkGraphAndBindings(Injector inj, Graph graph) {
		List<Vertex> ordering = graph.getTopologicalOrdering();
		if (ordering == null) {
			logger.severe("the graph has no topological ordering. Computation stops");
			return false;
		}
		for (Vertex v: ordering) {
			Binding<Job> binding = inj.getExistingBinding(Key.get(Job.class, Names.named(v.getKey())));
			if (binding == null || binding.getProvider() == null) {
				logger.severe("there is not job binding with key '" + v.getKey() + "'. Computation stops");
				for (Vertex vv: ordering) {
					vv.setJob(null);
				}
				return false;
			} else {
				v.setJob(binding.getProvider().get());
			}
		}
		return true;
	}
	
	public Flow submitFlow(String name, Graph graph, Module... modules) {
		Injector inj = engineInjector.createChildInjector(modules);
		if (!checkGraphAndBindings(inj, graph)) {
			return null;
		}
		DefaultFlow flow = new DefaultFlow(name, graph, executorServiceProvider.get());
		allFlows.add(flow);
		flow.run();
		return flow;
	}
	
	public void shutdown() {
		for (DefaultFlow f: allFlows) {
			f.executorService.shutdown();
		}
	}
	
	public void shutdownNow() {
		for (DefaultFlow f: allFlows) {
			f.executorService.shutdownNow();
		}
	}
}
