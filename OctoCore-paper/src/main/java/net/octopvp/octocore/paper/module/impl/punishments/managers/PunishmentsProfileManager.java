package net.octopvp.octocore.paper.module.impl.punishments.managers;

import com.mongodb.client.model.Filters;
import lombok.Getter;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishPlayerData;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PunishmentsProfileManager {
    private static PunishmentsProfileManager instance;
    private OctoCore plugin;

    public PunishmentsProfileManager(OctoCore plugin) {
        instance = this;
        this.plugin = plugin;
    }

    private Map<UUID, PunishPlayerData> playerData = new HashMap<>();

    public PunishPlayerData getPlayerDataFromUUID(UUID uuid) {
        if (playerData == null) {
            playerData = new HashMap<>();
            Logger.error("what the shit");
        }
        return this.playerData.get(uuid);
    }

    public void createPlayerData(UUID uuid, String playerName) {
        if (this.playerData.containsKey(uuid)) return;
        this.playerData.put(uuid, new PunishPlayerData(uuid, playerName));
    }

    public void unloadData(OfflinePlayer offlinePlayer) {
        if (offlinePlayer == null) return;
        if (offlinePlayer.getName() == null) return;

        Player target = Bukkit.getPlayer(offlinePlayer.getName());
        if (target == null) {
            getPlayerData().remove(offlinePlayer.getUniqueId());
        }
    }

    public void unloadData(UUID uuid) {
        if (uuid == null) return;
        Player target = Bukkit.getPlayer(uuid);
        if (target == null) {
            getPlayerData().remove(uuid);
        }
    }

    public boolean hasExpired(boolean active, boolean permanent, boolean last, long durationTime) {
        if (!active) return true;
        if (permanent) return false;
        if (!last) return true;

        return System.currentTimeMillis() >= durationTime;
    }

    public boolean isBanned(OfflinePlayer offlinePlayer) {
        PunishPlayerData playerData = this.getPlayerDataFromUUID(offlinePlayer.getUniqueId());
        if (playerData != null) {
            return playerData.getPunishData().isBanned();
        }
        Document document = PunishModule.getBans().find(Filters.eq("uuid", offlinePlayer.getUniqueId().toString())).first();
        if (document == null) return false;

        boolean permanent = document.getBoolean("permanent");
        boolean active = document.getBoolean("active");
        boolean last = document.getBoolean("last");
        long duration = document.getLong("durationTime");

        boolean banned = !hasExpired(active, permanent, last, duration);
        this.unloadData(offlinePlayer);
        return banned;
    }

    public String correctName(String current) {
        Document document = PunishModule.getPunishPlayerData().find(Filters.eq("lowerCaseName", current.toLowerCase())).first();
        if (document != null) {
            return document.getString("name");
        }
        return current;
    }

    public PunishPlayerData loadData(UUID uuid) {
        Document document = PunishModule.getPunishPlayerData().find(Filters.eq("uuid", uuid.toString())).first();

        if (document == null) {
            return null;
        }
        this.createPlayerData(uuid, document.getString("name"));
        return this.getPlayerDataFromUUID(uuid);
    }
}
