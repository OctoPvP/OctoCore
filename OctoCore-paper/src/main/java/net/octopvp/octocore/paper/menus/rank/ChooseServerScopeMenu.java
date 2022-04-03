package net.octopvp.octocore.paper.menus.rank;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.conversations.QuestionConversation;
import net.octopvp.octocore.paper.objects.ServerData;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.Skulls;
import net.octopvp.octocore.paper.utils.SoundUtil;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class ChooseServerScopeMenu extends PaginatedMenu {
    private final Consumer<ServerContext> callback;
    private final Menu previousMenu;
    private int i = 0;

    @Override
    public String getPagesTitle(Player player) {
        return CC.AQUA + "Choose server scope";
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
        return Lists.newArrayList(new GlobalButton(), new CustomButton());
    }

    @Override
    public Button getBackButton(Player player) {
        if (previousMenu == null) {
            return null;
        }
        return new BackButton() {
            @Override
            public void clicked(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
                previousMenu.open(player);
            }
        };
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
            SoundUtil.playPing(player);
            callback.accept(new ServerContext(serverData.getServerName()));
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
            SoundUtil.playPing(player);
            callback.accept(ServerContext.global());
        }
    }

    private class CustomButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.SIGN).name(CC.AQUA + "Custom server").lore(CC.YELLOW + "Click to set a custom server scope.").build();
        }

        @Override
        public int getSlot() {
            return 1;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            super.onClick(player, slot, clickType, event);
            prompt(player);
        }

        private void prompt(Player player) {
            OctoCore.getConversationFactory().withFirstPrompt(new QuestionConversation(Lang.CUSTOM_SERVER_SCOPE.getMsg(), (s) -> {
                if (s.equalsIgnoreCase("cancel") || s.equalsIgnoreCase("exit")) {
                    open(player);
                    player.sendMessage(CC.RED + "Cancelled!");
                    return Prompt.END_OF_CONVERSATION;
                }
                callback.accept(new ServerContext(s));
                SoundUtil.playPing(player);
                return Prompt.END_OF_CONVERSATION;
            })).buildConversation(player).begin();
        }
    }
}
