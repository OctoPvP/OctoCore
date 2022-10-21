package net.octopvp.octocore.paper.database.redis.packets.staff;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
public class DiscordAdminChatPacket extends RedisPacket {
    private String name, message, role, tag;

    @Override
    public void onReceive(JsonObject data) {
        String msg = Lang.DISCORD_ADMIN_CHAT_FORMAT.getMsg(name, message);
        TextComponent mainComponent = new TextComponent(msg);
        mainComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Rank: " + role + "\nUser: " + tag).create()));
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(Permissions.ADMINCHAT))
                player.sendMessage(mainComponent);
        }
    }
}
