package matrixalgo;

public class Algorithms {
    private final OpCounter counter;

    public Algorithms(OpCounter counter) {
        this.counter = counter;
    }

    public void addMatrices(Matrix target, Matrix op1, Matrix op2) {
        for (int y = 0; y < target.getRows(); y++) {
            for (int x = 0; x < target.getCols(); x++) {
                target.put(y, x, op1.at(y, x) + op2.at(y, x));
                counter.additive++;
            }
        }
    }

    public void subtractMatrices(Matrix target, Matrix op1, Matrix op2) {
        for (int y = 0; y < target.getRows(); y++) {
            for (int x = 0; x < target.getCols(); x++) {
                target.put(y, x, op1.at(y, x) - op2.at(y, x));
                counter.additive++;
            }
        }
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

    public void multiplyBinet(Matrix target, Matrix op1, Matrix op2, boolean unitAdd) {
        // again, checks go here
        if (target.getRows() == 1) {
            if (unitAdd) {
                target.addTo(0, 0, op1.at(0, 0) * op2.at(0, 0));
                counter.multiplicative++;
                counter.additive++;
            } else {
                target.put(0, 0, op1.at(0, 0) * op2.at(0, 0));
                counter.multiplicative++;
            }
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
            multiplyBinet(target_ul, op1_ul, op2_ul, unitAdd);

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
                if (unitAdd) {
                    target.addTo(m, x, sum);
                    counter.additive++;
                } else {
                    target.put(m, x, sum);
                }
            }

            // right side
            for (int y = 0; y < m; y++) {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += op1.at(y, k) * op2.at(k, m);
                    counter.multiplicative++;
                    counter.additive++;
                }
                if (unitAdd) {
                    target.addTo(y, m, sum);
                    counter.additive++;
                } else {
                    target.put(y, m, sum);
                }
            }

            // bottom right corner
            {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += op1.at(m, k) * op2.at(k, m);
                    counter.multiplicative++;
                    counter.additive++;
                }
                if (unitAdd) {
                    target.addTo(m, m, sum);
                    counter.additive++;
                } else {
                    target.put(m, m, sum);
                }
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

        multiplyBinet(target_11, op1_11, op2_11, unitAdd);
        multiplyBinet(target_11, op1_12, op2_21, true);

        multiplyBinet(target_12, op1_11, op2_12, unitAdd);
        multiplyBinet(target_12, op1_12, op2_22, true);

        multiplyBinet(target_21, op1_21, op2_11, unitAdd);
        multiplyBinet(target_21, op1_22, op2_21, true);

        multiplyBinet(target_22, op1_21, op2_12, unitAdd);
        multiplyBinet(target_22, op1_22, op2_22, true);
    }

    public void multiplyStrassen(Matrix target, Matrix A, Matrix B) {
        // again, checks go here
        if (target.getRows() == 1) {
            target.put(0, 0, A.at(0, 0) * B.at(0, 0));
            counter.multiplicative++;
            return;
        }

        int n = target.getRows();

        // if size is odd, cut off the edges, and recursively multiply
        // then add the missing values from the edges
        if (n % 2 == 1) {
            int m = n - 1;
            Matrix target_ul = target.view(0, m, 0, m);
            Matrix A_ul = A.view(0, m, 0, m);
            Matrix B_ul = B.view(0, m, 0, m);
            multiplyStrassen(target_ul, A_ul, B_ul);

            // inside of the cut off region
            for (int y = 0; y < m; y++) {
                for (int x = 0; x < m; x++) {
                    target.addTo(y, x, A.at(y, m) * B.at(m, x));
                    counter.multiplicative++;
                    counter.additive++;
                }
            }

            // bottom side
            for (int x = 0; x < m; x++) {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += A.at(m, k) * B.at(k, x);
                    counter.multiplicative++;
                    counter.additive++;
                }
                target.put(m, x, sum);
            }

            // right side
            for (int y = 0; y < m; y++) {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += A.at(y, k) * B.at(k, m);
                    counter.multiplicative++;
                    counter.additive++;
                }
                target.put(y, m, sum);
            }

            // bottom right corner
            {
                double sum = 0.0;
                for (int k = 0; k < n; k++) {
                    sum += A.at(m, k) * B.at(k, m);
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

        Matrix A_11 = A.view(0, half, 0, half);
        Matrix A_12 = A.view(0, half, half, n);
        Matrix A_21 = A.view(half, n, 0, half);
        Matrix A_22 = A.view(half, n, half, n);

        Matrix B_11 = B.view(0, half, 0, half);
        Matrix B_12 = B.view(0, half, half, n);
        Matrix B_21 = B.view(half, n, 0, half);
        Matrix B_22 = B.view(half, n, half, n);

        Matrix scratch = new Matrix(half, half);
        Matrix scratch2 = new Matrix(half, half);

        Matrix P1 = new Matrix(half, half);
        addMatrices(scratch, A_11, A_22);
        addMatrices(scratch2, B_11, B_22);
        multiplyStrassen(P1, scratch, scratch2);

        Matrix P2 = new Matrix(half, half);
        addMatrices(scratch, A_21, A_22);
        multiplyStrassen(P2, scratch, B_11);

        Matrix P3 = new Matrix(half, half);
        subtractMatrices(scratch, B_12, B_22);
        multiplyStrassen(P3, A_11, scratch);

        Matrix P4 = new Matrix(half, half);
        subtractMatrices(scratch, B_21, B_11);
        multiplyStrassen(P4, A_22, scratch);

        Matrix P5 = new Matrix(half, half);
        addMatrices(scratch, A_11, A_12);
        multiplyStrassen(P5, scratch, B_22);

        Matrix P6 = new Matrix(half, half);
        subtractMatrices(scratch, A_21, A_11);
        addMatrices(scratch2, B_11, B_12);
        multiplyStrassen(P6, scratch, scratch2);

        Matrix P7 = new Matrix(half, half);
        subtractMatrices(scratch, A_12, A_22);
        addMatrices(scratch2, B_21, B_22);
        multiplyStrassen(P7, scratch, scratch2);

        addMatrices(target_11, P1, P4);
        subtractMatrices(target_11, target_11, P5);
        addMatrices(target_11, target_11, P7);

        addMatrices(target_12, P3, P5);

        addMatrices(target_21, P2, P4);

        subtractMatrices(target_22, P1, P2);
        addMatrices(target_22, target_22, P3);
        addMatrices(target_22, target_22, P6);
    }
}
