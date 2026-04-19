package net.octopvp.octocore.common.util;

import lombok.Getter;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Logger {
    @Getter
    private static final Set<UUID> debugPlayers = new HashSet<>();
    private static Logger instance;
    private static String prefix = "[OctoCore] ";
    private static Messenger messenger = new NoOpMessenger();
    private final java.util.logging.Logger actualLogger;

    public Logger(java.util.logging.Logger logger, String prefix, Messenger messenger) {
        this.actualLogger = logger;
        Logger.prefix = prefix;
        Logger.messenger = messenger;
        instance = this;
    }

    public static void info(Object str, Object... placeholders) {
        String msg = (prefix == null || prefix.isEmpty()) ? str.toString() : prefix + " " + str;
        if (instance != null && instance.actualLogger != null)
            instance.actualLogger.info(StringUtils.replacePlaceholders(msg, placeholders));
        else OctoCoreCommon.getInstance().getServerImplementation().logInfo(msg, placeholders);
    }

    public static void warn(Object str, Object... placeholders) {
        String msg = (prefix == null || prefix.isEmpty()) ? str.toString() : prefix + " " + str;
        if (instance != null && instance.actualLogger != null)
            instance.actualLogger.warning(StringUtils.replacePlaceholders(msg, placeholders));
        else OctoCoreCommon.getInstance().getServerImplementation().logWarn(msg, placeholders);
    }

    public static void error(Object str, Object... placeholders) {
        String msg = (prefix == null || prefix.isEmpty()) ? str.toString() : prefix + " " + str;
        if (instance != null && instance.actualLogger != null)
            instance.actualLogger.severe(StringUtils.replacePlaceholders(msg, placeholders));
        else OctoCoreCommon.getInstance().getServerImplementation().logError(msg, placeholders);
    }

    public static void debug(Object str, Object... placeholders) {
        if (System.getProperty("octocore.debug", "false").equalsIgnoreCase("true")) {
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            String caller = elements[2].getFileName() + ":" + elements[2].getLineNumber();
            String message = "[DEBUG] " + caller + " | " + StringUtils.replacePlaceholders(str.toString(), placeholders);
            info(message);
            messenger.sendMessage(CC.BOLD + CC.YELLOW + "[DEBUG] " + CC.RESET + StringUtils.replacePlaceholders(str.toString(), placeholders), debugPlayers);
        }
    }

    public interface Messenger {
        void sendMessage(String message, Collection<UUID> players);
    }

    public static class NoOpMessenger implements Messenger {
        @Override
        public void sendMessage(String message, Collection<UUID> uuids) {
            //Do nothing
        }
    }
}
