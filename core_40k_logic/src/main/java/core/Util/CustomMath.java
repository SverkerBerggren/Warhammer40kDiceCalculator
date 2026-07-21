package core.Util;

import java.util.List;

public class CustomMath {

    // Sample variance: mean estimated from the same data (Bessel's correction, n-1)
    public static double sampleVariance(int[] values) {
        double mean = average(values);
        return sumSquaredDeviations(values, mean) / (values.length - 1);
    }


    private static double sumSquaredDeviations(int[] values, double mean) {
        double sum = 0;
        for (int v : values) {
            sum += Math.pow(v - mean, 2);
        }
        return sum;
    }

    private static double average(int[] values) {
        double sum = 0;
        for (int v : values) sum += v;
        return sum / values.length;
    }
}
