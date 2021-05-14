package net.octopvp.octocore.paper.utils.menu.pagination;

import lombok.AllArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.Button;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@AllArgsConstructor
public class PageButton extends Button {

	private int mod;
	private PaginatedMenu menu;

	@Override
	public ItemStack getButtonItem(Player player) {
		if (this.mod > 0) {
			if (hasNext(player)) {
				return new ItemBuilder(Material.ARROW)
						.name(ChatColor.GREEN + "Next Page")
						.lore(Arrays.asList(
								CC.GREEN + "Click here to go",
								CC.GREEN + "to the next page."
						))
						.build();
			} else {
				/*
				return new ItemBuilder(Material.BEDROCK)
						.name(ChatColor.GRAY + "Next Page")
						.lore(Arrays.asList(
								CC.RED + "There is no available",
								CC.RED + "next page."
						))
						.build();
				 */
				return new ItemBuilder(Material.STAINED_GLASS_PANE).data(15).displayname(" ").build();
			}
		} else {
			if (hasPrevious(player)) {
				return new ItemBuilder(Material.ARROW)
						.name(ChatColor.GREEN + "Previous Page")
						.lore(Arrays.asList(
								CC.GREEN + "Click here to go",
								CC.GREEN + "to the previous page."
						))
						.build();
			} else {
				/*
				return new ItemBuilder(Material.BEDROCK)
						.name(ChatColor.GRAY + "Previous Page")
						.lore(Arrays.asList(
								CC.RED + "There is no available",
								CC.RED + "previous page."
						))
						.build();
				 */
				return new ItemBuilder(Material.STAINED_GLASS_PANE).data(15).displayname(" ").build();
			}
		}
	}

	@Override
	public void click(Player player, ClickType clickType) {
		if (this.mod > 0) {
			if (hasNext(player)) {
				this.menu.modPage(player, this.mod);
				Button.playNeutral(player);
			} else {
				Button.playFail(player);
			}
		} else {
			if (hasPrevious(player)) {
				this.menu.modPage(player, this.mod);
				Button.playNeutral(player);
			} else {
				Button.playFail(player);
			}
		}
	}

	private boolean hasNext(Player player) {
		int pg = this.menu.getPage() + this.mod;
		return this.menu.getPages(player) >= pg;
	}

	private boolean hasPrevious(Player player) {
		int pg = this.menu.getPage() + this.mod;
		return pg > 0;
	}

}
