package net.octopvp.octocore.core.menus.rank.create;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import dev.octomc.agile.builder.item.ItemBuilder;
import dev.octomc.agile.guis.Gui;
import dev.octomc.agile.guis.GuiItem;
import dev.octomc.agile.guis.PaginatedGui;
import dev.octomc.agile.menu.Menu;
import dev.octomc.agile.menu.PaginatedMenu;
import net.octopvp.octocore.common.object.builders.NodeBuilder;
import net.octopvp.octocore.common.object.builders.RankBuilder;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.callback.ReturnableTypeCallback;
import net.octopvp.octocore.common.util.perms.Node;
import net.octopvp.octocore.common.util.perms.PermissionManager;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.conversations.QuestionConversation;
import net.octopvp.octocore.core.utils.SoundUtil;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class CreateRankManagePermissionsMenu extends PaginatedMenu<PaginatedGui> {
    private final Menu<?> previousMenu;
    private final RankBuilder builder;
    private final ReturnableTypeCallback<RankBuilder> callback;
    private final RankBuilder startBuilder;
    private final Menu<?> prev = this;
    private final int i = 0;
    private FilterOptions filterOptions = new FilterOptions();

    @SneakyThrows
    public CreateRankManagePermissionsMenu(Menu<?> previousMenu, RankBuilder builder, ReturnableTypeCallback<RankBuilder> callback) {
        this.previousMenu = previousMenu;
        this.builder = builder;
        this.callback = callback;
        this.startBuilder = builder.clone();
    }

    public GuiItem doneButton() {
        return ItemBuilder.from(Material.EMERALD_BLOCK)
                .name("Done")
                .lore(CC.YELLOW + "Click to save and go back!")
                .asGuiItem(event -> {
                    callback.call(builder);
                });
    }

    public GuiItem addPermissionButton() {
        return ItemBuilder.from(Material.NAME_TAG)
                .name("Add new permission")
                .lore(CC.YELLOW + "Click to set a new permission!")
                .asGuiItem(event -> {
                    new EditPermissionMenu(new NodeBuilder(), true, prev, (nodeBuilder) -> {
                        builder.addNode(nodeBuilder.build());
                        event.getWhoClicked().sendMessage("Done!");
                        open((Player) event.getWhoClicked());
                    }).open((Player) event.getWhoClicked());
                });
    }

    public GuiItem permissionButton(Node node, Consumer<NodeBuilder> callback) {
        return ItemBuilder.from(!node.isNegatedIgnoreScope() ? XMaterial.LIME_WOOL : XMaterial.RED_WOOL)
                .name(
                        (node.isNegatedIgnoreScope() ? CC.RED : CC.GREEN) + node.getPermissionString()
                ).lore(
                        CC.SEPARATOR,
                        CC.AQUA + "Negated: " + (node.isNegatedIgnoreScope() ? CC.RED + "Yes" : CC.GREEN + "No"),
                        CC.AQUA + "Scope: " + (node.getServerContext().isPresent() ? CC.YELLOW + node.getServerContext().get().getServersString() : CC.GRAY + "Global"),
                        CC.SEPARATOR,
                        CC.YELLOW + "Left-Click to edit!",
                        CC.RED + "Shift-Right Click to Remove!"
                ).asGuiItem(event -> {
                    ClickType clickType = event.getClick();
                    Player player = (Player) event.getWhoClicked();
                    if (clickType == ClickType.SHIFT_RIGHT) {
                        builder.unsetPermission(node);
                        open(player);
                    } else if (clickType == ClickType.LEFT) {
                        new EditPermissionMenu(new NodeBuilder(node), false, prev, callback).open(player);
                    }
                });
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> items = new ArrayList<>();
        for (Node node : PermissionManager.getInstance().findSignificantNodes(builder.getRank().getNodes())) {
            if (filterOptions.test(node)) {
                items.add(permissionButton(node, (nodeBuilder) -> {
                    // set the node to the new node
                    builder.unsetPermission(node);
                    builder.addNode(nodeBuilder.build()); // TODO make sure that this sets it correctly
                    SoundUtil.playPing(player);
                    player.sendMessage(CC.GREEN + "Done!");
                    open(player);
                }));
            }
        }
        return items;
    }

    @Override
    public void addStaticButtons() {
        gui.updateItem(gui.getRows(), 3, addPermissionButton());
        gui.updateItem(gui.getRows(), 7, doneButton());
    }

    @Override
    public Menu<?> getBackMenu() {
        return previousMenu;
    }

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Manage Permissions")
                .rows(6)
                .create();
    }

    @Override
    public GuiItem getFilterButton() {
        return ItemBuilder.from(Material.HOPPER)
                .name(CC.GREEN + "Filter")
                .lore(
                        CC.AQUA + "Filter: " + (filterOptions.getFilter() == null ? CC.GRAY + "None" : CC.YELLOW + filterOptions.getFilter()),
                        CC.AQUA + "Negated: " + (!filterOptions.negatedExists() ? CC.GRAY + "None" : CC.YELLOW + filterOptions.getNegated().get()),
                        CC.GRAY + "Click to edit filters")
                .asGuiItem(event -> {
                    new FilterMenu(filterOptions -> {
                        this.filterOptions = filterOptions;
                        this.open((Player) event.getWhoClicked());
                    }, filterOptions).open((Player) event.getWhoClicked());
                });
    }

    @Getter
    @Setter
    private static class FilterOptions {
        private Optional<Boolean> negated = Optional.empty();
        private String filter;

        public boolean test(Node node) {
            if ((negated == null || !negated.isPresent()) && (filter == null || filter.isEmpty())) {
                return true;
            }
            String perm = node.getPermissionString().toLowerCase();
            if (negated != null && negated.isPresent()) {
                boolean negatedState = negated.get();
                boolean nodeNegated = node.isNegatedIgnoreScope();
                if (negatedState != nodeNegated) {
                    return false;
                }
            }
            if (filter != null) {
                if (perm.contains(filter.toLowerCase())) {
                    return true;
                }
                try {
                    Pattern pattern = Pattern.compile(filter);
                    if (pattern.matcher(perm).matches()) {
                        return true;
                    }
                } catch (Exception ignored) {
                }
            }
            return false;
        }

        public boolean searchExists() {
            return filter != null && !filter.isEmpty();
        }

        public boolean negatedExists() {
            return negated != null && negated.isPresent();
        }
    }

    @RequiredArgsConstructor
    private static class FilterMenu extends Menu<Gui> {
        private final Consumer<FilterOptions> callback;
        private final FilterOptions filterOptions;

        @Override
        public Gui createGui(Player player) {
            return Gui.gui()
                    .rows(3)
                    .title("Filters")
                    .create();
        }

        @Override
        public void populateGui(Gui gui, Player player) {
            gui.getFiller().fill(PLACEHOLDER_ITEM);
            GuiItem backButton = ItemBuilder.from(Material.ARROW)
                    .name(CC.YELLOW + "Back")
                    .asGuiItem(event -> {
                        callback.accept(filterOptions);
                        SoundUtil.playPing((Player) event.getWhoClicked());
                    });
            gui.setItem(22, backButton);
            gui.setItem(12, ItemBuilder.from(Material.NAME_TAG).name(CC.YELLOW + "Permission")
                    .lore(filterOptions.searchExists() ? CC.GREEN + "Current: " + CC.WHITE + filterOptions.getFilter() : CC.GRAY + "Click to set a permission filter")
                    .asGuiItem(event -> {
                        event.getWhoClicked().closeInventory();
                        OctoCore.getConversationFactory().withFirstPrompt(new QuestionConversation(Lang.FILTER_PERMISSION_PROMPT.getMsg(), (s) -> {
                            if (!s.equalsIgnoreCase("cancel")) {
                                filterOptions.setFilter(s);
                            }
                            open(player);
                            return Prompt.END_OF_CONVERSATION;
                        })).withLocalEcho(false).buildConversation(player).begin();
                    }));
            gui.setItem(14, ItemBuilder.from(Material.ANVIL).name(CC.YELLOW + "Negated")
                    .lore(filterOptions.negatedExists() ? CC.GREEN + "Current: " + CC.WHITE + filterOptions.negated.get() : CC.GRAY + "Click to set a negated filter")
                    .asGuiItem(event -> {
                        // toggle unset -> true -> false -> unset
                        if (filterOptions.negated == null || !filterOptions.negated.isPresent()) { // unset
                            filterOptions.setNegated(Optional.of(true));
                        } else if (filterOptions.negated.get()) { // true
                            filterOptions.setNegated(Optional.of(false));
                        } else { // false
                            filterOptions.setNegated(Optional.empty());
                        }
                        update(player);
                    }));
        }
    }
}
