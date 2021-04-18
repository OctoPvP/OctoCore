package net.octopvp.octocore.paper.manager;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCorePaper;
import org.bukkit.ChatColor;

import java.util.UUID;

public class ChatManager implements Manager {
    @Override
    public void init(OctoCorePaper plugin) {}

    @Override
    public void disable(OctoCorePaper plugin) {

    }

    public static String formatChat(UUID uuid, String displayname,String message,boolean translateColor){
        if(translateColor)
            return PlayerManager.getProfile(uuid).getPrefix() + " " + displayname + CC.R + ": " + ChatColor.translateAlternateColorCodes('&',message);
        else return PlayerManager.getProfile(uuid).getPrefix() + " " + displayname + CC.R + ": " + message;
    }
}