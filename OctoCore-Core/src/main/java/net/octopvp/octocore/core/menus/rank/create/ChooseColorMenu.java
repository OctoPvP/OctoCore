package net.octopvp.octocore.core.menus.rank.create;

import dev.octomc.agile.menu.PaginatedMenu;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.builders.RankBuilder;
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
        return ItemBuilder.from(WoolUtils.convertChatColorToWoolMaterial(chatColor).parseMaterial())
                //.name(chatColor + StringUtils.capitalizeFirstDeep(chatColor.name().replace("_", " ")))
                .name(Component.text(chatColor + StringUtils.capitalizeFirstDeep(chatColor.name().replace("_", " "))))
                //.lore(CC.AQUA + "Click to select this as the color.")
                .lore(Component.text("Click to select this as the color.").color(NamedTextColor.AQUA))
                .asGuiItem(event -> {
                    builder.setColor(chatColor);
                    SoundUtil.playPing((Player) event.getWhoClicked());
                    callback.accept(builder);
                });
    }

    @Override
    public void addStaticButtons() {
        gui.updateItem(gui.getRows(), 4, ItemBuilder.from(Material.ARROW)
                //.name(CC.YELLOW + "Back")
                .name(Component.text("Back").color(NamedTextColor.YELLOW))
                .asGuiItem(event -> callback.accept(builder)));
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
                .rows(6)
                .create();
    }
}
