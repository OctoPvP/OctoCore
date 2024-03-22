
package net.octopvp.octocore.velocity.manager;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.velocity.objects.OnlinePlayerData;
import net.octopvp.octocore.common.util.Logger;

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
    }
}
