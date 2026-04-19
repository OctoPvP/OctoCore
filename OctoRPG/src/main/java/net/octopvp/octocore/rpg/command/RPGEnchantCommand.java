package net.octopvp.octocore.rpg.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.OctoRPG;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RPGEnchantCommand implements BasicCommand {
    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (!(stack.getSender() instanceof Player player)) {
            stack.getSender().sendMessage("This command can only be used by players.");
            return;
        }
        if (!player.hasPermission("octorpg.admin")) {
            player.sendMessage(CC.RED + "No permission.");
            return;
        }
        if (args.length < 1) {
            player.sendMessage(CC.RED + "Usage: /rpgenchant <enchant> [level]");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            player.sendMessage(CC.RED + "You must be holding an item.");
            return;
        }

        String enchantName = args[0].toLowerCase();
        if (!enchantName.contains(":")) {
            enchantName = "octorpg:" + enchantName;
        }

        NamespacedKey key = NamespacedKey.fromString(enchantName);
        Enchantment enchantment = Registry.ENCHANTMENT.get(key);

        if (enchantment == null) {
            player.sendMessage(CC.RED + "Enchantment not found: " + enchantName);
            return;
        }

        if (!OctoRPG.getInstance().getItemManager().canAddEnchantment(item, enchantment)) {
            player.sendMessage(CC.RED + "This item has reached its enchantment limit for this type of enchantment!");
            return;
        }

        int level = 1;
        if (args.length >= 2) {
            try {
                level = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(CC.RED + "Invalid level.");
                return;
            }
        }

        item.addUnsafeEnchantment(enchantment, level);
        OctoRPG.getInstance().getItemManager().rebuildLore(player, item);
        player.sendMessage(CC.GREEN + "Applied " + enchantment.getKey().getKey() + " " + level + " to item.");
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length <= 1) {
            return Registry.ENCHANTMENT.stream()
                    .map(ench -> ench.getKey().toString())
                    .filter(key -> key.contains("octorpg"))
                    .filter(key -> key.toLowerCase().startsWith(args.length == 0 ? "" : args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of("1", "2", "3", "4", "5", "10");
    }
}
