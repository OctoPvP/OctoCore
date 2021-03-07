package net.octopvp.octocore.paper.listeners;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCorePaper;
import net.octopvp.octocore.paper.manager.PlayerManager;
import net.octopvp.octocore.paper.utils.Logger;
import net.octopvp.octocore.paper.utils.database.DatabaseHelper;
import net.octopvp.octocore.paper.utils.tab.item.TextTabItem;
import net.octopvp.octocore.paper.utils.tab.tablist.TableTabList;
import net.octopvp.octocore.paper.utils.tab.util.Skin;
import net.octopvp.octocore.paper.utils.tab.util.Skins;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;

public class JoinLeaveListener implements Listener {
    private static String header = "";
    private static String footer = "";
    private static String value_color = CC.B + CC.GRAY;
    private static String title_color = CC.B + CC.AQUA;
    @EventHandler(priority = EventPriority.HIGH)
    public void onPreProcess(AsyncPlayerPreLoginEvent e){
        if(e.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED)
            return;
        PlayerManager.processJoin(e.getUniqueId());
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerLoginEvent e){
        Skin skin = Skins.BLOCK_COBBLE;
        Logger.debug(DatabaseHelper.GET_PROFILE.getSql(e.getPlayer().getUniqueId().toString()));
        Bukkit.getScheduler().scheduleSyncDelayedTask(OctoCorePaper.getInstance(), new Runnable() {
            @Override
            public void run() {
                TableTabList tab = OctoCorePaper.getTab().newTableTabList(e.getPlayer());
                tab.setHeaderFooter(header,footer);
                int i = 0;
                while (true){
                    i++;
                    if(i > 80)
                        break;
                    else{
                        if(i <= 20)
                            tab.set(0,i-1,new TextTabItem("",-1));
                        else if(i <= 40)
                            tab.set(1,i-20,new TextTabItem("",-1));
                        else if(i <= 60)
                            tab.set(2,i-40,new TextTabItem("",-1));
                        else if(i <= 80)
                            tab.set(3,i-60,new TextTabItem("",-1));
                        else break;
                    }
                }
                tab.set(0,0, new TextTabItem(title_color + "Server Stats", -1,skin));
                tab.set(0,2, new TextTabItem(value_color + "Online: 100", -1,skin));
                tab.set(0,4, new TextTabItem(value_color + "KitPvP: 50", -1,skin));
                tab.set(0,6, new TextTabItem(value_color + "Duels: 50", -1,skin));


                tab.set(1,0, new TextTabItem(title_color + "You (" + e.getPlayer().getDisplayName() + ")", -1,skin));
                tab.set(1,2, new TextTabItem(value_color + "Rank: " + PlayerManager.getProfile(e.getPlayer().getUniqueId()).getPrefix(), -1,skin));
                tab.set(1,4, new TextTabItem(value_color + "Coins: 100", -1,skin));
                tab.set(1,6, new TextTabItem(value_color + "XP: 100", -1,skin));
                tab.set(1,8, new TextTabItem(value_color + "Last Login: 1/1/2021", -1,skin));


                tab.set(2,0, new TextTabItem(title_color + "Stats", -1,skin));
                tab.set(2,2, new TextTabItem(value_color + "Kills: 1000", -1,skin));
                tab.set(2,4, new TextTabItem(value_color + "Deaths: 0", -1,skin));
                tab.set(2,6, new TextTabItem(value_color + "Duels Won: 1000", -1,skin));
                tab.set(2,6, new TextTabItem(value_color + "Duels Lost: 0", -1,skin));
                tab.set(2,8, new TextTabItem(value_color + "KDR: 1000:0", -1,skin));


                tab.set(3,0, new TextTabItem(title_color + "Misc", -1,skin));
                tab.set(3,2, new TextTabItem(value_color + "Client: Lunar Client", -1,skin));
                tab.set(3,4, new TextTabItem(value_color + "Ping: 1", -1,skin));
            }
        },20l);
    }
    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent e){
        PlayerManager.processLeave(e.getPlayer());
    }
    public static void init(){
        /*
        List<String> headerlist = OctoCorePaper.getInstance().getConfig().getStringList("tab.header");
        List<String> footerlist = OctoCorePaper.getInstance().getConfig().getStringList("tab.footer");
        StringBuilder sbheader = new StringBuilder();
        headerlist.forEach(header->{
            if(header == "")
                sbheader.append(CC.R + "\n");
            else sbheader.append("\n" + header + "\n");
        });
        StringBuilder sbfooter = new StringBuilder();
        footerlist.forEach(footer->{
            if(footer == "")
                sbfooter.append(CC.R + "\n");
            else sbfooter.append("\n" + footer + "\n");
        });
        header = sbheader.toString();
        footer = sbfooter.toString();
         */
        header = ChatColor.translateAlternateColorCodes('&',OctoCorePaper.getInstance().getConfig().getString("tab.header")).replace("\\n","\n");
        footer = ChatColor.translateAlternateColorCodes('&',OctoCorePaper.getInstance().getConfig().getString("tab.footer").replace("\\n","\n"));

    }
}