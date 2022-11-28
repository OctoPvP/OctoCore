package net.octopvp.octocore.core.command.impl.staff;

import net.octopvp.commander.annotation.Command;
import net.octopvp.octocore.core.OctoCore;
import org.bukkit.command.CommandSender;

public class MasterCommands {
    @Command(name = "master")
    public void execute(CommandSender sender) {
        sender.sendMessage(OctoCore.getInstance().getConfig().getString("master-url", "https://master.octopvp.net/"));
    }
}
