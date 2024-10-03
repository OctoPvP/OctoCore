package net.octopvp.octocore.common.object.punish;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Data;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.interfaces.IPlayerData;
import net.octopvp.octocore.common.interfaces.IPunishment;
import net.octopvp.octocore.common.util.DataCache;
import net.octopvp.octocore.common.util.Logger;
import org.bson.Document;

import java.util.UUID;

@Data
public class BasePunishment implements IPunishment {
    protected PunishmentType type;

    protected boolean active = true, permanent = true, silent = false, removedSilent = false, last = false, ipRelative = false, addedOnWebPanel = false, removedOnWebPanel = false;
    protected long addedAt = -1, durationTime = -1, whenRemoved;
    protected String reason = "", removedBy = "", enteredDuration = "", removedFor = "", addedByName = "", name = "", targetAddress;
    protected UUID addedBy, id, targetId, removedById;
    protected String webPanelId, removedOnWebPanelId, webPanelName, removedOnWebPanelName;

    public BasePunishment(IPlayerData data, PunishmentType type) {
        this.type = type;
        this.id = UUID.randomUUID();
        this.name = data.getName();
        this.targetId = data.getUuid();
    }

    public BasePunishment(PunishmentType type, String name, UUID uuid) {
        this.type = type;
        this.id = UUID.randomUUID();
        this.name = name;
        this.targetId = uuid;
    }

    public static BasePunishment fromDocument(Document document) {
        String json = document.toJson();
        return OctoCoreCommon.getInstance().getGson().fromJson(json, BasePunishment.class);
    }

    private static MongoCollection<Document> getCollection() {
        return OctoCoreCommon.getInstance().getPunishModule().getPunishmentsCollection();
    }

    @Override
    public boolean isTemporary() {
        return !this.permanent;
    }

    @Override
    public void save() {
        String json = OctoCoreCommon.getInstance().getGson().toJson(this);
        Document document = Document.parse(json);
        Logger.debug("Upserting punishment: " + json);
        MongoCollection<Document> collection = getCollection();
        collection.replaceOne(Filters.eq("id", this.id.toString()), document, new ReplaceOptions().upsert(true));
        DataCache.purge(targetId);
    }


    @Override
    public void setType(PunishmentType type) {
        this.type = type;
    }

    @Override
    public boolean hasExpired() {
        if (!isActive()) return true;
        if (isPermanent()) return false;
        if (!isLast()) return true; // TODO why is this here?

        return System.currentTimeMillis() >= durationTime;
    }

    @Override
    public long getRemoveTimestamp() {
        if (isPermanent()) return -1;
        // return the date (unix timestamp) when the punishment will be removed
        //return addedAt + durationTime;
        return durationTime;
    }

    @Override
    public String getStatusText() {
        return hasExpired() ? (isManuallyRemoved() ? "Removed" : "Expired") : "Active";
    }
}
