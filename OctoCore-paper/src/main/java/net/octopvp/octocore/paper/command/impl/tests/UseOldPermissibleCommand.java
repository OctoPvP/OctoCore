package net.octopvp.octocore.paper.command.impl.tests;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.objects.OctoPermissible;
import net.octopvp.octocore.paper.utils.Sender;

public class UseOldPermissibleCommand extends BaseCommand {
    @Command(name = "useoldpermissible",playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        if (sender.getPlayer().getPermissibleBase() instanceof OctoPermissible){
            sender.getPlayer().setPermissibleBase(((OctoPermissible) sender.getPlayer().getPermissibleBase()).getOldPermissibleBase());
            sender.sendMessage(CC.GREEN + "Done!");
        }else{
            sender.sendMessage(CC.RED + "Permissible isn't an OctoPermissible!");
        }
        return CommandResult.SUCCESS;
    }
}
