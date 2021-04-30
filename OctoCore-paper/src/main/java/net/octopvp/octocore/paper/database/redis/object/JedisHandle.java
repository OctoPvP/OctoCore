package net.octopvp.octocore.paper.database.redis.object;

import com.google.gson.JsonObject;

public interface JedisHandle {
    void handleMessage(JsonObject json);
}
