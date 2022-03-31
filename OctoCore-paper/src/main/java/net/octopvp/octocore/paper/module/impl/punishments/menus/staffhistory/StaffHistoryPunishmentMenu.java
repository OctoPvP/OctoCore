package net.octopvp.octocore.paper.module.impl.punishments.menus.staffhistory;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.module.impl.punishments.util.PunishmentType;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.SoundUtil;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.PlayerInfoButton;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class StaffHistoryPunishmentMenu extends PaginatedMenu {
    private final PlayerData playerData;
    private final PunishmentType punishmentType;
    private boolean activeOnly = true;

    @Override
    public String getPagesTitle(Player player) {
        return "&7Checking: " + playerData.getName();
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> slots = new ArrayList<>();

        AtomicInteger order = new AtomicInteger(1);
        PunishHistory.getPunishments(playerData, this.punishmentType, this.activeOnly)
                .stream().sorted(Comparator.comparingLong(PunishHistory::getAddedAt).reversed()).forEach(punishHistory -> {
                    slots.add(new PunishButton(punishHistory, order.getAndIncrement()));
                });

        return slots;
    }

    @Override
    public List<Button> getEveryMenuSlots(Player player) {
        List<Button> slots = new ArrayList<>();

        slots.add(new PlayerInfoButton(playerData, 4));
        slots.add(new ActiveOnlyButton(40));

        slots.add(new Button() {

            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.ARROW).setName("&c&lGo Back!").toItemStack();
            }

            @Override
            public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
                new StaffHistoryMenu(playerData).open(player);
            }

            @Override
            public int getSlot() {
                return 41;
            }

            @Override
            public int[] getSlots() {
                return new int[]{39};
            }
        });
        return slots;
    }

    @AllArgsConstructor
    private class PunishButton extends Button {
        private PunishHistory punishHistory;
        private int order;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(punishHistory.hasExpired() ? Material.BOOK : Material.ENCHANTED_BOOK);
            item.setName(CC.MAIN + "#" + order + " &7(" + CC.SECONDARY + DateUtils.getDate(punishHistory.getAddedAt()) + "&7)");
            item.addLoreLine(CC.SEPARATOR);
            item.addLoreLine(CC.MAIN + "Target&7: " + CC.SECONDARY + punishHistory.getTarget());
            if (punishHistory.getPunishmentType() != PunishmentType.KICK) {
                item.addLoreLine(CC.MAIN + "Duration&7: " + CC.SECONDARY + punishHistory.getNiceDuration());
                item.addLoreLine(CC.MAIN + "Expire&7: " + CC.SECONDARY + punishHistory.getNiceExpire());
            }
            item.addLoreLine(CC.MAIN + "Reason&7: " + CC.SECONDARY + punishHistory.getReason());
            item.addLoreLine(CC.SEPARATOR);
            item.addLoreLine(CC.MAIN + "Permanent&7: " + (punishHistory.isPermanent() ? "&aYes" : "&cNo"));
            item.addLoreLine(CC.MAIN + "Active&7: " + (!punishHistory.hasExpired() ? "&aYes" : "&cNo"));
            item.addLoreLine(CC.MAIN + "Silent&7: " + (punishHistory.isSilent() ? "&aYes" : "&cNo"));
            item.addLoreLine(CC.SEPARATOR);
            item.addLoreLine(CC.SECONDARY + "Click to check " + CC.MAIN + punishHistory.getTarget() + "'s " + CC.SECONDARY + "punishments");
            item.addLoreLine(CC.SEPARATOR);
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            player.closeInventory();
            Tasks.run(() -> player.performCommand("check " + punishHistory.getTarget()));
        }
    }

    @AllArgsConstructor
    private class ActiveOnlyButton extends Button {
        private int slot;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.PAPER);
            item.setName("&aPunishments to show");
            item.addLoreLine(" ");
            if (activeOnly) {
                item.addLoreLine("&7Currently showing");
                item.addLoreLine("&7active punishments only!");
            } else {
                item.addLoreLine("&7Currently showing all");
                item.addLoreLine("&7active/expired punishments!");
            }
            item.addLoreLine(" ");
            item.addLoreLine("&aClick to change!");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            activeOnly = !activeOnly;
            update(player);
            SoundUtil.playSound(player, Sound.ORB_PICKUP);
        }
    }
}
