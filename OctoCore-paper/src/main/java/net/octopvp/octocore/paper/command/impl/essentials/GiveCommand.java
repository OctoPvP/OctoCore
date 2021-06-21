package net.octopvp.octocore.paper.command.impl.essentials;

import com.google.common.collect.Lists;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.command.Completer;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.item.ItemUtils;
import net.octopvp.octocore.paper.utils.permission.Permission;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GiveCommand extends BaseCommand {
    @Command(name = "give",aliases = {"i","g","item"},permission = Permission.GIVE,playerOnly = true)
    public CommandResult execute(Sender sender, String[] args) {
        if(args.length == 1){
            Material material = ItemUtils.getItemMap().get(args[0]);
            if(material == null){
                sender.sendMessage(CC.RED + "Usage: /i <item> [amount]");
                return CommandResult.SUCCESS;
            }
            ItemStack itemStack = new ItemBuilder(material).amount(1).build();
            sender.getPlayer().getInventory().addItem(itemStack);
            return CommandResult.SUCCESS;
        }else if(args.length == 2){
            Material material = ItemUtils.getItemMap().get(args[0]);
            if(material == null){
                sender.sendMessage(CC.RED + "Usage: /i <item> [amount]");
                return CommandResult.SUCCESS;
            }
            int i = 1;
            try{
                i = Integer.getInteger(args[1]);
            } catch (Exception e) {
                sender.sendMessage(CC.RED + "Invalid Amount! Usage: /i <item> [amount]\nDefaulting to 1.");
            }
            ItemStack itemStack = new ItemBuilder(material).amount(i).build();
            sender.getPlayer().getInventory().addItem(itemStack);
            return CommandResult.SUCCESS;
        }
        else sender.sendMessage(CC.RED + "Usage: /i <item> [amount]");
        return CommandResult.SUCCESS;
    }

    @Completer(name = "give",aliases = {"i","g"})
    public List<String> tabComplete(Sender sender, String[] args) {
        if (args.length == 1)
            return Lists.newArrayList(ItemUtils.getItemMap().keySet());
        else if(args.length == 2)
            return Lists.newArrayList("1","10","32","64");
        return Lists.newArrayList("");
    }
}
