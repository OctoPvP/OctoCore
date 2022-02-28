package net.octopvp.octocore.paper.database.redis.payload;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.common.object.redis.JedisHandle;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.listeners.redis.MainRedisHandler;
import net.octopvp.octocore.paper.listeners.redis.PunishmentRedisHandler;
import net.octopvp.octocore.paper.manager.impl.JDAManager;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.RedisListenerManager;
import net.octopvp.octocore.paper.manager.impl.TagManager;
import net.octopvp.octocore.paper.objects.Broadcast;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.PlayerTag;
import net.octopvp.octocore.paper.objects.enums.AuditLogType;
import net.octopvp.octocore.paper.objects.enums.DataUpdateReason;
import net.octopvp.octocore.paper.objects.permissions.Grant;
import net.octopvp.octocore.paper.utils.chat.Clickable;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.stream.Collectors;

public class GlobalSubscription implements JedisHandle {
    //TODO bungeecord fallback

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
        if (data.isJsonNull()) {
            Logger.error("Received null data from redis");
            return;
        }
        RedisListenerManager.handleMessage(payload, data);
        if (payload == JedisAction.ADMIN_ALERT) {
            String message = data.get("message").getAsString();
            String msg = Lang.ADMIN_ALERTS.getMsg(message);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer.hasPermission(Permission.ADMIN_ALERT.getNode())) {
                    onlinePlayer.sendMessage(msg);
                }
            }
            return;
        }
        if (payload == JedisAction.AUDIT_LOG) {
            AuditLogType logType = AuditLogType.valueOf(data.get("type").getAsString());
            if (logType != AuditLogType.WORLDEDIT_ACTION && logType != AuditLogType.AUTH_FAIL)
                return;
            if (logType == AuditLogType.WORLDEDIT_ACTION) {
                String player = data.get("player").getAsString();
                String command = data.get("command").getAsString();

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission(Permission.RECEIVE_AUDIT_WORLDEDIT.getNode())) {
                        //TODO finish audit log
                    }
                }
                return;
            }
            return;
        }
        if (payload == JedisAction.GLOBAL_BROADCAST) {
            String message = CC.translate(data.get("message").getAsString());
            int i = Bukkit.broadcastMessage(message);
            if (data.has("player")) {
                String origin = data.get("origin").getAsString();
                JsonObject response = new JsonObject();
                response.addProperty("type", "GlobalBroadcastResponse");
                response.addProperty("value", i);
                response.addProperty("id", data.get("id").getAsString());
                response.addProperty("target", origin);
                response.addProperty("from", OctoCore.getServerName());
                OctoCore.getInstance().getRedisData().write(JedisAction.RESPONSE, response);
            }
            return;
        }
        if (payload == JedisAction.RESPONSE) {
            String responseType = data.get("type").getAsString();
            switch (responseType) {
                case "GlobalBroadcastResponse":
                    if (data.get("target").getAsString().equalsIgnoreCase(OctoCore.getServerName())) {
                        int value = data.get("value").getAsInt();
                        UUID id = UUID.fromString(data.get("id").getAsString());
                        Broadcast broadcast = Broadcast.getBroadcast(id);
                        broadcast.getResponses().put(data.get("from").getAsString(), value);
                    }
                    break;
            }
            return;
        }
        if (payload == JedisAction.RELOAD_TAGS) {
            TagManager.reloadTags();
            return;
        }
        if (payload == JedisAction.PDATA_UPDATE) {
            DataUpdateReason reason = DataUpdateReason.valueOf(data.get("reason").getAsString());
            switch (reason) {
                case TAGS_UPDATE_GIVE:
                    String target = data.get("target").getAsString();
                    String toAdd = data.get("add").getAsString();
                    if (Bukkit.getPlayer(target) != null) {
                        PlayerData pdata = PlayerManager.getProfile(Bukkit.getPlayer(target).getUniqueId());
                        pdata.addTag(TagManager.getTagByName(toAdd)); //maybe get by id
                    }
                    break;
                case TAGS_UPDATE_REMOVE:
                    String targetWho = data.get("target").getAsString();
                    String toRemove = data.get("remove").getAsString();
                    if (Bukkit.getPlayer(targetWho) != null) {
                        PlayerData playerData = PlayerManager.getProfile(Bukkit.getPlayer(targetWho).getUniqueId());
                        playerData.removeTag(TagManager.getTagByName(toRemove).getId());
                    }
                    break;
            }
            return;
        }
        if (payload == JedisAction.SAVE_REQUEST_SWITCH) {
            String id = data.get("uuid").getAsString();
            UUID uuid = UUID.fromString(id);
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                MainRedisHandler.getSaving().add(player.getUniqueId());
                //JoinLeaveListener.freezePlayer(player);
                Tasks.runAsyncLater(() -> {
                    if (Bukkit.getPlayer(uuid) != null) {
                        //JoinLeaveListener.unfreezePlayer(player);
                        player.sendMessage(CC.RED + "Could not send you to that server!");
                    }
                }, 100);
                PlayerManager.processLeave(player);
            }
            return;
        }
        if (payload == JedisAction.SAVE_REQUEST_MISC) {
            if (data.has("uuid")) {
                String id = data.get("uuid").getAsString();

                UUID uuid = UUID.fromString(id);
                if (Bukkit.getPlayer(uuid) != null) {
                    PlayerManager.getData(uuid).save();
                }
            } else {
                String name = data.get("name").getAsString();
                Player player = Bukkit.getPlayer(name);
                if (player != null) {
                    PlayerManager.getData(player).save();
                }
            }
            return;
        }
        if (payload == JedisAction.EXECUTE_UNBAN) {
            String sender = data.has("senderDisplay") ? data.get("senderDisplay").getAsString() : data.get("sender").getAsString();
            String target = data.get("target").getAsString();
            String reason = data.get("reason").getAsString();
            boolean silent = data.get("silent").getAsBoolean(), coloredNameEnabled = data.has("coloredName");
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
        if (payload == JedisAction.EXECUTE_UNMUTE) {
            String sender = data.has("senderDisplay") ? data.get("senderDisplay").getAsString() : data.get("sender").getAsString();
            String target = data.get("target").getAsString();
            String reason = data.get("reason").getAsString();
            boolean silent = data.get("silent").getAsBoolean(), coloredNameEnabled = data.has("coloredName");
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
        if (payload == JedisAction.EXECUTE_UNBLACKLIST) {
            String sender = data.get("sender").getAsString();
            String target = data.get("target").getAsString();
            String reason = data.get("reason").getAsString();
            boolean silent = data.get("silent").getAsBoolean(), coloredNameEnabled = data.has("coloredName");
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
        if (payload == JedisAction.PUNISHED_JOIN) {
            String type = data.get("type").getAsString(),
                    name = data.get("name").getAsString();
            Clickable clickable;
            if (data.get("more").getAsBoolean()) {
                String expire = data.get("expires").getAsString(),
                        addedBy = data.get("addedBy").getAsString();
                clickable = new Clickable(Lang.PUNISH_JOIN_ALERT.getMsg(name, type), Lang.PUNISH_JOIN_ALERT_HOVER.getMsg(expire, addedBy), "/history " + name);
            } else clickable = new Clickable(Lang.PUNISH_JOIN_ALERT.getMsg(name, type));
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission(Permission.PUNISHMENT_SEE_JOIN_ALERT.getNode())) {
                    clickable.sendToPlayer(player);
                }
            }
            return;
        }
        if (payload == JedisAction.EXECUTE_PUNISHMENT) {
            PunishmentRedisHandler.onExecPunishment(data);
            return;
        }
        if (payload == JedisAction.TAG_UPDATE) {
            System.out.println("Tag update: " + OctoCore.getGson().toJson(data));
            String type = data.get("type").getAsString();
            Player player = Bukkit.getPlayer(UUID.fromString(data.get("uuid").getAsString()));
            UUID tagId = UUID.fromString(data.get("tagId").getAsString());
            if (player == null || tagId == null) return;
            PlayerTag tag = TagManager.getTag(tagId);
            PlayerData playerData = PlayerManager.getData(player);
            switch (type) {
                case "GIVE_TAG": {
                    playerData.addTag(tag);
                    return;
                }
                case "REMOVE_TAG": {
                    playerData.removeTag(tagId);
                    return;
                }
            }
            return;
        }
    }
}
