package net.octopvp.octocore.paper.menus.tag;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.PlayerTag;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ManagePlayerTagsMenu extends PaginatedMenu {
    private final PlayerData data;

    @Override
    public String getPagesTitle(Player player) {
        return "Manage " + data.getName() + "'s Tags";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        return buttons;
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
                    CC.YELLOW + "Shift-Right-Click to remove this tag."
            );
            return builder.build();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            super.onClick(player, slot, clickType);
            if (clickType == ClickType.SHIFT_RIGHT) {
                data.removeTag(tag);
            }
        }
    }
}
