package net.octopvp.octocore.paper.utils.menu.pagination;

import lombok.AllArgsConstructor;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@AllArgsConstructor
public class PageInfoButton extends Button {

	private PaginatedMenu menu;

	@Override
	public ItemStack getButtonItem(Player player) {
		int pages = menu.getPages(player);

		return new ItemBuilder(Material.PAPER)
				.name(ChatColor.GOLD + "Page Info")
				.lore(
						Arrays.asList(ChatColor.YELLOW + "You are viewing page #" + menu.getPage() + ".",
								ChatColor.YELLOW + (pages == 1 ? "There is 1 page." : "There are " + pages + " pages."),
								"",
								ChatColor.YELLOW + "Middle click here to",
								ChatColor.YELLOW + "view all pages.")
				)
				.build();
	}

	@Override
	public void click(Player player, ClickType clickType) {
		if (clickType == ClickType.RIGHT) {
			new ViewAllPagesMenu(this.menu).openMenu(player);
			playNeutral(player);
		}
	}

}
