package net.octopvp.octocore.paper.database.redis.packets.player;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.utils.chat.Clickable;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class ExecuteUnblacklistPacket extends RedisPacket {
    private JsonBuilder builder;

    @Override
    public void onReceive(JsonObject data) throws Exception {
        String sender = data.get("sender").getAsString();
        String target = data.get("target").getAsString();
        String reason = data.get("reason").getAsString();
        boolean silent = data.get("silent").getAsBoolean(), coloredNameEnabled = data.has("coloredName");
        String coloredName = sender;
        if (coloredNameEnabled)
            coloredName = data.get("coloredName").getAsString() + sender;

        Clickable clickable = new Clickable((silent ? Lang.PUNISHMENT_SILENT.toString() : "") + Lang.PUNISHMENT_UNDO.getMsg(
                target,
                "blacklisted",
                coloredName,
                reason
        )/*,Lang.PUNISHMENT_UNMUTE_HOVER.getMsg(reason)*/);

        Bukkit.getConsoleSender().sendMessage(CC.translate(clickable.getText()));

        if (silent) {
            for (Player player : Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission(Permission.PUNISHMENT_SEE_SILENT.getNode())).collect(Collectors.toList())) {
                clickable.sendToPlayer(player);
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
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
        return "ExecuteUnblacklistPacket";
    }
}
