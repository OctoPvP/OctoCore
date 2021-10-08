package net.octopvp.octocore.common.profiling;

import net.octopvp.octocore.common.object.tuple.Pair;

import java.util.Map;

public interface Profiler {
    void start(String name);

    void start();

    void stop(String name, long extense);

    void stop(String name);

    void stop();

    void reset();

    Map<String, Pair<Integer, Double>> results(ResultsType type);
}
