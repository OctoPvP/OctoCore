package net.octopvp.octocore.paper.database.redis.packets.staff;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
public class StaffSwitchPacket extends RedisPacket {
    private String name, from, to;

    @Override
    public void onReceive(JsonObject data) {
        String name = data.get("name").getAsString();
        String to = data.get("to").getAsString();
        String from = data.get("from").getAsString();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission(Permissions.RECEIVE_JOIN_MESSAGE)) {
                onlinePlayer.sendMessage(Lang.STAFF_ALERTS.getMsg(Lang.STAFF_SWITCH_ALERT_FORMAT.getMsg(name, from, to)));
            }
        }
    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder()
                .addProperty("name", name)
                .addProperty("to", to)
                .addProperty("from", from);
    }

    @Override
    public String getName() {
        return "StaffSwitchPacket";
    }
}
