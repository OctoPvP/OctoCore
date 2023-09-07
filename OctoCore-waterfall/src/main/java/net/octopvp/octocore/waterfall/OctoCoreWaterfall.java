package net.octopvp.octocore.waterfall;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.PluginMsgChannels;
import net.octopvp.octocore.common.interfaces.ServerImplementation;
import net.octopvp.octocore.common.interfaces.manager.*;
import net.octopvp.octocore.common.manager.DefaultServerManagerImpl;
import net.octopvp.octocore.common.redis.RedisManager;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.perms.Node;
import net.octopvp.octocore.common.util.perms.NodeAdapter;
import net.octopvp.octocore.waterfall.commands.BungeeDataCommand;
import net.octopvp.octocore.waterfall.commands.BungeeHasPermissionCommand;
import net.octopvp.octocore.waterfall.commands.LobbyCommand;
import net.octopvp.octocore.waterfall.listeners.*;
import net.octopvp.octocore.waterfall.manager.OnlinePlayersManager;
import net.octopvp.octocore.waterfall.redis.BungeeRedisManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class OctoCoreWaterfall extends Plugin {
    @Getter
    private static final Gson gson = new GsonBuilder().setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Node.class, new NodeAdapter())
            .enableComplexMapKeySerialization().create();
    @Getter
    private static OctoCoreWaterfall instance;
    private static Configuration config;
    @Getter
    @Setter
    private RedisManager redisManager;

    @Getter
    @Setter
    private DefaultServerManagerImpl serverManager;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        new Logger(getLogger(), "[OctoCore] ", (message, players) -> {
            for (UUID player : players) {
                ProxyServer.getInstance().getPlayer(player).sendMessage(message);
            }
        });
        instance = this;

        if (!getDataFolder().exists())
            getDataFolder().mkdir();
        OctoCoreCommon.getInstance().setBungee(true);
        OctoCoreCommon.getInstance().init(gson, new ServerImplementation() {
            @Override
            public void sendMessage(UUID uuid, String message) {
                if (ProxyServer.getInstance().getPlayer(uuid) != null) {
                    ProxyServer.getInstance().getPlayer(uuid).sendMessage(message);
                }
            }

            @Override
            public void sendMessage(String name, String message) {
                if (ProxyServer.getInstance().getPlayer(name) != null) {
                    ProxyServer.getInstance().getPlayer(name).sendMessage(message);
                }
            }

            @Override
            public void logError(String message, Object... placeholders) {
                Logger.error(message, placeholders);
            }

            @Override
            public void logInfo(String message, Object... placeholders) {
                Logger.info(message, placeholders);
            }

            @Override
            public void logDebug(String message, Object... placeholders) {
                Logger.debug(message, placeholders);
            }

            @Override
            public void logWarn(String message, Object... placeholders) {
                Logger.warn(message, placeholders);
            }

            @Override
            public IServerManager getServerManager() {
                return serverManager;
            }

            @Override
            public ClassLoader getClassLoader() {
                return OctoCoreWaterfall.super.getClass().getClassLoader();
            }

            @Override
            public String getServerName() {
                return "BungeeCord";
            }

            @Override
            public String getName(UUID uuid) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public IRankManager getRankManager() {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public IPunishModule getPunishModule() {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public IPlayerManager getPlayerManager() {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public IDatabaseManager getDatabaseManager() {
                return new IDatabaseManager() {
                    @Override
                    public MongoClient getMongoClient() {
                        throw new UnsupportedOperationException("Not implemented");
                    }

                    @Override
                    public RedisManager getRedisManager() {
                        return redisManager;
                    }

                    @Override
                    public MongoDatabase getDatabase() {
                        throw new UnsupportedOperationException("Not implemented");
                    }
                };
            }
        });
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
        serverManager = new DefaultServerManagerImpl();
        getProxy().registerChannel(PluginMsgChannels.PLUGIN_MSG);
        getProxy().registerChannel(PluginMsgChannels.PERMISSIONS);
        getProxy().getPluginManager().registerCommand(this, new LobbyCommand());
        getProxy().getPluginManager().registerCommand(this, new BungeeHasPermissionCommand());
        getProxy().getPluginManager().registerCommand(this, new BungeeDataCommand("bungeedata"));
        getProxy().getPluginManager().registerListener(this, new KickListener());
        getProxy().getPluginManager().registerListener(this, new StaffListener());
        getProxy().getPluginManager().registerListener(this, new PermissionListener());
        getProxy().getPluginManager().registerListener(this, new PlayerListener());
        Logger.debug(Arrays.toString(config.getList("motd").toArray()));
        Logger.debug(config.getBoolean("protocol.enabled"));
        Logger.debug(config.getString("protocol.version"));
        getProxy().getPluginManager().registerListener(this, new PingEvent());

        getProxy().getScheduler().schedule(this, OnlinePlayersManager::update, 1, 1, TimeUnit.MINUTES);

        Logger.debug("OctoBungee Started! " + (System.currentTimeMillis() - start) + "ms");
        Logger.debug("Redis connected: " + redisManager.isConnected());
    }

    public Configuration getConfig() {
        return config;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
