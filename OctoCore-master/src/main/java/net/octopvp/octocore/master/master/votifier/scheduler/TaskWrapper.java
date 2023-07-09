package net.octopvp.octocore.master.master.votifier.scheduler;

import com.vexsoftware.votifier.platform.scheduler.ScheduledVotifierTask;

import java.util.concurrent.Future;

public class TaskWrapper implements ScheduledVotifierTask {
    private final Future<?> future;
    private final boolean canCancel;

    public TaskWrapper(Future<?> future, boolean... canCancel) {
        this.future = future;
        this.canCancel = canCancel.length > 0 && canCancel[0];
    }

    @Override
    public void cancel() {
        if (!canCancel) throw new UnsupportedOperationException("Cannot cancel this task");
        future.cancel(true);
    }
}
