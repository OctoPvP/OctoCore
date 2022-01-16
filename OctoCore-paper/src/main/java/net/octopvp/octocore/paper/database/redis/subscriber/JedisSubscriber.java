package net.octopvp.octocore.paper.database.redis.subscriber;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import lombok.Getter;
import net.octopvp.octocore.common.object.redis.JedisHandle;
import net.octopvp.octocore.common.object.redis.JedisSettings;
import net.octopvp.octocore.common.util.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

@Getter
public class JedisSubscriber {
    private static JsonParser JSON_PARSER = new JsonParser();

    private String channel;
    private Jedis jedis;
    private JedisPubSub pubSub;
    private JedisHandle subscriptionHandler;
    private static JedisSubscriber instance;

    public JedisSubscriber(String channel, JedisSettings settings, JedisHandle subscriptionHandler) {
        if (instance != null)
            throw new IllegalStateException("JedisSubscriber is not null!");
        instance = this;
        this.channel = channel;
        this.subscriptionHandler = subscriptionHandler;
        this.pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                try {
                    JsonObject object = JSON_PARSER.parse(message).getAsJsonObject();
                    JedisSubscriber.this.subscriptionHandler.handleMessage(object);
                } catch (JsonParseException e) {
                    Logger.debug("Received message that could not be parsed");
                }
            }
        };
        this.jedis = new Jedis(settings.getAddress(), settings.getPort());
        if (settings.hasPassword()) {
            this.jedis.auth(settings.getPassword());
        }
        new Thread(() -> this.jedis.subscribe(this.pubSub, this.channel)).start();
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
