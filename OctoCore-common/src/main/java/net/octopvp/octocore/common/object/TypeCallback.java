package net.octopvp.octocore.common.object;

/**
 * {@link Callback} but with a return type
 * @param <A>
 */
public interface TypeCallback<A> {
    A call();
}
