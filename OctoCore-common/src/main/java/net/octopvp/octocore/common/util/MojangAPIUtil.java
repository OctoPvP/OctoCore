package net.octopvp.octocore.common.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import org.shanerx.mojang.Mojang;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class MojangAPIUtil {
    public static final MojangAPIUtil INSTANCE = new MojangAPIUtil();

    private MojangAPIUtil() {
    }

    @Getter
    private final Mojang mojangAPI = new Mojang().connect();

    private final UUID NULL = UUID.randomUUID();

    private final CacheLoader<String, UUID> uuidCacheLoader = new CacheLoader<String, UUID>() {
        @Override
        public UUID load(String key) {
            UUID uuid = getUUIDFromMojang(key);
            return uuid == null ? NULL : uuid;
        }
    };

    private final LoadingCache<String, UUID> uuidCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build(uuidCacheLoader);
    private final CacheLoader<UUID, String> nameCacheLoader = new CacheLoader<UUID, String>() {
        @Override
        public String load(UUID key) throws Exception {
            return getNameFromMojang(key);
        }
    };

    private final LoadingCache<UUID, String> nameCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build(nameCacheLoader);

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

    private static final Pattern UUID_PATTERN = Pattern.compile("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)");

    @Deprecated
    public UUID getUUIDFromMojang(String name) {
        String s;
        try {
            s = mojangAPI.getUUIDOfUsername(name);
        } catch (Exception e) {
            return null;
        }
        return UUID.fromString(UUID_PATTERN.matcher(s).replaceAll("$1-$2-$3-$4-$5"));
    }

    public String getNameFromMojang(UUID uuid) {
        try {
            return mojangAPI.getPlayerProfile(uuid.toString()).getUsername();
        } catch (Exception e) {
            return null;
        }
    }

    public String getName(UUID uuid) {
        if (uuid == null) throw new NullPointerException("UUID cannot be null");
        try {
            return nameCache.get(uuid);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
