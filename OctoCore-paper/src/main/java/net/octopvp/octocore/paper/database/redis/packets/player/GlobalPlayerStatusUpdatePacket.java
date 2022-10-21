package net.octopvp.octocore.paper.database.redis.packets.player;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.GlobalPlayer;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;

@AllArgsConstructor
@NoArgsConstructor
public class GlobalPlayerStatusUpdatePacket extends RedisPacket {
    private String name;
    private boolean quited;

    @Override
    public void onReceive(JsonObject data) {
        GlobalPlayer player = OctoCore.getInstance().getServerManager().getGlobalPlayer(name);

        if (player != null) {
            player.setLeaving(data.has("quited") && data.get("quited").getAsBoolean());
        }
    }
}
