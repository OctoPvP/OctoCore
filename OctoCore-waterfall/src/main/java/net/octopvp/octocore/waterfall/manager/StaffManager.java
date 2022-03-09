package net.octopvp.octocore.waterfall.manager;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.octopvp.octocore.waterfall.redis.packet.impl.staff.StaffConnectPacket;
import net.octopvp.octocore.waterfall.redis.packet.impl.staff.StaffLeavePacket;
import net.octopvp.octocore.waterfall.redis.packet.impl.staff.StaffSwitchPacket;

public class StaffManager {
    public static void join(ServerInfo serverInfo, ProxiedPlayer player) {
        new StaffConnectPacket(player.getName(), serverInfo.getName()).send();
    }

    public static void sendSwitch(ServerInfo serverInfo, ProxiedPlayer player) {
        new StaffSwitchPacket(player.getName(), serverInfo.getName(), player.getServer().getInfo().getName()).send();
    }

    public static void leave(ServerInfo serverInfo, ProxiedPlayer player) {
        new StaffLeavePacket(player.getName(), serverInfo.getName());
    }
}
