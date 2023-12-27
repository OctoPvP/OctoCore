package net.octopvp.octocore.common.object.permissions;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.octopvp.octocore.common.OctoCoreCommon;
import net.octopvp.octocore.common.StringUtils;
import net.octopvp.octocore.common.interfaces.manager.IRankManager;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.common.object.SimplePlayerData;
import net.octopvp.octocore.common.object.builders.RankBuilder;
import net.octopvp.octocore.common.object.enums.RankType;
import net.octopvp.octocore.common.redis.packets.ReloadRanksPacket;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.common.util.ChatColor;
import net.octopvp.octocore.common.util.perms.Node;
import net.octopvp.octocore.common.util.perms.PermissionCheckResult;
import net.octopvp.octocore.common.util.perms.PermissionManager;

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
    private Map<String, Node> nodes = new HashMap<>();
    private Set<UUID> inheritedRanks = new HashSet<>();
    private String prefix = ""/*, color = CC.GRAY, chatColor = CC.GRAY*/;
    private ChatColor color = ChatColor.GREEN, chatColor = ChatColor.WHITE;
    private boolean bold = false, italic = false, purchasable = false, changableMainColor = false;
    private ServerContext scope = ServerContext.global();

    public void save(IRankManager... rankManager) {
        new ReloadRanksPacket().send();
        if (rankManager.length == 0)
            OctoCoreCommon.getInstance().getServerImplementation().getRankManager().save(this);
        else
            rankManager[0].save(this);
    }

    @Override
    public Rank clone() {
        try {
            return (Rank) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] getInheritedRanksName() {
        List<String> a = new ArrayList<>();
        inheritedRanks.forEach(inh -> {
            if (OctoCoreCommon.getInstance().getRankManager().getRankById(inh) == null)
                return;
            a.add(OctoCoreCommon.getInstance().getRankManager().getRankById(inh).getName());
        });
        return a.toArray(new String[0]);
    }

    public Node getNode(String perm) {
        // return nodes.stream().filter(node -> node.getPermission().equalsIgnoreCase(perm)).findFirst().orElse(null);
        return PermissionManager.getInstance().findNode(perm, nodes);
    }

    public boolean nodeExists(String perm) {
        return getNode(perm) != null;
    }

    public Map<String, Node> getFinalNodeTree() {
        Map<String, Node> nodeMap = new HashMap<>(nodes);
        for (Rank rank : getOrderedInheritance(OrderedInheritance.SMALL_TO_LARGE)) {
            PermissionManager.getInstance().mergeNodeTrees(nodeMap, rank.getNodes());
        }
        return nodeMap;
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
            Rank rank = OctoCoreCommon.getInstance().getRankManager().getRankById(inheritedRank);
            if (rank == null) continue;
            ranks.add(rank);
        }
        return ranks;
    }

    public PermissionCheckResult calculatePermission(String permission) {
        return PermissionManager.getInstance().checkPermission(permission, getFinalNodeTree());
    }

    public boolean hasPermission(String permission) {
        return PermissionManager.getInstance().checkPermission(permission, getFinalNodeTree()).allowed();
    }

    public boolean hasPermission(String permission, String server) {
        return PermissionManager.getInstance().checkPermission(permission, getFinalNodeTree(), server).allowed();
    }

    public PermissionCheckResult getPermissionResult(String permission, String server) {
        return PermissionManager.getInstance().checkPermission(permission, getFinalNodeTree(), server);
    }

    public PermissionCheckResult getPermissionResult(String permission) {
        return PermissionManager.getInstance().checkPermission(permission, getFinalNodeTree());
    }
    public String getDisplayName() {
        if (this.isItalic() && this.isBold()) {
            return this.getColor() + String.valueOf(ChatColor.BOLD) + ChatColor.ITALIC + this.getName();
        }
        if (this.isBold()) {
            return this.getColor() + String.valueOf(ChatColor.BOLD) + this.getName();
        }
        if (this.isItalic()) {
            return this.getColor() + String.valueOf(ChatColor.ITALIC) + this.getName();
        }
        return this.getColor() + this.getName();
    }

    public String getDisplayColor() {
        if (this.isItalic() && this.isBold()) {
            return this.getColor() + String.valueOf(ChatColor.BOLD) + ChatColor.ITALIC;
        }
        if (this.isBold()) {
            return this.getColor() + String.valueOf(ChatColor.BOLD);
        }
        if (this.isItalic()) {
            return this.getColor() + String.valueOf(ChatColor.ITALIC);
        }
        return String.valueOf(this.getColor());
    }

    public String getPrefix() {
        return CC.translate(StringUtils.replacePlaceholders(prefix, color.toString()));
    }

    public Component getPrefixComponent() {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(getPrefix());
    }

    public String getDefaultPrefix() {
        return CC.translate(StringUtils.replacePlaceholders(prefix, color.toString()));
    }

    public String getPrefix(String color1) {
        if (changableMainColor && color1 != null)
            return CC.translate(StringUtils.replacePlaceholders(prefix, color1));
        return CC.translate(StringUtils.replacePlaceholders(prefix, this.color.toString()));
    }

    public String getActivePrefix(SimplePlayerData data) {
        return data.getPrefix();
    }

    public Map<String, ServerContext> getAllEffectivePermissions() {
        Map<String, ServerContext> a = new HashMap<>();
        nodes.forEach((key, node) -> {
            if (!node.isNegated("*") && node.getServerContext().isPresent())
                a.put(node.getPermissionString(), node.getServerContext().get());
        });
        return ImmutableMap.copyOf(a);
    }

    public Map<String, Node> getNegatedPermissions() {
        Map<String, Node> map = new HashMap<>();
        nodes.forEach((key, node) -> {
            if (node.getNegated().isPresent() && node.getNegated().get())
                map.put(key, node);
        });
        return ImmutableMap.copyOf(map);
    }

    public Map<String, Node> getAllowedPermissions() {
        Map<String, Node> map = new HashMap<>();
        nodes.forEach((key, node) -> {
            if (node.isAllowed())
                map.put(key, node);
        });
        return ImmutableMap.copyOf(map);
    }

    public RankBuilder toBuilder() {
        return new RankBuilder(this);
    }

    public static Rank wouldHaveCircularInheritance(Rank rank, Rank rank1) {
        Set<UUID> visited = new HashSet<>();
        return wouldHaveCircularInheritanceHelper(rank, rank1, visited);
    }

    private static Rank wouldHaveCircularInheritanceHelper(Rank rank, Rank rank1, Set<UUID> visited) {
        if (visited.contains(rank.getRankId())) {
            return null; // Circular inheritance detected
        }

        visited.add(rank.getRankId());

        for (Rank rank2 : rank.getInheritedRanksConverted()) {
            if (rank2.getRankId().equals(rank1.getRankId())) {
                return rank2;
            }
            Rank r = wouldHaveCircularInheritanceHelper(rank2, rank1, visited);
            if (r != null) {
                return r;
            }
        }

        visited.remove(rank.getRankId()); // Remove the rank from visited set when backtracking
        return null;
    }


    public static List<Rank> findCircularInheritance(Rank rank, Rank rank1) {
        Set<UUID> visited = new HashSet<>();
        List<Rank> circularInheritancePath = new ArrayList<>();

        return findCircularInheritanceHelper(rank, rank1, visited, circularInheritancePath);
    }

    public static List<Rank> findCircularInheritancePre(Rank rank, Rank rank1) {
        Rank rankClone = rank.clone();
        rankClone.getInheritedRanks().add(rank1.getRankId());
        return findCircularInheritance(rankClone, rank1);
    }

    private static List<Rank> findCircularInheritanceHelper(Rank rank, Rank rank1, Set<UUID> visited, List<Rank> circularInheritancePath) {
        if (visited.contains(rank.getRankId())) {
            circularInheritancePath.add(rank);
            return circularInheritancePath; // Circular inheritance detected
        }

        visited.add(rank.getRankId());

        for (Rank rank2 : rank.getInheritedRanksConverted()) {
            List<Rank> newPath = new ArrayList<>(circularInheritancePath); // Create a new copy of the path to avoid modifying the existing one

            newPath.add(rank);
            if (rank2.getRankId().equals(rank1.getRankId())) {
                newPath.add(rank2);
                return newPath;
            }

            List<Rank> result = findCircularInheritanceHelper(rank2, rank1, visited, newPath);
            if (result != null) {
                return result;
            }
        }

        visited.remove(rank.getRankId()); // Remove the rank from visited set when backtracking
        return null;
    }

    public enum OrderedInheritance {
        SMALL_TO_LARGE, LARGE_TO_SMALL
    }
}
