package net.octopvp.octocore.paper.database.redis.packets.player;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.DisconnectReason;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
public class ExecuteAltKickPacket extends RedisPacket {
    private JsonBuilder jsonBuilder;

    @Override
    public void onReceive(JsonObject data) throws Exception {
        String name = data.get("name").getAsString();
        String sender = data.get("sender").getAsString();
        String reason = data.get("reason").getAsString();
        String niceDuration = data.get("duration").getAsString();
        boolean permanent = data.get("permanent").getAsBoolean();
        String type = data.get("type").getAsString();
        String alt = data.get("alt").getAsString();
        String expire = data.get("expire").getAsString();

        Tasks.runSync(() -> {
            Player target = Bukkit.getPlayer(name);
            if (target != null) {
                if (type.equalsIgnoreCase("BAN")) {
                    new DisconnectReason(
                            Lang.PUNISH_KICK_MESSAGE.getMsg(
                                    (!permanent ? Lang.TEMP : ""),
                                    "BANNED",
                                    "Banned",
                                    sender,
                                    reason,
                                    (!permanent ? Lang.PUNISH_KICK_TEMP_ENTRY.getMsg(
                                            expire, niceDuration) : Lang.PERM_ENTRY),
                                    true)).toString();
                } else if (type.equalsIgnoreCase("BLACKLIST")) {

                }
            }
        });
    }

    @Override
    public JsonBuilder getData() {
        return jsonBuilder;
    }

    @Override
    public String getName() {
        return "ExecuteAltKickPacket";
    }
}
