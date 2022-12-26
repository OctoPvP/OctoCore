package net.octopvp.octocore.core.module.impl.punishments.menus.alts;

import lombok.AllArgsConstructor;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.guis.PaginatedGui;
import net.octopvp.agile.menu.PaginatedMenu;
import net.octopvp.octocore.common.object.punish.Alt;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class AltsMenu extends PaginatedMenu<PaginatedGui> {
    private PlayerData playerData;

    /*
    @Override
    public String getPagesTitle(Player player) {
        return CC.translate("&7" + playerData.getName() + "'s alts");
    }

    @Override
    public List<Button> getEveryMenuSlots(Player player) {
        List<Button> slots = new ArrayList<>();

        slots.add(new Button() {

            @Override
            public ItemStack getItem(Player player) {
                ItemBuilder item = new ItemBuilder(Material.PAPER);
                item.setName(CC.MAIN + playerData.getName() + "'s possible alts");
                item.addLoreLine("");
                item.addLoreLine(CC.VALUE + "Alts amount&7: " + CC.SECONDARY + playerData.getAlts().size());
                item.addLoreLine(CC.VALUE + "Banned alts&7: " + CC.SECONDARY + playerData.getAlts().stream().filter(Alt::isBanned).collect(Collectors.toList()).size());
                item.addLoreLine(" ");

                return item.toItemStack();
            }

            @Override
            public int getSlot() {
                return 4;
            }

        });
        return slots;
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton.DefaultBackButton(this);
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> slots = new ArrayList<>();

        playerData.getAlts().forEach(alt -> slots.add(new AltButton(alt)));

        return slots;
    }
     */
    public static GuiItem altButton(final Alt alt) {
        return ItemBuilder.skull()
                .owner(Bukkit.getOfflinePlayer(alt.getUniqueId()))
                .name(CC.MAIN + alt.getName() + "&7(" + (alt.isBanned() ? "&cBanned" : Bukkit.getPlayer(alt.getName()) == null ? "&eOffline" : "&aOnline") + "&7)")
                .asGuiItem();
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> items = new ArrayList<>();
        playerData.getAlts().forEach(alt -> items.add(altButton(alt)));
        return items;
    }

    @Override
    public void populateGui(PaginatedGui gui, Player player) {
        super.populateGui(gui, player);
        gui.setItem(4, ItemBuilder.from(Material.PAPER)
                .name(CC.MAIN + playerData.getName() + "'s possible alts")
                .lore("", CC.VALUE + "Alts amount&7: " + CC.SECONDARY + playerData.getAlts().size(),
                        CC.VALUE + "Banned alts&7: " + CC.SECONDARY + playerData.getAlts().stream().filter(Alt::isBanned).collect(Collectors.toList()).size(), " ")
                .asGuiItem());
    }

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title(playerData.getName() + "'s alts")
                .rows(6)
                .create();
    }
}
