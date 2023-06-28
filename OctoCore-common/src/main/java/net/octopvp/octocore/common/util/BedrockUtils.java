package net.octopvp.octocore.common.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class BedrockUtils {
    private static CacheLoader<String, Long> xuidCacheLoader = new CacheLoader<String, Long>() {
        @Override
        public Long load(String key) throws Exception {
            return getXUID(key).get();
        }
    };

    private static LoadingCache<String, Long> xuidCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).maximumSize(200).build(xuidCacheLoader);

    private static CacheLoader<Long, String> gamerTagCacheLoader = new CacheLoader<Long, String>() {
        @Override
        public String load(Long key) throws Exception {
            return getGamerTag(key).get();
        }
    };
    private static LoadingCache<Long, String> gamerTagCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).maximumSize(200).build(gamerTagCacheLoader);


    public static Function<UUID, Boolean> isBedrockPlayer = uuid -> uuid.getMostSignificantBits() == 0 && uuid.getLeastSignificantBits() != 0; // 0-0 is console

    public static boolean isBedrockPlayer(UUID uuid) {
        return isBedrockPlayer.apply(uuid);
    }

    public static String bedrockPrefix = "*";

    private static final OkHttpClient client = new OkHttpClient().newBuilder().build();

    public static CompletableFuture<Long> getXUID(String gamertag1) {
        String gamertag = gamertag1.replace("*", "");
        if (xuidCache.getIfPresent(gamertag) != null) {
            return CompletableFuture.completedFuture(xuidCache.getIfPresent(gamertag));
        }
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder().url("https://api.geysermc.org/v2/xbox/xuid/" + gamertag).build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String result = null;
            try {
                result = response.body().string();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            long xuid = Long.parseLong(result.split(":")[1].replace("}", ""));
            xuidCache.put(gamertag.toLowerCase(), xuid);
            return xuid; // parsing json is for nerds
        });
    }

    public static CompletableFuture<UUID> getXUIDAsUUID(String gamertag) {
        return getXUID(gamertag).thenApply(xuid -> new UUID(0, xuid));
    }

    public static CompletableFuture<String> getGamerTag(long xuid) {
        if (gamerTagCache.getIfPresent(xuid) != null) {
            return CompletableFuture.completedFuture(gamerTagCache.getIfPresent(xuid));
        }
        return CompletableFuture.supplyAsync(() -> {
            Request request = new Request.Builder().url("https://api.geysermc.org/v2/xbox/gamertag/" + xuid).build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String result = null;
            try {
                result = response.body().string();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String s = result.split(":")[1].replace("}", "");
            s = s.substring(1, s.length() - 1);
            xuidCache.put(s.toLowerCase(), xuid);
            gamerTagCache.put(xuid, s);
            return s;
        });
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class BedrockInfo {
        private long xuid;
        private String gamertag;

        public UUID getUUID() {
            return new UUID(0, xuid);
        }
    }
    public static CompletableFuture<BedrockInfo> getBedrockInfo(long xuid) {
        if (gamerTagCache.getIfPresent(xuid) != null) {
            return CompletableFuture.completedFuture(new BedrockInfo(xuid, gamerTagCache.getIfPresent(xuid)));
        }
        return getGamerTag(xuid).thenApply(gamertag -> new BedrockInfo(xuid, gamertag));
    }

    public static CompletableFuture<BedrockInfo> getBedrockInfo(String gamerTag) {
        Long cached = xuidCache.getIfPresent(gamerTag.toLowerCase());
        if (cached != null) {
            return CompletableFuture.completedFuture(new BedrockInfo(cached, gamerTag));
        }
        return getXUID(gamerTag).thenApply(xuid -> new BedrockInfo(xuid, gamerTag));
    }
}
