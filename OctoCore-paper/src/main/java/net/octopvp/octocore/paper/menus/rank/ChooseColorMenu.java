package net.octopvp.octocore.paper.menus.rank;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.objects.builders.RankBuilder;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.SoundUtil;
import net.octopvp.octocore.paper.utils.item.WoolUtils;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ChooseColorMenu extends PaginatedMenu {
    private final RankBuilder builder;
    @Override
    public String getPagesTitle(Player player) {
        return CC.GREEN + "Choose color";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        for (ChatColor value : ChatColor.values()) {
            buttons.add(new ColorButton(value));
        }
        return buttons;
    }

    @Override
    public List<Button> getEveryMenuSlots(Player player) {
        return null;
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton() {
            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                new CreateRankMenu(builder).open(player);
            }
        };
    }

    private int i = 0;
    @RequiredArgsConstructor
    private class ColorButton extends Button{
        private final ChatColor chatColor;
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.WOOL).durability(WoolUtils.convertChatColorToWoolData(chatColor)).name(chatColor + chatColor.name()).lore(CC.AQUA + "Click to select this as the color.").build();
        }

        @Override
        public int getSlot() {
            return i++;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            builder.setColor(chatColor);
            SoundUtil.playPing(player);
            new CreateRankMenu(builder).open(player);
        }
    }
}
