package net.octopvp.octocore.rpg.util.runnable;

import net.octopvp.octocore.common.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "FieldAccessedSynchronizedAndUnsynchronized"})
public class Tasks<T> {
    private static final Map<String, Tasks<?>> sharedChains = new HashMap<>();
    private static final ThreadLocal<Tasks<?>> currentChain = new ThreadLocal<>();
    private static Plugin plugin;
    private final Map<String, Object> taskMap = new HashMap<>(0);
    private final ConcurrentLinkedQueue<TaskHolder<?, ?>> chainQueue = new ConcurrentLinkedQueue<>();
    @SuppressWarnings("WeakerAccess") // IDE is wrong, can't be private
    protected Runnable doneCallback;
    protected BiConsumer<Exception, Task<?, ?>> errorHandler;
    private boolean shared = false;
    private boolean done = false;


    private boolean executed = false;
    private boolean async;
    private String sharedName;
    private Object previous;
    private TaskHolder<?, ?> currentHolder;

    public static void init(Plugin plugin1) {
        plugin = plugin1;
    }

    public static BukkitTask run(Runnable callable) {
        if (plugin == null) {
            throw new IllegalStateException("Tasks has not been initialized! Please use Tasks.init!");
        }
        return Bukkit.getScheduler().runTask(plugin, callable);
    }

    public static BukkitTask runSync(Runnable callable) {
        if (plugin == null) {
            throw new IllegalStateException("Tasks has not been initialized! Please use Tasks.init!");
        }
        return run(callable);
    }

