package net.octopvp.octocore.waterfall;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.PluginMsgChannels;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.waterfall.commands.LobbyCommand;
import net.octopvp.octocore.waterfall.listeners.*;
import net.octopvp.octocore.waterfall.redis.BungeeRedisData;
import net.octopvp.octocore.waterfall.redis.BungeeRedisManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

public final class OctoCoreWaterfall extends Plugin {
    @Getter
    private static OctoCoreWaterfall instance;
    @Getter
    private static Configuration config;

    @Getter
    @Setter
    private BungeeRedisData redisData;

    @Override
    public void onEnable() {
        new Logger(getLogger(), "[OctoCore] ", (message, players) -> {
            for (UUID player : players) {
                ProxyServer.getInstance().getPlayer(player).sendMessage(message);
            }
        });
        instance = this;
        if (!getDataFolder().exists())
            getDataFolder().mkdir();
        OctoCoreCommon.setServerName("Bungee");
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
        new BungeeRedisManager();
        getProxy().registerChannel(PluginMsgChannels.PLUGIN_MSG);
        getProxy().registerChannel(PluginMsgChannels.PERMISSIONS);
        getProxy().getPluginManager().registerCommand(this,new LobbyCommand());
        getProxy().getPluginManager().registerListener(this,new KickListener());
        getProxy().getPluginManager().registerListener(this,new StaffListener());
        getProxy().getPluginManager().registerListener(this,new InComingChannelListener());
        getProxy().getPluginManager().registerListener(this,new PlayerListener());
        Logger.debug(Arrays.toString(config.getList("motd").toArray()));
        Logger.debug(config.getBoolean("protocol.enabled"));
        Logger.debug(config.getString("protocol.version"));
        getProxy().getPluginManager().registerListener(this,new PingEvent());
    }
    public Configuration getConfig(){
        return config;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
