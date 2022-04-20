package net.octopvp.octocore.paper.menus.tag;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.manager.impl.TagManager;
import net.octopvp.octocore.paper.objects.PlayerTag;
import net.octopvp.octocore.paper.objects.PlayerTagBuilder;
import net.octopvp.octocore.paper.other.ManageTagProcess;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ManageTagsMenu extends PaginatedMenu {
    private final Menu prev, instance = this;

    @Override
    public String getPagesTitle(Player player) {
        return "Manage Tags";
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
        return new BackButton.SuppliedBackButton(new TagAdminMenu());
    }

    @RequiredArgsConstructor
    private class TagButton extends Button {
        private final PlayerTag tag;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder builder = new ItemBuilder(tag.getMaterial()).name(CC.AQUA + tag.getName()).lore(
                    CC.SEPARATOR,
                    CC.AQUA + "Tag: " + CC.WHITE + tag.getTag(),
                    CC.AQUA + "Description: " + CC.WHITE + tag.getDescription(),
                    CC.AQUA + "ID: " + CC.WHITE + tag.getId(),
                    CC.SEPARATOR,
                    CC.YELLOW + "Click to manage this tag."
            );
            return builder.build();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            super.onClick(player, slot, clickType, event);
            PlayerTagBuilder builder = tag.toBuilder();
            new ManageTagMenu(instance, builder, new ManageTagProcess(builder, player)).open(player);
        }
    }
}
