package net.octopvp.octocore.master.master.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import org.shanerx.mojang.Mojang;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class AccountUtil {
    @Getter
    private Mojang mojangAPI = new Mojang().connect();
    ;

    @PostConstruct
    public void init() {

    }


    private final UUID NULL = UUID.randomUUID();

    private CacheLoader<String, UUID> uuidCacheLoader = new CacheLoader<>() {
        @Override
        public UUID load(String key) {
            UUID uuid = getUUIDFromMojang(key);
            return uuid == null ? NULL : uuid;
        }
    };

    private LoadingCache<String, UUID> uuidCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build(uuidCacheLoader);

    public UUID getUUID(String name) {
        try {
            UUID a = uuidCache.get(name);
            if (a == NULL) {
                return null;
            }
            return a;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Deprecated
    public UUID getUUIDFromMojang(String name) {
        String s;
        try {
            s = mojangAPI.getUUIDOfUsername(name);
        } catch (Exception e) {
            return null;
        }
        return UUID.fromString(s.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
        ));
    }
}
