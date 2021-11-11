package net.octopvp.octocore.paper;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import net.octopvp.octocore.common.HardwareUtils;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.PluginMsgChannels;
import net.octopvp.octocore.common.database.ConnectionPoolManager;
import net.octopvp.octocore.common.object.ServerType;
import net.octopvp.octocore.common.object.Settings;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.Logger;
import net.octopvp.octocore.paper.command.CommandFramework;
import net.octopvp.octocore.paper.database.DatabaseHelper;
import net.octopvp.octocore.paper.database.redis.RedisData;
import net.octopvp.octocore.paper.manager.impl.*;
import net.octopvp.octocore.paper.setup.*;
import net.octopvp.octocore.paper.utils.PacketUtil;
import net.octopvp.octocore.paper.utils.errorhandling.ErrorData;
import net.octopvp.octocore.paper.utils.errorhandling.ErrorHandling;
import net.octopvp.octocore.paper.utils.nametag.NameTagChanger;
import net.octopvp.octocore.paper.utils.runnable.Countdown;
import net.octopvp.octocore.paper.utils.runnable.Tasks;
import net.octopvp.octocore.paper.utils.tab.Tab;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;

public final class OctoCore extends JavaPlugin {
    public static String prefix = "[OctoCore] ";
    private static Chat chat;
    private static Permission perms = null;
    private static ConnectionPoolManager connectionPoolManager;
    private static Connection connection;
    private static OctoCore instance;
    private static CommandFramework commandFramework;
    private static Location spawn;

    @Getter
    private static Settings settings = new Settings();

    @Getter
    private ConversationFactory conversationFactory = new ConversationFactory(this);

    @Getter
    private static String serverName;
    @Getter
    @Setter
    private RedisData redisData;
    @Getter
    private static boolean master;
    @Getter
    @Setter
    private static ServerManager serverManager;
    @Getter
    @Setter
    private static ServerType serverType;
    private static SetupModules setupModules = new SetupModules();
    @Getter
    private static Gson gson = new Gson();    // https://stackoverflow.com/a/44800004/11588583
    private static Tab tab;
    //Setup Start
    @Getter
    SetupManager setupManager = new SetupManager();
    public static Chat getChat() {
        return OctoCore.chat;
    }
    public static Connection getConnection() {
        return OctoCore.connection;
    }
    public static OctoCore getInstance() {
        return OctoCore.instance;
    }
    public static CommandFramework getCommandFramework() {
        return OctoCore.commandFramework;
    }
    public static Tab getTab() {
        return OctoCore.tab;
    }
    //Setup End

    //they init from up to down
    @Getter
    private DatabaseManager databaseManager;
    @Getter
    private AuthManager authManager;
    @Getter
    private FilterManager filterManager;
    @Getter
    private NickManager nickManager;
    @Getter
    private RankManager rankManager;
    @Getter
    private PlayerManager playerManager;
    @Getter
    private SettingsManager settingsManager;
    @Getter
    private TabManager tabManager;
    @Getter
    private VaultManager vaultManager;
    @Getter
    private PluginMsgManager pluginMsgManager;
    @Getter
    private PlaceholderManager placeholderManager;
    @Getter
    private JDAManager jdaManager;
    @Getter
    private RedisManager redisManager;
    @Getter
    private ScoreBoardManager scoreBoardManager;
    @Getter
    private TagManager tagManager;

