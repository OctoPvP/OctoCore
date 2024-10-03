package net.octopvp.octocore.core.command.impl.punishments;

import com.mongodb.client.model.Filters;
import net.octopvp.commander.annotation.*;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.module.impl.punishments.PunishModule;
import net.octopvp.octocore.core.utils.OfflineHelpers;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StaffRollBackCommand {
    @Completer(name = "staffrollback", index = 2)
    public List<String> typeCompleter() {
        return Arrays.asList("bans", "mutes", "blacklists", "warns");
    }

    @Async
    @Command(name = "staffrollback")
    @Permission(Permissions.PUNISHMENT_STAFFROLLBACK)
    public void execute(CommandSender sender, @Name("player") String targetStr, @Duration long time, @Required PunishmentType type) {
        OfflineHelpers.OfflineInfo target = OfflineHelpers.getOfflineInfo(targetStr);
        if (!PlayerManager.getInstance().doesDocumentExistByUUID(target.getUniqueId()) && !targetStr.equalsIgnoreCase("console")) {
            sender.sendMessage(Lang.COULD_NOT_FIND_DATA.toString());
            return;
        }
        long check = System.currentTimeMillis() - time;
        sender.sendMessage(Lang.STAFF_ROLLBACK_WIPING.getMsg(target.getDisplayName(), StringUtils.capitalizeFirst(type.toString())));
        AtomicInteger removed = new AtomicInteger(0);
        PunishModule.getPunishments().find(
                Filters.and(
                        Filters.eq("type", type.toString()),
                        Filters.eq("addedBy", target.getUuid()),
                        Filters.gte("addedAt", check)
                )
        ).into(new ArrayList<>()).forEach(document -> {
            PunishModule.getPunishments().deleteOne(document);
            removed.incrementAndGet();
        });
        if (removed.get() == 0) {
            sender.sendMessage(Lang.STAFF_ROLLBACK_DONT_HAVE_HISTORY.getMsg(target.getDisplayName(), "bans"));
            return;
        }
        sender.sendMessage(Lang.STAFF_ROLLBACK_WIPED.getMsg(removed.get()));
    }
}
