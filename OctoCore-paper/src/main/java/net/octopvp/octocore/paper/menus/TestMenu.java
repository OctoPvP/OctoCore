package net.octopvp.octocore.paper.menus;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.PlaceholderButton;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TestMenu extends Menu {
    private boolean a = true;
    public TestMenu(boolean b){
        this.a = b;
    }
    private boolean b = true;
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
        public void onClick(Player player, int slot, ClickType clickType) {
            b = !b;
            update(player);
        }
    }
    @Override
    public List<Button> getButtons(Player player) {
        return Lists.newArrayList(new UpdateButton(),new PlaceholderButton1());
    }
    public class PlaceholderButton1 extends net.octopvp.octocore.paper.utils.menu.buttons.PlaceholderButton{
        @Override
        public int[] getSlots() {
            if (a)
                return genPlaceholderSpots(IntStream.range(0,26),4);
            else{
                List<Integer> a = new ArrayList<>();
                IntStream.range(0,26).forEach(i ->{
                    if (i != 4)
                        a.add(i);
                });
                return a.stream().mapToInt(i ->i).toArray();
            }
        }
    }

    @Override
    public String getName(Player player) {
        return "Test";
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton() {
            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.sendMessage("a");
            }

            @Override
            public int getSlot() {
                return 18;
            }
        };
    }
}
