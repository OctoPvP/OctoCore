package net.octopvp.octocore.paper.database.redis.payload;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.api.events.GlobalPlayerCreateEvent;
import net.octopvp.octocore.paper.api.events.GlobalPlayerDestroyEvent;
import net.octopvp.octocore.paper.database.redis.object.JedisAction;
import net.octopvp.octocore.paper.database.redis.object.JedisHandle;
import net.octopvp.octocore.paper.objects.GlobalPlayer;
import net.octopvp.octocore.paper.objects.ServerData;
import net.octopvp.octocore.paper.utils.Logger;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.UUID;

public class GlobalSubscription implements JedisHandle {
    private static OctoCore plugin = OctoCore.getInstance();
    @Override
    public void handleMessage(JsonObject object) {
        Logger.debug("Received redis pub/sub message! Payload: " + object.toString());
        JedisAction payload;
        try {
            payload = JedisAction.valueOf(object.get("payload").getAsString());
        } catch (IllegalArgumentException ignored) {
            return;
        }
        JsonObject data = object.get("data").getAsJsonObject();
        if (payload == JedisAction.SERVER_DATA) {
            ServerData serverData = plugin.getServerManager().getServerData(data.get("name").getAsString());
            if (serverData == null) {
                serverData = plugin.getServerManager().createServerData(data.get("name").getAsString());
            }
            serverData.setWhitelisted(data.get("whitelisted").getAsBoolean());
            serverData.setLastTick(data.get("lastTick").getAsLong());
            serverData.setMaxPlayers(data.get("maxPlayers").getAsInt());
            serverData.setRecentTps(new double[]{data.get("tps1").getAsDouble(), data.get("tps2").getAsDouble(), data.get("tps3").getAsDouble()});
            serverData.setNames(StringUtils.getListFromString(data.get("players").getAsString()));

            plugin.getServerManager().getConnectedServers().removeIf(next -> System.currentTimeMillis() - next.getLastTick() >= 15000L);

            Iterator<GlobalPlayer> globalPlayers = serverData.getOnlinePlayers().iterator();
            while (globalPlayers.hasNext()) {
                GlobalPlayer globalPlayer = globalPlayers.next();

                if (System.currentTimeMillis() - globalPlayer.getLastActivity() >= 5000L) {
                    GlobalPlayerDestroyEvent event = new GlobalPlayerDestroyEvent(globalPlayer);
                    plugin.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        globalPlayers.remove();
                    }
                }
            }
        }
        if (payload == JedisAction.PLAYER_DATA) {
            boolean created = false;
            GlobalPlayer globalPlayer = OctoCore.getServerManager().getGlobalPlayer(data.get("name").getAsString());
            if (globalPlayer == null) {
                ServerData serverData = OctoCore.getServerManager().getServerData(data.get("server").getAsString());
                if (serverData != null) {
                    GlobalPlayer toAdd = new GlobalPlayer();
                    toAdd.setName(data.get("name").getAsString());

                    serverData.getOnlinePlayers().add(toAdd);

                    created = true;
                    globalPlayer = OctoCore.getServerManager().getGlobalPlayer(data.get("name").getAsString());
                }
            }
            if (globalPlayer == null) return;

            globalPlayer.setName(data.get("name").getAsString());
            globalPlayer.setUniqueId(UUID.fromString(data.get("uuid").getAsString()));
            globalPlayer.setServer(data.get("server").getAsString());
            globalPlayer.setAddress(data.get("address").getAsString());
            globalPlayer.setRankName(data.get("rank").getAsString());
            globalPlayer.setLastSeen(data.get("lastSeen").getAsLong());
            globalPlayer.setFirstJoined(data.get("firstJoined").getAsString());
            globalPlayer.setLastActivity(data.get("lastActivity").getAsLong());
            globalPlayer.setVanished(data.has("vanished") && data.get("vanished").getAsBoolean());
            globalPlayer.setLastServer(data.has("lastServer") ? data.get("lastServer").getAsString() : null);
            globalPlayer.setStaffChatAlerts(data.has("staffChatAlerts") && data.get("staffChatAlerts").getAsBoolean());
            globalPlayer.setAdminChatAlerts(data.has("adminChatAlerts") && data.get("adminChatAlerts").getAsBoolean());
            globalPlayer.setReportAlerts(data.has("reportAlerts") && data.get("reportAlerts").getAsBoolean());

            if (created) {
                plugin.getServer().getPluginManager().callEvent(new GlobalPlayerCreateEvent(globalPlayer));
            }
        }
        if (payload == JedisAction.PLAYER_MESSAGE) {
            Player player = Bukkit.getPlayer(data.get("name").getAsString());
            if (player != null) {
                player.sendMessage(data.get("message").getAsString());
            }
        }
        if (payload == JedisAction.SERVER_ONLINE) {
            String server = data.get("server").getAsString();
            //TODO staff alert
        }
        if (payload == JedisAction.SERVER_OFFLINE) {
            String server = data.get("server").getAsString();
        }
        if (payload == JedisAction.REPORT_SAVE) {

        }
        if (payload == JedisAction.STAFF_CONNECT) {
            String name = data.get("name").getAsString();
            String server = data.get("server").getAsString();
        }
        if (payload == JedisAction.STAFF_SWITCH) {
            String name = data.get("name").getAsString();
            String server = data.get("server").getAsString();
            String from = data.get("from").getAsString();
        }
        if (payload == JedisAction.STAFF_DISCONNECT) {
            String name = data.get("name").getAsString();
            String server = data.get("server").getAsString();
        }
        if (payload == JedisAction.SERVER_COMMAND) {
            String server = data.get("server").getAsString();
            String command = data.get("command").getAsString();

            if (command.startsWith("/")) {
                command = command.substring(1);
            }
            if (server.equalsIgnoreCase("all")) {
                Bukkit.getConsoleSender().sendMessage(Lang.EXECUTING_REQUESTED_COMMAND.getMsg(command,data.get("sender").getAsString()));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            } else if (OctoCore.getServerName().equalsIgnoreCase(server)) {
                Bukkit.getConsoleSender().sendMessage(Lang.EXECUTING_REQUESTED_COMMAND.getMsg(command,data.get("sender").getAsString()));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
        if(payload == JedisAction.SEND_DISCORD_MESSAGE){
            if(OctoCore.isMaster()){
                String json = data.get("messagejson").getAsString();
                String channel = data.get("channel").getAsString();
                EmbedBuilder embedBuilder = OctoCore.getGson().fromJson(json, EmbedBuilder.class);
                OctoCore.getInstance().getSetupManager().getJdaManager().getJda().getTextChannelById(channel).sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
