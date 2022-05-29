package net.octopvp.octocore.paper.command.impl.essentials;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Optional;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Range;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.Sender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class EnchantCommand {
    @Command(name = "enchant", aliases = {"ench", "e"}, usage = "<enchant> [level>")
    @PlayerOnly
    @Permission(Permissions.ENCHANT)
    public CommandResult execute(Sender sender, Enchantment enchantment, @Optional @Range(defaultValue = 1, min = -Integer.MIN_VALUE, max = Integer.MAX_VALUE) int enchLevel) {
        Player target = sender.getPlayer();
        if (target.getInventory().getItemInHand() == null || target.getInventory().getItemInHand().getType().toString().toLowerCase().contains("air")) {
            target.sendMessage(CC.RED + "Please hold something to enchant!");
            return CommandResult.SUCCESS;
        }
        target.getInventory().getItemInHand().addUnsafeEnchantment(enchantment, enchLevel);
        return CommandResult.SUCCESS;
    }
}
