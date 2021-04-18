package net.octopvp.octocore.common.database;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;

@RequiredArgsConstructor
@Deprecated
//don't use as not really good lol
public class RedisUtils {
    private Jedis jedis;
    public boolean isPlayerOnline(String uuid){
        String resp = jedis.get(uuid + "|online");
        if(resp == null || resp == "false")
            return false;
        return true;
    }
    public void setIsPlayerOnline(String uuid,boolean b){
        jedis.set(uuid + "|online",b + "");
        return;
    }
}
