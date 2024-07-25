package de.peaqe.minecraftDiscordSynchronisation.api.provider.cache;

import com.google.gson.Gson;
import de.peaqe.minecraftDiscordSynchronisation.MinecraftDiscordSynchronisation;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * *
 *
 * @author peaqe
 * @version 1.0
 * @since 24.07.2024 | 16:27 Uhr
 * *
 */

@SuppressWarnings("unused")
public class CachedMap<K, V> {

    private final Map<K, V> cache;
    private final JedisPool jedisPool;
    private final String redisKey;
    private final Logger logger;
    private final Gson gson;

    // Habe auf das Passwort verzichtet. Du müsstest bei dem Test das MySQL Passwort verwenden
    public CachedMap(MinecraftDiscordSynchronisation minecraftDiscordSynchronisation) {
        this.cache = new HashMap<>();
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        this.jedisPool = new JedisPool(poolConfig, "localhost", 6379, 2000
                /*minecraftDiscordSynchronisation.getDatabaseConnection().password()*/);
        this.redisKey = "vicemc-api";
        this.logger = Logger.getLogger(CachedMap.class.getName());
        this.gson = new Gson();
    }

    public void put(K key, V value) {
        cache.put(key, value);
        try (Jedis jedis = jedisPool.getResource()) {
            String valueJson = serialize(value);
            jedis.hset(redisKey, key.toString(), valueJson);
        } catch (Exception e) {
            logger.severe("Failed to put value in Redis: " + e.getMessage());
        }
    }

    public V get(K key, Class<V> valueType) {
        V value = cache.get(key);
        if (value == null) {
            try (Jedis jedis = jedisPool.getResource()) {
                String data = jedis.hget(redisKey, key.toString());
                if (data != null) {
                    value = deserialize(data, valueType);
                    cache.put(key, value);
                }
            } catch (Exception e) {
                logger.severe("Failed to get value from Redis: " + e.getMessage());
            }
        }
        return value;
    }

    public void remove(K key) {
        cache.remove(key);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hdel(redisKey, key.toString());
        } catch (Exception e) {
            logger.severe("Failed to remove value from Redis: " + e.getMessage());
        }
    }

    public boolean containsKey(K key) {
        if (cache.containsKey(key)) {
            return true;
        } else {
            try (Jedis jedis = jedisPool.getResource()) {
                return jedis.hexists(redisKey, key.toString());
            } catch (Exception e) {
                logger.severe("Failed to check key existence in Redis: " + e.getMessage());
                return false;
            }
        }
    }

    public boolean containsValue(V value) {
        if (cache.containsValue(value)) {
            return true;
        } else {
            try (Jedis jedis = jedisPool.getResource()) {
                String valueJson = serialize(value);
                for (String val : jedis.hvals(redisKey)) {
                    if (val.equals(valueJson)) {
                        return true;
                    }
                }
                return false;
            } catch (Exception e) {
                logger.severe("Failed to check value existence in Redis: " + e.getMessage());
                return false;
            }
        }
    }

    public List<String> loadAllAsJson() {
        var list = (ArrayList<String>) null;

        try (var jedis = jedisPool.getResource()) {
            list = new ArrayList<>(jedis.hvals(redisKey));
        }

        return list;
    }

    public void close() {
        jedisPool.close();
    }

    private String serialize(Object obj) {
        return gson.toJson(obj);
    }

    private <T> T deserialize(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}
