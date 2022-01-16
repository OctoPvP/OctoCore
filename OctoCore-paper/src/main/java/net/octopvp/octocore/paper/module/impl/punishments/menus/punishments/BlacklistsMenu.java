package net.octopvp.octocore.paper.module.impl.punishments.menus.punishments;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.module.impl.punishments.menus.slots.DataInfoButton;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishData;
import net.octopvp.octocore.paper.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.paper.module.impl.punishments.util.PunishmentType;
import net.octopvp.octocore.paper.utils.DateUtils;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.item.WoolUtils;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.PlayerInfoButton;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
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
public class BlacklistsMenu extends PaginatedMenu {
    private final PunishData punishData;

    @Override
    public String getPagesTitle(Player player) {
        return CC.translate("&7" + punishData.getPlayerData().getPlayerName() + "'s blacklists");
    }

    @Override
    public List<Button> getEveryMenuSlots(Player player) {
        List<Button> slots = new ArrayList<>();

        slots.add(new PlayerInfoButton(punishData.getPlayerData().getUniqueId(), 4));

        return slots;
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton.DefaultBackButton(this);
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> slots = new ArrayList<>();

        AtomicInteger order = new AtomicInteger(1);
        List<Punishment> punishments = punishData.getPunishments().stream().sorted(Comparator.comparingLong(Punishment::getAddedAt).reversed()).filter(punishment -> punishment.getPunishmentType() == PunishmentType.BLACKLIST).collect(Collectors.toList());

        punishments.forEach(punishment -> slots.add(new PunishmentButton(punishment, order.getAndIncrement())));

        return slots;
    }

    @AllArgsConstructor
    private class PunishmentButton extends Button {
        private Punishment punishment;
        private int order;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setDurability(punishment.isActive() ? WoolUtils.convertChatColorToWoolData(ChatColor.GREEN) : WoolUtils.convertChatColorToWoolData(ChatColor.RED));
            item.setName(CC.MAIN + "#" + order + " &7(" + CC.SECONDARY + DateUtils.getDate(punishment.getAddedAt()) + "&7)");
            item.addLoreLine(CC.SEPARATOR);
            item.addLoreLine(CC.MAIN + "Added by&7: " + CC.SECONDARY + punishment.getAddedByName());
            item.addLoreLine(CC.MAIN + "Duration&7: " + CC.SECONDARY + punishment.getNiceDuration());
            item.addLoreLine(CC.MAIN + "Expire&7: " + CC.SECONDARY + punishment.getNiceExpire());
            item.addLoreLine(CC.MAIN + "Reason&7: " + CC.SECONDARY + punishment.getReason());
            item.addLoreLine("");
            item.addLoreLine(CC.MAIN + "Permanent&7: " + (punishment.isPermanent() ? "&aYes" : "&cNo"));
            item.addLoreLine(CC.MAIN + "Active&7: " + (!punishment.hasExpired() ? "&aYes" : "&cNo"));
            item.addLoreLine(CC.MAIN + "Silent&7: " + (punishment.isSilent() ? "&aYes" : "&cNo"));
            if (!punishment.getRemovedBy().equalsIgnoreCase("")) {
                item.addLoreLine("");
                item.addLoreLine(CC.MAIN + "Removed by&7: " + CC.SECONDARY + punishment.getRemovedBy());
                item.addLoreLine(CC.MAIN + "Reason&7: " + CC.SECONDARY + punishment.getRemovedFor());
                item.addLoreLine(CC.MAIN + "Date&7: " + CC.SECONDARY + DateUtils.getDate(punishment.getWhenRemoved()));
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
