package net.octopvp.octocore.core.menus.grant;

import lombok.AllArgsConstructor;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.manager.impl.RankManager;
import net.octopvp.octocore.core.objects.GrantProcedure;
import net.octopvp.octocore.core.objects.GrantProcedureState;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.objects.permissions.Rank;
import net.octopvp.octocore.core.utils.ItemBuilder;
import net.octopvp.octocore.core.utils.item.WoolUtils;
import net.octopvp.octocore.core.utils.menu.buttons.Button;
import net.octopvp.octocore.core.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.core.utils.menu.buttons.impl.PlayerInfoButton;
import net.octopvp.octocore.core.utils.menu.menu.PaginatedMenu;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AddGrantMenu extends PaginatedMenu {
    private final PlayerData data;

    public AddGrantMenu(PlayerData data) {
        this.data = data;
    }

    @Override
    public String getPagesTitle(Player player) {
        return CC.AQUA + "Select a rank!";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        RankManager.getRanks().stream().sorted(Comparator.comparingInt(Rank::getWeight).reversed()).forEach(rank -> buttons.add(new RankButton(rank, data)));
        return buttons;
    }

    @Override
    public List<Button> getEveryMenuSlots(Player player) {
        List<Button> slots = new ArrayList<>();

        slots.add(new PlayerInfoButton(data, 4));

        return slots;
    }

    @Override
    public void onOpen(Player player) {
    }

    @Override
    public void onClose(Player player) {
        PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());
        if (playerData.getGrantProcedure() != null && playerData.getGrantProcedure().getGrantProcedureState() == GrantProcedureState.START) {
            playerData.setGrantProcedure(null);
        }
        PlayerManager.getInstance().getPlayerProfiles().remove(data.getUuid());
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton() {
            @Override
            public void clicked(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
                previous.open(player);
            }
        };
    }

    @AllArgsConstructor
    private class RankButton extends Button {
        private Rank rankData;
        private PlayerData playerData;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setName(rankData.getDisplayName());
            item.durability((short) (rankData.isDefaultRank() ? 4 : WoolUtils.convertChatColorToWoolData(rankData.getColor())));
            item.lore(CC.SEPARATOR, CC.AQUA + "Weight" + CC.GRAY + ": " + CC.YELLOW + rankData.getWeight(), CC.AQUA + "Inherited: " + CC.YELLOW + StringUtils.join(rankData.getInheritedRanksName(), ", "), CC.AQUA + "Default: " + CC.YELLOW + rankData.isDefaultRank(),
                    CC.AQUA + "Prefix: " + CC.YELLOW + rankData.getPrefix(), CC.AQUA + "Changeable Color: " + CC.YELLOW + rankData.isChangableMainColor(), CC.AQUA + "Purchasable: " + CC.YELLOW + rankData.isPurchasable(),
                    CC.SEPARATOR
            );
            return item.build();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            if (rankData.isDefaultRank()) {
                player.sendMessage(Lang.GRANT_CANT_GRANT_DEFAULT.getMsg());
                return;
            }
            if (playerData.hasRank(rankData)) {
                player.sendMessage(Lang.GRANT_ALREADY_HAS_RANK.getMsg(playerData.getName(), rankData.getName()));
                return;
            }
            PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());
            if (!RankManager.getInstance().canGrant(playerData, rankData) && !player.hasPermission(Permissions.GRANT_ALL)) {
                player.sendMessage(Lang.GRANT_CANNOT_GRANT_HIGHER_RANK.getMsg());
                return;
            }
            /*
            if (player.hasPermission("octocore.grant.disallow." + rankData.getName().toLowerCase())) {
                player.sendMessage(Lang.GRANT_NO_PERMISSION_TO_GRANT_RANK.getMsg());
                return;
            }
             */
            playerData.setGrantProcedure(new GrantProcedure(this.playerData));
            playerData.getGrantProcedure().setRankName(rankData.getName());
            playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.SERVER_CHOOSE);

            new GrantServerMenu(data).open(player);
        }
    }
}
