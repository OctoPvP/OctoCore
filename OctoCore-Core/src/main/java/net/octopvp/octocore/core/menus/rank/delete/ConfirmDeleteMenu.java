package net.octopvp.octocore.core.menus.rank.delete;

import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.menu.Menu;
import net.octopvp.agile.util.XMaterial;
import net.octopvp.octocore.common.object.permissions.Rank;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.manager.impl.RankManager;
import net.octopvp.octocore.core.utils.runnable.Countdown;
import org.bukkit.entity.Player;

public class ConfirmDeleteMenu extends Menu<Gui> {
    private final Rank toDelete;
    private int counter = 5;

    public ConfirmDeleteMenu(Rank toDelete) {
        this.toDelete = toDelete;
        counter();
    }

    public void counter() {
        new Count(5).start();
    }

    @Override
    public Gui createGui(Player player) {
        return Gui.gui()
                .title(CC.RED + "Confirm Delete ")
                .rows(3)
                .create();
    }

    public GuiItem confirmButton() {
        return ItemBuilder.from(XMaterial.GREEN_STAINED_GLASS_PANE)
                .name(CC.GREEN + "Confirm")
                .asGuiItem(event -> {
                    RankManager.getInstance().delete(toDelete);
                    event.getWhoClicked().closeInventory();
                });
    }

    public GuiItem noButton() {
        return ItemBuilder.from(XMaterial.RED_STAINED_GLASS_PANE)
                .name(CC.RED + "No")
                .asGuiItem(event -> {
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().sendMessage(CC.RED + "Canceled!");
                });
    }

    public GuiItem infoButton() {
        return ItemBuilder.from(XMaterial.BEACON)
                .name(CC.RED + "Are you sure you want to delete this rank?")
                .lore(
                        CC.SEPARATOR,
                        CC.AQUA + "Rank: " + CC.YELLOW + toDelete.getName(),
                        CC.AQUA + "Display Name: " + CC.YELLOW + toDelete.getDisplayName(),
                        CC.AQUA + "Weight: " + CC.YELLOW + toDelete.getWeight(),
                        CC.AQUA + "Rank Type: " + CC.YELLOW + toDelete.getRankType(),
                        CC.SEPARATOR
                )
                .asGuiItem(event -> {
                });
    }

    public GuiItem waitButton() {
        return ItemBuilder.from(XMaterial.RED_STAINED_GLASS_PANE)
                .name(CC.D_RED + "Please wait " + counter + " seconds...")
                .asGuiItem(event -> {
                });
    }

    @Override
    public void populateGui(Gui gui, Player player) {
        gui.setItem(11, waitButton());
        gui.setItem(13, infoButton());
        gui.setItem(15, noButton());
        if (counter <= 0) {
            gui.setItem(11, confirmButton());
        }
    }

    class Count extends Countdown {

        public Count(int time) {
            super(time);
        }

        @Override
        public void count(int current) {
            counter = current;
        }
    }
}
