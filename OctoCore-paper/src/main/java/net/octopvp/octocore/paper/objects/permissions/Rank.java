package net.octopvp.octocore.paper.objects.permissions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.paper.manager.impl.RankManager;
import net.octopvp.octocore.paper.objects.PlayerData;
import net.octopvp.octocore.paper.objects.builders.RankBuilder;
import net.octopvp.octocore.paper.objects.enums.RankType;
import org.apache.commons.collections4.map.UnmodifiableMap;
import org.bukkit.ChatColor;
import org.javatuples.Pair;

import java.util.*;

@Getter
@Setter
public class Rank implements Cloneable {
    private String name;
    private UUID rankId = UUID.randomUUID();
    private int weight = 0;
    private boolean defaultRank = false;
    private RankType rankType = RankType.DEFAULT;

    //private Map<String, ServerContext> permissions = new ConcurrentHashMap<>();
    //private Map<String, ServerContext> negatedPermissions = new ConcurrentHashMap<>();
    private Set<Node> nodes = new HashSet<>();
    private Set<UUID> inheritedRanks = new HashSet<>();

    private String prefix = ""/*, color = CC.GRAY, chatColor = CC.GRAY*/;
    private ChatColor color = ChatColor.GRAY,chatColor = ChatColor.GRAY;
    private boolean bold = false, italic = false, purchasable = false,changableMainColor = false;
    private ServerContext scope = ServerContext.global();

    public void save() {
        RankManager.save(this);
    }

    @Override
    public Rank clone() throws CloneNotSupportedException {
        return (Rank) super.clone();
    }

    public String[] getInheritedRanksName() {
        List<String> a = new ArrayList<>();
        inheritedRanks.forEach(inh -> {
            if (RankManager.getRankById(inh) == null)
                return;
            a.add(RankManager.getRankById(inh).getName());
        });
        return a.toArray(new String[0]);
    }
    public Node getNode(String perm){
        return nodes.stream().filter(node -> node.getPermission().equalsIgnoreCase(perm)).findFirst().orElse(null);
    }
    public boolean nodeExists(String perm){
        return getNode(perm) != null;
    }

    public boolean hasPermission(String permission) {
        if (permissionNegated(permission))
            return false;
        return hasSetPermission(permission) || inheritsPermission(permission);
    }
    public boolean permissionNegated(String permission){
        if (nodeExists(permission)){
            return getNode(permission).getScope().isThisServer() && getNode(permission).isNegated();
        }
        return false;
    }

    public boolean hasPermission(String permission,String server) {
        if (permissionNegated(permission,server))
            return false;
        return hasSetPermission(permission,server) || inheritsPermission(permission,server);
    }
    public boolean permissionNegated(String permission,String server){
        if (nodeExists(permission)){
            return getNode(permission).getScope().getServer().equalsIgnoreCase(server) && getNode(permission).isNegated();
        }
        return false;
    }
    public boolean hasSetPermission(String permission,String server){
        if (permissionNegated(permission))
            return false;
        if (nodeExists(permission)) {
            if (getNode(permission).getScope().getServer().equalsIgnoreCase(server))
                return true;
        }
        return false;
    }public boolean hasSetPermission(String permission){
        if (permissionNegated(permission))
            return false;
        if (nodeExists(permission)) {
            if (getNode(permission).getScope().isThisServer())
                return true;
        }
        return false;
    }

    public boolean inheritsPermission(String permission) {
        boolean inherited = false;
        for (UUID inheritedRank : inheritedRanks) {
            Rank r = RankManager.getRankById(inheritedRank);
            if (r.hasPermission(permission)) {
                inherited = true;
                break;
            }
        }
        return inherited;
    }
    public boolean inheritsPermission(String permission,String server) {
        boolean inherited = false;
        for (UUID inheritedRank : inheritedRanks) {
            Rank r = RankManager.getRankById(inheritedRank);
            if (r.hasPermission(permission,server)) {
                inherited = true;
                break;
            }
        }
        return inherited;
    }

    public String getDisplayName() {
        if (this.isItalic() && this.isBold()) {
            return this.getColor() + "" + ChatColor.BOLD + ChatColor.ITALIC + this.getName();
        }
        if (this.isBold()) {
            return this.getColor() + "" + ChatColor.BOLD + this.getName();
        }
        if (this.isItalic()) {
            return this.getColor() + "" + ChatColor.ITALIC + this.getName();
        }
        return this.getColor() + this.getName();
    }

    public String getDisplayColor() {
        if (this.isItalic() && this.isBold()) {
            return this.getColor() + "" + ChatColor.BOLD + ChatColor.ITALIC;
        }
        if (this.isBold()) {
            return this.getColor() + "" + ChatColor.BOLD;
        }
        if (this.isItalic()) {
            return this.getColor() + "" + ChatColor.ITALIC;
        }
        return this.getColor().toString();
    }

    public String getPrefix() {
        return CC.translate(StringUtils.replacePlaceholders(prefix,color.toString()));
    }
    public String getPrefix(String color1) {
        if (changableMainColor && color1 != null)
            return CC.translate(StringUtils.replacePlaceholders(prefix,color1));
        return CC.translate(StringUtils.replacePlaceholders(prefix,this.color.toString()));
    }
    public String getActivePrefix(PlayerData data){
        return data.getPrefix();
    }
    public List<String> getEffectivePermissions(){
        List<String> a = new ArrayList<>();
        nodes.forEach(node ->{
            if (node.getScope().isThisServer() && hasPermission(node.getPermission()))
                a.add(node.getPermission());
        });
        return ImmutableList.copyOf(a);
    }
    public Map<String,ServerContext> getAllEffectivePermissions(){
        Map<String,ServerContext> a = new HashMap<>();
        nodes.forEach(node ->{
            if (!permissionNegated(node.getPermission(),"global"))
                a.put(node.getPermission(),node.getScope());
        });
        return ImmutableMap.copyOf(a);
    }
    public Map<String, Boolean> getEffectiveBungeePermissions(){
        Map<String,Boolean> a = new HashMap<>();
        nodes.forEach(node ->{
            if (node.getScope().isBungee() || node.getScope().isGlobal())
                a.put(node.getPermission(),node.getScope().isBungee());
        });
        return ImmutableMap.copyOf(a);
    }
    public Map<String, Pair<ServerContext,Boolean>> getAllPermissionsPair(){
        Map<String,Pair<ServerContext,Boolean>> map = new HashMap<>();
        nodes.forEach(node ->{
            if (hasPermission(node.getPermission()))
                map.put(node.getPermission(),new Pair<>(node.getScope(),true));
            else map.put(node.getPermission(),new Pair<>(node.getScope(),false));
        });
        return ImmutableMap.copyOf(map);
    }
    public Map<String,ServerContext> getNegatedPermissions(){
        Map<String,ServerContext> map = new HashMap<>();
        nodes.forEach(node ->{
            if (node.isNegated())
                map.put(node.getPermission(),node.getScope());
        });
        return ImmutableMap.copyOf(map);
    }
    public Map<String,ServerContext> getAllowedPermissions(){
        Map<String,ServerContext> map = new HashMap<>();
        nodes.forEach(node ->{
            if (node.isAllowed())
                map.put(node.getPermission(),node.getServer());
        });
        return ImmutableMap.copyOf(map);
    }
    public RankBuilder toBuilder(){
        return new RankBuilder(this);
    }
}
