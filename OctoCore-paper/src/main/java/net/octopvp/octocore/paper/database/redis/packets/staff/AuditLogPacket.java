package net.octopvp.octocore.paper.database.redis.packets.staff;

import com.google.gson.JsonObject;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.redis.RedisPacket;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.octopvp.octocore.paper.objects.enums.AuditLogType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@NoArgsConstructor
public class AuditLogPacket extends RedisPacket {
    @Override
    public void onReceive(JsonObject data) throws Exception {
        AuditLogType logType = AuditLogType.valueOf(data.get("type").getAsString());
        if (logType != AuditLogType.WORLDEDIT_ACTION && logType != AuditLogType.AUTH_FAIL)
            return;
        if (logType == AuditLogType.WORLDEDIT_ACTION) {
            String player = data.get("player").getAsString();
            String command = data.get("command").getAsString();

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission(Permission.RECEIVE_AUDIT_WORLDEDIT.getNode())) {
                    //TODO finish audit log
                }
            }
            return;
        }
        return;
    }

    @Override
    public JsonBuilder getData() {
        return null;
    }

    @Override
    public String getName() {
        return "AuditLogPacket";
    }
}
