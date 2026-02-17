
package net.octopvp.octocore.velocity.manager;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.OnlinePlayer;
import net.octopvp.octocore.common.redis.packets.NetworkSummaryPacket;
import net.octopvp.octocore.common.redis.packets.ServerDataPacket;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.velocity.OctoCoreVelocity;
import net.octopvp.octocore.velocity.objects.OnlinePlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class OnlinePlayersManager implements Runnable {
    @Getter
    @Setter
    private static Map<UUID, OnlinePlayerData> dataMap = new ConcurrentHashMap<>();

    @Override
    public void run() {
        dataMap.forEach((uuid, data) -> {
            Logger.debug("Updating player data for " + uuid.toString());
            data.update();
        });

        ArrayList<OnlinePlayer> onlinePlayers = new ArrayList<>();
        int vanishedCount = 0;
        for (Player player : OctoCoreVelocity.getInstance().getProxyServer().getAllPlayers()) {
            OnlinePlayerData data = dataMap.get(player.getUniqueId());
            boolean vanished = data != null && data.isVanished();
            if (vanished) {
                vanishedCount++;
                continue;
            }
            onlinePlayers.add(new OnlinePlayer(
                    player.getUniqueId(),
                    player.getUsername(),
                    player.getRemoteAddress().getAddress().getHostAddress(),
                    player.getCurrentServer().map(s -> s.getServerInfo().getName()).orElse("Unknown"),
                    false, // vanished
                    0 // vanishPriority
            ));
        }

        new ServerDataPacket(
                OctoCoreCommon.getInstance().getServerName(),
                onlinePlayers,
                OctoCoreVelocity.getInstance().getProxyServer().getConfiguration().getShowMaxPlayers(),
                System.currentTimeMillis(),
                false, // whitelisted
                20.0, 20.0, 20.0, // tps
                false // maintenance
        ).send();

        // Summary correlation
        Map<String, Integer> serverPlayerCounts = new HashMap<>();
        OctoCoreCommon.getInstance().getServerImplementation().getServerManager().getConnectedServers().forEach(serverData -> {
            int nonVanishedCount = (int) serverData.getOnlinePlayers().stream().filter(p -> !p.isVanished()).count();
            serverPlayerCounts.put(serverData.getServerName(), nonVanishedCount);
        });
        int totalPlayers = OctoCoreVelocity.getInstance().getProxyServer().getPlayerCount() - vanishedCount;
        new NetworkSummaryPacket(totalPlayers, serverPlayerCounts, System.currentTimeMillis()).send();
    }
}