    @Override
    public void onLoad() {
        super.onLoad();
        PacketUtil.setProtocolManager(ProtocolLibrary.getProtocolManager());
    }
    @Override
    public void onEnable() {
        new Logger(Bukkit.getLogger(),prefix);
        if(instance != null)
            throw new IllegalStateException("OctoCore is already initialized");
        instance = this;
        Tasks.init(this);
        commandFramework = new CommandFramework(this);
        tab = new Tab(this);
        serverName = getInstance().getConfig().getString("name");
        OctoCoreCommon.setServerName(OctoCore.getServerName());
        try{
            serverType = ServerType.valueOf(getConfig().getString("server-type").toUpperCase());
            master = (serverType == ServerType.MASTER);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            ErrorData errorData = new ErrorData();
            errorData.addDescription("On Startup");
            errorData.addData("ServerType",getConfig().getString("server-type").toUpperCase());
            errorData.addData("Master",master + "");
            errorData.addException(e);
            ErrorHandling.handleError(errorData);
        }
        spawn = new Location(
                Bukkit.getServer().getWorld(getConfig().getString("settings.world-name"))
                ,getConfig().getDouble("settings.spawn.x"),
                getConfig().getDouble("settings.spawn.y"),
                getConfig().getDouble("settings.spawn.z"));
        HardwareUtils.init();
        Logger.info("Starting OctoCore");
        if(!getDataFolder().exists())
            getDataFolder().mkdirs();
        new SetupConfig().setup(this);
        Logger.info("Starting ConnectionPoolManager");
        initdb();
        if(connection == null)
            Logger.error("Could not connect to database!");
        Logger.info("Setting up internal files");
        Logger.info("Setting up listeners");
        new SetupListeners().setup(this);
        Logger.info("Setting up managers");
        setupManager.setup(this);
        NameTagChanger.INSTANCE.init();
        Logger.info("Hooking into plugins.");
        new SetupHooks().setup(this);
        setupVault();
        Logger.info("Setting up commands.");
        new SetupCommands().setup(this);
        Logger.info("Setting up permissions.");
        new SetupPermissions().setup(this);
        Logger.info("Setting up modules.");
        setupModules.setup(this);
        new SetupOther().setup(this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, PluginMsgChannels.SubChannels.PERMISSIONS);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, PluginMsgChannels.PLUGIN_MSG);
        Logger.info("Done!");
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(CC.RED + "This server is restarting."));
        Bukkit.getScheduler().cancelTasks(this);
        setupManager.disable(getInstance());
        if(NameTagChanger.INSTANCE.isEnabled())
            NameTagChanger.INSTANCE.disable();
        setupModules.disable(this);
        try {
            connection.close();
            connectionPoolManager.closePool();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private void initdb(){
        try {
            String url = getConfig().getString("database.sql.url"),
                    port = getConfig().getString("database.sql.port"),
                    db = getConfig().getString("database.sql.db"),
                    username = getConfig().getString("database.sql.username"),
                    password = getConfig().getString("database.sql.password");
            Logger.info("Logging into SQL:\nURL: " + url + "\nPort: " + port + "\nDB: " + db + "\nUsername: " + username + "\nPasssword: " + password);
            connectionPoolManager = new ConnectionPoolManager(url,port,db, username, password,
                    10,
                    10,
                    500l,
                    "",
                    Bukkit.getLogger()
            );
            connection = connectionPoolManager.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.prepareStatement(DatabaseHelper.CREATE_BLACKLIST_WORD_TABLE.getSql()).executeUpdate();
            connection.prepareStatement(DatabaseHelper.CREATE_DISABLED_COMMANDS_TABLE.getSql()).executeUpdate();
            connection.prepareStatement(DatabaseHelper.CREATE_SETTINGS_TABLE.getSql()).executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public static boolean isVaultEnabled(){
        return Bukkit.getPluginManager().isPluginEnabled("Vault");
    }
    public static boolean vault(){
        return isVaultEnabled();
    }
    public static ConversationFactory getConversationFactory() {
        return instance.conversationFactory;
    }

    public void setupVault(){
        Logger.info(setupChat() ? CC.GREEN + "Successfully set up vault chat." : CC.RED + "Could not set up vault chat.");
        Logger.info(setupPermissions() ? CC.GREEN + "Successfully set up vault permissions." : CC.RED + "Could not set up vault permissions.");
    }
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp == null)
            return false;
        chat = rsp.getProvider();
        return chat != null;
    }
}
