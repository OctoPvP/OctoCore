package net.octopvp.octocore.paper.utils;

import net.octopvp.octocore.paper.OctoCorePaper;
import org.bukkit.Bukkit;

public class Logger {
    public static void info(String str){
        Bukkit.getLogger().info(OctoCorePaper.prefix + " " + str);
    }
    public static void warn(String str){
        Bukkit.getLogger().warning(OctoCorePaper.prefix + " " + str);
    }
    public static void error(String str){
        Bukkit.getLogger().severe(OctoCorePaper.prefix + " " + str);
    }
    public static void debug(String str){Bukkit.getLogger().info(OctoCorePaper.prefix + " [DEBUG] " + str);}
}
