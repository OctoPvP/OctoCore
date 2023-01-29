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
import net.octopvp.octocore.common.interfaces.IPunishData;
import net.octopvp.octocore.common.interfaces.IPunishment;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.utils.Buttons;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class BansMenu extends PaginatedMenu<PaginatedGui> {
    private final IPunishData punishData;
    private final Menu<?> parent;

    @SuppressWarnings("deprecation")
    public static GuiItem punishmentButton(IPunishment punishment, int order) {
        List<String> lore = new ArrayList<>(Arrays.asList(
                CC.SEPARATOR,
                CC.GREEN + "Added by" + CC.GRAY + ": " + CC.YELLOW + punishment.getAddedByName(),
                CC.GREEN + "Duration" + CC.GRAY + ": " + CC.YELLOW + punishment.getNiceDuration(),
                CC.GREEN + "Expire" + CC.GRAY + ": " + CC.YELLOW + punishment.getNiceExpire(),
                CC.GREEN + "Reason" + CC.GRAY + ": " + CC.YELLOW + punishment.getReason(),
                "",
                CC.GREEN + "Permanent" + CC.GRAY + ": " + (punishment.isPermanent() ? CC.GREEN + "Yes" : CC.RED + "No"),
                CC.GREEN + "Active" + CC.GRAY + ": " + (!punishment.hasExpired() ? CC.GREEN + "Yes" : CC.RED + "No"),
                CC.GREEN + "Silent" + CC.GRAY + ": " + (punishment.isSilent() ? CC.GREEN + "Yes" : CC.RED + "No")
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
                .asGuiItem();
    }

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Bans of " + punishData.getName())
                .rows(6)
                .create();
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
        List<IPunishment> punishments = punishData.getPunishments().stream().sorted(Comparator.comparingLong(IPunishment::getAddedAt).reversed()).filter(punishment -> punishment.getPunishmentType() == PunishmentType.BAN).collect(Collectors.toList());
        punishments.forEach(punishment -> items.add(punishmentButton(punishment, order.getAndIncrement())));
        return items;
    }
}
