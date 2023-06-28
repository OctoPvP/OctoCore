package net.octopvp.octocore.core.module.impl.punishments.menus.punishments;

import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.interfaces.IPunishment;
import net.octopvp.octocore.common.util.CC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PunishmentMenuCommons {
    public static List<String> addPunishmentLore(IPunishment punishment) {
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
        if (punishment.isManuallyRemoved()) {
            if (!punishment.getRemovedBy().equals("")) {
                lore.addAll(Arrays.asList(
                        "",
                        CC.GREEN + "Removed by" + CC.GRAY + ": " + CC.YELLOW + punishment.getRemovedBy(),
                        CC.GREEN + "Remove Reason" + CC.GRAY + ": " + CC.YELLOW + punishment.getRemovedFor(),
                        CC.GREEN + "Removed On" + CC.GRAY + ": " + CC.YELLOW + OctoCoreCommon.DATE_FORMAT.format(new Date(punishment.getWhenRemoved()))
                ));
            } else if (punishment.isRemovedOnWebPanel()) {
                lore.addAll(Arrays.asList(
                        "",
                        CC.GREEN + "Removed by" + CC.GRAY + ": " + CC.YELLOW + punishment.getRemovedOnWebPanelName() + " (WEB)",
                        CC.GREEN + "Remove Reason" + CC.GRAY + ": " + CC.YELLOW + punishment.getRemovedFor(),
                        CC.GREEN + "Removed On" + CC.GRAY + ": " + CC.YELLOW + OctoCoreCommon.DATE_FORMAT.format(new Date(punishment.getWhenRemoved())),
                        CC.GRAY + CC.ITALIC + "This punishment was removed on the web panel."
                ));
            }
        }
        lore.add(CC.SEPARATOR);
        return lore;
    }
}
