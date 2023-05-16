package net.octopvp.octocore.core.menus.tag;

import lombok.RequiredArgsConstructor;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.guis.GuiItem;
import net.octopvp.agile.guis.PaginatedGui;
import net.octopvp.agile.menu.Menu;
import net.octopvp.agile.util.XMaterial;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.database.redis.packets.player.TagUpdatePacket;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.objects.PlayerTag;
import net.octopvp.octocore.core.utils.SoundUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Objects;

@RequiredArgsConstructor
public class ManagePlayerTagsMenu extends Menu<PaginatedGui> {
    private final PlayerData data;

    /*

    @Override
    public String getPagesTitle(Player player) {
        return "Manage " + data.getName() + "'s Tags";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        for (PlayerTag allowedTag : data.getAllowedTags()) {
            buttons.add(new TagButton(allowedTag));
        }
        return buttons;
    }

    @Override
    public List<Button> getToolbarButtons() {
        return Collections.singletonList(new AddTagButton());
    }

    @RequiredArgsConstructor
    private class TagButton extends Button {

        private final PlayerTag tag;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder builder = new ItemBuilder(tag.getMaterial()).name(CC.AQUA + tag.getName()).lore(
                    CC.SEPARATOR,
                    CC.AQUA + "Tag: " + CC.WHITE + tag.getTag(),
                    CC.AQUA + "Description: " + CC.WHITE + tag.getDescription(),
                    CC.AQUA + "ID: " + CC.WHITE + tag.getId(),
                    CC.SEPARATOR,
                    CC.YELLOW + "Shift-Right Click to remove this tag."
            );
            return builder.build();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            super.onClick(player, slot, clickType, event);
            if (clickType == ClickType.SHIFT_RIGHT) {
                if (OctoCoreCommon.getInstance().getServerManager().isPlayerOnline(data.getUuid())) {
                    //new TagUpdatePacket(new JsonBuilder().addProperty("uuid", data.getUuid().toString()).addProperty("type", "REMOVE_TAG").addProperty("tagId", tag.getId().toString())).send();
                    new TagUpdatePacket(TagUpdatePacket.TagUpdateReason.REMOVE_TAG, data.getUuid(), tag.getId()).send();
                } else {
                    PlayerData d = PlayerManager.getInstance().getOfflineData(data.getUniqueId());
                    d.removeTag(tag.getId());
                    d.save();
                }
                SoundUtil.playPing(player);
                //data.load();
                //update(player);
                player.closeInventory();
            }
        }
    }

    @RequiredArgsConstructor
    private class AddTagButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.EMERALD).name(CC.GREEN + "Add Tag").lore(CC.YELLOW + "Click to give this player a tag!").build();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            new GiveTagsMenu(data).open(player);
        }
    }
     */
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
                        if (OctoCoreCommon.getInstance().getServerManager().isPlayerOnline(data.getUuid())) {
                            //new TagUpdatePacket(new JsonBuilder().addProperty("uuid", data.getUuid().toString()).addProperty("type", "REMOVE_TAG").addProperty("tagId", tag.getId().toString())).send();
                            new TagUpdatePacket(TagUpdatePacket.TagUpdateReason.REMOVE_TAG, data.getUuid(), tag.getId()).send();
                        } else {
                            PlayerData d = PlayerManager.getInstance().getOfflineData(data.getUniqueId());
                            d.removeTag(tag.getId());
                            d.getData();
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
        gui.getFiller().fillBorder(ItemBuilder.from(Objects.requireNonNull(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem())).name(" ").asGuiItem());
        gui.setItem(0, 0, addTagButton());


        for (PlayerTag allowedTag : data.getAllowedTags()) {
            gui.addItem(tagButton(allowedTag));
        }
    }
}
