package net.octopvp.octocore.paper.listeners;

import net.octopvp.octocore.paper.manager.NickManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TabCompleteEvent implements Listener {
    @EventHandler
    public void onTab(org.bukkit.event.server.TabCompleteEvent event){
        NickManager.getNicked().keySet().forEach(key->{

            if(event.getCompletions().contains(NickManager.getNicked().get(key))) {
                event.getCompletions().remove(NickManager.getNicked().get(key));
                event.getCompletions().add(NickManager.getNicked().get(key));
            }
        });
    }
}
