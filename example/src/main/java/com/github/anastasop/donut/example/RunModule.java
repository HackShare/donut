package com.github.anastasop.donut.example;

import com.github.anastasop.donut.engine.Job;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

public class RunModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(Job.class).annotatedWith(Names.named("SourceJob")).to(SourceJob.class).in(Scopes.SINGLETON);
		bind(Job.class).annotatedWith(Names.named("NumberJob")).to(NumberJob.class).in(Scopes.SINGLETON);
		bind(Job.class).annotatedWith(Names.named("StringJob")).to(StringJob.class).in(Scopes.SINGLETON);
		bind(Job.class).annotatedWith(Names.named("SinkJob")).to(SinkJob.class).in(Scopes.SINGLETON);
	}
}
