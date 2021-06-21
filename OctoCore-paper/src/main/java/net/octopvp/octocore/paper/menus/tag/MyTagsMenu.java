package net.octopvp.octocore.paper.menus.tag;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.TagManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.PlayerTag;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.Logger;
import net.octopvp.octocore.paper.utils.SoundUtil;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.buttons.impl.BackButton;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MyTagsMenu extends PaginatedMenu { //TODO back button
    private List<PlayerTag> currentTags;
    private Player player;
    public MyTagsMenu(List<PlayerTag> currentTags, Player player){
        for (PlayerTag currentTag : currentTags) {
            Logger.debug(" - " + currentTag.getName() + " | " + currentTag.getId());
        }
        this.currentTags = currentTags;
        this.player = player;
    }
    @Override
    public String getPagesTitle(Player player) {
        return CC.AQUA + "Your Tags";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        PlayerData data = PlayerManager.getProfile(player.getUniqueId());
        List<Button> buttons = new ArrayList<>();
        for (String allowedTag : data.getAllowedTags()) {
            PlayerTag tag = TagManager.getTag(allowedTag);
            if (tag == null)
                continue;
            buttons.add(new TagButton(tag,player));
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
        public void clicked(Player player, int slot, ClickType clickType) {
            new MainTagMenu().open(player);
        }
    }

    private static int i = 0;
    public class TagButton extends Button{
        private boolean a;
        private PlayerTag tag;
        public TagButton(PlayerTag tag,Player player){
            this.tag = tag;
            PlayerData data = PlayerManager.getProfile(player.getUniqueId());
            if (data.getTag() != null)
                a = data.getTag().getId() == tag.getId(); //this is the tag theyre using rn
        }
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(tag.getMaterial()).name(CC.AQUA + tag.getName()).lore(
                    CC.SCOREBOARD_SEPARATOR,
                    "",
                    CC.AQUA + "Tag: " + CC.WHITE + tag.getTag(),
                    "",
                    CC.AQUA + "Description: " + CC.WHITE + tag.getDescription(),
                    "",
                    CC.SCOREBOARD_SEPARATOR,
                    (a ? CC.RED + "Click to remove!" : CC.YELLOW + "Click to use!")

                    ).build();
        }
        @Override
        public int getSlot() {
            return i++;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            SoundUtil.playPing(player);
            Logger.debug("Clicked");
            if (a){
                a = false;
                player.sendMessage(CC.GREEN + "Unequipped your tag!");
                PlayerManager.getProfile(player.getUniqueId()).setTag(null);
                Logger.debug("a");
            }else {
                player.sendMessage(CC.GREEN + "Equipped your tag!");
                PlayerManager.getProfile(player.getUniqueId()).setTag(tag.getId());
                Logger.debug("b");
            }
            update(player);
        }
    }
}
