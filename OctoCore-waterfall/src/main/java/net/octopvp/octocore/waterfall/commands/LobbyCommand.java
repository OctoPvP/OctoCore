package net.octopvp.octocore.waterfall.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.octopvp.octocore.common.util.CC;

public class LobbyCommand extends Command {
    public LobbyCommand() {
        super("lobby", "octocore.command.lobby", "lobby","hub","l","h","plssendmetolobby");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)){
            sender.sendMessage(new TextComponent(CC.RED + "You must be a player to do this!"));
        }
    }
}
