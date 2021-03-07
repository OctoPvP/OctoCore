package net.octopvp.octocore.paper.utils.msg;

import org.bukkit.ChatColor;

public class PlaceholderManager {
    public static String replacePlaceholders(String message){
        return ChatColor.translateAlternateColorCodes('&',message);
    }
    public static String replacePlaceholders(Lang message){
        return ChatColor.translateAlternateColorCodes('&',message.getMsg());
    }
}
