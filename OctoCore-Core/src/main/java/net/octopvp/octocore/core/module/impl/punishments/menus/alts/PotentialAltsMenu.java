package net.octopvp.octocore.core.module.impl.punishments.menus.alts;

import lombok.AllArgsConstructor;
import dev.octomc.agile.builder.item.ItemBuilder;
import dev.octomc.agile.guis.Gui;
import dev.octomc.agile.guis.GuiItem;
import dev.octomc.agile.guis.PaginatedGui;
import dev.octomc.agile.menu.Menu;
import dev.octomc.agile.menu.PaginatedMenu;
import net.octopvp.octocore.common.interfaces.IPunishData;
import net.octopvp.octocore.common.object.punish.Alt;
import net.octopvp.octocore.common.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class PotentialAltsMenu extends PaginatedMenu<PaginatedGui> {
    private IPunishData playerData;
    private Menu<?> previousMenu;

    @Override
    public void addStaticButtons() {
        gui.setItem(4, ItemBuilder.from(Material.PAPER)
                .name(CC.MAIN + "About")
                .lore(
                        " ",
                        CC.GRAY + "All alts of " + playerData.getName() + ":",
                        CC.VALUE + "Alts amount" + CC.GRAY + ": " + CC.YELLOW + playerData.getAlts().size(),
                        CC.VALUE + "Banned alts" + CC.GRAY + ": " + CC.YELLOW + playerData.getAlts().stream().filter(Alt::isBanned).collect(Collectors.toList()).size(),
                        " "
                ).asGuiItem());
    }

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Alts of " + playerData.getName())
                .rows(6)
                .create();
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> items = new ArrayList<>();
        // playerData.getAlts().forEach(AltsMenu::altButton);
        playerData.getAlts().forEach(alt -> items.add(AltsMenu.altButton(alt)));
        return items;
    }

    @Override
    public Menu<?> getBackMenu() {
        return previousMenu;
    }
}
