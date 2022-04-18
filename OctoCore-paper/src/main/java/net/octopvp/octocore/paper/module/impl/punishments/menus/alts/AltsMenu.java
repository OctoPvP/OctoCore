package net.octopvp.octocore.paper.module.impl.punishments.menus.alts;

import lombok.AllArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.module.impl.punishments.util.Alt;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class AltsMenu extends PaginatedMenu {
    private PlayerData playerData;

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

    @AllArgsConstructor
    private class AltButton extends Button {
        private Alt alt;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.SKULL_ITEM);
            item.setDurability(3);
            item.setName(CC.MAIN + alt.getName() + "&7(" + (alt.isBanned() ? "&cBanned" : Bukkit.getPlayer(alt.getName()) == null ? "&eOffline" : "&aOnline") + "&7)");
            return item.toSkullBuilder().withOwner(alt.getUniqueId()).buildSkull();
        }

        @Override
        public int getSlot() {
            return 0;
        }
    }
}
