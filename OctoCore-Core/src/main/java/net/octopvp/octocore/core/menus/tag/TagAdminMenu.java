package net.octopvp.octocore.core.menus.tag;

import dev.octomc.agile.builder.item.ItemBuilder;
import dev.octomc.agile.guis.Gui;
import dev.octomc.agile.guis.GuiItem;
import dev.octomc.agile.menu.Menu;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.conversations.QuestionConversation;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.OfflineHelpers;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

public class TagAdminMenu extends Menu<Gui> {
    @Override
    public Gui createGui(Player player) {
        return Gui.gui().title("Tags Admin").rows(3).create();
    }

    public GuiItem manageTagsButton() {
        return ItemBuilder.from(Material.CHEST).name(CC.AQUA + "Manage Tags").lore(CC.YELLOW + "Click to manage tags").asGuiItem()
                .setAction(event -> {
                    new ManageTagsMenu(this).open((Player) event.getWhoClicked());
                });
    }

    public GuiItem createTagButton() {
        return ItemBuilder.from(Material.ANVIL).name(CC.GOLD + "Create Tag").lore(CC.YELLOW + "Click to create a tag.").asGuiItem()
                .setAction(event -> {
                    new ManageTagMenu((Player) event.getWhoClicked()).open((Player) event.getWhoClicked());
                });
    }

    public GuiItem managePlayerTagsButton() {
        return ItemBuilder.from(Material.CHEST).name(CC.GREEN + "Manage Player Tags").lore(CC.YELLOW + "Click to manage a player's tags!").asGuiItem()
                .setAction(event -> {
                    event.getWhoClicked().closeInventory();
                    new QuestionConversation(CC.GREEN + "Please enter the username of the player.", (answer) -> {
                        try {
                            // OfflinePlayer op = Bukkit.getOfflinePlayer(answer);
                            OfflineHelpers.OfflineInfo op = OfflineHelpers.getOfflineInfo(answer);
                            PlayerData data = PlayerManager.getInstance().getDataEvenIfOffline(op.getUniqueId(), false);
                            if (data == null) {
                                event.getWhoClicked().sendMessage(CC.RED + "That player does not exist!");
                                return Prompt.END_OF_CONVERSATION;
                            }
                            data.loadIfNot();

                            new ManagePlayerTagsMenu(data).open((Player) event.getWhoClicked());
                            return Prompt.END_OF_CONVERSATION;
                        } catch (Exception e) {
                            e.printStackTrace();
                            event.getWhoClicked().sendMessage(CC.RED + "An error occurred!");
                            return Prompt.END_OF_CONVERSATION;
                        }
                    }).start((Player) event.getWhoClicked());
                });
    }

    @Override
    public void populateGui(Gui gui, Player player) {
        gui.setItem(11, manageTagsButton());
        gui.setItem(13, createTagButton());
        gui.setItem(15, managePlayerTagsButton());
        gui.getFiller().fill(Menu.PLACEHOLDER_ITEM);
    }

}
