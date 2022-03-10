package net.octopvp.octocore.paper.objects.builders;


import net.octopvp.octocore.common.object.ServerContext;
import net.octopvp.octocore.paper.objects.permissions.Grant;
import net.octopvp.octocore.paper.objects.permissions.Rank;

import java.util.UUID;

public class GrantBuilder {
    private final Grant grant;

    public GrantBuilder(Rank targetRank) {
        grant = new Grant(targetRank);
    }

    public GrantBuilder(String rankName, UUID rank) {
        grant = new Grant(rankName, rank);
    }

    public GrantBuilder(Grant existing) {
        grant = existing;
    }

    public Grant build() {
        return grant;
    }

    public GrantBuilder setAddedAt(long l) {
        grant.setAddedAt(l);
        return this;
    }

    public GrantBuilder setDuration(long l) {
        grant.setDuration(l);
        return this;
    }

    public GrantBuilder setRemovedAt(long l) {
        grant.setRemovedAt(l);
        return this;
    }

    public GrantBuilder setAddedBy(String added) {
        grant.setAddedBy(added);
        return this;
    }

    public GrantBuilder setReason(String reason) {
        grant.setReason(reason);
        return this;
    }

    public GrantBuilder setRemovedBy(String removedBy) {
        grant.setRemovedBy(removedBy);
        return this;
    }

    public GrantBuilder setAddedByUUID(UUID addedBy) {
        grant.setAddedByUUID(addedBy);
        return this;
    }

    public GrantBuilder setRemovedByUUID(UUID removed) {
        grant.setRemovedByUUID(removed);
        return this;
    }

    public GrantBuilder setActive(boolean active) {
        grant.setActive(active);
        return this;
    }

    public GrantBuilder setPermanent(boolean perm) {
        grant.setPermanent(perm);
        return this;
    }

    public GrantBuilder setPerm(boolean p) {
        return setPermanent(p);
    }

    public GrantBuilder setServer(String server) {
        grant.setServer(new ServerContext(server));
        return this;
    }

    public GrantBuilder setGlobal() {
        grant.setServer(new ServerContext("Global"));
        return this;
    }

    public GrantBuilder setServer(ServerContext context) {
        grant.setServer(context);
        return this;
    }
}
