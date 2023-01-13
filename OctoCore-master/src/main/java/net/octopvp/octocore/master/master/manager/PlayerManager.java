package net.octopvp.octocore.master.master.manager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import net.octopvp.octocore.common.manager.IPlayerManager;
import net.octopvp.octocore.common.object.SimplePlayerData;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;
@Component
public class PlayerManager implements IPlayerManager {
    private MongoCollection<Document> pdataCollection = null;

    @Autowired
    private DatabaseManager databaseManager;

    @PostConstruct
    public void init() {
        pdataCollection = databaseManager.getDatabase().getCollection("pdata");
    }
    @Override
    public Document getProfileDocument(UUID uuid) {
        return pdataCollection.find(Filters.eq("uuid", uuid.toString())).first();
    }
    @Override
    public MongoCollection<Document> getPdataCollection() {
        return pdataCollection;
    }

    @Override
    public SimplePlayerData getData(UUID uuid) {
        return null;
    }

    @Override
    public SimplePlayerData getData(String name) {
        return null;
    }



    @Override
    public boolean doesDocumentExistByUUID(UUID uuid) {
        return false;
    }

    @Override
    public boolean doesDocumentExistByName(String name) {
        return false;
    }
}
