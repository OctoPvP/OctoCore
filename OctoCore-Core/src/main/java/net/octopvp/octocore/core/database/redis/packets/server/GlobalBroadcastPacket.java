package net.octopvp.octocore.core.database.redis.packets.server;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.CC;
import org.bukkit.Bukkit;

@AllArgsConstructor
@NoArgsConstructor
public class GlobalBroadcastPacket extends RedisPacket {
    private String message;

    @Override
    public void onReceive(JsonObject data) {
        String message = CC.translate(this.message);
        Bukkit.broadcastMessage(message);
    }
}
