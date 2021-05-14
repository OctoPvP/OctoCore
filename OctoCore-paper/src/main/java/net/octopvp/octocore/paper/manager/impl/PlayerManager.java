package net.octopvp.octocore.paper.manager.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.octopvp.octocore.paper.manager.LuckpermsManager;
import net.octopvp.octocore.paper.utils.errorhandling.ErrorData;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.HandleError;
import net.octopvp.octocore.paper.utils.Logger;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager implements Manager {
    private static MongoCollection<Document> pdataCollection = null;
    private static MongoCollection<Document> backupCollection = null;
    @Getter
    private static ConcurrentHashMap<UUID, PlayerData> playerProfiles = new ConcurrentHashMap<>();

    /**
     * After the database is initialized
     */
    public static void postDBInit(){
        pdataCollection = DatabaseManager.getMongoDatabase().getCollection("pdata");
        backupCollection = DatabaseManager.getMongoDatabase().getCollection("backup");
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(OctoCore.getInstance(), () -> {
            for (PlayerData value : playerProfiles.values()) {
                value.setPlayTime(value.getPlayTime()+1);
            }
        },0l,1200l);
    }

    /**
     * process player join
     * @param uuid
     */
    public static void processJoin(UUID uuid){

        //give a few ticks for profile to save incase
        //TODO redis cache
        Bukkit.getScheduler().scheduleSyncDelayedTask(OctoCore.getInstance(), () -> {
            try{
                PlayerData profile;
                if(doesDocumentExistByUUID(uuid))
                    profile = loadProfileFromDB(uuid);
                else profile = createNewProfile(uuid);
                playerProfiles.put(uuid, profile);
            } catch (Exception e) {
                e.printStackTrace();
                ErrorData ed = new ErrorData();
                ed.addData("OnPlayerJoin","");
                ed.addData("UUID",uuid.toString());
                HandleError.handlePlayerErrorNoPdata(ed,Bukkit.getPlayer(uuid),e,true);
            }
        },5l);
    }
    public static void processLeave(Player player){
        unloadProfile(player.getUniqueId());
    }

    /**
     * unloads a profile
     * @param uuid
     */
    public static void unloadProfile(UUID uuid){
        PlayerData profile = playerProfiles.get(uuid);
        playerProfiles.remove(uuid);
        saveProfile(profile);
    }

    /**
     * Loads a profile from db assuming its there
     * @param uuid
     * @return
     */
    public static PlayerData loadProfileFromDB(UUID uuid) {
        try {
            Logger.debug("Loading profile " + uuid.toString() + " from db.");
            PlayerData p;
            Document doc = getProfileDocument(uuid);
            String json = doc.toJson();
            Logger.debug("Json for profile " + uuid + " is: \n" + json);
            p = deserializeProfile(json);
            p.setLastLoaded(System.currentTimeMillis());
            p.setLastLogin(System.currentTimeMillis());
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
    public static PlayerData createNewProfile(UUID uuid){
        Logger.debug("Creating new profile for " + uuid.toString());
        PlayerData profile = new PlayerData(uuid);
        profile.setCoins(0);
        profile.setXp(0);
        profile.setFrozen(false);
        profile.setLastLogin(System.currentTimeMillis());
        Logger.debug("Serializing profile");
        String json = serializeProfileToJson(profile);
        Document doc = Document.parse(json);
        Logger.debug("Inserting to db");
        if(doesDocumentExistByUUID(uuid)){
            Logger.debug("There is already a profile with the uuid " + uuid + "! moving to backup!");
            Document doc1 = getProfileDocument(uuid);
            backupCollection.insertOne(doc1);
            pdataCollection.deleteOne(doc1);
        }
        pdataCollection.insertOne(doc);
        return profile;
    }

    /**
     * saves a profile assuming the profile is <b>already there</b>
     * @param profile
     */
    public static void saveProfile(PlayerData profile) {
        String json = serializeProfileToJson(profile);
        Logger.debug("Saving profile: \nUUID:" + profile.getUuid() + "\nJSON: " + json);
        //pdataCollection.updateOne(getProfileDocument(profile.getUuid()),Document.parse(json));
        pdataCollection.findOneAndUpdate(getProfileDocument(profile.getUuid()),Document.parse(json));
    }

    /**
     * get a profile without ctually loading it as a player
     * @param uuid
     * @return
     */
    public static PlayerData getProfileFromDB(UUID uuid){
        PlayerData profile = deserializeProfile(getProfileDocument(uuid).toJson());
        profile.setLastLoaded(System.currentTimeMillis());
        return profile;
    }

    /**
     * Serializing profile to json
     * @param profile
     * @return
     */
    public static String serializeProfileToJson(PlayerData profile){
        return OctoCore.getGson().toJson(profile);
    }

    /**
     * De-Serialize a profile from json
     * @param json
     * @return
     */
    public static PlayerData deserializeProfile(String json){
        return OctoCore.getGson().fromJson(json, PlayerData.class);
    }

    /**
     * Get a cached profile (on server not redis)
     * @param uuid
     * @return
     */
    public static PlayerData getProfile(UUID uuid) {
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
        return pdataCollection.find(Filters.eq("_id",uuid.toString())).first();
    }

    public static MongoCollection<Document> getPdataCollection() {
        return PlayerManager.pdataCollection;
    }

    public static MongoCollection<Document> getBackupCollection() {
        return PlayerManager.backupCollection;
    }

    public static boolean doesDocumentExistByUUID(UUID uuid){
        Document document = getPdataCollection().find(Filters.eq("uuid", uuid.toString())).first();
        return document != null;
    }

    @Override
    public void init(OctoCore plugin) {
        /*
        TODO check if player has permission to change name color if not, then use thier rank's default color
         */
        //repeating update player task
        /*
        Bukkit.getScheduler().scheduleSyncRepeatingTask(OctoCore.getInstance(), () -> {
            for (UUID uuid : playerProfiles.keySet()){
                PlayerProfile profile = playerProfiles.get(uuid);
                profile.setPrefix(getPrefix(uuid));
                profile.setMainColor(LuckpermsManager.getMainColor(uuid));
            }
        },0l, OctoCore.getInstance().getConfig().getLong("update-pdata-interval"));
         */
        LuckpermsManager.getLuckPerms().getEventBus().subscribe(OctoCore.getInstance(),UserDataRecalculateEvent.class, this::onLpDataUpdate);
    }
    public void onLpDataUpdate(UserDataRecalculateEvent event){
        PlayerData profile = playerProfiles.get(event.getUser().getUniqueId());
        Logger.debug("Lp data update event: User: " + event.getUser() + " Data: " + event.getData());
        if(profile == null)
            return;
        UUID uuid = event.getUser().getUniqueId();
        profile.setPrefix(getPrefix(uuid));
        profile.setMainColor(LuckpermsManager.getMainColor(uuid));
    }
    public void refreshProfile(PlayerData profile){
        UUID uuid = profile.getUuid();
        profile.setPrefix(getPrefix(uuid));
        profile.setMainColor(LuckpermsManager.getMainColor(uuid));
    }

    @Override
    public void disable(OctoCore plugin) {

    }

    /**
     * get the prefix of a player
     * @param uuid
     * @return prefix
     */
    public static String getPrefix(UUID uuid){
        if(getProfile(uuid).isNicked()){
            return getProfile(uuid).getNickPrefix();
        }
        return (Bukkit.getPluginManager().isPluginEnabled("Vault") && VaultManager.isChatHookEnabled()) ?
                VaultManager.getChat().getPlayerPrefix(Bukkit.getPlayer(uuid)) :
                LuckpermsManager.getPrefix(uuid);
    }
}
