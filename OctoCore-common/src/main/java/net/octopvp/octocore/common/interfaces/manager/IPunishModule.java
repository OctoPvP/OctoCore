package net.octopvp.octocore.common.interfaces.manager;

import com.mongodb.client.MongoCollection;
import net.octopvp.octocore.common.interfaces.IPunishment;
import org.bson.Document;

public interface IPunishModule {
    MongoCollection<Document> getPunishmentsCollection();

    IPunishment createPunishment(Document doc);
}
