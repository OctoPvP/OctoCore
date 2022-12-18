package net.octopvp.octocore.core.menus.impl.rank.create;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.objects.builders.RankBuilder;
import net.octopvp.octocore.core.utils.SoundUtil;
import net.octopvp.octocore.core.utils.item.WoolUtils;
import net.octopvp.octocore.core.utils.menu.buttons.Button;
import net.octopvp.octocore.core.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.core.utils.menu.menu.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class ChooseColorMenu extends PaginatedMenu {
    private final RankBuilder builder;
    private final Consumer<RankBuilder> callback;
    private int i = 0;

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
            public void clicked(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
                callback.accept(builder);
            }
        };
    }

    @RequiredArgsConstructor
    private class ColorButton extends Button {
        private final ChatColor chatColor;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.WOOL).durability(WoolUtils.convertChatColorToWoolData(chatColor)).name(chatColor + StringUtils.capatalizeFirstDeep(chatColor.name().replace("_", " "))).lore(CC.AQUA + "Click to select this as the color.").build();
        }

        @Override
        public int getSlot() {
            return i++;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            builder.setColor(chatColor);
            SoundUtil.playPing(player);
            callback.accept(builder);
        }
    }
}
