package net.octopvp.octocore.core.command.impl.punishments;

import com.mongodb.client.model.Filters;
import net.octopvp.commander.annotation.*;
import net.octopvp.octocore.common.object.Permissions;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.manager.impl.PlayerManager;
import net.octopvp.octocore.core.module.impl.punishments.PunishModule;
import net.octopvp.octocore.core.utils.msg.Lang;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StaffRollBackCommand {
    @Completer(name = "staffrollback", index = 2)
    public List<String> typeCompleter() {
        return Arrays.asList("bans", "mutes", "blacklists", "warns");
    }


    @Command(name = "staffrollback")
    @Permission(Permissions.PUNISHMENT_STAFFROLLBACK)
    public CommandResult execute(CommandSender sender, @Name("player") String targetStr, @Duration long time, @Required @Name("bans/mutes/blacklists/warns") String type) {
        Tasks.runAsync(() -> {
            OfflinePlayer target = Bukkit.getOfflinePlayer(PlayerManager.getInstance().getFixedName(targetStr));
            if (!PlayerManager.getInstance().doesDocumentExistByUUID(target.getUniqueId()) && !targetStr.equalsIgnoreCase("console")) {
                sender.sendMessage(Lang.COULD_NOT_FIND_DATA.toString());
                return;
            }
            long check = System.currentTimeMillis() - time;
            if (type.equalsIgnoreCase("bans")) {
                sender.sendMessage(Lang.STAFF_ROLLBACK_WIPING.getMsg(target.getName(), "bans"));
                AtomicInteger expired = new AtomicInteger(0);
                AtomicInteger active = new AtomicInteger(0);
                PunishModule.getPunishments().find(Filters.eq("type", PunishmentType.BAN.toString())).into(new ArrayList<>()).forEach(document -> {
                    if (document.containsKey("addedByName") && document.getString("addedByName").equalsIgnoreCase(targetStr)) {
                        if (document.containsKey("addedAt")) {
                            long addedAt = document.getLong("addedAt");

                            if (check <= addedAt) {
                                PunishModule.getPunishments().deleteOne(document);
                                if (document.containsKey("active") && document.getBoolean("active")) {
                                    active.getAndIncrement();
                                } else {
                                    expired.getAndIncrement();
                                }
                            }
                        }
                    }
                });
                if (expired.get() + active.get() == 0) {
                    sender.sendMessage(Lang.STAFF_ROLLBACK_DONT_HAVE_HISTORY.getMsg(target.getName(), "bans"));
                    return;
                }
                sender.sendMessage(Lang.STAFF_ROLLBACK_WIPED.getMsg(
                        target.getName(),
                        expired.get() + active.get(),
                        "ban",
                        active.get(),
                        expired.get()
                ));
                return;
            }
            if (type.equalsIgnoreCase("mutes")) {
                sender.sendMessage(Lang.STAFF_ROLLBACK_WIPING.getMsg(target.getName(), "mutes"));
                AtomicInteger expired = new AtomicInteger(0);
                AtomicInteger active = new AtomicInteger(0);
                PunishModule.getPunishments().find(Filters.eq("type", PunishmentType.MUTE.toString())).into(new ArrayList<>()).forEach(document -> {
                    if (document.containsKey("addedByName") && document.getString("addedByName").equalsIgnoreCase(targetStr)) {
                        if (document.containsKey("addedAt")) {
                            long addedAt = document.getLong("addedAt");

                            if (check <= addedAt) {
                                PunishModule.getPunishments().deleteOne(document);
                                if (document.containsKey("active") && document.getBoolean("active")) {
                                    active.getAndIncrement();
                                } else {
                                    expired.getAndIncrement();
                                }
                            }
                        }
                    }
                });
                if (expired.get() + active.get() == 0) {
                    sender.sendMessage(Lang.STAFF_ROLLBACK_DONT_HAVE_HISTORY.getMsg(target.getName(), "mutes"));
                    return;
                }
                sender.sendMessage(Lang.STAFF_ROLLBACK_WIPED.getMsg(
                        target.getName(),
                        expired.get() + active.get(),
                        "mute",
                        active.get(),
                        expired.get()
                ));
                return;
            }
            if (type.equalsIgnoreCase("blacklists")) {
                sender.sendMessage(Lang.STAFF_ROLLBACK_WIPING.getMsg(target.getName(), "blacklists"));
                AtomicInteger expired = new AtomicInteger(0);
                AtomicInteger active = new AtomicInteger(0);
                PunishModule.getPunishments().find(Filters.eq("type", PunishmentType.BLACKLIST.toString())).into(new ArrayList<>()).forEach(document -> {
                    if (document.containsKey("addedByName") && document.getString("addedByName").equalsIgnoreCase(targetStr)) {
                        if (document.containsKey("addedAt")) {
                            long addedAt = document.getLong("addedAt");

                            if (check <= addedAt) {
                                PunishModule.getPunishments().deleteOne(document);
                                if (document.containsKey("active") && document.getBoolean("active")) {
                                    active.getAndIncrement();
                                } else {
                                    expired.getAndIncrement();
                                }
                            }
                        }
                    }
                });
                if (expired.get() + active.get() == 0) {
                    sender.sendMessage(Lang.STAFF_ROLLBACK_DONT_HAVE_HISTORY.getMsg(target.getName(), "blacklists"));
                    return;
                }
                sender.sendMessage(Lang.STAFF_ROLLBACK_WIPED.getMsg(
                        target.getName(),
                        expired.get() + active.get(),
                        "blacklist",
                        active.get(),
                        expired.get()
                ));
                return;
            }
            if (type.equalsIgnoreCase("warns")) {
                sender.sendMessage(Lang.STAFF_ROLLBACK_WIPING.getMsg(target.getName(), "warns"));
                AtomicInteger expired = new AtomicInteger(0);
                AtomicInteger active = new AtomicInteger(0);
                PunishModule.getPunishments().find(Filters.eq("type", PunishmentType.WARN.toString())).into(new ArrayList<>()).forEach(document -> {
                    if (document.containsKey("addedByName") && document.getString("addedByName").equalsIgnoreCase(targetStr)) {
                        if (document.containsKey("addedAt")) {
                            long addedAt = document.getLong("addedAt");

                            if (check <= addedAt) {
                                PunishModule.getPunishments().deleteOne(document);
                                if (document.containsKey("active") && document.getBoolean("active")) {
                                    active.getAndIncrement();
                                } else {
                                    expired.getAndIncrement();
                                }
                            }
                        }
                    }
                });
                if (expired.get() + active.get() == 0) {
                    sender.sendMessage(Lang.STAFF_ROLLBACK_DONT_HAVE_HISTORY.getMsg(target.getName(), "warns"));
                    return;
                }
                sender.sendMessage(Lang.STAFF_ROLLBACK_WIPED.getMsg(
                        target.getName(),
                        expired.get() + active.get(),
                        "warn",
                        active.get(),
                        expired.get()
                ));
                return;
            }
            sender.sendMessage(CC.translate("&cCorrect usage: /staffrollback <staff> <time> <type>"));
            sender.sendMessage(CC.translate("&cFor the type you can use 'Bans, Mutes, Blacklists or Warns'"));
        });
        return CommandResult.SUCCESS;
    }
}
