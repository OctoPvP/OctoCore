package net.octopvp.octocore.common.redis.packets;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.OnlinePlayer;
import net.octopvp.octocore.common.object.ServerData;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class ServerDataPacket extends RedisPacket {
    @Getter
    @Setter
    private static Implementation implementation = null;
    private String name;

    private ArrayList<OnlinePlayer> onlinePlayers;

    private int maxPlayers;

    private long lastTick;

    private boolean whitelisted;

    private double tps1, tps2, tps3;

    private boolean maintenance;

    @Override
    public void onReceive(JsonObject jsonObject) {
        ServerData serverData = OctoCoreCommon.getInstance().getServerImplementation().getServerManager().getServerData(name);
        if (serverData == null)
            serverData = OctoCoreCommon.getInstance().getServerImplementation().getServerManager().createServerData(name);
        serverData.setNames(onlinePlayers.stream().map(OnlinePlayer::getName).collect(Collectors.toList()));
        serverData.setMaxPlayers(maxPlayers);
        serverData.setLastTick(lastTick);
        serverData.setWhitelisted(whitelisted);
        serverData.setRecentTps(new double[]{tps1, tps2, tps3});
        serverData.setMaintenance(maintenance);
        serverData.setOnlinePlayers(onlinePlayers);
        Iterator<ServerData> iterator = OctoCoreCommon.getInstance().getServerImplementation().getServerManager().getConnectedServers().iterator();
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
    }

    public interface Implementation {
        void onServerRemoved(ServerData connectedServer);
    }
}
