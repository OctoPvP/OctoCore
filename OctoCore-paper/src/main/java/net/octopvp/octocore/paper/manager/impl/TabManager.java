package net.octopvp.octocore.paper.manager.impl;

import com.lunarclient.bukkitapi.LunarClientAPI;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerProfile;
import net.octopvp.octocore.paper.utils.Logger;
import net.octopvp.octocore.paper.utils.tab.item.TextTabItem;
import net.octopvp.octocore.paper.utils.tab.tablist.TableTabList;
import net.octopvp.octocore.paper.utils.tab.util.Skin;
import net.octopvp.octocore.paper.utils.tab.util.Skins;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class TabManager implements Manager {
    private static String value_color = CC.B + CC.GRAY;
    private static String title_color = CC.B + CC.AQUA;
    private static Skin skin = Skins.getDot(ChatColor.GRAY);
    private static String header = "";
    private static String footer = "";
    private static HashMap<UUID, TableTabList> tablists = new HashMap<>();
    @Override
    public void init(OctoCore plugin) {
        header = ChatColor.translateAlternateColorCodes('&', OctoCore.getInstance().getConfig().getString("tab.header")).replace("\\n","\n");
        footer = ChatColor.translateAlternateColorCodes('&', OctoCore.getInstance().getConfig().getString("tab.footer").replace("\\n","\n"));
        if(plugin.getConfig().getBoolean("default-tab")){
            Bukkit.getScheduler().scheduleSyncRepeatingTask(OctoCore.getInstance(), () -> {
                if(!(Bukkit.getOnlinePlayers().toArray().length == 0)){
                    for (UUID uuid : PlayerManager.getPlayerProfiles().keySet()){
                        PlayerProfile profile = PlayerManager.getPlayerProfiles().get(uuid);
                        Player player = Bukkit.getPlayer(profile.getUuid());
                        Logger.debug("Sending tab");
                        sendTab(player,profile);
                    }
                }
            },0l, OctoCore.getInstance().getConfig().getLong("update-tab-interval"));
        }
    }

    @Override
    public void disable(OctoCore plugin) {

    }

    private static void sendTab(Player p, PlayerProfile profile){
        long start = System.currentTimeMillis();
        TableTabList tab = tablists.get(p.getUniqueId());
        if(tab == null){
            tab = OctoCore.getTab().newTableTabList(p);
            tablists.put(p.getUniqueId(),tab);
            sendPing(tab);
        }
        tab.setHeaderFooter(header,footer);

        tab.set(0,0, new TextTabItem(title_color + "Server Stats", -1,skin));
        tab.set(0,2, new TextTabItem(value_color + "Online: 100", -1,skin));
        tab.set(0,4, new TextTabItem(value_color + "KitPvP: 50", -1,skin));
        tab.set(0,6, new TextTabItem(value_color + "Duels: 50", -1,skin));


        tab.set(1,0, new TextTabItem(title_color + "You (" + p.getDisplayName() + ")", -1,skin));
        tab.set(1,2, new TextTabItem(value_color + "Rank: " + PlayerManager.getProfile(p.getUniqueId()).getPrefix(), -1,skin));
        tab.set(1,4, new TextTabItem(value_color + "Coins: " + profile.getCoins(), -1,skin));
        tab.set(1,6, new TextTabItem(value_color + "XP: " + profile.getXp(), -1,skin));
        tab.set(1,8, new TextTabItem(value_color + "Last Login: 1/1/2021", -1,skin));


        tab.set(2,0, new TextTabItem(title_color + "Stats", -1,skin));
        tab.set(2,2, new TextTabItem(value_color + "Kills: 1000", -1,skin));
        tab.set(2,4, new TextTabItem(value_color + "Deaths: 0", -1,skin));
        tab.set(2,6, new TextTabItem(value_color + "Duels Won: 1000", -1,skin));
        tab.set(2,6, new TextTabItem(value_color + "Duels Lost: 0", -1,skin));
        tab.set(2,8, new TextTabItem(value_color + "KDR: 1000:0", -1,skin));


        tab.set(3,0, new TextTabItem(title_color + "Misc", -1,skin));
        tab.set(3,2, new TextTabItem(value_color + "Client: " + (LunarClientAPI.getInstance().isRunningLunarClient(p) ? "Lunar Client" : "Other"), -1,skin));
        tab.set(3,4, new TextTabItem(value_color + "Ping: " + (Bukkit.getPlayer(profile.getUuid()).getPing()), -1,skin));
        Logger.debug("Sending tab took " +  (System.currentTimeMillis() - start) + " ms.");
    }
    private static void sendPing(TableTabList tab){
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
    }
    public static void onJoin(Player p){
        if(OctoCore.getInstance().getConfig().getBoolean("default-tab")) {
            if(PlayerManager.getPlayerProfiles().containsKey(p.getUniqueId()))
                sendTab(p, PlayerManager.getProfile(p.getUniqueId()));
        }
    }
    public static void onLeave(Player p) {
        if (OctoCore.getInstance().getConfig().getBoolean("default-tab")) {
            if(tablists.containsKey(p.getUniqueId())){
                tablists.remove(p.getUniqueId());
            }
        }
    }
}