package net.swedz.redis;

public abstract class RedisChildHandle {
	protected final RedisConnectionWrapper redis;
	
	public RedisChildHandle(RedisConnectionWrapper redis) {
		this.redis = redis;
	}
	
	public RedisConnectionWrapper getRedis() {
		return redis;
	}
}
