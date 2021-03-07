package net.octopvp.octocore.waterfall;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class GetUuid extends Command {
    public GetUuid(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        player.sendMessage(player.getUniqueId());
    }
}
