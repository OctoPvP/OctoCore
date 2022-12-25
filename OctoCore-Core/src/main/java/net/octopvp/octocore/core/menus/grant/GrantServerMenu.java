package net.octopvp.octocore.core.menus.grant;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.object.ServerData;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.GrantProcedureState;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.Skulls;
import net.octopvp.octocore.core.utils.menu.buttons.Button;
import net.octopvp.octocore.core.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.core.utils.menu.menu.PaginatedMenu;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class GrantServerMenu extends PaginatedMenu {
    private final PlayerData d;
    private int i = 0;

    @Override
    public String getPagesTitle(Player player) {
        return "Choose active server";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(new GlobalButton());
        OctoCore.getInstance().getServerManager().getConnectedServers().forEach(server -> buttons.add(new ServerButton(server))); //TODO make this work on offline servers.
        return buttons;
    }

    @Override
    public List<Button> getEveryMenuSlots(Player player) {
        return Lists.newArrayList(new GlobalButton());
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton.DefaultBackButton(previous);
    }

    @RequiredArgsConstructor
    private class ServerButton extends Button {
        private final ServerData serverData;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.PAPER).name(serverData.getServerName()).lore(CC.SEPARATOR, CC.GREEN + "Click to select " + serverData.getServerName() + " as the server to grant the rank on.").build();
        }

        @Override
        public int getSlot() {
            return i++;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            PlayerData data = PlayerManager.getInstance().getData(player.getUniqueId());
            if (data == null) {
                player.closeInventory();
                return;
            }
            if (data.getGrantProcedure() == null) {
                player.closeInventory();
                return;
            }

            data.getGrantProcedure().setGrantProcedureState(GrantProcedureState.DURATION);
            data.getGrantProcedure().setServer(serverData.getServerName());
            player.sendMessage(Lang.GRANT_SERVER_SET.getMsg(serverData.getServerName()));
            new DurationMenu(d).open(player);
        }
    }


    private class GlobalButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.SKULL_ITEM).name(CC.AQUA + "Global").lore(CC.GREEN + "Selecting this will make the rank server-wide").toSkullBuilder().base64Skin(Skulls.GLOBE_BASE_64).buildSkull();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());
            if (playerData == null) {
                player.closeInventory();
                return;
            }
            if (playerData.getGrantProcedure() == null) {
                player.closeInventory();
                return;
            }

            playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.DURATION);
            playerData.getGrantProcedure().setServer("Global");
            player.sendMessage(Lang.GRANT_SERVER_SET.getMsg("Global"));
            new DurationMenu(d).open(player);
        }
    }
}
