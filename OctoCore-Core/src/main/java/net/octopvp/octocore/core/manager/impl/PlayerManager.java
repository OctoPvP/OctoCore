package net.octopvp.octocore.core.manager.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.GlobalPlayer;
import net.octopvp.octocore.common.object.ObjectConsumer;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.enums.RankType;
import net.octopvp.octocore.common.object.permissions.Grant;
import net.octopvp.octocore.common.object.permissions.Rank;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.database.redis.packets.player.GlobalPlayerStatusUpdatePacket;
import net.octopvp.octocore.core.database.redis.packets.staff.StaffLeavePacket;
import net.octopvp.octocore.core.manager.Manager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.common.util.GsonType;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PlayerManager extends Manager {
    @Getter
    private static PlayerManager instance;
    @Getter
    private final Map<UUID, PlayerData> playerProfiles = new ConcurrentHashMap<>();
    @Getter
    private final Map<UUID, Integer> quitting = new HashMap<>();
    @Getter
    private MongoCollection<Document> pdataCollection = null;

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

    public PlayerData getDataEvenIfOffline(UUID uuid, boolean create) {
        PlayerData data = getData(uuid);
        if (data != null) {
            return data;
        }
        data = getOfflineData(uuid);
        if (data == null && create) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            data = createProfile(uuid, player.getName());
            data.save();
        }
        return data;
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

    public String getAddress(UUID uuid) {
        Document document = pdataCollection.find(Filters.eq("uuid", uuid.toString())).first();

        if (document == null) {
            return "";
        }
        return document.getString("address");
    }

    public String getFormattedName(String playerName) {
        Document document = pdataCollection.find(Filters.eq("lowerCaseName", playerName.toLowerCase())).first();
        if (document == null) {
            return CC.translate("&a" + playerName);
        }

        Rank defaultRank = RankManager.getInstance().getDefaultRank();

        List<Grant> grants = OctoCore.getGson().fromJson(document.getString("grants"), GsonType.GRANT);
        grants.removeIf(Objects::isNull);
        List<Grant> activeGrants = new ArrayList<>(grants).stream().filter(grant -> !grant.hasExpired() && RankManager.getInstance().getRankByName(grant.getRankName()) != null).collect(Collectors.toList());
        Rank rank = activeGrants.stream().filter(grant -> grant.getRank() != null && grant.getRank().getRankType() != RankType.HIDDEN).map(Grant::getRank)
                .max(Comparator.comparingInt(Rank::getWeight)).orElse(defaultRank);
        return rank.getDisplayColor() + playerName;
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
            GlobalPlayer globalPlayer = OctoCoreCommon.getInstance().getServerManager().getGlobalPlayer(name);

            if (globalPlayer != null && globalPlayer.isLeaving() && globalPlayer.hasPermission(Permissions.SEND_LEAVE_MESSAGE)) {
                new StaffLeavePacket(globalPlayer.getName(), globalPlayer.getServer() != null ? globalPlayer.getServer() : "Unknown").send();
            }
            quitting.remove(uuid);
        }, 80L);

        if (task != null) {
            quitting.put(player.getUniqueId(), task.getTaskId());
        }

    }

    public Document getDocument(String name) {
        return pdataCollection.find(Filters.eq("lowerCaseName", name.toLowerCase())).first();
    }


    public void join(Player player) {
        PlayerData data = getData(player.getUniqueId());
        data.onJoin(player);
        data.loadPunishmentsPerformed();
        data.setFullJoined(true);
        RankManager.getInstance().resetBungeePerms(player);
    }

    public long getLastSeen(UUID uuid) {
        Document document = getProfileDocument(uuid);
        if (document == null)
            return -1;
        return document.getLong("lastLogin");
    }
}
