package org.rrgames.underground.bm;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class FibonacciBenchmark {

    int last = 1;
    int before = 0;

    @Benchmark
    public int fibonacciStep() {
        int act = last + before;
        before = last;
        last = act;
        return act;
    }
}