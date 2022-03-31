package net.octopvp.octocore.paper.module.impl.punishments.player;

import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;
import net.octopvp.octocore.paper.module.impl.punishments.util.Alt;
import net.octopvp.octocore.paper.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.paper.module.impl.punishments.util.PunishmentType;
import net.octopvp.octocore.paper.objects.IPunishData;
import net.octopvp.octocore.paper.objects.PlayerData;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public class PunishData implements IPunishData {
    private final PlayerData playerData;

    private Collection<Punishment> punishments = new HashSet<>();

    @Override
    public boolean isBanned() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.BAN).findFirst().orElse(null) != null;
    }

    @Override
    public boolean isIPBanned() {
        return this.punishments.stream().filter(punishment -> punishment.isIPRelative() && !punishment.hasExpired() && punishment.getType() == PunishmentType.BAN).findFirst().orElse(null) != null;
    }

    @Override
    public boolean isBlacklisted() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.BLACKLIST).findFirst().orElse(null) != null;
    }

    @Override
    public boolean isWarned() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.WARN).findFirst().orElse(null) != null;
    }

    @Override
    public boolean isMuted() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.MUTE).findFirst().orElse(null) != null;
    }


    public Punishment getActiveBan() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.BAN).findFirst().orElse(null);
    }

    public Punishment getActiveMute() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.MUTE).findFirst().orElse(null);
    }

    public Punishment getActiveBlacklist() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.BLACKLIST).findFirst().orElse(null);
    }

    public List<Punishment> getPunishments(PunishmentType type) {
        return this.punishments.stream().filter(punishment -> punishment.getType() == type).collect(Collectors.toList());
    }

    public void load() {
        this.punishments.clear();

        List<Document> punishments = PunishModule.getPunishments().find().filter(
                Filters.eq("uuid", this.playerData.getUuid().toString())).into(new ArrayList<>());
        punishments.forEach(document -> {
            Punishment punishment = new Punishment(document);

            this.punishments.add(punishment);
        });
    }

    public void forceLoadActiveBansAndBlacklists() {
        this.punishments.removeIf(punishment -> punishment.getType() == PunishmentType.BAN || punishment.getType() == PunishmentType.BLACKLIST);

        List<Document> punishments = PunishModule.getPunishments().find(Filters.and(
                Filters.eq("uuid", this.playerData.getUuid().toString()),
                Filters.eq("active", true))).into(new ArrayList<>());

        punishments.forEach(document -> {
            Punishment punishment = new Punishment(document);

            this.punishments.add(punishment);
        });
    }

    @Override
    public Collection<Alt> getAlts() {
        return playerData.getAltsSafely();
    }

    @Override
    public String getName() {
        return playerData.getName();
    }

    @Override
    public UUID getUniqueId() {
        return playerData.getUuid();
    }

    @Override
    public String getAddress() {
        return playerData.getAddress();
    }
}
