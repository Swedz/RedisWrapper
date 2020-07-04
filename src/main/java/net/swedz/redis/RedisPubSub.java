package net.swedz.redis;

import org.json.JSONObject;
import redis.clients.jedis.JedisPubSub;

class RedisPubSub extends JedisPubSub {
	private final RedisConnectionWrapper redisConnection;
	
	public RedisPubSub(RedisConnectionWrapper redisConnection) {
		this.redisConnection = redisConnection;
	}
	
	@Override
	public void onMessage(String channel, String message) {
		try {
			// Call the listeners from our connection for this channel
			for(RedisListener listener : redisConnection.getListeners(channel)) {
				// We make a new json instance each listener call so that if one happens to modify it, it doesn't break other listeners
				listener.onRedisMessageReceived(channel, new JSONObject(message));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
