package net.octopvp.octocore.waterfall.manager;

import lombok.Getter;
import net.octopvp.octocore.waterfall.util.object.OnlinePlayerData;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class OnlinePlayersManager {
    @Getter
    private static Map<UUID, OnlinePlayerData> dataMap = new ConcurrentHashMap<>();
}
