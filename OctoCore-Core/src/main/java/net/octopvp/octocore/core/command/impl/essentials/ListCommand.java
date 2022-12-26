package net.octopvp.octocore.core.command.impl.essentials;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Switch;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.menus.ListMenu;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ListCommand {
    /*
    @Command(name = "list", aliases = {"players"}, description = "List all online players (gui)")
    @Permission(Permissions.LIST_PLAYERS)
    public CommandResult execute(CommandSender sender, String[] args) {
        if (false) {
            new ListMenu(args).open(sender.getPlayer());
            return CommandResult.SUCCESS;
        }
        if (sender.getCommandSender() instanceof Player) {
            //do the gui thing
            if (args.length > 1) {
                sender.sendMessage(Lang.LIST_MESSAGE_HEADER.getMsg(PlayerManager.getInstance().getPlayerProfiles().size()));
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());
                    String msg;
                    if (playerData == null)
                        msg = CC.GRAY + player.getName();
                    else msg = playerData.getFormattedName(false, player);
                    sender.sendMessage(Lang.LIST_MESSAGE_BODY_ENTRY.getMsg(msg));
                }
                sender.sendMessage(CC.SEPARATOR);
            } else {
                new ListMenu().open(sender.getPlayer());
            }
        } else {
            //console
            sender.sendMessage(Lang.LIST_MESSAGE_HEADER.getMsg(PlayerManager.getInstance().getPlayerProfiles().size()));
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());
                String msg;
                if (playerData == null)
                    msg = CC.GRAY + player.getName();
                else msg = playerData.getFormattedName(false, player);
                sender.sendMessage(Lang.LIST_MESSAGE_BODY_ENTRY.getMsg(msg));
            }
        }
        return null;
    }
     */
    @Command(name = "list", aliases = {"players"}, description = "List all online players")
    @Permission(Permissions.LIST_PLAYERS)
    public void list(CommandSender sender, @Switch("text") boolean sendText) {
        if (sender instanceof ConsoleCommandSender) sendText = true;

        if (sendText) {
            sendText(sender);
        } else {
            new ListMenu().open((Player) sender);
        }
    }

    public void sendText(CommandSender sender) {
        sender.sendMessage(Lang.LIST_MESSAGE_HEADER.getMsg(PlayerManager.getInstance().getPlayerProfiles().size()));
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());
            String msg;
            if (playerData == null)
                msg = CC.GRAY + player.getName();
            else msg = playerData.getFormattedName(false, player);
            sender.sendMessage(Lang.LIST_MESSAGE_BODY_ENTRY.getMsg(msg));
        }
        sender.sendMessage(CC.SEPARATOR);
    }


}
