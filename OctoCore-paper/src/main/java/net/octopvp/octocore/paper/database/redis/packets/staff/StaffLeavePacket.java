package net.octopvp.octocore.paper.database.redis.packets.staff;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
public class StaffLeavePacket extends RedisPacket {
    private String name, server;

    @Override
    public void onReceive(JsonObject data) throws Exception {
        String name = data.get("name").getAsString();
        String server = data.get("server").getAsString();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission(Permissions.RECEIVE_JOIN_MESSAGE.getNode())) {
                onlinePlayer.sendMessage(Lang.STAFF_ALERTS.getMsg(Lang.STAFF_LEAVE_ALERT_FORMAT.getMsg(name, server)));
            }
        }
    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder()
                .addProperty("name", name)
                .addProperty("server", server);
    }

    @Override
    public String getName() {
        return "StaffLeavePacket";
    }
}
