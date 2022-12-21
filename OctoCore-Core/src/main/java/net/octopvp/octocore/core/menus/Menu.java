package net.octopvp.octocore.core.menus;

import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.BaseGui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.utils.XMaterial;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public abstract class Menu<T extends BaseGui> {
    @Nullable // can be null until the gui is created
    protected T gui;

    public static GuiItem PLACEHOLDER_ITEM = ItemBuilder.from(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem())
            .name(CC.BLACK).asGuiItem(e -> {
            });

    public abstract T createGui(Player player);

    public abstract void populateGui(T gui, Player player);

    public void open(Player player) {
        gui = createGui(player);
        updateInteractions();
        populateGui(gui, player);
        gui.open(player);
    }

    public void updateInteractions() {
        gui.disableAllInteractions();
    }

    public GuiItem closeButton() {
        return ItemBuilder.from(XMaterial.BARRIER.parseItem())
                .name(CC.RED + "Close")
                .asGuiItem(e -> gui.close(e.getWhoClicked()));
    }
}
