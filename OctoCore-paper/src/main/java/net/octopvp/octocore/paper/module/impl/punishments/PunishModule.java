package net.octopvp.octocore.paper.module.impl.punishments;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.octopvp.octocore.common.object.DisconnectReason;
import net.octopvp.octocore.common.object.punish.Alt;
import net.octopvp.octocore.common.object.punish.IPunishment;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.packets.staff.PunishedJoinPacket;
import net.octopvp.octocore.paper.module.Module;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bson.Document;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

@Getter
public class PunishModule implements Module {

    @Getter
    private static PunishModule instance;
    @Getter
    private static MongoCollection<Document>
            punishments,
            punishHistory,
            notes;

    public static void postDbInit(MongoDatabase database) {
        punishHistory = database.getCollection("punishHistory");
        notes = database.getCollection("notes");
        punishments = database.getCollection("punishments");
    }

    public static boolean checkPunishments(AsyncPlayerPreLoginEvent event, PlayerData data, String name, UUID uuid) {
        if (data.getPunishData().isBlacklisted()) {
            IPunishment activeBlacklist = data.getPunishData().getActiveBlacklist();
            disallowBlacklist(event, activeBlacklist);
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
            return true;
        }
        if (data.getPunishData().isBanned()) {
            IPunishment activeBan = data.getPunishData().getActiveBan();
            boolean temp = activeBan.isTemporary();
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    new DisconnectReason(
                            Lang.PUNISH_KICK_MESSAGE.getMsg(
                                    (temp ? Lang.TEMP : Lang.PERM),
                                    "BANNED",
                                    "Banned",
                                    activeBan.getAddedByName(),
                                    activeBan.getReason(),
                                    (temp ? Lang.PUNISH_KICK_TEMP_ENTRY.getMsg(
                                            activeBan.getNiceExpire(), activeBan.getNiceDuration()) : Lang.PERM_ENTRY),
                                    true)).toString()
            );
            new PunishedJoinPacket(
                    new JsonBuilder().addProperty("name", name).addProperty("type", "banned").addProperty("more", true).addProperty("expires", activeBan.getNiceExpire()).addProperty("addedByName", activeBan.getAddedByName())
            ).send();
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
            return true;
        }

        for (Alt alt : data.getAlts()) {
            if (alt.isBlacklisted()) {
                IPunishment activeBlacklist = alt.getPunishData().getActiveBlacklist();
                disallowBlacklist(event, activeBlacklist);
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
                return true;
            } else if (alt.isIPBanned()) {
                IPunishment activeBan = alt.getPunishData().getActiveBan();
                boolean temp = activeBan.isTemporary();
                event.disallow(
                        AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                        new DisconnectReason(
                                Lang.PUNISH_KICK_MESSAGE.getMsg(
                                        (temp ? Lang.TEMP : Lang.PERM),
                                        "BANNED",
                                        "Banned",
                                        activeBan.getAddedByName(),
                                        activeBan.getReason(),
                                        (temp ? Lang.PUNISH_KICK_TEMP_ENTRY.getMsg(
                                                activeBan.getNiceExpire(), activeBan.getNiceDuration()) : Lang.PERM_ENTRY),
                                        true)).toString()
                );
                new PunishedJoinPacket(new JsonBuilder().addProperty("name", name).addProperty("type", "IP-Banned").addProperty("more", true).addProperty("expires", activeBan.getNiceExpire()).addProperty("addedByName", activeBan.getAddedByName())).send();
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
                return true;
            }
        }

        if (OctoCore.getInstance().getConfig().getBoolean("punish.alts.allow-alts", true)) {
            int max = OctoCore.getInstance().getConfig().getInt("punish.alts.max", 5);
            if (max != -1) {
                if (data.getAltsSafely().size() > max) {
                    event.disallow(
                            AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                            new DisconnectReason(
                                    Lang.TOO_MANY_ALTS_KICK_MESSAGE.getMsg(
                                            max,
                                            data.getAltsSafely().size()
                                    )
                            ).toString()
                    );
                    event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                    return true;
                }
            }
        }
        return false;
    }

    private static void disallowBlacklist(AsyncPlayerPreLoginEvent event, IPunishment punishment) {
        boolean temp = punishment.isTemporary();
        event.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                new DisconnectReason(
                        Lang.PUNISH_KICK_MESSAGE.getMsg(
                                (temp ? Lang.TEMP : Lang.PERM),
                                "BLACKLISTED",
                                "Blacklisted",
                                punishment.getAddedByName(),
                                punishment.getReason(),
                                (temp ? Lang.PUNISH_KICK_TEMP_ENTRY.getMsg(
                                        punishment.getNiceExpire(), punishment.getNiceDuration()) : Lang.PERM_ENTRY),
                                true)).toString()
        );
        new PunishedJoinPacket(new JsonBuilder().addProperty("name", event.getName()).addProperty("type", "blacklisted")).send();
    }

    @Override
    public void onEnable(OctoCore plugin) {
        Logger.debug("Starting Punishment Module");
        instance = this;
    }

    @Override
    public void onDisable(OctoCore plugin) {

    }
}
