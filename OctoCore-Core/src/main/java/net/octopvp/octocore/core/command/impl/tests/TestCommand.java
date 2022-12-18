package net.octopvp.octocore.core.command.impl.tests;

import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Optional;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Required;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.manager.impl.RankManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.objects.builders.GrantBuilder;
import net.octopvp.octocore.core.objects.permissions.Grant;
import net.octopvp.octocore.core.utils.Sender;
import org.bukkit.ChatColor;

public class TestCommand {
    private static final String perm = Permissions.COMMAND_NICK;

    @Command(name = "settestmeta", description = "Sets test meta")
    @PlayerOnly
    @Permission(Permissions.ADMIN)
    public CommandResult setTest(Sender sender, @Optional String meta) {
        PlayerData data = PlayerManager.getInstance().getData(sender.getPlayer());
        if (meta != null) {
            data.getMetaData().put("abc", meta);
        } else {
            sender.sendMessage("Meta: " + data.getMetaData().get("abc"));
        }
        return CommandResult.SUCCESS;
    }

    @Command(name = "test1")
    @Permission(Permissions.ADMIN)
    public void test(Sender sender, @Required String s) {
        sender.sendMessage(s);
    }

    @Command(name = "test", description = "test", aliases = {"test1", "test2"})
    @PlayerOnly
    //@Permission(Permissions.ADMIN)
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

}
