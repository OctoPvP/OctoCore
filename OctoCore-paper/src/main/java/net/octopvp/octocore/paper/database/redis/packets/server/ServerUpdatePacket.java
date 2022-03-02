package net.octopvp.octocore.paper.database.redis.packets.server;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.api.events.GlobalPlayerDestroyEvent;
import net.octopvp.octocore.paper.database.redis.packets.staff.AdminAlertPacket;
import net.octopvp.octocore.paper.objects.GlobalPlayer;
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
                    if (OctoCore.isMaster()) { //make sure these kind of broadcasts only happen on master
                        new AdminAlertPacket(CC.RED + connectedServer.getServerName() + " may have crashed (has not responded for 15 seconds)").send();
                    }
                }
            }

            //Iterator<GlobalPlayer> globalPlayers = serverData.getOnlinePlayers().iterator();
            for (Iterator<GlobalPlayer> globalPlayerIterator = serverData.getOnlinePlayers().iterator(); globalPlayerIterator.hasNext(); ) { //fix ConcurrentModificationException -> https://stackoverflow.com/a/25131800
                GlobalPlayer globalPlayer = globalPlayerIterator.next();

                if (System.currentTimeMillis() - globalPlayer.getLastActivity() >= 5000L) {
                    GlobalPlayerDestroyEvent event = new GlobalPlayerDestroyEvent(globalPlayer);
                    OctoCore.getInstance().getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        globalPlayerIterator.remove();
                    }
                }
            }
        } catch (Exception ignored) {}
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
