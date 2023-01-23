package net.octopvp.octocore.master.master.manager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import net.octopvp.octocore.common.interfaces.manager.IPlayerManager;
import net.octopvp.octocore.common.object.SimplePlayerData;
import net.octopvp.octocore.master.master.util.AccountUtil;
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

    @Autowired
    private AccountUtil accountUtil;

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
        Document document = getProfileDocument(uuid);
        if (document == null) {
            return null;
        }
        return new SimplePlayerData(uuid).load(document);
    }

    @Override
    public SimplePlayerData getData(String name) {
        return getData(accountUtil.getUUID(name));
    }

    @Override
    public boolean doesDocumentExistByUUID(UUID uuid) {
        return getProfileDocument(uuid) != null;
    }

    @Override
    public boolean doesDocumentExistByName(String name) {
        return doesDocumentExistByUUID(accountUtil.getUUID(name));
    }
}
