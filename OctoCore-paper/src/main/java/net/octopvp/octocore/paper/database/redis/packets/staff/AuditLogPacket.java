package net.octopvp.octocore.paper.database.redis.packets.staff;

import com.google.gson.JsonObject;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.redis.packet.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.objects.enums.AuditLogType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class AuditLogPacket extends RedisPacket {
    private AuditLogType logType;

    @Override
    public void onReceive(JsonObject data) {
        if (logType != AuditLogType.WORLDEDIT_ACTION && logType != AuditLogType.AUTH_FAIL)
            return;
        if (logType == AuditLogType.WORLDEDIT_ACTION) {
            String player = data.get("player").getAsString();
            String command = data.get("command").getAsString();

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission(Permissions.RECEIVE_AUDIT_WORLDEDIT)) {
                    //TODO finish audit log
                }
            }
        }
    }
}
