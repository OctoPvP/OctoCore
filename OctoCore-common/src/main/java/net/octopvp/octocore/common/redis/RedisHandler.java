package net.octopvp.octocore.common.redis;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.annotation.Sync;
import net.octopvp.octocore.common.object.redis.JedisSettings;
import net.octopvp.octocore.common.object.tuple.Pair;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.callback.TypeCallback;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

@RequiredArgsConstructor
public class RedisHandler {
    @Getter
    private static final String channel = "OCTO";
    //private final Packets packets;
    private final String packets;
    private final JedisSettings credentials;
    private final TypeCallback<Void, Runnable> runAsync;
    private final TypeCallback<Boolean, Pair<RedisPacket, JsonObject>> onPacketReceive;
    private final TypeCallback<Collection<Class<?>>, String> getClasses;
    private final Map<String, RedisPacket> packetData = new HashMap<>();
    @Getter
    private JedisPool subscriberPool, publisherPool;
    @Getter
    private long lastConnect = -1;
    @Getter
    private JedisPubSub pubsub;

    public void connect() {
        String host = credentials.getAddress();
        boolean hasPassword = credentials.hasPassword();
        String password = credentials.getPassword();
        int port = credentials.getPort();
        Logger.info("Attempting to connect to redis...");
        try {
            this.subscriberPool = new JedisPool(new JedisPoolConfig(), host, port,
                    30_000, !hasPassword ? null : password, 0, null);
            this.publisherPool = new JedisPool(new JedisPoolConfig(), host, port,
                    30_000, !hasPassword ? null : password, 0, null);

            this.subscriberPool.getResource();
            this.setupPubSub();

            try {
                Thread.sleep(1500L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            this.subscriberPool = null;
            this.publisherPool = null;
            e.printStackTrace();
        }

        Logger.info(isRedisConnected() ? "Successfully connected to redis!" : "Failed to connect to redis!");
        this.lastConnect = System.currentTimeMillis();
    }

    public void setupPubSub() {
        this.pubsub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                super.onMessage(channel, message);
                if (channel.equalsIgnoreCase(channel)) {
                    JsonObject json;
                    try {
                        json = OctoCoreCommon.getGson().fromJson(message, JsonObject.class);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        return;
                    }
                    String name = json.get("name").getAsString();
                    JsonObject data = json.get("data").getAsJsonObject();
                    RedisPacket packet = packetData.get(name);
                    if (isRedisConnected()) {
                        if (packet != null) {
                            if (OctoCoreCommon.isDisabling()) {
                                try {
                                    if (onPacketReceive.callback(new Pair<>(packet, data)))
                                        packet.onReceive(data);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (packet.getClass().isAnnotationPresent(Sync.class)) {
                                    if (onPacketReceive.callback(new Pair<>(packet, data)))
                                        try {
                                            if (onPacketReceive.callback(new Pair<>(packet, data)))
                                                packet.onReceive(data);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Logger.error("Could not parse packet %1, %2", name, data);
                                        }
                                } else {
                                    runAsync.callback(() -> {
                                        try {
                                            if (onPacketReceive.callback(new Pair<>(packet, data)))
                                                packet.onReceive(data);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Logger.error("Could not parse packet %1, %2", name, data);
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }
        };
        ForkJoinPool.commonPool().execute(() -> {
            try (Jedis jedis = this.subscriberPool.getResource()) {
                jedis.subscribe(this.pubsub, channel);
            } catch (Exception e) {
                subscriberPool = null;
                publisherPool = null;
            }
        });
    }

    public void setupPackets() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        int i = 0;
        /*
        for (Field field : this.packets.getClass().getDeclaredFields()) {
            if (RedisPacket.class.isAssignableFrom(field.getType()) && field.getType().getSuperclass() == RedisPacket.class) {
                boolean accessible = field.isAccessible();

                field.setAccessible(true);
                field.set(this.packets, field.getType().getDeclaredConstructor().newInstance());

                RedisPacket packet = (RedisPacket) field.get(this.packets);

                field.setAccessible(accessible);

                this.packetData.put(packet.getName(), packet);
                i++;
            }
        }
         */

        for (Class<?> aClass : getClasses.callback(packets)) {
            //check if the class extends RedisPacket
            if (RedisPacket.class.isAssignableFrom(aClass)) {
                if (Modifier.isAbstract(aClass.getModifiers()))
                    continue;
                RedisPacket packet = (RedisPacket) aClass.getDeclaredConstructor().newInstance();
                packetData.put(packet.getName(), packet);
                i++;
            }
        }
        if (isRedisConnected()) {
            Logger.info("Successfully registered %1 packets", i);
        }
    }

    public boolean isRedisConnected() {
        return this.subscriberPool != null && !this.subscriberPool.isClosed() && this.publisherPool != null && !this.publisherPool.isClosed();
    }

    public void close() {
        try {
            this.subscriberPool.close();
            this.publisherPool.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(RedisPacket packet, boolean here) {
        if (!isRedisConnected() || here) {
            try {
                JsonBuilder d = packet.getData();
                if (d == null)
                    d = new JsonBuilder();
                packet.onReceive(d.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        JsonObject object = new JsonObject();
        object.addProperty("name", packet.getName());
        JsonBuilder b = packet.getData();
        if (b == null)
            b = new JsonBuilder();
        object.add("data", b.get());

        this.sendRequest(channel, object);
    }

    public void sendRequest(String channel, JsonObject object) {
        try {
            if (object == null) {
                throw new IllegalStateException("Object that was being sent was null!");
            }

            try (Jedis jedis = this.publisherPool.getResource()) {
                try {
                    if (this.credentials.isAuth()) {
                        jedis.auth(credentials.getPassword());
                    }

                    jedis.publish(channel, object.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Jedis getJedis() {
        return this.publisherPool.getResource();
    }
}
