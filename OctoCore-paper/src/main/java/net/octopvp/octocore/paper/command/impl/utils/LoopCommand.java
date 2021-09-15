package net.octopvp.octocore.paper.command.impl.utils;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.common.object.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class LoopCommand extends BaseCommand {
    @Command(name = "loop",permission = Permission.LOOP,usage = "<times> <ticks delay> <command to execute>")
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length <= 2){
            sender.sendMessage(CC.RED + "Usage: /loop <times> <ticks delay> <command to execute>" + CC.NL + CC.RED + "Notes: use 0 as the delay to execute instantly" + CC.NL + CC.RED + "Example: /loop 10 1 summon cow");
            return CommandResult.INVALID_ARGS;
        }
        int times,delay;
        try{
            times = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(CC.RED + args[0] + " is not an integer!");
            return CommandResult.INVALID_ARGS;
        }
        try{
            delay = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(CC.RED + args[1] + " is not an integer!");
            return CommandResult.INVALID_ARGS;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if(i == 0)
                sb.append(args[i]);
            sb.append(" ").append(args[i]);
        }
        if (delay == 0){
            for (int i = 0; i < times; i++) {
                Bukkit.dispatchCommand(sender.getCommandSender(),sb.toString());
            }
            return CommandResult.SUCCESS;
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(OctoCore.getInstance(), new LoopScheduler(times,sender.getCommandSender(),sb.toString()), 0l, delay);
        return CommandResult.SUCCESS;
    }
    public class LoopScheduler extends BukkitRunnable {
        private int times;
        private String command;
        private org.bukkit.command.CommandSender sender;
        public LoopScheduler(int times, CommandSender sender,String command){
            this.times = times;
            this.sender = sender;
            this.command = command;
        }
        int a = 0;
        @Override
        public void run() {
            if(a >= times){
                cancel();
            }
            a++;
            Bukkit.dispatchCommand(sender,command);
        }
    }
}
