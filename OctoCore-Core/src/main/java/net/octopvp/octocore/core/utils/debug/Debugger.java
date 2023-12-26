package net.octopvp.octocore.core.utils.debug;

import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.object.builders.RankBuilder;
import net.octopvp.octocore.common.object.permissions.Rank;
import net.octopvp.octocore.common.redis.packets.PlayerMessagePacket;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.listeners.JoinLeaveListener;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.objects.OctoPermissible;
import net.octopvp.octocore.core.objects.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

import java.beans.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Debugger {
    private static final String PREFIX = ChatColor.YELLOW + "[DEBUG] " + ChatColor.RESET;

    private final CommandSender sender;

    public Debugger(CommandSender sender) {
        this.sender = sender;
    }

    public static void log(Object... objects) {
        String[] values = formStringArray(objects);
        String message = PREFIX + String.join(" ", values);

        Bukkit.getConsoleSender().sendMessage(message);
        Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(p -> p.sendMessage(message));
    }

    private static String[] formStringArray(Object[] objects) {
        return Arrays.stream(objects).map(String::valueOf).toArray(String[]::new);
    }

    private void print(Object... objects) {
        sender.sendMessage(PREFIX + String.join(" ", formStringArray(objects)));
    }

    public void execute(String cmd) {
        try {
            int[] pts = {cmd.indexOf('('), cmd.indexOf(')')};
            if (pts[0] == -1 || pts[1] == -1) throw new IllegalArgumentException();

            String name = cmd.substring(0, pts[0]);
            String content = cmd.substring(pts[0] + 1, pts[1]);

            Object[] args = content.isEmpty() ? null : buildObjects(content);

            Statement statement = new Statement(this, name, args);
            print("Running the expression \"" + ChatColor.AQUA + cmd + ChatColor.RESET + "\"...");
            statement.execute();
            print("Done running the expression \"" + ChatColor.AQUA + cmd + ChatColor.RESET + "\"!");

        } catch (Exception e) {
            print("Error: the expression \"" + ChatColor.AQUA + cmd + ChatColor.RESET + "\" failed to execute.");
            print(e.toString());
        }
    }

    public Object[] buildObjects(String content) {
        List<Object> list = new ArrayList<>();

        if (!content.isEmpty()) {
            String[] values = content.split(",");

            for (String str : values) {
                String value = str.startsWith(" ") ? str.substring(1) : str;
                Object obj = value;

                try {
                    obj = Double.parseDouble(value);
                } catch (NumberFormatException ignored) {
                }

                try {
                    obj = Integer.parseInt(value);
                } catch (NumberFormatException ignored) {
                }

                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                    obj = Boolean.parseBoolean(value);
                }

                list.add(obj);
            }
        }

        return list.toArray();
    }

    public void unfreezeme() {
        if (sender instanceof Player) {
            JoinLeaveListener.unfreezePlayer(((Player) sender));
            sender.sendMessage(CC.GREEN + "Done!");
        }
    }

    public void freezeme() {
        if (sender instanceof Player) {
            JoinLeaveListener.freezePlayer(((Player) sender));
            sender.sendMessage(CC.GREEN + "Done!");
        }
    }

    public void createTestRank() {
        RankBuilder rankBuilder = new RankBuilder("test");
        rankBuilder.build().save();
    }

    public void listGlobal() {
        OctoCoreCommon.getInstance().getServerManager().getOnlinePlayers().forEach((player) -> print(player.getName()));
    }

    public void amIMuted() {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerData playerData = PlayerManager.getInstance().getData(player);
            print(playerData.isMuted());
        }
    }

    public void isPermissibleInjected() {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            boolean b = player.getPermissibleBase() instanceof OctoPermissible;
            print(b);
        }
    }

    public void loadPunishments() {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerData playerData = PlayerManager.getInstance().getData(player);
            playerData.getPunishData().load();
        }
    }

    public void getDefaultRank() {
        Rank rank = OctoCore.getInstance().getRankManager().getDefaultRank();
        if (rank == null) {
            print("Rank is null!");
        } else {
            print(rank.getDisplayName());
        }
    }

    public void createDefaultRank() {
        Rank rank = OctoCore.getInstance().getRankManager().getDefaultRank();
        if (rank != null) {
            print("Rank already exists!");
            return;
        }
        OctoCore.getInstance().getRankManager().createDefaultRank();
    }

    public void testPacket() {
        PlayerMessagePacket packet = new PlayerMessagePacket(((Player) sender).getUniqueId(), "test");
        packet.send();
    }
}
