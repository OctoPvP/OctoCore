package net.octopvp.octocore.paper.database.redis;

import net.octopvp.octocore.common.redis.Packets;
import net.octopvp.octocore.paper.database.redis.packets.server.ServerUpdatePacket;
import net.octopvp.octocore.paper.database.redis.packets.staff.AdminAlertPacket;

public class RedisPackets extends Packets {
    private ServerUpdatePacket serverUpdatePacket;
    private AdminAlertPacket adminAlertPacket;
}
