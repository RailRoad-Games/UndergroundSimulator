package de.qstudios.underground.util;

import java.util.Arrays;

public class Percentile {

    public static double percentile(double[] data, double percentile) {
        Arrays.sort(data);

        double index = percentile / 100.0 * (data.length - 1);
        int lower = (int) Math.floor(index);
        int upper = (int) Math.ceil(index);

        if (lower == upper) {
            return data[lower];
        }

        double weight = index - lower;
        return data[lower] * (1 - weight) + data[upper] * weight;
    }

    public static void main(String[] args) {
        double[] values = {10, 20, 30, 40, 50};
        System.out.println(percentile(values, 25)); // z.B. 25. Perzentil
    }
}