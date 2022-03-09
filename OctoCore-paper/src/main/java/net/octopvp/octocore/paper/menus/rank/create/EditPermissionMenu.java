package net.octopvp.octocore.paper.menus.rank.create;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.permissions.NodeBuilder;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.conversations.QuestionConversation;
import net.octopvp.octocore.paper.manager.impl.FilterManager;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.SoundUtil;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class EditPermissionMenu extends Menu {
    private final NodeBuilder nodeBuilder;
    private final boolean create;
    private final Consumer<NodeBuilder> callback;
    private final Menu previous;

    @SneakyThrows
    public EditPermissionMenu(NodeBuilder nodeBuilder, boolean create, Menu previous, Consumer<NodeBuilder> callback) {
        this.nodeBuilder = nodeBuilder.clone();
        this.create = create;
        this.callback = callback;
        this.previous = previous;
    }

    @Override
    public List<Button> getButtons(Player player) {
        return Lists.newArrayList(new PermissionInfoButton(), new PermissionButton(), new AllowedButton(), new ScopeButton(), new PlaceholderButton(), new CreateButton());
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton() {
            @Override
            public void clicked(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
                previous.open(player);
            }

            @Override
            public int getSlot() {
                return 18;
            }
        };
    }

    @Override
    public String getName(Player player) {
        return CC.AQUA + (create ? "Create" : "Edit") + " permission";
    }

    //permission info / permission / allowed/negated / scope
    private class PermissionInfoButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.LEVER).name((nodeBuilder.isAllowed() ? CC.GREEN : CC.RED) + nodeBuilder.getPermission()).lore(CC.AQUA + "Allowed: " + (nodeBuilder.isAllowed() ? CC.GREEN + "Yes" : CC.RED + "No"), CC.AQUA + "Scope: " + CC.YELLOW + nodeBuilder.getScope().getServer(), CC.AQUA + "Permission: " + CC.YELLOW + nodeBuilder.getPermission()).build();
        }

        @Override
        public int getSlot() {
            return 4;
        }
    }

    private class PermissionButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.REDSTONE_TORCH_ON).name(CC.translate("&bPermission")).lore(
                    CC.SEPARATOR, CC.AQUA + "Permission: " + CC.YELLOW + nodeBuilder.getPermission(),
                    CC.SEPARATOR,
                    CC.YELLOW + "Click to set permission!"
            ).build();
        }

        @Override
        public int getSlot() {
            return 12;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            super.onClick(player, slot, clickType, event);
            SoundUtil.playPing(player);
            player.closeInventory();
            prompt(player);
        }

        private void prompt(Player player) {
            OctoCore.getConversationFactory().withFirstPrompt(new QuestionConversation(Lang.EDIT_PERMISSION_SET_PERMISSION.getMsg(), (s) -> {
                if (s.equalsIgnoreCase("cancel") || s.equalsIgnoreCase("exit")) {
                    open(player);
                    player.sendMessage(CC.RED + "Canceled!");
                    return Prompt.END_OF_CONVERSATION;
                }
                if (s.contains(" ")) {
                    player.sendMessage(CC.RED + "There may not be spaces in the permission!");
                    prompt(player);
                    return Prompt.END_OF_CONVERSATION;
                } else if (FilterManager.containsUnicode(s)) {
                    player.sendMessage(CC.RED + "You may not use unicode!");
                    prompt(player);
                    return Prompt.END_OF_CONVERSATION;
                }
                nodeBuilder.setPermission(s);
                SoundUtil.playPing(player);
                player.sendMessage(Lang.EDIT_PERMISSION_PERMISSION_SET_SUCCESS.getMsg(s));
                open(player);
                return Prompt.END_OF_CONVERSATION;
            })).buildConversation(player).begin();
        }
    }

    private class AllowedButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder((nodeBuilder.isAllowed()) ? Material.EMERALD : Material.REDSTONE).name(CC.AQUA + "Permission allowed").lore(
                    CC.SEPARATOR,
                    CC.AQUA + "Permission Allowed: " + (nodeBuilder.isAllowed() ? CC.GREEN + "Yes" : CC.RED + "No (Negated)"),
                    CC.SEPARATOR,
                    CC.YELLOW + "Click to set to " + ((nodeBuilder.isAllowed()) ? "No" : "Yes")
            ).build();
        }

        @Override
        public int getSlot() {
            return 13;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            super.onClick(player, slot, clickType, event);
            nodeBuilder.setAllowed(!nodeBuilder.isAllowed());
            player.sendMessage(Lang.EDIT_PERMISSION_SET_ALLOWED.getMsg(nodeBuilder.isAllowed() ? CC.GREEN + "True" : CC.RED + "False"));
            update(player);
            SoundUtil.playPing(player);
        }
    }

    private class ScopeButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.SIGN).name(CC.AQUA + "Scope").lore(CC.SEPARATOR, CC.AQUA + "Scope: " + CC.YELLOW + nodeBuilder.getScope().getServer(), CC.SEPARATOR, CC.YELLOW + "Click to set scope!").build();
        }

        @Override
        public int getSlot() {
            return 14;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            super.onClick(player, slot, clickType, event);
            new ChooseServerScopeMenu((context) -> {
                nodeBuilder.setScope(context);
                open(player);
            }).open(player);
        }
    }

    private class CreateButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.EMERALD_BLOCK).name(CC.GREEN + "Save").lore(CC.YELLOW + "Click to save!").build();
        }

        @Override
        public int getSlot() {
            return 26;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            super.onClick(player, slot, clickType, event);
            if (nodeBuilder.getPermission().equalsIgnoreCase("Not Set")) {
                player.sendMessage(CC.RED + "Please set a permission!");
                SoundUtil.playError(player);
                return;
            }
            callback.accept(nodeBuilder);
        }
    }

    private class PlaceholderButton extends net.octopvp.octocore.paper.utils.menu.buttons.PlaceholderButton {
        @Override
        public int[] getSlots() {
            List<Integer> a = new ArrayList<>();
            IntStream.range(0, 26).forEach((i) -> {
                if (i != 13 && i != 12 && i != 14 && i != 4) {
                    a.add(i);
                }
            });
            return a.stream().mapToInt(i -> i).toArray();
        }
    }
}
