package net.octopvp.octocore.core.command.providers;

import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.InvalidArgsException;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import net.octopvp.octocore.common.StringUtils;
import org.bukkit.enchantments.Enchantment;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class EnchantmentProvider implements Provider<Enchantment> {
    @Override
    public Enchantment provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        String ench = StringUtils.getEnchantment(args.pop());
        Enchantment enchantment = Enchantment.getByName(ench);
        if (enchantment == null) throw new InvalidArgsException("Enchantment not found: " + ench);
        return enchantment;
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return Arrays.asList("sharp", "sharpness", "ff", "featherfalling", "feather", "fire", "fireaspect", "kb", "knock", "knockback", "smi", "smite", "bane", "baneof", "baneofarthropods", "prot", "protection", "firep", "fireprot", "fireprotection",
                "blast", "blastprot", "blastprotection", "proj", "projprot", "projectileprotection", "loot", "looting", "fort", "fortune", "silk", "silktouch", "pow", "power", "pun", "punch", "fla", "flame", "inf", "infinity", "unb", "unbreaking", "eff", "efficiency");
    }
}
