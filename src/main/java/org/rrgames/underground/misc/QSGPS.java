package org.rrgames.underground.misc;

public class QSGPS {

    // =========================
    // MAIN CALCULATOR
    // =========================
public static int calculate(
        double parseScore,
        double arrayScore,
        double mapScore,
        double stringScore) {

    double score =
            normalize(parseScore, 100_000_000) * 0.30 +
            normalize(arrayScore, 50_000_000) * 0.25 +
            normalize(mapScore, 200_000_000) * 0.25 +
            normalize(stringScore, 30_000_000) * 0.20;

    return (int) (score * 10000);
}

// =========================
// BETTER NORMALIZATION
// =========================
private static double normalize(double value, double maxExpected) {

    if (value <= 0) return 0;

    double ratio = value / maxExpected;

    return ratio / (1.0 + ratio); // smooth scaling
}

    // =========================
    // HUMAN READABLE OUTPUT
    // =========================
    public static void print(int score) {

        System.out.println("==================================");
        System.out.println(" qstudios Performance Score (BGPS)");
        System.out.println("==================================");

        System.out.println("Score: " + score + " / 10000");
        System.out.println("Performance: " + scoreToPercent(score) + "%");
        System.out.println();
        System.out.println("Class: " + getClass(score));
        System.out.println("==================================");

        drawBar(score);
    }

    // =========================
    // CATEGORY SYSTEM
    // =========================
    private static String getClass(int score) {

        if (score >= 8500)
            return "🚀 Quantum Beast System";
        if (score >= 6500)
            return "🔥 Beast System";
        if (score >= 4500)
            return "💻 Gaming Ready";
        if (score >= 2500)
            return "🧠 Normal Performance";
        if (score >= 1000)
            return "📱 Weak System";
        return "🥔 Potato System";
    }

    // =========================
    // PERCENT CONVERSION
    // =========================
    private static int scoreToPercent(int score) {
        return (int) ((score / 10000.0) * 100);
    }

    // =========================
    // SIMPLE ASCII BAR
    // =========================
    private static void drawBar(int score) {

        int bars = score / 500; // 0–20 bars

        System.out.print("[");

        for (int i = 0; i < 20; i++) {
            if (i < bars)
                System.out.print("█");
            else
                System.out.print(" ");
        }

        System.out.println("]");

    }
}