package net.octopvp.octocore.paper.manager.impl;

import com.google.gson.JsonObject;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.viaversion.viaversion.api.Via;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.protocol.User;
import lombok.Getter;
import net.octopvp.octocore.common.object.AlertType;
import net.octopvp.octocore.common.object.HashedAddress;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.object.ServerType;
import net.octopvp.octocore.common.object.builder.SentryMessageBuilder;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.DatabaseManager;
import net.octopvp.octocore.paper.database.redis.packets.server.SaveRequestMiscPacket;
import net.octopvp.octocore.paper.database.redis.packets.server.SaveRequestSwitchPacket;
import net.octopvp.octocore.paper.database.redis.packets.staff.*;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.manager.impl.autoinit.BookManager;
import net.octopvp.octocore.paper.objects.GlobalPlayer;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.apache.commons.lang3.Validate;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager extends Manager {
    private static MongoCollection<Document> pdataCollection = null;
    private static MongoCollection<Document> backupCollection = null;
    @Getter
    private static final Map<UUID, PlayerData> playerProfiles = new ConcurrentHashMap<>();
    private static final JsonWriterSettings settings = JsonWriterSettings.builder()
            .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
            .build();

    public static PlayerData getPlayerData(String name) {
        return playerProfiles.values().stream().filter(profile -> profile.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static Map<UUID, PlayerData> getOnlineData() {
        return playerProfiles;
    }

    //get the data of a player that is not online
    public static CompletableFuture<PlayerData> getOfflineData(String name) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        return getOfflineData(op);
    }

    public static CompletableFuture<PlayerData> getOfflineData(UUID uuid) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        return getOfflineData(op);
    }

    public static CompletableFuture<PlayerData> getOfflineData(OfflinePlayer op) {
        CompletableFuture<PlayerData> completableFuture = new CompletableFuture<>();
        String name = op.getName();
        if (Bukkit.getPlayer(op.getUniqueId()) != null)
            completableFuture.complete(getData(op.getPlayer()));
        else if (OctoCore.getServerManager().isPlayerOnline(name)) {
            Tasks.runAsync(() -> {
                new SaveRequestMiscPacket(new JsonBuilder().addProperty("name", name)).send();
                PlayerData data = null;
                int tries = 0;
                while (data == null) {
                    try {
                        Thread.sleep(40);
                        if (tries == 5) //maybe the packet was dropped
                            new SaveRequestMiscPacket(new JsonBuilder().addProperty("name", name)).send();
                        if (Bukkit.getPlayer(name) != null) {
                            data = getData(Bukkit.getPlayer(name));
                            break;
                        }
                        if (tries >= 10) {
                            data = OctoCore.getGson().fromJson(getProfileJsonOnlineorOffline(name), PlayerData.class);
                            break;
                        }
                        if (getProfileDocument(name).getLong("lastSave") - System.currentTimeMillis() > 5000) {
                            data = OctoCore.getGson().fromJson(getProfileJsonOnlineorOffline(name), PlayerData.class);
                            break;
                        }
                        tries++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                completableFuture.complete(data);
            });
        } else {
            String json = getProfileJsonOnlineorOffline(name);
            if (json == null)
                completableFuture.complete(null);
            completableFuture.complete(OctoCore.getGson().fromJson(json, PlayerData.class));
        }
        return completableFuture;
    }

    public static void postDBInit() {
        pdataCollection = DatabaseManager.getMongoDatabase().getCollection("pdata");
        backupCollection = DatabaseManager.getMongoDatabase().getCollection("backup");
    }

    public static void processJoin(Player player, UUID uuid, String ip) {
        Sentry.addBreadcrumb(player.getName() + " joined");
        Tasks.runAsync(() -> {
            PlayerData profile = playerProfiles.get(uuid);
            if (profile == null) {
                Logger.debug("Profile is null!");
                captureSentryEvent("Profile is null!", player.getUniqueId(), player.getName());
                return;
            }
            PermissionManager.injectPermissible(player, profile);
            profile.setRankType(profile.getHighestRank().getRankType());

            profile.setLastSeenServer(OctoCore.getServerName());
            profile.setLastSeenIp(new HashedAddress(ip));
            profile.onJoin(player);

            GlobalPlayer globalPlayer = OctoCore.getServerManager().getGlobalPlayer(player.getName());
            if (globalPlayer != null && globalPlayer.getLastServer() != null && !globalPlayer.getLastServer().equalsIgnoreCase(OctoCore.getServerName())) {
                if (player.hasPermission(Permission.SEND_SWITCH_MESSAGE.getNode())) {
                    //sendStaffAlert(AlertType.SWITCH, player.getName(), globalPlayer.getLastServer(), OctoCore.getServerName());
                }
            } else {
                if (player.hasPermission(Permission.SEND_JOIN_MESSAGE.getNode())) {
                    //sendStaffAlert(AlertType.JOIN, player.getName(), OctoCore.getServerName());
                }
            }
            long time = profile.getWorldTime().getTime();
            if (time == -1)
                player.resetPlayerTime();
            else player.setPlayerTime(time, false);
            TabManager.onJoin(player);
            if (player.hasPermission(Permission.STAFF_MODULES.getNode()))
                LunarClientAPI.getInstance().giveAllStaffModules(player);
            Player finalPlayer = player;
            Tasks.run(() -> {
                ScoreBoardManager.handleJoin(finalPlayer);
                if (OctoCore.getServerType() == ServerType.HUB || OctoCore.getServerType() == ServerType.MASTER) {
                    int version = Via.getAPI().getPlayerVersion(finalPlayer);
                    if (version != 47 && version != -1) {
                        BookManager.showUnsupportedVerBook(finalPlayer);
                    }
                }

            });
        });
    }

    public static void captureSentryEvent(String eventName, Player player) {
        captureSentryEvent(eventName, player.getUniqueId(), player.getName());
    }

    public static void captureSentryEvent(String eventName, UUID uuid, String name) {
        if (Sentry.isEnabled()) {
            SentryEvent event = new SentryEvent();
            event.setLevel(SentryLevel.ERROR);
            event.setMessage(new SentryMessageBuilder().setMessage(eventName).build());
            User user = new User();
            user.setUsername(name);
            user.setId(uuid.toString());
            event.setUser(user);
            Sentry.captureEvent(event);
        }
    }

    public static void loadPData(UUID uuid, String name, boolean saveState) {
        try {
            PlayerData profile;
            boolean b = !doesDocumentExistByUUID(uuid), passed = !OctoCore.getServerManager().isPlayerOnline(name), a = false;
            int tries = 0;
            while (!passed) {
                PlayerData.SaveState state = getState(uuid);
                if (state == null)
                    break;
                if (state == PlayerData.SaveState.SAVING) {
                    if (tries > 10) {
                        Logger.debug("Tried to load player data for " + name + " but it was still saving!");
                        return;
                    }
                    Logger.debug("Waiting 50 millis, then requesting savestate again");
                    Thread.sleep(50);//oh no
                    tries++;
                    if (!a) {
                        new SaveRequestSwitchPacket(uuid).send();
                        a = true;
                    }
                } else {
                    break;
                }
            }
            if (!b)
                profile = loadProfileFromDB(uuid, true);
            else profile = createNewProfile(uuid, name);
            playerProfiles.put(uuid, profile);
            if (saveState)
                setSavingState(uuid);
        } catch (Exception e) {
            System.err.println("------------------------------");
            e.printStackTrace();
            System.err.println("------------------------------");
        }
    }

    /**
     * runs asynchronously, blocks the thread untill the data is saved
     *
     * @param uuid
     * @return
     */
    public static CompletableFuture<PlayerData> waitForData(UUID uuid) {
        CompletableFuture<PlayerData> future = new CompletableFuture<>();
        Tasks.runAsync(() -> {

        });
        return future;
    }

    public static void setSavingState(UUID uuid) {
        BasicDBObject query = new BasicDBObject(), update = new BasicDBObject(), newDoc = new BasicDBObject();
        query.put("uuid", uuid.toString());
        update.put("$set", newDoc);
        newDoc.put("saveState", PlayerData.SaveState.SAVING.name());
        getPdataCollection().updateOne(query, update);
    }

    public static void processLeave(Player player) {
        PlayerData playerData = getProfile(player.getUniqueId());
        if (playerData == null)
            return;
        playerData.setSaveState(PlayerData.SaveState.SAVED);
        unloadProfile(player.getUniqueId());
        Tasks.runLater(() -> {
            if (playerData.isOnline()) {
                //sendStaffAlert(AlertType.LEAVE, player.getName(), OctoCore.getServerName());
            }
        }, 45l);
    }

    /**
     * unloads a profile
     *
     * @param uuid
     */
    public static void unloadProfile(UUID uuid) {
        PlayerData profile = playerProfiles.get(uuid);
        playerProfiles.remove(uuid);
        saveProfile(profile);
    }

    public static PlayerData.SaveState getState(UUID uuid) {
        Document document = getProfileDocument(uuid);
        if (document == null)
            return null;
        return PlayerData.SaveState.valueOf(document.getString("saveState"));
    }

    public static long getLastSave(UUID uuid) {
        Document document = getProfileDocument(uuid);
        if (document == null)
            return -1;
        return document.getLong("lastSave");
    }

    /**
     * Loads a profile from db assuming its there
     *
     * @param uuid
     * @return
     */
    public static PlayerData loadProfileFromDB(UUID uuid, boolean saveState) {
        try {
            PlayerData p;
            Document doc = getProfileDocument(uuid);
            String json = getProfileJson(doc);
            p = deserializeProfile(json);
            p.setLastLoaded(System.currentTimeMillis());
            p.setLastLogin(System.currentTimeMillis());
            p.onLoad(doc);
            if (saveState)
                setSavingState(uuid);
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * creates new profile
     *
     * @param uuid
     * @return
     */
    public static PlayerData createNewProfile(UUID uuid, String name) {
        Logger.debug("Creating new profile for " + uuid.toString() + " | " + name);
        PlayerData profile = new PlayerData(uuid, name);
        profile.setCoins(0);
        profile.setXp(0);
        profile.setFrozen(false);
        profile.setLastLogin(System.currentTimeMillis());
        profile.setName(name);
        Logger.debug("Serializing profile");
        String json = serializeProfileToJson(profile);
        Document doc = Document.parse(json);
        Logger.debug("Inserting to db");
        if (doesDocumentExistByUUID(uuid)) {
            Logger.debug("There is already a profile with the uuid " + uuid + "! moving to backup!");
            Document doc1 = getProfileDocument(uuid);
            backupCollection.insertOne(doc1);
            pdataCollection.deleteOne(doc1);
        }
        pdataCollection.insertOne(doc);
        return profile;
    }

    public static String getProfileJson(Document document) {
        return (document != null ? document.toJson(settings) : null);
    }

    /**
     * saves a profile assuming the profile is <b>already there</b>
     *
     * @param profile
     */
    public static void saveProfile(PlayerData profile) {
        if (profile == null)
            return;
        profile.setLastSave(System.currentTimeMillis());
        String json = serializeProfileToJson(profile);
        //Logger.debug("Saving profile: \nUUID:" + profile.getUuid() + "\nJSON: " + json);
        pdataCollection.replaceOne(getProfileDocument(profile.getUuid()), Document.parse(json), new ReplaceOptions().upsert(true));
        profile.setLastDataSave(0);
    }

    /**
     * get a profile without actually loading it as a player
     *
     * @param uuid
     * @return
     */
    public static PlayerData getProfileFromDB(UUID uuid) {
        Document doc = getProfileDocument(uuid);
        PlayerData profile = deserializeProfile(getProfileJson(doc));
        if (profile == null)
            return null;
        profile.onLoad(doc);
        return profile;
    }

    public static PlayerData getProfileFromDB(String name) {
        Document doc = getProfileDocument(name);
        PlayerData profile = deserializeProfile(getProfileJson(doc));
        if (profile == null)
            return null;
        profile.onLoad(doc);
        return profile;
    }

    /**
     * Serializing profile to json
     *
     * @param profile
     * @return
     */
    public static String serializeProfileToJson(PlayerData profile) {
        if (profile == null)
            return null;
        return OctoCore.getGson().toJson(profile);
    }

    /**
     * De-Serialize a profile from json
     *
     * @param json
     * @return
     */
    public static PlayerData deserializeProfile(String json) {
        return json != null && !json.equals("") ? OctoCore.getGson().fromJson(json, PlayerData.class) : null;
    }

    /**
     * Get a cached profile (on server not redis)
     *
     * @param uuid
     * @return
     */
    public static PlayerData getProfile(UUID uuid) {
        Validate.notNull(uuid);
        return playerProfiles.get(uuid);
    }

    public static PlayerData getPlayerData(UUID uuid) {
        return getProfile(uuid);
    }

    public static PlayerData getData(UUID uuid) {
        return getPlayerData(uuid);
    }

    public static PlayerData getProfile(Player player) {
        return getProfile(player.getUniqueId());
    }

    public static PlayerData getData(Player player) {
        return getProfile(player);
    }

    /**
     * Gets the document of a profile (using uuid)
     *
     * @param uuid
     * @return document
     */
    public static Document getProfileDocument(UUID uuid) {
        String a = uuid.toString();
        return pdataCollection.find(Filters.eq("uuid", a)).first();
    }

    /**
     * Gets the document of a profile (using name)
     *
     * @param name
     * @return document
     */
    public static Document getProfileDocument(String name) {
        return pdataCollection.find(Filters.eq("lastKnownName", name)).first();
    }

    public static MongoCollection<Document> getPdataCollection() {
        return PlayerManager.pdataCollection;
    }

    public static MongoCollection<Document> getBackupCollection() {
        return PlayerManager.backupCollection;
    }

    public static boolean doesDocumentExistByUUID(UUID uuid) {
        Document document = getPdataCollection().find(Filters.eq("uuid", uuid.toString())).first();
        return document != null;
    }

    public static boolean doesDocumentExistByName(String name) {
        return getPdataCollection().find(Filters.eq("name", name)).first() != null;
    }

    @Override
    public void init(OctoCore plugin) {
        //LuckpermsManager.getLuckPerms().getEventBus().subscribe(OctoCore.getInstance(),UserDataRecalculateEvent.class, this::onLpDataUpdate);
    }
    /*
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
     */

    @Override
    public void disable() {

    }

    /**
     * get the prefix of a player
     *
     * @param uuid
     * @return prefix
     */
    public static String getPrefix(UUID uuid) {
        if (getProfile(uuid).isNicked()) {
            return getProfile(uuid).getNickPrefix();
        }
        return (Bukkit.getPluginManager().isPluginEnabled("Vault") && VaultManager.isChatHookEnabled()) ?
                VaultManager.getChat().getPlayerPrefix(Bukkit.getPlayer(uuid)) :
                getProfile(uuid).getPrefix();
    }

    public static String getPrefix(PlayerData pdata) {
        if (pdata.isNicked()) {
            return pdata.getNickPrefix();
        }
        return (Bukkit.getPluginManager().isPluginEnabled("Vault") && VaultManager.isChatHookEnabled()) ?
                VaultManager.getChat().getPlayerPrefix(Bukkit.getPlayer(pdata.getUuid())) :
                pdata.getPrefix();
    }

    public static void sendStaffAlert(AlertType type, String... placeholders) {
        if (type == AlertType.JOIN) {
            new StaffConnectPacket(placeholders[0], placeholders[1]).send();
        } else if (type == AlertType.LEAVE) {
            new StaffLeavePacket(placeholders[0], placeholders[1]).send();
        } else if (type == AlertType.SWITCH) {
            new StaffSwitchPacket(placeholders[0], placeholders[1], placeholders[2]).send();
        }
    }

    public static void sendStaffChat(String player, String message, String server) {
        new StaffChatPacket(player, message, server).send();
    }

    public static void sendAdminChat(String player, String message, String server) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", player);
        jsonObject.addProperty("message", message);
        jsonObject.addProperty("server", server);
        new AdminChatPacket(player, server, message).send();
    }

    public static void sendStaffChat(Player p, String message, String server) {
        String player = PlayerManager.getPrefix(p.getUniqueId()) + " " + p.getName();
        new StaffChatPacket(player, message, server, p.getUniqueId()).send();
    }

    public static void sendAdminChat(Player p, String message, String server) {
        String player = PlayerManager.getPrefix(p.getUniqueId()) + " " + p.getName();
        new AdminChatPacket(player, server, message, p.getUniqueId()).send();
    }

    public static List<String> getOnlinePlayersString() {
        List<String> a = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            a.add(onlinePlayer.getName());
        }
        return a;
    }

    public static String getProfileJsonOnlineorOffline(String name) {
        if (Bukkit.getPlayer(name) != null)
            return serializeProfileToJson(getProfile(Bukkit.getPlayer(name)));
        if (doesDocumentExistByName(name))
            return serializeProfileToJson(getProfileFromDB(name));
        return null;
    }

    public static String getFixedName(String name) {
        Document document = pdataCollection.find(Filters.eq("lowerName", name.toLowerCase())).first();
        if (document == null) return name;
        return document.getString("name");
    }

    public static void deleteData(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) return;
        playerProfiles.remove(uuid);
    }

    public static void saveAllData() {
        Tasks.runAsync(() -> Bukkit.getOnlinePlayers().forEach(player -> saveProfile(getProfile(player))));
    }
}
