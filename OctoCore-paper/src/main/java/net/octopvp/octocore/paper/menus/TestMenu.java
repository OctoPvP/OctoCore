package net.octopvp.octocore.paper.menus;

import com.google.common.collect.Lists;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TestMenu extends Menu {
    private boolean a = true;
    private boolean b = true;

    public TestMenu(boolean b) {
        this.a = b;
    }

    @Override
    public List<Button> getButtons(Player player) {
        return Lists.newArrayList(new UpdateButton(), new PlaceholderButton1(), new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.NAME_TAG).name(CC.AQUA + "Test").build();
            }

            @Override
            public int getSlot() {
                return 5;
            }

            @Override
            public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
                super.onClick(player, slot, clickType, event);
                new TestMenu2().open(player);
            }
        });
    }

    @Override
    public String getName(Player player) {
        return "Menu 1";
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton() {
            @Override
            public void clicked(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
                getPrevious().open(player);
            }

            @Override
            public int getSlot() {
                return 18;
            }
        };
    }

    private class UpdateButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder((b ? Material.EMERALD : Material.REDSTONE)).name((b ? CC.GREEN + "Test" : CC.RED + "Test")).build();
        }

        @Override
        public int getSlot() {
            return 4;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            b = !b;
            update(player);
        }
    }

    private class PlaceholderButton1 extends net.octopvp.octocore.paper.utils.menu.buttons.PlaceholderButton {
        @Override
        public int[] getSlots() {
            if (a)
                return genPlaceholderSpots(IntStream.range(0, 26), 4);
            else {
                List<Integer> a = new ArrayList<>();
                IntStream.range(0, 26).forEach(i -> {
                    if (i != 4)
                        a.add(i);
                });
                return a.stream().mapToInt(i -> i).toArray();
            }
        }
    }
}
