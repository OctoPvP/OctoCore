package net.octopvp.octocore.paper.database.redis.packets.server;

import com.google.gson.JsonObject;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;

@AllArgsConstructor
@NoArgsConstructor
public class GlobalCommandPacket extends RedisPacket {
    private String command;
    @Override
    public void onReceive(JsonObject data) throws Exception {
        String command = data.get("command").getAsString();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),command);
     }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder().addProperty("command",command);
    }

    @Override
    public String getName() {
        return "GlobalCommandPacket";
    }
}
