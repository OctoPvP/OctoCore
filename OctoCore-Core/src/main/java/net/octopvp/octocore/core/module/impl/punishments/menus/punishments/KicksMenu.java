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
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.core.utils.Buttons;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class KicksMenu extends PaginatedMenu<PaginatedGui> {
    private final IPunishData punishData;
    private final Menu<?> parent;

    public GuiItem punishmentButton(IPunishment punishment, int order) {
        // return ItemBuilder.from(Material.WOOL)
        //        .durability(punishment.isActive() ? WoolUtils.convertChatColorToWoolData(ChatColor.GREEN) : WoolUtils.convertChatColorToWoolData(ChatColor.RED))
        return ItemBuilder.from(punishment.isActive() ? XMaterial.LIME_WOOL : XMaterial.RED_WOOL)
                .name(CC.GREEN + "#" + order + " " + CC.GRAY + "(" + CC.YELLOW + DateUtils.getDate(punishment.getAddedAt()) + CC.GRAY + ")")
                .lore(CC.SEPARATOR,
                        CC.GREEN + "Added by" + CC.GRAY + ": " + CC.YELLOW + punishment.getAddedByName(),
                        CC.GREEN + "Reason" + CC.GRAY + ": " + CC.YELLOW + punishment.getReason(),
                        CC.GREEN + "Silent" + CC.GRAY + ": " + (punishment.isSilent() ? "&aYes" : "&cNo"),
                        CC.SEPARATOR
                )
                .asGuiItem();
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> items = new ArrayList<>();
        AtomicInteger order = new AtomicInteger(1);
        List<IPunishment> punishments = punishData.getPunishments().stream().sorted((o1, o2) -> Long.compare(o2.getAddedAt(), o1.getAddedAt())).filter(punishment -> punishment.getPunishmentType() == PunishmentType.KICK).collect(Collectors.toList());
        punishments.forEach(punishment -> items.add(punishmentButton(punishment, order.getAndIncrement())));
        return items;
    }

    @Override
    public void addStaticButtons() {
        gui.setItem(4, Buttons.playerInfo(punishData.getUniqueId()));
    }

    @Override
    public Menu<?> getBackMenu() {
        return parent;
    }

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Kicks for " + punishData.getName())
                .rows(6)
                .create();
    }
}
