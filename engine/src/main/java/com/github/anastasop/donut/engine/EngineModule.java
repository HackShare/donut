package com.github.anastasop.donut.engine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.name.Named;

public class EngineModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(Engine.class).in(Scopes.SINGLETON);
	}
	
	@Provides @Named("EngineExecutorService")
	public ExecutorService provideSingleThreadExecutorService() {
		return Executors.newSingleThreadExecutor();
	}
}
