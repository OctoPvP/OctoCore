package net.octopvp.octocore.common.object.redis.packet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.octopvp.aetheriacore.common.AetheriaCoreCommon;

public abstract class RedisPacket {

    public abstract void onReceive(JsonObject data);

    public abstract String getType();

    public JsonObject serialize() {
        Gson gson = AetheriaCoreCommon.getInstance().getGson();
        JsonObject json = new JsonObject();
        json.addProperty("type", getType());
        json.add("data", gson.toJsonTree(this));
        return json;
    }

    public void send() {
        AetheriaCoreCommon.getInstance().getRedisManager().write(this);
    }
}
