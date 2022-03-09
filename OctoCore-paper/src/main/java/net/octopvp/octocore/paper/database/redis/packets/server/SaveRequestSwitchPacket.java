package net.octopvp.octocore.paper.database.redis.packets.server;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.listeners.redis.MainRedisHandler;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class SaveRequestSwitchPacket extends RedisPacket {
    private UUID uuid;

    @Override
    public void onReceive(JsonObject data) throws Exception {
        String id = data.get("uuid").getAsString();
        UUID uuid = UUID.fromString(id);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            MainRedisHandler.getSaving().add(player.getUniqueId());
            //JoinLeaveListener.freezePlayer(player);
            Tasks.runAsyncLater(() -> {
                if (Bukkit.getPlayer(uuid) != null) {
                    //JoinLeaveListener.unfreezePlayer(player);
                    player.sendMessage(CC.RED + "Could not send you to that server!");
                }
            }, 100);
            PlayerManager.processLeave(player);
        }
    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder()
                .add("uuid", uuid.toString());
    }

    @Override
    public String getName() {
        return "SaveRequestSwitchPacket";
    }
}
