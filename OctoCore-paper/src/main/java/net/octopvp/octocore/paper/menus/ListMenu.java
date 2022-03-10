package net.octopvp.octocore.paper.menus;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.conversations.tag.FilterConversation;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListMenu extends PaginatedMenu {
    private String filter;
    private boolean filtered;
    private int i = 0;
    private int a = 0;

    public ListMenu(String filter) {
        this.filter = filter;
        filtered = true;
    }

    public ListMenu() {
    }

    public ListMenu(String[] args) {
        //setShowPageNumbersInTitle(args.length > 0);
    }

    @Override
    public String getPagesTitle(Player player) {
        return CC.GREEN + "Online Players";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> buttonList = new ArrayList<>();
        for (int j = 0; j < 100; j++) {
            buttonList.add(new PlayerButton(player));
        }
        return buttonList;
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton() {
            @Override
            public void clicked(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
                player.closeInventory();
            }
        };
    }

    @Override
    public void onOpen(Player player) {
        //Logger.debug(MenuManager.getOpenedMenus().put(player.getUniqueId(), this));
    }

    @Override
    public void onClose(Player player) {
        //Logger.debug(MenuManager.getOpenedMenus().remove(player.getUniqueId()));
    }

    @Override
    public List<Button> getToolbarButtons() {
        if (true)
            return null;
        return Arrays.asList(new Button() {

            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.DIAMOND_SWORD).name("Test").build();
            }

            @Override
            public int getSlot() {
                return 0;
            }

            @Override
            public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
                player.sendMessage("asdf");
            }
        });
    }

    public class PlayerButton extends Button {
        private final Player p;

        public PlayerButton(Player pl) {
            p = pl;
        }

        @Override
        public ItemStack getItem(Player player) {
            a++;

            ItemBuilder ib = new ItemBuilder(Material.SKULL_ITEM).name(p.getName());
            ib.lore(a + ""); //TODO client
            ib.lore(Arrays.asList(CC.SEPARATOR, CC.GREEN + "Client: " + CC.GOLD + "Lunar Client", CC.SEPARATOR, "", CC.YELLOW + CC.B + "Click to view info!"));

            return ib.toSkullBuilder().withOwner(p.getName()).buildSkull();
        }

        @Override
        public int getSlot() {
            i++;
            return i;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            super.onClick(player, slot, clickType, event);
            player.chat("/punish " + p.getName());
        }
    }


    public class FilterPlayersButton extends Button {
        private boolean filtered1 = false;

        public FilterPlayersButton(boolean f) {
            this.filtered1 = f;
        }

        @Override
        public ItemStack getItem(Player player) {
            if (filtered1)
                return new ItemBuilder(Material.HOPPER).name(CC.AQUA + "Filter").lore(CC.YELLOW + "Click to remove the current filter").build();
            return new ItemBuilder(Material.HOPPER).name(CC.AQUA + "Filter").lore(CC.YELLOW + "Click to add a filter").build();
        }

        @Override
        public int getSlot() {
            return 37;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            if (filtered1) {
                player.closeInventory();
                new ListMenu().open(player);
                return;
            }
            player.getOpenInventory().close();
            OctoCore.getConversationFactory().withFirstPrompt(new FilterConversation((s) -> {
                if (s.equalsIgnoreCase("cancel") || s.equalsIgnoreCase("exit"))
                    new ListMenu().open(player);
                else new ListMenu(s).open(player);
            })).withLocalEcho(false).buildConversation(player).begin();
        }
    }
}
