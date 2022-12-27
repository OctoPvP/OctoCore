package net.octopvp.octocore.core.module.impl.punishments.menus.punishments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.guis.PaginatedGui;
import net.octopvp.agile.menu.Menu;
import net.octopvp.agile.menu.PaginatedMenu;
import net.octopvp.agile.util.XMaterial;
import net.octopvp.octocore.common.object.punish.IPunishData;
import net.octopvp.octocore.common.object.punish.IPunishment;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.utils.Buttons;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class WarnsMenu extends PaginatedMenu<PaginatedGui> {
    private final IPunishData punishData;
    private final Menu<?> parent;

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Warns of " + punishData.getName())
                .rows(6)
                .create();
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> items = new ArrayList<>();
        AtomicInteger order = new AtomicInteger(1);
        List<IPunishment> punishments = punishData.getPunishments().stream().sorted(Comparator.comparingLong(IPunishment::getAddedAt).reversed()).filter(punishment -> punishment.getPunishmentType() == PunishmentType.WARN).collect(Collectors.toList());
        punishments.forEach(punishment -> items.add(punishmentButton(punishment, order.getAndIncrement())));
        return items;
    }

    @Override
    public void populateGui(PaginatedGui gui, Player player) {
        super.populateGui(gui, player);
        gui.setItem(4, Buttons.playerInfo(punishData.getUniqueId()));
    }

    @SuppressWarnings("deprecation")
    public GuiItem punishmentButton(IPunishment punishment, int order) {
        List<String> lore = new ArrayList<>(Arrays.asList(
                CC.SEPARATOR,
                CC.GREEN + "Added by" + CC.GRAY + ": " + CC.YELLOW + punishment.getAddedByName(),
                CC.GREEN + "Duration" + CC.GRAY + ": " + CC.YELLOW + punishment.getNiceDuration(),
                CC.GREEN + "Expire" + CC.GRAY + ": " + CC.YELLOW + punishment.getNiceExpire(),
                CC.GREEN + "Reason" + CC.GRAY + ": " + CC.YELLOW + punishment.getReason(),
                "",
                CC.GREEN + "Permanent" + CC.GRAY + ": " + (punishment.isPermanent() ? "&aYes" : "&cNo"),
                CC.GREEN + "Active" + CC.GRAY + ": " + (!punishment.hasExpired() ? "&aYes" : "&cNo"),
                CC.GREEN + "Silent" + CC.GRAY + ": " + (punishment.isSilent() ? "&aYes" : "&cNo")
        ));
        if (!punishment.getRemovedBy().equals("")) {
            lore.addAll(Arrays.asList(
                    "",
                    CC.GREEN + "Removed by" + CC.GRAY + ": " + CC.YELLOW + punishment.getRemovedBy(),
                    CC.GREEN + "Remove Reason" + CC.GRAY + ": " + CC.YELLOW + punishment.getRemovedFor(),
                    CC.GREEN + "Date" + CC.GRAY + ": " + CC.YELLOW + punishment.getWhenRemoved()
            ));
        }
        lore.add(CC.SEPARATOR);
        return ItemBuilder.from(punishment.isActive() ? XMaterial.GREEN_WOOL : XMaterial.RED_WOOL)
                .name(CC.GREEN + "#" + order + " " + CC.GRAY + "(" + CC.YELLOW + punishment.getAddedAt() + CC.GRAY + ")")
                .setLore(lore)
                .asGuiItem(event -> {
                    if (!punishment.getRemovedBy().equals("")) {
                        return;
                    }
                    punishment.setActive(false);
                    punishment.setLast(false);
                    punishment.setRemovedBy(event.getWhoClicked().getName());
                    punishment.setRemovedFor("Not entered.");
                    punishment.setRemovedSilent(false);
                    punishment.setWhenRemoved(System.currentTimeMillis());

                    open((Player) event.getWhoClicked());

                    Tasks.runAsync(() -> {
                        punishment.save(true);
                    });
                });
    }

    @Override
    public Menu<?> getBackMenu() {
        return parent;
    }


}
