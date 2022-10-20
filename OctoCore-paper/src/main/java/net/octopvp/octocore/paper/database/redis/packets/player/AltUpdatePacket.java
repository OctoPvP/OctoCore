package net.octopvp.octocore.paper.database.redis.packets.player;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.punish.Alt;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class AltUpdatePacket extends RedisPacket {

    private UUID uuid;
    private String name;
    private UUID altId;
    private String altName;

    @Override
    public void onReceive(JsonObject data) {
        Player player = Bukkit.getPlayer(UUID.fromString(data.get("uuid").getAsString()));

        if (player != null) {
            PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());

            if (playerData != null) {
                if (playerData.getAlt(UUID.fromString(data.get("altId").getAsString())) == null) {
                    PlayerData targetData = PlayerManager.getInstance().getData(UUID.fromString(data.get("altId").getAsString()));

                    if (targetData != null) {
                        playerData.getAlts().add(new Alt(UUID.fromString(data.get("altId").getAsString()),
                                data.get("altName").getAsString(), targetData.getPunishData()).updateDisplayName());
                    } else {
                        playerData.getAlts().add(new Alt(UUID.fromString(data.get("altId").getAsString()),
                                data.get("altName").getAsString(), new PlayerData(UUID.fromString(data.get("altId").getAsString()),
                                data.get("altName").getAsString()).getPunishData()).updateDisplayName());
                    }
                }
            }
        }
    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder()
                .addProperty("uuid", this.uuid.toString())
                .addProperty("name", this.name)
                .addProperty("altId", this.altId.toString())
                .addProperty("altName", this.altName);
    }

    @Override
    public String getName() {
        return "AltUpdatePacket";
    }
}
