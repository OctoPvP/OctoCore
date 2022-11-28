package net.octopvp.octocore.core.manager.impl;

import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import net.octopvp.octocore.core.manager.Manager;
import net.octopvp.octocore.core.utils.tab.DefaultTabProvider;
import net.octopvp.octocore.core.utils.tab.TabHandler;
import net.octopvp.octocore.core.utils.tab.TabProvider;
import net.octopvp.octocore.core.utils.tab.entry.TabElement;
import net.octopvp.octocore.core.utils.tab.implementation.v1_8_R3TabAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TabManager extends Manager {
    @Getter
    @Setter
    private String header, footer;
    @Getter
    @Setter
    private TabProvider tabProvider = new DefaultTabProvider();

    @Override
    public void init(OctoCore plugin) {
        if (tabProvider == null) {
            tabProvider = new DefaultTabProvider();
        }

        header = CC.translate(OctoCore.getInstance().getConfig().getString("tab.header")).replace("\\n", "\n");
        footer = CC.translate(OctoCore.getInstance().getConfig().getString("tab.footer").replace("\\n", "\n"));
        if (tabProvider.useDefaultTab()) {
            new TabUpdateRunnable().runTaskTimer(plugin, 20L, tabProvider.getInterval());
        } else setupTab();
    }

    public void setupTab() {
        new TabHandler(player -> {
            TabElement element = tabProvider.getTab();
            if (tabProvider.showHeader()) {
                element.setHeader(tabProvider.getHeader());
            } else if (tabProvider.showFooter()) {
                element.setFooter(tabProvider.getFooter());
            }
            return element;
        },
                OctoCore.getInstance(),
                tabProvider.getInterval());
    }

    public void setHeaderFooter(Player player, String header, String footer) {
        v1_8_R3TabAdapter.INSTANCE.sendHeaderFooter(player, header, footer);
    }

    @Override
    public void disable() {

    }

    private class TabUpdateRunnable extends BukkitRunnable {

        @Override
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                setHeaderFooter(player, tabProvider.getHeader(), tabProvider.getFooter());
            }
        }
    }
}
