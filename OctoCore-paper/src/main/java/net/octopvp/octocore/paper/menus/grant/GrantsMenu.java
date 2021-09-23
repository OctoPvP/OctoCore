package net.octopvp.octocore.paper.menus.grant;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.json.JsonChain;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.permissions.Grant;
import net.octopvp.octocore.paper.objects.permissions.Rank;
import net.octopvp.octocore.paper.utils.DateUtils;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.SoundUtil;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.PlaceholderButton;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.PlayerInfoButton;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GrantsMenu extends PaginatedMenu {
    private final PlayerData targetData;
    public GrantsMenu(PlayerData data){
        this.targetData = data;
    }
    private boolean all = true;
    private Comparator<Grant> GRANT_COMPARATOR = Comparator.comparingLong(Grant::getAddedAt).reversed();
    @Override
    public String getPagesTitle(Player player) {
        return CC.GREEN + targetData.getName() + "'s grants";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        if (!all){
            this.targetData.getGrants().stream().sorted(GRANT_COMPARATOR).filter(grant -> !grant.hasExpired()).forEach(grant -> buttons.add(new GrantEntryButton(grant)));
        }else this.targetData.getGrants().stream().sorted(GRANT_COMPARATOR).forEach(grant -> buttons.add(new GrantEntryButton(grant)));
        if (buttons.isEmpty()){
            buttons.add(new Button() {
                @Override
                public ItemStack getItem(Player player) {
                    return new ItemBuilder(Material.BEDROCK).name(CC.RED + "No grants!").lore(CC.SEPARATOR,"",CC.RED + "This player does not",CC.RED + " have any grants!").build();
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
        return Lists.newArrayList(new PlayerInfoButton(targetData,4),new Placeholder());
    }

    @Override
    public Button getFilterButton() {
        return new FilterButton();
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton() {

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new MainGrantMenu(targetData).open(player);
            }

            @Override
            public int getSlot() {
                return 39;
            }
        };
    }
    private class Placeholder extends PlaceholderButton{
        @Override
        public int[] getSlots() {
            return new int[]{0,1,2,3,5,6,7,8,38,41,42,43};
        }
    }
    private class FilterButton extends net.octopvp.octocore.paper.utils.menu.buttons.impl.FilterButton {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder ib = new ItemBuilder(Material.HOPPER).name(CC.GREEN + "Filter");
            if (all)
                ib.lore(CC.AQUA + "Currently Showing " + CC.U + "ALL" + CC.R + CC.AQUA + " active grants.","",CC.YELLOW + "Click to change to active only!");
            else ib.lore(CC.AQUA + "Currently Showing " + CC.U + "Active Only" + CC.R + CC.AQUA + " grants.","",CC.YELLOW + "Click to change to all!");
            return ib.build();
        }

        @Override
        public void clicked(Player player, ClickType type, int slot) {
            all = !all;
            update(player);
            SoundUtil.playPing(player);
        }
    }
    private static int i = 0;
    @RequiredArgsConstructor
    private class GrantEntryButton extends Button{
        private final Grant grant;
        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder ib = new ItemBuilder(Material.WOOL)
                    .durability(grant.isActive() ? 5 : 14)
                    .name(
                            grant.isActive() ? CC.GREEN : CC.RED +
                                    grant.getRankName());
                    /*.lore(CC.SEPARATOR,CC.AQUA + "Rank: " +
                            grant.getRank().getDisplayName(),
                            CC.AQUA + "Active: " +
                                    (!grant.hasExpired() ? CC.GREEN + "Yes" : CC.RED + "No"))
                     */
            Rank rank = grant.getRank();
            if (rank != null){
                ib.lore( CC.SEPARATOR,
                        CC.AQUA + "Rank: " + grant.getRank().getDisplayName(),
                        CC.AQUA + "Added By: " + CC.YELLOW + grant.getAddedBy(),
                        CC.AQUA + "Added Date: " + CC.YELLOW + DateUtils.getDate(grant.getAddedAt()),
                        CC.AQUA + "Duration: " + CC.YELLOW + (grant.isPermanent() ? "Permanent" : grant.getNiceDuration()),
                        CC.AQUA + "Reason: " + CC.YELLOW + grant.getReason(),
                        CC.AQUA + "Server: " + CC.YELLOW + grant.getServer().getServer(),
                        "",
                        CC.AQUA + "Active: " + (grant.hasExpired() ? CC.RED + "No" : CC.GREEN + "Yes"),
                        CC.AQUA + "Expire: " + CC.YELLOW + grant.getNiceExpire()
                );
                if (grant.getRemovedBy() != null){
                    ib.lore(
                            CC.AQUA + "Removed By: " + CC.YELLOW + grant.getRemovedBy(),
                            CC.AQUA + "Removed At: " + CC.YELLOW + DateUtils.getDate(grant.getRemovedAt()),
                            CC.SEPARATOR
                    );
                }
                if (!grant.hasExpired() && !rank.isDefaultRank()){
                    ib.lore("",CC.YELLOW + "Click to remove this grant.",CC.SEPARATOR);
                }
            }else{
                ib.lore(CC.SEPARATOR,CC.RED + "Rank was deleted!",
                        CC.AQUA + "Rank: " + grant.getRankName(),
                        CC.AQUA + "Added By: " + CC.YELLOW + grant.getAddedBy(),
                        CC.AQUA + "Added Date: " + CC.YELLOW + DateUtils.getDate(grant.getAddedAt()),
                        CC.AQUA + "Duration: " + CC.YELLOW + (grant.isPermanent() ? "Permanent" : grant.getNiceDuration()),
                        CC.AQUA + "Reason: " + CC.YELLOW + grant.getReason(),
                        CC.AQUA + "Server: " + CC.YELLOW + grant.getServer().getServer(),
                        "",
                        CC.AQUA + "Active: " + (grant.hasExpired() ? CC.RED + "No" : CC.GREEN + "Yes"),
                        CC.AQUA + "Expire: " + CC.YELLOW + grant.getNiceExpire(),
                        CC.SEPARATOR
                );
                if (grant.getRemovedBy() != null){
                    ib.lore(
                            CC.AQUA + "Removed By: " + CC.YELLOW + grant.getRemovedBy(),
                            CC.AQUA + "Removed At: " + CC.YELLOW + DateUtils.getDate(grant.getRemovedAt()),
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
        public void onClick(Player player, int slot, ClickType clickType) {
            Rank rank = grant.getRank();
            if (rank != null && rank.isDefaultRank()) return;
            if (grant.hasExpired()) return;
            grant.setActive(false);
            grant.setRemovedBy(player.getName());
            grant.setRemovedAt(System.currentTimeMillis());
            OctoCore.getInstance().getRedisData().write(JedisAction.GRANTS_UPDATE,new JsonChain().addProperty("name",targetData.getName()).addProperty("add",false).addProperty("tochange",OctoCore.getGson().toJson(grant)).get());
            update(player);
        }
    }
}
