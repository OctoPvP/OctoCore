package net.octopvp.octocore.paper.database.redis.publisher;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.octopvp.octocore.common.PluginMsgChannels;
import net.octopvp.octocore.paper.OctoCore;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class BungeeFallback {
    private static JsonParser JSON_PARSER = new JsonParser();

    public void process(byte[] bytes) {
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        DataInputStream in = new DataInputStream(stream);
        try {
            String channel = in.readUTF();
            if (!channel.equals(PluginMsgChannels.PLUGIN_MSG)) return;
            String payload = in.readUTF();
            JsonObject object = JSON_PARSER.parse(payload).getAsJsonObject();
            OctoCore.getInstance().getRedisData().getGlobalSuscription().handleMessage(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
