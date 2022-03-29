package net.octopvp.octocore.paper.menus.tag;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.PlayerTag;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.SoundUtil;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.PlaceholderButton;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class MainTagMenu extends Menu {
    private final Menu instance = this;

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(new PlaceHolderButton());
        buttons.add(new CloseButton());
        buttons.add(new BuyTagsButton());
        buttons.add(new ViewAllTagsButton());
        buttons.add(new ViewYourTagsButton());
        return buttons;
    }

    @Override
    public String getName(Player player) {
        return "Tags";
    }

    public class BuyTagsButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.GOLD_BARDING).name(CC.AQUA + "Buy Tags").lore(CC.SEPARATOR, CC.AQUA + "Click here to buy new tags!", CC.SEPARATOR).build();
        }

        @Override
        public int getSlot() {
            return 11;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            SoundUtil.playError(player);
            player.sendMessage(Lang.FEATURE_NOT_IMPLEMENTED.getMsg());
        }
    }

    public class ViewAllTagsButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.GOLDEN_CARROT).name(CC.GOLD + "View All Tags").lore(CC.SEPARATOR, CC.AQUA + "Click here to view all tags!", CC.SEPARATOR).build();
        }

        @Override
        public int getSlot() {
            return 13;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            new ListTagsMenu(instance).open(player);
        }
    }

    public class ViewYourTagsButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.CHEST).name(CC.GREEN + "My Tags").lore(CC.SEPARATOR, CC.AQUA + "Click here to view all tags you own!", CC.SEPARATOR).build();
        }

        @Override
        public int getSlot() {
            return 15;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            PlayerData data = PlayerManager.getInstance().getData(player.getUniqueId());
            List<PlayerTag> tags = new ArrayList<>();
            data.getAllowedTags().forEach(tag -> {
                if (tag != null)
                    tags.add(tag);
            });
            Logger.debug("Tags: " + tags.size() + " | " + tags);
            new MyTagsMenu(tags, player).open(player);
        }
    }

    public class PlaceHolderButton extends PlaceholderButton {

        @Override
        public int[] getSlots() {
            List<Integer> a = new ArrayList<>();
            IntStream.range(0, 27).forEach((i) -> {
                if (!(i == 11 || i == 13 || i == 15 || i == 22))
                    a.add(i);
            });
            return a.stream().mapToInt(i -> i).toArray();
        }
    }

    private class CloseButton extends net.octopvp.octocore.paper.utils.menu.buttons.impl.CloseButton {
        @Override
        public int getSlot() {
            return 22;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            player.getOpenInventory().close();
        }
    }

}
