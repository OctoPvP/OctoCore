package net.octopvp.octocore.paper.event.raffle;

import lombok.Getter;
import net.octopvp.octocore.common.object.ActionResult;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.event.Event;
import net.octopvp.octocore.paper.manager.impl.PlayerManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.utils.msg.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RaffleEvent implements Event {
    private static final Title title = new Title(CC.GOLD + "RAFFLE!");
    private String reward;
    private Player p;
    private List<UUID> rafflePlayers = new ArrayList<>();
    @Getter
    private static RaffleEvent currentRaffle;
    @Getter
    private static boolean isRaffleRunning = false;
    public RaffleEvent(String reward,Player p){
        this.reward = reward;
        this.p = p;
    }
    public ActionResult start(){
        if(isRaffleRunning()){
            return ActionResult.OTHER;
        }
        currentRaffle = this;
        PlayerData profile = PlayerManager.getProfile(p.getUniqueId());
        final String player1 = profile.getFormattedName(true);
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle(title);
            player.sendMessage(Lang.RAFFLE_STARTED.getMsg(reward,player1));
        });
        return ActionResult.SUCCESS;
    }
    public ActionResult addPlayer(UUID uuid){
        if(!isRaffleRunning)
            return ActionResult.OTHER;
        if(rafflePlayers.contains(uuid))
            Bukkit.getPlayer(uuid).sendMessage(Lang.ALREADY_IN_RAFFLE.getMsg());
        else{
            if(PlayerManager.getProfile(uuid) == null){
                Bukkit.getPlayer(uuid).sendMessage(Lang.PDATA_NOT_LOADING.getMsg());
                return ActionResult.ERROR;
            }
            else rafflePlayers.add(uuid);
        }
        return ActionResult.SUCCESS;
    }
    public void end(){
        Player p = getOnlinePlayerFromList(rafflePlayers);
        if(p == null){
            Bukkit.broadcastMessage(Lang.TOO_MANY_PLAYERS_LEFT_FOR_RAFFLE.getMsg());
            return;
        }
        PlayerData profile = PlayerManager.getProfile(p.getUniqueId());
        Bukkit.broadcastMessage(Lang.WON_RAFFLE.getMsg(profile.getFormattedName(true),rafflePlayers.size() + ""));
        rafflePlayers.clear();
    }
    private static Player getOnlinePlayerFromList(List<UUID> list){
        Random rand = new Random();
        UUID uuid = list.get(rand.nextInt(list.size()));
        Player p = Bukkit.getPlayer(uuid);
        int tries = 0;
        //  *efficiency*
        while(p == null){
            tries++;
            //shouldn't be over 15 but *redundancy*
            if(tries >= 15)
                break;
            if(tries == 1)
                list.remove(uuid);
            UUID randUUID = list.get(rand.nextInt(list.size()));
            p = Bukkit.getPlayer(randUUID);
            if(p == null)
                list.remove(randUUID);
        }
        return p;
    }
}