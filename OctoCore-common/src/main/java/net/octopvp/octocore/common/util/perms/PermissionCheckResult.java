package net.octopvp.octocore.common.util.perms;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.object.ServerContext;

import java.util.Optional;

@Data
@RequiredArgsConstructor
public class PermissionCheckResult {
    private final String permission;
    private final boolean wildcard;
    private final Optional<Boolean> negated;
    private final Optional<ServerContext> serverContext;
    private final Reason reason;

    private long expire = -1; // cache expire time

    public boolean getResult() {
        return negated.map(aBoolean -> !aBoolean).orElseGet(reason::getResult);
    }

    public boolean allowed() {
        return getResult();
    }

    @Override
    public String toString() {
        return "(" + allowed() + ") PermissionCheckResult{" +
                "permission='" + permission + '\'' +
                ", wildcard=" + wildcard +
                ", negated=" + negated +
                ", serverContext=" + serverContext +
                ", reason=" + reason +
                '}';
    }

    public enum Reason {
        NEGATED(false), WILDCARD(true), EXPLICIT_SET(true), NOT_SET(false);
        boolean result;

        Reason(boolean result) {
            this.result = result;
        }

        public boolean getResult() {
            return this.result;
        }
    }
}
