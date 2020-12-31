package net.swedz.redis.channel;

import net.swedz.redis.parser.DefaultRedisObjectParser;
import net.swedz.redis.parser.RedisObjectParser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedisChannel {
	String value();
	
	Class<? extends RedisObjectParser> parser() default DefaultRedisObjectParser.class;
	
	ListenerPriority priority() default ListenerPriority.NORMAL;
}
