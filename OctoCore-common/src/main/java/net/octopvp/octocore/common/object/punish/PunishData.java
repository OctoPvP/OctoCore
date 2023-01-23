package net.octopvp.octocore.common.object.punish;

import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.interfaces.IPunishData;
import net.octopvp.octocore.common.interfaces.IPunishment;
import net.octopvp.octocore.common.object.SimplePlayerData;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public class PunishData implements IPunishData {
    private final SimplePlayerData playerData;

    private Collection<IPunishment> punishments = new HashSet<>(); //TODO ordered punishments'

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

    @Override
    public boolean isIPMuted() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.MUTE && punishment.isIPRelative()).findFirst().orElse(null) != null;
    }

    @Override
    public IPunishment getActiveBan() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.BAN).findFirst().orElse(null);
    }

    @Override
    public IPunishment getActiveMute() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.MUTE).findFirst().orElse(null);
    }

    @Override
    public IPunishment getActiveBlacklist() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.BLACKLIST).findFirst().orElse(null);
    }

    @Override
    public List<IPunishment> getPunishments(PunishmentType type) {
        return this.punishments.stream().filter(punishment -> punishment.getType() == type).collect(Collectors.toList());
    }

    public void load() { // TODO: Wasn't this supposed to be called on playerdata load?
        this.punishments.clear();

        List<Document> punishments = OctoCoreCommon.getInstance().getPunishModule().getPunishmentsCollection().find().filter(
                Filters.eq("uuid", this.playerData.getUuid().toString())).into(new ArrayList<>());
        punishments.forEach(document -> {
            IPunishment punishment = OctoCoreCommon.getInstance().getPunishModule().createPunishment(document);

            this.punishments.add(punishment);
        });
    }

    public void forceLoadActiveBansAndBlacklists() {
        this.punishments.removeIf(punishment -> punishment.getType() == PunishmentType.BAN || punishment.getType() == PunishmentType.BLACKLIST);

        List<Document> punishments = OctoCoreCommon.getInstance().getPunishModule().getPunishmentsCollection().find(Filters.and(
                Filters.eq("uuid", this.playerData.getUuid().toString()),
                Filters.eq("active", true))).into(new ArrayList<>());

        punishments.forEach(document -> {
            IPunishment punishment = OctoCoreCommon.getInstance().getPunishModule().createPunishment(document);
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
