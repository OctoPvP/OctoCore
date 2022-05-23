package net.octopvp.octocore.paper.command.impl.essentials;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.item.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GiveCommand {
    @Command(name = "give", aliases = {"i", "g", "item"})
    @Permission(Permissions.GIVE)
    @PlayerOnly
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length == 1) {
            Material material = ItemUtils.getItemMap().get(args[0].toUpperCase());
            if (material == null) {
                sender.sendMessage(CC.RED + "Usage: /i <item> [amount]");
                return CommandResult.SUCCESS;
            }
            ItemStack itemStack = new ItemBuilder(material).amount(1).build();
            sender.getPlayer().getInventory().addItem(itemStack);
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
            ItemStack itemStack = new ItemBuilder(material).amount(i).build();
            sender.getPlayer().getInventory().addItem(itemStack);
            return CommandResult.SUCCESS;
        } else sender.sendMessage(CC.RED + "Usage: /i <item> [amount]");
        return CommandResult.SUCCESS;
    }
}
