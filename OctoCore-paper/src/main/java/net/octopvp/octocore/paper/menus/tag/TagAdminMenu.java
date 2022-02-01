package net.octopvp.octocore.paper.menus.tag;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.conversations.QuestionConversation;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.PlaceholderButton;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TagAdminMenu extends Menu {
    private final Menu instance = this;

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(new PlaceHolderButton());
        buttons.add(new ManageTagsButton());
        buttons.add(new CreateTagButton());
        buttons.add(new ManagePlayerTagsButton());
        return buttons;
    }

    @Override
    public Button getCloseButton() {
        return new CloseButton();
    }

    @Override
    public String getName(Player player) {
        return "Tags Admin";
    }

    public class ManageTagsButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.CHEST).name(CC.AQUA + "Manage Tags").lore(CC.YELLOW + "Click to manage tags").build();
        }

        @Override
        public int getSlot() {
            return 11;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            new ManageTagsMenu(instance).open(player);
        }
    }

    public class CreateTagButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.ANVIL).name(CC.GOLD + "Create Tag").lore(CC.YELLOW + "Click to create a tag.").build();
        }

        @Override
        public int getSlot() {
            return 13;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            new ManageTagMenu(player).open(player);
        }
    }

    public class ManagePlayerTagsButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.CHEST).name(CC.GREEN + "Manage Player Tags").lore(CC.YELLOW + "Click to manage a player's tags!").build();
        }

        @Override
        public int getSlot() {
            return 15;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            player.closeInventory();
            new QuestionConversation(CC.GREEN + "Please enter the username of the player.", (answer) -> {
                PlayerManager.getOfflineData(answer).thenAcceptAsync(data -> {
                    if (data == null) {
                        player.sendMessage(CC.RED + "That player does not exist!");
                        return;
                    }
                    new ManagePlayerTagsMenu(data).open(player);
                });
                return Prompt.END_OF_CONVERSATION;
            }).start(player);
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

    @RequiredArgsConstructor
    public class CloseButton extends net.octopvp.octocore.paper.utils.menu.buttons.impl.CloseButton {
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
