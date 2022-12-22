package net.octopvp.octocore.core.menus.impl.tag;

import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.menu.Menu;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.objects.PlayerTag;
import net.octopvp.octocore.core.utils.SoundUtil;
import net.octopvp.octocore.core.utils.menu.buttons.Button;
import net.octopvp.octocore.core.utils.menu.buttons.PlaceholderButton;
import net.octopvp.octocore.core.utils.menu.menu.Menu;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class MainTagMenu extends Menu<Gui> {
    private final Menu<?> instance = this;

    @Override
    public Gui createGui(Player player) {
        return Gui.gui()
                .title("Tags")
                .rows(3)
                .create();
    }

    public GuiItem buy() {
        return ItemBuilder.from(Material.GOLD_BARDING)
                .name(CC.AQUA + "Buy Tags")
                .lore(CC.SEPARATOR, CC.AQUA + "Click here to buy new tags!", CC.SEPARATOR)
                .asGuiItem(event -> {
                    SoundUtil.playError((Player) event.getWhoClicked());
                    event.getWhoClicked().sendMessage(Lang.FEATURE_NOT_IMPLEMENTED.getMsg());
                });
    }

    public GuiItem viewAll() {
        return ItemBuilder.from(Material.GOLDEN_CARROT)
                .name(CC.GOLD + "View All Tags")
                .lore(CC.SEPARATOR, CC.AQUA + "Click here to view all tags!", CC.SEPARATOR)
                .asGuiItem(event -> {
                    new ListTagsMenu(instance).open((Player) event.getWhoClicked());
                });
    }

    public GuiItem viewYour() {
        return ItemBuilder.from(Material.CHEST)
                .name(CC.GREEN + "My Tags")
                .lore(CC.SEPARATOR, CC.AQUA + "Click here to view all tags you own!", CC.SEPARATOR)
                .asGuiItem(event -> {
                    PlayerData data = PlayerManager.getInstance().getData(((Player) event.getWhoClicked()).getUniqueId());
                    List<PlayerTag> tags = new ArrayList<>();
                    data.getAllowedTags().forEach(tag -> {
                        if (tag != null)
                            tags.add(tag);
                    });
                    Logger.debug("Tags: " + tags.size() + " | " + tags);
                    new MyTagsMenu(tags, (Player) event.getWhoClicked()).open((Player) event.getWhoClicked());
                });
    }

    @Override
    public void populateGui(Gui gui, Player player) {
        gui.setItem(11, buy());
        gui.setItem(13, viewAll());
        gui.setItem(15, viewYour());
        gui.setItem(22, closeButton());
        gui.getFiller().fill(PLACEHOLDER_ITEM);
    }
}
