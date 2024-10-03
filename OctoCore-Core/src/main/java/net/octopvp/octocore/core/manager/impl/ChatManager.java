package net.octopvp.octocore.core.manager.impl;

import net.kyori.adventure.text.Component;
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

    public static String formatChat(Player player, String message, boolean translateColor) {
        PlayerData profile = PlayerManager.getInstance().getData(player);
        if (profile == null)
            return null;
        String name = profile.getFormattedName(true, player);
        if (translateColor)
            return name + CC.WHITE + ": " + CC.translate(message);
        else return name + CC.WHITE + ": " + message;
    }

    public static Component formatChatComponent(Player player, Component message, boolean translateColor) {
        PlayerData profile = PlayerManager.getInstance().getData(player);
        if (profile == null) return null;
        Component name = profile.getFormattedNameComponent(true, player);
        return Component.text().append(name).append(Component.text(": ")).append(message).build();
    }

    @Override
    public void init(OctoCore plugin) {
    }

    @Override
    public void disable() {

    }
}
