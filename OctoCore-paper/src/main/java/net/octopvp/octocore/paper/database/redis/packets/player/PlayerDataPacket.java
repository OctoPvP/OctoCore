package net.octopvp.octocore.paper.database.redis.packets.player;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.ServerManager;
import net.octopvp.octocore.paper.manager.impl.TagManager;
import net.octopvp.octocore.paper.objects.GlobalPlayer;
import net.octopvp.octocore.paper.objects.PlayerTag;
import net.octopvp.octocore.paper.utils.GsonSerializer;
import net.octopvp.octocore.paper.utils.GsonType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class PlayerDataPacket extends RedisPacket {
    private JsonBuilder jsonBuilder;

    @Override
    public void onReceive(JsonObject data) throws Exception {
        if (!data.has("name") || data.get("name").isJsonNull()) {
            Logger.error("Received data without name: " + OctoCore.getGson().toJson(data));
            return;
        }
        GlobalPlayer player = ServerManager.getInstance().getRealGlobalPlayer(data.get("name").getAsString());
        if (player == null) {
            player = new GlobalPlayer(UUID.fromString(data.get("uuid").getAsString()), data.get("name").getAsString());
            ServerManager.getInstance().getRealGlobalPlayers().put(player.getName().toLowerCase(), player);
        }
        player.setServer(data.get("server").getAsString());
        player.setFirstJoined(data.get("firstJoined").getAsString());
        player.setLastServer(data.has("lastServer") ? data.get("lastServer").getAsString() : null);
        player.setAddress(data.get("address").getAsString());
        player.setRankName(data.get("rank").getAsString());

        player.setVanished(data.has("vanished") && data.get("vanished").getAsBoolean());
        player.setStaffChatAlerts(data.has("staffChatAlerts") && data.get("staffChatAlerts").getAsBoolean());
        player.setAdminChatAlerts(data.has("adminChatAlerts") && data.get("adminChatAlerts").getAsBoolean());
        player.setReportAlerts(data.has("reportAlerts") && data.get("reportAlerts").getAsBoolean());
        player.setLeaving(data.has("quited") && data.get("quited").getAsBoolean());

        player.setLastSeen(data.get("lastSeen").getAsLong());
        player.setLastActivity(data.get("lastActivity").getAsLong());

        Set<UUID> tagsUUID = GsonSerializer.deserializeUUIDSet(data.get("allTags").getAsString());
        List<PlayerTag> tags = new ArrayList<>();
        for (UUID uuid1 : tagsUUID) {
            PlayerTag tag = TagManager.getTag(uuid1);
            if (tag != null) tags.add(tag);
        }
        player.setAllTags(tags);

        player.setPermissions(OctoCore.getGson().fromJson(data.get("permissions").getAsString(), GsonType.STRING_SERVER_CONTEXT_MAP));
        player.setNegatedPermissions(OctoCore.getGson().fromJson(data.get("negated-permissions").getAsString(), GsonType.STRING_SERVER_CONTEXT_MAP));
        player.setAlts(data.has("alts") ? OctoCore.getGson().fromJson(data.get("alts").getAsString(), GsonType.ALT) : new ArrayList<>());
        player.setAddresses(StringUtils.getListFromString(data.get("addresses").getAsString()));

        player.setRankWeight(data.get("rankWeight").getAsInt());
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
