package net.octopvp.octocore.common.object.redis.packet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.StringUtils;

public abstract class RedisPacket {

    public abstract void onReceive(JsonObject data);

    public String getType() {
        return StringUtils.classNameToPacketName(getClass().getSimpleName());
    }

    public JsonObject serialize() {
        Gson gson = OctoCoreCommon.getInstance().getGson();
        JsonObject json = new JsonObject();
        json.addProperty("type", getType());
        json.add("data", gson.toJsonTree(this));
        return json;
    }

    public void send() {
        OctoCoreCommon.getInstance().getRedisManager().write(this);
    }
}
