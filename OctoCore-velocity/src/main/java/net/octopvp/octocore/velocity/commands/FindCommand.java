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
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.OnlinePlayer;

public class FindCommand {
    public static BrigadierCommand createCommand(final ProxyServer server) {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
                .<CommandSource>literal("gfind")
                .requires(source -> source.hasPermission("octocore.staff"))
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            server.getAllPlayers().forEach(player -> builder.suggest(player.getUsername()));
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            String targetName = context.getArgument("player", String.class);
                            OnlinePlayer player = OctoCoreCommon.getInstance().getServerImplementation().getServerManager().getOnlinePlayer(targetName);
                            if (player != null) {
                                context.getSource().sendMessage(Component.text(player.getName() + " is currently on ", NamedTextColor.YELLOW)
                                        .append(Component.text(player.getServer(), NamedTextColor.GREEN)));
                            } else {
                                context.getSource().sendMessage(Component.text("Player '" + targetName + "' is not online.", NamedTextColor.RED));
                            }
                            return 1;
                        }))
                .build();
        return new BrigadierCommand(node);
    }
}
