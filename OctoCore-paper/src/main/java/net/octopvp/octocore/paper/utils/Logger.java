package net.octopvp.octocore.paper.utils;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Logger {
    public static ArrayList<Player> debugPlayers = new ArrayList<>();
    public static void info(String str){
        Bukkit.getLogger().info(OctoCore.prefix + " " + str);
    }
    public static void warn(String str){
        Bukkit.getLogger().warning(OctoCore.prefix + " " + str);
    }
    public static void error(String str){
        Bukkit.getLogger().severe(OctoCore.prefix + " " + str);
    }
    public static void debug(String str){
        Bukkit.getLogger().info(OctoCore.prefix + " [DEBUG] " + str);
        for (Player player : debugPlayers) {
            if(!player.isOnline())
                debugPlayers.remove(player);
            else
                player.sendMessage(CC.YELLOW + "[DEBUG] " + CC.GRAY + str);
        }
    }
}
