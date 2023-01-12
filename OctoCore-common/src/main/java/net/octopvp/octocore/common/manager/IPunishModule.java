package net.octopvp.octocore.common.manager;

import com.mongodb.client.MongoCollection;
import net.octopvp.octocore.common.object.punish.IPunishment;
import org.bson.Document;

public interface IPunishModule {
    MongoCollection<Document> getPunishmentsCollection();

    IPunishment createPunishment(Document doc);
}
