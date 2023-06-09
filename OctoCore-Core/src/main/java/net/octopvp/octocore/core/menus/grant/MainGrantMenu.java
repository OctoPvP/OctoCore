package net.octopvp.octocore.core.menus.grant;

import com.cryptomorin.xseries.XMaterial;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.menu.Menu;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MainGrantMenu extends Menu<Gui> {
    private final PlayerData playerData;

    public MainGrantMenu(PlayerData playerData) {
        this.playerData = playerData;
    }

    public GuiItem addGrant() {
        return ItemBuilder.from(XMaterial.WRITABLE_BOOK.parseMaterial() != null ? XMaterial.WRITABLE_BOOK.parseMaterial() : Material.BOOK)
                .name(CC.GREEN + "Add a new grant")
                .asGuiItem(event -> new AddGrantMenu(playerData).open((Player) event.getWhoClicked()));
    }

    public GuiItem viewGrants() {
        return ItemBuilder.from(Material.PAPER)
                .name(CC.AQUA + "View " + playerData.getName() + "'s grants")
                .lore("", CC.SEPARATOR, CC.YELLOW + playerData.getActiveGrants().size() + CC.GREEN + " currently active grants.", CC.YELLOW + playerData.getGrants().size() + CC.GREEN + " total grants", CC.SEPARATOR)
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
        System.out.println("Populating main grant menu.");
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
