package net.octopvp.octocore.core.database.redis.packets.server;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class SaveRequestSwitchPacket extends RedisPacket {
    private UUID uuid;

    @Override
    public void onReceive(JsonObject data) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            PlayerManager.getSaving().add(player.getUniqueId());
            //JoinLeaveListener.freezePlayer(player);
            Tasks.runAsyncLater(() -> {
                if (Bukkit.getPlayer(uuid) != null) {
                    //JoinLeaveListener.unfreezePlayer(player);
                    player.sendMessage(CC.RED + "Could not send you to that server!");
                }
            }, 100);
        }
    }
}
