package net.octopvp.octocore.core.menus.rank.create;

import com.cryptomorin.xseries.XMaterial;
import lombok.SneakyThrows;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.menu.Menu;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.common.object.builders.NodeBuilder;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.conversations.QuestionConversation;
import net.octopvp.octocore.core.manager.impl.FilterManager;
import net.octopvp.octocore.core.menus.rank.ChooseServerScopeMenu;
import net.octopvp.octocore.core.utils.SoundUtil;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class EditPermissionMenu extends Menu<Gui> {
    private final NodeBuilder nodeBuilder;
    private final boolean create;
    private final Consumer<NodeBuilder> callback;
    private final Menu<?> previous;

    private final Menu<?> instance = this;

    @SneakyThrows
    public EditPermissionMenu(NodeBuilder nodeBuilder, boolean create, Menu<?> previous, Consumer<NodeBuilder> callback) {
        this.nodeBuilder = nodeBuilder.clone();
        this.create = create;
        this.callback = callback;
        this.previous = previous;
    }

    @Override
    public Gui createGui(Player player) {
        return Gui.gui()
                .title((create ? "Create" : "Edit") + " Permission")
                .rows(3)
                .create();
    }

    public GuiItem createButton() {
        return ItemBuilder.from(Material.EMERALD_BLOCK)
                .name(CC.GREEN + "Save")
                .lore(CC.YELLOW + "Click to save!")
                .asGuiItem(event -> {
                    if (nodeBuilder.getPermission().equalsIgnoreCase("Not Set")) {
                        event.getWhoClicked().sendMessage(CC.RED + "Please set a permission!");
                        SoundUtil.playError((Player) event.getWhoClicked());
                        return;
                    }
                    callback.accept(nodeBuilder);
                });
    }

    public GuiItem scopeButton() {
        return ItemBuilder.from(Material.SIGN)
                .name(CC.AQUA + "Scope")
                .lore(CC.SEPARATOR, CC.AQUA + "Scope: " + CC.YELLOW + nodeBuilder.getScope().orElse(new ServerContext("Global")).getServersString(), CC.SEPARATOR, CC.YELLOW + "Click to set scope!")
                .asGuiItem(event -> new ChooseServerScopeMenu((context) -> {
                    nodeBuilder.setScope(context);
                    open((Player) event.getWhoClicked());
                }, instance).open((Player) event.getWhoClicked()));
    }

    public GuiItem allowedButton() {
        return ItemBuilder.from((nodeBuilder.isNegated()) ? Material.REDSTONE : Material.EMERALD)
                .name(CC.AQUA + "Permission negated")
                .lore(CC.SEPARATOR,
                        CC.AQUA + "Permission Negated: " + (nodeBuilder.isNegated() ? CC.GREEN + "Yes" : CC.RED + "No"),
                        CC.SEPARATOR,
                        CC.YELLOW + "Click to set to " + ((nodeBuilder.isNegated()) ? "No" : "Yes"))
                .asGuiItem(event -> {
                    nodeBuilder.setNegated(!nodeBuilder.isNegated());
                    event.getWhoClicked().sendMessage(Lang.EDIT_PERMISSION_SET_NEGATED.getMsg(nodeBuilder.isNegated() ? CC.GREEN + "True" : CC.RED + "False"));
                    update((Player) event.getWhoClicked());
                    SoundUtil.playPing((Player) event.getWhoClicked());
                });
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
        })).withLocalEcho(false).buildConversation(player).begin();
    }

    public GuiItem permissionButton() {
        return ItemBuilder.from(XMaterial.REDSTONE_TORCH)
                .name(CC.AQUA + "Permission")
                .lore(CC.SEPARATOR, CC.AQUA + "Permission: " + CC.YELLOW + nodeBuilder.getPermission(), CC.SEPARATOR, CC.YELLOW + "Click to set permission!")
                .asGuiItem(event -> {
                    SoundUtil.playPing((Player) event.getWhoClicked());
                    event.getWhoClicked().closeInventory();
                    prompt((Player) event.getWhoClicked());
                });
    }

    public GuiItem permissionInfoButton() {
        return ItemBuilder.from(Material.LEVER)
                .name((nodeBuilder.isNegated() ? CC.GREEN : CC.RED) + nodeBuilder.getPermission())
                .lore(CC.AQUA + "Negated: " + (nodeBuilder.isNegated() ? CC.RED + "Yes" : CC.GREEN + "No"),
                        CC.AQUA + "Scope: " + CC.YELLOW + nodeBuilder.getScope().orElse(new ServerContext("Global")).getServersString(),
                        CC.AQUA + "Permission: " + CC.YELLOW + nodeBuilder.getPermission())
                .asGuiItem(event -> {
                });
    }

    /*

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
     */

    @Override
    public void populateGui(Gui gui, Player player) {
        gui.setItem(18, backButton(previous));
        gui.setItem(4, permissionInfoButton());
        gui.setItem(12, permissionButton());
        gui.setItem(13, allowedButton());
        gui.setItem(14, scopeButton());
        gui.setItem(26, createButton());
        gui.getFiller().fill(PLACEHOLDER_ITEM);
    }
}
