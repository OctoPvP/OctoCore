package net.octopvp.octocore.core.manager.impl;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.Manager;
import net.octopvp.octocore.core.utils.AdventureUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TabManager extends Manager {
    @Getter
    private static final List<Function<Player, String>> footerExtensions = new ArrayList<>();

    @Getter
    @Setter
    private String header, footer;

    @Override
    public void init(OctoCore plugin) {
        header = CC.translate(OctoCore.getInstance().getConfig().getString("tab.header")).replace("\\n", "\n");
        footer = CC.translate(OctoCore.getInstance().getConfig().getString("tab.footer").replace("\\n", "\n"));
        new TabUpdateRunnable().runTaskTimerAsynchronously(plugin, 0L, 40L);
    }

    public void setHeaderFooter(Player player, String header, String footer) {
        StringBuilder finalFooter = new StringBuilder(footer);
        for (Function<Player, String> extension : footerExtensions) {
            String append = extension.apply(player);
            if (append != null && !append.isEmpty()) {
                finalFooter.append("\n").append(append);
            }
        }
        OctoCore.getInstance().getServerImplementation().setTabHeaderFooter(player, AdventureUtils.format(header), AdventureUtils.format(finalFooter.toString()));
    }

    public void updateTab(Player player) {
        setHeaderFooter(player, header, footer);
    }

    public void resetTab(Player player) {
        OctoCore.getInstance().getServerImplementation().setTabHeaderFooter(player, AdventureUtils.format(""), AdventureUtils.format(""));
    }

    @Override
    public void disable() {

    }

    private class TabUpdateRunnable extends BukkitRunnable {

        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                setHeaderFooter(player, header, footer);
            }
        }
    }
}
