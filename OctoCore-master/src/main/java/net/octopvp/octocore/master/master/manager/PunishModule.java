package net.octopvp.octocore.master.master.manager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.annotation.PostConstruct;
import net.octopvp.octocore.common.interfaces.manager.IPunishModule;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PunishModule implements IPunishModule {
    private static MongoCollection<Document>
            punishments;
    @Autowired
    private DatabaseManager databaseManager;

    @PostConstruct
    public void init() {
        MongoDatabase database = databaseManager.getDatabase();

        punishments = database.getCollection("punishments");
    }

    @Override
    public MongoCollection<Document> getPunishmentsCollection() {
        return punishments;
    }
}
