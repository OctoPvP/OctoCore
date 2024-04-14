package net.octopvp.octocore.core.menus.tag;

import lombok.RequiredArgsConstructor;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.guis.PaginatedGui;
import net.octopvp.agile.menu.Menu;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.database.redis.packets.player.TagUpdatePacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.objects.PlayerTag;
import net.octopvp.octocore.core.utils.SoundUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@RequiredArgsConstructor
public class ManagePlayerTagsMenu extends Menu<PaginatedGui> {
    private final PlayerData data;

    public GuiItem addTagButton() {
        return ItemBuilder.from(Material.EMERALD)
                .name(CC.GREEN + "Add Tag")
                .lore(CC.YELLOW + "Click to give this player a tag!")
                .asGuiItem()
                .click(event -> new GiveTagsMenu(data).open((Player) event.getWhoClicked()));
    }

    public GuiItem tagButton(PlayerTag tag) {
        return ItemBuilder.from(tag.getMaterial())
                .name(CC.AQUA + tag.getName())
                .lore(
                        CC.SEPARATOR,
                        CC.AQUA + "Tag: " + CC.WHITE + tag.getTag(),
                        CC.AQUA + "Description: " + CC.WHITE + tag.getDescription(),
                        CC.AQUA + "ID: " + CC.WHITE + tag.getId(),
                        CC.SEPARATOR,
                        CC.YELLOW + "Shift-Right Click to remove this tag."
                )
                .asGuiItem()
                .click(event -> {
                    if (event.getClick() == ClickType.SHIFT_RIGHT) {
                        if (data.isOnline()) {
                            //new TagUpdatePacket(new JsonBuilder().addProperty("uuid", data.getUuid().toString()).addProperty("type", "REMOVE_TAG").addProperty("tagId", tag.getId().toString())).send();
                            new TagUpdatePacket(TagUpdatePacket.TagUpdateReason.REMOVE_TAG, data.getUuid(), tag.getId()).send();
                        } else {
                            PlayerData d = PlayerManager.getInstance().getDataEvenIfOffline(data.getUniqueId(), false);
                            d.removeTag(tag.getId());
                            d.save();
                        }
                        SoundUtil.playPing((Player) event.getWhoClicked());
                        //data.load();
                        //update(player);
                        event.getWhoClicked().closeInventory();
                    }
                });
    }


    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Manage " + data.getName() + "'s Tags")
                .rows(6)
                .create();
    }

    @Override
    public void populateGui(PaginatedGui gui, Player player) {
        gui.getFiller().fillBorder(PLACEHOLDER_ITEM);
        gui.setItem(0, addTagButton());


        for (PlayerTag allowedTag : data.getAllowedTags()) {
            gui.addItem(tagButton(allowedTag));
        }
    }
}
