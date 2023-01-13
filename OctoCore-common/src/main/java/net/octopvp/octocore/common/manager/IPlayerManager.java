package net.octopvp.octocore.common.manager;

import com.mongodb.client.MongoCollection;
import net.octopvp.octocore.common.object.SimplePlayerData;
import org.bson.Document;

import java.util.UUID;

public interface IPlayerManager {
    MongoCollection<Document> getPdataCollection();

    SimplePlayerData getData(UUID uuid);

    SimplePlayerData getData(String name);

    Document getProfileDocument(UUID uuid);

    boolean doesDocumentExistByUUID(UUID uuid);

    boolean doesDocumentExistByName(String name);
}
