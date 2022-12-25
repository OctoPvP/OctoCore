package net.octopvp.octocore.core.menus.grant;

import com.google.common.collect.Lists;
import net.octopvp.octocore.common.object.GlobalPlayer;
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
import net.octopvp.octocore.core.objects.builders.GrantBuilder;
import net.octopvp.octocore.core.objects.permissions.Grant;
import net.octopvp.octocore.core.objects.permissions.Rank;
import net.octopvp.octocore.core.utils.menu.buttons.Button;
import net.octopvp.octocore.core.utils.menu.menu.Menu;
import net.octopvp.octocore.core.utils.msg.Lang;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class GrantConfirmationMenu extends Menu {
    @Override
    public List<Button> getButtons(Player player) {
        return Lists.newArrayList(new YesButton(), new NoButton(), new InfoButton(), new PlaceholderButton());
    }

    @Override
    public String getName(Player player) {
        return CC.GREEN + "Are you sure?";
    }

    private class YesButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.STAINED_CLAY).durability(13).name(CC.GREEN + CC.B + "Yes").build();
        }

        @Override
        public int getSlot() {
            return 11;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
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

                GlobalPlayer globalPlayer = OctoCore.getInstance().getServerManager().getGlobalPlayer(targetData.get().getName());
                if (grant.isPermanent()) {
                    player.sendMessage(Lang.GRANT_PERM_GRANTED_EXECUTOR.getMsg(targetRank.getDisplayName(), targetData.get().getName(), grantProcedure.getEnteredReason()));
                    if (globalPlayer != null)
                        globalPlayer.sendMessage(Lang.GRANT_PERM_GRANTED_TO.getMsg(targetRank.getDisplayName()));

                    new AdminAlertPacket(Lang.GRANT_ADMIN_ALERT_PERM.getMsg(player.getName(), globalPlayer.getName(), targetRank.getDisplayName(), grantProcedure.getEnteredReason())).send();
                } else {
                    player.sendMessage(Lang.GRANT_TEMP_GRANTED_EXECUTOR.getMsg(targetRank.getDisplayName(), targetData.get().getName(), grantProcedure.getNiceDuration()));
                    if (globalPlayer != null) {
                        globalPlayer.sendMessage(Lang.GRANT_TEMP_GRANTED_TO.getMsg(targetRank.getDisplayName(), grantProcedure.getNiceDuration()));
                    }
                    new AdminAlertPacket(Lang.GRANT_ADMIN_ALERT_TEMP.getMsg(player.getName(), globalPlayer.getName(), targetRank.getDisplayName(), grantProcedure.getNiceDuration(), grantProcedure.getEnteredReason())).send();
                }
                grant.setActive(true);
                if (Bukkit.getPlayer(targetData.get().getUuid()) != null) {
                    PlayerData data = targetData.get();
                    //data.getGrants().add(grant);
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
                        /*
                        PlayerData data = PlayerManager.getInstance().getData(targetData.get().getUuid());
                        //data.getGrants().add(grant);
                        data.applyGrant(grant);
                        data.save();
                         */
                    }
                }
            });
        }
    }

    private class NoButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.STAINED_CLAY).durability(14).name(CC.RED + CC.B + "No").build();
        }

        @Override
        public int getSlot() {
            return 15;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            player.closeInventory();
        }
    }

    private class InfoButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            GrantProcedure procedure = PlayerManager.getInstance().getData(player.getUniqueId()).getGrantProcedure();
            return new ItemBuilder(Material.BEACON).name(CC.AQUA + "Are you sure?").lore(CC.SEPARATOR, CC.AQUA + "Player: " + CC.YELLOW + procedure.getTargetData().getName(), CC.AQUA + "Rank: " + CC.YELLOW + procedure.getRankName(), CC.AQUA + "Current Rank: " + CC.YELLOW + procedure.getTargetData().getHighestRank().getName(), CC.AQUA + "Duration: " + CC.YELLOW + procedure.getNiceDuration(), CC.AQUA + "Server: " + CC.YELLOW + procedure.getServer(), CC.SEPARATOR).build();
        }

        @Override
        public int getSlot() {
            return 13;
        }
    }

    private class PlaceholderButton extends net.octopvp.octocore.core.utils.menu.buttons.PlaceholderButton {
        @Override
        public int[] getSlots() {
            List<Integer> a = new ArrayList<>();
            IntStream.range(0, 27).forEach((i) -> {
                if (!(i == 11 || i == 15 || i == 13))
                    a.add(i);
            });
            return a.stream().mapToInt(i -> i).toArray();
        }
    }
}
