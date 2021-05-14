package net.octopvp.octocore.paper.menus;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.Button;
import net.octopvp.octocore.paper.utils.menu.pagination.PaginatedMenu;
import net.octopvp.octocore.paper.utils.menu.pagination.TempFilterPaginatedMenu;
import net.octopvp.octocore.paper.utils.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ListMenu extends TempFilterPaginatedMenu {
    @Override
    public String getPrePaginatedTitle(Player player) {
        return CC.GREEN + "Online Players";
    }

    @Override
    public Map<Integer, Button> getFilteredButtons(Player player) {
        HashMap<Integer,Button> buttons = new HashMap<>();
        int i = 0;
        for (Player p : Bukkit.getOnlinePlayers()){
            buttons.put(i,new PlayerButton(p));
        }
        return buttons;
    }


    private class PlayerButton extends Button{
        private Player p;
        public PlayerButton(Player p){
            this.p = p;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            ItemBuilder ib = new ItemBuilder(Material.SKULL_ITEM).name(p.getName());
            if(player.hasPermission(Permission.PUNISH_PLAYER.getNode())) {
                ib.lore(Arrays.asList(CC.SEPARATOR,CC.GREEN + "Client: " + CC.GOLD + "Lunar Client",CC.SEPARATOR,"",CC.YELLOW + CC.B + "Click to punish!"));
            }
            else
                ib.lore(Arrays.asList(CC.SEPARATOR,CC.GREEN + "Client: " + CC.GOLD + "Lunar Client",CC.SEPARATOR,""));
            return ib.toSkullBuilder().withOwner(p.getName()).buildSkull();
        }

        @Override
        public void click(Player player, ClickType clickType) {
            super.click(player, clickType);
            player.sendMessage(CC.GREEN + "Click");
            if(player.hasPermission(Permission.PUNISH_PLAYER.getNode())){
                player.chat("/punish " + p.getName());
            }
        }
    }
}
