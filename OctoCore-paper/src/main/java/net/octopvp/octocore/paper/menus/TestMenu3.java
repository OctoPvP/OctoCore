package net.octopvp.octocore.paper.menus;

import com.google.common.collect.Lists;
import net.octopvp.octocore.paper.menus.TestMenu;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TestMenu3 extends Menu {
    @Override
    public List<Button> getButtons(Player player) {
        return Lists.newArrayList(new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.BRICK).name("Test lol").build();
            }

            @Override
            public int getSlot() {
                return 0;
            }

            @Override
            public void onClick(Player player, int slot, ClickType clickType) {
                new TestMenu(true).open(player);
            }
        });
    }

    @Override
    public String getName(Player player) {
        return "Menu 3";
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton() {
            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                getPrevious().open(player);
            }
        };
    }
}
