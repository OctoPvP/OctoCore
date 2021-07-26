package net.octopvp.octocore.paper.menus.rank;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.objects.builders.NodeBuilder;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.callback.TypeCallback;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EditPermissionMenu extends Menu {
    private final NodeBuilder nodeBuilder;
    private final boolean create;
    private final TypeCallback<Void,NodeBuilder> callback;
    public EditPermissionMenu(NodeBuilder nodeBuilder, boolean create, TypeCallback<Void,NodeBuilder> callback){
        this.nodeBuilder = nodeBuilder;
        this.create = create;
        this.callback = callback;
    }
    @Override
    public List<Button> getButtons(Player player) {
        return null;
    }

    @Override
    public String getName(Player player) {
        return CC.AQUA + (create ? "Create" : "Edit") + " permission";
    }
    private class PermissionInfoButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.LEVER).name((nodeBuilder.isAllowed() ? CC.GREEN : CC.RED) + nodeBuilder.getPermission()).lore(CC.AQUA + "Allowed: " + (nodeBuilder.isAllowed() ? CC.GREEN + "Yes" : CC.RED + "No"),CC.AQUA + "Scope: " + CC.YELLOW + nodeBuilder.getScope().getServer(),CC.AQUA + "Permission: " + CC.YELLOW + nodeBuilder.getPermission()).build();
        }

        @Override
        public int getSlot() {
            return 4;
        }
    }
    private class
}
