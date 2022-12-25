package net.octopvp.octocore.core.menus.rank.create;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.callback.ReturnableTypeCallback;
import net.octopvp.octocore.common.util.permissions.Node;
import net.octopvp.octocore.common.util.permissions.NodeBuilder;
import net.octopvp.octocore.core.objects.builders.RankBuilder;
import net.octopvp.octocore.core.utils.SoundUtil;
import net.octopvp.octocore.core.utils.item.WoolUtils;
import net.octopvp.octocore.core.utils.menu.buttons.Button;
import net.octopvp.octocore.core.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.core.utils.menu.menu.Menu;
import net.octopvp.octocore.core.utils.menu.menu.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CreateRankManagePermissionsMenu extends PaginatedMenu {
    private final Menu previousMenu;
    private final RankBuilder builder;
    private final ReturnableTypeCallback<RankBuilder> callback;
    private final RankBuilder startBuilder;
    private final Menu prev = this;
    private int i = 0;

    @SneakyThrows
    public CreateRankManagePermissionsMenu(Menu previousMenu, RankBuilder builder, ReturnableTypeCallback<RankBuilder> callback) {
        this.previousMenu = previousMenu;
        this.builder = builder;
        this.callback = callback;
        this.startBuilder = builder.clone();
    }

    @Override
    public String getPagesTitle(Player player) {
        return CC.GREEN + "Manage Permissions";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        builder.getRank().getNodes().forEach(node -> buttons.add(new PermissionButton(node)));
        return buttons;
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton() {
            @Override
            public void clicked(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
                previousMenu.open(player);
            }

            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.ARROW).name(CC.GREEN + "Back").lore(CC.RED + CC.U + "WILL NOT SAVE CHANGES").build();
            }
        };
    }

    @Override
    public List<Button> getEveryMenuSlots(Player player) {
        return Lists.newArrayList(new AddPermissionButton(), new DoneButton());
    }

    @AllArgsConstructor
    private class PermissionButton extends Button {
        private Node node;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.WOOL).durability(WoolUtils.convertChatColorToWoolData(node.isAllowed() ? ChatColor.GREEN : ChatColor.RED)).name((node.isAllowed() ? ChatColor.GREEN : ChatColor.RED) + node.getPermission()).lore(CC.SEPARATOR, CC.AQUA + "Allowed: " + (node.isAllowed() ? CC.GREEN + "Yes" : CC.RED + "No"), CC.SEPARATOR, CC.YELLOW + "Left-Click to edit!", CC.RED + "Shift-Right Click to Remove!").build();
        }

        @Override
        public int getSlot() {
            return i++;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            if (clickType == ClickType.SHIFT_RIGHT) {
                builder.unsetPermission(node);
                update(player);
            } else if (clickType == ClickType.LEFT) {
                new EditPermissionMenu(new NodeBuilder(node), false, prev, (nodeBuilder) -> {
                    node = nodeBuilder.build(); //TODO make sure that this sets it correctly
                    SoundUtil.playPing(player);
                    player.sendMessage(CC.GREEN + "Done!");
                    open(player);
                }).open(player);
            }
        }
    }

    @AllArgsConstructor
    private class AddPermissionButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.NAME_TAG).name(CC.AQUA + "Add new permission").lore(CC.YELLOW + "Click to set a new permission!").build();
        }

        @Override
        public int getSlot() {
            return 8;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            super.onClick(player, slot, clickType, event);
            new EditPermissionMenu(new NodeBuilder(), true, prev, (nodeBuilder) -> {
                builder.addNode(nodeBuilder.build());
                player.sendMessage(CC.GREEN + "Done!");
                open(player);
            }).open(player);
        }
    }

    private class DoneButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.EMERALD_BLOCK).name(CC.GREEN + "Done").lore(CC.YELLOW + "Click to " + CC.B + "SAVE" + CC.R + CC.YELLOW + " and go back!").build();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            super.onClick(player, slot, clickType, event);
            callback.call(builder);
        }
    }
}
