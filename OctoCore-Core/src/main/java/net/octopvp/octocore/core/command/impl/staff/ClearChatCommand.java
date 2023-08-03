package net.octopvp.octocore.core.command.impl.staff;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.database.redis.packets.staff.StaffAlertPacket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

public class ClearChatCommand {
    @Command(name = "clearchat", aliases = {"cc"}, description = "Clear the chat")
    @Permission(Permissions.CLEAR_CHAT)
    public void clearChat(@Sender CommandSender sender) {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            ChatColor color = ChatColor.values()[random.nextInt(ChatColor.values().length)];
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.hasPermission(Permissions.STAFF)) {
                    onlinePlayer.sendMessage(color + "");
                }
            }
        }
        String senderName = sender instanceof Player ? ((Player) sender).getName() : "Console";
        new StaffAlertPacket(senderName + " has cleared the chat").send();
    }
}
