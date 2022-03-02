package net.octopvp.octocore.paper.database.redis.packets.other;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.JDAManager;

@AllArgsConstructor
@NoArgsConstructor
public class DiscordMessagePacket extends RedisPacket {
    private String messagejson, channel;

    @Override
    public void onReceive(JsonObject data) throws Exception {
        if (OctoCore.isMaster()) {
            if (!JDAManager.isEnabled())
                return;
            String json = data.get("messagejson").getAsString();
            String channel = data.get("channel").getAsString();
            EmbedBuilder embedBuilder = OctoCore.getGson().fromJson(json, EmbedBuilder.class);
            JDAManager.getJda().getTextChannelById(channel).sendMessage(embedBuilder.build()).queue();
        }
    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder()
                .addProperty("messagejson", messagejson)
                .addProperty("channel", channel);
    }

    @Override
    public String getName() {
        return "DiscordMessagePacket";
    }
}
