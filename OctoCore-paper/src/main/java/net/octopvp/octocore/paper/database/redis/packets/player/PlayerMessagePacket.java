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
        Player player = Bukkit.getPlayer(data.get("name").getAsString());
        if (player != null) {
            player.sendMessage(data.get("message").getAsString());
        }
    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder().addProperty("name", name).addProperty("message", message);
    }

    @Override
    public String getName() {
        return "PlayerMessagePacket";
    }
}
