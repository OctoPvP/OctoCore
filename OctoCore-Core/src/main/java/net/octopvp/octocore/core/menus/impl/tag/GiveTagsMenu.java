package net.octopvp.octocore.core.menus.impl.tag;

import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.guis.PaginatedGui;
import net.octopvp.agile.menu.PaginatedMenu;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.database.redis.packets.player.TagUpdatePacket;
import net.octopvp.octocore.core.manager.impl.TagManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.objects.PlayerTag;
import net.octopvp.octocore.core.utils.SoundUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GiveTagsMenu extends PaginatedMenu<PaginatedGui> {
    private final PlayerData data;

    public GiveTagsMenu(PlayerData data) {
        this.data = data;
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> items = new ArrayList<>();
        for (PlayerTag tag : TagManager.getTags()) {
            items.add(tag(tag));
        }
        return items;
    }

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Give " + data.getName() + " tags")
                .rows(6)
                .create();
    }

    public GuiItem tag(PlayerTag tag) {
        return ItemBuilder.from(tag.getMaterial())
                .name(CC.AQUA + tag.getName())
                .lore(CC.SEPARATOR, "",
                        CC.AQUA + "Tag: " + tag.getTag(),
                        CC.AQUA + "Description: " + tag.getDescription(), "",
                        CC.SEPARATOR, (!data.hasTag(tag) ? CC.YELLOW + "Click to give " + data.getName() + " this tag!" : CC.RED + data.getName() + " already has this tag!"))
                .asGuiItem(event -> {
                    if (data.hasTag(tag)) {
                        event.getWhoClicked().sendMessage(CC.RED + "That player already has that tag!");
                        return;
                    }
                    SoundUtil.playPing((Player) event.getWhoClicked());
                    //new TagUpdatePacket(new JsonBuilder().addProperty("uuid", data.getUuid().toString()).addProperty("type", "GIVE_TAG").addProperty("tagId", tag.getId().toString())).send();
                    new TagUpdatePacket(
                            TagUpdatePacket.TagUpdateReason.GIVE_TAG,
                            data.getUuid(),
                            tag.getId()
                    ).send();
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().sendMessage(CC.GREEN + "Gave " + data.getName() + " the " + tag.getName() + " tag!");
                });
    }
}
