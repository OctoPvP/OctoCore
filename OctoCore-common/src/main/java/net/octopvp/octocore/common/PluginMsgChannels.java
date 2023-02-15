package net.octopvp.octocore.common;

public class PluginMsgChannels {
    /*
    public static final String SPIGOT_TO_BUNGEE = "OCTO|1";
    public static final String BUNGEE_TO_SPIGOT = "OCTO|2";
     */
    public static final String PLUGIN_MSG = "octo:msg";
    public static final String PERMISSIONS = PLUGIN_MSG;
    public static final String BUNGEE = "BungeeCord";
    public static final String LUNAR_CLIENT = "Lunar-Client";

    public class SubChannels {
        public static final String SYNC = "octo:sync";
        public static final String PERMISSIONS = "octo:permissions";
        public static final String PERMISSION_UPDATED = "octo:permission_updated";
        public static final String FREEZE = "octo:freeze";
        public static final String TEST = "octo:test";
    }
}
