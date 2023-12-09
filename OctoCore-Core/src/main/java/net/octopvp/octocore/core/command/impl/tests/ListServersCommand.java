package net.octopvp.octocore.core.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.ServerData;
import net.octopvp.octocore.core.OctoCore;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class ListServersCommand {
    @Command(name = "listservers")
    @Permission(Permissions.ADMIN)
    public void execute(CommandSender sender) {
        Set<ServerData> servers = OctoCore.getInstance().getServerManager().getConnectedServers();
        sender.sendMessage("Connected servers: " + servers.size());
        for (ServerData server : servers) {
            sender.sendMessage(" - " + server.getServerName() + " (" + server.getOnlinePlayers().size() + "/" + server.getMaxPlayers() + ")");
            for (String name : server.getNames()) {
                sender.sendMessage("   - " + name);
            }
        }
    }
}
