import net.swedz.redis.RedisConnectionWrapper;
import net.swedz.redis.channel.RedisChannel;
import net.swedz.redis.resource.RedisResource;
import redis.clients.jedis.Jedis;

public class Example {
	public static void main(String[] args) throws Exception {
		// Initialize the redis database connection
		RedisConnectionWrapper redis = RedisConnectionWrapper.builder()
				.ip("localhost", 6379)
				.password("password")
				.timeout(10 * 1000)
				.build();
		
		// Register the test parser
		redis.registerParsers(new TestObjectParser());
		
		// Register the test channel listeners
		// You just need to supply the instances of classes where the listeners exist in
		redis.registerChannels(new Example());
		
		/*
		 * In order for channels to be subscribed to, the prepare method must be called.
		 * This will unsubscribe on the currently running pubsub (if any) and then
		 *  subscribe to the list of channels that are registered.
		 * If you register more channels after this is called, the newly registered channels
		 *  will not work. However new listeners will trigger if the channel they are listening to
		 *  was already registered before the prepare method was called.
		 */
		redis.getPubSub().prepare();
		
		// Safely work with a resource instance
		// The resource auto-closes after completion
		redis.safeResourceCall((r) -> {
			// Get the jedis resource field
			Jedis j = r.resource();
			
			// Publish to both channels
			j.publish("channel1", "value");
			j.publish("channel2", "value");
			
			// You can interact with keys normally through Jedis
			j.set("key1", "value");
			String value1 = j.get("key1");
			System.out.println("'key1' has value: '" + value1 + "'");
			
			// You can also interact with keys using the parser api
			RedisResource<TestObject> test = r.resource(TestObjectParser.class);
			test.set("key2", new TestObject("value"));
			TestObject value2 = test.get("key2");
			System.out.println("'key2' has value: '" + value2.getValue() + "'");
			
			// Delete the key values after we are finished so
			//  we don't interfere with anything by accident.
			// This is purely for testing purposes.
			j.del("key1", "key2");
		});
		
		// Sleep for a second so all of the commands complete fully
		Thread.sleep(1000L);
		
		// Close the redis connection
		redis.close();
	}
	
	/*
	 * This channel listener listens to channel "channel1" and the message
	 *  received gets passed straight through as the raw string value.
	 */
	@RedisChannel("channel1")
	private void channel1(String value) {
		System.out.println("'channel1' received value: '" + value + "'");
	}
	
	/*
	 * This channel listener listens to channel "channel2" and the message
	 *  received gets parsed into a TestObject value using the TestObjectParser
	 *  registered earlier.
	 */
	@RedisChannel(value = "channel2", parser = TestObjectParser.class)
	private void channel2(TestObject value) {
		System.out.println("'channel2' received value: '" + value.getValue() + "'");
	}
}
