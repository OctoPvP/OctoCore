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
public class StaffAlertPacket extends RedisPacket {
    private String message;

    @Override
    public void onReceive(JsonObject data) {
        String msg = Lang.STAFF_ALERTS.getMsg(message);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission(Permissions.STAFF_ALERT)) {
                onlinePlayer.sendMessage(msg);
            }
        }
    }
}
