package net.octopvp.octocore.core.database.redis.packets.staff;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
public class StaffVanishStateUpdatePacket extends RedisPacket {
    private String player;
    private boolean vanished;
    private int vanishPriority;

    @Override
    public void onReceive(JsonObject data) {
        String msg = Lang.STAFF_ALERTS.getMsg(player + CC.GREEN + (vanished ? " vanished with priority " + CC.YELLOW + vanishPriority : " unvanished"));
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission(Permissions.VANISH)) {
                PlayerData playerData = PlayerManager.getInstance().getData(onlinePlayer);
                if (playerData == null || playerData.getVanishPriority() < vanishPriority) {
                    continue;
                }
                onlinePlayer.sendMessage(msg);
            }
        }
    }
}
