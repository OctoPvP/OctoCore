package net.octopvp.octocore.paper.database.redis;

import net.octopvp.octocore.common.redis.Packets;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.paper.database.redis.packets.other.DiscordMessagePacket;
import net.octopvp.octocore.paper.database.redis.packets.other.GrantsUpdatePacket;
import net.octopvp.octocore.paper.database.redis.packets.other.ReloadRanksPacket;
import net.octopvp.octocore.paper.database.redis.packets.server.ServerUpdatePacket;
import net.octopvp.octocore.paper.database.redis.packets.staff.AdminAlertPacket;

public class RedisPackets extends Packets {
    private DiscordMessagePacket discordMessagePacket;
    private GrantsUpdatePacket grantsUpdatePacket;
    private ReloadRanksPacket reloadRanksPacket;
}
