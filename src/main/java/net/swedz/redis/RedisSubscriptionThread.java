package net.swedz.redis;

class RedisSubscriptionThread extends Thread {
	private final RedisConnectionWrapper connection;
	private final String[]               channels;
	
	RedisSubscriptionThread(RedisConnectionWrapper connection, String[] channels) {
		this.connection = connection;
		this.channels = channels;
		this.setName("Redis Subscription Thread");
	}
	
	@Override
	public void run() {
		this.connection.safeResourceCall((resource) -> {
			try {
				resource.subscribe(new RedisPubSub(connection), channels);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}
}