    public static BukkitTask runAsync(Runnable callable) {
        if (plugin == null) {
            throw new IllegalStateException("Tasks has not been initialized! Please use Tasks.init!");
        }
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, callable);
    }

    public static BukkitTask runLater(Runnable callable, long delay) {
        if (plugin == null) {
            throw new IllegalStateException("Tasks has not been initialized! Please use Tasks.init!");
        }
        return Bukkit.getScheduler().runTaskLater(plugin, callable, delay);
    }

    public static BukkitTask runAsyncLater(Runnable callable, long delay) {
        if (plugin == null) {
            throw new IllegalStateException("Tasks has not been initialized! Please use Tasks.init!");
        }
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, callable, delay);
    }

    public static BukkitTask runTimer(Runnable callable, long delay, long interval) {
        if (plugin == null) {
            throw new IllegalStateException("Tasks has not been initialized! Please use Tasks.init!");
        }
        return Bukkit.getScheduler().runTaskTimer(plugin, callable, delay, interval);
    }

    public static BukkitTask runAsyncTimer(Runnable callable, long delay, long interval) {
        if (plugin == null) {
            throw new IllegalStateException("Tasks has not been initialized! Please use Tasks.init!");
        }
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, callable, delay, interval);
    }

    private static void log(String log) {
        for (String s : log.split("\n")) {
            Logger.info(s);
        }
    }

    public static void logError(String log) {
        for (String s : log.split("\n")) {
            Logger.error(s);
        }
    }

    public static <T> Tasks<T> newChain() {
        if (plugin == null) {
            throw new IllegalStateException("Tasks has not been initialized! Please use Tasks.init!");
        }
        return new Tasks<>();
    }

    public static synchronized <T> Tasks<T> newSharedChain(String name) {
        Tasks<?> chain;
        synchronized (sharedChains) {
            chain = sharedChains.get(name);
        }

        if (chain != null) {
            synchronized (chain) {
                if (chain.done) {
                    chain = null;
                }
            }
        }

        if (chain == null) {
            chain = newChain();
            chain.shared = true;
            chain.sharedName = name;
            sharedChains.put(name, chain);
        }

        return new SharedTasks<>((Tasks<T>) chain);
    }

    public static <T> Tasks<T> newSharedChain(Player player) {
        return newSharedChain(player, "__MAIN__");
    }

    public static <T> Tasks<T> newSharedChain(Player player, String name) {
        return newSharedChain(player.getUniqueId() + "__PlayerChain__" + name);
    }


    public static void abort() throws AbortChainException {
        throw new AbortChainException();
    }

    public boolean hasTaskData(String key) {
        return taskMap.containsKey(key);
    }

    public <R> R getTaskData(String key) {
        return (R) taskMap.get(key);
    }

    public <R> R setTaskData(String key, Object val) {
        return (R) taskMap.put(key, val);
    }

    public <R> R removeTaskData(String key) {
        return (R) taskMap.remove(key);
    }

    public Tasks<T> abortIfNull() {
        return abortIfNull(null, null);
    }

    public Tasks<T> abortIfNull(Player player, String msg) {
        return current((obj) -> {
            if (obj == null) {
                if (msg != null && player != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                }
                abort();
                return null;
            }
            return obj;
        });
    }

    public Tasks<T> storeAsData(String key) {
        return current((val) -> {
            setTaskData(key, val);
            return val;
        });
    }

    public <R> Tasks<R> returnData(String key) {
        return currentFirst(() -> getTaskData(key));
    }

    public Tasks<Tasks<?>> returnChain() {
        return currentFirst(() -> this);
    }

    public Tasks<T> delay(final int ticks) {
        return currentCallback((input, next) -> {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> next.accept(input), ticks);
        });
    }

    public <R> Tasks<R> syncFirstCallback(AsyncExecutingFirstTask<R> task) {
        return add0(new TaskHolder<>(this, false, task));
    }

    public <R> Tasks<R> asyncFirstCallback(AsyncExecutingFirstTask<R> task) {
        return add0(new TaskHolder<>(this, true, task));
    }

    public <R> Tasks<R> currentFirstCallback(AsyncExecutingFirstTask<R> task) {
        return add0(new TaskHolder<>(this, null, task));
    }

    public <R> Tasks<R> syncCallback(AsyncExecutingTask<R, T> task) {
        return add0(new TaskHolder<>(this, false, task));
    }

    public Tasks<?> syncCallback(AsyncExecutingGenericTask task) {
        return add0(new TaskHolder<>(this, false, task));
    }

    public <R> Tasks<R> asyncCallback(AsyncExecutingTask<R, T> task) {
        return add0(new TaskHolder<>(this, true, task));
    }

    public Tasks<?> asyncCallback(AsyncExecutingGenericTask task) {
        return add0(new TaskHolder<>(this, true, task));
    }

    public <R> Tasks<R> currentCallback(AsyncExecutingTask<R, T> task) {
        return add0(new TaskHolder<>(this, null, task));
    }

    public Tasks<?> currentCallback(AsyncExecutingGenericTask task) {
        return add0(new TaskHolder<>(this, null, task));
    }

    public <R> Tasks<R> syncFirst(FirstTask<R> task) {
        return add0(new TaskHolder<>(this, false, task));
    }

    public <R> Tasks<R> asyncFirst(FirstTask<R> task) {
        return add0(new TaskHolder<>(this, true, task));
    }

    public <R> Tasks<R> currentFirst(FirstTask<R> task) {
        return add0(new TaskHolder<>(this, null, task));
    }

    public <R> Tasks<R> sync(Task<R, T> task) {
        return add0(new TaskHolder<>(this, false, task));
    }

    public Tasks<?> sync(GenericTask task) {
        return add0(new TaskHolder<>(this, false, task));
    }

    public <R> Tasks<R> async(Task<R, T> task) {
        return add0(new TaskHolder<>(this, true, task));
    }

    public Tasks<?> async(GenericTask task) {
        return add0(new TaskHolder<>(this, true, task));
    }

    public <R> Tasks<R> current(Task<R, T> task) {
        return add0(new TaskHolder<>(this, null, task));
    }

    public Tasks<?> current(GenericTask task) {
        return add0(new TaskHolder<>(this, null, task));
    }


    public Tasks<?> syncLast(LastTask<T> task) {
        return add0(new TaskHolder<>(this, false, task));
    }

    public Tasks<?> asyncLast(LastTask<T> task) {
        return add0(new TaskHolder<>(this, true, task));
    }

    public Tasks<?> currentLast(LastTask<T> task) {
        return add0(new TaskHolder<>(this, null, task));
    }


    public void execute() {
        execute0();
    }

    protected void execute0() {
        synchronized (this) {
            if (this.executed) {
                if (this.shared) {
                    return;
                }
                throw new RuntimeException("Already executed and not a shared chain");
            }
            this.executed = true;
        }
        async = !Bukkit.isPrimaryThread();
        nextTask();
    }

    public void executeNext() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::execute, 1);
    }

    public void execute(Runnable done) {
        this.doneCallback = done;
        execute();
    }

    public void execute(BiConsumer<Exception, Task<?, ?>> errorHandler) {
        this.errorHandler = errorHandler;
        execute();
    }

    public void execute(Runnable done, BiConsumer<Exception, Task<?, ?>> errorHandler) {
        this.doneCallback = done;
        this.errorHandler = errorHandler;
        execute();
    }

    protected void done() {
        this.done = true;
        if (this.shared) {
            synchronized (sharedChains) {
                sharedChains.remove(this.sharedName);
            }
        }
        if (this.doneCallback != null) {
            this.doneCallback.run();
        }
    }

    @SuppressWarnings("rawtypes")
    protected Tasks add0(TaskHolder<?, ?> task) {
        synchronized (this) {
            if (!this.shared && this.executed) {
                throw new RuntimeException("Tasks is executing and not shared");
            }
        }

        this.chainQueue.add(task);
        return this;
    }

    private void nextTask() {
        synchronized (this) {
            this.currentHolder = this.chainQueue.poll();
            if (this.currentHolder == null) {
                this.done = true; // to ensure its done while synchronized
            }
        }

        if (this.currentHolder == null) {
            this.previous = null;
            this.done();
            return;
        }

        Boolean isNextAsync = this.currentHolder.async;
        if (isNextAsync == null) {
            isNextAsync = this.async;
        }

        if (isNextAsync) {
            if (this.async) {
                this.currentHolder.run();
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    this.async = true;
                    this.currentHolder.run();
                });
            }
        } else {
            if (this.async) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    this.async = false;
                    this.currentHolder.run();
                });
            } else {
                this.currentHolder.run();
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public interface Task<R, A> {
        default Tasks<?> getCurrentChain() {
            return currentChain.get();
        }

        R run(A input) throws AbortChainException;
    }

    @SuppressWarnings("WeakerAccess")
    public interface AsyncExecutingTask<R, A> extends Task<R, A> {
        default Tasks<?> getCurrentChain() {
            return currentChain.get();
        }

        @Override
        default R run(A input) throws AbortChainException {
            return null;
        }

        void runAsync(A input, Consumer<R> next) throws AbortChainException;
    }

    @SuppressWarnings("WeakerAccess")
    public interface FirstTask<R> extends Task<R, Object> {
        @Override
        default R run(Object input) throws AbortChainException {
            return run();
        }

        R run() throws AbortChainException;
    }

    @SuppressWarnings("WeakerAccess")
    public interface AsyncExecutingFirstTask<R> extends AsyncExecutingTask<R, Object> {
        @Override
        default R run(Object input) throws AbortChainException {
            return null;
        }

        @Override
        default void runAsync(Object input, Consumer<R> next) throws AbortChainException {
            run(next);
        }

        void run(Consumer<R> next) throws AbortChainException;
    }

    @SuppressWarnings("WeakerAccess")
    public interface LastTask<A> extends Task<Object, A> {
        @Override
        default Object run(A input) throws AbortChainException {
            runLast(input);
            return null;
        }

        void runLast(A input) throws AbortChainException;
    }

    @SuppressWarnings("WeakerAccess")
    public interface GenericTask extends Task<Object, Object> {
        @Override
        default Object run(Object input) throws AbortChainException {
            runGeneric();
            return null;
        }

        void runGeneric() throws AbortChainException;
    }

    @SuppressWarnings("WeakerAccess")
    public interface AsyncExecutingGenericTask extends AsyncExecutingTask<Object, Object> {
        @Override
        default Object run(Object input) throws AbortChainException {
            return null;
        }

        @Override
        default void runAsync(Object input, Consumer<Object> next) throws AbortChainException {
            run(() -> next.accept(null));
        }

        void run(Runnable next) throws AbortChainException;
    }

    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    private static class TaskHolder<R, A> {
        public final Boolean async;
        private final Tasks<?> chain;
        private final Task<R, A> task;
        private boolean executed = false;
        private boolean aborted = false;

        private TaskHolder(Tasks<?> chain, Boolean async, Task<R, A> task) {
            this.task = task;
            this.chain = chain;
            this.async = async;
        }


        private void run() {
            final Object arg = this.chain.previous;
            this.chain.previous = null;
            final R res;
            try {
                currentChain.set(this.chain);
                if (this.task instanceof AsyncExecutingTask) {
                    ((AsyncExecutingTask<R, A>) this.task).runAsync((A) arg, this::next);
                } else {
                    next(this.task.run((A) arg));
                }
            } catch (AbortChainException ignored) {
                this.abort();
            } catch (Exception e) {
                if (this.chain.errorHandler != null) {
                    this.chain.errorHandler.accept(e, this.task);
                } else {
                    logError("Tasks Exception on " + this.task.getClass().getName());
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    logError(sw.toString());
                }
                this.abort();
            } finally {
                currentChain.remove();
            }
        }

        private synchronized void abort() {
            this.aborted = true;
            this.chain.previous = null;
            this.chain.chainQueue.clear();
            this.chain.done();
        }

        private void next(Object resp) {
            synchronized (this) {
                if (this.aborted) {
                    this.chain.done();
                    return;
                }
                if (this.executed) {
                    this.chain.done();
                    throw new RuntimeException("This task has already been executed.");
                }
                this.executed = true;
            }

            this.chain.async = !Bukkit.isPrimaryThread(); 
            this.chain.previous = resp;
            this.chain.nextTask();
        }
    }

    @SuppressWarnings("PublicInnerClass,WeakerAccess")
    public static class AbortChainException extends Throwable {
    }

    private static class SharedTasks<R> extends Tasks<R> {
        private final Tasks<R> backingChain;

        private SharedTasks(Tasks<R> backingChain) {
            this.backingChain = backingChain;
        }

        @Override
        public void execute() {
            synchronized (backingChain) {
                SharedTasks<R> sharedChain = this;
                backingChain.currentCallback((AsyncExecutingGenericTask) sharedChain::execute);
                backingChain.execute();
            }
        }

        @Override
        public void execute(Runnable done) {
            this.doneCallback = done;
            execute0();
        }
    }
}
