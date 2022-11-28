package net.octopvp.octocore.core.database.redis.packets.staff;

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
public class StaffLeavePacket extends RedisPacket {
    private String name, server;

    @Override
    public void onReceive(JsonObject data) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission(Permissions.RECEIVE_JOIN_MESSAGE)) {
                onlinePlayer.sendMessage(Lang.STAFF_ALERTS.getMsg(Lang.STAFF_LEAVE_ALERT_FORMAT.getMsg(name, server)));
            }
        }
    }
}
