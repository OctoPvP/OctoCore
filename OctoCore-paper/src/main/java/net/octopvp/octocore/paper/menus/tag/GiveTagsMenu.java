package net.octopvp.octocore.paper.menus.tag;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.PlayerTag;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GiveTagsMenu extends PaginatedMenu {
    private PlayerData data;
    public GiveTagsMenu(PlayerData data){
        this.data = data;
    }
    @Override
    public String getPagesTitle(Player player) {
        return null;
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        return null;
    }

    @Override
    public List<Button> getEveryMenuSlots(Player player) {
        return null;
    } //TODO overhaul this menu
    public class GiveTagButton extends Button{
        private PlayerTag tag;
        private PlayerData playerData;
        public GiveTagButton(PlayerTag tag,PlayerData playerData){
            this.tag = tag;
            this.playerData = playerData;
        }
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(tag.getMaterial()).setName(CC.AQUA + tag.getName()).lore(CC.SEPARATOR,"",CC.AQUA + "Tag: " + tag.getTag(),CC.AQUA + "Description: " + tag.getDescription(), "",CC.SEPARATOR,(data.getTag().getName() == tag.getName() ? CC.RED + "Click to un-equip!" : CC.YELLOW + "Click to equip!")).build();
        }

        @Override
        public int getSlot() {
            return 0;
        }
    }
}