package net.octopvp.octocore.velocity.commands;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

public class HubCommand {
    public static BrigadierCommand createCommand(final ProxyServer server) {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
                .<CommandSource>literal("ghub")
                .executes(context -> {
                    if (!(context.getSource() instanceof Player)) {
                        context.getSource().sendMessage(Component.text("This command can only be used by players.", NamedTextColor.RED));
                        return 1;
                    }
                    Player player = (Player) context.getSource();
                    Optional<RegisteredServer> hub = server.getServer("hub"); // Default to 'hub'
                    if (!hub.isPresent()) {
                        hub = server.getServer("lobby"); // Try 'lobby'
                    }

                    if (hub.isPresent()) {
                        if (player.getCurrentServer().isPresent() && player.getCurrentServer().get().getServerInfo().getName().equalsIgnoreCase(hub.get().getServerInfo().getName())) {
                            player.sendMessage(Component.text("You are already connected to the hub!", NamedTextColor.RED));
                        } else {
                            player.createConnectionRequest(hub.get()).connect();
                            player.sendMessage(Component.text("Sending you to the hub...", NamedTextColor.GREEN));
                        }
                    } else {
                        player.sendMessage(Component.text("No hub server found!", NamedTextColor.RED));
                    }
                    return 1;
                })
                .build();
        return new BrigadierCommand(node);
    }
}
