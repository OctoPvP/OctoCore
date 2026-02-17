package net.octopvp.octocore.velocity.redis.packet.impl;

import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.Player;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.ServerData;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.velocity.OctoCoreVelocity;

@AllArgsConstructor
@NoArgsConstructor
public class ServerOfflinePacket extends RedisPacket {
    private String server;

    @Override
    public void onReceive(JsonObject data) {
        if (server.equalsIgnoreCase(OctoCoreCommon.getInstance().getServerName())) {
            return;
        }
        ServerData sd = OctoCoreCommon.getInstance().getServerManager().getServerData(server);
        if (sd != null) {
            sd.setSafelyStopped(true);
        }
        String message = CC.RED + CC.B + "Admin " + CC.D_GRAY + CC.ARROW_RIGHT + CC.WHITE + " " + CC.RED + "Server " + server + " is now offline.";
        for (Player player : OctoCoreVelocity.getInstance().getProxyServer().getAllPlayers()) {
            if (player.hasPermission(Permissions.RECEIVE_SERVER_OFFLINE_MESSAGE)) {
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(message));
            }
        }
    }
}
