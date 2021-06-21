package net.octopvp.octocore.paper.objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ServerData {

    private final String serverName;

    private long lastTick;
    private boolean whitelisted, maintenance;
    private int maxPlayers;
    private List<GlobalPlayer> onlinePlayers = new ArrayList<>();
    private List<String> names = new ArrayList<>();
    private double[] recentTps = new double[]{20.0, 20.0, 20.0};
    boolean safelyStopped;

}
