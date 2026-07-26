package org.rrgames.underground.bm;

import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.*;

import org.rrgames.underground.misc.QSGPS;

import java.util.Collection;

public class BenchmarkToBGPS {

    public static void main(String[] args) throws Exception {

        Options opt = new OptionsBuilder()
                .include(GeneralBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(1)
                .measurementIterations(2)
                .build();

        Collection<RunResult> results = new Runner(opt).run();

        double parse = 0;
        double array = 0;
        double map = 0;
        double string = 0;

        for (RunResult r : results) {

            String name = r.getParams().getBenchmark();
            double score = r.getPrimaryResult().getScore();

            if (name.contains("parseInput")) parse = score;
            if (name.contains("sumArray")) array = score;
            if (name.contains("mapLookup")) map = score;
            if (name.contains("buildString")) string = score;
        }

        int bgps = QSGPS.calculate(parse, array, map, string);

        QSGPS.print(bgps);
    }
}