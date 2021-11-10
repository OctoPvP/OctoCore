package net.octopvp.octocore.paper.menus.grant;

import com.google.common.collect.Lists;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.json.JsonChain;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.api.events.PlayerGrantEvent;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.objects.GlobalPlayer;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.builders.GrantBuilder;
import net.octopvp.octocore.paper.objects.permissions.Grant;
import net.octopvp.octocore.paper.objects.permissions.Rank;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
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
        public void onClick(Player player, int slot, ClickType clickType) {
            PlayerData senderData = PlayerManager.getData(player);
            GrantProcedure grantProcedure = senderData.getGrantProcedure();
            Rank targetRank = RankManager.getRankByName(grantProcedure.getRankName());
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
            player.closeInventory();
            PlayerGrantEvent event = new PlayerGrantEvent(grant, grantProcedure.getTargetData(), player);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) return;
            Tasks.runAsync(() -> {
                AtomicReference<PlayerData> targetData = new AtomicReference<>(grantProcedure.getTargetData());
                if (targetData.get() == null) {
                    try {
                        PlayerManager.getOfflineData(grantProcedure.getPlayerName()).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    GlobalPlayer globalPlayer = OctoCore.getServerManager().getGlobalPlayer(grantProcedure.getTargetData().getName());
                    if (grant.isPermanent()) {
                        player.sendMessage(Lang.GRANT_PERM_GRANTED_EXECUTOR.getMsg(targetRank.getDisplayName(), grantProcedure.getTargetData().getName(), grantProcedure.getEnteredReason()));
                        if (globalPlayer != null)
                            globalPlayer.sendMessage(Lang.GRANT_PERM_GRANTED_TO.getMsg(targetRank.getDisplayName()));
                        OctoCore.getInstance().getRedisData().write(JedisAction.ADMIN_ALERT, new JsonChain().addProperty("message", Lang.GRANT_ADMIN_ALERT_PERM.getMsg(player.getName(), globalPlayer.getName(), targetRank.getDisplayName(), grantProcedure.getEnteredReason())).get());
                    } else {
                        player.sendMessage(Lang.GRANT_TEMP_GRANTED_EXECUTOR.getMsg(targetRank.getDisplayName(), grantProcedure.getTargetData().getName(), grantProcedure.getNiceDuration()));
                        if (globalPlayer != null) {
                            globalPlayer.sendMessage(Lang.GRANT_TEMP_GRANTED_TO.getMsg(targetRank.getDisplayName(), grantProcedure.getNiceDuration()));
                        }
                        OctoCore.getInstance().getRedisData().write(JedisAction.ADMIN_ALERT, new JsonChain().addProperty("message", Lang.GRANT_ADMIN_ALERT_TEMP.getMsg(player.getName(), globalPlayer.getName(), targetRank.getDisplayName(), grantProcedure.getNiceDuration(), grantProcedure.getEnteredReason())).get());
                    }
                    if (globalPlayer != null) {
                        OctoCore.getInstance().getRedisData().write(JedisAction.GRANTS_UPDATE, new JsonChain().addProperty("name", targetData.get().getName()).addProperty("add", true).addProperty("tochange", OctoCore.getGson().toJson(grant)).get());
                    } else {
                        PlayerData data = PlayerManager.getProfile(grantProcedure.getTargetData().getUuid());
                        if (data == null)
                            data = PlayerManager.loadProfileFromDB(grantProcedure.getTargetData().getUuid(), false);
                        data.getGrants().add(grant);
                        data.save();
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
    }

    private class InfoButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            GrantProcedure procedure = PlayerManager.getProfile(player.getUniqueId()).getGrantProcedure();
            return new ItemBuilder(Material.BEACON).name(CC.AQUA + "Are you sure?").lore(CC.SEPARATOR, CC.AQUA + "Player: " + CC.YELLOW + procedure.getTargetData().getName(), CC.AQUA + "Rank: " + CC.YELLOW + procedure.getRankName(), CC.AQUA + "Current Rank: " + CC.YELLOW + procedure.getTargetData().getHighestRank().getName(), CC.AQUA + "Duration: " + CC.YELLOW + procedure.getNiceDuration(), CC.AQUA + "Server: " + CC.YELLOW + procedure.getServer(), CC.SEPARATOR).build();
        }

        @Override
        public int getSlot() {
            return 13;
        }
    }

    private class PlaceholderButton extends net.octopvp.octocore.paper.utils.menu.buttons.PlaceholderButton {
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
