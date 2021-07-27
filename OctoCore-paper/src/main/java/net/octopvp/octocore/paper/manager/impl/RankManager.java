package net.octopvp.octocore.paper.manager.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import net.octopvp.octocore.common.PluginMsgChannels;
import net.octopvp.octocore.common.SubChannels;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.json.JsonChain;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.builders.RankBuilder;
import net.octopvp.octocore.paper.objects.enums.RankType;
import net.octopvp.octocore.paper.objects.permissions.Rank;
import org.bson.Document;
import org.bukkit.Bukkit;
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
    private static MongoCollection<Document> ranksCollection = DatabaseManager.getMongoDatabase().getCollection("ranks");

    @Getter
    private static Set<Rank> ranks = new HashSet<>();

    @Override
    public void init(OctoCore plugin) {
        loadRanks();
        if (OctoCore.isMaster()){
            if (getDefaultRank() == null)
                createDefaultRank();
        }
    }

    @Override
    public void disable() {

    }
    public static void loadRanks(){
        Logger.info("Loading ranks...");
        for (Document document : ranksCollection.find()) {
            Rank rank = OctoCore.getGson().fromJson(document.toJson(DatabaseManager.getJsonWriterSettings()),Rank.class);
            if (rank == null)
                continue;
            if (ranks.contains(rank))
                continue;
            ranks.add(rank);
        }
        Logger.info("Loaded (" + ranks.size() + ") ranks.");
    }
    public static void reloadRanks(){
        ranks.clear();
        loadRanks();
    }
    public static Rank getRankById(UUID uuid){
        return ranks.stream().filter(rank -> rank.getRankId().toString().equalsIgnoreCase(uuid.toString())).findFirst().orElse(null);
    }
    public static Rank getRankByName(String name){
        return ranks.stream().filter(rank -> rank.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
    public static Rank getDefaultRank(){
        return ranks.stream().filter(Rank::isDefaultRank).findFirst().orElse(null);
    }

    public void createDefaultRank() {
        Rank defaultRank = getDefaultRank();
        if (defaultRank == null) {
            RankBuilder rank = new RankBuilder("Default");
            rank.setPrefix("&a").setDefaultRank(true).setColor(ChatColor.GRAY.toString()).setWeight(1).setRankType(RankType.DEFAULT);
            Rank r = rank.build();
            ranks.add(r);
            r.save();
        }
    }
    public static void save(Rank rank){
        ranksCollection.insertOne(Document.parse(OctoCore.getGson().toJson(rank)));
        broadcastReload();
    }
    public static void sendPermissionToBungee(Player player, String name, String permission, boolean set) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(PluginMsgChannels.PLUGIN_MSG);
            out.writeUTF(name);
            out.writeUTF(permission);
            out.writeUTF(String.valueOf(set));
        } catch (IOException e) {
            Logger.error("Failed to send permission to bungee. for " + player.getName());
        }
        player.sendPluginMessage(OctoCore.getInstance(), PluginMsgChannels.SubChannels.PERMISSIONS, b.toByteArray());
    }
    public static boolean canGrant(PlayerData granter, Rank rankData) {
        Rank granterRank = granter.getHighestRank();
        return granterRank.getWeight() > rankData.getWeight();
    }
    public static void createNewRank(RankBuilder builder){
        createNewRank(builder.build());
    }
    public static void createNewRank(Rank rank){
        ranks.add(rank);
        rank.save();
    }
    public static void broadcastReload(){
        OctoCore.getInstance().getRedisData().write(JedisAction.RELOAD_RANKS,new JsonChain().get());
    }
    public static void delete(Rank rank){
        ranksCollection.findOneAndDelete(Filters.eq("rankId",rank.getRankId()));
        broadcastReload();
    }

}
