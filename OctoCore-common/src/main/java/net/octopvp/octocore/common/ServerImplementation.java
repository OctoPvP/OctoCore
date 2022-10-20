package net.octopvp.octocore.common;

import net.octopvp.octocore.common.manager.IServerManager;

import java.util.UUID;

public interface ServerImplementation {
    void sendMessage(UUID uuid, String message);

    void sendMessage(String name, String message);

    void logError(String message, Object... placeholders);

    void logInfo(String message, Object... placeholders);

    void logDebug(String message, Object... placeholders);

    void logWarn(String message, Object... placeholders);

    IServerManager getServerManager();

    ClassLoader getPluginClassLoader();
}
