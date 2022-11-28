package net.octopvp.octocore.core.module.impl.punishments.menus.staffhistory;

import lombok.AllArgsConstructor;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.ItemBuilder;
import net.octopvp.octocore.core.utils.item.WoolUtils;
import net.octopvp.octocore.core.utils.menu.buttons.Button;
import net.octopvp.octocore.core.utils.menu.buttons.impl.PlayerInfoButton;
import net.octopvp.octocore.core.utils.menu.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class StaffHistoryMenu extends Menu {
    private PlayerData playerData;

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> slots = new ArrayList<>();

        slots.add(new PlayerInfoButton(playerData, 4));

        slots.add(new BansButton());
        slots.add(new BlacklistsButton());
        slots.add(new MutesButton());
        slots.add(new WarnsButton());
        slots.add(new KicksButton());
        /*
        for (int i = 0; i < 36; i++) {
            if (!Button.hasSlot(slots, i)) {
                slots.add(Button.getGlass(i));
            }
        }
         */
        return slots;
    }

    @Override
    public int getInventorySize(List<Button> buttons) {
        return 36;
    }

    @Override
    public void onOpen(Player p) {
    }

    @Override
    public String getName(Player player) {
        return "Punishments preformed by " + playerData.getName();
    }

    @AllArgsConstructor
    private class BansButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            int active = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.BAN).count();
            int all = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.BAN).count();

            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.durability(WoolUtils.convertChatColorToWoolData(ChatColor.RED));
            item.setName("&cBans");
            item.addLoreLine("");
            item.addLoreLine(CC.AQUA + "Bans performed&7: " + CC.SECONDARY + all);
            item.addLoreLine(CC.AQUA + "Active&7: " + CC.SECONDARY + active + CC.VALUE + "/" + CC.SECONDARY + all);
            item.addLoreLine("");

            return item.build();
        }

        @Override
        public int getSlot() {
            return 18;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            int all = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.BAN).count();
            if (all == 0) return;
            new StaffHistoryPunishmentMenu(playerData, PunishmentType.BAN).open(player);
        }
    }

    @AllArgsConstructor
    private class BlacklistsButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            int active = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.BLACKLIST).count();
            int all = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.BLACKLIST).count();

            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setDurability(WoolUtils.convertChatColorToWoolData(ChatColor.RED));
            item.setName("&4Blacklists");
            item.addLoreLine("");
            item.addLoreLine(CC.AQUA + "Blacklists performed&7: " + CC.SECONDARY + all);
            item.addLoreLine(CC.AQUA + "Active&7: " + CC.SECONDARY + active + CC.VALUE + "/" + CC.SECONDARY + all);
            item.addLoreLine("");

            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 20;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            int all = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.BLACKLIST).count();
            if (all == 0) return;
            new StaffHistoryPunishmentMenu(playerData, PunishmentType.BLACKLIST).open(player);
        }
    }

    @AllArgsConstructor
    private class MutesButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            int active = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.MUTE).count();
            int all = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.MUTE).count();

            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setDurability(WoolUtils.convertChatColorToWoolData(ChatColor.GOLD));
            item.setName("&6Mutes");
            item.addLoreLine("");
            item.addLoreLine(CC.AQUA + "Mutes performed&7: " + CC.SECONDARY + all);
            item.addLoreLine(CC.AQUA + "Active&7: " + CC.SECONDARY + active + CC.VALUE + "/" + CC.SECONDARY + all);
            item.addLoreLine("");

            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 22;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            int all = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.MUTE).count();
            if (all == 0) return;
            new StaffHistoryPunishmentMenu(playerData, PunishmentType.MUTE).open(player);
        }
    }

    @AllArgsConstructor
    private class KicksButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            int active = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.KICK).count();
            int all = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.KICK).count();

            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setDurability(9);
            item.setName("&3Kicks");
            item.addLoreLine("");
            item.addLoreLine(CC.AQUA + "Kicks performed&7: " + CC.SECONDARY + all);
            item.addLoreLine(CC.AQUA + "Active&7: " + CC.SECONDARY + active + CC.VALUE + "/" + CC.SECONDARY + all);
            item.addLoreLine("");

            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 26;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            int all = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.KICK).count();
            if (all == 0) return;
            new StaffHistoryPunishmentMenu(playerData, PunishmentType.KICK).open(player);
        }
    }

    @AllArgsConstructor
    private class WarnsButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            int active = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.WARN).count();
            int all = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.WARN).count();

            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setDurability(WoolUtils.convertChatColorToWoolData(ChatColor.YELLOW));
            item.setName("&eWarns");
            item.addLoreLine("");
            item.addLoreLine(CC.AQUA + "Warns performed&7: " + CC.SECONDARY + all);
            item.addLoreLine(CC.AQUA + "Active&7: " + CC.SECONDARY + active + CC.VALUE + "/" + CC.SECONDARY + all);
            item.addLoreLine("");

            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 24;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            int all = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.WARN).count();
            if (all == 0) return;
            new StaffHistoryPunishmentMenu(playerData, PunishmentType.WARN).open(player);
        }
    }
}
