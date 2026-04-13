package net.octopvp.octocore.velocity;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.mongodb.client.MongoClient;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.interfaces.ServerImplementation;
import net.octopvp.octocore.common.redis.RedisManager;
import net.octopvp.octocore.velocity.commands.HasPermCommand;
import net.octopvp.octocore.velocity.listeners.PingListener;
import net.octopvp.octocore.velocity.listeners.PlayerListener;
import net.octopvp.octocore.velocity.manager.OnlinePlayersManager;
import net.octopvp.octocore.velocity.objects.VelocityConfiguration;
import net.octopvp.octocore.velocity.redis.packet.impl.ServerOfflinePacket;
import net.octopvp.octocore.velocity.redis.packet.impl.ServerOnlinePacket;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@Plugin(id = "octocore-velocity", name = "OctoCore-velocity", version = BuildConstants.VERSION)
public class OctoCoreVelocity {
    @Getter
    private static OctoCoreVelocity instance;
    @Getter
    private static final Gson gson = OctoCoreCommon.getGsonBuilder().create();

    private Logger velocityLogger;
    private ProxyServer proxyServer;
    private Path dataDirectory;
    private MongoClient mongoClient;
    private ServerImplementation serverImplementation;
    private OctoCoreVelocity octoCoreVelocity = this;

    @Inject
    public OctoCoreVelocity(Logger velocityLogger, ProxyServer proxyServer, @DataDirectory Path dataDirectory) {
        instance = this;
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
                                LegacyComponentSerializer.legacySection().deserialize(message)));
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
        
        java.util.function.Function<com.mongodb.MongoClientSettings.Builder, com.mongodb.MongoClientSettings.Builder> mutator = b -> {
            org.bson.codecs.configuration.CodecRegistry registry = org.bson.codecs.configuration.CodecRegistries.fromCodecs(new net.octopvp.octocore.common.mongo.codec.UUIDCodec());
            org.bson.codecs.configuration.CodecRegistry defaultRegistry = com.mongodb.MongoClientSettings.getDefaultCodecRegistry();
            return b.codecRegistry(org.bson.codecs.configuration.CodecRegistries.fromRegistries(registry, defaultRegistry));
        };
        com.mongodb.MongoCredential credentials;
        if (config.getMongo().getAuth().isEnabled()) {
            credentials = com.mongodb.MongoCredential.createCredential(config.getMongo().getAuth().getUsername(), config.getMongo().getAuth().getDatabase(), config.getMongo().getAuth().getPassword().toCharArray());
            mongoClient = com.mongodb.client.MongoClients.create(
                    mutator.apply(com.mongodb.MongoClientSettings.builder()
                                    .applyToClusterSettings(builder ->
                                            builder.hosts(java.util.Collections.singletonList(new com.mongodb.ServerAddress(config.getMongo().getHost(), config.getMongo().getPort()))))
                                    .credential(credentials))
                            .build());
        } else {
            mongoClient = com.mongodb.client.MongoClients.create(
                    mutator.apply(com.mongodb.MongoClientSettings.builder()
                            .applyToClusterSettings(builder ->
                                    builder.hosts(java.util.Collections.singletonList(new com.mongodb.ServerAddress(config.getMongo().getHost(), config.getMongo().getPort()))))
                    ).build());
        }
        
        this.serverImplementation = new VelocityServerImpl(proxyServer, velocityLogger, mongoClient, null, octoCoreVelocity);
        OctoCoreCommon.getInstance().init(gson, this.serverImplementation);
        redisManager = new RedisManager(config.getRedis(), "net.octopvp.octocore.velocity.redis", null);
        redisManager.getListenerManager().init("net.octopvp.octocore.common.redis.packets", null);
        Object[] listeners = {
                new PingListener(this),
                new PlayerListener(this, (VelocityServerImpl) this.serverImplementation)
        };
        for (Object listener : listeners) {
            proxyServer.getEventManager().register(this, listener);
        }
        BrigadierCommand hasPermCommand = HasPermCommand.createCommand(proxyServer);
        proxyServer.getCommandManager().register(hasPermCommand);
        // getProxy().getScheduler().schedule(this, OnlinePlayersManager::update, 1, 1,
        // TimeUnit.MINUTES);
        getProxyServer().getScheduler().buildTask(this, new OnlinePlayersManager())
                .repeat(10, TimeUnit.SECONDS)
                .schedule();
        getProxyServer().getScheduler().buildTask(this, () -> new ServerOnlinePacket(OctoCoreCommon.getInstance().getServerName()).send())
                .delay(1, TimeUnit.SECONDS)
                .schedule();
        velocityLogger.info("OctoCore Velocity has loaded in " + (System.currentTimeMillis() - start) + "ms.");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        new ServerOfflinePacket(OctoCoreCommon.getInstance().getServerName()).send();
    }
}
