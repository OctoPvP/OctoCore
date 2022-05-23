package net.octopvp.octocore.paper.database.redis.packets.staff;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.utils.chat.Clickable;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
public class PunishedJoinPacket extends RedisPacket {
    private JsonBuilder builder;

    @Override
    public void onReceive(JsonObject data) throws Exception {
        String type = data.get("type").getAsString(),
                name = data.get("name").getAsString();
        Clickable clickable;
        if (data.has("more") && data.get("more").getAsBoolean()) {
            String expire = data.get("expires").getAsString(),
                    addedBy = data.get("addedByName").getAsString();
            clickable = new Clickable(Lang.PUNISH_JOIN_ALERT.getMsg(name, type), Lang.PUNISH_JOIN_ALERT_HOVER.getMsg(expire, addedBy), "/history " + name);
        } else clickable = new Clickable(Lang.PUNISH_JOIN_ALERT.getMsg(name, type));
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(Permissions.PUNISHMENT_SEE_JOIN_ALERT.getNode())) {
                clickable.sendToPlayer(player);
            }
        }
    }

    @Override
    public JsonBuilder getData() {
        return builder;
    }

    @Override
    public String getName() {
        return "PunishedJoinPacket";
    }
}
