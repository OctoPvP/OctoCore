package net.octopvp.octocore.core.menus.impl.tag;

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

import java.util.List;

@RequiredArgsConstructor
public class MyTagsMenu extends PaginatedMenu<PaginatedGui> {
    private final List<PlayerTag> currentTags;
    private final Player player;
    /*
    { //TODO back button
    private static int i = 0;
    private final List<PlayerTag> currentTags;
    private final Player player;

    public MyTagsMenu(List<PlayerTag> currentTags, Player player) {
        for (PlayerTag currentTag : currentTags) {
        }
        this.currentTags = currentTags;
        this.player = player;
    }

    @Override
    public String getPagesTitle(Player player) {
        return "Your Tags";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        PlayerData data = PlayerManager.getInstance().getData(player.getUniqueId());
        List<Button> buttons = new ArrayList<>();
        for (PlayerTag tag : currentTags) {
            if (tag == null)
                continue;
            buttons.add(new TagButton(tag, player));
        }
        return buttons;
    }

    @Override
    public List<Button> getEveryMenuSlots(Player player) {
        return null;
    }

    @Override
    public Button getBackButton(Player player) {
        return new MenuBackButton();
    }

    public class MenuBackButton extends BackButton {
        @Override
        public void clicked(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            new MainTagMenu().open(player);
        }
    }

    public class TagButton extends Button {
        private final PlayerTag tag;
        private boolean a;

        public TagButton(PlayerTag tag, Player player) {
            this.tag = tag;
            PlayerData data = PlayerManager.getInstance().getData(player.getUniqueId());
            if (data.getTag() != null)
                a = data.getTag().getId().toString().equalsIgnoreCase(tag.getId().toString()); //this is the tag theyre using rn
        }

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(tag.getMaterial()).name(CC.AQUA + tag.getName()).lore(
                    CC.SEPARATOR,
                    CC.AQUA + "Tag: " + CC.WHITE + tag.getTag(),
                    CC.AQUA + "Description: " + CC.WHITE + tag.getDescription(),
                    CC.SEPARATOR,
                    (a ? CC.RED + "Click to remove!" : CC.YELLOW + "Click to use!")

            ).build();
        }

        @Override
        public int getSlot() {
            return i++;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            SoundUtil.playPing(player);
            if (a) {
                a = false;
                player.sendMessage(CC.GREEN + "Unequipped your tag!");
                PlayerManager.getInstance().getData(player.getUniqueId()).setTag(null);
            } else {
                player.sendMessage(CC.GREEN + "Equipped your tag!");
                PlayerManager.getInstance().getData(player.getUniqueId()).setTag(tag);
            }
            update(player);
        }
    }
}
     */

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Your Tags")
                .rows(6)
                .create();
    }

    @Override
    public List<GuiItem> getItems(Player player) {
        return null;
    }

    public GuiItem tagsButton(Player player, PlayerData data, PlayerTag tag) {
        final boolean[] a = {data.getTag() != null && data.getTag().getId().toString().equalsIgnoreCase(tag.getId().toString())};
        return ItemBuilder.from(tag.getMaterial())
                .name(CC.AQUA + tag.getName())
                .lore(
                        CC.SEPARATOR,
                        CC.AQUA + "Tag: " + CC.WHITE + tag.getTag(),
                        CC.AQUA + "Description: " + CC.WHITE + tag.getDescription(),
                        CC.SEPARATOR,
                        (a[0] ? CC.RED + "Click to remove!" : CC.YELLOW + "Click to use!")
                ).asGuiItem(event -> {
                    SoundUtil.playPing(player);
                    if (a[0]) {
                        a[0] = false;
                        player.sendMessage(CC.GREEN + "Unequipped your tag!");
                        PlayerManager.getInstance().getData(player.getUniqueId()).setTag(null);
                    } else {
                        player.sendMessage(CC.GREEN + "Equipped your tag!");
                        PlayerManager.getInstance().getData(player.getUniqueId()).setTag(tag);
                    }
                    update(player);
                });
    }
}
