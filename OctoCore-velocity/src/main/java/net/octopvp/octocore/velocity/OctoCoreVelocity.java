package net.octopvp.octocore.velocity;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.redis.RedisManager;
import net.octopvp.octocore.velocity.commands.HasPermCommand;
import net.octopvp.octocore.velocity.listeners.PingListener;
import net.octopvp.octocore.velocity.listeners.PlayerListener;
import net.octopvp.octocore.velocity.manager.OnlinePlayersManager;
import net.octopvp.octocore.velocity.objects.VelocityConfiguration;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@Plugin(
        id = "octocore-velocity",
        name = "OctoCore-velocity",
        version = BuildConstants.VERSION
)
public class OctoCoreVelocity {
    @Getter
    private static final Gson gson = OctoCoreCommon.getGsonBuilder().create();

    private Logger velocityLogger;
    private ProxyServer proxyServer;
    private Path dataDirectory;

    @Inject
    public OctoCoreVelocity(Logger velocityLogger, ProxyServer proxyServer, @DataDirectory Path dataDirectory) {
        this.velocityLogger = velocityLogger;
        this.proxyServer = proxyServer;
        this.dataDirectory = dataDirectory;
    }

    private RedisManager redisManager;
    private VelocityConfiguration config;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        long start = System.currentTimeMillis();
        new net.octopvp.octocore.common.util.Logger(null, // null so we fallback to the server impl callback
                "[OctoCore] ", (message, players) -> {
            for (UUID player : players) {
                proxyServer.getPlayer(player).ifPresent(p -> p.sendMessage(
                        LegacyComponentSerializer.legacySection().deserialize(message)
                ));
            }
        });
        OctoCoreCommon.getInstance().setProxy(true);
        File dataDirectory = this.dataDirectory.toFile();
        if (!dataDirectory.exists())
            dataDirectory.mkdir();
        File configFile = new File(dataDirectory, "config.json");
        if (configFile.exists()) {
            config = VelocityConfiguration.load(gson, configFile);
        } else {
            config = new VelocityConfiguration();
            String json = gson.toJson(config);
            try {
                Files.write(configFile.toPath(), json.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        OctoCoreCommon.getInstance().init(gson, new VelocityServerImpl(proxyServer, velocityLogger, this));
        redisManager = new RedisManager(config.getRedis(), "net.octopvp.octocore.velocity.redis", null);
        Object[] listeners = {
                new PingListener(this),
                new PlayerListener()
        };
        for (Object listener : listeners) {
            proxyServer.getEventManager().register(this, listener);
        }
        BrigadierCommand hasPermCommand = HasPermCommand.createCommand(proxyServer);
        proxyServer.getCommandManager().register(hasPermCommand);
        //         getProxy().getScheduler().schedule(this, OnlinePlayersManager::update, 1, 1, TimeUnit.MINUTES);
        getProxyServer().getScheduler().
                buildTask(this, new OnlinePlayersManager())
                .repeat(15, TimeUnit.SECONDS)
                .schedule();
        velocityLogger.info("OctoCore Velocity has been enabled in " + (System.currentTimeMillis() - start) + "ms.");
    }
}
