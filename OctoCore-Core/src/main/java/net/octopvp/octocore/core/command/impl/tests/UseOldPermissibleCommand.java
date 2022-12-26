package net.octopvp.octocore.core.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.objects.OctoPermissible;
import org.bukkit.entity.Player;

public class UseOldPermissibleCommand {
    @Command(name = "useoldpermissible")
    @PlayerOnly
    public CommandResult execute(@Sender Player sender) {
        if (sender.getPermissibleBase() instanceof OctoPermissible) {
            sender.setPermissibleBase(((OctoPermissible) sender.getPermissibleBase()).getOldPermissibleBase());
            sender.sendMessage(CC.GREEN + "Done!");
        } else {
            sender.sendMessage(CC.RED + "Permissible isn't an OctoPermissible!");
        }
        return CommandResult.SUCCESS;
    }

    @Command(name = "delpermcache")
    @PlayerOnly
    public CommandResult cache(@Sender Player sender) {
        if (sender.getPermissibleBase() instanceof OctoPermissible) {
            sender.getPermissibleBase().recalculatePermissions();
            sender.sendMessage(CC.GREEN + "Done!");
        } else {
            sender.sendMessage(CC.RED + "Permissible isn't an OctoPermissible!");
        }
        return CommandResult.SUCCESS;
    }
}
