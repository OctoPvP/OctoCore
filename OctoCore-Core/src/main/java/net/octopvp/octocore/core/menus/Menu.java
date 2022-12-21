package net.octopvp.octocore.core.menus;

import net.octopvp.agile.guis.BaseGui;
import org.bukkit.entity.Player;

public abstract class Menu<T extends BaseGui> {
    public abstract T createGui(Player player);

    public abstract void populateGui(T gui, Player player);

    public void open(Player player) {
        T gui = createGui(player);
        populateGui(gui, player);
        gui.open(player);
    }
}
