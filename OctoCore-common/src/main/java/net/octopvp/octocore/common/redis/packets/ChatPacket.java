package net.octopvp.octocore.common.redis.packets;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;

import java.util.UUID;

@Getter
@Setter
public abstract class ChatPacket extends RedisPacket {
    protected String name, server, message, coloredName;
    protected UUID uuid;
    protected long timestamp = System.currentTimeMillis();
    protected boolean web = false;
    protected String webProfilePic = "";

    public ChatPacket(String name, String coloredName, String server, String message, UUID uuid, long timestamp) {
        this.name = name;
        this.coloredName = coloredName;
        this.server = server;
        this.message = message;
        this.uuid = uuid;
        this.timestamp = timestamp;
    }

    public ChatPacket() {
    }
}
