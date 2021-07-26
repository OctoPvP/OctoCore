package net.octopvp.octocore.waterfall.manager;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.octopvp.octocore.common.object.redis.JedisAction;
import net.octopvp.octocore.common.util.json.JsonChain;
import net.octopvp.octocore.waterfall.OctoCoreWaterfall;

public class StaffManager {
    public static void join(ServerInfo serverInfo, ProxiedPlayer player){
        OctoCoreWaterfall.getInstance().getRedisData().write(JedisAction.STAFF_CONNECT,new JsonChain().addProperty("name",player.getName()).addProperty("server",serverInfo.getName()).get());
    }
    public static void sendSwitch(ServerInfo serverInfo, ProxiedPlayer player){
        OctoCoreWaterfall.getInstance().getRedisData().write(JedisAction.STAFF_SWITCH,new JsonChain().addProperty("name",player.getName()).addProperty("from",serverInfo.getName()).addProperty("to",player.getServer().getInfo().getName()).get());
    }
    public static void leave(ServerInfo serverInfo,ProxiedPlayer player){
        OctoCoreWaterfall.getInstance().getRedisData().write(JedisAction.STAFF_DISCONNECT,new JsonChain().addProperty("name",player.getName()).addProperty("server",serverInfo.getName()).get());
    }
}
