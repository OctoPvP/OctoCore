package net.octopvp.octocore.core.module.impl.punishments.menus.alts;

import lombok.AllArgsConstructor;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.guis.PaginatedGui;
import net.octopvp.agile.menu.Menu;
import net.octopvp.agile.menu.PaginatedMenu;
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
                        CC.GRAY + "This menu is showing all " + CC.SECONDARY + playerData.getName() + "'s " + CC.GRAY + "alts",
                        CC.GRAY + "that are recorded on ip addresses",
                        CC.GRAY + "that user were joining from.",
                        " ",
                        CC.GRAY + "- " + CC.RED + "This is not secure and doesn't mean",
                        CC.GRAY + "- " + CC.RED + "that the user is actually alting!",
                        " ",
                        CC.VALUE + "Alts amount" + CC.GRAY + ": " + CC.SECONDARY + playerData.getAlts().size(),
                        CC.VALUE + "Banned alts" + CC.GRAY + ": " + CC.SECONDARY + playerData.getAlts().stream().filter(Alt::isBanned).collect(Collectors.toList()).size(),
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
        playerData.getAlts().forEach(AltsMenu::altButton);
        return items;
    }

    @Override
    public Menu<?> getBackMenu() {
        return previousMenu;
    }
}
