package net.octopvp.octocore.paper.module.impl.punishments.menus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.module.impl.punishments.menus.alts.AltsMenu;
import net.octopvp.octocore.paper.module.impl.punishments.menus.alts.PotentialAltsMenu;
import net.octopvp.octocore.paper.module.impl.punishments.menus.punishments.*;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishData;
import net.octopvp.octocore.paper.module.impl.punishments.player.PunishPlayerData;
import net.octopvp.octocore.paper.module.impl.punishments.util.Punishment;
import net.octopvp.octocore.paper.module.impl.punishments.util.PunishmentType;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.item.WoolUtils;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.PlaceholderButton;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.PlayerInfoButton;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Getter
public class HistoryMenu extends Menu {
    private final PunishData punishData;

    @Override
    public String getName(Player player) {
        return CC.translate( punishData.getPlayerData().getPlayerName() + "'s punishments");
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> slots = new ArrayList<>();

        slots.add(new PlayerInfoButton(punishData.getPlayerData().getUniqueId(), 4));

        slots.add(new BansButton(punishData.getPlayerData()));
        slots.add(new BlacklistsButton(punishData.getPlayerData()));
        slots.add(new MutesButton(punishData.getPlayerData()));
        slots.add(new WarnsButton(punishData.getPlayerData()));
        slots.add(new KicksButton(punishData.getPlayerData()));

        slots.add(new AltsButton(punishData.getPlayerData()));

        //slots.add(new PlaceholderBtn());
        return slots;
    }
    private class PlaceholderBtn extends PlaceholderButton {
        @Override
        public int[] getSlots() {
            return genPlaceholderSpots(IntStream.range(0,44));
        }
    }
    @AllArgsConstructor
    private class BansButton extends Button {
        private PunishPlayerData playerData;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setDurability(WoolUtils.convertChatColorToWoolData(ChatColor.RED));
            item.setName("&cBans");
            item.addLoreLine("");
            List<Punishment> bans = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.BAN).collect(Collectors.toList());
            item.addLoreLine(CC.GREEN + "Currently banned&7: " + (playerData.getPunishData().isBanned() ? "&aYes" : "&cNo"));
            item.addLoreLine(CC.GREEN + "User was banned " + CC.YELLOW + bans.size() + CC.GREEN + " times.");
            item.addLoreLine("");
            item.addLoreLine(CC.YELLOW + "Click to view all bans.");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 18;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            List<Punishment> punishments = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.BAN).collect(Collectors.toList());
            if (punishments.size() == 0) return;
            new BansMenu(playerData.getPunishData()).open(player);
        }
    }

    @AllArgsConstructor
    private class BlacklistsButton extends Button {
        private PunishPlayerData playerData;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setDurability(14);
            item.setName("&4Blacklists");
            item.addLoreLine("");
            List<Punishment> blacklists = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.BLACKLIST).collect(Collectors.toList());
            item.addLoreLine(CC.GREEN + "Currently blacklisted&7: " + (playerData.getPunishData().isBlacklisted() ? "&aYes" : "&cNo"));
            item.addLoreLine(CC.GREEN + "User was blacklisted " + CC.YELLOW + blacklists.size() + CC.GREEN + " times.");
            item.addLoreLine("");
            item.addLoreLine(CC.YELLOW + "Click to view all blacklists.");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 20;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            List<Punishment> punishments = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.BLACKLIST).collect(Collectors.toList());
            if (punishments.size() == 0) return;
            new BlacklistsMenu(playerData.getPunishData()).open(player);

        }
    }

    @AllArgsConstructor
    private class MutesButton extends Button {
        private PunishPlayerData playerData;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setDurability(WoolUtils.convertChatColorToWoolData(ChatColor.GOLD));
            item.setName("&eMutes");
            item.addLoreLine("");
            List<Punishment> mutes = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.MUTE).collect(Collectors.toList());
            item.addLoreLine(CC.GREEN + "Currently muted&7: " + (playerData.getPunishData().isMuted() ? "&aYes" : "&cNo"));
            item.addLoreLine(CC.GREEN + "User was muted " + CC.YELLOW + mutes.size() + CC.GREEN + " times.");
            item.addLoreLine("");
            item.addLoreLine(CC.YELLOW + "Click to view all mutes.");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 22;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            List<Punishment> punishments = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.MUTE).collect(Collectors.toList());
            if (punishments.size() == 0) return;
            new MutesMenu(playerData.getPunishData()).open(player);
        }
    }

    @AllArgsConstructor
    private class KicksButton extends Button {
        private PunishPlayerData playerData;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setDurability(9);
            item.setName("&3Kicks");
            item.addLoreLine("");
            List<Punishment> kicks = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.KICK).collect(Collectors.toList());
            item.addLoreLine(CC.GREEN + "User was kicked " + CC.YELLOW + kicks.size() + CC.GREEN + " times.");
            item.addLoreLine("");
            item.addLoreLine(CC.YELLOW + "Click to view all kicks.");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 26;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            List<Punishment> punishments = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.KICK).collect(Collectors.toList());
            if (punishments.size() == 0) return;
            new KicksMenu(playerData.getPunishData()).open(player);
        }
    }

    @AllArgsConstructor
    private class WarnsButton extends Button {
        private PunishPlayerData playerData;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setDurability(WoolUtils.convertChatColorToWoolData(ChatColor.YELLOW));
            item.setName("&eWarns");
            item.addLoreLine("");
            List<Punishment> warns = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.WARN).collect(Collectors.toList());
            item.addLoreLine(CC.GREEN + "Currently warned&7: " + (playerData.getPunishData().isWarned() ? "&aYes" : "&cNo"));
            item.addLoreLine(CC.GREEN + "User was warned " + CC.YELLOW + warns.size() + CC.GREEN + " times.");
            item.addLoreLine("");
            item.addLoreLine(CC.YELLOW + "Click to view all warns.");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 24;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            List<Punishment> punishments = playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.WARN).collect(Collectors.toList());
            if (punishments.size() == 0) return;
            new WarnsMenu(playerData.getPunishData()).open(player);
        }
    }

    @AllArgsConstructor
    private class AltsButton extends Button {
        private PunishPlayerData playerData;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.ANVIL);
            item.setName(CC.MAIN + "Alts &7(" + CC.SECONDARY + playerData.getPotentialAlts().size() + " potential&7, " + CC.SECONDARY + playerData.getAlts().size() + " on last ip&7)");
            item.addLoreLine("&7(&cBanned&7, &aOnline&7, &eOffline&7)");
            item.addLoreLine(" ");
            item.addLoreLine(CC.SECONDARY + "Potential Alts");
            if (playerData.getPotentialAlts().size() == 0) {
                item.addLoreLine(CC.VALUE + "- &cNone found!");
            } else {
                playerData.getPotentialAlts().stream().limit(5).forEach(alt -> item.addLoreLine(CC.VALUE + "- " + alt.getNameColor() + alt.getName()));
            }
            item.addLoreLine(CC.SECONDARY + "Alts on last ip &7(More secured)");
            if (playerData.getAlts().size() == 0) {
                item.addLoreLine(CC.VALUE + "- &cNone found!");
            } else {
                playerData.getAlts().stream().limit(5).forEach(alt -> item.addLoreLine(CC.VALUE + "- " + alt.getNameColor() + alt.getName()));
            }
            item.addLoreLine(" ");
            item.addLoreLine(CC.YELLOW + "Left click to see all alts on last ip.");
            item.addLoreLine(CC.YELLOW + "Right click to see all potential alts.");
            return item.toSkullBuilder().withOwner(punishData.getPlayerData().getUniqueId()).buildSkull();
        }

        @Override
        public int getSlot() {
            return 40;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            if (clickType == ClickType.RIGHT) {
                new PotentialAltsMenu(playerData).open(player);
            } else if (clickType == ClickType.LEFT) {
                new AltsMenu(playerData).open(player);
            }
        }
    }
}
