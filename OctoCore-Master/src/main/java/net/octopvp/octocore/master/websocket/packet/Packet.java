package net.octopvp.aetheriacoremaster.websocket.packet;

import com.google.gson.JsonElement;
import net.octopvp.aetheriacore.common.AetheriaCoreCommon;

public abstract class Packet {
    public abstract PacketDirection getDirection();

    public JsonElement serialize() {
        return AetheriaCoreCommon.getInstance().getGson().toJsonTree(this);
    }
}
