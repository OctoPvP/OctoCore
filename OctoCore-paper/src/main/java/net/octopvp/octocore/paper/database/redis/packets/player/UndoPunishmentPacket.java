package net.octopvp.octocore.paper.database.redis.packets.player;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.utils.chat.Clickable;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
public class UndoPunishmentPacket extends RedisPacket {

    private PunishmentType type;
    private String senderDisplay, coloredName, sender, target, reason;
    private boolean silent;

    @Override
    public void onReceive(JsonObject data) {
        this.type = PunishmentType.valueOf(data.get("type").getAsString());
        String t;
        if (this.type == PunishmentType.BAN) {
            t = "banned";
        } else if (this.type == PunishmentType.MUTE) {
            t = "muted";
        } else if (this.type == PunishmentType.BLACKLIST) {
            t = "blacklisted";
        } else {
            t = "punished";
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(data.get("target").getAsString());

        String userName = PlayerManager.getInstance().getFormattedName(offlinePlayer.getName());
        String coloredName = data.get("coloredName").getAsString();
        String reason0 = data.get("reason").getAsString();
        Clickable clickable = new Clickable((silent ? Lang.PUNISHMENT_SILENT.toString() : "") + Lang.PUNISHMENT_UNDO.getMsg(
                userName,
                t,
                coloredName,
                reason0
        )/*,Lang.PUNISHMENT_UNMUTE_HOVER.getMsg(reason)*/);
        Bukkit.getConsoleSender().sendMessage(CC.translate(clickable.getText()));

        if (data.get("silent").getAsBoolean()) {
            for (Player player : Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission(Permissions.PUNISHMENT_SEE_SILENT)).collect(Collectors.toList())) {
                String reason = data.get("reason").getAsString().trim();
                String currentMessage = clickable.getText();
                Clickable click = new Clickable(currentMessage, CC.translate("&aReason&7: &f" + reason), null);
                click.sendToPlayer(player);
            }
        } else {
            String reason = data.get("reason").getAsString()
                    .replace("-s", "")
                    .replace("-silent", "")
                    .replace("-c", "")
                    .replace("-clear", "").trim();
            String currentMessage = clickable.getText();
            Clickable click = new Clickable(currentMessage, CC.translate("&aReason&7: &f" + reason), null);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission(Permissions.PUNISHMENT_SEE_SILENT)) {
                    click.sendToPlayer(player);
                } else {
                    clickable.sendToPlayer(player);
                }
            }
        }

        Player target = Bukkit.getPlayer(data.get("target").getAsString());
        if (target != null && (type == PunishmentType.WARN || type == PunishmentType.MUTE)) {
            String punishType = "";
            switch (type) {
                case MUTE:
                    punishType = "unmuted";
                    break;
                case WARN:
                    punishType = "unwarned";
                    break;
            }
            target.sendMessage(CC.translate("&bYou have been &a" + punishType + "&b."));
        }
    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder()
                .addProperty("type", this.type.toString())
                .addProperty("senderDisplay", this.senderDisplay)
                .addProperty("coloredName", this.coloredName)
                .addProperty("sender", this.sender)
                .addProperty("target", this.target)
                .addProperty("reason", this.reason)
                .addProperty("silent", this.silent);
    }

    @Override
    public String getName() {
        return "UndoPunishmentPacket";
    }
}
