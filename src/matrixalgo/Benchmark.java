package matrixalgo;

import java.util.Arrays;

public class Benchmark {
    /**
     * Time how long a function takes to execute
     * @param r the function to time
     * @return seconds taken to execute the function
     */
    public static double timed(Runnable r) {
        long start = System.nanoTime();
        r.run();
        long end = System.nanoTime();
        return (end - start) / 1e9;
    }

    private static double timedMultiply(Matrix result, OpCounter counter, Runnable r) {
        double[] times = new double[3];
        for (int i = 0; i < times.length; i++) {
            result.reset();
            counter.reset();
            times[i] = timed(r);
            // needs testing whether running the garbage collector after a run is good
//            System.gc();
        }
        Arrays.sort(times);
        // median
        return times[1];
    }

    public static void benchmark() {
        OpCounter counter = new OpCounter();
        Algorithms algorithms = new Algorithms(counter);

        for (int i = 1; i < 100; i++) {
            Matrix m1 = Matrix.generateRandom(i, i);
            Matrix m2 = Matrix.generateRandom(i, i);
            Matrix result = new Matrix(i, i);

            double classicTime = timedMultiply(result, counter, () -> {
                algorithms.multiplyClassic(result, m1, m2);
            });

            double binetTime = timedMultiply(result, counter, () -> {
                algorithms.multiplyBinet(result, m1, m2, false);
            });

            double strassenTime = timedMultiply(result, counter, () -> {
                algorithms.multiplyStrassen(result, m1, m2);
            });

            double ladermanTime = timedMultiply(result, counter, () -> {
                algorithms.multiplyLaderman(result, m1, m2);
            });

            System.out.printf("Benchmark %dx%d:%n", i, i);
            System.out.printf("Classic: %.4f s%n", classicTime);
            System.out.printf("Binet: %.4f s%n", binetTime);
            System.out.printf("Strassen: %.4f s%n", strassenTime);
            System.out.printf("Laderman: %.4f s%n", ladermanTime);
            System.out.println();
        }
    }
}
