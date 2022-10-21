package net.octopvp.octocore.common.redis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.redis.JedisSettings;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

@Getter
public class JedisSubscriber {
    private static final JsonParser JSON_PARSER = new JsonParser();

    private final String channel;
    private final Jedis jedis;
    private final JedisPubSub pubSub;

    private final RedisListenerManager listenerManager;

    public JedisSubscriber(String channel, JedisSettings settings, RedisListenerManager listenerManager) {
        System.out.println("Init Subscriber");
        this.channel = channel;
        this.listenerManager = listenerManager;
        this.pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                try {
                    //System.out.println("Received " + channel + " : " + message);
                    JsonObject object = JSON_PARSER.parse(message).getAsJsonObject();
                    String type = object.get("type").getAsString();
                    JsonObject data = object.get("data").getAsJsonObject();

                    for (RedisPacket redisPacket : listenerManager.getPackets().stream().filter(packet -> packet.getType().equalsIgnoreCase(type)).toList()) {
                        RedisPacket newPacket = OctoCoreCommon.getInstance().getGson().fromJson(data, redisPacket.getClass());
                        newPacket.onReceive(data);
                    }
                } catch (Exception e) {
                    System.out.println("Received message that could not be parsed");
                    e.printStackTrace();
                }
            }
        };
        this.jedis = new Jedis(settings.getAddress(), settings.getPort());
        if (settings.isAuth()) {
            System.out.println("Authenticating Jedis [Subscriber]");
            this.jedis.auth(settings.getPassword());
        }
        new Thread("Jedis Subscriber Thread") {
            @Override
            public void run() {
                System.out.println("Subscribing to channel " + channel);
                jedis.subscribe(pubSub, channel);
            }
        }.start();
    }

    public void close() {
        if (this.pubSub != null) {
            this.pubSub.unsubscribe();
        }
        if (this.jedis != null) {
            this.jedis.close();
        }
    }
}
