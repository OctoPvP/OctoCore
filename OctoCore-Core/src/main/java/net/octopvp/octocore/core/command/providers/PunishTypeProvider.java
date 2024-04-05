package net.octopvp.octocore.core.command.providers;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import net.octopvp.octocore.common.object.punish.PunishmentType;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class PunishTypeProvider implements Provider<PunishmentType> {
    @Override
    public PunishmentType provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        return PunishmentType.valueOf(args.pop().toUpperCase());
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return Arrays.stream(PunishmentType.values()).map(Enum::name).collect(Collectors.toList());
    }
}
