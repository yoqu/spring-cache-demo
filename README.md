# Spring cache demo

项目使用Spring cache框架搭建了个demo，cache的实现为redis，通过自定义`CacheManager`的bean来设置过期时间以及自定义的序列化方式。

项目基于Spring boot构建，版本号为：`2.0.5.RELEASE`

cache主要依赖：

```pom
 		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
```



配置代码：


```java
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(12))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()));
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(connectionFactory).cacheDefaults(redisCacheConfiguration);
        builder.withInitialCacheConfigurations(customCacheConfig());
        return builder.build();
    }

    private Map<String, RedisCacheConfiguration> customCacheConfig() {
        Map<String, RedisCacheConfiguration> map = new HashMap<>();
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()));
        map.put("userdetail", redisCacheConfiguration);
        return map;
    }

    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    private RedisSerializer<Object> valueSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
```

## 测试使用

1. 运行`SpringCacheDemoApplication`主函数
2. 访问`http://localhost:8080`查看数据

## 自定义key的CacheConfig源码讲解

先讲自定义可以干嘛，再讲解源码：

通过自定义cache config，可以用来设置自定义的过期时间，自定义的序列化方式，自定义前缀等等。

`@Cacheable` 注解不能设置过期时间，这点是由于cache本身是抽象，各种实现过期时间的一些具体缓存框架可能有差异，不过我觉得这是一个非常不爽的点。
所以我们来阅读源代码吧。

### Cache启动初始化

`AbstractCacheManager`类中有一个`cacheMap`变量存储所有的缓存实现，在项目初始化时，由于类中实现了`InitializingBean`接口，所有会初始化缓存，代码：

```java
	public abstract class AbstractCacheManager implements CacheManager, InitializingBean {

	private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>(16);

	private volatile Set<String> cacheNames = Collections.emptySet();

	@Override
	public void afterPropertiesSet() {
		initializeCaches();
	}

	/**
	 * Initialize the static configuration of caches.
	 * <p>Triggered on startup through {@link #afterPropertiesSet()};
	 * can also be called to re-initialize at runtime.
	 * @since 4.2.2
	 * @see #loadCaches()
	 */
	public void initializeCaches() {
        // 1⃣️重点在loadCaches方法
		Collection<? extends Cache> caches = loadCaches();
		synchronized (this.cacheMap) {
			this.cacheNames = Collections.emptySet();
			this.cacheMap.clear();           
			Set<String> cacheNames = new LinkedHashSet<>(caches.size());
			for (Cache cache : caches) {
				String name = cache.getName();
				this.cacheMap.put(name, decorateCache(cache));
				cacheNames.add(name);
			}
			this.cacheNames = Collections.unmodifiableSet(cacheNames);
		}
	}
}
```

由于loadCaches方法是抽象的，我们实现使用的redis实现，所有直接查看`org.springframework.data.redis.cache.RedisCacheManager`类的实现，阅读源代码发现：

```java
public class RedisCacheManager extends AbstractTransactionSupportingCacheManager {
...
	private final Map<String, RedisCacheConfiguration> initialCacheConfiguration;
...
	@Override
	protected Collection<RedisCache> loadCaches() {
	    //1⃣️可以看到实际上就是取initialCacheConfiguration变量的值
		List<RedisCache> caches = new LinkedList<>();
		for (Map.Entry<String, RedisCacheConfiguration> entry : initialCacheConfiguration.entrySet()) {
            //2⃣️初始化cache
			caches.add(createRedisCache(entry.getKey(), entry.getValue()));
		}
		return caches;
	}
	protected RedisCache createRedisCache(String name, @Nullable RedisCacheConfiguration cacheConfig) {
		return new RedisCache(name, cacheWriter, cacheConfig != null ? cacheConfig : defaultCacheConfig);
	}
...    
}    
```

通过注入自定义的cacheConfig能够使不同的key拥有不同的cache配置，达到自定义的效果。

### Cache被调用

回到上面的正题，在`cacheManager`初始化完成后，当有请求来到@Cacheable注解处的方法时，会通过aop代理的形式做invoke，顶层是在`CacheAspectSupport`的execute方法进行代理，

中间一个步骤省略，它最后会直接通过CacheManager去获取cache，方法为：

```java
public abstract class AbstractCacheManager implements CacheManager, InitializingBean {
...
    @Override
	@Nullable
	public Cache getCache(String name) {
		Cache cache = this.cacheMap.get(name);
		if (cache != null) {
			return cache;
		}
		else {
			// Fully synchronize now for missing cache creation...
			synchronized (this.cacheMap) {
				cache = this.cacheMap.get(name);
				if (cache == null) {
					cache = getMissingCache(name);
					if (cache != null) {
						cache = decorateCache(cache);
						this.cacheMap.put(name, cache);
						updateCacheNames(name);
					}
				}
				return cache;
			}
		}
	}
...
}
```

我们查看下`RedisCache`内部调用生成缓存的方法来看一下。

```java
public class RedisCache extends AbstractValueAdaptingCache {
    @Override
	public void put(Object key, @Nullable Object value) {
		Object cacheValue = preProcessCacheValue(value);
...
    	//1⃣️ 过期时间是通过cacheConfig配置进行获取的。
		cacheWriter.put(name, createAndConvertCacheKey(key), serializeCacheValue(cacheValue), cacheConfig.getTtl());
	}
    protected byte[] serializeCacheValue(Object value) {

		if (isAllowNullValues() && value instanceof NullValue) {
			return BINARY_NULL_VALUE;
		}
        //2⃣️ value的序列化方式也是通过cacheConfig配置来初始化的
		return ByteUtils.getBytes(cacheConfig.getValueSerializationPair().write(value));
	}
}
```







### 自定义CacheConfig的配置方法

```java
 	@Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(connectionFactory);
        builder.withInitialCacheConfigurations(customCacheConfig());
        return builder.build();
    }

    private Map<String, RedisCacheConfiguration> customCacheConfig() {
        Map<String, RedisCacheConfiguration> map = new HashMap<>();
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(1)).serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
```

> PS: 感觉使用Spring cache还是略麻烦，不如自己实现一个基于aop的cache吧。