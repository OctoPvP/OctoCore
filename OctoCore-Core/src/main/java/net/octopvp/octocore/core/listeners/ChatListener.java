package net.octopvp.octocore.core.listeners;

import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.database.redis.packets.staff.AdminChatPacket;
import net.octopvp.octocore.core.database.redis.packets.staff.StaffChatPacket;
import net.octopvp.octocore.core.manager.impl.ChatManager;
import net.octopvp.octocore.core.manager.impl.FilterManager;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Iterator;

public class ChatListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void messageListener(AsyncPlayerChatEvent e) {
        PlayerData playerData = PlayerManager.getInstance().getData(e.getPlayer().getUniqueId());

        if (playerData.isStaffChat()) {
            if (playerData.isAdminChat()) {
                playerData.setAdminChat(false);
                e.getPlayer().sendMessage(Lang.ADMIN_CHAT_DISABLED.toString());
            }
            if (e.getPlayer().hasPermission(Permissions.STAFFCHAT)) {
                new StaffChatPacket(e.getPlayer().getName(), OctoCore.getServerName(), e.getMessage(), e.getPlayer().getUniqueId());
                e.setCancelled(true);
                return;
            } else {
                e.getPlayer().sendMessage(Lang.STAFF_CHAT_DISABLED.getMsg());
                playerData.setStaffChat(false);
                return;
            }
        } else if (playerData.isAdminChat()) {
            if (playerData.isStaffChat()) {
                playerData.setStaffChat(false);
                e.getPlayer().sendMessage(Lang.STAFF_CHAT_DISABLED.toString());
            }
            if (e.getPlayer().hasPermission(Permissions.ADMINCHAT)) {
                new AdminChatPacket(e.getPlayer().getName(), OctoCore.getServerName(), e.getMessage(), e.getPlayer().getUniqueId());
                e.setCancelled(true);
                return;
            } else {
                e.getPlayer().sendMessage(Lang.ADMIN_CHAT_DISABLED.getMsg());
                playerData.setAdminChat(false);
                return;
            }
        }

        //spam prot
        /*
        if (playerData.getLastMessage() == e.getMessage()) {
            if (!e.getPlayer().hasPermission(Permission.BYPASS_SPAM_PROT)){
                Chat.sendMessage(e.getPlayer(), Lang.PLEASE_DONT_SPAM);
                e.setCancelled(true);
            }
            return;
        }
         */

        if (!playerData.getMessageSettings().isGlobalChat()) {
            e.getPlayer().sendMessage(Lang.GLOBAL_CHAT_IS_DISABLED.getMsg());
        }

        if (FilterManager.containsUnicode(e.getMessage()) && !e.getPlayer().hasPermission(Permissions.USE_UNICODE_CHAT)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Lang.NOT_ALLOWED_TO_USE_UNICODE.getMsg());
            return;
        }
        playerData.setLastMessage(e.getMessage());
        //String format = ChatManager.formatChat(e.getPlayer().getUniqueId(),e.getPlayer().getDisplayName(),FilterManager.process(e.getMessage(),e.getPlayer()),e.getPlayer().hasPermission(Permission.USE_COLOR_CHAT));
        String format = ChatManager.formatChat(e.getPlayer(), FilterManager.process(e.getMessage(), e.getPlayer()), e.getPlayer().hasPermission(Permissions.USE_COLOR_CHAT));
        if (format == null) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Lang.PDATA_DID_NOT_LOAD.getMsg());
        }
        e.setFormat(format);

        Iterator<Player> iterator = e.getRecipients().iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            PlayerData data = PlayerManager.getInstance().getData(player.getUniqueId());
            if (!data.getMessageSettings().isGlobalChat())
                iterator.remove();
        }
    }
}
