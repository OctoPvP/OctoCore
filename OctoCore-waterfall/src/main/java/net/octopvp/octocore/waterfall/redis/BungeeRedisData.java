package net.octopvp.octocore.waterfall.redis;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.common.object.redis.JedisChannels;
import net.octopvp.octocore.common.object.redis.JedisSettings;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.waterfall.redis.pubsub.JedisPublisher;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Getter
@Setter
public class BungeeRedisData {
    private JedisSettings settings;
    private JedisPool pool;
    private JedisPublisher publisher;
    private boolean connected = true;
    private static BungeeRedisData instance = null;
    public BungeeRedisData(JedisSettings settings){
        Logger.debug("a1");
        Logger.debug(settings);
        if (instance != null){
            throw new IllegalStateException("RedisData is not null!");
        }
        instance = this;
        this.settings = settings;
        try{
            this.pool = new JedisPool(this.settings.getAddress(),this.settings.getPort());
            Jedis jedis = this.pool.getResource();
            Logger.debug("a2");
            try {
                if (this.settings.hasPassword())
                    jedis.auth(this.settings.getPassword());
                Logger.debug("Registering pub/sub");
                this.publisher = new JedisPublisher(settings);
                Logger.debug("ax");
            } catch (Exception e) {
                Logger.debug("a3");
                e.printStackTrace();
            }
        } catch (Exception ignored) {
            Logger.debug("a4");
            this.connected = false;
        }
    }
    public void write(JedisAction payload, JsonObject data) {
        try{
            if (!isConnected() || !isActive()) {
                return;
            }
            JsonObject object = new JsonObject();
            object.addProperty("payload", payload.name());
            object.add("data", data == null ? new JsonObject() : data);
            this.publisher.write(JedisChannels.OCTOCORE.getChannel(), object);
        } catch (NullPointerException e) {
            Logger.debug("npe ???");
            e.printStackTrace();
        }
    }
    public boolean isActive() {
        return this.pool != null && !this.pool.isClosed();
    }
}
