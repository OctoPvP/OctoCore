package net.octopvp.octocore.core.listeners;

import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.ChatColor;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.database.redis.packets.staff.chat.AdminChatPacket;
import net.octopvp.octocore.core.database.redis.packets.staff.chat.StaffChatPacket;
import net.octopvp.octocore.core.manager.impl.ChatManager;
import net.octopvp.octocore.core.manager.impl.FilterManager;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.manager.impl.VanishManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Iterator;

public class ChatListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void messageListener(AsyncPlayerChatEvent e) {
        // https://discord.com/channels/289587909051416579/555462289851940864/1188010602770210887
        PlayerData playerData = PlayerManager.getInstance().getData(e.getPlayer().getUniqueId());

        if (playerData.isStaffChat()) {
            if (playerData.isAdminChat()) {
                playerData.setAdminChat(false);
                e.getPlayer().sendMessage(Lang.ADMIN_CHAT_DISABLED.toString());
            }
            if (e.getPlayer().hasPermission(Permissions.STAFFCHAT)) {
                new StaffChatPacket(e.getPlayer().getName(), playerData.getFormattedName(false, e.getPlayer(), false), OctoCore.getServerName(), e.getMessage(), e.getPlayer().getUniqueId()).send();
                e.setCancelled(true);
            } else {
                e.getPlayer().sendMessage(Lang.STAFF_CHAT_DISABLED.getMsg());
                playerData.setStaffChat(false);
            }
            return;
        } else if (playerData.isAdminChat()) {
            if (e.getPlayer().hasPermission(Permissions.ADMINCHAT)) {
                new AdminChatPacket(e.getPlayer().getName(), playerData.getFormattedName(false, e.getPlayer(), false), OctoCore.getServerName(), e.getMessage(), e.getPlayer().getUniqueId()).send();
                e.setCancelled(true);
            } else {
                e.getPlayer().sendMessage(Lang.ADMIN_CHAT_DISABLED.getMsg());
                playerData.setAdminChat(false);
            }
            return;
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
            return;
        }

        if (FilterManager.containsUnicode(e.getMessage()) && !e.getPlayer().hasPermission(Permissions.USE_UNICODE_CHAT)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Lang.NOT_ALLOWED_TO_USE_UNICODE.getMsg());
            return;
        }
        if (VanishManager.getInstance().isVanished(e.getPlayer())) {
            if (e.getMessage().endsWith("\\")) {
                //remove the \
                e.setMessage(e.getMessage().substring(0, e.getMessage().length() - 1));
                return;
            }
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "Your chat message has been blocked because you are vanished. add a '\\' at the end of your message to bypass this.");
            return;
        }
        if (Boolean.getBoolean("octocore.newChatFormatting")) return;
        //String format = ChatManager.formatChat(e.getPlayer().getUniqueId(),e.getPlayer().getDisplayName(),FilterManager.process(e.getMessage(),e.getPlayer()),e.getPlayer().hasPermission(Permission.USE_COLOR_CHAT));
        String format = ChatManager.formatChat(e.getPlayer(), FilterManager.process(e.getMessage(), e.getPlayer()), e.getPlayer().hasPermission(Permissions.USE_COLOR_CHAT));
        if (format == null) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Lang.PDATA_DID_NOT_LOAD.getMsg());
            return;
        }
        playerData.setLastMessage(e.getMessage());
        e.setFormat(format);

        Iterator<Player> iterator = e.getRecipients().iterator();
        while (iterator.hasNext()) { // Concurrency shit
            Player player = iterator.next();
            PlayerData data = PlayerManager.getInstance().getData(player.getUniqueId());
            if (!data.getMessageSettings().isGlobalChat())
                iterator.remove();
        }
    }
}
