package net.octopvp.octocore.paper.module.impl.punishments.util;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.object.IPlayerData;
import net.octopvp.octocore.common.object.punish.IPunishment;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.packets.player.ExecutePunishmentPacket;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;
import net.octopvp.octocore.paper.objects.PlayerData;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;


@Getter
@Setter
public class Punishment implements IPunishment {
    private PunishmentType punishmentType;

    private boolean active = true, permanent = true, silent = false, removedSilent = false, last = false, IPRelative = false;
    private long addedAt = -5L, durationTime = -5L, whenRemoved;
    private String reason = "", removedBy = "", enteredDuration = "", removedFor = "", addedByName = "", name = "", targetAddress;
    private UUID addedBy, id, targetId;

    public Punishment(Document document) {
        this.load(document);
        this.id = UUID.fromString(document.getString("id"));
        this.targetId = UUID.fromString(document.getString("uuid"));
        this.name = document.getString("name");
    }

    public Punishment(IPlayerData data, PunishmentType type) {
        this.punishmentType = type;
        this.id = UUID.randomUUID();
        this.name = data.getName();
        this.targetId = data.getUuid();
    }

    public Punishment(PunishmentType type, String name, UUID uuid) {
        this.punishmentType = type;
        this.id = UUID.randomUUID();
        this.name = name;
        this.targetId = uuid;
    }

    private static MongoCollection<Document> getCollection() {
        return PunishModule.getPunishments();
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
            document.put("IPAddress", targetAddress);
            document.put("addedByName", this.addedByName);
            document.put("id", this.id.toString());
            document.put("type", this.punishmentType.name());
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
        Logger.debug("a");
        this.save(false);
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
        this.removedSilent = document.getBoolean("removedSilent");
        this.whenRemoved = document.getLong("whenRemoved");
        this.enteredDuration = document.getString("enteredDuration");
        this.last = document.getBoolean("last");
        this.IPRelative = document.getBoolean("IPRelative");
        this.name = document.getString("name");
        this.addedByName = document.getString("addedByName");
        this.addedBy = UUID.fromString(document.getString("addedBy"));
        this.punishmentType = PunishmentType.valueOf(document.getString("type"));
    }

    @Override
    public boolean hasExpired() {
        if (!isActive()) return true;
        if (isPermanent()) return false;
        if (!isLast()) return true;

        return System.currentTimeMillis() >= durationTime;
    }

    public void execute(CommandSender sender) {
        JsonBuilder jsonChain = new JsonBuilder();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            jsonChain.addProperty("sender", player.getDisplayName());

            PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());
            jsonChain.addProperty("coloredName", playerData.getHighestRank().getColor() + playerData.getName());
        } else {
            jsonChain.addProperty("sender", sender.getName());
        }
        jsonChain.addProperty("senderName", sender.getName())
                .addProperty("name", name)
                .addProperty("reason", this.getReason())
                .addProperty("duration", this.getDurationTime())
                .addProperty("niceDuration", this.getNiceDuration())
                .addProperty("niceExpire", this.getNiceExpire())
                .addProperty("permanent", this.isPermanent())
                .addProperty("uuid", targetId.toString())
                .addProperty("silent", this.isSilent())
                .addProperty("addedByName", this.addedByName)
                .addProperty("addedBy", addedByName)
                .addProperty("server", OctoCore.getServerName())
                .addProperty("type", this.punishmentType.name())
                .addProperty("IPRelative", this.IPRelative)
                .addProperty("punishment", OctoCore.getGson().toJson(this));

        new ExecutePunishmentPacket(jsonChain).send();
    }

    public PunishmentType getType() {
        return punishmentType;
    }

    @Override
    public void setType(PunishmentType type) {
        this.punishmentType = type;
    }
}
