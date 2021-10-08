package net.octopvp.octocore.waterfall.redis.pubsub;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.object.redis.JedisSettings;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.waterfall.OctoCoreWaterfall;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RequiredArgsConstructor
public class JedisPublisher {

    private JedisSettings jedisSettings;

    public JedisPublisher(JedisSettings settings) {
        this.jedisSettings = settings;
    }

    public void write(String channel, JsonObject payload) {
        if (OctoCoreWaterfall.getInstance().getRedisData() == null){
            Logger.info("RedisData is null");
            return;
        }
        JedisPool pool = OctoCoreWaterfall.getInstance().getRedisData().getPool();
        if (pool == null) return;

        Jedis jedis = null;

        try {
            jedis = OctoCoreWaterfall.getInstance().getRedisData().getPool().getResource();

            if(OctoCoreWaterfall.getInstance().getRedisData().getSettings().hasPassword()) {
                jedis.auth(OctoCoreWaterfall.getInstance().getRedisData().getSettings().getPassword());
            }

            jedis.publish(channel, payload.toString());
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }
}