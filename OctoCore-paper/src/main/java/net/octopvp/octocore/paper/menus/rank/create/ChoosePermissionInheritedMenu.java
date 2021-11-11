package net.octopvp.octocore.paper.menus.rank.create;

import com.google.common.collect.Lists;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.objects.builders.RankBuilder;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.SoundUtil;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.PlaceholderButton;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class ChoosePermissionInheritedMenu extends Menu {
    private RankBuilder rankBuilder;
    private final Consumer<RankBuilder> callback;
    private Menu instance = this;

    public ChoosePermissionInheritedMenu(RankBuilder rankBuilder, Consumer<RankBuilder> callback) {
        this.rankBuilder = rankBuilder;
        this.callback = callback;
    }

    @Override
    public List<Button> getButtons(Player player) {
        return Lists.newArrayList(new PlaceHolderButton(),new PermissionsButton(),new Donebutton());
    }

    public class PermissionsButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.IRON_INGOT).name(CC.AQUA + "Permissions").lore(CC.SEPARATOR,CC.AQUA + "Total Permissions: " + CC.YELLOW + rankBuilder.getRank().getNodes().size(),CC.AQUA + "Total Allowed Permissions: " + CC.YELLOW + rankBuilder.getRank().getAllowedPermissions().size(),CC.AQUA + "Total Negated Permissions: " + CC.YELLOW + rankBuilder.getRank().getNegatedPermissions().size(),CC.SEPARATOR).build();
        }

        @Override
        public int getSlot() {
            return 11;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            new CreateRankManagePermissionsMenu(instance, rankBuilder,(b)->{
                rankBuilder = b;
                open(player);
                SoundUtil.playPing(player);
            }).open(player);
            SoundUtil.playPing(player);
        }
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton() {
            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                callback.accept(rankBuilder);
            }

            @Override
            public int getSlot() {
                return 22;
            }
        };
    }
    public class Donebutton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.EMERALD_BLOCK).name(CC.GREEN + "Done!").build();
        }

        @Override
        public int getSlot() {
            return 26;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            super.onClick(player, slot, clickType);
            SoundUtil.playPing(player);
            callback.accept(rankBuilder);
        }
    }

    @Override
    public String getName(Player player) {
        return CC.GREEN + "Choose an action.";
    }
    public class PlaceHolderButton extends PlaceholderButton {
        @Override
        public int[] getSlots() {
            List<Integer> a = new ArrayList<>();
            IntStream.range(0,26).forEach((i)->{
                if (!(i == 11 || i == 15))
                    a.add(i);
            });
            return a.stream().mapToInt(i ->i).toArray();
        }
    }
}
