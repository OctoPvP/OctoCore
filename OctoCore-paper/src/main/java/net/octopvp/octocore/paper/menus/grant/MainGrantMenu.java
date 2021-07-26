package net.octopvp.octocore.paper.menus.grant;

import com.google.common.collect.Lists;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.PlaceholderButton;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class MainGrantMenu extends Menu {
    private final PlayerData playerData;

    public MainGrantMenu(PlayerData playerData) {
        this.playerData = playerData;
    }

    @Override
    public List<Button> getButtons(Player player) {
        return Lists.newArrayList(new ViewGrantsButton(),new AddGrantButton(),new PlaceHolderButton());
    }

    @Override
    public String getName(Player player) {
        return CC.GREEN + "Choose an action.";
    }
    public class ViewGrantsButton extends Button{

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.PAPER).name(CC.AQUA + "View " + playerData.getName() + "'s grants").lore("",CC.SEPARATOR,CC.YELLOW + playerData.getActiveGrants().size() + CC.GREEN + " currently active grants.",CC.YELLOW + playerData.getGrants().size() + CC.GREEN + " total grants",CC.SEPARATOR).build();
        }

        @Override
        public int getSlot() {
            return 11;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            new GrantsMenu(playerData).open(player);
        }
    }
    public class AddGrantButton extends Button{

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.BOOK_AND_QUILL).name(CC.GREEN + "Add a new grant").build();
        }

        @Override
        public int getSlot() {
            return 15;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            new AddGrantMenu(playerData).open(player);
        }
    }
    public class PlaceHolderButton extends PlaceholderButton {
        @Override
        public int[] getSlots() {
            List<Integer> a = new ArrayList<>();
            IntStream.range(0,27).forEach((i)->{
                if (!(i == 11 || i == 15))
                    a.add(i);
            });
            return a.stream().mapToInt(i ->i).toArray();
        }
    }
}
