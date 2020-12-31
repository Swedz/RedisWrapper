package net.swedz.redis.parser;

import net.swedz.redis.RedisChildHandle;
import net.swedz.redis.RedisConnectionWrapper;
import net.swedz.redis.RedisException;

import java.util.HashMap;
import java.util.Map;

public final class RedisParserHandle extends RedisChildHandle {
	private final Map<Class<? extends RedisObjectParser>, RedisObjectParser> parsers;
	
	public RedisParserHandle(RedisConnectionWrapper redis) {
		super(redis);
		this.parsers = new HashMap<>();
		this.register(new DefaultRedisObjectParser());
	}
	
	public <T> RedisObjectParser<T> get(Class<? extends RedisObjectParser<T>> parserClass) {
		if(!parsers.containsKey(parserClass))
			throw new RedisException("Parser not registered");
		return (RedisObjectParser<T>) parsers.get(parserClass);
	}
	
	public RedisObjectParser getWildcard(Class<? extends RedisObjectParser> parserClass) {
		if(!parsers.containsKey(parserClass))
			throw new RedisException("Parser not registered");
		return parsers.get(parserClass);
	}
	
	public void register(RedisObjectParser<?> parser) {
		Class<? extends RedisObjectParser> parserClass = parser.getClass();
		if(parsers.containsKey(parserClass))
			throw new RedisException("Parser already registered");
		parsers.put(parserClass, parser);
	}
	
	public void register(RedisObjectParser<?>...parsers) {
		for(RedisObjectParser<?> parser : parsers) {
			this.register(parser);
		}
	}
}
