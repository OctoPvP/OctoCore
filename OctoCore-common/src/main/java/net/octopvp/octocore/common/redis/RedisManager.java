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

    private static final String CHANNEL = "aec", DEV_BRIDGE_CHANNEL = "aec:dev_bridge";
    private static final JsonParser jsonParser = new JsonParser();
    @Getter
    @Setter
    private static RedisManager instance;
    private final JedisSettings settings;
    private JedisSettings devBridgeSettings;
    private JedisPool pool, devBridgePool;
    private boolean connected = true, devBridgeConnected = true;
    private JedisSubscriber subscriber, devBridgeSubscriber;

    private final RedisListenerManager listenerManager;
    private RedisListenerManager devBridgeListenerManager;
    private long lastConnect = -1;
    public RedisManager(String hostname, int port, String password, String packetsPackage, Object packetsClass) {
        instance = this;
        listenerManager = new RedisListenerManager();
        listenerManager.init(packetsPackage, packetsClass);
        settings = new JedisSettings();
        settings.setAddress(hostname);
        settings.setAuth(password != null && !password.isEmpty());
        settings.setPassword(password);
        settings.setPort(port);
        try {
            this.pool = new JedisPool(hostname, port);
            Jedis jedis = this.pool.getResource();
            try {
                if (this.settings.isAuth())
                    jedis.auth(this.settings.getPassword());
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

    public void setupDevBridge(String host, int port, String password, String listenersPackage, Object packetsClass) {
        devBridgeListenerManager = new RedisListenerManager();
        devBridgeListenerManager.init(listenersPackage, packetsClass);
        devBridgeSettings = new JedisSettings();
        devBridgeSettings.setAddress(host);
        devBridgeSettings.setAuth(password != null && !password.isEmpty());
        devBridgeSettings.setPassword(password);
        devBridgeSettings.setPort(port);
        try {
            this.devBridgePool = new JedisPool(host, port);
            Jedis jedis = this.devBridgePool.getResource();
            try {
                if (this.devBridgeSettings.isAuth())
                    jedis.auth(this.devBridgeSettings.getPassword());
                OctoCoreCommon.getInstance().getServerImplementation().logDebug("Registering Pub/Sub [Dev Bridge]");
                this.devBridgeSubscriber = new JedisSubscriber(DEV_BRIDGE_CHANNEL, devBridgeSettings, devBridgeListenerManager);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            devBridgeConnected = false;
        }
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

    public void writeBridge(RedisPacket data) {
        if (!devBridgeConnected) {
            return;
        }
        writeBridge(data.serialize());
    }

    private void writeBridge(JsonObject object) {
        if (devBridgePool == null || !devBridgeConnected || devBridgeSettings == null)
            return;
        try (Jedis jedis = devBridgePool.getResource()) {
            if (getSettings().isAuth())
                jedis.auth(devBridgeSettings.getPassword());
            jedis.publish(DEV_BRIDGE_CHANNEL, object.toString());
        }
    }

    public void disable() {
        getJedis().disconnect();
    }
}
