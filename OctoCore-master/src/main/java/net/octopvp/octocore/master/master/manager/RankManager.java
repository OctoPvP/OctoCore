package net.octopvp.octocore.master.master.manager;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.octopvp.octocore.common.interfaces.manager.IRankManager;
import net.octopvp.octocore.common.object.SimplePlayerData;
import net.octopvp.octocore.common.object.builders.RankBuilder;
import net.octopvp.octocore.common.object.enums.RankType;
import net.octopvp.octocore.common.object.permissions.Rank;
import net.octopvp.octocore.common.redis.packets.ReloadRanksPacket;
import net.octopvp.octocore.common.util.ChatColor;
import net.octopvp.octocore.master.master.OctoCoreMaster;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class RankManager implements IRankManager {
    @Autowired
    private DatabaseManager databaseManager;
    @Autowired
    private Gson gson;
    @Getter
    private MongoCollection<Document> ranksCollection;
    @Getter
    private final Set<Rank> ranks = new HashSet<>();
    @Getter
    private static RankManager instance;
    private boolean loadingRanks = false;

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(RankManager.class.getName());

    @PostConstruct
    public void init() {
        instance = this;
        ranksCollection = databaseManager.getDatabase().getCollection("ranks");
        loadRanks();
        if (getDefaultRank() == null)
            createDefaultRank();
    }

    public void loadRanks() {
        LOG.info("Loading ranks...");
        loadingRanks = true;
        for (Document document : ranksCollection.find()) {
            Rank rank = gson.fromJson(document.toJson(DatabaseManager.getJsonWriterSettings()), Rank.class);
            if (rank == null)
                continue;
            if (ranks.contains(rank))
                continue;
            ranks.add(rank);
        }
        loadingRanks = false;
        LOG.info("Loaded (" + ranks.size() + ") ranks.");
    }

    @Override
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
            ranksCollection.replaceOne(Filters.eq("rankId", rank.getRankId().toString()), Document.parse(gson.toJson(rank)), new ReplaceOptions().upsert(true));
        else ranksCollection.insertOne(Document.parse(gson.toJson(rank)));
        if (!OctoCoreMaster.isLoading())
            broadcastReload();
    }

    @Override
    public boolean canGrant(SimplePlayerData granter, Rank rankData) {
        Rank granterRank = granter.getHighestRank();
        return granterRank.getWeight() > rankData.getWeight();
    }

    public void createNewRank(RankBuilder builder) {
        createNewRank(builder.build());
    }

    public void createNewRank(Rank rank) {
        ranks.add(rank);
        rank.save(this);
    }

    public void broadcastReload() {
        databaseManager.getRedisManager().write(new ReloadRanksPacket());
        // new ReloadRanksPacket().send();
    }

    public void delete(Rank rank) {
        ranksCollection.findOneAndDelete(Filters.eq("rankId", rank.getRankId().toString()));
        broadcastReload();
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
            r.save(this);
        }
        return defaultRank;
    }

    @Override
    public boolean isLoadingRanks() {
        return loadingRanks;
    }
}
