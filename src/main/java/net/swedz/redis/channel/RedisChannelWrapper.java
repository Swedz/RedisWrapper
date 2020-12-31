package net.swedz.redis.channel;

import net.swedz.redis.parser.RedisObjectParser;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public final class RedisChannelWrapper {
	private final String channel;
	private final Class<? extends RedisObjectParser> parserClass;
	private final ListenerPriority priority;
	private final MethodHandle method;
	
	public RedisChannelWrapper(String channel, Class<? extends RedisObjectParser> parserClass, ListenerPriority priority,
							   Object instance, Method method) throws IllegalAccessException {
		this.channel = channel;
		this.parserClass = parserClass;
		this.priority = priority;
		this.method = MethodHandles.lookup().unreflect(method).bindTo(instance);
	}
	
	public String getChannel() {
		return channel;
	}
	
	public Class<? extends RedisObjectParser> getParserClass() {
		return parserClass;
	}
	
	public ListenerPriority getPriority() {
		return priority;
	}
	
	public MethodHandle getMethod() {
		return method;
	}
}
