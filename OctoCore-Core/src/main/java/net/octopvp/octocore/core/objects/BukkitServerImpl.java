package net.octopvp.octocore.core.objects;

import net.octopvp.octocore.common.interfaces.ServerImplementation;
import net.octopvp.octocore.common.interfaces.manager.*;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.module.impl.punishments.PunishModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitServerImpl implements ServerImplementation {
    @Override
    public void sendMessage(UUID uuid, String message) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.sendMessage(message);
        }
    }

    @Override
    public void sendMessage(String name, String message) {
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            player.sendMessage(message);
        }
    }

    @Override
    public void logError(String message, Object... placeholders) {
        Logger.error(message, placeholders);
    }

    @Override
    public void logInfo(String message, Object... placeholders) {
        Logger.info(message, placeholders);
    }

    @Override
    public void logDebug(String message, Object... placeholders) {
        Logger.debug(message, placeholders);
    }

    @Override
    public void logWarn(String message, Object... placeholders) {
        Logger.warn(message, placeholders);
    }

    @Override
    public IServerManager getServerManager() {
        return OctoCore.getInstance().getServerManager();
    }

    @Override
    public ClassLoader getClassLoader() {
        return OctoCore.getInstance().getClassLoader0();
    }

    @Override
    public String getServerName() {
        return OctoCore.getServerName();
    }

    @Override
    public String getName(UUID uuid) {
        System.out.println("Getting name for UUID " + uuid.toString());
        if (Bukkit.getPlayer(uuid) != null) {
            System.out.println("Player is online, returning name");
            return Bukkit.getPlayer(uuid).getName();
        } else {
            System.out.println("Player is offline, returning offline name");
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            System.out.println("Offline name is " + name);
            return name;
        }
    }

    @Override
    public IRankManager getRankManager() {
        return OctoCore.getInstance().getRankManager();
    }

    @Override
    public IPunishModule getPunishModule() {
        return PunishModule.getInstance();
    }

    @Override
    public IPlayerManager getPlayerManager() {
        return PlayerManager.getInstance();
    }

    @Override
    public IDatabaseManager getDatabaseManager() {
        return OctoCore.getInstance().getDatabaseManager();
    }

    /*
    @Override
    public ServerInfo getServerInfo() {
        return new ServerInfo() {
            @Override
            public String getServerName() {
                return OctoCore.getServerName();
            }

            @Override
            public String getCommitHash() {
                return "unknown";
            }

            @Override
            public String getCommitBranch() {
                return "unknown";
            }

            @Override
            public boolean isOnline(UUID uuid) {
                return Bukkit.getPlayer(uuid) != null;
            }
        };
    }

     */
}
