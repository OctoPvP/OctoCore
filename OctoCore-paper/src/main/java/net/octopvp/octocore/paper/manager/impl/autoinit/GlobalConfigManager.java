package net.octopvp.octocore.paper.manager.impl.autoinit;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.DatabaseManager;
import net.octopvp.octocore.paper.manager.Manager;
import org.bson.Document;

@Getter
public class GlobalConfigManager extends Manager {
    @Getter
    private static GlobalConfigManager instance;


    @Getter
    private static MongoCollection<Document> configCollection = null;

    public void load(MongoDatabase database) {
        configCollection = database.getCollection("config");

    }

    @Override
    public void init(OctoCore plugin) {
        instance = this;
        load(DatabaseManager.getMongoDatabase());
    }

    @Override
    public void disable() {

    }
}
