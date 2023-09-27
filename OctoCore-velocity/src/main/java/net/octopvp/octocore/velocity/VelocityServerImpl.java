package net.octopvp.octocore.velocity;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.interfaces.ServerImplementation;
import net.octopvp.octocore.common.interfaces.manager.*;
import net.octopvp.octocore.common.manager.DefaultServerManagerImpl;
import net.octopvp.octocore.common.redis.RedisManager;
import net.octopvp.octocore.common.util.MojangAPIUtil;
import org.slf4j.Logger;

import java.util.UUID;

@AllArgsConstructor
public class VelocityServerImpl implements ServerImplementation {
    private final ProxyServer proxyServer;
    private final Logger velocityLogger;
    private final RedisManager redisManager;
    private final IServerManager serverManager = new DefaultServerManagerImpl();
    @Override
    public void sendMessage(UUID uuid, String message) {
        proxyServer.getPlayer(uuid).ifPresent(p -> p.sendMessage(Component.text(message)));
    }

    @Override
    public void sendMessage(String name, String message) {
        proxyServer.getPlayer(name).ifPresent(p -> p.sendMessage(Component.text(message)));
    }

    @Override
    public void logError(String message, Object... placeholders) {
        velocityLogger.error(StringUtils.replacePlaceholders(message, placeholders));
    }

    @Override
    public void logInfo(String message, Object... placeholders) {
        velocityLogger.info(StringUtils.replacePlaceholders(message, placeholders));
    }

    @Override
    public void logDebug(String message, Object... placeholders) {
        velocityLogger.debug(StringUtils.replacePlaceholders(message, placeholders));
    }

    @Override
    public void logWarn(String message, Object... placeholders) {
        velocityLogger.warn(StringUtils.replacePlaceholders(message, placeholders));
    }

    @Override
    public IServerManager getServerManager() {
        return serverManager;
    }

    @Override
    public ClassLoader getClassLoader() {
        return VelocityServerImpl.class.getClassLoader();
    }

    @Override
    public String getServerName() {
        return "Velocity";
    }

    @Override
    public String getName(UUID uuid) {
        return proxyServer.getPlayer(uuid).map(Player::getUsername).orElse(MojangAPIUtil.INSTANCE.getName(uuid));
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
}
