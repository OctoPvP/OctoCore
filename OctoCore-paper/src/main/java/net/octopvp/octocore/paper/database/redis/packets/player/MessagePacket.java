package net.octopvp.octocore.paper.database.redis.packets.player;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.SoundUtil;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class MessagePacket extends RedisPacket {
    private String message, from, to;
    private UUID fromId, toId;

    @Override
    public void onReceive(JsonObject data) {
        message = data.get("message").getAsString();
        from = data.get("from").getAsString();
        to = data.get("to").getAsString();
        fromId = UUID.fromString(data.get("fromId").getAsString());
        toId = UUID.fromString(data.get("toId").getAsString());

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(fromId)) { // sender
                player.sendMessage(Lang.MSG_PLAYER_TO.getMsg(to, message));
                PlayerData d = PlayerManager.getInstance().getData(player);
                if (d != null) {
                    d.setLastMessaged(toId);
                }
                continue;
            }
            if (player.getUniqueId().equals(toId)) { // receiver
                player.sendMessage(Lang.MSG_PLAYER.getMsg(from, message));
                PlayerData d = PlayerManager.getInstance().getData(player);
                if (d != null) {
                    if (d.getMessageSettings().isSoundsEnabled()) {
                        SoundUtil.playPing(player);
                    }
                    d.setLastMessaged(fromId);
                }
                continue;
            }

            if (player.hasPermission(Permissions.SOCIAL_SPY)) {
                PlayerData d = PlayerManager.getInstance().getData(player.getUniqueId());
                if (d.isSocialSpy()) {
                    player.sendMessage(Lang.SOCIAL_SPY.getMsg(from, to, message));
                }
            }
        }
    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder()
                .add("message", message)
                .add("from", from)
                .add("to", to)
                .add("fromId", fromId.toString())
                .add("toId", toId.toString());
    }

    @Override
    public String getName() {
        return "MessagePacket";
    }
}
