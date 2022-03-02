package net.octopvp.octocore.paper.module.impl.punishments.util;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.packets.player.ExecutePunishmentPacket;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishPlayerData;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.DateUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Getter
@Setter
public class Punishment {
    private final OctoCore plugin = OctoCore.getInstance();
    private final PunishPlayerData playerData;
    private final PunishmentType punishmentType;

    private boolean active = true, permanent = true, silent = false, removedSilent = false, last = false, IPRelative = false;
    private long addedAt = -5L;
    private long durationTime = -5L, whenRemoved;
    private String reason = "", removedBy = "", enteredDuration = "", removedFor = "",addedByName = "", name = "";
    private UUID addedBy;

    public boolean isTemporary(){
        return !this.permanent;
    }

    public void save(boolean replace) {
        Logger.debug("b");
        try{
            Document document = new Document();
            document.put("uuid", playerData.getUniqueId().toString());
            document.put("name", playerData.getPlayerName());
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
            document.put("BannedIP", this.playerData.getAddress());
            document.put("addedByName",this.addedByName);
            Logger.debug("c");
            if (replace) {
                this.getCollection().replaceOne(
                        Filters.and(
                                Filters.eq(
                                        "uuid",
                                        this.playerData.getUniqueId().toString()),
                                Filters.eq(
                                        "last",
                                        true)),
                        document,
                        new ReplaceOptions().upsert(true));
            } else {
                MongoCollection<Document> collection = getCollection();
                Logger.debug("Collection: %1",collection);
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
    }

    public String getNiceDuration() {
        if (this.permanent) return "Permanent";
        if (this.durationTime == -5L) return "";

        return DurationFormatUtils.formatDurationWords(DateUtils.handleParseTime(this.enteredDuration), true, true);
    }

    public String getNiceExpire() {
        if (this.permanent) return "Never";
        if (hasExpired()) return "Expired";
        if (this.durationTime == -5L) return "";

        return DateUtils.formatDateDiff(this.getDurationTime());
    }

    public boolean hasExpired() {
        if (!isActive()) return true;
        if (isPermanent()) return false;
        if (!isLast()) return true;

        return System.currentTimeMillis() >= durationTime;
    }

    private MongoCollection<Document> getCollection() {
        if (this.punishmentType == PunishmentType.BAN) {
            return PunishModule.getBans();
        } else if (this.punishmentType == PunishmentType.MUTE) {
            return PunishModule.getMutes();
        } else if (this.punishmentType == PunishmentType.KICK) {
            return PunishModule.getKicks();
        } else if (this.punishmentType == PunishmentType.BLACKLIST) {
            return PunishModule.getBlacklists();
        }
        return PunishModule.getWarns();
    }

    public void execute(CommandSender sender) {
        JsonBuilder jsonChain = new JsonBuilder();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            jsonChain.addProperty("sender", player.getDisplayName());

            PlayerData playerData = PlayerManager.getData(player.getUniqueId());
            jsonChain.addProperty("coloredName", playerData.getHighestRank().getColor() + playerData.getName());
        } else {
            jsonChain.addProperty("sender", sender.getName());
        }
        jsonChain.addProperty("senderName", sender.getName());
        jsonChain.addProperty("name", this.getPlayerData().getPlayerName());
        jsonChain.addProperty("reason", this.getReason());
        jsonChain.addProperty("duration", this.getDurationTime());
        jsonChain.addProperty("niceDuration", this.getNiceDuration());
        jsonChain.addProperty("niceExpire", this.getNiceExpire());
        jsonChain.addProperty("permanent", this.isPermanent());
        jsonChain.addProperty("uuid", this.getPlayerData().getUniqueId().toString());
        jsonChain.addProperty("silent", this.isSilent());
        jsonChain.addProperty("addedByName", this.addedByName);
        jsonChain.addProperty("addedBy", addedByName);
        jsonChain.addProperty("server", OctoCore.getServerName());
        jsonChain.addProperty("type", this.punishmentType.name());
        jsonChain.addProperty("IPRelative", this.IPRelative);
        List<Punishment> warns = playerData.getPunishData().getPunishments().stream().filter(punishment -> !punishment.hasExpired() && punishment.getPunishmentType() == PunishmentType.WARN).collect(Collectors.toList());
        jsonChain.addProperty("warns", warns.size());
        jsonChain.addProperty("alts", StringUtils.getStringFromList(this.playerData.getAlts().stream().map(Alt::getName).collect(Collectors.toList())));

        new ExecutePunishmentPacket(jsonChain).send();
    }

    @Override
    public String toString() {
        return "Punishment{" +
                "plugin=" + plugin +
                ", punishmentType=" + punishmentType +
                ", active=" + active +
                ", permanent=" + permanent +
                ", silent=" + silent +
                ", removedSilent=" + removedSilent +
                ", last=" + last +
                ", IPRelative=" + IPRelative +
                ", addedAt=" + addedAt +
                ", durationTime=" + durationTime +
                ", whenRemoved=" + whenRemoved +
                ", reason='" + reason + '\'' +
                ", removedBy='" + removedBy + '\'' +
                ", enteredDuration='" + enteredDuration + '\'' +
                ", removedFor='" + removedFor + '\'' +
                ", addedByName='" + addedByName + '\'' +
                ", name='" + name + '\'' +
                ", addedBy=" + addedBy +
                '}';
    }
}
