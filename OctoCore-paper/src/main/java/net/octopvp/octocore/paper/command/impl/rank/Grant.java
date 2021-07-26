package net.octopvp.octocore.paper.command.impl.rank;

import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.runnable.Tasks;

public class Grant extends BaseCommand{
    @Command(name = "grant",permission = Permission.GRANT,cooldown = 1)
    public CommandResult execute(Sender sender, String[] args) {
        Tasks.runAsync(()->{
            if (args.length != 1){
                sender.sendMessage(CC.RED + "Usage: /grant <player>");
                return;
            }
            PlayerData d1 = PlayerManager.getPlayerData(args[0]);
            if (d1 != null){
            }
        });
        return CommandResult.SUCCESS;
    }
}
