package net.octopvp.octocore.core.database.redis.packets.other;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.impl.JDAManager;

@AllArgsConstructor
@NoArgsConstructor
public class DiscordMessagePacket extends RedisPacket {
    private String messagejson, channel;

    @Override
    public void onReceive(JsonObject data) {
        if (OctoCore.isMaster()) {
            if (!JDAManager.isEnabled())
                return;
            String json = data.get("messagejson").getAsString();
            String channel = data.get("channel").getAsString();
            EmbedBuilder embedBuilder = OctoCore.getGson().fromJson(json, EmbedBuilder.class);
            JDAManager.getJda().getTextChannelById(channel).sendMessage(embedBuilder.build()).queue();
        }
    }
}
