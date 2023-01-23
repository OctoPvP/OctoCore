package net.octopvp.octocore.common.interfaces.util;

import java.util.function.Consumer;

public interface ObjectConsumer<T> extends Consumer<T> {
    default ObjectConsumer<T> run(T t, Consumer<T> andThen) {
        accept(t);
        if (andThen != null) {
            andThen.accept(t);
        }
        return this;
    }

    default ObjectConsumer<T> run(T t) {
        return run(t, null);
    }
}
