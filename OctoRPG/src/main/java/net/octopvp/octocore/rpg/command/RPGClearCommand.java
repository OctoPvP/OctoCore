package net.octopvp.octocore.rpg.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.item.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RPGClearCommand implements BasicCommand {
    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (!stack.getSender().hasPermission("octorpg.admin")) {
            stack.getSender().sendMessage(CC.RED + "No permission.");
            return;
        }

        Player target;
        if (args.length >= 1) {
            target = Bukkit.getPlayer(args[0]);
        } else if (stack.getSender() instanceof Player player) {
            target = player;
        } else {
            stack.getSender().sendMessage(CC.RED + "You must specify a player.");
            return;
        }

        if (target == null) {
            stack.getSender().sendMessage(CC.RED + "Player not found.");
            return;
        }

        int cleared = 0;
        for (int i = 0; i < target.getInventory().getSize(); i++) {
            ItemStack item = target.getInventory().getItem(i);
            if (item == null || item.getType().isAir()) continue;

            CustomItem customItem = OctoRPG.getInstance().getItemManager().getCustomItem(item);
            if (customItem != null) {
                target.getInventory().setItem(i, null);
                cleared++;
            }
        }

        stack.getSender().sendMessage(CC.GREEN + "Cleared " + cleared + " RPG items from " + target.getName() + "'s inventory.");
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length <= 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args.length == 0 ? "" : args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
