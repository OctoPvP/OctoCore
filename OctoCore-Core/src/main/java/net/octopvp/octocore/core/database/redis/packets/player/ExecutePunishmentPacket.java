package net.octopvp.octocore.core.database.redis.packets.player;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.DisconnectReason;
import net.octopvp.octocore.common.object.punish.Alt;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.chat.Clickable;
import net.octopvp.octocore.core.utils.msg.Lang;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class ExecutePunishmentPacket extends RedisPacket {

    /*

    @Override
    public void onReceive(JsonObject data) {
        try {
            Punishment punishment = OctoCore.getGson().fromJson(data.get("punishment").getAsString(), GsonType.PUNISHMENT);
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
            PlayerData playerData = PlayerManager.getInstance().getData(uuid);

            if (playerData == null) {
                playerData = new PlayerData(uuid, name);
                playerData.loadAlts(uuid);
            }
            PunishmentType type = PunishmentType.valueOf(data.get("type").getAsString());
            Logger.debug("Type: %1\nPermanent: %2\nSilent: %3\nIPRelative: %4", type, permanent, silent, IPRelative);
            if (type == PunishmentType.BAN) {
                Tasks.run(() -> {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.kickBungee(new DisconnectReason(
                                Lang.PUNISH_KICK_MESSAGE.getMsg(
                                        (temp ? Lang.TEMP : Lang.PERM),
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
                    altKick(playerData.getAltsSafely(), name, "BAN", permanent, niceDuration, reason, sender, niceExpire);
                }
            }
            if (type == PunishmentType.BLACKLIST) {
                Tasks.run(() -> {
                    Player player = Bukkit.getPlayer(uuid);

                    if (player != null) {
                        player.kickBungee(
                                new DisconnectReason(
                                        Lang.PUNISH_KICK_MESSAGE.getMsg(
                                                (temp ? Lang.TEMP : Lang.PERM),
                                                "BLACKLISTED",
                                                "Blacklisted",
                                                addedByName,
                                                reason,
                                                (temp ? Lang.PUNISH_KICK_TEMP_ENTRY.getMsg(
                                                        niceExpire, niceDuration) : Lang.PERM_ENTRY),
                                                true)).toString()
                        );
                    }
                });
                altKick(playerData.getAltsSafely(), name, "BLACKLIST", permanent, niceDuration, reason, sender, niceExpire);
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
                playerData.getAltsSafely().forEach(alt -> {
                    Player p = Bukkit.getPlayer(uuid);
                    if (p != null) {
                        String message = Lang.MUTE_MESSAGE.getMsg(
                                (permanent ? Lang.PERM : Lang.TEMP), reason,
                                (!permanent ? Lang.TEMP_MUTE_ENTRY_MESSAGE.getMsg(niceExpire) : ""));
                        p.sendMessage(CC.translate(message));
                    }
                });
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

            if (type == PunishmentType.MUTE || type == PunishmentType.WARN) {
                if (OctoCore.getServerName().equalsIgnoreCase(server)) {
                    PlayerData pdata = PlayerManager.getInstance().getData(uuid);
                    if (pdata == null) {
                        pdata = new PlayerData(uuid, name);
                        pdata.getPunishData().load();
                    }
                    punishment.setTargetAddress(PlayerManager.getInstance().getAddress(uuid));
                }
            }

            String typeStr;
            switch (type) {
                case BAN:
                    typeStr = (temp ? Lang.TEMP : Lang.PERM) + " " + (IPRelative ? "ip-" : "") + "banned";
                    break;
                case KICK:
                    typeStr = "kicked";
                    break;
                case MUTE:
                    typeStr = (temp ? Lang.TEMP : Lang.PERM) + " " + (IPRelative ? "ip-" : "") + "muted";
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
                    ),
                    Lang.PUNISH_HOVER.getMsg(
                            niceExpire,
                            addedByName,
                            niceDuration
                    ),
                    "/history " + name
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
     */
    private String sender;
    //private JsonBuilder data;
    private String coloredName;
    private String senderName;
    private String name;
    private String reason;
    private long duration;
    private String niceDuration;
    private String niceExpire;
    private boolean permanent;
    private UUID uuid;
    private boolean silent;
    private String addedByName;
    private String server;
    private PunishmentType type;
    private boolean ipRelative;
    private String punishment;

    public static void altKick(List<Alt> alts, String name, String type, boolean permanent, String niceDuration, String reason, String sender, String expire) {
        alts.forEach(alt -> {
            /*
            new ExecuteAltKickPacket(new JsonBuilder()
                    .addProperty("alt", name)
                    .addProperty("type", type)
                    .addProperty("permanent", permanent)
                    .addProperty("duration", niceDuration)
                    .addProperty("expire", expire)
                    .addProperty("reason", reason)
                    .addProperty("sender", sender)
                    .addProperty("name", alt.getName())).send();
             */
            new ExecuteAltKickPacket(
                    alt.getName(), sender, reason, niceDuration, type, name, expire, permanent
            ).send();
        });
    }

    @Override
    public void onReceive(JsonObject data) {
        try {
            Punishment punishment = OctoCore.getGson().fromJson(this.punishment, Punishment.class);
            boolean temp = !permanent;
            PlayerData playerData = PlayerManager.getInstance().getData(uuid);

            if (playerData == null) {
                playerData = new PlayerData(uuid, name);
                playerData.loadAlts(uuid);
            } else {
                playerData.getPunishData().getPunishments().add(punishment);
            }
            Logger.debug("Type: %1\nPermanent: %2\nSilent: %3\nIPRelative: %4", type, permanent, silent, ipRelative);
            if (type == PunishmentType.BAN) {
                Tasks.run(() -> {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.kickBungee(new DisconnectReason(
                                Lang.PUNISH_KICK_MESSAGE.getMsg(
                                        (temp ? Lang.TEMP : Lang.PERM),
                                        "BANNED",
                                        "Banned",
                                        addedByName,
                                        reason,
                                        (temp ? Lang.PUNISH_KICK_TEMP_ENTRY.getMsg(
                                                niceExpire, niceDuration) : Lang.PERM_ENTRY),
                                        true)).toString());
                    }
                });
                if (ipRelative) {
                    altKick(playerData.getAltsSafely(), name, "BAN", permanent, niceDuration, reason, sender, niceExpire);
                }
            }
            if (type == PunishmentType.BLACKLIST) {
                Tasks.run(() -> {
                    Player player = Bukkit.getPlayer(uuid);

                    if (player != null) {
                        player.kickBungee(
                                new DisconnectReason(
                                        Lang.PUNISH_KICK_MESSAGE.getMsg(
                                                (temp ? Lang.TEMP : Lang.PERM),
                                                "BLACKLISTED",
                                                "Blacklisted",
                                                addedByName,
                                                reason,
                                                (temp ? Lang.PUNISH_KICK_TEMP_ENTRY.getMsg(
                                                        niceExpire, niceDuration) : Lang.PERM_ENTRY),
                                                true)).toString()
                        );
                    }
                });
                altKick(playerData.getAltsSafely(), name, "BLACKLIST", permanent, niceDuration, reason, sender, niceExpire);
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
                Consumer<Player> pConsumer = (p) -> {
                    if (p != null) {
                        String message = Lang.MUTE_MESSAGE.getMsg(
                                (permanent ? Lang.PERM : Lang.TEMP), reason,
                                (!permanent ? Lang.TEMP_MUTE_ENTRY_MESSAGE.getMsg(niceExpire) : ""));
                        p.sendMessage(CC.translate(message));
                    }
                };
                pConsumer.accept(Bukkit.getPlayer(uuid));
                playerData.getAltsSafely().forEach(alt -> {
                    Logger.debug(" - Sending to alt: " + alt.getName());
                    if (alt.getUuid().equals(uuid)) return;
                    pConsumer.accept(Bukkit.getPlayer(uuid));
                });
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

            if (type == PunishmentType.MUTE || type == PunishmentType.WARN) { // player stays online
                if (OctoCore.getServerName().equalsIgnoreCase(server)) {
                    PlayerData pdata = PlayerManager.getInstance().getData(uuid);
                    if (pdata == null) {
                        pdata = new PlayerData(uuid, name);
                        pdata.getPunishData().load();
                    }
                    punishment.setTargetAddress(PlayerManager.getInstance().getAddress(uuid));
                }
            }

            String typeStr;
            switch (type) {
                case BAN:
                    typeStr = (temp ? Lang.TEMP : Lang.PERM) + " " + (ipRelative ? "ip-" : "") + "banned";
                    break;
                case KICK:
                    typeStr = "kicked";
                    break;
                case MUTE:
                    typeStr = (temp ? Lang.TEMP : Lang.PERM) + " " + (ipRelative ? "ip-" : "") + "muted";
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
                    ),
                    Lang.PUNISH_HOVER.getMsg(
                            niceExpire,
                            addedByName,
                            niceDuration
                    ),
                    "/history " + name
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
}
