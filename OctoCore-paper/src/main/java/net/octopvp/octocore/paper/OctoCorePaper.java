package net.octopvp.octocore.paper;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import net.octopvp.octocore.common.HardwareUtils;
import net.octopvp.octocore.common.database.ConnectionPoolManager;
import net.octopvp.octocore.common.rank.LuckpermsManager;
import net.octopvp.octocore.paper.command.CommandFramework;
import net.octopvp.octocore.paper.setup.SetupCommands;
import net.octopvp.octocore.paper.setup.SetupConfig;
import net.octopvp.octocore.paper.setup.SetupListeners;
import net.octopvp.octocore.paper.setup.SetupManager;
import net.octopvp.octocore.paper.utils.Logger;
import net.octopvp.octocore.paper.utils.database.DatabaseHelper;
import net.octopvp.octocore.paper.utils.nametag.NameTagChanger;
import net.octopvp.octocore.paper.utils.tab.Tab;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class OctoCorePaper extends JavaPlugin {
    public static String prefix = "[OctoCore] ";
    @Getter
    private static Chat chat;
    @Getter
    private static ConnectionPoolManager connectionPoolManager;
    @Getter
    private static Connection connection;
    @Getter
    private static OctoCorePaper instance;
    @Getter
    private static CommandFramework commandFramework;
    @Getter
    private static ProtocolManager protocolManager;
    @Getter
    private static Tab tab;
    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        instance = this;
        commandFramework = new CommandFramework(this);
        tab = new Tab(this);
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
        new SetupManager().setup(this);
        NameTagChanger.INSTANCE.init();
        Logger.info("Hooking into plugins.");
        LuckpermsManager.init();
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        setupChat();
        Logger.info("Setting up commands.");
        new SetupCommands().setup(this);
        Logger.info("Done!");
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.kickPlayer(ChatColor.RED + "This server is restarting.");
        });
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
            connectionPoolManager = new ConnectionPoolManager(getConfig().getString("database.sql.url"),
                    getConfig().getString("database.sql.port"),
                    getConfig().getString("database.sql.db"),
                    getConfig().getString("database.sql.username"),
                    getConfig().getString("database.sql.password"),
                    10,
                    10,
                    500l,
                    "",
                    Bukkit.getLogger()
            );
            connection = connectionPoolManager.getConnection();
            PreparedStatement ps1 = connection.prepareStatement(DatabaseHelper.CREATE_STAFFDATA_TABLE.getSql());
            ps1.executeUpdate();
            PreparedStatement ps2 = connection.prepareStatement(DatabaseHelper.CREATE_NICK_TABLE.getSql());
            ps2.executeUpdate();
            PreparedStatement ps3 = connection.prepareStatement(DatabaseHelper.CREATE_PLAYERDATA_TABLE.getSql());
            ps3.executeUpdate();
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
