package net.octopvp.octocore.master.master.redis.impl.staff;

import net.octopvp.octocore.common.redis.packets.ChatPacket;

import java.util.UUID;

public class StaffChatPacket extends MasterChatPacket {
    public StaffChatPacket(String name, String coloredName, String server, String message, UUID uuid, long timestamp) {
        super(name, coloredName, server, message, uuid, timestamp);
    }
    public StaffChatPacket() {super();}
}
