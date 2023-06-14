package net.octopvp.octocore.core.menus.tag;

import lombok.RequiredArgsConstructor;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.guis.PaginatedGui;
import net.octopvp.agile.menu.PaginatedMenu;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.objects.PlayerTag;
import net.octopvp.octocore.core.utils.SoundUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MyTagsMenu extends PaginatedMenu<PaginatedGui> {
    private final List<PlayerTag> currentTags;

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Your Tags")
                .rows(6)
                .create();
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        PlayerData data = PlayerManager.getInstance().getData(player.getUniqueId());
        List<GuiItem> items = new ArrayList<>();
        for (PlayerTag tag : currentTags) {
            if (tag == null)
                continue;
            items.add(tagsButton(player, data, tag));
        }
        return items;
    }

    public GuiItem tagsButton(Player player, PlayerData data, PlayerTag tag) {
        final boolean[] playerTag = {data.getTag() != null && data.getTag().getId().toString().equalsIgnoreCase(tag.getId().toString())};
        return ItemBuilder.from(tag.getMaterial())
                .name(CC.AQUA + tag.getName())
                .lore(
                        CC.SEPARATOR,
                        CC.AQUA + "Tag: " + CC.WHITE + tag.getTag(),
                        CC.AQUA + "Description: " + CC.WHITE + tag.getDescription(),
                        CC.SEPARATOR,
                        (playerTag[0] ? CC.RED + "Click to remove!" : CC.YELLOW + "Click to use!")
                ).asGuiItem(event -> {
                    SoundUtil.playPing(player);
                    if (playerTag[0]) {
                        playerTag[0] = false;
                        PlayerManager.getInstance().getData(player.getUniqueId()).setTag(null);
                        player.sendMessage(CC.GREEN + "Unequipped your tag!");
                    } else {
                        PlayerManager.getInstance().getData(player.getUniqueId()).setTag(tag);
                        player.sendMessage(CC.GREEN + "Equipped your tag!");
                    }
                    // clear();
                    // update(player);
                    gui.updateItem(event.getSlot(), tagsButton(player, data, tag));
                    // gui.update();
                });
    }
}
