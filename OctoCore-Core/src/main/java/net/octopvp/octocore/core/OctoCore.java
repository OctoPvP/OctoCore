package net.octopvp.octocore.core;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import net.octopvp.commander.Commander;
import net.octopvp.commander.bukkit.BukkitCommander;
import net.octopvp.commander.exception.CommandException;
import net.octopvp.commander.exception.InvalidArgsException;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.PluginMsgChannels;
import net.octopvp.octocore.common.SentryManager;
import net.octopvp.octocore.common.object.*;
import net.octopvp.octocore.common.object.permissions.Rank;
import net.octopvp.octocore.common.object.punish.PunishmentType;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.common.util.Utilities;
import net.octopvp.octocore.core.command.CommandResult;
import net.octopvp.octocore.core.command.providers.*;
import net.octopvp.octocore.core.database.DatabaseManager;
import net.octopvp.octocore.core.database.redis.packets.server.ServerOnlinePacket;
import net.octopvp.octocore.core.manager.impl.*;
import net.octopvp.octocore.core.objects.BukkitServerImpl;
import net.octopvp.octocore.core.objects.OfflinePunishData;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.setup.*;
import net.octopvp.octocore.core.utils.AdventureUtils;
import net.octopvp.octocore.core.utils.OfflineHelpers;
import net.octopvp.octocore.core.utils.PacketUtil;
import net.octopvp.octocore.core.utils.errorhandling.ErrorData;
import net.octopvp.octocore.core.utils.errorhandling.ErrorHandling;
import net.octopvp.octocore.core.utils.runnable.Tasks;
import net.octopvp.octocore.core.utils.runnable.runnables.DataUpdateThread;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.UUID;

@Getter
@SuppressWarnings("unused")
public abstract class OctoCore extends JavaPlugin {
    @Getter
    private static final Settings settings = new Settings();
    private static final SetupModules setupModules = new SetupModules();
    @Getter
    private static final Gson gson = OctoCoreCommon.getGsonBuilder().create();    // https://stackoverflow.com/a/44800004/11588583
    public static String prefix = "[OctoCore] ";
    private static Chat chat;
    private static OctoCore instance;
    @Getter
    private static Location spawn;
    @Getter
    private static String serverName;
    @Getter
    private static boolean loading;
    @Getter
    @Setter
    private static ServerType serverType;
    private final ConversationFactory conversationFactory = new ConversationFactory(this);
    //Setup Start
    @Getter
    SetupManager setupManager = new SetupManager();
    private DataUpdateThread dataUpdateThread;

    @Setter
    private net.octopvp.octocore.common.redis.RedisManager actualRedisManager;
    //they init from up to down
    private PlayerManager playerManager;
    private DatabaseManager databaseManager;
    private ServerManager serverManager;
    private AuthManager authManager;
    private FilterManager filterManager;
    private RankManager rankManager;
    private SettingsManager settingsManager;
    private TabManager tabManager;
    private VaultManager vaultManager;
    private PluginMsgManager pluginMsgManager;
    private PlaceholderManager placeholderManager;
    private RedisManager redisManager; // TODO managers not registered
    private TagManager tagManager;
    private VanishManager vanishManager;
    //Setup End

    private Commander commander;

    public static Chat getChat() {
        return OctoCore.chat;
    }

    public static OctoCore getInstance() {
        return OctoCore.instance;
    }

