package net.octopvp.octocore.paper.menus.tag;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.conversations.tag.FilterConversation;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.TagManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.PlayerTag;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.SoundUtil;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GiveTagsMenu extends PaginatedMenu { //TODO overhaul this menu
    private String target;
    private List<PlayerTag> currentTags;
    private List<String> tagStringList;
    private boolean filtered = false;
    private String filterString = "";
    public GiveTagsMenu(String target,List<PlayerTag> tags){
        this.target = target;
        this.currentTags = tags;
        tagStringList = new ArrayList<>();
        for (PlayerTag currentTag : currentTags) {
            tagStringList.add(currentTag.getName().toLowerCase());
        }
    }
    public GiveTagsMenu(String target,List<PlayerTag> tags,String filter){
        this.target = target;
        this.currentTags = tags;
        tagStringList = new ArrayList<>();
        for (PlayerTag currentTag : currentTags) {
            tagStringList.add(currentTag.getName().toLowerCase());
        }
        this.filterString = filter;
        filtered = true;
    }

    @Override
    public String getPagesTitle(Player player) {
        return CC.GREEN + "Give " + target + " a tag";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        for (PlayerTag tag : TagManager.getTags()) { //check by name instead
            if (filtered) {
                if (ChatColor.stripColor(tag.getName()).toLowerCase().contains(filterString.toLowerCase()) || ChatColor.stripColor(tag.getDescription()).toLowerCase().contains(filterString.toLowerCase())) {
                    if (tagStringList.contains(tag.getName().toLowerCase()))
                        buttons.add(new RemoveTagButton(tag));
                    else buttons.add(new GiveTagButton(tag));
                } //im too lazy lmao
            }else{
                if (tagStringList.contains(tag.getName().toLowerCase()))
                    buttons.add(new RemoveTagButton(tag));
                else buttons.add(new GiveTagButton(tag));
            }
        }
        return buttons;
    }

    @Override
    public Button getFilterButton() {
        return new FilterTagsButton(filtered);
    }

    @Override
    public List<Button> getEveryMenuSlots(Player player) {
        return null;
    }

    public static int slot1 = 0;
    public class GiveTagButton extends Button{
        private PlayerTag tag;
        public GiveTagButton(PlayerTag tag){
            this.tag = tag;
        }

        @Override
        public ItemStack getItem(Player player) { //maybe bold the matching string if filtered, later
            return new ItemBuilder(tag.getMaterial()).name(tag.getName()).lore(CC.GREEN + "Tag: " + CC.D_GRAY + CC.ARROW_LEFT + tag.getTag() + CC.D_GRAY + CC.ARROW_RIGHT,tag.getDescription(),"",CC.YELLOW + "Click to add!").build();
        }

        @Override
        public int getSlot() {
            return slot1++;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            PlayerData data = PlayerManager.getProfile(player.getUniqueId());
            data.getAllowedTags().add(tag);
            SoundUtil.playPing(player);
            if (filtered)
                new GiveTagsMenu(target,currentTags,filterString).open(player);
            else new GiveTagsMenu(target,currentTags).open(player);
        }
    }
    public class RemoveTagButton extends Button{
        private PlayerTag tag;
        public RemoveTagButton(PlayerTag tag){
            this.tag = tag;
        }

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(tag.getMaterial()).name(tag.getName()).lore(CC.GREEN + "Tag: " + CC.D_GRAY + CC.ARROW_LEFT + tag.getTag() + CC.D_GRAY + CC.ARROW_RIGHT,tag.getDescription(),"",CC.RED + "Click to remove!").build();
        }

        @Override
        public int getSlot() {
            return slot1++;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            PlayerData data = PlayerManager.getProfile(player.getUniqueId());
            if (data.getAllowedTags().contains(tag))
                data.getAllowedTags().remove(tag);
            SoundUtil.playPing(player);
            if (filtered)
                new GiveTagsMenu(target,currentTags,filterString).open(player);
            else new GiveTagsMenu(target,currentTags).open(player);
        }
    }
    public class FilterTagsButton extends Button {
        private boolean filtered1 = false;
        public FilterTagsButton(boolean f){
            this.filtered1 = f;
        }

        @Override
        public ItemStack getItem(Player player) {
            if (filtered1)
                return new ItemBuilder(Material.HOPPER).name(CC.AQUA + "Filter").lore(CC.RED + "Click to remove the current filter").build();
            return new ItemBuilder(Material.HOPPER).name(CC.AQUA + "Filter").lore(CC.YELLOW + "Click to add a filter").build();
        }

        @Override
        public int getSlot() {
            return 37;
        }

        @Override
        public void onClick(Player player, int slot,ClickType clickType) {
            if (filtered1){
                player.closeInventory();
                new GiveTagsMenu(target,currentTags).open(player);
                return;
            }
            player.getOpenInventory().close();
            OctoCore.getConversationFactory().withFirstPrompt(new FilterConversation((s)->{
                if (s.equalsIgnoreCase("cancel") || s.equalsIgnoreCase("exit"))
                    new GiveTagsMenu(target,currentTags).open(player);
                else new GiveTagsMenu(target,currentTags,s).open(player);
            })).withLocalEcho(false).buildConversation(player).begin();
        }
    }
}