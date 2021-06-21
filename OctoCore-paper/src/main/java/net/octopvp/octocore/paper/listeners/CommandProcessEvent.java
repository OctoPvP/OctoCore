package net.octopvp.octocore.paper.listeners;

import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.objects.AuditLogEntry;
import net.octopvp.octocore.paper.objects.enums.AuditLogType;
import net.octopvp.octocore.paper.utils.permission.Permission;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;

public class CommandProcessEvent implements Listener {
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        if (event.getPlayer().hasPermission(Permission.LOG_WORLDEDIT.getNode()) && event.getMessage().startsWith("//")) {
            HashMap<String,String> entries = new HashMap<>();
            entries.put("Player",event.getPlayer().getName());
            entries.put("Command",event.getMessage());
            //TODO entries.put("Blocks Affected","Unknown");
            AuditLogEntry entry = new AuditLogEntry(entries,AuditLogType.WORLDEDIT_ACTION);
            OctoCore.getInstance().getJdaManager().sendAuditLogMsg(entry);
        }
    }
}
