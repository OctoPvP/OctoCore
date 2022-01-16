package net.octopvp.octocore.paper.utils.menu.buttons;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlaceholderButton extends Button {

    public PlaceholderButton(){}
    @Override
    public ItemStack getItem(Player player) {
        return new ItemBuilder(Material.STAINED_GLASS_PANE).durability((short)7).name(CC.GRAY).build();
    }

    @Override
    public int getSlot() {
        return -1;
    }

    @Override
    public int[] getSlots() {

        /*
        a.add(43);
        IntStream.range(37,43).forEach((i)-> {
            if (i != 40) {
                if (menu != null){
                    if (menu.getBackButton(player) == null)
                        a.add(i);
                    if (i != 49)
                        a.add(i);
                }
                else a.add(i);
            }
        });
         */
        return null;
    }

}