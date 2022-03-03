package net.octopvp.octocore.paper.database.redis.packets.player;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.DisconnectReason;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;
import net.octopvp.octocore.paper.module.impl.punishments.util.PunishmentType;
import net.octopvp.octocore.paper.utils.chat.Clickable;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class ExecutePunishmentPacket extends RedisPacket {
    private JsonBuilder data;

    @Override
    public void onReceive(JsonObject data) throws Exception {
        try {
            UUID uuid = UUID.fromString(data.get("uuid").getAsString());
            String name = data.get("name").getAsString();
            String sender = data.get("sender").getAsString();
            String addedByName = data.get("addedByName").getAsString();
            String reason = data.get("reason").getAsString();
            String niceDuration = data.get("niceDuration").getAsString(), niceExpire = data.get("niceExpire").getAsString();
            String server = data.get("server").getAsString();
            boolean silent = data.get("silent").getAsBoolean();
            boolean permanent = data.get("permanent").getAsBoolean();
            boolean IPRelative = data.get("IPRelative").getAsBoolean();
            boolean temp = !permanent;
            String alts = data.get("alts").getAsString();

            PunishmentType type = PunishmentType.valueOf(data.get("type").getAsString());
            int warns = data.get("warns").getAsInt();
            Logger.debug("Type: %1\nPermanent: %2\nSilent: %3\nIPRelative: %4\nWarns: %5", type, permanent, silent, IPRelative, warns);
            if (type == PunishmentType.BAN) {
                Tasks.run(() -> {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.kickBungee(new DisconnectReason(
                                Lang.PUNISH_KICK_MESSAGE.getMsg(
                                        (temp ? Lang.TEMP : ""),
                                        "BANNED",
                                        "Banned",
                                        addedByName,
                                        reason,
                                        (temp ? Lang.PUNISH_KICK_TEMP_ENTRY.getMsg(
                                                niceExpire, niceDuration) : Lang.PERM_ENTRY),
                                        true)).toString());
                    }
                });
                if (IPRelative) {
                /*
                StringUtils.getListFromString(alts).forEach(alt -> {
                    OctoCore.getInstance().getRedisData().write(JedisAction.EXECUTE_ALT_KICK,
                            new JsonChain()
                                    .addProperty("alt", name)
                                    .addProperty("type", "BAN")
                                    .addProperty("permanent", permanent)
                                    .addProperty("duration", niceDuration)
                                    .addProperty("reason", reason)
                                    .addProperty("sender", sender)
                                    .addProperty("name", alt).get());
                });
                 */
                    altKick(alts, name, "BAN", permanent, niceDuration, reason, sender, niceExpire);
                }
                PunishModule.getInstance().getProfileManager().unloadData(uuid);
            }
            if (type == PunishmentType.BLACKLIST) {
                Tasks.run(() -> {
                    Player player = Bukkit.getPlayer(uuid);

                    if (player != null) {
                        player.kickBungee(
                                new DisconnectReason(
                                        Lang.PUNISH_KICK_MESSAGE.getMsg(
                                                (temp ? Lang.TEMP : ""),
                                                "BLACKLISTED",
                                                "Blacklisted",
                                                addedByName,
                                                reason,
                                                (temp ? Lang.PUNISH_KICK_TEMP_ENTRY.getMsg(
                                                        niceExpire, niceDuration) : Lang.PERM_ENTRY),
                                                true)).toString()
                        );
                       /*
                       player.kickBungee(new DisconnectReason(
                               Lang.PUNISH_KICK_MESSAGE.getMsg(
                                       (temp ? Lang.TEMP : ""),
                                       "BLACKLISTED",
                                       reason,
                                       (temp ? Lang.PUNISH_KICK_TEMP_ENTRY.getMsg(
                                               niceExpire) : ""))).toString());
                        */
                    }
                });
            /*
            StringUtils.getListFromString(alts).forEach(alt -> {
                OctoCore.getInstance().getRedisData().write(JedisAction.EXECUTE_ALT_KICK,
                        new JsonChain()
                                .addProperty("alt", name)
                                .addProperty("type", "BLACKLIST")
                                .addProperty("permanent", permanent)
                                .addProperty("duration", niceDuration)
                                .addProperty("reason", reason)
                                .addProperty("sender", sender)
                                .addProperty("name", alt).get());
            });
             */
                altKick(alts, name, "BLACKLIST", permanent, niceDuration, reason, sender, niceExpire);
                PunishModule.getInstance().getProfileManager().unloadData(uuid);
            }
            if (type == PunishmentType.KICK) {
                Tasks.run(() -> {
                    Player player = Bukkit.getPlayer(uuid);

                    if (player != null) {
                        player.kickBungee(CC.translate(reason));
                    }
                });
            }
            if (type == PunishmentType.MUTE) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    String message = Lang.MUTE_MESSAGE.getMsg(
                            (permanent ? Lang.PERM : Lang.TEMP), reason,
                            (!permanent ? Lang.TEMP_MUTE_ENTRY_MESSAGE.getMsg(niceExpire) : ""));
                    player.sendMessage(CC.translate(message));
                }
            }
            if (type == PunishmentType.WARN) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    String message = Lang.WARN_MESSAGE.getMsg(
                            (permanent ? Lang.PERM : Lang.TEMP), reason,
                            (!permanent ? Lang.TEMP_WARN_ENTRY_MESSAGE.getMsg(niceExpire) : ""));
                    player.sendMessage(CC.translate(message));
                }
            }
        /*
        if (type == PunishmentType.BAN) {
            Tasks.run(()->{
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.kickBungee(
                            new DisconnectReason(
                                    Lang.PUNISH_KICK_MESSAGE.getMsg(
                                            (temp ? Lang.TEMP : ""),
                                            "BANNED",
                                            "Banned",
                                            addedByName,
                                            reason,
                                            (temp ? Lang.PUNISH_KICK_TEMP_ENTRY.getMsg(
                                                    niceExpire,niceDuration) : Lang.PERM_ENTRY),
                                            true)).toString()
                    );
                }
            });
            if (IPRelative){
                /*
                StringUtils.getListFromString(alts).forEach(alt -> {
                    OctoCore.getInstance().getRedisData().write(JedisAction.EXECUTE_ALT_KICK,
                            new JsonChain()
                                    .addProperty("alt", name)
                                    .addProperty("type", "BAN")
                                    .addProperty("permanent", permanent)
                                    .addProperty("duration", niceDuration)
                                    .addProperty("reason", reason)
                                    .addProperty("sender", sender)
                                    .addProperty("name", alt).get());
                });
                altKick(alts,name,"BAN",permanent,niceDuration,reason,sender);
            }
        }
        if (type == PunishmentType.BLACKLIST){
            Tasks.run(()->{
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.kickBungee(
                            new DisconnectReason(
                                    Lang.PUNISH_KICK_MESSAGE.getMsg(
                                            (temp ? Lang.TEMP : ""),
                                            "BLACKLISTED",
                                            "Blacklisted",
                                            addedByName,
                                            reason,
                                            (temp ? Lang.PUNISH_KICK_TEMP_ENTRY.getMsg(
                                                    niceExpire,niceDuration) : Lang.PERM_ENTRY),
                                            true)).toString()
                    );
                }
            });
            altKick(alts,name,"BLACKLIST",permanent,niceDuration,reason,sender);
        }
                         */
            String typeStr;
            switch (type) {
                case BAN:
                    typeStr = (IPRelative ? "ip-" : "") + "banned";
                    break;
                case KICK:
                    typeStr = "kicked";
                    break;
                case MUTE:
                    typeStr = (IPRelative ? "ip-" : "") + "muted";
                    break;
                case WARN:
                    typeStr = "warned";
                    break;
                case BLACKLIST:
                    typeStr = "blacklisted";
                    break;
                default:
                    typeStr = "punished";
                    break;
            }
            Clickable clickable = new Clickable(
                    (silent ? Lang.PUNISHMENT_SILENT.getMsg() : "") + Lang.PUNISHMENT_DO.getMsg(
                            name,
                            typeStr,
                            sender,
                            reason
                    )
            );

            Bukkit.getConsoleSender().sendMessage(CC.translate(clickable.getText()));

            if (silent) {
                for (Player player : Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission(Lang.PUNISHMENT_SILENT.getMsg())).collect(Collectors.toList())) {
                    clickable.sendToPlayer(player);
                }
            } else {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    clickable.sendToPlayer(player);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public JsonBuilder getData() {
        return data;
    }

    @Override
    public String getName() {
        return "ExecutePunishmentPacket";
    }

    public static void altKick(String alts, String name, String type, boolean permanent, String niceDuration, String reason, String sender, String expire) {
        StringUtils.getListFromString(alts).forEach(alt -> {
            new ExecuteAltKickPacket(new JsonBuilder()
                    .addProperty("alt", name)
                    .addProperty("type", type)
                    .addProperty("permanent", permanent)
                    .addProperty("duration", niceDuration)
                    .addProperty("expire", expire)
                    .addProperty("reason", reason)
                    .addProperty("sender", sender)
                    .addProperty("name", alt)).send();
        });
    }
}
