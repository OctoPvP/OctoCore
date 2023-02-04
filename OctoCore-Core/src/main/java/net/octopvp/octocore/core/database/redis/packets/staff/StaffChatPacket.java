package net.octopvp.octocore.core.database.redis.packets.staff;

import com.google.gson.JsonObject;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.impl.JDAManager;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StaffChatPacket extends RedisPacket {
    private String name, coloredName, server, message;
    private UUID uuid;
    private boolean web = false;

    public StaffChatPacket(String name, String coloredName, String server, String message, UUID uuid) {
        this.name = name;
        this.coloredName = coloredName;
        this.server = server;
        this.message = message;
        this.uuid = uuid;
    }

    public StaffChatPacket(String name, String coloredName, String server, String message) {
        this(name, coloredName, server, message, null);
    }

    public StaffChatPacket() {
    }

    @Override
    public void onReceive(JsonObject data) {
        String msg = Lang.STAFF_CHAT_FORMAT.getMsg(coloredName, server, message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(Permissions.STAFFCHAT)) {
                player.sendMessage(msg);
            }
        }
        if (OctoCore.isMaster()) {
            JDAManager.sendDiscordSC(name, server, message);
        }
    }
}
