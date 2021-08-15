package net.octopvp.octocore.paper.menus;

import com.google.common.collect.Lists;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.TestMenu3;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class TestMenu2 extends Menu {
    @Override
    public List<Button> getButtons(Player player) {
        return Lists.newArrayList(new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.DIRT).name(CC.GREEN + "Test").build();
            }

            @Override
            public int getSlot() {
                return 1;
            }

            @Override
            public void onClick(Player player, int slot, ClickType clickType) {
                new TestMenu3().open(player);
            }
        });
    }

    @Override
    public String getName(Player player) {
        return "Menu 2";
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
