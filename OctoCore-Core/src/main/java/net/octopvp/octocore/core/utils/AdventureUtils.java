package net.octopvp.octocore.core.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.core.OctoCore;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.regex.Pattern;

public class AdventureUtils {
    private static final boolean useCache = Boolean.getBoolean("octocore.cache.adventure");
    private static final Pattern MINI_MESSAGE_PATTERN = Pattern.compile("<[^>]*>");
    private static CacheLoader<String, Component> cacheLoader;
    private static LoadingCache<String, Component> cache;
    private static BukkitAudiences adventure;

    public static BukkitAudiences adventure() {
        if (adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }

    public static void disable() {
        if (adventure != null) {
            adventure.close();
            adventure = null;
        }
    }

    public static Component formatNoCache(String text) {
        if (text == null) {
            return Component.empty();
        }
        if (MINI_MESSAGE_PATTERN.matcher(text).find()) {
            return MiniMessage.miniMessage().deserialize(text);
        } else {
            return LegacyComponentSerializer.legacySection().deserialize(CC.translate(text));
        }
    }

    public static Component format(String text) {
        if (cache != null) return cache.getUnchecked(text);
        return formatNoCache(text);
    }

    public static void sendMessage(CommandSender sender, Component component) {
        adventure().sender(sender).sendMessage(component);
    }

    public static void init() {
        adventure = BukkitAudiences.create(OctoCore.getInstance());
        if (useCache) {
            cacheLoader = new CacheLoader<String, Component>() {
                @Override
                public @NotNull Component load(@NotNull String s) {
                    return formatNoCache(s);
                }
            };
            cache = CacheBuilder.newBuilder()
                    .expireAfterAccess(Duration.ofMinutes(30)) // save ram? idk
                    .softValues()
                    .maximumSize(200) // cache 200 messages, need to tweak this
                    .build(cacheLoader);
        }
    }
}
