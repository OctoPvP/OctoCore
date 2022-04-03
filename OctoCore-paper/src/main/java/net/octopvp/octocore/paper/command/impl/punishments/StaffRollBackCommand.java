package net.octopvp.octocore.paper.command.impl.punishments;

import com.mongodb.client.model.Filters;
import net.octopvp.octocore.common.object.Permission;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.DateUtils;
import net.octopvp.octocore.paper.command.BaseCommand;
import net.octopvp.octocore.paper.command.Command;
import net.octopvp.octocore.paper.command.CommandResult;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.module.impl.punishments.PunishModule;
import net.octopvp.octocore.paper.module.impl.punishments.util.PunishmentType;
import net.octopvp.octocore.paper.utils.Sender;
import net.octopvp.octocore.paper.utils.msg.Lang;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class StaffRollBackCommand extends BaseCommand {
    @Command(name = "staffrollback", permission = Permission.PUNISHMENT_STAFFROLLBACK)
    public CommandResult execute(Sender sender, String[] args) {
        Tasks.runAsync(() -> {
            if (args.length < 2) {
                sender.sendMessage(CC.translate("&cUsage: /staffrollback <staff> <time> <Bans/Mutes/Blacklists/Warns>"));
                sender.sendMessage(CC.translate("&cFor the type you can use 'Warns, Mutes, Bans or Blacklists'"));
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(PlayerManager.getInstance().getFixedName(args[0]));
            if (!PlayerManager.getInstance().doesDocumentExistByUUID(target.getUniqueId()) && !args[0].equalsIgnoreCase("console")) {
                sender.sendMessage(Lang.COULD_NOT_FIND_DATA);
                return;
            }
            long time = -1L;
            try {
                time = System.currentTimeMillis() - DateUtils.parseDateDiff(args[1], false);
            } catch (Exception e) {
                sender.sendMessage(Lang.INVALID_TIME_INPUT.toString());
                return;
            }
            long check = System.currentTimeMillis() - time;
            if (args[2].equalsIgnoreCase("bans")) {
                sender.sendMessage(Lang.STAFF_ROLLBACK_WIPING.getMsg("bans"));
                AtomicInteger expired = new AtomicInteger(0);
                AtomicInteger active = new AtomicInteger(0);
                PunishModule.getPunishments().find(Filters.eq("type", PunishmentType.BAN.toString())).into(new ArrayList<>()).forEach(document -> {
                    if (document.containsKey("addedByName") && document.getString("addedByName").equalsIgnoreCase(args[0])) {
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
            if (args[2].equalsIgnoreCase("mutes")) {
                sender.sendMessage(Lang.STAFF_ROLLBACK_WIPING.getMsg("mutes"));
                AtomicInteger expired = new AtomicInteger(0);
                AtomicInteger active = new AtomicInteger(0);
                PunishModule.getPunishments().find(Filters.eq("type", PunishmentType.MUTE.toString())).into(new ArrayList<>()).forEach(document -> {
                    if (document.containsKey("addedByName") && document.getString("addedByName").equalsIgnoreCase(args[0])) {
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
            if (args[2].equalsIgnoreCase("blacklists")) {
                sender.sendMessage(Lang.STAFF_ROLLBACK_WIPING.getMsg("blacklists"));
                AtomicInteger expired = new AtomicInteger(0);
                AtomicInteger active = new AtomicInteger(0);
                PunishModule.getPunishments().find(Filters.eq("type", PunishmentType.BLACKLIST.toString())).into(new ArrayList<>()).forEach(document -> {
                    if (document.containsKey("addedByName") && document.getString("addedByName").equalsIgnoreCase(args[0])) {
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
            if (args[2].equalsIgnoreCase("warns")) {
                sender.sendMessage(Lang.STAFF_ROLLBACK_WIPING.getMsg("warns"));
                AtomicInteger expired = new AtomicInteger(0);
                AtomicInteger active = new AtomicInteger(0);
                PunishModule.getPunishments().find(Filters.eq("type", PunishmentType.WARN.toString())).into(new ArrayList<>()).forEach(document -> {
                    if (document.containsKey("addedByName") && document.getString("addedByName").equalsIgnoreCase(args[0])) {
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
