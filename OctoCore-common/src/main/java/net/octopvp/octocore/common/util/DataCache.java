package net.octopvp.octocore.common.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.OctoCoreCommon;
import org.bson.Document;
import redis.clients.jedis.Jedis;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class DataCache {
    private final UUID uuid;

    public Document getData() {
        if (!OctoCoreCommon.getInstance().getRedisManager().isConnected()) return null;
        try (Jedis jedis = OctoCoreCommon.getInstance().getRedisManager().getPool().getResource()) {
            String json = jedis.hget("player-data", this.uuid.toString());
            if (json == null) return null;
            return Document.parse(json);
        } catch (Exception e) {
            return null;
        }
    }

    public void update(Document document) {
        if (!OctoCoreCommon.getInstance().getRedisManager().isConnected()) return;

        try (Jedis jedis = OctoCoreCommon.getInstance().getRedisManager().getPool().getResource()) {
            jedis.hset("player-data", this.uuid.toString(), document.toJson());
            jedis.expire("player-data", 3600);
        } catch (Exception e) {
            if (OctoCoreCommon.getInstance().getRedisManager().isConnected() && System.currentTimeMillis() - OctoCoreCommon.getInstance().getRedisManager().getLastConnect() >= 3000L) {
                e.printStackTrace();
                Logger.error("Failed to cache data for " + uuid + ": " + e.getMessage());
            }
        }
    }
}
