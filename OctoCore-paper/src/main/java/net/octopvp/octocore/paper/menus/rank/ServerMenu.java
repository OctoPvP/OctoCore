package net.octopvp.octocore.paper.menus.rank;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.objects.ServerData;
import net.octopvp.octocore.paper.objects.builders.RankBuilder;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.Skulls;
import net.octopvp.octocore.paper.utils.SoundUtil;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
class ServerMenu extends PaginatedMenu {
    private final RankBuilder builder;

    @Override
    public String getPagesTitle(Player player) {
        return CC.AQUA + "Choose server";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(new GlobalButton());
        OctoCore.getServerManager().getConnectedServers().forEach(server -> buttons.add(new ServerButton(server))); //TODO make this work on offline servers.
        return buttons;
    }

    @Override
    public List<Button> getEveryMenuSlots(Player player) {
        return Lists.newArrayList(new GlobalButton());
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton() {
            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new CreateRankMenu(builder).open(player);
            }
        };
    }

    private int i = 0;

    @RequiredArgsConstructor
    private class ServerButton extends Button {
        private final ServerData serverData;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.PAPER).name(serverData.getServerName()).lore(CC.SEPARATOR, CC.GREEN + "Click to select " + serverData.getServerName() + " as the server to create the rank on.").build();
        }

        @Override
        public int getSlot() {
            return i++;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            builder.setScope(new ServerContext(serverData.getServerName()));
            new CreateRankMenu(builder).open(player);
            SoundUtil.playPing(player);
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
        public void onClick(Player player, int slot, ClickType clickType) {
            builder.setScope(new ServerContext("Global"));
            new CreateRankMenu(builder).open(player);
            SoundUtil.playPing(player);
        }
    }
}
