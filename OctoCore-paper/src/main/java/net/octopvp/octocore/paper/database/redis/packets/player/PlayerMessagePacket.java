package net.octopvp.octocore.paper.database.redis.packets.player;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
public class PlayerMessagePacket extends RedisPacket {
    private String name, message;

    @Override
    public void onReceive(JsonObject data) {
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            player.sendMessage(message);
        }
    }
}
