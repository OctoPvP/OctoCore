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
public class AdminAlertPacket extends RedisPacket {
    private String message;

    @Override
    public void onReceive(JsonObject data) throws Exception {
        String m = data.get("message").getAsString();
        String msg = Lang.ADMIN_ALERTS.getMsg(m);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission(Permissions.ADMIN_ALERT)) {
                onlinePlayer.sendMessage(msg);
            }
        }
    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder()
                .addProperty("message", message);
    }

    @Override
    public String getName() {
        return "AdminAlertPacket";
    }
}
