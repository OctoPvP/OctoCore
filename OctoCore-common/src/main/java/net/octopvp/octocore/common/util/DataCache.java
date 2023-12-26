package net.octopvp.octocore.common.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.redis.RedisManager;
import org.bson.Document;
import redis.clients.jedis.Jedis;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class DataCache {
    private final UUID uuid;

    public Document getData() {
        if (!OctoCoreCommon.getInstance().getRedisManager().isConnected() || Boolean.getBoolean("octocore.disablecache")) return null;
        try (Jedis jedis = RedisManager.getJedis()) {
            String json = jedis.hget("player-data", this.uuid.toString());
            if (json == null) return null;
            return Document.parse(json);
        } catch (Exception e) {
            return null;
        }
    }

    public void update(Document document) {
        if (!OctoCoreCommon.getInstance().getRedisManager().isConnected() || Boolean.getBoolean("octocore.disablecache")) return;

        try (Jedis jedis = RedisManager.getJedis()) {
            jedis.hset("player-data", this.uuid.toString(), document.toJson());
            // we're on keydb, call EXPIREMEMBER
            // EXPIREMEMBER player-data 5bd217f6-b89a-4064-a7f9-11733e8baafa 2
            // use eval(), it is a lua script, for example eval "return redis.call('SET', 1, 2)" 0
            jedis.eval("return redis.call('EXPIREMEMBER', 'player-data', '" + this.uuid + "', 60)", 0); // expire in 60 seconds
            // calling EXPIREMEMBER has a O(log N) time complexity according to the keydb blog - https://docs.keydb.dev/blog/2021/06/08/blog-post/
            // I want to figure out a way to reduce the amount of calls
        } catch (Exception e) {
            if (OctoCoreCommon.getInstance().getRedisManager().isConnected() && System.currentTimeMillis() - OctoCoreCommon.getInstance().getRedisManager().getLastConnect() >= 3000L) {
                e.printStackTrace();
                Logger.error("Failed to cache data for " + uuid + ": " + e.getMessage());
            }
        }
    }
}
