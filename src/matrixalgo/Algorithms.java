package matrixalgo;

public class Algorithms {
    private final OpCounter counter;

    public Algorithms(OpCounter counter) {
        this.counter = counter;
    }

    public void multiplyClassic(Matrix target, Matrix op1, Matrix op2) {
        // here would go checks that the matrix sizes are correct, alas i dont feel like writing those
        for (int y = 0; y < target.getRows(); y++) {
            for (int x = 0; x < target.getCols(); x++) {
                double sum = 0.0;
                for (int k = 0; k < op1.getCols(); k++) {
                    sum += op1.at(y, k) * op2.at(k, x);
                    counter.multiplicative++;
                    counter.additive++;
                }
                target.put(y, x, sum);
            }
        }
    }

    public void multiplyBinet(Matrix target, Matrix op1, Matrix op2) {
        // again, checks go here
        if (target.getRows() == 1) {
            target.addTo(0, 0, op1.at(0, 0) * op2.at(0, 0));
            counter.multiplicative++;
            counter.additive++;
            return;
        }

        int n = target.getRows();

        // if size is odd, cut off the edges, and recursively multiply
        // then add the missing values from the edges
        if (n % 2 == 1) {
            int m = n - 1;
            Matrix target_ul = target.view(0, m, 0, m);
            Matrix op1_ul = op1.view(0, m, 0, m);
            Matrix op2_ul = op2.view(0, m, 0, m);
            multiplyBinet(target_ul, op1_ul, op2_ul);

            // inside of the cut off region
            for (int y = 0; y < m; y++) {
                for (int x = 0; x < m; x++) {
                    target.addTo(y, x, op1.at(y, m) * op2.at(m, x));
                    counter.multiplicative++;
                    counter.additive++;
                }
            }

            // bottom side
            for (int x = 0; x < m; x++) {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += op1.at(m, k) * op2.at(k, x);
                    counter.multiplicative++;
                    counter.additive++;
                }
                target.put(m, x, sum);
            }

            // right side
            for (int y = 0; y < m; y++) {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += op1.at(y, k) * op2.at(k, m);
                    counter.multiplicative++;
                    counter.additive++;
                }
                target.put(y, m, sum);
            }

            // bottom right corner
            {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += op1.at(m, k) * op2.at(k, m);
                    counter.multiplicative++;
                    counter.additive++;
                }
                target.put(m, m, sum);
            }

            return;
        }

        int half = target.getRows() / 2;

        Matrix target_11 = target.view(0, half, 0, half);
        Matrix target_12 = target.view(0, half, half, n);
        Matrix target_21 = target.view(half, n, 0, half);
        Matrix target_22 = target.view(half, n, half, n);

        Matrix op1_11 = op1.view(0, half, 0, half);
        Matrix op1_12 = op1.view(0, half, half, n);
        Matrix op1_21 = op1.view(half, n, 0, half);
        Matrix op1_22 = op1.view(half, n, half, n);

        Matrix op2_11 = op2.view(0, half, 0, half);
        Matrix op2_12 = op2.view(0, half, half, n);
        Matrix op2_21 = op2.view(half, n, 0, half);
        Matrix op2_22 = op2.view(half, n, half, n);

        multiplyBinet(target_11, op1_11, op2_11);
        multiplyBinet(target_11, op1_12, op2_21);

        multiplyBinet(target_12, op1_11, op2_12);
        multiplyBinet(target_12, op1_12, op2_22);

        multiplyBinet(target_21, op1_21, op2_11);
        multiplyBinet(target_21, op1_22, op2_21);

        multiplyBinet(target_22, op1_21, op2_12);
        multiplyBinet(target_22, op1_22, op2_22);
    }
}
