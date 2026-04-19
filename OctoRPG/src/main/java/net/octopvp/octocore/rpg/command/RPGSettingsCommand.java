package net.octopvp.octocore.rpg.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.menu.SettingsMenu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RPGSettingsCommand implements BasicCommand {
    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (!(stack.getSender() instanceof Player)) {
            stack.getSender().sendMessage(CC.RED + "Only players can use this command.");
            return;
        }
        Player player = (Player) stack.getSender();
        new SettingsMenu().open(player);
    }
}
