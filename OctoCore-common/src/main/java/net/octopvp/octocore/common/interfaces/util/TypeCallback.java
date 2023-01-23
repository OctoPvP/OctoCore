package net.octopvp.octocore.common.interfaces.util;

/**
 * {@link Callback} but with a return type
 *
 * @param <A>
 */
public interface TypeCallback<A> {
    A call();
}
