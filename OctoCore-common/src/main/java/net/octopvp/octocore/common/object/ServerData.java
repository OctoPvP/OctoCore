package net.octopvp.octocore.common.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class ServerData {

    private final String serverName;
    boolean safelyStopped;
    private long lastTick;
    private boolean whitelisted, maintenance;
    private int maxPlayers;
    private List<OnlinePlayer> onlinePlayers = new ArrayList<>();
    private List<String> names = new ArrayList<>();
    private double[] recentTps = new double[]{20.0, 20.0, 20.0};

    public static ServerData createDummyData(String name) {
        ServerData serverData = new ServerData(name);
        serverData.setLastTick(System.currentTimeMillis());
        serverData.setSafelyStopped(true);
        serverData.setWhitelisted(false);
        serverData.setMaintenance(false);
        serverData.setMaxPlayers(100);
        // random between 18-21
        double tps = Math.random() * 3 + 18;
        serverData.setRecentTps(new double[]{tps, 20.0, 20.0});
        int playerCount = (int) (Math.random() * 100);
        for (int i = 0; i < playerCount; i++) {
            String randName = "Player" + (int) (Math.random() * 1000);
            serverData.getOnlinePlayers().add(OnlinePlayer.createDummyData());
            serverData.getNames().add(randName);
        }
        return serverData;
    }

    public String getFormattedTPS() {
        double tps = recentTps[0];
        if (tps > 20.00) {
            return "20*";
        }
        return String.format("%.2f", tps);
    }
}
