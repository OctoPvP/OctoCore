package net.octopvp.octocore.common.redis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.redis.JedisSettings;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Getter
public class RedisManager {

    private static final String CHANNEL = "octo", DEV_BRIDGE_CHANNEL = "octo:dev_bridge";
    private static final JsonParser jsonParser = new JsonParser();
    @Getter
    @Setter
    private static RedisManager instance;
    private final JedisSettings settings;
    private final RedisListenerManager listenerManager;
    private JedisPool pool;
    private boolean connected = true;
    private JedisSubscriber subscriber;
    private long lastConnect = -1;

    public RedisManager(String hostname, int port, String password, String packetsPackage, Object packetsClass) {
        this(new JedisSettings(hostname, port, password, password != null && !password.isEmpty()),
                packetsPackage, packetsClass);
    }
    public RedisManager(JedisSettings settings, String packetsPackage, Object packetsClass) {
        instance = this;
        listenerManager = new RedisListenerManager();
        listenerManager.init(packetsPackage, packetsClass);
        this.settings = settings;
        try {
            this.pool = new JedisPool(settings.getAddress(), settings.getPort());
            try {
                getJedis();
                OctoCoreCommon.getInstance().getServerImplementation().logDebug("Registering Pub/Sub");
                this.subscriber = new JedisSubscriber(CHANNEL, settings, listenerManager);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            connected = false;
        }
        lastConnect = System.currentTimeMillis();
    }

    public static Jedis getJedis() {
        Jedis jedis = getInstance().getPool().getResource();
        if (getInstance().getSettings().isAuth())
            jedis.auth(getInstance().getSettings().getPassword());
        return jedis;
    }

    public static long getPing() {
        long start = System.currentTimeMillis();
        getJedis().ping();
        return System.currentTimeMillis() - start;
    }

    public void write(RedisPacket data) {
        if (!connected) {
            return;
        }
        write(data.serialize());
    }

    private void write(JsonObject object) {
        write(object.toString());
    }

    public void write(String data) {
        if (pool == null) {
            System.out.println("Pool is null!");
            return;
        }
        try (Jedis jedis = pool.getResource()) {
            if (getSettings().isAuth())
                jedis.auth(settings.getPassword());
            //System.out.println("Publishing - " + str);
            /*
            for (Method declaredMethod : jedis.getClass().getDeclaredMethods()) {
                StringBuilder args = new StringBuilder();
                for (int i = 0; i < declaredMethod.getParameterTypes().length; i++) {
                    args.append(declaredMethod.getParameterTypes()[i].getName());
                    if (i < declaredMethod.getParameterTypes().length - 1)
                        args.append(", ");
                }
                System.out.println(" - " + declaredMethod.getName() + "(" + args + ")");
            }
             */
            jedis.publish(CHANNEL, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disable() {
        getJedis().disconnect();
    }
}
