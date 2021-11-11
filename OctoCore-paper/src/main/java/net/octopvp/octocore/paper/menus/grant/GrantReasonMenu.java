package net.octopvp.octocore.paper.menus.grant;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.conversations.QuestionConversation;
import net.octopvp.octocore.paper.conversations.grant.GrantReasonConversation;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.GrantReason;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.menu.Menu;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class GrantReasonMenu extends Menu {
    private final PlayerData playerData;
    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(new OtherButton());
        buttons.add(new ReasonButton(1,GrantReason.FAMOUS));
        buttons.add(new ReasonButton(2,GrantReason.DEMOTION));
        buttons.add(new ReasonButton(3,GrantReason.PROMOTION));
        buttons.add(new ReasonButton(4,GrantReason.STORE));
        /*
        for (GrantReason value : GrantReason.values()) {
            buttons.add(new ReasonButton(value));
        }
         */
        return buttons;
    }

    @Override
    public String getName(Player player) {
        return CC.GREEN + "Reason";
    }
    @RequiredArgsConstructor
    private class ReasonButton extends Button{
        private final int i;
        private final GrantReason grantReason;
        @Override
        public ItemStack getItem(Player player) {
            return grantReason.getMaterial();
        }

        @Override
        public int getSlot() {
            return i;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            PlayerData sendData = PlayerManager.getProfile(player.getUniqueId());
            if (sendData == null || !playerData.isOnlineThisServer()){
                player.closeInventory();
                return;
            }
            sendData.getGrantProcedure().setEnteredReason(grantReason.getReason());
            player.sendMessage(Lang.GRANT_REASON_SET.getMsg(grantReason.getReason()));
            sendData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.CONFIRMATION);
            new GrantConfirmationMenu().open(player);
        }
    }
    @RequiredArgsConstructor
    private class OtherButton extends Button{

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.BOOK_AND_QUILL).name(CC.GREEN + "Other").lore(CC.AQUA + "Select to enter a custom reason.").build();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            prompt(player);
            player.closeInventory();
        }
        public void prompt(Player player){
            OctoCore.getConversationFactory().withFirstPrompt(new GrantReasonConversation(playerData,player)).withLocalEcho(false).buildConversation(player).begin();
        }
    }
}
