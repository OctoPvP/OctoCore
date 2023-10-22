package net.octopvp.octocore.waterfall.redis;

import lombok.Getter;
import net.md_5.bungee.config.Configuration;
import net.octopvp.octocore.common.object.redis.JedisSettings;
import net.octopvp.octocore.common.redis.RedisManager;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.waterfall.OctoCoreWaterfall;
import net.octopvp.octocore.waterfall.redis.packet.impl.ServerOnlinePacket;
import redis.clients.jedis.Jedis;

@Getter
public class BungeeRedisManager {
    private static Jedis jedis;

    public BungeeRedisManager() {
        Logger.debug("Starting redis");
        Configuration config = OctoCoreWaterfall.getInstance().getConfig();
        JedisSettings settings = new JedisSettings(
                config.getString("redis.host"),
                config.getInt("redis.port"),
                config.getString("redis.auth.password"),
                config.getBoolean("redis.auth.enabled")
        );
        /*
        OctoCoreWaterfall.getInstance().setRedisManager(new RedisManager("net.octopvp.octocore.waterfall.redis.packet.impl", settings, (runnable) -> {
            ProxyServer.getInstance().getScheduler().runAsync(OctoCoreWaterfall.getInstance(), runnable);
            return null;
        }, (p) -> true,
                (packageName) -> {
                    Reflections reflections = new Reflections(packageName);
                    return reflections.getSubTypesOf(Object.class);
                }));
         */
        OctoCoreWaterfall.getInstance().setRedisManager(new RedisManager(settings.getAddress(), settings.getPort(), settings.getPassword(),
                "net.octopvp.octocore.waterfall.redis.packet.impl", null
        ));
        //OctoCoreWaterfall.getInstance().getRedisManager().connect();
        //OctoCoreCommon.setRedisHandler(OctoCoreWaterfall.getInstance().getRedisManager());

        new ServerOnlinePacket("Bungee #UNKNOWN").send();
    }
}
