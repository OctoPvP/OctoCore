package net.octopvp.octocore.paper.database.redis.packets.other;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.permissions.Grant;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
public class GrantsUpdatePacket extends RedisPacket {
    private String name, toChange;
    private boolean add;
    @Override
    public void onReceive(JsonObject data) throws Exception {
        String name = data.get("name").getAsString();
        String tochange = data.get("tochange").getAsString();
        boolean add = data.get("add").getAsBoolean();
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            PlayerData playerData = PlayerManager.getData(player.getUniqueId());
            Grant grant = OctoCore.getGson().fromJson(tochange, Grant.class);
            if (add)
                playerData.getGrants().add(grant);
            else playerData.getGrants().remove(grant);
            playerData.loadPerms(player);
            playerData.save();
        }
    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder()
                .addProperty("name", name)
                .addProperty("tochange", toChange)
                .addProperty("add", add);
    }

    @Override
    public String getName() {
        return "GrantsUpdatePacket";
    }
}
