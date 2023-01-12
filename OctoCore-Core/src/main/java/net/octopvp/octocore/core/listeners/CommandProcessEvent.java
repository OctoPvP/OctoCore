package net.octopvp.octocore.core.listeners;

import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.enums.AuditLogType;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.objects.AuditLogEntry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;

public class CommandProcessEvent implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().hasPermission(Permissions.LOG_WORLDEDIT) && event.getMessage().startsWith("//")) {
            HashMap<String, String> entries = new HashMap<>();
            entries.put("Player", event.getPlayer().getName());
            entries.put("Command", event.getMessage());
            //TODO entries.put("Blocks Affected","Unknown");
            AuditLogEntry entry = new AuditLogEntry(entries, AuditLogType.WORLDEDIT_ACTION);
            OctoCore.getInstance().getJdaManager().sendAuditLogMsg(entry); //TODO help
        }
    }
}
