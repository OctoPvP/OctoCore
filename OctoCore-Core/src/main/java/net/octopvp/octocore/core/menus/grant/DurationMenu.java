package net.octopvp.octocore.core.menus.grant;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.GrantProcedure;
import net.octopvp.octocore.core.objects.GrantProcedureState;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.ItemBuilder;
import net.octopvp.octocore.core.utils.SoundUtil;
import net.octopvp.octocore.core.utils.menu.buttons.Button;
import net.octopvp.octocore.core.utils.menu.menu.Menu;
import net.octopvp.octocore.core.utils.msg.Lang;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class DurationMenu extends Menu {
    private final PlayerData data;

    @Override
    public List<Button> getButtons(Player player) {
        return Lists.newArrayList(new PermanentButton(), new CustomDurationButton(), new PlaceholderButton());
    }

    @Override
    public String getName(Player player) {
        return CC.AQUA + "Choose duration";
    }


    private class PermanentButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.BEDROCK).name(CC.AQUA + "Permanent").lore(CC.GREEN + "This will make the duration " + CC.UNDERLINE + "Permanent").build();
        }

        @Override
        public int getSlot() {
            return 11;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            PlayerData playerData = PlayerManager.getInstance().getData(player.getUniqueId());
            if (playerData == null) {
                player.closeInventory();
                return;
            }
            playerData.getGrantProcedure().setEnteredDuration(-1L);
            playerData.getGrantProcedure().setPermanent(true);
            playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.REASON);
            player.sendMessage(Lang.GRANT_DURATION_SET.getMsg("Permanent"));
            new GrantReasonMenu(data).open(player);
        }
    }

    private class CustomDurationButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.BOOK_AND_QUILL).name(CC.AQUA + "Custom Duration").lore(CC.GREEN + "Click to set a custom duration.").build();
        }

        @Override
        public int getSlot() {
            return 15;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            callback(data, player);
        }

        private void callback(PlayerData playerData, Player player) {
            player.closeInventory();
            SoundUtil.playPing(player);
            OctoCore.getConversationFactory().withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext conversationContext) {
                    return Lang.GRANT_ENTER_DURATION.getMsg();
                }

                @Override
                public Prompt acceptInput(ConversationContext conversationContext, String s) {
                    if (playerData == null || !playerData.isOnlineThisServer())
                        return Prompt.END_OF_CONVERSATION;
                    if (playerData.getGrantProcedure() == null)
                        playerData.setGrantProcedure(new GrantProcedure(playerData));
                    if (s.equalsIgnoreCase("perm") || s.equalsIgnoreCase("permanent")) {
                        playerData.getGrantProcedure().setEnteredDuration(-1L);
                        playerData.getGrantProcedure().setPermanent(true);
                        playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.REASON);
                        player.sendMessage(Lang.GRANT_DURATION_SET.getMsg("Permanent"));
                        new GrantReasonMenu(data).open(player);
                        return Prompt.END_OF_CONVERSATION;
                    }
                    long duration;
                    try {
                        duration = System.currentTimeMillis() - DateUtils.parseDateDiff(s, false);
                    } catch (Exception e) {
                        player.sendMessage(Lang.GRANT_INVALID_TIME.getMsg());
                        callback(playerData, player); //FIXME might not work
                        return Prompt.END_OF_CONVERSATION;
                    }
                    playerData.getGrantProcedure().setPermanent(false);
                    playerData.getGrantProcedure().setEnteredDuration(duration);
                    player.sendMessage(Lang.GRANT_DURATION_SET.getMsg(playerData.getGrantProcedure().getNiceDuration()));
                    playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.REASON);
                    new GrantReasonMenu(data).open(player);
                    return Prompt.END_OF_CONVERSATION;
                }
            }).withLocalEcho(false).buildConversation(player).begin();
        }
    }

    private class PlaceholderButton extends net.octopvp.octocore.core.utils.menu.buttons.PlaceholderButton {
        @Override
        public int[] getSlots() {
            List<Integer> a = new ArrayList<>();
            IntStream.range(0, 27).forEach((i) -> {
                if (!(i == 11 || i == 15))
                    a.add(i);
            });
            return a.stream().mapToInt(i -> i).toArray();
        }
    }
}
