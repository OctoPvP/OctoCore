package net.octopvp.octocore.common.util;

public class Logger {
    private java.util.logging.Logger actualLogger;
    private static Logger instance;
    private static String prefix = "[OctoCore] ";
    public Logger(java.util.logging.Logger logger,String prefix){
        this.actualLogger = logger;
        prefix = prefix;
        instance = this;
    }
    public static void info(Object str){
        instance.actualLogger.info(prefix + " " + str);
    }
    public static void warn(Object str){
        instance.actualLogger.warning(prefix + " " + str);
    }
    public static void error(Object str){
        instance.actualLogger.severe(prefix + " " + str);
    }
    public static void debug(Object str){
        info("[DEBUG] " + str);
    }
}
