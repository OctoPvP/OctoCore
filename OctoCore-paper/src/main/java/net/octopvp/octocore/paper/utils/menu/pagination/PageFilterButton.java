package net.octopvp.octocore.paper.utils.menu.pagination;

import lombok.AllArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.Button;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class PageFilterButton<T> extends Button {

	private FilterablePaginatedMenu<T> menu;

	@Override
	public ItemStack getButtonItem(Player player) {
		if (menu.getFilters() == null || menu.getFilters().isEmpty()) {
			return new ItemStack(Material.AIR);
		}

		List<String> lore = new ArrayList<>();
		lore.add(CC.SEPARATOR);

		for (PageFilter filter : menu.getFilters()) {
			String color;
			String decoration = "";
			String icon;

			if (filter.isEnabled()) {
				color = CC.GREEN;
				icon = StringEscapeUtils.unescapeJava("\u2713");
			} else {
				color = CC.RED;
				icon = StringEscapeUtils.unescapeJava("\u2717");
			}

			if (menu.getFilters().get(menu.getScrollIndex()).equals(filter)) {
				decoration = CC.YELLOW + StringEscapeUtils.unescapeJava("» ") + " ";
			}

			lore.add(decoration + color + icon + " " + filter.getName());
		}

		lore.add(CC.SEPARATOR);
		lore.add("&eLeft click to scroll.");
		lore.add("&eRight click to toggle a filter.");
		lore.add(CC.SEPARATOR);

		return new ItemBuilder(Material.HOPPER)
				.name("&7Filters")
				.lore(lore)
				.build();
	}

	@Override
	public void click(Player player, ClickType clickType) {
		if (menu.getFilters() == null || menu.getFilters().isEmpty()) {
			player.sendMessage(CC.RED + "There are no filters.");
		} else {
			if (clickType == ClickType.LEFT) {
				if (menu.getScrollIndex() == menu.getFilters().size() - 1) {
					menu.setScrollIndex(0);
				} else {
					menu.setScrollIndex(menu.getScrollIndex() + 1);
				}
			} else if (clickType == ClickType.RIGHT) {
				PageFilter<T> filter = menu.getFilters().get(menu.getScrollIndex());
				filter.setEnabled(!filter.isEnabled());
			}
		}
	}

	@Override
	public boolean shouldUpdate(Player player, ClickType clickType) {
		return true;
	}

}
