package net.octopvp.octocore.paper.menus.rank;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.objects.builders.RankBuilder;
import net.octopvp.octocore.paper.objects.permissions.Node;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.item.WoolUtils;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CreateRankManagePermissionsMenu extends PaginatedMenu {
    private final RankBuilder builder;
    @Override
    public String getPagesTitle(Player player) {
        return CC.GREEN + "Manage Permissions";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        builder.getRank().getNodes().forEach(node ->buttons.add(new PermissionButton(node)));
        return buttons;
    }

    @Override
    public List<Button> getEveryMenuSlots(Player player) {
        return null;
    }
    private int i = 0;
    @RequiredArgsConstructor
    private class PermissionButton extends Button{
        private final Node node;
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.WOOL).durability(WoolUtils.convertChatColorToWoolData(node.isAllowed() ? ChatColor.GREEN : ChatColor.RED)).name((node.isAllowed() ? ChatColor.GREEN : ChatColor.RED) + node.getPermission()).lore(CC.SEPARATOR,CC.AQUA + "Allowed: " + (node.isAllowed() ? CC.GREEN + "Yes" : CC.RED + "No"),CC.SEPARATOR,CC.YELLOW + "Left-Click to edit!",CC.RED + "Shift-Right Click to Remove!").build();
        }

        @Override
        public int getSlot() {
            return i++;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (clickType == ClickType.SHIFT_RIGHT){
                builder.unsetPermission(node);
            }else if (clickType == ClickType.LEFT){

            }
        }
    }
    private class AddPermissionButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return null;
        }

        @Override
        public int getSlot() {
            return 0;
        }
    }
}
