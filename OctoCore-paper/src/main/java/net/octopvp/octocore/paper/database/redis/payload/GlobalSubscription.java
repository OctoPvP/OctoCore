package net.octopvp.octocore.paper.database.redis.payload;

import com.google.gson.JsonObject;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.common.object.redis.JedisHandle;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.RedisListenerManager;

public class GlobalSubscription implements JedisHandle {
    //TODO bungeecord fallback

    private static final OctoCore plugin = OctoCore.getInstance();

    @Override
    public void handleMessage(JsonObject object) {
        JedisAction payload;
        try {
            payload = JedisAction.valueOf(object.get("payload").getAsString());
        } catch (IllegalArgumentException ignored) {
            return;
        }
        JsonObject data = object.get("data").getAsJsonObject();
        if (data.isJsonNull()) {
            Logger.error("Received null data from redis");
            return;
        }
        RedisListenerManager.handleMessage(payload, data);

    }
}
