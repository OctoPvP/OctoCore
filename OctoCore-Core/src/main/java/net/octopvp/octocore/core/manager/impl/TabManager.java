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

public class TabManager extends Manager {
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
        OctoCore.getInstance().getServerImplementation().setTabHeaderFooter(player, AdventureUtils.format(header), AdventureUtils.format(footer));
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
