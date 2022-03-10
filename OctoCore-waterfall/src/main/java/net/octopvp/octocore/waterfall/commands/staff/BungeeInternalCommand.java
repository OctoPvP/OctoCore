package net.octopvp.octocore.waterfall.commands.staff;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.octopvp.octocore.waterfall.manager.StaffManager;

public class BungeeInternalCommand extends Command {
    public BungeeInternalCommand() {
        super("bungeeinternal");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (sender.getName() != "Badbird5907") {
                sender.sendMessage(ChatColor.RED + "This can only be executed from console.");
                return;
            }
        }
        if (args.length > 1) {
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("sendjoin")) {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[1]);
                    StaffManager.join(player.getServer().getInfo(), player);
                } else if (args[0].equalsIgnoreCase("sendleave")) {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[1]);
                    StaffManager.leave(player.getServer().getInfo(), player);
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("sendswitch")) {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[1]);
                    ServerInfo from = ProxyServer.getInstance().getServerInfo(args[2]);
                    StaffManager.sendSwitch(from, player);
                }
            }
        }
    }
}
