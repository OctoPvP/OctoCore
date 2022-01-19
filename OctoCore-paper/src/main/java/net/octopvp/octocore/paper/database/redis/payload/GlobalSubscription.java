package net.octopvp.octocore.paper.database.redis.payload;

import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.common.object.redis.JedisHandle;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.api.events.GlobalPlayerCreateEvent;
import net.octopvp.octocore.paper.api.events.GlobalPlayerDestroyEvent;
import net.octopvp.octocore.paper.listeners.redis.MainRedisHandler;
import net.octopvp.octocore.paper.listeners.redis.PunishmentRedisHandler;
import net.octopvp.octocore.paper.manager.impl.*;
import net.octopvp.octocore.paper.objects.*;
import net.octopvp.octocore.paper.objects.enums.AuditLogType;
import net.octopvp.octocore.paper.objects.enums.DataUpdateReason;
import net.octopvp.octocore.paper.objects.permissions.Grant;
import net.octopvp.octocore.paper.utils.chat.Clickable;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class GlobalSubscription implements JedisHandle {
    //TODO bungeecord fallback
    private static final ArrayList<UUID> alreadyCreating = new ArrayList<>();

    private static final OctoCore plugin = OctoCore.getInstance();
    @Override
    public void handleMessage(JsonObject object) {
        JedisAction payload;
        try {
            payload = JedisAction.valueOf(object.get("payload").getAsString());
        } catch (IllegalArgumentException ignored) {
            return;
        }
        JsonObject data = object.get("data").getAsJsonObject();
        RedisListenerManager.handleMessage(payload,data);
        if (payload == JedisAction.SERVER_DATA) {
            ServerData serverData = OctoCore.getServerManager().getServerData(data.get("name").getAsString());
            if (serverData == null) {
                serverData = OctoCore.getServerManager().createServerData(data.get("name").getAsString());
            }
            serverData.setWhitelisted(data.get("whitelisted").getAsBoolean());
            serverData.setLastTick(data.get("lastTick").getAsLong());
            serverData.setMaxPlayers(data.get("maxPlayers").getAsInt());
            serverData.setRecentTps(new double[]{data.get("tps1").getAsDouble(), data.get("tps2").getAsDouble(), data.get("tps3").getAsDouble()});
            serverData.setNames(StringUtils.getListFromString(data.get("players").getAsString()));
            Iterator iterator = OctoCore.getServerManager().getConnectedServers().iterator();
            while (iterator.hasNext()) {
                ServerData connectedServer = (ServerData) iterator.next();
                boolean time = System.currentTimeMillis() - connectedServer.getLastTick() >= 15000L, removed = false;
                if (time || connectedServer.isSafelyStopped()) {
                    iterator.remove();
                    removed = true;
                }
                if (removed && !connectedServer.isSafelyStopped()) {
                    if (OctoCore.isMaster()) { //make sure these kind of broadcasts only happen on master
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("message", CC.RED + connectedServer.getServerName() + " may have crashed (has not responded for 15 seconds)");
                        plugin.getRedisData().write(JedisAction.ADMIN_ALERT, jsonObject);
                    }
                }
            }

            //Iterator<GlobalPlayer> globalPlayers = serverData.getOnlinePlayers().iterator();
            for (Iterator<GlobalPlayer> globalPlayerIterator = serverData.getOnlinePlayers().iterator();globalPlayerIterator.hasNext();){ //fix ConcurrentModificationException -> https://stackoverflow.com/a/25131800
                GlobalPlayer globalPlayer = globalPlayerIterator.next();

                if (System.currentTimeMillis() - globalPlayer.getLastActivity() >= 5000L) {
                    GlobalPlayerDestroyEvent event = new GlobalPlayerDestroyEvent(globalPlayer);
                    plugin.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        globalPlayerIterator.remove();
                    }
                }
            }
            /*
            while (globalPlayers.hasNext()) {
                GlobalPlayer globalPlayer = globalPlayers.next();

                if (System.currentTimeMillis() - globalPlayer.getLastActivity() >= 5000L) {
                    GlobalPlayerDestroyEvent event = new GlobalPlayerDestroyEvent(globalPlayer);
                    plugin.getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        if(OctoCore.isMaster()) {
                            globalPlayer.hasPermission(Permission.SEND_LEAVE_MESSAGE.getNode()).thenAcceptAsync(b ->{
                                if (b) PlayerManager.sendStaffAlert(AlertType.LEAVE, globalPlayer.getName(), OctoCore.getServerName());
                            });
                        }
                        globalPlayers.remove();
                    }
                }
            }
             */
        }
        if (payload == JedisAction.PLAYER_DATA) {
            boolean created = false;
            GlobalPlayer globalPlayer = OctoCore.getServerManager().getGlobalPlayer(data.get("name").getAsString());
            String from = null;
            UUID uuid = UUID.fromString(data.get("uuid").getAsString());
            if (globalPlayer != null)
                from = globalPlayer.getServer(); //get globalplayer data (from) before updating
            if (alreadyCreating.contains(uuid)){
                return;
            }
            if (globalPlayer == null) {
                ServerData serverData = OctoCore.getServerManager().getServerData(data.get("server").getAsString());
                if (serverData != null) {
                    alreadyCreating.add(uuid);
                    Tasks.runLater(()->{
                        alreadyCreating.remove(uuid);
                    },15l);
                    GlobalPlayer gPlayer = new GlobalPlayer();
                    gPlayer.setName(data.get("name").getAsString());

                    serverData.getOnlinePlayers().add(gPlayer);

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
            HashSet<UUID> tagsUUID = OctoCore.getGson().fromJson(data.get("allTags").getAsString(), HashSet.class);
            List<PlayerTag> tags = new ArrayList<>();
            for (UUID uuid1 : tagsUUID) {
                PlayerTag tag = TagManager.getTag(uuid1);
                if (tag != null) tags.add(tag);
            }
            globalPlayer.setAllTags(tags);
            if (created) {
                plugin.getServer().getPluginManager().callEvent(new GlobalPlayerCreateEvent(globalPlayer));
            }
            return;
        }
        if (payload == JedisAction.PLAYER_MESSAGE) {
            Player player = Bukkit.getPlayer(data.get("name").getAsString());
            if (player != null) {
                player.sendMessage(data.get("message").getAsString());
            }
            return;
        }
        if (payload == JedisAction.SERVER_ONLINE) {
            String server = data.get("server").getAsString();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if(onlinePlayer.hasPermission(Permission.RECEIVE_SERVER_ONLINE_MESSAGE.getNode())){
                    onlinePlayer.sendMessage(Lang.ADMIN_ALERTS.getMsg(Lang.SERVER_ONLINE_FORMAT.getMsg(server)));
                }
            }
            return;
        }
        if (payload == JedisAction.SERVER_OFFLINE) {
            String server = data.get("server").getAsString();
            OctoCore.getServerManager().getServerData(server).setSafelyStopped(true); //so master dosen't send the crash alert
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if(onlinePlayer.hasPermission(Permission.RECEIVE_SERVER_OFFLINE_MESSAGE.getNode())){
                    onlinePlayer.sendMessage(Lang.ADMIN_ALERTS.getMsg(Lang.SERVER_OFFLINE_FORMAT.getMsg(server)));
                }
            }
            return;
        }
        if (payload == JedisAction.REPORT_SAVE) {

        }
        if (payload == JedisAction.STAFF_CONNECT) {
            String name = data.get("name").getAsString();
            String server = data.get("server").getAsString();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if(onlinePlayer.hasPermission(Permission.RECEIVE_JOIN_MESSAGE.getNode())){
                    Logger.debug("Sending " + onlinePlayer.getName() + " staff connect message");
                    onlinePlayer.sendMessage(Lang.STAFF_ALERTS.getMsg(Lang.STAFF_JOIN_ALERT_FORMAT.getMsg(name,server)));
                }
            }
            return;
        }
        if (payload == JedisAction.STAFF_SWITCH) {
            String name = data.get("name").getAsString();
            String to = data.get("to").getAsString();
            String from = data.get("from").getAsString();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if(onlinePlayer.hasPermission(Permission.RECEIVE_JOIN_MESSAGE.getNode())){
                    onlinePlayer.sendMessage(Lang.STAFF_ALERTS.getMsg(Lang.STAFF_SWITCH_ALERT_FORMAT.getMsg(name,from,to)));
                }
            }
            return;
        }
        if (payload == JedisAction.STAFF_DISCONNECT) {
            String name = data.get("name").getAsString();
            String server = data.get("server").getAsString();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if(onlinePlayer.hasPermission(Permission.RECEIVE_JOIN_MESSAGE.getNode())){
                    onlinePlayer.sendMessage(Lang.STAFF_ALERTS.getMsg(Lang.STAFF_LEAVE_ALERT_FORMAT.getMsg(name,server)));
                }
            }
            return;
        }
        if (payload == JedisAction.SERVER_COMMAND) {
            String server = data.get("server").getAsString();
            String command = data.get("command").getAsString();

            if (command.startsWith("/")) {
                command = command.substring(1);
            }
            if (OctoCore.getServerName().equalsIgnoreCase(server)) {
                Bukkit.getConsoleSender().sendMessage(Lang.EXECUTING_REQUESTED_COMMAND.getMsg(command,data.get("sender").getAsString()));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
            return;
        }
        if (payload == JedisAction.GLOBAL_COMMAND){
            String command = data.get("command").getAsString();
            Tasks.runSync(()-> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)); //prevents this: Please notify author of plugin causing this execution to fix this bug! see: http://bit.ly/1oSiM6C
            return;
        }
        if(payload == JedisAction.SEND_DISCORD_MESSAGE){
            if(OctoCore.isMaster()){
                if (!JDAManager.isEnabled())
                    return;
                String json = data.get("messagejson").getAsString();
                String channel = data.get("channel").getAsString();
                EmbedBuilder embedBuilder = OctoCore.getGson().fromJson(json, EmbedBuilder.class);
                JDAManager.getJda().getTextChannelById(channel).sendMessage(embedBuilder.build()).queue();
            }
            return;
        }
        if(payload == JedisAction.STAFF_CHAT){
            String name = data.get("name").getAsString();
            String server = data.get("server").getAsString();
            String message = data.get("message").getAsString();
            String msg = Lang.STAFF_CHAT_FORMAT.getMsg(name,server,message);
            for (Player player : Bukkit.getOnlinePlayers()){
                if(player.hasPermission(Permission.STAFFCHAT.getNode())){
                    player.sendMessage(msg);
                }
            }
            if(OctoCore.isMaster()){
                JDAManager.sendDiscordSC(name,server,message);
            }
            return;
        }
        if(payload == JedisAction.ADMIN_CHAT){
            String name = data.get("name").getAsString();
            String server = data.get("server").getAsString();
            String message = data.get("message").getAsString();
            String msg = Lang.ADMIN_CHAT_FORMAT.getMsg(name,server,message);
            for (Player player : Bukkit.getOnlinePlayers()){
                if(player.hasPermission(Permission.ADMINCHAT.getNode())){
                    player.sendMessage(msg);
                }
            }
            if(OctoCore.isMaster()){
                JDAManager.sendDiscordAC(name,server,message);
            }
            return;
        }
        if(payload == JedisAction.DISCORD_STAFF_CHAT){
            String name = data.get("name").getAsString();
            String message = data.get("message").getAsString();
            String msg = Lang.DISCORD_STAFF_CHAT_FORMAT.getMsg(name,message);
            String role = data.get("role").getAsString();
            String tag = data.get("tag").getAsString();
            TextComponent mainComponent = new TextComponent(msg);
            mainComponent.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Rank: " + role + "\nUser: " + tag).create()));
            for(Player player : Bukkit.getOnlinePlayers()){
                if(player.hasPermission(Permission.STAFFCHAT.getNode()))
                    player.sendMessage(mainComponent);
            }
            return;
        }
        if(payload == JedisAction.DISCORD_ADMIN_CHAT){
            String name = data.get("name").getAsString();
            String message = data.get("message").getAsString();
            String msg = Lang.DISCORD_ADMIN_CHAT_FORMAT.getMsg(name,message);
            String role = data.get("role").getAsString();
            String tag = data.get("tag").getAsString();
            TextComponent mainComponent = new TextComponent(msg);
            mainComponent.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Rank: " + role + "\nUser: " + tag).create()));
            for(Player player : Bukkit.getOnlinePlayers()){
                if(player.hasPermission(Permission.ADMINCHAT.getNode()))
                    player.sendMessage(mainComponent);
            }
            return;
        }
        if (payload == JedisAction.ADMIN_ALERT){
            String message = data.get("message").getAsString();
            String msg = Lang.ADMIN_ALERTS.getMsg(message);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if(onlinePlayer.hasPermission(Permission.ADMIN_ALERT.getNode())){
                    onlinePlayer.sendMessage(msg);
                }
            }
            return;
        }
        if (payload == JedisAction.AUDIT_LOG){
            AuditLogType logType = AuditLogType.valueOf(data.get("type").getAsString());
            if (logType != AuditLogType.WORLDEDIT_ACTION && logType != AuditLogType.AUTH_FAIL)
                return;
            if (logType == AuditLogType.WORLDEDIT_ACTION){
                String player = data.get("player").getAsString();
                String command = data.get("command").getAsString();

                for (Player p : Bukkit.getOnlinePlayers()){
                    if (p.hasPermission(Permission.RECEIVE_AUDIT_WORLDEDIT.getNode())){
                        //TODO finish audit log
                    }
                }
                return;
            }
            return;
        }
        if (payload == JedisAction.GLOBAL_BROADCAST){
            String message = CC.translate(data.get("message").getAsString());
            int i = Bukkit.broadcastMessage(message);
            if (data.has("player")){
                String origin = data.get("origin").getAsString();
                JsonObject response = new JsonObject();
                response.addProperty("type","GlobalBroadcastResponse");
                response.addProperty("value",i);
                response.addProperty("id",data.get("id").getAsString());
                response.addProperty("target",origin);
                response.addProperty("from",OctoCore.getServerName());
                OctoCore.getInstance().getRedisData().write(JedisAction.RESPONSE,response);
            }
            return;
        }
        if (payload == JedisAction.RESPONSE){
            String responseType = data.get("type").getAsString();
            switch (responseType){
                case "GlobalBroadcastResponse":
                    if (data.get("target").getAsString().equalsIgnoreCase(OctoCore.getServerName())){
                        int value = data.get("value").getAsInt();
                        UUID id = UUID.fromString(data.get("id").getAsString());
                        Broadcast broadcast = Broadcast.getBroadcast(id);
                        broadcast.getResponses().put(data.get("from").getAsString(),value);
                    }
                    break;
            }
            return;
        }
        if (payload == JedisAction.RELOAD_TAGS){
            TagManager.reloadTags();
            return;
        }
        if (payload == JedisAction.PDATA_UPDATE){
            DataUpdateReason reason = DataUpdateReason.valueOf(data.get("reason").getAsString());
            switch (reason){
                case TAGS_UPDATE_GIVE:
                    String target = data.get("target").getAsString();
                    String toAdd = data.get("add").getAsString();
                    if (Bukkit.getPlayer(target) != null){
                        PlayerData pdata = PlayerManager.getProfile(Bukkit.getPlayer(target).getUniqueId());
                        pdata.addTag(TagManager.getTagByName(toAdd)); //maybe get by id
                    }
                    break;
                case TAGS_UPDATE_REMOVE:
                    String targetWho = data.get("target").getAsString();
                    String toRemove = data.get("remove").getAsString();
                    if (Bukkit.getPlayer(targetWho) != null){
                        PlayerData playerData = PlayerManager.getProfile(Bukkit.getPlayer(targetWho).getUniqueId());
                        playerData.removeTag(TagManager.getTagByName(toRemove).getId());
                    }
                    break;
            }
            return;
        }
        if (payload == JedisAction.RELOAD_RANKS){
            RankManager.reloadRanks();
            return;
        }
        if (payload == JedisAction.GRANTS_UPDATE){
            String name = data.get("name").getAsString();
            String tochange = data.get("tochange").getAsString();
            boolean add = data.get("add").getAsBoolean();
            Player player = Bukkit.getPlayer(name);
            if (player != null){
                PlayerData playerData = PlayerManager.getData(player.getUniqueId());
                Grant grant = OctoCore.getGson().fromJson(tochange,Grant.class);
                if (add)
                    playerData.getGrants().add(grant);
                else playerData.getGrants().remove(grant);
                playerData.loadPerms(player);
                playerData.save();
            }
            return;
        }
        if (payload == JedisAction.SAVE_REQUEST_SWITCH){
            String id = data.get("uuid").getAsString();
            UUID uuid = UUID.fromString(id);
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                MainRedisHandler.getSaving().add(uuid);
                //JoinLeaveListener.freezePlayer(player);
                Tasks.runAsyncLater(()->{
                    if (Bukkit.getPlayer(uuid) != null){
                        //JoinLeaveListener.unfreezePlayer(player);
                        player.sendMessage(CC.RED + "Could not send you to that server!");
                    }
                },100);
                PlayerManager.processLeave(player);
            }
            return;
        }
        if (payload == JedisAction.SAVE_REQUEST_MISC){
            if (data.has("uuid")){
                String id = data.get("uuid").getAsString();

                UUID uuid = UUID.fromString(id);
                if (Bukkit.getPlayer(uuid) != null){
                    PlayerManager.getData(uuid).save();
                }
            }
            else{
                String name = data.get("name").getAsString();
                Player player = Bukkit.getPlayer(name);
                if (player != null){
                    PlayerManager.getData(player).save();
                }
            }
            return;
        }
        if (payload == JedisAction.EXECUTE_UNBAN){
            String sender = data.has("senderDisplay") ? data.get("senderDisplay").getAsString() : data.get("sender").getAsString();
            String target = data.get("target").getAsString();
            String reason = data.get("reason").getAsString();
            boolean silent = data.get("silent").getAsBoolean(),coloredNameEnabled = data.has("coloredName");
            String coloredName = sender;
            if (coloredNameEnabled)
                coloredName = data.get("coloredName").getAsString() + sender;

            Clickable clickable = new Clickable((silent ? Lang.PUNISHMENT_SILENT.toString() : "") + Lang.PUNISHMENT_UNDO.getMsg(
                    target,
                    "banned",
                    coloredName,
                    reason
            )/*,Lang.PUNISHMENT_UNBAN_HOVER.getMsg(reason)*/);

            Bukkit.getConsoleSender().sendMessage(CC.translate(clickable.getText()));

            if (silent) {
                for (Player player : Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission(Permission.PUNISHMENT_SEE_SILENT.getNode())).collect(Collectors.toList())) {
                    clickable.sendToPlayer(player);
                }
            } else {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    clickable.sendToPlayer(player);
                }
            }
            return;
        }
        if (payload == JedisAction.EXECUTE_UNMUTE){
            String sender = data.has("senderDisplay") ? data.get("senderDisplay").getAsString() : data.get("sender").getAsString();
            String target = data.get("target").getAsString();
            String reason = data.get("reason").getAsString();
            boolean silent = data.get("silent").getAsBoolean(),coloredNameEnabled = data.has("coloredName");
            String coloredName = sender;
            if (coloredNameEnabled)
                coloredName = data.get("coloredName").getAsString() + sender;

            Clickable clickable = new Clickable((silent ? Lang.PUNISHMENT_SILENT.toString() : "") + Lang.PUNISHMENT_UNDO.getMsg(
                    target,
                    "muted",
                    coloredName,
                    reason
            )/*,Lang.PUNISHMENT_UNMUTE_HOVER.getMsg(reason)*/);

            Bukkit.getConsoleSender().sendMessage(CC.translate(clickable.getText()));

            if (silent) {
                for (Player player : Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission(Permission.PUNISHMENT_SEE_SILENT.getNode())).collect(Collectors.toList())) {
                    clickable.sendToPlayer(player);
                }
            } else {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    clickable.sendToPlayer(player);
                }
            }
            return;
        }
        if (payload == JedisAction.EXECUTE_UNBLACKLIST){
            String sender = data.get("sender").getAsString();
            String target = data.get("target").getAsString();
            String reason = data.get("reason").getAsString();
            boolean silent = data.get("silent").getAsBoolean(),coloredNameEnabled = data.has("coloredName");
            String coloredName = sender;
            if (coloredNameEnabled)
                coloredName = data.get("coloredName").getAsString() + sender;

            Clickable clickable = new Clickable((silent ? Lang.PUNISHMENT_SILENT.toString() : "") + Lang.PUNISHMENT_UNDO.getMsg(
                    target,
                    "blacklisted",
                    coloredName,
                    reason
            )/*,Lang.PUNISHMENT_UNMUTE_HOVER.getMsg(reason)*/);

            Bukkit.getConsoleSender().sendMessage(CC.translate(clickable.getText()));

            if (silent) {
                for (Player player : Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission(Permission.PUNISHMENT_SEE_SILENT.getNode())).collect(Collectors.toList())) {
                    clickable.sendToPlayer(player);
                }
            } else {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    clickable.sendToPlayer(player);
                }
            }
            return;
        }
        if (payload == JedisAction.PUNISHED_JOIN){
            String type = data.get("type").getAsString(),
            name = data.get("name").getAsString();
            Clickable clickable;
            if (data.get("more").getAsBoolean()){
                String expire = data.get("expires").getAsString(),
                addedBy = data.get("addedBy").getAsString();
                clickable = new Clickable(Lang.PUNISH_JOIN_ALERT.getMsg(name, type),Lang.PUNISH_JOIN_ALERT_HOVER.getMsg(expire,addedBy),"/history " + name);
            }else clickable = new Clickable(Lang.PUNISH_JOIN_ALERT.getMsg(name, type));
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission(Permission.PUNISHMENT_SEE_JOIN_ALERT.getNode())) {
                    clickable.sendToPlayer(player);
                }
            }
            return;
        }
        if (payload == JedisAction.EXECUTE_PUNISHMENT) {
            PunishmentRedisHandler.onExecPunishment(data);
        }


    }
}
