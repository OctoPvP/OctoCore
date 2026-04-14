package net.octopvp.octocore.rpg.menu;

import dev.octomc.agile.builder.item.ItemBuilder;
import dev.octomc.agile.guis.Gui;
import dev.octomc.agile.guis.GuiItem;
import dev.octomc.agile.menu.Menu;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.OctoRPG;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MainItemsMenu extends Menu<Gui> {

    @Override
    public Gui createGui(Player player) {
        return Gui.gui()
                .title("RPG Items")
                .rows(3)
                .create();
    }

    @Override
    public void populateGui(Gui gui, Player player) {
        gui.setItem(11, ItemBuilder.from(Material.NETHERITE_SWORD)
                .name(CC.translate("&6Weapons"))
                .lore("", CC.translate("&eClick to see all weapons!"))
                .asGuiItem(event -> {
                    new ItemsMenu("Weapons", OctoRPG.getInstance().getItemManager().getCustomItems().values().stream().toList()).open(player);
                }));

        gui.setItem(13, ItemBuilder.from(Material.NETHERITE_CHESTPLATE)
                .name(CC.translate("&bArmor"))
                .lore("", CC.translate("&eClick to see all armor!"))
                .asGuiItem(event -> {
                    player.sendMessage(CC.translate("&cNo armor items yet!"));
                }));

        gui.setItem(15, ItemBuilder.from(Material.GOLD_INGOT)
                .name(CC.translate("&eCustom Items"))
                .lore("", CC.translate("&eClick to see all custom items!"))
                .asGuiItem(event -> {
                    new ItemsMenu("Custom Items", OctoRPG.getInstance().getItemManager().getCustomItems().values().stream().toList()).open(player);
                }));

        gui.getFiller().fill(PLACEHOLDER_ITEM);
    }
}
