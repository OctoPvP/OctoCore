package net.octopvp.octocore.paper.database.redis.packets.staff;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
public class DiscordStaffChatPacket extends RedisPacket {
    private JsonObject jo;

    @Override
    public void onReceive(JsonObject data) throws Exception {
        String name = data.get("name").getAsString();
        String message = data.get("message").getAsString();
        String msg = Lang.DISCORD_STAFF_CHAT_FORMAT.getMsg(name, message);
        String role = data.get("role").getAsString();
        String tag = data.get("tag").getAsString();
        TextComponent mainComponent = new TextComponent(msg);
        mainComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Rank: " + role + "\nUser: " + tag).create()));
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(Permissions.STAFFCHAT.getNode()))
                player.sendMessage(mainComponent);
        }
    }

    @Override
    public JsonBuilder getData() {
        return new JsonBuilder(jo);
    }

    @Override
    public String getName() {
        return "DiscordStaffChatPacket";
    }
}
