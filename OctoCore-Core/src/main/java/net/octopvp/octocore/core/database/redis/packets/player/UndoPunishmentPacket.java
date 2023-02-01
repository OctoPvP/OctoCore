package net.octopvp.octocore.core.database.redis.packets.player;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.utils.chat.Clickable;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
public class UndoPunishmentPacket extends RedisPacket {

    private PunishmentType type;
    //private String senderDisplay;
    private String coloredName;
    //private String sender;
    private String target, reason;
    private boolean silent;

    @Override
    public void onReceive(JsonObject data) {
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

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(target);

        String userName = PlayerManager.getInstance().getFormattedName(offlinePlayer.getName());
        Clickable clickable = new Clickable((silent ? Lang.PUNISHMENT_SILENT.toString() : "") + Lang.PUNISHMENT_UNDO.getMsg(
                userName,
                t,
                coloredName,
                reason
        )/*,Lang.PUNISHMENT_UNMUTE_HOVER.getMsg(reason)*/);
        Bukkit.getConsoleSender().sendMessage(CC.translate(clickable.getText()));

        if (silent) {
            for (Player player : Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission(Permissions.PUNISHMENT_SEE_SILENT)).collect(Collectors.toList())) {
                String currentMessage = clickable.getText();
                Clickable click = new Clickable(currentMessage, CC.translate("&aReason&7: &f" + reason.trim()), null);
                click.sendToPlayer(player);
            }
        } else {
            String reason = this.reason
                    .replace("--s", "")
                    .replace("--silent", "")
                    .replace("-s", "")
                    .replace("-silent", "")
                    .trim();
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

        Player target = Bukkit.getPlayer(this.target);
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
}
