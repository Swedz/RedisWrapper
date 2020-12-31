package net.swedz.redis.channel;

import net.swedz.redis.RedisChildHandle;
import net.swedz.redis.RedisConnectionWrapper;
import net.swedz.redis.RedisException;
import net.swedz.redis.parser.RedisObjectParser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class RedisChannelHandle extends RedisChildHandle {
	private final Map<String, List<RedisChannelWrapper>> channels;
	
	public RedisChannelHandle(RedisConnectionWrapper redis) {
		super(redis);
		this.channels = new HashMap<>();
	}
	
	public Set<String> getRegisteredChannels() {
		return channels.keySet();
	}
	
	public List<RedisChannelWrapper> getOrComputeListeners(String channel) {
		return channels.computeIfAbsent(channel, (k) -> new ArrayList<>());
	}
	
	public void listenersForEach(String channel, Consumer<RedisChannelWrapper> action) {
		if(!channels.containsKey(channel))
			throw new RedisException("No listeners registered on channel '" + channel + "'");
		List<RedisChannelWrapper> listeners = new ArrayList<>(channels.get(channel));
		listeners.sort(Comparator.comparingInt((c) -> c.getPriority().ordinal()));
		listeners.forEach(action);
	}
	
	private void register(Object instance, Method method) {
		try {
			RedisChannel redisChannel = method.getDeclaredAnnotation(RedisChannel.class);
			String channel = redisChannel.value();
			Class<? extends RedisObjectParser> parserClass = redisChannel.parser();
			ListenerPriority priority = redisChannel.priority();
			RedisChannelWrapper channelWrapper = new RedisChannelWrapper(
					channel, parserClass, priority,
					instance, method);
			this.getOrComputeListeners(channel).add(channelWrapper);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException("Failed to register redis channel listener", ex);
		}
	}
	
	public void register(Object instance) {
		Class<?> instanceClass = instance.getClass();
		for(Method method : instanceClass.getDeclaredMethods()) {
			if(method.isAnnotationPresent(RedisChannel.class)) {
				method.setAccessible(true);
				this.register(instance, method);
			}
		}
	}
	
	public void register(Object...instances) {
		for(Object instance : instances) {
			this.register(instance);
		}
	}
}
