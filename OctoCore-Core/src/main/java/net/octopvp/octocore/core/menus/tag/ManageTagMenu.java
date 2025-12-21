package net.octopvp.octocore.core.menus.tag;

import dev.octomc.agile.builder.item.ItemBuilder;
import dev.octomc.agile.guis.Gui;
import dev.octomc.agile.guis.GuiItem;
import dev.octomc.agile.menu.Menu;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.manager.impl.TagManager;
import net.octopvp.octocore.core.objects.PlayerTagBuilder;
import net.octopvp.octocore.core.other.ManageTagProcess;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ManageTagMenu extends Menu<Gui> {
    private final PlayerTagBuilder builder;
    private final ManageTagProcess process;
    private boolean edit = false;
    private Menu<?> prev = null;

    public ManageTagMenu(Player player) {
        process = new ManageTagProcess(player);
        builder = process.getBuilder();
    }

    public ManageTagMenu(Menu<?> prev, PlayerTagBuilder builder, ManageTagProcess process) {
        this.builder = builder;
        this.process = process;
        this.prev = prev;
        edit = true;
    }

    public GuiItem deleteButton() {
        return ItemBuilder.from(Material.TNT).name(CC.RED + "Delete")
                .lore(CC.YELLOW + "Click to delete this tag.").asGuiItem(event -> {
                    TagManager.deleteTag(builder.getBase().getId());
                    event.getWhoClicked().sendMessage(CC.GREEN + "Deleted tag " + builder.getBase().getId());
                    event.getWhoClicked().closeInventory();
                });
    }

    public GuiItem tagButton() {
        return ItemBuilder.from(Material.NAME_TAG).name(CC.AQUA + "Tag")
                .lore(CC.SEPARATOR, "", CC.AQUA + "Tag: " + CC.WHITE + builder.getTag(), "", CC.SEPARATOR)
                .asGuiItem(event -> {
                    event.getWhoClicked().closeInventory();
                    process.setTagProcess((builder) -> {
                        open((Player) event.getWhoClicked());
                    });
                });
    }

    public GuiItem descButton() {
        return ItemBuilder.from(Material.OAK_SIGN).name(CC.AQUA + "Tag description")
                .lore(CC.SEPARATOR, "", CC.AQUA + "Description: " + CC.WHITE + builder.getDesc(), "", CC.SEPARATOR)
                .asGuiItem(event -> {
                    event.getWhoClicked().closeInventory();
                    process.setDescProcess((builder) -> {
                        open((Player) event.getWhoClicked());
                    });
                });
    }

    public GuiItem nameButton() {
        return ItemBuilder.from(Material.OAK_SIGN).name(CC.AQUA + "Tag name")
                .lore(CC.SEPARATOR, "", CC.AQUA + "Name: " + CC.WHITE + builder.getTagName(), "", CC.SEPARATOR)
                .asGuiItem(event -> {
                    event.getWhoClicked().closeInventory();
                    process.setNameProcess((builder) -> {
                        open((Player) event.getWhoClicked());
                    });
                });
    }

    public GuiItem buildButton() {
        return ItemBuilder.from(Material.EMERALD_BLOCK).name(CC.GREEN + "Create!")
                .lore(CC.SEPARATOR, "", CC.YELLOW + "Click to create the tag!", "", CC.SEPARATOR)
                .asGuiItem(event -> {
                    event.getWhoClicked().closeInventory();
                    process.build();
                });
    }

    @Override
    public Gui createGui(Player player) {
        return Gui.gui()
                .title(edit ? "Manage Tag" : "New Tag")
                .rows(3)
                .create();
    }

    @Override
    public void populateGui(Gui gui, Player player) {
        gui.setItem(10, tagButton());
        gui.setItem(12, nameButton());
        gui.setItem(14, descButton());
        gui.setItem(16, buildButton());
        if (edit) {
            gui.setItem(0, deleteButton());
            gui.setItem(22, backButton(prev));
        } else {
            gui.setItem(22, closeButton());
        }
        gui.getFiller().fill(Menu.PLACEHOLDER_ITEM);
    }
}
