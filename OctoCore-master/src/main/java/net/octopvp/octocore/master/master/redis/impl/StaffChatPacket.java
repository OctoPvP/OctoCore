package net.octopvp.octocore.master.master.redis.impl;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.badbird5907.lightning.event.Event;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.master.master.manager.StaffChatModule;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StaffChatPacket extends RedisPacket implements Event {
    private String name, server, message;
    private UUID uuid;
    private long timestamp = System.currentTimeMillis();

    @Override
    public void onReceive(JsonObject data) {
        StaffChatModule.getInstance().onMessageSent(this);
    }
}
