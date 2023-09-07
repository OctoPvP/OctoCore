package net.octopvp.octocore.core.menus.rank;

import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.menu.Menu;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.builders.RankBuilder;
import net.octopvp.octocore.common.object.enums.RankType;
import net.octopvp.octocore.common.object.permissions.Rank;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.conversations.QuestionConversation;
import net.octopvp.octocore.core.manager.impl.FilterManager;
import net.octopvp.octocore.core.manager.impl.RankManager;
import net.octopvp.octocore.core.menus.rank.create.ChooseColorMenu;
import net.octopvp.octocore.core.menus.rank.create.ChoosePermissionInheritedMenu;
import net.octopvp.octocore.core.utils.SoundUtil;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class EditRankMenu extends Menu<Gui> {
    private final String name;
    private final boolean edit;
    private final Menu<?> instance = this;
    private RankBuilder builder;

    public EditRankMenu(String name) {
        this.name = name;
        builder = new RankBuilder(name);
        edit = false;
    }


    public EditRankMenu(Rank rank) {
        name = rank.getName();
        builder = new RankBuilder(rank);
        edit = true;
    }

    public GuiItem buildButton() {
        return ItemBuilder.from(Material.EMERALD_BLOCK)
                .name(CC.GREEN + CC.B + (edit ? "Save" : "Create"))
                .lore(CC.YELLOW + "Click to " + (edit ? "save" : "create"))
                .asGuiItem(event -> {
                    if (!edit)
                        RankManager.getInstance().createNewRank(builder);
                    else builder.build().save();
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().sendMessage(CC.GREEN + "Success!");
                    SoundUtil.playPing((Player) event.getWhoClicked());
                });
    }

    public GuiItem permissionsButton() {
        return ItemBuilder.from(Material.IRON_SWORD)
                .name(CC.AQUA + "Permissions \\ Inherited Ranks")
                .lore(CC.SEPARATOR, CC.AQUA + "Total Permissions: " + CC.YELLOW + builder.getRank().getNodes().size(), CC.AQUA + "Total Allowed Permissions: " + CC.YELLOW + builder.getRank().getAllowedPermissions().size(), CC.AQUA + "Total Negated Permissions: " + CC.YELLOW + builder.getRank().getNegatedPermissions().size(), CC.AQUA + "Inherited Ranks: " + CC.YELLOW + builder.getRank().getInheritedRanks().size(), CC.SEPARATOR)
                .asGuiItem(event -> {
                    new ChoosePermissionInheritedMenu(builder, (b) -> {
                        builder = b;
                        open((Player) event.getWhoClicked());
                    }).open((Player) event.getWhoClicked());
                });
    }

    public GuiItem serverButton() {
        return ItemBuilder.from(Material.ANVIL)
                .name(CC.AQUA + "Server")
                .lore(CC.SEPARATOR, CC.AQUA + "Server: " + CC.YELLOW + builder.getRank().getScope().getServersString(), CC.SEPARATOR)
                .asGuiItem(event -> {
                    new ChooseServerScopeMenu((server) -> {
                        if (server == null)
                            open((Player) event.getWhoClicked());
                        builder.setScope(server);
                        open((Player) event.getWhoClicked());
                    }, instance).open((Player) event.getWhoClicked());
                });
    }

    public GuiItem rankTypesButton() {
        return ItemBuilder.from(Material.PAPER)
                .name(CC.AQUA + "Rank Type")
                .lore(CC.SEPARATOR,
                        (builder.getRank().getRankType() == RankType.DEFAULT ? CC.GRAY + CC.SELECTOR_ARROW : "") + CC.D_AQUA + " Default",
                        (builder.getRank().getRankType() == RankType.HIDDEN ? CC.GRAY + CC.SELECTOR_ARROW : "") + CC.D_AQUA + " Hidden",
                        (builder.getRank().getRankType() == RankType.DONATOR ? CC.GRAY + CC.SELECTOR_ARROW : "") + CC.D_AQUA + " Donator",
                        (builder.getRank().getRankType() == RankType.STAFF ? CC.GRAY + CC.SELECTOR_ARROW : "") + CC.D_AQUA + " Staff",
                        "", CC.SEPARATOR, CC.YELLOW + "Left-Click to cycle forward!", CC.YELLOW + "Right-Click to cycle backwards!")
                .asGuiItem(event -> {
                    SoundUtil.playPing((Player) event.getWhoClicked());
                    RankType type = builder.getRank().getRankType();
                    if (event.getClick() == ClickType.LEFT) { // Default -> Hidden -> Donator -> Staff
                        if (type == RankType.DEFAULT)
                            type = RankType.HIDDEN;
                        else if (type == RankType.HIDDEN)
                            type = RankType.DONATOR;
                        else if (type == RankType.DONATOR)
                            type = RankType.STAFF;
                        else if (type == RankType.STAFF)
                            type = RankType.DEFAULT;
                    } else if (event.getClick() == ClickType.RIGHT) { // Staff -> Donator -> Hidden -> Default
                        if (type == RankType.STAFF)
                            type = RankType.DONATOR;
                        else if (type == RankType.DONATOR)
                            type = RankType.HIDDEN;
                        else if (type == RankType.HIDDEN)
                            type = RankType.DEFAULT;
                        else if (type == RankType.DEFAULT)
                            type = RankType.STAFF;
                    }
                    builder.setRankType(type);
                    update((Player) event.getWhoClicked());
                });
    }

    public GuiItem weightButton() {
        return ItemBuilder.from(Material.IRON_INGOT)
                .name(CC.AQUA + "Weight")
                .lore(CC.SEPARATOR, CC.AQUA + "Weight: " + CC.YELLOW + builder.getRank().getWeight(), CC.SEPARATOR)
                .asGuiItem(event -> {
                    event.getWhoClicked().closeInventory();
                    promptSetWeight((Player) event.getWhoClicked());
                });
    }

    private void promptSetWeight(Player player) {
        OctoCore.getConversationFactory().withFirstPrompt(new QuestionConversation(Lang.CREATE_RANK_SET_WEIGHT.getMsg(), (s) -> {
            if (s.equalsIgnoreCase("cancel") || s.equalsIgnoreCase("close")) {
                open(player);
                return Prompt.END_OF_CONVERSATION;
            }
            int i;
            try {
                i = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                player.sendMessage(CC.RED + "Invalid integer!");
                promptSetWeight(player);
                return Prompt.END_OF_CONVERSATION;
            }
            builder.setWeight(i);
            open(player);
            return Prompt.END_OF_CONVERSATION;
        })).withLocalEcho(false).buildConversation(player).begin();
    }

    public GuiItem colorButton() {
        return ItemBuilder.from(Material.EMERALD)
                .name(CC.AQUA + "Color")
                .lore(CC.SEPARATOR, CC.AQUA + "Color: " + builder.getRank().getColor() + StringUtils.capatalizeFirstDeep(builder.getRank().getColor().name().toLowerCase().replace("_", " ")), CC.SEPARATOR)
                .asGuiItem(event -> {
                    new ChooseColorMenu(builder, (b) -> {
                        builder = b;
                        open((Player) event.getWhoClicked());
                    }).open((Player) event.getWhoClicked());
                });
    }

    public GuiItem prefixButton() {
        return ItemBuilder.from(Material.SIGN)
                .name(CC.AQUA + "Prefix")
                .lore(CC.SEPARATOR, CC.AQUA + "Prefix: " + ((builder.getRank().getPrefix() == null || builder.getRank().getPrefix().equalsIgnoreCase("")) ? CC.RED + "Not set" : builder.getRank().getPrefix()), CC.SEPARATOR)
                .asGuiItem(event -> {
                    event.getWhoClicked().closeInventory();
                    OctoCore.getConversationFactory().withFirstPrompt(new QuestionConversation(Lang.CREATE_RANK_SET_PREFIX.getMsg(), (s) -> {
                        if (s.equalsIgnoreCase("cancel") || s.equalsIgnoreCase("close")) {
                            open((Player) event.getWhoClicked());
                            return Prompt.END_OF_CONVERSATION;
                        }
                        builder.setPrefix(s);
                        open((Player) event.getWhoClicked());
                        SoundUtil.playPing((Player) event.getWhoClicked());
                        return Prompt.END_OF_CONVERSATION;
                    })).withLocalEcho(false).buildConversation((Player) event.getWhoClicked()).begin();
                });
    }

    private void promptPrefix(Player player) {
        OctoCore.getConversationFactory().withFirstPrompt(new QuestionConversation(Lang.CREATE_RANK_SET_PREFIX.getMsg(), (s) -> {
            if (s.equalsIgnoreCase("cancel") || s.equalsIgnoreCase("close")) {
                open(player);
                return Prompt.END_OF_CONVERSATION;
            }
            if (FilterManager.containsUnicode(s)) {
                player.sendMessage(CC.RED + "You can't use unicode!");
                SoundUtil.playError(player);
                promptPrefix(player);
                return Prompt.END_OF_CONVERSATION;
            }
            if (s.contains(" ")) {
                player.sendMessage(CC.RED + "You can't have spaces in the name!");
                SoundUtil.playError(player);
                promptPrefix(player);
                return Prompt.END_OF_CONVERSATION;
            }
            if (RankManager.getInstance().getRankByName(s) != null) {
                player.sendMessage(CC.RED + "That rank already exists!");
                SoundUtil.playError(player);
                promptPrefix(player);
                return Prompt.END_OF_CONVERSATION;
            }
            builder.setName(s); //TODO name is null?!
            open(player);
            SoundUtil.playPing(player);
            return Prompt.END_OF_CONVERSATION;
        })).withLocalEcho(false).buildConversation(player).begin();
    }

    public GuiItem nameButton() {
        return ItemBuilder.from(Material.NAME_TAG)
                .name(CC.AQUA + "Name")
                .lore(CC.SEPARATOR, CC.AQUA + "Name: " + builder.getRank().getName(), CC.SEPARATOR)
                .asGuiItem(event -> {
                    event.getWhoClicked().closeInventory();
                    promptPrefix((Player) event.getWhoClicked());
                });
    }

    @Override
    public Gui createGui(Player player) {
        return Gui.gui()
                .title(edit ? "Edit rank" : "Create a new rank")
                .rows(3)
                .create();
    }

    @Override
    public void populateGui(Gui gui, Player player) {
        gui.setItem(10, nameButton());
        gui.setItem(11, prefixButton());
        gui.setItem(12, colorButton());
        gui.setItem(13, weightButton());
        gui.setItem(14, rankTypesButton());
        gui.setItem(15, serverButton());
        gui.setItem(16, permissionsButton());
        gui.setItem(26, buildButton());
        gui.getFiller().fill(PLACEHOLDER_ITEM);
        gui.open(player);
    }
}
