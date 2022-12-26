package net.octopvp.octocore.core.command.impl.essentials;

import net.octopvp.commander.annotation.*;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.command.CommandResult;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class EnchantCommand {
    @Command(name = "enchant", aliases = {"ench", "e"}, usage = "<enchant> [level]")
    @PlayerOnly
    @Permission(Permissions.ENCHANT)
    public CommandResult execute(@Sender Player target, Enchantment enchantment, @Optional @DefaultNumber(1) @Range(min = Integer.MIN_VALUE, max = Integer.MAX_VALUE) int enchLevel) {
        if (target.getInventory().getItemInHand() == null || target.getInventory().getItemInHand().getType().toString().toLowerCase().contains("air")) {
            target.sendMessage(CC.RED + "Please hold something to enchant!");
            return CommandResult.SUCCESS;
        }
        target.getInventory().getItemInHand().addUnsafeEnchantment(enchantment, enchLevel);
        return CommandResult.SUCCESS;
    }
}
