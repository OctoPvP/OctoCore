package net.octopvp.octocore.paper.database.redis.packets.server;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.packets.staff.AdminAlertPacket;
import net.octopvp.octocore.paper.manager.impl.ServerManager;
import net.octopvp.octocore.paper.objects.ServerData;

import java.util.Iterator;

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
            Iterator iterator = OctoCore.getServerManager().getConnectedServers().iterator();
            while (iterator.hasNext()) {
                ServerData connectedServer = (ServerData) iterator.next();
                boolean time = System.currentTimeMillis() - connectedServer.getLastTick() >= 15000L, removed = false;
                if (time || connectedServer.isSafelyStopped()) {
                    iterator.remove();
                    removed = true;
                }
                if (removed && !connectedServer.isSafelyStopped()) {
                    new AdminAlertPacket().onReceive(new JsonBuilder().add("message", CC.RED + connectedServer.getServerName() + " may have crashed (has not responded for 15 seconds)").get());
                }
            }
            try {
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
