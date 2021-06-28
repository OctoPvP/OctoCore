package net.octopvp.octocore.paper.module.impl.punishments.player;

import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.paper.module.impl.punishments.PunishmentModule;
import net.octopvp.octocore.paper.module.impl.punishments.utilities.punishments.Punishment;
import net.octopvp.octocore.paper.module.impl.punishments.utilities.punishments.PunishmentType;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class PunishData {
    private final PunishPlayerData playerData;

    private Set<Punishment> punishments = new HashSet<>();

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

        List<Document> bans = PunishmentModule.INSTANCE.getBans().find().filter(Filters.eq("uuid", this.playerData.getUniqueId().toString())).into(new ArrayList<>());
        bans.forEach(saved -> {
            Punishment punishment = new Punishment( this.playerData, PunishmentType.BAN);
            punishment.load(saved);

            this.punishments.add(punishment);
        });

        List<Document> mutes = PunishmentModule.INSTANCE.getMutes().find().filter(Filters.eq("uuid", this.playerData.getUniqueId().toString())).into(new ArrayList<>());
        mutes.forEach(saved -> {
            Punishment punishment = new Punishment( this.playerData, PunishmentType.MUTE);
            punishment.load(saved);

            this.punishments.add(punishment);
        });

        List<Document> warns = PunishmentModule.INSTANCE.getWarns().find().filter(Filters.eq("uuid", this.playerData.getUniqueId().toString())).into(new ArrayList<>());
        warns.forEach(saved -> {
            Punishment punishment = new Punishment( this.playerData, PunishmentType.WARN);
            punishment.load(saved);

            this.punishments.add(punishment);
        });

        List<Document> blacklists = PunishmentModule.INSTANCE.getBlacklists().find().filter(Filters.eq("uuid", this.playerData.getUniqueId().toString())).into(new ArrayList<>());
        blacklists.forEach(saved -> {
            Punishment punishment = new Punishment( this.playerData, PunishmentType.BLACKLIST);
            punishment.load(saved);

            this.punishments.add(punishment);
        });

        List<Document> kicks = PunishmentModule.INSTANCE.getKicks().find().filter(Filters.eq("uuid", this.playerData.getUniqueId().toString())).into(new ArrayList<>());
        kicks.forEach(saved -> {
            Punishment punishment = new Punishment( this.playerData, PunishmentType.KICK);
            punishment.load(saved);

            this.punishments.add(punishment);
        });
        playerData.setLoading(false);
    }

    public void forceLoadBans(UUID uuid) {
        this.punishments.removeIf(punishment -> punishment.getPunishmentType() == PunishmentType.BAN);

        List<Document> bans = PunishmentModule.INSTANCE.getBans().find().filter(Filters.eq("uuid", uuid.toString())).into(new ArrayList<>());
        bans.forEach(saved -> {
            Punishment punishment = new Punishment( null, PunishmentType.BAN);
            punishment.load(saved);

            this.punishments.add(punishment);
        });
    }

    public void forceLoadBlacklists(UUID uuid) {
        this.punishments.removeIf(punishment -> punishment.getPunishmentType() == PunishmentType.BLACKLIST);

        List<Document> blacklists = PunishmentModule.INSTANCE.getBlacklists().find().filter(Filters.eq("uuid", uuid.toString())).into(new ArrayList<>());
        blacklists.forEach(saved -> {
            Punishment punishment = new Punishment( this.playerData, PunishmentType.BLACKLIST);
            punishment.load(saved);

            this.punishments.add(punishment);
        });
    }
}
