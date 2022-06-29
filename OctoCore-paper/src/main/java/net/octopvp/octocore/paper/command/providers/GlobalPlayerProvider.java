package net.octopvp.octocore.paper.command.providers;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.objects.GlobalPlayer;
import org.bukkit.entity.Player;

import java.util.Deque;
import java.util.List;

public class GlobalPlayerProvider implements Provider<GlobalPlayer> {
    @Override
    public GlobalPlayer provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        if (commandInfo.getCommander().getPlatform().isSenderParameter(parameterInfo))
            return OctoCore.getInstance().getServerManager().getGlobalPlayer(context.getCommandSender().getIdentifier());
        return OctoCore.getInstance().getServerManager().getGlobalPlayer(args.pop());
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return OctoCore.getInstance().getCommander().getArgumentProviders().get(Player.class).provideSuggestions(input, lastArg, sender);
    }
}
