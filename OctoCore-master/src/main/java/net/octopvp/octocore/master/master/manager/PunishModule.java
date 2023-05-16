package net.octopvp.octocore.master.master.manager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.annotation.PostConstruct;
import net.octopvp.octocore.common.interfaces.manager.IPunishModule;
import net.octopvp.octocore.common.object.punish.BasePunishment;
import net.octopvp.octocore.common.interfaces.IPunishment;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PunishModule implements IPunishModule {
    private static MongoCollection<Document>
            punishments,
            punishHistory,
            notes;

    @Autowired
    private DatabaseManager databaseManager;

    @PostConstruct
    public void init() {
        MongoDatabase database = databaseManager.getDatabase();

        punishHistory = database.getCollection("punishHistory");
        notes = database.getCollection("notes");
        punishments = database.getCollection("punishments");
    }

    @Override
    public MongoCollection<Document> getPunishmentsCollection() {
        return punishments;
    }

    @Override
    public IPunishment createPunishment(Document doc) {
        return new BasePunishment(doc);
    }
}
