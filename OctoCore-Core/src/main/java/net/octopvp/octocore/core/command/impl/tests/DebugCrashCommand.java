package net.octopvp.octocore.core.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Switch;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.redis.packets.ServerCrashPacket;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import org.bukkit.command.CommandSender;

public class DebugCrashCommand {

    @Command(name = "debugcrash", description = "Simulate a server crash to test Discord bot logging.")
    @Permission(Permissions.ADMIN)
    public void execute(CommandSender sender, @Switch("large") boolean large) {
        String serverName = OctoCore.getServerName();
        String logContent;

        if (large) {
            // Generate a log > 1900 chars to test file attachment logic
            StringBuilder sb = new StringBuilder("Simulating a large crash log...\n");
            for (int i = 0; i < 100; i++) {
                sb.append("Line ").append(i).append(": java.lang.NullPointerException: Synthetic crash for testing purposes.\n");
            }
            logContent = sb.toString();
            sender.sendMessage(CC.YELLOW + "Sending LARGE crash packet (should appear as a .log file in Discord)...");
        } else {
            logContent = "java.lang.RuntimeException: Manual debug crash triggered by " + sender.getName();
            sender.sendMessage(CC.GREEN + "Sending small crash packet (should appear as a code block in Discord)...");
        }

        // Trigger the packet
        new ServerCrashPacket(serverName, logContent).send();
    }
}
