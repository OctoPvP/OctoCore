package net.octopvp.octocore.common.util;

import net.octopvp.octocore.common.StringUtils;

public class Logger {
    private java.util.logging.Logger actualLogger;
    private static Logger instance;
    private static String prefix = "[OctoCore] ";
    public Logger(java.util.logging.Logger logger,String prefix){
        this.actualLogger = logger;
        prefix = prefix;
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
    }
}
