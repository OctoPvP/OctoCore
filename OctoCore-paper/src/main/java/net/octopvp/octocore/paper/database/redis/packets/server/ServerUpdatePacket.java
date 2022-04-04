package net.octopvp.octocore.paper.database.redis.packets.server;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.ServerManager;
import net.octopvp.octocore.paper.objects.ServerData;

@AllArgsConstructor
@NoArgsConstructor
public class ServerUpdatePacket extends RedisPacket {
    private JsonBuilder jsonBuilder;

    @Override
    public void onReceive(JsonObject data) {
        try {
            ServerData serverData = OctoCore.getServerManager().getServerData(data.get("name").getAsString());
            if (serverData == null) {
                serverData = OctoCore.getServerManager().createServerData(data.get("name").getAsString());
            }
            serverData.setWhitelisted(data.get("whitelisted").getAsBoolean());
            serverData.setLastTick(data.get("lastTick").getAsLong());
            serverData.setMaxPlayers(data.get("maxPlayers").getAsInt());
            serverData.setRecentTps(new double[]{data.get("tps1").getAsDouble(), data.get("tps2").getAsDouble(), data.get("tps3").getAsDouble()});
            serverData.setNames(StringUtils.getListFromString(data.get("players").getAsString()));
            try {
                ServerManager.getInstance().removeInActiveServers();
                ServerManager.getInstance().removeInActivePlayers();
            } catch (Exception ignored) {
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public JsonBuilder getData() {
        return jsonBuilder;
    }

    @Override
    public String getName() {
        return "ServerUpdatePacket";
    }
}
