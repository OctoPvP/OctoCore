package net.octopvp.octocore.common.util;

import net.octopvp.octocore.common.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Logger {
    private java.util.logging.Logger actualLogger;
    private static Logger instance;
    private static String prefix = "[OctoCore] ";
    private static Messenger messenger = new NoOpMessenger();
    private static Set<UUID> debugPlayers = new HashSet<>();
    public Logger(java.util.logging.Logger logger,String prefix,Messenger messenger){
        this.actualLogger = logger;
        this.prefix = prefix;
        this.messenger = messenger;
        instance = this;
    }
    public static void info(Object str,Object... placeholders){
        instance.actualLogger.info(StringUtils.replacePlaceholders(prefix + " " + str,placeholders));
    }
    public static void warn(Object str,Object... placeholders){
        instance.actualLogger.warning(StringUtils.replacePlaceholders(prefix + " " + str,placeholders));
    }
    public static void error(Object str,Object... placeholders){
        instance.actualLogger.severe(StringUtils.replacePlaceholders(prefix + " " + str,placeholders));
    }
    public static void debug(Object str,Object... placeholders){
        info("[DEBUG] " + StringUtils.replacePlaceholders(str.toString(),placeholders));
        messenger.sendMessage(CC.BOLD + CC.YELLOW + "[DEBUG] " + CC.RESET + StringUtils.replacePlaceholders(str.toString(),placeholders),debugPlayers);
    }
    public static interface Messenger {
        void sendMessage(String message, Collection<UUID> players);
    }
    public static class NoOpMessenger implements Messenger {
        @Override
        public void sendMessage(String message, Collection<UUID> uuids) {
            //Do nothing
        }
    }
}
