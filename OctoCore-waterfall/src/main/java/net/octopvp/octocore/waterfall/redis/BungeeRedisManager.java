package net.octopvp.octocore.waterfall.redis;

import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.redis.JedisSettings;
import net.octopvp.octocore.common.redis.RedisHandler;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.waterfall.OctoCoreWaterfall;
import net.octopvp.octocore.waterfall.redis.packet.impl.ServerOnlinePacket;
import org.reflections.Reflections;
import redis.clients.jedis.Jedis;

@Getter
public class BungeeRedisManager {
    private static Jedis jedis;

    public BungeeRedisManager() {
        Logger.debug("Starting redis");
        JedisSettings settings = new JedisSettings();
        Configuration config = OctoCoreWaterfall.getInstance().getConfig();
        settings.setAddress(config.getString("redis.host"));
        settings.setPort(config.getInt("redis.port"));
        if (config.getBoolean("redis.auth.enabled")) {
            settings.setPassword(config.getString("redis.auth.password"));
        }
        OctoCoreWaterfall.getInstance().setRedisData(new BungeeRedisData(settings));
        OctoCoreWaterfall.getInstance().setRedisHandler(new RedisHandler("net.octopvp.octocore.waterfall.redis.packet.impl", settings, (runnable) -> {
            ProxyServer.getInstance().getScheduler().runAsync(OctoCoreWaterfall.getInstance(), runnable);
            return null;
        }, (p) -> true,
                (packageName) -> {
                    Reflections reflections = new Reflections(packageName);
                    return reflections.getSubTypesOf(Object.class);
                }));
        OctoCoreWaterfall.getInstance().getRedisHandler().connect();
        OctoCoreCommon.setRedisHandler(OctoCoreWaterfall.getInstance().getRedisHandler());

        new ServerOnlinePacket("Bungee #UNKNOWN").send();
    }
}
