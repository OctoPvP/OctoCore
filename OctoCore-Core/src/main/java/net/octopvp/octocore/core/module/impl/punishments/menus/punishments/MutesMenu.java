package net.octopvp.octocore.core.module.impl.punishments.menus.punishments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import dev.octomc.agile.menu.Menu;
import dev.octomc.agile.menu.PaginatedMenu;
import net.octopvp.octocore.common.interfaces.IPunishData;
import net.octopvp.octocore.common.interfaces.IPunishment;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.core.utils.Buttons;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class MutesMenu extends PaginatedMenu<PaginatedGui> {
    private final IPunishData punishData;
    private final Menu<?> parent;

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated().title("Mutes of " + punishData.getName()).rows(6).create();
    }

    @Override
    public Menu<?> getBackMenu() {
        return parent;
    }

    @Override
    public void addStaticButtons() {
        gui.setItem(4, Buttons.playerInfo(punishData.getUniqueId()));
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> items = new ArrayList<>();
        AtomicInteger order = new AtomicInteger(1);
        List<IPunishment> punishments = punishData.getPunishments().stream().sorted(Comparator.comparingLong(IPunishment::getAddedAt).reversed()).filter(punishment -> punishment.getPunishmentType() == PunishmentType.MUTE).collect(Collectors.toList());
        punishments.forEach(punishment -> items.add(BansMenu.punishmentButton(punishment, order.getAndIncrement())));
        return items;
    }
}
