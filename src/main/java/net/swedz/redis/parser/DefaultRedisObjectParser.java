package net.swedz.redis.parser;

public final class DefaultRedisObjectParser extends RedisObjectParser<String> {
	@Override
	public String parse(String value) {
		return value;
	}
	
	@Override
	public String serialize(String value) {
		return value;
	}
}