    public static boolean isVaultEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("Vault");
    }

    public static boolean vault() {
        return isVaultEnabled();
    }

    public static ConversationFactory getConversationFactory() {
        return instance.conversationFactory;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (instance != null)
            throw new IllegalStateException("OctoCore is already initialized");
        instance = this;
        /*
        if (Bukkit.getOnlineMode()) {
            System.err.println("fucking idiot, you have online mode on, OctoCore can't work alone");
            System.exit(-69);
        } else {
            if (!Bukkit.spigot().getSpigotConfig().getBoolean("settings.bungeecord")) {
                System.err.println("fucking idiot, you have bungee off and offline mode on are u high");
                System.exit(69);
            }
        }
         */
        PacketUtil.setProtocolManager(ProtocolLibrary.getProtocolManager());
        getServerImplementation().onLoad();
    }

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        if (!getDataFolder().exists())
            //noinspection ResultOfMethodCallIgnored
            getDataFolder().mkdirs();
        saveDefaultConfig();
        serverName = getInstance().getConfig().getString("name");
        OctoCoreCommon.getInstance().init(gson, new BukkitServerImpl());

        new Logger(Bukkit.getLogger(), prefix, (message, players) -> {
            for (UUID uuid : players) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.sendMessage(message);
                }
            }
        });
        Logger.info("Starting OctoCore");
        loading = true;
        Tasks.init(this);
        AdventureUtils.init();
        commander = BukkitCommander.getCommander(this);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, PluginMsgChannels.SubChannels.PERMISSIONS);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, PluginMsgChannels.PLUGIN_MSG);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, PluginMsgChannels.BUNGEE);
        //Bukkit.getMessenger().registerOutgoingPluginChannel(this, "test");

        try {
            serverType = ServerType.valueOf(getConfig().getString("server-type").toUpperCase());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            ErrorData errorData = new ErrorData();
            errorData.addDescription("On Startup");
            errorData.addData("ServerType", getConfig().getString("server-type").toUpperCase());
            errorData.addException(e);
            ErrorHandling.handleError(errorData);
        }
        if (getConfig().getBoolean("sentry.enable", false))
            SentryManager.init(getConfig().getString("sentry.sentry-dsn", ""));
        spawn = new Location(
                Bukkit.getServer().getWorld(getConfig().getString("settings.world-name")),
                getConfig().getDouble("settings.spawn.x"),
                getConfig().getDouble("settings.spawn.y"),
                getConfig().getDouble("settings.spawn.z"));
        Utilities.init();
        getServerImplementation().onEnable();

        this.dataUpdateThread = new DataUpdateThread(this);

        Logger.info("Setting up internal files");
        Logger.info("Setting up listeners");
        new SetupListeners().setup(this);
        Logger.info("Setting up managers");
        setupManager.setup(this);
        Logger.info("Hooking into plugins.");
        new SetupHooks().setup(this);
        setupVault();
        Logger.info("Setting up commands.");
        commander
                .registerPackage(CommandResult.class.getPackage().getName())
                .registerDependency(OctoCore.class, this)
                .registerDependency(PlayerManager.class, playerManager)
                .registerDependency(ServerManager.class, serverManager)

                .registerProvider(PlayerData.class, new PlayerDataProvider())
                .registerProvider(SimplePlayerData.class, new PlayerDataProvider())
                .registerProvider(GameMode.class, new GameModeProvider())
                .registerProvider(Rank.class, new RankProvider())
                .registerProvider(OfflinePunishData.class, new OfflinePunishDataProvider())
                .registerProvider(OnlinePlayer.class, new OnlinePlayerProvider())
                .registerProvider(Enchantment.class, new EnchantmentProvider())
                .registerProvider(OfflineHelpers.OfflineInfo.class, new OfflineInfoProvider())
                .registerProvider(PunishmentType.class, new PunishTypeProvider())
                .registerCommandPostProcessor((ctx, obj) -> {
                    if (obj instanceof CommandResult) {
                        CommandResult result = (CommandResult) obj;
                        //noinspection StatementWithEmptyBody
                        if (result == CommandResult.SUCCESS) {
                        } else if (result == CommandResult.INVALID_ARGS) {
                            throw new InvalidArgsException(ctx.getCommandInfo());
                        } else //noinspection StatementWithEmptyBody
                            if (Objects.equals(result.getMsg(), "") || Objects.equals(result.getMsg(), " ")) {
                            } else //noinspection StatementWithEmptyBody
                                if (result.getMsg() == null) {
                                } else {
                                    ctx.getCommandSender().sendMessage(result.getMsg());
                                }
                    }
                })
                .registerCommandPreProcessor(ctx -> {
                    if (ctx.getCommandInfo().getInstance().getClass().isAnnotationPresent(Disable.class) || ctx.getCommandInfo().getMethod().isAnnotationPresent(Disable.class)) {
                        throw new CommandException("This command is disabled.");
                    }
                })
        ;
        Logger.info("Setting up permissions.");
        new SetupPermissions().setup(this);
        Logger.info("Setting up modules.");
        setupModules.setup(this);
        new SetupOther().setup(this);
        Logger.info("Done!");
        dataUpdateThread.start();
        Tasks.runLater(() -> {
            loading = false;
            new ServerOnlinePacket(serverName).send();
        }, 20L);
        Logger.info("OctoCore took " + (System.currentTimeMillis() - start) + "ms to load.");
    }

    @Override
    public void onDisable() {
        getServerImplementation().onDisable();
        String dcReason = new DisconnectReason("This server is restarting!").toString();
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(dcReason));
        Bukkit.getScheduler().cancelTasks(this);
        setupManager.disable(this);
        setupModules.disable(this);
        AdventureUtils.disable();
    }

    public void setupVault() {
        Logger.info(setupChat() ? CC.GREEN + "Successfully set up vault chat." : CC.RED + "Could not set up vault chat.");
        Logger.info(setupPermissions() ? CC.GREEN + "Successfully set up vault permissions." : CC.RED + "Could not set up vault permissions.");
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        Permission perms = rsp.getProvider();
        return perms != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null)
            return false;
        chat = rsp.getProvider();
        return chat != null;
    }

    public ClassLoader getClassLoader0() {
        return getClassLoader();
    }

    public abstract BukkitServerImplementation getServerImplementation();
}
