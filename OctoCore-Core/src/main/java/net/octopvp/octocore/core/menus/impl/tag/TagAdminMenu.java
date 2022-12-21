package net.octopvp.octocore.core.menus.impl.tag;

import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.conversations.QuestionConversation;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.menus.Menu;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
                            OfflinePlayer op = Bukkit.getOfflinePlayer(answer);
                            PlayerData data = PlayerManager.getInstance().getOfflineData(op.getUniqueId());
                            if (data == null) {
                                ((Player) event.getWhoClicked()).sendMessage(CC.RED + "That player does not exist!");
                                return Prompt.END_OF_CONVERSATION;
                            }
                            data.load();

                            new ManagePlayerTagsMenu(data).open((Player) event.getWhoClicked());
                            return Prompt.END_OF_CONVERSATION;
                        } catch (Exception e) {
                            e.printStackTrace();
                            ((Player) event.getWhoClicked()).sendMessage(CC.RED + "An error occurred!");
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
        gui.getFiller().fill(ItemBuilder.from(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).name(" ").asGuiItem());
    }

}
