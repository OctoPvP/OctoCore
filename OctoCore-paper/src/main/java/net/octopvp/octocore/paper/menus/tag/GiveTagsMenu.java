package net.octopvp.octocore.paper.menus.tag;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.conversations.tag.FilterConversation;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.TagManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.PlayerTag;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.Logger;
import net.octopvp.octocore.paper.utils.SoundUtil;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GiveTagsMenu extends PaginatedMenu{
    private String target;
    private List<PlayerTag> currentTags;
    private List<PlayerTag> allTags;
    public GiveTagsMenu(List<PlayerTag> currentTags,String target){
        for (PlayerTag currentTag : currentTags) {
            Logger.debug(" - " + currentTag.getName() + " | " + currentTag.getId());
        }
        this.allTags = TagManager.getTags();
        this.currentTags = currentTags;
        this.target = target;
    }
    @Override
    public String getPagesTitle(Player player) {
        return CC.AQUA + "Give " + player.getName() + " tags";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        //PlayerData data = PlayerManager.getProfile(target.getUniqueId());
        List<Button> buttons = new ArrayList<>();
        for (PlayerTag tag : TagManager.getTags()) {
            buttons.add(new TagButton(tag,tag));
        }
        return buttons;
    }

    @Override
    public List<Button> getEveryMenuSlots(Player player) {
        return null;
    }
    private static int a = -1;
    public class TagButton extends Button{
        private PlayerTag tag;
        boolean b = false;
        private PlayerTag currentTag;
        public TagButton(PlayerTag tag,PlayerTag currentTag){
            this.tag = tag;
            this.currentTag = currentTag;
            b = !allTags.contains(currentTag);
        }
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(tag.getMaterial()).setName(CC.AQUA + tag.getName()).lore(CC.SEPARATOR,"",CC.AQUA + "Tag: " + tag.getTag(),"",CC.AQUA + "Description: " + tag.getDescription(),"",CC.SEPARATOR,(b ? CC.RED + "Click to remove tag" : CC.YELLOW + "Click to give tag")).build();
        }

        @Override
        public int getSlot() {
            return a++;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (b){
                b = !b;
                player.chat("/removetag " + target + " " + tag.getName());
            }else{
                b = !b;
                player.chat("/givetag " + target + " " + tag.getName());
            }
            update(player);
            SoundUtil.playPing(player);
        }
    }
}