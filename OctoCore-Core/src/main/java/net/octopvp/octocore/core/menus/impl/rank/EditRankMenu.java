package net.octopvp.octocore.core.menus.impl.rank;

import com.google.common.collect.Lists;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.conversations.QuestionConversation;
import net.octopvp.octocore.core.manager.impl.FilterManager;
import net.octopvp.octocore.core.manager.impl.RankManager;
import net.octopvp.octocore.core.menus.impl.rank.create.ChooseColorMenu;
import net.octopvp.octocore.core.menus.impl.rank.create.ChoosePermissionInheritedMenu;
import net.octopvp.octocore.core.objects.builders.RankBuilder;
import net.octopvp.octocore.core.objects.enums.RankType;
import net.octopvp.octocore.core.objects.permissions.Rank;
import net.octopvp.octocore.core.utils.ItemBuilder;
import net.octopvp.octocore.core.utils.SoundUtil;
import net.octopvp.octocore.core.utils.menu.buttons.Button;
import net.octopvp.octocore.core.utils.menu.buttons.PlaceholderButton;
import net.octopvp.octocore.core.utils.menu.menu.Menu;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class EditRankMenu extends Menu {
    private final String name;
    private final boolean edit;
    private final Menu instance = this;
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

    @Override
    public void onOpen(Player player) {
        if (!edit) {
            if (FilterManager.containsUnicode(name)) {
                player.sendMessage(CC.RED + "You can't use unicode!");
                SoundUtil.playError(player);
                player.closeInventory();
            }
            if (name.contains(" ")) {
                player.sendMessage(CC.RED + "You can't have spaces in the name!");
                SoundUtil.playError(player);
                player.closeInventory();
            }
            if (RankManager.getInstance().getRankByName(name) != null) {
                player.sendMessage(CC.RED + "That rank already exists!");
                SoundUtil.playError(player);
                player.closeInventory();
            }
        }
    }

    @Override
    public List<Button> getButtons(Player player) {
        return Lists.newArrayList(new PlaceholderBtn(), new NameButton(), new PrefixButton(), new ColorButton(), new WeightButton(), new RankTypeButton(), new ServerButton(), new BuildButton(), new PermissionsButton());
    }

    @Override
    public String getName(Player player) {
        return CC.AQUA + "Create a new rank";
    }

    private class PlaceholderBtn extends PlaceholderButton {
        @Override
        public int[] getSlots() {
            List<Integer> a = new ArrayList<>();
            IntStream.range(0, 26).forEach((i) -> {
                if (!(i <= 16 && i >= 10 || i == 26))
                    a.add(i);
            });
            return a.stream().mapToInt(i -> i).toArray();
        }
    }

    private class NameButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.NAME_TAG).name(CC.AQUA + "Name").lore(
                    CC.SEPARATOR,
                    CC.AQUA + "Name: " + name,
                    CC.SEPARATOR
            ).build();
        }

        @Override
        public int getSlot() {
            return 10;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            player.closeInventory();
            prompt(player);
        }

        private void prompt(Player player) {
            OctoCore.getConversationFactory().withFirstPrompt(new QuestionConversation(Lang.CREATE_RANK_SET_PREFIX.getMsg(), (s) -> {
                if (s.equalsIgnoreCase("cancel") || s.equalsIgnoreCase("close")) {
                    open(player);
                    return Prompt.END_OF_CONVERSATION;
                }
                if (FilterManager.containsUnicode(s)) {
                    player.sendMessage(CC.RED + "You can't use unicode!");
                    SoundUtil.playError(player);
                    prompt(player);
                    return Prompt.END_OF_CONVERSATION;
                }
                if (s.contains(" ")) {
                    player.sendMessage(CC.RED + "You can't have spaces in the name!");
                    SoundUtil.playError(player);
                    prompt(player);
                    return Prompt.END_OF_CONVERSATION;
                }
                if (RankManager.getInstance().getRankByName(s) != null) {
                    player.sendMessage(CC.RED + "That rank already exists!");
                    SoundUtil.playError(player);
                    prompt(player);
                    return Prompt.END_OF_CONVERSATION;
                }
                builder.setName(s); //TODO name is null?!
                open(player);
                SoundUtil.playPing(player);
                return Prompt.END_OF_CONVERSATION;
            })).withLocalEcho(false).buildConversation(player).begin();
        }
    }

    private class PrefixButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.SIGN).name(CC.AQUA + "Prefix").lore(CC.SEPARATOR, CC.AQUA + "Prefix: " + ((builder.getRank().getPrefix() == null || builder.getRank().getPrefix().equalsIgnoreCase("")) ? CC.RED + "Not set" : builder.getRank().getPrefix()), CC.SEPARATOR).build();
        }

        @Override
        public int getSlot() {
            return 11;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            player.closeInventory();
            OctoCore.getConversationFactory().withFirstPrompt(new QuestionConversation(Lang.CREATE_RANK_SET_PREFIX.getMsg(), (s) -> {
                if (s.equalsIgnoreCase("cancel") || s.equalsIgnoreCase("close")) {
                    open(player);
                    return Prompt.END_OF_CONVERSATION;
                }
                builder.setPrefix(s);
                open(player);
                SoundUtil.playPing(player);
                return Prompt.END_OF_CONVERSATION;
            })).withLocalEcho(false).buildConversation(player).begin();
        }
    }

    private class ColorButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.EMERALD).name(CC.AQUA + "Color").lore(CC.SEPARATOR, CC.AQUA + "Color: " + builder.getRank().getColor() + StringUtils.capatalizeFirstDeep(builder.getRank().getColor().name().toLowerCase().replace("_", " ")), CC.SEPARATOR).build();
        }

        @Override
        public int getSlot() {
            return 12;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            new ChooseColorMenu(builder, (b) -> {
                builder = b;
                open(player);
            }).open(player);
        }
    }

    private class WeightButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.IRON_INGOT).name(CC.AQUA + "Weight").lore(CC.SEPARATOR, CC.AQUA + "Weight: " + CC.YELLOW + builder.getRank().getWeight(), CC.SEPARATOR).build();
        }

        @Override
        public int getSlot() {
            return 13;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            player.closeInventory();
            prompt(player);
        }

        private void prompt(Player player) {
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
                    prompt(player);
                    return Prompt.END_OF_CONVERSATION;
                }
                builder.setWeight(i);
                open(player);
                return Prompt.END_OF_CONVERSATION;
            })).withLocalEcho(false).buildConversation(player).begin();
        }
    }

    private class RankTypeButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.PAPER).name(CC.AQUA + "Rank Type").lore(CC.SEPARATOR,
                    (builder.getRank().getRankType() == RankType.DEFAULT ? CC.GRAY + CC.SELECTOR_ARROW : "") + CC.D_AQUA + " Default",
                    (builder.getRank().getRankType() == RankType.HIDDEN ? CC.GRAY + CC.SELECTOR_ARROW : "") + CC.D_AQUA + " Hidden",
                    (builder.getRank().getRankType() == RankType.DONATOR ? CC.GRAY + CC.SELECTOR_ARROW : "") + CC.D_AQUA + " Donator",
                    (builder.getRank().getRankType() == RankType.STAFF ? CC.GRAY + CC.SELECTOR_ARROW : "") + CC.D_AQUA + " Staff",
                    "", CC.SEPARATOR, CC.YELLOW + "Left-Click to cycle forward!", CC.YELLOW + "Right-Click to cycle backwards!").build();
        }

        @Override
        public int getSlot() {
            return 14;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            SoundUtil.playPing(player);
            RankType type = builder.getRank().getRankType();
            if (clickType == ClickType.LEFT) { // Default -> Hidden -> Donator -> Staff
                if (type == RankType.DEFAULT)
                    type = RankType.HIDDEN;
                else if (type == RankType.HIDDEN)
                    type = RankType.DONATOR;
                else if (type == RankType.DONATOR)
                    type = RankType.STAFF;
                else if (type == RankType.STAFF)
                    type = RankType.DEFAULT;
            } else if (clickType == ClickType.RIGHT) { // Staff -> Donator -> Hidden -> Default
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
            update(player);
        }
    }

    private class ServerButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.ANVIL).name(CC.AQUA + "Server").lore(CC.SEPARATOR, CC.AQUA + "Server: " + CC.YELLOW + builder.getRank().getScope().getServer(), CC.SEPARATOR).build();
        }

        @Override
        public int getSlot() {
            return 15;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            SoundUtil.playPing(player);
            new ChooseServerScopeMenu((server) -> {
                if (server == null)
                    open(player);
                builder.setScope(server);
                open(player);
            }, instance).open(player);
        }
    }

    private class PermissionsButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.IRON_SWORD).name(CC.AQUA + "Permissions \\ Inherited Ranks").lore(CC.SEPARATOR, CC.AQUA + "Total Permissions: " + CC.YELLOW + builder.getRank().getNodes().size(), CC.AQUA + "Total Allowed Permissions: " + CC.YELLOW + builder.getRank().getAllowedPermissions().size(), CC.AQUA + "Total Negated Permissions: " + CC.YELLOW + builder.getRank().getNegatedPermissions().size(), CC.AQUA + "Inherited Ranks: " + CC.YELLOW + builder.getRank().getInheritedRanks().size(), CC.SEPARATOR).build();
        }

        @Override
        public int getSlot() {
            return 16;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            new ChoosePermissionInheritedMenu(builder, (b) -> {
                builder = b;
                open(player);
            }).open(player);
        }
    }

    private class BuildButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.EMERALD_BLOCK).name(CC.GREEN + CC.B + (edit ? "Save" : "Create")).lore(CC.YELLOW + "Click to " + (edit ? "save" : "create")).build();
        }

        @Override
        public int getSlot() {
            return 26;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            if (!edit)
                RankManager.getInstance().createNewRank(builder);
            else builder.build().save();
            player.closeInventory();
            player.sendMessage(CC.GREEN + "Success!");
            SoundUtil.playPing(player);
        }
    }

}
