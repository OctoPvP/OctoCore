package net.octopvp.octocore.core.command.impl.essentials;

import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.utils.item.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GiveCommand {
    @Command(name = "give", aliases = {"i", "g", "item"})
    @Permission(Permissions.GIVE)
    @PlayerOnly
    public CommandResult execute(@Sender Player sender, String[] args) {
        if (args.length == 1) {
            Material material = ItemUtils.getItemMap().get(args[0].toUpperCase());
            if (material == null) {
                sender.sendMessage(CC.RED + "Usage: /i <item> [amount]");
                return CommandResult.SUCCESS;
            }
            sender.getPlayer().getInventory().addItem(ItemBuilder.from(material).amount(1).build());
            return CommandResult.SUCCESS;
        } else if (args.length == 2) {
            Material material = ItemUtils.getItemMap().get(args[0].toUpperCase());
            if (material == null) {
                sender.sendMessage(CC.RED + "Usage: /i <item> [amount]");
                return CommandResult.SUCCESS;
            }
            int i = 1;
            try {
                i = Integer.getInteger(args[1]);
            } catch (Exception e) {
                sender.sendMessage(CC.RED + "Invalid Amount! Usage: /i <item> [amount]\nDefaulting to 1.");
            }
            sender.getPlayer().getInventory().addItem(ItemBuilder.from(material).amount(i).build());
            return CommandResult.SUCCESS;
        } else sender.sendMessage(CC.RED + "Usage: /i <item> [amount]");
        return CommandResult.SUCCESS;
    }
}
