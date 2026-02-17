package net.octopvp.octocore.velocity.redis.packet.impl.staff.chat;

import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.redis.packets.ChatPacket;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.velocity.OctoCoreVelocity;

import java.util.UUID;

public class StaffChatPacket extends ChatPacket {
    public StaffChatPacket(String name, String coloredName, String server, String message, UUID uuid) {
        super(name, coloredName, server, message, uuid, System.currentTimeMillis());
    }

    public StaffChatPacket() {
    }

    @Override
    public void onReceive(JsonObject data) {
        String msg = CC.GOLD + CC.B + "StaffChat " + CC.D_GRAY + CC.ARROW_RIGHT + CC.GRAY + " " + coloredName + " " + CC.GRAY + "(" + server + ")" + CC.WHITE + ": " + message;
        for (Player player : OctoCoreVelocity.getInstance().getProxyServer().getAllPlayers()) {
            if (player.hasPermission(Permissions.STAFFCHAT)) {
                player.sendMessage(LegacyComponentSerializer.legacySection().deserialize(msg));
            }
        }
    }
}
