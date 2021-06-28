package net.octopvp.octocore.paper.module.impl.punishments;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.DatabaseManager;
import net.octopvp.octocore.paper.module.Module;
import net.octopvp.octocore.paper.module.impl.punishments.managers.PunishmentsProfileManager;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishPlayerData;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.Iterator;

@Getter
public class PunishmentModule implements Module {

    @Getter public static PunishmentModule INSTANCE;
    @Getter
    private PunishmentsProfileManager profileManager;
    @Getter
    private static MongoCollection<Document> bans,mutes,kicks,warns,punishPlayerData,blacklists,punishHistory;


    @Override
    public void onEnable(OctoCore plugin) {
        INSTANCE = this;
        this.profileManager = new PunishmentsProfileManager();
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new PlayerDataUpdate(), 20L * 5, 20L * 5);
        MongoDatabase mongoDatabase = DatabaseManager.getMongoDatabase();
        bans = mongoDatabase.getCollection("bans");
        mutes = mongoDatabase.getCollection("mutes");
        kicks = mongoDatabase.getCollection("kicks");
        warns = mongoDatabase.getCollection("warns");
        blacklists = mongoDatabase.getCollection("blacklists");
        punishPlayerData = mongoDatabase.getCollection("data");
        punishHistory = mongoDatabase.getCollection("punish-history");
    }

    @Override
    public void onDisable(OctoCore plugin) {

    }

    private class PlayerDataUpdate implements Runnable {
        @Override
        public void run() {
            Iterator<PunishPlayerData> playerDataIterator = profileManager.getPlayerData().values().iterator();
            try {
                do {
                    PunishPlayerData data = playerDataIterator.next();
                    if (!data.isLoading()) {
                        data.updateBannedAlts();
                    }
                } while (playerDataIterator.hasNext());
            } catch (Exception ignored) { }
        }
    }
}
