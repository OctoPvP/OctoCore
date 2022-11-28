package net.octopvp.octocore.core.database.redis.packets.server;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
public class ServerOnlinePacket extends RedisPacket {
    private String server;

    @Override
    public void onReceive(JsonObject data) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission(Permissions.RECEIVE_SERVER_ONLINE_MESSAGE)) {
                onlinePlayer.sendMessage(Lang.ADMIN_ALERTS.getMsg(Lang.SERVER_ONLINE_FORMAT.getMsg(server)));
            }
        }
    }
}
