package net.octopvp.octocore.paper.command.impl.punishments.undo;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.json.JsonChain;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
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

public class UnBanCommand extends BaseCommand {

    @Command(name = "unban", permission = Permission.PUNISHMENT_UNBAN)
    public CommandResult execute(Sender sender, String[] args) {
        Tasks.runAsync(() -> {
            if (args.length < 2) {
                sender.sendMessage(CC.translate("&cUsage: /unban <player> <reason> [-s]"));
                return;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer( PunishModule.getInstance().getProfileManager().correctName(args[0]));

            PunishPlayerData targetData =  PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());

            if (targetData == null || !target.isOnline()) {
                 PunishModule.getInstance().getProfileManager().createPlayerData(target.getUniqueId(), target.getName());
                targetData =  PunishModule.getInstance().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());
                targetData.getPunishData().load();
            }
            if (!targetData.getPunishData().isBanned()) {
                sender.sendMessage(Lang.NOT_BANNED);
                 PunishModule.getInstance().getProfileManager().unloadData(target);
                return;
            }


            StringBuilder reasonBuilder = new StringBuilder();

            for (int i = 1; i < args.length; ++i) {
                reasonBuilder.append(args[i]).append(" ");
            }
            if (reasonBuilder.length() == 0) reasonBuilder.append("Un-Banned");

            String reason = reasonBuilder.toString().trim();
            boolean silent = reason.contains("-silent") || reason.contains("-s");

            if (reason.contains("-silent")) {
                reason = reason.replace("-silent", "");
            } else if (reason.contains("-s")) {
                reason = reason.replace("-s", "");
            }

            Punishment punishment = targetData.getPunishData().getActiveBan();
            punishment.setActive(false);
            punishment.setLast(false);
            punishment.setRemovedBy(sender.getName());
            punishment.setRemovedFor(reason);
            punishment.setRemovedSilent(silent);
            punishment.setWhenRemoved(System.currentTimeMillis());

            JsonChain jsonChain = new JsonChain();
            if (sender instanceof Player) {
                Player player = (Player) sender;
                jsonChain.addProperty("senderDisplay", player.getDisplayName());

                PlayerData playerData = PlayerManager.getData(player.getUniqueId());
                jsonChain.addProperty("coloredName", playerData.getHighestRank().getColor() + playerData.getName());
            } else {
                jsonChain.addProperty("senderDisplay", sender.getName());
            }
            jsonChain.addProperty("sender", sender.getName());
            jsonChain.addProperty("target", targetData.getPlayerName());
            jsonChain.addProperty("silent", punishment.isRemovedSilent());
            jsonChain.addProperty("reason", reason);

            plugin.getRedisData().write(JedisAction.EXECUTE_UNBAN, jsonChain.get());

            punishment.save(true);

            Player addedBy = Bukkit.getPlayer(punishment.getAddedByName());
            if (addedBy != null) {
                PlayerData addedByData = PlayerManager.getData(addedBy.getUniqueId());
                addedByData.getPunishmentsExecuted().forEach(punishHistory -> {
                    if (punishHistory.getPunishmentType() == PunishmentType.BAN) {
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
