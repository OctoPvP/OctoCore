package net.octopvp.octocore.velocity.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

public class IPBanCommand {
        public static BrigadierCommand createCommand(final ProxyServer server) {
                LiteralCommandNode<CommandSource> helloNode = LiteralArgumentBuilder
                                .<CommandSource>literal("ipban")
                                .then(RequiredArgumentBuilder
                                                .<CommandSource, String>argument("player", StringArgumentType.word())
                                                .executes(context -> {
                                                        String playerName = context.getArgument("player", String.class);
                                                        server.getPlayer(playerName).ifPresent(player -> {
                                                                String ipAddress = player.getRemoteAddress()
                                                                                .getAddress().getHostAddress();
                                                                context.getSource()
                                                                                .sendMessage(Component.text(ipAddress));
                                                        });
                                                        return 1;
                                                }))
                                .build();
                return new BrigadierCommand(helloNode);
        }
}
