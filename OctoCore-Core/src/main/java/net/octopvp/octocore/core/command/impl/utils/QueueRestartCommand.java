package net.octopvp.octocore.core.command.impl.utils;

import com.cryptomorin.xseries.XSound;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.octopvp.commander.annotation.*;
import net.octopvp.octocore.common.object.DisconnectReason;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class QueueRestartCommand {
    public static NamedTextColor getColor(int i) {
        if (i <= 2) {
            return NamedTextColor.RED;
        } else if (i <= 5) {
            return NamedTextColor.YELLOW;
        } else {
            return NamedTextColor.GREEN;
        }
    }

    private BukkitTask task;
    private int count;

    @Command(name = "queuerestart", aliases = "restart")
    @Permission(Permissions.ADMIN)
    public void execute(@Sender CommandSender sender, @Switch(value = "cancel", aliases = "c") boolean cancel, @Switch("now") boolean now, @Flag("time") @DefaultNumber(30) int time, @Optional @JoinStrings String reason) {
        if (now) {
            Bukkit.spigot().restart();
            return;
        }
        String reasonStr;
        if (reason != null && !reason.isEmpty()) {
            if (reason.equalsIgnoreCase("update")) {
                reasonStr = CC.GREEN + "Game Update";
            } else if (reason.equalsIgnoreCase("scheduled")) {
                reasonStr = CC.GREEN + "Scheduled Restart";
            } else {
                reasonStr = reason;
            }
        } else {
            reasonStr = "";
        }
        if (cancel) {
            if (task != null) {
                task.cancel();
                task = null;
                Bukkit.broadcastMessage(CC.GREEN + "Restart cancelled.");
                return;
            } else {
                sender.sendMessage(CC.RED + "No restart scheduled.");
                return;
            }
        }
        if (task != null) {
            sender.sendMessage(CC.RED + "Restart already scheduled.");
            return;
        }
        count = 0;

        task = Bukkit.getScheduler().runTaskTimer(OctoCore.getInstance(), () -> {
            count = count <= 10 ? count + 1 : -1;
            if (count >= 10 || count == -1) {
                task.cancel();
                Logger.info("Restarting Server");

                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF("lobby");
                byte[] arr = out.toByteArray();
                String strReason = new DisconnectReason("This server is restarting!").toString();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    //player.kickPlayer(new DisconnectReason("This server is restarting!").toString());
                    player.sendMessage(strReason);
                    player.sendPluginMessage(OctoCore.getInstance(), "BungeeCord", arr);
                }
                Tasks.runLater(() -> Bukkit.spigot().restart(), 40L);
                return;
            }
            int left = 10 - count;
            for (Player player : Bukkit.getOnlinePlayers()) {
                Title title = getTitle(reasonStr, left);
                OctoCore.getInstance().getServerImplementation().sendTitle(player, title);
                // player.sendMessage(CC.RED + CC.translate("This server is restarting in " + getColor(left) + left + "&c seconds!"));
                player.sendMessage(Component.text("This server is restarting in ", NamedTextColor.RED)
                        .append(Component.text(left, getColor(left)))
                        .append(Component.text(" seconds!", NamedTextColor.RED)));
                if (left <= 3) {
                    player.playSound(player.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1, 1);
                }
            }
        }, 20, 20);
    }

    @NotNull
    private static Title getTitle(String reasonStr, int left) {
        Title title;
        if (reasonStr.isEmpty()) {
            // player.sendTitle(CC.translate("&cServer Restart"), CC.translate("&cThis server is restarting in " + getColor(left) + left + "&c seconds!"), 2, 60, 10);
            title = Title.title(
                    Component.text("Server Restart", NamedTextColor.GREEN),
                    Component.text("This server is restarting in ", NamedTextColor.RED)
                            .append(Component.text(left, getColor(left)))
                            .append(Component.text(" seconds!", NamedTextColor.RED))
                    , Title.Times.times(
                            Duration.ofMillis(100),
                            Duration.ofSeconds(3),
                            Duration.ofSeconds(10)
                    ));
        } else {
            // player.sendTitle(CC.translate("&cServer Restart In " + getColor(left) + left + "&c Seconds"), CC.translate(CC.GOLD + "Restarting for: " + CC.GREEN + reasonStr), 2, 60, 10);
            title = Title.title(
                    Component.text("Server Restart In ", NamedTextColor.GREEN)
                            .append(Component.text(left, getColor(left)))
                            .append(Component.text(" seconds!", NamedTextColor.GREEN)),
                    Component.text("Restarting for: ", NamedTextColor.GOLD)
                            .append(Component.text(reasonStr, NamedTextColor.GREEN))
                    , Title.Times.times(
                            Duration.ofMillis(100),
                            Duration.ofSeconds(3),
                            Duration.ofSeconds(10)
                    ));
        }
        return title;
    }
}
