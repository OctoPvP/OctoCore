package net.octopvp.octocore.paper.module.impl.punishments.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;
import net.octopvp.octocore.paper.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.paper.module.impl.punishments.util.PunishmentType;
import net.octopvp.octocore.paper.objects.PlayerData;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class PunishData {
    private OctoCore plugin = OctoCore.getInstance();

    private final PunishPlayerData playerData;

    private Set<Punishment> punishments = new HashSet<>();

    public PunishData(PunishPlayerData data){
        this.playerData = data;
    }
    public PunishData(PunishPlayerData data, JsonArray punishments){
        this.playerData = data;
        this.punishments.clear();
        for (JsonElement entry : punishments) {
            Punishment punishment = OctoCore.getGson().fromJson(entry, Punishment.class);
            this.punishments.add(punishment);
        }
    }

    public boolean isBanned() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getPunishmentType() == PunishmentType.BAN).findFirst().orElse(null) != null;
    }

    public boolean isIPBanned() {
        return this.punishments.stream().filter(punishment -> punishment.isIPRelative() && !punishment.hasExpired() && punishment.getPunishmentType() == PunishmentType.BAN).findFirst().orElse(null) != null;
    }

    public boolean isBlacklisted() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getPunishmentType() == PunishmentType.BLACKLIST).findFirst().orElse(null) != null;
    }

    public boolean isWarned() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getPunishmentType() == PunishmentType.WARN).findFirst().orElse(null) != null;
    }

    public boolean isMuted() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getPunishmentType() == PunishmentType.MUTE).findFirst().orElse(null) != null;
    }


    public Punishment getActiveBan() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getPunishmentType() == PunishmentType.BAN).findFirst().orElse(null);
    }

    public Punishment getActiveMute() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getPunishmentType() == PunishmentType.MUTE).findFirst().orElse(null);
    }

    public Punishment getActiveBlacklist() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getPunishmentType() == PunishmentType.BLACKLIST).findFirst().orElse(null);
    }

    public List<Punishment> getPunishments(PunishmentType type) {
        return this.punishments.stream().filter(punishment -> punishment.getPunishmentType() == type).collect(Collectors.toList());
    }

    public void load() {
        playerData.setLoading(true);
        this.punishments.clear();

        List<Document> bans = PunishModule.getBans().find().filter(Filters.eq("uuid", this.playerData.getUniqueId().toString())).into(new ArrayList<>());
        bans.forEach(saved -> {
            Punishment punishment = new Punishment(this.playerData, PunishmentType.BAN);
            punishment.load(saved);

            this.punishments.add(punishment);
        });

        List<Document> mutes = PunishModule.getMutes().find().filter(Filters.eq("uuid", this.playerData.getUniqueId().toString())).into(new ArrayList<>());
        mutes.forEach(saved -> {
            Punishment punishment = new Punishment(this.playerData, PunishmentType.MUTE);
            punishment.load(saved);

            this.punishments.add(punishment);
        });

        List<Document> warns = PunishModule.getWarns().find().filter(Filters.eq("uuid", this.playerData.getUniqueId().toString())).into(new ArrayList<>());
        warns.forEach(saved -> {
            Punishment punishment = new Punishment(this.playerData, PunishmentType.WARN);
            punishment.load(saved);

            this.punishments.add(punishment);
        });

        List<Document> blacklists = PunishModule.getBlacklists().find().filter(Filters.eq("uuid", this.playerData.getUniqueId().toString())).into(new ArrayList<>());
        blacklists.forEach(saved -> {
            Punishment punishment = new Punishment(this.playerData, PunishmentType.BLACKLIST);
            punishment.load(saved);

            this.punishments.add(punishment);
        });

        List<Document> kicks = PunishModule.getKicks().find().filter(Filters.eq("uuid", this.playerData.getUniqueId().toString())).into(new ArrayList<>());
        kicks.forEach(saved -> {
            Punishment punishment = new Punishment(this.playerData, PunishmentType.KICK);
            punishment.load(saved);

            this.punishments.add(punishment);
        });
        playerData.setLoading(false);
    }

    public void forceLoadBans(UUID uuid) {
        this.punishments.removeIf(punishment -> punishment.getPunishmentType() == PunishmentType.BAN);

        List<Document> bans = PunishModule.getBans().find().filter(Filters.eq("uuid", uuid.toString())).into(new ArrayList<>());
        bans.forEach(saved -> {
            Punishment punishment = new Punishment(null, PunishmentType.BAN);
            punishment.load(saved);

            this.punishments.add(punishment);
        });
    }
    public void save(){
        this.punishments.forEach(Punishment::save);
    }

    public void forceLoadBlacklists(UUID uuid) {
        this.punishments.removeIf(punishment -> punishment.getPunishmentType() == PunishmentType.BLACKLIST);

        List<Document> blacklists = PunishModule.getBlacklists().find().filter(Filters.eq("uuid", uuid.toString())).into(new ArrayList<>());
        blacklists.forEach(saved -> {
            Punishment punishment = new Punishment(this.playerData, PunishmentType.BLACKLIST);
            punishment.load(saved);

            this.punishments.add(punishment);
        });
    }

    @Override
    public String toString() {
        return "PunishData{" +
                "punishments=" + punishments +
                '}';
    }
}
