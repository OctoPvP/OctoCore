package net.octopvp.octocore.paper.utils.chat;

import net.md_5.bungee.api.ChatColor;
import net.octopvp.octocore.common.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatUtil {
    public static void clearChat(){
        //String[] msg = StringUtils.getRandomChatColors(10)
        for(Player p: Bukkit.getOnlinePlayers()){
        }
    }
}