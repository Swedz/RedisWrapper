import net.swedz.redis.parser.RedisObjectParser;

public class TestObjectParser extends RedisObjectParser<TestObject> {
	/*
	 * The raw string message received from the server gets passed into this
	 *  method and the TestObject is returned from it.
	 */
	@Override
	public TestObject parse(String value) {
		return new TestObject(value);
	}
	
	/*
	 * The parsed message as TestObject is passed into this method and is returned
	 *  back into a raw string message value.
	 */
	@Override
	public String serialize(TestObject value) {
		return value.getValue();
	}
}
