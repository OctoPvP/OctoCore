package net.octopvp.octocore.paper.utils;

import net.md_5.bungee.api.chat.TextComponent;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.paper.manager.impl.PlaceholderManager;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.text.MessageFormat;
import java.util.Set;
import java.util.UUID;

public class Sender implements CommandSender {
    private CommandSender commandSender;

    public Sender(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    @Override
    public void sendMessage(String s) {
        commandSender.sendMessage(PlaceholderManager.replacePlaceholders(s));
    }

    @Override
    public void sendMessage(String[] strings) {
        for (String string : strings) {
            commandSender.sendMessage(PlaceholderManager.replacePlaceholders(string));
        }
    }

    public void sendMessage(TextComponent textComponent) {
        getPlayer().sendMessage(textComponent);
    }

    public void sendMessage(Lang lang) {
        sendMessage(lang.getMsg());
    }

    @Override
    public Server getServer() {
        return commandSender.getServer();
    }

    @Override
    public String getName() {
        return commandSender.getName();
    }

    @Override
    public void sendFormattedMessage(String s, Object... objects) {
        this.sendMessage(MessageFormat.format(PlaceholderManager.replacePlaceholders(s), objects));
    }

    @Override
    public boolean isPermissionSet(String s) {
        return commandSender.isPermissionSet(s);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return commandSender.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(String s) {
        return commandSender.hasPermission(s);
    }

    public boolean hasPermission(Permissions permission) {
        return commandSender.hasPermission(permission.getNode());
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return commandSender.hasPermission(permission);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        return commandSender.addAttachment(plugin, s, b);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return commandSender.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        return commandSender.addAttachment(plugin, s, b, i);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return commandSender.addAttachment(plugin, i);
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {
        commandSender.removeAttachment(permissionAttachment);
    }

    @Override
    public void recalculatePermissions() {
        commandSender.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return commandSender.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return commandSender.isOp();
    }

    @Override
    public void setOp(boolean b) {
        commandSender.setOp(b);
    }

    public Player getPlayer() {
        try {
            return Bukkit.getPlayer(getName());
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isPlayer() {
        return commandSender instanceof Player;
    }

    public String getDisplayName() {
        return getPlayer() == null ? "CONSOLE" : getPlayer().getDisplayName();
    }

    public CommandSender getCommandSender() {
        return this.commandSender;
    }

    public void setCommandSender(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    public UUID getUUID() {
        return getPlayer() == null ? new UUID(0, 0) : getPlayer().getUniqueId();
    }

    public UUID getUniqueId() {
        return getUUID();
    }
}
