package net.octopvp.octocore.core.menus.settings;

import com.cryptomorin.xseries.XMaterial;
import lombok.RequiredArgsConstructor;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.menu.Menu;
import net.octopvp.octocore.common.object.WorldTime;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.Skulls;
import net.octopvp.octocore.core.utils.SoundUtil;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@RequiredArgsConstructor
public class SettingsMenu extends Menu<Gui> {
    private final PlayerData data;
    private boolean changed;

    @Override
    public Gui createGui(Player player) {
        return (Gui) Gui.gui()
                .title("Settings")
                .rows(3)
                .create()
                .setCloseGuiAction(event -> {
                    if (changed) {
                        data.save();
                    }
                });
    }


    public GuiItem changeTimeButton() {
        WorldTime time = data.getWorldTime();
        return ItemBuilder.from(XMaterial.COMPASS)
                .name(CC.GREEN + "Current time: " + data.getWorldTime().getFormattedName())
                .lore(
                        CC.SEPARATOR,
                        (time == WorldTime.DEFAULT ? CC.GRAY + CC.SELECTOR_ARROW : "") + CC.AQUA + " Default",
                        (time == WorldTime.SUNRISE ? CC.GRAY + CC.SELECTOR_ARROW : "") + CC.AQUA + " Sunrise",
                        (time == WorldTime.DAY ? CC.GRAY + CC.SELECTOR_ARROW : "") + CC.AQUA + " Day",
                        (time == WorldTime.SUNSET ? CC.GRAY + CC.SELECTOR_ARROW : "") + CC.AQUA + " Sunset",
                        (time == WorldTime.NIGHT ? CC.GRAY + CC.SELECTOR_ARROW : "") + CC.AQUA + " Night",
                        CC.SEPARATOR,
                        "",
                        CC.YELLOW + "Left Click to cycle forward!",
                        CC.YELLOW + "Right Click to cycle backwards!"
                )
                .asGuiItem(event -> {
                    Player player = (Player) event.getWhoClicked();
                    changed = true;
                    SoundUtil.playPing(player);
                    WorldTime type = data.getWorldTime();
                    ClickType clickType = event.getClick();
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
                });
    }

    public GuiItem toggleGlobalChat() {
        return ItemBuilder.skull().texture(Skulls.GLOBE_BASE_64)
                .name(data.getMessageSettings().isGlobalChat() ? CC.GREEN + "Global chat: ON" : CC.GREEN + "Global chat: OFF")
                .lore(data.getMessageSettings().isGlobalChat() ? CC.YELLOW + "Click to toggle global chat OFF" : CC.YELLOW + "Click to toggle global chat ON")
                .asGuiItem(event -> {
                    Player player = (Player) event.getWhoClicked();
                    changed = true;
                    data.getMessageSettings().setGlobalChat(!data.getMessageSettings().isGlobalChat());
                    player.sendMessage(data.getMessageSettings().isGlobalChat() ? Lang.TOGGLE_ON.getMsg("Global chat") : Lang.TOGGLE_OFF.getMsg("Global chat"));
                    update(player);
                });
    }

    public GuiItem toggleMessages() {
        return ItemBuilder.from(XMaterial.REPEATER)
                .name(data.getMessageSettings().isMessagesOff() ? CC.GREEN + "Private messages: OFF" : CC.GREEN + "Private messages: ON")
                .lore(data.getMessageSettings().isMessagesOff() ? CC.YELLOW + "Click to toggle messages on" : CC.YELLOW + "Click to toggle messages OFF")
                .asGuiItem(event -> {
                    Player player = (Player) event.getWhoClicked();
                    changed = true;
                    data.getMessageSettings().setMessagesOff(!data.getMessageSettings().isMessagesOff());
                    player.sendMessage(data.getMessageSettings().isMessagesOff() ? Lang.TOGGLE_OFF.getMsg("Private Messages") : Lang.TOGGLE_ON.getMsg("Private Messages"));
                    update(player);
                });
    }

    @Override
    public void populateGui(Gui gui, Player player) {
        gui.setItem(11, changeTimeButton());
        gui.setItem(13, toggleGlobalChat());
        gui.setItem(15, toggleMessages());
        gui.getFiller().fill(PLACEHOLDER_ITEM);
    }
}
