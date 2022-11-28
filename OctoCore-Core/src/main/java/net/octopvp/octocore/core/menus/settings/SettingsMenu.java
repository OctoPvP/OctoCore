package net.octopvp.octocore.core.menus.settings;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.object.WorldTime;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.ItemBuilder;
import net.octopvp.octocore.core.utils.Skulls;
import net.octopvp.octocore.core.utils.SoundUtil;
import net.octopvp.octocore.core.utils.menu.buttons.Button;
import net.octopvp.octocore.core.utils.menu.menu.Menu;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class SettingsMenu extends Menu {
    private final PlayerData data;
    private boolean changed;

    @Override
    public List<Button> getButtons(Player player) {
        ArrayList<Button> buttons = new ArrayList<>();
        buttons.add(new ToggleMessagesButton());
        buttons.add(new ToggleGlobalChatButton());
        buttons.add(new ChangeTimeButton());
        buttons.add(new Placeholders());
        return buttons;
    }

    @Override
    public String getName(Player player) {
        return "Settings";
    }

    @Override
    public void onClose(Player player) {
        super.onClose(player);
        if (changed) {
            data.save();
        }
    }

    private class Placeholders extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return GLASS;
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public int[] getSlots() {
            List<Integer> a = new ArrayList<>();
            IntStream.range(0, 36).forEach((i) -> {
                if (i != 11 && i != 15 && i != 22)
                    a.add(i);
            });
            return a.stream().mapToInt(i -> i).toArray();
        }
    }

    private class ToggleMessagesButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.DIODE)
                    .name(data.getMessageSettings().isMessagesOff() ? "&aPrivate messages: &cOFF" : "&aPrivate messages: ON")
                    .lore(data.getMessageSettings().isMessagesOff() ? "&eClick to toggle messages on" : "&eClick to toggle messages off")
                    .build();
        }

        @Override
        public int getSlot() {
            return 11;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            changed = true;
            data.getMessageSettings().setMessagesOff(!data.getMessageSettings().isMessagesOff());
            player.sendMessage(data.getMessageSettings().isMessagesOff() ? Lang.TOGGLE_OFF.getMsg("Private Messages") : Lang.TOGGLE_ON.getMsg("Private Messages"));
            update(player);
        }
    }

    private class ToggleGlobalChatButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.SKULL_ITEM).name(data.getMessageSettings().isGlobalChat() ? "&aGlobal chat: ON" : "&aGlobal chat: &cOFF")
                    .lore(data.getMessageSettings().isGlobalChat() ? "&eClick to toggle global chat OFF" : "&eClick to toggle global chat ON")
                    .toSkullBuilder().base64Skin(Skulls.GLOBE_BASE_64).buildSkull();
        }

        @Override
        public int getSlot() {
            return 15;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            changed = true;
            data.getMessageSettings().setGlobalChat(!data.getMessageSettings().isGlobalChat());
            player.sendMessage(data.getMessageSettings().isGlobalChat() ? Lang.TOGGLE_ON.getMsg("Global chat") : Lang.TOGGLE_OFF.getMsg("Global chat"));
            update(player);
        }
    }

    private class ChangeTimeButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            WorldTime time = data.getWorldTime();
            return new ItemBuilder(Material.WATCH).name("&aCurrent time: " + time.getFormattedName())
                    .lore(
                            CC.SEPARATOR,
                            (time == WorldTime.DEFAULT ? CC.GRAY + CC.SELECTOR_ARROW : "") + CC.AQUA + " Default",
                            (time == WorldTime.SUNRISE ? CC.GRAY + CC.SELECTOR_ARROW : "") + CC.AQUA + " Sunrise",
                            (time == WorldTime.DAY ? CC.GRAY + CC.SELECTOR_ARROW : "") + CC.AQUA + " Day",
                            (time == WorldTime.SUNSET ? CC.GRAY + CC.SELECTOR_ARROW : "") + CC.AQUA + " Sunset",
                            (time == WorldTime.NIGHT ? CC.GRAY + CC.SELECTOR_ARROW : "") + CC.AQUA + " Night",
                            CC.SEPARATOR,
                            "",
                            "&eLeft Click to cycle forward!",
                            "&eRight Click to cycle backwards!"
                    )
                    .build();
        }

        @Override
        public int getSlot() {
            return 22;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            changed = true;
            SoundUtil.playPing(player);
            WorldTime type = data.getWorldTime();
            if (clickType == ClickType.LEFT) { // Default -> Sunrise -> Day -> Sunset -> Night -> Default
                if (type == WorldTime.DEFAULT)
                    type = WorldTime.SUNRISE;
                else if (type == WorldTime.SUNRISE)
                    type = WorldTime.DAY;
                else if (type == WorldTime.DAY)
                    type = WorldTime.SUNSET;
                else if (type == WorldTime.SUNSET)
                    type = WorldTime.NIGHT;
                else if (type == WorldTime.NIGHT)
                    type = WorldTime.DEFAULT;
            } else if (clickType == ClickType.RIGHT) { // Night -> Sunset -> Day -> Sunrise -> Default
                if (type == WorldTime.NIGHT)
                    type = WorldTime.SUNSET;
                else if (type == WorldTime.SUNSET)
                    type = WorldTime.DAY;
                else if (type == WorldTime.DAY)
                    type = WorldTime.SUNRISE;
                else if (type == WorldTime.SUNRISE)
                    type = WorldTime.DEFAULT;
                else if (type == WorldTime.DEFAULT)
                    type = WorldTime.NIGHT;
            }
            data.setWorldTime(type);
            data.updateTime(player);
            update(player);
        }
    }
}
