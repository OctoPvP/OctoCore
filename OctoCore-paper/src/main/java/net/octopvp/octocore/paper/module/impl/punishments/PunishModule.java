package net.octopvp.octocore.paper.module.impl.punishments;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.module.Module;
import org.bson.Document;

@Getter
public class PunishModule implements Module {

    @Getter
    private static PunishModule instance;
    @Getter
    private static MongoCollection<Document>
            punishments,
            punishPlayerData,
            punishHistory,
            notes;

    public static void postDbInit(MongoDatabase database) {
        punishPlayerData = database.getCollection("punishPlayerData");
        punishHistory = database.getCollection("punishHistory");
        notes = database.getCollection("notes");
        punishments = database.getCollection("Punishments");
    }

    @Override
    public void onEnable(OctoCore plugin) {
        Logger.debug("Starting Punishment Module");
        instance = this;
    }

    @Override
    public void onDisable(OctoCore plugin) {

    }
}
