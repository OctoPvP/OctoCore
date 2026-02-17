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
public class StaffSwitchPacket extends RedisPacket {
    private String name, from, to;

    @Override
    public void onReceive(JsonObject data) {
        String message = CC.GOLD + CC.B + "Staff " + CC.D_GRAY + CC.ARROW_RIGHT + CC.WHITE + " " + CC.YELLOW + name + " switched from " + from + " to " + to;
        for (Player player : OctoCoreVelocity.getInstance().getProxyServer().getAllPlayers()) {
            if (player.hasPermission(Permissions.RECEIVE_JOIN_MESSAGE)) {
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(message));
            }
        }
    }
}
