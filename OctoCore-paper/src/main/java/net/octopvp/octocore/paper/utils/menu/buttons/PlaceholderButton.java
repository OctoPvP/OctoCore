package net.octopvp.octocore.paper.utils.menu.buttons;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class PlaceholderButton extends Button{
    private PaginatedMenu menu;
    public PlaceholderButton(PaginatedMenu menu){
        this.menu = menu;
    }
    public PlaceholderButton(){}
    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(Material.STAINED_GLASS_PANE).durability((short)7).name(CC.GRAY).build();
    }

    @Override
    public int getSlot() {
        return 0;
    }

    @Override
    public int[] getSlots() {
        List<Integer> a = new ArrayList<>(); //TODO side border
        IntStream.range(1,9).forEach((i)->a.add(i));
        a.add(43);
        IntStream.range(37,43).forEach((i)-> {
            if (i != 40) {
                if (menu != null ) {
                    if (menu.getFilterButton() != null){
                        if (menu.getFilterButton().getSlot() != i)
                            a.add(i);
                        else return;
                    }else a.add(i); //filter button not set
                } //menu not set
                else a.add(i);
            }
        });
        return a.stream().mapToInt(i -> i).toArray();
    }
}
