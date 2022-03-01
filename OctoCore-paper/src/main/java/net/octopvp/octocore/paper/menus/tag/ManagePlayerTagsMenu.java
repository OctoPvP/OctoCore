package net.octopvp.octocore.paper.menus.tag;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.database.redis.packets.player.TagUpdatePacket;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.PlayerTag;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.SoundUtil;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
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
        for (PlayerTag allowedTag : data.getAllowedTags()) {
            buttons.add(new TagButton(allowedTag));
        }
        return buttons;
    }

    @Override
    public List<Button> getToolbarButtons() {
        return Arrays.asList(new AddTagButton());
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
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            super.onClick(player, slot, clickType, event);
            if (clickType == ClickType.SHIFT_RIGHT) {
                new TagUpdatePacket(new JsonBuilder().addProperty("uuid", data.getUuid().toString()).addProperty("type", "REMOVE_TAG").addProperty("tagId", tag.getId().toString())).send();
                SoundUtil.playPing(player);
                player.closeInventory();
            }
        }
    }

    @RequiredArgsConstructor
    private class AddTagButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.EMERALD).name(CC.GREEN + "Add Tag").lore(CC.YELLOW + "Click to give this player a tag!").build();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            new GiveTagsMenu(data).open(player);
        }
    }
}
