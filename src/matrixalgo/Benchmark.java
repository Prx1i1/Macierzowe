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
            try {
                times[i] = timed(r);
            } catch (Exception e) {
                e.printStackTrace();
                return Double.NaN;
            }
            // needs testing whether running the garbage collector after a run is good
//            System.gc();
        }
        Arrays.sort(times);
        // median
        return times[1];
    }

    public static void warmup() {
        OpCounter counter = new OpCounter();
        Algorithms algorithms = new Algorithms(counter);

        Matrix m1 = Matrix.generateRandom(6, 6);
        Matrix m2 = Matrix.generateRandom(6, 6);
        Matrix result = new Matrix(6, 6);
        System.out.println("Warming up...");
        long start = System.currentTimeMillis();
        while (true) {
            result.reset();
            counter.reset();
            algorithms.multiplyClassic(result, m1, m2);
            algorithms.multiplyBinet(result, m1, m2, false);
            algorithms.multiplyStrassen(result, m1, m2);
            algorithms.multiplyLaderman(result, m1, m2);

            // 10 seconds of warmup, need to get the cpu smoking, fans running
            if (System.currentTimeMillis() - start > 10_000) break;
        }
        System.out.println("done");
    }

    public static void benchmark() {
        OpCounter counter = new OpCounter();
        Algorithms algorithms = new Algorithms(counter);
        ResultWriter writer = new ResultWriter("results");

        int MAX_SIZE = 100;

        for (int i = 1; i < MAX_SIZE; i++) {
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


            writer.writeResult("lab1test.txt", new double[]{i, classicTime, binetTime, strassenTime, ladermanTime});
        }
    }
}
