package net.octopvp.octocore.core.menus.impl.tag;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.manager.impl.TagManager;
import net.octopvp.octocore.core.objects.PlayerTag;
import net.octopvp.octocore.core.utils.ItemBuilder;
import net.octopvp.octocore.core.utils.menu.buttons.Button;
import net.octopvp.octocore.core.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.core.utils.menu.menu.Menu;
import net.octopvp.octocore.core.utils.menu.menu.PaginatedMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ListTagsMenu extends PaginatedMenu {
    private final Menu prev;

    @Override
    public String getPagesTitle(Player player) {
        return "Tags";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        for (PlayerTag tag : TagManager.getTags()) {
            buttons.add(new TagButton(tag));
        }
        return buttons;
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton() {
            @Override
            public void clicked(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
                prev.open(player);
            }
        };
    }

    @RequiredArgsConstructor
    private class TagButton extends Button {
        private final PlayerTag tag;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(tag.getMaterial()).name(CC.AQUA + tag.getName()).lore(
                    CC.SCOREBOARD_SEPARATOR,
                    CC.AQUA + "Tag: " + CC.WHITE + tag.getTag(),
                    CC.AQUA + "Description: " + CC.WHITE + tag.getDescription(),
                    CC.SCOREBOARD_SEPARATOR,
                    "&7ID: " + tag.getId()
            ).build();
        }

        @Override
        public int getSlot() {
            return 0;
        }
    }
}
