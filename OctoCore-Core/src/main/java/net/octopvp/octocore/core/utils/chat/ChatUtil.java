package net.octopvp.octocore.core.utils.chat;

import org.bukkit.ChatColor;

public class ChatUtil {
    public static ChatColor convertChatColor(net.octopvp.octocore.common.util.ChatColor color) {
        return ChatColor.valueOf(color.name());
    }
}
