package net.octopvp.octocore.paper.database.redis.publisher;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.object.JedisSettings;
import net.octopvp.octocore.paper.utils.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RequiredArgsConstructor
public class JedisPublisher {

    private JedisSettings jedisSettings;

    public JedisPublisher(JedisSettings settings) {
        this.jedisSettings = settings;
    }

    public void write(String channel, JsonObject payload) {
        JedisPool pool = OctoCore.getInstance().getRedisData().getPool();
        if (pool == null) return;

        Jedis jedis = null;

        try {
            jedis = OctoCore.getInstance().getRedisData().getPool().getResource();

            if(OctoCore.getInstance().getRedisData().getSettings().hasPassword()) {
                jedis.auth(OctoCore.getInstance().getRedisData().getSettings().getPassword());
            }

            jedis.publish(channel, payload.toString());
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }
}
