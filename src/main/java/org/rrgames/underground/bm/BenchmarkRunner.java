package org.rrgames.underground.bm;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarkRunner {

    public static void main(String[] args) throws Exception {
Options opt = new OptionsBuilder()
        .include("org.rrgames.underground.bm.GeneralBenchmark")
        .forks(1)
        .warmupIterations(1)
        .measurementIterations(3)
        .build();

new Runner(opt).run();
    }
}