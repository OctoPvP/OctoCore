package net.octopvp.octocore.velocity.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

public class HasPermCommand {
    public static BrigadierCommand createCommand(final ProxyServer server) {
        LiteralCommandNode<CommandSource> helloNode = LiteralArgumentBuilder
                .<CommandSource>literal("proxyhasperm")
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("permission", StringArgumentType.word())
                        .executes(context -> {
                            context.getSource().sendMessage(
                                    Component.text(
                                            context.getSource().getPermissionValue(context.getArgument("permission", String.class))
                                                    .name()
                                    )
                            );
                            return 1;
                        }))
                .build();
        return new BrigadierCommand(helloNode);
    }
}
