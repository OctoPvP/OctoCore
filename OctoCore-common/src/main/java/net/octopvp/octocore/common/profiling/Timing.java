package net.octopvp.octocore.common.profiling;

import lombok.RequiredArgsConstructor;
import net.octopvp.octocore.common.util.math.SimpleAverage;

@RequiredArgsConstructor
public class Timing {
    public final String name;
    public int calls;
    public long call, total, lastCall;
    public double stdDev;
    public SimpleAverage average = new SimpleAverage(300, 0);
}
