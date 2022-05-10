package net.octopvp.octocore.paper.command.impl;

import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.menus.settings.SettingsMenu;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;

public class SettingsCommand extends BaseCommand {
    @Command(name = "settings")
    public CommandResult execute(Sender sender, String[] args) {
        PlayerData data = PlayerManager.getInstance().getData(sender.getUUID());
        new SettingsMenu(data).open(sender);
        return CommandResult.SUCCESS;
    }
}
