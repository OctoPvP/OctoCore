package net.octopvp.octocore.core.command.impl.staff;

import net.octopvp.commander.annotation.*;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.manager.impl.VanishManager;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class VanishCommand {
    @Command(name = "vanish", aliases = {"v"}, description = "Vanish from other players")
    @Permission("octocore.command.vanish")
    public void vanish(@Sender Player sender, @Optional @Name("target") Player target1, @Flag(value = "priority", aliases = "p") @Range(min = 1) int priority, @Switch(value = "silent", aliases = "s") boolean silent) {
        // TODO @DefaultNumber(1) figure out why default broke
        Player target = target1 == null ? sender : target1;
        BiConsumer<Integer, Boolean> vanish = (i, b) -> {
            PlayerData data = PlayerManager.getInstance().getData(sender);
            int p = VanishManager.getInstance().getVanishPriority(data, b);
            if (p < priority) {
                if (target != sender) {
                    sender.sendMessage(CC.RED + "You cannot vanish " + target.getName() + " with a higher priority than your highest priority. (" + CC.YELLOW + p + CC.RED + ")");
                    return;
                } else
                    sender.sendMessage(CC.RED + "You cannot vanish with a higher priority than " + p);
                return;
            }
            VanishManager.getInstance().vanish(target, priority, silent);
            data.setJoinVanished(true);
            data.save();
            if (target != sender)
                sender.sendMessage(CC.GREEN + "You have vanished " + target.getName() + " with a priority of " + CC.YELLOW + priority + CC.GREEN + ".");
            else
                sender.sendMessage(CC.GREEN + "You are now vanished with a priority of " + CC.YELLOW + VanishManager.getInstance().getVanished().get(target.getUniqueId()) + CC.GREEN + ".");
        };
        if (priority > 0 && VanishManager.getInstance().isVanished(target)) { // if priority is specified and they are already vanished
            vanish.accept(priority, true);
            return;
        }
        if (VanishManager.getInstance().isVanished(target)) {
            VanishManager.getInstance().unvanish(target, silent);
            PlayerManager.getInstance().modifyData(target.getUniqueId(), data -> data.setJoinVanished(false));
            if (target != sender)
                sender.sendMessage(CC.GREEN + "You have unvanished " + target.getName() + ".");
            else
                sender.sendMessage(CC.GREEN + "You are no longer vanished.");
        } else {
            vanish.accept(priority, false);
        }
    }
}
