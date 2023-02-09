package net.octopvp.octocore.common.interfaces;

import net.octopvp.octocore.common.interfaces.manager.*;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.UUID;
import java.util.jar.Manifest;

public interface ServerImplementation {
    void sendMessage(UUID uuid, String message);

    void sendMessage(String name, String message);

    void logError(String message, Object... placeholders);

    void logInfo(String message, Object... placeholders);

    void logDebug(String message, Object... placeholders);

    void logWarn(String message, Object... placeholders);

    IServerManager getServerManager();

    ClassLoader getClassLoader();

    String getServerName();

    default Manifest getManifest() {
        // get the manifest from the jar
        try {
            Enumeration<URL> resources = getClass().getClassLoader()
                    .getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                try {
                    Manifest manifest = new Manifest(resources.nextElement().openStream());
                    // check if the manifest has Core: true
                    System.out.println("Found manifest: " + manifest);
                    if (manifest.getEntries().containsKey("Core")) {
                        System.out.println("Found core manifest");
                        return manifest;
                    }
                    System.out.println("Found non-core manifest");
                } catch (IOException E) {
                    E.printStackTrace();
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    default String getCommit() {
        Manifest manifest = getManifest();
        if (manifest != null) {
            return manifest.getMainAttributes().getValue("Git-Commit");
        }
        return "Unknown";
    }

    String getName(UUID uuid);

    IRankManager getRankManager();

    IPunishModule getPunishModule();

    IPlayerManager getPlayerManager();

    IDatabaseManager getDatabaseManager();
}
