package net.octopvp.octocore.core.database.redis.packets.server;

import com.google.gson.JsonObject;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SaveRequestMiscPacket extends RedisPacket {
    private UUID uuid;
    private String name;

    public SaveRequestMiscPacket(UUID uuid) {
        this.uuid = uuid;
    }

    public SaveRequestMiscPacket(String name) {
        this.name = name;
    }

    public SaveRequestMiscPacket() {
    }

    @Override
    public void onReceive(JsonObject data) {
        if (uuid != null) {
            if (Bukkit.getPlayer(uuid) != null) {
                PlayerManager.getInstance().getData(uuid).getData();
            }
        } else {
            Player player = Bukkit.getPlayer(name);
            if (player != null) {
                PlayerManager.getInstance().getData(player).getData();
            }
        }
    }


}
