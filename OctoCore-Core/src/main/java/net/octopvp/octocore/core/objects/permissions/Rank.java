package net.octopvp.octocore.core.objects.permissions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.common.object.maps.pair.HashPairMap;
import net.octopvp.octocore.common.object.maps.pair.PairMap;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.permissions.Node;
import net.octopvp.octocore.common.util.permissions.PermissionCalculator;
import net.octopvp.octocore.common.util.permissions.PermissionResult;
import net.octopvp.octocore.core.manager.impl.RankManager;
import net.octopvp.octocore.core.objects.PlayerData;
import net.octopvp.octocore.core.objects.builders.RankBuilder;
import net.octopvp.octocore.core.objects.enums.RankType;
import org.bukkit.ChatColor;
import org.javatuples.Pair;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class Rank implements Cloneable {
    private static final Comparator<Rank> inheritanceComparatorLargeToSmall = Comparator.comparingInt(Rank::getWeight).reversed();
    private static final Comparator<Rank> inheritanceComparatorSmallToLarge = Comparator.comparingInt(Rank::getWeight);
    private String name;
    private UUID rankId = UUID.randomUUID();
    private int weight = 1;
    private boolean defaultRank = false;
    private RankType rankType = RankType.DEFAULT;
    //private Map<String, ServerContext> permissions = new ConcurrentHashMap<>();
    //private Map<String, ServerContext> negatedPermissions = new ConcurrentHashMap<>();
    private Set<Node> nodes = new HashSet<>();
    private Set<UUID> inheritedRanks = new HashSet<>();
    private String prefix = ""/*, color = CC.GRAY, chatColor = CC.GRAY*/;
    private ChatColor color = ChatColor.GREEN, chatColor = ChatColor.WHITE;
    private boolean bold = false, italic = false, purchasable = false, changableMainColor = false;
    private ServerContext scope = ServerContext.global();

    public void save() {
        RankManager.getInstance().save(this);
    }

    @Override
    public Rank clone() throws CloneNotSupportedException {
        return (Rank) super.clone();
    }

    public String[] getInheritedRanksName() {
        List<String> a = new ArrayList<>();
        inheritedRanks.forEach(inh -> {
            if (RankManager.getInstance().getRankById(inh) == null)
                return;
            a.add(RankManager.getInstance().getRankById(inh).getName());
        });
        return a.toArray(new String[0]);
    }

    public Node getNode(String perm) {
        return nodes.stream().filter(node -> node.getPermission().equalsIgnoreCase(perm)).findFirst().orElse(null);
    }

    public boolean nodeExists(String perm) {
        return getNode(perm) != null;
    }

    public Set<Node> getFinalNodes() {
        PairMap<String, Node, Boolean> map = new HashPairMap<>();
        Set<Node> nodes = new HashSet<>();
        for (Node node : this.getNodes()) {
            if (node.getScope().isThisServer())
                nodes.add(node);
        }
        for (Rank rank : getOrderedInheritance(OrderedInheritance.SMALL_TO_LARGE)) {
            for (Node finalNode : rank.getFinalNodes()) {
                if (map.containsKey(finalNode.getPermission().toLowerCase())) {
                    if (map.get(finalNode.getPermission().toLowerCase()).getValue0().getWeight() < finalNode.getWeight()) { //priorities
                        map.remove(finalNode.getPermission().toLowerCase());
                        map.put(finalNode.getPermission().toLowerCase(), finalNode, finalNode.isAllowed());
                    }
                    continue;
                }
                map.put(finalNode.getPermission().toLowerCase(), finalNode, finalNode.isAllowed());
            }
        }
        map.forEach((s, node, b) -> nodes.add(node));
        return nodes;
    }

    public List<Rank> getOrderedInheritance(OrderedInheritance orderedInheritance) {
        if (orderedInheritance == OrderedInheritance.SMALL_TO_LARGE) {
            return this.getInheritedRanksConverted().stream().sorted(inheritanceComparatorSmallToLarge).collect(Collectors.toList());
        } else
            return this.getInheritedRanksConverted().stream().sorted(inheritanceComparatorLargeToSmall).collect(Collectors.toList());
    }

    public Set<Rank> getInheritedRanksConverted() {
        Set<Rank> ranks = new HashSet<>();
        for (UUID inheritedRank : inheritedRanks) {
            Rank rank = RankManager.getInstance().getRankById(inheritedRank);
            if (rank == null) continue;
            ranks.add(rank);
        }
        return ranks;
    }

    public boolean hasPermission(String permission) {
        return PermissionCalculator.hasPermissionResult(permission, getFinalNodes()).allowed();
    }

    public boolean hasPermission(String permission, String server) {
        return PermissionCalculator.hasPermissionResult(permission, getFinalNodes(), server).allowed();
    }

    public PermissionResult getPermissionResult(String permission, String server) {
        return PermissionCalculator.hasPermissionResult(permission, getFinalNodes(), server);
    }

    public PermissionResult getPermissionResult(String permission) {
        return PermissionCalculator.hasPermissionResult(permission, getFinalNodes());
    }

    public boolean permissionNegated(String permission, String server) {
        if (nodeExists(permission)) {
            return getNode(permission).getScope().getServer().equalsIgnoreCase(server) && getNode(permission).isNegated();
        }
        return false;
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
            return this.getColor() + "" + ChatColor.BOLD + "" + ChatColor.ITALIC;
        }
        if (this.isBold()) {
            return this.getColor() + "" + ChatColor.BOLD;
        }
        if (this.isItalic()) {
            return this.getColor() + "" + ChatColor.ITALIC;
        }
        return this.getColor() + "";
    }

    public String getPrefix() {
        return CC.translate(StringUtils.replacePlaceholders(prefix, color.toString()));
    }

    public String getDefaultPrefix() {
        return CC.translate(StringUtils.replacePlaceholders(prefix, color.toString()));
    }

    public String getPrefix(String color1) {
        if (changableMainColor && color1 != null)
            return CC.translate(StringUtils.replacePlaceholders(prefix, color1));
        return CC.translate(StringUtils.replacePlaceholders(prefix, this.color.toString()));
    }

    public String getActivePrefix(PlayerData data) {
        return data.getPrefix();
    }

    public Map<String, ServerContext> getAllEffectivePermissions() {
        Map<String, ServerContext> a = new HashMap<>();
        nodes.forEach(node -> {
            if (!permissionNegated(node.getPermission(), "global"))
                a.put(node.getPermission(), node.getScope());
        });
        return ImmutableMap.copyOf(a);
    }

    public Collection<Node> getEffectiveBungeePermissions() {
        Set<Node> a = new HashSet<>();
        getFinalNodes().forEach(node -> {
            if (node.getScope().isBungee() || node.getScope().isGlobal())
                a.add(node);
        });
        return a;
    }

    public Map<String, Pair<ServerContext, Boolean>> getAllPermissionsPair() {
        Map<String, Pair<ServerContext, Boolean>> map = new HashMap<>();
        nodes.forEach(node -> {
            if (hasPermission(node.getPermission()))
                map.put(node.getPermission(), new Pair<>(node.getScope(), true));
            else map.put(node.getPermission(), new Pair<>(node.getScope(), false));
        });
        return ImmutableMap.copyOf(map);
    }

    public Map<String, ServerContext> getNegatedPermissions() {
        Map<String, ServerContext> map = new HashMap<>();
        nodes.forEach(node -> {
            if (node.isNegated())
                map.put(node.getPermission(), node.getScope());
        });
        return ImmutableMap.copyOf(map);
    }

    public Map<String, ServerContext> getAllowedPermissions() {
        Map<String, ServerContext> map = new HashMap<>();
        nodes.forEach(node -> {
            if (node.isAllowed())
                map.put(node.getPermission(), node.getServer());
        });
        return ImmutableMap.copyOf(map);
    }

    public RankBuilder toBuilder() {
        return new RankBuilder(this);
    }

    public enum OrderedInheritance {
        SMALL_TO_LARGE, LARGE_TO_SMALL
    }
}
