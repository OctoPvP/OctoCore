package net.octopvp.octocore.paper.manager.impl;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ChatManager extends Manager {
    //TODO emojis (perm for each emoji)
    private static HashMap<String,String> emojis = new HashMap<>();
    static {
        emojis.put("<3","❤");
    }
    @Override
    public void init(OctoCore plugin) {}

    @Override
    public void disable() {

    }
    public static void sendMessage(Player p){

    }
    public static String formatChat(Player player,String message,boolean translateColor){
        PlayerData profile = PlayerManager.getProfile(player);
        if(profile == null)
            return null;
        if(translateColor)
            return profile.getFormattedName(true,player) + CC.WHITE + ": " + CC.translate(message);
        else return profile.getFormattedName(true,player) + CC.WHITE + ": " + message;
    }
}