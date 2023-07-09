package net.octopvp.octocore.waterfall.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.waterfall.manager.OnlinePlayersManager;
import net.octopvp.octocore.waterfall.util.object.OnlinePlayerData;

public class BungeeHasPermissionCommand extends Command {
    public BungeeHasPermissionCommand() {
        super("bhasperm");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender instanceof ProxiedPlayer player) {
            OnlinePlayerData data = OnlinePlayersManager.getDataMap().get(player.getUniqueId());
            if (data == null) {
                commandSender.sendMessage(CC.RED + "Data is null!");
                return;
            }
            if (args.length == 1) {
                player.sendMessage(String.valueOf(player.hasPermission(args[0])));
            } else {
                player.sendMessage(CC.RED + "Usage: /bhasperm <permission>");
            }
        }
    }
}
