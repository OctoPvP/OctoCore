package net.octopvp.octocore.common.interfaces.manager;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public interface IPunishModule {
    MongoCollection<Document> getPunishmentsCollection();
}
