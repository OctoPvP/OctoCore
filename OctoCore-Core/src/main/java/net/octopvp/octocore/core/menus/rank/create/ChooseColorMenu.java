package net.octopvp.octocore.core.menus.rank.create;

import lombok.RequiredArgsConstructor;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.guis.PaginatedGui;
import net.octopvp.agile.menu.Menu;
import net.octopvp.agile.menu.PaginatedMenu;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.builders.RankBuilder;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.utils.SoundUtil;
import net.octopvp.octocore.core.utils.item.WoolUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class ChooseColorMenu extends PaginatedMenu<PaginatedGui> {
    private final RankBuilder builder;
    private final Consumer<RankBuilder> callback;

    public GuiItem colorButton(ChatColor chatColor) {
        return ItemBuilder.from(Material.WOOL)
                .durability(WoolUtils.convertChatColorToWoolData(chatColor)) // TODO replace this with XMaterial
                .name(chatColor + StringUtils.capatalizeFirstDeep(chatColor.name().replace("_", " ")))
                .lore(CC.AQUA + "Click to select this as the color.").asGuiItem(event -> {
                    builder.setColor(chatColor);
                    SoundUtil.playPing((Player) event.getWhoClicked());
                    callback.accept(builder);
                });
    }

    @Override
    public GuiItem getBackButton(Menu<?> menu) {
        return ItemBuilder.from(Material.ARROW)
                .name(CC.RED + "Back")
                .asGuiItem(event -> {
                    callback.accept(builder);
                });
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> items = new ArrayList<>();
        for (ChatColor value : ChatColor.values()) {
            items.add(colorButton(value));
        }
        return items;
    }

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Choose color")
                .rows(7)
                .create();
    }
}
