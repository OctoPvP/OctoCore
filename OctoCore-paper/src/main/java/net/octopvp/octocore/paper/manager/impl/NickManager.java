package net.octopvp.octocore.paper.manager.impl;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.PlayerData;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class NickManager implements Manager {
    private static HashMap<PlayerData, String> nicked = new HashMap<>();

    public static HashMap<PlayerData, String> getNicked() {
        return NickManager.nicked;
    }

    @Override
    public void init(OctoCore plugin) {

    }

    @Override
    public void disable(OctoCore plugin) {

    }

    public static boolean isNicked(UUID uuid){
        try {
            PreparedStatement ps = OctoCore.getConnection().prepareStatement("SELECT * FROM ");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
    public static void addNick(PlayerData profile, String nick){
        if(!nicked.containsKey(profile))
            nicked.put(profile, nick);
        else{
            removeNick(profile);
            nicked.put(profile,nick);
        }
    }
    public static void removeNick(PlayerData name){
        if(nicked.containsKey(name))
            nicked.remove(name);
    }
}
