package net.octopvp.octocore.waterfall.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.octopvp.octocore.waterfall.manager.OnlinePlayersManager;
import net.octopvp.octocore.waterfall.util.object.OnlinePlayerData;

public class BungeeDataCommand extends Command {
    public BungeeDataCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if (sender instanceof ProxiedPlayer player) {
            OnlinePlayerData data = OnlinePlayersManager.getDataMap().get(player.getUniqueId());
            if (strings.length > 0) {
                data.getCachedPermResults().clear();
            }
            player.sendMessage(data.toString());

        }
    }
}
