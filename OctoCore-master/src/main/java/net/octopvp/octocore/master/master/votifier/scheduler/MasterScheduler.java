package net.octopvp.octocore.master.master.votifier.scheduler;

import com.vexsoftware.votifier.platform.scheduler.ScheduledVotifierTask;
import com.vexsoftware.votifier.platform.scheduler.VotifierScheduler;
import net.octopvp.octocore.master.master.votifier.NuVotifierMaster;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MasterScheduler implements VotifierScheduler {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

    @Override
    public ScheduledVotifierTask sync(Runnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        NuVotifierMaster.runSyncQueue.add(new NuVotifierMaster.SyncWrapper(runnable, future));
        return new TaskWrapper(future);
    }

    @Override
    public ScheduledVotifierTask onPool(Runnable runnable) {
        return new TaskWrapper(scheduler.submit(runnable));
    }

    @Override
    public ScheduledVotifierTask delayedSync(Runnable runnable, int i, TimeUnit timeUnit) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        NuVotifierMaster.runSyncQueue.add(new NuVotifierMaster.SyncWrapper(runnable, future).setExpectedRunTime(System.currentTimeMillis() + timeUnit.toMillis(i)));
        return new TaskWrapper(future);
    }

    @Override
    public ScheduledVotifierTask delayedOnPool(Runnable runnable, int i, TimeUnit timeUnit) {
        return new TaskWrapper(scheduler.schedule(runnable, i, timeUnit));
    }

    @Override
    public ScheduledVotifierTask repeatOnPool(Runnable runnable, int i, int i1, TimeUnit timeUnit) {
        return new TaskWrapper(scheduler.scheduleAtFixedRate(runnable, i, i1, timeUnit));
    }
}
