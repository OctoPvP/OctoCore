package net.octopvp.octocore.paper.menus.tag;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.objects.PlayerTagBuilder;
import net.octopvp.octocore.paper.other.CreateTagProcess;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.PlaceholderButton;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CreateTagMenu extends Menu {
    public CreateTagMenu(Player player){
        process = new CreateTagProcess(player);
        builder = process.getBuilder();
    }
    public CreateTagMenu(PlayerTagBuilder builder,CreateTagProcess process) {
        this.builder = builder;
        this.process = process;
    }
    private PlayerTagBuilder builder;
    private CreateTagProcess process;
    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(new TagNameButton((builder.getTagName() == "") ? "Not Set Yet" : builder.getTagName()));
        buttons.add(new TagDescButton((builder.getDesc() == "") ? "Not Set Yet" : builder.getDesc()));
        buttons.add(new TagButton((builder.getTag() == "") ? "Not Set Yet" : builder.getTag()));
        buttons.add(new BuildTagButton());
        buttons.add(new PlaceHolderButton());
        buttons.add(new CloseButton());
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
            return new ItemBuilder(Material.EMERALD_BLOCK).name(CC.GREEN + "Create!").lore(CC.GREEN + "Click to create the tag!").build();
        }

        @Override
        public int getSlot() {
            return 26;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
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
                    CC.SCOREBOARD_SEPARATOR,
                    "",
                    CC.AQUA + "Name: " + CC.WHITE + name,
                    "",
                    CC.SCOREBOARD_SEPARATOR
            ).build();
        }

        @Override
        public int getSlot() {
            return 11;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            process.setNameProcess((builder)-> new CreateTagMenu(builder, process).open(player));
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
                    CC.SCOREBOARD_SEPARATOR,
                    "",
                    CC.AQUA + "Description: " + CC.WHITE + desc,
                    "",
                    CC.SCOREBOARD_SEPARATOR
            ).build();
        }

        @Override
        public int getSlot() {
            return 13;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            process.setDescProcess((builder)->{
                new CreateTagMenu(builder, process).open(player);
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
                    CC.SCOREBOARD_SEPARATOR,
                    "",
                    CC.AQUA + "Tag: " + CC.WHITE + tag,
                    "",
                    CC.SCOREBOARD_SEPARATOR
            ).build();
        }

        @Override
        public int getSlot() {
            return 15;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            process.setTagProcess((builder)->{
                this.tag = builder.getTag();
                new CreateTagMenu(builder, process).open(player);
            });
        }
    }
    public class PlaceHolderButton extends PlaceholderButton{

        @Override
        public int[] getSlots() {
            List<Integer> a = new ArrayList<>();
            IntStream.range(0,26).forEach((i)->{
                if (!(i == 11 || i == 13 || i == 15 || i == 26 || i == 22))
                    a.add(i);
            });
            return a.stream().mapToInt(i ->i).toArray();
        }
    }
    public class CloseButton extends net.octopvp.octocore.paper.utils.menu.buttons.impl.CloseButton{
        @Override
        public int getSlot() {
            return 22;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            player.getOpenInventory().close();
        }
    }

}
