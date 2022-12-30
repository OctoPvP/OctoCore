package net.octopvp.octocore.core.menus.grant;

import lombok.RequiredArgsConstructor;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.guis.PaginatedGui;
import net.octopvp.agile.menu.Menu;
import net.octopvp.agile.menu.PaginatedMenu;
import net.octopvp.octocore.common.object.ServerData;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.GrantProcedureState;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.Skulls;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class GrantServerMenu extends PaginatedMenu<PaginatedGui> {
    private final PlayerData d;
    private final Menu<?> previous;

    public GuiItem globalButton() {
        return ItemBuilder.skull().name(CC.AQUA + "Global")
                .lore(CC.GREEN + "Selecting this will make the rank server-wide")
                .texture(Skulls.GLOBE_BASE_64)
                .asGuiItem(event -> {
                    PlayerData playerData = PlayerManager.getInstance().getData(event.getWhoClicked().getUniqueId());
                    if (playerData == null) {
                        event.getWhoClicked().closeInventory();
                        return;
                    }
                    if (playerData.getGrantProcedure() == null) {
                        event.getWhoClicked().closeInventory();
                        return;
                    }
                    playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.DURATION);
                    playerData.getGrantProcedure().setServer("Global");
                    event.getWhoClicked().sendMessage(Lang.GRANT_SERVER_SET.getMsg("Global"));
                    new DurationMenu(d).open((Player) event.getWhoClicked());
                });
    }

    public GuiItem serverButton(final ServerData serverData) {
        return ItemBuilder.from(Material.PAPER).name(serverData.getServerName())
                .lore(CC.GREEN + "Click to select " + serverData.getServerName() + " as the server to grant the rank on.")
                .asGuiItem(event -> {
                    PlayerData playerData = PlayerManager.getInstance().getData(event.getWhoClicked().getUniqueId());
                    if (playerData == null) {
                        event.getWhoClicked().closeInventory();
                        return;
                    }
                    if (playerData.getGrantProcedure() == null) {
                        event.getWhoClicked().closeInventory();
                        return;
                    }
                    playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.DURATION);
                    playerData.getGrantProcedure().setServer(serverData.getServerName());
                    event.getWhoClicked().sendMessage(Lang.GRANT_SERVER_SET.getMsg(serverData.getServerName()));
                    new DurationMenu(d).open((Player) event.getWhoClicked());
                });
    }

    @Override
    public Menu<?> getBackMenu() {
        return previous;
    }

    @Override
    public void addStaticButtons() {
        gui.setItem(0, globalButton());
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> items = new ArrayList<>();
        items.add(globalButton());
        OctoCore.getInstance().getServerManager().getConnectedServers().forEach(server -> items.add(serverButton(server))); //TODO make this work on offline servers.
        return items;
    }

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Choose active server")
                .rows(6)
                .create();
    }

}
