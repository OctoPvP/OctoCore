package net.octopvp.octocore.paper.database.redis.packets.server;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
public class ServerOnlinePacket extends RedisPacket {
    private String server;

    @Override
    public void onReceive(JsonObject data) throws Exception {
        String server = data.get("server").getAsString();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission(Permission.RECEIVE_SERVER_ONLINE_MESSAGE.getNode())) {
                onlinePlayer.sendMessage(Lang.ADMIN_ALERTS.getMsg(Lang.SERVER_ONLINE_FORMAT.getMsg(server)));
            }
        }
    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder().addProperty("server", server);
    }

    @Override
    public String getName() {
        return "ServerOnlinePacket";
    }
}
