package net.octopvp.octocore.paper.utils.chat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatUtil {
    public static void clearChat() {
        //String[] msg = StringUtils.getRandomChatColors(10)
        for (Player p : Bukkit.getOnlinePlayers()) {
        }
    }
}
