package net.octopvp.octocore.common.redis;

import com.google.gson.JsonObject;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.util.json.JsonBuilder;

public abstract class RedisPacket {
    public abstract void onReceive(JsonObject data) throws Exception;

    public abstract JsonBuilder getData();

    public abstract String getName();

    public void send() {
        OctoCoreCommon.getRedisHandler().sendRequest(this, false);
    }

    public void send(boolean justHere) {
        OctoCoreCommon.getRedisHandler().sendRequest(this, justHere);
    }
}
