package net.octopvp.octocore.paper.command.impl.tests;

import lombok.RequiredArgsConstructor;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.builders.GrantBuilder;
import net.octopvp.octocore.paper.objects.permissions.Grant;
import net.octopvp.octocore.paper.utils.ItemBuilder;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.menu.buttons.Button;
import net.octopvp.octocore.paper.utils.menu.menu.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TestCommand {
    private static final String perm = Permissions.COMMAND_NICK;

    @Command(name = "settestmeta", description = "Sets test meta")
    @PlayerOnly
    @Permission(Permissions.ADMIN)
    public CommandResult setTest(Sender sender, String[] args) {
        PlayerData data = PlayerManager.getInstance().getData(sender.getPlayer());
        if (args.length == 1) {
            data.getMetaData().put("abc", args[0]);
        } else {
            sender.sendMessage("Meta: " + data.getMetaData().get("abc"));
        }
        return CommandResult.SUCCESS;
    }

    @Command(name = "test", description = "test", aliases = {"test1", "test2"})
    @PlayerOnly
    @Permission(Permissions.ADMIN)
    public CommandResult execute(Sender sender) {
        PlayerData data = PlayerManager.getInstance().getData(sender.getPlayer().getUniqueId());
        if (data == null) {
            sender.sendMessage(ChatColor.RED + "Data is null!");
            return CommandResult.SUCCESS;
        }
        Grant grant = new GrantBuilder(RankManager.getInstance().getRankByName("Owner")).setActive(true).setPerm(true).setReason("lmao").setServer(ServerContext.global()).build();
        data.applyGrant(grant);
        sender.sendMessage(ChatColor.GREEN + "Done");
        return CommandResult.SUCCESS;
    }

    @Command(name = "testmenu")
    @PlayerOnly
    @Permission(Permissions.ADMIN)
    public CommandResult exec(Sender sender) {
        new TestMenu().open(sender);
        return CommandResult.SUCCESS;
    }

    @RequiredArgsConstructor
    private class TestMenu extends PaginatedMenu {
        @Override
        public List<Button> getToolbarButtons() {
            List<Button> buttons = new ArrayList<>();
            buttons.add(new Button() {
                @Override
                public ItemStack getItem(Player player) {
                    return new ItemBuilder(Material.DIAMOND_SWORD).name("ez").build();
                }

                @Override
                public int getSlot() {
                    return 0;
                }

                @Override
                public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
                    player.sendMessage("ez");
                }
            });
            return null;
        }

        @Override
        public String getPagesTitle(Player player) {
            return "test";
        }

        @Override
        public List<Button> getPaginatedButtons(Player player) {
            List<Button> buttons = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                int finalI = i;
                buttons.add(new Button() {
                    @Override
                    public ItemStack getItem(Player player) {
                        return new ItemBuilder(Material.DIAMOND_SWORD).setName(finalI + "").build();
                    }

                    @Override
                    public int getSlot() {
                        return 0;
                    }

                    @Override
                    public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
                        player.sendMessage(finalI + "");
                    }
                });
            }
            return buttons;
        }

    }
}
