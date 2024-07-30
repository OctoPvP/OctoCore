package net.octopvp.octocore.core.command.impl.fun;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiType;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
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
            gui.setItem(3, ItemBuilder.from(Material.PRISMARINE_CRYSTALS).name(Component.text("Raw Crack").decoration(TextDecoration.ITALIC, false)).asGuiItem());
            gui.getFiller().fill(ItemBuilder.from(Material.SUGAR).name(Component.text("Processed Crack").decoration(TextDecoration.ITALIC, false)).asGuiItem());
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
