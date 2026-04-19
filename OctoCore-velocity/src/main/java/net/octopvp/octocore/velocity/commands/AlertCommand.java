package net.octopvp.octocore.velocity.commands;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.octopvp.octocore.common.redis.packets.GlobalBroadcastPacket;
import net.octopvp.octocore.common.util.CC;

public class AlertCommand {
    public static BrigadierCommand createCommand(final ProxyServer server) {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
                .<CommandSource>literal("galert")
                .requires(source -> source.hasPermission("octocore.admin"))
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("message", StringArgumentType.greedyString())
                        .executes(context -> {
                            String message = context.getArgument("message", String.class);
                            String formatted = CC.translate(message);
                            Component component = Component.text("[Alert] ", NamedTextColor.RED)
                                    .append(Component.text(formatted, NamedTextColor.WHITE));
                            server.getAllPlayers().forEach(p -> p.sendMessage(component));
                            server.getConsoleCommandSource().sendMessage(component);
                            
                            new GlobalBroadcastPacket(formatted).send();
                            return 1;
                        }))
                .build();
        return new BrigadierCommand(node);
    }
}
