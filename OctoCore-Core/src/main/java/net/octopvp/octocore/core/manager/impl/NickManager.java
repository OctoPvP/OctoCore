package net.octopvp.octocore.core.manager.impl;

import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.Manager;
import net.octopvp.octocore.core.objects.PlayerData;

import java.util.HashMap;

public class NickManager extends Manager {
    private static final HashMap<PlayerData, String> nicked = new HashMap<>();

    public static HashMap<PlayerData, String> getNicked() {
        return NickManager.nicked;
    }

    public static void addNick(PlayerData profile, String nick) {
        if (!nicked.containsKey(profile))
            nicked.put(profile, nick);
        else {
            removeNick(profile);
            nicked.put(profile, nick);
        }
    }

    public static void removeNick(PlayerData name) {
        nicked.remove(name);
    }

    @Override
    public void init(OctoCore plugin) {

    }

    @Override
    public void disable() {

    }
}
