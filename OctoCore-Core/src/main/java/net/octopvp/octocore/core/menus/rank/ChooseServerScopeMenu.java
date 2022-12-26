package net.octopvp.octocore.core.menus.rank;

import lombok.RequiredArgsConstructor;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.guis.PaginatedGui;
import net.octopvp.agile.menu.Menu;
import net.octopvp.agile.menu.PaginatedMenu;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.common.object.ServerData;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.conversations.QuestionConversation;
import net.octopvp.octocore.core.utils.SoundUtil;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class ChooseServerScopeMenu extends PaginatedMenu<PaginatedGui> {
    private final Consumer<ServerContext> callback;
    private final Menu<?> previousMenu;

    @Override
    public void populateGui(PaginatedGui gui, Player player) {
        super.populateGui(gui, player);
        gui.setItem(0, globalButton());
        gui.setItem(1, customButton());
    }

    @Override
    public Menu<?> getBackMenu() {
        return previousMenu;
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
                .title(CC.AQUA + "Choose server scope")
                .rows(3)
                .create();
    }

    public GuiItem serverButton(ServerData serverData) {
        return ItemBuilder.from(Material.PAPER)
                .name(serverData.getServerName())
                .lore(CC.SEPARATOR, CC.GREEN + "Click to select " + serverData.getServerName() + " as the server to grant the rank on.")
                .asGuiItem(event -> {
                    SoundUtil.playPing((Player) event.getWhoClicked());
                    callback.accept(new ServerContext(serverData.getServerName()));
                });
    }

    public GuiItem customButton() {
        return ItemBuilder.from(Material.SIGN)
                .name(CC.AQUA + "Custom server")
                .lore(CC.YELLOW + "Click to set a custom server scope.")
                .asGuiItem(event -> {
                    promptScope((Player) event.getWhoClicked());
                });
    }

    public void promptScope(Player player) {
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

    public GuiItem globalButton() {
        return ItemBuilder.from(Material.SKULL_ITEM)
                .name(CC.AQUA + "Global")
                .lore(CC.GREEN + "Selecting this will make the rank server-wide")
                .asGuiItem(event -> {
                    SoundUtil.playPing((Player) event.getWhoClicked());
                    callback.accept(ServerContext.global());
                });
    }
}
