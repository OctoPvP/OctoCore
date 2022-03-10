package net.octopvp.octocore.paper.menus.rank.create;

import com.google.common.collect.Lists;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.objects.builders.RankBuilder;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.SoundUtil;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.PlaceholderButton;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class ChoosePermissionInheritedMenu extends Menu {
    private final Consumer<RankBuilder> callback;
    private final Menu instance = this;
    private RankBuilder rankBuilder;

    public ChoosePermissionInheritedMenu(RankBuilder rankBuilder, Consumer<RankBuilder> callback) {
        this.rankBuilder = rankBuilder;
        this.callback = callback;
    }

    @Override
    public List<Button> getButtons(Player player) {
        return Lists.newArrayList(new PlaceHolderButton(), new PermissionsButton(), new InheritedButton(), new DoneButton());
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton() {
            @Override
            public void clicked(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
                callback.accept(rankBuilder);
            }

            @Override
            public int getSlot() {
                return 22;
            }
        };
    }

    @Override
    public String getName(Player player) {
        return CC.GREEN + "Choose an action.";
    }

    private class PermissionsButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.EMERALD).name(CC.AQUA + "Permissions").lore(CC.SEPARATOR, CC.AQUA + "Total Permissions: " + CC.YELLOW + rankBuilder.getRank().getNodes().size(), CC.AQUA + "Total Allowed Permissions: " + CC.YELLOW + rankBuilder.getRank().getAllowedPermissions().size(), CC.AQUA + "Total Negated Permissions: " + CC.YELLOW + rankBuilder.getRank().getNegatedPermissions().size(), CC.SEPARATOR).build();
        }

        @Override
        public int getSlot() {
            return 11;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            new CreateRankManagePermissionsMenu(instance, rankBuilder, (b) -> {
                rankBuilder = b;
                open(player);
                SoundUtil.playPing(player);
            }).open(player);
            SoundUtil.playPing(player);
        }
    }

    private class InheritedButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.ANVIL).name(CC.AQUA + "Inherited").lore(CC.SEPARATOR, CC.AQUA + "Inherited Ranks: " + CC.YELLOW + rankBuilder.getRank().getInheritedRanks().size(), CC.SEPARATOR).build();
        }

        @Override
        public int getSlot() {
            return 15;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            new InheritedRanksMenu(instance, rankBuilder, (b) -> {
                rankBuilder = b;
                open(player);
                SoundUtil.playPing(player);
            }).open(player);
            SoundUtil.playPing(player);
        }
    }

    private class DoneButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.EMERALD_BLOCK).name(CC.GREEN + "Done!").build();
        }

        @Override
        public int getSlot() {
            return 26;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            super.onClick(player, slot, clickType, event);
            SoundUtil.playPing(player);
            callback.accept(rankBuilder);
        }
    }

    public class PlaceHolderButton extends PlaceholderButton {
        @Override
        public int[] getSlots() {
            List<Integer> a = new ArrayList<>();
            IntStream.range(0, 26).forEach((i) -> {
                if (!(i == 11 || i == 15))
                    a.add(i);
            });
            return a.stream().mapToInt(i -> i).toArray();
        }
    }
}
