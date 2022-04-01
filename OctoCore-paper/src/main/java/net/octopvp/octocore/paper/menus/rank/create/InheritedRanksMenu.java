package net.octopvp.octocore.paper.menus.rank.create;

import lombok.SneakyThrows;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.callback.ReturnableTypeCallback;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.objects.builders.RankBuilder;
import net.octopvp.octocore.paper.objects.permissions.Rank;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.SoundUtil;
import net.octopvp.octocore.paper.utils.item.WoolUtils;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InheritedRanksMenu extends PaginatedMenu {
    private final Menu previousMenu;
    private final RankBuilder builder;
    private final ReturnableTypeCallback<RankBuilder> callback;
    private final RankBuilder startBuilder;
    private final Menu prev = this;
    private boolean showOnlyInherited = false;
    private boolean changed = false;
    @SneakyThrows
    public InheritedRanksMenu(Menu previousMenu, RankBuilder builder, ReturnableTypeCallback<RankBuilder> callback) {
        this.previousMenu = previousMenu;
        this.builder = builder;
        this.callback = callback;
        this.startBuilder = builder.clone();
    }

    @Override
    public String getPagesTitle(Player player) {
        return CC.GREEN + "Choose inherited ranks!";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> list = new ArrayList<>();
        for (Rank rank : RankManager.getInstance().getRanks()) {
            if (showOnlyInherited) {
                if (builder.getRank().getInheritedRanks().contains(rank.getRankId()))
                    list.add(new RankButton(rank));
            } else list.add(new RankButton(rank));
        }
        return list;
    }

    @Override
    public List<Button> getEveryMenuSlots(Player player) {
        return null;
    }

    @Override
    public Button getFilterButton() {
        return new FilterButton();
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton() {
            @Override
            public void clicked(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
                previousMenu.open(player);
            }
        };
    }

    @Override
    public void onClose(Player player, InventoryCloseEvent event) {
        super.onClose(player, event);
        /*
        if (event.isClosedByPlayer() && changed) {
            open(player);
            SoundUtil.playError(player);
            player.sendMessage(CC.RED + "You need to save your changes!");
        }
         */
    }

    private class RankButton extends Button {
        private final Rank rankData;

        public RankButton(Rank rank) {
            this.rankData = rank;
        }


        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setName(rankData.getDisplayName());
            item.durability((short) (rankData.isDefaultRank() ? 4 : WoolUtils.convertChatColorToWoolData(rankData.getColor())));
            item.lore(CC.SEPARATOR, CC.AQUA + "Weight" + CC.GRAY + ": " + CC.YELLOW + rankData.getWeight(), CC.AQUA + "Inherited: " + CC.YELLOW + StringUtils.join(rankData.getInheritedRanksName(), ", "), CC.AQUA + "Default: " + CC.YELLOW + rankData.isDefaultRank(),
                    CC.AQUA + "Prefix: " + CC.YELLOW + rankData.getPrefix(), CC.AQUA + "Changeable Color: " + CC.YELLOW + rankData.isChangableMainColor(), CC.AQUA + "Purchasable: " + CC.YELLOW + rankData.isPurchasable(),
                    CC.SEPARATOR,
                    CC.YELLOW + (builder.getRank().getInheritedRanks().contains(rankData.getRankId()) ? CC.RED + "Click to remove inherited rank" : "Click to add inherited rank"));
            return item.build();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            if (builder.getRank().getInheritedRanks().contains(rankData.getRankId())) {
                builder.removeInheritedRank(rankData.getRankId());
            } else {
                builder.addInheritedRank(rankData.getRankId());
            }
            changed = true;
            SoundUtil.playPing(player);
            update(player);
        }
    }

    private class FilterButton extends net.octopvp.octocore.paper.utils.menu.buttons.impl.FilterButton {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder builder = new ItemBuilder(Material.HOPPER).name(CC.GREEN + "Filter");
            if (showOnlyInherited)
                builder.lore(CC.GRAY + "Click to show all ranks");
            else
                builder.lore(CC.GRAY + "Click to show only inherited ranks");
            return builder.build();
        }

        @Override
        public void clicked(Player player, ClickType type, int slot) {
            showOnlyInherited = !showOnlyInherited;
            update(player);
        }
    }
}
