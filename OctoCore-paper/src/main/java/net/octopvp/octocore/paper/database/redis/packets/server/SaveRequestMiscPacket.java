package net.octopvp.octocore.paper.database.redis.packets.server;

import com.google.gson.JsonObject;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class SaveRequestMiscPacket extends RedisPacket {
    private JsonBuilder jsonBuilder;
    @Override
    public void onReceive(JsonObject data) throws Exception {
        if (data.has("uuid")) {
            String id = data.get("uuid").getAsString();

            UUID uuid = UUID.fromString(id);
            if (Bukkit.getPlayer(uuid) != null) {
                PlayerManager.getData(uuid).save();
            }
        } else {
            String name = data.get("name").getAsString();
            Player player = Bukkit.getPlayer(name);
            if (player != null) {
                PlayerManager.getData(player).save();
            }
        }
    }

    @Override
    public JsonBuilder getData() {
        return jsonBuilder;
    }

    @Override
    public String getName() {
        return "SaveRequestMiscPacket";
    }
}
