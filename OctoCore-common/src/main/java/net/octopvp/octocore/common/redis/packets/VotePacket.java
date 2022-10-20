package net.octopvp.octocore.common.redis.packets;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.aetheriacore.common.object.redis.packet.RedisPacket;

import java.util.UUID;

@AllArgsConstructor
public class VotePacket extends RedisPacket {
    @Getter
    @Setter
    private static VotePacketImplementation implementation;
    private UUID uuid;
    @Override
    public void onReceive(JsonObject data) {
        if (implementation != null) {
            implementation.onVote(uuid);
        }
    }

    @Override
    public String getType() {
        return "VOTE_PACKET";
    }
    public interface VotePacketImplementation {
        void onVote(UUID uuid);
    }
}
