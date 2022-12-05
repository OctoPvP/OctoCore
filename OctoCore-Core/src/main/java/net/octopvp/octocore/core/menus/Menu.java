package net.octopvp.octocore.core.menus;

import net.octopvp.gui.guis.BaseGui;
import org.bukkit.entity.Player;

public abstract class Menu {
    public abstract BaseGui getBaseGui(Player player);

    public abstract void populateGui(BaseGui gui);

    public void open(Player player) {
        BaseGui gui = getBaseGui(player);
        populateGui(gui);
        gui.open(player);
    }
}
