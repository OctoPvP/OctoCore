package net.octopvp.octocore.core.menus.grant;

import com.cryptomorin.xseries.XMaterial;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.guis.PaginatedGui;
import net.octopvp.agile.menu.Menu;
import net.octopvp.agile.menu.PaginatedMenu;
import net.octopvp.octocore.common.object.permissions.Grant;
import net.octopvp.octocore.common.object.permissions.Rank;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.core.database.redis.packets.other.grant.RemoveGrantPacket;
import net.octopvp.octocore.core.database.redis.packets.staff.AdminAlertPacket;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.Buttons;
import net.octopvp.octocore.core.utils.SoundUtil;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GrantsMenu extends PaginatedMenu<PaginatedGui> {
    private static final int i = 0;
    private final PlayerData targetData;
    private final Comparator<Grant> GRANT_COMPARATOR = Comparator.comparingLong(Grant::getAddedAt).reversed();
    private boolean all = true;

    public GrantsMenu(PlayerData data) {
        this.targetData = data;
    }

    /*
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
     */
    public GuiItem grantEntryButton(final Grant grant) {
        List<String> lore = new ArrayList<>(Arrays.asList(
                CC.SEPARATOR,
                CC.AQUA + "Rank" + CC.GRAY + ": " + grant.getRank().getDisplayName(),
                CC.AQUA + "Added By" + CC.GRAY + ": " + CC.YELLOW + grant.getAddedBy(),
                CC.AQUA + "Added Date" + CC.GRAY + ": " + CC.YELLOW + DateUtils.getDate(grant.getAddedAt()),
                CC.AQUA + "Duration" + CC.GRAY + ": " + CC.YELLOW + (grant.isPermanent() ? "Permanent" : grant.getNiceDuration()),
                CC.AQUA + "Reason" + CC.GRAY + ": " + CC.YELLOW + grant.getReason(),
                CC.AQUA + "Server" + CC.GRAY + ": " + CC.YELLOW + grant.getServer().getServer()
        ));

        if (grant.isManuallyRemoved()) {
            lore.add(CC.AQUA + "Removed By" + CC.GRAY + ": " + CC.YELLOW + grant.getRemovedBy());
            lore.add(CC.AQUA + "Removed Date" + CC.GRAY + ": " + CC.YELLOW + DateUtils.getDate(grant.getRemovedAt()));
            lore.add("");
        }

        lore.addAll(Arrays.asList("",
                CC.AQUA + "Active" + CC.GRAY + ": " + (grant.hasExpired() ? CC.RED + "No" : CC.GREEN + "Yes"),
                CC.AQUA + "Expires" + CC.GRAY + ": " + CC.YELLOW + grant.getNiceExpire()));
        if (!grant.isPermanent())
            lore.add(CC.AQUA + "Expiry Date" + CC.GRAY + ": " + CC.YELLOW + grant.getExpireDate());
        lore.add(CC.SEPARATOR);

        if (grant.hasExpired()) {
            lore.add("");
            if (grant.isManuallyRemoved())
                lore.add(CC.RED + "This grant was manually removed!");
            else
                lore.add(CC.RED + "This grant has expired!");
        } else {
            lore.add("");
            lore.add(CC.YELLOW + "Click to revoke this grant!");
        }
        return ItemBuilder.from(grant.isActive() ? XMaterial.LIME_WOOL : XMaterial.RED_WOOL)
                .name(grant.isActive() ? CC.GREEN + grant.getRankName() : CC.RED + grant.getRankName())
                .setLore(lore)
                .asGuiItem(event -> {
                    Rank rank = grant.getRank();
                    if (rank != null && rank.isDefaultRank()) return;
                    if (grant.hasExpired()) return;
                    grant.setActive(false);
                    grant.setRemovedBy(event.getWhoClicked().getName());
                    grant.setRemovedAt(System.currentTimeMillis());
                    grant.setRemovedByUUID(event.getWhoClicked().getUniqueId());
                    if (!targetData.isOnline()) { // they are not on the server, so we'll save for them
                        targetData.save();
                    }
                    new AdminAlertPacket(Lang.GRANT_REVOKE_ADMIN_ALERT.getMsg(
                            event.getWhoClicked().getName(),
                            targetData.getName(),
                            grant.getRank().getDisplayName()
                    )).send();
                    new RemoveGrantPacket(targetData.getName(), targetData.getUniqueId(), grant.getId()).send();
                    event.getWhoClicked().sendMessage(CC.GREEN + "You have revoked " + CC.YELLOW + grant.getRankName() + CC.GREEN + " from " + CC.YELLOW + targetData.getName() + CC.GREEN + "!");
                    event.getWhoClicked().closeInventory();
                    open((Player) event.getWhoClicked());
                });
    }

    @Override
    public GuiItem getFilterButton() {
        return ItemBuilder.from(XMaterial.HOPPER)
                .name(CC.GREEN + "Filter")
                .lore(all ? CC.AQUA + "Currently Showing " + CC.U + "ALL" + CC.R + CC.AQUA + " active grants." : CC.AQUA + "Currently Showing " + CC.U + "Active Only" + CC.R + CC.AQUA + " grants.")
                .lore("", CC.YELLOW + "Click to change to " + (all ? "active only" : "all") + "!")
                .asGuiItem(event -> {
                    all = !all;
                    open((Player) event.getWhoClicked());
                    SoundUtil.playPing((Player) event.getWhoClicked());
                });
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> items = new ArrayList<>();
        if (!all) {
            this.targetData.getGrants().stream().sorted(GRANT_COMPARATOR).filter(grant -> !grant.hasExpired()).forEach(grant -> items.add(grantEntryButton(grant)));
        } else
            this.targetData.getGrants().stream().sorted(GRANT_COMPARATOR).forEach(grant -> items.add(grantEntryButton(grant)));
        if (items.isEmpty()) {
            items.add(ItemBuilder.from(XMaterial.BEDROCK)
                    .name(CC.RED + "No grants!")
                    .lore("", CC.RED + "This player does not", CC.RED + " have any grants!")
                    .asGuiItem());
        }
        return items;
    }

    @Override
    public Menu<?> getBackMenu() {
        return new MainGrantMenu(targetData);
    }

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title(targetData.getName() + "'s grants")
                .rows(6)
                .create();
    }

    @Override
    public void addStaticButtons() {
        gui.setItem(4, Buttons.playerInfo(targetData));
    }
}
