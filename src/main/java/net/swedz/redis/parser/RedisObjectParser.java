package net.swedz.redis.parser;

public abstract class RedisObjectParser<T> {
	public abstract T parse(String value);
	
	public abstract String serialize(T value);
}
