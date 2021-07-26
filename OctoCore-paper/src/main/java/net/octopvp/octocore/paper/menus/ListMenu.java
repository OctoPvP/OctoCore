package net.octopvp.octocore.paper.menus;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.conversations.tag.FilterConversation;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.common.object.Permission;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListMenu extends PaginatedMenu {
    private String filter;
    private boolean filtered;
    public ListMenu(String filter){
        this.filter = filter;
        filtered = true;
    }
    public ListMenu(){}
    @Override
    public String getPagesTitle(Player player) {
        return CC.GREEN + "Online Players";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> buttonList = new ArrayList<>();
        for (int j = 0; j < 200; j++) {
            buttonList.add(new PlayerButton(player));
        }
        return buttonList;
    }

    @Override
    public List<Button> getEveryMenuSlots(Player player) {
        return null;
    }
    private static int i = 0;
    private static int a = 0;
    public class PlayerButton extends Button{
        private Player p;
        public PlayerButton(Player pl){
            p = pl;
        }

        @Override
        public ItemStack getItem(Player player) {
            a++;
            ItemBuilder ib = new ItemBuilder(Material.SKULL_ITEM).name(p.getName());
            ib.lore(a + "");
            if(player.hasPermission(Permission.PUNISH_PLAYER.getNode())) {
                ib.lore(Arrays.asList(CC.SEPARATOR,CC.GREEN + "Client: " + CC.GOLD + "Lunar Client",CC.SEPARATOR,"",CC.YELLOW + CC.B + "Click to punish!"));
            }
            else ib.lore(Arrays.asList(CC.SEPARATOR,CC.GREEN + "Client: " + CC.GOLD + "Lunar Client",CC.SEPARATOR,""));
            return ib.toSkullBuilder().withOwner(p.getName()).buildSkull();        }

        @Override
        public int getSlot() {
            i++;
            return i;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            super.onClick(player, slot, clickType);
            player.chat("/punish " + p.getName());
        }
    }


    public class FilterPlayersButton extends Button {
        private boolean filtered1 = false;
        public FilterPlayersButton(boolean f){
            this.filtered1 = f;
        }

        @Override
        public ItemStack getItem(Player player) {
            if (filtered1)
                return new ItemBuilder(Material.HOPPER).name(CC.AQUA + "Filter").lore(CC.RED + "Click to remove the current filter").build();
            return new ItemBuilder(Material.HOPPER).name(CC.AQUA + "Filter").lore(CC.YELLOW + "Click to add a filter").build();
        }

        @Override
        public int getSlot() {
            return 37;
        }

        @Override
        public void onClick(Player player, int slot,ClickType clickType) {
            if (filtered1){
                player.closeInventory();
                new ListMenu().open(player);
                return;
            }
            player.getOpenInventory().close();
            OctoCore.getConversationFactory().withFirstPrompt(new FilterConversation((s)->{
                if (s.equalsIgnoreCase("cancel") || s.equalsIgnoreCase("exit"))
                    new ListMenu().open(player);
                else new ListMenu(s).open(player);
            })).withLocalEcho(false).buildConversation(player).begin();
        }
    }
}
