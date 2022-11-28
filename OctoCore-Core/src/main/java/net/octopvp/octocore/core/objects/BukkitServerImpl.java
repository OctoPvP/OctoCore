package net.octopvp.octocore.core.objects;

import net.octopvp.octocore.common.ServerImplementation;
import net.octopvp.octocore.common.manager.IServerManager;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.OctoCore;
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
    public String getCommit() {
        //return OctoCore.getCommit();
        // TODO
        return "unknown";
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
