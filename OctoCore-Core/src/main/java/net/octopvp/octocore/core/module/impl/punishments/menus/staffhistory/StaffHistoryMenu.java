package net.octopvp.octocore.core.module.impl.punishments.menus.staffhistory;

import com.cryptomorin.xseries.XMaterial;
import lombok.AllArgsConstructor;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.menu.Menu;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.Buttons;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class StaffHistoryMenu extends Menu<Gui> {
    private PlayerData playerData;

    @Override
    public Gui createGui(Player player) {
        return Gui.gui()
                .title(playerData.getName() + "'s staff history")
                .rows(4)
                .create();
    }

    public GuiItem warnsButton() { // slot 24
        int active = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.WARN).count();
        int all = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.WARN).count();
        return ItemBuilder.from(XMaterial.YELLOW_WOOL).name(CC.YELLOW + "Warns")
                .lore(CC.AQUA + "Warns performed&7: " + CC.SECONDARY + all)
                .lore(CC.AQUA + "Active&7: " + CC.SECONDARY + active + CC.VALUE + "/" + CC.SECONDARY + all)
                .asGuiItem(event -> {
                    int all1 = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.WARN).count();
                    if (all1 == 0) return;
                    new StaffHistoryPunishmentMenu(playerData, PunishmentType.WARN).open((Player) event.getWhoClicked());
                });
    }

    public GuiItem kicksButton() { // Slot 26
        int active = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.KICK).count();
        int all = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.KICK).count();
        return ItemBuilder.from(XMaterial.BLUE_WOOL).name(CC.BLUE + "Kicks")
                .lore(CC.AQUA + "Kicks performed&7: " + CC.SECONDARY + all)
                .lore(CC.AQUA + "Active&7: " + CC.SECONDARY + active + CC.VALUE + "/" + CC.SECONDARY + all)
                .asGuiItem(event -> {
                    int all1 = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.KICK).count();
                    if (all1 == 0) return;
                    new StaffHistoryPunishmentMenu(playerData, PunishmentType.KICK).open((Player) event.getWhoClicked());
                });
    }

    public GuiItem mutesButton() { // Slot 22
        int active = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.MUTE).count();
        int all = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.MUTE).count();
        return ItemBuilder.from(XMaterial.ORANGE_WOOL).name(CC.GOLD + "Mutes")
                .lore(CC.AQUA + "Mutes performed&7: " + CC.SECONDARY + all)
                .lore(CC.AQUA + "Active&7: " + CC.SECONDARY + active + CC.VALUE + "/" + CC.SECONDARY + all)
                .asGuiItem(event -> {
                    int all1 = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.MUTE).count();
                    if (all1 == 0) return;
                    new StaffHistoryPunishmentMenu(playerData, PunishmentType.MUTE).open((Player) event.getWhoClicked());
                });
    }

    public GuiItem blacklistsButton() { // Slot 20
        int active = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.BLACKLIST).count();
        int all = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.BLACKLIST).count();
        return ItemBuilder.from(XMaterial.RED_WOOL).name(CC.RED + "Blacklists")
                .lore(CC.AQUA + "Blacklists performed&7: " + CC.SECONDARY + all)
                .lore(CC.AQUA + "Active&7: " + CC.SECONDARY + active + CC.VALUE + "/" + CC.SECONDARY + all)
                .asGuiItem(event -> {
                    int all1 = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.BLACKLIST).count();
                    if (all1 == 0) return;
                    new StaffHistoryPunishmentMenu(playerData, PunishmentType.BLACKLIST).open((Player) event.getWhoClicked());
                });
    }

    public GuiItem bansButton() { // Slot 18
        int active = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> !punishment.hasExpired() && punishment.getType() == PunishmentType.BAN).count();
        int all = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.BAN).count();
        return ItemBuilder.from(XMaterial.RED_WOOL).name(CC.RED + "Bans")
                .lore(CC.AQUA + "Bans performed&7: " + CC.SECONDARY + all)
                .lore(CC.AQUA + "Active&7: " + CC.SECONDARY + active + CC.VALUE + "/" + CC.SECONDARY + all)
                .asGuiItem(event -> {
                    int all1 = (int) playerData.getPunishmentsExecuted().stream().filter(punishment -> punishment.getType() == PunishmentType.BAN).count();
                    if (all1 == 0) return;
                    new StaffHistoryPunishmentMenu(playerData, PunishmentType.BAN).open((Player) event.getWhoClicked());
                });
    }


    @Override
    public void populateGui(Gui gui, Player player) {
        gui.setItem(4, Buttons.playerInfo(playerData));
        gui.setItem(18, bansButton());
        gui.setItem(20, blacklistsButton());
        gui.setItem(22, mutesButton());
        gui.setItem(26, kicksButton());
        gui.setItem(24, warnsButton());
    }

}
