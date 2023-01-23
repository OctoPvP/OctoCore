package net.octopvp.octocore.core.module.impl.punishments.menus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.menu.Menu;
import net.octopvp.agile.util.XMaterial;
import net.octopvp.octocore.common.interfaces.IPunishData;
import net.octopvp.octocore.common.interfaces.IPunishment;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.module.impl.punishments.menus.alts.PotentialAltsMenu;
import net.octopvp.octocore.core.module.impl.punishments.menus.punishments.*;
import net.octopvp.octocore.core.utils.Buttons;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class HistoryMenu extends Menu<Gui> {
    private final IPunishData iPunishData;

    @SuppressWarnings("deprecation")
    public GuiItem altsButton(IPunishData playerData) { // slot 32
        List<String> lore = new ArrayList<>(Arrays.asList(
                CC.GRAY + "(" + CC.RED + "Banned" + CC.GRAY + ", " + CC.GREEN + "Online" + CC.GRAY + ", " + CC.YELLOW + "Offline" + CC.GRAY + ")",
                " ",
                CC.GRAY + "Potential Alts"
        ));
        if (playerData.getAlts().size() == 0) {
            lore.add(CC.VALUE + "- " + CC.RED + "None found!");
        } else {
            playerData.getAlts().stream().limit(5).forEach(alt -> lore.add(CC.VALUE + "- " + alt.getNameColor() + alt.getName()));
        }
        lore.add(CC.GRAY + "Alts on last ip " + CC.GRAY + "(More secured)");
        if (playerData.getAlts().size() == 0) {
            lore.add(CC.VALUE + "- " + CC.RED + "None found!");
        } else {
            playerData.getAlts().stream().limit(5).forEach(alt -> lore.add(CC.VALUE + "- " + alt.getNameColor() + alt.getName()));
        }
        lore.add(" ");
        lore.add(CC.YELLOW + "Click to see all potential alts.");
        lore.add(" ");

        return ItemBuilder.skull()
                .name(CC.MAIN + "Alts " + CC.GRAY + "(" + CC.SECONDARY + playerData.getAlts().size() + CC.GRAY + ")")
                .setLore(lore)
                .owner(Bukkit.getOfflinePlayer(playerData.getUniqueId()))
                .asGuiItem(event -> {
                    new PotentialAltsMenu(playerData, this).open((Player) event.getWhoClicked());
                });
    }

    @SuppressWarnings("deprecation")
    public GuiItem warnsButton(IPunishData playerData) { // slot 31
        List<IPunishment> warns = playerData.getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.WARN).collect(Collectors.toList());
        List<String> lore = Arrays.asList(
                CC.GRAY + "Currently warned&7: " + (playerData.isWarned() ? CC.GREEN + "Yes" : CC.RED + "No"),
                CC.GRAY + "User was warned " + CC.YELLOW + warns.size() + CC.GRAY + " times.",
                " ",
                CC.YELLOW + "Click to view all warns."
        );

        return ItemBuilder.from(XMaterial.YELLOW_WOOL)
                .name(CC.MAIN + "Warns " + CC.GRAY + "(" + CC.SECONDARY + warns.size() + CC.GRAY + ")")
                .setLore(lore)
                .asGuiItem(event -> {
                    List<IPunishment> punishments = playerData.getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.WARN).collect(Collectors.toList());
                    if (punishments.size() == 0) return;
                    new WarnsMenu(playerData, HistoryMenu.this).open((Player) event.getWhoClicked());
                });
    }

    @SuppressWarnings("deprecation")
    public GuiItem kicksButton(IPunishData playerData) { // slot 30
        List<IPunishment> kicks = playerData.getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.KICK).collect(Collectors.toList());
        List<String> lore = Arrays.asList(
                CC.GRAY + "User was kicked " + CC.YELLOW + kicks.size() + CC.GRAY + " times.",
                " ",
                CC.YELLOW + "Click to view all kicks."
        );
        return ItemBuilder.from(XMaterial.LIME_WOOL)
                .name(CC.MAIN + "Kicks " + CC.GRAY + "(" + CC.SECONDARY + kicks.size() + CC.GRAY + ")")
                .setLore(lore)
                .asGuiItem(event -> {
                    List<IPunishment> punishments = playerData.getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.KICK).collect(Collectors.toList());
                    if (punishments.size() == 0) return;
                    new KicksMenu(playerData, HistoryMenu.this).open((Player) event.getWhoClicked());
                });
    }

    @SuppressWarnings("deprecation")
    public GuiItem mutesButton(IPunishData playerData) {
        List<IPunishment> mutes = playerData.getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.MUTE).collect(Collectors.toList());
        List<String> lore = Arrays.asList(
                CC.GRAY + "Currently muted&7: " + (playerData.isMuted() ? CC.GREEN + "Yes" : CC.RED + "No"),
                CC.GRAY + "User was muted " + CC.YELLOW + mutes.size() + CC.GRAY + " times.",
                " ",
                CC.YELLOW + "Click to view all mutes."
        );
        return ItemBuilder.from(XMaterial.ORANGE_WOOL)
                .name(CC.MAIN + "Mutes " + CC.GRAY + "(" + CC.SECONDARY + mutes.size() + CC.GRAY + ")")
                .setLore(lore)
                .asGuiItem(event -> {
                    List<IPunishment> punishments = playerData.getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.MUTE).collect(Collectors.toList());
                    if (punishments.size() == 0) return;
                    new MutesMenu(playerData, HistoryMenu.this).open((Player) event.getWhoClicked());
                });
    }

    @SuppressWarnings("deprecation")
    public GuiItem blacklistsButton(IPunishData playerData) {
        List<IPunishment> blacklists = playerData.getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.BLACKLIST).collect(Collectors.toList());
        List<String> lore = Arrays.asList(
                CC.GRAY + "Currently blacklisted&7: " + (playerData.isBlacklisted() ? CC.GREEN + "Yes" : CC.RED + "No"),
                CC.GRAY + "User was blacklisted " + CC.YELLOW + blacklists.size() + CC.GRAY + " times.",
                " ",
                CC.YELLOW + "Click to view all blacklists."
        );
        return ItemBuilder.from(XMaterial.RED_WOOL)
                .name(CC.MAIN + "Blacklists " + CC.GRAY + "(" + CC.SECONDARY + blacklists.size() + CC.GRAY + ")")
                .setLore(lore)
                .asGuiItem(event -> {
                    List<IPunishment> punishments = playerData.getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.BLACKLIST).collect(Collectors.toList());
                    if (punishments.size() == 0) return;
                    new BlacklistsMenu(playerData, HistoryMenu.this).open((Player) event.getWhoClicked());
                });
    }

    @SuppressWarnings("deprecation")
    public GuiItem bansButton(IPunishData playerData) {
        List<IPunishment> bans = playerData.getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.BAN).collect(Collectors.toList());
        List<String> lore = Arrays.asList(
                CC.GRAY + "Currently banned&7: " + (playerData.isBanned() ? CC.GREEN + "Yes" : CC.RED + "No"),
                CC.GRAY + "User was banned " + CC.YELLOW + bans.size() + CC.GRAY + " times.",
                " ",
                CC.YELLOW + "Click to view all bans."
        );
        return ItemBuilder.from(XMaterial.RED_WOOL)
                .name(CC.MAIN + "Bans " + CC.GRAY + "(" + CC.SECONDARY + bans.size() + CC.GRAY + ")")
                .setLore(lore)
                .asGuiItem(event -> {
                    List<IPunishment> punishments = playerData.getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == PunishmentType.BAN).collect(Collectors.toList());
                    if (punishments.size() == 0) return;
                    new BansMenu(playerData, HistoryMenu.this).open((Player) event.getWhoClicked());
                });
    }

    @Override
    public Gui createGui(Player player) {
        return Gui.gui()
                .title(iPunishData.getName() + "'s punishments")
                .rows(5)
                .create();
    }

    @Override
    public void populateGui(Gui gui, Player player) {
        gui.setItem(13, Buttons.playerInfo(iPunishData.getUniqueId()));

        gui.setItem(21, kicksButton(iPunishData));
        gui.setItem(22, mutesButton(iPunishData));
        gui.setItem(23, blacklistsButton(iPunishData));
        gui.setItem(30, warnsButton(iPunishData));
        gui.setItem(31, bansButton(iPunishData));

        gui.setItem(40, altsButton(iPunishData));
    }
}
