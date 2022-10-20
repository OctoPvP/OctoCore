package net.octopvp.octocore.common.redis.packets;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.GlobalPlayer;
import net.octopvp.octocore.common.object.ServerData;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.Logger;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@AllArgsConstructor
@NoArgsConstructor
public class PlayerDataPacket extends RedisPacket {

    private static List<UUID> alreadyCreating = new CopyOnWriteArrayList<>();

    private static PlayerDataPacketImplementation implementation;

    private UUID uuid;
    private String server;
    private String prefix, color, name;
    private long lastActivity;
    private boolean vanished;
    private boolean staff;

    @Override
    public void onReceive(JsonObject data) {
        if (uuid == null) {
            Logger.error("Received data without uuid: " + OctoCoreCommon.getInstance().getGson().toJson(data));
            return;
        }
        if (name == null) {
            Logger.error("Received data without name: " + OctoCoreCommon.getInstance().getGson().toJson(data));
            //return;
            name = implementation.getOfflineName(uuid);
        }
        boolean created = false;
        GlobalPlayer globalPlayer =
                OctoCoreCommon.getInstance().getServerImplementation()
                        .getServerManager()
                        .getGlobalPlayer(name);
        String from = null;
        if (globalPlayer != null)
            from = globalPlayer.getServer(); //get globalplayer data (from) before updating
        if (alreadyCreating.contains(uuid)) {
            return;
        }
        if (globalPlayer == null) {
            ServerData serverData = OctoCoreCommon.getInstance().getServerImplementation().getServerManager().getServerData(server);
            if (serverData != null) {
                alreadyCreating.add(uuid);
                implementation.runLater(() -> {
                    alreadyCreating.remove(uuid);
                }, Duration.ofMillis(750)); // TODO remove this ASAP
                GlobalPlayer gPlayer = new GlobalPlayer(uuid, name);

                serverData.getOnlinePlayers().add(gPlayer);

                created = true;
                globalPlayer = OctoCoreCommon.getInstance().getServerImplementation().getServerManager().getGlobalPlayer(name);
            }
        }
        if (globalPlayer == null) return;

        globalPlayer.setServer(server);
        globalPlayer.setName(name);
        globalPlayer.setUuid(uuid);
        globalPlayer.setLastActivity(lastActivity);
        globalPlayer.setStaff(staff);

        if (prefix != null)
            globalPlayer.setPrefix(prefix);
        if (color != null)
            globalPlayer.setColor(color);
        globalPlayer.setVanished(vanished);

        if (created) {
            Logger.debug("Created global player: " + OctoCoreCommon.getInstance().getGson().toJson(globalPlayer));
        }
    }

    @Override
    public String getType() {
        return "PLAYER_DATA";
    }

    public static interface PlayerDataPacketImplementation {
        void runLater(Runnable runnable, Duration duration);

        String getOfflineName(UUID uuid);
    }
}
