package net.octopvp.octocore.paper.setup;

import net.octopvp.octocore.paper.OctoCorePaper;
import org.bukkit.configuration.file.FileConfiguration;

public class SetupConfig implements Setup{
    public void setup(OctoCorePaper plugi){
        FileConfiguration config = OctoCorePaper.getInstance().getConfig();
        config.addDefault("database.username", "");
        config.addDefault("database.password", "");
        config.addDefault("database.db", "");
        config.addDefault("database.url", "");
        config.addDefault("database.port", 3306);
        config.addDefault("tab.header","&a&lPlaying on the OctoPvP Network&r");
        config.addDefault("tab.footer","\n&a&l&kA &r&6&lplay.octopvp.net&a&l&k A&r\n\n&b&ldiscord.gg/<discord>\n\n&b&loctopvp.net");
        config.addDefault("update-pdata-interval", 100l);
        config.addDefault("update-tab-interval", 45l);
        config.options().copyDefaults();
        OctoCorePaper.getInstance().saveDefaultConfig();
    }
}
