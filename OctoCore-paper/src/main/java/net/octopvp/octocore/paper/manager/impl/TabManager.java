package net.octopvp.octocore.paper.manager.impl;

import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.Manager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.tab.item.TextTabItem;
import net.octopvp.octocore.paper.utils.tab.tablist.TableTabList;
import net.octopvp.octocore.paper.utils.tab.util.Skin;
import net.octopvp.octocore.paper.utils.tab.util.Skins;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TabManager extends Manager {
    private static String value_color = CC.B + CC.GRAY;
    private static String title_color = CC.B + CC.AQUA;
    //private static Skin skin = Skins.getDot(ChatColor.GRAY);
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
                        PlayerData profile = PlayerManager.getPlayerProfiles().get(uuid);
                        Player player = Bukkit.getPlayer(profile.getUuid());
                        sendTab(player,profile);
                    }
                }
            },0l, OctoCore.getInstance().getConfig().getLong("update-tab-interval"));
        }
    }

    @Override
    public void disable() {

    }

    private static void sendTab(Player p, PlayerData p1){
        long start = System.currentTimeMillis();
        TableTabList tab = tablists.get(p.getUniqueId());
        if(tab == null){
            tab = OctoCore.getTab().newTableTabList(p);
            tablists.put(p.getUniqueId(),tab);
            //sendPing(tab);
        }
        tab.setHeaderFooter(header,footer);
        AtomicInteger collum = new AtomicInteger(-1);
        AtomicInteger row = new AtomicInteger(-1);
        TableTabList finalTab = tab;
        PlayerManager.getPlayerProfiles().forEach((uuid, data) -> {
            row.getAndIncrement();
            if (row.get() >= 9){
                row.set(0);
                collum.getAndIncrement();
            }
            Player player = Bukkit.getPlayer(uuid);
            finalTab.set(collum.get(),row.get(), new TextTabItem(data.getCurrentColor() + player.getDisplayName(),player.getPing(),Skins.getPlayer(data.getMainSkinUUID())));
        });

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
        /*
        if(OctoCore.getInstance().getConfig().getBoolean("health-display"))
            p.setScoreboard(SetupOther.getScoreboard());
         */
    }
    public static void onLeave(Player p) {
        if (OctoCore.getInstance().getConfig().getBoolean("default-tab")) {
            if(tablists.containsKey(p.getUniqueId())){
                tablists.remove(p.getUniqueId());
            }
        }
    }
}