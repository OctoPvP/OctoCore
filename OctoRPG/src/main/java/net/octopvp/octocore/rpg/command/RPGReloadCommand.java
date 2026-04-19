package net.octopvp.octocore.rpg.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.OctoRPG;
import org.jetbrains.annotations.NotNull;

public class RPGReloadCommand implements BasicCommand {
    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (!stack.getSender().hasPermission("octorpg.admin")) {
            stack.getSender().sendMessage(CC.RED + "No permission.");
            return;
        }

        long start = System.currentTimeMillis();
        OctoRPG.getInstance().reload();
        long end = System.currentTimeMillis();

        stack.getSender().sendMessage(CC.GREEN + "OctoRPG has been reloaded in " + (end - start) + "ms.");
    }
}
