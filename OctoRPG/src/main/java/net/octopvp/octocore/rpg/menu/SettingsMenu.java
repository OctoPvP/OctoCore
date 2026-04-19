package net.octopvp.octocore.rpg.menu;

import dev.octomc.agile.builder.item.ItemBuilder;
import dev.octomc.agile.guis.Gui;
import dev.octomc.agile.menu.Menu;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.manager.RPGPlayerManager;
import net.octopvp.octocore.rpg.object.RPGPlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SettingsMenu extends Menu<Gui> {
    @Override
    public Gui createGui(Player player) {
        return Gui.gui()
                .title("RPG Settings")
                .rows(3)
                .create();
    }

    @Override
    public void populateGui(Gui gui, Player player) {
        RPGPlayerData data = RPGPlayerManager.getInstance().getData(player);
        if (data == null) return;

        gui.setItem(13, ItemBuilder.from(Material.ENCHANTED_BOOK)
                .name(CC.translate("&bShow Enchantment Descriptions"))
                .lore(
                        "",
                        CC.translate("&7Toggle whether custom enchantment"),
                        CC.translate("&7descriptions are shown on items."),
                        "",
                        CC.translate("&7Current: " + (data.isShowEnchantments() ? "&aEnabled" : "&cDisabled")),
                        "",
                        CC.translate("&eClick to toggle!")
                )
                .asGuiItem(event -> {
                    data.setShowEnchantments(!data.isShowEnchantments());
                    player.sendMessage(CC.translate("&7[&bRPG&7] &fEnchantment descriptions: " + (data.isShowEnchantments() ? "&aEnabled" : "&cDisabled")));
                    populateGui(gui, player); // Refresh
                }));

        gui.getFiller().fill(PLACEHOLDER_ITEM);
    }
}
