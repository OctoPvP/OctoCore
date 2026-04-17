package net.octopvp.octocore.rpg.manager;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.interfaces.manager.IDatabaseManager;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RPGPlayerManager implements Listener {
    private static RPGPlayerManager instance;
    private final Map<UUID, RPGPlayerData> dataMap = new ConcurrentHashMap<>();
    private MongoCollection<Document> dataCollection;
    private final Gson gson;

    public RPGPlayerManager(OctoRPG plugin) {
        instance = this;
        this.gson = OctoCoreCommon.getGsonBuilder().create();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        IDatabaseManager dbManager = OctoCoreCommon.getInstance().getServerImplementation().getDatabaseManager();
        if (dbManager != null && dbManager.getDatabase() != null) {
            MongoDatabase database = dbManager.getDatabase();
            this.dataCollection = database.getCollection("rpg_data");
        } else {
            Logger.warn("DatabaseManager or Database is null! RPG data will not be saved.");
        }
    }

    public static RPGPlayerManager getInstance() {
        return instance;
    }

    public Map<UUID, RPGPlayerData> getDataMap() {
        return dataMap;
    }

    public RPGPlayerData getData(UUID uuid) {
        return dataMap.get(uuid);
    }

    public RPGPlayerData getData(Player player) {
        return dataMap.get(player.getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Player player = event.getPlayer();

        OctoRPG.getInstance().getServer().getScheduler().runTaskAsynchronously(OctoRPG.getInstance(), () -> {
            RPGPlayerData data = null;

            if (dataCollection != null) {
                Document doc = dataCollection.find(Filters.eq("uuid", uuid.toString())).first();
                if (doc != null) {
                    try {
                        data = gson.fromJson(doc.toJson(), RPGPlayerData.class);
                    } catch (Exception e) {
                        Logger.error("Failed to load RPG data for " + player.getName(), e);
                    }
                }
            }

            if (data == null) {
                data = new RPGPlayerData(uuid);
            }

            final RPGPlayerData finalData = data;
            OctoRPG.getInstance().getServer().getScheduler().runTask(OctoRPG.getInstance(), () -> {
                dataMap.put(uuid, finalData);
                finalData.join(player);
            });
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        RPGPlayerData data = dataMap.remove(uuid);

        if (data != null && dataCollection != null) {
            OctoRPG.getInstance().getServer().getScheduler().runTaskAsynchronously(OctoRPG.getInstance(), () -> {
                saveData(data);
            });
        }
    }

    @EventHandler
    public void onHeldItemChange(PlayerItemHeldEvent event) {
        RPGPlayerData data = getData(event.getPlayer());
        if (data != null) {
            // We run it next tick because the item in hand hasn't actually changed yet in the event
            OctoRPG.getInstance().getServer().getScheduler().runTask(OctoRPG.getInstance(), () -> {
                data.update();
            });
        }
    }

    public void saveData(RPGPlayerData data) {
        if (dataCollection == null || data == null || data.getUuid() == null) return;
        
        try {
            String json = gson.toJson(data);
            Document document = Document.parse(json);
            dataCollection.replaceOne(
                    Filters.eq("uuid", data.getUuid().toString()), 
                    document, 
                    new ReplaceOptions().upsert(true)
            );
        } catch (Exception e) {
            Logger.error("Error saving RPG data for " + data.getUuid(), e);
        }
    }
}
