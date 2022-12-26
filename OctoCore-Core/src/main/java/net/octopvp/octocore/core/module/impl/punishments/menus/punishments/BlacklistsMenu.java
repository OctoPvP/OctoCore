package net.octopvp.octocore.core.module.impl.punishments.menus.punishments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.guis.PaginatedGui;
import net.octopvp.agile.menu.Menu;
import net.octopvp.agile.menu.PaginatedMenu;
import net.octopvp.octocore.common.object.punish.IPunishData;
import net.octopvp.octocore.common.object.punish.IPunishment;
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
public class BlacklistsMenu extends PaginatedMenu<PaginatedGui> {
    private final IPunishData punishData;
    private final Menu<?> parent;

    /*


    @Override
    public String getPagesTitle(Player player) {
        return CC.translate(punishData.getName() + "'s blacklists");
    }

    @Override
    public List<Button> getEveryMenuSlots(Player player) {
        List<Button> slots = new ArrayList<>();

        slots.add(new PlayerInfoButton(punishData.getUniqueId(), 4));

        return slots;
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton.SuppliedBackButton(parent);
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> slots = new ArrayList<>();

        AtomicInteger order = new AtomicInteger(1);
        List<IPunishment> punishments = punishData.getPunishments().stream().sorted(Comparator.comparingLong(IPunishment::getAddedAt).reversed()).filter(punishment -> punishment.getPunishmentType() == PunishmentType.BLACKLIST).collect(Collectors.toList());

        punishments.forEach(punishment -> slots.add(new PunishmentButton(punishment, order.getAndIncrement())));

        return slots;
    }
     */

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> items = new ArrayList<>();
        AtomicInteger order = new AtomicInteger(1);
        List<IPunishment> punishments = punishData.getPunishments().stream().sorted(Comparator.comparingLong(IPunishment::getAddedAt).reversed()).filter(punishment -> punishment.getPunishmentType() == PunishmentType.BLACKLIST).collect(Collectors.toList());
        punishments.forEach(punishment -> items.add(BansMenu.punishmentButton(punishment, order.getAndIncrement())));
        return items;
    }

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Blacklists for " + punishData.getName())
                .rows(6)
                .create();
    }

    @Override
    public Menu<?> getBackMenu() {
        return parent;
    }

    @Override
    public void populateGui(PaginatedGui gui, Player player) {
        super.populateGui(gui, player);
        gui.setItem(4, Buttons.playerInfo(punishData.getUniqueId()));
    }
}
