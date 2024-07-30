package net.octopvp.octocore.core.menus.grant;

import com.cryptomorin.xseries.XMaterial;
import dev.octomc.agile.menu.Menu;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.octopvp.octocore.common.object.OnlinePlayer;
import net.octopvp.octocore.common.object.builders.GrantBuilder;
import net.octopvp.octocore.common.object.permissions.Grant;
import net.octopvp.octocore.common.object.permissions.Rank;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.api.events.PlayerGrantEvent;
import net.octopvp.octocore.core.database.redis.packets.other.grant.AddGrantPacket;
import net.octopvp.octocore.core.database.redis.packets.staff.AdminAlertPacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.manager.impl.RankManager;
import net.octopvp.octocore.core.objects.GrantProcedure;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.msg.Lang;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class GrantConfirmationMenu extends Menu<Gui> {
    private PlayerData targetData;

    public GuiItem infoButton(Player player) {
        GrantProcedure procedure = PlayerManager.getInstance().getData(player.getUniqueId()).getGrantProcedure();
        return ItemBuilder.from(Material.BEACON)
                .name(Component.text("Are you sure?").color(NamedTextColor.AQUA))
                .lore(
                        Component.text(CC.SEPARATOR),
                        Component.text("Player: ")
                                .color(NamedTextColor.AQUA)
                                .append(Component.text(targetData.getName()).color(NamedTextColor.YELLOW)),
                        Component.text("Rank: ")
                                .color(NamedTextColor.AQUA)
                                .append(Component.text(procedure.getRankName()).color(NamedTextColor.YELLOW)),
                        Component.text("Current Rank: ")
                                .color(NamedTextColor.AQUA)
                                .append(Component.text(targetData.getHighestRank().getName()).color(NamedTextColor.YELLOW)),
                        Component.text("Duration: ")
                                .color(NamedTextColor.AQUA)
                                .append(Component.text(procedure.getNiceDuration()).color(NamedTextColor.YELLOW)),
                        Component.text("Server: ")
                                .color(NamedTextColor.AQUA)
                                .append(Component.text(procedure.getServer()).color(NamedTextColor.YELLOW)),
                        Component.text(CC.SEPARATOR)
                )
                .asGuiItem();
    }

    public GuiItem noButton(Player player) {
        return // ItemBuilder.from(Material.STAINED_CLAY)
                // .durability(14)
                ItemBuilder.from(XMaterial.RED_STAINED_GLASS_PANE.parseItem())
                        .name(Component.text("No").color(NamedTextColor.RED))
                        .asGuiItem(event -> player.closeInventory());
    }

    public GuiItem yesButton() { // slot 11
        return // ItemBuilder.from(Material.WOOL)
                // .durability(13)
                ItemBuilder.from(XMaterial.GREEN_STAINED_GLASS_PANE.parseItem())
                        .name(Component.text("Yes").color(NamedTextColor.GREEN))
                        .asGuiItem(event -> {
                            Player player = (Player) event.getWhoClicked();
                            PlayerData senderData = PlayerManager.getInstance().getData(player);
                            GrantProcedure grantProcedure = senderData.getGrantProcedure();
                            Rank targetRank = RankManager.getInstance().getRankByName(grantProcedure.getRankName());
                            if (targetRank == null) {
                                player.closeInventory();
                                player.sendMessage(Lang.GRANT_RANK_NOT_FOUND.getMsg(grantProcedure.getRankName()));
                                return;
                            }
                            GrantBuilder builder = new GrantBuilder(targetRank);
                            builder.setAddedByUUID(player.getUniqueId())
                                    .setAddedBy(player.getName())
                                    .setAddedAt(System.currentTimeMillis())
                                    .setDuration(grantProcedure.getEnteredDuration())
                                    .setReason(grantProcedure.getEnteredReason())
                                    .setServer(grantProcedure.getServer())
                                    .setActive(true)
                                    .setPerm(grantProcedure.isPermanent());
                            Grant grant = builder.build();
                            Logger.debug("Grant: " + grant.toString());
                            Logger.debug("Expired: " + grant.hasExpired() + " | Active: " + grant.isActive());
                            player.closeInventory();
                            PlayerGrantEvent grantEvent = new PlayerGrantEvent(grant, targetData, player);
                            Bukkit.getPluginManager().callEvent(grantEvent);
                            if (grantEvent.isCancelled()) return;
                            Tasks.runAsync(() -> {
                                if (targetData == null) {
                                    targetData = PlayerManager.getInstance().getOfflineData(grantProcedure.getPlayerName());
                                }
                                if (targetData == null) {
                                    player.sendMessage(Lang.GRANT_DATA_COULD_NOT_BE_LOADED.getMsg(senderData.getGrantProcedure().getPlayerName()));
                                    return;
                                }
                                Logger.debug("Applying Grant To: " + targetData.getName());

                                OnlinePlayer onlinePlayer = OctoCore.getInstance().getServerManager().getOnlinePlayer(targetData.getUniqueId());
                                String name = onlinePlayer != null ? onlinePlayer.getName() : targetData.getName();
                                if (grant.isPermanent()) {
                                    player.sendMessage(Lang.GRANT_PERM_GRANTED_EXECUTOR.getMsg(targetRank.getDisplayName(), targetData.getName(), grantProcedure.getEnteredReason()));
                                    if (onlinePlayer != null) {
                                        onlinePlayer.sendMessage(Lang.GRANT_PERM_GRANTED_TO.getMsg(targetRank.getDisplayName()));
                                    }
                                    new AdminAlertPacket(Lang.GRANT_ADMIN_ALERT_PERM.getMsg(
                                            player.getName(),
                                            name,
                                            targetRank.getDisplayName(),
                                            grantProcedure.getEnteredReason())
                                    ).send();
                                } else {
                                    player.sendMessage(Lang.GRANT_TEMP_GRANTED_EXECUTOR.getMsg(targetRank.getDisplayName(), targetData.getName(), grantProcedure.getNiceDuration()));
                                    if (onlinePlayer != null) {
                                        onlinePlayer.sendMessage(Lang.GRANT_TEMP_GRANTED_TO.getMsg(targetRank.getDisplayName(), grantProcedure.getNiceDuration()));
                                    }
                                    new AdminAlertPacket(Lang.GRANT_ADMIN_ALERT_TEMP.getMsg(player.getName(), name, targetRank.getDisplayName(), grantProcedure.getNiceDuration(), grantProcedure.getEnteredReason())).send();
                                }
                                if (Bukkit.getPlayer(targetData.getUuid()) != null) {
                                    PlayerData data = targetData;
                                    data.applyGrant(grant);
                                    data.save();
                                } else {
                                    if (onlinePlayer != null) {
                                        new AddGrantPacket(
                                                targetData.getName(),
                                                targetData.getUniqueId(),
                                                grant
                                        ).send();
                                    } else {
                                        PlayerManager.getInstance().modifyData(targetData.getUuid(), data -> {
                                            data.applyGrant(grant);
                                        });
                                    }
                                }
                            });
                        });
    }

    @Override
    public Gui createGui(Player player) {
        return Gui.gui()
                .title("Are you sure?")
                .rows(3)
                .create();
    }

    @Override
    public void populateGui(Gui gui, Player player) {
        gui.setItem(11, yesButton());
        gui.setItem(15, noButton(player));
        gui.setItem(13, infoButton(player));

        gui.getFiller().fill(PLACEHOLDER_ITEM);
    }
}
