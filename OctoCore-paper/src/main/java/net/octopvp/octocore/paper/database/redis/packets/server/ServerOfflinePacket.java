package net.octopvp.octocore.paper.database.redis.packets.server;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
public class ServerOfflinePacket extends RedisPacket {
    private String server;

    @Override
    public void onReceive(JsonObject data) {
        String server = data.get("server").getAsString();
        OctoCore.getInstance().getServerManager().getServerData(server).setSafelyStopped(true); //so master dosen't send the crash alert
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission(Permissions.RECEIVE_SERVER_OFFLINE_MESSAGE)) {
                onlinePlayer.sendMessage(Lang.ADMIN_ALERTS.getMsg(Lang.SERVER_OFFLINE_FORMAT.getMsg(server)));
            }
        }
    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder().addProperty("server", server);
    }

    @Override
    public String getName() {
        return "ServerOfflinePacket";
    }
}
