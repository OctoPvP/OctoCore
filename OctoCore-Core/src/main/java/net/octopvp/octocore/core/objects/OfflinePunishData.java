package net.octopvp.octocore.core.objects;

import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.interfaces.IPunishData;
import net.octopvp.octocore.common.interfaces.IPunishment;
import net.octopvp.octocore.common.object.punish.Alt;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.database.redis.packets.player.AltUpdatePacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.module.impl.punishments.PunishModule;
import net.octopvp.octocore.core.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.core.utils.OfflineHelpers;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class OfflinePunishData implements IPunishData {

    private String name, ipaddress;
    private UUID uniqueId;

    private Collection<Alt> alts = new ArrayList<>();
    private Collection<IPunishment> punishments = new ArrayList<>();
    private List<String> addresses = ImmutableList.of();

    public OfflinePunishData(String name) {
        this.name = name;
    }

    public OfflinePunishData(String name, String ipaddress) {
        this.name = name;
        this.ipaddress = ipaddress;
    }

    public OfflinePunishData load() {
        return this.load(true);
    }

    public OfflinePunishData load(boolean activeOnly) {
        Logger.debug("Loading punish data for " + this.name);
        this.punishments.clear();

        Player player = Bukkit.getPlayer(name);

        if (player == null) {
            // OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(this.name);
            OfflineHelpers.OfflineInfo offlineInfo = OfflineHelpers.getOfflineInfo(this.name);
            List<Document> punishments = PunishModule.getPunishments().find().filter(Filters.eq("targetId", offlineInfo.getUuid().toString()))
                    .into(new ArrayList<>());

            for (Document document : punishments) {
                Logger.debug(document.getString("name"));
            }

            this.uniqueId = offlineInfo.getUniqueId();
            this.name = offlineInfo.getName();
            this.addresses = PlayerManager.getInstance().getAddresses(this.uniqueId);
            punishments.forEach(document -> {
                Punishment punishment = Punishment.fromDocument(document);
                if (punishment.hasExpired() && activeOnly)
                    return;
                this.punishments.add(punishment);
            });
        } else {
            this.name = player.getName();
            this.uniqueId = player.getUniqueId();

            PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());
            this.addresses = playerData.getAddresses();

            this.punishments.addAll(playerData.getPunishData().loadIfNot().getPunishments().stream().filter(
                    punishment -> !activeOnly || !punishment.hasExpired()).collect(Collectors.toList()
            ));
        }
        return this;
    }

    public Alt getAlt(UUID uuid) {
        return this.alts.stream().filter(alt -> alt.getUniqueId() == uuid).findFirst().orElse(null);
    }

    public OfflinePunishData loadAlts(Document doc) {
        if (doc == null)
            return this;
        List<Alt> a = new ArrayList<>();
        try (MongoCursor<Document> cursor = PlayerManager.getInstance().getPdataCollection()
                .find(Filters.eq("address", addresses)).iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                PlayerData playerData = new PlayerData(UUID.fromString(document.getString("uuid")),
                        document.getString("name"));

                playerData.getPunishData().forceLoadActiveBansAndBlacklists();

                if (!playerData.getUuid().toString().equals(this.uniqueId.toString())
                        && this.getAlt(playerData.getUuid()) == null) {
                    a.add(new Alt(playerData.getUuid(), playerData.getName(), playerData.getPunishData())
                            .updateDisplayName());
                }
            }
        }

        OctoCoreCommon.getInstance().getServerManager().getOnlinePlayers().forEach(onlinePlayer -> {
            if (!onlinePlayer.getUuid().equals(this.uniqueId) && onlinePlayer.getAddress().equals(getLastKnownAddress()) // TODO find some non O(n^2) way to find alts by matching their addresses array
                    && this.getAlt(onlinePlayer.getUuid()) == null) {
                new AltUpdatePacket(this.uniqueId, this.name, onlinePlayer.getUuid(), onlinePlayer.getName()).send();
            }
        });
        List<Alt> nAlts = new ArrayList<>(a);
        this.alts.clear();
        this.alts.addAll(Alt.removeDuplicates(nAlts, this));

        return this;
    }

    @Override
    public String getLastKnownAddress() {
        return addresses.get(0);
    }

    @Override
    public boolean isBanned() {
        return this.punishments.stream()
                .filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.BAN)
                .findFirst().orElse(null) != null;
    }

    @Override
    public boolean isIPBanned() {
        return this.punishments.stream().filter(punishment -> punishment.isIpRelative() && !punishment.hasExpired()
                && punishment.getType() == PunishmentType.BAN).findFirst().orElse(null) != null;
    }

    @Override
    public boolean isBlacklisted() {
        return this.punishments.stream()
                .filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.BLACKLIST)
                .findFirst().orElse(null) != null;
    }

    @Override
    public boolean isWarned() {
        return this.punishments.stream()
                .filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.WARN)
                .findFirst().orElse(null) != null;
    }

    @Override
    public boolean isMuted() {
        return this.punishments.stream()
                .filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.MUTE)
                .findFirst().orElse(null) != null;
    }

    @Override
    public boolean isIPMuted() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired()
                        && punishment.getType() == PunishmentType.MUTE && punishment.isIpRelative()).findFirst()
                .orElse(null) != null;
    }

    @Override
    public IPunishment getActiveBan() {
        return this.punishments.stream()
                .filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.BAN)
                .findFirst().orElse(null);
    }

    @Override
    public IPunishment getActiveMute() {
        return this.punishments.stream()
                .filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.MUTE)
                .findFirst().orElse(null);
    }

    @Override
    public IPunishment getActiveBlacklist() {
        return this.punishments.stream()
                .filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.BLACKLIST)
                .findFirst().orElse(null);
    }

    @Override
    public List<IPunishment> getPunishments(PunishmentType type) {
        return punishments.stream().filter(punishment -> punishment.getType() == type).collect(Collectors.toList());
    }

    @Override
    public UUID getUuid() {
        return this.uniqueId;
    }
}
