package net.octopvp.octocore.paper.utils.menu.button;

import net.octopvp.octocore.paper.utils.menu.Button;
import net.octopvp.octocore.paper.utils.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class JumpToMenuButton extends Button {

	private Menu menu;
	private ItemStack itemStack;

	public JumpToMenuButton(Menu menu, ItemStack itemStack) {
		this.menu = menu;
		this.itemStack = itemStack;
	}

	@Override
	public ItemStack getButtonItem(Player player) {
		return itemStack;
	}

	@Override
	public void click(Player player, ClickType clickType) {
		menu.openMenu(player);
	}

}
