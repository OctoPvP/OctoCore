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
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ListTagsMenu extends PaginatedMenu<PaginatedGui> {
    private final Menu<?> prev;

    public GuiItem tag(PlayerTag tag) {
        return ItemBuilder.from(tag.getMaterial())
                .name(CC.AQUA + tag.getName())
                .lore(
                        CC.SCOREBOARD_SEPARATOR,
                        CC.AQUA + "Tag: " + CC.WHITE + tag.getTag(),
                        CC.AQUA + "Description: " + CC.WHITE + tag.getDescription(),
                        CC.SCOREBOARD_SEPARATOR,
                        "&7ID: " + tag.getId()
                ).asGuiItem();
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> items = new ArrayList<>();
        for (PlayerTag tag : TagManager.getTags()) {
            items.add(tag(tag));
        }
        return items;
    }

    @Override
    public Menu<?> getBackMenu() {
        return prev;
    }

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Tags")
                .rows(6)
                .create();
    }
}
