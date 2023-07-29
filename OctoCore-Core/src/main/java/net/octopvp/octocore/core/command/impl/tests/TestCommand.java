package net.octopvp.octocore.core.command.impl.tests;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.octopvp.agile.builder.item.ItemBuilder;
import net.octopvp.agile.guis.Gui;
import net.octopvp.agile.menu.Menu;
import net.octopvp.commander.annotation.*;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.common.object.builders.GrantBuilder;
import net.octopvp.octocore.common.object.permissions.Grant;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.manager.impl.RankManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.utils.AdventureUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand {
    private static final String perm = Permissions.COMMAND_NICK;

    @Command(name = "settestmeta", description = "Sets test meta")
    @PlayerOnly
    @Permission(Permissions.ADMIN)
    public CommandResult setTest(@Sender Player sender, @Optional String meta) {
        PlayerData data = PlayerManager.getInstance().getData(sender);
        if (meta != null) {
            data.getMetaData().put("abc", meta);
        } else {
            sender.sendMessage("Meta: " + data.getMetaData().get("abc"));
        }
        return CommandResult.SUCCESS;
    }

    @Command(name = "flags")
    @Permission(Permissions.ADMIN)
    public void test(@Sender Player sender, @Optional Player player,  @Flag(value = "priority", aliases = "p") int priority, @Switch(value = "test", aliases = "t") boolean test) {
        sender.sendMessage("Priority: " + priority + " | " + (player == null ? "null" : player.getName()) + " | " + test);
    }

    @Command(name = "testmenu", description = "testmenu")
    @PlayerOnly
    @Permission(Permissions.ADMIN)
    public void testMenu(@Sender Player sender) {
        sender.sendMessage("Opening menu");
        try {
            new TestMenu().open(sender);
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("Error: " + e.getMessage());
        }
        sender.sendMessage("Opened menu");
    }

    @Command(name = "test", description = "test", aliases = {"test1", "test2"})
    @PlayerOnly
    @Permission(Permissions.ADMIN)
    public CommandResult execute(@Sender Player sender) {
        PlayerData data = PlayerManager.getInstance().getData(sender.getUniqueId());
        if (data == null) {
            sender.sendMessage(ChatColor.RED + "Data is null!");
            return CommandResult.SUCCESS;
        }
        Grant grant = new GrantBuilder(RankManager.getInstance().getRankByName("Owner")).setActive(true).setPerm(true).setReason("lmao").setServer(ServerContext.global()).build();
        data.applyGrant(grant);
        sender.sendMessage(ChatColor.GREEN + "Done");
        return CommandResult.SUCCESS;
    }

    @Command(name = "deserializetext", description = "Deserializes text to component format")
    public void execute(@Sender CommandSender sender, @JoinStrings String text) {
        AdventureUtils.sendMessage(sender, AdventureUtils.format(text));
    }

    private static class TestMenu extends Menu<Gui> {

        @Override
        public Gui createGui(Player player) {
            System.out.println("Creating");
            return Gui.gui()
                    .title("Test")
                    .rows(3)
                    .create();
        }

        @Override
        public void populateGui(Gui gui, Player player) {
            System.out.println("Populating");
            gui.setItem(0, ItemBuilder.from(Material.PRISMARINE_CRYSTALS).name(CC.GREEN + "Test1").asGuiItem());
            System.out.println("a1");
            Component component = Component.text("Test2")
                    .color(NamedTextColor.GREEN)
                    .append(Component.text("Test3")
                            .color(NamedTextColor.RED));
            System.out.println("b2");
            try {
                gui.setItem(1, ItemBuilder.from(Material.DIRT).name(component).asGuiItem());
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("c3");
        }
    }

}
