package net.octopvp.octocore.paper.utils;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCorePaper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class Logger {
    public static ArrayList<Player> debugPlayers = new ArrayList<>();
    public static void info(String str){
        Bukkit.getLogger().info(OctoCorePaper.prefix + " " + str);
    }
    public static void warn(String str){
        Bukkit.getLogger().warning(OctoCorePaper.prefix + " " + str);
    }
    public static void error(String str){
        Bukkit.getLogger().severe(OctoCorePaper.prefix + " " + str);
    }
    public static void debug(String str){
        Bukkit.getLogger().info(OctoCorePaper.prefix + " [DEBUG] " + str);
        debugPlayers.forEach(player -> {
            if(!player.isOnline())
                debugPlayers.remove(player);
            else
                player.sendMessage(CC.YELLOW + "[DEBUG] " + CC.GRAY + str);
        });
    }
}
