package net.octopvp.octocore.paper;

import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.chat.Chat;
import net.octopvp.octocore.common.HardwareUtils;
import net.octopvp.octocore.common.database.ConnectionPoolManager;
import net.octopvp.octocore.paper.command.CommandFramework;
import net.octopvp.octocore.paper.manager.ServerManager;
import net.octopvp.octocore.paper.setup.*;
import net.octopvp.octocore.paper.utils.Logger;
import net.octopvp.octocore.paper.utils.nametag.NameTagChanger;
import net.octopvp.octocore.paper.utils.tab.Tab;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;

public final class OctoCorePaper extends JavaPlugin {
    public static String prefix = "[OctoCore] ";
    private static Chat chat;
    private static ConnectionPoolManager connectionPoolManager;
    private static Connection connection;
    private static OctoCorePaper instance;
    private static CommandFramework commandFramework;
    private static Location spawn;
    @Getter
    private static final boolean master = OctoCorePaper.getInstance().getConfig().getBoolean("master");
    @Getter
    @Setter
    private static ServerManager serverManager;

    private static Tab tab;

    //Setup Start
    SetupManager setupManager = new SetupManager();

    public static Chat getChat() {
        return OctoCorePaper.chat;
    }

    public static ConnectionPoolManager getConnectionPoolManager() {
        return OctoCorePaper.connectionPoolManager;
    }

    public static Connection getConnection() {
        return OctoCorePaper.connection;
    }

    public static OctoCorePaper getInstance() {
        return OctoCorePaper.instance;
    }

    public static CommandFramework getCommandFramework() {
        return OctoCorePaper.commandFramework;
    }

    public static Location getSpawn() {
        return OctoCorePaper.spawn;
    }

    public static Tab getTab() {
        return OctoCorePaper.tab;
    }

    public static void setSpawn(Location spawn) {
        OctoCorePaper.spawn = spawn;
    }

    //Setup End



    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        if(instance != null)
            throw new IllegalStateException("OctoCore is already initialized??? Try restarting the server if there is any problems");
        instance = this;
        commandFramework = new CommandFramework(this);
        tab = new Tab(this);
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
    }
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }


}
