package net.octopvp.octocore.paper.module.impl.punishments;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.octopvp.octocore.common.object.DisconnectReason;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.packets.staff.PunishedJoinPacket;
import net.octopvp.octocore.paper.listeners.PunishmentListener;
import net.octopvp.octocore.paper.module.Module;
import net.octopvp.octocore.paper.module.impl.punishments.managers.PunishmentsProfileManager;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishData;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishPlayerData;
import net.octopvp.octocore.paper.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class PunishModule implements Module {

    @Getter
    private static PunishModule instance;
    @Getter
    private static MongoCollection<Document>
            bans,
            mutes,
            kicks,
            warns,
            blacklists,
            punishPlayerData,
            punishHistory,
            notes;
    private PunishmentsProfileManager profileManager;

    public static void postDbInit(MongoDatabase database) {
        bans = database.getCollection("bans");
        mutes = database.getCollection("mutes");
        kicks = database.getCollection("kicks");
        warns = database.getCollection("warns");
        blacklists = database.getCollection("blacklists");
        punishPlayerData = database.getCollection("punishPlayerData");
        punishHistory = database.getCollection("punishHistory");
        notes = database.getCollection("notes");
    }

    @Override
    public void onEnable(OctoCore plugin) {
        Logger.debug("Starting Punishment Module");
        instance = this;
        this.profileManager = new PunishmentsProfileManager(plugin);
    }

    @Override
    public void onDisable(OctoCore plugin) {

    }
}
