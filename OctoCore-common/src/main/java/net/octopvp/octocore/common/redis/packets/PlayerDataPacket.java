package net.octopvp.octocore.common.redis.packets;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.GlobalPlayer;
import net.octopvp.octocore.common.object.MessageSettings;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.common.object.ServerData;
import net.octopvp.octocore.common.object.punish.Alt;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.Logger;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@AllArgsConstructor
@NoArgsConstructor
public class PlayerDataPacket extends RedisPacket {

    private static final List<UUID> alreadyCreating = new CopyOnWriteArrayList<>();
    @Getter
    @Setter
    private static PlayerDataPacketImplementation implementation;

    private UUID uuid;
    private String server;
    private String name, lastServer, address, rank;
    private long lastActivity, firstJoined, lastSeen;
    private boolean vanished, staffChatAlerts, adminChatAlerts, reportAlerts, staff;
    private Set<UUID> allTags;
    private Map<String, ServerContext> permissions, negatedPermissions;
    private List<Alt> alts;
    private List<String> addresses;
    private int rankWeight;
    private MessageSettings messageSettings;
    private String coloredName;
    private boolean op;

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
                if (implementation != null) {
                    implementation.runLater(() -> {
                        alreadyCreating.remove(uuid);
                    }, Duration.ofMillis(750)); // TODO remove this ASAP
                }
                GlobalPlayer gPlayer = new GlobalPlayer(uuid, name);

                serverData.getOnlinePlayers().add(gPlayer);

                created = true;
                globalPlayer = OctoCoreCommon.getInstance().getServerImplementation().getServerManager().getGlobalPlayer(name);
            }
        }
        if (globalPlayer == null) return;

        globalPlayer.setServer(server);
        globalPlayer.setLastServer(lastServer);
        globalPlayer.setAddress(address);
        globalPlayer.setRankName(rank);
        globalPlayer.setLastActivity(lastActivity);
        globalPlayer.setFirstJoined(firstJoined);
        globalPlayer.setLastSeen(lastSeen);
        globalPlayer.setVanished(vanished);
        globalPlayer.setStaffChatAlerts(staffChatAlerts);
        globalPlayer.setAdminChatAlerts(adminChatAlerts);
        globalPlayer.setReportAlerts(reportAlerts);
        globalPlayer.setStaff(staff);
        globalPlayer.setAllTags(new ArrayList<>(allTags));
        globalPlayer.setPermissions(permissions);
        globalPlayer.setNegatedPermissions(negatedPermissions);
        globalPlayer.setAlts(alts);
        globalPlayer.setAddresses(addresses);
        globalPlayer.setRankWeight(rankWeight);
        globalPlayer.setMessageSettings(messageSettings);
        globalPlayer.setColoredName(coloredName);
        globalPlayer.setOp(op);

        if (created) {
            Logger.debug("Created global player: " + OctoCoreCommon.getInstance().getGson().toJson(globalPlayer));
        }
    }

    public interface PlayerDataPacketImplementation {
        void runLater(Runnable runnable, Duration duration);

        String getOfflineName(UUID uuid);
    }
}
