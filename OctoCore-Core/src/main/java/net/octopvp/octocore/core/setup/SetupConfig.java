package net.octopvp.octocore.core.setup;

import net.octopvp.octocore.core.OctoCore;
import org.bukkit.configuration.file.FileConfiguration;

public class SetupConfig implements Setup {
    public void setup(OctoCore plugi) {
        FileConfiguration config = OctoCore.getInstance().getConfig();
        config.addDefault("database.sql.url", "localhost");
        config.addDefault("database.sql.username", "");
        config.addDefault("database.sql.password", "");
        config.addDefault("database.sql.db", "OctoCore");
        config.addDefault("database.sql.port", 3306);

        config.addDefault("database.mongo.host", "localhost");
        config.addDefault("database.mongo.port", 27019);
        config.addDefault("database.mongo.auth.enabled", false);
        config.addDefault("database.mongo.auth.username", "");
        config.addDefault("database.mongo.auth.db", "");
        config.addDefault("database.mongo.auth.password", "");

        config.addDefault("database.redis.host", "localhost");
        config.addDefault("database.redis.port", 6379);

        config.addDefault("tab.header", "&a&lPlaying on the OctoPvP Network&r");
        config.addDefault("tab.footer", "\n&a&l&kA &r&6&lplay.octopvp.net&a&l&k A&r\n\n&b&ldiscord.gg/<discord>\n\n&b&loctopvp.net");

        config.addDefault("update-pdata-interval", 100L);
        config.addDefault("update-tab-interval", 45L);
        config.addDefault("default-tab", true);

        config.addDefault("cooldown.pearl.time", 16);

        config.options().copyDefaults();
        OctoCore.getInstance().saveDefaultConfig();
    }

    @Override
    public void disable(OctoCore plugin) {

    }
}
