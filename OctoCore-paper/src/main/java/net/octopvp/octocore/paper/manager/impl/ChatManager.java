package net.octopvp.octocore.paper.manager.impl;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ChatManager implements Manager {
    //TODO emojis (perm for each emoji)
    private static HashMap<String,String> emojis = new HashMap<>();
    static {
        emojis.put("<3","❤");
    }
    @Override
    public void init(OctoCore plugin) {}

    @Override
    public void disable(OctoCore plugin) {

    }
    public static void sendMessage(Player p){

    }
    public static String formatChat(UUID uuid, String displayname,String message,boolean translateColor){
        PlayerData profile = PlayerManager.getProfile(uuid);
        if(profile == null)
            return null;
        if(translateColor)
            return profile.getCurrentPrefix() + " " + profile.getMainColor() + displayname + CC.R + ": " + ChatColor.translateAlternateColorCodes('&',message);
        else return profile.getCurrentPrefix() + " " + profile.getMainColor() + displayname + CC.R + ": " + message;
    }
}