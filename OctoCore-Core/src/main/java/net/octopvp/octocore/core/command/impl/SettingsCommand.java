package net.octopvp.octocore.core.command.impl;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.octocore.core.menus.settings.SettingsMenu;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.entity.Player;

public class SettingsCommand {
    @Command(name = "settings")
    public void execute(@Sender Player player, @Sender PlayerData sender) {
        new SettingsMenu(sender).open(player);
    }
}
