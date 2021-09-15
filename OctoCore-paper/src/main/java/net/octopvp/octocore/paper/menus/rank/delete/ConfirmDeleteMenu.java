package net.octopvp.octocore.paper.menus.rank.delete;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.objects.permissions.Rank;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.item.WoolUtils;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.PlaceholderButton;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import net.octopvp.octocore.paper.utils.runnable.Countdown;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ConfirmDeleteMenu extends Menu {
    private final Rank toDelete;

    public ConfirmDeleteMenu(Rank toDelete) {
        this.toDelete = toDelete;
        counter();
    }
    private int counter = 5;
    public void counter() {
        new Count(5).start();
    }

    class Count extends Countdown{

        public Count(int time) {
            super(time);
        }

        @Override
        public void count(int current) {
            counter = current;
        }
    }
    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(new PlaceHolderButton());
        buttons.add(new NoButton());
        if (counter <= 0){
            buttons.add(new ConfirmButton());
        }
        else buttons.add(new WaitButton());
        buttons.add(new InfoButton());
        return buttons;
    }

    @Override
    public String getName(Player player) {
        return CC.RED + "Confirm Delete ";
    }
    public class ConfirmButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.STAINED_GLASS).durability(WoolUtils.convertChatColorToWoolData(ChatColor.GREEN)).name(CC.GREEN + "Confirm").build();
        }

        @Override
        public int getSlot() {
            return 11;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            super.onClick(player, slot, clickType);
            RankManager.delete(toDelete);
            player.closeInventory();
        }
    }
    public class NoButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.STAINED_GLASS).durability(WoolUtils.convertChatColorToWoolData(ChatColor.RED)).name(CC.RED + "No").build();
        }

        @Override
        public int getSlot() {
            return 15;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            player.sendMessage(CC.RED + "Canceled!");
        }
    }
    public class InfoButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.BEACON).name(CC.RED + "Are you sure you want to delete this rank?").lore(
                    CC.SEPARATOR,
                    CC.AQUA + "Rank: " + CC.YELLOW + toDelete.getName(),
                    CC.AQUA + "Display Name: " + CC.YELLOW + toDelete.getDisplayName(),
                    CC.AQUA + "Weight: " + CC.YELLOW + toDelete.getWeight(),
                    CC.AQUA + "Rank Type: " + CC.YELLOW + toDelete.getRankType(),
                    CC.SEPARATOR
            ).build();
        }

        @Override
        public int getSlot() {
            return 13;
        }
    }
    public class WaitButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.STAINED_CLAY).durability(WoolUtils.convertChatColorToWoolData(ChatColor.RED)).name(CC.D_RED + "Please wait " + counter + " seconds...").build();
        }

        @Override
        public int getSlot() {
            return 11;
        }
    }
    public class PlaceHolderButton extends PlaceholderButton {

        @Override
        public int[] getSlots() {
            List<Integer> a = new ArrayList<>();
            IntStream.range(0,27).forEach((i)->{
                if (!(i == 11 || i == 13 || i == 15))
                    a.add(i);
            });
            return a.stream().mapToInt(i ->i).toArray();
        }
    }
}
