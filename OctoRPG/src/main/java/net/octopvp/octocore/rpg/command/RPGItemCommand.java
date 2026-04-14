package net.octopvp.octocore.rpg.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.octopvp.octocore.rpg.OctoRPG;
import net.octopvp.octocore.rpg.item.CustomItem;
import net.octopvp.octocore.rpg.menu.MainItemsMenu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RPGItemCommand implements BasicCommand {
    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (!(stack.getSender() instanceof Player player)) {
            stack.getSender().sendMessage("This command can only be used by players.");
            return;
        }
        if (!player.hasPermission("octorpg.admin")) {
            player.sendMessage("No permission.");
            return;
        }
        if (args.length == 0) {
            new MainItemsMenu().open(player);
            return;
        }
        String id = args[0].toUpperCase();
        CustomItem item = OctoRPG.getInstance().getItemManager().getCustomItemById(id);
        if (item != null) {
            player.getInventory().addItem(item.build());
            player.sendMessage("You received a " + item.getName() + "!");
        } else {
            player.sendMessage("Item not found: " + id);
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (args.length <= 1) {
            return OctoRPG.getInstance().getItemManager().getCustomItems().keySet().stream()
                    .filter(id -> id.toLowerCase().startsWith(args.length == 0 ? "" : args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
