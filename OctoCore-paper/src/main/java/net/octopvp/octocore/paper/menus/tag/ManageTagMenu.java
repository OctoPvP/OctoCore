package net.octopvp.octocore.paper.menus.tag;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.manager.impl.TagManager;
import net.octopvp.octocore.paper.objects.PlayerTagBuilder;
import net.octopvp.octocore.paper.other.ManageTagProcess;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.PlaceholderButton;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ManageTagMenu extends Menu {
    private boolean edit = false;
    private Menu prev = null;

    public ManageTagMenu(Player player) {
        process = new ManageTagProcess(player);
        builder = process.getBuilder();
    }

    public ManageTagMenu(Menu prev, PlayerTagBuilder builder, ManageTagProcess process) {
        this.builder = builder;
        this.process = process;
        this.prev = prev;
        edit = true;
    }

    private final PlayerTagBuilder builder;
    private final ManageTagProcess process;

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(new TagNameButton((builder.getTagName() == "") ? "Not Set Yet" : builder.getTagName()));
        buttons.add(new TagDescButton((builder.getDesc() == "") ? "Not Set Yet" : builder.getDesc()));
        buttons.add(new TagButton((builder.getTag() == "") ? "Not Set Yet" : builder.getTag()));
        buttons.add(new BuildTagButton());
        buttons.add(new PlaceHolderButton());
        if (edit) {
            buttons.add(new BackButton());
            buttons.add(new DeleteButton());
        } else {
            buttons.add(new CloseButton());
        }
        return buttons;
    }

    @Override
    public String getName(Player player) {
        return "Create a tag";
    }

    @Override
    public int getInventorySize(List<Button> buttons) {
        return 27;
    }
    public class BuildTagButton extends Button{

        @Override
        public ItemStack getItem(Player player) {
            if (!edit)
                return new ItemBuilder(Material.EMERALD_BLOCK).name(CC.GREEN + "Create!").lore(CC.YELLOW + "Click to create the tag!").build();
            return new ItemBuilder(Material.EMERALD_BLOCK).name(CC.GREEN + "Save").lore(CC.YELLOW + "Click to save the tag!").build();
        }

        @Override
        public int getSlot() {
            return 26;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            player.closeInventory();
            process.build();
        }
    }
    public class TagNameButton extends Button{
        public TagNameButton(String name){
            this.name = name;
        }
        String name;
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.SIGN).name(CC.AQUA + "Tag name").lore(
                    CC.SEPARATOR,
                    "",
                    CC.AQUA + "Name: " + CC.WHITE + name,
                    "",
                    CC.SEPARATOR
            ).build();
        }

        @Override
        public int getSlot() {
            return 11;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            player.closeInventory();
            process.setNameProcess((builder) -> open(player));
        }
    }
    public class TagDescButton extends Button{
        public TagDescButton(String desc){
            this.desc = desc;
        }
        String desc;
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.SIGN).name(CC.AQUA + "Tag description").lore(
                    CC.SEPARATOR,
                    "",
                    CC.AQUA + "Description: " + CC.WHITE + desc,
                    "",
                    CC.SEPARATOR
            ).build();
        }

        @Override
        public int getSlot() {
            return 13;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            player.closeInventory();
            process.setDescProcess((builder) -> {
                open(player);
            });
        }
    }
    public class TagButton extends Button{
        public TagButton(String tag){
            this.tag = tag;
        }
        String tag;
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.NAME_TAG).name(CC.AQUA + "Tag").lore(
                    CC.SEPARATOR,
                    "",
                    CC.AQUA + "Tag: " + CC.WHITE + tag,
                    "",
                    CC.SEPARATOR
            ).build();
        }

        @Override
        public int getSlot() {
            return 15;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            player.closeInventory();
            process.setTagProcess((builder) -> {
                this.tag = builder.getTag();
                open(player);
            });
        }
    }
    public class PlaceHolderButton extends PlaceholderButton{

        @Override
        public int[] getSlots() {
            List<Integer> a = new ArrayList<>();
            IntStream.range(0, 26).forEach((i) -> {
                if (!(i == 11 || i == 13 || i == 15 || i == 26 || i == 22)) {
                    if (edit && i == 0)
                        return;
                    a.add(i);
                }
            });
            return a.stream().mapToInt(i -> i).toArray();
        }
    }

    public class BackButton extends net.octopvp.octocore.paper.utils.menu.buttons.impl.BackButton {
        @Override
        public int getSlot() {
            return 22;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            prev.open(player);
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

    public class DeleteButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.REDSTONE_BLOCK).name(CC.RED + "Delete").lore(CC.YELLOW + "Click to delete this tag.").build();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            super.onClick(player, slot, clickType, event);
            TagManager.deleteTag(builder.getBase().getId());
            player.sendMessage(CC.GREEN + "Deleted tag " + builder.getBase().getId());
            player.closeInventory();
        }
    }
}
