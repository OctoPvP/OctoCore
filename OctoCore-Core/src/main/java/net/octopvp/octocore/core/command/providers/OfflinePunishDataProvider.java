package net.octopvp.octocore.core.command.providers;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.objects.OfflinePunishData;
import org.bukkit.entity.Player;

import java.util.Deque;
import java.util.List;

public class OfflinePunishDataProvider implements Provider<OfflinePunishData> {
    @Override
    public OfflinePunishData provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        return new OfflinePunishData(args.pop());
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return OctoCore.getInstance().getCommander().getArgumentProviders().get(Player.class).provideSuggestions(input, lastArg, sender);
    }
}
