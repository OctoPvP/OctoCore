package net.octopvp.octocore.core.command.impl.fun;

import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.components.GuiType;
import net.octopvp.agile.guis.Gui;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Permission(Permissions.ADMIN)
public class CrackCommand {
    @Command(name = "crack")
    public void crack(@Sender Player player) {
        try {
            Gui gui = Gui.gui(GuiType.BREWING)
                    .title("Crack")
                    .create();
            gui.setItem(3, ItemBuilder.from(Material.PRISMARINE_CRYSTALS).name(CC.R + "Raw Crack").asGuiItem());
            gui.getFiller().fill(ItemBuilder.from(Material.SUGAR).name(CC.R + "Processed Crack").asGuiItem());
            gui.setDefaultClickAction(event -> {
                event.setCancelled(true);
                player.sendMessage(CC.RED + "No crack 4 u :)");
            });
            gui.open(player);
            player.sendMessage("Let there be crack!");
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(CC.RED + e.getMessage());
        }
    }
}
