package net.octopvp.octocore.rpg.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.octopvp.octocore.common.util.CC;
import org.jetbrains.annotations.NotNull;

public class RPGHelpCommand implements BasicCommand {
    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        stack.getSender().sendMessage(CC.translate("&6&lOctoRPG Help:"));
        stack.getSender().sendMessage(CC.translate("&e/rpgitem &7- Open the items menu"));
        stack.getSender().sendMessage(CC.translate("&e/rpgenchant <enchant> [level] &7- Enchant the item in your hand"));
        stack.getSender().sendMessage(CC.translate("&e/rpgdebug &7- Toggle debug mode"));
        if (stack.getSender().hasPermission("octorpg.admin")) {
            stack.getSender().sendMessage(CC.translate("&c/rpgstats <player> <stat> <value> &7- Modify player stats"));
            stack.getSender().sendMessage(CC.translate("&c/rpgclear [player] &7- Clear RPG items from inventory"));
            stack.getSender().sendMessage(CC.translate("&c/rpgquest <player> <action> &7- Manage player quests"));
        }
    }
}
