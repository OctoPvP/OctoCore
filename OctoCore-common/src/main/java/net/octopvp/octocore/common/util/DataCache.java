package net.octopvp.octocore.common.util;

import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.redis.RedisManager;
import org.bson.Document;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.UUID;

public class DataCache {

    public static Document getData(UUID uuid) {
        if (!OctoCoreCommon.getInstance().getRedisManager().isConnected() || Boolean.getBoolean("octocore.disablecache"))
            return null;
        try (Jedis jedis = RedisManager.getJedis()) {
            String json = jedis.get("pdata:" + uuid.toString());
            if (json == null) return null;
            return Document.parse(json);
        } catch (Exception e) {
            return null;
        }
    }

    public static void update(Document document, UUID uuid) {
        if (!OctoCoreCommon.getInstance().getRedisManager().isConnected() || Boolean.getBoolean("octocore.disablecache"))
            return;

        try (Jedis jedis = RedisManager.getJedis()) {
            jedis.set("pdata:" + uuid.toString(), document.toJson(), SetParams.setParams().ex(60));
        } catch (Exception e) {
            if (OctoCoreCommon.getInstance().getRedisManager().isConnected() && System.currentTimeMillis() - OctoCoreCommon.getInstance().getRedisManager().getLastConnect() >= 3000L) {
                e.printStackTrace();
                Logger.error("Failed to cache data for " + uuid + ": " + e.getMessage());
            }
        }
    }

    public static void purge(UUID uuid) {
        try (Jedis jedis = RedisManager.getJedis()) {
            jedis.del("pdata:" + uuid.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
