package net.octopvp.octocore.core.menus.rank.create;

import lombok.SneakyThrows;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.guis.PaginatedGui;
import net.octopvp.agile.menu.Menu;
import net.octopvp.agile.menu.PaginatedMenu;
import net.octopvp.agile.util.XMaterial;
import net.octopvp.octocore.common.object.builders.RankBuilder;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.callback.ReturnableTypeCallback;
import net.octopvp.octocore.common.util.permissions.Node;
import net.octopvp.octocore.common.util.permissions.NodeBuilder;
import net.octopvp.octocore.core.utils.SoundUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CreateRankManagePermissionsMenu extends PaginatedMenu<PaginatedGui> {
    private final Menu<?> previousMenu;
    private final RankBuilder builder;
    private final ReturnableTypeCallback<RankBuilder> callback;
    private final RankBuilder startBuilder;
    private final Menu<?> prev = this;
    private final int i = 0;

    @SneakyThrows
    public CreateRankManagePermissionsMenu(Menu previousMenu, RankBuilder builder, ReturnableTypeCallback<RankBuilder> callback) {
        this.previousMenu = previousMenu;
        this.builder = builder;
        this.callback = callback;
        this.startBuilder = builder.clone();
    }

    /*

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
     */
    public GuiItem doneButton() {
        return ItemBuilder.from(Material.EMERALD_BLOCK)
                .name("Done")
                .lore("Click to save and go back!")
                .asGuiItem(event -> {
                    callback.call(builder);
                });
    }

    public GuiItem addPermissionButton() {
        return ItemBuilder.from(Material.NAME_TAG)
                .name("Add new permission")
                .lore("Click to set a new permission!")
                .asGuiItem(event -> {
                    new EditPermissionMenu(new NodeBuilder(), true, prev, (nodeBuilder) -> {
                        builder.addNode(nodeBuilder.build());
                        event.getWhoClicked().sendMessage("Done!");
                        open((Player) event.getWhoClicked());
                    }).open((Player) event.getWhoClicked());
                });
    }

    public GuiItem permissionButton(Node node, Consumer<NodeBuilder> callback) {
        Node a = node;
        return ItemBuilder.from(node.isAllowed() ? XMaterial.GREEN_WOOL : XMaterial.RED_WOOL)
                .name(
                        (node.isAllowed() ? CC.GREEN : CC.RED) + node.getPermission()
                ).lore(
                        CC.SEPARATOR,
                        CC.AQUA + "Allowed: " + (node.isAllowed() ? CC.GREEN + "Yes" : CC.RED + "No"),
                        CC.SEPARATOR,
                        CC.YELLOW + "Left-Click to edit!",
                        CC.RED + "Shift-Right Click to Remove!"
                ).asGuiItem(event -> {
                    ClickType clickType = event.getClick();
                    Player player = (Player) event.getWhoClicked();
                    if (clickType == ClickType.SHIFT_RIGHT) {
                        builder.unsetPermission(a);
                        update(player);
                    } else if (clickType == ClickType.LEFT) {
                        new EditPermissionMenu(new NodeBuilder(a), false, prev, callback).open(player);
                    }
                });
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> items = new ArrayList<>();
        builder.getRank().getNodes().forEach(node -> items.add(permissionButton(node, (nodeBuilder) -> {
            // set the node to the new node
            builder.unsetPermission(node);
            builder.addNode(nodeBuilder.build()); // TODO make sure that this sets it correctly
            SoundUtil.playPing(player);
            player.sendMessage(CC.GREEN + "Done!");
            open(player);
        })));
        return items;
    }

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Manage Permissions")
                .rows(6)
                .create();
    }
}
