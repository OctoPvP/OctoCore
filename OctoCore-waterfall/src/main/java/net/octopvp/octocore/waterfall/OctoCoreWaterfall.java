package net.octopvp.octocore.waterfall;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.octopvp.octocore.waterfall.commands.LobbyCommand;
import net.octopvp.octocore.waterfall.listeners.KickEvent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class OctoCoreWaterfall extends Plugin {
    @Getter
    private static OctoCoreWaterfall instance;
    @Getter
    private static Configuration config;

    public static OctoCoreWaterfall getInstance() {
        return OctoCoreWaterfall.instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");


        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        getProxy().getPluginManager().registerCommand(this,new LobbyCommand());
        getProxy().getPluginManager().registerListener(this,new KickEvent());
    }
    public Configuration getConfig(){
        return config;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
