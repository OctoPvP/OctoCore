package net.octopvp.octocore.paper.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.OctoCore;
import org.bson.Document;
import redis.clients.jedis.Jedis;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class CachedData {

    private final UUID uuid;

    public Document getData() {
        if (!OctoCore.getInstance().getRedisHandler().isRedisConnected()) return null;

        try (Jedis jedis = OctoCore.getInstance().getRedisHandler().getSubscriberPool().getResource()) {
            String json = jedis.hget("player-data", this.uuid.toString());

            if (json == null) {
                return null;
            }
            return Document.parse(json);
        } catch (Exception e) {
            return null;
        }
    }

    public void update(Document document) {
        if (!OctoCore.getInstance().getRedisHandler().isRedisConnected()) return;

        try (Jedis jedis = OctoCore.getInstance().getRedisHandler().getSubscriberPool().getResource()) {
            jedis.hset("player-data", this.uuid.toString(), document.toJson());
            jedis.expire("player-data", 3600);
        } catch (Exception e) {
            if (OctoCore.getInstance().getRedisHandler().isRedisConnected() && System.currentTimeMillis() - OctoCore.getInstance().getRedisHandler().getLastConnect() >= 3000L) {
                e.printStackTrace();
                Logger.error("Failed to cache data for " + uuid + ": " + e.getMessage());
            }
        }
    }
}
