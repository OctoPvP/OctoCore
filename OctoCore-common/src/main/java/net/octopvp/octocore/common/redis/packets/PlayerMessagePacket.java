package net.octopvp.octocore.common.redis.packets;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.aetheriacore.common.AetheriaCoreCommon;
import net.octopvp.aetheriacore.common.object.redis.packet.RedisPacket;

@AllArgsConstructor
@NoArgsConstructor
public class PlayerMessagePacket extends RedisPacket {
    private String name, message;

    @Override
    public void onReceive(JsonObject data) {
        if (AetheriaCoreCommon.getInstance().isBungee()) {
            AetheriaCoreCommon.getInstance().getServerImplementation().sendMessage(name, message);
        }
    }

    @Override
    public String getType() {
        return "PLAYER_MESSAGE";
    }
}
