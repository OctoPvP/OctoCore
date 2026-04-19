package net.octopvp.octocore.velocity.commands;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.ServerData;

import java.util.Collection;

public class GListCommand {
    public static BrigadierCommand createCommand(final ProxyServer server) {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
                .<CommandSource>literal("glist")
                .requires(source -> source.hasPermission("octocore.staff"))
                .executes(context -> {
                    Collection<ServerData> servers = OctoCoreCommon.getInstance().getServerImplementation().getServerManager().getConnectedServers();
                    int total = server.getPlayerCount();
                    context.getSource().sendMessage(Component.text("Network Players:", NamedTextColor.GOLD));
                    for (ServerData serverData : servers) {
                        if (serverData.getServerName().equalsIgnoreCase("Velocity") || serverData.getServerName().equalsIgnoreCase(OctoCoreCommon.getInstance().getServerName())) {
                            continue;
                        }
                        int count = serverData.getOnlinePlayers().size();
                        context.getSource().sendMessage(Component.text(" - " + serverData.getServerName() + ": ", NamedTextColor.YELLOW)
                                .append(Component.text(count, NamedTextColor.GREEN)));
                    }
                    context.getSource().sendMessage(Component.text("Total Players: ", NamedTextColor.GOLD)
                            .append(Component.text(total, NamedTextColor.GREEN)));
                    return 1;
                })
                .build();
        return new BrigadierCommand(node);
    }
}
