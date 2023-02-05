package net.octopvp.octocore.master.master.redis.impl.staff;

import com.google.gson.JsonObject;
import net.octopvp.octocore.common.redis.packets.ChatPacket;
import net.octopvp.octocore.master.master.manager.StaffChatModule;

import java.util.UUID;

public abstract class MasterChatPacket extends ChatPacket {
    public MasterChatPacket(String name, String coloredName, String server, String message, UUID uuid, long timestamp) {
        super(name, coloredName, server, message, uuid, timestamp);
    }
    public MasterChatPacket() {super();}
    @Override
    public void onReceive(JsonObject data) {
        StaffChatModule.getInstance().onMessageSent(this);
    }
}
