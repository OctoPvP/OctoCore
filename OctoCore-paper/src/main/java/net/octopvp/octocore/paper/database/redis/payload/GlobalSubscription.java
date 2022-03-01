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
        if (payload == JedisAction.EXECUTE_PUNISHMENT) {
            PunishmentRedisHandler.onExecPunishment(data);
            return;
        }
    }
}
