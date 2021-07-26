package net.octopvp.octocore.paper.command.impl.essentials;

import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.command.Completer;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.common.object.Permission;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class EnchantCommand extends BaseCommand {
    @Command(name = "enchant",aliases = {"ench","e"},playerOnly = true,permission = Permission.ENCHANT)
    public CommandResult execute(Sender sender, String[] args) {
        if (args.length == 1){
            String ench = StringUtils.getEnchantment(args[0]);
            Enchantment enchantment;
            try{
                enchantment = Enchantment.getByName(ench);
            } catch (Exception e) {
                sender.sendMessage(CC.RED + "Could not find the enchant \"" + args[0] + "\". Usage: /enchant <enchant> <level>");
                return CommandResult.INVALID_ARGS;
            }
            Player target = sender.getPlayer();
            if(target.getInventory().getItemInHand().getType().toString().toLowerCase().contains("air")){
                target.sendMessage(CC.RED + "Please hold something to enchant!");
                return CommandResult.SUCCESS;
            }
            target.getInventory().getItemInHand().addUnsafeEnchantment(enchantment,1);
            return CommandResult.SUCCESS;
        }else if(args.length >= 2){
            String ench = StringUtils.getEnchantment(args[0]);
            Enchantment enchantment;
            try{
                enchantment = Enchantment.getByName(ench);
            } catch (Exception e) {
                sender.sendMessage(CC.RED + "Could not find the enchant \"" + args[0] + "\". Usage: /enchant <enchant> <level>");
                return CommandResult.INVALID_ARGS;
            }
            Player target = sender.getPlayer();
            if(target.getInventory().getItemInHand().getType().toString().toLowerCase().contains("air")){
                target.sendMessage(CC.RED + "Please hold something to enchant!");
                return CommandResult.SUCCESS;
            }
            int level;
            try{
                level = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                target.sendMessage(CC.RED + args[1] + " is not an integer!");
                return CommandResult.INVALID_ARGS;
            }
            target.getInventory().getItemInHand().addUnsafeEnchantment(enchantment,level);
            return CommandResult.SUCCESS;
        }
        sender.sendMessage(CC.RED + "Usage: /enchant <enchant> <level>");
        return CommandResult.SUCCESS;
    }

    @Completer(name = "enchant",aliases = {"ench","e"})
    public List<String> tabComplete(Sender sender, String[] args) {
        String[] a = new String[]{"sharp","sharpness","ff","featherfalling","feather","fire","fireaspect","kb","knock","knockback","smi","smite","bane","baneof","baneofarthropods","prot","protection","firep","fireprot","fireprotection",
                "blast","blastprot","blastprotection","proj","projprot","projectileprotection","loot","looting","fort","fortune","silk","silktouch","pow","power","pun","punch","fla","flame","inf","infinity","unb","unbreaking","eff","efficiency"};
        if(args.length == 1)
            return Arrays.asList(a);
        else {
            return Arrays.asList(new String[]{"1","4","5","10","20","32767"});
        }
    }
}
