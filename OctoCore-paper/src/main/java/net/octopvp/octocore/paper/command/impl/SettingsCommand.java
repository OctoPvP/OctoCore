package net.octopvp.octocore.paper.command.impl;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.octocore.paper.menus.settings.SettingsMenu;
import net.octopvp.octocore.paper.objects.PlayerData;
import org.bukkit.entity.Player;

public class SettingsCommand {
    @Command(name = "settings")
    public void execute(@Sender Player player, PlayerData sender) {
        new SettingsMenu(sender).open(player);
    }
}
