package net.octopvp.octocore.paper.manager.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.PlayerData;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager extends Manager {
    @Getter
    private static PlayerManager instance;

    private MongoCollection<Document> pdataCollection = null;

    @Getter
    private final Map<UUID, PlayerData> playerProfiles = new ConcurrentHashMap<>();

    public PlayerData getData(UUID uuid) {
        return playerProfiles.get(uuid);
    }

    public PlayerData getData(Player player) {
        return getData(player.getUniqueId());
    }

    public PlayerData getData(String name) {
        return playerProfiles.entrySet().stream().filter(entry -> entry.getValue().getName().equalsIgnoreCase(name)).findFirst().orElse(null).getValue();
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
}
