package net.octopvp.octocore.paper.database.redis.packets.player;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.api.events.GlobalPlayerCreateEvent;
import net.octopvp.octocore.paper.manager.impl.ServerManager;
import net.octopvp.octocore.paper.manager.impl.TagManager;
import net.octopvp.octocore.paper.objects.GlobalPlayer;
import net.octopvp.octocore.paper.objects.PlayerTag;
import net.octopvp.octocore.paper.objects.ServerData;
import net.octopvp.octocore.paper.utils.GsonSerializer;
import net.octopvp.octocore.paper.utils.runnable.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class PlayerDataPacket extends RedisPacket {
    private static final ArrayList<UUID> alreadyCreating = new ArrayList<>();
    private JsonBuilder jsonBuilder;

    @Override
    public void onReceive(JsonObject data) throws Exception {
        if (!data.has("name") || data.get("name").isJsonNull()) {
            Logger.error("Received data without name: " + OctoCore.getGson().toJson(data));
            return;
        }
        boolean created = false;
        GlobalPlayer globalPlayer = OctoCore.getServerManager().getGlobalPlayer(data.get("name").getAsString());
        String from = null;
        UUID uuid = UUID.fromString(data.get("uuid").getAsString());
        if (globalPlayer != null)
            from = globalPlayer.getServer(); //get globalplayer data (from) before updating
        if (alreadyCreating.contains(uuid)) {
            return;
        }
        if (globalPlayer == null) {
            ServerData serverData = OctoCore.getServerManager().getServerData(data.get("server").getAsString());
            if (serverData != null) {
                alreadyCreating.add(uuid);
                Tasks.runLater(() -> {
                    alreadyCreating.remove(uuid);
                }, 15l);
                GlobalPlayer gPlayer = new GlobalPlayer();
                gPlayer.setName(data.get("name").getAsString());

                serverData.getOnlinePlayers().add(gPlayer);
                ServerManager.getInstance().getRealGlobalPlayers().put(gPlayer.getName().toLowerCase(), globalPlayer);

                created = true;
                globalPlayer = OctoCore.getServerManager().getGlobalPlayer(data.get("name").getAsString());
            }
        }
        if (globalPlayer == null) return;

        globalPlayer.setServer(data.get("server").getAsString());
        globalPlayer.setName(data.get("name").getAsString());
        globalPlayer.setUniqueId(uuid);
        globalPlayer.setLastSeen(data.get("lastSeen").getAsLong());
        globalPlayer.setFirstJoined(data.get("firstJoined").getAsString());
        globalPlayer.setLastActivity(data.get("lastActivity").getAsLong());
        globalPlayer.setVanished(data.has("vanished") && data.get("vanished").getAsBoolean());
        globalPlayer.setLastServer(data.has("lastServer") ? data.get("lastServer").getAsString() : null);
        globalPlayer.setStaffChatAlerts(data.has("staffChatAlerts") && data.get("staffChatAlerts").getAsBoolean());
        globalPlayer.setAdminChatAlerts(data.has("adminChatAlerts") && data.get("adminChatAlerts").getAsBoolean());
        globalPlayer.setReportAlerts(data.has("reportAlerts") && data.get("reportAlerts").getAsBoolean());
        Set<UUID> tagsUUID = GsonSerializer.deserializeUUIDSet(data.get("allTags").getAsString());
        List<PlayerTag> tags = new ArrayList<>();
        for (UUID uuid1 : tagsUUID) {
            PlayerTag tag = TagManager.getTag(uuid1);
            if (tag != null) tags.add(tag);
        }
        globalPlayer.setAllTags(tags);
        if (created) {
            OctoCore.getInstance().getServer().getPluginManager().callEvent(new GlobalPlayerCreateEvent(globalPlayer));
        }
    }

    @Override
    public JsonBuilder getData() {
        return jsonBuilder;
    }

    @Override
    public String getName() {
        return "PlayerDataPacket";
    }
}
