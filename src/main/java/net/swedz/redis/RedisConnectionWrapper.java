package net.swedz.redis;

import com.google.common.collect.Lists;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RedisConnectionWrapper {
	private final JedisPool                        jedisPool;
	private final Map<String, List<RedisListener>> listeners;
	
	private RedisSubscriptionThread subscriptionThread;
	
	public RedisConnectionWrapper(String hostname, int port, String password, int timeout) {
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		poolConfig.setMaxTotal(64);
		poolConfig.setMaxIdle(64);
		poolConfig.setMinIdle(16);
		this.jedisPool = new JedisPool(poolConfig, hostname, port, timeout, password);
		this.listeners = new HashMap<>();
	}
	
	public JedisPool getJedisPool() {
		return jedisPool;
	}
	
	public Jedis getResource() {
		return getJedisPool().getResource();
	}
	
	public Map<String, List<RedisListener>> getListeners() {
		return listeners;
	}
	
	public List<RedisListener> getListeners(String channel) {
		return this.getListeners().getOrDefault(channel, Lists.newArrayList());
	}
	
	private void addListener(String channel, RedisListener listener) {
		List<RedisListener> listeners = this.getListeners(channel);
		listeners.add(listener);
		this.listeners.put(channel, listeners);
	}
	
	public void registerListeners(RedisListener... listeners) {
		// Make sure we only call this once
		// We don't want more than one subscription running
		if(this.listeners.size() > 0)
			throw new IllegalStateException("Cannot register listeners more than once!");
		
		// Get all of the channels
		final List<String> allChannels = Lists.newArrayList();
		for(RedisListener listener : listeners) {
			Class<? extends RedisListener> listenerClass = listener.getClass();
			// Make sure the annotation is present
			if(!listenerClass.isAnnotationPresent(RedisSubscriber.class))
				throw new IllegalArgumentException("RedisListener (" + listenerClass.getSimpleName() + ") is missing RedisSubscriber annotation!");
			// Get the annotation data
			RedisSubscriber subscriber = listenerClass.getAnnotation(RedisSubscriber.class);
			String[] channels = subscriber.channels();
			// Track these channels
			allChannels.addAll(Arrays.asList(channels));
			for(String channel : channels)
				this.addListener(channel, listener);
		}
		
		// Subscribe to the channels on a new thread so we don't block anything
		subscriptionThread = new RedisSubscriptionThread(this, allChannels.toArray(new String[0]));
		subscriptionThread.start();
	}
	
	public void safeResourceCall(Consumer<Jedis> consumer) {
		try (Jedis resource = this.getResource()) {
			consumer.accept(resource);
		}
	}
	
	public void asyncResourceCall(Consumer<Jedis> consumer) {
		CompletableFuture.runAsync(() -> this.safeResourceCall(consumer));
	}
	
	public void close() {
		subscriptionThread.interrupt();
		jedisPool.close();
	}
}
