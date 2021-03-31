package net.octopvp.octocore.paper.manager;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.nametagedit.plugin.NametagEdit;
import lombok.Getter;
import net.octopvp.octocore.common.rank.LuckpermsManager;
import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.player.PlayerProfile;
import net.octopvp.octocore.paper.utils.nametag.NameTagChanger;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager implements Manager {
    @Getter
    private static MongoCollection<Document> pdataCollection = null;
    @Getter
    private static HashMap<UUID, PlayerProfile> playerProfiles = new HashMap<>();
    public static void postDBInit(){
        pdataCollection = DatabaseManager.getMongoDatabase().getCollection("pdata");
    }
    public static void processJoin(UUID uuid){
        //OctoPlayerProfile profile = loadProfileFromDB(uuid);
        PlayerProfile profile = new PlayerProfile(uuid);
        profile.setCoins(0);
        profile.setXp(0);
        playerProfiles.put(uuid, profile);
        profile.getPlayer().setPlayerListName(profile.getMainColor() + profile.getPlayer().getDisplayName());
        NametagEdit.getApi().setPrefix(profile.getPlayer(),profile.getMainColor());
    }
    public static void processLeave(Player player){
        unloadProfile(player.getUniqueId());
    }
    public static void unloadProfile(UUID uuid){
        //PlayerProfile profile = playerProfiles.get(uuid);
        playerProfiles.remove(uuid);
    }
    public static PlayerProfile loadProfileFromDB(UUID uuid){
        //TODO load profile stats here
        Document doc = pdataCollection.find(Filters.eq("uuid",uuid.toString())).projection(Projections.excludeId()).first();
        String json = doc.toJson();
        return deserializeProfile(json);
    }
    public static void saveProfile(UUID uuid) {
        PlayerProfile profile = getProfile(uuid);
        String json = serializeProfileToJson(profile);
    }
    public static String serializeProfileToJson(PlayerProfile profile){
        return new Gson().toJson(profile);
    }
    public static PlayerProfile deserializeProfile(String json){
        return new Gson().fromJson(json,PlayerProfile.class);
    }
    public static PlayerProfile getProfile(UUID uuid){
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
                    PlayerProfile profile = playerProfiles.get(uuid);
                    profile.setPrefix(getPrefix(uuid));
                    profile.setMainColor(LuckpermsManager.getMainColor(uuid));
                }
            }
        },0l,OctoCorePaper.getInstance().getConfig().getLong("update-tab-interval"));
    }
    public static String getPrefix(UUID uuid){
        return (Bukkit.getPluginManager().isPluginEnabled("Vault") && VaultManager.isChatHookEnabled()) ?
                VaultManager.getChat().getPlayerPrefix(Bukkit.getPlayer(uuid)) :
                LuckpermsManager.getPrefix(uuid);
    }
}
