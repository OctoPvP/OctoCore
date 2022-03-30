package net.octopvp.octocore.paper.manager.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import net.octopvp.octocore.common.object.ObjectConsumer;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.packets.player.GlobalPlayerStatusUpdatePacket;
import net.octopvp.octocore.paper.database.redis.packets.staff.StaffLeavePacket;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.GlobalPlayer;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager extends Manager {
    @Getter
    private static PlayerManager instance;

    @Getter
    private MongoCollection<Document> pdataCollection = null;

    @Getter
    private final Map<UUID, PlayerData> playerProfiles = new ConcurrentHashMap<>();
    @Getter
    private Map<UUID, Integer> quitting = new HashMap<>();


    public PlayerData getData(UUID uuid) {
        return playerProfiles.get(uuid);
    }


    public PlayerData getData(Player player) {
        return getData(player.getUniqueId());
    }

    public PlayerData getData(String name) {
        return playerProfiles.entrySet().stream().filter(entry -> entry.getValue().getName().equalsIgnoreCase(name)).findFirst().orElse(null).getValue();
    }

    public PlayerData getOfflineData(UUID uuid) {
        PlayerData data = getData(uuid);
        if (data != null) {
            return data;
        }
        Document document = pdataCollection.find(Filters.eq("uuid", uuid.toString())).first();

        if (document == null) {
            return null;
        }
        return new PlayerData(uuid, document.getString("name"));
    }

    public PlayerData getOfflineData(String name) {
        PlayerData data = getData(name);
        if (data != null) {
            return data;
        }
        Document document = pdataCollection.find(Filters.eq("lowerCaseName", name.toLowerCase())).first();
        if (document == null) {
            return null;
        }
        return new PlayerData(UUID.fromString(document.getString("uuid")), document.getString("name"));
    }

    public void modifyData(UUID uuid, ObjectConsumer<PlayerData> callback) {
        boolean offline = Bukkit.getPlayer(uuid) == null;
        PlayerData data = getOfflineData(uuid);
        if (offline)
            data.load();
        callback.run(data, PlayerData::save);
    }

    public void modifyData(String name, ObjectConsumer<PlayerData> callback) {
        boolean offline = Bukkit.getPlayer(name) == null;
        PlayerData data = getOfflineData(name);
        if (offline)
            data.load();
        callback.run(data, PlayerData::save);
    }

    @Override
    public void init(OctoCore plugin) {
        instance = this;
    }

    public void postDBInit(MongoDatabase db) {
        pdataCollection = db.getCollection("pdata");
    }

    @Override
    public void disable() {
    }

    public Document getProfileDocument(UUID uuid) {
        if (pdataCollection == null || uuid == null) return null;
        return pdataCollection.find(Filters.eq("uuid", uuid.toString())).first();
    }

    public boolean doesDocumentExistByUUID(UUID uuid) {
        Document document = pdataCollection.find(Filters.eq("uuid", uuid.toString())).first();
        return document != null;
    }

    public boolean doesDocumentExistByName(String name) {
        return pdataCollection.find(Filters.eq("name", name)).first() != null;
    }

    public String getFixedName(String name) {
        Document document = pdataCollection.find(Filters.eq("lowerCaseName", name.toLowerCase())).first();
        if (document == null) {
            return name;
        }
        return document.getString("name");
    }


    public PlayerData createProfile(UUID uuid, String name) {
        if (this.playerProfiles.containsKey(uuid)) return getData(uuid);

        this.playerProfiles.put(uuid, new PlayerData(uuid, name));
        return getData(uuid);
    }


    public void leave(Player player) {
        PlayerData data = getData(player);
        if (data == null) return;
        data.setSavingOnQuit(true);
        Tasks.runAsync(() -> {
            playerProfiles.remove(player.getUniqueId());
            data.setLastSeen(System.currentTimeMillis());
            data.save();
        });

        Tasks.runLater(() -> new GlobalPlayerStatusUpdatePacket(player.getName(), true).send(), 30L);

        String name = player.getName();
        UUID uuid = player.getUniqueId();
        BukkitTask task = Tasks.runAsyncLater(() -> {
            GlobalPlayer globalPlayer = ServerManager.getInstance().getGlobalPlayer(name);

            if (globalPlayer != null && globalPlayer.isLeaving() && globalPlayer.hasPermission(Permission.SEND_LEAVE_MESSAGE.getNode())) {
                new StaffLeavePacket(globalPlayer.getName(), globalPlayer.getServer() != null ? globalPlayer.getServer() : "Unknown").send();
            }
            quitting.remove(uuid);
        }, 80L);

        if (task != null) {
            quitting.put(player.getUniqueId(), task.getTaskId());
        }

    }

    public void join(Player player) {

    }

}
