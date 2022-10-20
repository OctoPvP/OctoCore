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
        GlobalPlayer player = OctoCore.getInstance().getServerManager().getGlobalPlayer(data.get("name").getAsString());

        if (player != null) {
            player.setLeaving(data.has("quited") && data.get("quited").getAsBoolean());
        }
    }

    @Override
    public String getType() {
        return "GLOBAL_PLAYER_STATUS_UPDATE";
    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder().addProperty("name", this.name).addProperty("quited", this.quited);
    }

    @Override
    public String getName() {
        return "GlobalPlayerStatusUpdate";
    }
}
