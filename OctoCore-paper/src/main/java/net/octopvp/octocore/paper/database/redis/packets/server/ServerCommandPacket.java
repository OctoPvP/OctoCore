package net.octopvp.octocore.paper.database.redis.packets.server;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Bukkit;

@AllArgsConstructor
@NoArgsConstructor
public class ServerCommandPacket extends RedisPacket {
    private String server, command;

    @Override
    public void onReceive(JsonObject data) {
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        if (OctoCore.getServerName().equalsIgnoreCase(server)) {
            Bukkit.getConsoleSender().sendMessage(Lang.EXECUTING_REQUESTED_COMMAND.getMsg(command, data.get("sender").getAsString()));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }
}
