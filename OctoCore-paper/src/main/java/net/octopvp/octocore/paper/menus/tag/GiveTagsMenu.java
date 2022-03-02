package net.octopvp.octocore.paper.menus.tag;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.json.JsonBuilder;
import net.octopvp.octocore.paper.database.redis.packets.player.TagUpdatePacket;
import net.octopvp.octocore.paper.manager.impl.TagManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.PlayerTag;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.SoundUtil;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GiveTagsMenu extends PaginatedMenu {
    private final PlayerData data;

    public GiveTagsMenu(PlayerData data) {
        this.data = data;
    }

    @Override
    public String getPagesTitle(Player player) {
        return "Give " + data.getName() + " tags";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        for (PlayerTag tag : TagManager.getTags()) {
            buttons.add(new GiveTagButton(tag));
        }
        return buttons;
    }

    public class GiveTagButton extends Button {
        private final PlayerTag tag;

        public GiveTagButton(PlayerTag tag) {
            this.tag = tag;
        }

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(tag.getMaterial()).setName(CC.AQUA + tag.getName())
                    .lore(CC.SEPARATOR, "",
                            CC.AQUA + "Tag: " + tag.getTag(),
                            CC.AQUA + "Description: " + tag.getDescription(), "",
                            CC.SEPARATOR, (!data.hasTag(tag) ? CC.YELLOW + "Click to give " + data.getName() + " this tag!" : CC.RED + data.getName() + " already has this tag!")).build();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            if (data.hasTag(tag)) {
                player.sendMessage(CC.RED + "That player already has that tag!");
                return;
            }
            SoundUtil.playPing(player);
            new TagUpdatePacket(new JsonBuilder().addProperty("uuid", data.getUuid().toString()).addProperty("type", "GIVE_TAG").addProperty("tagId", tag.getId().toString())).send();
            player.closeInventory();
            player.sendMessage(CC.GREEN + "Gave " + data.getName() + " the " + tag.getName() + " tag!");
        }
    }
}
