package net.octopvp.octocore.paper;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.chat.Chat;
import net.octopvp.octocore.common.HardwareUtils;
import net.octopvp.octocore.common.database.ConnectionPoolManager;
import net.octopvp.octocore.common.object.ServerType;
import net.octopvp.octocore.paper.utils.errorhandling.ErrorData;
import net.octopvp.octocore.paper.utils.errorhandling.ErrorHandling;
import net.octopvp.octocore.paper.command.CommandFramework;
import net.octopvp.octocore.paper.database.DatabaseHelper;
import net.octopvp.octocore.paper.database.redis.RedisData;
import net.octopvp.octocore.paper.manager.impl.ServerManager;
import net.octopvp.octocore.paper.objects.BlackListedCommand;
import net.octopvp.octocore.paper.setup.*;
import net.octopvp.octocore.paper.utils.Logger;
import net.octopvp.octocore.paper.utils.nametag.NameTagChanger;
import net.octopvp.octocore.paper.utils.tab.Tab;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class OctoCore extends JavaPlugin {
    public static String prefix = "[OctoCore] ";
    private static Chat chat;
    private static ConnectionPoolManager connectionPoolManager;
    private static Connection connection;
    private static OctoCore instance;
    private static CommandFramework commandFramework;
    private static Location spawn;
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
    @Getter
    // https://stackoverflow.com/a/44800004/11588583
    private static Gson gson = new Gson();

    private static Tab tab;

    //Setup Start
    @Getter
    SetupManager setupManager = new SetupManager();

    public static Chat getChat() {
        return OctoCore.chat;
    }

    public static ConnectionPoolManager getConnectionPoolManager() {
        return OctoCore.connectionPoolManager;
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

    public static Location getSpawn() {
        return OctoCore.spawn;
    }

    public static Tab getTab() {
        return OctoCore.tab;
    }

    public static void setSpawn(Location spawn) {
        OctoCore.spawn = spawn;
    }

    //Setup End


    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        if(instance != null)
            throw new IllegalStateException("OctoCore is already initialized");
        instance = this;
        commandFramework = new CommandFramework(this);
        tab = new Tab(this);
        serverName = getInstance().getConfig().getString("name");
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
        setupChat();
        Logger.info("Setting up commands.");
        new SetupCommands().setup(this);
        Logger.info("Setting up permissions.");
        new SetupPermissions().setup(this);
        Logger.info("Done!");
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(ChatColor.RED + "This server is restarting."));
        setupManager.disable(getInstance());
        if(NameTagChanger.INSTANCE.isEnabled())
            NameTagChanger.INSTANCE.disable();
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }
    public static List<String> disabledCommands(){
        //TODO list all disable commands and somehow reload them at interval
        List<String> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = OctoCore.getConnection().prepareStatement(DatabaseHelper.GET_DISABLED_COMMANDS.getSql()).executeQuery();
            while(rs.next()){
                BlackListedCommand cmd = new BlackListedCommand(rs.getString("cmd"));
                cmd.setDisabledOn(rs.getLong("DisabledOn"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Logger.warn("Unable to load blacklisted words from database!");
        }
        return list;
    }

}
