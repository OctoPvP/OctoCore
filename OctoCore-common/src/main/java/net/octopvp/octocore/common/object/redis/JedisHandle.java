package net.octopvp.octocore.common.object.redis;

import com.google.gson.JsonObject;

public interface JedisHandle {
    void handleMessage(JsonObject json);
}
