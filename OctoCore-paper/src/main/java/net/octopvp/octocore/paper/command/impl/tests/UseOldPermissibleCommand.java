package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.objects.OctoPermissible;
import net.octopvp.octocore.paper.utils.Sender;

public class UseOldPermissibleCommand {
    @Command(name = "useoldpermissible")
    @PlayerOnly
    public CommandResult execute(Sender sender) {
        if (sender.getPlayer().getPermissibleBase() instanceof OctoPermissible) {
            sender.getPlayer().setPermissibleBase(((OctoPermissible) sender.getPlayer().getPermissibleBase()).getOldPermissibleBase());
            sender.sendMessage(CC.GREEN + "Done!");
        } else {
            sender.sendMessage(CC.RED + "Permissible isn't an OctoPermissible!");
        }
        return CommandResult.SUCCESS;
    }

    @Command(name = "delpermcache")
    @PlayerOnly
    public CommandResult cache(Sender sender) {
        if (sender.getPlayer().getPermissibleBase() instanceof OctoPermissible) {
            sender.getPlayer().getPermissibleBase().recalculatePermissions();
            sender.sendMessage(CC.GREEN + "Done!");
        } else {
            sender.sendMessage(CC.RED + "Permissible isn't an OctoPermissible!");
        }
        return CommandResult.SUCCESS;
    }
}
