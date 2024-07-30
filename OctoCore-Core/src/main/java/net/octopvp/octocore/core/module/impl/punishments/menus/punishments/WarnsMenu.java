package net.octopvp.octocore.core.module.impl.punishments.menus.punishments;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import dev.octomc.agile.menu.Menu;
import dev.octomc.agile.menu.PaginatedMenu;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.interfaces.IPunishData;
import net.octopvp.octocore.common.interfaces.IPunishment;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.utils.Buttons;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
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
    public void addStaticButtons() {
        gui.setItem(4, Buttons.playerInfo(punishData.getUniqueId()));
    }

    @SuppressWarnings("deprecation")
    public GuiItem punishmentButton(IPunishment punishment, int order) {
        List<String> lore = PunishmentMenuCommons.addPunishmentLore(punishment);
        return ItemBuilder.from(!punishment.hasExpired() ? XMaterial.LIME_WOOL : (punishment.isManuallyRemoved() ? XMaterial.ORANGE_WOOL : XMaterial.RED_WOOL))
                .name(CC.GREEN + "#" + order + " " + CC.GRAY + "(" + CC.YELLOW + OctoCoreCommon.DATE_FORMAT.format(new Date(punishment.getAddedAt())) + CC.GRAY + ")")
                .setLore(lore)
                .asGuiItem(event -> {
                    if (true) return;
                    if (!punishment.isManuallyRemoved()) { // TODO: make sure this works as intended, original: !punishment.getRemovedBy().equals("")
                        return;
                    }
                    punishment.setActive(false);
                    punishment.setLast(false);
                    punishment.setRemovedBy(event.getWhoClicked().getName());
                    punishment.setRemovedFor("Not entered.");
                    punishment.setRemovedSilent(false);
                    punishment.setWhenRemoved(System.currentTimeMillis());

                    open((Player) event.getWhoClicked());

                    Tasks.runAsync(punishment::save);
                });
    }

    @Override
    public Menu<?> getBackMenu() {
        return parent;
    }


}
