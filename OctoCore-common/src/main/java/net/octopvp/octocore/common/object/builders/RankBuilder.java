package net.octopvp.octocore.common.object.builders;

import lombok.Getter;
import lombok.SneakyThrows;
import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.common.object.enums.RankType;
import net.octopvp.octocore.common.object.permissions.Rank;
import net.octopvp.octocore.common.util.ChatColor;
import net.octopvp.octocore.common.util.perms.Node;
import net.octopvp.octocore.common.util.perms.PermissionManager;

import java.util.UUID;

public class RankBuilder implements Cloneable {
    @Getter
    private final Rank rank;

    public RankBuilder(String name) {
        rank = new Rank();
        rank.setName(name);
        rank.setDefaultRank(false);
    }

    public RankBuilder(Rank r) {
        this.rank = r;
    }

    @SneakyThrows
    @Override
    public RankBuilder clone() {
        return (RankBuilder) super.clone();
    }

    public RankBuilder setName(String name) {
        rank.setName(name);
        return this;
    }

    public RankBuilder setPrefix(String prefix) {
        rank.setPrefix(prefix);
        return this;
    }

    public RankBuilder setWeight(int w) {
        rank.setWeight(w);
        return this;
    }

    public RankBuilder setColor(ChatColor color) {
        rank.setColor(color);
        return this;
    }

    public RankBuilder setColor(Enum<?> c) {
        return setColor(ChatColor.from(c));
    }

    public RankBuilder setColor(String color) {
        rank.setColor(ChatColor.getByChar(color.replace("\u00a7", "").replace("&", "")));
        return this;
    }

    public RankBuilder setChatColor(ChatColor color) {
        rank.setChatColor(color);
        return this;
    }

    public RankBuilder setChatColor(Enum<?> c) {
        return setChatColor(ChatColor.from(c));
    }

    public RankBuilder setChatColor(String color) {
        rank.setChatColor(ChatColor.getByChar(color.replace("\u00a7", "").replace("&", "")));
        return this;
    }

    public RankBuilder setRankType(RankType type) {
        rank.setRankType(type);
        return this;
    }

    public RankBuilder setDefaultRank(boolean defaultRank) {
        rank.setDefaultRank(defaultRank);
        return this;
    }

    public RankBuilder addNode(Node node) {
        PermissionManager.getInstance().addNode(node, rank.getNodes());
        return this;
    }

    public RankBuilder addPermission(String s) {
        // rank.getNodes().add(new Node(s, new ServerContext("Global"), true, rank.getWeight()));
        PermissionManager.getInstance().addNode(Node.create(s, false, "*"), rank.getNodes());
        return this;
    }

    public RankBuilder addPermission(String s, ServerContext context) {
        // rank.getNodes().add(new Node(s, context, true, rank.getWeight()));
        PermissionManager.getInstance().addNode(Node.create(s, false, context.getServersString()), rank.getNodes());
        return this;
    }

    public RankBuilder addPermission(String s, String context) {
        // rank.getNodes().add(new Node(s, new ServerContext(context), true, rank.getWeight()));
        PermissionManager.getInstance().addNode(Node.create(s, false, context), rank.getNodes());
        return this;
    }

    public RankBuilder negatePermission(String s) {
        // rank.getNodes().add(new Node(s, new ServerContext("Global"), false, rank.getWeight()));
        PermissionManager.getInstance().addNode(Node.create(s, true, "*"), rank.getNodes());
        return this;
    }

    public RankBuilder negatePermission(String s, ServerContext context) {
        // rank.getNodes().add(new Node(s, context, false, rank.getWeight()));
        PermissionManager.getInstance().addNode(Node.create(s, true, context.getServersString()), rank.getNodes());
        return this;
    }

    public RankBuilder negatePermission(String s, String context) {
        // rank.getNodes().add(new Node(s, new ServerContext(context), false, rank.getWeight()));
        PermissionManager.getInstance().addNode(Node.create(s, true, context), rank.getNodes());
        return this;
    }

    public RankBuilder unsetPermission(String perm) {
        PermissionManager.getInstance().removePermission(perm, rank.getNodes());
        return this;
    }

    public RankBuilder unsetPermission(Node node) {
        PermissionManager.getInstance().removePermission(node.getPermissionString(), rank.getNodes());
        return this;
    }

    public RankBuilder addInheritedRank(Rank rank1) {
        rank.getInheritedRanks().add(rank1.getRankId());
        return this;
    }

    public RankBuilder addInheritedRank(UUID rank1) {
        rank.getInheritedRanks().add(rank1);
        return this;
    }

    public RankBuilder removeInheritedRank(UUID rank1) {
        rank.getInheritedRanks().remove(rank1);
        return this;
    }

    public RankBuilder setBold(boolean bold) {
        rank.setBold(bold);
        return this;
    }

    public RankBuilder setItalic(boolean italic) {
        rank.setBold(italic);
        return this;
    }

    public RankBuilder setPurchasable(boolean setPurchasable) {
        rank.setPurchasable(setPurchasable);
        return this;
    }

    public RankBuilder setChangeableMainColor(boolean changeableMainColor) {
        rank.setChangableMainColor(changeableMainColor);
        return this;
    }

    public RankBuilder setScope(ServerContext context) {
        rank.setScope(context);
        return this;
    }

    public Rank build() {
        return rank;
    }
}
