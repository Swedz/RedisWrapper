# RedisWrapper

RedisWrapper is a simple library that wraps Jedis to make interacting with it much more clean.

**Special features:**
- Multithreaded pubsub
- Channel pubsub listeners using annotations
- Value parsing and serializing

**See [Example.java](/src/test/java/Example.java) (and the other classes it references) for an example on how to use the wrapper. Most features are included in the example.**

## Download

Latest version: 1.2-SNAPSHOT

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