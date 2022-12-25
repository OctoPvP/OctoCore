package net.octopvp.octocore.core.menus.tag;

import lombok.RequiredArgsConstructor;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.guis.PaginatedGui;
import net.octopvp.agile.menu.Menu;
import net.octopvp.agile.menu.PaginatedMenu;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.manager.impl.TagManager;
import net.octopvp.octocore.core.objects.PlayerTag;
import net.octopvp.octocore.core.objects.PlayerTagBuilder;
import net.octopvp.octocore.core.other.ManageTagProcess;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ManageTagsMenu extends PaginatedMenu<PaginatedGui> {
    private final Menu<?> prev, instance = this;

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Manage Tags")
                .rows(6)
                .create();
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> items = new ArrayList<>();
        for (PlayerTag tag : TagManager.getTags()) {
            items.add(tag(tag));
        }
        return items;
    }

    public GuiItem tag(PlayerTag tag) {
        return ItemBuilder.from(tag.getMaterial())
                .name(CC.AQUA + tag.getName())
                .lore(
                        CC.SEPARATOR,
                        CC.AQUA + "Tag: " + CC.WHITE + tag.getTag(),
                        CC.AQUA + "Description: " + CC.WHITE + tag.getDescription(),
                        CC.AQUA + "ID: " + CC.WHITE + tag.getId(),
                        CC.SEPARATOR,
                        CC.YELLOW + "Click to manage this tag."
                )
                .asGuiItem(event -> {
                    PlayerTagBuilder builder = tag.toBuilder();
                    new ManageTagMenu(instance, builder, new ManageTagProcess(builder, (Player) event.getWhoClicked())).open((Player) event.getWhoClicked());
                });
    }

    @Override
    public Menu<?> getBackMenu() {
        return prev;
    }
}
