package net.swedz.redis.pubsub;

import net.swedz.redis.RedisChildHandle;
import net.swedz.redis.RedisConnectionWrapper;
import redis.clients.jedis.Jedis;

public final class RedisPubSubHandle extends RedisChildHandle {
	private final Jedis resource;
	private final RedisPubSub pubSub;
	
	private Thread thread;
	
	public RedisPubSubHandle(RedisConnectionWrapper redis) {
		super(redis);
		this.resource = redis.getJedis().getResource();
		this.pubSub = new RedisPubSub(redis);
	}
	
	private String[] collectChannels() {
		return redis.getChannels().getRegisteredChannels().toArray(new String[0]);
	}
	
	private void reset() {
		if(pubSub.getChannels() != null)
			pubSub.unsubscribe(pubSub.getChannels());
		if(thread != null && thread.isAlive())
			thread.interrupt();
	}
	
	public void prepare() {
		this.reset();
		thread = new Thread(() -> {
			try {
				resource.subscribe(pubSub, this.collectChannels());
			} catch (ClassCastException ignore) {}
		});
		thread.start();
	}
	
	public void close() {
		this.reset();
		resource.close();
	}
}
