package net.octopvp.octocore.paper.manager.impl;

import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.octopvp.octocore.common.object.AlertType;
import net.octopvp.octocore.common.object.HashedAddress;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.object.JedisAction;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.GlobalPlayer;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.enums.RankType;
import net.octopvp.octocore.paper.utils.Logger;
import net.octopvp.octocore.paper.utils.permission.Permission;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager extends Manager {
    private static MongoCollection<Document> pdataCollection = null;
    private static MongoCollection<Document> backupCollection = null;
    @Getter
    private static Map<UUID, PlayerData> playerProfiles = new ConcurrentHashMap<>();
    private static JsonWriterSettings settings = JsonWriterSettings.builder()
            .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
            .build();

    /**
     * After the database is initialized
     */
    public static void postDBInit(){
        pdataCollection = DatabaseManager.getMongoDatabase().getCollection("pdata");
        backupCollection = DatabaseManager.getMongoDatabase().getCollection("backup");
    }

    /**
     * process player join
     * @param uuid
     */
    public static void processJoin(UUID uuid,String ip) {
        Tasks.runAsync(()->{
            PlayerData profile = playerProfiles.get(uuid);
            if(profile == null)
                Logger.debug("Profile is null!");
            profile.setRankType(RankType.getRankType(uuid));
            profile.setLastSeenServer(OctoCore.getServerName());
            profile.setLastSeenIp(new HashedAddress(ip));
            profile.reloadLuckPermsThings();
            Player player = Bukkit.getPlayer(uuid);
            if (player == null)
                return;
            GlobalPlayer globalPlayer = OctoCore.getServerManager().getGlobalPlayer(player.getName());
            if (globalPlayer != null && globalPlayer.getLastServer() != null && !globalPlayer.getLastServer().equalsIgnoreCase(OctoCore.getServerName())) {
                if (player.hasPermission(Permission.SEND_SWITCH_MESSAGE.getNode())){
                    sendStaffAlert(AlertType.SWITCH,player.getName(),globalPlayer.getLastServer(),OctoCore.getServerName());
                }
            }else{
                if (player.hasPermission(Permission.SEND_JOIN_MESSAGE.getNode())){
                    sendStaffAlert(AlertType.JOIN,player.getName(),OctoCore.getServerName());
                }
            }
        });
    }
    public static void loadPData(UUID uuid){
        try{
            PlayerData profile;
            if(doesDocumentExistByUUID(uuid)){
                profile = loadProfileFromDB(uuid);
            }else profile = createNewProfile(uuid);
            playerProfiles.put(uuid, profile);
        } catch (Exception e) {
            System.err.println("------------------------------");
            e.printStackTrace();
            System.err.println("------------------------------");
        }
    }

    public static void processLeave(Player player){
        PlayerData playerData = getProfile(player.getUniqueId());
        playerData.reloadLuckPermsThings();
        unloadProfile(player.getUniqueId());
        Tasks.runLater(()->{
            if (playerData.isOnline()){
                sendStaffAlert(AlertType.LEAVE,player.getName(),OctoCore.getServerName());
            }
        },45l);
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
            String json = getProfileJson(doc);
            Logger.debug("Json for profile " + uuid + " is: \n" + json);
            p = deserializeProfile(json);
            p.setLastLoaded(System.currentTimeMillis());
            p.setLastLogin(System.currentTimeMillis());
            p.setRankName(LuckpermsManager.getUser(uuid).getPrimaryGroup());
            return p;
        } catch (Exception e) {
            e.printStackTrace();
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

    public static String getProfileJson(Document document){
        return (document != null ? document.toJson(settings) : null);
    }

    /**
     * saves a profile assuming the profile is <b>already there</b>
     * @param profile
     */
    public static void saveProfile(PlayerData profile) {
        if(profile == null)
            return;
        String json = serializeProfileToJson(profile);
        Logger.debug("Saving profile: \nUUID:" + profile.getUuid() + "\nJSON: " + json);
        pdataCollection.replaceOne(getProfileDocument(profile.getUuid()),Document.parse(json));
    }

    /**
     * get a profile without actually loading it as a player
     * @param uuid
     * @return
     */
    public static PlayerData getProfileFromDB(UUID uuid){
        PlayerData profile = deserializeProfile(getProfileJson(getProfileDocument(uuid)));
        if (profile == null)
            return null;
        profile.setLastLoaded(System.currentTimeMillis());
        return profile;
    }
    public static PlayerData getProfileFromDB(String name){
        PlayerData profile = deserializeProfile(getProfileJson(getProfileDocument(name)));
        if (profile == null)
            return null;
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
        return json != null && !json.equals("") ? OctoCore.getGson().fromJson(json, PlayerData.class) : null;
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
        String a = uuid.toString();
        return pdataCollection.find(Filters.eq("_id",a)).first();
    }
    /**
     * Gets the document of a profile (using name)
     * @param name
     * @return document
     */
    public static Document getProfileDocument(String name){
        return pdataCollection.find(Filters.eq("lastKnownName",name)).first();
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
        profile.setRankType(RankType.getRankType(uuid));
    }
    public void refreshProfile(PlayerData profile){
        UUID uuid = profile.getUuid();
        profile.setPrefix(getPrefix(uuid));
        profile.setMainColor(LuckpermsManager.getMainColor(uuid));
    }

    @Override
    public void disable() {

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
    public static String getPrefix(PlayerData pdata){
        if(pdata.isNicked()){
            return pdata.getNickPrefix();
        }
        return (Bukkit.getPluginManager().isPluginEnabled("Vault") && VaultManager.isChatHookEnabled()) ?
                VaultManager.getChat().getPlayerPrefix(Bukkit.getPlayer(pdata.getUuid())) :
                LuckpermsManager.getPrefix(pdata.getUuid());
    }

    public static void sendStaffAlert(AlertType type, String... placeholders) {
        if(type == AlertType.JOIN) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name",placeholders[0]);
            jsonObject.addProperty("server",placeholders[1]);
            OctoCore.getInstance().getRedisData().write(JedisAction.STAFF_CONNECT, jsonObject);
            Logger.debug("Writing staff connect");
        }else if(type == AlertType.LEAVE){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name",placeholders[0]);
            jsonObject.addProperty("server",placeholders[1]);
            OctoCore.getInstance().getRedisData().write(JedisAction.STAFF_DISCONNECT, jsonObject);
            Logger.debug("Writing staff disconnect");
        }else if(type == AlertType.SWITCH){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name",placeholders[0]);
            jsonObject.addProperty("from",placeholders[1]);
            jsonObject.addProperty("to",placeholders[2]);
            OctoCore.getInstance().getRedisData().write(JedisAction.STAFF_SWITCH, jsonObject);
            Logger.debug("Writing staff switch");
        }
    }
    public static void sendStaffChat(String player, String message, String server){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name",player);
        jsonObject.addProperty("message",message);
        jsonObject.addProperty("server",server);
        OctoCore.getInstance().getRedisData().write(JedisAction.STAFF_CHAT, jsonObject);
    }
    public static void sendAdminChat(String player, String message, String server) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name",player);
        jsonObject.addProperty("message",message);
        jsonObject.addProperty("server",server);
        OctoCore.getInstance().getRedisData().write(JedisAction.ADMIN_CHAT, jsonObject);
    }
    public static void sendStaffChat(Player p, String message, String server){
        JsonObject jsonObject = new JsonObject();
        String player = PlayerManager.getPrefix(p.getUniqueId()) + " " + p.getName();
        jsonObject.addProperty("name",player);
        jsonObject.addProperty("message",message);
        jsonObject.addProperty("server",server);
        jsonObject.addProperty("uuid",p.getUniqueId() + "");
        OctoCore.getInstance().getRedisData().write(JedisAction.STAFF_CHAT, jsonObject);
    }
    public static void sendAdminChat(Player p, String message, String server) {
        JsonObject jsonObject = new JsonObject();
        String player = PlayerManager.getPrefix(p.getUniqueId()) + " " + p.getName();
        jsonObject.addProperty("name",player);
        jsonObject.addProperty("message",message);
        jsonObject.addProperty("server",server);
        jsonObject.addProperty("uuid",p.getUniqueId() + "");
        OctoCore.getInstance().getRedisData().write(JedisAction.ADMIN_CHAT, jsonObject);
    }
    public static List<String> getOnlinePlayersString(){
        List<String> a = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            a.add(onlinePlayer.getName());
        }
        return a;
    }
}
