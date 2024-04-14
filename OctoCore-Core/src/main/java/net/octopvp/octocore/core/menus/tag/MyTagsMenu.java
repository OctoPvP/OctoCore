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
import org.bukkit.util.ChatPaginator;

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
        boolean equipped = data.getTag() != null && data.getTag().getId().equals(tag.getId());
        return ItemBuilder.from(tag.getMaterial())
                .name(CC.AQUA + tag.getName())
                .lore(
                        CC.SEPARATOR,
                        CC.AQUA + "Tag: " + CC.WHITE + tag.getTag(),
                        CC.AQUA + "Description:"
                ).addLore(ChatPaginator.wordWrap(tag.getDescription(), 30))
                .addLore(CC.SEPARATOR, (equipped ? CC.RED + "Click to remove!" : CC.YELLOW + "Click to use!"))
                .asGuiItem(event -> {
                    SoundUtil.playPing(player);
                    if (equipped) {
                        data.setTag(null);
                        player.sendMessage(CC.RED + "Removed tag " + tag.getName() + "!");
                    } else {
                        data.setTag(tag);
                        player.sendMessage(CC.GREEN + "Equipped tag " + tag.getName() + "!");
                    }
                    gui.clearPageItems(false);
                    populateGui(gui, player);
                });
    }
}
