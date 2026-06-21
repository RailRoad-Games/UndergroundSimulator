package de.qstudios.underground.bm;

import org.openjdk.jmh.annotations.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class GeneralBenchmark {

    private String input;
    private int[] data;
    private Map<String, Integer> map;

    // =========================
    // Setup
    // =========================
    @Setup
    public void setup() {

        input = "--lang=fr --debug --sound=2 --disableAutoLangDetect";

        data = new int[1000];
        for (int i = 0; i < data.length; i++) {
            data[i] = i;
        }

        map = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            map.put("key" + i, i);
        }
    }

    // =========================
    // 1. CLI Parsing
    // =========================
    @Benchmark
    public int parseInput() {

        int score = 0;

        if (input.contains("--debug")) score++;
        if (input.contains("--lang=")) score++;
        if (input.contains("--sound=")) score++;
        if (input.contains("--disableAutoLangDetect")) score++;

        return score;
    }

    // =========================
    // 2. Array Processing
    // =========================
    @Benchmark
    public int sumArray() {

        int sum = 0;

        for (int v : data) {
            sum += v;
        }

        return sum;
    }

    // =========================
    // 3. HashMap Lookup
    // =========================
    @Benchmark
    public int mapLookup() {
        return map.get("key500");
    }

    // =========================
    // 4. String Building
    // =========================
    @Benchmark
    public String buildString() {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            sb.append("action=").append(i).append(";");
        }

        return sb.toString();
    }
}