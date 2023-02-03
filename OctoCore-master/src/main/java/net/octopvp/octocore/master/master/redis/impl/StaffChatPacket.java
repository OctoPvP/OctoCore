package net.octopvp.octocore.master.master.redis.impl;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.badbird5907.lightning.event.Event;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.master.master.manager.StaffChatModule;

import java.util.UUID;

@Getter
@Setter
public class StaffChatPacket extends RedisPacket implements Event {
    private String name, server, message;
    private UUID uuid;
    private long timestamp = System.currentTimeMillis();
    private boolean web = false;
    private String webProfilePic = "";

    public StaffChatPacket(String name, String server, String message, UUID uuid, long timestamp) {
        this.name = name;
        this.server = server;
        this.message = message;
        this.uuid = uuid;
        this.timestamp = timestamp;
    }

    public StaffChatPacket() {
    }

    @Override
    public void onReceive(JsonObject data) {
        StaffChatModule.getInstance().onMessageSent(this);
    }
}
