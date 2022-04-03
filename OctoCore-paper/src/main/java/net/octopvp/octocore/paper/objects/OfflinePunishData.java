package net.octopvp.octocore.paper.objects;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.database.redis.packets.player.AltUpdatePacket;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.ServerManager;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;
import net.octopvp.octocore.paper.module.impl.punishments.util.Alt;
import net.octopvp.octocore.paper.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.paper.module.impl.punishments.util.PunishmentType;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OfflinePunishData implements IPlayerData, IPunishData {

    private String name, address;
    private UUID uniqueId;

    private Collection<Alt> alts = new ArrayList<>();
    private Collection<Punishment> punishments = new ArrayList<>();

    public OfflinePunishData(String name) {
        this.name = name;
    }

    public OfflinePunishData load() {
        return this.load(true);
    }

    public OfflinePunishData load(boolean activeOnly) {
        this.punishments.clear();

        Player player = Bukkit.getPlayer(name);

        if (player == null) {

            List<Document> punishments = PunishModule.getPunishments().find().filter(Filters.and(
                    Filters.eq("uuid", Bukkit.getOfflinePlayer(this.name).getUniqueId().toString()),
                    activeOnly ? Filters.eq("active", true) : Filters.eq("uuid", Bukkit.getOfflinePlayer(this.name).getUniqueId().toString()))).into(new ArrayList<>());

            for (Document document : punishments) {
                Logger.debug(document.getString("name"));
            }

            if (punishments.size() > 0) {
                this.name = punishments.get(0).getString("name");
                this.uniqueId = UUID.fromString(punishments.get(0).getString("uuid"));
                this.address = punishments.get(0).getString("BannedIP");
            } else {
                this.uniqueId = Bukkit.getOfflinePlayer(this.name).getUniqueId();
                this.address = PlayerManager.getInstance().getAddress(this.uniqueId);
            }
            punishments.forEach(document -> {
                Punishment punishment = new Punishment(document);

                this.punishments.add(punishment);
            });
        } else {
            this.name = player.getName();
            this.uniqueId = player.getUniqueId();

            PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());

            this.address = playerData.getAddress();
            this.punishments.addAll(playerData.getPunishData().getPunishments());
        }
        return this;
    }

    public Alt getAlt(UUID uuid) {
        return this.alts.stream().filter(alt -> alt.getUniqueId() == uuid).findFirst().orElse(null);
    }

    public OfflinePunishData loadAlts(Document doc) {
        if (doc == null) return this;
        this.uniqueId = UUID.fromString(doc.getString("uuid"));
        this.address = doc.getString("address");
        this.alts.clear();

        try (MongoCursor<Document> cursor = PlayerManager.getInstance().getPdataCollection().find(Filters.eq("address", address)).iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();

                PlayerData playerData = new PlayerData(UUID.fromString(document.getString("uuid")), document.getString("name"));

                playerData.getPunishData().forceLoadActiveBansAndBlacklists();

                if (!playerData.getUuid().toString().equals(this.uniqueId.toString()) && this.getAlt(playerData.getUuid()) == null) {
                    this.alts.add(new Alt(playerData.getUuid(), playerData.getName(), playerData.getPunishData()).updateDisplayName());
                }
            }
        }

        ServerManager.getInstance().getGlobalPlayers().values().forEach(globalPlayer -> {
            if (!globalPlayer.getUniqueId().toString().equals(this.uniqueId.toString()) && globalPlayer.getAddress().equalsIgnoreCase(address) && this.getAlt(globalPlayer.getUniqueId()) == null) {
                new AltUpdatePacket(this.uniqueId, this.name, globalPlayer.getUniqueId(), globalPlayer.getName());
            }
        });

        this.alts.removeIf(alt -> alt.getName().equalsIgnoreCase(this.name));

        //MAKE SURE THERE ARE NO DUPLICATED ALTS
        List<Alt> alts = new ArrayList<>();
        this.alts.forEach(alt -> {
            if (alts.stream().filter(current -> current.getName().equalsIgnoreCase(alt.getName())).findFirst().orElse(null) == null) {
                alts.add(alt);
            }
        });

        this.alts.clear();
        this.alts.addAll(alts);

        return this;
    }

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

    public Punishment getActiveBan() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.BAN).findFirst().orElse(null);
    }

    public Punishment getActiveMute() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.MUTE).findFirst().orElse(null);
    }

    public Punishment getActiveBlacklist() {
        return this.punishments.stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.BLACKLIST).findFirst().orElse(null);
    }

    @Override
    public UUID getUuid() {
        return this.uniqueId;
    }
}
