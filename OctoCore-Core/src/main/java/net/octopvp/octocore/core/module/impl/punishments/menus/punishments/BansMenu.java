package net.octopvp.octocore.core.module.impl.punishments.menus.punishments;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.object.punish.IPunishData;
import net.octopvp.octocore.common.object.punish.IPunishment;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.core.utils.ItemBuilder;
import net.octopvp.octocore.core.utils.item.WoolUtils;
import net.octopvp.octocore.core.utils.menu.buttons.Button;
import net.octopvp.octocore.core.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.core.utils.menu.buttons.impl.PlayerInfoButton;
import net.octopvp.octocore.core.utils.menu.menu.Menu;
import net.octopvp.octocore.core.utils.menu.menu.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class BansMenu extends PaginatedMenu {
    private final IPunishData punishData;
    private final Menu parent;

    @Override
    public String getPagesTitle(Player player) {
        return CC.translate(punishData.getName() + "'s bans");
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
        List<IPunishment> punishments = punishData.getPunishments().stream().sorted(Comparator.comparingLong(IPunishment::getAddedAt).reversed()).filter(punishment -> punishment.getPunishmentType() == PunishmentType.BAN).collect(Collectors.toList());

        punishments.forEach(punishment -> slots.add(new PunishmentButton(punishment, order.getAndIncrement())));

        return slots;
    }

    @AllArgsConstructor
    private class PunishmentButton extends Button {
        private IPunishment punishment;
        private int order;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setDurability(punishment.isActive() ? WoolUtils.convertChatColorToWoolData(ChatColor.GREEN) : WoolUtils.convertChatColorToWoolData(ChatColor.RED));
            item.setName(CC.GREEN + "#" + order + " &7(" + CC.YELLOW + DateUtils.getDate(punishment.getAddedAt()) + "&7)");
            item.addLoreLine(CC.SEPARATOR);
            item.addLoreLine(CC.GREEN + "Added by&7: " + CC.YELLOW + punishment.getAddedByName());
            item.addLoreLine(CC.GREEN + "Duration&7: " + CC.YELLOW + punishment.getNiceDuration());
            item.addLoreLine(CC.GREEN + "Expire&7: " + CC.YELLOW + punishment.getNiceExpire());
            item.addLoreLine(CC.GREEN + "Reason&7: " + CC.YELLOW + punishment.getReason());
            item.addLoreLine("");
            item.addLoreLine(CC.GREEN + "Permanent&7: " + (punishment.isPermanent() ? "&aYes" : "&cNo"));
            item.addLoreLine(CC.GREEN + "Active&7: " + (!punishment.hasExpired() ? "&aYes" : "&cNo"));
            item.addLoreLine(CC.GREEN + "Silent&7: " + (punishment.isSilent() ? "&aYes" : "&cNo"));
            if (!punishment.getRemovedBy().equalsIgnoreCase("")) {
                item.addLoreLine("");
                item.addLoreLine(CC.GREEN + "Removed by&7: " + CC.YELLOW + punishment.getRemovedBy());
                item.addLoreLine(CC.GREEN + "Reason&7: " + CC.YELLOW + punishment.getRemovedFor());
                item.addLoreLine(CC.GREEN + "Date&7: " + CC.YELLOW + DateUtils.getDate(punishment.getWhenRemoved()));
            }
            item.addLoreLine(CC.SEPARATOR);
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 0;
        }
    }
}
