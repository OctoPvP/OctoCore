package net.octopvp.octocore.paper.database.redis.packets.server;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import org.bukkit.Bukkit;

@AllArgsConstructor
@NoArgsConstructor
public class GlobalBroadcastPacket extends RedisPacket {
    private String message;

    @Override
    public void onReceive(JsonObject data) {
        String message = CC.translate(data.get("message").getAsString());
        Bukkit.broadcastMessage(message);
    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder()
                .add("message", message);
    }

    @Override
    public String getName() {
        return "GlobalBroadcastPacket";
    }
}
