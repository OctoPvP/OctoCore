package net.octopvp.octocore.common.object.punish;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Data;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.interfaces.IPlayerData;
import net.octopvp.octocore.common.interfaces.IPunishment;
import net.octopvp.octocore.common.util.DocumentUtils;
import net.octopvp.octocore.common.util.Logger;
import org.bson.Document;

import java.util.UUID;

@Data
public class BasePunishment implements IPunishment {
    protected PunishmentType punishmentType;

    protected boolean active = true, permanent = true, silent = false, removedSilent = false, last = false, IPRelative = false, addedOnWebPanel = false, removedOnWebPanel = false;
    protected long addedAt = -5L, durationTime = -5L, whenRemoved;
    protected String reason = "", removedBy = "", enteredDuration = "", removedFor = "", addedByName = "", name = "", targetAddress;
    protected UUID addedBy, id, targetId, removedById;
    protected String webPanelId, removedOnWebPanelId, webPanelName, removedOnWebPanelName;

    public BasePunishment(Document document) {
        this.load(document);
        this.id = UUID.fromString(document.getString("id"));
        this.targetId = UUID.fromString(document.getString("uuid"));
        this.name = document.getString("name");
    }

    public BasePunishment(IPlayerData data, PunishmentType type) {
        this.punishmentType = type;
        this.id = UUID.randomUUID();
        this.name = data.getName();
        this.targetId = data.getUuid();
    }

    public BasePunishment(PunishmentType type, String name, UUID uuid) {
        this.punishmentType = type;
        this.id = UUID.randomUUID();
        this.name = name;
        this.targetId = uuid;
    }

    public void load(Document document) {
        this.active = document.getBoolean("active");
        this.permanent = document.getBoolean("permanent");
        this.durationTime = document.getLong("durationTime");
        this.addedAt = document.getLong("addedAt");
        this.reason = document.getString("reason");
        this.silent = document.getBoolean("silent");
        this.removedBy = document.getString("removedBy");
        this.removedFor = document.getString("removedFor");
        this.removedSilent = document.getBoolean("removedSilent", false);
        this.whenRemoved = DocumentUtils.getLong(document, "whenRemoved", -1L);
        this.enteredDuration = document.getString("enteredDuration");
        this.last = document.getBoolean("last");
        this.IPRelative = document.getBoolean("IPRelative");
        this.name = document.getString("name");
        this.addedByName = document.getString("addedByName");
        this.addedBy = UUID.fromString(document.getString("addedBy"));
        this.punishmentType = PunishmentType.valueOf(document.getString("type"));
        this.addedOnWebPanel = document.getBoolean("addedOnWebPanel", false);
        this.webPanelId = document.getString("webPanelId");
        if (document.containsKey("removedById")) this.removedById = UUID.fromString(document.getString("removedById"));
        this.removedOnWebPanel = document.getBoolean("removedOnWebPanel", false);
        this.removedOnWebPanelId = document.getString("removedOnWebPanelId");
        this.webPanelName = document.getString("webPanelName");
        this.removedOnWebPanelName = document.getString("removedOnWebPanelName");
    }

    private static MongoCollection<Document> getCollection() {
        return OctoCoreCommon.getInstance().getPunishModule().getPunishmentsCollection();
    }

    @Override
    public boolean isTemporary() {
        return !this.permanent;
    }

    public void save(boolean replace) {
        try {
            Document document = new Document();
            document.put("uuid", targetId.toString());
            document.put("name", name);
            document.put("active", this.active);
            document.put("permanent", this.permanent);
            document.put("durationTime", this.durationTime);
            document.put("addedAt", this.addedAt);
            document.put("reason", this.reason);
            document.put("silent", this.silent);
            document.put("enteredDuration", enteredDuration);
            document.put("addedBy", this.addedBy.toString());
            document.put("removedBy", this.removedBy);
            document.put("removedFor", this.removedFor);
            document.put("removedSilent", this.removedSilent);
            document.put("whenRemoved", this.whenRemoved);
            document.put("last", this.last);
            document.put("IPRelative", this.IPRelative);
            document.put("IPAddress", this.targetAddress);
            document.put("addedByName", this.addedByName);
            document.put("id", this.id.toString());
            document.put("type", this.punishmentType.name());
            document.put("addedOnWebPanel", this.addedOnWebPanel);
            document.put("webPanelId", this.webPanelId);
            document.put("removedOnWebPanel", this.removedOnWebPanel);
            document.put("removedOnWebPanelId", this.removedOnWebPanelId);
            document.put("webPanelName", this.webPanelName);
            document.put("removedOnWebPanelName", this.removedOnWebPanelName);
            if (this.removedById != null) document.put("removedById", this.removedById.toString());

            if (replace) {
                getCollection().replaceOne(
                        Filters.and(
                                Filters.eq(
                                        "uuid",
                                        this.targetId.toString()),
                                Filters.eq(
                                        "last",
                                        true)),
                        document,
                        new ReplaceOptions().upsert(true));
            } else {
                MongoCollection<Document> collection = getCollection();
                Logger.debug("Collection: %1", collection);
                collection.insertOne(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        this.save(false);
    }


    public PunishmentType getType() {
        return punishmentType;
    }

    @Override
    public void setType(PunishmentType type) {
        this.punishmentType = type;
    }

    public boolean isActive() {
        //return !hasExpired() || !active;
        return active;
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
        return isActive() ? "Active" : (isManuallyRemoved() ? "Removed" : "Expired");
    }
}
