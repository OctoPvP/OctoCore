package net.octopvp.octocore.core.menus.grant;

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
import net.octopvp.octocore.core.database.redis.packets.other.GrantsUpdatePacket;
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

import java.util.concurrent.atomic.AtomicReference;

public class GrantConfirmationMenu extends Menu<Gui> {
    public GuiItem infoButton(Player player) {
        GrantProcedure procedure = PlayerManager.getInstance().getData(player.getUniqueId()).getGrantProcedure();
        return ItemBuilder.from(Material.BEACON)
                .name(CC.AQUA + "Are you sure?")
                .lore(CC.SEPARATOR, CC.AQUA + "Player: " + CC.YELLOW + procedure.getTargetData().getName(), CC.AQUA + "Rank: " + CC.YELLOW + procedure.getRankName(), CC.AQUA + "Current Rank: " + CC.YELLOW + procedure.getTargetData().getHighestRank().getName(), CC.AQUA + "Duration: " + CC.YELLOW + procedure.getNiceDuration(), CC.AQUA + "Server: " + CC.YELLOW + procedure.getServer(), CC.SEPARATOR)
                .asGuiItem();
    }

    public GuiItem noButton(Player player) {
        return ItemBuilder.from(Material.STAINED_CLAY)
                .durability(14)
                .name(CC.RED + CC.B + "No")
                .asGuiItem(event -> player.closeInventory());
    }

    public GuiItem yesButton() { // slot 11
        return ItemBuilder.from(Material.WOOL)
                .durability(13)
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
                    builder.setAddedByUUID(player.getUniqueId()).setActive(true).setAddedBy(player.getName()).setAddedAt(System.currentTimeMillis());
                    builder.setDuration(grantProcedure.getEnteredDuration()).setReason(grantProcedure.getEnteredReason()).setServer(grantProcedure.getServer());
                    if (grantProcedure.isPermanent()) builder.setPerm(true);
                    else builder.setDuration(grantProcedure.getEnteredDuration());
                    Grant grant = builder.build();
                    Logger.debug("Grant: " + grant.toString());
                    player.closeInventory();
                    PlayerGrantEvent grantEvent = new PlayerGrantEvent(grant, grantProcedure.getTargetData(), player);
                    Bukkit.getPluginManager().callEvent(grantEvent);
                    if (grantEvent.isCancelled()) return;
                    Tasks.runAsync(() -> {
                        AtomicReference<PlayerData> targetData = new AtomicReference<>(grantProcedure.getTargetData());
                        Logger.debug("Applying Grant To: " + targetData.get().getName());
                        if (targetData.get() == null) {
                            targetData.set(PlayerManager.getInstance().getOfflineData(grantProcedure.getPlayerName()));
                        }
                        if (targetData.get() == null) {
                            player.sendMessage(Lang.GRANT_DATA_COULD_NOT_BE_LOADED.getMsg(senderData.getGrantProcedure().getPlayerName()));
                            return;
                        }

                        GlobalPlayer globalPlayer = OctoCore.getInstance().getServerManager().getGlobalPlayer(targetData.get().getUniqueId()); // FIXME globalplayer is null for some reason
                        String name = globalPlayer != null ? globalPlayer.getName() : targetData.get().getName();
                        if (grant.isPermanent()) {
                            player.sendMessage(Lang.GRANT_PERM_GRANTED_EXECUTOR.getMsg(targetRank.getDisplayName(), targetData.get().getName(), grantProcedure.getEnteredReason()));
                            if (globalPlayer != null)
                                globalPlayer.sendMessage(Lang.GRANT_PERM_GRANTED_TO.getMsg(targetRank.getDisplayName()));
                            new AdminAlertPacket(Lang.GRANT_ADMIN_ALERT_PERM.getMsg(
                                    player.getName(),
                                    name,
                                    targetRank.getDisplayName(),
                                    grantProcedure.getEnteredReason())
                            ).send();
                        } else {
                            player.sendMessage(Lang.GRANT_TEMP_GRANTED_EXECUTOR.getMsg(targetRank.getDisplayName(), targetData.get().getName(), grantProcedure.getNiceDuration()));
                            if (globalPlayer != null) {
                                globalPlayer.sendMessage(Lang.GRANT_TEMP_GRANTED_TO.getMsg(targetRank.getDisplayName(), grantProcedure.getNiceDuration()));
                            }
                            new AdminAlertPacket(Lang.GRANT_ADMIN_ALERT_TEMP.getMsg(player.getName(), name, targetRank.getDisplayName(), grantProcedure.getNiceDuration(), grantProcedure.getEnteredReason())).send();
                        }
                        grant.setActive(true);
                        if (Bukkit.getPlayer(targetData.get().getUuid()) != null) {
                            PlayerData data = targetData.get();
                            data.applyGrant(grant);
                            data.save();
                        } else {
                            if (globalPlayer != null) {
                                new GrantsUpdatePacket(
                                        targetData.get().getName(),
                                        OctoCore.getGson().toJson(grant),
                                        true
                                ).send();
                            } else {
                                PlayerManager.getInstance().modifyData(targetData.get().getUuid(), data -> {
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
                .title(CC.AQUA + "Are you sure?")
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
