package net.octopvp.octocore.core.database.redis.packets.server;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.ServerData;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.core.OctoCore;

@AllArgsConstructor
@NoArgsConstructor
public class ServerUpdatePacket extends RedisPacket {
    private String name;

    @Override
    public void onReceive(JsonObject data) {
        try {
            ServerData serverData = OctoCore.getInstance().getServerManager().getServerData(name);
            if (serverData == null) {
                serverData = OctoCore.getInstance().getServerManager().createServerData(name);
            }
            serverData.setWhitelisted(data.get("whitelisted").getAsBoolean());
            serverData.setLastTick(data.get("lastTick").getAsLong());
            serverData.setMaxPlayers(data.get("maxPlayers").getAsInt());
            serverData.setRecentTps(new double[]{data.get("tps1").getAsDouble(), data.get("tps2").getAsDouble(), data.get("tps3").getAsDouble()});
            serverData.setNames(StringUtils.getListFromString(data.get("players").getAsString()));

            /*
            try {
                OctoCoreCommon.getInstance().getServerManager().removeInActiveServers();
                OctoCoreCommon.getInstance().getServerManager().removeInActivePlayers();
            } catch (Exception ignored) {
            }
             */
        } catch (Exception ignored) {
        }
    }
}
