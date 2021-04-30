package net.octopvp.octocore.paper.setup;

import com.lunarclient.bukkitapi.cooldown.LCCooldown;
import com.lunarclient.bukkitapi.cooldown.LunarClientAPICooldown;
import lombok.Getter;
import net.octopvp.octocore.paper.OctoCore;
import net.octopvp.octocore.paper.manager.*;
import net.octopvp.octocore.paper.manager.impl.*;
import org.bukkit.Material;

import java.util.ArrayList;

@Getter
public class SetupManager implements Setup {
    private JDAManager jdaManager;
    private AuthManager authManager;
    private FilterManager filterManager;
    private NickManager nickManager;
    private PlayerManager playerManager;
    private TabManager tabManager;
    private VaultManager vaultManager;
    private PluginMsgManager pluginMsgManager;
    private ServerManager serverManager;
    private DatabaseManager databaseManager;
    private LuckpermsManager luckpermsManager;
    private ArrayList<Manager> managers = new ArrayList<>();

    public static SetupManager instance;

    @Override
    public void setup(OctoCore plugin) {
        instance = this;
        //TODO: use reflection to do this
        /*
        ArrayList<Field> managers = new ArrayList<>();
        for(Field field : this.getClass().getDeclaredFields()){
            if(field.getName().toLowerCase().contains("manager") && (!field.getName().toLowerCase().contains("setupmanager"))){
                field.setAccessible(true);
                managers.add(field);
            }
        }
        managers.forEach(manager ->{
            try {
                manager.getDeclaringClass().getMethod("init").invoke(OctoCore.getInstance());
                Constructor constructor = manager.getType().getDeclaredConstructor(SetupManager.instance.getClass());
                manager.set(OctoCore.getInstance(),constructor.newInstance(OctoCore.getInstance()));
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        });
         */
        jdaManager = new JDAManager();
        managers.add(jdaManager);
        authManager = new AuthManager();
        managers.add(authManager);
        filterManager = new FilterManager();
        managers.add(filterManager);
        nickManager = new NickManager();
        managers.add(nickManager);
        luckpermsManager = new LuckpermsManager();
        managers.add(luckpermsManager);
        //luckpermsmanager MUST be above
        playerManager = new PlayerManager();
        managers.add(playerManager);

        tabManager = new TabManager();
        managers.add(tabManager);
        vaultManager = new VaultManager();
        managers.add(vaultManager);
        pluginMsgManager = new PluginMsgManager();
        managers.add(pluginMsgManager);
        serverManager = new ServerManager();
        managers.add(serverManager);
        databaseManager = new DatabaseManager();
        managers.add(databaseManager);

        managers.forEach(m ->{
            m.init(plugin);
        });
        LunarClientAPICooldown.registerCooldown(new LCCooldown("Enderpearl",plugin.getConfig().getInt("cooldown.pearl.time"), Material.ENDER_PEARL));
    }

    @Override
    public void disable(OctoCore plugin) {
        managers.forEach(manager -> manager.disable(plugin));
    }
}
