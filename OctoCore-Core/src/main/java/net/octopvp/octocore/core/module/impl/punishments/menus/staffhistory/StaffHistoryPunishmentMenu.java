package net.octopvp.octocore.core.module.impl.punishments.menus.staffhistory;

import lombok.RequiredArgsConstructor;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.guis.PaginatedGui;
import net.octopvp.agile.menu.PaginatedMenu;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.core.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.Buttons;
import net.octopvp.octocore.core.utils.SoundUtil;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class StaffHistoryPunishmentMenu extends PaginatedMenu<PaginatedGui> {
    private final PlayerData playerData;
    private final PunishmentType punishmentType;
    private boolean activeOnly = true;

    @SuppressWarnings("deprecation")
    public GuiItem activeOnlyButton() {
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        if (activeOnly) {
            lore.add(CC.GRAY + "Currently showing");
            lore.add(CC.GRAY + "active punishments only!");
        } else {
            lore.add(CC.GRAY + "Currently showing all");
            lore.add(CC.GRAY + "active/expired punishments!");
        }
        lore.add(" ");
        lore.add(CC.GREEN + "Click to change!");
        return ItemBuilder.from(Material.PAPER)
                .name(CC.GREEN + "Punishments to show")
                .setLore(lore)
                .asGuiItem(event -> {
                    activeOnly = !activeOnly;
                    update((Player) event.getWhoClicked());
                    SoundUtil.playSound((Player) event.getWhoClicked(), Sound.ORB_PICKUP);
                });
    }

    @SuppressWarnings("deprecation")
    public GuiItem punishButton(Punishment punishment, int order) {
        List<String> lore = new ArrayList<>();
        lore.add(CC.SEPARATOR);
        lore.add(CC.MAIN + "Target&7: " + CC.SECONDARY + punishment.getName());
        if (punishment.getPunishmentType() != PunishmentType.KICK) {
            lore.add(CC.MAIN + "Duration&7: " + CC.SECONDARY + punishment.getNiceDuration());
            lore.add(CC.MAIN + "Expire&7: " + CC.SECONDARY + punishment.getNiceExpire());
        }
        lore.add(CC.MAIN + "Reason&7: " + CC.SECONDARY + punishment.getReason());
        lore.add(CC.SEPARATOR);
        lore.add(CC.MAIN + "Permanent&7: " + (punishment.isPermanent() ? "&aYes" : "&cNo"));
        lore.add(CC.MAIN + "Active&7: " + (!punishment.hasExpired() ? "&aYes" : "&cNo"));
        lore.add(CC.MAIN + "Silent&7: " + (punishment.isSilent() ? "&aYes" : "&cNo"));
        lore.add(CC.SEPARATOR);
        lore.add(CC.SECONDARY + "Click to check " + CC.MAIN + punishment.getName() + "'s " + CC.SECONDARY + "punishments");
        lore.add(CC.SEPARATOR);
        return ItemBuilder.from(punishment.hasExpired() ? Material.BOOK : Material.ENCHANTED_BOOK)
                .name(CC.MAIN + "#" + order + " &7(" + CC.SECONDARY + DateUtils.getDate(punishment.getAddedAt()) + "&7)")
                .setLore(lore)
                .asGuiItem(event -> {
                    event.getWhoClicked().closeInventory();
                    Tasks.run(() -> ((Player) event.getWhoClicked()).performCommand("check " + punishment.getName()));
                });
    }


        /*
    @Override
    public String getPagesTitle(Player player) {
        return "&7Checking: " + playerData.getName();
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> slots = new ArrayList<>();

        AtomicInteger order = new AtomicInteger(1);
        playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == this.punishmentType).sorted(Comparator.comparingLong(Punishment::getAddedAt).reversed()).forEach(punishment -> {
            slots.add(new PunishButton(punishment, order.getAndIncrement()));
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


     */

    @Override
    public void addStaticButtons() {
        gui.setItem(4, Buttons.playerInfo(playerData));
        gui.setItem(40, activeOnlyButton());
        GuiItem item = backButton(new StaffHistoryMenu(playerData));
        gui.setItem(41, item);
        gui.setItem(39, item);
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> slots = new ArrayList<>();
        AtomicInteger order = new AtomicInteger(1);
        playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == this.punishmentType).forEach(punishment -> {
            if (activeOnly) {
                if (!punishment.hasExpired()) {
                    slots.add(punishButton(punishment, order.getAndIncrement()));
                }
            } else {
                slots.add(punishButton(punishment, order.getAndIncrement()));
            }
        });
        return slots;
    }

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Checking: " + playerData.getName())
                .rows(6)
                .create();
    }

}
