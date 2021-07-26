package net.octopvp.octocore.waterfall.redis;

import lombok.Getter;
import net.md_5.bungee.config.Configuration;
import net.octopvp.octocore.common.object.redis.JedisSettings;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.waterfall.OctoCoreWaterfall;
import redis.clients.jedis.Jedis;

@Getter
public class BungeeRedisManager {
    private static Jedis jedis;
    public BungeeRedisManager(){
        Logger.debug("Starting redis");
        JedisSettings settings = new JedisSettings();
        Configuration config = OctoCoreWaterfall.getInstance().getConfig();
        settings.setAddress(config.getString("redis.host"));
        settings.setPort(config.getInt("redis.port"));
        if (config.getBoolean("redis.auth.enabled")){
            settings.setPassword(config.getString("redis.auth.password"));
        }
        OctoCoreWaterfall.getInstance().setRedisData(new BungeeRedisData(settings));
    }
}
