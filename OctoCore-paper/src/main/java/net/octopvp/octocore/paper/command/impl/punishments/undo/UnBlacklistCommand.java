package net.octopvp.octocore.paper.command.impl.punishments.undo;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.database.redis.packets.player.ExecuteUnblacklistPacket;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishPlayerData;
import net.octopvp.octocore.paper.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.paper.module.impl.punishments.util.PunishmentType;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class UnBlacklistCommand extends BaseCommand {

    @Command(name = "unblacklist", permission = Permission.PUNISHMENT_UNBLACKLIST, usage = "<player> <reason>", aliases = {"unbl", "unblplayer", "unblacklistplayer"})
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length < 2) {
            return CommandResult.INVALID_ARGS;
        }
        Tasks.runAsync(() -> {
            OfflinePlayer target = Bukkit.getOfflinePlayer(PunishModule.getInstance().getProfileManager().correctName(args[0]));

            PunishPlayerData targetData = PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());

            if (targetData == null || !target.isOnline()) {
                PunishModule.getInstance().getProfileManager().createPlayerData(target.getUniqueId(), target.getName());
                targetData = PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());
                targetData.getPunishData().load();
            }
            if (!targetData.getPunishData().isBlacklisted()) {
                sender.sendMessage(Lang.BLACKLIST_NOT_BLACKLISTED.getMsg(target.getName()));
                PunishModule.getInstance().getProfileManager().unloadData(target);
                return;
            }

            StringBuilder reasonBuilder = new StringBuilder();

            for (int i = 1; i < args.length; ++i) {
                reasonBuilder.append(args[i]).append(" ");
            }
            if (reasonBuilder.length() == 0) reasonBuilder.append("Un-Blacklisted");

            String reason = reasonBuilder.toString().trim();
            boolean silent = reason.contains("-silent") || reason.contains("-s");

            if (silent) {
                reason = reason.replace("-silent", "").replace("-s", "").trim();
            }

            Punishment punishment = targetData.getPunishData().getActiveBlacklist();
            punishment.setActive(false);
            punishment.setLast(false);
            punishment.setRemovedBy(sender.getName());
            punishment.setRemovedFor(reason);
            punishment.setRemovedSilent(silent);
            punishment.setWhenRemoved(System.currentTimeMillis());

            JsonBuilder jsonChain = new JsonBuilder();
            jsonChain.addProperty("sender", sender.getName());
            jsonChain.addProperty("target", targetData.getPlayerName());
            jsonChain.addProperty("silent", punishment.isRemovedSilent());
            jsonChain.addProperty("reason", reason);
            if (sender.isPlayer()) {
                Player player = sender.getPlayer();
                jsonChain.addProperty("senderDisplay", player.getDisplayName());

                PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());
                jsonChain.addProperty("coloredName", playerData.getHighestRank().getColor() + playerData.getName());
            } else {
                jsonChain.addProperty("senderDisplay", sender.getName());
            }

            new ExecuteUnblacklistPacket(jsonChain).send();

            punishment.save(true);

            Player addedBy = Bukkit.getPlayer(punishment.getAddedByName());
            if (addedBy != null) {
                PlayerData addedByData = PlayerManager.getInstance().getData(addedBy.getUniqueId());
                addedByData.getPunishmentsExecuted().forEach(punishHistory -> {
                    if (punishHistory.getPunishmentType() == PunishmentType.BLACKLIST) {
                        if (punishHistory.getTarget().equals(target.getName())) {
                            if (punishHistory.getAddedAt() == punishment.getAddedAt()) {
                                punishHistory.setActive(false);
                            }
                        }
                    }
                });
            }

            PunishModule.getInstance().getProfileManager().unloadData(target);
        });
        return CommandResult.SUCCESS;
    }
}
