package net.octopvp.octocore.core.command.impl.utils;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.JoinStrings;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.utils.Sender;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class LoopCommand {
    @Command(name = "loop")
    @Permission(Permissions.LOOP)
    public CommandResult execute(Sender sender, int times, int delay, @JoinStrings String cmd) {
        if (cmd == null || cmd.length() == 0) {
            return CommandResult.INVALID_ARGS;
        }
        if (delay == 0) {
            for (int i = 0; i < times; i++) {
                Bukkit.dispatchCommand(sender.getCommandSender(), cmd.replace("%i", i + ""));
            }
            return CommandResult.SUCCESS;
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(OctoCore.getInstance(), new LoopScheduler(times, sender.getCommandSender(), cmd), 0L, delay);
        return CommandResult.SUCCESS;
    }

    public class LoopScheduler extends BukkitRunnable {
        private final int times;
        private final String command;
        private final org.bukkit.command.CommandSender sender;
        int a = 0;

        public LoopScheduler(int times, CommandSender sender, String command) {
            this.times = times;
            this.sender = sender;
            this.command = command;
        }

        @Override
        public void run() {
            if (a >= times) {
                cancel();
            }
            a++;
            Bukkit.dispatchCommand(sender, command);
        }
    }
}
