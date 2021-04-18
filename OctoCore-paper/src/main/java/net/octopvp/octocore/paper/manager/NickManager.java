package net.octopvp.octocore.paper.manager;

import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.objects.PlayerProfile;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class NickManager implements Manager{
    private static HashMap<PlayerProfile, String> nicked = new HashMap<>();

    public static HashMap<PlayerProfile, String> getNicked() {
        return NickManager.nicked;
    }

    @Override
    public void init(OctoCorePaper plugin) {

    }

    @Override
    public void disable(OctoCorePaper plugin) {

    }

    public static boolean isNicked(UUID uuid){
        try {
            PreparedStatement ps = OctoCorePaper.getConnection().prepareStatement("SELECT * FROM ");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
    public static void addNick(PlayerProfile profile, String nick){
        if(!nicked.containsKey(profile))
            nicked.put(profile, nick);
        else{
            removeNick(profile);
            nicked.put(profile,nick);
        }
    }
    public static void removeNick(PlayerProfile name){
        if(nicked.containsKey(name))
            nicked.remove(name);
    }
}
