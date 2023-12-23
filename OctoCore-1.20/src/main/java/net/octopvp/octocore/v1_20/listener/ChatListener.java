package net.octopvp.octocore.v1_20.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.manager.impl.ChatManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;


public class ChatListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChat(AsyncChatEvent e) { // the cool new listener
        Component component = ChatManager.formatChatComponent(e.getPlayer(),
                e.message(),
                e.getPlayer().hasPermission(Permissions.USE_COLOR_CHAT)
        );
        e.message(component == null ? Component.empty() : component);
    }
}
