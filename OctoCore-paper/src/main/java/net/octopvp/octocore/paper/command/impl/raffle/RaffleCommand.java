package net.octopvp.octocore.paper.command.impl.raffle;

import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.event.raffle.RaffleEvent;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.permission.Permission;

import java.util.List;

public class RaffleCommand extends BaseCommand {
    @Command(name = "raffle",description = "Start/manage/enter a raffle",cooldown = 10,playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("enter") || args[0].equalsIgnoreCase("join")){
                if(!sender.hasPermission(Permission.ENTER_RAFFLE))
                    return CommandResult.NO_PERMS;
                if(RaffleEvent.isRaffleRunning()){
                    RaffleEvent.getCurrentRaffle().addPlayer(sender.getPlayer().getUniqueId());
                }else{
                    sender.sendMessage(Lang.NO_RAFFLE_RUNNING);
                }
            }else if(args[0].equalsIgnoreCase("end")){
                if(!sender.hasPermission(Permission.END_RAFFLE)) {
                    return CommandResult.NO_PERMS;
                }
                if(RaffleEvent.isRaffleRunning()){
                    RaffleEvent.getCurrentRaffle().end();
                }
            }
        }else if(args.length >= 2){
            if(args[0].equalsIgnoreCase("start")){
                if(!sender.hasPermission(Permission.START_RAFFLE))
                    return CommandResult.NO_PERMS;
                if(RaffleEvent.isRaffleRunning()){
                    sender.sendMessage(Lang.RAFFLE_ALREADY_RUNNING);
                    return CommandResult.SUCCESS;
                }
                String prize = StringUtils.buildString(args,1);
                RaffleEvent event = new RaffleEvent(prize,sender.getPlayer());
                event.start();
            }
        }
        return CommandResult.SUCCESS;
    }

    @Override
    public List<String> tabComplete(Sender sender, String[] args) {
        return null;
    }
}
