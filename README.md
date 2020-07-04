# RedisWrapper

This redis wrapper uses the org.json JSON library. Subscriptions are handled as json messages only. If your subscriptions are not JSONs then there will be errors. You can modify this however you want to make it work for your needs.

To establish a redis connection:
```java
RedisConnectionWrapper connection = new RedisConnectionWrapper("hostname", 6379, "password", 10 * 1000);
```

Each redis listener has its own class which implements RedisListener:
```java
@RedisSubscriber(channels = {"testchannel"})
public class TestRedisListener implements RedisListener {
	public TestRedisListener() {
		super("testchannel");
	}

	@Override
	public void onRedisMessageReceived(String channel, JSONObject message) {
		System.out.println("Received redis message from " + channel + ": " + message.toString());
	}
}
```

After that, register **all** of your listeners at once:
```java
connection.registerListeners(
	new TestRedisListener()
);
```
If you don't register your listeners all in one call you will not be able to register them again. This is because of how redis subscriptions work. There are potentially some workarounds, but this works the best as far as I am aware.

Note that this creates a new thread for your subscriptions to be listened on. The listeners will be called asynchronously, so make sure all of your calls are thread-safe.

To make a call to redis, you can do:
```java
connection.safeResourceCall((resource) -> {
	resource.set("testkey", "testing :)");
	System.out.println("testkey's value: " + resource.get("testkey"));
});
```
This automatically gets the resource and uses "try-with-resources" to make sure the resource gets returned to the pool after you're done using it. You can call any redis command using the Jedis instance. There is also #asyncResourceCall(), which automatically calls #safeResourceCall() asynchronously for you using CompletableFuture.

Of course, when you close your program, you should close your redis connection. It simple to do so:
```java
connection.close();
```

## Download

Latest version: 1.0-SNAPSHOT

Be sure to replace the **VERSION** key below with one of the versions available.

**Maven**

```xml
<repository>
    <id>swedz</id>
    <name>swedz-repo</name>
    <url>https://swedz.net/repo/</url>
</repository>
```

```xml
<dependency>
    <groupId>net.swedz</groupId>
    <artifactId>RedisWrapper</artifactId>
    <version>VERSION</version>
</dependency>
```

**Gradle**

```groovy
repositories {
    maven { url = 'https://swedz.net/repo/' }
}
```

```groovy
dependencies {
    compile group: 'net.swedz', name: 'RedisWrapper', version: 'VERSION'
}
```