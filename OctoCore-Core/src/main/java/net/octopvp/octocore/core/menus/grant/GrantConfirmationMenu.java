package net.octopvp.octocore.core.menus.grant;

import com.cryptomorin.xseries.XMaterial;
import lombok.AllArgsConstructor;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.menu.Menu;
import net.octopvp.octocore.common.object.GlobalPlayer;
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
                .name(CC.AQUA + "Are you sure?")
                .lore(CC.SEPARATOR, CC.AQUA + "Player: " + CC.YELLOW + targetData.getName(), CC.AQUA + "Rank: " + CC.YELLOW + procedure.getRankName(), CC.AQUA + "Current Rank: " + CC.YELLOW + targetData.getHighestRank().getName(), CC.AQUA + "Duration: " + CC.YELLOW + procedure.getNiceDuration(), CC.AQUA + "Server: " + CC.YELLOW + procedure.getServer(), CC.SEPARATOR)
                .asGuiItem();
    }

    public GuiItem noButton(Player player) {
        return // ItemBuilder.from(Material.STAINED_CLAY)
                // .durability(14)
                ItemBuilder.from(XMaterial.RED_STAINED_GLASS_PANE)
                        .name(CC.RED + CC.B + "No")
                        .asGuiItem(event -> player.closeInventory());
    }

    public GuiItem yesButton() { // slot 11
        return // ItemBuilder.from(Material.WOOL)
                // .durability(13)
                ItemBuilder.from(XMaterial.GREEN_STAINED_GLASS_PANE)
                        .name(CC.GREEN + "Yes")
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

                                GlobalPlayer globalPlayer = OctoCore.getInstance().getServerManager().getGlobalPlayer(targetData.getUniqueId()); // FIXME globalplayer is null for some reason
                                String name = globalPlayer != null ? globalPlayer.getName() : targetData.getName();
                                if (grant.isPermanent()) {
                                    player.sendMessage(Lang.GRANT_PERM_GRANTED_EXECUTOR.getMsg(targetRank.getDisplayName(), targetData.getName(), grantProcedure.getEnteredReason()));
                                    if (globalPlayer != null) {
                                        globalPlayer.sendMessage(Lang.GRANT_PERM_GRANTED_TO.getMsg(targetRank.getDisplayName()));
                                    }
                                    new AdminAlertPacket(Lang.GRANT_ADMIN_ALERT_PERM.getMsg(
                                            player.getName(),
                                            name,
                                            targetRank.getDisplayName(),
                                            grantProcedure.getEnteredReason())
                                    ).send();
                                } else {
                                    player.sendMessage(Lang.GRANT_TEMP_GRANTED_EXECUTOR.getMsg(targetRank.getDisplayName(), targetData.getName(), grantProcedure.getNiceDuration()));
                                    if (globalPlayer != null) {
                                        globalPlayer.sendMessage(Lang.GRANT_TEMP_GRANTED_TO.getMsg(targetRank.getDisplayName(), grantProcedure.getNiceDuration()));
                                    }
                                    new AdminAlertPacket(Lang.GRANT_ADMIN_ALERT_TEMP.getMsg(player.getName(), name, targetRank.getDisplayName(), grantProcedure.getNiceDuration(), grantProcedure.getEnteredReason())).send();
                                }
                                if (Bukkit.getPlayer(targetData.getUuid()) != null) {
                                    PlayerData data = targetData;
                                    data.applyGrant(grant);
                                    data.save();
                                } else {
                                    if (globalPlayer != null) {
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
