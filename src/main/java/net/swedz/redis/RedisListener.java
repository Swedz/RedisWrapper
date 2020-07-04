package net.swedz.redis;

import org.json.JSONObject;

public interface RedisListener {
	void onRedisMessageReceived(String channel, JSONObject message);
}
