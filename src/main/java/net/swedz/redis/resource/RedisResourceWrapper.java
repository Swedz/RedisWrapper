package net.swedz.redis.resource;

import net.swedz.redis.RedisConnectionWrapper;
import net.swedz.redis.parser.RedisObjectParser;
import redis.clients.jedis.Jedis;

import java.io.Closeable;

public final class RedisResourceWrapper implements Closeable {
	private final RedisConnectionWrapper redis;
	private final Jedis                  resource;
	
	public RedisResourceWrapper(RedisConnectionWrapper redis) {
		this.redis = redis;
		this.resource = redis.getJedis().getResource();
	}
	
	public RedisConnectionWrapper getRedis() {
		return redis;
	}
	
	public Jedis resource() {
		return resource;
	}
	
	public <T> RedisResource<T> resource(Class<? extends RedisObjectParser<T>> parserClass) {
		return new RedisResource<>(this, parserClass);
	}
	
	@Override
	public void close() {
		resource.close();
	}
}
