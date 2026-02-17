package net.octopvp.octocore.velocity.redis.packet.impl.staff;

import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.Player;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.velocity.OctoCoreVelocity;

@AllArgsConstructor
@NoArgsConstructor
public class StaffConnectPacket extends RedisPacket {
    private String name, server;
    private int vanishPriority;
    private boolean vanished;

    @Override
    public void onReceive(JsonObject data) {
        String message = CC.GOLD + CC.B + "Staff " + CC.D_GRAY + CC.ARROW_RIGHT + CC.WHITE + " " + CC.GREEN + name + CC.translate(" &ajoined the network to ") + server;
        for (Player player : OctoCoreVelocity.getInstance().getProxyServer().getAllPlayers()) {
            if (player.hasPermission(Permissions.RECEIVE_JOIN_MESSAGE)) {
                if (vanished) continue; 
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(message));
            }
        }
    }
}
