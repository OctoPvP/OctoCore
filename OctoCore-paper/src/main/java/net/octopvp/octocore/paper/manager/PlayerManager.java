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
import net.octopvp.octocore.paper.utils.Logger;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager implements Manager {
    @Getter
    private static MongoCollection<Document> pdataCollection = null;
    @Getter
    private static MongoCollection<Document> backupCollection = null;
    @Getter
    private static HashMap<UUID, PlayerProfile> playerProfiles = new HashMap<>();

    /**
     * After the database is init
     */
    public static void postDBInit(){
        pdataCollection = DatabaseManager.getMongoDatabase().getCollection("pdata");
        backupCollection = DatabaseManager.getMongoDatabase().getCollection("backup");
    }

    /**
     * process player join
     * @param uuid
     */
    public static void processJoin(UUID uuid){
        PlayerProfile profile;
        if(DatabaseManager.doesDocumentExistByUUID(uuid))
            profile = loadProfileFromDB(uuid);
        else profile = createNewProfile(uuid);
        playerProfiles.put(uuid, profile);
        profile.getPlayer().setPlayerListName(profile.getMainColor() + profile.getPlayer().getDisplayName());
        NametagEdit.getApi().setPrefix(profile.getPlayer(),profile.getMainColor());
    }
    public static void processLeave(Player player){
        unloadProfile(player.getUniqueId());
    }

    /**
     * unloads a profile
     * @param uuid
     */
    public static void unloadProfile(UUID uuid){
        PlayerProfile profile = playerProfiles.get(uuid);
        saveProfile(profile);
        playerProfiles.remove(uuid);
    }

    /**
     * Loads a profile from db assuming its there
     * @param uuid
     * @return
     */
    public static PlayerProfile loadProfileFromDB(UUID uuid) {
        try {
            Logger.debug("Loading profile " + uuid.toString() + " from db.");
            PlayerProfile p = null;
            Document doc = getProfileDocument(uuid);
            String json = doc.toJson();
            Logger.debug("Json for profile " + uuid.toString() + " is: \n" + json);
            p = deserializeProfile(json);
            p.setLastLoaded(System.currentTimeMillis());
            return p;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * creates new profile
     * @param uuid
     * @return
     */
    public static PlayerProfile createNewProfile(UUID uuid){
        Logger.debug("Creating new profile for " + uuid.toString());
        PlayerProfile profile = new PlayerProfile(uuid);
        profile.setCoins(0);
        profile.setXp(0);
        profile.setFrozen(false);
        Logger.debug("Serializing profile");
        String json = serializeProfileToJson(profile);
        Document doc = Document.parse(json);
        Logger.debug("Inserting to db");
        if(DatabaseManager.doesDocumentExistByUUID(uuid)){
            Logger.debug("There is already a profile with the uuid " + uuid + "! moving to backup!");
            Document doc1 = getProfileDocument(uuid);
            backupCollection.insertOne(doc1);
            pdataCollection.deleteOne(doc1);
        }
        pdataCollection.insertOne(doc);
        return profile;
    }

    /**
     * saves a profile
     * @param profile
     */
    public static void saveProfile(PlayerProfile profile) {
        String json = serializeProfileToJson(profile);
        pdataCollection.updateOne(getProfileDocument(profile.getUuid()), Document.parse(json));
    }

    /**
     * Serializing profile to json
     * @param profile
     * @return
     */
    public static String serializeProfileToJson(PlayerProfile profile){
        return new Gson().toJson(profile);
    }

    /**
     * De-Serialize a profile from json
     * @param json
     * @return
     */
    public static PlayerProfile deserializeProfile(String json){
        return new Gson().fromJson(json,PlayerProfile.class);
    }

    /**
     * Get a cached profile (on server not redis)
     * @param uuid
     * @return
     */
    public static PlayerProfile getProfile(UUID uuid) {
        if (!playerProfiles.containsKey(uuid))
            return null;
        return playerProfiles.get(uuid);
    }

    /**
     * Gets the document of a profile (using uuid)
     * @param uuid
     * @return document
     */
    public static Document getProfileDocument(UUID uuid){
        return pdataCollection.find(Filters.eq("uuid",uuid.toString())).projection(Projections.excludeId()).first();
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

    /**
     * get the prefix of a player
     * @param uuid
     * @return prefix
     */
    public static String getPrefix(UUID uuid){
        return (Bukkit.getPluginManager().isPluginEnabled("Vault") && VaultManager.isChatHookEnabled()) ?
                VaultManager.getChat().getPlayerPrefix(Bukkit.getPlayer(uuid)) :
                LuckpermsManager.getPrefix(uuid);
    }
}
