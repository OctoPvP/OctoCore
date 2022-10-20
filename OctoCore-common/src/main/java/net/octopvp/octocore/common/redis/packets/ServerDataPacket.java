package net.octopvp.octocore.common.redis.packets;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.octopvp.aetheriacore.common.AetheriaCoreCommon;
import net.octopvp.aetheriacore.common.object.ServerData;
import net.octopvp.aetheriacore.common.object.redis.packet.RedisPacket;

import java.util.ArrayList;
import java.util.Iterator;

@AllArgsConstructor
@NoArgsConstructor
public class ServerDataPacket extends RedisPacket {
    @Getter
    @Setter
    private static Implementation implementation = null;
    private String name;

    private ArrayList<String> names;

    private int maxPlayers, players;

    private long lastTick;

    private boolean whitelisted;

    private double tps1, tps2, tps3;

    private boolean maintenance, rpg;

    @Override
    public void onReceive(JsonObject jsonObject) {
        ServerData serverData = AetheriaCoreCommon.getInstance().getServerImplementation().getServerManager().getServerData(name);
        if (serverData == null)
            serverData = AetheriaCoreCommon.getInstance().getServerImplementation().getServerManager().createServerData(name);
        serverData.setNames(names);
        serverData.setMaxPlayers(maxPlayers);
        serverData.setPlayers(players);
        serverData.setLastTick(lastTick);
        serverData.setWhitelisted(whitelisted);
        serverData.setRecentTps(new double[]{tps1, tps2, tps3});
        serverData.setMaintenance(maintenance);
        serverData.setRpgServer(rpg);
        Iterator<ServerData> iterator = AetheriaCoreCommon.getInstance().getServerImplementation().getServerManager().getConnectedServers().iterator();
        while (iterator.hasNext()) {
            ServerData connectedServer = iterator.next();
            boolean time = System.currentTimeMillis() - connectedServer.getLastTick() >= 15000L, removed = false;
            if (time || connectedServer.isSafelyStopped()) {
                //AetheriaCorePaper.getInstance().getServerManager().getConnectedServers().remove(connectedServer);
                iterator.remove();
                removed = true;
            }
            if (removed) {
                if (implementation != null) implementation.onServerRemoved(connectedServer);
            }
        }
        //fix ConcurrentModificationException -> https://stackoverflow.com/a/25131800
        serverData.getOnlinePlayers().removeIf(globalPlayer -> System.currentTimeMillis() - globalPlayer.getLastActivity() >= 5000L);
    }

    @Override
    public String getType() {
        return "SERVER_DATA";
    }

    public static interface Implementation {
        void onServerRemoved(ServerData connectedServer);
    }
}
