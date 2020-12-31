package net.swedz.redis.pubsub;

import net.swedz.redis.RedisConnectionWrapper;
import net.swedz.redis.RedisException;
import net.swedz.redis.parser.RedisObjectParser;
import redis.clients.jedis.JedisPubSub;

public final class RedisPubSub extends JedisPubSub {
	private final RedisConnectionWrapper redis;
	
	private String[] channels;
	
	public RedisPubSub(RedisConnectionWrapper redis) {
		this.redis = redis;
	}
	
	public RedisConnectionWrapper getRedis() {
		return redis;
	}
	
	public String[] getChannels() {
		return channels;
	}
	
	@Override
	public void subscribe(String...channels) {
		this.channels = channels;
		super.subscribe(channels);
	}
	
	@Override
	public void unsubscribe() {
		this.channels = null;
		super.unsubscribe();
	}
	
	@Override
	public void onMessage(String channel, String message) {
		redis.getChannels().listenersForEach(channel, (c) -> {
			try {
				RedisObjectParser parser = redis.getParser().getWildcard(c.getParserClass());
				Object parsedValue = parser.parse(message);
				c.getMethod().invokeWithArguments(parsedValue);
			} catch (Throwable throwable) {
				throw new RedisException("Failed to invoke channel listener method", throwable);
			}
		});
	}
}
