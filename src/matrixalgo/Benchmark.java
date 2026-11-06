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

    private static double timedMultiply(Matrix result, OpCounter counter, Runnable r, int attempts) {
        double[] times = new double[attempts];
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
        return times[(int) Math.floor((double) attempts / 2)];
    }
    //i am not too sure if it should be a new method, but...
    private static double timedInvert(Matrix result, OpCounter counter, Runnable r, int attempts) {
        double[] times = new double[attempts];
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
        return times[(int) Math.floor((double) attempts / 2)];
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

        int MAX_SIZE = 1000;
        //i = 1
        for (int i = 1; i < MAX_SIZE; i++) {
            Matrix m1 = Matrix.generateRandom(i, i);
            Matrix m2 = Matrix.generateRandom(i, i);
            Matrix result = new Matrix(i, i);

            double classicTime = timedMultiply(result, counter, () -> {
                algorithms.multiplyClassic(result, m1, m2);
            }, 1);

            int[] classicOps = {0, 0};
            classicOps[0] = counter.additive;
            classicOps[1] = counter.multiplicative;

            double binetTime = timedMultiply(result, counter, () -> {
                algorithms.multiplyBinet(result, m1, m2, false);
            }, 1);

            int[] binetOps = {0, 0};
            binetOps[0] = counter.additive;
            binetOps[1] = counter.multiplicative;

            double strassenTime = timedMultiply(result, counter, () -> {
                algorithms.multiplyStrassen(result, m1, m2);
            }, 1);

            int[] strassenOps = {0, 0};
            strassenOps[0] = counter.additive;
            strassenOps[1] = counter.multiplicative;

            double ladermanTime = timedMultiply(result, counter, () -> {
                algorithms.multiplyLaderman(result, m1, m2);
            }, 1);

            int[] ladermanOps = {0, 0};
            ladermanOps[0] = counter.additive;
            ladermanOps[1] = counter.multiplicative;

            System.out.printf("Benchmark %dx%d:%n", i, i);
            System.out.printf("Classic: %.4f s%n", classicTime);
            System.out.printf("Binet: %.4f s%n", binetTime);
            System.out.printf("Strassen: %.4f s%n", strassenTime);
            System.out.printf("Laderman: %.4f s%n", ladermanTime);
            System.out.println();


            writer.writeResult("lab1test.txt",
                    new double[]{i, classicTime, binetTime, strassenTime, ladermanTime},
                    new int[][]{classicOps, binetOps, strassenOps, ladermanOps}
            );
        }
    }

    public static void lab2benchmark(){
        OpCounter counter = new OpCounter();
        Algorithms algorithms = new Algorithms(counter);
        ResultWriter writer = new ResultWriter("results");

        int MAX_SIZE = 1000;

        for (int i = 1; i < MAX_SIZE; i++) {
            Matrix result = new Matrix(i, i);

            double binetTime = timedInvert(result, counter, () -> {
                algorithms.invertBinet(result);
            }, 1);

            int[] binetOps = {0, 0};
            binetOps[0] = counter.additive;
            binetOps[1] = counter.multiplicative;

            double strassenTime = timedInvert(result, counter, () -> {
                algorithms.invertStrassen(result);
            }, 1);

            int[] strassenOps = {0, 0};
            strassenOps[0] = counter.additive;
            strassenOps[1] = counter.multiplicative;

            double ladermanTime = timedInvert(result, counter, () -> {
                //?
                algorithms.invertLaderman(result ,result);
            }, 1);

            int[] ladermanOps = {0, 0};
            ladermanOps[0] = counter.additive;
            ladermanOps[1] = counter.multiplicative;

            System.out.printf("n:%d\n", i);
            System.out.printf("Binet: %.4f s%n", binetTime);
            System.out.printf("Strassen: %.4f s%n", strassenTime);
            System.out.printf("Laderman: %.4f s%n", ladermanTime);
            System.out.println();

            writer.writeResult("lab2test.txt",
                    new double[]{i, binetTime, strassenTime, ladermanTime},
                    new int[][]{binetOps, strassenOps, ladermanOps}
            );

        }
    }
}
