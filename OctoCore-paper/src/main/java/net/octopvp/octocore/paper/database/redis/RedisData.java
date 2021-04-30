package net.octopvp.octocore.paper.database.redis;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.octopvp.octocore.common.PluginMsgChannels;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.object.JedisAction;
import net.octopvp.octocore.paper.database.redis.object.JedisChannels;
import net.octopvp.octocore.paper.database.redis.object.JedisSettings;
import net.octopvp.octocore.paper.database.redis.payload.GlobalSubscription;
import net.octopvp.octocore.paper.database.redis.publisher.JedisPublisher;
import net.octopvp.octocore.paper.database.redis.subscriber.JedisSubscriber;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Getter
public class RedisData {
    private JedisSettings settings;
    private JedisPool pool;
    private JedisPublisher publisher;
    private JedisSubscriber subscriber;
    private boolean connected = true;
    private GlobalSubscription globalSuscription;

    public RedisData(JedisSettings settings) {
        this.globalSuscription = new GlobalSubscription();

        try {
            this.settings = settings;
            this.pool = new JedisPool(this.settings.getAddress(), this.settings.getPort());
            Jedis jedis = this.pool.getResource();
            try {
                if (this.settings.hasPassword()) {
                    jedis.auth(this.settings.getPassword());
                }
                this.publisher = new JedisPublisher(this.settings);
                this.subscriber = new JedisSubscriber(JedisChannels.OCTOCORE.getChannel(), this.settings, new GlobalSubscription());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ignored) {
            this.connected = false;
        }
    }

    public boolean isActive() {
        return this.pool != null && !this.pool.isClosed();
    }

    public void write(JedisAction payload, JsonObject data) {
        if (!isConnected() || !isActive()) {
            JsonObject object = new JsonObject();
            object.addProperty("payload", payload.name());
            object.add("data", data == null ? new JsonObject() : data);
            this.sendChannelToBungee(object.toString());
            this.getGlobalSuscription().handleMessage(object);
            return;
        }
        JsonObject object = new JsonObject();
        object.addProperty("payload", payload.name());
        object.add("data", data == null ? new JsonObject() : data);
        this.publisher.write("aquacore", object);
    }

    public void sendChannelToBungee(String object) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF(PluginMsgChannels.SubChannels.SYNC);
            out.writeUTF(object);
        } catch (IOException e) {

        }
        Bukkit.getServer().sendPluginMessage(OctoCore.getInstance(), PluginMsgChannels.SPIGOT_TO_BUNGEE, b.toByteArray());
    }
}
