package net.octopvp.octocore.paper.manager.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import net.octopvp.octocore.common.PluginMsgChannels;
import net.octopvp.octocore.common.object.PermUpdateType;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.permissions.Node;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.DatabaseManager;
import net.octopvp.octocore.paper.database.redis.packets.other.ReloadRanksPacket;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.builders.RankBuilder;
import net.octopvp.octocore.paper.objects.enums.RankType;
import net.octopvp.octocore.paper.objects.permissions.Rank;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RankManager extends Manager {
    @Getter
    private static RankManager instance;

    @Getter
    private static final MongoCollection<Document> ranksCollection = DatabaseManager.getMongoDatabase().getCollection("ranks");

    @Getter
    private static final Set<Rank> ranks = new HashSet<>();

    public void loadRanks() {
        Logger.info("Loading ranks...");
        for (Document document : ranksCollection.find()) {
            Rank rank = OctoCore.getGson().fromJson(document.toJson(DatabaseManager.getJsonWriterSettings()), Rank.class);
            if (rank == null)
                continue;
            if (ranks.contains(rank))
                continue;
            ranks.add(rank);
        }
        Logger.info("Loaded (%1) ranks.", ranks.size());
    }

    public void reloadRanks() {
        ranks.clear();
        loadRanks();
    }

    public Rank getRankById(UUID uuid) {
        return ranks.stream().filter(rank -> rank.getRankId().toString().equalsIgnoreCase(uuid.toString())).findFirst().orElse(null);
    }

    public Rank getRankByName(String name) {
        return ranks.stream().filter(rank -> rank.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Rank getDefaultRank() {
        return ranks.stream().filter(Rank::isDefaultRank).findFirst().orElse(null);
    }

    public void save(Rank rank) {
        if (ranksCollection.find(Filters.eq("rankId", rank.getRankId().toString())).first() != null)
            ranksCollection.replaceOne(Filters.eq("rankId", rank.getRankId().toString()), Document.parse(OctoCore.getGson().toJson(rank)), new ReplaceOptions().upsert(true));
        else ranksCollection.insertOne(Document.parse(OctoCore.getGson().toJson(rank)));
        if (!OctoCore.isLoading())
            broadcastReload();
    }

    public void sendPermissionToBungee(Player player, String name, Node node) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(PluginMsgChannels.SubChannels.PERMISSIONS);
            out.writeUTF(PermUpdateType.ADD.name());
            out.writeUTF(name);
            out.writeUTF(node.getPermission());
            out.writeUTF(String.valueOf(node.isAllowed() && node.getServer().isBungee()));
            out.writeUTF(node.getScope().getServer());
        } catch (IOException e) {
            Logger.error("Failed to send permission to bungee. for " + player.getName());
        }
        String channel = PluginMsgChannels.SubChannels.PERMISSIONS;
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
            RankBuilder rank = new RankBuilder("Default").setPrefix("&a").setDefaultRank(true).setColor(ChatColor.GREEN.toString()).setWeight(1).setRankType(RankType.DEFAULT);
            Rank r = rank.build();
            ranks.add(r);
            r.save();
        }
        return defaultRank;
    }

}
