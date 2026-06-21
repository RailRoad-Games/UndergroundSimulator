package de.qstudios.underground.misc;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import de.qstudios.underground.bm.FibonacciBenchmark;

public class BenchmarkUI {

    public static void run() throws Exception {

        printHeader();

        fakeProgress("Warmup Phase", 40);
        System.out.println("[INFO] Warmup completed.");

        fakeProgress("Measurement Phase", 60);
        System.out.println("[INFO] Starting JMH benchmark...\n");

        runJmhBenchmark();

        printFooter();
    }

    private static void printHeader() {
        System.out.println("====================================");
        System.out.println(" Underground Benchmark Suite");
        System.out.println("====================================\n");
    }

    private static void runJmhBenchmark() throws Exception {

        Options opt = new OptionsBuilder()
                .include(FibonacciBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();

        new Runner(opt).run();
    }

    private static void fakeProgress(String label, int steps) throws InterruptedException {

        System.out.println("[INFO] " + label);

        for (int i = 0; i <= steps; i++) {

            int percent = (int) ((i / (double) steps) * 100);

            System.out.print("\rProgress: " + percent + "%");

            Thread.sleep(30);
        }

        System.out.println();
    }

    private static void printFooter() {
        System.out.println("\n====================================");
        System.out.println("[RESULT] Benchmark finished successfully.");
        System.out.println("====================================");
    }
}