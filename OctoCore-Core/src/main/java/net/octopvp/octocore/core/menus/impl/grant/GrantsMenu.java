package net.octopvp.octocore.core.menus.impl.grant;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.database.redis.packets.other.GrantsUpdatePacket;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.objects.permissions.Grant;
import net.octopvp.octocore.core.objects.permissions.Rank;
import net.octopvp.octocore.core.utils.ItemBuilder;
import net.octopvp.octocore.core.utils.SoundUtil;
import net.octopvp.octocore.core.utils.menu.buttons.Button;
import net.octopvp.octocore.core.utils.menu.buttons.PlaceholderButton;
import net.octopvp.octocore.core.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.core.utils.menu.buttons.impl.PlayerInfoButton;
import net.octopvp.octocore.core.utils.menu.menu.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GrantsMenu extends PaginatedMenu {
    private static int i = 0;
    private final PlayerData targetData;
    private final Comparator<Grant> GRANT_COMPARATOR = Comparator.comparingLong(Grant::getAddedAt).reversed();
    private boolean all = true;

    public GrantsMenu(PlayerData data) {
        this.targetData = data;
    }

    @Override
    public String getPagesTitle(Player player) {
        return CC.GREEN + targetData.getName() + "'s grants";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        if (!all) {
            this.targetData.getGrants().stream().sorted(GRANT_COMPARATOR).filter(grant -> !grant.hasExpired()).forEach(grant -> buttons.add(new GrantEntryButton(grant)));
        } else
            this.targetData.getGrants().stream().sorted(GRANT_COMPARATOR).forEach(grant -> buttons.add(new GrantEntryButton(grant)));
        if (buttons.isEmpty()) {
            buttons.add(new Button() {
                @Override
                public ItemStack getItem(Player player) {
                    return new ItemBuilder(Material.BEDROCK).name(CC.RED + "No grants!").lore("", CC.RED + "This player does not", CC.RED + " have any grants!").build();
                }

                @Override
                public int getSlot() {
                    return 0;
                }
            });
        }
        return buttons;
    }

    @Override
    public List<Button> getEveryMenuSlots(Player player) {
        return Lists.newArrayList(new PlayerInfoButton(targetData, 4), new Placeholder());
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
                new MainGrantMenu(targetData).open(player);
            }

            @Override
            public int getSlot() {
                return 39;
            }
        };
    }

    private class Placeholder extends PlaceholderButton {
        @Override
        public int[] getSlots() {
            return new int[]{0, 1, 2, 3, 5, 6, 7, 8, 38, 41, 42, 43};
        }
    }

    private class FilterButton extends net.octopvp.octocore.core.utils.menu.buttons.impl.FilterButton {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder ib = new ItemBuilder(Material.HOPPER).name(CC.GREEN + "Filter");
            if (all)
                ib.lore(CC.AQUA + "Currently Showing " + CC.U + "ALL" + CC.R + CC.AQUA + " active grants.", "", CC.YELLOW + "Click to change to active only!");
            else
                ib.lore(CC.AQUA + "Currently Showing " + CC.U + "Active Only" + CC.R + CC.AQUA + " grants.", "", CC.YELLOW + "Click to change to all!");
            return ib.build();
        }

        @Override
        public void clicked(Player player, ClickType type, int slot) {
            all = !all;
            update(player);
            SoundUtil.playPing(player);
        }
    }

    @RequiredArgsConstructor
    private class GrantEntryButton extends Button {
        private final Grant grant;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder ib = new ItemBuilder(Material.WOOL)
                    .durability(grant.isActive() ? 5 : 14)
                    .name(
                            grant.isActive() ? CC.GREEN + grant.getRankName() : CC.RED +
                                    grant.getRankName());
                    /*.lore(CC.SEPARATOR,CC.AQUA + "Rank: " +
                            grant.getRank().getDisplayName(),
                            CC.AQUA + "Active: " +
                                    (!grant.hasExpired() ? CC.GREEN + "Yes" : CC.RED + "No"))
                     */
            Rank rank = grant.getRank();
            if (rank != null) {
                ib.lore(CC.SEPARATOR,
                        CC.AQUA + "Rank&7: " + grant.getRank().getDisplayName(),
                        CC.AQUA + "Added By&7: " + CC.YELLOW + grant.getAddedBy(),
                        CC.AQUA + "Added Date&7: " + CC.YELLOW + DateUtils.getDate(grant.getAddedAt()),
                        CC.AQUA + "Duration&7: " + CC.YELLOW + (grant.isPermanent() ? "Permanent" : grant.getNiceDuration()),
                        CC.AQUA + "Reason&7: " + CC.YELLOW + grant.getReason(),
                        CC.AQUA + "Server&7: " + CC.YELLOW + grant.getServer().getServer(),
                        "",
                        CC.AQUA + "Active&7: " + (grant.hasExpired() ? CC.RED + "No" : CC.GREEN + "Yes"),
                        CC.AQUA + "Expires&7: " + CC.YELLOW + grant.getNiceExpire()
                );
                if (grant.getRemovedBy() != null) {
                    ib.lore(
                            CC.AQUA + "Removed By&7: " + CC.YELLOW + grant.getRemovedBy(),
                            CC.AQUA + "Removed At&7: " + CC.YELLOW + DateUtils.getDate(grant.getRemovedAt())
                    );
                }
                if (!grant.hasExpired() && !rank.isDefaultRank()) {
                    ib.lore("", CC.YELLOW + "Click to remove this grant.");
                }
                ib.lore(CC.SEPARATOR);
            } else {
                ib.lore(CC.SEPARATOR, CC.RED + "Rank was deleted!",
                        CC.AQUA + "Rank&7: " + grant.getRankName(),
                        CC.AQUA + "Added By&7: " + CC.YELLOW + grant.getAddedBy(),
                        CC.AQUA + "Added Date&7: " + CC.YELLOW + DateUtils.getDate(grant.getAddedAt()),
                        CC.AQUA + "Duration&7 " + CC.YELLOW + (grant.isPermanent() ? "Permanent" : grant.getNiceDuration()),
                        CC.AQUA + "Reason&7 " + CC.YELLOW + grant.getReason(),
                        CC.AQUA + "Server&7 " + CC.YELLOW + grant.getServer().getServer(),
                        "",
                        CC.AQUA + "Active&7 " + (grant.hasExpired() ? CC.RED + "No" : CC.GREEN + "Yes"),
                        CC.AQUA + "Expires&7 " + CC.YELLOW + grant.getNiceExpire(),
                        CC.SEPARATOR
                );
                if (grant.getRemovedBy() != null) {
                    ib.lore(
                            CC.AQUA + "Removed By&7 " + CC.YELLOW + grant.getRemovedBy(),
                            CC.AQUA + "Removed At&7 " + CC.YELLOW + DateUtils.getDate(grant.getRemovedAt()),
                            CC.SEPARATOR
                    );
                }
            }
            return ib.build();
        }

        @Override
        public int getSlot() {
            return i++;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            Rank rank = grant.getRank();
            if (rank != null && rank.isDefaultRank()) return;
            if (grant.hasExpired()) return;
            grant.setActive(false);
            grant.setRemovedBy(player.getName());
            grant.setRemovedAt(System.currentTimeMillis());
            if (!targetData.isOnline()) {
                targetData.save();
            }
            new GrantsUpdatePacket(targetData.getName(), OctoCore.getGson().toJson(grant), false).send();
            update(player);
        }
    }
}
