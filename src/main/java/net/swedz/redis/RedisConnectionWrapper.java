package net.swedz.redis;

import net.swedz.redis.channel.RedisChannelHandle;
import net.swedz.redis.parser.RedisObjectParser;
import net.swedz.redis.parser.RedisParserHandle;
import net.swedz.redis.pubsub.RedisPubSubHandle;
import net.swedz.redis.resource.RedisResourceWrapper;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class RedisConnectionWrapper {
	private final JedisPool jedis;
	
	private final RedisParserHandle parser;
	private final RedisChannelHandle channels;
	private final RedisPubSubHandle pubSub;
	
	private RedisConnectionWrapper(String hostname, int port, String password, int timeout, GenericObjectPoolConfig config) {
		this.jedis = new JedisPool(config, hostname, port, timeout, password);
		this.parser = new RedisParserHandle(this);
		this.channels = new RedisChannelHandle(this);
		this.pubSub = new RedisPubSubHandle(this);
	}
	
	public JedisPool getJedis() {
		return jedis;
	}
	
	public RedisResourceWrapper getResource() {
		return new RedisResourceWrapper(this);
	}
	
	public void safeResourceCall(Consumer<RedisResourceWrapper> action) {
		try (RedisResourceWrapper resource = this.getResource()) {
			action.accept(resource);
		}
	}
	
	public void asyncResourceCall(Consumer<RedisResourceWrapper> action) {
		CompletableFuture.runAsync(() -> this.safeResourceCall(action));
	}
	
	public RedisParserHandle getParser() {
		return parser;
	}
	
	public <T> T parse(Class<? extends RedisObjectParser<T>> parserClass, String value) {
		return parser.get(parserClass).parse(value);
	}
	
	public <T> String serialize(Class<? extends RedisObjectParser<T>> parserClass, T value) {
		return parser.get(parserClass).serialize(value);
	}
	
	public void registerParsers(RedisObjectParser<?>...parsers) {
		parser.register(parsers);
	}
	
	public RedisChannelHandle getChannels() {
		return channels;
	}
	
	public void registerChannels(Object...instances) {
		channels.register(instances);
	}
	
	public RedisPubSubHandle getPubSub() {
		return pubSub;
	}
	
	public void close() {
		pubSub.close();
		jedis.close();
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		private String hostname;
		private int port;
		private String password;
		private int timeout;
		private GenericObjectPoolConfig config;
		
		private Builder() {
			config = new GenericObjectPoolConfig();
			config.setMaxTotal(64);
			config.setMaxIdle(64);
			config.setMinIdle(16);
		}
		
		public String getHostname() {
			return hostname;
		}
		
		public int getPort() {
			return port;
		}
		
		public String getPassword() {
			return password;
		}
		
		public int getTimeout() {
			return timeout;
		}
		
		public GenericObjectPoolConfig getConfig() {
			return config;
		}
		
		public Builder ip(String hostname, int port) {
			this.hostname = hostname;
			this.port = port;
			return this;
		}
		
		public Builder password(String password) {
			this.password = password;
			return this;
		}
		
		public Builder timeout(int timeout) {
			this.timeout = timeout;
			return this;
		}
		
		public Builder config(Consumer<GenericObjectPoolConfig> consumer) {
			consumer.accept(config);
			return this;
		}
		
		public RedisConnectionWrapper build() {
			return new RedisConnectionWrapper(hostname, port, password, timeout, config);
		}
	}
}
