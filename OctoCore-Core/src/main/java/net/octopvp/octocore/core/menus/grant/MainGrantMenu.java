package net.octopvp.octocore.core.menus.grant;

import com.cryptomorin.xseries.XMaterial;
import dev.octomc.agile.menu.Menu;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MainGrantMenu extends Menu<Gui> {
    private final PlayerData playerData;

    public MainGrantMenu(PlayerData playerData) {
        this.playerData = playerData;
        if (!playerData.isOnlineThisServer())
            playerData.load();
    }

    public GuiItem addGrant() {
        return ItemBuilder.from(XMaterial.WRITABLE_BOOK.parseMaterial() != null ? XMaterial.WRITABLE_BOOK.parseMaterial() : Material.BOOK)
                //.name(CC.GREEN + "Add a new grant")
                .name(Component.text("Add a new grant").color(NamedTextColor.GREEN))
                .asGuiItem(event -> new AddGrantMenu(playerData).open((Player) event.getWhoClicked()));
    }

    public GuiItem viewGrants() {
        return ItemBuilder.from(Material.PAPER)
                //.name(CC.AQUA + "View " + playerData.getName() + "'s grants")
                .name(Component.text("View " + playerData.getName() + "'s grants").color(NamedTextColor.AQUA))
                //.lore("", CC.SEPARATOR, CC.YELLOW + playerData.getActiveGrants().size() + CC.GREEN + " currently active grants.", CC.YELLOW + playerData.getGrants().size() + CC.GREEN + " total grants", CC.SEPARATOR)
                .lore(
                        Component.text(""),
                        Component.text("-----"),
                        Component.text(playerData.getActiveGrants().size() + " currently active grants.").color(NamedTextColor.YELLOW),
                        Component.text(playerData.getGrants().size() + " total grants").color(NamedTextColor.GREEN),
                        Component.text("-----")
                )
                .asGuiItem(event -> new GrantsMenu(playerData).open((Player) event.getWhoClicked()));
    }

    @Override
    public Gui createGui(Player player) {
        return Gui.gui()
                .title("Choose an action.")
                .rows(3)
                .create();
    }

    @Override
    public void populateGui(Gui gui, Player player) {
        try {
            gui.setItem(11, addGrant());
            gui.setItem(15, viewGrants());
            gui.getFiller().fill(PLACEHOLDER_ITEM);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
