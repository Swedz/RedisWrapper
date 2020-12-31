package net.swedz.redis.resource;

import net.swedz.redis.parser.RedisObjectParser;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class RedisResource<T> {
	private final RedisResourceWrapper resource;
	private final Class<? extends RedisObjectParser<T>> parserClass;
	
	public RedisResource(RedisResourceWrapper resource, Class<? extends RedisObjectParser<T>> parserClass) {
		this.resource = resource;
		this.parserClass = parserClass;
	}
	
	public RedisResourceWrapper getParent() {
		return resource;
	}
	
	private Jedis resource() {
		return resource.resource();
	}
	
	public Class<? extends RedisObjectParser<T>> getParserClass() {
		return parserClass;
	}
	
	private T parse(String value) {
		return resource.getRedis().parse(parserClass, value);
	}
	
	private String serialize(T value) {
		return resource.getRedis().serialize(parserClass, value);
	}
	
	// Single value key type commands
	
	public void set(String key, T value) {
		this.resource().set(key, this.serialize(value));
	}
	
	public void set(String key, T value, SetParams params) {
		this.resource().set(key, this.serialize(value), params);
	}
	
	public T get(String key) {
		return this.parse(this.resource().get(key));
	}
	
	// Set value key type commands
	
	public void sadd(String key, Set<T> values) {
		this.resource().sadd(key, values.stream()
				.map(this::serialize)
				.toArray(String[]::new));
	}
	
	public void sadd(String key, T value) {
		this.resource().sadd(key, this.serialize(value));
	}
	
	public void srem(String key, Set<T> values) {
		this.resource().srem(key, values.stream()
				.map(this::serialize)
				.toArray(String[]::new));
	}
	
	public void srem(String key, T value) {
		this.resource().srem(key, this.serialize(value));
	}
	
	public Set<T> smembers(String key) {
		return this.resource().smembers(key).stream()
				.map(this::parse)
				.collect(Collectors.toSet());
	}
	
	// Map value key type commands
	
	public void hdel(String key, String...fields) {
		this.resource().hdel(key, fields);
	}
	
	public T hget(String key, String field) {
		return this.parse(this.resource().hget(key, field));
	}
	
	public void hset(String key, String field, T value) {
		this.resource().hset(key, field, this.serialize(value));
	}
	
	public Map<String, T> hgetAll(String key) {
		return this.resource().hgetAll(key).entrySet().stream().collect(Collectors.toMap(
				Map.Entry::getKey,
				(e) -> this.parse(e.getValue())));
	}
	
	public List<T> hmget(String key, String...fields) {
		return this.resource().hmget(key, fields).stream()
				.map(this::parse)
				.collect(Collectors.toList());
	}
	
	public void hmset(String key, Map<String, T> map) {
		this.resource().hmset(key, map.entrySet().stream().collect(Collectors.toMap(
				Map.Entry::getKey,
				(e) -> this.serialize(e.getValue()))));
	}
	
	public List<T> hvals(String key) {
		return this.resource().hvals(key).stream()
				.map(this::parse)
				.collect(Collectors.toList());
	}
}
