package net.octopvp.octocore.core.manager.impl;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.Manager;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ChatManager extends Manager {
    //TODO emojis (perm for each emoji)
    private static final HashMap<String, String> emojis = new HashMap<>();

    static {
        emojis.put("<3", "❤");
    }

    public static void sendMessage(Player p) {

    }

    public static String formatChat(Player player, String message, boolean translateColor) {
        PlayerData profile = PlayerManager.getInstance().getData(player);
        if (profile == null)
            return null;
        String name = profile.getFormattedName(true, player);
        if (translateColor)
            return name + CC.WHITE + ": " + CC.translate(message);
        else return name + CC.WHITE + ": " + message;
    }

    @Override
    public void init(OctoCore plugin) {
    }

    @Override
    public void disable() {

    }
}
