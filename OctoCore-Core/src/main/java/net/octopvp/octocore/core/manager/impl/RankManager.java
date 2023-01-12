package net.octopvp.octocore.core.manager.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import net.octopvp.octocore.common.PluginMsgChannels;
import net.octopvp.octocore.common.manager.IRankManager;
import net.octopvp.octocore.common.object.builders.RankBuilder;
import net.octopvp.octocore.common.object.enums.RankType;
import net.octopvp.octocore.common.object.permissions.Rank;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.database.DatabaseManager;
import net.octopvp.octocore.core.database.redis.packets.other.ReloadRanksPacket;
import net.octopvp.octocore.core.manager.Manager;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RankManager extends Manager implements IRankManager {
    @Getter
    private static final MongoCollection<Document> ranksCollection = DatabaseManager.getMongoDatabase().getCollection("ranks");
    @Getter
    private static final Set<Rank> ranks = new HashSet<>();
    @Getter
    private static RankManager instance;
    private static boolean loadingRanks = false;

    public void loadRanks() {
        Logger.info("Loading ranks...");
        loadingRanks = true;
        for (Document document : ranksCollection.find()) {
            Rank rank = OctoCore.getGson().fromJson(document.toJson(DatabaseManager.getJsonWriterSettings()), Rank.class);
            if (rank == null)
                continue;
            if (ranks.contains(rank))
                continue;
            ranks.add(rank);
        }
        loadingRanks = false;
        Logger.info("Loaded (%1) ranks.", ranks.size());
    }

    public void reloadRanks() {
        ranks.clear();
        loadRanks();
    }

    @Override
    public Rank getRankById(UUID uuid) {
        return ranks.stream().filter(rank -> rank.getRankId().equals(uuid)).findFirst().orElse(null);
    }

    @Override
    public Rank getRankByName(String name) {
        return ranks.stream().filter(rank -> rank.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public Rank getDefaultRank() {
        return ranks.stream().filter(Rank::isDefaultRank).findFirst().orElse(null);
    }

    @Override
    public void save(Rank rank) {
        if (ranksCollection.find(Filters.eq("rankId", rank.getRankId().toString())).first() != null)
            ranksCollection.replaceOne(Filters.eq("rankId", rank.getRankId().toString()), Document.parse(OctoCore.getGson().toJson(rank)), new ReplaceOptions().upsert(true));
        else ranksCollection.insertOne(Document.parse(OctoCore.getGson().toJson(rank)));
        if (!OctoCore.isLoading())
            broadcastReload();
    }

    public void resetBungeePerms(Player player) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(PluginMsgChannels.SubChannels.PERMISSION_UPDATED);
            out.writeUTF(player.getUniqueId().toString());
        } catch (IOException e) {
            Logger.error("Failed to send permission to bungee. for " + player.getName());
        }
        player.sendPluginMessage(OctoCore.getInstance(), PluginMsgChannels.PLUGIN_MSG, b.toByteArray());
    }

    public boolean canGrant(PlayerData granter, Rank rankData) {
        Rank granterRank = granter.getHighestRank();
        return granterRank.getWeight() > rankData.getWeight();
    }

    public void createNewRank(RankBuilder builder) {
        createNewRank(builder.build());
    }

    public void createNewRank(Rank rank) {
        ranks.add(rank);
        rank.save();
    }

    public void broadcastReload() {
        new ReloadRanksPacket().send();
    }

    public void delete(Rank rank) {
        ranksCollection.findOneAndDelete(Filters.eq("rankId", rank.getRankId().toString()));
        broadcastReload();
    }

    @Override
    public void init(OctoCore plugin) {
        instance = this;
        loadRanks();
        if (OctoCore.isMaster()) {
            if (getDefaultRank() == null)
                createDefaultRank();
        }
    }

    @Override
    public void disable() {

    }

    public Rank createDefaultRank() {
        return createDefaultRank(true);
    }

    public Rank createDefaultRank(boolean createIfNotExist) {
        Rank defaultRank = getDefaultRank();
        if (defaultRank == null) {
            if (!createIfNotExist)
                return null;
            RankBuilder rank = new RankBuilder("Default").setPrefix("&a").setDefaultRank(true).setColor(ChatColor.GREEN.toString()).setWeight(0).setRankType(RankType.DEFAULT);
            Rank r = rank.build();
            ranks.add(r);
            r.save();
        }
        return defaultRank;
    }

    @Override
    public boolean isLoadingRanks() {
        return loadingRanks;
    }
}
