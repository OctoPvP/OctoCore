package net.octopvp.octocore.paper.module.impl.punishments.utilities.punishments;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.object.JedisAction;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.module.impl.punishments.PunishmentModule;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishPlayerData;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.DateUtils;
import net.octopvp.octocore.paper.utils.json.JsonChain;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Getter
@Setter
public class Punishment {
    private final PunishPlayerData playerData;
    private final PunishmentType punishmentType;

    private boolean active = true, permanent = true, silent = false, removedSilent = false, last = false, IPRelative = false;
    private long addedAt = -5L;
    private long durationTime = -5L, whenRemoved;
    private String reason = "", removedBy = "", enteredDuration = "", removedFor = "", addedBy = "", name = "";

    public void save(boolean replace) {
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
        document.put("addedBy", this.addedBy);
        document.put("removedBy", this.removedBy);
        document.put("removedFor", this.removedFor);
        document.put("removedSilent", this.removedSilent);
        document.put("whenRemoved", this.whenRemoved);
        document.put("last", this.last);
        document.put("IPRelative", this.IPRelative);
        document.put("BannedIP", this.playerData.getAddress());
        if (replace) {
            this.getCollection().replaceOne(Filters.and(Filters.eq("uuid", this.playerData.getUniqueId().toString()), Filters.eq("last", true)), document, new ReplaceOptions().upsert(true));
        } else {
            this.getCollection().insertOne(document);
        }
    }

    public void save() {
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
        this.addedBy = document.getString("addedBy");
        this.last = document.getBoolean("last");
        this.IPRelative = document.getBoolean("IPRelative");
        this.name = document.getString("name");
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
            return PunishmentModule.INSTANCE.getBans();
        } else if (this.punishmentType == PunishmentType.MUTE) {
            return PunishmentModule.INSTANCE.getMutes();
        } else if (this.punishmentType == PunishmentType.KICK) {
            return PunishmentModule.INSTANCE.getKicks();
        } else if (this.punishmentType == PunishmentType.BLACKLIST) {
            return PunishmentModule.INSTANCE.getBlacklists();
        }
        return PunishmentModule.INSTANCE.getWarns();
    }

    public void execute(CommandSender sender) {
        JsonChain jsonChain = new JsonChain();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            jsonChain.addProperty("sender", player.getDisplayName());

            PlayerData playerData = PlayerManager.getProfile(player.getUniqueId());
            jsonChain.addProperty("coloredName", playerData.getMainColor() + playerData.getName());
        } else {
            jsonChain.addProperty("sender", sender.getName());
        }
        jsonChain.addProperty("senderName", sender.getName());
        jsonChain.addProperty("name", this.getPlayerData().getPlayerName());
        jsonChain.addProperty("reason", this.getReason());
        jsonChain.addProperty("duration", this.getDurationTime());
        jsonChain.addProperty("niceDuration", this.getNiceDuration());
        jsonChain.addProperty("permanent", this.isPermanent());
        jsonChain.addProperty("uuid", this.getPlayerData().getUniqueId().toString());
        jsonChain.addProperty("silent", this.isSilent());
        jsonChain.addProperty("server", OctoCore.getServerName());
        jsonChain.addProperty("type", this.punishmentType.toString());
        jsonChain.addProperty("IPRelative", this.IPRelative);
        List<Punishment> warns = playerData.getPunishData().getPunishments().stream().filter(punishment -> !punishment.hasExpired() && punishment.getPunishmentType() == PunishmentType.WARN).collect(Collectors.toList());
        jsonChain.addProperty("warns", warns.size());
        jsonChain.addProperty("alts", StringUtils.getStringFromList(this.playerData.getAlts().stream().map(Alt::getName).collect(Collectors.toList())));

        OctoCore.getInstance().getRedisData().write(JedisAction.EXECUTE_PUNISHMENT, jsonChain.get());
    }
}
