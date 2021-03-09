package net.octopvp.octocore.paper.manager;

import lombok.Getter;
import net.octopvp.octocore.common.rank.LuckpermsManager;
import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.player.OctoPlayerProfile;
import net.octopvp.octocore.paper.utils.Logger;
import net.octopvp.octocore.paper.utils.database.DatabaseHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerManager implements Manager {
    @Getter
    private static HashMap<UUID, OctoPlayerProfile> playerProfiles = new HashMap<>();
    public static void processJoin(UUID uuid){
        //OctoPlayerProfile profile = loadProfileFromDB(uuid);
        OctoPlayerProfile profile = new OctoPlayerProfile(uuid);
        profile.setCoins(0);
        profile.setXp(0);
        playerProfiles.put(uuid, profile);
    }
    public static void processLeave(Player player){
        unloadProfile(player.getUniqueId());
    }
    public static void unloadProfile(UUID uuid){
        OctoPlayerProfile profile = playerProfiles.get(uuid);
        playerProfiles.remove(uuid);
    }
    public static OctoPlayerProfile loadProfileFromDB(UUID uuid){
        Logger.debug(DatabaseHelper.GET_PROFILE.getSql(uuid.toString()));
        boolean frozen = false;
        long coins = 0;
        long xp = 0;
        try {
            PreparedStatement ps = OctoCorePaper.getConnection().prepareStatement(DatabaseHelper.GET_PROFILE.getSql(uuid.toString()));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //TODO load profile stats here
        OctoPlayerProfile profile = new OctoPlayerProfile(uuid);
        profile.setFrozen(frozen);
        profile.setCoins(coins);
        profile.setXp(xp);
        return profile;
    }
    public static OctoPlayerProfile getProfile(UUID uuid){
        OctoPlayerProfile returnedprofile = null;
        if(!playerProfiles.containsKey(uuid))
            return null;
        return playerProfiles.get(uuid);
    }

    @Override
    public void init(OctoCorePaper plugin) {
        //repeating update player task
        Bukkit.getScheduler().scheduleSyncRepeatingTask(OctoCorePaper.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (UUID uuid : playerProfiles.keySet()){
                    OctoPlayerProfile profile = playerProfiles.get(uuid);
                    profile.setPrefix(LuckpermsManager.getPrefix(uuid));
                    profile.setMainColor(LuckpermsManager.getMainColor(uuid));
                }
            }
        },0l,45l);
    }
}
