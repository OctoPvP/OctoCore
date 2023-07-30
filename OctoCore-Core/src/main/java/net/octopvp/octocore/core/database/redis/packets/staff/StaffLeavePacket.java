package net.octopvp.octocore.core.database.redis.packets.staff;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.manager.impl.VanishManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
public class StaffLeavePacket extends RedisPacket {
    private String name, server;
    private int vanishPriority;
    private boolean vanished;

    @Override
    public void onReceive(JsonObject data) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission(Permissions.RECEIVE_JOIN_MESSAGE)) {
                PlayerData playerData = PlayerManager.getInstance().getData(onlinePlayer);
                int playerPriority = VanishManager.getInstance().getVanishPriority(playerData, true);
                if (vanished && playerPriority < vanishPriority) continue;
                onlinePlayer.sendMessage(Lang.STAFF_ALERTS.getMsg(Lang.STAFF_LEAVE_ALERT_FORMAT.getMsg(name, server)));
            }
        }
    }
}
