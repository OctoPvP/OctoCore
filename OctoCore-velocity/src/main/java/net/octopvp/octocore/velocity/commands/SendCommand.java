package net.octopvp.octocore.velocity.commands;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Optional;

public class SendCommand {
    public static BrigadierCommand createCommand(final ProxyServer server) {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
                .<CommandSource>literal("send")
                .requires(source -> source.hasPermission("octocore.admin"))
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("target", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            builder.suggest("all");
                            builder.suggest("current");
                            builder.suggest("*");
                            server.getAllPlayers().forEach(player -> builder.suggest(player.getUsername()));
                            return builder.buildFuture();
                        })
                        .then(RequiredArgumentBuilder.<CommandSource, String>argument("server", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    server.getAllServers().forEach(s -> builder.suggest(s.getServerInfo().getName()));
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    String targetName = context.getArgument("target", String.class);
                                    String serverName = context.getArgument("server", String.class);
                                    Optional<RegisteredServer> targetServer = server.getServer(serverName);

                                    if (!targetServer.isPresent()) {
                                        context.getSource().sendMessage(Component.text("Server '" + serverName + "' not found.", NamedTextColor.RED));
                                        return 1;
                                    }

                                    if (targetName.equalsIgnoreCase("all") || targetName.equals("*")) {
                                        server.getAllPlayers().forEach(p -> p.createConnectionRequest(targetServer.get()).connect());
                                        context.getSource().sendMessage(Component.text("Sending all players to " + serverName, NamedTextColor.GREEN));
                                    } else if (targetName.equalsIgnoreCase("current")) {
                                        if (context.getSource() instanceof Player) {
                                            Player sender = (Player) context.getSource();
                                            sender.getCurrentServer().ifPresent(s -> {
                                                s.getServer().getPlayersConnected().forEach(p -> p.createConnectionRequest(targetServer.get()).connect());
                                            });
                                            context.getSource().sendMessage(Component.text("Sending all players on your current server to " + serverName, NamedTextColor.GREEN));
                                        } else {
                                            context.getSource().sendMessage(Component.text("This target can only be used by players.", NamedTextColor.RED));
                                        }
                                    } else {
                                        Optional<Player> targetPlayer = server.getPlayer(targetName);
                                        if (targetPlayer.isPresent()) {
                                            targetPlayer.get().createConnectionRequest(targetServer.get()).connect();
                                            context.getSource().sendMessage(Component.text("Sending " + targetName + " to " + serverName, NamedTextColor.GREEN));
                                        } else {
                                            context.getSource().sendMessage(Component.text("Player '" + targetName + "' not found.", NamedTextColor.RED));
                                        }
                                    }
                                    return 1;
                                })))
                .build();
        return new BrigadierCommand(node);
    }
}
